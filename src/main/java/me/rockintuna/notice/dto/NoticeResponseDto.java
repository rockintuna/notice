package me.rockintuna.notice.dto;

import lombok.Builder;
import lombok.Getter;
import me.rockintuna.notice.domain.Notice;

import java.time.LocalDateTime;

@Getter
@Builder
public class NoticeResponseDto {
    private final Long id;
    private final String title;
    private final String content;
    private final LocalDateTime registrationDate;
    private final Long viewCount;
    private final String writer;

    public static NoticeResponseDto from(Notice createdNotice) {
        return NoticeResponseDto.builder()
                .id(createdNotice.getId())
                .title(createdNotice.getTitle())
                .content(createdNotice.getContent())
                .registrationDate(createdNotice.getRegistrationDate())
                .viewCount(createdNotice.getViewCount())
                .writer(createdNotice.getUser().getUsername())
                .build();
    }
}
