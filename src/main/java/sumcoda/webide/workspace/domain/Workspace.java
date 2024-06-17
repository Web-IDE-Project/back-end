package sumcoda.webide.workspace.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sumcoda.webide.chat.domain.ChatRoom;
import sumcoda.webide.entry.domain.Entry;
import sumcoda.webide.memberworkspace.domain.MemberWorkspace;
import sumcoda.webide.workspace.enumerate.Category;
import sumcoda.webide.workspace.enumerate.Language;
import sumcoda.webide.workspace.enumerate.Status;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Workspace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 컨테이너 생성시 어떤 컨테이너인지 설명
    @Column(nullable = false)
    private String title;

    // 해당 컨테이너가 어떤 종류의 컨테이너인지
    @ElementCollection(targetClass = Category.class)
    @CollectionTable(name = "workspace_category", joinColumns = @JoinColumn(name = "workspace_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private Set<Category> categories = new HashSet<>();

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Language language;

    private String description;

    @Column(nullable = false)
    private String rootName;

    // private 인지 public 인지
    @Column(nullable = false)
    private Boolean isPublic;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    // 양방향 연관관계
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "workspace")
    private List<MemberWorkspace> memberWorkspaces = new ArrayList<>();

    // 양방향 연관관계
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "workspace")
    private List<Entry> entries = new ArrayList<>();

    // 양방향 연관관계
    @OneToOne(mappedBy = "workspace")
    private ChatRoom chatRoom;


    // 빌더 패턴 생성자
    @Builder
    public Workspace(String title, Set<Category> categories, Language language, String description, String rootName, Boolean isPublic, Status status) {
        this.title = title;
        this.categories = categories;
        this.language = language;
        this.description = description;
        this.rootName = rootName;
        this.isPublic = isPublic;
        this.status = status;
    }

    // 직접 빌더 패턴의 생성자를 활용하지 말고 해당 메서드를 활용하여 엔티티 생성
    public static Workspace createWorkspace(String title, Set<Category> categories, Language language, String description, String rootName, Boolean isPublic, Status status) {
        return Workspace.builder()
                .title(title)
                .categories(categories)
                .language(language)
                .description(description)
                .rootName(rootName)
                .isPublic(isPublic)
                .status(status)
                .build();
    }

    // Workspace 1 <-> N MemberWorkspace
    // 양방향 연관관계 편의 메서드
    public void addMemberWorkspace(MemberWorkspace memberWorkspace) {
        this.memberWorkspaces.add(memberWorkspace);

        if (memberWorkspace.getWorkspace() != this) {
            memberWorkspace.assignWorkspace(this);
        }
    }

    // Workspace 1 <-> N Entry
    // 양방향 연관관계 편의 메서드
    public void addEntry(Entry entry) {
        this.entries.add(entry);

        if (entry.getWorkspace() != this) {
            entry.assignWorkspace(this);
        }
    }

    // Workspace 1 <-> 1 ChatRoom
    // 양방향 연관관계 편의 메서드드
    public void assignChatRoom(ChatRoom chatRoom) {
        if (this.chatRoom != null) {
            this.chatRoom.assignWorkspace(null);
        }
        this.chatRoom = chatRoom;
        if (chatRoom != null && chatRoom.getWorkspace() != this) {
            chatRoom.assignWorkspace(this);
        }
    }
}
