package me.rockintuna.notice.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.rockintuna.notice.dto.NoticeRequestDto;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;

    private String content;

    private LocalDateTime registrationDate;

    private LocalDateTime startedDate;

    private LocalDateTime endDate;

    private Long viewCount;

    @OneToMany(mappedBy = "notice")
    private List<File> fileList = new ArrayList<>();

    @ManyToOne
    private User user;

    private Notice(NoticeRequestDto requestDto, User user) {
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.startedDate = requestDto.getStartedDate();
        this.endDate = requestDto.getEndDate();
        this.registrationDate = LocalDateTime.now();
        this.viewCount = 0L;
        this.user = user;
    }

    public static Notice create(NoticeRequestDto requestDto, User user) {
        return new Notice(requestDto, user);
    }

    public void addFiles(List<File> fileList) {
        this.fileList.addAll(fileList);
    }

    public void plusViewCount() {
        this.viewCount += 1;
    }

    public void update(NoticeRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.startedDate = requestDto.getStartedDate();
        this.endDate = requestDto.getEndDate();
    }
}
