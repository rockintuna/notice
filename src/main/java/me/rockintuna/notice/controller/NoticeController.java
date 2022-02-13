package me.rockintuna.notice.controller;

import lombok.RequiredArgsConstructor;
import me.rockintuna.notice.domain.FileInfo;
import me.rockintuna.notice.domain.User;
import me.rockintuna.notice.dto.NoticeRequestDto;
import me.rockintuna.notice.dto.NoticeResponseDto;
import me.rockintuna.notice.service.FileService;
import me.rockintuna.notice.service.NoticeService;
import me.rockintuna.notice.service.UserDetailsImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;
    private final FileService fileService;

    @PostMapping("/api/notice")
    public ResponseEntity<NoticeResponseDto> createNotice(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestPart(value = "noticePostRequest") NoticeRequestDto requestDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files)
            throws URISyntaxException, IOException {
        User user = userDetails.getUser();
        List<FileInfo> fileInfoList = fileService.upload(files);
        NoticeResponseDto responseDto = noticeService.createNotice(requestDto, user, fileInfoList);
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