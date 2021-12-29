package com.esferalia.aon.occam.api.fiscal;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902021;

public interface IMODEL3902021 {

	public Mod3902021 get(AONContext ctx,Mod390 mod390);
	public Mod3902021 get(AONContext ctx,Integer id);
	public String getXML(AONContext aonContext, int id);
	public Mod3902021 save(AONContext ctx, Mod3902021 mod390);
	public void delete(AONContext ctx,Mod3902021 mod390);
	public Mod3902021 changeStatus(AONContext ctx, Mod3902021 mod184, FiscalStatus newStatus);
	
}
