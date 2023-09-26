package com.franzoia.stockservice.repository;

import com.franzoia.common.dto.TransactionType;
import com.franzoia.stockservice.model.Transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long> {

    List<Transaction> findAllByProductIdOrderByDate(final Long productId);

    List<Transaction> findAllByDateBetweenOrderByDate(final LocalDate startDate, final LocalDate finalDate);

    List<Transaction> findAllByDateBetweenAndProductIdOrderByDate(final LocalDate startDate, final LocalDate finalDate, Long productId);

    List<Transaction> findAllByTypeOrderByDate(final TransactionType type);

}
