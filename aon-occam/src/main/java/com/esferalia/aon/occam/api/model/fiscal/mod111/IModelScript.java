package com.esferalia.aon.occam.api.model.fiscal.mod111;

import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.occam.api.model.type.Mod111KeyInfo;

public interface IModelScript {
	String getLabel();
	Mod111Key[] getKeys();
	boolean hasGraphicParticularity();
	boolean isEnabled();
	Mod111KeyInfo getInfoKey();
	boolean paintHeaderBefore();
}
