package com.esferalia.aon.occam.api.model.fiscal;

import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.COMPUTE;
import static com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo.TITLE;

import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;

public interface IModelScript<T extends IFiscalModelKey> {
	static final int FIELD_LENGTH = 12; 
	static final int PERCENT_FIELD_LENGTH = 6;
	
	String getLabel();
	T[] getKeys();
	boolean hasGraphicParticularity();
	boolean isEnabled();
	FiscalModelKeyInfo[] getInfoKeys();
	boolean paintHeaderBefore();
	boolean isTitle();
	
	default int getFieldSize(T key) {
		return FIELD_LENGTH;
	}
	default boolean isEnabled(T key) {
		return (getInfoKeys()[0] != COMPUTE && getInfoKeys()[0] != TITLE);
	}
}
