package com.esferalia.aon.occam.impl.jooq.dao.mod390HF;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.Mod390Key;

public interface IMod390KeyDAO {
	
	Mod390Key getKey();
	boolean hasAccepter();
	boolean acceptValue(Mod390HF mod,VatContext vctx);
	void initialize(AONContext ctx,Mod390HF mod,VatContext vctx);
	void firstInitialize(AONContext ctx,Mod390HF mod);
	String getExpression();
	String getTemplate();

}
