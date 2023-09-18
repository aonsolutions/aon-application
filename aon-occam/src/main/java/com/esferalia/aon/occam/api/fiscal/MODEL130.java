package com.esferalia.aon.occam.api.fiscal;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod130Key;
import com.esferalia.aon.occam.impl.jooq.fiscal.MODEL130Impl;

public class MODEL130 {
	
	private MODEL130() {
		
	}
	
	private static IMODEL130 getImpl() {
		return new MODEL130Impl();
	}

	public static LinkedList<Mod130> getMod130s(Occam occam) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getMod130s(ctx, occam.getDomain());
		}
	}

	public static Mod130 get(Occam occam, int id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().get(ctx, id);
		}
	}

	public static Mod130 calculate(Occam occam, Mod130 mod130) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().calculate(ctx, mod130);
		}
	}

	public static Mod130 save(Occam occam, Mod130 mod130) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().save(ctx, mod130);
		}
	}

	public static Mod130 saveComments(Occam occam, Mod130 mod130) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().saveComments(ctx, mod130);
		}
	}
	
	public static Mod130 initializeForFinish(Occam occam, Mod130 mod130) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().initializeForFinish(ctx, mod130);
		}
	}

	public static void delete(Occam occam, Mod130 mod130) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			getImpl().delete(ctx, mod130);
		}
	}

	public static Mod130 initialize(Occam occam, Mod130 mod130) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().initialize(ctx, mod130);
		}
	}

	public static Mod130 create(Occam occam,Mod130 mod130) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().create(ctx, mod130);
		}
	}

	public static Mod130 aeatPresentation(Occam occam, Mod130 mod130, String aeatResponse) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().aeatPresentation(ctx, mod130, aeatResponse);
		}
	}

	public static Mod130 markAsFinished(Occam occam, Mod130 mod130) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsFinished(ctx, mod130);
		}
	}

	public static Mod130 markAsPending(Occam occam, Mod130 mod130) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsPending(ctx, mod130);
		}
	}

	public static Mod130 markAsSent(Occam occam, Mod130 mod130) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsSent(ctx, mod130);
		}
	}

	public static Mod130 markAsCustomerCheck(Occam occam, Mod130 mod130) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsCustomerCheck(ctx, mod130);
		}
	}
	
	public static Mod130 markAsCustomerAccepted(Occam occam, Mod130 mod130) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsCustomerAccepted(ctx, mod130);
		}
	}

	public static Mod130 markAsCustomerRejected(Occam occam, Mod130 mod130, String reason) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsCustomerRejected(ctx, mod130, reason);
		}
	}

	public static String getInfo(Occam occam, Mod130 mod130, IModelScript<Mod130Key> script,
			FiscalModelKeyInfo infoKey) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getInfo(ctx, mod130, script, infoKey);
		}
	}

}
