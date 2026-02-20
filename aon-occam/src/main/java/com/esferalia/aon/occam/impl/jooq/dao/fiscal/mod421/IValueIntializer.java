package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod421;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.Mod421;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;

@FunctionalInterface
public interface IValueIntializer {
	void initialize(AONContext ctx,Mod421 mod, VatContext vat);
}
	