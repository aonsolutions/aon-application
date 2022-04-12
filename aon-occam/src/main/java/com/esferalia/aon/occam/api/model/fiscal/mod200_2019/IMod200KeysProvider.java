package com.esferalia.aon.occam.api.model.fiscal.mod200_2019;

import com.esferalia.aon.occam.api.model.fiscal.mod200.IMod200Key;

public interface IMod200KeysProvider {

	IMod200Key[] getKeys();
	String getDescription();
	
}
