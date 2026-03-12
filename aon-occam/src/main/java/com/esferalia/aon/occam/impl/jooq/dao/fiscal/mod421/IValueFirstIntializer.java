package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod421;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.Mod421;

@FunctionalInterface
public interface IValueFirstIntializer {
	void initialize(AONContext ctx,Mod421 mod);
}
