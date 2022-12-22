package com.esferalia.aon.occam.api.fiscal;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902015;

public interface IMODEL3902015 {

	public Mod3902015 get(AONContext ctx,Mod390 mod390);
	public Mod3902015 get(AONContext ctx,Integer id);
	public String getXML(AONContext aonContext, int id);
	public Mod3902015 save(AONContext ctx, Mod3902015 mod390);
	public void delete(AONContext ctx,Mod3902015 mod390);
	public Mod3902015 changeStatus(AONContext ctx, Mod3902015 mod184, FiscalStatus newStatus);
	
}
