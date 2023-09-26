package com.franzoia.stockservice.service.mapper;

import com.franzoia.common.dto.TransactionDTO;
import com.franzoia.common.util.AbstractMapper;
import com.franzoia.stockservice.model.Transaction;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TransactionMapper implements AbstractMapper<Transaction, TransactionDTO> {

    @Override
    public Transaction convertDtoToEntity(TransactionDTO dto) {
        return new Transaction(dto.id(), dto.date(), dto.type(), dto.productId(), dto.price(), dto.quantity());
    }

    @Override
    public List<Transaction> convertDtoToEntity(List<TransactionDTO> dto) {
        return dto.stream().map(this::convertDtoToEntity).collect(Collectors.toList());
    }

    @Override
    public TransactionDTO convertEntityToDTO(Transaction transaction) {
        return TransactionDTO.builder()
                .id(transaction.getId())
                .date(transaction.getDate())
                .type(transaction.getType())
                .productId(transaction.getProductId())
                .price(transaction.getPrice())
                .quantity(transaction.getQuantity())
                .build();
    }

    @Override
    public List<TransactionDTO> convertEntityToDTO(List<Transaction> t) {
        return t.stream().map(this::convertEntityToDTO).collect(Collectors.toList());
    }
}
