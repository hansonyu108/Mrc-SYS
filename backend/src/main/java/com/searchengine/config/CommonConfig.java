package com.searchengine.config;

import com.google.gson.Gson;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;

@Configuration
@Slf4j
public class CommonConfig {

    @Value("${springdoc.swagger-ui.path:/swagger-ui/index.html}")
    private String swaggerUiPath;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Value("${server.port}")
    private int port;
    @Bean
    public Gson gson(){
        return new Gson();
    }

    @Bean
    public HttpClient httpClient(){return HttpClient.newHttpClient();}

    @Bean
    public OpenAPI customOpenAPI(){
        Info info = new Info().title("检索引擎接口文档")
                .description("检索引擎接口文档")
                .version("1.0");
        return new OpenAPI().info(info);
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("时空数据检索")
                .pathsToMatch("/**")
                .packagesToScan("com.searchengine.controller")
                .build();
    }

    @Bean
    public ApplicationRunner swaggerUrlPrinter() {


        return args -> {
            // 如果 contextPath 为空，则直接打印 Swagger 地址
            String swaggerUrl = (contextPath.isEmpty()) ? "http://localhost:8080" + swaggerUiPath :
                    "http://localhost:8080" + contextPath + swaggerUiPath;
            System.out.println("Swagger文档路径: " + swaggerUrl);
            System.out.println("Swagger文档路径: " + "http://localhost:" + port + "/doc.html");
        };
    }
}
