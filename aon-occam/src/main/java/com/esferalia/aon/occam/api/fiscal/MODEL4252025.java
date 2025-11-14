package com.esferalia.aon.occam.api.fiscal;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.mod425.Mod4252025;
import com.esferalia.aon.occam.impl.jooq.fiscal.MODEL4252025Impl;

public class MODEL4252025 {

	private static IMODEL4252025 getImpl() {
		return new MODEL4252025Impl();
	}

	public static Mod4252025 get(Occam occam, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().get(ctx, id);
		}
	}

	public static Mod4252025 get(Occam occam, Mod390 mod425) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().get(ctx, mod425);
		}
	}

//	public static String getXML(Occam occam, int id) {
//		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
//			return getImpl().getXML(ctx, id);
//		}
//	}

	public static Mod4252025 save(Occam occam, Mod4252025 mod425) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().save(ctx, mod425);
		}
	}

	public static void delete(Occam occam, Mod4252025 mod425) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			getImpl().delete(ctx, mod425);
		}
	}
	
	public static Mod4252025 changeStatus(Occam occam, Mod4252025 mod425, FiscalStatus status) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().changeStatus(ctx, mod425,  status);
		}
	}

//	public static Mod4252025 aeatPresentation(Occam occam, Mod4252025 mod, String aeatResponse) {
//		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
//			return getImpl().aeatPresentation(ctx, mod, aeatResponse);
//		}
//	}
	
}
