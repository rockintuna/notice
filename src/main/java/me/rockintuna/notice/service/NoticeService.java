package me.rockintuna.notice.service;

import lombok.RequiredArgsConstructor;
import me.rockintuna.notice.domain.FileInfo;
import me.rockintuna.notice.domain.Notice;
import me.rockintuna.notice.domain.User;
import me.rockintuna.notice.dto.NoticeRequestDto;
import me.rockintuna.notice.dto.NoticeResponseDto;
import me.rockintuna.notice.exception.NoticeNotFoundException;
import me.rockintuna.notice.repository.NoticeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public NoticeResponseDto createNotice(NoticeRequestDto requestDto, User user, List<FileInfo> fileInfoList) {
        Notice notice = Notice.create(requestDto, user);
        notice.addFiles(fileInfoList);
        Notice createdNotice = noticeRepository.save(notice);
        return NoticeResponseDto.from(createdNotice);
    }

    public NoticeResponseDto getNoticeById(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new NoticeNotFoundException("공지를 찾을 수 없습니다."));
        notice.plusViewCount();
        return NoticeResponseDto.from(notice);
    }

    public NoticeResponseDto updateNoticeById(Long id, NoticeRequestDto requestDto) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new NoticeNotFoundException("공지를 찾을 수 없습니다."));
        notice.update(requestDto);
        return NoticeResponseDto.from(notice);
    }

    public NoticeResponseDto deleteNoticeById(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new NoticeNotFoundException("공지를 찾을 수 없습니다."));
        noticeRepository.deleteById(id);
        return NoticeResponseDto.from(notice);
    }
}
