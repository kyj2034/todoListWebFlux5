package com.study.webflux.config;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.webflux.handler.TodoHandler;

import lombok.AllArgsConstructor;
import lombok.NonNull;

@Configuration
@EnableWebFlux
@AllArgsConstructor
public class WebConfig implements WebFluxConfigurer 
{
    
    @NonNull
    private ObjectMapper objectMapper;

    
    /*
     *  Codecs
     *  
     *  spring-web, spring-core 모듈을 사용하면 리액티브 논블로킹 방식으로 바이트 컨텐츠를 고수준 객체롸 직렬화, 역직렬화할 수 있다.
     *  
     *  Jackson JSON
     *  JSON, binarySON(Smile) 모두 Jackson 라이브러리 디펜던시가 있으면 추가된다.
     *  
     *  Jackson2Decoder : Jackson의 비동기, 논블로킹 파서가 TokenBuffer로 바이트 청크 스트림을 모아 JSON 객체로 변환.
     *                    각 TokenBuffer는 Jackson의 ObjectMapper 로 넘겨져 고수준 객체를 만든다.
     *                    single-value publisher(e.g. Mono) 를 디코딩 할 때는 TokenBuffer가 하나뿐이다.
     *                    multi-value publisher (e.g Flux) 를 디코딩 할 때는, 각 TokenBuffer에 객체를 구성할 수 있을 만큼
     *                    바이트가 모이면 그때그때 ObjectMapper로 전달한다. 입력 컨텐츠는 JSON 배열이거나, 컨텐츠 타입이 application/stream+json 이라면
     *                    line-delimited JSON(?) 일 수도 있다.
     *  Jackson2Encoder : single-value publisher(e.g Mono)는 바로 ObjectMapper에서 직렬화한다.
     *                    multi-value publisher를 application/json 로 직렬화할 땐 기본적으로 Flux#collectToList()로 값을 수집한 다음 그 컬렉션을
     *                    직렬화한다.
     *                    application/stream+json, application/stream+x-jackson-smile 같은 스트리밍 타입을 multi-value publisher로
     *                    직렬화하면 line-delimited JSON 포맷 으로 따로따로 인코딩하고, write, flush한다.
     *                    SSE라면 이벤트가 발생할 때마다 Jackson2Encoder를 호출하고 바로 flush한다. 
     *  
     *       
     *       출처 : Spring Web on Reactive Stack - Spring WebFlux (2)
     *      ( https://godekdls.github.io/Reactive%20Spring/springwebflux2/ )
     *  
     * */
    
    
    /**
     *  HTTP 메시지 코덱 (HTTP message codecs)
     *  
     *  요청과 응답 본문(body)를 읽고 쓰는 방식을 사용자 정의하는 방법.
     *  
     *  출처 : 1. 스프링 웹플럭스: 1.11. WebFlux Config
     *  ( https://madplay.github.io/post/spring-webflux-references-webflux-config )
     */
    
    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        configurer.defaultCodecs()
                .jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN));
        configurer.defaultCodecs()
                .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN));
    }
    
    
    /**
     * 
     * <pre>
     *  routerFunctions 의 route() 메소드는 RouterFunctionBuilder를 반환한다.
     *  빌더 패턴을 이용하여 RouterFunction을 완성시킬 수 있다.
     *  GET : http GET 메소드가 들어왔을 때를 정의. uri pattern과 함수형 인터페이스를 인자로 받을 수 있다.
     *  POST : 함수형 인터페이스를 postHandler::create 처럼 축약하려 사용한 형태.
     *  uri pattern 과 함수형 인터페이스와 더불어, accept, 혹은 content-type을 명시적으로 써줄 수 있다.
     *  build() 메소드로 빌더 패턴을 완성시켜 구체 클래스를 만들어 낸다.
     *  
     *  
     *  출처 : Spring Webflux의 Functional Endpoints 사용법. (with RouterFunction)
     * ( https://gardeny.tistory.com/47 )
     * 
     * </pre>
     * @param handler
     * @return
     */
    
    @Bean
    public RouterFunction<ServerResponse> routes(TodoHandler handler){
        
        return route()
                        .GET("/todolist", handler::all)
                        .POST("/todolist", handler::create)
                        .PUT("/todolist/{key}", handler::update)
                        .DELETE("/todolist/{key}", handler::delete)
                        .build();
    }
}
