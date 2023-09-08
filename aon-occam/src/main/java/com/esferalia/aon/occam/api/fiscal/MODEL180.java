package com.esferalia.aon.occam.api.fiscal;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.Mod180Detail;
import com.esferalia.aon.occam.impl.jooq.fiscal.MODEL180Impl;

public class MODEL180 {
	private MODEL180() {
		
	}
	
	private static IMODEL180 getImpl() {
		return new MODEL180Impl();
	}

	public static LinkedList<Mod180> getMod180s(Occam occam) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getMod180s(ctx, occam.getDomain());
		}
	}

	public static Mod180 get(Occam occam,Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().get(ctx, id);
		}
	}

	public static Mod180 initialize(Occam occam, Integer year) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().initialize(ctx, year);
		}
	}

	public static Mod180 save(Occam occam, Mod180 mod180) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().save(ctx, mod180);
		}
	}

	public static void delete(Occam occam, Mod180 mod180) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			getImpl().delete(ctx, mod180);
		}
	}

	public static Mod180Detail getDetail(Occam occam, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getDetail(ctx, id);
		}
	}
	
	public static Mod180 saveComments(Occam occam, Mod180 mod180) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().saveComments(ctx, mod180);
		}
	}

	public static Mod180 changeStatus(Occam occam, Mod180 mod180, FiscalStatus newStatus) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().changeStatus(ctx, mod180, newStatus);
		}
	}
	
	public static Mod180 duplicate(Occam occam, Mod180 mod180) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().duplicate(ctx, mod180);
		}
	}
	
	public static Mod180 aeatPresentation(Occam occam, Mod180 mod, String aeatResponse) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().aeatPresentation(ctx, mod, aeatResponse);
		}
	}


}
