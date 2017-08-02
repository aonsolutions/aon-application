package com.esferalia.aon.occam.impl.jooq.dao.mod303;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;

@FunctionalInterface
public interface IValueFirstIntializer {
	void initialize(AONContext ctx,Mod303 mod);
}
