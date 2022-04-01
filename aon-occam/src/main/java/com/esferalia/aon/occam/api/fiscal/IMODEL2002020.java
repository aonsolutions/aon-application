package com.esferalia.aon.occam.api.fiscal;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020;

public interface IMODEL2002020 {

	public Mod2002020 createMod2002020(AONContext ctx, int year);
	public Mod2002020 initializeNewMod2002020(AONContext ctx, Mod2002020 mod200);
	public Mod2002020 initializeMod2002020(AONContext ctx, Mod2002020 mod200);
	public Mod2002020 getMod2002020ByYear(AONContext ctx, int year);
	public Mod2002020 getMod2002020ById(AONContext ctx, int id);
	public Mod2002020 calculateMod2002020(Mod2002020 mod200);
//	public Mod2002020 validateMod2002020(Mod2002020 mod200);
	public Mod2002020 saveMod2002020(AONContext ctx, Mod2002020 mod200);
	public void deleteMod2002020(AONContext ctx, int id);
	public String dumpAEATMod2002020(Mod2002020 mod200);
	public Mod2002020 importMod2002019(AONContext ctx, Mod2002020 mod200);
	
}
