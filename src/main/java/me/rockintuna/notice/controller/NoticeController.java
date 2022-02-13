package me.rockintuna.notice.controller;

import lombok.RequiredArgsConstructor;
import me.rockintuna.notice.domain.File;
import me.rockintuna.notice.domain.User;
import me.rockintuna.notice.dto.NoticeRequestDto;
import me.rockintuna.notice.dto.NoticeResponseDto;
import me.rockintuna.notice.service.NoticeService;
import me.rockintuna.notice.service.UserDetailsImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping("/api/notice")
    public ResponseEntity<NoticeResponseDto> createNotice(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody NoticeRequestDto requestDto)
            throws URISyntaxException {
        User user = userDetails.getUser();
        List<File> fileList = new ArrayList<>();
        NoticeResponseDto responseDto = noticeService.createNotice(requestDto, user, fileList);
        String url = "/api/notice/"+responseDto.getId();

        return ResponseEntity.created(new URI(url)).body(responseDto);
    }

    @GetMapping("/api/notice/{id}")
    public NoticeResponseDto getNoticeById(@PathVariable Long id) {
        return noticeService.getNoticeById(id);
    }

    @PutMapping("/api/notice/{id}")
    public NoticeResponseDto updateNoticeById(@PathVariable Long id,
                                              @RequestBody NoticeRequestDto requestDto) {
        return noticeService.updateNoticeById(id, requestDto);
    }

    @DeleteMapping("/api/notice/{id}")
    public NoticeResponseDto deleteNoticeById(@PathVariable Long id) {
        return noticeService.deleteNoticeById(id);
    }
}