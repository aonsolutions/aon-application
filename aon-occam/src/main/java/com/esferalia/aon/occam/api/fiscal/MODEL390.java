package com.esferalia.aon.occam.api.fiscal;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.impl.jooq.fiscal.MODEL390Impl;

public class MODEL390 {

	private static IMODEL390 getImpl() {
		return new MODEL390Impl();
	}

	public static Mod390 getMod390(Occam occam, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getMod390(ctx, id);
		}
	}
	public static LinkedList<Mod390> getMod390s(Occam occam) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getMod390s(ctx, occam.getDomain());
		}
	}
	public static Mod390 initialize(Occam occam, int year) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().initialize(ctx, year);
		}
	}

//	public static Mod390 create(Occam occam, Mod390 mod390) {
//		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
//			return getImpl().create(ctx, mod390);
//		}
//	}
	
	public static Mod390 saveComments(Occam occam, Mod390 mod390) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().saveComments(ctx, mod390);
		}
	}
	
	public static void deleteMod390(Occam occam, Mod390 mod390) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			getImpl().deleteMod390(ctx, mod390);
		}
	}

}
