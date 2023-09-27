package com.franzoia.stockservice.controller;

import com.franzoia.common.dto.TransactionDTO;
import com.franzoia.common.dto.TransactionType;
import com.franzoia.common.exception.ConstraintsViolationException;
import com.franzoia.common.exception.EntityNotFoundException;
import com.franzoia.common.exception.InvalidRequestException;
import com.franzoia.common.exception.ServiceNotAvailableException;
import com.franzoia.common.util.ErrorResponse;
import com.franzoia.stockservice.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * All operations with a transaction will be routed by this controller.
 * <p/>
 */
@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

	private final TransactionService transactionService;

	@Autowired
	public TransactionController(final TransactionService transactionsService) {
		this.transactionService = transactionsService;
	}

	@GetMapping("/{transactionId}")
	public TransactionDTO getTransaction(@PathVariable final long transactionId) throws EntityNotFoundException {
		return transactionService.getMapper().convertEntityToDTO(transactionService.find(transactionId));
	}

	@PostMapping
	public TransactionDTO createTransaction(@RequestBody final TransactionDTO transactionsDTO)
			throws ServiceNotAvailableException, EntityNotFoundException, ConstraintsViolationException {
		return transactionService.create(transactionsDTO);
    }

	@DeleteMapping("/{transactionId}")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public ErrorResponse deleteTransaction(@PathVariable final long transactionId) throws EntityNotFoundException {
		transactionService.delete(transactionId);
		return ErrorResponse.builder()
				.message("Transaction successfully deleted")
				.build();
	}

	@GetMapping("/dates")
	public List<TransactionDTO> findByDateBetween(@RequestParam(value = "startDate", required = false) final String startDate,
												  @RequestParam(value = "endDate", required = false) final String endDate)
			throws InvalidRequestException {
		TransactionService.ValidDates validDates = transactionService.getValidDates(startDate, endDate);
		return transactionService.listByDates(validDates.start(), validDates.end());
	}

	@GetMapping("/datesAndProduct/{productId}")
	public List<TransactionDTO> findByDateBetweenAndProduct(@RequestParam(value = "startDate", required = false) final String startDate,
															@RequestParam(value = "endDate", required = false) final String endDate,
															@PathVariable("productId") final Long productId)
			throws ServiceNotAvailableException, EntityNotFoundException {
		TransactionService.ValidDates validDates = transactionService.getValidDates(startDate, endDate);
		return transactionService.listByDatesAndProduct(validDates.start(), validDates.end(), productId);
    }

	@GetMapping("/type/{type}")
	public Map<TransactionType, List<TransactionDTO>> findType(@PathVariable final TransactionType type) {
		return transactionService.listByTpe(type);
}

	@GetMapping
	public List<TransactionDTO> findAllTransaction() {
		return transactionService.listAllOrderByDate();
	}


}
