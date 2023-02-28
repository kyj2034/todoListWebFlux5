package com.study.webflux.handler;

import java.net.URI;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.*;

import com.study.webflux.model.Todo;
import com.study.webflux.repository.TodoRepository;


@Component
public class TodoHandler
{
    private final TodoRepository todos;

    public TodoHandler(TodoRepository todos) {
        this.todos = todos;
    }
    
    public Mono<ServerResponse> all(ServerRequest req) {
        return ok().body(this.todos.findAll (), Todo.class); // 성공이라면 본문(body) return! (findAll의 결과 )
    }
    
    public Mono<ServerResponse> create(ServerRequest req) {
        return req.bodyToMono ( Todo.class )
                        .flatMap ( this.todos::save ) // methods 호출
                        .flatMap ( todo -> created(URI.create ( "/todolist/" + todo.getKey () )) // 201 Created : 성공 상태 응답코드
                                        .build() );                                  // 요청이 성공해 리소스 생성.
                                                                                     // 
    }
    
    public Mono<ServerResponse> update(ServerRequest req) {
        var existed = this.todos.findByKey( req.pathVariable ( "key" ) );
        return Mono
                 .zip ( (data) -> {  // 여러 개의 모노 객체를 하나의 모노로 결합할 경우, 사용하는 zip. 각 Mono 스레드를 병렬처리할 수도 있음.
                     Todo t = (Todo) data[0]; //  com.study.webflux.handler.TodoHandler.update(ServerRequest req)와 연결? 
                     Todo t2 = (Todo) data[1];
                     if (t2 != null && StringUtils.hasText(t2.getTodo())) {
                         t.setTodo(t2.getTodo());
                     }

                     if (t2 != null) {
                         t.setFinish(t2.isFinish ());
                     }
                     
                     return t; //수정된 투두
                 }, existed,
                 req.bodyToMono ( Todo.class ) // 가져온 Todo.class Body(본문)을 Mono 객체로 변환한다.
               ).cast ( Todo.class ) // 현재 Mono 타입을 target의 타입으로 캐스팅한다.
                 .flatMap ( this.todos::update ) //methods 호출
                 .flatMap(update -> noContent().build());  // 204 noContent : 성공 상태 응답 코드! 페이지 바꾸지 않고 리소스 업데이트
                        
    }
    
    public Mono<ServerResponse> delete(ServerRequest req) {
        return this.todos.deleteByKey (req.pathVariable ( "key" ))
                        .flatMap ( deleted -> noContent().build());
    }
}
