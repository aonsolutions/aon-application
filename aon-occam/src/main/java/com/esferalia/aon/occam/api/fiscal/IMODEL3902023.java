package com.esferalia.aon.occam.api.fiscal;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902023;

public interface IMODEL3902023 {

	public Mod3902023 get(AONContext ctx,Mod390 mod390);
	public Mod3902023 get(AONContext ctx,Integer id);
	public String getXML(AONContext aonContext, int id);
	public Mod3902023 save(AONContext ctx, Mod3902023 mod390);
	public void delete(AONContext ctx,Mod3902023 mod390);
	public Mod3902023 changeStatus(AONContext ctx, Mod3902023 mod, FiscalStatus newStatus);
	public Mod3902023 aeatPresentation(AONContext ctx, Mod3902023 mod, String aeatResponse);
	
}
