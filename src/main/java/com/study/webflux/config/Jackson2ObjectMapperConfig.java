package com.study.webflux.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class Jackson2ObjectMapperConfig
{
    
    /**
     * 
     * <pre>
     * 
     * Util 로 만들어 Json String 으로 만들어 디비에 저장, 혹은 Json String을 특정 객체로 매핑시킬 때 사용.
     * 
     * 기본 조건의 경우, 스프링 부트에서 직렬화/역직렬화(Serialize, Deserialize) 를 담당한다.
     * 즉, ResponseEntity가 response body를 json으로 직렬화 할 때, ObjectMapper가 사용된다.
     * 
     * 기본조건으로 LocalDataTime, LocalDate, LocalTime과 같은 객체들을 직렬화가능.
     * 
     * 출처 : ObjectMapper로 LocalDateTime를 Json으로 직렬화하기(Serialize)
     * ( https://sas-study.tistory.com/386 )
     * 
     * </pre>
     * @return
     */
    
    
    @Bean
    public ObjectMapper objectMapper() {
        
        var builder = Jackson2ObjectMapperBuilder.json();
        builder.serializationInclusion ( JsonInclude.Include.NON_EMPTY );
        builder.featuresToDisable ( 
                        SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,     // JSON에서 날짜를 문자열로 표시하도록 매퍼에게 지시
                        SerializationFeature.FAIL_ON_EMPTY_BEANS,          // 필드가 없는 클래스를 Serialize 하는것을 방지
                        DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, //JSON 에는 있지만 매핑될 Object에는 없는 필드 무시
                        DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES); //JSON에 있는 property가 mapping될 Object에 primitive인데 null 값이 전달을 무시해야하는 경우
        builder.featuresToEnable ( DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY ); // JSON Array 처리중 단일 데이터의 경우를 허용하기 위함 
        builder.modulesToInstall ( JavaTimeModule.class );
    
        return builder.build ();
    }
}
