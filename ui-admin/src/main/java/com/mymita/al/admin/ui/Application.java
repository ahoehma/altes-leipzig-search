package com.mymita.al.admin.ui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.mymita.al")
@ComponentScan(basePackages = "com.mymita.al")
@EntityScan(basePackages = "com.mymita.al")
@EnableAsync
public class Application {

  public static void main(final String[] args) {
    SpringApplication.run(Application.class, args);
  }

}
