package com.mmall;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zhangruiyan
 */
@SpringBootApplication
@EnableDubbo
public class PortalControllerApplication {
    public static void main(String[] args) {
        SpringApplication.run(PortalControllerApplication.class,args);
    }

}
