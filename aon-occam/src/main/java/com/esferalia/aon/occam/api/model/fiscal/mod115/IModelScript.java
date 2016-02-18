package com.esferalia.aon.occam.api.model.fiscal.mod115;

import com.esferalia.aon.occam.api.model.type.Mod115Key;
import com.esferalia.aon.occam.api.model.type.Mod115KeyInfo;

public interface IModelScript {
	String getLabel();
	Mod115Key[] getKeys();
	boolean hasGraphicParticularity();
	boolean isEnabled();
	Mod115KeyInfo getInfoKey();
	boolean paintHeaderBefore();
}
