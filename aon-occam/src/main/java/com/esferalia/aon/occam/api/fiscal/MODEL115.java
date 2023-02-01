package com.esferalia.aon.occam.api.fiscal;

import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod115Key;
import com.esferalia.aon.occam.impl.jooq.fiscal.MODEL115Impl;

public class MODEL115 {
	
	private MODEL115() {
		
	}

	private static IMODEL115 getImpl() {
		return new MODEL115Impl();
	}

	public static LinkedList<Mod115> getMod115s(Occam occam) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getMod115s(ctx, occam.getDomain());
		}
	}

	public static Mod115 get(Occam occam, int id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().get(ctx, id);
		}
	}

	public static Mod115 calculate(Occam occam, Mod115 mod115) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().calculate(ctx, mod115);
		}
	}

	public static Mod115 save(Occam occam, Mod115 mod115) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().save(ctx, mod115);
		}
	}

	public static Mod115 saveComments(Occam occam, Mod115 mod115) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().saveComments(ctx, mod115);
		}
	}
	
	public static Mod115 initializeForFinish(Occam occam, Mod115 mod115) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().initializeForFinish(ctx, mod115);
		}
	}

	public static Mod115 markAsFinished(Occam occam, Mod115 mod115) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsFinished(ctx, mod115);
		}
	}

	public static Mod115 markAsSent(Occam occam, Mod115 mod115) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsSent(ctx, mod115);
		}
	}

	public static Mod115 markAsCustomerCheck(Occam occam, Mod115 mod115) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsCustomerCheck(ctx, mod115);
		}
	}

	public static Mod115 markAsCustomerAccepted(Occam occam, Mod115 mod115) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsCustomerAccepted(ctx, mod115);
		}
	}

	public static Mod115 markAsCustomerRejected(Occam occam, Mod115 mod115, String reason) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsCustomerRejected(ctx, mod115, reason);
		}
	}

	public static Mod115 markAsPending(Occam occam, Mod115 mod115) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsPending(ctx, mod115);
		}
	}

	public static void delete(Occam occam, Mod115 mod115) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			getImpl().delete(ctx, mod115);
		}
	}

	public static Mod115 initialize(Occam occam, Mod115 mod115) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().initialize(ctx, mod115);
		}
	}

	public static Mod115 create(Occam occam,Mod115 mod115) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().create(ctx, mod115);
		}
	}

	public static String getInfo(Occam occam, Mod115 mod115, IModelScript<Mod115Key> script, FiscalModelKeyInfo infoKey) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getInfo(ctx, mod115, script, infoKey);
		}
	}
	public static Stream<IrpfBreakdown> getInfo(Occam occam, Mod115 mod115,Mod115Key key) {
	 	final CloseableAONContext ctx = AONContext.getAONContext(occam);
	 	return getImpl().getInfo(ctx, mod115, key, () -> AONContext.closeQuietly(ctx));
	}
	
	public static Mod115 aeatPresentation(Occam occam, Mod115 mod115, String aeatResponse) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().aeatPresentationMod115(ctx, mod115, aeatResponse);
		}
	}

	public static Mod115 reset(Occam occam, Mod115 mod115) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().reset(ctx, mod115);
		}
	}
}
