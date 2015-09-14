package com.esferalia.aon.gwt.office.client.values;

public class IssueValue extends Value<IssueValue.Prop> {
	
	public static enum Prop implements ValueProp {
		OPEN("open"),
		CLOSE("close"),
		;
		public final String value;
		
		private Prop(String value) {
			this.value = value;
		}
		
		@Override
		public String value() {
			return value;
		}
	}
}
