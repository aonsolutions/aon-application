package com.esferalia.aon.gwt.fiscal.server;

import static com.esferalia.aon.gwt.common.server.AonServletUtils.commit;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.disableAutoCommit;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.enableAutoCommit;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.getConnection;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.rollback;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200ConstantsActivation.ACTIVE_EXPRESSION_MAP;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200ConstantsCharacter.CHARACTERS_KEYS;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200ConstantsCompute.COMPUTE_EXPRESSION_MAP;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200ConstantsInitialization.INITIALIZE_EXPRESSION_MAP;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200ConstantsValidation.ERROR_EXPRESSION_LIST;

import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.annotation.WebServlet;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import com.code.aon.accounting.util.AccountingUtil;
import com.esferalia.aon.accounting.mining.server.AccMiningMVELContext;
import com.esferalia.aon.accounting.mining.server.IAccMiningKeyAccept;
import com.esferalia.aon.accounting.mining.shared.AccMiningException;
import com.esferalia.aon.accounting.mining.shared.AccMiningParameters;
import com.esferalia.aon.accounting.mining.shared.AccountingPeriod;
import com.esferalia.aon.accounting.mining.sql.SQLAccounting;
import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.common.server.DateUtil;
import com.esferalia.aon.gwt.common.shared.AonSQLException;
import com.esferalia.aon.gwt.common.shared.AonUtil;
import com.esferalia.aon.gwt.common.shared.CommonEnum.Administration;
import com.esferalia.aon.gwt.common.shared.CompanyAdministrator;
import com.esferalia.aon.gwt.common.shared.CompanyParticipation;
import com.esferalia.aon.gwt.common.shared.FiscalParameters;
import com.esferalia.aon.gwt.common.sql.SQLAppParams;
import com.esferalia.aon.gwt.common.sql.SQLCompany;
import com.esferalia.aon.gwt.common.sql.SQLUtils;
import com.esferalia.aon.gwt.fiscal.client.Mod200Service;
import com.esferalia.aon.gwt.fiscal.shared.mod200.DoubleVariable;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200.BalanceType;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Key;
import com.esferalia.aon.gwt.fiscal.shared.mod200.ValidationMessage;
import com.esferalia.aon.gwt.fiscal.sql.SQLMod200;

@SuppressWarnings("serial")
@WebServlet(name = "Mod200 Servlet", urlPatterns = { "/aon_gwt_fiscal/Mod200" })
public class Mod200ServiceImpl extends AonRemoteServiceServlet implements Mod200Service {

	private static final IAccMiningKeyAccept ACCEPTER = new IAccMiningKeyAccept() {
		
		@Override
		public boolean acceptKey(Object key) {
			try {
				return (Mod200Key.valueOf((String) key) != null);	
			} catch (IllegalArgumentException e) {
				return false;
			}
		}
	};

	// ------------------------------------------------------- FISCAL PARAMETERS
	@Override
	public Mod200 getMod200(int domain, int year) throws AonSQLException {
		Connection conn = null;
		try {
			conn = getConnection();
			disableAutoCommit(conn);
			DSLContext dsl = DSL.using(conn, AccountingUtil.getDefaultSettings());
			Mod200 mod200 = SQLMod200.get( conn,domain,year );
			if (mod200 == null) {
				mod200 = new Mod200();
				mod200.setDomain(domain);
				mod200.setYear(year);
				initializeNewMod200(mod200, dsl, conn);
			} else {
				initializeActiveMap(mod200);
			}
			commit(conn);
			return mod200;
		} catch (Throwable e) {
			e.printStackTrace();
			rollback(conn);
			throw new AonSQLException(e.getMessage(),e);
		} finally {
			enableAutoCommit(conn);
			SQLUtils.closeQuietly(conn);
		}
	}

