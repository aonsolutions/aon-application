package com.esferalia.aon.gwt.payroll.shared;

import java.util.Date;

import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;
import com.google.gwt.i18n.client.DateTimeFormat;

public class StringVariable extends Variable {

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
		
		public StringVariable create() {
			StringVariable nevv = new StringVariable();
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

	String value;

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public void setValue(Object value) {
		if ( value == null )
			this.value = null;
		else if ( value instanceof String)
			this.value = (String) value;
		else if ( value instanceof Date)
			this.value = format((Date)value);
		else 
			this.value = String.valueOf(value);
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
	
	public static Date parse(String text) {
	    return DateTimeFormat.getFormat("dd/MM/yyyy").parse(text);
	}
	
	public static String format(Date date) {
		return date.getDate() + "/" +  (date.getMonth()+1) + "/" +  ( date.getYear() + 1900);		
	}
}