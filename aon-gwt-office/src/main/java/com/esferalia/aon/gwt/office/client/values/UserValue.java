package com.esferalia.aon.gwt.office.client.values;

public class UserValue extends Value<UserValue.Prop> {

	public static enum Prop implements ValueProp {
		ID("id"), 
		NAME("name"), 
		LOGIN("login");

		private final String value;

		private Prop(String value) {
			this.value = value;
		}

		@Override
		public String value() {
			return value;
		}
	}

	public void setId(String id) {
		prop.put(Prop.ID, id);
	}

	public void setName(String name) {
		prop.put(Prop.NAME, name);
	}
	
	public void setLogin(String login) {
		prop.put(Prop.LOGIN, login);
	}
}
