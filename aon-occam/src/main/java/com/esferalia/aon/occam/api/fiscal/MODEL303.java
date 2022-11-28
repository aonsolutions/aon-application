package com.esferalia.aon.occam.api.fiscal;

import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.impl.jooq.fiscal.MODEL303Impl;

public class MODEL303 {

	private static IMODEL303 getImpl() {
		return new MODEL303Impl();
	}

	public static LinkedList<Mod303> getMod303s(Occam occam) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getMod303s(ctx, occam.getDomain());
		}
	}

	public static Mod303 get(Occam occam, int id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().get(ctx, id);
		}
	}

	public static Mod303 calculate(Occam occam, Mod303 mod303) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().calculate(ctx, mod303);
		}
	}

	public static Mod303 calculateProrrate(Occam occam, Mod303 mod303) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().calculateProrrate(ctx, mod303);
		}
	}

	public static Mod303 save(Occam occam, Mod303 mod303) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().save(ctx, mod303);
		}
	}

	public static Mod303 saveComments(Occam occam, Mod303 mod303) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().saveComments(ctx, mod303);
		}
	}
	
	public static Mod303 initializeForFinish(Occam occam, Mod303 mod303) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().initializeForFinish(ctx, mod303);
		}
	}

	public static Mod303 markAsFinished(Occam occam, Mod303 mod303) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsFinished(ctx, mod303);
		}
	}

	public static Mod303 markAsPending(Occam occam, Mod303 mod303) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsPending(ctx, mod303);
		}
	}
	
	public static Mod303 markAsSent(Occam occam, Mod303 mod303) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsSent(ctx, mod303);
		}
	}
	public static Mod303 markAsCustomerCheck(Occam occam, Mod303 mod303) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsCustomerCheck(ctx, mod303);
		}
	}
	public static Mod303 markAsCustomerAccepted(Occam occam, Mod303 mod303) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsCustomerAccepted(ctx, mod303);
		}
	}
	public static Mod303 markAsCustomerRejected(Occam occam, Mod303 mod303, String reason) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsCustomerRejected(ctx, mod303, reason);
		}
	}
	
	
	public static void delete(Occam occam, Mod303 mod303) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			getImpl().delete(ctx, mod303);
		}
	}

	public static Mod303 initialize(Occam occam, Mod303 mod303) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().initialize(ctx, mod303);
		}
	}

	public static Mod303 create(Occam occam,Mod303 mod303) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().create(ctx, mod303);
		}
	}

	public static String getInfo(Occam occam, Mod303 mod303
			,IModelScript<Mod303Key> script,FiscalModelKeyInfo infoKey) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getInfo(ctx, mod303, script, infoKey);
		}
	}
	
	public static Stream<VatContext> getInfo(Occam occam, Mod303 mod303,Mod303Key key) {
	 	final CloseableAONContext ctx = AONContext.getAONContext(occam);
	 	return getImpl().getInfo(ctx, mod303, key, () -> AONContext.closeQuietly(ctx));
	}

	public static Mod303 aeatPresentation(Occam occam, Mod303 mod303, String aeatResponse) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().aeatPresentation(ctx, mod303, aeatResponse);
		}
	}

	public static Mod303 reset(Occam occam, Mod303 mod303) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().reset(ctx, mod303);
		}
	}

	public static Mod303 doRecord(Occam occam, Mod303 mod303) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().doRecord(ctx, mod303);
		}
	}

	public static Mod303 unrecord(Occam occam, Mod303 mod303) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().unrecord(ctx, mod303);
		}
	}
}
