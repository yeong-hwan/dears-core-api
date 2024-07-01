package com.example.demo.portfolio.mapper;

import com.example.demo.portfolio.domain.Portfolio;
import com.example.demo.portfolio.dto.PortfolioDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PortfolioMapper {
    PortfolioMapper INSTANCE = Mappers.getMapper(PortfolioMapper.class);

    PortfolioDTO.Response entityToResponse(Portfolio portfolio);
    Portfolio requestToEntity(PortfolioDTO.Request portfolioRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Portfolio updateFromRequest(PortfolioDTO.Request portfolioRequest, @MappingTarget Portfolio portfolio);
}