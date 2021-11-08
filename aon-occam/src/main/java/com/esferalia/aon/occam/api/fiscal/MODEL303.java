package com.esferalia.aon.occam.api.fiscal;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IFiscal;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.impl.jooq.FiscalImpl;
import com.esferalia.aon.occam.impl.jooq.fiscal.MODEL303Impl;

public class MODEL303 {

	private static IMODEL303 getImpl() {
		return new MODEL303Impl();
	}

	public static LinkedList<Mod303> getMod303s(Occam occam) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getMod303s(ctx, occam.getDomain());
		}
	}

	public static Mod303 getMod303(Occam occam, int id) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getMod303(ctx, id);
		}
	}

	public static Mod303 calculate(Occam occam, Mod303 mod303) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().calculateMod303(ctx, mod303);
		}
	}

	public static Mod303 save(Occam occam, Mod303 mod303) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().saveMod303(ctx, mod303);
		}
	}

	public static Mod303 saveComments(Occam occam, Mod303 mod303) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().saveCommentsMod303(ctx, mod303);
		}
	}
	
	public static Mod303 initializeForFinish(Occam occam, Mod303 mod303) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().initializeForFinishMod303(ctx, mod303);
		}
	}

	public static Mod303 finish(Occam occam, Mod303 mod303) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsFinishedMod303(ctx, mod303);
		}
	}

	public static Mod303 reopen(Occam occam, Mod303 mod303) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsPendingMod303(ctx, mod303);
		}
	}
	
	public static Mod303 markAsSent(Occam occam, Mod303 mod303) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsSentMod303(ctx, mod303);
		}
	}
	public static Mod303 markAsCustomerCheck(Occam occam, Mod303 mod303) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().markAsCustomerCheckMod303(ctx, mod303);
		}
	}
	
	
	public static void deleteMod303(Occam occam, Mod303 mod303) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			getImpl().deleteMod303(ctx, mod303);
		}
	}

	public static Mod303 initializeMod303(Occam occam, Mod303 mod303) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().initializeMod303(ctx, mod303);
		}
	}

	public static Mod303 createMod303(Occam occam,Mod303 mod303) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().createMod303(ctx, mod303);
		}
	}

	public static Mod303 declarationChanged(Occam occam,Mod303 mod303) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().declarationChanged(ctx, mod303);
		}
	}

	public static String getMod303Info(Occam occam, Mod303 mod303
			,IModelScript<Mod303Key> script,FiscalModelKeyInfo infoKey) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getMod303Info(ctx, mod303, script, infoKey);
		}
	}
	
	public static Mod303 aeatPresentationMod303(Occam occam, Mod303 mod303, String aeatResponse) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().aeatPresentationMod303(ctx, mod303, aeatResponse);
		}
	}

}
