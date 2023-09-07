package net.aonsolutions.invofox.model;

import java.io.Serializable;
import java.util.Optional;

public class OCRApprovalInfo implements Serializable {

	private static final long serialVersionUID = 7135211115643798523L;
	
	private String beginning;
	private OCRSeverity result;
	private String comments;
	private String appliedWorkflow;
	private String[] steps;
	
	public Optional<String> getBeginning() {
		return Optional.ofNullable(beginning);
	}
	public OCRApprovalInfo setBeginning(String beginning) {
		this.beginning = beginning;
		return this;
	}
	
	public Optional<OCRSeverity> getResult() {
		return Optional.ofNullable(result);
	}
	public OCRApprovalInfo setResult(OCRSeverity result) {
		this.result = result;
		return this;
	}
	
	public Optional<String> getComments() {
		return Optional.ofNullable(comments);
	}
	public OCRApprovalInfo setComments(String comments) {
		this.comments = comments;
		return this;
	}
	
	public Optional<String> getAppliedWorkflow() {
		return Optional.ofNullable(appliedWorkflow);
	}
	public OCRApprovalInfo setAppliedWorkflow(String appliedWorkflow) {
		this.appliedWorkflow = appliedWorkflow;
		return this;
	}
	
	public Optional<String[]> getSteps() {
		return Optional.ofNullable(steps);
	}
	public OCRApprovalInfo setSteps(String[] steps) {
		this.steps = steps;
		return this;
	}
	
	
}
