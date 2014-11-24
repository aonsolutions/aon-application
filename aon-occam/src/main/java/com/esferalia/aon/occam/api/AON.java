package com.esferalia.aon.occam.api;

import java.sql.ResultSet;
import java.util.Date;
import java.util.function.Function;

import org.jooq.Condition;
import org.jooq.lambda.Seq;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.SalaryAccountEntry;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.impl.jooq.AccountingImpl;
import com.esferalia.aon.occam.impl.jooq.CommonImpl;
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

}
