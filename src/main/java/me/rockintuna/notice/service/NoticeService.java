package me.rockintuna.notice.service;

import lombok.RequiredArgsConstructor;
import me.rockintuna.notice.domain.File;
import me.rockintuna.notice.domain.Notice;
import me.rockintuna.notice.domain.User;
import me.rockintuna.notice.dto.NoticeRequestDto;
import me.rockintuna.notice.dto.NoticeResponseDto;
import me.rockintuna.notice.repository.NoticeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public NoticeResponseDto createNotice(NoticeRequestDto requestDto, User user, List<File> fileList) {
        Notice notice = Notice.create(requestDto, user);
        notice.addFiles(fileList);
        Notice createdNotice = noticeRepository.save(notice);
        return NoticeResponseDto.from(createdNotice);
    }
}
