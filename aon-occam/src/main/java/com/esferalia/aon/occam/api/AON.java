package com.esferalia.aon.occam.api;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
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
import com.esferalia.aon.occam.api.model.Bonus;
import com.esferalia.aon.occam.api.model.BonusFilter;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.Salary;
import com.esferalia.aon.occam.api.model.SalaryAccountEntry;
import com.esferalia.aon.occam.api.model.SalaryFilter;
import com.esferalia.aon.occam.api.model.accounting.AccMiningParameters;
import com.esferalia.aon.occam.api.model.accounting.AccountBalance;
import com.esferalia.aon.occam.api.model.callcenter.Issue;
import com.esferalia.aon.occam.api.model.callcenter.IssueComment;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivity;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelMatrix;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
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
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014;
import com.esferalia.aon.occam.api.model.fiscal.modules.Modules2015.Epigraph;
import com.esferalia.aon.occam.api.model.management.OfferDetail;
import com.esferalia.aon.occam.api.model.management.OfferFilter;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductTag;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.impl.jooq.AccountingImpl;
import com.esferalia.aon.occam.impl.jooq.AgreementImpl;
import com.esferalia.aon.occam.impl.jooq.CommonImpl;
import com.esferalia.aon.occam.impl.jooq.FeeImpl;
import com.esferalia.aon.occam.impl.jooq.FinanceImpl;
import com.esferalia.aon.occam.impl.jooq.FiscalImpl;
import com.esferalia.aon.occam.impl.jooq.GroupwareImpl;
import com.esferalia.aon.occam.impl.jooq.ManagementImpl;
import com.esferalia.aon.occam.impl.jooq.ProductImpl;
import com.esferalia.aon.occam.impl.jooq.SalaryImpl;
import com.esferalia.aon.occam.impl.jooq.SecurityImpl;
import com.esferalia.aon.occam.impl.jooq.SystemImpl;
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

	private static ISystem getSystem() {
		return new SystemImpl();
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

	private static IProduct getProduct() {
		return new ProductImpl();
	}

	private static IFee getFee() {
		return new FeeImpl();
	}
	
	private static IGroupware getGroupware() {
		return new GroupwareImpl();
	}

	// ********************************************
	// ******************************** SECURITY **
	// ********************************************
	public static User getUser(String domainName, int domainId, String login) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getSecurity().getUser(ctx, login);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Integer[] getUserScopes(String domainName, int domainId,
			Integer userId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getSecurity().getUserScopes(ctx, userId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// ********************************************
	// ********************************** COMMON **
	// ********************************************

	// --------------------- APPLICATION PARAMETERS
	public static FiscalParameters getFiscalParameters(String domainName,
			int domainId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getCommon().getFiscalParameters(ctx);
		} finally {
			if (ctx != null)
				ctx.close();
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
			if (ctx != null)
				ctx.close();
		}
	}

	public static Enterprise getEnterprise(String domainName, int domain, int id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain);
			return getCommon().getEnterprise(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Company getCompanyForDomain(String domainName, int domainId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getCommon().getCompany(ctx, domainId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static ArrayList<CompanyBank> getCompanyBanks(String domainName,
			int domain, int enterprise) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain);
			return getCommon().getCompanyBanks(ctx, enterprise);
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
			if (ctx != null)
				ctx.close();
		}
	}

	public static Map<Integer, String[]> getProductTagMap(String domainName,
			int domainId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getCommon().getProductTagMap(ctx);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// ********************************************
	// ****************************** ACCOUNTING **
	// ********************************************
	public static Account getAccount(AONContext ctx, Integer id) {
		return getAccounting().fetchAccount(ctx, id);
	}

	public static Account getAccount(AONContext ctx, String code) {
		return getAccounting().fetchAccount(ctx, code);
	}

	// ********************************************
	// ********************************* PRODUCT **
	// ********************************************

	// ------------------------------------ PRODUCT

	public static Product getProduct(AONContext ctx, Integer id) {
		return getProduct().getProduct(ctx, id);
	}

	public static void insert(AONContext ctx, Product p) {
		getProduct().insert(ctx, p);
	}

	public static void insertWithId(AONContext ctx, Product p) {
		getProduct().insertWithId(ctx, p);
	}

	public static void insert(AONContext ctx, Stream<Product> ps) {
		getProduct().insert(ctx, ps);
	}

	public static void insertWithId(AONContext ctx, Stream<Product> ps) {
		getProduct().insertWithId(ctx, ps);
	}

	public static void update(AONContext ctx, Product p) {
		getProduct().update(ctx, p);
	}

	public static void delete(AONContext ctx, Product p) {
		getProduct().delete(ctx, p);
	}

	public static void delete(AONContext ctx, Stream<Product> ps) {
		getProduct().delete(ctx, ps);
	}

	// ------------------------------------ PRODUCT_TAG

	public static void insertProductTag(AONContext ctx, ProductTag pt) {
		getProduct().insertProductTag(ctx, pt);
	}

	public static void insertProductTag(AONContext ctx, Stream<ProductTag> pts) {
		getProduct().insertProductTag(ctx, pts);
	}

	public static void updateProductTag(AONContext ctx, ProductTag pt) {
		getProduct().updateProductTag(ctx, pt);
	}

	public static void deleteProductTag(AONContext ctx, ProductTag pt) {
		getProduct().deleteProductTag(ctx, pt);
	}

	public static void deleteProductTag(AONContext ctx, Stream<ProductTag> pts) {
		getProduct().deleteProductTag(ctx, pts);
	}

	// ------------------------------------ ITEM

	public static void insertItem(AONContext ctx, Item i) {
		getProduct().insertItem(ctx, i);
	}

	public static void insertItemWithId(AONContext ctx, Item i) {
		getProduct().insertItemWithId(ctx, i);
	}

	public static void insertItem(AONContext ctx, Stream<Item> is) {
		getProduct().insertItem(ctx, is);
	}

	public static void insertItemWithId(AONContext ctx, Stream<Item> is) {
		getProduct().insertItemWithId(ctx, is);
	}

	public static void updateItem(AONContext ctx, Item i) {
		getProduct().updateItem(ctx, i);
	}

	public static void deleteItem(AONContext ctx, Item i) {
		getProduct().deleteItem(ctx, i);
	}

	public static void deleteItem(AONContext ctx, Stream<Item> is) {
		getProduct().deleteItem(ctx, is);
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

	public static LinkedHashMap<String, AccountBalance> getAccountBalances(
			AONContext ctx, AccMiningParameters params) throws AonCoreException {
		return getAccounting().getAccountBalances(ctx, params);
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
			if (ctx != null)
				ctx.close();
		}
	}

	// -------------------------- FISCAL PANEL
	public static LinkedList<IFiscalModel> getAllModels(String domainName,
			int domain, String login) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain);
			User user = getUser(domainName, domain, login);
			return getFiscal().getAllModels(ctx, domain, user.getId());
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static LinkedList<IFiscalModel> getAllModels(String domainName,
			int domain, int year, String login) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain);
			User user = getUser(domainName, domain, login);
			return getFiscal().getAllModels(ctx, domain, year, user.getId());
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// -------------------------- FISCAL ACTIVITIES
	public static ArrayList<FiscalActivity> getFiscalActivities(
			String domainName, int domainId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().getActivities(ctx, domainId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static FiscalActivity calculate(String domainName, FiscalActivity fa) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, fa.getDomain());
			return getFiscal().calculate(ctx, fa);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static FiscalActivity getFiscalActivity(String domainName,
			int domainId, int id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().getActivity(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static FiscalActivity save(String domainName, FiscalActivity fa) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, fa.getDomain());
			return getFiscal().save(ctx, fa);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void delete(String domainName, FiscalActivity fa) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, fa.getDomain());
			getFiscal().delete(ctx, fa);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static FiscalActivity getFiscalActivityFor(String domainName,
			Epigraph epigraph, FiscalActivity fa) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, fa.getDomain());
			return getFiscal().getActivityFor(ctx, epigraph, fa);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// -------------------------- FISCAL MODELS
	public static LinkedList<FiscalModel> getFiscalModels(String domainName,
			int domainId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().getModels(ctx, domainId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static FiscalModel getFiscalModel(String domainName, int domainId,
			int id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().getModel(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static FiscalModel save(String domainName, FiscalModel fm) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, fm.getDomain());
			return getFiscal().save(ctx, fm);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void delete(String domainName, FiscalModel fm) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, fm.getDomain());
			getFiscal().delete(ctx, fm);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// ----------------------------------MODELO 131
	public static LinkedList<Mod131> getMod131s(String domainName, int domainId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().getMod131s(ctx, domainId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod131 getMod131(String domainName, int domainId, int id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().getMod131(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod131 calculate(String domainName, Mod131 mod131) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod131.getDomain());
			return getFiscal().calculateMod131(ctx, mod131);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod131 save(String domainName, Mod131 mod131) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod131.getDomain());
			return getFiscal().saveMod131(ctx, mod131);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void deleteMod131(String domainName, Mod131 mod131) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod131.getDomain());
			getFiscal().deleteMod131(ctx, mod131);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod131 initializeMod131(String domainName, int domain,
			Mod131 mod131) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain);
			return getFiscal().initializeMod131(ctx, mod131);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// ----------------------------------MODELO 180
	public static ArrayList<Mod180> getMod180s(String domainName, int domainId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().getMod180s(ctx, domainId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod180 getMod180(String domainName, int domainId, Integer id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().getMod180(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod180 initializeMod180(String domainName, int domainId,
			Integer year) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().initializeMod180(ctx, year);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod180 saveMod180(String domainName, int domainId,
			Mod180 mod180) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().saveMod180(ctx, mod180);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void deleteMod180(String domainName, int domainId,
			Mod180 mod180) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			getFiscal().deleteMod180(ctx, mod180);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod180Detail getMod180Detail(String domainName, int domainId,
			Integer id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().getMod180Detail(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// ----------------------------------MODELO 190
	public static ArrayList<Mod190> getMod190s(String domainName, int domainId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().getMod190s(ctx, domainId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod190 getMod190(String domainName, int domainId, Integer id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().getMod190(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod190 initializeMod190(String domainName, int domainId,
			Integer year) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().initializeMod190(ctx, year);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod190 saveMod190(String domainName, int domainId,
			Mod190 mod190) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().saveMod190(ctx, mod190);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void deleteMod190(String domainName, int domainId,
			Mod190 mod190) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			getFiscal().deleteMod190(ctx, mod190);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod190Detail getMod190Detail(String domainName, int domainId,
			Integer id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().getMod190Detail(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// ----------------------------------MODELO 193
	public static ArrayList<Mod193> getMod193s(String domainName, int domainId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().getMod193s(ctx, domainId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod193 getMod193(String domainName, int domainId, Integer id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().getMod193(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod193 initializeMod193(String domainName, int domainId,
			Integer year) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().initializeMod193(ctx, year);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod193 saveMod193(String domainName, int domainId,
			Mod193 mod193) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().saveMod193(ctx, mod193);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void deleteMod193(String domainName, int domainId,
			Mod193 mod193) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			getFiscal().deleteMod193(ctx, mod193);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod193Detail getMod193Detail(String domainName, int domainId,
			Integer id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().getMod193Detail(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// ----------------------------------MODELO 184
	public static ArrayList<Mod184> getMod184s(String domainName, int domainId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().getMod184s(ctx, domainId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod184 getMod184(String domainName, int domainId, Integer id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().getMod184(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod184 initializeMod184(String domainName, int domainId,
			Integer year) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().initializeMod184(ctx, year);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod184 saveMod184(String domainName, int domainId,
			Mod184 mod184) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().saveMod184(ctx, mod184);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void deleteMod184(String domainName, int domainId,
			Mod184 mod184) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			getFiscal().deleteMod184(ctx, mod184);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// ----------------------------------MODELO 202
	public static LinkedList<Mod202> getMod202s(String domainName, int domainId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().getMod202s(ctx, domainId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod202 getMod202(String domainName, int domainId, int id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().getMod202(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod202 calculate(String domainName, Mod202 mod202) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod202.getDomain());
			return getFiscal().calculateMod202(ctx, mod202);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod202 save(String domainName, Mod202 mod202) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod202.getDomain());
			return getFiscal().saveMod202(ctx, mod202);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void deleteMod202(String domainName, Mod202 mod202) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, mod202.getDomain());
			getFiscal().deleteMod202(ctx, mod202);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod202 initializeMod202(String domainName, int domain,
			Mod202 mod202) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain);
			return getFiscal().initializeMod202(ctx, mod202);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// ----------------------------------MODELO 200 - 2013
	public static Mod2002013 initializeNewMod2002013(String domainName,
			int domain, Mod2002013 mod200) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain);
			return getFiscal().initializeNewMod2002013(ctx, mod200);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod2002013 initializeMod2002013(String domainName,
			int domain, Mod2002013 mod200) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain);
			return getFiscal().initializeMod2002013(ctx, mod200);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod2002013 getMod2002013ByYear(String domainName, int domain,
			int year) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain);
			return getFiscal().getMod2002013ByYear(ctx, year);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod2002013 getMod2002013ById(String domainName, int domain,
			int id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain);
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

	public static Mod2002013 saveMod2002013(String domainName, int domain,
			Mod2002013 mod200) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain);
			return getFiscal().saveMod2002013(ctx, mod200);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void deleteMod2002013(String domainName, int domain, int id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain);
			getFiscal().deleteMod2002013(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static String dumpAEATMod2002013(Mod2002013 mod200) {
		return getFiscal().dumpAEATMod2002013(mod200);
	}	
	
	public static Domain insertDomain(String domainName, int parentDomain,
			String cifEnterprise, String nameEnterprise, List<String> messages) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, parentDomain);
			return getCommon().insertDomain(ctx, parentDomain, cifEnterprise,
					nameEnterprise, messages);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// ----------------------------------MODELO 200 - 2014
	public static Mod2002014 initializeNewMod2002014(String domainName,
			int domain, Mod2002014 mod200) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain);
			return getFiscal().initializeNewMod2002014(ctx, mod200);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod2002014 initializeMod2002014(String domainName,
			int domain, Mod2002014 mod200) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain);
			return getFiscal().initializeMod2002014(ctx, mod200);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod2002014 getMod2002014ByYear(String domainName, int domain,
			int year) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain);
			return getFiscal().getMod2002014ByYear(ctx, year);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod2002014 getMod2002014ById(String domainName, int domain,
			int id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain);
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

	public static Mod2002014 saveMod2002014(String domainName, int domain,
			Mod2002014 mod200) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain);
			return getFiscal().saveMod2002014(ctx, mod200);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void deleteMod2002014(String domainName, int domain, int id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain);
			getFiscal().deleteMod2002014(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static String dumpAEATMod2002014(Mod2002014 mod200) {
		return getFiscal().dumpAEATMod2002014(mod200);
	}

	public static Mod2002014 importMod2002013(String domainName, int domain,
			Mod2002014 mod200) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain);
			return getFiscal().importMod2002013(ctx, mod200);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// ----------------------------------MODELO 390
	public static ArrayList<Mod390> getMod390s(String domainName, int domainId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().getMod390s(ctx, domainId);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod390 getMod390(String domainName, int domainId, int id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().getMod390(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static String getMod390XML(String domainName, int domainId, int id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().getMod390XML(ctx, id);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod390 initializeMod390(String domainName, int domainId,
			Integer year) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().initializeMod390(ctx, year);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static Mod390 saveMod390(String domainName, int domainId,
			Mod390 mod390) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().saveMod390(ctx, mod390);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static void deleteMod390(String domainName, int domainId,
			Mod390 mod390) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			getFiscal().deleteMod390(ctx, mod390);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static ArrayList<Mod390Detail> getMod390Details(String domainName,
			Integer domainId, Mod390 mod390) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFiscal().getMod390Details(ctx, mod390);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	// ********************************************
	// ********************************* FINANCE **
	// ********************************************
	public static Stream<InvoiceDetail> getInvoiceDetails(String domainName,
			Integer domainId, InvoiceFilter filter) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId);
			return getFinance().getInvoiceDetails(ctx, filter);
		} finally {
			if (ctx != null)
				ctx.close();
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

	public static void saveAgreement(AONContext ctx, Agreement... agreements)
			throws AonCoreException {
		getAgreement().save(ctx, agreements);
	}

	public static Stream<Salary> getSalaries(AONContext ctx, SalaryFilter filter) {
		return getSalary().getSalaries(ctx, filter, Salary::new);
	}

	public static Stream<Salary> getSalaryData(AONContext ctx, SalaryFilter filter) {
		return getSalary().getSalaryData(ctx, filter, Salary::new);
	}

	public static Stream<Bonus> getAvailableBonuses(AONContext ctx,
			BonusFilter filter) {
		return getSystem().getAvailableBonus(ctx, filter, Bonus::new);
	}

	// ********************************************
	// ************************************* FEE **
	// ********************************************

	public static void insertFee(AONContext ctx, Fee f) {
		getFee().insertFee(ctx, f);
	}

	public static void insertFee(AONContext ctx, Stream<Fee> fs) {
		getFee().insertFee(ctx, fs);
	}

	public static void updateFee(AONContext ctx, Fee f) {
		getFee().updateFee(ctx, f);
	}

	public static void deleteFee(AONContext ctx, Fee f) {
		getFee().deleteFee(ctx, f);
	}

	public static void deleteFee(AONContext ctx, Stream<Fee> fs) {
		getFee().deleteFee(ctx, fs);
	}
	
	// ********************************************
	// ****************************** CALLCENTER **
	// ********************************************
	
	public static ArrayList<Issue> getOpenIssues(String domainName,
			int domain, String subject) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain);
			return getGroupware().getOpenIssues(ctx, domain, subject);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public static ArrayList<Issue> getClosedIssues(String domainName,
			int domain, String subject) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain);
			return getGroupware().getClosedIssues(ctx, domain, subject);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public static ArrayList<IssueComment> getIssueComments(String domainName,
			int domain, int issue) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domain);
			return getGroupware().getIssueComments(ctx, issue);
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
}
