package com.esferalia.aon.occam.api;

import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.fiscal.FiscalMatrixParams;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.IRPFParams;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod131Activity;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.Mod180Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
import com.esferalia.aon.occam.api.model.fiscal.Mod200;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.Mod347;
import com.esferalia.aon.occam.api.model.fiscal.Mod347Declared;
import com.esferalia.aon.occam.api.model.fiscal.Mod349;
import com.esferalia.aon.occam.api.model.fiscal.Mod349Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902014;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902015;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.fiscal.OperationBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.OperationParams;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.fiscal.VatParams;
import com.esferalia.aon.occam.api.model.fiscal.VatSummaryContext;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2017.Mod2002017;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.occam.api.model.type.Mod115Key;
import com.esferalia.aon.occam.api.model.type.Mod123Key;
import com.esferalia.aon.occam.api.model.type.Mod130Key;
import com.esferalia.aon.occam.api.model.type.Mod131Key;
import com.esferalia.aon.occam.api.model.type.Mod202Key;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.api.model.type.Mod390Key;
import com.esferalia.aon.occam.impl.jooq.FiscalImpl;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class FISCAL {

	private static IFiscal getFiscal() {
		return new FiscalImpl();
	}

	// ********************************************
	// ********************************** FISCAL **
	// ********************************************

	// -------------------------- FISCAL PANEL
	public static LinkedList<IFiscalModel> getFiscalPanel(String domainName,
			int domain, FiscalMatrixParams params, String login) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, login);
			User user = AON.getUser(domainName, domain, login);
			return getFiscal().getFiscalPanel(ctx, domain, params, user.getId());
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// ----------------------------------MODELO 303
	public static LinkedList<Mod303> getMod303s(String domainName,int domainId, String user) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId,user);
			return getFiscal().getMod303s(ctx, domainId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod303 getMod303(String domainName, int domainId, String user, int id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId,user);
			return getFiscal().getMod303(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod303 calculate(String domainName, String user, Mod303 mod303) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod303.getDomain(),user);
			return getFiscal().calculateMod303(ctx, mod303);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod303 save(String domainName, String user, Mod303 mod303) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod303.getDomain(),user);
			return getFiscal().saveMod303(ctx, mod303);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod303 saveComments(String domainName, String user, Mod303 mod303) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod303.getDomain(),user);
			return getFiscal().saveCommentsMod303(ctx, mod303);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Mod303 initializeForFinish(String domainName, String user, Mod303 mod303) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod303.getDomain(),user);
			return getFiscal().initializeForFinishMod303(ctx, mod303);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod303 finish(String domainName, String user, Mod303 mod303) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod303.getDomain(),user);
			return getFiscal().markAsFinishedMod303(ctx, mod303);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod303 reopen(String domainName, String user, Mod303 mod303) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod303.getDomain(),user);
			return getFiscal().markAsPendingMod303(ctx, mod303);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Mod303 markAsSent(String domainName, Mod303 mod303, String user) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod303.getDomain(),user);
			return getFiscal().markAsSentMod303(ctx, mod303);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void deleteMod303(String domainName, String user, Mod303 mod303) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod303.getDomain(),user);
			getFiscal().deleteMod303(ctx, mod303);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod303 initializeMod303(String domainName, int domain, String user,Mod303 mod303) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,user);
			return getFiscal().initializeMod303(ctx, mod303);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod303 createMod303(String domainName, int domain, String user,Mod303 mod303) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,user);
			return getFiscal().createMod303(ctx, mod303);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod303 declarationChanged(String domainName, int domain, String user,Mod303 mod303) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,user);
			return getFiscal().declarationChanged(ctx, mod303);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static String getMod303Info(String domainName, int domain, String user, Mod303 mod303
			,IModelScript<Mod303Key> script,FiscalModelKeyInfo infoKey) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,user);
			return getFiscal().getMod303Info(ctx, mod303, script, infoKey);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	// ----------------------------------MODELO 390 HACIENDAS FORALES
	public static LinkedList<Mod390HF> getMod390HFs(String domainName,int domainId, String user) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId,user);
			return getFiscal().getMod390HFs(ctx, domainId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod390HF getMod390HF(String domainName, int domainId, String user, int id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId,user);
			return getFiscal().getMod390HF(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod390HF calculate(String domainName, String user, Mod390HF mod) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod.getDomain(),user);
			return getFiscal().calculateMod390HF(ctx, mod);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod390HF save(String domainName, String user, Mod390HF mod) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod.getDomain(),user);
			return getFiscal().saveMod390HF(ctx, mod);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod390HF saveComments(String domainName, String user, Mod390HF mod) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod.getDomain(),user);
			return getFiscal().saveCommentsMod390HF(ctx, mod);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Mod390HF initializeForFinish(String domainName, String user, Mod390HF mod) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod.getDomain(),user);
			return getFiscal().initializeForFinishMod390HF(ctx, mod);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod390HF finish(String domainName, String user, Mod390HF mod) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod.getDomain(),user);
			return getFiscal().markAsFinishedMod390HF(ctx, mod);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod390HF reopen(String domainName, String user, Mod390HF mod) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod.getDomain(),user);
			return getFiscal().markAsPendingMod390HF(ctx, mod);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Mod390HF markAsSent(String domainName, Mod390HF mod, String user) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod.getDomain(),user);
			return getFiscal().markAsSentMod390HF(ctx, mod);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void deleteMod390HF(String domainName, String user, Mod390HF mod) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod.getDomain(),user);
			getFiscal().deleteMod390HF(ctx, mod);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod390HF initializeMod390HF(String domainName, int domain, String user,Mod390HF mod) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,user);
			return getFiscal().initializeMod390HF(ctx, mod);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod390HF createMod390HF(String domainName, int domain, String user,Mod390HF mod) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,user);
			return getFiscal().createMod390HF(ctx, mod);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod390HF declarationChanged(String domainName, int domain, String user,Mod390HF mod) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,user);
			return getFiscal().declarationChanged(ctx, mod);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static String getMod390HFInfo(String domainName, int domain, String user, Mod390HF mod
			,IModelScript<Mod390Key> script,FiscalModelKeyInfo infoKey) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,user);
			return getFiscal().getMod390HFInfo(ctx, mod, script, infoKey);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	
	// ----------------------------------MODELO 111
	public static LinkedList<Mod111> getMod111s(String domainName,int domainId, String user) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId,user);
			return getFiscal().getMod111s(ctx, domainId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod111 getMod111(String domainName, int domainId, String user, int id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId,user);
			return getFiscal().getMod111(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod111 calculate(String domainName, String user, Mod111 mod111) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod111.getDomain(),user);
			return getFiscal().calculate(ctx, mod111);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod111 save(String domainName, String user, Mod111 mod111) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod111.getDomain(),user);
			return getFiscal().save(ctx, mod111);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod111 saveComments(String domainName, String user, Mod111 mod111) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod111.getDomain(),user);
			return getFiscal().saveComments(ctx, mod111);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Mod111 initializeForFinish(String domainName, String user, Mod111 mod111) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod111.getDomain(),user);
			return getFiscal().initializeForFinish(ctx, mod111);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod111 markAsFinished(String domainName, String user, Mod111 mod111) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod111.getDomain(),user);
			return getFiscal().markAsFinished(ctx, mod111);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod111 markAsPending(String domainName, String user, Mod111 mod111) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod111.getDomain(),user);
			return getFiscal().markAsPending(ctx, mod111);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod111 markAsSent(String domainName, String user, Mod111 mod111) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod111.getDomain(),user);
			return getFiscal().markAsSent(ctx, mod111);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void deleteMod111(String domainName, String user, Mod111 mod111) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod111.getDomain(),user);
			getFiscal().delete(ctx, mod111);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod111 initializeMod111(String domainName, int domain, String user,Mod111 mod111) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,user);
			return getFiscal().initialize(ctx, mod111);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod111 createMod111(String domainName, int domain, String user,Mod111 mod111) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,user);
			return getFiscal().create(ctx, mod111);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static String getMod111Info(String domainName, int domain, String user, Mod111 mod111
			,IModelScript<Mod111Key> script,FiscalModelKeyInfo infoKey) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,user);
			return getFiscal().getInfo(ctx, mod111, script, infoKey);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// ----------------------------------MODELO 115
	public static LinkedList<Mod115> getMod115s(String domainName,int domainId, String user) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId,user);
			return getFiscal().getMod115s(ctx, domainId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod115 getMod115(String domainName, int domainId, String user, int id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId,user);
			return getFiscal().getMod115(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod115 calculate(String domainName, String user, Mod115 mod115) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod115.getDomain(),user);
			return getFiscal().calculate(ctx, mod115);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod115 save(String domainName, String user, Mod115 mod115) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod115.getDomain(),user);
			return getFiscal().save(ctx, mod115);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod115 saveComments(String domainName, String user, Mod115 mod115) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod115.getDomain(),user);
			return getFiscal().saveComments(ctx, mod115);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Mod115 initializeForFinish(String domainName, String user, Mod115 mod115) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod115.getDomain(),user);
			return getFiscal().initializeForFinish(ctx, mod115);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod115 markAsFinished(String domainName, String user, Mod115 mod115) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod115.getDomain(),user);
			return getFiscal().markAsFinished(ctx, mod115);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod115 markAsSent(String domainName, String userLogin, Mod115 mod115) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod115.getDomain(),userLogin);
			return getFiscal().markAsSent(ctx, mod115);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod115 markAsPending(String domainName, String user, Mod115 mod115) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod115.getDomain(),user);
			return getFiscal().markAsPending(ctx, mod115);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void deleteMod115(String domainName, String user, Mod115 mod115) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod115.getDomain(),user);
			getFiscal().delete(ctx, mod115);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod115 initializeMod115(String domainName, int domain, String user,Mod115 mod115) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,user);
			return getFiscal().initialize(ctx, mod115);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod115 createMod115(String domainName, int domain, String user,Mod115 mod115) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,user);
			return getFiscal().create(ctx, mod115);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static String getMod115Info(String domainName, int domain, String user, Mod115 mod115, IModelScript<Mod115Key> script,
			FiscalModelKeyInfo infoKey) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,user);
			return getFiscal().getInfo(ctx, mod115, script, infoKey);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// ----------------------------------MODELO 123
	public static LinkedList<Mod123> getMod123s(String domainName,int domainId, String user) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId,user);
			return getFiscal().getMod123s(ctx, domainId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod123 getMod123(String domainName, int domainId, String user, int id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId,user);
			return getFiscal().getMod123(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod123 calculate(String domainName, String user, Mod123 mod123) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod123.getDomain(),user);
			return getFiscal().calculate(ctx, mod123);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod123 save(String domainName, String user, Mod123 mod123) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod123.getDomain(),user);
			return getFiscal().save(ctx, mod123);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod123 saveComments(String domainName, String user, Mod123 mod123) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod123.getDomain(),user);
			return getFiscal().saveComments(ctx, mod123);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Mod123 initializeForFinish(String domainName, String user, Mod123 mod123) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod123.getDomain(),user);
			return getFiscal().initializeForFinish(ctx, mod123);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod123 markAsFinished(String domainName, String user, Mod123 mod123) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod123.getDomain(),user);
			return getFiscal().markAsFinished(ctx, mod123);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod123 markAsSent(String domainName, String user, Mod123 mod123) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod123.getDomain(),user);
			return getFiscal().markAsSent(ctx, mod123);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod123 markAsPending(String domainName, String user, Mod123 mod123) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod123.getDomain(),user);
			return getFiscal().markAsPending(ctx, mod123);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void deleteMod123(String domainName, String user, Mod123 mod123) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod123.getDomain(),user);
			getFiscal().delete(ctx, mod123);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod123 initializeMod123(String domainName, int domain, String user,Mod123 mod123) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,user);
			return getFiscal().initialize(ctx, mod123);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod123 createMod123(String domainName, int domain, String user,Mod123 mod123) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,user);
			return getFiscal().create(ctx, mod123);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static String getMod123Info(String domainName, int domain, String user, Mod123 mod123, IModelScript<Mod123Key> script,
			FiscalModelKeyInfo infoKey) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,user);
			return getFiscal().getInfo(ctx, mod123, script, infoKey);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// ----------------------------------MODELO 131
	public static LinkedList<Mod131> getMod131s(String domainName,int domainId, String user) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId,user);
			return getFiscal().getMod131s(ctx, domainId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod131 getMod131(String domainName, int domainId, String user, int id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId,user);
			return getFiscal().getMod131(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod131 calculate(String domainName, String user, Mod131 mod131) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod131.getDomain(),user);
			return getFiscal().calculate(ctx, mod131);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod131Activity calculate(String domainName, int domain, String userLogin, Mod131Activity activity) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,userLogin);
			return getFiscal().calculateActivity(ctx, activity);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod131 save(String domainName, String user, Mod131 mod131) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod131.getDomain(),user);
			return getFiscal().save(ctx, mod131);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod131 saveComments(String domainName, String user, Mod131 mod131) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod131.getDomain(),user);
			return getFiscal().saveComments(ctx, mod131);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Mod131 initializeForFinish(String domainName, String user, Mod131 mod131) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod131.getDomain(),user);
			return getFiscal().initializeForFinish(ctx, mod131);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod131 finish(String domainName, String user, Mod131 mod131) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod131.getDomain(),user);
			return getFiscal().markAsFinished(ctx, mod131);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	public static Mod131 markAsSent(String domainName, String userLogin, Mod131 mod131) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod131.getDomain(),userLogin);
			return getFiscal().markAsSent(ctx, mod131);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod131 reopen(String domainName, String user, Mod131 mod131) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod131.getDomain(),user);
			return getFiscal().markAsPending(ctx, mod131);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void deleteMod131(String domainName, String user, Mod131 mod131) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod131.getDomain(),user);
			getFiscal().delete(ctx, mod131);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod131 initializeMod131(String domainName, int domain, String user,Mod131 mod131) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,user);
			return getFiscal().initialize(ctx, mod131);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod131 createMod131(String domainName, int domain, String user,Mod131 mod131) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,user);
			return getFiscal().create(ctx, mod131);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static String getMod131Info(String domainName, int domain, String user, Mod131 mod131, IModelScript<Mod131Key> script,
			FiscalModelKeyInfo infoKey) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,user);
			return getFiscal().getInfo(ctx, mod131, script, infoKey);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// ----------------------------------MODELO 130
	public static LinkedList<Mod130> getMod130s(String domainName,int domainId, String user) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId,user);
			return getFiscal().getMod130s(ctx, domainId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod130 getMod130(String domainName, int domainId, String user, int id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId,user);
			return getFiscal().getMod130(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod130 calculate(String domainName, String user, Mod130 mod130) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod130.getDomain(),user);
			return getFiscal().calculate(ctx, mod130);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod130 save(String domainName, String user, Mod130 mod130) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod130.getDomain(),user);
			return getFiscal().save(ctx, mod130);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod130 saveComments(String domainName, String user, Mod130 mod130) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod130.getDomain(),user);
			return getFiscal().saveComments(ctx, mod130);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Mod130 initializeForFinish(String domainName, String user, Mod130 mod130) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod130.getDomain(),user);
			return getFiscal().initializeForFinish(ctx, mod130);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod130 markAsFinished(String domainName, String user, Mod130 mod130) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod130.getDomain(),user);
			return getFiscal().markAsFinished(ctx, mod130);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod130 markAsSent(String domainName, String userLogin, Mod130 mod130) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod130.getDomain(),userLogin);
			return getFiscal().markAsSent(ctx, mod130);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod130 markAsPending(String domainName, String user, Mod130 mod130) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod130.getDomain(),user);
			return getFiscal().markAsPending(ctx, mod130);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void deleteMod130(String domainName, String user, Mod130 mod130) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod130.getDomain(),user);
			getFiscal().delete(ctx, mod130);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod130 initializeMod130(String domainName, int domain, String user,Mod130 mod130) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,user);
			return getFiscal().initialize(ctx, mod130);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod130 createMod130(String domainName, int domain, String user,Mod130 mod130) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,user);
			return getFiscal().create(ctx, mod130);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static String getMod130Info(String domainName, int domain, String user, Mod130 mod130, IModelScript<Mod130Key> script,
			FiscalModelKeyInfo infoKey) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,user);
			return getFiscal().getInfo(ctx, mod130, script, infoKey);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// ----------------------------------MODELO 180
	public static LinkedList<Mod180> getMod180s(String domainName, int domainId,
			String user) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getFiscal().getMod180s(ctx, domainId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod180 getMod180(String domainName, int domainId, String user,
			Integer id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getFiscal().getMod180(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod180 initializeMod180(String domainName, int domainId,
			String user, Integer year) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getFiscal().initializeMod180(ctx, year);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod180 saveMod180(String domainName, int domainId,
			String user, Mod180 mod180) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getFiscal().saveMod180(ctx, mod180);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void deleteMod180(String domainName, int domainId,
			String user, Mod180 mod180) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			getFiscal().deleteMod180(ctx, mod180);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod180Detail getMod180Detail(String domainName, int domainId,
			String user, Integer id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getFiscal().getMod180Detail(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	public static Mod180 saveComments(String domainName, String user, Mod180 mod180) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod180.getDomain(),user);
			return getFiscal().saveCommentsMod180(ctx, mod180);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod180 changeStatusMod180(String domainName, String user, Mod180 mod180, FiscalStatus newStatus) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod180.getDomain(),user);
			return getFiscal().changeStatusMod180(ctx, mod180, newStatus);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Mod180 duplicateNextYearMod180(String domainName, Integer domain, String userLogin, Integer id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,userLogin);
			return getFiscal().duplicateNextYearMod180(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// ----------------------------------MODELO 190
	public static LinkedList<Mod190> getMod190s(String domainName, int domainId,
			String user) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getFiscal().getMod190s(ctx, domainId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod190 getMod190(String domainName, int domainId, String user,
			Integer id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getFiscal().getMod190(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod190 initializeMod190(String domainName, int domainId,
			String user, Integer year) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getFiscal().initializeMod190(ctx, year);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod190 saveMod190(String domainName, int domainId,
			String user, Mod190 mod190) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getFiscal().saveMod190(ctx, mod190);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void deleteMod190(String domainName, int domainId,
			String user, Mod190 mod190) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			getFiscal().deleteMod190(ctx, mod190);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod190Detail getMod190Detail(String domainName, int domainId,
			String user, Integer id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getFiscal().getMod190Detail(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod190 saveComments(String domainName, String user, Mod190 mod190) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod190.getDomain(),user);
			return getFiscal().saveCommentsMod190(ctx, mod190);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod190 changeStatusMod190(String domainName, String user, Mod190 mod190, FiscalStatus newStatus) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod190.getDomain(),user);
			return getFiscal().changeStatusMod190(ctx, mod190, newStatus);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Mod190 duplicateNextYearMod190(String domainName, Integer domain, String userLogin, Integer id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,userLogin);
			return getFiscal().duplicateNextYearMod190(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// ----------------------------------MODELO 193
	public static LinkedList<Mod193> getMod193s(String domainName, int domainId,
			String user) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getFiscal().getMod193s(ctx, domainId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod193 getMod193(String domainName, int domainId, String user,
			Integer id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getFiscal().getMod193(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod193 initializeMod193(String domainName, int domainId,
			String user, Integer year) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getFiscal().initializeMod193(ctx, year);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod193 saveMod193(String domainName, int domainId,
			String user, Mod193 mod193) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getFiscal().saveMod193(ctx, mod193);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void deleteMod193(String domainName, int domainId,
			String user, Mod193 mod193) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			getFiscal().deleteMod193(ctx, mod193);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod193 saveComments(String domainName, String user, Mod193 mod193) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod193.getDomain(),user);
			return getFiscal().saveCommentsMod193(ctx, mod193);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod193 changeStatusMod193(String domainName, String user, Mod193 mod193, FiscalStatus newStatus) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod193.getDomain(),user);
			return getFiscal().changeStatusMod193(ctx, mod193, newStatus);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	public static Mod193 duplicateNextYearMod193(String domainName, Integer domain, String userLogin, Integer id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,userLogin);
			return getFiscal().duplicateNextYearMod193(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	// ----------------------------------MODELO 184
	public static LinkedList<Mod184> getMod184s(String domainName, int domainId,
			String user) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getFiscal().getMod184s(ctx, domainId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod184 getMod184(String domainName, int domainId, String user,
			Integer id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getFiscal().getMod184(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod184 initializeMod184(String domainName, int domainId,
			String user, Integer year) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getFiscal().initializeMod184(ctx, year);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod184 saveMod184(String domainName, int domainId,
			String user, Mod184 mod184) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getFiscal().saveMod184(ctx, mod184);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void deleteMod184(String domainName, int domainId,
			String user, Mod184 mod184) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			getFiscal().deleteMod184(ctx, mod184);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod184 saveComments(String domainName, String user, Mod184 mod184) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod184.getDomain(),user);
			return getFiscal().saveCommentsMod184(ctx, mod184);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod184 changeStatusMod184(String domainName, String user, Mod184 mod184, FiscalStatus newStatus) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod184.getDomain(),user);
			return getFiscal().changeStatusMod184(ctx, mod184, newStatus);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Mod184 duplicateNextYearMod184(String domainName, Integer domain, String userLogin, Integer id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,userLogin);
			return getFiscal().duplicateNextYearMod184(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	// ----------------------------------MODELO 202
	public static LinkedList<Mod202> getMod202s(String domainName,int domainId, String user) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId,user);
			return getFiscal().getMod202s(ctx, domainId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod202 getMod202(String domainName, int domainId, String user, int id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId,user);
			return getFiscal().getMod202(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod202 calculate(String domainName, String user, Mod202 mod202) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod202.getDomain(),user);
			return getFiscal().calculate(ctx, mod202);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod202 save(String domainName, String user, Mod202 mod202) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod202.getDomain(),user);
			return getFiscal().save(ctx, mod202);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void deleteMod202(String domainName, String user, Mod202 mod202) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod202.getDomain(),user);
			getFiscal().delete(ctx, mod202);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod202 initializeMod202(String domainName, int domain, String user,Mod202 mod202) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,user);
			return getFiscal().initialize(ctx, mod202);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Mod202 saveComments(String domainName, String user, Mod202 mod202) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod202.getDomain(),user);
			return getFiscal().saveComments(ctx, mod202);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Mod202 initializeForFinish(String domainName, String user, Mod202 mod202) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod202.getDomain(),user);
			return getFiscal().initializeForFinish(ctx, mod202);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod202 markAsFinished(String domainName, String user, Mod202 mod202) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod202.getDomain(),user);
			return getFiscal().markAsFinished(ctx, mod202);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod202 markAsSent(String domainName, String userLogin, Mod202 mod202) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod202.getDomain(),userLogin);
			return getFiscal().markAsSent(ctx, mod202);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod202 markAsPending(String domainName, String user, Mod202 mod202) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod202.getDomain(),user);
			return getFiscal().markAsPending(ctx, mod202);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod202 createMod202(String domainName, int domain, String user,Mod202 mod202) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,user);
			return getFiscal().create(ctx, mod202);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static String getMod202Info(String domainName, int domain, String user, Mod202 mod202
			,IModelScript<Mod202Key> script,FiscalModelKeyInfo infoKey) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,user);
			return getFiscal().getInfo(ctx, mod202, script, infoKey);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// ----------------------------------MODELO 200
	public static LinkedList<Mod200> getMod200s(String domainName,int domainId, String user) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId,user);
			return getFiscal().getMod200s(ctx, domainId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	// ----------------------------------MODELO 200 - 2013
	public static Mod2002013 createMod2002013(String domainName, int domain,String login,
			int year) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,login);
			return getFiscal().createMod2002013(ctx, year);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod2002013 initializeNewMod2002013(String domainName,
			int domain, String login,Mod2002013 mod200) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, login);
			return getFiscal().initializeNewMod2002013(ctx, mod200);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod2002013 initializeMod2002013(String domainName, int domain,String login,
			Mod2002013 mod200) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, login);
			return getFiscal().initializeMod2002013(ctx, mod200);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod2002013 getMod2002013ByYear(String domainName, int domain,String login,
			int year) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, login);
			return getFiscal().getMod2002013ByYear(ctx, year);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod2002013 getMod2002013ById(String domainName, int domain,String login,
			int id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, login);
			return getFiscal().getMod2002013ById(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod2002013 calculateMod2002013(Mod2002013 mod200) {
		return getFiscal().calculateMod2002013(mod200);
	}

	public static Mod2002013 validateMod2002013(Mod2002013 mod200) {
		return getFiscal().validateMod2002013(mod200);
	}

	public static Mod2002013 saveMod2002013(String domainName, int domain,String login,
			Mod2002013 mod200) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, login);
			return getFiscal().saveMod2002013(ctx, mod200);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void deleteMod2002013(String domainName, int domain,String login, int id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, login);
			getFiscal().deleteMod2002013(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static String dumpAEATMod2002013(Mod2002013 mod200) {
		return getFiscal().dumpAEATMod2002013(mod200);
	}

	// ----------------------------------MODELO 200 - 2014
	public static Mod2002014 createMod2002014(String domainName, int domain,String login,
			int year) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,login);
			return getFiscal().createMod2002014(ctx, year);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod2002014 initializeNewMod2002014(String domainName,
			int domain,String login, Mod2002014 mod200) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,login);
			return getFiscal().initializeNewMod2002014(ctx, mod200);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod2002014 initializeMod2002014(String domainName, int domain,String login,
			Mod2002014 mod200) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,login);
			return getFiscal().initializeMod2002014(ctx, mod200);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod2002014 getMod2002014ByYear(String domainName, int domain,String login,
			int year) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,login);
			return getFiscal().getMod2002014ByYear(ctx, year);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod2002014 getMod2002014ById(String domainName, int domain,String login,
			int id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,login);
			return getFiscal().getMod2002014ById(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod2002014 calculateMod2002014(Mod2002014 mod200) {
		return getFiscal().calculateMod2002014(mod200);
	}

	public static Mod2002014 validateMod2002014(Mod2002014 mod200) {
		return getFiscal().validateMod2002014(mod200);
	}

	public static Mod2002014 saveMod2002014(String domainName, int domain,String login,
			Mod2002014 mod200) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,login);
			return getFiscal().saveMod2002014(ctx, mod200);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void deleteMod2002014(String domainName, int domain,String login, int id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,login);
			getFiscal().deleteMod2002014(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static String dumpAEATMod2002014(Mod2002014 mod200) {
		return getFiscal().dumpAEATMod2002014(mod200);
	}

	public static Mod2002014 importMod2002013(String domainName, int domain,String login,
			Mod2002014 mod200) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, login);
			return getFiscal().importMod2002013(ctx, mod200);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// ----------------------------------MODELO 200 - 2015
	public static Mod2002015 createMod2002015(String domainName, int domain,String login,
			int year) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,login);
			return getFiscal().createMod2002015(ctx, year);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod2002015 initializeNewMod2002015(String domainName,
			int domain,String login, Mod2002015 mod200) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,login);
			return getFiscal().initializeNewMod2002015(ctx, mod200);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod2002015 initializeMod2002015(String domainName, int domain,String login,
			Mod2002015 mod200) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,login);
			return getFiscal().initializeMod2002015(ctx, mod200);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod2002015 getMod2002015ByYear(String domainName, int domain,String login,
			int year) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,login);
			return getFiscal().getMod2002015ByYear(ctx, year);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod2002015 getMod2002015ById(String domainName, int domain,String login,
			int id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,login);
			return getFiscal().getMod2002015ById(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod2002015 calculateMod2002015(Mod2002015 mod200) {
		return getFiscal().calculateMod2002015(mod200);
	}

	public static Mod2002015 validateMod2002015(Mod2002015 mod200) {
		return getFiscal().validateMod2002015(mod200);
	}

	public static Mod2002015 saveMod2002015(String domainName, int domain,String login,
			Mod2002015 mod200) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,login);
			return getFiscal().saveMod2002015(ctx, mod200);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void deleteMod2002015(String domainName, int domain,String login, int id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,login);
			getFiscal().deleteMod2002015(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static String dumpAEATMod2002015(Mod2002015 mod200) {
		return getFiscal().dumpAEATMod2002015(mod200);
	}

	public static Mod2002015 importMod2002014(String domainName, int domain,String login,
			Mod2002015 mod200) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, login);
			return getFiscal().importMod2002014(ctx, mod200);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// ----------------------------------MODELO 200 - 2016
	public static Mod2002016 createMod2002016(String domainName, int domain,String login,
			int year) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,login);
			return getFiscal().createMod2002016(ctx, year);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod2002016 initializeNewMod2002016(String domainName,
			int domain,String login, Mod2002016 mod200) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,login);
			return getFiscal().initializeNewMod2002016(ctx, mod200);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod2002016 initializeMod2002016(String domainName, int domain,String login,
			Mod2002016 mod200) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,login);
			return getFiscal().initializeMod2002016(ctx, mod200);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod2002016 getMod2002016ByYear(String domainName, int domain,String login,
			int year) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,login);
			return getFiscal().getMod2002016ByYear(ctx, year);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod2002016 getMod2002016ById(String domainName, int domain,String login,
			int id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,login);
			return getFiscal().getMod2002016ById(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod2002016 calculateMod2002016(Mod2002016 mod200) {
		return getFiscal().calculateMod2002016(mod200);
	}

	public static Mod2002016 validateMod2002016(Mod2002016 mod200) {
		return getFiscal().validateMod2002016(mod200);
	}

	public static Mod2002016 saveMod2002016(String domainName, int domain,String login,
			Mod2002016 mod200) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,login);
			return getFiscal().saveMod2002016(ctx, mod200);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void deleteMod2002016(String domainName, int domain,String login, int id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,login);
			getFiscal().deleteMod2002016(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static String dumpAEATMod2002016(Mod2002016 mod200) {
		return getFiscal().dumpAEATMod2002016(mod200);
	}

	public static Mod2002016 importMod2002015(String domainName, int domain,String login,
			Mod2002016 mod200) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, login);
			return getFiscal().importMod2002015(ctx, mod200);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// ----------------------------------MODELO 200 - 2017
	public static Mod2002017 createMod2002017(String domainName, int domain,String login,
			int year) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,login);
			return getFiscal().createMod2002017(ctx, year);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod2002017 initializeNewMod2002017(String domainName,
			int domain,String login, Mod2002017 mod200) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,login);
			return getFiscal().initializeNewMod2002017(ctx, mod200);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod2002017 initializeMod2002017(String domainName, int domain,String login,
			Mod2002017 mod200) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,login);
			return getFiscal().initializeMod2002017(ctx, mod200);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod2002017 getMod2002017ByYear(String domainName, int domain,String login,
			int year) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,login);
			return getFiscal().getMod2002017ByYear(ctx, year);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod2002017 getMod2002017ById(String domainName, int domain,String login,
			int id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,login);
			return getFiscal().getMod2002017ById(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod2002017 calculateMod2002017(Mod2002017 mod200) {
		return getFiscal().calculateMod2002017(mod200);
	}

	public static Mod2002017 validateMod2002017(Mod2002017 mod200) {
		return getFiscal().validateMod2002017(mod200);
	}

	public static Mod2002017 saveMod2002017(String domainName, int domain,String login,
			Mod2002017 mod200) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,login);
			return getFiscal().saveMod2002017(ctx, mod200);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void deleteMod2002017(String domainName, int domain,String login, int id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,login);
			getFiscal().deleteMod2002017(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static String dumpAEATMod2002017(Mod2002017 mod200) {
		return getFiscal().dumpAEATMod2002017(mod200);
	}

	public static Mod2002017 importMod2002016(String domainName, int domain,String login,
			Mod2002017 mod200) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, login);
			return getFiscal().importMod2002016(ctx, mod200);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// ----------------------------------MODELO 390
	public static LinkedList<Mod390> getMod390s(String domainName, int domainId, String login) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getFiscal().getMod390s(ctx, domainId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	public static Mod390 initialize(String domainName, int domain, String login, int year) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, login);
			return getFiscal().initialize(ctx, year);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod390 create(String domainName, int domain, String login, Mod390 mod390) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, login);
			return getFiscal().create(ctx, mod390);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Mod390 saveComments(String domainName, int domain, String login, Mod390 mod390) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, login);
			return getFiscal().saveComments(ctx, mod390);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	public static Mod3902015 changeStatusMod3902015(String domainName, String userLogin, Mod3902015 mod390, FiscalStatus status) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod390.getDomain(), userLogin);
			return getFiscal().changeStatusMod3902015(ctx, mod390,  status);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// ----------------------------------MODELO 390 - 2014
	public static Mod3902014 getMod3902014(String domainName, int domainId,
			String login, Mod390 mod390) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getFiscal().getMod3902014(ctx, mod390);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod3902014 getMod3902014(String domainName, int domainId,
			String login, int id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getFiscal().getMod3902014(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static String getMod3902014XML(String domainName, int domainId,
			String login, int id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getFiscal().getMod3902014XML(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod3902014 saveMod3902014(String domainName, int domainId,
			String login, Mod3902014 mod390) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getFiscal().saveMod3902014(ctx, mod390);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void deleteMod3902014(String domainName, int domainId,
			String login, Mod3902014 mod390) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getFiscal().deleteMod3902014(ctx, mod390);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// ----------------------------------MODELO 390 - 2015
	public static Mod3902015 getMod3902015(String domainName, int domainId,
			String login, Integer id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getFiscal().getMod3902015(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod3902015 getMod3902015(String domainName, int domainId,
			String login, Mod390 mod390) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getFiscal().getMod3902015(ctx, mod390);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static String getMod3902015XML(String domainName, int domainId,
			String login, int id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getFiscal().getMod3902015XML(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod3902015 saveMod3902015(String domainName, int domainId,
			String login, Mod3902015 mod390) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			return getFiscal().saveMod3902015(ctx, mod390);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void deleteMod3902015(String domainName, int domainId,
			String login, Mod3902015 mod390) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			getFiscal().deleteMod3902015(ctx, mod390);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Stream<VatSummaryContext> getVatSummaryContext(String domainName, int domainId, String user, VatParams params) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getFiscal().getVatSummaryContext(ctx, params);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	public static Stream<VatContext> getVatContext(String domainName, int domainId, String user, VatParams params) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getFiscal().getVatContext(ctx, params);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Stream<VatContext> getSiiVatContext(String domainName, int domainId, String user, VatParams params, String sii) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getFiscal().getSiiVatContext(ctx, params, sii);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void importMod303(String domainName, int domain, String userLogin) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, userLogin);
			getFiscal().importMod303(ctx, domain);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	// ----------------------------------MODELO 349
	public static LinkedList<Mod349> getMod349s(String domainName, int domainId,
			String user) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getFiscal().getMod349s(ctx, domainId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod349 getMod349(String domainName, int domainId, String user,
			Integer id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getFiscal().getMod349(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod349 initializeMod349(String domainName, int domainId,
			String user) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getFiscal().initializeMod349(ctx);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod349 saveMod349(String domainName, int domainId,
			String user, Mod349 mod349) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getFiscal().saveMod349(ctx, mod349);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void deleteMod349(String domainName, int domainId,
			String user, Mod349 mod349) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			getFiscal().deleteMod349(ctx, mod349);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod349Detail getMod349Detail(String domainName, int domainId,
			String user, Integer id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user);
			return getFiscal().getMod349Detail(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static Mod349 saveComments(String domainName, String user, Mod349 mod349) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod349.getDomain(),user);
			return getFiscal().saveCommentsMod349(ctx, mod349);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod349 changeStatusMod349(String domainName, String user, Mod349 mod349, FiscalStatus newStatus) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod349.getDomain(),user);
			return getFiscal().changeStatusMod349(ctx, mod349, newStatus);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static String getMod349Info(String domainName, int domain, String user, Mod349 mod349, Mod349Detail detail, FiscalModelKeyInfo infoKey) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,user);
			return getFiscal().getMod349Info(ctx, mod349, detail, infoKey);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	// ----------------------------------MODELO 347
		public static LinkedList<Mod347> getMod347s(String domainName, int domainId,
				String user) {
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domainName, domainId, user);
				return getFiscal().getMod347s(ctx, domainId);
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}

		public static Mod347 getMod347(String domainName, int domainId, String user,
				Integer id) {
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domainName, domainId, user);
				return getFiscal().getMod347(ctx, id);
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}

		public static Mod347 initializeMod347(String domainName, int domainId,
				String user) {
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domainName, domainId, user);
				return getFiscal().initializeMod347(ctx);
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}

		public static Mod347 saveMod347(String domainName, int domainId,
				String user, Mod347 mod347) {
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domainName, domainId, user);
				return getFiscal().saveMod347(ctx, mod347);
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}

		public static void deleteMod347(String domainName, int domainId,
				String user, Mod347 mod347) {
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domainName, domainId, user);
				getFiscal().deleteMod347(ctx, mod347);
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
		public static Mod347 saveComments(String domainName, String user, Mod347 mod347) {
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domainName, mod347.getDomain(),user);
				return getFiscal().saveCommentsMod347(ctx, mod347);
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}

		public static Mod347 changeStatusMod347(String domainName, String user, Mod347 mod347, FiscalStatus newStatus) {
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domainName, mod347.getDomain(),user);
				return getFiscal().changeStatusMod347(ctx, mod347, newStatus);
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
		public static String getMod347Info(String domainName, int domain, String user, Mod347 mod347, Mod347Declared declared, FiscalModelKeyInfo infoKey) {
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domainName, domain,user);
				return getFiscal().getMod347Info(ctx, mod347, declared, infoKey);
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
		public static Mod347 duplicateNextYearMod347(String domainName, Integer domain, String userLogin, Integer id) {
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domainName, domain,userLogin);
				return getFiscal().duplicateNextYearMod347(ctx, id);
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
		public static Stream<IrpfBreakdown> getIrpfBreakdownSummary(String domainName, String user, int domain,IRPFParams params) {
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domainName, domain, user);
				return getFiscal().getIrpfBreakdownSummary(ctx, params);
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}

		public static Stream<IrpfBreakdown> getIrpfBreakdown(String domainName, String user, int domain,IRPFParams params) {
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domainName, domain, user);
				return getFiscal().getIrpfBreakdown(ctx, params);
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}

		public static Stream<OperationBreakdown> getOperationBreakdown(String domainName, String user, int domain, OperationParams params) {
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domainName, domain, user);
				return getFiscal().getOperationBreakdown(ctx, domain, params);
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		
		
}
