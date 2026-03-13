package com.esferalia.aon.occam.api.fiscal;

import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod421;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod421Key;
import com.esferalia.aon.occam.impl.jooq.fiscal.MODEL421Impl;

public class MODEL421 {

	private static IMODEL421 getImpl() {
		return new MODEL421Impl();
	}

	public static LinkedList<Mod421> getMod421s(Occam occam) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getMod421s(ctx, occam.getDomain());
		}
	}

	public static Mod421 get(Occam occam, int id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().get(ctx, id);
		}
	}

	public static Mod421 calculate(Occam occam, Mod421 mod421) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().calculate(ctx, mod421);
		}
	}

	public static Mod421 save(Occam occam, Mod421 mod421) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().save(ctx, mod421);
		}
	}

	public static Mod421 saveComments(Occam occam, Mod421 mod421) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().saveComments(ctx, mod421);
		}
	}
	
	public static Mod421 initializeForFinish(Occam occam, Mod421 mod421) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().initializeForFinish(ctx, mod421);
		}
	}

	public static Mod421 markAsFinished(Occam occam, Mod421 mod421) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsFinished(ctx, mod421);
		}
	}

	public static Mod421 markAsPending(Occam occam, Mod421 mod421) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsPending(ctx, mod421);
		}
	}
	
	public static Mod421 markAsSent(Occam occam, Mod421 mod421) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsSent(ctx, mod421);
		}
	}
	public static Mod421 markAsCustomerCheck(Occam occam, Mod421 mod421) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsCustomerCheck(ctx, mod421);
		}
	}
	public static Mod421 markAsCustomerAccepted(Occam occam, Mod421 mod421) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsCustomerAccepted(ctx, mod421);
		}
	}
	public static Mod421 markAsCustomerRejected(Occam occam, Mod421 mod421, String reason) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsCustomerRejected(ctx, mod421, reason);
		}
	}
	
	
	public static void delete(Occam occam, Mod421 mod421) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			getImpl().delete(ctx, mod421);
		}
	}

	public static Mod421 initialize(Occam occam, Mod421 mod421) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().initialize(ctx, mod421);
		}
	}

	public static Mod421 create(Occam occam,Mod421 mod421) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().create(ctx, mod421);
		}
	}

	public static String getInfo(Occam occam, Mod421 mod421,IModelScript<Mod421Key> script,FiscalModelKeyInfo infoKey) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getInfo(ctx, mod421, script, infoKey);
		}
	}
	
	public static Stream<VatContext> getInfo(Occam occam, Mod421 mod421,Mod421Key key) {
	 	final CloseableAONContext ctx = AONContext.getAONContext(occam);
	 	return getImpl().getInfo(ctx, mod421, key, () -> AONContext.closeQuietly(ctx));
	}

	public static Mod421 doRecord(Occam occam, Mod421 mod421) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().doRecord(ctx, mod421);
		}
	}

	public static Mod421 unrecord(Occam occam, Mod421 mod421) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().unrecord(ctx, mod421);
		}
	}
}
