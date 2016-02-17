package com.code.aon.file.tax.model.MOD111;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.config.enumeration.Administration;

public enum MOD111Format {

	ALAVA_2011(2011
			,Administration.ALAVA
			,MimeType.MIME_XML
			,Alava2011MOD111Factory.class),
	NAVARRA_2011(2011
			,Administration.NAVARRA
			,MimeType.MIME_TXT
			,Navarra2011MOD111Factory.class),
	AEAT_2016(2016
			,Administration.COMMON_TERRITORY
			,MimeType.MIME_TXT
			,Aeat2016MOD111Factory.class),
	AEAT_2011(2011
			,Administration.COMMON_TERRITORY
			,MimeType.MIME_TXT
			,Aeat2011MOD111Factory.class),
	;

	private Integer year;
	private MimeType mimeType;
	private Administration administration;
	private Class<? extends IMOD111Factory> factory;


	private MOD111Format(Integer year,Administration administration,MimeType mimeType,Class<? extends IMOD111Factory> factory)	{
		this.year = year;
		this.mimeType = mimeType;		
		this.administration = administration;
		this.factory = factory;
	}

	public Administration getAdministration() {
		return administration;
	}
	public Integer getYear() {
		return year;
	}
	public MimeType getMimeType() {
		return mimeType;
	}
	public Class<? extends IMOD111Factory> getFactory() {
		return factory;
	}
	
	
	public synchronized static MOD111Format getFormat(Administration administration, int year) {
		MOD111Format format = null;
		for (MOD111Format f : MOD111Format.values()) {
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
