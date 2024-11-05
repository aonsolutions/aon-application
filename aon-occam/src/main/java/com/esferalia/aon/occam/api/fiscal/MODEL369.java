package com.esferalia.aon.occam.api.fiscal;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod369;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.impl.jooq.fiscal.MODEL369Impl;

public class MODEL369 {
	private MODEL369() {
		
	}
	
	private static IMODEL369 getImpl() {
		return new MODEL369Impl();
	}

	public static LinkedList<Mod369> getMod369s(Occam occam) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getMod369s(ctx, occam.getDomain());
		}
	}

	public static Mod369 get(Occam occam, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().get(ctx, id);
		}
	}

	public static Mod369 initialize(Occam occam, Integer year, Period period ) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().initialize(ctx, year, period);
		}
	}

	public static Mod369 save(Occam occam, Mod369 mod369) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().save(ctx, mod369);
		}
	}

	public static void delete(Occam occam, Mod369 mod369) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			getImpl().delete(ctx, mod369);
		}
	}

	public static Mod369 saveComments(Occam occam, Mod369 mod369) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().saveComments(ctx, mod369);
		}
	}

	public static Mod369 changeStatus(Occam occam, Mod369 mod369, FiscalStatus newStatus) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().changeStatus(ctx, mod369, newStatus);
		}
	}
	
	public static Mod369 duplicate(Occam occam, Mod369 mod369) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().duplicate(ctx, mod369);
		}
	}

}
