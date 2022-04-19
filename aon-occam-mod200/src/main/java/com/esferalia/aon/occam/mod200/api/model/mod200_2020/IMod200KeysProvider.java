package com.esferalia.aon.occam.mod200.api.model.mod200_2020;

import com.esferalia.aon.occam.mod200.api.model.IMod200Key;

public interface IMod200KeysProvider {

	IMod200Key[] getKeys();
	String getDescription();
	
}
