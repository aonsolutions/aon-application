package com.esferalia.aon.occam.api.fiscal;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
import com.esferalia.aon.occam.impl.jooq.fiscal.MODEL193Impl;

public class MODEL193 {
	private MODEL193() {
		
	}
	
	private static IMODEL193 getImpl() {
		return new MODEL193Impl();
	}

	public static LinkedList<Mod193> getMod193s(Occam occam) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getMod193s(ctx, occam.getDomain());
		}
	}

	public static Mod193 get(Occam occam, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().get(ctx, id);
		}
	}

	public static Mod193 initialize(Occam occam, Integer year) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().initialize(ctx, year);
		}
	}

	public static Mod193 save(Occam occam, Mod193 mod193) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().save(ctx, mod193);
		}
	}

	public static void delete(Occam occam, Mod193 mod193) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			getImpl().delete(ctx, mod193);
		}
	}

	public static Mod193 saveComments(Occam occam, Mod193 mod193) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().saveComments(ctx, mod193);
		}
	}

	public static Mod193 changeStatus(Occam occam, Mod193 mod193, FiscalStatus newStatus) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().changeStatus(ctx, mod193, newStatus);
		}
	}
	
	public static Mod193 duplicate(Occam occam, Mod193 mod193) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().duplicate(ctx, mod193);
		}
	}
	
	public static Mod193 aeatPresentation(Occam occam, Mod193 mod, String aeatResponse) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().aeatPresentation(ctx, mod, aeatResponse);
		}
	}

}
