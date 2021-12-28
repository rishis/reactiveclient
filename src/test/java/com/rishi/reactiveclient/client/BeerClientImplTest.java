package com.rishi.reactiveclient.client;

import com.rishi.reactiveclient.config.WebclientConfig;
import com.rishi.reactiveclient.domain.BeerDto;
import com.rishi.reactiveclient.domain.BeerPagedList;
import org.hibernate.query.criteria.internal.expression.SizeOfPluralAttributeExpression;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;


class BeerClientImplTest {

    BeerClientImpl beerClient;

    @BeforeEach
    void setUp() {
        beerClient =  new BeerClientImpl(new WebclientConfig().webClient());
    }

    @Test
    void listBeers() {
        Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(null,null,"Mango Bobs",null,null);
        BeerPagedList beerPagedList = beerPagedListMono.block();
        assertNotNull(beerPagedList);
        System.out.println("Op is -->>" + beerPagedList.toList());

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getBeerById() {
        //
        Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(null,null,"Mango Bobs",null,null);
        BeerPagedList beerPagedList = beerPagedListMono.block();
        UUID id = beerPagedList.getContent().get(0).getId();
        //
        Mono<BeerDto> beerDtoMono = beerClient.getBeerById(id,false);
        BeerDto beerDto = beerDtoMono.block();
        System.out.println("the dto is "+ beerDto);


    }



    @Test
    void createBeer() {
        BeerDto beerDto = BeerDto.builder()
                .beerName("Rishi Beers")
                .beerStyle("Delhi style")
                .upc("234848549559")
                .price(new BigDecimal("10.99"))
          //      .createdDate(OffsetDateTime.of(LocalDateTime.of(new LocalDate)))
                .build();
        System.out.println("The input in test method is "+beerDto);

       Mono<ResponseEntity<Void>> responseEntityMono = beerClient.createBeer(beerDto);
       ResponseEntity responseEntity = responseEntityMono.block();
       System.out.println("----->>>> op of insert "+responseEntity.getStatusCode());
    }

    @Test
    void updateBeer() throws Exception{
        Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(null,null,"Mango Bobs",null,null);
        BeerPagedList beerPagedList = beerPagedListMono.block();
        BeerDto reterievedBeer = beerPagedList.getContent().get(0);
        UUID id = reterievedBeer.getId();
        BeerDto updatedbeer = BeerDto.builder().beerName("Updated Delhi Beer")
                .beerStyle(reterievedBeer.getBeerStyle())
                .upc(reterievedBeer.getUpc())
                .price(reterievedBeer.getPrice())
                .build();
        System.out.println("The beer to be updated -->>"+updatedbeer);
        AtomicReference<String> statusCode = new AtomicReference<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        System.out.println("The thread at 1 is -->"+Thread.currentThread().getId());
       // final ResponseEntity responseEntity = null;

        Mono<ResponseEntity<Void>> responseEntityMono = beerClient.updateBeer(id,updatedbeer);
        assertThrows(WebClientException.class,()-> {
            System.out.println("Inside assertThrows, thread id is "+Thread.currentThread().getId());
            ResponseEntity<Void> responseEntity = responseEntityMono.block();
            statusCode.set(responseEntity.getStatusCode().toString());
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            System.out.println("The exception assert passed !!!!");
            countDownLatch.countDown();
        });
        countDownLatch.await();
        System.out.println("after countdown latch await, thread id is "+Thread.currentThread().getId());




        System.out.println("----->>>> after latch -->> "+statusCode);





    }

    @Test
    void testThreading() throws Exception {
        System.out.println("Inside method");
        AtomicReference<String> beerName = new AtomicReference<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        System.out.println("Initial Thread "+Thread.currentThread().getId());
        beerClient.listBeers(null,null,"Mango Bobs",null,null)
                .map(beerPagedList -> beerPagedList.getContent().get(0).getId())
                .map(beerId ->
                    beerClient.getBeerById(beerId,null))
                .flatMap(mono -> {
                    System.out.println("Thread in flatMap -->"+Thread.currentThread().getId());
                    return mono;
                })
                .subscribe(beerDto -> {
                    System.out.println("Thread at start of subscribe()"+Thread.currentThread().getId());
                    System.out.println("the beer name is --->>"+beerDto.getBeerName());
                    beerName.set(beerDto.getBeerName());
                    countDownLatch.countDown();
                    System.out.println("Thread in subscribe()"+Thread.currentThread().getId());
                });
        countDownLatch.await();
        System.out.println("The beer name is "+beerName);
        System.out.println("Thread after countDown latch "+Thread.currentThread().getId());

        /*
        beerClient.listBeers(null, null, "Mango Bobs", null,
                null)
                .map(beerPagedList -> beerPagedList.getContent().get(0).getId())
                .map(beerId -> beerClient.getBeerById(beerId, false))
                .flatMap(mono -> mono)
                .subscribe(beerDto -> {
                    System.out.println(beerDto.getBeerName());
                 //   beerName.set(beerDto.getBeerName());
                    assertThat(beerDto.getBeerName()).isEqualTo("Mango Bobs");
               //     countDownLatch.countDown();
                }); */

    }

    @Test
    void getBeerByUPC() {
    }
}