package com.esferalia.aon.occam.api.model.type;

public enum Mod190Key {
	 A (null)
	,B (new String[] { "01", "02", "03" })
	,C (null)
	,D (null)
	,E (new String[] { "01", "02" })
	,F (new String[] { "01", "02" })
	,G (new String[] { "01", "02", "03", "04" })
	,H (new String[] { "01","02", "03", "04" })
	,I (new String[] { "01", "02" })
	,J (null)
	,K (new String[] { "01", "02" })
	,L (new String[] { "01", "02", "03", "04", "05", "06"
					 , "07", "08", "09", "10", "11", "12"
					 , "13", "14", "15", "16", "17", "18"
					 , "19", "20", "21" }), ;

	private String[] subKeys;

	private Mod190Key(String[] subKeys) {
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
	
	public static Mod190Key getDefaultKeyForProfessionalRetentions() {
		return Mod190Key.G;
	}
	public static String getDefaultSubkeyForProfessionalRetentions() {
		return Mod190Key.G.subKeys[0];
	}
	public static Mod190Key getDefaultKeyForFarmerRetentions() {
		return Mod190Key.H;
	}
	public static String getDefaultSubkeyForFarmerRetentions() {
		return Mod190Key.H.subKeys[0];
	}
	public static Mod190Key getDefaultKeyForTransportRetentions() {
		return Mod190Key.H;
	}
	public static String getDefaultSubkeyForTransportRetentions() {
		return Mod190Key.H.subKeys[3];
	}
}
