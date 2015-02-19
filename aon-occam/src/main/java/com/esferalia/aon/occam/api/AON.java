package com.esferalia.aon.occam.api;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.function.Function;

import org.jooq.Condition;
import org.jooq.lambda.Seq;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.Agreement;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.SalaryAccountEntry;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.Mod180Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
import com.esferalia.aon.occam.api.model.fiscal.Mod193Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.Mod390.Mod390Detail;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.impl.jooq.AccountingImpl;
import com.esferalia.aon.occam.impl.jooq.AgreementImpl;
import com.esferalia.aon.occam.impl.jooq.CommonImpl;
import com.esferalia.aon.occam.impl.jooq.FiscalImpl;
import com.esferalia.aon.occam.impl.jooq.SalaryImpl;
import com.esferalia.aon.watson.error.AonCoreException;

public class AON {

	private static ICommon getCommon() {
		return new CommonImpl();
	}

	private static IAccounting getAccounting() {
		return new AccountingImpl();
	}

	private static ISalary getSalary() {
		return new SalaryImpl();
	}
	
	private static IFiscal getFiscal() {
		return new FiscalImpl();
	}

	private static IAgreement getAgreement() {
		return new AgreementImpl();
	}

	// ********************************************
	// ********************************** COMMON **
	// ********************************************

	// --------------------- APPLICATION PARAMETERS
	public static ApplicationParameter fetchApplicationParameter(
			AONContext ctx, AppParam param) {
		return getCommon().fetchOne(ctx, param);
	}

	// ********************************************
	// ****************************** ACCOUNTING **
	// ********************************************

	// ------------------------------------ ACCOUNT
	public static Account fetchAccount(AONContext ctx, Integer accountId) {
		return getAccounting().fetchAccount(ctx, accountId);
	}

	// ----------------------------- ACCOUNT PERIOD
	public static AccountPeriod fetchPeriod(AONContext ctx, Date date) {
		return getAccounting().fetchPeriod(ctx, date);
	}

	public static AccountPeriod fetchPeriod(AONContext ctx, Integer id) {
		return getAccounting().fetchPeriod(ctx, id);
	}

	public static AccountPeriod fetchPeriod(AONContext ctx, Condition condition) {
		return getAccounting().fetchPeriod(ctx, condition);
	}

	public static void insert(AONContext ctx, AccountPeriod ap) {
		getAccounting().insert(ctx, ap);
	}

	public static void update(AONContext ctx, AccountPeriod ap) {
		getAccounting().update(ctx, ap);
	}

	public static void delete(AONContext ctx, AccountPeriod ap) {
		getAccounting().delete(ctx, ap);
	}

	// ------------------------------ ACCOUNT ENTRY
	public static AccountEntry fetchOneAccountEntry(AONContext ctx,
			Condition condition) {
		return getAccounting().fetchOneAccountEntry(ctx, condition);
	}

	public static Seq<AccountEntry> fetchAccountEntry(AONContext ctx,
			Condition condition, int offset, int numberOfRows) {
		return getAccounting().fetchAccountEntry(ctx, condition, offset,
				numberOfRows);
	}

	public static Seq<AccountEntry> fetchAccountEntry(AONContext ctx,
			Condition condition, int offset, int numberOfRows,
			Function<ResultSet, AccountEntry> function) {
		return getAccounting().fetchAccountEntry(ctx, condition, offset,
				numberOfRows, function);
	}

	public static String fetchAccountEntryCSV(AONContext ctx,
			Condition condition, int offset, int numberOfRows) {
		return getAccounting().fetchAccountEntryCSV(ctx, condition, offset,
				numberOfRows);
	}

	public static boolean existsAnyEntry(AONContext ctx, Integer period,
			AccountEntryType accountEntryType) {
		return getAccounting().existsAnyEntry(ctx, period, accountEntryType);
	}

	public static void insert(AONContext ctx, AccountEntry ae) {
		getAccounting().insert(ctx, ae);
	}

	public static void update(AONContext ctx, AccountEntry ae) {
		getAccounting().update(ctx, ae);
	}

	public static void delete(AONContext ctx, AccountEntry ae) {
		getAccounting().delete(ctx, ae);
	}

	/**
	 * Inserta en el borrador contable un apunte de nóminas con los datos leídos
	 * desde nóminas:
	 * 
	 * @param ctx
	 *            Contexto de AON
	 * @param enterprise
	 *            Código de empresa
	 * @param from
	 *            Fecha desde la cual leer las nóminas.
	 * @param to
	 *            Fecha hasta la cual leer las nóminas.
	 * @param concept
	 *            Concepto que aparecerá en el apunte contable. Si el valor es
	 *            NULL, entonces "NÓMINAS" a piñon fijo.
	 * @param registryBank
	 *            Código del banco de la empresa por la que se pagarán las
	 *            nóminas, Si el valor es NULL la partida se destinará a
	 *            "Remuneraciones pendientes de pago"
	 * @return El apunte contable grabado.
	 */
	public static AccountEntry insertSalaryEntry(AONContext ctx,
			Integer enterprise, Date from, Date to, String concept,
			Integer registryBank) {
		return getAccounting().insertSalaryEntry(
				ctx,
				getSalary().getSalaryAccountEntry(ctx, enterprise, from, to,
						concept, registryBank));
	}

