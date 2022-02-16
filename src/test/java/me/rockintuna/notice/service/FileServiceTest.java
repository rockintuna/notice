package me.rockintuna.notice.service;

import me.rockintuna.notice.domain.FileInfo;
import me.rockintuna.notice.repository.FileInfoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @InjectMocks
    private FileService fileService;

    @Mock
    private FileInfoRepository fileInfoRepository;

    @Test
    @DisplayName("파일 업로드 성공")
    void upload() throws IOException {
        //given
        String destination = "src/test/resources/static/";
        ReflectionTestUtils.setField(fileService, "destination", destination);
        List<MultipartFile> multipartFiles = new ArrayList<>();
        String fileName = "test.png";
        String contentType = "image/png";
        String filePath = "src/test/resources/static/test.png";
        MockMultipartFile mockMultipartFile = getMockMultipartFile(fileName, contentType, filePath);
        multipartFiles.add(mockMultipartFile);
        FileInfo savedFile = FileInfo.from(new File(filePath));

        given(fileInfoRepository.save(any(FileInfo.class))).willReturn(savedFile);

        //when
        List<FileInfo> fileInfoList = fileService.upload(multipartFiles);

        //then
        assertThat(fileInfoList.size()).isEqualTo(1L);
        assertThat(fileInfoList.get(0).getFileName()).isEqualTo(fileName);
        assertThat(fileInfoList.get(0).getFilePath()).isEqualTo(filePath);
    }

    private MockMultipartFile getMockMultipartFile(String fileName, String contentType, String path) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(path);
        return new MockMultipartFile(fileName, fileName, contentType, fileInputStream);
    }

    @Test
    @DisplayName("파일 없음")
    void nofiles() {
        List<MultipartFile> multipartFiles = new ArrayList<>();
        List<FileInfo> upload = fileService.upload(multipartFiles);

        assertThat(upload).isEmpty();
    }
}