package com.study.webflux.config;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


// 초기 데이터 추가

//@Component
//@Slf4j
//@RequiredArgsConstructor
//public class DataInitializer
//{
//    private final DatabaseClient databaseClient;
//    
//    @EventListener(value = ContextRefreshedEvent.class)
//    public void init() throws Exception {
//        log.info ( "start data initialization..." );
//        this.databaseClient
//        .sql ( "INSERT INTO todolist (key, todo, finish) VALUES (:key, :todo, :finish)" )
//        .bind ("key", "todokey")
//        .bind ( "todo", "my first todo" )
//        .bind("finish", false)
//        .fetch ()
//        .first ()
//        .subscribe (
//                        data -> log.info ( "inserted data : {}", data ),
//                        error -> log.info ( "error: {}", error )
//         );
//    }
//}
