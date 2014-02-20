package com.code.aon.file.tax.model.MOD340;

import com.code.aon.config.enumeration.Administration;

public enum MOD340Format {

	AEAT_2014(2014
			,Administration.COMMON_TERRITORY
			,"/com/code/aon/file/tax/model/MOD340/xml/2014_Deponent.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2014_IssuedInvoice.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2014_ReceivedInvoice.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2014_InvestmentInvoice.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2014_IntracommunitaryInvoice.xml"),
	ALAVA_2014(2014
			,Administration.ALAVA
			,"/com/code/aon/file/tax/model/MOD340/xml/2014_Deponent.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2014_IssuedInvoice.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2014_ReceivedInvoice.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2014_InvestmentInvoice.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2014_IntracommunitaryInvoice.xml"),
	BIZKAIA_2014(2014
			,Administration.BIZKAIA
			,"/com/code/aon/file/tax/model/MOD340/xml/2014_Deponent.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2014_IssuedInvoice.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2014_ReceivedInvoice.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2014_InvestmentInvoice.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2014_IntracommunitaryInvoice.xml"),
	GIPUZKOA_2014(2014
			,Administration.GIPUZKOA
			,"/com/code/aon/file/tax/model/MOD340/xml/2014_Deponent.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2014_IssuedInvoice.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2014_ReceivedInvoice.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2014_InvestmentInvoice.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2014_IntracommunitaryInvoice.xml"),
	NAVARRA_2014(2014
			,Administration.NAVARRA
			,"/com/code/aon/file/tax/model/MOD340/xml/2014_Deponent.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2014_IssuedInvoice.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2014_ReceivedInvoice.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2014_InvestmentInvoice.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2014_IntracommunitaryInvoice.xml"),

	AEAT_2009(2009
			,Administration.COMMON_TERRITORY
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_Deponent.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_IssuedInvoice.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_ReceivedInvoice.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_InvestmentInvoice.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_IntracommunitaryInvoice.xml"),
	ALAVA_2009(2009
			,Administration.ALAVA
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_Deponent.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_IssuedInvoice.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_ReceivedInvoice.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_InvestmentInvoice.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_IntracommunitaryInvoice.xml"),
	BIZKAIA_2009(2009
			,Administration.BIZKAIA
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_Deponent.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_IssuedInvoice.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_ReceivedInvoice.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_InvestmentInvoice.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_IntracommunitaryInvoice.xml"),
	GIPUZKOA_2009(2009
			,Administration.GIPUZKOA
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_Deponent.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_IssuedInvoice.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_ReceivedInvoice.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_InvestmentInvoice.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_IntracommunitaryInvoice.xml"),
	NAVARRA_2009(2009
			,Administration.NAVARRA
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_Deponent.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_IssuedInvoice.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_ReceivedInvoice.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_InvestmentInvoice.xml"
			,"/com/code/aon/file/tax/model/MOD340/xml/2009_IntracommunitaryInvoice.xml");

	private Integer year;
	private Administration administration;
	private String deponentMetadataResource;
	private String issuedMetadataResource;
	private String receivedMetadataResource;
	private String investmentMetadataResource;
	private String intracommunitaryMetadataResource;


	private MOD340Format(Integer year,Administration administration,String deponentMetadataResource,
			String issuedMetadataResource,String receivedMetadataResource,
			String investmentMetadataResource,String intracommunitaryMetadataResource)	{
		this.year = year;
		this.administration = administration;
		this.deponentMetadataResource = deponentMetadataResource;
		this.issuedMetadataResource = issuedMetadataResource;
		this.receivedMetadataResource = receivedMetadataResource;
		this.investmentMetadataResource = investmentMetadataResource;
		this.intracommunitaryMetadataResource = intracommunitaryMetadataResource;
	}

	public Administration getAdministration() {
		return administration;
	}
	public Integer getYear() {
		return year;
	}
	public String getDeponentMetadataResource() {
		return deponentMetadataResource;
	}
	public String getIssuedMetadataResource() {
		return issuedMetadataResource;
	}
	public String getReceivedMetadataResource() {
		return receivedMetadataResource;
	}
	public String getInvestmentMetadataResource() {
		return investmentMetadataResource;
	}
	public String getIntracommunitaryMetadataResource() {
		return intracommunitaryMetadataResource;
	}
}
