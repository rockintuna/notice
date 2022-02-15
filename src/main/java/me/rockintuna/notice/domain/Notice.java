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

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime registrationDate;

    private LocalDateTime startedDate;

    private LocalDateTime endDate;

    @Column(nullable = false)
    private Long viewCount;

    @OneToMany(mappedBy = "notice")
    private List<FileInfo> fileInfoList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
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

    public void addFiles(List<FileInfo> fileInfoList) {
        this.fileInfoList.addAll(fileInfoList);
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