	@Override
	public Mod200 initialize(Mod200 mod200) throws AonSQLException {
		Connection conn = null;
		try {
			conn = getConnection();
			disableAutoCommit(conn);
			DSLContext dsl = DSL.using(conn, AccountingUtil.getDefaultSettings());
			initializeMod200(mod200, dsl, conn);
			commit(conn);
			return mod200;
		} catch (Throwable e) {
			e.printStackTrace();
			rollback(conn);
			throw new AonSQLException(e.getMessage(),e);
		} finally {
			enableAutoCommit(conn);
			SQLUtils.closeQuietly(conn);
		}
	}
	
	private void initializeNewMod200(Mod200 mod200,DSLContext dsl, Connection conn) throws AonSQLException, AccMiningException {
		mod200.setPeriodType(1);
		mod200.setPeriodStart(DateUtil.getYearFirstDay(mod200.getYear()));
		mod200.setPeriodEnd(DateUtil.getYearLastDay(mod200.getYear()));

		mod200.setBalanceType( BalanceType.ABREVIADO );
		mod200.setPygType(BalanceType.ABREVIADO );
		
		FiscalParameters fiscalParameters = SQLAppParams.getFiscalParameters(dsl, mod200.getDomain());
		
		mod200.setEnterprise(fiscalParameters.getCompany());
		mod200.setEnterpriseDocument(fiscalParameters.getDocument());
		mod200.setEnterpriseName(fiscalParameters.getName());
		mod200.setEnterprisePhone1(fiscalParameters.getContactPhone());
		mod200.setEnterprisePhone2(fiscalParameters.getContactCellular());
		Administration adm = fiscalParameters.getAdministration(Administration.COMMON_TERRITORY);
		mod200.setAdministration(adm.ordinal());
	}

	private void initializeMod200(Mod200 mod200,DSLContext dsl, Connection conn) throws AonSQLException, AccMiningException {
		List<CompanyAdministrator> adms = SQLCompany.getAdministrators(dsl, mod200.getDomain());
		if ( adms != null && adms.size() > 0 ) {
			List<CompanyAdministrator> administrators = new LinkedList<CompanyAdministrator>();
			List<CompanyParticipation> participationsIn = new LinkedList<CompanyParticipation>();
			for (CompanyAdministrator ca : adms ) {
				if (ca.isAdministrator()) {
					administrators.add(ca);
				}
				if (ca.isShareholder()) {
					CompanyParticipation cp = new CompanyParticipation();
					cp.setDocument(ca.getDocument());
					cp.setName(ca.getName());
					cp.setProvince(ca.getProvince());
					cp.setPercent(ca.getPercent());
					cp.setNominalValue(ca.getNominalValue());
					cp.setRepresentative(ca.isRepresentative());
					participationsIn.add(cp);		
				}
			}
			mod200.setAdministrators(administrators);
			mod200.setParticipationsIn(participationsIn);
			mod200.setParticipationsOut(new LinkedList<CompanyParticipation>());
		}

		AccMiningMVELContext ctx = new AccMiningMVELContext( ACCEPTER );
		ctx.setAccounts( SQLAccounting.getAccountBalances(conn, getParams(conn,mod200)) );
		ctx.setExpressionMap(INITIALIZE_EXPRESSION_MAP);
		addCharacters(ctx,mod200);
		addBalanceCharacters(ctx,mod200);
		DoubleVariable dv = null;
		for (String stringKey : INITIALIZE_EXPRESSION_MAP.keySet()) {
			Mod200Key k = Mod200Key.valueOf(stringKey.toString());
			String expression = INITIALIZE_EXPRESSION_MAP.get(stringKey);
			ctx.put(stringKey, 0.0 );
			Object ret = ctx.evaluateExpression(stringKey,expression);
			ctx.put(stringKey, ret );
			if (ret instanceof Double ) {
				dv = new DoubleVariable( k );
				dv.setValue( (Double) ret );
				mod200.addVariable( dv );
			} 
			if (ret instanceof Boolean) {
				dv = new DoubleVariable( k );
				dv.setValue((Boolean) ret );
				mod200.addVariable( dv );
			}
		}		
		calculate(mod200,false);
		initializeActiveMap(mod200);
	}
	
	
	private void initializeActiveMap(Mod200 mod200) {
		AccMiningMVELContext ctx = new AccMiningMVELContext( ACCEPTER );
		ctx.setExpressionMap(ACTIVE_EXPRESSION_MAP);
		addCharacters(ctx,mod200);
		for (Mod200Key key : Mod200Key.values() ) {
			if (ACTIVE_EXPRESSION_MAP.containsKey(key.toString()) ) {
				Object ret = ctx.get( key.toString() );
				if (ret instanceof Boolean && ((Boolean) ret) ) {
					mod200.getVisibleMap().put(key,true);
				}
			} else {
				mod200.getVisibleMap().put(key,true);
			}
		}
	}

