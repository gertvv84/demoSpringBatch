package com.example.batchDemo;

import org.jspecify.annotations.Nullable;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class TextItemProcessor implements ItemProcessor<String,String> {

    @Override
    public String process(String message) throws Exception {
        String maskedMessage = message.replaceAll("\\d","*");

        return maskedMessage;
    }
}
