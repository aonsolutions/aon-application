package net.aonsolutions.payroll.report;

import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;

public class Item<T> implements HasName, HasType<T>, HasAmount, HasDescription, HasExpression, HasId {
	
	private Integer id;
	private T type;
	private String name;
	private Double amount;
	private String expression;
	private String description;
	

	
	@Override
	public Integer getId() {
		return id;
	}
	
	public Item<T> setId(Integer id) {
		this.id = id;
		return this;
	}

	@Override
	public String getName() {
		return name;
	}
	
	public Item<T> setName(String name) {
		this.name = name;
		return this;
	}
	
	@Override
	public double getAmount() {
		return amount;
	}
	
	public Item<T> setAmount(Double amount) {
		this.amount = amount;
		return this;
	}

	@Override
	public String getDescription() {
		return description;
	}
	
	public Item<T> setDescription(String description) {
		this.description = description;
		return this;
	}
	
	@Override
	public String getExpression() {
		return expression;
	}
	
	public Item<T> setExpression(String expression) {
		this.expression = expression;
		return this;
	}
	
	@Override
	public T getType() {
		return type;
	}
	
	public Item<T> setType( T type ) {
		this.type = type;
		return this;
	}
	
	public <C> C get(Class<C> clazz) {
		return (C) this;
	}

	public Item<T> setType(Byte b, Class<T> t) {
		if ( b == null )
			return this;
		if ( b < 0 )
			return this;
		T values [] = t.getEnumConstants();
		if ( b >= values.length )
			return this;
		setType(values[b]);
		return this;
	}

	public static class Payment extends Item<PaymentType> implements net.aonsolutions.payroll.report.Payment {
		
		public Item<PaymentType> setType(Byte b) {
			return setType(b, PaymentType.class);
		}
		
	}
	
	public static class Deduction extends Item<DeductionType> implements net.aonsolutions.payroll.report.Deduction {

		public Item<DeductionType> setType(Byte b) {
			return setType(b, DeductionType.class);
		}
		
	}
}