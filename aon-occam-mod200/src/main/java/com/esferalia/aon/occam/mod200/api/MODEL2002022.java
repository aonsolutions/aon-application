package com.esferalia.aon.occam.mod200.api;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022;
import com.esferalia.aon.occam.mod200.impl.jooq.MODEL2002022Impl;

public class MODEL2002022 {
	
	private MODEL2002022() {

	}

	private static IMODEL2002022 getImpl() {
		return new MODEL2002022Impl();
	}

	public static Mod2002022 createMod2002022(Occam occam, int year) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().createMod2002022(ctx, year);
		} 
	}

	public static Mod2002022 initializeMod2002022(Occam occam, Mod2002022 mod200) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().initializeMod2002022(ctx, mod200);
		} 
	}

	public static Mod2002022 getMod2002022ById(Occam occam, int id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getMod2002022ById(ctx, id);
		} 
	}

	public static Mod2002022 calculateMod2002022(Mod2002022 mod200) {
		return getImpl().calculateMod2002022(mod200);
	}

	public static Mod2002022 saveMod2002022(Occam occam, Mod2002022 mod200) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().saveMod2002022(ctx, mod200);
		} 
	}

	public static void deleteMod2002022(Occam occam, Mod2002022 mod200) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			getImpl().deleteMod2002022(ctx, mod200);
		} 
	}
	
	public static Mod2002022 aeatPresentation(Occam occam, Mod2002022 mod200, String aeatResponse) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().aeatPresentation(ctx, mod200, aeatResponse);
		}
	}


}
