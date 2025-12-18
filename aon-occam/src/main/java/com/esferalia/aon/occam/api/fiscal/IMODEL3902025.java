package com.esferalia.aon.occam.api.fiscal;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902025;

public interface IMODEL3902025 {

	public Mod3902025 get(AONContext ctx,Mod390 mod390);
	public Mod3902025 get(AONContext ctx,Integer id);
	public String getXML(AONContext aonContext, int id);
	public Mod3902025 save(AONContext ctx, Mod3902025 mod390);
	public void delete(AONContext ctx,Mod3902025 mod390);
	public Mod3902025 changeStatus(AONContext ctx, Mod3902025 mod, FiscalStatus newStatus);
	public Mod3902025 aeatPresentation(AONContext ctx, Mod3902025 mod, String aeatResponse);
	
}
