package com.mmall.service.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.TradeStatus;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.*;
import com.mmall.pojo.*;

import com.mmall.service.OrderService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.FastDFSUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.OrderItemVo;
import com.mmall.vo.OrderProductVo;
import com.mmall.vo.OrderVo;
import com.mmall.vo.ShippingVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;


/**
 * @author zhangruiyan
 */
@DubboService
public class OrderServiceImpl implements OrderService {

    private final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private static AlipayTradeService tradeService;
    static {
        /**
         * 一定要在创建AplipayTradeService之前调用Configs.init()设置默认参数
         * Config会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /**
         * 使用Configs提供的默认参数
         * AlipayTradeService可以使用单利或者为静态成员对象，不需要反复new
         */
        tradeService=new AlipayTradeServiceImpl.ClientBuilder().build();
    }
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private PayInfoMapper payInfoMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;


    @Override
    public ServerResponse pay(Long orderNo, Integer userId, String path) {
        Map<String ,String> resultMap = Maps.newHashMap();
        Order order = orderMapper.selectByUserIdAndOrderNo(userId,orderNo);
        if(order == null){
            return ServerResponse.createByErrorMessage("用户没有该订单");
        }
        resultMap.put("orderNo",String.valueOf(order.getOrderNo()));


        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = order.getOrderNo().toString();

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = new StringBuilder().append("happymmall扫码支付,订单号:").append(outTradeNo).toString();

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = new StringBuilder().append("订单").append(outTradeNo).append("购买商品共").append(totalAmount).append("元").toString();

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");


        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();

        List<OrderItem> orderItemList = orderItemMapper.getByOrderNoUserId(orderNo,userId);
        for(OrderItem orderItem : orderItemList){
            GoodsDetail goods = GoodsDetail.newInstance(orderItem.getProductId().toString(), orderItem.getProductName(),
                    BigDecimalUtil.mul(orderItem.getCurrentUnitPrice().doubleValue(),new Double(100).doubleValue()).longValue(),
                    orderItem.getQuantity());
            goodsDetailList.add(goods);
        }

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl("http://zj48dg.natappfree.cc/order/alipay_callback.do")//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);


        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case TradeStatus.SUCCESS:
                logger.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                File folder = new File(path);
                if(!folder.exists()){
                    folder.setWritable(true);
                    folder.mkdirs();
                }

                String qrUrl = String.format(path+"/qr-%s.png",response.getOutTradeNo());

                // 需要修改为运行机器上的路径
                logger.info("qrUrl:" + qrUrl);
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrUrl);
                String upload = FastDFSUtil.upload(qrUrl);
                resultMap.put("qrUrl",upload);
                resultMap.put("orderNo",orderNo.toString());
                return ServerResponse.createBySuccess(resultMap);

                /*// 需要修改为运行机器上的路径
                //细节细节细节
                String qrPath = String.format(path+"/qr-%s.png",response.getOutTradeNo());
                String qrFileName = String.format("qr-%s.png",response.getOutTradeNo());
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);

                File targetFile = new File(path,qrFileName);
                try {
                    FastDFSUtil.upload((MultipartFile)targetFile);
                } catch (Exception e) {
                    logger.error("上传二维码异常",e);
                }
                logger.info("qrPath:" + qrPath);
                String qrUrl = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFile.getName();
                resultMap.put("qrUrl",qrUrl);
                return ServerResponse.createBySuccess(resultMap);*/
            case TradeStatus.FAILED:
                logger.error("支付宝预下单失败!!!");
                return ServerResponse.createByErrorMessage("支付宝预下单失败!!!");

            case TradeStatus.UNKNOWN:
                logger.error("系统异常，预下单状态未知!!!");
                return ServerResponse.createByErrorMessage("系统异常，预下单状态未知!!!");

