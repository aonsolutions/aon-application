package com.esferalia.aon.in.payroll.pdf.jooq.check;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.ISalaryBuilderListener;
import com.esferalia.aon.salary.bonus.IBonus;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.salary.expression.TimedObject;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class CheckSalaryBuilder<S extends ISalaryBuilder<T>, T extends ISalary> implements ISalaryBuilder<T> {
	
	private static class DataObject {
		
		private Map<String, ITimedVariable<?>> percentDatas;
		
		private Double cgcBaseE;
		private Double cgpBaseE;
		private Double extrBaseE;
		private Double nExtrBaseE;
		
		private Double cgcPercentE;
		private Double itPercentE;
		private Double imsPercentE;
		private Double unemploymentPercentE;
		private Double jobTrainingPercentE;
		private Double fogasaPercentE;
		private Double extrPercentE;
		private Double nExtrPercentE;
		
		private Double cgcAmountE;
		private Double itAmountE;
		private Double imsAmountE;
		private Double unemploymentAmountE;
		private Double jobTrainingAmountE;
		private Double fogasaAmountE;
		private Double extrAmountE;
		private Double nExtrAmountE;
		
		private DataObject() {
			percentDatas = new LinkedHashMap<>();
		}
		
		private void calculateCgcPercentE() {
			if (cgcPercentE == null) {
				cgcPercentE = calculatePercent(cgcBaseE, cgcAmountE);
			}
		}
		
		private void calculateItPercentE() {
			if (itPercentE == null) {
				itPercentE = calculatePercent(cgpBaseE, itAmountE);
			}
		}
		
		private void calculateImsPercentE() {
			if (imsPercentE == null) {
				imsPercentE = calculatePercent(cgpBaseE, imsAmountE);
			}
		}
		
		private void calculateUnemploymentPercentE() {
			if (unemploymentPercentE == null) {
				unemploymentPercentE = calculatePercent(cgpBaseE, unemploymentAmountE);
			}
		}
		
		private void calculateJobTrainingPercentE() {
			if (jobTrainingPercentE == null) {
				jobTrainingPercentE = calculatePercent(cgpBaseE, jobTrainingAmountE);
			}
		}
		
		private void calculateFogasaPercentE() {
			if (fogasaPercentE == null) {
				fogasaPercentE = calculatePercent(cgpBaseE, fogasaAmountE);
			}
		}
		
		private void calculateExtrPercentE() {
			if (extrPercentE == null) {
				extrPercentE = calculatePercent(extrBaseE, extrAmountE);
			}
		}
		
		private void calculateNExtrPercentE() {
			if (nExtrPercentE == null) {
				nExtrPercentE = calculatePercent(nExtrBaseE, nExtrAmountE);
			}
		}
		
		private void calculateMissingEnterprisePercents() {
			calculateCgcPercentE();
			calculateItPercentE();
			calculateImsPercentE();
			calculateUnemploymentPercentE();
			calculateJobTrainingPercentE();
			calculateFogasaPercentE();
			calculateExtrPercentE();
			calculateNExtrPercentE();
		}
		private Double calculatePercent(Double base, Double amount) {
			if (base != null && amount != null) {
				Double safeBase = AonNumberUtils.zeroIfNull(base);
				Double amountBase = AonNumberUtils.zeroIfNull(amount);
				if (safeBase != 0) {
					
					return AonMathUtils.round(amountBase / safeBase * 100);
				}
			}
			return null;
		}
		
		private static Double getDoubleFromTimedVariable(Object value) {
			try {				
				if (value instanceof String) {
					return AonNumberUtils.toDouble((String) value);
				} else if (value instanceof Number) {
					return ((Number) value).doubleValue();
				}
			} catch (Exception e) {}
			return null;
		}
		
		private void manageEnterprisePercent(String name, ITimedVariable<?> datas) {
			if (datas == null || datas.getPeriod() == null) {
				return;
			}
			Object valueObj = datas.getValue(datas.getPeriod());
			Double percent = getDoubleFromTimedVariable(valueObj);
			if (ContextVariable.CGC_ENTERPRISE_PERCENT.getName().equals(name)) {
				cgcPercentE = percent;
			} else if (ContextVariable.IMS_ENTERPRISE_PERCENT.getName().equals(name)) {				
				imsPercentE = percent;
			} else if (ContextVariable.IT_RATE.getName().equals(name)) {				
				itPercentE = percent;
			} else if (ContextVariable.UNEMPLOY_ENTERPRISE_PERCENT.getName().equals(name)) {
				unemploymentPercentE = percent;
			} else if (ContextVariable.FP_ENTERPRISE_PERCENT.getName().equals(name)) {
				jobTrainingPercentE = percent;				
			} else if (ContextVariable.FOGASA_ENTERPRISE_PERCENT.getName().equals(name)) {				
				fogasaPercentE = percent;
			} else if ("PORCENTAJE_EXTR_E".equals(name)) {
				extrPercentE = percent;
			} else if ("PORCENTAJE_NEXTR_E".equals(name)) {				
				nExtrPercentE = percent;
			}
			percentDatas.put(name, datas);
		}
		
		private void manageEnterpriseBase(String name, ITimedVariable<?> datas) {
			if (datas == null || datas.getPeriod() == null) {
				return;
			}
			Object valueObj = datas.getValue(datas.getPeriod());
			Double base = getDoubleFromTimedVariable(valueObj);
			if (ContextVariable.CGC_BASE_ENTERPRISE.getName().equals(name)) {
				cgcBaseE = base;
			} else if (ContextVariable.CGP_BASE_ENTERPRISE.getName().equals(name)) {				
				cgpBaseE = base;
			} else if (ContextVariable.STRUCTURAL_OVERTIME_BASE.getName().equals(name)) {				
				extrBaseE = base;
			} else if (ContextVariable.NON_STRUCTURAL_OVERTIME_BASE.getName().equals(name)) {				
				nExtrBaseE = base;
			}
		}
		
		private void manageCost(Double amount, IDeduction cost) {
			switch (cost.getType()) {
			case COMMON_CONTINGENCY:
				cgcAmountE = amount;
				break;
			case PROFESSIONAL_CONTINGENCY:
				itAmountE = amount;
				break;
			case UNEMPLOYMENT:
				unemploymentAmountE = amount;
				break;
			case JOB_TRAINING:
				jobTrainingAmountE = amount;
				break;
			case FOGASA:
				fogasaAmountE = amount;
				break;
			case STRUCTURAL_OVERTIME:
				extrAmountE = amount;
				break;
			case NON_STRUCTURAL_OVERTIME:
				nExtrAmountE = amount;
				break;
			case ADVANCE_PAYMENT:
			case IN_KIND:
			case IRPF:
			case OTHER:
			default:
				break;
			}
		}
		
	}
	
	private static final String[] PERCENT_NAMES = {
			ContextVariable.CGC_ENTERPRISE_PERCENT.getName(),
			ContextVariable.IMS_ENTERPRISE_PERCENT.getName(),
			ContextVariable.IT_RATE.getName(),
			ContextVariable.UNEMPLOY_ENTERPRISE_PERCENT.getName(),
			ContextVariable.FP_ENTERPRISE_PERCENT.getName(),
			ContextVariable.FOGASA_ENTERPRISE_PERCENT.getName(),
			"PORCENTAJE_EXTR_E",
			"PORCENTAJE_NEXTR_E"
	};
	
	private static final String[] ENTERPRISE_BASE_NAMES = {
			ContextVariable.CGC_BASE_ENTERPRISE.getName(),
			ContextVariable.CGP_BASE_ENTERPRISE.getName(),
			ContextVariable.STRUCTURAL_OVERTIME_BASE.getName(),
			ContextVariable.NON_STRUCTURAL_OVERTIME_BASE.getName()
	};	
	
	private DataObject dataObject;
	private ISalaryBuilder<T> salaryBuilder;
	
	private Date startDate;
	private Date endDate;
	
	public ISalaryBuilder<T> getBuilder() {
		return salaryBuilder;
	}
	

	public CheckSalaryBuilder(ISalaryBuilder<T> salaryBuilder) {
		super();
		this.salaryBuilder = salaryBuilder;
		this.dataObject = new DataObject();
	}
	
	private void calculateEnterprisePercents() {
		dataObject.calculateMissingEnterprisePercents();
		Period period = new Period(startDate, endDate);
		Map<String, ITimedVariable<?>> percentMap = dataObject.percentDatas;
		if (!percentMap.containsKey(ContextVariable.CGC_ENTERPRISE_PERCENT.getName())) {
			salaryBuilder.addData(ContextVariable.CGC_ENTERPRISE_PERCENT.getName(), new TimedObject<>(dataObject.cgcPercentE, period));
		}
		if (!percentMap.containsKey(ContextVariable.IMS_ENTERPRISE_PERCENT.getName())) {				
			salaryBuilder.addData(ContextVariable.IMS_ENTERPRISE_PERCENT.getName(), new TimedObject<>(dataObject.imsPercentE, period));
		}
		if (!percentMap.containsKey(ContextVariable.IT_RATE.getName())) {				
			salaryBuilder.addData(ContextVariable.IT_RATE.getName(), new TimedObject<>(dataObject.itPercentE, period));
		}
		if (!percentMap.containsKey(ContextVariable.UNEMPLOY_ENTERPRISE_PERCENT.getName())) {
			salaryBuilder.addData(ContextVariable.UNEMPLOY_ENTERPRISE_PERCENT.getName(), new TimedObject<>(dataObject.unemploymentPercentE, period));			
		}
		if (!percentMap.containsKey(ContextVariable.FP_ENTERPRISE_PERCENT.getName())) {
			salaryBuilder.addData(ContextVariable.FP_ENTERPRISE_PERCENT.getName(), new TimedObject<>(dataObject.jobTrainingPercentE, period));			
		}
		if (!percentMap.containsKey(ContextVariable.FOGASA_ENTERPRISE_PERCENT.getName())) {				
			salaryBuilder.addData(ContextVariable.FOGASA_ENTERPRISE_PERCENT.getName(), new TimedObject<>(dataObject.fogasaPercentE, period));
		}
		if (!percentMap.containsKey("PORCENTAJE_EXTR_E")) {
			salaryBuilder.addData("PORCENTAJE_EXTR_E", new TimedObject<>(dataObject.extrPercentE, period));
		}
		if (!percentMap.containsKey("PORCENTAJE_NEXTR_E")) {				
			salaryBuilder.addData("PORCENTAJE_NEXTR_E", new TimedObject<>(dataObject.nExtrPercentE, period));
		}
	}
	
	@Override
	public T getSalary() {
		calculateEnterprisePercents();
		return salaryBuilder.getSalary();
	}
	
	@Override
	public void createNewSalary() {
		salaryBuilder.createNewSalary();
	}
	
	@Override
	public void setContract(Object contract) {
		salaryBuilder.setContract(contract);
	}
	
	@Override
	public void setCcc(String ccc) {
		salaryBuilder.setCcc(ccc);
	}
	
	@Override
	public void setEnterpriseCity(String enterpriseCity) {
		salaryBuilder.setEnterpriseCity(enterpriseCity);
	}
	
	@Override
	public void setEnterpriseName(String enterpriseName) {
		salaryBuilder.setEnterpriseName(enterpriseName);
	}
	
	@Override
	public void setEnterpriseAddress(String enterpriseAddress) {
		salaryBuilder.setEnterpriseAddress(enterpriseAddress);
	}
	
	@Override
	public void setEnterpriseDocument(String enterpriseDocument) {
		salaryBuilder.setEnterpriseDocument(enterpriseDocument);
	}
	
	@Override
	public void setRegime(String regime) {
		salaryBuilder.setRegime(regime);
	}
	
	@Override
	public void setRegistration(Integer registration) {
		salaryBuilder.setRegistration(registration);
	}
	
	@Override
	public void setEmployeeCity(String employeeCity) {
		salaryBuilder.setEmployeeCity(employeeCity);
	}
	
	@Override
	public void setEmployeeAddress(String employeeAddress) {
		salaryBuilder.setEmployeeAddress(employeeAddress);
	}
	
	@Override
	public void setEmployeeName(String employeeName) {
		salaryBuilder.setEmployeeName(employeeName);
	}
	
	@Override
	public void setEmployeeDocument(String employeeDocument) {
		salaryBuilder.setEmployeeDocument(employeeDocument);
	}
	
	@Override
	public void setSocialSecurityNumber(String socialSecurityNumber) {
		salaryBuilder.setSocialSecurityNumber(socialSecurityNumber);
	}
	
	@Override
	public void setCategory(String category) {
		salaryBuilder.setCategory(category);
	}
	
	@Override
	public void setQuoteGroup(String quoteGroup) {
		salaryBuilder.setQuoteGroup(quoteGroup);
	}
	
	@Override
	public void setSeniorityDate(Date seniorityDate) {
		salaryBuilder.setSeniorityDate(seniorityDate);
	}
	
	@Override
	public void setType(SalaryType type) {
		salaryBuilder.setType(type);
	}
	
	@Override
	public void setIssueDate(Date issueDate) {
		salaryBuilder.setIssueDate(issueDate);
	}
	
	@Override
	public void setChargeDate(Date issueDate) {
		salaryBuilder.setChargeDate(issueDate);
	}
	
	@Override
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
		salaryBuilder.setStartDate(startDate);
	}
	
	@Override
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
		salaryBuilder.setEndDate(endDate);
	}
	
	@Override
	public void setTimeUnits(Integer timeUnits) {
		salaryBuilder.setTimeUnits(timeUnits);
	}
	
	@Override
	public void setItBase(Double itBase) {
		salaryBuilder.setItBase(itBase);
	}
	
	@Override
	public void setRawCgcBase(Double rawCgcBase) {
		salaryBuilder.setRawCgcBase(rawCgcBase);
	}
	
	@Override
	public void setCgcBase(Double cgcBase) {
		salaryBuilder.setCgcBase(cgcBase);
	}
	
	@Override
	public void setCgpBase(Double cgpBase) {
		salaryBuilder.setCgpBase(cgpBase);
	}
	
	@Override
	public void setRemuneration(Double remuneration) {
		salaryBuilder.setRemuneration(remuneration);
	}
	
	@Override
	public void setProExtBase(Double proExtBase) {
		salaryBuilder.setProExtBase(proExtBase);
	}
	
	@Override
	public void setIrpfBase(Double irpfBase) {
		salaryBuilder.setIrpfBase(irpfBase);
	}
	
	@Override
	public void setMoneyIrpfBase(Double moneyIrpfBase) {
		salaryBuilder.setMoneyIrpfBase(moneyIrpfBase);
	}
	
	@Override
	public void setInkindIrpfBase(Double inkindIrpfBase) {
		salaryBuilder.setInkindIrpfBase(inkindIrpfBase);
	}
	
	@Override
	public void setHExtraBase(Double hExtraBase) {
		salaryBuilder.setHExtraBase(hExtraBase);
		if (dataObject.extrBaseE == null) {			
			dataObject.extrBaseE = hExtraBase;
		}
	}
	
	@Override
	public void setNonHExtraBase(Double nonHExtraBase) {
		salaryBuilder.setNonHExtraBase(nonHExtraBase);
		if (dataObject.nExtrBaseE == null) {			
			dataObject.nExtrBaseE = nonHExtraBase;
		}
	}
	
	@Override
	public void setTotalSS(Double totalSS) {
		salaryBuilder.setTotalSS(totalSS);
	}
	
	@Override
	public void setTotalIrpf(Double totalIrpf) {
		salaryBuilder.setTotalIrpf(totalIrpf);
	}
	
	@Override
	public void setTotalDeduction(Double totalDeduction) {
		salaryBuilder.setTotalDeduction(totalDeduction);
	}
	
	@Override
	public void setTotalLiquid(Double totalLiquid) {
		salaryBuilder.setTotalLiquid(totalLiquid);
	}
	
	@Override
	public void setTotalPayment(Double totalPayment) {
		salaryBuilder.setTotalPayment(totalPayment);
	}
	
	@Override
	public void setTotalEnterprise(Double totalEnterprise) {
		salaryBuilder.setTotalEnterprise(totalEnterprise);
	}
	
	@Override
	public void addData(String name, ITimedVariable<?> datas) {
		if (Arrays.asList(PERCENT_NAMES).contains(AonStringUtils.trimToEmpty(name))) {			
			dataObject.manageEnterprisePercent(name, datas);
		} else if (Arrays.asList(ENTERPRISE_BASE_NAMES).contains(AonStringUtils.trimToEmpty(name))) {
			dataObject.manageEnterpriseBase(name, datas);			
		}
		salaryBuilder.addData(name, datas);
	}
	
	@Override
	public void addCost(Double amount, String description, Date startDate, Date endDate, IDeduction cost,
			Map<String, ITimedVariable<?>> context) {
		salaryBuilder.addCost(amount, description, startDate, endDate, cost, context);
		dataObject.manageCost(amount, cost);
	}
	
	@Override
	public void addBonus(Double amount, String description, Date startDate, Date endDate, IBonus bonus,
			Map<String, ITimedVariable<?>> context) {
		salaryBuilder.addBonus(amount, description, startDate, endDate, bonus, context);
	}
	
	@Override
	public void addPayment(Double amount, Double quote, Double tax, String description, Date start, Date end,
			IPayment payment, Map<String, ITimedVariable<?>> context) {
		salaryBuilder.addPayment(amount, quote, tax, description, start, end, payment, context);
	}
	
	@Override
	public void addZeroPayment(Double quote, Double tax, Date startDate, Date endDate, IPayment payment,
			Map<String, ITimedVariable<?>> context) {
		salaryBuilder.addZeroPayment(quote, tax, startDate, endDate, payment, context);
	}
	
	@Override
	public void addDeduction(Double amount, String description, Date start, Date end, IDeduction deduction,
			Map<String, ITimedVariable<?>> context) {
		salaryBuilder.addDeduction(amount, description, start, end, deduction, context);
	}
	
	@Override
	public void addZeroDeduction(Date start, Date end, IDeduction deduction, Map<String, ITimedVariable<?>> context) {
		salaryBuilder.addZeroDeduction(start, end, deduction, context);
	}
	
	@Override
	public void addEmbargo(Integer id, Double amount, String description, IDeduction embargo,
			Map<String, ITimedVariable<?>> context) {
		salaryBuilder.addEmbargo(id, amount, description, embargo, context);
	}
	
	@Override
	public void addZeroEmbargo(Integer id, IDeduction embargo, Map<String, ITimedVariable<?>> context) {
		salaryBuilder.addZeroEmbargo(id, embargo, context);
	}
	
	@Override
	public void setListener(ISalaryBuilderListener listener) {
		salaryBuilder.setListener(listener);
	}
	
	@Override
	public void setExpressionContext(ExpressionContext context) {
		salaryBuilder.setExpressionContext(context);
	}
	
}
