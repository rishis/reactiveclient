package com.rishi.reactiveclient.client;

import com.rishi.reactiveclient.config.WebclientConfig;
import com.rishi.reactiveclient.domain.BeerPagedList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;

class BeerClientImplTest {

    BeerClientImpl beerClient;

    @BeforeEach
    void setUp() {
        beerClient =  new BeerClientImpl(new WebclientConfig().webClient());
    }

    @Test
    void listBeers() {
        Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(null,null,null,null,null);
        BeerPagedList beerPagedList = beerPagedListMono.block();
        assertNotNull(beerPagedList);
        System.out.println("Op is -->>" + beerPagedList.toList());

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getBeerById() {
    }



    @Test
    void createBeer() {
    }

    @Test
    void updateBeer() {
    }

    @Test
    void deleteBeerById() {
    }

    @Test
    void getBeerByUPC() {
    }
}