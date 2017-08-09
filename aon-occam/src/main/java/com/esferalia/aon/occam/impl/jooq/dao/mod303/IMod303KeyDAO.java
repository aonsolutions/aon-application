package com.esferalia.aon.occam.impl.jooq.dao.mod303;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.Mod303Key;

public interface IMod303KeyDAO {
	
	Mod303Key getKey();
	boolean hasAccepter();
	boolean acceptValue(Mod303 mod,VatContext vctx);
	void initialize(AONContext ctx,Mod303 mod,VatContext vctx);
	void firstInitialize(AONContext ctx,Mod303 mod);
	String getExpression();
	
}
