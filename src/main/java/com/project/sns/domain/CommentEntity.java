package com.project.sns.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comment",
        indexes = {
            @Index(name = "post_id_idx", columnList = "post_id")
        }
)
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String comment;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "post_id")
    private PostEntity postEntity;

    public static CommentEntity of(String comment, UserEntity userEntity, PostEntity postEntity) {
        return new CommentEntity(null, comment, userEntity, postEntity);
    }
}
