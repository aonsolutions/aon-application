package net.aonsolutions.infovox.model;

import java.util.List;
import java.util.Optional;

public class OCRDocument {
	
	private String id;
	private String account;
	private String environment;
	private String company;
	private String creator;
	private List<OCRClientData> clientData;
	private OCRType type;
	private String name;
	private String creation;
	private String[] images;
	private String mimeType;
	private OCRSeverity publicState;
	private OCRConfidence confidence;
	private OCRValidationInfo validationInfo;
	private OCRApprovalInfo approvalInfo;
	private String[] approvedBy;
	private String[] requiredSigns;
	private OCRImportData importData;
	private String[] exports;
	private OCRGeometry geometry;
	
	private OCRInvoice data;
	
	public Optional<String> getId() {
		return Optional.ofNullable(id);
	}
	public OCRDocument setId(String id) {
		this.id = id;
		return this;
	}
	
	public Optional<String> getAccount() {
		return Optional.ofNullable(account);
	}
	public OCRDocument setAccount(String account) {
		this.account = account;
		return this;
	}
	
	public Optional<String> getEnvironment() {
		return Optional.ofNullable(environment);
	}
	public OCRDocument setEnvironment(String environment) {
		this.environment = environment;
		return this;
	}
	
	public Optional<String> getCompany() {
		return Optional.ofNullable(company);
	}
	public OCRDocument setCompany(String company) {
		this.company = company;
		return this;
	}
	
	public Optional<String> getCreator() {
		return Optional.ofNullable(creator);
	}
	public OCRDocument setCreator(String creator) {
		this.creator = creator;
		return this;
	}
	
	public Optional<List<OCRClientData>> getClientData() {
		return Optional.ofNullable(clientData);
	}
	public OCRDocument setClientData(List<OCRClientData> clientData) {
		this.clientData = clientData;
		return this;
	}
	
	public Optional<OCRType> getType() {
		return Optional.ofNullable(type);
	}
	public OCRDocument setType(OCRType type) {
		this.type = type;
		return this;
	}
	
	public Optional<String> getName() {
		return Optional.ofNullable(name);
	}
	public OCRDocument setName(String name) {
		this.name = name;
		return this;
	}
	
	public Optional<String> getCreation() {
		return Optional.ofNullable(creation);
	}
	public OCRDocument setCreation(String creation) {
		this.creation = creation;
		return this;
	}
	
	public Optional<String[]> getImages() {
		return Optional.ofNullable(images);
	}
	public OCRDocument setImages(String[] images) {
		this.images = images;
		return this;
	}
	
	public Optional<String> getMimeType() {
		return Optional.ofNullable(mimeType);
	}
	public OCRDocument setMimeType(String mimeType) {
		this.mimeType = mimeType;
		return this;
	}
	
	public Optional<OCRInvoice> getData() {
		return Optional.ofNullable(data);
	}
	public OCRDocument setData(OCRInvoice data) {
		this.data = data;
		return this;
	}
	
	public Optional<OCRSeverity> getPublicState() {
		return Optional.ofNullable(publicState);
	}
	public OCRDocument setPublicState(OCRSeverity publicState) {
		this.publicState = publicState;
		return this;
	}
	
	public Optional<OCRConfidence> getConfidence() {
		return Optional.ofNullable(confidence);
	}
	public OCRDocument setConfidence(OCRConfidence confidence) {
		this.confidence = confidence;
		return this;
	}
	
	public Optional<OCRValidationInfo> getValidationInfo() {
		return Optional.ofNullable(validationInfo);
	}
	public OCRDocument setValidationInfo(OCRValidationInfo validationInfo) {
		this.validationInfo = validationInfo;
		return this;
	}
	
	public Optional<OCRApprovalInfo> getApprovalInfo() {
		return Optional.ofNullable(approvalInfo);
	}
	public OCRDocument setApprovalInfo(OCRApprovalInfo approvalInfo) {
		this.approvalInfo = approvalInfo;
		return this;
	}
	
	public Optional<String[]> getApprovedBy() {
		return Optional.ofNullable(approvedBy);
	}
	public OCRDocument setApprovedBy(String[] approvedBy) {
		this.approvedBy = approvedBy;
		return this;
	}
	
	public Optional<String[]> getRequiredSigns() {
		return Optional.ofNullable(requiredSigns);
	}
	public OCRDocument setRequiredSigns(String[] requiredSigns) {
		this.requiredSigns = requiredSigns;
		return this;
	}
	
	public Optional<OCRImportData> getImportData() {
		return Optional.ofNullable(importData);
	}
	public OCRDocument setImportData(OCRImportData importData) {
		this.importData = importData;
		return this;
	}
	
	public Optional<String[]> getExports() {
		return Optional.ofNullable(exports);
	}
	public OCRDocument setExports(String[] exports) {
		this.exports = exports;
		return this;
	}
	
	public Optional<OCRGeometry> getGeometry() {
		return Optional.ofNullable(geometry);
	}
	public OCRDocument setGeometry(OCRGeometry geometry) {
		this.geometry = geometry;
		return this;
	}
}
