package com.study.webflux.repository;

import java.util.function.BiFunction;

import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;

import com.study.webflux.model.Todo;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class TodoRepository {
    
    // 빌더 패턴으로 객체 안전하게 생성
    public static final BiFunction<Row, RowMetadata, Todo> MAPPING_FUNCTION = (row, rowMetaData) -> Todo.builder()
                    .id ( row.get ("id", Long.class) )
                    .todo(row.get ( "todo", String.class))
                    .finish(row.get ( "finish", Boolean.class ))
                    .key(row.get ("key", String.class ))
                    .build();
    
    private final DatabaseClient dbClient;
    
    public Flux<Todo> findAll() {
        return this.dbClient
                        .sql ( "SELECT * FROM todolist" )
                        .map(MAPPING_FUNCTION)
                        .all ();
    }
    
    public Mono<Todo> findByKey(String key) {
        return this.dbClient
                .sql("SELECT * FROM todolist WHERE key=:key")
                .bind("key", key)
                .map(MAPPING_FUNCTION)
                .one();
    }
    
//    public Mono<String> save(Todo t) {
//        return this.dbClient.sql ( "INSERT INTO todolist (todo, finish, key) VALUES (:todo, :finish, :key)")
//                        .bind("todo", t.getTodo () )
//                        .bind("finish", t.isFinish ())
//                        .bind("key", t.getKey ())
//                        .fetch()
//                        .first()
//                        .map(r -> (String) r.get ( "key" ));
//    }
    
    
    public Mono<Todo> save(Todo t) {
        return this.dbClient.sql ( "INSERT INTO todolist (todo, finish, key) VALUES (:todo, :finish, :key)")
                        .bind("todo", t.getTodo () )
                        .bind("finish", t.isFinish ())
                        .bind("key", t.getKey ())
                        .map(MAPPING_FUNCTION)
                        .one();
        
    }
    
    public Mono<Integer> update(Todo t) {
        return this.dbClient.sql ( "UPDATE todolist set todo=:todo, finish=:finish WHERE key=:key" )
                        .bind ( "todo", t.getTodo() )
                        .bind ( "finish", t.isFinish () )
                        .bind ("key", t.getKey ())
                        .fetch()
                        .rowsUpdated ();
    }
    
    public Mono<Integer> deleteByKey(String key) {
        return this.dbClient.sql ( "DELETE FROM todolist WHERE key=:key" )
                        .bind ( "key", key )
                        .fetch()
                        .rowsUpdated();
    }
    
    public Mono<Integer> deleteAll() {
        return this.dbClient.sql ( "DELETE FROM todolist" )
                        .fetch ()
                        .rowsUpdated ();
    }
    
 }