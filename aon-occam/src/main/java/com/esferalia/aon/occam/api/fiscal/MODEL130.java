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

	public static Mod130 getMod130(Occam occam, int id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getMod130(ctx, id);
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

	public static Mod130 markAsFinished(Occam occam, Mod130 mod130) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsFinished(ctx, mod130);
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
	
	public static Mod130 markAsPending(Occam occam, Mod130 mod130) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsPending(ctx, mod130);
		}
	}

	public static void deleteMod130(Occam occam, Mod130 mod130) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			getImpl().delete(ctx, mod130);
		}
	}

	public static Mod130 initializeMod130(Occam occam, Mod130 mod130) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().initialize(ctx, mod130);
		}
	}

	public static Mod130 createMod130(Occam occam,Mod130 mod130) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().create(ctx, mod130);
		}
	}

	public static String getMod130Info(Occam occam, Mod130 mod130, IModelScript<Mod130Key> script,
			FiscalModelKeyInfo infoKey) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getInfo(ctx, mod130, script, infoKey);
		}
	}

	public static Mod130 aeatPresentation(Occam occam, Mod130 mod130, String aeatResponse) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().aeatPresentation(ctx, mod130, aeatResponse);
		}
	}

	public static Mod130 resetMod130(Occam occam, Mod130 mod130) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().reset(ctx, mod130);
		}
	}


}
