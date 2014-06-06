package com.esferalia.aon.salary.payment;


import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.code.aon.AonVersion;
import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.salary.enumeration.PaymentType;


public class Payments implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Map<PaymentType, IPayment> map;
	private BaseSalary baseSalary;
	private SalarySupplements salarySupplements;
	private SalaryInKind salaryInKind;
	private CompensationOrPrepaidExpenses compensationOrPrepaidExpenses;
	private SpecialSecurityBenefits specialSecurityBenefits;
	private SpecialBonuses specialBonuses;
	private MovingCompensation movingCompensation;
	private OvertimeHours overtimeHours;
	private OtherNonWages otherNonWages;

	public Payments() {
		map = new HashMap<PaymentType, IPayment>();	
	}

	public BaseSalary getBaseSalary() {
		if (baseSalary == null) {
			setBaseSalary( new BaseSalary() );
		}
		return baseSalary;
	}
	public void setBaseSalary(BaseSalary p) {
		this.baseSalary = p;
	}
	public void addBaseSalary(IPayment p) {
		getBaseSalary().addPayment(p);
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

	public OvertimeHours getOvertimeHours() {
		if (overtimeHours == null) {
			setOvertimeHours( new OvertimeHours() );
		}
		return overtimeHours;
	}
	public void setOvertimeHours(OvertimeHours p) {
		this.overtimeHours = p;
	}
	public void addOvertimeHours(IPayment p) {
		getOvertimeHours().addPayment(p);
	}

	public SpecialBonuses getSpecialBonuses() {
		if (specialBonuses == null) {
			setSpecialBonuses( new SpecialBonuses() );
		}
		return specialBonuses;
	}
	public void setSpecialBonuses(SpecialBonuses p) {
		this.specialBonuses = p;
	}
	public void addSpecialBonuses(IPayment p) {
		getSpecialBonuses().addPayment(p);
	}

	public SalaryInKind getSalaryInKind() {
		if (salaryInKind == null) {
			setSalaryInKind( new SalaryInKind() );
		}
		return salaryInKind;
	}
	public void setSalaryInKind(SalaryInKind p) {
		this.salaryInKind = p;
	}
	public void addSalaryInKind(IPayment p) {
		getSalaryInKind().addPayment(p);
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

	public SpecialSecurityBenefits getSpecialSecurityBenefits() {
		if (specialSecurityBenefits == null) {
			setSpecialSecurityBenefits(new SpecialSecurityBenefits());
		}
		return specialSecurityBenefits;
	}
	public void setSpecialSecurityBenefits(SpecialSecurityBenefits p) {
		this.specialSecurityBenefits = p;
	}
	public void addSpecialSecurityBenefits(IPayment p) {
		getSpecialSecurityBenefits().addPayment(p);
	}
	
	public MovingCompensation getMovingCompensation() {
		if (movingCompensation == null) {
			setMovingCompensation(new MovingCompensation());
		}
		return movingCompensation;
	}
	public void setMovingCompensation(MovingCompensation p) {
		this.movingCompensation = p;
	}
	public void addMovingCompensation(IPayment p) {
		getMovingCompensation().addPayment(p);
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

	/**
	 * amounts group by PaymentType
	 * @return
	 */
	public Double getBaseSalaryAmount() {
		double total = 0;
		for (IPayment d: getBaseSalary().getValues()) {
			total = CommonUtil.round( total + d.getAmount());	
		}
		return total;  
	}
	public Double getSalarySupplementsAmount() {
		double total = 0;
		for (IPayment d: getSalarySupplements().getValues()) {
			total = CommonUtil.round( total + d.getAmount());	
		}
		return total;  
	}
	public Double getSalaryInKindAmount() {
		double total = 0;
		for (IPayment d: getSalaryInKind().getValues()) {
			total = CommonUtil.round( total + d.getAmount());	
		}
		return total;  
	}
	public Double getCompensationOrPrepaidExpensesAmount() {
		double total = 0;
		for (IPayment d: getCompensationOrPrepaidExpenses().getValues()) {
			total = CommonUtil.round( total + d.getAmount());	
		}
		return total;  
	}
	public Double getSpecialSecurityBenefitsAmount() {
		double total = 0;
		for (IPayment d: getSpecialSecurityBenefits().getValues()) {
			total = CommonUtil.round( total + d.getAmount());	
		}
		return total;  
	}
	public Double getSpecialBonusesAmount() {
		double total = 0;
		for (IPayment d: getSpecialBonuses().getValues()) {
			total = CommonUtil.round( total + d.getAmount());	
		}
		return total;  
	}
	public Double getMovingCompensationAmount() {
		double total = 0;
		for (IPayment d: getMovingCompensation().getValues()) {
			total = CommonUtil.round( total + d.getAmount());	
		}
		return total;  
	}
	public Double getOvertimeHoursAmount() {
		double total = 0;
		for (IPayment d: getOvertimeHours().getValues()) {
			total = CommonUtil.round( total + d.getAmount());	
		}
		return total;  
	}
	public Double getOtherNonWagesAmount() {
		double total = 0;
		for (IPayment d: getOtherNonWages().getValues()) {
			total = CommonUtil.round( total + d.getAmount());	
		}
		return total;  
	}
	public Double getTotal() {
		return CommonUtil.round( getBaseSalaryAmount() +
		getSalarySupplementsAmount() + 
		getSalaryInKindAmount() +
		getCompensationOrPrepaidExpensesAmount() + 
		getSpecialSecurityBenefitsAmount() + 
		getSpecialBonusesAmount() +
		getMovingCompensationAmount() + 
		getOvertimeHoursAmount() +
		getOtherNonWagesAmount());
	}
	
	/**
	 * Coleccion de los devengos ordenados para el jasper de impresion de la nomina
	 * 
	 * @return
	 */
	public Collection<IPayment> getCollection() {
		List<IPayment> list = new LinkedList<IPayment>();
		IPayment p;

		if(!getBaseSalary().getValues().isEmpty()){
			for(IPayment payment: getBaseSalary().getValues()){
				list.add(payment);
			}
		}
		if(!getSalarySupplements().getValues().isEmpty()){
			for(IPayment payment: getSalarySupplements().getValues()){
				list.add(payment);
			}
		}
		if(!getOvertimeHours().getValues().isEmpty()){
			for(IPayment payment: getOvertimeHours().getValues()){
				list.add(payment);
			}
		}
		p = map.get(PaymentType.NON_STRUCTURAL_HOURS);
		if (p != null) {
			list.add(map.get(PaymentType.NON_STRUCTURAL_HOURS));
		}
		if(!getSpecialBonuses().getValues().isEmpty()){
			for(IPayment payment: getSpecialBonuses().getValues()){
				list.add(payment);
			}
		}
		if(!getSalaryInKind().getValues().isEmpty()){
			for(IPayment payment: getSalaryInKind().getValues()){
				list.add(payment);
			}
		}
		if(!getCompensationOrPrepaidExpenses().getValues().isEmpty()){
			for(IPayment payment: getCompensationOrPrepaidExpenses().getValues()){
				list.add(payment);
			}
		}
		if(!getSpecialSecurityBenefits().getValues().isEmpty()){
			for(IPayment payment: getSpecialSecurityBenefits().getValues()){
				list.add(payment);
			}
		}
		if(!getMovingCompensation().getValues().isEmpty()){
			for(IPayment payment: getMovingCompensation().getValues()){
				list.add(payment);
			}
		}
		if(!getOtherNonWages().getValues().isEmpty()){
			for(IPayment payment: getOtherNonWages().getValues()){
				list.add(payment);
			}
		}
		return list;
	}

	public Collection<IPayment> getOrderedPayment() {
		List<IPayment> list = new LinkedList<IPayment>();
		list.addAll(getSalarySupplements().getValues());
		Collections.sort(list, new Comparator<IPayment>() {
			@Override
			public int compare(IPayment x, IPayment y) {
				if ( x.getType() == y.getType() ) {
					return x.getDescription().compareTo(y.getDescription());
				}
				if ( y == null || x.getType().ordinal() >= y.getType().ordinal() )
					return 1;
				if ( x == null || x.getType().ordinal() < y.getType().ordinal() )
					return -1;
				return ( (Comparable) x ).compareTo(y);
			}
		});
		return list;
	}
	
}
