package com.code.aon.ui.employee.controller;

import java.util.LinkedList;

import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.employee.SalaryDeduction;
import com.code.aon.employee.SalaryPayment;
import com.code.aon.employee.enumeration.DeductionType;
import com.code.aon.employee.enumeration.PaymentType;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;

public class SalaryController extends BasicController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SalaryController.class.getName());

	private final String SALARY_PAYMENT_CONTROLLER = "salaryPayment";
    private final String SALARY_DEDUCTION_CONTROLLER = "salaryDeduction";

	
	private SalaryPayments payments;
	private SalaryDeductions deductions;
	
	public SalaryPayments getPayments() {
		return payments;
	}
	public void setPayments(SalaryPayments payments) {
		this.payments = payments;
	}
	public SalaryDeductions getDeductions() {
		return deductions;
	}
	public void setDeductions(SalaryDeductions deductions) {
		this.deductions = deductions;
	}


	public void onLoadSalaryReport(ActionEvent event){
		setPayments(new SalaryPayments());
		getPayments().setSalarySupplements(new LinkedList<SalaryPayment>());
		getPayments().setComplementarySuply(new LinkedList<SalaryPayment>());
		setDeductions(new SalaryDeductions());
		IController pBean = FormUtil.getController(SALARY_PAYMENT_CONTROLLER);
		IController dBean = FormUtil.getController(SALARY_DEDUCTION_CONTROLLER);
		try {
			for(ITransferObject to: pBean.getManagerBean().getList(pBean.getCriteria())){
				SalaryPayment sp = (SalaryPayment) to;
				if (sp.getType() == PaymentType.BASE_SALARY) {
					getPayments().setBaseSalary(sp);
				} else if (sp.getType() == PaymentType.SALARY_SUPPLEMENTS) {
					 getPayments().getSalarySupplements().add(sp);
				} else if (sp.getType() == PaymentType.OVERTIME_HOURS) {
					getPayments().setOvertimeHours(sp);
				} else if (sp.getType() == PaymentType.SPECIAL_BONUSES) {
					getPayments().setSpecialBonuses(sp);
				} else if (sp.getType() == PaymentType.SALARY_IN_KIND) {
					getPayments().setSalaryInKid(sp);
				} else if (sp.getType() == PaymentType.COMPENSATION_SUPLY) {
					getPayments().getComplementarySuply().add(sp);
				} else if (sp.getType() == PaymentType.SOCIAL_SECURITY_BENEFITS) {
					getPayments().setSpecialSecurityBenefits(sp);
				} else if (sp.getType() == PaymentType.MOVING_COMPENSATION) {
					getPayments().setMovingCompensation(sp);
				} else if (sp.getType() == PaymentType.OTHER_NON_WAGE) {
					getPayments().setOtherNonWage(sp);
				}
			}

			for(ITransferObject to: dBean.getManagerBean().getList(dBean.getCriteria())){
				SalaryDeduction sd = (SalaryDeduction) to;
				if (sd.getType() == DeductionType.COMMON_CONTINGENCY) {
					getDeductions().setCommonContingency(sd);
				} else if (sd.getType() == DeductionType.UNEMPLOYMENT) {
					getDeductions().setUnemployment(sd);
				} else if (sd.getType() == DeductionType.JOB_TRAINING) {
					getDeductions().setJobTraining(sd);
				} else if (sd.getType() == DeductionType.STRUCTURAL_OVERTIME) {
					getDeductions().setStructuralOvertime(sd);
				} else if (sd.getType() == DeductionType.NON_STRUCTURAL_OVERTIME) {
					getDeductions().setNonStructuralOvertime(sd);
				} else if (sd.getType() == DeductionType.IRPF) {
					getDeductions().setIrpf(sd);
				} else if (sd.getType() == DeductionType.ADVANCE_PAYMENT) {
					getDeductions().setAdvancePayment(sd);
				} else if (sd.getType() == DeductionType.IN_KIND) {
					getDeductions().setInKid(sd);
				} else if (sd.getType() == DeductionType.OTHER) {
					getDeductions().setOther(sd);
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

}
