package com.esferalia.aon.occam.api;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.lambda.Seq;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.Agreement;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.Salary;
import com.esferalia.aon.occam.api.model.SalaryAccountEntry;
import com.esferalia.aon.occam.api.model.SalaryFilter;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivity;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelMatrix;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.Mod180Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
import com.esferalia.aon.occam.api.model.fiscal.Mod193Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.Mod390.Mod390Detail;
import com.esferalia.aon.occam.api.model.fiscal.modules.Modules2015.Epigraph;
import com.esferalia.aon.occam.api.model.management.OfferDetail;
import com.esferalia.aon.occam.api.model.management.OfferFilter;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.impl.jooq.AccountingImpl;
import com.esferalia.aon.occam.impl.jooq.AgreementImpl;
import com.esferalia.aon.occam.impl.jooq.CommonImpl;
import com.esferalia.aon.occam.impl.jooq.FinanceImpl;
import com.esferalia.aon.occam.impl.jooq.FiscalImpl;
import com.esferalia.aon.occam.impl.jooq.ManagementImpl;
import com.esferalia.aon.occam.impl.jooq.SalaryImpl;
import com.esferalia.aon.occam.impl.jooq.SecurityImpl;
import com.esferalia.aon.watson.error.AonCoreException;

public class AON {

	private static ISecurity getSecurity() {
		return new SecurityImpl();
	}

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

	private static IFinance getFinance() {
		return new FinanceImpl();
	}

	private static IManagement getManagement() {
		return new ManagementImpl();
	}

	private static IAgreement getAgreement() {
		return new AgreementImpl();
	}

