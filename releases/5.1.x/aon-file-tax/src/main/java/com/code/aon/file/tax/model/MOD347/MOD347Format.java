package com.code.aon.file.tax.model.MOD347;

public enum MOD347Format {

	ALAVA_2009(2009
			,"Alava - 2009"
			,"/com/code/aon/file/tax/model/MOD347/xml/2009_ALAVA_Deponent.xml"
			,"/com/code/aon/file/tax/model/MOD347/xml/2009_ALAVA_Declared.xml"
			,"/com/code/aon/file/tax/model/MOD347/xml/2009_ALAVA_Building.xml"),
	BIZKAIA_2009(2009
			,"Bizkaia - 2009"
			,"/com/code/aon/file/tax/model/MOD347/xml/2009_ALAVA_Deponent.xml"
			,"/com/code/aon/file/tax/model/MOD347/xml/2009_ALAVA_Declared.xml"
			,"/com/code/aon/file/tax/model/MOD347/xml/2009_ALAVA_Building.xml"),
	GIPUZKOA_2009(2009
			,"Gipuzkoa - 2009"
			,"/com/code/aon/file/tax/model/MOD347/xml/2009_ALAVA_Deponent.xml"
			,"/com/code/aon/file/tax/model/MOD347/xml/2009_ALAVA_Declared.xml"
			,"/com/code/aon/file/tax/model/MOD347/xml/2009_ALAVA_Building.xml"),
	NAVARRA_2009(2009
			,"Navarra - 2009"
			,"/com/code/aon/file/tax/model/MOD347/xml/2009_ALAVA_Deponent.xml"
			,"/com/code/aon/file/tax/model/MOD347/xml/2009_ALAVA_Declared.xml"
			,"/com/code/aon/file/tax/model/MOD347/xml/2009_ALAVA_Building.xml"),
	AEAT_2009(2009
			,"AEAT - 2009"
			,"/com/code/aon/file/tax/model/MOD347/xml/2009_ALAVA_Deponent.xml"
			,"/com/code/aon/file/tax/model/MOD347/xml/2009_ALAVA_Declared.xml"
			,"/com/code/aon/file/tax/model/MOD347/xml/2009_ALAVA_Building.xml");

	private Integer year;
	private String description;
	private String deponentMetadataResource;
	private String declaredMetadataResource;
	private String buildingMetadataResource;


	private MOD347Format(Integer year,String description,String deponentMetadataResource,
			String declaredMetadataResource,String buildingMetadataResource)	{
		this.year = year;
		this.description = description;
		this.deponentMetadataResource = deponentMetadataResource;
		this.declaredMetadataResource = declaredMetadataResource;
		this.buildingMetadataResource = buildingMetadataResource;
	}


	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	
	public String getBuildingMetadataResource() {
		return buildingMetadataResource;
	}
	public void setBuildingMetadataResource(String buildingMetadataResource) {
		this.buildingMetadataResource = buildingMetadataResource;
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
