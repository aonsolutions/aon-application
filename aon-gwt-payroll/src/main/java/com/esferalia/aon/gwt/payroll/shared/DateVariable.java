package com.esferalia.aon.gwt.payroll.shared;

import java.util.Date;

import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;

public class DateVariable extends StringVariable {
	
	public static class Builder extends StringVariable.Builder {
		
		DateVariable template = new DateVariable() ;

		@Override
		public Builder setValue(Object value) {
			template.setValue(value);
			return this;
		}

		@Override
		public Builder setName(String name) {
			template.setName(name);
			return this;
		}

		@Override
		public Builder setDomain(Integer domain) {
			template.setDomain(domain);
			return this;
		}

		@Override
		public Builder setStartDate(Date startDate) {
			template.setStartDate(startDate);
			return this;
		}

		@Override
		public Builder setEndDate(Date endDate) {
			template.setEndDate(endDate);
			return this;
		}

		@Override
		public Builder setImplicit(boolean implicit) {
			template.setImplicit(implicit);
			return this;
		}

		@Override
		public Builder setScope(Scope scope) {
			template.setScope(scope);
			return this;
		}

		@Override
		public Builder setExpression(String expression) {
			template.setExpression(expression);
			return this;
		}

		@Override
		public Builder setDefined(boolean[] defined) {
			template.setDefined(defined);
			return this;
		}

		@Override
		public DateVariable create() {
			DateVariable nevv = new DateVariable();
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
	
	@Override
	public void setValue(Object object) {
		super.setValue(Shared.format((Date)object));
	}

	@Override
	public Date getValue() {
		return super.value != null ? Shared.parse(super.value) : null;
	}
	
}
