package com.esferalia.aon.gwt.fiscal.client.mod190;

public interface Model190Enum {

	public enum Key {
		A(null), B(new String[] { "01", "02", "03" }), C(null), D(null), E(null), F(
				new String[] { "01", "02" }), G(
				new String[] { "01", "02", "03" }), H(new String[] { "01",
				"02", "03", "04" }), I(new String[] { "01", "02" }), J(null), K(
				new String[] { "01", "02" }), L(new String[] { "01", "02",
				"03", "04", "05", "06", "07", "08", "09", "10", "11", "12",
				"13", "14", "15", "16", "17", "18", "19", "20", "21" }), ;

		private String[] subKeys;

		private Key(String[] subKeys) {
			this.subKeys = subKeys;
		}

		public boolean hasSubkeys() {
			return this.subKeys != null;
		}

		public String[] getSubKeys() {
			return subKeys;
		}

		public String getValue() {
			return toString();
		}
	}

}
