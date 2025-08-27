package com.jhompo.model.gateways;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GenericRepository  <T , ID>  {

    Mono<T> save(T domainModel);

    Mono<T> findById(ID id);

    Flux<T> findAll();

    Mono<Void> deleteById(ID id);
}
