package com.code.aon.file.tax.model.MOD349;

import com.code.aon.config.enumeration.Administration;

public enum MOD349Format {

	AEAT_2011(2011,Administration.COMMON_TERRITORY
			,"/com/code/aon/file/tax/model/MOD349/xml/2011_AEAT_Deponent.xml"
			,"/com/code/aon/file/tax/model/MOD349/xml/2011_AEAT_Operator.xml"
			,"/com/code/aon/file/tax/model/MOD349/xml/2011_AEAT_Rectification.xml");

	private Integer year;
	private Administration administration;
	private String deponentMetadataResource;
	private String operatorMetadataResource;
	private String rectificationMetadataResource;


	private MOD349Format(Integer year,Administration administration,
			String deponentMetadataResource,
			String operatorMetadataResource,
			String rectificationMetadataResource)	{
		setYear(year);
		setAdministration(administration);
		setDeponentMetadataResource(deponentMetadataResource);
		setOperatorMetadataResource(operatorMetadataResource);
		setRectificationMetadataResource(rectificationMetadataResource);
	}

	public String getDescription() {
		// TODO 
		return administration.name();
	}
	
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	
	public Administration getAdministration() {
		return administration;
	}
	public void setAdministration(Administration administration) {
		this.administration = administration;
	}

	public String getDeponentMetadataResource() {
		return deponentMetadataResource;
	}
	public void setDeponentMetadataResource(String deponentMetadataResource) {
		this.deponentMetadataResource = deponentMetadataResource;
	}

	public String getOperatorMetadataResource() {
		return operatorMetadataResource;
	}
	public void setOperatorMetadataResource(String operatorMetadataResource) {
		this.operatorMetadataResource = operatorMetadataResource;
	}

	public String getRectificationMetadataResource() {
		return rectificationMetadataResource;
	}

	public void setRectificationMetadataResource(
			String rectificationMetadataResource) {
		this.rectificationMetadataResource = rectificationMetadataResource;
	}
	
}
