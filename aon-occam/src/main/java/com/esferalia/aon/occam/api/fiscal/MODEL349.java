package com.esferalia.aon.occam.api.fiscal;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod349;
import com.esferalia.aon.occam.api.model.fiscal.Mod349Detail;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.impl.jooq.fiscal.MODEL349Impl;

public class MODEL349 {
	private MODEL349() {
		
	}
	
	private static IMODEL349 getImpl() {
		return new MODEL349Impl();
	}

	public static LinkedList<Mod349> getMod349s(Occam occam) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getMod349s(ctx, occam.getDomain());
		}
	}

	public static Mod349 get(Occam occam, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().get(ctx, id);
		}
	}

	public static Mod349 initialize(Occam occam, int year, Period period) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().initialize(ctx, year, period);
		}
	}

	public static Mod349 save(Occam occam, Mod349 mod349) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().save(ctx, mod349);
		}
	}

	public static Mod349 reset(Occam occam, Mod349 mod349) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().reset(ctx, mod349);
		}
	}

	public static void delete(Occam occam, Mod349 mod349) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			getImpl().delete(ctx, mod349);
		}
	}

	public static Mod349Detail getDetail(Occam occam, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getDetail(ctx, id);
		}
	}
	
	public static Mod349 saveComments(Occam occam, Mod349 mod349) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().saveComments(ctx, mod349);
		}
	}

	public static Mod349 changeStatus(Occam occam, Mod349 mod349, FiscalStatus newStatus) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().changeStatus(ctx, mod349, newStatus);
		}
	}
	
	public static String getInfo(Occam occam, Mod349 mod349, Mod349Detail detail, FiscalModelKeyInfo infoKey) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getInfo(ctx, mod349, detail, infoKey);
		}
	}
	
	public static Mod349 duplicate(Occam occam, Mod349 mod349) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().duplicate(ctx, mod349);
		}
	}
	
	public static Mod349 aeatPresentation(Occam occam, Mod349 mod, String aeatResponse) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().aeatPresentation(ctx, mod, aeatResponse);
		}
	}

}
