package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod131;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.type.Mod131Key;

public interface IMod131KeyDAO {
	
	Mod131Key getKey();
	boolean acceptIrpfBreakdown(Mod131 mod,IrpfBreakdown  br);
	void initialize(AONContext ctx,Mod131 mod);
	String getExpression();
	String info(AONContext ctx,Mod131 mod);
	
	void fillActivityFromMap(Mod131 mod,Mod131Key key);
	void fillMapFromActivity(Mod131 mod,Mod131Key key);
}

