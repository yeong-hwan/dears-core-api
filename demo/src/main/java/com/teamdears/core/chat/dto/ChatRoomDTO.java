package com.teamdears.core.chat.dto;

import lombok.*;

import java.util.HashSet;
import java.util.List;

public class ChatRoomDTO {

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        private Long id;

        private Long portfolioId;
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private List<MessageDTO.Response> messages;

        // TODO : isOppositeConnected 로 변경 여지
        private HashSet<String> userIds;

        private Long chatRoomId;
    }
}
