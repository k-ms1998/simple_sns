package com.project.sns.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "upvote",
    indexes = {
        @Index(name = "post_id_idx", columnList = "post_id")
    }
)
public class UpVoteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "post_id")
    private PostEntity postEntity;

    private boolean upVoted;

    public static UpVoteEntity of(UserEntity userEntity, PostEntity postEntity, boolean upVoted) {
        return new UpVoteEntity(null, userEntity, postEntity, upVoted);
    }

    public void updateUpVote(){
        this.upVoted = !this.upVoted;
    }
}
