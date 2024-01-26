package com.esferalia.aon.occam.mod200.api;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023;
import com.esferalia.aon.occam.mod200.impl.jooq.MODEL2002023Impl;

public class MODEL2002023 {
	
	private MODEL2002023() {

	}

	private static IMODEL2002023 getImpl() {
		return new MODEL2002023Impl();
	}

	public static Mod2002023 createMod2002023(Occam occam, int year) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().createMod2002023(ctx, year);
		} 
	}

	public static Mod2002023 initializeMod2002023(Occam occam, Mod2002023 mod200) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().initializeMod2002023(ctx, mod200);
		} 
	}

	public static Mod2002023 getMod2002023ById(Occam occam, int id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getMod2002023ById(ctx, id);
		} 
	}

	public static Mod2002023 calculateMod2002023(Mod2002023 mod200) {
		return getImpl().calculateMod2002023(mod200);
	}

	public static Mod2002023 saveMod2002023(Occam occam, Mod2002023 mod200) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().saveMod2002023(ctx, mod200);
		} 
	}

	public static void deleteMod2002023(Occam occam, Mod2002023 mod200) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			getImpl().deleteMod2002023(ctx, mod200);
		} 
	}
	
	public static Mod2002023 aeatPresentation(Occam occam, Mod2002023 mod200, String aeatResponse) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().aeatPresentation(ctx, mod200, aeatResponse);
		}
	}


}
