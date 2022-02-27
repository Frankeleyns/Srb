package com.frankeleyn.srb.mqtest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Frankeleyn
 * @date 2022/2/9 11:29
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@ComponentScan(basePackages = {"com.frankeleyn.srb","com.frankeleyn.common"})
public class ServiceMQTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceMQTestApplication.class, args);
    }
}