	public static AccountEntry insertSalaryEntry(AONContext ctx,
			SalaryAccountEntry sae) {
		return getAccounting().insertSalaryEntry(ctx, sae);
	}

	// ********************************************
	// ********************************** FISCAL **
	// ********************************************

	// ----------------------------------MODELO 180
	public static ArrayList<Mod180> getMod180s(String domainName, int domainId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().getMod180s(ctx,domainId);	
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	public static Mod180 getMod180(String domainName, int domainId,Integer id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().getMod180(ctx,id);
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	public static Mod180 initializeMod180(String domainName, int domainId,Integer year) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().initializeMod180(ctx, year);
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	public static Mod180 saveMod180(String domainName, int domainId,Mod180 mod180) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().saveMod180(ctx, mod180);
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	public static void deleteMod180(String domainName, int domainId,Mod180 mod180) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			getFiscal().deleteMod180(ctx, mod180);
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	public static Mod180Detail getMod180Detail(String domainName, int domainId,Integer id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().getMod180Detail(ctx,id);
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	// ----------------------------------MODELO 190
	public static ArrayList<Mod190> getMod190s(String domainName, int domainId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().getMod190s(ctx,domainId);
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	public static Mod190 getMod190(String domainName, int domainId,Integer id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().getMod190(ctx,id);
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	public static Mod190 initializeMod190(String domainName, int domainId,Integer year) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().initializeMod190(ctx, year);
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	public static Mod190 saveMod190(String domainName, int domainId,Mod190 mod190) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().saveMod190(ctx, mod190);
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	public static void deleteMod190(String domainName, int domainId,Mod190 mod190) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			getFiscal().deleteMod190(ctx, mod190);
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	public static Mod190Detail getMod190Detail(String domainName, int domainId,Integer id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().getMod190Detail(ctx,id);
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	// ----------------------------------MODELO 193
	public static ArrayList<Mod193> getMod193s(String domainName, int domainId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().getMod193s(ctx,domainId);
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	public static Mod193 getMod193(String domainName, int domainId,Integer id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().getMod193(ctx,id);
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	public static Mod193 initializeMod193(String domainName, int domainId,Integer year) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().initializeMod193(ctx, year);
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	public static Mod193 saveMod193(String domainName, int domainId,Mod193 mod193) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().saveMod193(ctx, mod193);
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	public static void deleteMod193(String domainName, int domainId,Mod193 mod193) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			getFiscal().deleteMod193(ctx, mod193);
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	public static Mod193Detail getMod193Detail(String domainName, int domainId,Integer id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().getMod193Detail(ctx,id);
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	
	// ----------------------------------MODELO 184
	public static ArrayList<Mod184> getMod184s(String domainName, int domainId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().getMod184s(ctx,domainId);
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	public static Mod184 getMod184(String domainName, int domainId,Integer id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().getMod184(ctx,id);
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	public static Mod184 initializeMod184(String domainName, int domainId,Integer year) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().initializeMod184(ctx, year);
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	public static Mod184 saveMod184(String domainName, int domainId,Mod184 mod184) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().saveMod184(ctx, mod184);
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	public static void deleteMod184(String domainName, int domainId,Mod184 mod184) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			getFiscal().deleteMod184(ctx, mod184);
		} finally {
			if (ctx != null) ctx.close();	
		}
	}

	// ----------------------------------MODELO 390
	public static ArrayList<Mod390> getMod390s(String domainName, int domainId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().getMod390s(ctx,domainId);
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	public static Mod390 getMod390(String domainName, int domainId, int id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().getMod390(ctx,id);
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	public static String getMod390XML(String domainName, int domainId, int id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().getMod390XML(ctx,id);
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	public static Mod390 initializeMod390(String domainName, int domainId,Integer year) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().initializeMod390(ctx, year);
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	public static Mod390 saveMod390(String domainName, int domainId,Mod390 mod390) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().saveMod390(ctx, mod390);
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	public static void deleteMod390(String domainName, int domainId,Mod390 mod390) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			getFiscal().deleteMod390(ctx, mod390);
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	public static ArrayList<Mod390Detail> getMod390Details(String domainName,Integer domainId,Mod390 mod390) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().getMod390Details(ctx, mod390);
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	
	// 
	public static void saveAgreement(AONContext ctx, Agreement ...agreements) throws AonCoreException {
		getAgreement().save(ctx, agreements);
	}
}
