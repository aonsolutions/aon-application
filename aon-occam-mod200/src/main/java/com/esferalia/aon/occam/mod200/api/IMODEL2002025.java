package com.esferalia.aon.occam.mod200.api;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025;

public interface IMODEL2002025 {
	
	public Mod2002025 getMod2002025ById(AONContext ctx, int id);	
	public Mod2002025 createMod2002025(AONContext ctx, int year);
	public Mod2002025 initializeMod2002025(AONContext ctx, Mod2002025 mod200);
	public Mod2002025 saveMod2002025(AONContext ctx, Mod2002025 mod200);
	public void deleteMod2002025(AONContext ctx, Mod2002025 mod200);
	public Mod2002025 calculateMod2002025(Mod2002025 mod200);
	public Mod2002025 aeatPresentation(AONContext ctx, Mod2002025 mod200, String aeatResponse);
	
}