	private AccMiningParameters getParams(Connection conn,Mod200 mod200) throws AonSQLException {
		AccMiningParameters params = new AccMiningParameters();
		params.setDomain(mod200.getDomain());
		params.setYear(mod200.getYear());
		AccountingPeriod period = SQLAccounting.getPeriod(mod200.getDomain(), mod200.getYear(), conn);
		if (period == null) {
			throw new AonSQLException("Ejercicio '"+mod200.getYear()+"' no encontrado.");
		}
		if (!period.isClosed()) {
			throw new AonSQLException("Cierre el ejercicio contable '"+mod200.getYear()+"' para poder continuar.");
		}
		params.setPeriodId(period.getId());
		params.setStartDate(period.getStart());
		params.setEndDate(period.getEnd());
		return params;
	}

	@Override
	public Mod200 calculate(Mod200 mod200) throws AonSQLException {
		return calculate(mod200,true);
	}

	private Mod200 calculate(Mod200 mod200, boolean addToDraft) throws AonSQLException {
		try {
			AccMiningMVELContext ctx = new AccMiningMVELContext( ACCEPTER );
			ctx.setExpressionMap(COMPUTE_EXPRESSION_MAP);
			for (DoubleVariable dv : mod200.getKeysMap().values()) {
				ctx.put(dv.getKey().toString(), dv.getValue());
			}
			addCharacters(ctx,mod200);
			addBalanceCharacters(ctx,mod200);
			
			DoubleVariable d = null;
			for (Mod200Key key : mod200.getDraftMap().keySet() ) {
				d = mod200.getDraftMap().get(key);
				if (d.isChangedByUser()) {
					ctx.put(key.toString(), d.getValue());
				}
			}
			
			DoubleVariable v = null;
			for (String stringKey : COMPUTE_EXPRESSION_MAP.keySet()) {
				Mod200Key k = Mod200Key.valueOf(stringKey.toString());
				String expression = COMPUTE_EXPRESSION_MAP.get(stringKey);
				DoubleVariable existingVariable = mod200.getVariable(k);
				if ( existingVariable == null ) {
					existingVariable = new DoubleVariable( k );
					existingVariable.setValue( 0.0 );
				}
				ctx.put(stringKey, existingVariable.getValue());

				Object ret = ctx.evaluateExpression(stringKey,expression);
				
				if (ret instanceof Double) {
					Double calculated = (Double) ret;
					ctx.put(stringKey, calculated);
					if ( !AonUtil.equals( existingVariable.getValue() , calculated ) ) {
						v = new DoubleVariable( k );
						v.setValue( calculated );
						if (addToDraft) {
							mod200.addDraftVariable(v);	
						} else {
							mod200.addVariable(v);
						}
						
					}
				}
			}
//			for (Mod200Key key : Mod200Key.values() ) {
//				if (COMPUTE_EXPRESSION_MAP.containsKey(key.toString())) {
//					Object ret = ctx.get( key.toString() );
//					if (ret instanceof Double) {
//						Double calculated = (Double) ret; 
//						Mod200Key k = Mod200Key.valueOf(key.toString());
//						DoubleVariable existingVariable = mod200.getVariable(k);
//						if ( existingVariable == null || !AonUtil.equals( existingVariable.getValue() , calculated ) ) {
//							System.out.println( "DRAFT : " + k.toString() + " [" +
//									((existingVariable == null)?"null":existingVariable.getValue())
//									+ "] [" + calculated);
//							v = new DoubleVariable( k );
//							v.setValue( calculated );
//							mod200.addDraftVariable(v);
//						}
//					}
//				}
//			}
			return mod200;
		} catch (Throwable e) {
			e.printStackTrace();
			throw new AonSQLException(e);
		}
	}

