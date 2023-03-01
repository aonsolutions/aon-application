package com.esferalia.aon.occam.api.fiscal;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.occam.impl.jooq.fiscal.MODEL190Impl;

public class MODEL190 {
	private MODEL190() {
		
	}
	
	private static IMODEL190 getImpl() {
		return new MODEL190Impl();
	}

	// ----------------------------------MODELO 190
	public static LinkedList<Mod190> getMod190s(Occam occam) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getMod190s(ctx, occam.getDomain());
		}
	}

	public static Mod190 get(Occam occam, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().get(ctx, id);
		}
	}

	public static Mod190 initialize(Occam occam, Integer year) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().initialize(ctx, year);
		}
	}

	public static Mod190 save(Occam occam, Mod190 mod190) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().save(ctx, mod190);
		}
	}

	public static void delete(Occam occam, Mod190 mod190) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			getImpl().delete(ctx, mod190);
		}
	}

	public static Mod190Detail getDetail(Occam occam, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getDetail(ctx, id);
		}
	}

	public static Mod190 saveComments(Occam occam, Mod190 mod190) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().saveComments(ctx, mod190);
		}
	}

	public static Mod190 changeStatus(Occam occam, Mod190 mod190, FiscalStatus newStatus) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().changeStatus(ctx, mod190, newStatus);
		}
	}
	
	public static Mod190 duplicate(Occam occam, Mod190 mod190) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().duplicate(ctx, mod190);
		}
	}

	public static LinkedList<Mod190Detail> validateSalaries(Occam occam, Mod190 mod190) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().validateSalaries(ctx, mod190);
		}
	}
	
	public static Mod190 aeatPresentation(Occam occam, Mod190 mod, String aeatResponse) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().aeatPresentation(ctx, mod, aeatResponse);
		}
	}

}
