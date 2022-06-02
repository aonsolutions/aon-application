package com.esferalia.aon.gwt.payroll.shared;

import java.util.Date;

import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;

public class NumberVariable extends Variable {

	public static class Builder {
		StringVariable template = new StringVariable() ;

		public Builder setValue(Object value) {
			template.setValue(value);
			return this;
		}

		public Builder setName(String name) {
			template.setName(name);
			return this;
		}

		public Builder setDomain(Integer domain) {
			template.setDomain(domain);
			return this;
		}

		public Builder setStartDate(Date startDate) {
			template.setStartDate(startDate);
			return this;
		}

		public Builder setEndDate(Date endDate) {
			template.setEndDate(endDate);
			return this;
		}

		public Builder setImplicit(boolean implicit) {
			template.setImplicit(implicit);
			return this;
		}

		public Builder setScope(Scope scope) {
			template.setScope(scope);
			return this;
		}

		public Builder setExpression(String expression) {
			template.setExpression(expression);
			return this;
		}

		public Builder setDefined(boolean[] defined) {
			template.setDefined(defined);
			return this;
		}
		
		public NumberVariable create() {
			NumberVariable nevv = new NumberVariable();
			nevv.setName(template.getName());
			nevv.setValue(template.getValue());
			nevv.setDefined(template.defined); // TODO: ???
			nevv.setScope(template.getScope());
			nevv.setImplicit(template.isImpicit());
			nevv.setDomain(template.getDomain());
			nevv.setStartDate(template.getStartDate());
			nevv.setEndDate(template.getEndDate());
			nevv.setExpression(template.getExpression());
			return nevv;
		}
		
	}

	Number value;

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public void setValue(Object value) {
		if (value == null)
			this.value = null;
		else if (value instanceof Number)
			this.value = (Number) value;
		else if (value instanceof String)
			this.value = Double.valueOf((String) value);
		else
			throw new IllegalArgumentException(value
					+ " can't be assigned to a '" + Number.class.getName()
					+ "'");
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	public double doubleValue() {
		return value != null ? value.doubleValue() : 0.00;
	}

	
	

}