package me.rockintuna.notice.service;

import lombok.RequiredArgsConstructor;
import me.rockintuna.notice.domain.FileInfo;
import me.rockintuna.notice.exception.FileUploadException;
import me.rockintuna.notice.repository.FileInfoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileInfoRepository fileInfoRepository;

    @Value("${file.destination}")
    private String destination;

    public List<FileInfo> upload(List<MultipartFile> files) {
        List<FileInfo> fileInfoList = new ArrayList<>();
        if ( files != null ) {
            try {
                for (MultipartFile multipartFile : files) {
                    String originalfileName = multipartFile.getOriginalFilename();
                    File file = new File(destination + originalfileName);
                    multipartFile.transferTo(file);
                    FileInfo savedFileInfo = fileInfoRepository.save(FileInfo.from(file));
                    fileInfoList.add(savedFileInfo);
                }
            } catch ( IOException exception ) {
                throw new FileUploadException("파일 업로드를 실패하였습니다." + exception.getMessage());
            }
        }
        return fileInfoList;
    }
}
