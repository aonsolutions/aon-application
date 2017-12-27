package com.esferalia.aon.occam.impl.jooq.dao.mod390HF;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;

@FunctionalInterface
public interface IValueIntializer {
	void initialize(AONContext ctx,Mod390HF mod, VatContext vat);
}
	