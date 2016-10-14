package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.mutable.MutableDouble;
import com.esferalia.aon.watson.util.AonMathUtils;

@SuppressWarnings("serial")
public class SalaryAccountEntry implements Serializable {
	
	@FunctionalInterface
	private interface IFillAccountEntryAmountVisitor {
		public void visit(AccountEntryDetail aed, SalaryAccountEntry sae,SalaryAccountEntryLine sael);
	}
	@FunctionalInterface
	private interface INetAmountCalculatorVisitor { 
		public void visit(SalaryAccountEntryLine sael,SalaryAccountEntry sae, MutableDouble netAmount);
	}
	@FunctionalInterface
	private interface ISocialInsuranceCalculatorVisitor { 
		public void visit(SalaryAccountEntryLine sael, MutableDouble socialInsAmount); 
	}
	
	public static enum SalaryAccountEntryLineType implements Serializable{
		 SALARY(
				AppParam.ACC_DEFAULT_SALARY_ACC
				, new IFillAccountEntryAmountVisitor() {
					@Override
					public void visit(AccountEntryDetail aed,SalaryAccountEntry sae, SalaryAccountEntryLine sael) {
						aed.setDebit( sael.getAmount() );
					}
				}
				, new INetAmountCalculatorVisitor() {
					@Override
					public void visit(SalaryAccountEntryLine sael,SalaryAccountEntry sae, MutableDouble netAmount) {
						netAmount.setValue(AonMathUtils.round(netAmount.getValue() + sael.getAmount()));
					}
				}
				, null 
				)
		,SALARY_IN_KIND( 
				AppParam.ACC_DEFAULT_SALARY_IK_ACC
				, new IFillAccountEntryAmountVisitor() {
					@Override
					public void visit(AccountEntryDetail aed,
							SalaryAccountEntry sae, SalaryAccountEntryLine sael) {
						aed.setDebit( sael.getAmount() );
					}
				}
				, new INetAmountCalculatorVisitor() {
					@Override
					public void visit(SalaryAccountEntryLine sael,SalaryAccountEntry sae, MutableDouble netAmount) {
						netAmount.setValue(AonMathUtils.round(netAmount.getValue() + sael.getAmount()));
					}
				}
				, null 
				)
		,ALLOWANCE(
				AppParam.ACC_DEFAULT_ALLOWANCE_ACC
				, new IFillAccountEntryAmountVisitor() {
					@Override
					public void visit(AccountEntryDetail aed,
							SalaryAccountEntry sae, SalaryAccountEntryLine sael) {
						aed.setDebit( sael.getAmount() );
					}
				}
				, new INetAmountCalculatorVisitor() {
					@Override
					public void visit(SalaryAccountEntryLine sael,SalaryAccountEntry sae, MutableDouble netAmount) {
						netAmount.setValue(AonMathUtils.round(netAmount.getValue() + sael.getAmount()));
					}
				}
				, null 
				)				
		,COMPENSATION(
				AppParam.ACC_DEFAULT_COMPENSATION_ACC
				, new IFillAccountEntryAmountVisitor() {
					@Override
					public void visit(AccountEntryDetail aed,
							SalaryAccountEntry sae, SalaryAccountEntryLine sael) {
						aed.setDebit( sael.getAmount() );
					}
				}
				, new INetAmountCalculatorVisitor() {
					@Override
					public void visit(SalaryAccountEntryLine sael,SalaryAccountEntry sae, MutableDouble netAmount) {
						netAmount.setValue(AonMathUtils.round(netAmount.getValue() + sael.getAmount()));
					}
				}
				, null 
				)
		,RETENTION(
				AppParam.ACC_SALARY_CHARGED_RET_ACC
				, new IFillAccountEntryAmountVisitor() {
					@Override
					public void visit(AccountEntryDetail aed,
							SalaryAccountEntry sae, SalaryAccountEntryLine sael) {
						aed.setCredit( sael.getAmount() );
					}
				}
				, new INetAmountCalculatorVisitor() {
					@Override
					public void visit(SalaryAccountEntryLine sael,SalaryAccountEntry sae, MutableDouble netAmount) {
						netAmount.setValue(AonMathUtils.round(netAmount.getValue() - sael.getAmount()));
					}
				}
				, null 
				)
		,RETENTION_IN_KIND(
				AppParam.ACC_SALARY_CHARGED_RET_IK_ACC
				, new IFillAccountEntryAmountVisitor() {
					@Override
					public void visit(AccountEntryDetail aed,
							SalaryAccountEntry sae, SalaryAccountEntryLine sael) {
						aed.setCredit( sael.getAmount() );
					}
				}
				, new INetAmountCalculatorVisitor() {
					@Override
					public void visit(SalaryAccountEntryLine sael,SalaryAccountEntry sae, MutableDouble netAmount) {
						netAmount.setValue(AonMathUtils.round(netAmount.getValue() - sael.getAmount()));
					}
				}
				, null 
				)
		,EMPLOYEE_SOC_INS(
				AppParam.ACC_DEFAULT_SOCIAL_INSURANCE_ACC
				, new IFillAccountEntryAmountVisitor() {
					@Override
					public void visit(AccountEntryDetail aed,
							SalaryAccountEntry sae, SalaryAccountEntryLine sael) {
						aed.setCredit( sae.getSocialInsuranceAmount() );
					}
				}
				, new INetAmountCalculatorVisitor() {
					@Override
					public void visit(SalaryAccountEntryLine sael,SalaryAccountEntry sae, MutableDouble netAmount) {
						netAmount.setValue(AonMathUtils.round(netAmount.getValue() - sae.getSocialInsuranceAmount()));
					}
				}
				, new ISocialInsuranceCalculatorVisitor() {

					@Override
					public void visit(SalaryAccountEntryLine sael,MutableDouble socialInsAmount) {
						socialInsAmount.setValue(AonMathUtils.round(socialInsAmount.getValue() + sael.getAmount()));
					}

				}
				)
		,COMPANY_SOC_INS(
				AppParam.ACC_DEFAULT_COMPANY_SOC_INS_ACC
				, new IFillAccountEntryAmountVisitor() {
					@Override
					public void visit(AccountEntryDetail aed,
							SalaryAccountEntry sae, SalaryAccountEntryLine sael) {
						aed.setDebit( sael.getAmount() );
					}
				}
				, new INetAmountCalculatorVisitor() {
					@Override
					public void visit(SalaryAccountEntryLine sael,SalaryAccountEntry sae, MutableDouble netAmount) {
						netAmount.setValue(AonMathUtils.round(netAmount.getValue() + sael.getAmount()));
					}
				}
				, new ISocialInsuranceCalculatorVisitor() {

					@Override
					public void visit(SalaryAccountEntryLine sael,MutableDouble socialInsAmount) {
						socialInsAmount.setValue(AonMathUtils.round(socialInsAmount.getValue() + sael.getAmount()));
					}

				}
				)
		,DED_ADVANCE_PAYMENT (
				AppParam.ACC_SALARY_DED_ADV_PAYMENT_ACC
				, new IFillAccountEntryAmountVisitor() {
					@Override
					public void visit(AccountEntryDetail aed,
							SalaryAccountEntry sae, SalaryAccountEntryLine sael) {
						aed.setCredit( sael.getAmount() );
					}
				}
				, null 
				, null 
				)
		,DED_SEIZE (
				AppParam.ACC_SALARY_DED_SEIZE_ACC
				, new IFillAccountEntryAmountVisitor() {
					@Override
					public void visit(AccountEntryDetail aed,
							SalaryAccountEntry sae, SalaryAccountEntryLine sael) {
						aed.setCredit( sael.getAmount() );
					}
				}
				, null 
				, null 
				)
		,DED_IN_KIND (
				AppParam.ACC_DEFAULT_SALARY_IK_ACC
				, new IFillAccountEntryAmountVisitor() {
					@Override
					public void visit(AccountEntryDetail aed,
							SalaryAccountEntry sae, SalaryAccountEntryLine sael) {
						aed.setCredit( sael.getAmount() );
					}
				}
				, null 
				, null 
				)
		,DED_OTHER (
				AppParam.ACC_SALARY_DED_OTHER_ACC
				, new IFillAccountEntryAmountVisitor() {
					@Override
					public void visit(AccountEntryDetail aed,
							SalaryAccountEntry sae, SalaryAccountEntryLine sael) {
						aed.setCredit( sael.getAmount() );
					}
				}
				, null 
				, null 
				)
		,DEFAULT_PENDING_SALARY(
				AppParam.ACC_DEFAULT_PENDING_SALARY_ACC
				, new IFillAccountEntryAmountVisitor() {
					@Override
					public void visit(AccountEntryDetail aed,
							SalaryAccountEntry sae, SalaryAccountEntryLine sael) {
						aed.setCredit( sael.getAmount() );
					}
				}
				, null 
				, null 
				)
		,BANK_ACCOUNT(
				null
				, new IFillAccountEntryAmountVisitor() {
					@Override
					public void visit(AccountEntryDetail aed,
							SalaryAccountEntry sae, SalaryAccountEntryLine sael) {
						aed.setCredit( sael.getAmount() );
					}
				}
				, null 
				, null 
				)
		;
		private IFillAccountEntryAmountVisitor fillAccountEntryVisitor;
		private INetAmountCalculatorVisitor netAmountVisitor;
		private ISocialInsuranceCalculatorVisitor socialInsuranceCalculatorVisitor;
		
