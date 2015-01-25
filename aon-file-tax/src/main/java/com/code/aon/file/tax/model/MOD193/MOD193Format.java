package com.code.aon.file.tax.model.MOD193;

import com.code.aon.config.enumeration.Administration;

public enum MOD193Format {

	ALAVA_2014(2014,Administration.ALAVA
			,"/com/code/aon/file/tax/model/MOD193/Deponent.xml"
			,"/com/code/aon/file/tax/model/MOD193/Receiver.xml"
			,"/com/code/aon/file/tax/model/MOD193/Expenses.xml"),
	BIZKAIA_2014(2014,Administration.BIZKAIA
			,"/com/code/aon/file/tax/model/MOD193/Deponent.xml"
			,"/com/code/aon/file/tax/model/MOD193/Receiver.xml"
			,"/com/code/aon/file/tax/model/MOD193/Expenses.xml"),
	GIPUZKOA_2014(2014,Administration.GIPUZKOA
			,"/com/code/aon/file/tax/model/MOD193/Deponent.xml"
			,"/com/code/aon/file/tax/model/MOD193/Receiver.xml"
			,"/com/code/aon/file/tax/model/MOD193/Expenses.xml"),
	NAVARRA_2014(2014,Administration.NAVARRA
			,"/com/code/aon/file/tax/model/MOD193/Deponent.xml"
			,"/com/code/aon/file/tax/model/MOD193/Receiver.xml"
			,"/com/code/aon/file/tax/model/MOD193/Expenses.xml"),
	AEAT_2014(2014,Administration.COMMON_TERRITORY
			,"/com/code/aon/file/tax/model/MOD193/Deponent.xml"
			,"/com/code/aon/file/tax/model/MOD193/Receiver.xml"
			,"/com/code/aon/file/tax/model/MOD193/Expenses.xml");
			  
	private Integer year;
	private Administration administration;
	private String deponentMetadataResource;
	private String receiverMetadataResource;
	private String expensesMetadataResource;


	private MOD193Format(Integer year,Administration administration,String deponentMetadataResource,
			String receiverMetadataResource, String expensesMetadataResource)	{
		this.year =  year;
		this.administration = administration;
		this.deponentMetadataResource = deponentMetadataResource;
		this.receiverMetadataResource = receiverMetadataResource;
		this.expensesMetadataResource = expensesMetadataResource;
	}
	
	public Integer getYear() {
		return year;
	}
	
	public Administration getAdministration() {
		return administration;
	}

	public String getDeponentMetadataResource() {
		return deponentMetadataResource;
	}

	public String getReceiverMetadataResource() {
		return receiverMetadataResource;
	}
	public String getExpensesMetadataResource() {
		return expensesMetadataResource;
	}
}
