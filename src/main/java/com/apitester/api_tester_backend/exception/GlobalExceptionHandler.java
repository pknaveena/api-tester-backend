package com.apitester.api_tester_backend.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationException(
			MethodArgumentNotValidException exception,
			HttpServletRequest request) {

		String message = exception.getBindingResult()
				.getFieldErrors()
				.stream()
				.findFirst()
				.map(error -> error.getField()
						+ " "
						+ error.getDefaultMessage())
				.orElse("Validation failed");

		ErrorResponse response = ErrorResponse.builder()
				.timestamp(LocalDateTime.now())
				.status(400)
				.error("Bad Request")
				.message(message)
				.path(request.getRequestURI())
				.build();

		return ResponseEntity.badRequest()
				.body(response);
	}

	@ExceptionHandler(ApiException.class)
	public ResponseEntity<ErrorResponse> handleApiException(
			ApiException exception,
			HttpServletRequest request) {

		log.warn(
				"Business Exception: {}",
				exception.getMessage());

		ErrorResponse response = ErrorResponse.builder()
				.timestamp(LocalDateTime.now())
				.status(
						exception.getStatus()
								.value())
				.error(
						exception.getStatus()
								.getReasonPhrase())
				.message(
						exception.getMessage())
				.path(
						request.getRequestURI())
				.build();

		return ResponseEntity
				.status(exception.getStatus())
				.body(response);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGenericException(
			Exception exception,
			HttpServletRequest request) {

		log.error(
				"Unexpected Error",
				exception);

		ErrorResponse response = ErrorResponse.builder()
				.timestamp(LocalDateTime.now())
				.status(HttpStatus.INTERNAL_SERVER_ERROR
                                        .value())
				.error(HttpStatus.INTERNAL_SERVER_ERROR
                                        .getReasonPhrase())
				.message(
						"Something went wrong")
				.path(
						request.getRequestURI())
				.build();

		return ResponseEntity
				.internalServerError()
				.body(response);
	}

}
