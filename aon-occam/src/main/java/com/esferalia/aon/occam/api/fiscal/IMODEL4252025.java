package com.esferalia.aon.occam.api.fiscal;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.mod425.Mod4252025;

public interface IMODEL4252025 {

	public Mod4252025 get(AONContext ctx, Mod390 mod425);
	public Mod4252025 get(AONContext ctx, Integer id);
//	public String getXML(AONContext aonContext, int id);
	public Mod4252025 save(AONContext ctx, Mod4252025 mod425);
	public void delete(AONContext ctx, Mod4252025 mod425);
	public Mod4252025 changeStatus(AONContext ctx, Mod4252025 mod, FiscalStatus newStatus);
//	public Mod4252025 aeatPresentation(AONContext ctx, Mod4252025 mod, String aeatResponse);
	
}