package com.esferalia.aon.salary.payment;


import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;

import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.salary.enumeration.PaymentType;

public class CompositePayment implements IPayment {
	
	List<IPayment> payments;

	String name;
	String description;
	String expression;
	PaymentType type;
	
	protected List<IPayment> getPayments() {
		if (payments == null) {
			setPayments(new LinkedList<IPayment>());
		}
		return payments;
	}
	protected void setPayments(List<IPayment> payments) {
		this.payments = payments;
	}

	public void addPayment(IPayment p) {
		addPayment(p,true);
	}

	public void addPayment(IPayment p, boolean check) {
		if (getPayments().size() == 0) {
			setDescription(p.getDescription());
			setType(p.getType());
			setExpression(p.getExpression());
			setName(p.getName());
		}
		boolean ok = true;
		// En principio solo se permite la inclusión en un composite de payments del mismo tipo,
		// lo de la descripción es más discutible ....
		if (check 
			 &&	ObjectUtils.equals(getDescription(), p.getDescription()) 
			 && ObjectUtils.equals(getType(), p.getType())) {
				getPayments().add(p);
		}
		if (ok) {
			getPayments().add(p);
		} else {
			throw new IllegalArgumentException("No se soportan devengos de diferente tipo,función o descripción");
		}
	}
	
	@Override
	public double getAmount() {
		double t = 0;
		for (IPayment d : getPayments()) {
			t = CommonUtil.round(t + d.getAmount());
		}
		return t;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getExpression() {
		return expression;
	}

	@Override
	public PaymentType getType() {
		return type;
	}
	
	protected void setDescription(String description) {
		this.description = description;
	}
	protected void setName(String name) {
		this.name = name;
	}
	protected void setExpression(String expression) {
		this.expression = expression;
	}
	protected void setType(PaymentType type) {
		this.type = type;
	}

}
