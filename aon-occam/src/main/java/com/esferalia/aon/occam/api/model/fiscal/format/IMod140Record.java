package com.esferalia.aon.occam.api.model.fiscal.format;

public interface IMod140Record {
	public static final String MODEL = "140";
	public static final String END_LINE = "\r\n";
	
	
	IRecordFiller getFiller();
}
