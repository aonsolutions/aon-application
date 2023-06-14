package net.aonsolutions.infovox.model;

import java.util.List;
import java.util.Optional;

public class OCRError {
	private OCRSeverity severity;
	private String code;
	private OCRAdditionalInfo additionalInfo;
	private String timestamp;
	private String user;
	private String  info;
	private String transactionId;
	private List<OCRField> fields;
	
	public Optional<OCRSeverity> getSeverity() {
		return Optional.ofNullable(severity);
	}
	public OCRError setSeverity(OCRSeverity severity) {
		this.severity = severity;
		return this;
	}
	
	public Optional<String> getCode() {
		return Optional.ofNullable(code);
	}
	public OCRError setCode(String code) {
		this.code = code;
		return this;
	}
	
	public Optional<OCRAdditionalInfo> getAdditionalInfo() {
		return Optional.ofNullable(additionalInfo);
	}
	public OCRError setAdditionalInfo(OCRAdditionalInfo additionalInfo) {
		this.additionalInfo = additionalInfo;
		return this;
	}
	
	public Optional<String> getTimestamp() {
		return Optional.ofNullable(timestamp);
	}
	public OCRError setTimestamp(String timestamp) {
		this.timestamp = timestamp;
		return this;
	}
	
	public Optional<String> getUser() {
		return Optional.ofNullable(user);
	}
	public OCRError setUser(String user) {
		this.user = user;
		return this;
	}
	
	public Optional<String> getInfo() {
		return Optional.ofNullable(info);
	}
	public OCRError setInfo(String info) {
		this.info = info;
		return this;
	}
	
	public Optional<String> getTransactionId() {
		return Optional.ofNullable(transactionId);
	}
	public OCRError setTransactionId(String transactionId) {
		this.transactionId = transactionId;
		return this;
	}
	
	public Optional<List<OCRField>> getFields() {
		return Optional.ofNullable(fields);
	}
	public OCRError setFields(List<OCRField> fields) {
		this.fields = fields;
		return this;
	}
}
