package com.code.aon.file.tax.model.MOD115;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.config.enumeration.Administration;

public enum MOD115Format {

	ALAVA_2011(2011
			,Administration.ALAVA
			,MimeType.MIME_XML
			,Alava2011MOD115Factory.class),
	NAVARRA_2014(2014
			,Administration.NAVARRA
			,MimeType.MIME_TXT
			,Navarra2014MOD115Factory.class),
	AEAT_2015(2015
			,Administration.COMMON_TERRITORY
			,MimeType.MIME_TXT
			,Aeat2015MOD115Factory.class),
	AEAT_2011(2011
			,Administration.COMMON_TERRITORY
			,MimeType.MIME_TXT
			,Aeat2011MOD115Factory.class);

	private Integer year;
	private MimeType mimeType;
	private Administration administration;
	private Class<? extends IMOD115Factory> factory;


	private MOD115Format(Integer year,Administration administration,MimeType mimeType,Class<? extends IMOD115Factory> factory)	{
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
	public Class<? extends IMOD115Factory> getFactory() {
		return factory;
	}
	
	public synchronized static MOD115Format getFormat(Administration administration, int year) {
		MOD115Format format = null;
		for (MOD115Format f : MOD115Format.values()) {
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
