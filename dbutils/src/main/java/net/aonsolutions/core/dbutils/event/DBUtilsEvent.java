package net.aonsolutions.core.dbutils.event;

import java.util.EventObject;

public class DBUtilsEvent extends EventObject  {

	private static final long serialVersionUID = 6696093108839597015L;
	private String message;

	public DBUtilsEvent ( Object source, String message ) {
		super ( source );
		this.message = message;
	}
	
	public String getMessage(){
		return message;
	}
}