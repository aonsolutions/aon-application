package com.esferalia.aon.occam.api.fiscal;

import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod123Key;
import com.esferalia.aon.occam.impl.jooq.fiscal.MODEL123Impl;

public class MODEL123 {
	
	private MODEL123() {
		
	}

	private static IMODEL123 getImpl() {
		return new MODEL123Impl();
	}
	
	public static LinkedList<Mod123> getMod123s(Occam occam) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getMod123s(ctx, occam.getDomain());
		}
	}

	public static Mod123 get(Occam occam, int id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().get(ctx, id);
		}
	}

	public static Mod123 calculate(Occam occam, Mod123 mod123) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().calculate(ctx, mod123);
		}
	}

	public static Mod123 save(Occam occam, Mod123 mod123) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().save(ctx, mod123);
		}
	}

	public static Mod123 saveComments(Occam occam, Mod123 mod123) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().saveComments(ctx, mod123);
		}
	}
	
	public static Mod123 initializeForFinish(Occam occam, Mod123 mod123) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().initializeForFinish(ctx, mod123);
		}
	}

	public static Mod123 markAsFinished(Occam occam, Mod123 mod123) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsFinished(ctx, mod123);
		}
	}

	public static Mod123 markAsSent(Occam occam, Mod123 mod123) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsSent(ctx, mod123);
		}
	}

	public static Mod123 markAsCustomerCheck(Occam occam, Mod123 mod123) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsCustomerCheck(ctx, mod123);
		}
	}

	public static Mod123 markAsCustomerAccepted(Occam occam, Mod123 mod123) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsCustomerAccepted(ctx, mod123);
		}
	}

	public static Mod123 markAsCustomerRejected(Occam occam, Mod123 mod123,String reason) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsCustomerRejected(ctx, mod123, reason);
		}
	}
	
	public static Mod123 markAsPending(Occam occam, Mod123 mod123) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsPending(ctx, mod123);
		}
	}

	public static void delete(Occam occam, Mod123 mod123) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			getImpl().delete(ctx, mod123);
		}
	}

	public static Mod123 initialize(Occam occam,Mod123 mod123) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().initialize(ctx, mod123);
		}
	}

	public static Mod123 create(Occam occam,Mod123 mod123) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().create(ctx, mod123);
		}
	}

	public static String getInfo(Occam occam, Mod123 mod123, IModelScript<Mod123Key> script, FiscalModelKeyInfo infoKey) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getInfo(ctx, mod123, script, infoKey);
		}
	}
	public static Stream<IrpfBreakdown> getInfo(Occam occam, Mod123 mod123,Mod123Key key) {
	 	final CloseableAONContext ctx = AONContext.getAONContext(occam);
	 	return getImpl().getInfo(ctx, mod123, key, () -> AONContext.closeQuietly(ctx));
	}

	public static Mod123 aeatPresentation(Occam occam, Mod123 mod123, String aeatResponse) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().aeatPresentation(ctx, mod123, aeatResponse);
		}
	}

}
