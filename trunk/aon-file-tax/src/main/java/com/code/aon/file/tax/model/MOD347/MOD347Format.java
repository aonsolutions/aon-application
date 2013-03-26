package com.code.aon.file.tax.model.MOD347;

import com.code.aon.config.enumeration.Administration;

public enum MOD347Format {

	ALAVA_2010(2010,Administration.ALAVA
			,"/com/code/aon/file/tax/model/MOD347/xml/2010_ALAVA_Deponent.xml"
			,"/com/code/aon/file/tax/model/MOD347/xml/2010_ALAVA_Declared.xml"),
	ALAVA_2011(2011,Administration.ALAVA
			,"/com/code/aon/file/tax/model/MOD347/xml/2011_ALAVA_Deponent.xml"
			,"/com/code/aon/file/tax/model/MOD347/xml/2011_ALAVA_Declared.xml"),
	BIZKAIA_2010(2010,Administration.BIZKAIA
			,"/com/code/aon/file/tax/model/MOD347/xml/2010_BIZKAIA_Deponent.xml"
			,"/com/code/aon/file/tax/model/MOD347/xml/2010_BIZKAIA_Declared.xml"),
	BIZKAIA_2011(2011,Administration.BIZKAIA
			,"/com/code/aon/file/tax/model/MOD347/xml/2011_BIZKAIA_Deponent.xml"
			,"/com/code/aon/file/tax/model/MOD347/xml/2011_BIZKAIA_Declared.xml"),
	GIPUZKOA_2010(2010,Administration.GIPUZKOA
			,"/com/code/aon/file/tax/model/MOD347/xml/2010_GIPUZKOA_Deponent.xml"
			,"/com/code/aon/file/tax/model/MOD347/xml/2010_GIPUZKOA_Declared.xml"),
	GIPUZKOA_2011(2011,Administration.GIPUZKOA
			,"/com/code/aon/file/tax/model/MOD347/xml/2011_GIPUZKOA_Deponent.xml"
			,"/com/code/aon/file/tax/model/MOD347/xml/2011_GIPUZKOA_Declared.xml"),
	NAVARRA_2010(2010,Administration.NAVARRA
			,"/com/code/aon/file/tax/model/MOD347/xml/2010_NAVARRA_Deponent.xml"
			,"/com/code/aon/file/tax/model/MOD347/xml/2010_NAVARRA_Declared.xml"),
	NAVARRA_2011(2011,Administration.NAVARRA
			,"/com/code/aon/file/tax/model/MOD347/xml/2011_NAVARRA_Deponent.xml"
			,"/com/code/aon/file/tax/model/MOD347/xml/2011_NAVARRA_Declared.xml"),
	AEAT_2010(2010,Administration.COMMON_TERRITORY
			,"/com/code/aon/file/tax/model/MOD347/xml/2010_AEAT_Deponent.xml"
			,"/com/code/aon/file/tax/model/MOD347/xml/2010_AEAT_Declared.xml"),
	AEAT_2011(2011,Administration.COMMON_TERRITORY
			,"/com/code/aon/file/tax/model/MOD347/xml/2011_AEAT_Deponent.xml"
			,"/com/code/aon/file/tax/model/MOD347/xml/2011_AEAT_Declared.xml");

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
