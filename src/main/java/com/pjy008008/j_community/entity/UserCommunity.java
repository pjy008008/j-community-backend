package com.pjy008008.j_community.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(
        name = "user_community",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "community_id"})
        }
)
public class UserCommunity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id", nullable = false)
    private Community community;

    @Builder
    public UserCommunity(User user, Community community) {
        this.user = user;
        this.community = community;
    }
}