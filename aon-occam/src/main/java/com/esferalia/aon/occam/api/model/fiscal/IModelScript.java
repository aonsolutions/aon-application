package com.esferalia.aon.occam.api.model.fiscal;

import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;

public interface IModelScript<T extends IFiscalModelKey> {
	String getLabel();

	T[] getKeys();

	boolean hasGraphicParticularity();

	boolean isEnabled();

	FiscalModelKeyInfo[] getInfoKeys();

	boolean paintHeaderBefore();

	boolean isTitle();
}
