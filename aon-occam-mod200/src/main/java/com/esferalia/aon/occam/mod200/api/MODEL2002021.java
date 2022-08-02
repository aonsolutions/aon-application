package com.esferalia.aon.occam.mod200.api;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021;
import com.esferalia.aon.occam.mod200.impl.jooq.MODEL2002021Impl;

public class MODEL2002021 {
	
	private MODEL2002021() {

	}

	private static IMODEL2002021 getImpl() {
		return new MODEL2002021Impl();
	}

	public static Mod2002021 createMod2002021(Occam occam, int year) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().createMod2002021(ctx, year);
		} 
	}

	public static Mod2002021 initializeMod2002021(Occam occam, Mod2002021 mod200) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().initializeMod2002021(ctx, mod200);
		} 
	}

	public static Mod2002021 getMod2002021ById(Occam occam, int id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getMod2002021ById(ctx, id);
		} 
	}

	public static Mod2002021 calculateMod2002021(Mod2002021 mod200) {
		return getImpl().calculateMod2002021(mod200);
	}

	public static Mod2002021 saveMod2002021(Occam occam, Mod2002021 mod200) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().saveMod2002021(ctx, mod200);
		} 
	}

	public static void deleteMod2002021(Occam occam, Mod2002021 mod200) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			getImpl().deleteMod2002021(ctx, mod200);
		} 
	}

}