	private void addBalanceCharacters(AccMiningMVELContext ctx, Mod200 mod200) {
		ctx.put(Mod200Key.C0050.toString(), mod200.getBalanceType() == BalanceType.NORMAL);
		ctx.put(Mod200Key.C0051.toString(), mod200.getBalanceType() == BalanceType.ABREVIADO);
		ctx.put(Mod200Key.C0052.toString(), mod200.getBalanceType() == BalanceType.PYMES);

		ctx.put(Mod200Key.C0053.toString(), mod200.getPygType() == BalanceType.NORMAL);
		ctx.put(Mod200Key.C0054.toString(), mod200.getPygType() == BalanceType.ABREVIADO);
		ctx.put(Mod200Key.C0055.toString(), mod200.getPygType() == BalanceType.PYMES);
	}

	private void addCharacters(AccMiningMVELContext ctx, Mod200 mod200) {
		for (Mod200Key key : CHARACTERS_KEYS) {
			ctx.put(key.toString(), 
					(mod200.getKeysMap().containsKey(key) 
				 && (AonUtil.equals(mod200.getKeysMap().get(key).getValue(),1.0))));	
		}
	}

	@Override
	public Mod200 save(Mod200 mod200) throws AonSQLException {
		Connection conn = null;
		try {
			conn = getConnection();
			disableAutoCommit(conn);
			mod200 = SQLMod200.save(conn, mod200);
			commit(conn);
			return mod200;
		} catch (Throwable e) {
			e.printStackTrace();
			rollback(conn);
			throw new AonSQLException(e.getMessage(),e);
		} finally {
			enableAutoCommit(conn);
			SQLUtils.closeQuietly(conn);
		}
	}
	
	@Override
	public Mod200 delete(Mod200 mod200) throws AonSQLException {
		Connection conn = null;
		try {
			conn = getConnection();
			disableAutoCommit(conn);
			SQLMod200.delete(conn, mod200);
			commit(conn);
			return mod200;
		} catch (Throwable e) {
			e.printStackTrace();
			rollback(conn);
			throw new AonSQLException(e.getMessage(),e);
		} finally {
			enableAutoCommit(conn);
			SQLUtils.closeQuietly(conn);
		}
	}

