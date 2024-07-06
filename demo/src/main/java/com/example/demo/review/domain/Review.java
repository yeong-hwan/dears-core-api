package com.example.demo.review.domain;

import com.example.demo.base.BaseTimeEntity;
import com.example.demo.enums.review.RadarKey;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SQLDelete(sql = "UPDATE review SET is_deleted = true WHERE review_id = ?")
@Where(clause = "is_deleted = false")
public class Review extends BaseTimeEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "review_id")
        private Long id;

        @Builder.Default
        private boolean isDeleted = Boolean.FALSE;

        private String reviewerName;
        private String content;
        private Boolean isProvided;
        private String wroteAt;

        private Float rating;
        private Integer estimate;

        @ElementCollection
        @CollectionTable(name = "review_tags", joinColumns = @JoinColumn(name = "review_id"))
        @Column(name = "tag_value")
        private List<String> tags;
        // tag: name or code?
        // colorCode: at view or server?

        @ElementCollection
        @CollectionTable(name = "review_photos", joinColumns = @JoinColumn(name = "review_id"))
        @Column(name = "photo_url")
        private List<String> weddingPhotoUrls;

        @ElementCollection
        @CollectionTable(name = "review_radar", joinColumns = @JoinColumn(name = "review_id"))
        @MapKeyColumn(name = "radar_key")
        @Column(name = "radar_value")
        private Map<RadarKey, Float> radar;

}
