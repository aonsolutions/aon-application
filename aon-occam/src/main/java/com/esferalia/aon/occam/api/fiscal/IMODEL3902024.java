package com.esferalia.aon.occam.api.fiscal;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902024;

public interface IMODEL3902024 {

	public Mod3902024 get(AONContext ctx,Mod390 mod390);
	public Mod3902024 get(AONContext ctx,Integer id);
	public String getXML(AONContext aonContext, int id);
	public Mod3902024 save(AONContext ctx, Mod3902024 mod390);
	public void delete(AONContext ctx,Mod3902024 mod390);
	public Mod3902024 changeStatus(AONContext ctx, Mod3902024 mod, FiscalStatus newStatus);
	public Mod3902024 aeatPresentation(AONContext ctx, Mod3902024 mod, String aeatResponse);
	
}
