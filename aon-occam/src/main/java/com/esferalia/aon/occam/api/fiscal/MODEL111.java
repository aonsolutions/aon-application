package com.esferalia.aon.occam.api.fiscal;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.occam.impl.jooq.fiscal.MODEL111Impl;

public class MODEL111 {

	private MODEL111() {
		
	}

	private static IMODEL111 getImpl() {
		return new MODEL111Impl();
	}

	public static LinkedList<Mod111> getMod111s(Occam occam) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getMod111s(ctx, occam.getDomain());
		}
	}

	public static Mod111 get(Occam occam, int id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().get(ctx, id);
		}
	}

	public static Mod111 calculate(Occam occam, Mod111 mod111) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().calculate(ctx, mod111);
		}
	}

	public static Mod111 save(Occam occam, Mod111 mod111) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().save(ctx, mod111);
		}
	}

	public static Mod111 saveComments(Occam occam, Mod111 mod111) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().saveComments(ctx, mod111);
		}
	}
	
	public static Mod111 initializeForFinish(Occam occam, Mod111 mod111) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().initializeForFinish(ctx, mod111);
		}
	}

	public static Mod111 markAsFinished(Occam occam, Mod111 mod111) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsFinished(ctx, mod111);
		}
	}

	public static Mod111 markAsPending(Occam occam, Mod111 mod111) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsPending(ctx, mod111);
		}
	}

	public static Mod111 markAsSent(Occam occam, Mod111 mod111) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsSent(ctx, mod111);
		}
	}

	public static Mod111 markAsCustomerCheck(Occam occam, Mod111 mod111) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsCustomerCheck(ctx, mod111);
		}
	}

	public static Mod111 markAsCustomerAccepted(Occam occam, Mod111 mod111) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsCustomerAccepted(ctx, mod111);
		}
	}

	public static Mod111 markAsCustomerRejected(Occam occam, Mod111 mod111, String reason) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsCustomerRejected(ctx, mod111, reason);
		}
	}

	public static void delete(Occam occam, Mod111 mod111) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			getImpl().delete(ctx, mod111);
		}
	}

	public static Mod111 initialize(Occam occam,Mod111 mod111) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().initialize(ctx, mod111);
		}
	}

	public static Mod111 create(Occam occam,Mod111 mod111) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().create(ctx, mod111);
		}
	}

	public static String getInfo(Occam occam, Mod111 mod111
			,IModelScript<Mod111Key> script,FiscalModelKeyInfo infoKey) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getInfo(ctx, mod111, script, infoKey);
		}
	}
	
	public static Mod111 aeatPresentation(Occam occam, Mod111 mod111, String aeatResponse) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().aeatPresentation(ctx, mod111, aeatResponse);
		}
	}

}
