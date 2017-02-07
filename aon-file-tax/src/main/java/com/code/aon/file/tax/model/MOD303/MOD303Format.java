package com.code.aon.file.tax.model.MOD303;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.config.enumeration.Administration;

public enum MOD303Format {

	ALAVA_2010(2011
			,Administration.ALAVA
			,MimeType.MIME_XML
			,Alava2010MOD303Factory.class)
	,BIZKAIA_2015(2015
			,Administration.BIZKAIA
			,MimeType.MIME_TXT
			,Bizkaia2015MOD303Factory.class)
	,BIZKAIA_2012(2011
			,Administration.BIZKAIA
			,MimeType.MIME_TXT
			,Bizkaia2012MOD303Factory.class)
	,GIPUZKOA_2010(2010
			,Administration.GIPUZKOA
			,MimeType.MIME_TXT
			,Gipuzkoa2010MOD303Factory.class)
	,AEAT_2017(2017
			,Administration.COMMON_TERRITORY
			,MimeType.MIME_TXT
			,Aeat2017MOD303Factory.class)
	,AEAT_2015(2015
			,Administration.COMMON_TERRITORY
			,MimeType.MIME_TXT
			,Aeat2015MOD303Factory.class)
	,AEAT_2014(2014
			,Administration.COMMON_TERRITORY
			,MimeType.MIME_TXT
			,Aeat2014MOD303Factory.class)
	;

	private Integer year;
	private Administration administration;
	private Class<? extends IMOD303Factory> factory;
	private MimeType mimeType;


	private MOD303Format(Integer year,Administration administration,MimeType mimeType,Class<? extends IMOD303Factory> factory)	{
		this.year = year;
		this.administration = administration;
		this.factory = factory;
		this.mimeType = mimeType;
	}

	public Administration getAdministration() {
		return administration;
	}
	public void setAdministration(Administration administration) {
		this.administration = administration;
	}
	
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	
	public Class<? extends IMOD303Factory> getFactory() {
		return factory;
	}
	public void setFactory(Class<? extends IMOD303Factory> factory) {
		this.factory = factory;
	}

	public MimeType getMimeType() {
		return mimeType;
	}
	
	public String getFileName(String year, String period ) {
		String prefix = "MOD303"; 
		if (getAdministration() == Administration.GIPUZKOA) {
			prefix = "MOD" + (period.contains("T")?"320":"300");	
		}
		return prefix + year + period +"."+getMimeType().getExtension();
	}
	
	public synchronized static MOD303Format getFormat(Administration administration, int year) {
		MOD303Format format = null;
		for (MOD303Format f : MOD303Format.values()) {
			if (f.getAdministration() == administration && year >= f.getYear()) {
				format = f;
				break;
			}
		}
		if (format == null) {
			String msg = "La generación de archivos para la administracion "
					+ administration + " no está aún implementada.";
			throw new IllegalArgumentException(msg);
		}
		return format;
	}
	
}