	// ********************************************
	// ******************************** SECURITY **
	// ********************************************
	public static User getUser(String domainName, int domainId, String login) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getSecurity().getUser(ctx,login);
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	public static Integer[] getUserScopes(String domainName, int domainId,
			Integer userId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getSecurity().getUserScopes(ctx,userId);
		} finally {
			if (ctx != null) ctx.close();	
		}
	}

	// ********************************************
	// ********************************** COMMON **
	// ********************************************

	// --------------------- APPLICATION PARAMETERS
	public static FiscalParameters getFiscalParameters(String domainName, int domainId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getCommon().getFiscalParameters(ctx);
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	public static ApplicationParameter fetchApplicationParameter(
			AONContext ctx, AppParam param) {
		return getCommon().fetchOne(ctx, param);
	}

	// --------------------------------- ENTERPRISE
	public static ArrayList<Enterprise> getParentEnterprises(String domainName,
			int domain, String query) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain);
			return getCommon().getParentEnterprises(ctx, query);
		} finally {
			if (ctx != null) ctx.close();	
		}
	}	
	
	public static Enterprise getEnterprise(String domainName, int domain, int id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain);
			return getCommon().getEnterprise(ctx, id);
		} finally {
			if (ctx != null) ctx.close();	
		}
	}

	public static ArrayList<CompanyBank> getCompanyBanks(String domainName,
			int domain, int enterprise) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain);
			return getCommon().getCompanyBanks(ctx,enterprise);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static ArrayList<CompanyBank> getCompanyBanks(String domainName,
			int domain) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain);
			return getCommon().getCompanyBanks(ctx);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// ------------------------------------ PRODUCT
	public static List<String> getProductTags(String domainName, int domainId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getCommon().getProductTags(ctx);
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	public static Map<Integer,String[]> getProductTagMap(String domainName, int domainId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getCommon().getProductTagMap(ctx);
		} finally {
			if (ctx != null) ctx.close();	
		}
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

	// -------------------------- FISCAL PANEL
	public static FiscalModelMatrix getFiscalPanel(String domainName,
			int domain, int year, String login) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain);
			User user = getUser(domainName, domain, login);
			return getFiscal().getFiscalPanel(ctx, domain, year, user.getId());
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	// -------------------------- FISCAL ACTIVITIES
	public static ArrayList<FiscalActivity> getFiscalActivities(String domainName, int domainId){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().getActivities(ctx, domainId);	
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	public static FiscalActivity calculate(String domainName, FiscalActivity fa) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, fa.getDomain());
			return getFiscal().calculate(ctx, fa);	
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	
	public static FiscalActivity getFiscalActivity(String domainName,
			int domainId, int id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().getActivity(ctx, id);	
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	public static FiscalActivity save(String domainName, FiscalActivity fa) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, fa.getDomain());
			return getFiscal().save(ctx, fa);	
		} finally {
			if (ctx != null) ctx.close();	
		}
	}

	public static void delete(String domainName, FiscalActivity fa) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, fa.getDomain());
			getFiscal().delete(ctx, fa);	
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	
	public static FiscalActivity getFiscalActivityFor(String domainName,Epigraph epigraph,FiscalActivity fa) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, fa.getDomain());
			return getFiscal().getActivityFor(ctx,epigraph, fa);
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	// -------------------------- FISCAL MODELS
	public static LinkedList<FiscalModel> getFiscalModels(String domainName, int domainId){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().getModels(ctx, domainId);	
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	
	public static FiscalModel getFiscalModel(String domainName,
			int domainId, int id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().getModel(ctx, id);	
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	public static FiscalModel save(String domainName, FiscalModel fm) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, fm.getDomain());
			return getFiscal().save(ctx, fm);	
		} finally {
			if (ctx != null) ctx.close();	
		}
	}

	public static void delete(String domainName, FiscalModel fm) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, fm.getDomain());
			getFiscal().delete(ctx, fm);	
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	// ----------------------------------MODELO 131
 	// ----------------------------------MODELO 131
	public static LinkedList<Mod131> getMod131s(String domainName,
			int domainId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().getMod131s(ctx, domainId);	
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
 	public static Mod131 getMod131(String domainName,
 			int domainId, int id) {
 		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().getMod131(ctx, id);	
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	public static Mod131 calculate(String domainName, Mod131 mod131) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod131.getDomain());
			return getFiscal().calculateMod131(ctx, mod131);	
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	public static Mod131 save(String domainName, Mod131 mod131) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod131.getDomain());
			return getFiscal().saveMod131(ctx, mod131);	
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	
	
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

	// ----------------------------------MODELO 202
	public static LinkedList<Mod202> getMod202s(String domainName,
			int domainId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().getMod202s(ctx, domainId);	
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	public static Mod202 getMod202(String domainName,
			int domainId, int id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().getMod202(ctx, id);	
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	public static Mod202 calculate(String domainName, Mod202 mod202) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod202.getDomain());
			return getFiscal().calculateMod202(ctx, mod202);	
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	public static Mod202 save(String domainName, Mod202 mod202) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod202.getDomain());
			return getFiscal().saveMod202(ctx, mod202);	
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	public static void deleteMod202(String domainName, Mod202 mod202) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod202.getDomain());
			getFiscal().deleteMod202(ctx, mod202);	
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	public static Mod202 initialize(String domainName, int domain) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain);
			return getFiscal().initializeMod202(ctx);	
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
	
	// ********************************************
	// ********************************* FINANCE **
	// ********************************************
	public static Stream<InvoiceDetail> getInvoiceDetails(String domainName,Integer domainId, InvoiceFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFinance().getInvoiceDetails(ctx, filter);
		} finally {
			if (ctx != null) ctx.close();	
		}
	}
	
	// ********************************************
	// ****************************** MANAGEMENT **
	// ********************************************
	public static Stream<OfferDetail> getOfferDetails(String domainName,
			Integer domainId, OfferFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getManagement().getOfferDetails(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// ********************************************
	// ********************************* PAYROLL **
	// ********************************************

	public static void saveAgreement(AONContext ctx, Agreement ...agreements) throws AonCoreException {
		getAgreement().save(ctx, agreements);
	}
	
	public static Stream<Salary> getSalaries(AONContext ctx,
			SalaryFilter filter) {
		return getSalary().getSalaries(ctx, filter, Salary::new);
	}


}
