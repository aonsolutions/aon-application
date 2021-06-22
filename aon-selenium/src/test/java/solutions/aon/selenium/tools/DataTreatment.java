package solutions.aon.selenium.tools;

public class DataTreatment {

	
	public static Object safeValue(Object obj, Object def){
		return obj == null? def : obj;		
	}
	
	public static Double safeDouble(Double obj, Double def) {
		return  (double) safeValue(obj, def);
	}
	
	public static Integer safeInt(Integer obj, Integer def) {
		return  (int) safeValue(obj, def);
	}
	
	public static String safeString(String obj, String def) {
		return  "" + safeValue(obj, def);
	}

	
}
