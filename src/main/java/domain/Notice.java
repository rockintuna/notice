package domain;

import dto.NoticeRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
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
    private List<File> fileList;

    @ManyToOne
    private User user;

    private Notice(NoticeRequestDto requestDto, User user) {
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.startedDate = requestDto.getStartedDate();
        this.endDate = requestDto.getEndDate();
        this.registrationDate = LocalDateTime.now();
        this.user = user;
    }

    public static Notice of(NoticeRequestDto requestDto, User user) {
        return new Notice(requestDto, user);
    }

    public void addFiles(List<File> fileList) {
        this.fileList.addAll(fileList);
    }
}
