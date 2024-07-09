package com.example.demo.portfolio;

import com.example.demo.portfolio.domain.Portfolio;
import com.example.demo.portfolio.repository.PortfolioRepository;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@DataJpaTest //JPA Repository들에 대한 빈 등록하여 테스트 가능
@DisplayName("Portfolio Test")
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PortfolioRepositoryTest {

    @Autowired
    private PortfolioRepository portfolioRepository;


    @Before
    void setUp() {
    }

    @Test
    @DisplayName("포트폴리오 전체 조회 테스트")
    void findAllByPage() {
        PageRequest pageRequest = PageRequest.of(0, 1, Sort.by("id").ascending());

        Page<Portfolio> portfolios = portfolioRepository.findAll(pageRequest);

        //junit
        Assertions.assertThat(portfolios.getSize()).isEqualTo(1);
    }
}
