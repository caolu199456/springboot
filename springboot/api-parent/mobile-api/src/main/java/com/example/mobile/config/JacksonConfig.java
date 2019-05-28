package com.example.mobile.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.NumberSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;

@Configuration
public class JacksonConfig {
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customJackson() {
        return jacksonObjectMapperBuilder -> {
            jacksonObjectMapperBuilder.dateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

            /**
             * BigDecimal科学计数法问题
             */
            jacksonObjectMapperBuilder.serializerByType(BigDecimal.class, new NumberSerializer(BigDecimal.class) {
                @Override
                public void serialize(Number value, JsonGenerator g, SerializerProvider provider) throws IOException {
                    if (value != null) {
                        g.writeNumber(((BigDecimal) value).toPlainString());
                    }

                }
            });

            /**
             * id为Long的时候js精度丢失
             */
            jacksonObjectMapperBuilder.serializerByType(Long.class, new NumberSerializer(Long.class) {
                @Override
                public void serialize(Number value, JsonGenerator g, SerializerProvider provider) throws IOException {
                    if (value != null) {
                        g.writeString(((Long) value) + "");;
                    }
                }
            });
        };
    }

}
