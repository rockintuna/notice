package controller;

import domain.File;
import domain.User;
import dto.NoticeRequestDto;
import dto.NoticeResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import service.NoticeService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping("/api/notice")
    public NoticeResponseDto createNotice(@RequestBody NoticeRequestDto requestDto) {
        User user = new User();
        List<File> fileList = new ArrayList<>();
        return noticeService.createNotice(requestDto, user, fileList);
    }

    @GetMapping("/api/notice/{id}")
    public NoticeResponseDto getNoticeById(@PathVariable String id) {
        return null;
    }

    @PutMapping("/api/notice/{id}")
    public NoticeResponseDto updateNoticeById(@PathVariable String id,
                                              @RequestBody NoticeRequestDto requestDto) {
        return null;
    }

    @DeleteMapping("/api/notice/{id}")
    public NoticeResponseDto deleteNoticeById(@PathVariable String id) {
        return null;
    }
}