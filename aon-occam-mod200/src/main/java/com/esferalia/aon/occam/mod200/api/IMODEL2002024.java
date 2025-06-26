package com.esferalia.aon.occam.mod200.api;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.mod200.api.model.mod200_2024.Mod2002024;

public interface IMODEL2002024 {
	
	public Mod2002024 getMod2002024ById(AONContext ctx, int id);	
	public Mod2002024 createMod2002024(AONContext ctx, int year);
	public Mod2002024 initializeMod2002024(AONContext ctx, Mod2002024 mod200);
	public Mod2002024 saveMod2002024(AONContext ctx, Mod2002024 mod200);
	public void deleteMod2002024(AONContext ctx, Mod2002024 mod200);
	public Mod2002024 calculateMod2002024(Mod2002024 mod200);
	public Mod2002024 aeatPresentation(AONContext ctx, Mod2002024 mod200, String aeatResponse);
	
}
