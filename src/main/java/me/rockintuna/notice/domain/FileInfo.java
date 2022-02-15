package me.rockintuna.notice.domain;

import lombok.Getter;

import javax.persistence.*;
import java.io.File;

@Entity
@Getter
public class FileInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String fileName;

    private String filePath;

    @ManyToOne
    private Notice notice;

    public FileInfo(String fileName, String filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public FileInfo() {
    }

    public static FileInfo from(File file) {
        return new FileInfo(file.getName(), file.getPath());
    }
}
