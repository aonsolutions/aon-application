package net.aonsolutions.infovox.model;

import java.util.List;
import java.util.Optional;

public class OCRValidationInfo {

	private List<OCRError> errors;
	private OCRSeverity result;
	private String comments;
	private String validator;
	
	public Optional<List<OCRError>> getErrors() {
		return Optional.ofNullable(errors);
	}
	public OCRValidationInfo setErrors(List<OCRError> errors) {
		this.errors = errors;
		return this;
	}
	
	public Optional<OCRSeverity> getResult() {
		return Optional.ofNullable(result);
	}
	public OCRValidationInfo setResult(OCRSeverity result) {
		this.result = result;
		return this;
	}
	
	public Optional<String> getComments() {
		return Optional.ofNullable(comments);
	}
	public OCRValidationInfo setComments(String comments) {
		this.comments = comments;
		return this;
	}
	
	public Optional<String> getValidator() {
		return Optional.ofNullable(validator);
	}
	public OCRValidationInfo setValidator(String validator) {
		this.validator = validator;
		return this;
	}
	
}
