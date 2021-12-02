package com.esferalia.aon.occam.api.fiscal;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
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
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getMod390HFs(ctx, ctx.getDomainId());
		}
	}

	public static Mod390HF getMod390HF(Occam occam, int id) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getMod390HF(ctx, id);
		}
	}

	public static Mod390HF calculate(Occam occam, Mod390HF mod) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().calculateMod390HF(ctx, mod);
		}
	}

	public static Mod390HF save(Occam occam, Mod390HF mod) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().saveMod390HF(ctx, mod);
		}
	}

	public static Mod390HF saveComments(Occam occam, Mod390HF mod) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().saveCommentsMod390HF(ctx, mod);
		}
	}
	
	public static Mod390HF initializeForFinish(Occam occam, Mod390HF mod) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().initializeForFinishMod390HF(ctx, mod);
		}
	}

	public static Mod390HF finish(Occam occam, Mod390HF mod) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsFinishedMod390HF(ctx, mod);
		}
	}

	public static Mod390HF reopen(Occam occam, Mod390HF mod) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsPendingMod390HF(ctx, mod);
		}
	}
	
	public static Mod390HF markAsSent(Occam occam, Mod390HF mod) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsSentMod390HF(ctx, mod);
		}
	}

	public static Mod390HF markAsCustomerCheck(Occam occam, Mod390HF mod) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsCustomerCheck(ctx, mod);
		}
	}

	public static void deleteMod390HF(Occam occam, Mod390HF mod) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			getImpl().deleteMod390HF(ctx, mod);
		}
	}

	public static Mod390HF initializeMod390HF(Occam occam,Mod390HF mod) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().initializeMod390HF(ctx, mod);
		}
	}

	public static Mod390HF createMod390HF(Occam occam,Mod390HF mod) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().createMod390HF(ctx, mod);
		}
	}

	public static Mod390HF declarationChanged(Occam occam,Mod390HF mod) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().declarationChanged(ctx, mod);
		}
	}

	public static String getMod390HFInfo(Occam occam, Mod390HF mod ,IModelScript<Mod390Key> script,FiscalModelKeyInfo infoKey) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getMod390HFInfo(ctx, mod, script, infoKey);
		}
	}

	public static Mod390HF resetMod390HF(Occam occam, Mod390HF mod) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().resetMod390HF(ctx, mod);
		}
	}

}
