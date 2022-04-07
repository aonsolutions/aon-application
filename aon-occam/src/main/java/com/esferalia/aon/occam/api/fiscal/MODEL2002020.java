package com.esferalia.aon.occam.api.fiscal;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020;
import com.esferalia.aon.occam.impl.jooq.fiscal.MODEL2002020Impl;

public class MODEL2002020 {
	
	private MODEL2002020() {

	}

	private static IMODEL2002020 getImpl() {
		return new MODEL2002020Impl();
	}

	public static Mod2002020 createMod2002020(Occam occam, int year) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().createMod2002020(ctx, year);
		} 
	}

	public static Mod2002020 initializeNewMod2002020(Occam occam, Mod2002020 mod200) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().initializeNewMod2002020(ctx, mod200);
		} 
	}

	public static Mod2002020 initializeMod2002020(Occam occam, Mod2002020 mod200) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().initializeMod2002020(ctx, mod200);
		} 
	}

	public static Mod2002020 getMod2002020ByYear(Occam occam, int year) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {		
			return getImpl().getMod2002020ByYear(ctx, year);
		} 
	}

	public static Mod2002020 getMod2002020ById(Occam occam, int id) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getMod2002020ById(ctx, id);
		} 
	}

	public static Mod2002020 calculateMod2002020(Mod2002020 mod200) {
		return getImpl().calculateMod2002020(mod200);
	}

	public static Mod2002020 saveMod2002020(Occam occam, Mod2002020 mod200) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().saveMod2002020(ctx, mod200);
		} 
	}

	public static void deleteMod2002020(Occam occam, int id) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			getImpl().deleteMod2002020(ctx, id);
		} 
	}

	public static String dumpAEATMod2002020(Mod2002020 mod200) {
		return getImpl().dumpAEATMod2002020(mod200);
	}

	public static Mod2002020 importMod2002019(Occam occam, Mod2002020 mod200) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().importMod2002019(ctx, mod200);
		} 
	}

}
