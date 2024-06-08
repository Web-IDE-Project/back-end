package sumcoda.webide.entry.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sumcoda.webide.memberworkspace.domain.MemberWorkspace;
import sumcoda.webide.workspace.domain.Workspace;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
public class Entry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Lob
    private String content;

    @Column(nullable = false)
    private Boolean isDirectory;

    // 자기 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Entry parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Entry> children = new ArrayList<>();

    // 연관관게 주인
    // 양방향
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;

    @Builder
    public Entry(String name, String content, Boolean isDirectory, Entry parent, Workspace workspace) {
        this.name = name;
        this.content = content;
        this.isDirectory = isDirectory;
        this.assignParent(parent);
        this.assignWorkspace(workspace);
    }

    // 직접 빌더 패턴의 생성자를 활용하지 말고 해당 메서드를 활용하여 엔티티 생성
    public static Entry createEntry(String name, String content, Boolean isDirectory, Entry parent, Workspace workspace) {
        return Entry.builder()
                .name(name)
                .content(content)
                .isDirectory(isDirectory)
                .parent(parent)
                .workspace(workspace)
                .build();
    }

    // Entry N <-> 1 Workspace
    // 양방향 연관관계 편의 메서드
    public void assignWorkspace(Workspace workspace) {
        if (this.workspace != null) {
            this.workspace.getEntries().remove(this);
        }
        this.workspace = workspace;

        if (!workspace.getEntries().contains(this)) {
            workspace.addEntry(this);
        }
    }

    // Entry(parent) 1 <-> N Entry(child)
    // 양방향 연관관계 편의 메서드
    public void assignParent(Entry parent) {
        if (this.parent != null) {
            this.parent.getChildren().remove(this);
        }
        this.parent = parent;

        if (!parent.getChildren().contains(this)) {
            parent.addChild(this);
        }
    }

    // Entry(child) N <-> 1 Entry(parent)
    // 양방향 연관관계 편의 메서드
    public void addChild(Entry child) {
        this.children.add(child);

        if (child.getParent() != this) {
            child.assignParent(this);
        }
    }

}
