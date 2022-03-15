package com.pardini.vuttr.api.exceptions;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class StandardError implements Serializable {

	private static final long serialVersionUID = 1L;

	private OffsetDateTime datetime;
	private Long unixTimestamp;
	private Integer status;
	private String error;
	private String message;
	private String path;
	private List<FieldErrorMessage> errors = new ArrayList<>();

	public StandardError(OffsetDateTime datetime, Long unixTimestamp, Integer status, String error, String message,
			String path) {
		super();
		this.datetime = datetime;
		this.unixTimestamp = unixTimestamp;
		this.status = status;
		this.error = error;
		this.message = message;
		this.path = path;
	}

	public OffsetDateTime getDatetime() {
		return datetime;
	}

	public void setDatetime(OffsetDateTime datetime) {
		this.datetime = datetime;
	}

	public Long getUnixTimestamp() {
		return unixTimestamp;
	}

	public void setUnixTimestamp(Long unixTimestamp) {
		this.unixTimestamp = unixTimestamp;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<FieldErrorMessage> getErrors() {
		return errors;
	}

	public void setErrors(List<FieldErrorMessage> errors) {
		this.errors = errors;
	}

}
