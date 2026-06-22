package com.esferalia.aon.occam.mod200.api;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025;
import com.esferalia.aon.occam.mod200.impl.jooq.MODEL2002025Impl;

public class MODEL2002025 {
	
	private MODEL2002025() {

	}

	private static IMODEL2002025 getImpl() {
		return new MODEL2002025Impl();
	}

	public static Mod2002025 createMod2002025(Occam occam, int year) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().createMod2002025(ctx, year);
		} 
	}

	public static Mod2002025 initializeMod2002025(Occam occam, Mod2002025 mod200) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().initializeMod2002025(ctx, mod200);
		} 
	}

	public static Mod2002025 getMod2002025ById(Occam occam, int id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getMod2002025ById(ctx, id);
		} 
	}

	public static Mod2002025 calculateMod2002025(Mod2002025 mod200) {
		return getImpl().calculateMod2002025(mod200);
	}

	public static Mod2002025 saveMod2002025(Occam occam, Mod2002025 mod200) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().saveMod2002025(ctx, mod200);
		} 
	}

	public static void deleteMod2002025(Occam occam, Mod2002025 mod200) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			getImpl().deleteMod2002025(ctx, mod200);
		} 
	}
	
	public static Mod2002025 aeatPresentation(Occam occam, Mod2002025 mod200, String aeatResponse) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().aeatPresentation(ctx, mod200, aeatResponse);
		}
	}


}
