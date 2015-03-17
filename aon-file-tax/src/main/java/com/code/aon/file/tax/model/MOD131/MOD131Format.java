package com.code.aon.file.tax.model.MOD131;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.config.enumeration.Administration;

public enum MOD131Format {

	AEAT_2015(2015
			,Administration.COMMON_TERRITORY
			,MimeType.MIME_TXT
			,Aeat2015MOD131Factory.class),
	AEAT_2013(2013
			,Administration.COMMON_TERRITORY
			,MimeType.MIME_TXT
			,Aeat2013MOD131Factory.class);

	private Integer year;
	private MimeType mimeType;
	private Administration administration;
	private Class<? extends IMOD131Factory> factory;


	private MOD131Format(Integer year,Administration administration,MimeType mimeType,Class<? extends IMOD131Factory> factory)	{
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
	public Class<? extends IMOD131Factory> getFactory() {
		return factory;
	}
	
	
	public synchronized static MOD131Format getFormat(Administration administration, int year) {
		MOD131Format format = null;
		for (MOD131Format f : MOD131Format.values()) {
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
