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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

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
												  @RequestParam(value = "endDate", required = false) final String endDate) throws InvalidRequestException {
		LocalDate start, end;
		if (startDate == null) {
			start = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1);
		} else {
			start = LocalDate.parse(startDate);
		}
		if (endDate == null) {
			end = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), LocalDate.now().lengthOfMonth());
		} else {
			end = LocalDate.parse(endDate);
		}
		if (end.isBefore(start)) {
			throw new InvalidRequestException("End date cannot be before start date");
		}
		return transactionService.listByDates(start, end);
	}

	@GetMapping("/datesAndProduct/{productId}")
	public List<TransactionDTO> findByDateBetweenAndProduct(@RequestParam(value = "startDate", required = false) final String startDate,
															@RequestParam(value = "endDate", required = false) final String endDate,
															@PathVariable("productId") final Long productId)
			throws ServiceNotAvailableException, EntityNotFoundException {
		LocalDate start, end;
		if (startDate == null) {
			start = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1);
		} else {
			start = LocalDate.parse(startDate);
		}
		if (endDate == null) {
			end = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), LocalDate.now().lengthOfMonth());
		} else {
			end = LocalDate.parse(endDate);
		}
		if (end.isBefore(start)) {
			throw new InvalidRequestException("End date cannot be before start date");
		}
		return transactionService.listByDatesAndProduct(start, end, productId);
    }

	@GetMapping("/type/{type}")
	public List<TransactionDTO> findType(@PathVariable final TransactionType type) {
		return transactionService.listByTpe(type);
}

	@GetMapping
	public List<TransactionDTO> findAllTransaction() {
		return transactionService.listAllOrderByDate();
	}
}
