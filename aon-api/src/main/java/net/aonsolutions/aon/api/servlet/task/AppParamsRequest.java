package net.aonsolutions.aon.api.servlet.task;

public enum AppParamsRequest{
	APP_REQUESTS_INT_WORKGROUP,
	APP_REQUESTS_INT_TASK_HOLDER,
	
	//-----INTERNA NOTIFICATION
	APP_REQUESTS_INT_OPENED,
	APP_REQUESTS_INT_CLOSED,
	APP_REQUESTS_INT_COMMENT,
	APP_REQUESTS_INT_ASSIGN,
	APP_REQUESTS_EXT_WORKGROUP,
	APP_REQUESTS_EXT_TASK_HOLDER,
	
	//-----EXTERNA NOTIFICATION
	APP_REQUESTS_EXT_OPENED,
	APP_REQUESTS_EXT_CLOSED,
	APP_REQUESTS_EXT_COMMENT,
	APP_REQUESTS_EXT_ASSIGN,
	APP_REQUESTS_EMAIL_RATING,
	
	//-----INTERNA EMAIL
	APP_REQUESTS_INT_EMAIL_OPENED,
	APP_REQUESTS_INT_EMAIL_CLOSED,
	APP_REQUESTS_INT_EMAIL_COMMENT,
	APP_REQUESTS_INT_EMAIL_ASSIGN,
	
	//-----EXTERNA EMAIL
	APP_REQUESTS_EXT_EMAIL_OPENED,
	APP_REQUESTS_EXT_EMAIL_CLOSED,
	APP_REQUESTS_EXT_EMAIL_COMMENT,
	APP_REQUESTS_EXT_EMAIL_ASSIGN,
	APP_REQUESTS_EMAIL_RATING_CLOSED
	;
	
	private AppParamsRequest() {}

	public String getName() {
    	return this.toString().toLowerCase();
    }
    
	public static AppParamsRequest safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static AppParamsRequest safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= AppParamsRequest.values().length) return null;
		return AppParamsRequest.values()[i];
	}
}
