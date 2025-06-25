package com.esferalia.aon.occam.mod200.api;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.mod200.api.model.mod200_2024.Mod2002024;
import com.esferalia.aon.occam.mod200.impl.jooq.MODEL2002024Impl;

public class MODEL2002024 {
	
	private MODEL2002024() {

	}

	private static IMODEL2002024 getImpl() {
		return new MODEL2002024Impl();
	}

	public static Mod2002024 createMod2002024(Occam occam, int year) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().createMod2002024(ctx, year);
		} 
	}

	public static Mod2002024 initializeMod2002024(Occam occam, Mod2002024 mod200) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().initializeMod2002024(ctx, mod200);
		} 
	}

	public static Mod2002024 getMod2002024ById(Occam occam, int id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getMod2002024ById(ctx, id);
		} 
	}

	public static Mod2002024 calculateMod2002024(Mod2002024 mod200) {
		return getImpl().calculateMod2002024(mod200);
	}

	public static Mod2002024 saveMod2002024(Occam occam, Mod2002024 mod200) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().saveMod2002024(ctx, mod200);
		} 
	}

	public static void deleteMod2002024(Occam occam, Mod2002024 mod200) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			getImpl().deleteMod2002024(ctx, mod200);
		} 
	}
	
	public static Mod2002024 aeatPresentation(Occam occam, Mod2002024 mod200, String aeatResponse) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().aeatPresentation(ctx, mod200, aeatResponse);
		}
	}


}
