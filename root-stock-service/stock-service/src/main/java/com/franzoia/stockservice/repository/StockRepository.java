package com.franzoia.stockservice.repository;

import com.franzoia.common.dto.StockKey;
import com.franzoia.stockservice.model.Stock;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository extends CrudRepository<Stock, StockKey> {

}
