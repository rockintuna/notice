package me.rockintuna.notice.domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String fileName;

    private String filePath;

    @ManyToOne
    private Notice notice;
}
