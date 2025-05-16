package com.agora.app.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;

@Data
public class ChatGptTextGenerateImageRequest {

    private String model;

    private String prompt;

    private Integer n;

    private String size;

    @JsonCreator
    public ChatGptTextGenerateImageRequest(String model, String prompt, Integer n, String size) {
        this.model = model;
        this.prompt = prompt;
        this.n = n;
        this.size = size;
    }
}
