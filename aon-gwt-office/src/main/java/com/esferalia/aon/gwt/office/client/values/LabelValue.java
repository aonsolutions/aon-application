package com.esferalia.aon.gwt.office.client.values;

public class LabelValue extends Value<LabelValue.Prop> {

	public static enum Prop implements ValueProp {
		URL("url"), NAME("name"), COLOR("color"), TYPE("type");

		private final String value;

		private Prop(String value) {
			this.value = value;
		}

		@Override
		public String value() {
			return value;
		}
	}

	public void setName(String name) {
		prop.put(Prop.NAME, name);
	}
	
	public void setColor(String color) {
		prop.put(Prop.COLOR, color);
	}
	
	public void setUrl(String url) {
		prop.put(Prop.URL, url);
	}
	
	public void setType(Integer type) {
		prop.put(Prop.TYPE, type);
	}
}
