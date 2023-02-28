package com.study.webflux.serialize;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import io.r2dbc.postgresql.codec.Json;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TdJsonObjectDeserializer extends JsonDeserializer<Json> {

    @Override
    public Json deserialize ( JsonParser p,
                              DeserializationContext ctxt ) throws IOException, JacksonException
    {
        var value = ctxt.readTree ( p );
        log.info ( "read json value: {}", value );
        return Json.of ( value.toString () );
    }
}
