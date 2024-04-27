package com.myworld.gradution_project_backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
@MapperScan("com.myworld.gradution_project_backend.mapper")
public class GradutionProjectBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(GradutionProjectBackendApplication.class, args);
    }

}