            default:
                logger.error("不支持的交易状态，交易返回异常!!!");
                return ServerResponse.createByErrorMessage("不支持的交易状态，交易返回异常!!!");
        }
    }

    @Override
    public ServerResponse aliCallback(Map<String, String> params) {
        long id = Long.parseLong(params.get("out_trade_no"));
        String trade_no = params.get("trade_no");
        //订单状态
        String rade_status = params.get("trade_status");

        Order order = orderMapper.selectByOrderNo(id);
        if (order==null){
            return ServerResponse.createByErrorMessage("非我方订单");
        }
        if (order.getStatus()>= Const.OrderStatusEnum.PAID.getCode()){
            return ServerResponse.createByErrorMessage("用户已付款,支付宝重复调用");
        }

        //付款成功
        if (rade_status.equals(Const.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS)){
            //修改付款时间
            order.setPaymentTime(DateTimeUtil.strToDate(params.get("gmt_payment")));
            //修改订单状态
            order.setStatus(Const.OrderStatusEnum.PAID.getCode());
            //修改
            orderMapper.updateByPrimaryKeySelective(order);
        }
        //存放支付信息
        PayInfo payInfo = new PayInfo();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(order.getOrderNo());
        //支付方式
        payInfo.setPayPlatform(Const.PayPlatFormEnum.ALIPAY.getCode());
        //支付宝订单号
        payInfo.setPlatformNumber(trade_no);
        //状态
        payInfo.setPlatformStatus(rade_status);
        //创建时间
        payInfo.setCreateTime(new Date());

        payInfoMapper.insert(payInfo);
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse queryOrderPayStatus(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByUserIdAndOrderNo(userId,orderNo);
        if (order==null){
            return ServerResponse.createByErrorMessage("没有这个订单");
        }
        if (order.getStatus()>=Const.OrderStatusEnum.PAID.getCode()){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

    @Override
    public ServerResponse<PageInfo<OrderVo>> list(Integer id) {

        List<OrderVo> list = new ArrayList<>();
        //通过当前用户id获得订单号，通过订单号获得订单字表中的所有该订单中的数据
        List<Order> orders = orderMapper.listAll();
        for (Order order : orders) {
            OrderVo orderVo = new OrderVo();
            orderVo.setOrderNo(order.getOrderNo());
            orderVo.setPayment(order.getPayment());
            orderVo.setPaymentType(order.getPaymentType());
            //支付方式
            orderVo.setPaymentTypeDesc(Const.PayPlatFormEnum.ALIPAY.name());
            logger.warn("支付方式：{}",Const.PayPlatFormEnum.ALIPAY.name());
            orderVo.setPostage(order.getPostage());
            orderVo.setStatus(order.getStatus());
            //订单状态
            orderVo.setStatusDesc(Const.OrderStatusEnum.codeOf(orderVo.getStatus()).name());
            logger.warn("订单状态:{}",Const.OrderStatusEnum.codeOf(orderVo.getStatus()).name());
            orderVo.setPaymentTime(DateTimeUtil.dateToStr(order.getPaymentTime()));
            orderVo.setSendTime(DateTimeUtil.dateToStr(order.getSendTime()));
            orderVo.setEndTime(DateTimeUtil.dateToStr(order.getSendTime()));
            orderVo.setCloseTime(DateTimeUtil.dateToStr(order.getCloseTime()));
            orderVo.setCreateTime(DateTimeUtil.dateToStr(order.getCreateTime()));

            //获得订单中信息
            List<OrderItem> orderItems = orderItemMapper.list(order.getOrderNo(),id);
            List<OrderItemVo> orderItemVoList = Lists.newArrayList();
            for (OrderItem orderItem : orderItems) {
                OrderItemVo orderItemVo = new OrderItemVo();
                orderItemVo.setOrderNo(orderItem.getOrderNo());
                orderItemVo.setProductId(orderItem.getProductId());
                orderItemVo.setProductName(orderItem.getProductName());
                orderItemVo.setProductImage(orderItem.getProductImage());
                orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
                orderItemVo.setQuantity(orderItem.getQuantity());
                orderItemVo.setTotalPrice(orderItem.getTotalPrice());
                orderItemVo.setCreateTime(DateTimeUtil.dateToStr(orderItem.getCreateTime()));
                orderItemVoList.add(orderItemVo);
            }
            orderVo.setOrderItemVoList(orderItemVoList);

            orderVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
            orderVo.setShippingId(order.getShippingId());
            logger.warn("订单中shoppingId:{}",orderVo.getShippingId());
            ShippingVo shippingVo = orderMapper.getShipping(order.getShippingId());
            orderVo.setShippingVo(shippingVo);
            logger.warn("orderVo中的sppingVo:{}",orderVo.getShippingVo().toString());
            orderVo.setReceiverName(shippingVo.getReceiverName());
            list.add(orderVo);
        }
        PageInfo<OrderVo> listPageInfo = new PageInfo<OrderVo>(list);
        return ServerResponse.createBySuccess(listPageInfo);
    }

    /**
     * 根据订单号查询
     * @param orderNo
     * @param id
     * @return
     */
    @Override
    public ServerResponse<OrderVo> search(Long orderNo, Integer id) {
        Order order = orderMapper.search(orderNo,id);
        if (order==null){
            return ServerResponse.createByErrorCodeMessage(1,"没有找到订单");
        }
        OrderVo orderVo = new OrderVo();

        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setPayment(order.getPayment());
        orderVo.setPaymentType(order.getPaymentType());
        //支付方式
        orderVo.setPaymentTypeDesc(Const.PayPlatFormEnum.ALIPAY.name());
        logger.warn("支付方式：{}",Const.PayPlatFormEnum.ALIPAY.name());
        orderVo.setPostage(order.getPostage());
        orderVo.setStatus(order.getStatus());
        //订单状态
        orderVo.setStatusDesc(Const.OrderStatusEnum.codeOf(orderVo.getStatus()).name());
        logger.warn("订单状态:{}",Const.OrderStatusEnum.codeOf(orderVo.getStatus()).name());
        orderVo.setPaymentTime(DateTimeUtil.dateToStr(order.getPaymentTime()));
        orderVo.setSendTime(DateTimeUtil.dateToStr(order.getSendTime()));
        orderVo.setEndTime(DateTimeUtil.dateToStr(order.getSendTime()));
        orderVo.setCloseTime(DateTimeUtil.dateToStr(order.getCloseTime()));
        orderVo.setCreateTime(DateTimeUtil.dateToStr(order.getCreateTime()));

        //获得订单中信息
        List<OrderItem> orderItems = orderItemMapper.list(order.getOrderNo(),id);
        List<OrderItemVo> orderItemVoList = Lists.newArrayList();
        for (OrderItem orderItem : orderItems) {
            OrderItemVo orderItemVo = new OrderItemVo();
            orderItemVo.setOrderNo(orderItem.getOrderNo());
            orderItemVo.setProductId(orderItem.getProductId());
            orderItemVo.setProductName(orderItem.getProductName());
            orderItemVo.setProductImage(orderItem.getProductImage());
            orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
            orderItemVo.setQuantity(orderItem.getQuantity());
            orderItemVo.setTotalPrice(orderItem.getTotalPrice());
            orderItemVo.setCreateTime(DateTimeUtil.dateToStr(orderItem.getCreateTime()));
            orderItemVoList.add(orderItemVo);
        }
        orderVo.setOrderItemVoList(orderItemVoList);

        orderVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        orderVo.setShippingId(order.getShippingId());
        logger.warn("订单中shoppingId:{}",orderVo.getShippingId());
        ShippingVo shippingVo = orderMapper.getShipping(order.getShippingId());
        orderVo.setShippingVo(shippingVo);
        logger.warn("orderVo中的sppingVo:{}",orderVo.getShippingVo().toString());
        orderVo.setReceiverName(shippingVo.getReceiverName());

        return ServerResponse.createBySuccess(orderVo);
    }

    /**
     * 发货
     * @param orderNo
     * @param id
     * @return
     */
    @Override
    public ServerResponse<String> sendGoods(Long orderNo, Integer id) {
        //order表中的status需要更改(40已发货)，发货时间send_time需要修改,更新时间需要改
        Order order = orderMapper.search(orderNo,id);
        order.setStatus(Const.OrderStatusEnum.SHIPPED.getCode());
        order.setSendTime(new Date());
        order.setUpdateTime(new Date());
        int i = orderMapper.sendGoods(order);
        if(i<=0){
            return ServerResponse.createByErrorCodeMessage(1,"发货失败");
        }
        return ServerResponse.createBySuccess("发货成功");
    }

    /**
     * 创建订单
     * @param shippingId
     * @param id
     * @return
     */
    @Override
    public ServerResponse<OrderVo> create(int shippingId, Integer id) {

        //订单号自己生成（不能重复）使用时间戳加随机数
        long l = System.currentTimeMillis();
        int r = (int) (Math.random() * 100 + 1);
        long orderNo = l*100+r;

        //引用已经存在的地址
        //根据当前用户id查询到购物车中勾选的产品
        List<Cart> list = cartMapper.selectCheckByUserId(id);

        //通过购物车中的数据获得orderItem中对应的数据
        List<OrderItemVo> orderItems = new ArrayList<>();
        for (Cart cart : list) {
            //生成订单子表
            Product product = productMapper.selectProduct(cart.getProductId());
            int i = orderItemMapper.addOrderitem(id,product,cart.getQuantity(),orderNo);
            if (i<=0){
                return ServerResponse.createByErrorMessage("购物车为空");
            }
            //根据当前用户id和产品id获得订单子表中的信息
            OrderItem carOrderItemList = orderItemMapper.getCarOrderItemList(id,orderNo, product.getId());


            OrderItemVo orderItemVo = new OrderItemVo();
            orderItemVo.setOrderNo(carOrderItemList.getOrderNo());
            orderItemVo.setProductId(carOrderItemList.getProductId());
            orderItemVo.setProductName(carOrderItemList.getProductName());
            orderItemVo.setProductImage(carOrderItemList.getProductImage());
            orderItemVo.setCurrentUnitPrice(carOrderItemList.getCurrentUnitPrice());
            orderItemVo.setQuantity(carOrderItemList.getQuantity());
            orderItemVo.setTotalPrice(carOrderItemList.getTotalPrice());
            orderItemVo.setCreateTime(DateTimeUtil.dateToStr(carOrderItemList.getCreateTime()));
            orderItems.add(orderItemVo);

            //修改库存
            product.setStock(product.getStock()-carOrderItemList.getQuantity());
            int editStock = productMapper.editStock(product.getStock(),product.getId());
            if (editStock<=0){
                return ServerResponse.createByErrorMessage("修改库存失败");
            }

            //清除购物车里面选中的商品
            cartMapper.deleteByPrimaryKey(cart.getId());

        }

        if (CollectionUtils.isEmpty(orderItems)){
            return ServerResponse.createByErrorMessage("购物车为空");
        }
        //获得购物车中选中商品的钱数
        BigDecimal price = new BigDecimal(0.00);
        for (OrderItemVo orderItemVo : orderItems) {
            price = price.add(orderItemVo.getTotalPrice());
        }

        //通过当前登陆用户id，总价（payment），shippingId，

        int count = orderMapper.create(id,price,orderNo,shippingId);
        if (count<=0){
            return ServerResponse.createByErrorMessage("创建订单失败");
        }

        //找到刚才创建的订单
        Order order = orderMapper.search(orderNo, id);
        OrderVo orderVo = new OrderVo();
        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setPayment(order.getPayment());
        orderVo.setPaymentType(order.getPaymentType());
        orderVo.setStatus(order.getStatus());
        orderVo.setCreateTime(DateTimeUtil.dateToStr(order.getCreateTime()));
        orderVo.setOrderItemVoList(orderItems);
        orderVo.setShippingId(order.getShippingId());

        return ServerResponse.createBySuccess(orderVo);
    }

    /**
     * 订单商品信息
     * @param id
     * @return
     */
    @Override
    public ServerResponse<OrderProductVo> getOrderCartProduct(Integer id) {
        OrderProductVo orderProductVo = new OrderProductVo();
        //获得购物车信息
        List<Cart> list = cartMapper.selectCheckByUserId(id);
        ServerResponse cartOrderItemList = getCartOrderItemList(id, list);
        if (!cartOrderItemList.isSuccess()){
            return cartOrderItemList;
        }

        //订单子表
        List<OrderItem> orderItemList = (List<OrderItem>) cartOrderItemList.getData();
        List<OrderItemVo> orderItemVoList = Lists.newArrayList();
        //总价
        BigDecimal price = new BigDecimal(0.00);
        for (OrderItem orderItem : orderItemList) {
            price = price.add(orderItem.getTotalPrice());
            OrderItemVo orderItemVo = new OrderItemVo();
            orderItemVo.setOrderNo(orderItem.getOrderNo());
            orderItemVo.setProductId(orderItem.getProductId());
            orderItemVo.setProductName(orderItem.getProductName());
            orderItemVo.setProductImage(orderItem.getProductImage());
            orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
            orderItemVo.setQuantity(orderItem.getQuantity());
            orderItemVo.setQuantity(orderItem.getQuantity());
            orderItemVo.setCreateTime(DateTimeUtil.dateToStr(orderItem.getCreateTime()));
            orderItemVoList.add(orderItemVo);
        }
        orderProductVo.setOrderItemVoList(orderItemVoList);
        orderProductVo.setProductTotalPrice(price);
        orderProductVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return ServerResponse.createBySuccess(orderProductVo);
    }

    /**
     * 取消订单
     * @param orderNo
     * @param id
     * @return
     */
    @Override
    public ServerResponse<String> cancel(Long orderNo, Integer id) {
        //判断订单是否存在，若存在则查看该订单状态，若已付款则无法取消
        Order order = orderMapper.search(orderNo, id);
        if (order==null){
            return ServerResponse.createByErrorMessage("该用户没有此订单");
        }
        // 若订单为已支付状态
        if (order.getStatus().equals(Const.OrderStatusEnum.PAID.getCode())){
            return ServerResponse.createByErrorMessage("该订单已支付，无法取消");
        }
        //修改订单状态、修改商品库存
        order.setStatus(Const.OrderStatusEnum.CANCELED.getCode());
        int i = orderMapper.updateByPrimaryKeySelective(order);
        if (i<=0){
            return ServerResponse.createByErrorMessage("修改订单状态失败");
        }
        List<OrderItem> list = orderItemMapper.list(orderNo,id);
        restoreStock(list);
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse<PageInfo<OrderVo>> searchByOrderNo(Long orderNo, Integer id) {
        ServerResponse<OrderVo> search = search(orderNo, id);
        OrderVo data = search.getData();
        List<OrderVo> list = new ArrayList<>();
        list.add(data);
        PageInfo<OrderVo> orderVoPageInfo = new PageInfo<>(list);
        return ServerResponse.createBySuccess(orderVoPageInfo);
    }

    /**
     * 恢复商品库存
     * @param orderItems
     */
    public void restoreStock(List<OrderItem> orderItems){
        for (OrderItem orderItem : orderItems) {
            //查看正在出售的商品
            Product product = productMapper.selecetProductOnSale(orderItem.getProductId());
            if (product!=null){
                //恢复库存
                product.setStock(orderItem.getQuantity()+product.getStock());
                productMapper.updateByPrimaryKeySelective(product);
            }
        }
    }


    /**
     * 获得购物车中、订单中的订单子表信息
     * @param userId
     * @param cartList
     * @return
     */
    public ServerResponse getCartOrderItemList(Integer userId, List<Cart> cartList) {
        List<OrderItem> orderItemList = Lists.newArrayList();

        if (CollectionUtils.isEmpty(cartList)) {
            return ServerResponse.createByErrorMessage("购物车为空");
        }

        //校验购物车的数量，包括产品的状态和数量
        for (Cart cart : cartList) {
            //一个子订单的明细
            OrderItem orderItem = new OrderItem();
            Product product = productMapper.selectProduct(cart.getProductId());
            if (Const.ProductStatusEnum.ON_SALE.getCode() != product.getStatus()) {
                return ServerResponse.createByErrorMessage("产品" + product.getName() + "不是在售状态");
            }

            //检查库存
            if (cart.getQuantity() > product.getStock()) {
                return ServerResponse.createByErrorMessage("产品" + product.getName() + "库存不足");
            }

            orderItem.setUserId(userId);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cart.getQuantity()));

            orderItemList.add(orderItem);
        }
        return ServerResponse.createBySuccess(orderItemList);
    }

    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            logger.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                logger.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            logger.info("body:" + response.getBody());
        }
    }
}
