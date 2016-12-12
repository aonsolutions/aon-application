package com.esferalia.aon.occam.api.model.type;

public enum Mod1902016Key {
	 A (null)
	,B (new String[] { "01", "02", "03"})
	,C (null)
	,D (null)
	,E (new String[] { "01", "02", "03", "04"  })
	,F (new String[] { "01", "02" }) 
	,G (new String[] { "01", "02", "03"})
	,H (new String[] { "01", "02", "03", "04" })
	,I (new String[] { "01", "02" })
	,J (null)
	,K (new String[] { "01", "02", "03" })
	,L (new String[] { "01", "02", "03", "04", "05", "06"
					 , "07", "08", "09", "10", "11", "12"
					 , "13", "14", "15", "16", "17", "18"
					 , "19", "20", "21", "22", "23", "24", "25" }), ;

	private String[] subKeys;

	private Mod1902016Key(String[] subKeys) {
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
	
	public static Mod1902016Key getDefaultKeyForProfessionalRetentions() {
		return Mod1902016Key.G;
	}
	public static String getDefaultSubkeyForProfessionalRetentions() {
		return Mod1902016Key.G.subKeys[0];
	}
	public static Mod1902016Key getDefaultKeyForFarmerRetentions() {
		return Mod1902016Key.H;
	}
	public static String getDefaultSubkeyForFarmerRetentions() {
		return Mod1902016Key.H.subKeys[0];
	}
	public static Mod1902016Key getDefaultKeyForTransportRetentions() {
		return Mod1902016Key.H;
	}
	public static String getDefaultSubkeyForTransportRetentions() {
		return Mod1902016Key.H.subKeys[3];
	}
}
