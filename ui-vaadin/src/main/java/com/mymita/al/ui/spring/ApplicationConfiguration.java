package com.mymita.al.ui.spring;

import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

@Configuration
public class ApplicationConfiguration {

  @Bean
  ListeningExecutorService executorService() {
    return MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));
  }

}
