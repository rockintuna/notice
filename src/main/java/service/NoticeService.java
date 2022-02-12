package service;

import domain.File;
import domain.Notice;
import domain.User;
import dto.NoticeRequestDto;
import dto.NoticeResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeService {

    public NoticeResponseDto createNotice(NoticeRequestDto requestDto, User user, List<File> fileList) {
        Notice createdNotice = Notice.of(requestDto, user);
        createdNotice.addFiles(fileList);
        return NoticeResponseDto.from(createdNotice);
    }
}
