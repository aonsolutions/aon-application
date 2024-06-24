package com.esferalia.aon.occam.mod200.api;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023;

public interface IMODEL2002023 {
	
	public Mod2002023 getMod2002023ById(AONContext ctx, int id);	
	public Mod2002023 createMod2002023(AONContext ctx, int year);
	public Mod2002023 initializeMod2002023(AONContext ctx, Mod2002023 mod200);
	public Mod2002023 saveMod2002023(AONContext ctx, Mod2002023 mod200);
	public void deleteMod2002023(AONContext ctx, Mod2002023 mod200);
	public Mod2002023 calculateMod2002023(Mod2002023 mod200);
	public Mod2002023 aeatPresentation(AONContext ctx, Mod2002023 mod200, String aeatResponse);
	
}
