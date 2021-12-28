package com.rishi.reactiveclient.client;

import com.rishi.reactiveclient.config.WebClientProperties;
import com.rishi.reactiveclient.domain.BeerDto;
import com.rishi.reactiveclient.domain.BeerPagedList;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BeerClientImpl implements BeerClient {

    private final WebClient webClient;
    @Override
    public Mono<BeerDto> getBeerById(UUID id, Boolean showInventoryOnHand) {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(WebClientProperties.BASE_V1_PATH + "/" + id.toString())
                    .queryParamIfPresent("showInventoryOnHand", Optional.ofNullable(showInventoryOnHand))
                    .build()
                )
                .retrieve()
               .bodyToMono(BeerDto.class);
    }

    @Override
    public Mono<BeerPagedList> listBeers(Integer pageNumber, Integer pageSize, String beerName, String beerStyle, Boolean showInventoryOnhand) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(WebClientProperties.BASE_V1_PATH)
                        .queryParamIfPresent("pageNumber",Optional.ofNullable(pageNumber))
                        .queryParamIfPresent("pageSize",Optional.ofNullable(pageSize))
                        .queryParamIfPresent("beerName",Optional.ofNullable(beerName))
                        .queryParamIfPresent("beerStyle",Optional.ofNullable(beerStyle))
                        .queryParamIfPresent("showInventoryOnhand",Optional.ofNullable(showInventoryOnhand))
                        .build()
                )
                .retrieve()
                .bodyToMono(BeerPagedList.class);
    }

    @Override
    public Mono<ResponseEntity<Void>> createBeer(BeerDto beerDto) {
        return webClient.post().uri(uriBuilder -> uriBuilder.path(WebClientProperties.BASE_V1_PATH).build())
                .body(BodyInserters.fromValue(beerDto))
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public Mono<ResponseEntity<Void>> updateBeer(UUID id, BeerDto beerDto) {
        return webClient.put().uri(uriBuilder -> uriBuilder.path(WebClientProperties.BASE_V1_PATH).build(id))
                .body(BodyInserters.fromValue(beerDto))
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public Mono<ResponseEntity> deleteBeerById(UUID id) {
        return null;
    }

    @Override
    public Mono<BeerDto> getBeerByUPC(String upc) {
        return null;
    }
}
