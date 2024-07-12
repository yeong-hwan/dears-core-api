package com.example.demo.member.domain;

import com.example.demo.chat.domain.CustomerMessage;
import com.example.demo.enums.member.MemberRole;
import com.example.demo.review.domain.Review;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long id;

    @Column
    private MemberRole role;

    @Column
    private String name;

    @Column
    private String UUID;

    @Column
    private boolean isDeleted;

    @Column
    private String profileImageUrl;

    @OneToMany
    @JoinColumn(name = "customer_id")
    private List<Review> reviewList;

    //매칭 1:N
    @OneToMany
    @JoinColumn(name = "customer_id")
    private List<WeddingPlanner> weddingPlannerList;

    @OneToMany
    @JoinColumn(name = "customer_id")
    private List<CustomerMessage> customerMessageList;

    //chatroom 1:N
    //@OneToMany(mappedBy = "customer")
    //private List<ChatRoom> chatRoomList;

}
