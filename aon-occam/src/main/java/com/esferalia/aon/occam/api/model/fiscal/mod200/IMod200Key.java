package com.esferalia.aon.occam.api.model.fiscal.mod200;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.Administration;

public interface IMod200Key extends Serializable {

	String getDescription();

	String getCode(Administration administration);
}
