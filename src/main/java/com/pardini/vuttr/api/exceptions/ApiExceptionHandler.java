package com.pardini.vuttr.api.exceptions;

import java.time.OffsetDateTime;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		StandardError standardError = new StandardError(OffsetDateTime.now(), System.currentTimeMillis(),
				status.value(), status.name(), "Error when trying to validate fields", request.getContextPath());
		var fieldErrors = ex.getBindingResult().getFieldErrors();
		for (FieldError fieldError : fieldErrors) {
			var fieldErrorMessage = new FieldErrorMessage(fieldError.getField(), fieldError.getDefaultMessage());
			standardError.getErrors().add(fieldErrorMessage);
		}
		return handleExceptionInternal(ex, standardError, headers, status, request);
	}

}
