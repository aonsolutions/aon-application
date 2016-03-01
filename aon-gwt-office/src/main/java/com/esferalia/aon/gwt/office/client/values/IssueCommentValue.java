package com.esferalia.aon.gwt.office.client.values;

public class IssueCommentValue extends Value<IssueCommentValue.Prop> {

	public static enum Prop implements ValueProp {
		
		BODY("body"),
		StartDate("startDate")
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
	
	public void setBody(String body) {
		prop.put(Prop.BODY, body);
	}
	
	public void setStartDate(String startDate) {
		prop.put(Prop.StartDate, startDate);
	}
}
