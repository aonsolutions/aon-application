package com.esferalia.aon.ui.payroll.utils;


import java.io.Serializable;

import net.sf.jasperreports.engine.JRDefaultScriptlet;
import net.sf.jasperreports.engine.JRScriptletException;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryData;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.SalaryException;

public class SalaryReportScriptlet extends JRDefaultScriptlet implements Serializable {

	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SalaryReportScriptlet.class.getName());
	
	private static final String FIELD_SALARY = "salary";
	
	private Salary salary;

	@Override
	public void afterPageInit() throws JRScriptletException {
		super.afterPageInit();
		salary = (Salary) super.getFieldValue(FIELD_SALARY);
	}
	
	public boolean printDraftWatermark(){
		return false;
	}
	
	/*
	 * BASES
	 */
	public Double getCgcBase() throws JRScriptletException{
		Double base = obtainTotalCommonBase(salary);
		return (base!=null && base>0) ? base : (salary.getCommonBase() == 0.0 ? null : salary.getCommonBase());
	}
	public Double getCgpBase() throws JRScriptletException{
		Double base = obtainTotalProfessionalBase(salary);
		return (base!=null && base>0) ? base : (salary.getProfessionalBase() == 0.0 ? null : salary.getProfessionalBase());
	}
	public Double getCgcBaseEnterprise() throws JRScriptletException{
		Double base = obtainTotalCommonBaseEnterprise(salary);
		return (base!=null && base>0) ? base : (salary.getCommonBase() == 0.0 ? null : salary.getCommonBase());
	}
	public Double getCgpBaseEnterprise() throws JRScriptletException{
		Double base = obtainTotalProfessionalBaseEnterprise(salary);
		return (base!=null && base>0) ? base : (salary.getProfessionalBase() == 0.0 ? null : salary.getProfessionalBase());
	}
	public Double getOvertimeBase() throws JRScriptletException{
		return salary.getOvertimeBase() == 0.0 ? null : salary.getOvertimeBase();
	}
	public Double getNonStructuralOvertimeBase() throws JRScriptletException{
		return salary.getNonEstructuralOvertimeBase() == 0.00 ? null : salary.getNonEstructuralOvertimeBase();
	}
	public Double getIrpfBase() throws JRScriptletException{
		return salary.getIrpfBase() == 0.00 ? null : salary.getIrpfBase();
	}
	
	/*
	 * DEDUCTIONS
	 */
	public Double getCgc() throws SalaryException,
	JRScriptletException {
		return salary.getDeductions().getCommonContingency() == null ? 0.0
				: salary.getDeductions().getCommonContingency()
						.getAmount();
	}
	
	public Double getUnemployment() throws SalaryException,
	JRScriptletException {
		return salary.getDeductions().getUnemployment() == null ? 0.0
				: (salary.getDeductions().getUnemployment()
						.getAmount());
	}

	public Double getFp() throws SalaryException,
		JRScriptletException {
		return salary.getDeductions().getJobTraining() == null ? 0.0
				: (salary.getDeductions().getJobTraining()
						.getAmount());
	}

	public Double getIrpf() throws SalaryException,
	JRScriptletException {
		return salary.getDeductions().getIrpf() == null ? 0.0
				: (salary.getDeductions().getIrpf()
						.getAmount());
	}

	public Double getOvertime() throws SalaryException, JRScriptletException {
		return salary.getDeductions().getStructuralOvertime() == null ? 0.0
				: (salary.getDeductions().getStructuralOvertime()
						.getAmount());
	}
	
	public Double getNonStructuralOvertime() throws SalaryException,
			JRScriptletException {
		return salary.getDeductions().getNonStructuralOvertime() == null ? 0.0
				: (salary.getDeductions().getNonStructuralOvertime()
						.getAmount());
	}
	
	
	/*
	 * CUOTAS
	 */
	public Double getCgcEnterprise() throws SalaryException,
			JRScriptletException {
		return salary.getEnterpriseCosts().getCommonContingency() == null ? 0.0
				: salary.getEnterpriseCosts().getCommonContingency()
						.getAmount();
	}
	
	public Double getAtEpEnterprise() throws SalaryException,
			JRScriptletException {
		return salary.getEnterpriseCosts().getAtepIt() == null ? 0.0
				: ((salary.getEnterpriseCosts().getAtepIt().getAmount() + salary
						.getEnterpriseCosts().getAtepIms().getAmount()));
	}
	
	public Double getUnemploymentEnterprise() throws SalaryException,
			JRScriptletException {
		return salary.getEnterpriseCosts().getUnemployment() == null ? 0.0
				: (salary.getEnterpriseCosts().getUnemployment()
						.getAmount());
	}
	
	public Double getFpEnterprise() throws SalaryException,
			JRScriptletException {
		return salary.getEnterpriseCosts().getJobTraining() == null ? 0.0
				: (salary.getEnterpriseCosts().getJobTraining()
						.getAmount());
	}
	
	public Double getFogasa() throws SalaryException, JRScriptletException {
		return salary.getEnterpriseCosts().getFogasa() == null ? 0.0
				: (salary.getEnterpriseCosts().getFogasa().getAmount());
	}
	
	public Double getOvertimeEnterprise() throws SalaryException, JRScriptletException {
		return salary.getEnterpriseCosts().getStructuralOvertime() == null ? 0.0
				: (salary.getEnterpriseCosts().getStructuralOvertime()
						.getAmount());
	}
	
	public Double getNonStructuralOvertimeEnterprise() throws SalaryException,
			JRScriptletException {
		return salary.getEnterpriseCosts().getNonStructuralOvertime() == null ? 0.0
				: (salary.getEnterpriseCosts().getNonStructuralOvertime()
						.getAmount());
	}
	
	/*
	 * PORCENTAJES
	 */
	
	public Double getIrpfPercent() throws SalaryException, JRScriptletException {
		Double base = getIrpfBase();
		Double amount = getIrpf();
		return base != null && base > 0  && amount > 0 ? CommonUtil.round((amount / base) * 100) / 100 : null;
	}
	
	public Double getCgcPercent() throws SalaryException, JRScriptletException {
		Double base = getCgcBase();
		Double amount = getCgc();
		return base != null && base > 0  && amount > 0 ? CommonUtil.round((amount / base) * 100) / 100 : null;
	}

	public Double getCgcPercentEnterprise() throws SalaryException, JRScriptletException {
		Double base = getCgcBaseEnterprise();
		Double amount = getCgcEnterprise();
		return base != null && base > 0  && amount > 0 ? CommonUtil.round((amount / base) * 100) / 100 : null;
	}

	public Double getAtEpPercentEnterprise() throws SalaryException, JRScriptletException {
		Double base = getCgpBaseEnterprise();
		Double amount = getAtEpEnterprise();
		return base != null && base > 0  && amount > 0 ? CommonUtil.round((amount / base) * 100) / 100 : null;
	}

	public Double getUnemploymentPercent() throws SalaryException,
	JRScriptletException {
		Double base = getCgpBase();
		Double amount = getUnemployment();
		return base != null && base > 0  && amount > 0 ? CommonUtil.round((amount / base) * 100) / 100 : null;
	}

	public Double getUnemploymentPercentEnterprise() throws SalaryException,
			JRScriptletException {
		Double base = getCgpBaseEnterprise();
		Double amount = getUnemploymentEnterprise();
		return base != null && base > 0  && amount > 0 ? CommonUtil.round((amount / base) * 100) / 100 : null;
	}

	public Double getFpPercent() throws SalaryException, JRScriptletException {
		Double base = getCgpBase();
		Double amount = getFp();
		return base != null && base > 0  && amount > 0 ? CommonUtil.round((amount / base) * 100) / 100 : null;
	}
	
	public Double getFpPercentEnterprise() throws SalaryException, JRScriptletException {
		Double base = getCgpBaseEnterprise();
		Double amount = getFpEnterprise();
		return base != null && base > 0  && amount > 0 ? CommonUtil.round((amount / base) * 100) / 100 : null;
	}

	public Double getFogasaPercent() throws SalaryException,
			JRScriptletException {
		Double base = getCgpBaseEnterprise();
		Double amount = getFogasa();
		return base != null && base > 0  && amount > 0 ? CommonUtil.round((amount / base) * 100) / 100 : null;
	}

	public Double getOvertimePercent() throws SalaryException,
	JRScriptletException {
		Double base = getCgpBase();
		Double amount = getOvertime();
		return base != null && base > 0  && amount > 0 ? CommonUtil.round((amount / base) * 100) / 100 : null;
	}
	
	public Double getOvertimePercentEnterprise() throws SalaryException,
			JRScriptletException {
		Double base = getCgpBaseEnterprise();
		Double amount = getOvertimeEnterprise();
		return base != null && base > 0  && amount > 0 ? CommonUtil.round((amount / base) * 100) / 100 : null;
	}

	public Double getNonStructuralOvertimePercent() throws SalaryException,
	JRScriptletException {
		Double base = getCgpBase();
		Double amount = getNonStructuralOvertime();
		return base != null && base > 0  && amount > 0 ? CommonUtil.round((amount / base) * 100) / 100 : null;
	}

	public Double getNonStructuralOvertimePercentEnterprise() throws SalaryException,
			JRScriptletException {
		Double base = getCgpBaseEnterprise();
		Double amount = getNonStructuralOvertimeEnterprise();
		return base != null && base > 0  && amount > 0 ? CommonUtil.round((amount / base) * 100) / 100 : null;
	}
	
	
	
	///////////////////////////////////
	///////////////////////////////////
	///////////////////////////////////
	
	private Double obtainTotalCommonBase(Salary salary){
		Double base = 0.0;
		try {
			base = salary.getSalaryDatas()
					.stream()
					.filter(o -> (o.getName().equals(ContextVariable.CGC_BASE.getName())))
					.map(SalaryData::getExpression)
					.mapToDouble(o -> (NumberUtils.isNumber(o)?Double.valueOf(o):0.0)).sum();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return base;
	}

	private Double obtainTotalCommonBaseEnterprise(Salary salary){
		Double base = 0.0;
		try {
			base = salary.getSalaryDatas()
					.stream()
					.filter(o -> (o.getName().equals(ContextVariable.CGC_BASE_ENTERPRISE.getName())))
					.map(SalaryData::getExpression)
					.mapToDouble(o -> (NumberUtils.isNumber(o)?Double.valueOf(o):0.0)).sum();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return base;
	}
	
	private Double obtainTotalProfessionalBase(Salary salary){
		Double base = 0.0;
		try {
			base = salary.getSalaryDatas()
					.stream()
					.filter(o -> (o.getName().equals(ContextVariable.CGP_BASE.getName())))
					.map(SalaryData::getExpression)
					.mapToDouble(o -> (NumberUtils.isNumber(o)?Double.valueOf(o):0.0)).sum();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return base;
	}
	
	private Double obtainTotalProfessionalBaseEnterprise(Salary salary){
		Double base = 0.0;
		try {
			base = salary.getSalaryDatas()
					.stream()
					.filter(o -> (o.getName().equals(ContextVariable.CGP_BASE_ENTERPRISE.getName())))
					.map(SalaryData::getExpression)
					.mapToDouble(o -> (NumberUtils.isNumber(o)?Double.valueOf(o):0.0)).sum();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return base;
	}
	
}
