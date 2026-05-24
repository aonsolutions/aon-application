package com.esferalia.aon.gwt.payroll.util;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Salary.Type;
import com.esferalia.aon.gwt.payroll.shared.Salary.TypeVisitor;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.cost.Costs;
import com.esferalia.aon.salary.data.IData;
import com.esferalia.aon.salary.deduction.Deductions;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.salary.payment.Payments;

public class SalaryDraftUtils {
	
	private SalaryDraftUtils() {
	}
	
	
	
	public static ISalary asSalary(SalaryDraft salaryDraft) {
		return new ISalary() {
			
			@Override
			public boolean isFullTime() {
				// TODO: UnsupportedOperationException
				throw new UnsupportedOperationException();
			}
			
			@Override
			public Integer getRegistration() {
				// TODO: UnsupportedOperationException
				throw new UnsupportedOperationException();
			}
			
			@Override
			public Date getSeniorityDate() {
				// TODO: UnsupportedOperationException
				throw new UnsupportedOperationException();
			}
			
			@Override
			public Payments getPayments() throws SalaryException {
				// TODO: UnsupportedOperationException
				throw new UnsupportedOperationException();
			}
			
			@Override
			public Costs getEnterpriseCosts() throws SalaryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Deductions getDeductions() throws SalaryException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public SalaryType getType() {
				Type type = salaryDraft.getType();
				return type.accept( new TypeVisitor<SalaryType>() {

					@Override
					public SalaryType visitSalary(Type type) {
						return SalaryType.SALARY;
					}

					@Override
					public SalaryType visitExtra(Type type) {
						return SalaryType.EXTRA;
					}

					@Override
					public SalaryType visitSettle(Type type) {
						return SalaryType.SETTLE;
					}

					@Override
					public SalaryType visitDelay(Type type) {
						return SalaryType.DELAY;
					}
					
					@Override
					public SalaryType visitProcedural(Type type) {
						return SalaryType.PROCEDURAL;
					}
				});
			}
			
			@Override
			public Double getTotalPayment() {
				return salaryDraft.getTotalPayment();
			}
			
			@Override
			public Double getTotalLiquid() {
				return salaryDraft.getTotalLiquid();
			}
			
			@Override
			public Double getTotalIrpf() {
				return salaryDraft.getTotalIrpf();
			}
			
			@Override
			public Double getTotalEnterprise() {
				return salaryDraft.getTotalEnterprise();
			}
			
			@Override
			public Double getTotalDeduction() {
				return salaryDraft.getTotalDeduction();
			}
			
			@Override
			public Integer getTimeUnits() {
				return salaryDraft.getTimeUnits();
			}
			
			@Override
			public Date getStartDate() {
				return salaryDraft.getStartDate();
			}
			
			@Override
			public String getSocialSecurityNumber() {
				return salaryDraft.getEmployeeSS();
			}
			
			@Override
			public Double getSocialSecurityContributions() {
				return salaryDraft.getTotalEmployee();
			}
			
			@Override
			public Double getRemuneration() {
				return salaryDraft.getRemuneration();
			}
			
			@Override
			public Double getRawCommonBase() {
				return salaryDraft.getRawCgcBase();
			}
			
			@Override
			public String getQuoteGroup() {
				return salaryDraft.getEmployeeQuoteGroup();
			}
			
			@Override
			public Double getProfessionalBase() {
				return salaryDraft.getCgpBase();
			}
			
			@Override
			public Double getOvertimeBase() {
				return salaryDraft.gethExtraBase();
			}
			
			@Override
			public Double getNonEstructuralOvertimeBase() {
				return salaryDraft.getNonHExtraBase();
			}
			
			@Override
			public Date getIssueDate() {
				return salaryDraft.getIssueDate();
			}
			
			@Override
			public Double getIrpfBase() {
				return salaryDraft.getIrpfBase();
			}
			
			@Override
			public Double getInMoneyIrpfBase() {
				return salaryDraft.getMoneyIrpfBase();
			}
			
			@Override
			public Double getInKindIrpfBase() {
				return salaryDraft.getInkindIrpfBase();
			}
			
			@Override
			public Integer getId() {
				return salaryDraft.getId();
			}
			
			@Override
			public String getEnterpriseName() {
				return salaryDraft.getEnterpriseName();
			}
			
			@Override
			public String getEnterpriseDocument() {
				return salaryDraft.getEnterpriseDocument();
			}			
			
			
			@Override
			public String getEnterpriseAddress() {
				return salaryDraft.getEnterpriseAddress();
			}
			
			@Override
			public Date getEndDate() {
				return salaryDraft.getEndDate();
			}
			
			@Override
			public String getEmployeeName() {
				return salaryDraft.getEmployeeName();
			}
			
			@Override
			public String getEmployeeDocument() {
				return salaryDraft.getEmployeeDocument();
			}
			
			@Override
			public Collection<IDeduction> getEmbargoS() throws SalaryException {
				return salaryDraft.getCosts().stream().map( SalaryDraftUtils::asDeduction ).toList();
			}
			
			@Override
			public Collection<IDeduction> getDeductionS() throws SalaryException {
				return salaryDraft.getCosts().stream().map( SalaryDraftUtils::asDeduction ).toList();
			}
			
			@Override
			public Map<String, List<IData>> getDataS() throws SalaryException {
				return salaryDraft.getContext().stream()
						.map(SalaryDraftUtils::asData)
						.collect(Collectors.groupingBy(IData::getName));
			}
			
			@Override
			public Collection<IDeduction> getCostS() throws SalaryException {
				return salaryDraft.getCosts().stream().map( SalaryDraftUtils::asDeduction ).toList();
			}
			
			@Override
			public Collection<IPayment> getPaymentS() throws SalaryException {
				return salaryDraft.getPayments().stream().map( SalaryDraftUtils::asPayment ).toList();
			}
			
			@Override
			public Double getCommonBase() {
				return salaryDraft.getCgcBase();
			}
			
			@Override
			public Date getChargeDate() {
				return salaryDraft.getChargeDate();
			}
			
			@Override
			public String getCcc() {
				return salaryDraft.getEnterpriseCCC();
			}
			
			@Override
			public String getCategory() {
				return salaryDraft.getEmployeeAgreementCategory();
			}

			@Override
			public Double getExtraPayProration() {
				return salaryDraft.getProrationBase();
			}
			
		};
	}
	
