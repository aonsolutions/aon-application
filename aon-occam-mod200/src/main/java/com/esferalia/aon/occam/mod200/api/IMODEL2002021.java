package com.esferalia.aon.occam.mod200.api;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021;

public interface IMODEL2002021 {
	
	public Mod2002021 getMod2002021ById(AONContext ctx, int id);	
	public Mod2002021 createMod2002021(AONContext ctx, int year);
	public Mod2002021 initializeMod2002021(AONContext ctx, Mod2002021 mod200);
	public Mod2002021 saveMod2002021(AONContext ctx, Mod2002021 mod200);
	public void deleteMod2002021(AONContext ctx, int id);
	public Mod2002021 calculateMod2002021(Mod2002021 mod200);
	
}
