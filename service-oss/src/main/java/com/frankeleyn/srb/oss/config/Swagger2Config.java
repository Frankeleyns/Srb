package com.frankeleyn.srb.oss.config;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author Frankeleyn
 * @date 2022/1/21 15:03
 */
@EnableSwagger2
@Configuration
public class Swagger2Config {

    @Bean
    public Docket coreApiConfig() {
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(adminApiInfo())
                .groupName("ossApi")
                .select()
                // 扫描 Controller 下面的 admin 包
                .paths(Predicates.and(PathSelectors.regex("/api/.*")))
                .build();
        return docket;
    }

    private ApiInfo adminApiInfo(){

        return new ApiInfoBuilder()
                .title("尚融宝文件系统-API文档")
                .description("本文档描述了尚融宝文件系统接口")
                .version("1.0")
                .contact(new Contact("Frankeleyn", "https://githubfast.com/Frankeleyns/", "2582726641@qq.com"))
                .build();
    }

}
