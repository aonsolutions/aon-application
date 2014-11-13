package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;

public class SalaryAccountEntry implements Serializable {
	private static final long serialVersionUID = 4147035089236493960L;
	
	@FunctionalInterface
	public interface ISalaryAccountEntryLineTypeVisitor {
		public void visit(AccountEntryDetail accountEntryDetail, SalaryAccountEntryLine salaryAccountEntryLine);
	}
	
	public static enum SalaryAccountEntryLineType implements Serializable{
		 SALARY(
				 AppParam.ACC_DEFAULT_SALARY_ACC
				 , (aed, sael) -> aed.setDebit( sael.getAmount() ))
		,SALARY_IN_KIND( 
				AppParam.ACC_DEFAULT_SALARY_IK_ACC
				, (aed, sael) -> aed.setDebit( sael.getAmount() ))
		,ALLOWANCE(
				AppParam.ACC_DEFAULT_ALLOWANCE_ACC
				,  (aed, sael) -> aed.setDebit( sael.getAmount() ))
		,COMPENSATION(
				AppParam.ACC_DEFAULT_COMPENSATION_ACC
				,  (aed, sael) -> aed.setDebit( sael.getAmount() ))
		,RETENTION(
				AppParam.ACC_SALARY_CHARGED_RET_ACC
				,  (aed, sael) -> aed.setCredit( sael.getAmount() ))
		,RETENTION_IN_KIND(
				AppParam.ACC_SALARY_CHARGED_RET_IK_ACC
				,  (aed, sael) -> aed.setCredit( sael.getAmount() ))
		,EMPLOYEE_SOC_INS(
				AppParam.ACC_DEFAULT_SOCIAL_INSURANCE_ACC
				,  (aed, sael) -> aed.setCredit( sael.getAmount() ))
		,COMPANY_SOC_INS(
				AppParam.ACC_DEFAULT_COMPANY_SOC_INS_ACC
				,  (aed, sael) -> aed.setDebit( sael.getAmount() ))
		;
		private ISalaryAccountEntryLineTypeVisitor visitor;
		private AppParam param;
		
		private SalaryAccountEntryLineType(AppParam param, ISalaryAccountEntryLineTypeVisitor visitor) {
			this.visitor = visitor;	
			this.param = param;
		}
		public void visit(AccountEntryDetail accountEntryDetail, SalaryAccountEntryLine salaryAccountEntryLine){
			visitor.visit( accountEntryDetail , salaryAccountEntryLine );
		};
	}
	
	public static class SalaryAccountEntryLine implements Serializable{
		private static final long serialVersionUID = 2189133414469007321L;
	
		private SalaryAccountEntryLineType type;
		private Integer account;
		private double amount;
		
		public SalaryAccountEntryLineType getType() {
			return type;
		}
		public void setType(SalaryAccountEntryLineType type) {
			this.type = type;
		}
		public Integer getAccount() {
			return account;
		}
		public void setAccount(Integer account) {
			this.account = account;
		}
		public double getAmount() {
			return amount;
		}
		public void setAmount(double amount) {
			this.amount = amount;
		}
	}
	
	
	private Date date;
	private String concept;
	private Integer registryBank;
	private SecurityLevel securityLevel;
	private List<SalaryAccountEntryLine> lines;
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

	public Integer getRegistryBank() {
		return registryBank;
	}
	public void setRegistryBank(Integer registryBank) {
		this.registryBank = registryBank;
	}
	
	public String getConcept() {
		return concept;
	}
	public void setConcept(String concept) {
		this.concept = concept;
	}

	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}
	public List<SalaryAccountEntryLine> getLines() {
		return lines;
	}
	public void setLines(List<SalaryAccountEntryLine> lines) {
		this.lines = lines;
	}
	public SalaryAccountEntry addLine(SalaryAccountEntryLine line) {
		if (this.lines == null) {
			this.lines = new LinkedList<SalaryAccountEntryLine>();
		}
		this.lines.add(line);
		return this;
	}
}