		private AppParam param;
		
		private SalaryAccountEntryLineType(AppParam param
				, IFillAccountEntryAmountVisitor visitor 
				, INetAmountCalculatorVisitor netAmountVisitor
				, ISocialInsuranceCalculatorVisitor socialInsuranceCalculatorVisitor) {
			this.fillAccountEntryVisitor = visitor;	
			this.netAmountVisitor = netAmountVisitor;
			this.socialInsuranceCalculatorVisitor = socialInsuranceCalculatorVisitor;
			this.param = param;
		}
		public void visitFillAccountEntry(AccountEntryDetail accountEntryDetail, SalaryAccountEntry sae, SalaryAccountEntryLine sael){
			if (fillAccountEntryVisitor != null) {
				fillAccountEntryVisitor.visit( accountEntryDetail , sae, sael );
			}
		};
		public void visitNetAmountCalculator(SalaryAccountEntryLine sael,SalaryAccountEntry sae, MutableDouble netAmount){
			if (netAmountVisitor != null) {
				netAmountVisitor.visit( sael,sae,netAmount );
			}
		};
		public void visitSocialInsuranceCalculator(SalaryAccountEntryLine salaryAccountEntryLine, MutableDouble netAmount){
			if (socialInsuranceCalculatorVisitor != null) {
				socialInsuranceCalculatorVisitor.visit( salaryAccountEntryLine, netAmount );
			}
		};
		public AppParam getParam() {
			return param;
		}
	}
	
