package com.esferalia.aon.gwt.fiscal.server;

import static com.esferalia.aon.gwt.common.server.AonServletUtils.commit;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.disableAutoCommit;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.enableAutoCommit;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.getConnection;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.rollback;
import static com.esferalia.aon.gwt.fiscal.server.mod200.Mod200Activation.ACTIVE_EXPRESSION_MAP;
import static com.esferalia.aon.gwt.fiscal.server.mod200.Mod200Compute.COMPUTE_EXPRESSION_MAP;
import static com.esferalia.aon.gwt.fiscal.server.mod200.Mod200Initialization.INITIALIZE_EXPRESSION_MAP;
import static com.esferalia.aon.gwt.fiscal.server.mod200.Mod200Validation.VALIDATION_EXPRESSION_LIST;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Character.CHARACTERS_KEYS;

import java.io.StringWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.common.util.CommonUtil;
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
import com.esferalia.aon.gwt.common.shared.CommonEnum.CNAE;
import com.esferalia.aon.gwt.common.shared.CompanyAdministrator;
import com.esferalia.aon.gwt.common.shared.CompanyBank;
import com.esferalia.aon.gwt.common.shared.CompanyParticipation;
import com.esferalia.aon.gwt.common.shared.DocumentUtil;
import com.esferalia.aon.gwt.common.shared.FiscalParameters;
import com.esferalia.aon.gwt.common.shared.LegalRepresentative;
import com.esferalia.aon.gwt.common.sql.SQLAppParams;
import com.esferalia.aon.gwt.common.sql.SQLCompany;
import com.esferalia.aon.gwt.common.sql.SQLUtils;
import com.esferalia.aon.gwt.fiscal.client.Mod200Service;
import com.esferalia.aon.gwt.fiscal.server.mod200.Mod200MVELContext;
import com.esferalia.aon.gwt.fiscal.server.mod200.xml.MOD2002013;
import com.esferalia.aon.gwt.fiscal.server.mod200.xml.Mod200toMOD2002013;
import com.esferalia.aon.gwt.fiscal.shared.mod200.DoubleVariable;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200.BalanceType;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Key;
import com.esferalia.aon.gwt.fiscal.shared.mod200.ValidationMessage;
import com.esferalia.aon.gwt.fiscal.sql.SQLMod200;

@SuppressWarnings("serial")
@WebServlet(name = "Mod200 Servlet", urlPatterns = { "/aon_gwt_fiscal/Mod200" })
public class Mod200ServiceImpl extends AonRemoteServiceServlet implements Mod200Service {
	private static final int PAGE00 = 0;
	private static final int PAGE01 = 1;
	
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
		List<CompanyAdministrator> adms = SQLCompany.getDirStaff(dsl, mod200.getDomain());
		if ( adms != null && adms.size() > 0 ) {
			for (CompanyAdministrator ca : adms ) {
				if (ca.isAdministrator()) {
					mod200.getAdministrators().add(ca);
				}
				if (ca.isShareholder()) {
					CompanyParticipation cp = new CompanyParticipation();
					cp.setDocument(ca.getDocument());
					cp.setName(ca.getName());
					cp.setProvince(ca.getProvince());
					cp.setPercent(ca.getPercent());
					cp.setNominalValue(ca.getNominalValue());
					cp.setRepresentative(ca.isRepresentative());
					mod200.getParticipationsIn().add(cp);		
				}
				if (ca.isRepresentative()) {
					LegalRepresentative lr = new LegalRepresentative();
					lr.setDocument(ca.getDocument());
					lr.setName(ca.getName());
					mod200.getRepresentatives().add(lr);		
				}
																							}
		}

