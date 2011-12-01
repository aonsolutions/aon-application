package com.code.aon.file.tax.model.MOD347;

import com.code.aon.config.enumeration.Administration;

public enum MOD347Format {

	ALAVA(2010,Administration.ALAVA
			,"/com/code/aon/file/tax/model/MOD347/xml/2010_ALAVA_Deponent.xml"
			,"/com/code/aon/file/tax/model/MOD347/xml/2010_ALAVA_Declared.xml"),
	BIZKAIA(2010,Administration.BIZKAIA
			,"/com/code/aon/file/tax/model/MOD347/xml/2010_BIZKAIA_Deponent.xml"
			,"/com/code/aon/file/tax/model/MOD347/xml/2010_BIZKAIA_Declared.xml"),
	GIPUZKOA(2010,Administration.GIPUZKOA
			,"/com/code/aon/file/tax/model/MOD347/xml/2010_GIPUZKOA_Deponent.xml"
			,"/com/code/aon/file/tax/model/MOD347/xml/2010_GIPUZKOA_Declared.xml"),
	NAVARRA(2010,Administration.NAVARRA
			,"/com/code/aon/file/tax/model/MOD347/xml/2010_NAVARRA_Deponent.xml"
			,"/com/code/aon/file/tax/model/MOD347/xml/2010_NAVARRA_Declared.xml"),
	AEAT(2010,Administration.COMMON_TERRITORY
			,"/com/code/aon/file/tax/model/MOD347/xml/2010_AEAT_Deponent.xml"
			,"/com/code/aon/file/tax/model/MOD347/xml/2010_AEAT_Declared.xml");

	private Integer year;
	private Administration administration;
	private String deponentMetadataResource;
	private String declaredMetadataResource;


	private MOD347Format(Integer year,Administration administration,String deponentMetadataResource,
			String declaredMetadataResource)	{
		setYear(year);
		setAdministration(administration);
		setDeponentMetadataResource(deponentMetadataResource);
		setDeclaredMetadataResource(declaredMetadataResource);
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

	public String getDeclaredMetadataResource() {
		return declaredMetadataResource;
	}
	public void setDeclaredMetadataResource(String declaredMetadataResource) {
		this.declaredMetadataResource = declaredMetadataResource;
	}
	
}
