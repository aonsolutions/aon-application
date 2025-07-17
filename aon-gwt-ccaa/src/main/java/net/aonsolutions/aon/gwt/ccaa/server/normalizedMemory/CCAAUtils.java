package net.aonsolutions.aon.gwt.ccaa.server.normalizedMemory;

public class CCAAUtils {

	private CCAAUtils() {
		
	}
	
	public static String getDocumentTypeDescription(String code) {
		if (code == null)
			return "";
		else 
			return switch (code) {
				case "1" -> "DNI";
				case "2" -> "NIF";
				case "3" -> "NIE";
				case "4" -> "TIN";
				case "5" -> "PASAPORTE";
			    case "6" -> "OTRO";
				default -> "";
			};
	}

}
