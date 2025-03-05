package com.esferalia.aon.occam.api.fiscal;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod202Key;
import com.esferalia.aon.occam.impl.jooq.fiscal.MODEL202Impl;

public class MODEL202 {
	
	private MODEL202() {
		
	}

	private static IMODEL202 getImpl() {
		return new MODEL202Impl();
	}

	public static LinkedList<Mod202> getMod202s(Occam occam) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getMod202s(ctx, occam.getDomain());
		}
	}

	public static Mod202 get(Occam occam, int id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().get(ctx, id);
		}
	}

	public static Mod202 calculate(Occam occam, Mod202 mod202) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().calculate(ctx, mod202);
		}
	}

	public static Mod202 save(Occam occam, Mod202 mod202) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().save(ctx, mod202);
		}
	}

	public static void delete(Occam occam, Mod202 mod202) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			getImpl().delete(ctx, mod202);
		}
	}

	public static Mod202 initialize(Occam occam,Mod202 mod202) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().initialize(ctx, mod202);
		}
	}
	
	public static Mod202 saveComments(Occam occam, Mod202 mod202) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().saveComments(ctx, mod202);
		}
	}
	
	public static Mod202 initializeForFinish(Occam occam, Mod202 mod202) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().initializeForFinish(ctx, mod202);
		}
	}

	public static Mod202 markAsFinished(Occam occam, Mod202 mod202) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsFinished(ctx, mod202);
		}
	}

	public static Mod202 markAsSent(Occam occam, Mod202 mod202) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsSent(ctx, mod202);
		}
	}

	public static Mod202 markAsCustomerCheck(Occam occam, Mod202 mod202) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsCustomerCheck(ctx, mod202);
		}
	}

	public static Mod202 markAsCustomerAccepted(Occam occam, Mod202 mod202) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsCustomerAccepted(ctx, mod202);
		}
	}
	public static Mod202 markAsCustomerRejected(Occam occam, Mod202 mod202, String reason) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsCustomerRejected(ctx, mod202, reason);
		}
	}
	
	public static Mod202 markAsPending(Occam occam, Mod202 mod202) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsPending(ctx, mod202);
		}
	}

	public static Mod202 create(Occam occam,Mod202 mod202) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().create(ctx, mod202);
		}
	}

	public static String getInfo(Occam occam, Mod202 mod202, IModelScript<Mod202Key> script,FiscalModelKeyInfo infoKey) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getInfo(ctx, mod202, script, infoKey);
		}
	}

	public static Mod202 aeatPresentation(Occam occam, Mod202 mod202, String aeatResponse) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().aeatPresentation(ctx, mod202, aeatResponse);
		}
	}

}
