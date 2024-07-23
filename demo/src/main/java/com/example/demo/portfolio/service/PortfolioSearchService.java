package com.example.demo.portfolio.service;

import com.example.demo.portfolio.domain.Portfolio;
import com.example.demo.portfolio.dto.PortfolioSearchDTO;
import com.example.demo.portfolio.mapper.PortfolioMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.*;
import org.opensearch.client.opensearch.core.search.Hit;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PortfolioSearchService {

    private final OpenSearchClient openSearchClient;
    private static final String indexName = "portfolio";
    private final PortfolioMapper portfolioMapper;

    public PortfolioSearchService(OpenSearchClient openSearchClient, PortfolioMapper portfolioMapper) {
        this.openSearchClient = openSearchClient;
        this.portfolioMapper = portfolioMapper;
    }

    //인덱스 생성
    public void createIndex() {
        try {
            CreateIndexRequest request = CreateIndexRequest.of(builder -> builder.index(indexName));
            openSearchClient.indices().create(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void indexDocumentUsingDTO(Portfolio portfolio) {

        PortfolioSearchDTO.Request request = portfolioMapper.entityToSearchRequest(portfolio);
        try {
            IndexRequest<PortfolioSearchDTO.Request> indexRequest = IndexRequest.of(builder ->
                    builder.index(indexName)
                            .id(String.valueOf(request.getId()))
                            .document(request)
            );
            IndexResponse response = openSearchClient.index(indexRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateDocumentUsingDTO(Portfolio portfolio){

        PortfolioSearchDTO.Request request = portfolioMapper.entityToSearchRequest(portfolio);

        try {
            UpdateRequest<PortfolioSearchDTO.Request, Object> updateRequest = UpdateRequest.of(builder ->
                    builder.index(indexName)
                            .id(String.valueOf(portfolio.getId()))
                            .doc(request)
            );

            UpdateResponse updateResponse = openSearchClient.update(updateRequest, PortfolioSearchDTO.Request.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<PortfolioSearchDTO.Response> search(String keyword) {
        List<PortfolioSearchDTO.Response> resultList = new ArrayList<>();

        try {
            SearchRequest request = SearchRequest.of(searchRequest ->
                searchRequest.index(indexName)
                    .query(query ->
                        query.bool(bool ->
                            bool.should(should ->
                                should.wildcard(wildcard ->
                                    wildcard.field("services")
                                        .value("*" + keyword + "*")
                                )
                            )
                            .should(should ->
                                should.wildcard(wildcard ->
                                    wildcard.field("plannerName")
                                    .value("*" + keyword + "*")
                                )
                            )
                            .should(should ->
                                should.wildcard(wildcard ->
                                    wildcard.field("organization")
                                    .value("*" + keyword + "*")
                                )
                            )
                            .should(should ->
                                should.wildcard(wildcard ->
                                    wildcard.field("introduction")
                                        .value("*" + keyword + "*")
                                )
                            )
                        )
                    )
            );

            SearchResponse<PortfolioSearchDTO.Request> response = openSearchClient.search(request, PortfolioSearchDTO.Request.class);
            List<Hit<PortfolioSearchDTO.Request>> hits = response.hits().hits();
            for (Hit<PortfolioSearchDTO.Request> hit : hits) {
                resultList.add(portfolioMapper.requestToSearchResponse(hit.source()));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }

    public void deleteDocumentById(Long id) {
        try {
            DeleteRequest deleteRequest = DeleteRequest.of(builder ->
                    builder.index(indexName)
                            .id(String.valueOf(id))
            );
            DeleteResponse response = openSearchClient.delete(deleteRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

