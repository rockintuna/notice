package me.rockintuna.notice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.rockintuna.notice.configuration.JwtTokenProvider;
import me.rockintuna.notice.domain.FileInfo;
import me.rockintuna.notice.domain.Notice;
import me.rockintuna.notice.domain.User;
import me.rockintuna.notice.dto.NoticeRequestDto;
import me.rockintuna.notice.dto.NoticeResponseDto;
import me.rockintuna.notice.dto.RegisterUserRequestDto;
import me.rockintuna.notice.exception.FileUploadException;
import me.rockintuna.notice.exception.NoticeNotFoundException;
import me.rockintuna.notice.service.FileService;
import me.rockintuna.notice.service.NoticeService;
import me.rockintuna.notice.service.UserDetailsImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = NoticeController.class)
@RunWith(PowerMockRunner.class)
class NoticeControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private NoticeService noticeService;
    @MockBean
    private FileService fileService;
    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    User testUser;
    UserDetails mockUserDetails;
    SecurityContext securityContext;

    private void createUser() {
        RegisterUserRequestDto registerUserRequestDto =
                new RegisterUserRequestDto("tester", "tester@notice.test", "password");
        testUser = User.create(registerUserRequestDto, "encodedPassword");
    }

    private void authenticated() {
        createUser();
        mockUserDetails = UserDetailsImpl.from(testUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(mockUserDetails, "", mockUserDetails.getAuthorities());
        securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);
    }

    @Test
    @DisplayName("공지 생성 성공")
    void createNotice() throws Exception {
        //given
        authenticated();
        NoticeRequestDto noticeRequestDto =
                new NoticeRequestDto("title","content", LocalDateTime.now().minusDays(1L), LocalDateTime.now());
        String body = objectMapper.writeValueAsString(noticeRequestDto);

        MockPart noticePostRequest = new MockPart("noticePostRequest", body.getBytes(StandardCharsets.UTF_8));
        noticePostRequest.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Notice notice = Notice.create(noticeRequestDto, testUser);
        NoticeResponseDto responseDto = NoticeResponseDto.from(notice);
        Whitebox.setInternalState(responseDto, "id", 1L);

        given(fileService.upload(null)).willReturn(new ArrayList<>());
        given(noticeService.createNotice(any(NoticeRequestDto.class), any(User.class), any(List.class)))
                .willReturn(responseDto);

        //when
        mvc.perform(multipart("/api/notice")
                        .part(noticePostRequest))
                .andDo(print())

                //then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.title").value(notice.getTitle()))
                .andExpect(jsonPath("$.content").value(notice.getContent()))
                .andExpect(jsonPath("$.registrationDate").isString())
                .andExpect(jsonPath("$.viewCount").value(0))
                .andExpect(jsonPath("$.writer").value(testUser.getUsername()))
                .andExpect(redirectedUrl("/api/notice/"+responseDto.getId()));
    }

    @Test
    @DisplayName("공지 생성 실패 - 제목 없음")
    void createNoticeWithNoTitle() throws Exception {
        //given
        authenticated();
        NoticeRequestDto noticeRequestDto =
                new NoticeRequestDto(null,"content", LocalDateTime.now().minusDays(1L), LocalDateTime.now());
        String body = objectMapper.writeValueAsString(noticeRequestDto);

        MockPart noticePostRequest = new MockPart("noticePostRequest", body.getBytes(StandardCharsets.UTF_8));
        noticePostRequest.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        //when
        mvc.perform(multipart("/api/notice")
                        .part(noticePostRequest))
                .andDo(print())

                //then
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("공지 생성 실패 - 내용 없음")
    void createNoticeWithNoContent() throws Exception {
        //given
        authenticated();
        NoticeRequestDto noticeRequestDto =
                new NoticeRequestDto("title",null, LocalDateTime.now().minusDays(1L), LocalDateTime.now());
        String body = objectMapper.writeValueAsString(noticeRequestDto);

        MockPart noticePostRequest = new MockPart("noticePostRequest", body.getBytes(StandardCharsets.UTF_8));
        noticePostRequest.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        //when
        mvc.perform(multipart("/api/notice")
                        .part(noticePostRequest))
                .andDo(print())

                //then
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("공지 생성 성공 - 파일 업로드")
    void createNoticeWithFiles() throws Exception {
        //given
        authenticated();
        NoticeRequestDto noticeRequestDto =
                new NoticeRequestDto("title","content", LocalDateTime.now().minusDays(1L), LocalDateTime.now());
        String body = objectMapper.writeValueAsString(noticeRequestDto);

        MockPart noticePostRequest = new MockPart("noticePostRequest", body.getBytes(StandardCharsets.UTF_8));
        noticePostRequest.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String fileName = "test.png";
        String contentType = "image/png";
        String filePath = "src/test/resources/static/test.png";
        MockMultipartFile mockMultipartFile = getMockMultipartFile(fileName, contentType, filePath);

        List<FileInfo> fileInfoList = List.of(FileInfo.from(new File(filePath)));
        given(fileService.upload(any(List.class))).willReturn(fileInfoList);

        Notice notice = Notice.create(noticeRequestDto, testUser);
        NoticeResponseDto responseDto = NoticeResponseDto.from(notice);
        Whitebox.setInternalState(responseDto, "id", 1L);
        given(noticeService.createNotice(any(NoticeRequestDto.class), any(User.class), anyList()))
                .willReturn(responseDto);

        //when
        mvc.perform(multipart("/api/notice")
                        .file(mockMultipartFile)
                        .part(noticePostRequest))
                .andDo(print())

                //then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.title").value(notice.getTitle()))
                .andExpect(jsonPath("$.content").value(notice.getContent()))
                .andExpect(jsonPath("$.registrationDate").isString())
                .andExpect(jsonPath("$.viewCount").value(0))
                .andExpect(jsonPath("$.writer").value(testUser.getUsername()))
                .andExpect(redirectedUrl("/api/notice/"+responseDto.getId()));
    }

    @Test
    @DisplayName("공지 생성 실패 - 파일 업로드 실패")
    void createNoticeWithFileUploadFailure() throws Exception {
        //given
        authenticated();
        NoticeRequestDto noticeRequestDto =
                new NoticeRequestDto("title","content", LocalDateTime.now().minusDays(1L), LocalDateTime.now());
        String body = objectMapper.writeValueAsString(noticeRequestDto);

        MockPart noticePostRequest = new MockPart("noticePostRequest", body.getBytes(StandardCharsets.UTF_8));
        noticePostRequest.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String fileName = "test.png";
        String contentType = "image/png";
        String filePath = "src/test/resources/static/test.png";
        MockMultipartFile mockMultipartFile = getMockMultipartFile(fileName, contentType, filePath);

        given(fileService.upload(any())).willThrow(new FileUploadException("파일 업로드를 실패하였습니다."));

        //when
        mvc.perform(multipart("/api/notice")
                        .file(mockMultipartFile)
                        .part(noticePostRequest))
                .andDo(print())

                //then
                .andExpect(status().isInternalServerError());
    }

    private MockMultipartFile getMockMultipartFile(String fileName, String contentType, String path) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(path);
        return new MockMultipartFile(fileName, fileName, contentType, fileInputStream);
    }

    @Test
    @DisplayName("공지 검색 성공")
    void getNoticeById() throws Exception {
        //given
        createUser();
        NoticeRequestDto noticeRequestDto =
                new NoticeRequestDto("title","content", LocalDateTime.now().minusDays(1L), LocalDateTime.now());
        Notice notice = Notice.create(noticeRequestDto, testUser);

        NoticeResponseDto responseDto = NoticeResponseDto.from(notice);
        Whitebox.setInternalState(responseDto, "id", 1L);

        given(noticeService.getNoticeById(1L)).willReturn(responseDto);

        //when
        mvc.perform(get("/api/notice/1"))
                .andDo(print())
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.title").value(notice.getTitle()))
                .andExpect(jsonPath("$.content").value(notice.getContent()))
                .andExpect(jsonPath("$.registrationDate").isString())
                .andExpect(jsonPath("$.viewCount").value(0))
                .andExpect(jsonPath("$.writer").value(testUser.getUsername()));
    }

    @Test
    @DisplayName("공지 검색 실패 - 공지 없음")
    void getNoticeByIdWithNoNotice() throws Exception {
        //given
        given(noticeService.getNoticeById(1L)).willThrow(new NoticeNotFoundException("공지를 찾을 수 없습니다."));

        //when
        mvc.perform(get("/api/notice/1"))
                .andDo(print())
                //then
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("공지 수정 성공")
    void updateNoticeById() throws Exception {
        //given
        authenticated();
        NoticeRequestDto noticeRequestDto =
                new NoticeRequestDto("title","content", LocalDateTime.now().minusDays(1L), LocalDateTime.now());
        String body = objectMapper.writeValueAsString(noticeRequestDto);

        Notice notice = Notice.create(noticeRequestDto, testUser);
        NoticeResponseDto responseDto = NoticeResponseDto.from(notice);
        Whitebox.setInternalState(responseDto, "id", 1L);

        given(noticeService.updateNoticeById(eq(1L), any(NoticeRequestDto.class)))
                .willReturn(responseDto);

        //when
        mvc.perform(put("/api/notice/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.title").value(notice.getTitle()))
                .andExpect(jsonPath("$.content").value(notice.getContent()))
                .andExpect(jsonPath("$.registrationDate").isString())
                .andExpect(jsonPath("$.viewCount").value(0))
                .andExpect(jsonPath("$.writer").value(testUser.getUsername()));
    }

    @Test
    @DisplayName("공지 제거 성공")
    void deleteNoticeById() throws Exception {
        //given
        authenticated();
        NoticeRequestDto noticeRequestDto =
                new NoticeRequestDto("title","content", LocalDateTime.now().minusDays(1L), LocalDateTime.now());
        Notice notice = Notice.create(noticeRequestDto, testUser);
        NoticeResponseDto responseDto = NoticeResponseDto.from(notice);
        Whitebox.setInternalState(responseDto, "id", 1L);

        given(noticeService.deleteNoticeById(eq(1L)))
                .willReturn(responseDto);

        //when
        mvc.perform(delete("/api/notice/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())

                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.title").value(notice.getTitle()))
                .andExpect(jsonPath("$.content").value(notice.getContent()))
                .andExpect(jsonPath("$.registrationDate").isString())
                .andExpect(jsonPath("$.viewCount").value(0))
                .andExpect(jsonPath("$.writer").value(testUser.getUsername()));
    }
}