	public static class SalaryAccountEntryLine implements Serializable{
		private static final long serialVersionUID = 2189133414469007321L;
	
		private SalaryAccountEntryLineType type;
		private Integer account;
		private double amount;
		public SalaryAccountEntryLine(SalaryAccountEntryLineType type,Integer account, BigDecimal amount) {
			this(type,account,amount==null?0:amount.doubleValue());
		}
		
		public SalaryAccountEntryLine(SalaryAccountEntryLineType type,Integer account, double amount) {
			this.type = type;
			this.account = account;
			this.amount = AonMathUtils.round(amount);
		}

		public SalaryAccountEntryLineType getType() {
			return type;
		}
		public Integer getAccount() {
			return account;
		}
		public double getAmount() {
			return amount;
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
	public SalaryAccountEntry setDate(Date date) {
		this.date = date;
		return this;
	}

	public Integer getRegistryBank() {
		return registryBank;
	}
	public SalaryAccountEntry setRegistryBank(Integer registryBank) {
		this.registryBank = registryBank;
		return this;
	}
	
	public String getConcept() {
		return concept;
	}
	public SalaryAccountEntry setConcept(String concept) {
		this.concept = concept;
		return this;
	}

	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public SalaryAccountEntry setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
		return this;
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

	public double getNetAmount() {
		final MutableDouble netAmount = new MutableDouble(0.0);
		for (SalaryAccountEntryLine sael : lines ) {
			sael.getType().visitNetAmountCalculator(sael,SalaryAccountEntry.this, netAmount);
		}
		return netAmount.getValue();
	}

	public double getSocialInsuranceAmount() {
		final MutableDouble socInsAmount = new MutableDouble(0.0);
		for (SalaryAccountEntryLine sael : lines ) {
			sael.getType().visitSocialInsuranceCalculator(sael,socInsAmount);
		}
		return socInsAmount.getValue();
	}
	
}