package com.esferalia.aon.occam.api.model.fiscal.mod390;

public enum ActivityType {
	 A01 ("Arrendadores de bienes inmuebles")
	,A02 ("Ganader\u00EDa independiente")
	,A03 ("Resto de actividades empresariales no incluidas en los dos subtipos anteriores")
	,A04 ("Actividades profesionales de car\u00E1cter art\u00EDstico o deportivo")
	,A05 ("Resto de actividades profesionales")
	,B01 ("Actividad agr\u00EDcola")
	,B02 ("Actividad ganadera dependiente")
	,B03 ("Actividad forestal")
	,B04 ("Producci\u00F3n del mejill\u00F3n en batea")
	,B05 ("Actividad pesquera, excepto la actividad de producci\u00F3n de mejill\u00F3n en batea")
	,C   ("Actividades no iniciadas")	
	;
	
	private String description;
	
	private ActivityType(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}

	public static String toString(ActivityType activityType) {
		if (activityType == null) return null;
		return activityType.toString();
	}
	
	public static ActivityType ensure(String activityType) {
		if (activityType == null) return null;
		try {
			return ActivityType.valueOf(activityType);
		} catch (Exception e) {
			return null;	
		}
	}
}














