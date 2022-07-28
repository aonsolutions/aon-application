package com.esferalia.aon.occam.mod200.api;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.mod200.api.model.mod200_2013.Mod2002013;
import com.esferalia.aon.occam.mod200.api.model.mod200_2014.Mod2002014;
import com.esferalia.aon.occam.mod200.api.model.mod200_2015.Mod2002015;
import com.esferalia.aon.occam.mod200.api.model.mod200_2016.Mod2002016;
import com.esferalia.aon.occam.mod200.api.model.mod200_2017.Mod2002017;
import com.esferalia.aon.occam.mod200.api.model.mod200_2018.Mod2002018;
import com.esferalia.aon.occam.mod200.api.model.mod200_2019.Mod2002019;
import com.esferalia.aon.occam.mod200.impl.jooq.FiscalImpl;

public class FISCAL {

	private static IFiscal getFiscal() {
		return new FiscalImpl();
	}
	
	// ----------------------------------MODELO 200 - 2013
	public static Mod2002013 createMod2002013(String domainName, int domain,String login,
			int year) {
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, login);
			return getFiscal().saveMod2002013(ctx, mod200);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void deleteMod2002013(String domainName, int domain,String login, int id) {
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,login);
			return getFiscal().saveMod2002014(ctx, mod200);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void deleteMod2002014(String domainName, int domain,String login, int id) {
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,login);
			return getFiscal().saveMod2002015(ctx, mod200);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void deleteMod2002015(String domainName, int domain,String login, int id) {
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,login);
			return getFiscal().saveMod2002016(ctx, mod200);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void deleteMod2002016(String domainName, int domain,String login, int id) {
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,login);
			return getFiscal().saveMod2002017(ctx, mod200);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void deleteMod2002017(String domainName, int domain,String login, int id) {
		CloseableAONContext ctx = null;
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
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, login);
			return getFiscal().importMod2002016(ctx, mod200);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	
	// ----------------------------------MODELO 200 - 2018
	public static Mod2002018 createMod2002018(String domainName, int domain,String login,
			int year) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,login);
			return getFiscal().createMod2002018(ctx, year);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod2002018 initializeNewMod2002018(String domainName,
			int domain,String login, Mod2002018 mod200) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,login);
			return getFiscal().initializeNewMod2002018(ctx, mod200);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod2002018 initializeMod2002018(String domainName, int domain,String login,
			Mod2002018 mod200) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,login);
			return getFiscal().initializeMod2002018(ctx, mod200);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod2002018 getMod2002018ByYear(String domainName, int domain,String login,
			int year) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,login);
			return getFiscal().getMod2002018ByYear(ctx, year);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod2002018 getMod2002018ById(String domainName, int domain,String login,
			int id) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,login);
			return getFiscal().getMod2002018ById(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod2002018 calculateMod2002018(Mod2002018 mod200) {
		return getFiscal().calculateMod2002018(mod200);
	}

	public static Mod2002018 validateMod2002018(Mod2002018 mod200) {
		return getFiscal().validateMod2002018(mod200);
	}

	public static Mod2002018 saveMod2002018(String domainName, int domain,String login,
			Mod2002018 mod200) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,login);
			return getFiscal().saveMod2002018(ctx, mod200);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void deleteMod2002018(String domainName, int domain,String login, int id) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,login);
			getFiscal().deleteMod2002018(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static String dumpAEATMod2002018(Mod2002018 mod200) {
		return getFiscal().dumpAEATMod2002018(mod200);
	}

	public static Mod2002018 importMod2002017(String domainName, int domain,String login,
			Mod2002018 mod200) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, login);
			return getFiscal().importMod2002017(ctx, mod200);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}	
	
	// ----------------------------------MODELO 200 - 2019
	public static Mod2002019 createMod2002019(String domainName, int domain,String login,
			int year) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,login);
			return getFiscal().createMod2002019(ctx, year);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod2002019 initializeNewMod2002019(String domainName,
			int domain,String login, Mod2002019 mod200) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,login);
			return getFiscal().initializeNewMod2002019(ctx, mod200);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod2002019 initializeMod2002019(String domainName, int domain,String login,
			Mod2002019 mod200) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,login);
			return getFiscal().initializeMod2002019(ctx, mod200);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod2002019 getMod2002019ByYear(String domainName, int domain,String login,
			int year) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,login);
			return getFiscal().getMod2002019ByYear(ctx, year);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod2002019 getMod2002019ById(String domainName, int domain,String login,
			int id) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,login);
			return getFiscal().getMod2002019ById(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod2002019 calculateMod2002019(Mod2002019 mod200) {
		return getFiscal().calculateMod2002019(mod200);
	}

	public static Mod2002019 validateMod2002019(Mod2002019 mod200) {
		return getFiscal().validateMod2002019(mod200);
	}

	public static Mod2002019 saveMod2002019(String domainName, int domain,String login,
			Mod2002019 mod200) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,login);
			return getFiscal().saveMod2002019(ctx, mod200);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void deleteMod2002019(String domainName, int domain,String login, int id) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain,login);
			getFiscal().deleteMod2002019(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static String dumpAEATMod2002019(Mod2002019 mod200) {
		return getFiscal().dumpAEATMod2002019(mod200);
	}

	public static Mod2002019 importMod2002018(String domainName, int domain,String login,
			Mod2002019 mod200) {
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain, login);
			return getFiscal().importMod2002018(ctx, mod200);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
}
