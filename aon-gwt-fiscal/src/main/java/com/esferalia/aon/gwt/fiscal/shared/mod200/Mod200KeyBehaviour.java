package com.esferalia.aon.gwt.fiscal.shared.mod200;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;


public enum Mod200KeyBehaviour implements Serializable, IsSerializable {
	NORMAL
	,TITLE_DISABLED
	,TITLE_ENABLED;

	public static boolean isTitle() {
		return false;
	}
	
}