	@SuppressWarnings("serial")
	public static IData asData(Variable variable) {
		return new IData() {
			
			@Override
			public String getValue() {
				return Objects.toString(variable.getValue());
			}
			
			@Override
			public Date getStartDate() {
				return variable.getStartDate();
			}
			
			@Override
			public String getName() {
				return variable.getName();
			}
			
			@Override
			public Date getEndDate() {
				return variable.getEndDate();
			}
		};
	}
	
	@SuppressWarnings("serial")
	public static IPayment asPayment(Payment payment) {
		return new IPayment() {
			
			@Override
			public PaymentType getType() {
				try {
					return PaymentType.values()[payment.getType().ordinal()];
				} catch ( Exception e ) {
					return null;
				}
			}
			
			@Override
			public String getName() {
				return payment.getName();
			}
			
			@Override
			public String getDescription() {
				return payment.getDescription();
			}
			
			@Override
			public double getAmount() {
				return payment.getAmount();
			}
			
			@Override
			public String getExpression() {
				return payment.getExpression();
			}
		};
	}
	
	@SuppressWarnings("serial")
	public static IDeduction asDeduction(Deduction deduction) {
		return new IDeduction() {
			
			@Override
			public DeductionType getType() {
				try {
					return DeductionType.values()[deduction.getType().ordinal()];
				} catch ( Exception e ) {
					return null;
				}
			}
			
			@Override
			public String getName() {
				return deduction.getName();
			}
			
			@Override
			public String getDescription() {
				return deduction.getDescription();
			}
			
			@Override
			public double getAmount() {
				return deduction.getAmount();
			}
			
			@Override
			public String getExpression() {
				return deduction.getExpression();
			}
		};
	}

}
