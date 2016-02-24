package com.esferalia.aon.gwt.office.client.values;

public class LabelControlValue extends Value<LabelControlValue.Prop> {

	public static enum Prop implements ValueProp {
		ADD("addLabels"), 
		DELETED("deletedLabels");

		private final String value;

		private Prop(String value) {
			this.value = value;
		}

		@Override
		public String value() {
			return value;
		}
	}

	public void addLabels(String[] addLabels) {
		prop.put(Prop.ADD, addLabels);
	}
	
	public void deletedLabels(String[] deletedLabels) {
		prop.put(Prop.DELETED, deletedLabels);
	}
}
