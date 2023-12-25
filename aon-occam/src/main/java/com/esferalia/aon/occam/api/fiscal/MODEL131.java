package com.esferalia.aon.occam.api.fiscal;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod131Activity;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod131Key;
import com.esferalia.aon.occam.impl.jooq.fiscal.MODEL131Impl;

public class MODEL131 {
	
	private MODEL131() {
		
	}

	private static IMODEL131 getImpl() {
		return new MODEL131Impl();
	}

	public static LinkedList<Mod131> getMod131s(Occam occam) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getMod131s(ctx, occam.getDomain());
		}
	}

	public static Mod131 get(Occam occam, int id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().get(ctx, id);
		}
	}

	public static Mod131 calculate(Occam occam, Mod131 mod131) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().calculate(ctx, mod131);
		}
	}

	public static Mod131Activity calculate(Occam occam, Mod131 mod131, Mod131Activity activity) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().calculateActivity(ctx, mod131, activity);
		}
	}

	public static Mod131 save(Occam occam, Mod131 mod131) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().save(ctx, mod131);
		}
	}

	public static Mod131 saveComments(Occam occam, Mod131 mod131) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().saveComments(ctx, mod131);
		}
	}
	
	public static Mod131 initializeForFinish(Occam occam, Mod131 mod131) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().initializeForFinish(ctx, mod131);
		}
	}

	public static Mod131 finish(Occam occam, Mod131 mod131) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsFinished(ctx, mod131);
		}
	}
	public static Mod131 markAsSent(Occam occam, Mod131 mod131) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsSent(ctx, mod131);
		}
	}

	public static Mod131 markAsCustomerCheck(Occam occam, Mod131 mod131) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsCustomerCheck(ctx, mod131);
		}
	}

	public static Mod131 reopen(Occam occam, Mod131 mod131) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsPending(ctx, mod131);
		}
	}

	public static void delete(Occam occam, Mod131 mod131) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			getImpl().delete(ctx, mod131);
		}
	}

	public static Mod131 initialize(Occam occam,Mod131 mod131) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().initialize(ctx, mod131);
		}
	}

	public static Mod131 create(Occam occam,Mod131 mod131) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().create(ctx, mod131);
		}
	}

	public static String getInfo(Occam occam, Mod131 mod131, IModelScript<Mod131Key> script, FiscalModelKeyInfo infoKey) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getInfo(ctx, mod131, script, infoKey);
		}
	}

	public static Mod131 aeatPresentation(Occam occam, Mod131 mod131, String aeatResponse) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().aeatPresentation(ctx, mod131, aeatResponse);
		}
	}

	public static Mod131 reset(Occam occam, Mod131 mod131) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().reset(ctx, mod131);
		}
	}
}
