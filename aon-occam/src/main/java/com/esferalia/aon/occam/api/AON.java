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
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Mod180;
import com.esferalia.aon.occam.api.model.Mod180Detail;
import com.esferalia.aon.occam.api.model.Mod190;
import com.esferalia.aon.occam.api.model.Mod190Detail;
import com.esferalia.aon.occam.api.model.SalaryAccountEntry;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.impl.jooq.AccountingImpl;
import com.esferalia.aon.occam.impl.jooq.CommonImpl;
import com.esferalia.aon.occam.impl.jooq.FiscalImpl;
import com.esferalia.aon.occam.impl.jooq.SalaryImpl;

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
		return getFiscal().getMod180s(AONContext.getAONContext(domainName, domainId),domainId);
	}
	public static Mod180 getMod180(String domainName, int domainId,Integer id) {
		return getFiscal().getMod180(AONContext.getAONContext(domainName, domainId),id);
	}
	
	public static Mod180 saveMod180(String domainName, int domainId,Mod180 mod180) {
		return getFiscal().saveMod180(AONContext.getAONContext(domainName, domainId), mod180);
	}
	public static void deleteMod180(String domainName, int domainId,Mod180 mod180) {
		getFiscal().deleteMod180(AONContext.getAONContext(domainName, domainId), mod180);
	}
	public static Mod180Detail getMod180Detail(String domainName, int domainId,Integer id) {
		return getFiscal().getMod180Detail(AONContext.getAONContext(domainName, domainId),id);
	}
	// ----------------------------------MODELO 190
	public static ArrayList<Mod190> getMod190s(String domainName, int domainId) {
		return getFiscal().getMod190s(AONContext.getAONContext(domainName, domainId),domainId);
	}
	public static Mod190 getMod190(String domainName, int domainId,Integer id) {
		return getFiscal().getMod190(AONContext.getAONContext(domainName, domainId),id);
	}
	
	public static Mod190 saveMod190(String domainName, int domainId,Mod190 mod190) {
		return getFiscal().saveMod190(AONContext.getAONContext(domainName, domainId), mod190);
	}
	public static void deleteMod190(String domainName, int domainId,Mod190 mod190) {
		getFiscal().deleteMod190(AONContext.getAONContext(domainName, domainId), mod190);
	}
	public static Mod190Detail getMod190Detail(String domainName, int domainId,Integer id) {
		return getFiscal().getMod190Detail(AONContext.getAONContext(domainName, domainId),id);
	}
	
}
