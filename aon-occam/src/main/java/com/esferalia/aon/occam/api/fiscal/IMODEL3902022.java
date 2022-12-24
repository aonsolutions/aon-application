package com.esferalia.aon.occam.api.fiscal;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902022;

public interface IMODEL3902022 {

	public Mod3902022 get(AONContext ctx,Mod390 mod390);
	public Mod3902022 get(AONContext ctx,Integer id);
	public String getXML(AONContext aonContext, int id);
	public Mod3902022 save(AONContext ctx, Mod3902022 mod390);
	public void delete(AONContext ctx,Mod3902022 mod390);
	public Mod3902022 changeStatus(AONContext ctx, Mod3902022 mod, FiscalStatus newStatus);
	public Mod3902022 aeatPresentation(AONContext ctx, Mod3902022 mod, String aeatResponse);
	
}
