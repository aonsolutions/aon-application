package com.esferalia.aon.occam.impl.jooq.dao.mod200_2013;

import static com.esferalia.aon.jooq.tables.FsModel200.FS_MODEL200;
import static com.esferalia.aon.jooq.tables.FsModel200Detail.FS_MODEL200_DETAIL;
import static com.esferalia.aon.jooq.tables.FsModel200Registry.FS_MODEL200_REGISTRY;
import static com.esferalia.aon.occam.impl.jooq.dao.mod200_2013.Mod2002013Initialization.INITIALIZE_EXPRESSION_MAP;
import static com.esferalia.aon.occam.impl.jooq.dao.mod200_2013.Mod2002013Validation.VALIDATION_EXPRESSION_LIST;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.jooq.Result;

import com.esferalia.aon.jooq.tables.records.FsModel200DetailRecord;
import com.esferalia.aon.jooq.tables.records.FsModel200Record;
import com.esferalia.aon.jooq.tables.records.FsModel200RegistryRecord;
import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.CompanyAdministrator;
import com.esferalia.aon.occam.api.model.CompanyParticipation;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.accounting.AccMiningParameters;
import com.esferalia.aon.occam.api.model.accounting.IAccMiningKeyAccept;
import com.esferalia.aon.occam.api.model.fiscal.LegalRepresentative;
import com.esferalia.aon.occam.api.model.fiscal.Secretary;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.DoubleVariable2013;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013.BalanceType;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013Character;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.ValidationMessage2013;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.CNAE;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AppParamDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.mod200_2013.jaxb.MOD2002013;
import com.esferalia.aon.occam.impl.jooq.dao.mod200_2013.jaxb.Mod2002013toMOD2002013;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class Mod2002013DAO  {

	private static final IAccMiningKeyAccept ACCEPTER = new IAccMiningKeyAccept() {
		
		@Override
		public boolean acceptKey(Object key) {
			try {
				return (Mod2002013Key.valueOf((String) key) != null);	
			} catch (IllegalArgumentException e) {
				return false;
			}
		}
	};

	public static Mod2002013 save(AONContext ctx, Mod2002013 mod200) {
		if (mod200.getId() == null) {
			return insert(ctx, mod200);
		} else {
			return update(ctx, mod200);
		}
	}
	
	private static Mod2002013 insert(AONContext ctx, Mod2002013 mod200)  {
		FsModel200Record record = ctx.getDslContext()
			.insertInto(FS_MODEL200)
			 .set(FS_MODEL200.DOMAIN, mod200.getDomain() )
			 .set(FS_MODEL200.ENTERPRISE, mod200.getEnterprise())
			 .set(FS_MODEL200.YEAR, mod200.getYear())
			 .set(FS_MODEL200.ADMINISTRATION, mod200.getAdministration().getValue() )
			 .set(FS_MODEL200.DOCUMENT, mod200.getEnterpriseDocument())
			 .set(FS_MODEL200.NAME, mod200.getEnterpriseName())
			 .set(FS_MODEL200.PHONE1, mod200.getEnterprisePhone1())
			 .set(FS_MODEL200.PHONE2, mod200.getEnterprisePhone2())
			 .set(FS_MODEL200.COMPLEMENTARY, (byte) (mod200.isComplementary()?1:0) )
			 .set(FS_MODEL200.RECEIPT,mod200.getReceipt())
			 .set(FS_MODEL200.COMPLEMENTARY_RECEIPT,mod200.getComplementaryReceipt())
			 .set(FS_MODEL200.CNAE,mod200.getCnae())
			 .set(FS_MODEL200.PERIOD_TYPE,(byte) mod200.getPeriodType())
			 .set(FS_MODEL200.PERIOD_START, mod200.getPeriodStart()==null?
					 null:new java.sql.Date(mod200.getPeriodStart().getTime()))
			 .set(FS_MODEL200.PERIOD_END, mod200.getPeriodEnd()==null?
					 null:new java.sql.Date(mod200.getPeriodEnd().getTime()))
			 .set(FS_MODEL200.FISCAL_GROUP,mod200.getFiscalGroup())
			 .set(FS_MODEL200.DOMINANT_DOCUMENT,mod200.getDominantDocument())
			 .set(FS_MODEL200.SECRETARY_DOCUMENT,mod200.getSecretary()==null?null:mod200.getSecretary().getDocument())
			 .set(FS_MODEL200.SECRETARY_NAME,mod200.getSecretary()==null?null:mod200.getSecretary().getName())
			 .set(FS_MODEL200.IRNR,(mod200.getSecretary() != null && mod200.getSecretary().getIrnr() != null)
					 ?new java.sql.Date( mod200.getSecretary().getIrnr().getTime() ):null)
			 .set(FS_MODEL200.RESULT_TYPE,mod200.getResultType())
			 .set(FS_MODEL200.DEV_TYPE,mod200.getDevType())
			 .set(FS_MODEL200.PAY_TYPE,mod200.getPayType())
			 .set(FS_MODEL200.AMOUNT,mod200.getAmount())
			 .set(FS_MODEL200.IBAN,mod200.getIban())
			 .set(FS_MODEL200.COMMENTS,mod200.getComments())
			 .returning()
			 .fetchOne();
		mod200.setId(record.getValue(FS_MODEL200.ID));
		insertDetail(ctx, mod200);	
		insertAdministrator(ctx, mod200);
		return mod200;
	}
	
	private static void insertAdministrator(AONContext ctx,Mod2002013 mod200) {
		List<FsModel200RegistryRecord> list = new LinkedList<FsModel200RegistryRecord>();
		FsModel200RegistryRecord detail = null;
		if (mod200.getAdministrators() != null) {
			for ( CompanyAdministrator ca : mod200.getAdministrators() ) {
				detail = new FsModel200RegistryRecord();
				detail.setFsModel200(mod200.getId());
				detail.setDomain(mod200.getDomain());
				detail.setDocument(ca.getDocument());
				detail.setName(ca.getName());
				detail.setRepresentative( (byte) (ca.isRepresentative()?1:0) );
				detail.setProvince( (byte) ca.getProvince() );
				detail.setType((byte) 0);
				list.add(detail);
			}
		}
		if (mod200.getParticipationsOut() != null) {
			for ( CompanyParticipation cp : mod200.getParticipationsOut() ) {
				detail = new FsModel200RegistryRecord();
				detail.setFsModel200(mod200.getId());
				detail.setDomain(mod200.getDomain());
				detail.setDocument(cp.getDocument());
				detail.setName(cp.getName());
				detail.setProvince( (byte) cp.getProvince() );
				detail.setCountry( cp.getCountry() );
				detail.setType((byte) 1);
				detail.setPercent(cp.getPercent());
				detail.setNominalValue(cp.getNominalValue());
				detail.setBookValue(cp.getBookValue());
				detail.setIncomes(cp.getIncomes());
				detail.setAValue(cp.getaValue());
				detail.setBValue(cp.getbValue());
				detail.setCValue(cp.getcValue());
				detail.setDValue(cp.getdValue());
				detail.setCapital(cp.getCapital());
				detail.setReserve(cp.getReserve());
				detail.setOtherAmounts(cp.getOtherAmounts());
				detail.setResult(cp.getResult());
				list.add(detail);
			}
		}
		if (mod200.getParticipationsIn() != null) {
			for ( CompanyParticipation cp : mod200.getParticipationsIn() ) {
				detail = new FsModel200RegistryRecord();
				detail.setFsModel200(mod200.getId());
				detail.setDomain(mod200.getDomain());
				detail.setDocument(cp.getDocument());
				detail.setName(cp.getName());
				detail.setProvince( (byte) cp.getProvince() );
				detail.setCountry( cp.getCountry() );
				detail.setType((byte) 2);
				detail.setRepresentative( (byte) (cp.isRepresentative()?1:0) );
				detail.setPercent(cp.getPercent());
				detail.setNominalValue(cp.getNominalValue());
				list.add(detail);
			}
		}
		if (mod200.getRepresentatives() != null) {
			for ( LegalRepresentative lr : mod200.getRepresentatives() ) {
				detail = new FsModel200RegistryRecord();
				detail.setFsModel200(mod200.getId());
				detail.setDomain(mod200.getDomain());
				detail.setDocument(lr.getDocument());
				detail.setNotary(lr.getNotary());
				detail.setNotaryDate( lr.getNotaryDate()==null?null:new java.sql.Date( lr.getNotaryDate().getTime() ) );
				detail.setName(lr.getName());
				detail.setType((byte) 3);
				list.add(detail);
			}
		}
		if (!list.isEmpty()) {
			ctx.getDslContext().batchStore(list).execute();
		}
	}

	private static void insertDetail(AONContext ctx, Mod2002013 mod200) {
		List<FsModel200DetailRecord> list = new LinkedList<FsModel200DetailRecord>();
		FsModel200DetailRecord detail = null;
		for (Mod2002013Key k : Mod2002013Key.values()) {
			DoubleVariable2013 dv = null;	
			if (mod200.getDraftMap().containsKey(k)) {
				dv = mod200.getDraftMap().get(k);
			} else {
				dv = mod200.getKeysMap().get(k);
			}
			if (dv != null && dv.getValue() != 0.0){
				detail = new FsModel200DetailRecord();
				detail.setFsModel200(mod200.getId());
				detail.setDomain(mod200.getDomain());
				detail.setKey(dv.getKey().toString());
				detail.setValue(dv.getValue());
				list.add(detail);
			}
		}
		ctx.getDslContext().batchStore(list).execute();
	}
	
	private static Mod2002013 update(AONContext ctx, Mod2002013 mod200)  {
		ctx.getDslContext().update(FS_MODEL200)
		 .set(FS_MODEL200.DOMAIN, mod200.getDomain() )
		 .set(FS_MODEL200.ENTERPRISE, mod200.getEnterprise())
		 .set(FS_MODEL200.YEAR, mod200.getYear())
		 .set(FS_MODEL200.ADMINISTRATION, mod200.getAdministration().getValue())
		 .set(FS_MODEL200.DOCUMENT, mod200.getEnterpriseDocument())
		 .set(FS_MODEL200.NAME, mod200.getEnterpriseName())
		 .set(FS_MODEL200.PHONE1, mod200.getEnterprisePhone1())
		 .set(FS_MODEL200.PHONE2, mod200.getEnterprisePhone2())
		 .set(FS_MODEL200.COMPLEMENTARY, (byte) (mod200.isComplementary()?1:0) )
		 .set(FS_MODEL200.RECEIPT,mod200.getReceipt())
		 .set(FS_MODEL200.COMPLEMENTARY_RECEIPT,mod200.getComplementaryReceipt())
		 .set(FS_MODEL200.CNAE,mod200.getCnae())
		 .set(FS_MODEL200.PERIOD_TYPE,(byte) mod200.getPeriodType())
		 .set(FS_MODEL200.PERIOD_START, mod200.getPeriodStart()==null?
				 null:new java.sql.Date(mod200.getPeriodStart().getTime()))
		 .set(FS_MODEL200.PERIOD_END, mod200.getPeriodEnd()==null?
				 null:new java.sql.Date(mod200.getPeriodEnd().getTime()))
		 .set(FS_MODEL200.COMMENTS,mod200.getComments())
		 .set(FS_MODEL200.FISCAL_GROUP,mod200.getFiscalGroup())
		 .set(FS_MODEL200.DOMINANT_DOCUMENT,mod200.getDominantDocument())
		 .set(FS_MODEL200.SECRETARY_DOCUMENT,mod200.getSecretary()==null?null:mod200.getSecretary().getDocument())
		 .set(FS_MODEL200.SECRETARY_NAME,mod200.getSecretary()==null?null:mod200.getSecretary().getName())
		 .set(FS_MODEL200.IRNR,(mod200.getSecretary() != null && mod200.getSecretary().getIrnr() != null)
				 ?new java.sql.Date( mod200.getSecretary().getIrnr().getTime() ):null)
		 .set(FS_MODEL200.RESULT_TYPE,mod200.getResultType())
		 .set(FS_MODEL200.DEV_TYPE,mod200.getDevType())
		 .set(FS_MODEL200.PAY_TYPE,mod200.getPayType())
		 .set(FS_MODEL200.AMOUNT,mod200.getAmount())
		 .set(FS_MODEL200.IBAN,mod200.getIban())
		 .where(FS_MODEL200.ID.equal(mod200.getId()))
		 .execute();
		deleteDetail(ctx, mod200.getId());
		insertDetail(ctx, mod200);
		deleteRegistry(ctx, mod200.getId());
		insertAdministrator(ctx, mod200);
		return mod200;
	}

	private static void deleteDetail(AONContext ctx, int id ) {
		ctx.getDslContext().delete(FS_MODEL200_DETAIL)
		   .where(FS_MODEL200_DETAIL.FS_MODEL200.equal(id) )
		   .execute();
	}

	private static void deleteRegistry(AONContext ctx, int id ) {
		ctx.getDslContext().delete(FS_MODEL200_REGISTRY)
		   .where(FS_MODEL200_REGISTRY.FS_MODEL200.equal(id) )
		   .execute();
	}
	
	public static void delete(AONContext ctx, int id )  {
		deleteRegistry(ctx, id);
		deleteDetail(ctx, id);
		ctx.getDslContext().delete(FS_MODEL200)
			.where(FS_MODEL200.ID.equal(id) )
			.execute();
	}

	public static Mod2002013 getById(AONContext ctx, int id ) {
		FsModel200Record record = ctx.getDslContext()
				.selectFrom(FS_MODEL200)
				.where(FS_MODEL200.ID.equal(id))
				.fetchOne();
		Mod2002013 mod200 =  populateMod200(ctx,record);
		initializeActiveMap(mod200);
		return mod200;
	}

	public static Mod2002013 getByYear(AONContext ctx, int year) {
		FsModel200Record record = ctx.getDslContext()
				.selectFrom(FS_MODEL200)
				.where(FS_MODEL200.DOMAIN.equal(ctx.getDomainId()))
				.and(FS_MODEL200.YEAR.equal(year))
				.limit(1)
				.fetchOne();
		Mod2002013 mod200 = populateMod200(ctx,record);
		if (mod200 == null) {
			mod200 = new Mod2002013();
			mod200.setDomain(ctx.getDomainId());
			mod200.setYear(year);
			initializeNewMod200(ctx,mod200);
		} else {
			initializeActiveMap(mod200);
		}
		return mod200;
	}

	private static Mod2002013 getMod200(FsModel200Record record) {
		
		Mod2002013 mod200 = new Mod2002013();
		mod200 = new Mod2002013();
		mod200.setId(record.getId());
		mod200.setYear(record.getYear());
		mod200.setDomain(record.getDomain());
		mod200.setAdministration(AonEnumUtils.enumValue(Administration.class, record.getAdministration()));
		mod200.setEnterprise(record.getEnterprise());
		mod200.setEnterpriseDocument(record.getDocument());
		mod200.setEnterpriseName(record.getName());
		mod200.setEnterprisePhone1(record.getPhone1());
		mod200.setEnterprisePhone2(record.getPhone2());
		mod200.setComplementary(record.getComplementary()==1);
		mod200.setComplementaryReceipt(record.getComplementaryReceipt());
		mod200.setCnae(record.getCnae());
		mod200.setPeriodEnd(record.getPeriodEnd());
		mod200.setPeriodStart(record.getPeriodStart());
		mod200.setPeriodType(record.getPeriodType());
		mod200.setReceipt(record.getReceipt());
		mod200.setComments(record.getComments());
		mod200.setFiscalGroup(record.getFiscalGroup());
		mod200.setDominantDocument(record.getDominantDocument());
		Secretary secretary = new Secretary();
		secretary.setDocument(record.getSecretaryDocument());
		secretary.setName(record.getSecretaryName());
		secretary.setIrnr(record.getIrnr());
		mod200.setSecretary(secretary);
		mod200.setComments(record.getComments());
		mod200.setResultType(record.getResultType());
		mod200.setDevType(record.getDevType());
		mod200.setPayType(record.getPayType());
		mod200.setAmount(record.getAmount());
		mod200.setIban(record.getIban());
		return mod200;
	}
	
	private static Mod2002013 populateMod200(AONContext ctx, FsModel200Record record) {
		Mod2002013 mod200 = null;
		if (record != null) {
			mod200 = getMod200(record);
			fillDetail(mod200,ctx);
			fillRegistryLists(mod200,ctx);
		}
		return mod200;
	}
	
	private static void fillDetail(Mod2002013 mod200,AONContext ctx) {
		
		Map<String,Double> map = new HashMap<String,Double>();
		Result<FsModel200DetailRecord> result = ctx.getDslContext()
				.selectFrom(FS_MODEL200_DETAIL)
			 	.where(	FS_MODEL200_DETAIL.FS_MODEL200.equal(mod200.getId()))
			 	.fetch();
		for (FsModel200DetailRecord det : result) {
			map.put(det.getKey(),det.getValue());
		}

		DoubleVariable2013 v = null;
		for (Mod2002013Key key : Mod2002013Key.values() ) {
			v = new DoubleVariable2013( key );
			if (map.containsKey(key.toString())) {
				v.setValue( map.get(key.toString()));
			} else {
				v.setValue( 0.0 );
			}
			mod200.addVariable(v);
		}

		BalanceType bt = null;
		if (map.containsKey( Mod2002013Key.C0050.toString() )) {
			bt = BalanceType.NORMAL;
		} else if (map.containsKey( Mod2002013Key.C0051.toString() )) {
			bt = BalanceType.ABREVIADO;
		} else if (map.containsKey( Mod2002013Key.C0052.toString() )) {
			bt = BalanceType.PYMES;
		}
		mod200.setBalanceType( bt );
		if (map.containsKey( Mod2002013Key.C0053.toString() )) {
			bt = BalanceType.NORMAL;
		} else if (map.containsKey( Mod2002013Key.C0054.toString() )) {
			bt = BalanceType.ABREVIADO;
		} else if (map.containsKey( Mod2002013Key.C0055.toString() )) {
			bt = BalanceType.PYMES;
		}
		mod200.setPygType( bt );
	}
	
	private static void fillRegistryLists(Mod2002013 mod200, AONContext ctx) {
		Result<FsModel200RegistryRecord> res = ctx.getDslContext() 
				.selectFrom(FS_MODEL200_REGISTRY)
			 	.where(	FS_MODEL200_REGISTRY.FS_MODEL200.equal(mod200.getId()))
			 	.fetch();
		CompanyAdministrator ca = null;
		CompanyParticipation cp = null;
		LegalRepresentative lr  = null;
		for (FsModel200RegistryRecord reg : res) {
			if (reg.getType() == 0) {
				ca = new CompanyAdministrator();
				ca.setDocument( reg.getDocument());
				ca.setName( reg.getName());
				ca.setRepresentative( reg.getRepresentative() == 1 );
				ca.setProvince( reg.getProvince() );
				mod200.getAdministrators().add(ca);
			} else if (reg.getType() == 1) {
				cp = new CompanyParticipation();
				cp.setDocument(reg.getDocument());
				cp.setName(reg.getName());
				cp.setProvince( reg.getProvince() );
				cp.setCountry( reg.getCountry() );
				cp.setPercent(reg.getPercent());
				cp.setNominalValue(reg.getNominalValue());
				cp.setBookValue(reg.getBookValue());
				cp.setIncomes(reg.getIncomes());
				cp.setaValue(reg.getAValue());
				cp.setbValue(reg.getBValue());
				cp.setcValue(reg.getCValue());
				cp.setdValue(reg.getDValue());
				cp.setCapital(reg.getCapital());
				cp.setReserve(reg.getReserve());
				cp.setOtherAmounts(reg.getOtherAmounts());
				cp.setResult(reg.getResult());
				mod200.getParticipationsOut().add(cp);
			} if (reg.getType() == 2) {
				cp = new CompanyParticipation();					
				cp.setDocument(reg.getDocument());
				cp.setName(reg.getName());
				cp.setProvince(reg.getProvince() );
				cp.setCountry(reg.getCountry() );				
				cp.setRepresentative( reg.getRepresentative() == 1 );
				cp.setPercent(reg.getPercent());
				cp.setNominalValue(reg.getNominalValue());
				mod200.getParticipationsIn().add(cp);
			} if (reg.getType() == 3) {
				lr = new LegalRepresentative();					
				lr.setDocument(reg.getDocument());
				lr.setName(reg.getName());
				lr.setNotary(reg.getNotary());
				lr.setNotaryDate(reg.getNotaryDate());
				mod200.getRepresentatives().add(lr);
			}
		}
	}
	
	public static Mod2002013 initializeNewMod200(AONContext ctx, Mod2002013 mod200) {
		mod200.setPeriodType(1);
		mod200.setPeriodStart(AonDateUtils.getYearFirstDay(mod200.getYear()));
		mod200.setPeriodEnd(AonDateUtils.getYearLastDay(mod200.getYear()));

		mod200.setBalanceType( BalanceType.ABREVIADO );
		mod200.setPygType(BalanceType.ABREVIADO );
		
		FiscalParameters fiscalParameters = AppParamDAO.getFiscalParameters(ctx);
		
		mod200.setEnterprise(fiscalParameters.getCompany());
		mod200.setEnterpriseDocument(fiscalParameters.getDocument());
		mod200.setEnterpriseName(fiscalParameters.getName());
		mod200.setEnterprisePhone1(fiscalParameters.getContactPhone());
		mod200.setEnterprisePhone2(fiscalParameters.getContactCellular());
		Administration adm = fiscalParameters.getAdministration(Administration.COMMON_TERRITORY);
		mod200.setAdministration(adm);
		return mod200;
	}

	
	public static Mod2002013 initializeMod200(AONContext ctx, Mod2002013 mod200) {
		List<CompanyAdministrator> adms = CompanyDAO.getDirStaff(ctx, mod200.getDomain());
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

		Mod2002013MVELContext mvelCtx = new Mod2002013MVELContext( mod200, ACCEPTER );
		mvelCtx.setAccounts( ACCOUNTING.getAccountBalances(ctx, getParams(ctx,mod200)) );
		mvelCtx.setExpressionMap(INITIALIZE_EXPRESSION_MAP);
		addCharacters(mvelCtx,mod200);
		addBalanceCharacters(mvelCtx,mod200);
		DoubleVariable2013 dv = null;
		for (String stringKey : INITIALIZE_EXPRESSION_MAP.keySet()) {
			Mod2002013Key k = Mod2002013Key.valueOf(stringKey.toString());
			String expression = INITIALIZE_EXPRESSION_MAP.get(stringKey);
			mvelCtx.put(stringKey, 0.0 );
			Object ret = mvelCtx.evaluateExpression(stringKey,expression);
			mvelCtx.put(stringKey, ret );
			if (ret instanceof Double ) {
				dv = new DoubleVariable2013( k );
				dv.setValue( (Double) ret );
				mod200.addVariable( dv );
			} 
			if (ret instanceof Boolean) {
				dv = new DoubleVariable2013( k );
				dv.setValue((Boolean) ret );
				mod200.addVariable( dv );
			}
		}		
		calculate(mod200,false);
		initializeActiveMap(mod200);
		return mod200;
	}
	
	public static Mod2002013 calculate(Mod2002013 mod200) {
		return calculate(mod200,true);
	}
	
	public static Mod2002013 calculate(Mod2002013 mod200, boolean addToDraft) {
		try {
			Mod2002013MVELContext ctx = new Mod2002013MVELContext( mod200, ACCEPTER );
			ctx.setExpressionMap(Mod2002013Compute.COMPUTE_EXPRESSION_MAP);
			for (DoubleVariable2013 dv : mod200.getKeysMap().values()) {
				ctx.put(dv.getKey().toString(), dv.getValue());
			}
			addCharacters(ctx,mod200);
			addBalanceCharacters(ctx,mod200);
			
			DoubleVariable2013 d = null;
			for (Mod2002013Key key : mod200.getDraftMap().keySet() ) {
				d = mod200.getDraftMap().get(key);
				if (d.isChangedByUser()) {
					ctx.put(key.toString(), d.getValue());
				}
			}
			
			DoubleVariable2013 v = null;
			for (String stringKey : Mod2002013Compute.COMPUTE_EXPRESSION_MAP.keySet()) {
				Mod2002013Key k = Mod2002013Key.valueOf(stringKey.toString());
				String expression = Mod2002013Compute.COMPUTE_EXPRESSION_MAP.get(stringKey);
				DoubleVariable2013 existingVariable = mod200.getVariable(k);
				if ( existingVariable == null ) {
					existingVariable = new DoubleVariable2013( k );
					existingVariable.setValue( 0.0 );
				}
				ctx.put(stringKey, existingVariable.getValue());
				Object ret = ctx.evaluateExpression(stringKey,expression);
				
				if (ret instanceof Double) {
					Double calculated = (Double) ret;
					ctx.put(stringKey, calculated);
					if ( !AonMathUtils.equals( existingVariable.getValue() , calculated ) ) {
						v = new DoubleVariable2013( k );
						v.setValue( calculated );
						if (addToDraft) {
							mod200.addDraftVariable(v);	
						} else {
							mod200.addVariable(v);
						}
					}
				}
			}
			v = mod200.getVariable(Mod2002013Key.BN621);
			mod200.setResultType(null);
			if (v == null || v.getValue() == 0) {
				mod200.setResultType("C");
				mod200.setAmount( 0.0 );
				mod200.setDevType(null);
				mod200.setPayType(null);
			} else if (AonMathUtils.round(v.getValue()) < 0.0) {
				mod200.setResultType("D");
				mod200.setAmount( AonMathUtils.round( v.getValue() * -1));
				mod200.setDevType(AonStringUtils.isEmpty(mod200.getDevType())?"R":mod200.getDevType());
				mod200.setPayType(null);
			} else {
				mod200.setResultType("I");
				mod200.setAmount( v.getValue() );
				mod200.setPayType(AonStringUtils.isEmpty(mod200.getPayType())?"H":mod200.getPayType());
				mod200.setDevType(null);
			}
			return mod200;
		} catch (Throwable e) {
			e.printStackTrace();
			throw new AonCoreException(e);
		}
	}
	
	private static AccMiningParameters getParams(AONContext ctx,Mod2002013 mod200) throws AonCoreException {
		AccMiningParameters params = new AccMiningParameters();
		params.setDomain(mod200.getDomain());
		params.setYear(mod200.getYear());
		 
		AccountPeriod period =  AccountPeriodDAO.fetchOneByYear(ctx, mod200.getYear());
		if (period == null) {
			throw new AonCoreException("Ejercicio '"+mod200.getYear()+"' no encontrado.");
		}
		if (!period.isClosed()) {
			throw new AonCoreException("Cierre el ejercicio contable '"+mod200.getYear()+"' para poder continuar.");
		}
		params.setPeriodId(period.getId());
		params.setStartDate(period.getInitiationDate());
		params.setEndDate(period.getDeadline());
		return params;
	}

	private static void addCharacters(Mod2002013MVELContext ctx, Mod2002013 mod200) {
		for (Mod2002013Key key : Mod2002013Character.CHARACTERS_KEYS) {
			ctx.put(key.toString(), 
					(mod200.getKeysMap().containsKey(key) 
				 && (AonMathUtils.equals(mod200.getKeysMap().get(key).getValue(),1.0))));	
		}
	}
	
	private static void initializeActiveMap(Mod2002013 mod200) {
		Mod2002013MVELContext ctx = new Mod2002013MVELContext( mod200, ACCEPTER );
		ctx.setExpressionMap(Mod2002013Activation.ACTIVE_EXPRESSION_MAP);
		addCharacters(ctx,mod200);
		for (Mod2002013Key key : Mod2002013Key.values() ) {
			if (Mod2002013Activation.ACTIVE_EXPRESSION_MAP.containsKey(key.toString()) ) {
				Object ret = ctx.get( key.toString() );
				if (ret instanceof Boolean && ((Boolean) ret) ) {
					mod200.getVisibleMap().put(key,true);
				}
			} else {
				mod200.getVisibleMap().put(key,true);
			}
		}
	}

	private static void addBalanceCharacters(Mod2002013MVELContext ctx, Mod2002013 mod200) {
		ctx.put(Mod2002013Key.C0050.toString(), mod200.getBalanceType() == BalanceType.NORMAL);
		ctx.put(Mod2002013Key.C0051.toString(), mod200.getBalanceType() == BalanceType.ABREVIADO);
		ctx.put(Mod2002013Key.C0052.toString(), mod200.getBalanceType() == BalanceType.PYMES);

		ctx.put(Mod2002013Key.C0053.toString(), mod200.getPygType() == BalanceType.NORMAL);
		ctx.put(Mod2002013Key.C0054.toString(), mod200.getPygType() == BalanceType.ABREVIADO);
		ctx.put(Mod2002013Key.C0055.toString(), mod200.getPygType() == BalanceType.PYMES);
	}
	
	public static Mod2002013 validate(Mod2002013 mod200) {
		List<ValidationMessage2013> list = new LinkedList<ValidationMessage2013>();
		validateComplementary(list,mod200);
		validateDocument(list,mod200);
		validateCNAE(list,mod200);
		validateSecretary(list,mod200);
		validateRepresentatives(list,mod200);
		validateAdministrators(list,mod200);
		validateParticipationsIn(list,mod200);
		validateParticipationsOut(list,mod200);
		Mod2002013MVELContext ctx = new Mod2002013MVELContext( mod200, ACCEPTER );
		for (DoubleVariable2013 dv : mod200.getKeysMap().values()) {
			ctx.put(dv.getKey().toString(), dv.getValue());
		}
		addCharacters(ctx,mod200);
		DoubleVariable2013 d = null;
		for (Mod2002013Key key : mod200.getDraftMap().keySet() ) {
			d = mod200.getDraftMap().get(key);
			ctx.put(key.toString(), d.getValue());
		}
		mod200.setMessages(null);	
		for (ValidationMessage2013 validation : VALIDATION_EXPRESSION_LIST) {
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
	
	private static final int PAGE00 = 0;
	private static final int PAGE01 = 1;
	private static final int PAGE02 = 2;
	
	
	private static void validateComplementary(List<ValidationMessage2013> list, Mod2002013 mod200) {
		if (mod200.isComplementary() ) {
			if (AonStringUtils.isEmpty(mod200.getComplementaryReceipt() )) {
				list.add(new ValidationMessage2013(PAGE00,"Si marca Decl. Complementaria, debe indicar un n. de justificante anterior."));
			} else {
				if (mod200.getComplementaryReceipt().length() != 13) {
					list.add(new ValidationMessage2013(PAGE00,"El n. de justificante anterior debe tener 13 caracteres."));
				}
				if (!mod200.getComplementaryReceipt().startsWith("200") && !mod200.getComplementaryReceipt().startsWith("206")) {
					list.add(new ValidationMessage2013(PAGE00,"El n. de justificante anterior debe empezar por 200 o 206."));
				}
			}
			
		} else if (!mod200.isComplementary() && !AonStringUtils.isEmpty(mod200.getComplementaryReceipt())) {
			list.add(new ValidationMessage2013(PAGE00,"Si no marca Decl. Complementaria, no debe indicar un n. de justificante anterior."));
		}
	}

	private static void validateDocument(List<ValidationMessage2013> list, Mod2002013 mod200) {
		if (!AonDocumentUtil.isValid(mod200.getEnterpriseDocument())) {
			list.add(new ValidationMessage2013(PAGE00,"NIF de la declaraci\u00F3n incorrecto."));
		}
		
	}
	private static void validateCNAE(List<ValidationMessage2013> list, Mod2002013 mod200) {
		if (AonStringUtils.isEmpty(mod200.getCnae())) {
			list.add(new ValidationMessage2013(PAGE00,"Rellene el CNAE de la empresa."));
		} else if (CNAE.valueOfCode(mod200.getCnae()) == null) {
			list.add(new ValidationMessage2013(PAGE00,"CNAE de la empresa, no válido."));	
		}
	}
	private static void validateAdministrators(List<ValidationMessage2013> list,Mod2002013 mod200) {
		if (mod200.getAdministrators() == null || mod200.getAdministrators().size() == 0 ) {
			list.add(new ValidationMessage2013(PAGE01,"Debe rellenar al menos un administrador."));
		} else {
			for (int i = 0; i < mod200.getAdministrators().size(); i++ ) {
				CompanyAdministrator ca = mod200.getAdministrators().get(i); 
				if (!AonDocumentUtil.isValid(ca.getDocument())) {
					list.add(new ValidationMessage2013(PAGE01,"NIF del administrador nº "+(i+1) +" incorrecto ["+ca.getDocument()+"]"));		
				}
				if (AonStringUtils.isEmpty(ca.getName())) {
					list.add(new ValidationMessage2013(PAGE01,"Falta nombre del administrador nº "+(i+1) +". ["+ca.getDocument()+"]"));
				}
			}
		}
	}
	
	private static void validateSecretary(List<ValidationMessage2013> list, Mod2002013 mod200) {
		if (AonDocumentUtil.isEntity(mod200.getEnterpriseDocument())) {
			if (mod200.getSecretary() == null) {
				list.add(new ValidationMessage2013(PAGE01,"Para personas jur\u00EDdicas, debe rellenar los datos del secretario"));
			} else {
				if (!AonDocumentUtil.isValid(mod200.getSecretary().getDocument())) {
					list.add(new ValidationMessage2013(PAGE01,"NIF del secretario incorrecto."));
				}
				if ( AonStringUtils.isEmpty(mod200.getSecretary().getName())) {
					list.add(new ValidationMessage2013(PAGE01,"Falta nombre del secretario."));
				} else if (mod200.getSecretary().getName().length() > 25) {
					list.add(new ValidationMessage2013(PAGE01,"Longitud excedida en el nombre del secretario. Debe limitarse a 25 caracteres."));	
				}
				if (mod200.getSecretary().getIrnr() == null && (mod200.isChecked(Mod2002013Key.C0021) || mod200.isChecked(Mod2002013Key.C0046)) ) {
					list.add(new ValidationMessage2013(PAGE01,"Falta fecha IRNR."));
				}
			}
		}
	}

	private static void validateRepresentatives(List<ValidationMessage2013> list,Mod2002013 mod200) {
		if (AonDocumentUtil.isEntity(mod200.getEnterpriseDocument())) {
			if (mod200.getRepresentatives() == null || mod200.getRepresentatives().size() == 0 ) {
				list.add(new ValidationMessage2013(PAGE01,"Para personas jur\u00EDdicas, debe rellenar al menos un representante."));
			} else {
				for (int i = 0; i < mod200.getRepresentatives().size(); i++ ) {
					LegalRepresentative lr = mod200.getRepresentatives().get(i); 
					if (!AonDocumentUtil.isValid(lr.getDocument())) {
						list.add(new ValidationMessage2013(PAGE01,"NIF del representante legal nº "+(i+1) +" incorrecto ["+lr.getDocument()+"]"));		
					}
					if (AonStringUtils.isEmpty(lr.getName())) {
						list.add(new ValidationMessage2013(PAGE01,"Falta nombre del representante legal nº "+(i+1) +". ["+lr.getDocument()+"]"));
					}
					if (AonStringUtils.isEmpty(lr.getNotary())) {
						list.add(new ValidationMessage2013(PAGE01,"Falta el dato de la notar\u00EDa del representante legal nº "+(i+1) +". ["+lr.getDocument()+"]"));
					} else if (lr.getNotary().length() > 20) {
						list.add(new ValidationMessage2013(PAGE01,"Longitud excedida en la notar\u00EDa del representante legal nº "+(i+1) +". ["+lr.getDocument()+"]. Debe limitarse a 20 caracteres."));	
					}
					if (lr.getNotaryDate() == null) {
						list.add(new ValidationMessage2013(PAGE01,"Falta el dato fecha de la notar\u00EDa del representante legal nº "+(i+1) +". ["+lr.getDocument()+"]"));
					}
				}
			}
		}
	}
	
	private static void validateParticipationsIn(List<ValidationMessage2013> list,Mod2002013 mod200) {
		 if (AonDocumentUtil.isEntity(mod200.getEnterpriseDocument())) {
			 List<CompanyParticipation> participations = mod200.getParticipationsIn();
			if (participations == null || participations.size() == 0) {
				list.add(new ValidationMessage2013(PAGE02,"Para personas jur\u00EDdicas, debe rellenar los datos de participaci\u00F3n en la declarante"));
			} else {
				for (int i = 0; i < participations.size(); i++ ) {
					CompanyParticipation cp = participations.get(i); 
					if (!AonDocumentUtil.isValid(cp.getDocument())) {
						list.add(new ValidationMessage2013(PAGE02,"NIF de la participaci\u00F3n en la declarante nº "+(i+1) +" incorrecto ["+cp.getDocument()+"]"));		
					}
					if (AonStringUtils.isEmpty(cp.getName())) {
						list.add(new ValidationMessage2013(PAGE02,"Falta nombre de la participaci\u00F3n en la declarante nº "+(i+1) +". ["+cp.getDocument()+"]"));
					}
					if (cp.getPercent() < 0 || cp.getPercent() > 100) {
						list.add(new ValidationMessage2013(PAGE02,"Porcentaje no correcto en la participaci\u00F3n en la declarante nº "+(i+1) +". ["+cp.getDocument()+"]"));
					}
				}
			}
		}
	}
	
	private static void validateParticipationsOut(List<ValidationMessage2013> list,Mod2002013 mod200) {
		 if (AonDocumentUtil.isEntity(mod200.getEnterpriseDocument())) {
			 List<CompanyParticipation> participations = mod200.getParticipationsOut();
			if (participations == null || participations.size() == 0) {
			} else {
				for (int i = 0; i < participations.size(); i++ ) {
					CompanyParticipation cp = participations.get(i); 
					if (!AonDocumentUtil.isValid(cp.getDocument())) {
						list.add(new ValidationMessage2013(PAGE02,"NIF de la participaci\u00F3n de la declarante en otras nº "+(i+1) +" incorrecto ["+cp.getDocument()+"]"));		
					}
					if (AonStringUtils.isEmpty(cp.getName())) {
						list.add(new ValidationMessage2013(PAGE02,"Falta nombre de la participaci\u00F3n de la declarante en otras nº "+(i+1) +". ["+cp.getDocument()+"]"));
					}
					if (cp.getPercent() < 0 || cp.getPercent() > 100) {
						list.add(new ValidationMessage2013(PAGE02,"Porcentaje no correcto en la participaci\u00F3n de la declarante en otras nº "+(i+1) +". ["+cp.getDocument()+"]"));
					}
				}
			}
		}
	}
	
	public static String dumpAEAT(Mod2002013 mod200)  {
		try {
			MOD2002013 mod = Mod2002013toMOD2002013.getMOD2002013(mod200);
			StringWriter writer = new StringWriter();
			JAXBContext context = JAXBContext.newInstance(MOD2002013.class);
			Marshaller um = context.createMarshaller();
			um.setProperty("jaxb.encoding", "ISO-8859-1");
			um.marshal(mod,writer);
			return writer.toString();
		} catch (JAXBException e) {
			throw new AonCoreException(e.getMessage(),e);
		}				
	}	
}
