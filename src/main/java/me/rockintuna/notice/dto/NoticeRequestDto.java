package me.rockintuna.notice.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class NoticeRequestDto {
    private final String title;
    private final String content;
    private final LocalDateTime startedDate;
    private final LocalDateTime endDate;
}
