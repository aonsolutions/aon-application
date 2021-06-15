package net.aonsolutions.aon.api.servlet.registry;

public enum RegistryAdditionalInfo {
	
	ADDRESS,
	ADDRESSES,
	BANKS,
	PAYMETHODS,
	MEDIA;

	private RegistryAdditionalInfo() {
		// TODO Auto-generated constructor stub
	}
	
	public static RegistryAdditionalInfo safeValueOf( String i ) {
		for (RegistryAdditionalInfo rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
	
}
