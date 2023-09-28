package com.franzoia.stockservice.repository;

import com.franzoia.common.dto.StockKey;
import com.franzoia.stockservice.model.Stock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends CrudRepository<Stock, StockKey> {

    @Query(value = "SELECT s " +
                    "FROM Stock s " +
                    "ORDER BY s.key.yearMonth ASC, s.key.productId ASC")
    List<Stock> findAllOrderByYearMonthAndProductId();

    @Query(value = "SELECT s " +
                   "FROM Stock s " +
                   "WHERE s.key.productId = :product " +
                   "ORDER BY s.key.yearMonth")
    List<Stock> findAllByProductId(@Param("product") final Long productId);

    @Query(value = "SELECT s " +
                   "FROM Stock s " +
                   "WHERE s.key.yearMonth = :period " +
                   "ORDER BY s.key.productId")
    List<Stock> findAllByYearMonth(@Param("period") final String productId);
}
