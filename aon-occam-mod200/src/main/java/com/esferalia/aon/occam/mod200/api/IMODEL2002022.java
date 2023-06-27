package com.esferalia.aon.occam.mod200.api;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022;

public interface IMODEL2002022 {
	
	public Mod2002022 getMod2002022ById(AONContext ctx, int id);	
	public Mod2002022 createMod2002022(AONContext ctx, int year);
	public Mod2002022 initializeMod2002022(AONContext ctx, Mod2002022 mod200);
	public Mod2002022 saveMod2002022(AONContext ctx, Mod2002022 mod200);
	public void deleteMod2002022(AONContext ctx, Mod2002022 mod200);
	public Mod2002022 calculateMod2002022(Mod2002022 mod200);
	public Mod2002022 aeatPresentation(AONContext ctx, Mod2002022 mod200, String aeatResponse);
	
}
