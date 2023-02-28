package com.study.webflux.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Todo {
    
    private Long id;
    private String todo;
    private boolean finish;
    private String key;
    
}