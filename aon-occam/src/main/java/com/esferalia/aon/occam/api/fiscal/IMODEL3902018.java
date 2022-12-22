package com.esferalia.aon.occam.api.fiscal;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902018;

public interface IMODEL3902018 {

	public Mod3902018 get(AONContext ctx,Mod390 mod390);
	public Mod3902018 get(AONContext ctx,Integer id);
	public String getXML(AONContext aonContext, int id);
	public Mod3902018 save(AONContext ctx, Mod3902018 mod390);
	public void delete(AONContext ctx,Mod3902018 mod390);
	public Mod3902018 changeStatus(AONContext ctx, Mod3902018 mod184, FiscalStatus newStatus);
	
}
