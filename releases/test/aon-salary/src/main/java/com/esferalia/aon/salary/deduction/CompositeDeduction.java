package com.esferalia.aon.salary.deduction;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;

import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.salary.enumeration.DeductionType;

public class CompositeDeduction implements IDeduction {
	
	List<IDeduction> deductions;

	String description;
	String function;
	DeductionType type;
	
	private List<IDeduction> getDeductions() {
		if (deductions == null) {
			setDeductions(new LinkedList<IDeduction>());
		}
		return deductions;
	}
	private void setDeductions(List<IDeduction> deductions) {
		this.deductions = deductions;
	}

	public void addDeduction(IDeduction d) {
		if (getDeductions().size() == 0) {
			setDescription(d.getDescription());
			setType(d.getType());
			setFunction(d.getExpression());
		}
		if (ObjectUtils.equals(getDescription(), d.getDescription()) 
			 && ObjectUtils.equals(getType(), d.getType())
			 && ObjectUtils.equals(getExpression(), d.getExpression())) {
				getDeductions().add(d);
		} else {
			throw new IllegalArgumentException("No se soportan deducciones de diferente tipo,función o descripción");
		}
	}
	
	@Override
	public double getAmount() {
		double t = 0;
		for (IDeduction d : getDeductions()) {
			t = CommonUtil.round(t + d.getAmount());
		}
		return t;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getExpression() {
		return function;
	}

	@Override
	public DeductionType getType() {
		return type;
	}

	
	private void setDescription(String description) {
		this.description = description;
	}
	private void setFunction(String function) {
		this.function = function;
	}
	private void setType(DeductionType type) {
		this.type = type;
	}

}