		Mod200MVELContext ctx = new Mod200MVELContext( mod200, ACCEPTER );
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
		Mod200MVELContext ctx = new Mod200MVELContext( mod200, ACCEPTER );
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
			Mod200MVELContext ctx = new Mod200MVELContext( mod200, ACCEPTER );
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
			v = mod200.getVariable(Mod200Key.BN621);
			mod200.setResultType(null);
			if (v == null || v.getValue() == 0) {
				mod200.setResultType("C");
				mod200.setAmount( 0.0 );
				mod200.setDevType(null);
				mod200.setPayType(null);
			} else if (AonUtil.round(v.getValue()) < 0.0) {
				mod200.setResultType("D");
				mod200.setAmount( AonUtil.round( v.getValue() * -1));
				mod200.setDevType(AonUtil.isEmpty(mod200.getDevType())?"R":mod200.getDevType());
				mod200.setPayType(null);
			} else {
				mod200.setResultType("I");
				mod200.setAmount( v.getValue() );
				mod200.setPayType(AonUtil.isEmpty(mod200.getPayType())?"H":mod200.getPayType());
				mod200.setDevType(null);
			}
			return mod200;
		} catch (Throwable e) {
			e.printStackTrace();
			throw new AonSQLException(e);
		}
	}

	private void addBalanceCharacters(Mod200MVELContext ctx, Mod200 mod200) {
		ctx.put(Mod200Key.C0050.toString(), mod200.getBalanceType() == BalanceType.NORMAL);
		ctx.put(Mod200Key.C0051.toString(), mod200.getBalanceType() == BalanceType.ABREVIADO);
		ctx.put(Mod200Key.C0052.toString(), mod200.getBalanceType() == BalanceType.PYMES);

		ctx.put(Mod200Key.C0053.toString(), mod200.getPygType() == BalanceType.NORMAL);
		ctx.put(Mod200Key.C0054.toString(), mod200.getPygType() == BalanceType.ABREVIADO);
		ctx.put(Mod200Key.C0055.toString(), mod200.getPygType() == BalanceType.PYMES);
	}

	private void addCharacters(Mod200MVELContext ctx, Mod200 mod200) {
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
	public String dumpAEAT(Mod200 mod200) throws AonSQLException {
		try {
			MOD2002013 mod = Mod200toMOD2002013.getMOD2002013(mod200);
			StringWriter writer = new StringWriter();
			JAXBContext context = JAXBContext.newInstance(MOD2002013.class);
			Marshaller um = context.createMarshaller();
			um.setProperty("jaxb.encoding", "ISO-8859-1");
			um.marshal(mod,writer);
			return writer.toString();
		} catch (JAXBException e) {
			throw new AonSQLException(e.getMessage(),e);
		}				
	}
	
	@Override
	public ArrayList<CompanyBank> getCompanyBanks(int enterprise) throws AonSQLException {
		Connection conn = null;
		try {
			conn = getConnection();
			disableAutoCommit(conn);
			DSLContext dsl = DSL.using(conn, AccountingUtil.getDefaultSettings());
			ArrayList<CompanyBank> list = SQLCompany.getBanks(dsl, enterprise);
			commit(conn);
			return list;
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
		List<ValidationMessage> list = new LinkedList<ValidationMessage>();
		validateComplementary(list,mod200);
		validateDocument(list,mod200);
		validateCNAE(list,mod200);
		validateSecretary(list,mod200);
		validateRepresentatives(list,mod200);
		validateAdministrators(list,mod200);
		Mod200MVELContext ctx = new Mod200MVELContext( mod200, ACCEPTER );
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
		for (ValidationMessage validation : VALIDATION_EXPRESSION_LIST) {
			Boolean valid = (Boolean) ctx.evaluateExpression(validation.getKey().toString(),validation.getExpression());
			if (!valid) {
				list.add(validation);
			}
		}
		if (list.size() > 0) {
			mod200.setMessages(list);	
		}
		return mod200;
	}

	private void validateComplementary(List<ValidationMessage> list, Mod200 mod200) {
		if (mod200.isComplementary() ) {
			if (AonUtil.isEmpty(mod200.getComplementaryReceipt() )) {
				list.add(new ValidationMessage(PAGE00,"Si marca Decl. Complementaria, debe indicar un n. de justificante anterior."));
			} else {
				if (mod200.getComplementaryReceipt().length() != 13) {
					list.add(new ValidationMessage(PAGE00,"El n. de justificante anterior debe tener 13 caracteres."));
				}
				if (!mod200.getComplementaryReceipt().startsWith("200") && !mod200.getComplementaryReceipt().startsWith("206")) {
					list.add(new ValidationMessage(PAGE00,"El n. de justificante anterior debe empezar por 200 o 206."));
				}
			}
			
		} else if (!mod200.isComplementary() && !AonUtil.isEmpty(mod200.getComplementaryReceipt())) {
			list.add(new ValidationMessage(PAGE00,"Si no marca Decl. Complementaria, no debe indicar un n. de justificante anterior."));
		}
	}

	private void validateDocument(List<ValidationMessage> list, Mod200 mod200) {
		if (!DocumentUtil.isValid(mod200.getEnterpriseDocument())) {
			list.add(new ValidationMessage(PAGE00,"NIF de la declaraci\u00F3n incorrecto."));
		}
		
	}
	private void validateCNAE(List<ValidationMessage> list, Mod200 mod200) {
		if (AonUtil.isEmpty(mod200.getCnae())) {
			list.add(new ValidationMessage(PAGE00,"Rellene el CNAE de la empresa."));
		} else if (CNAE.valueOfCode(mod200.getCnae()) == null) {
			list.add(new ValidationMessage(PAGE00,"CNAE de la empresa, no válido."));	
		}
	}
	private void validateAdministrators(List<ValidationMessage> list,Mod200 mod200) {
		if (mod200.getAdministrators() == null || mod200.getAdministrators().size() == 0 ) {
			list.add(new ValidationMessage(PAGE01,"Debe rellenar al menos un administrador."));
		} else {
			for (int i = 0; i < mod200.getAdministrators().size(); i++ ) {
				CompanyAdministrator ca = mod200.getAdministrators().get(i); 
				if (!DocumentUtil.isValid(ca.getDocument())) {
					list.add(new ValidationMessage(PAGE01,"NIF del administrador nº "+(i+1) +" incorrecto ["+ca.getDocument()+"]"));		
				}
				if (AonUtil.isEmpty(ca.getName())) {
					list.add(new ValidationMessage(PAGE01,"Falta nombre del administrador nº "+(i+1) +". ["+ca.getDocument()+"]"));
				}
			}
		}
		
	}
	private void validateSecretary(List<ValidationMessage> list, Mod200 mod200) {
		if (DocumentUtil.isEntity(mod200.getEnterpriseDocument())) {
			if (mod200.getSecretary() == null) {
				list.add(new ValidationMessage(PAGE01,"Para personas jur\u00EDdicas, debe rellenar los datos del secretario"));
			} else {
				if (!DocumentUtil.isValid(mod200.getSecretary().getDocument())) {
					list.add(new ValidationMessage(PAGE01,"NIF del secretario incorrecto."));
				}
				if ( AonUtil.isEmpty(mod200.getSecretary().getName())) {
					list.add(new ValidationMessage(PAGE01,"Falta nombre del secretario."));
				} else if (mod200.getSecretary().getName().length() > 25) {
					list.add(new ValidationMessage(PAGE01,"Longitud excedida en el nombre del secretario. Debe limitarse a 25 caracteres."));	
				}
				if (mod200.getSecretary().getIrnr() == null && (mod200.isChecked(Mod200Key.C0021) || mod200.isChecked(Mod200Key.C0046)) ) {
					list.add(new ValidationMessage(PAGE01,"Falta fecha IRNR."));
				}
			}
		}
	}

	private void validateRepresentatives(List<ValidationMessage> list,Mod200 mod200) {
		if (DocumentUtil.isEntity(mod200.getEnterpriseDocument())) {
			if (mod200.getRepresentatives() == null || mod200.getRepresentatives().size() == 0 ) {
				list.add(new ValidationMessage(PAGE01,"Para personas jur\u00EDdicas, debe rellenar al menos un representante."));
			} else {
				for (int i = 0; i < mod200.getRepresentatives().size(); i++ ) {
					LegalRepresentative lr = mod200.getRepresentatives().get(i); 
					if (!DocumentUtil.isValid(lr.getDocument())) {
						list.add(new ValidationMessage(PAGE01,"NIF del representante legal nº "+(i+1) +" incorrecto ["+lr.getDocument()+"]"));		
					}
					if (AonUtil.isEmpty(lr.getName())) {
						list.add(new ValidationMessage(PAGE01,"Falta nombre del representante legal nº "+(i+1) +". ["+lr.getDocument()+"]"));
					}
					if (AonUtil.isEmpty(lr.getNotary())) {
						list.add(new ValidationMessage(PAGE01,"Falta el dato de la notar\u00EDa del representante legal nº "+(i+1) +". ["+lr.getDocument()+"]"));
					} else if (lr.getNotary().length() > 20) {
						list.add(new ValidationMessage(PAGE01,"Longitud excedida en la notar\u00EDa del representante legal nº "+(i+1) +". ["+lr.getDocument()+"]. Debe limitarse a 20 caracteres."));	
					}
					if (lr.getNotaryDate() == null) {
						list.add(new ValidationMessage(PAGE01,"Falta el dato fecha de la notar\u00EDa del representante legal nº "+(i+1) +". ["+lr.getDocument()+"]"));
					}
				}
			}
		}
	}

}
