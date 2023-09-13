package com.esferalia.aon.occam.api.fiscal;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.esferalia.aon.occam.impl.jooq.fiscal.MODEL184Impl;

public class MODEL184 {
	private MODEL184() {
		
	}
	
	private static IMODEL184 getImpl() {
		return new MODEL184Impl();
	}

	public static LinkedList<Mod184> getMod184s(Occam occam) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getMod184s(ctx, occam.getDomain());
		}
	}

	public static Mod184 get(Occam occam, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().get(ctx, id);
		}
	}

	public static Mod184 initialize(Occam occam, Integer year) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().initialize(ctx, year);
		}
	}

	public static Mod184 save(Occam occam, Mod184 mod184) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().save(ctx, mod184);
		}
	}

	public static void delete(Occam occam, Mod184 mod184) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			getImpl().delete(ctx, mod184);
		}
	}

	public static Mod184 saveComments(Occam occam, Mod184 mod184) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().saveComments(ctx, mod184);
		}
	}

	public static Mod184 changeStatus(Occam occam, Mod184 mod184, FiscalStatus newStatus) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().changeStatus(ctx, mod184, newStatus);
		}
	}
	
	public static Mod184 duplicate(Occam occam, Mod184 mod184) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().duplicate(ctx, mod184);
		}
	}
	
	public static Mod184 aeatPresentation(Occam occam, Mod184 mod, String aeatResponse) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().aeatPresentation(ctx, mod, aeatResponse);
		}
	}

}
