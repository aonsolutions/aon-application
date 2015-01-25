package com.esferalia.aon.occam.api.model.fiscal;

public enum Mod193Key {
	 A (new String[] { "01", "02", "03", "04", "05", "06", "07", "08" })
	,B (new String[] { "01", "02", "03", "04", "05", "06", "07" })
	,C (new String[] { "01", "02", "03", "04", "05", "06", "07" })
	,D (new String[] { "01", "02", "03", "04", "05", "06", "07", 
					   "08", "09", "10", "11", "12" })
	;
	 
	private String[] natures;

	private Mod193Key(String[] natures) {
		this.natures = natures;
	}

	public boolean hasNatures() {
		return this.natures != null;
	}

	public String[] getNatures() {
		return natures;
	}

	public String getValue() {
		return toString();
	}
	
}
