package me.rockintuna.notice.domain;

import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;

    private String content;

    private LocalDateTime startedDate;

    private LocalDateTime endDate;

    @OneToMany(mappedBy = "notice")
    private List<File> fileList;

    @ManyToOne
    private User user;

}
