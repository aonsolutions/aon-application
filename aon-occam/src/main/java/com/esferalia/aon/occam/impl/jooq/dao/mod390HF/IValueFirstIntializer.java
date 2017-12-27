package com.esferalia.aon.occam.impl.jooq.dao.mod390HF;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;

@FunctionalInterface
public interface IValueFirstIntializer {
	void initialize(AONContext ctx,Mod390HF mod);
}