	@Override
	public Mod200 validate(Mod200 mod200) throws AonSQLException {
		AccMiningMVELContext ctx = new AccMiningMVELContext( ACCEPTER );
		for (DoubleVariable dv : mod200.getKeysMap().values()) {
			ctx.put(dv.getKey().toString(), dv.getValue());
		}
		addCharacters(ctx,mod200);
		DoubleVariable d = null;
		for (Mod200Key key : mod200.getDraftMap().keySet() ) {
			d = mod200.getDraftMap().get(key);
			ctx.put(key.toString(), d.getValue());
		}
		mod200.setMessages(null);	
		List<ValidationMessage> list = new LinkedList<ValidationMessage>();
		for (ValidationMessage validation : ERROR_EXPRESSION_LIST) {
			Boolean error = (Boolean) ctx.evaluateExpression(validation.getKey().toString(),validation.getExpression());
			if (error) {
				list.add(validation);
			}
		}
		if (list.size() > 0) {
			mod200.setMessages(list);	
		}
		return mod200;
	}
/*
	private void dumpMod200(Mod200 mod200) {
		System.out.println();
		System.out.println("***********************************************************");
		System.out.println("[START]");
		System.out.println("***********************************************************");
		System.out.println("Id..:"+mod200.getId());
		System.out.println("Domain..:"+mod200.getDomain());
		System.out.println("Year..:"+mod200.getYear());
		System.out.println("Administration..:"+mod200.getAdministration());
		System.out.println("Enterprise..:"+mod200.getEnterprise());
		System.out.println("EnterpriseDocument..:"+mod200.getEnterpriseDocument());
		System.out.println("EnterpriseName..:"+mod200.getEnterpriseName());
		System.out.println("EnterprisePhone1..:"+mod200.getEnterprisePhone1());
		System.out.println("EnterprisePhone2..:"+mod200.getEnterprisePhone2());
		System.out.println("BalanceType..:"+mod200.getBalanceType());
		System.out.println("PygType..:"+mod200.getPygType());
		System.out.println("\t------------------------------");
		if (mod200.getAdministrators() != null) {
			for (CompanyAdministrator ca : mod200.getAdministrators()) {
				System.out.println("Administrator");
				System.out.println("Document..:"+ca.getDocument());
				System.out.println("Name..:"+ca.getName());
				System.out.println("Shareholder..:"+ca.isShareholder());
				System.out.println("Representative..:"+ca.isRepresentative());
				System.out.println("Administrator..:"+ca.isAdministrator());
				System.out.println("Percent..:"+ca.getPercent());
				System.out.println("NominalValue..:"+ca.getNominalValue());
				System.out.println("Residence..:"+ca.getResidence());
				System.out.println("Province..:"+ca.getProvince());
				System.out.println();
			}
		}
		System.out.println("\t------------------------------");
		if (mod200.getParticipationsIn() != null) {
			for (CompanyParticipation cp : mod200.getParticipationsIn()) {
				System.out.println("Participation IN");
				System.out.println("\tDocument..:"+cp.getDocument());
				System.out.println("\tName..:"+cp.getName());
				System.out.println("\tProvince..:"+cp.getProvince());
				System.out.println("\tRepresentative..:"+cp.isRepresentative());
				System.out.println("\tPercent..:"+cp.getPercent());
				System.out.println("\tNominalValue..:"+cp.getNominalValue());
				System.out.println();
			}
		}
		System.out.println("\t------------------------------");
		if (mod200.getParticipationsOut() != null) {
			for (CompanyParticipation cp : mod200.getParticipationsOut()) {
				System.out.println("Participation OUT");
				System.out.println("\tDocument..:"+cp.getDocument());
				System.out.println("\tName..:"+cp.getName());
				System.out.println("\tProvince..:"+cp.getProvince());
				System.out.println("\tRepresentative..:"+cp.isRepresentative());
				System.out.println("\tPercent..:"+cp.getPercent());
				System.out.println("\tNominalValue..:"+cp.getNominalValue());
				System.out.println("\tbookValue..:"+cp.getBookValue() );
				System.out.println("\tincomes..:"+cp.getIncomes() );
				System.out.println("\taValue..:"+cp.getaValue() );
				System.out.println("\tbValue..:"+cp.getbValue() );
				System.out.println("\tcValue..:"+cp.getcValue() );
				System.out.println("\tdValue..:"+cp.getdValue() );
				System.out.println("\tcapital..:"+cp.getCapital() ); 
				System.out.println("\treserve..:"+cp.getReserve() );
				System.out.println("\totherAmounts..:"+cp.getOtherAmounts() );
				System.out.println("\tresult..:"+cp.getResult() );
				System.out.println();
			}
		}
		System.out.println("\t------------------------------");
		for (Mod200Key key : mod200.getCharacterMap().keySet()) {
			System.out.println(key.toString() + " ........................ " + mod200.getCharacterMap().get(key).getValue()); 
		}
		System.out.println("\t------------------------------");
		for (Mod200Key key : mod200.getDraftMap().keySet()) {
			System.out.println(key.toString() + " ........................ " + mod200.getDraftMap().get(key).getValue()); 
		}
		System.out.println("\t------------------------------");
		
		System.out.println("\t------------------------------");
		for (Mod200Key key : mod200.getKeysMap().keySet()) {
			System.out.println(key.toString() + " ........................ " + mod200.getKeysMap().get(key).getValue()); 
		}
		System.out.println("\t------------------------------");
		
		
		System.out.println("Comments..:"+mod200.getComments());
		System.out.println("***********************************************************");
		System.out.println("[END]");
		System.out.println("***********************************************************");
		System.out.println();
	}
*/	
}
	