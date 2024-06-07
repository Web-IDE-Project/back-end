package sumcoda.webide.workspace.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Workspace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 컨테이너 생성시 어떤 컨테이너인지 설명
    @Column(nullable = false)
    private String title;

    // 해당 컨테이너가 어떤 종류의 컨테이너인지
    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String language;

    @Column(nullable = false)
    private String content;

    // private인지 public 인지
    @Column(nullable = false)
    private Boolean status;


    @Builder
    public Workspace(String title, String category, String language, String content, Boolean status) {
        this.title = title;
        this.category = category;
        this.language = language;
        this.content = content;
        this.status = status;
    }
}
