package com.agora.app.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatGptMessage {
    private String role;

    private List<ContentEntity> content;

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ContentEntity {
        private String type;

        private String text;

        @JsonProperty("image_url")
        private ImageUrl imageUrl;
    }

    @Data
    public static class ImageUrl {
        private String url;
    }
}
