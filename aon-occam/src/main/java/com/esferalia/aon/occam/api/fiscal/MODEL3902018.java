package com.esferalia.aon.occam.api.fiscal;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902018;
import com.esferalia.aon.occam.impl.jooq.fiscal.MODEL3902018Impl;

public class MODEL3902018 {

	private static IMODEL3902018 getImpl() {
		return new MODEL3902018Impl();
	}

	public static Mod3902018 get(Occam occam, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().get(ctx, id);
		}
	}

	public static Mod3902018 get(Occam occam, Mod390 mod390) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().get(ctx, mod390);
		}
	}

	public static String getXML(Occam occam, int id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getXML(ctx, id);
		}
	}

	public static Mod3902018 save(Occam occam, Mod3902018 mod390) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().save(ctx, mod390);
		}
	}

	public static void delete(Occam occam, Mod3902018 mod390) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			getImpl().delete(ctx, mod390);
		}
	}
	
	public static Mod3902018 changeStatus(Occam occam, Mod3902018 mod390, FiscalStatus status) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().changeStatus(ctx, mod390,  status);
		}
	}
	
}
