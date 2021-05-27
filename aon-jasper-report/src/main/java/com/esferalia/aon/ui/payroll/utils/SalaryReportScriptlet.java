package com.esferalia.aon.ui.payroll.utils;

import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.payroll.Salary;

import net.sf.jasperreports.engine.JRDefaultScriptlet;
import net.sf.jasperreports.engine.JRScriptletException;

public class SalaryReportScriptlet extends JRDefaultScriptlet {

	private Salary salary;

	@Override
	public void afterPageInit() throws JRScriptletException {
		super.afterPageInit();
		this.salary = (Salary) getFieldValue("salary");
	}

	public Double getCgcBase() throws JRScriptletException {
		return (salary.getCommonBase() == 0.0 ? null : salary.getCommonBase());
	}

	public Double getCgpBase() throws JRScriptletException {
		return (salary.getProfessionalBase() == 0.0 ? null : salary.getProfessionalBase());
	}

	public Double getCgcBaseEnterprise() throws JRScriptletException {
		return (salary.getCommonBase() == 0.0 ? null : salary.getCommonBase());
	}

	public Double getCgpBaseEnterprise() throws JRScriptletException {
		return (salary.getProfessionalBase() == 0.0 ? null : salary.getProfessionalBase());
	}

	public Double getOvertimeBase() throws JRScriptletException {
		return salary.getOvertimeBase() == 0.0 ? null : salary.getOvertimeBase();
	}

	public Double getNonStructuralOvertimeBase() throws JRScriptletException {
		return salary.getNonEstructuralOvertimeBase() == 0.00 ? null : salary.getNonEstructuralOvertimeBase();
	}

	public Double getIrpfBase() throws JRScriptletException {
		return salary.getIrpfBase() == 0.00 ? null : salary.getIrpfBase();
	}

	// ------------------------------------------------------------------------

	public Double getFogasa() throws JRScriptletException {
		return salary.getEnterpriseCosts().getFogasa() == null ? 0.0
				: (salary.getEnterpriseCosts().getFogasa().getAmount());
	}

	public Double getFpEnterprise() throws JRScriptletException {
		return salary.getEnterpriseCosts().getJobTraining() == null ? 0.0
				: (salary.getEnterpriseCosts().getJobTraining().getAmount());
	}

	public Double getAtEpEnterprise() throws JRScriptletException {
		return salary.getEnterpriseCosts().getAtepIt() == null ? 0.0
				: ((salary.getEnterpriseCosts().getAtepIt().getAmount()
						+ salary.getEnterpriseCosts().getAtepIms().getAmount()));
	}

	public Double getCgcEnterprise() throws JRScriptletException {
		return salary.getEnterpriseCosts().getCommonContingency().getAmount();
	}

	public Double getUnemploymentEnterprise() throws JRScriptletException {
		return salary.getEnterpriseCosts().getUnemployment() == null ? 0.0
				: (salary.getEnterpriseCosts().getUnemployment().getAmount());
	}

	public Double getOvertime() throws JRScriptletException {
		return salary.getEnterpriseCosts().getStructuralOvertime() == null ? 0.0
				: (salary.getEnterpriseCosts().getStructuralOvertime().getAmount());
	}

	public Double getNonStructuralOvertime() throws JRScriptletException {
		return salary.getEnterpriseCosts().getNonStructuralOvertime() == null ? 0.0
				: (salary.getEnterpriseCosts().getNonStructuralOvertime().getAmount());
	}

	// ------------------------------------------------------------------------

	public Double getFogasaPercent() throws JRScriptletException {
		Double base = getCgpBaseEnterprise();
		Double amount = getFogasa();
		return base != null && base > 0 && amount > 0 ? CommonUtil.round((amount / base) * 100) / 100 : null;
	}

	public Double getCgcPercentEnterprise() throws JRScriptletException {
		Double base = getCgcBaseEnterprise();
		Double amount = getCgcEnterprise();
		return base != null && base > 0 && amount > 0 ? CommonUtil.round((amount / base) * 100) / 100 : null;
	}

	public Double getAtEpPercentEnterprise() throws JRScriptletException {
		Double base = getCgpBaseEnterprise();
		Double amount = getAtEpEnterprise();
		return base != null && base > 0 && amount > 0 ? CommonUtil.round((amount / base) * 100) / 100 : null;
	}

	public Double getUnemploymentPercentEnterprise() throws JRScriptletException {
		Double base = getCgpBaseEnterprise();
		Double amount = getUnemploymentEnterprise();
		return base != null && base > 0 && amount > 0 ? CommonUtil.round((amount / base) * 100) / 100 : null;
	}

	public Double getFpPercentEnterprise() throws JRScriptletException {
		Double base = getCgpBaseEnterprise();
		Double amount = getFpEnterprise();
		return base != null && base > 0 && amount > 0 ? CommonUtil.round((amount / base) * 100) / 100 : null;
	}

	public Double getOvertimePercent() throws JRScriptletException {
		Double base = getCgpBaseEnterprise();
		Double amount = getOvertime();
		return base != null && base > 0 && amount > 0 ? CommonUtil.round((amount / base) * 100) / 100 : null;
	}

	public Double getNonStructuralOvertimePercent() throws JRScriptletException {
		Double base = getCgpBaseEnterprise();
		Double amount = getNonStructuralOvertime();
		return base != null && base > 0 && amount > 0 ? CommonUtil.round((amount / base) * 100) / 100 : null;
	}
}
