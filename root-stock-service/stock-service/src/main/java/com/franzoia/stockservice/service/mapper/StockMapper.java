package com.franzoia.stockservice.service.mapper;

import com.franzoia.common.dto.StockDTO;
import com.franzoia.common.dto.StockKey;
import com.franzoia.common.util.AbstractMapper;
import com.franzoia.stockservice.model.Stock;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class StockMapper implements AbstractMapper<Stock, StockDTO> {

    @Override
    public Stock convertDtoToEntity(StockDTO dto) {
        StockKey key = new StockKey(dto.getKey().getYearMonth(), dto.getKey().getProductId());
        return new Stock(key, dto.getPreviousBalance(), dto.getInputs(), dto.getOutputs(), dto.getCurrentBalance());
    }

    @Override
    public List<Stock> convertDtoToEntity(List<StockDTO> dto) {
        return dto.stream().map(this::convertDtoToEntity).collect(Collectors.toList());
    }

    @Override
    public StockDTO convertEntityToDTO(Stock stock) {
        return StockDTO.builder()
                .key(stock.getKey())
                .inputs(stock.getInputs())
                .outputs(stock.getOutputs())
                .previousBalance(stock.getPreviousBalance())
                .currentBalance(stock.getCurrentBalance())
                .build();
    }

    @Override
    public List<StockDTO> convertEntityToDTO(List<Stock> t) {
        return t.stream().map(this::convertEntityToDTO).collect(Collectors.toList());
    }
}
