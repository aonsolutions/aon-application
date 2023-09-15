package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

public interface IFiscalModelKey extends Serializable {
	
	String getValue();
	int getBox();
	String getBoxFormatted();
	
}
