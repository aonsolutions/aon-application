package com.esferalia.aon.salary.payment;


import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.salary.enumeration.PaymentType;


public class Payments {

	private Map<PaymentType, IPayment> map;
	private SalarySupplements salarySupplements;
	private CompensationOrPrepaidExpenses compensationOrPrepaidExpenses;
	private OtherNonWages otherNonWages;

	public Payments() {
		map = new HashMap<PaymentType, IPayment>();	
	}

	public IPayment getBaseSalary() {
		return map.get(PaymentType.BASE_SALARY);
	}
	public void setBaseSalary(IPayment p) {
		put(PaymentType.BASE_SALARY, p);
	}

	public IPayment getOvertimeHours() {
		return map.get(PaymentType.NON_STRUCTURAL_HOURS);
	}
	public void setOvertimeHours(IPayment p) {
		put(PaymentType.NON_STRUCTURAL_HOURS, p);
	}

	public IPayment getSpecialBonuses() {
		return map.get(PaymentType.SPECIAL_BONUSES);
	}
	public void setSpecialBonuses(IPayment p) {
		put(PaymentType.SPECIAL_BONUSES, p);
	}

	public IPayment getSalaryInKind() {
		return map.get(PaymentType.SALARY_IN_KIND);
	}
	public void setSalaryInKind(IPayment p) {
		put(PaymentType.SALARY_IN_KIND, p);
	}

	public IPayment getSpecialSecurityBenefits() {
		return map.get(PaymentType.SOCIAL_SECURITY_BENEFITS);
	}
	public void setSpecialSecurityBenefits(IPayment p) {
		put(PaymentType.SOCIAL_SECURITY_BENEFITS, p);
	}
	
	public IPayment getMovingCompensation() {
		return map.get(PaymentType.MOVING_COMPENSATION);
	}
	public void setMovingCompensation(IPayment p) {
		put(PaymentType.MOVING_COMPENSATION, p);
	}
	
	public SalarySupplements getSalarySupplements() {
		if (salarySupplements == null) {
			setSalarySupplements( new SalarySupplements() );
		}
		return salarySupplements;
	}
	public void setSalarySupplements(SalarySupplements salarySupplements) {
		this.salarySupplements = salarySupplements;
	}
	public void addSalarySupplements(IPayment p) {
		getSalarySupplements().addPayment(p);
	}
	
	public CompensationOrPrepaidExpenses getCompensationOrPrepaidExpenses() {
		if (compensationOrPrepaidExpenses == null) {
			setCompensationOrPrepaidExpenses( new CompensationOrPrepaidExpenses() );
		}
		return compensationOrPrepaidExpenses;
	}
	public void setCompensationOrPrepaidExpenses(CompensationOrPrepaidExpenses p) {
		this.compensationOrPrepaidExpenses = p;
	}
	public void addCompensationOrPrepaidExpenses(IPayment p) {
		getCompensationOrPrepaidExpenses().addPayment(p);
	}
	
	public OtherNonWages getOtherNonWages() {
		if (otherNonWages == null) {
			setOtherNonWages( new OtherNonWages() );
		}
		return otherNonWages;
	}
	public void setOtherNonWages(OtherNonWages p) {
		this.otherNonWages = p;
	}
	public void addOtherNonWages(IPayment p) {
		getOtherNonWages().addPayment(p);
	}

	private void put(PaymentType type, IPayment p) {
		map.put(type,p);
	}

	public Double getTotal() {
		double total = 0;
		for (IPayment d: map.values()) {
			total = CommonUtil.round( total + d.getAmount());	
		}
		for (IPayment d: getSalarySupplements().getValues()) {
			total = CommonUtil.round( total + d.getAmount());	
		}
		for (IPayment d: getCompensationOrPrepaidExpenses().getValues()) {
			total = CommonUtil.round( total + d.getAmount());	
		}
		return total;  
	}
	
	/**
	 * Coleccion de los devengos ordenados para el jasper de impresion de la nomina
	 * 
	 * @return
	 */
	public Collection<IPayment> getCollection() {
		List<IPayment> list = new LinkedList<IPayment>();
		IPayment p;

		p = map.get(PaymentType.BASE_SALARY);
		if (p != null) {
			list.add(map.get(PaymentType.BASE_SALARY));
		}
		if(!getSalarySupplements().getValues().isEmpty()){
			for(IPayment payment: getSalarySupplements().getValues()){
				list.add(payment);
			}
		}
		p = map.get(PaymentType.STRUCTURAL_HOURS);
		if (p != null) {
			list.add(map.get(PaymentType.STRUCTURAL_HOURS));
		}
		p = map.get(PaymentType.NON_STRUCTURAL_HOURS);
		if (p != null) {
			list.add(map.get(PaymentType.NON_STRUCTURAL_HOURS));
		}
		p = map.get(PaymentType.SPECIAL_BONUSES);
		if (p != null) {
			list.add(map.get(PaymentType.SPECIAL_BONUSES));
		}
		p = map.get(PaymentType.SALARY_IN_KIND);
		if (p != null) {
			list.add(map.get(PaymentType.SALARY_IN_KIND));
		}
		if(!getCompensationOrPrepaidExpenses().getValues().isEmpty()){
			for(IPayment payment: getCompensationOrPrepaidExpenses().getValues()){
				list.add(payment);
			}
		}
		p = map.get(PaymentType.SOCIAL_SECURITY_BENEFITS);
		if (p != null) {
			list.add(map.get(PaymentType.SOCIAL_SECURITY_BENEFITS));
		}
		p = map.get(PaymentType.MOVING_COMPENSATION);
		if (p != null) {
			list.add(map.get(PaymentType.MOVING_COMPENSATION));
		}
		if(!getOtherNonWages().getValues().isEmpty()){
			for(IPayment payment: getOtherNonWages().getValues()){
				list.add(payment);
			}
		}
		return list;
	}
	
}
