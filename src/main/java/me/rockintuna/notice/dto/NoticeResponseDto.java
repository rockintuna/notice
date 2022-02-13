package me.rockintuna.notice.dto;

import domain.Notice;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public class NoticeResponseDto {
    private final String title;
    private final String content;
    private final LocalDateTime registrationDate;
    private final Long viewCount;
    private final String writer;

    public static NoticeResponseDto from(Notice createdNotice) {
        return NoticeResponseDto.builder()
                .title(createdNotice.getTitle())
                .content(createdNotice.getContent())
                .registrationDate(createdNotice.getRegistrationDate())
                .viewCount(createdNotice.getViewCount())
                .writer(createdNotice.getUser().getUsername())
                .build();
    }
}
