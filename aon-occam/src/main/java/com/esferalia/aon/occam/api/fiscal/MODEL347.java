package com.esferalia.aon.occam.api.fiscal;

import java.io.Writer;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod347;
import com.esferalia.aon.occam.api.model.fiscal.Mod347Declared;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.impl.jooq.fiscal.MODEL347Impl;

public class MODEL347 {
	private MODEL347() {

	}

	private static IMODEL347 getImpl() {
		return new MODEL347Impl();
	}

	public static LinkedList<Mod347> getMod347s(Occam occam) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getMod347s(ctx, occam.getDomain());
		}
	}

	public static Mod347 get(Occam occam, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().get(ctx, id);
		}
	}

	public static Mod347 initialize(Occam occam, int year) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().initialize(ctx, year);
		}
	}

	public static Mod347 reset(Occam occam, Mod347 mod347) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().reset(ctx, mod347);
		}
	}

	public static Mod347 save(Occam occam, Mod347 mod347) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().save(ctx, mod347);
		}
	}

	public static void delete(Occam occam, Mod347 mod347) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			getImpl().delete(ctx, mod347);
		}
	}

	public static Mod347 saveComments(Occam occam, Mod347 mod347) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().saveComments(ctx, mod347);
		}
	}

	public static Mod347 changeStatus(Occam occam, Mod347 mod347, FiscalStatus newStatus) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().changeStatus(ctx, mod347, newStatus);
		}
	}

	public static String getInfo(Occam occam, Mod347 mod347, Mod347Declared declared, FiscalModelKeyInfo infoKey) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getInfo(ctx, mod347, declared, infoKey);
		}
	}

	public static Mod347 duplicate(Occam occam, Mod347 mod347) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().duplicate(ctx, mod347);
		}
	}

	public static void writeMailMergeReport(Occam occam, Mod347 mod347, Writer writer) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			getImpl().writeMailMergeReport(ctx, mod347, writer);
		}
	}
	
	public static Mod347 aeatPresentation(Occam occam, Mod347 mod, String aeatResponse) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().aeatPresentation(ctx, mod, aeatResponse);
		}
	}

}
