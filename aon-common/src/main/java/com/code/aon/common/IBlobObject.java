package com.code.aon.common;

import java.io.Serializable;

public interface IBlobObject {
	
	String DATA_PROPERTY = "data";
	
	String INCOME_FILE_PROPERTY = "incomeFile";
	
	String OUTCOME_FILE_PROPERTY = "outcomeFile";
	
	String[] DATA_BLOB_PROPERTIES = new String[]{DATA_PROPERTY};
	
	String[] FILE_BLOB_PROPERTIES = new String[]{INCOME_FILE_PROPERTY, OUTCOME_FILE_PROPERTY};
		
	Serializable getReference();
	
	IBlobManager getManager();
	
	String[] getBlobProperties();
	
	void reset();

}
