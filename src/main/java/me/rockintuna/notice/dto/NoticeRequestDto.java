package me.rockintuna.notice.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class NoticeRequestDto {
    @NotEmpty
    private final String title;
    @NotEmpty
    private final String content;
    private final LocalDateTime startedDate;
    private final LocalDateTime endDate;
}
