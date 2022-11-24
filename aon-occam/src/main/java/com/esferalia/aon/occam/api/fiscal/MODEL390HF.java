package com.esferalia.aon.occam.api.fiscal;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod390Key;
import com.esferalia.aon.occam.impl.jooq.fiscal.MODEL390HFImpl;

public class MODEL390HF {
	private MODEL390HF() {
		
	}

	private static IMODEL390HF getImpl() {
		return new MODEL390HFImpl();
	}

	public static LinkedList<Mod390HF> getMod390HFs(Occam occam) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getMod390HFs(ctx, ctx.getDomainId());
		}
	}

	public static Mod390HF get(Occam occam, int id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().get(ctx, id);
		}
	}

	public static Mod390HF calculate(Occam occam, Mod390HF mod) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().calculate(ctx, mod);
		}
	}

	public static Mod390HF calculateProrrate(Occam occam, Mod390HF mod) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().calculateProrrate(ctx, mod);
		}
	}
	
	
	public static Mod390HF save(Occam occam, Mod390HF mod) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().save(ctx, mod);
		}
	}

	public static Mod390HF saveComments(Occam occam, Mod390HF mod) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().saveComments(ctx, mod);
		}
	}
	
	public static Mod390HF initializeForFinish(Occam occam, Mod390HF mod) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().initializeForFinish(ctx, mod);
		}
	}

	public static void delete(Occam occam, Mod390HF mod) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			getImpl().delete(ctx, mod);
		}
	}

	public static Mod390HF initialize(Occam occam,Mod390HF mod) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().initialize(ctx, mod);
		}
	}

	public static Mod390HF create(Occam occam,Mod390HF mod) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().create(ctx, mod);
		}
	}

	public static String getInfo(Occam occam, Mod390HF mod ,IModelScript<Mod390Key> script,FiscalModelKeyInfo infoKey) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getInfo(ctx, mod, script, infoKey);
		}
	}

	public static Mod390HF reset(Occam occam, Mod390HF mod) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().reset(ctx, mod);
		}
	}

	public static Mod390HF markAsFinished(Occam occam, Mod390HF mod) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsFinished(ctx, mod);
		}
	}

	public static Mod390HF markAsPending(Occam occam, Mod390HF mod) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsPending(ctx, mod);
		}
	}
	
	public static Mod390HF markAsSent(Occam occam, Mod390HF mod) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsSent(ctx, mod);
		}
	}

	public static Mod390HF markAsCustomerCheck(Occam occam, Mod390HF mod) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsCustomerCheck(ctx, mod);
		}
	}
	
	public static Mod390HF markAsCustomerAccepted(Occam occam, Mod390HF mod) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsCustomerAccepted(ctx, mod);
		}
	}
	
	public static Mod390HF markAsCustomerRejected(Occam occam, Mod390HF mod, String reason) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsCustomerRejected(ctx, mod, reason);
		}
	}

}
