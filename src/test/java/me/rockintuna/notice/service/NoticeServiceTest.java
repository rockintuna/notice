package me.rockintuna.notice.service;

import me.rockintuna.notice.domain.Notice;
import me.rockintuna.notice.domain.User;
import me.rockintuna.notice.dto.NoticeRequestDto;
import me.rockintuna.notice.dto.NoticeResponseDto;
import me.rockintuna.notice.dto.RegisterUserRequestDto;
import me.rockintuna.notice.exception.NoticeNotFoundException;
import me.rockintuna.notice.repository.NoticeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class NoticeServiceTest {

    @InjectMocks
    private NoticeService noticeService;

    @Mock
    private NoticeRepository noticeRepository;

    User testUser;

    private void createUser() {
        RegisterUserRequestDto registerUserRequestDto =
                new RegisterUserRequestDto("tester", "tester@notice.test", "password");
        testUser = User.create(registerUserRequestDto, "encodedPassword");
    }

    @Test
    @DisplayName("공지 생성 성공")
    void createNotice() {
        //given
        createUser();
        NoticeRequestDto noticeRequestDto =
                new NoticeRequestDto("title","content", LocalDateTime.now().minusDays(1L), LocalDateTime.now());
        Notice notice = Notice.create(noticeRequestDto, testUser);
        given(noticeRepository.save(any(Notice.class))).willReturn(notice);

        //when
        NoticeResponseDto responseDto = noticeService.createNotice(noticeRequestDto, testUser, new ArrayList<>());

        //then
        assertThat(responseDto.getTitle()).isEqualTo(noticeRequestDto.getTitle());
        assertThat(responseDto.getContent()).isEqualTo(noticeRequestDto.getContent());
        assertThat(responseDto.getRegistrationDate()).isNotNull();
        assertThat(responseDto.getWriter()).isEqualTo(testUser.getUsername());
        assertThat(responseDto.getViewCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("공지 검색 성공")
    void getNoticeById() {
        //given
        createUser();
        NoticeRequestDto noticeRequestDto =
                new NoticeRequestDto("title","content", LocalDateTime.now().minusDays(1L), LocalDateTime.now());
        Notice notice = Notice.create(noticeRequestDto, testUser);
        given(noticeRepository.findById(1L)).willReturn(Optional.of(notice));

        //when
        NoticeResponseDto responseDto = noticeService.getNoticeById(1L);

        //then
        assertThat(responseDto.getTitle()).isEqualTo(noticeRequestDto.getTitle());
        assertThat(responseDto.getContent()).isEqualTo(noticeRequestDto.getContent());
        assertThat(responseDto.getRegistrationDate()).isNotNull();
        assertThat(responseDto.getWriter()).isEqualTo(testUser.getUsername());
        assertThat(responseDto.getViewCount()).isEqualTo(notice.getViewCount());
    }

    @Test
    @DisplayName("공지 검색 실패 - 공지 없음")
    void getNoticeByIdWithNoNotice() {
        //given
        given(noticeRepository.findById(1L)).willReturn(Optional.empty());

        //when, then
        assertThrows(NoticeNotFoundException.class, () -> noticeService.getNoticeById(1L));
    }

    @Test
    @DisplayName("공지 수정 성공")
    void updateNoticeById() {
        //given
        createUser();
        NoticeRequestDto noticeRequestDto =
                new NoticeRequestDto("title","content", LocalDateTime.now().minusDays(1L), LocalDateTime.now());
        Notice notice = Notice.create(noticeRequestDto, testUser);
        given(noticeRepository.findById(1L)).willReturn(Optional.of(notice));

        //when
        NoticeResponseDto responseDto = noticeService.updateNoticeById(1L, noticeRequestDto);

        //then
        assertThat(responseDto.getTitle()).isEqualTo(noticeRequestDto.getTitle());
        assertThat(responseDto.getContent()).isEqualTo(noticeRequestDto.getContent());
        assertThat(responseDto.getRegistrationDate()).isNotNull();
        assertThat(responseDto.getWriter()).isEqualTo(testUser.getUsername());
        assertThat(responseDto.getViewCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("공지 제거 성공")
    void deleteNoticeById() {
        //given
        createUser();
        NoticeRequestDto noticeRequestDto =
                new NoticeRequestDto("title","content", LocalDateTime.now().minusDays(1L), LocalDateTime.now());
        Notice notice = Notice.create(noticeRequestDto, testUser);
        given(noticeRepository.findById(1L)).willReturn(Optional.of(notice));

        //when
        NoticeResponseDto responseDto = noticeService.deleteNoticeById(1L);

        //then
        assertThat(responseDto.getTitle()).isEqualTo(noticeRequestDto.getTitle());
        assertThat(responseDto.getContent()).isEqualTo(noticeRequestDto.getContent());
        assertThat(responseDto.getRegistrationDate()).isNotNull();
        assertThat(responseDto.getWriter()).isEqualTo(testUser.getUsername());
        assertThat(responseDto.getViewCount()).isEqualTo(0);
    }
}