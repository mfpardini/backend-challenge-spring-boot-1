package com.pardini.vuttr.api.exceptions;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class ValidationError extends StandardError {

	private static final long serialVersionUID = 1L;

	private List<FieldErrorMessage> fieldErrors = new ArrayList<>();

	public ValidationError(OffsetDateTime datetime, Long unixTimestamp, Integer status, String error, String message,
			String path) {
		super(datetime, unixTimestamp, status, error, message, path);
	}

	public List<FieldErrorMessage> getFieldErrors() {
		return fieldErrors;
	}

	public void setFieldErrors(List<FieldErrorMessage> fieldErrors) {
		this.fieldErrors = fieldErrors;
	}
	
	public void addError(String fieldName, String message) {
		this.fieldErrors.add(new FieldErrorMessage(fieldName, message));
	}

}
