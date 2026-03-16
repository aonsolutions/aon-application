package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod421;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.Mod421;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.Mod421Key;

public interface IMod421KeyDAO {
	
	Mod421Key getKey();
	boolean hasAccepter();
	boolean acceptValue(Mod421 mod,VatContext vctx);
	void initialize(AONContext ctx,Mod421 mod,VatContext vctx);
	void firstInitialize(AONContext ctx,Mod421 mod);
	String getExpression();
	String getTemplate();
	
}
