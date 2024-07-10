package com.example.demo.wishlist.repository;

import com.example.demo.wishlist.domain.WishList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WishListRepository extends JpaRepository<WishList, Long> {

    Page<WishList> findAllByMemberId(Long memberId, Pageable pageable);

    @Query("SELECT COUNT(w) FROM WishList w WHERE w.portfolio = :portfolioId")
    long countByPortfolioId(@Param("portfolioId") Long portfolioId);
}
