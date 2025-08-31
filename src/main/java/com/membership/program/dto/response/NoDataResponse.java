package com.membership.program.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoDataResponse {
    
    private LocalDateTime timestamp;
    private String message;
    private String details;
    private String path;
    
    public static NoDataResponse create(String message, String path) {
        return NoDataResponse.builder()
                .timestamp(LocalDateTime.now())
                .message(message)
                .details("No data found for the requested criteria")
                .path(path)
                .build();
    }
    
    public static NoDataResponse create(String message, String details, String path) {
        return NoDataResponse.builder()
                .timestamp(LocalDateTime.now())
                .message(message)
                .details(details)
                .path(path)
                .build();
    }
}
