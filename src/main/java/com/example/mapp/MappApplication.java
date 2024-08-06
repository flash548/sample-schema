package com.example.mapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@SpringBootApplication
public class MappApplication {

    public static void main(String[] args) {
        SpringApplication.run(MappApplication.class, args);
    }

    /**
     * Override any datasource - forcing to an in-memory H2 for now during test
     * @return
     */
    @Profile("test")
    @Bean
    public DataSource dataSourceMem() {
        return DataSourceBuilder.create()
                .driverClassName("org.h2.Driver")
                .url("jdbc:h2:mem:testdb")
                .username("sa")
                .password("")
                .build();
    }

    @Profile("default")
    @Bean
    public DataSource dataSourcePg() {
        return DataSourceBuilder.create()
                .driverClassName("org.postgresql.Driver")
                .url("jdbc:postgresql://localhost:5432/testmapp")
                .username("postgres")
                .password("postgres")
                .build();
    }
}
