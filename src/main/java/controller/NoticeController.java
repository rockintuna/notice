package controller;

import dto.NoticeRequestDto;
import dto.NoticeResponseDto;
import org.springframework.web.bind.annotation.*;

@RestController
public class NoticeController {

    @PostMapping("/api/notice")
    public NoticeResponseDto createNotice(@RequestBody NoticeRequestDto requestDto) {
        return null;
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