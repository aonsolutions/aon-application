package com.esferalia.aon.occam.impl.jooq.dao.mod303;

import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;

@FunctionalInterface
public interface IValueAccepter {
	boolean accept(Mod303 mod, VatContext vat);
}
