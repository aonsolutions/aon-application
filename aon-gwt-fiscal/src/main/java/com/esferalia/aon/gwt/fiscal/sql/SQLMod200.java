package com.esferalia.aon.gwt.fiscal.sql;

import static com.esferalia.aon.jooq.tables.FsModel200.FS_MODEL200;
import static com.esferalia.aon.jooq.tables.FsModel200Detail.FS_MODEL200_DETAIL;
import static com.esferalia.aon.jooq.tables.FsModel200Registry.FS_MODEL200_REGISTRY;

import java.sql.Connection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.code.aon.accounting.util.AccountingUtil;
import com.esferalia.aon.gwt.common.shared.AonSQLException;
import com.esferalia.aon.gwt.common.shared.CompanyAdministrator;
import com.esferalia.aon.gwt.common.shared.CompanyParticipation;
import com.esferalia.aon.gwt.fiscal.shared.mod200.DoubleVariable;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200.BalanceType;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Key;
import com.esferalia.aon.jooq.tables.records.FsModel200DetailRecord;
import com.esferalia.aon.jooq.tables.records.FsModel200Record;
import com.esferalia.aon.jooq.tables.records.FsModel200RegistryRecord;

public class SQLMod200 {
	
	public static Mod200 save(Connection conn, Mod200 mod200) throws AonSQLException {
		DSLContext dsl = DSL.using(conn, AccountingUtil.getDefaultSettings());
		if (mod200.getId() == null) {
			return insert(conn, dsl, mod200);
		} else {
			return update(conn, dsl, mod200);
		}
	}

	public static void delete(Connection conn, Mod200 mod200) throws AonSQLException {
		DSLContext dsl = DSL.using(conn, AccountingUtil.getDefaultSettings());
		deleteRegistry(conn, dsl, mod200);
		deleteDetail(conn, dsl, mod200);
		dsl.delete(FS_MODEL200)
			.where(FS_MODEL200.ID.equal(mod200.getId()) )
			.execute();
	}

	private static Mod200 insert(Connection conn,DSLContext dsl, Mod200 mod200) throws AonSQLException {
		FsModel200Record record = 
			dsl.insertInto(FS_MODEL200)
			 .set(FS_MODEL200.DOMAIN, mod200.getDomain() )
			 .set(FS_MODEL200.ENTERPRISE, mod200.getEnterprise())
			 .set(FS_MODEL200.YEAR, mod200.getYear())
			 .set(FS_MODEL200.ADMINISTRATION, (byte) mod200.getAdministration())
			 .set(FS_MODEL200.DOCUMENT, mod200.getEnterpriseDocument())
			 .set(FS_MODEL200.NAME, mod200.getEnterpriseName())
			 .set(FS_MODEL200.COMPLEMENTARY, (byte) (mod200.isComplementary()?0:1) )
			 .set(FS_MODEL200.RECEIPT,mod200.getReceipt())
			 .set(FS_MODEL200.COMPLEMENTARY_RECEIPT,mod200.getComplementaryReceipt())
			 .set(FS_MODEL200.CNAE,mod200.getCnae())
			 .set(FS_MODEL200.PERIOD_TYPE,(byte) mod200.getPeriodType())
			 .set(FS_MODEL200.PERIOD_START, mod200.getPeriodStart()==null?
					 null:new java.sql.Date(mod200.getPeriodStart().getTime()))
			 .set(FS_MODEL200.PERIOD_END, mod200.getPeriodEnd()==null?
					 null:new java.sql.Date(mod200.getPeriodEnd().getTime()))
			 .set(FS_MODEL200.COMMENTS,mod200.getComments())
			 .returning()
			 .fetchOne();
		mod200.setId(record.getValue(FS_MODEL200.ID));
		insertDetail(conn, dsl, mod200);	
		insertAdministrator(conn, dsl, mod200);
		return mod200;
	}
	
	private static void insertAdministrator(Connection conn, DSLContext dsl,Mod200 mod200) {
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
				detail.setType((byte) 2);
				detail.setRepresentative( (byte) (cp.isRepresentative()?1:0) );
				detail.setPercent(cp.getPercent());
				detail.setNominalValue(cp.getNominalValue());
				list.add(detail);
			}
		}
		if (!list.isEmpty()) {
			dsl.batchStore(list).execute();
		}
	}

	private static void insertDetail(Connection conn,DSLContext dsl, Mod200 mod200) throws AonSQLException {
		List<FsModel200DetailRecord> list = new LinkedList<FsModel200DetailRecord>();
		FsModel200DetailRecord detail = null;
		for (DoubleVariable dv : mod200.getKeysMap().values() ){
			if (mod200.getDraftMap().containsKey(dv.getKey())) {
				dv = mod200.getDraftMap().get(dv.getKey());
			}
			if (dv.getValue() != 0.0){
				detail = new FsModel200DetailRecord();
				detail.setFsModel200(mod200.getId());
				detail.setDomain(mod200.getDomain());
				detail.setKey(dv.getKey().toString());
				detail.setValue(dv.getValue());
				list.add(detail);
			}
		}
		dsl.batchStore(list).execute();
	}
	

	private static void deleteDetail(Connection conn,DSLContext dsl, Mod200 mod200) throws AonSQLException {
		dsl.delete(FS_MODEL200_DETAIL)
		   .where(FS_MODEL200_DETAIL.FS_MODEL200.equal(mod200.getId()) )
		   .execute();
	}

	private static void deleteRegistry(Connection conn,DSLContext dsl, Mod200 mod200) throws AonSQLException {
		dsl.delete(FS_MODEL200_REGISTRY)
		   .where(FS_MODEL200_REGISTRY.FS_MODEL200.equal(mod200.getId()) )
		   .execute();
	}

	private static Mod200 update(Connection conn,DSLContext dsl, Mod200 mod200) throws AonSQLException {
		dsl.update(FS_MODEL200)
		 .set(FS_MODEL200.DOMAIN, mod200.getDomain() )
		 .set(FS_MODEL200.ENTERPRISE, mod200.getEnterprise())
		 .set(FS_MODEL200.YEAR, mod200.getYear())
		 .set(FS_MODEL200.ADMINISTRATION, (byte) mod200.getAdministration())
		 .set(FS_MODEL200.DOCUMENT, mod200.getEnterpriseDocument())
		 .set(FS_MODEL200.NAME, mod200.getEnterpriseName())
		 .set(FS_MODEL200.COMPLEMENTARY, (byte) (mod200.isComplementary()?0:1) )
		 .set(FS_MODEL200.RECEIPT,mod200.getReceipt())
		 .set(FS_MODEL200.COMPLEMENTARY_RECEIPT,mod200.getComplementaryReceipt())
		 .set(FS_MODEL200.CNAE,mod200.getCnae())
		 .set(FS_MODEL200.PERIOD_TYPE,(byte) mod200.getPeriodType())
		 .set(FS_MODEL200.PERIOD_START, mod200.getPeriodStart()==null?
				 null:new java.sql.Date(mod200.getPeriodStart().getTime()))
		 .set(FS_MODEL200.PERIOD_END, mod200.getPeriodEnd()==null?
				 null:new java.sql.Date(mod200.getPeriodEnd().getTime()))
		 .set(FS_MODEL200.COMMENTS,mod200.getComments())
		 .where(FS_MODEL200.ID.equal(mod200.getId()))
		 .execute();
		deleteDetail(conn, dsl, mod200);
		insertDetail(conn, dsl, mod200);
		return mod200;
	}

	public static Mod200 get(Connection conn, int domain, int year) {
		DSLContext dsl = DSL.using(conn, AccountingUtil.getDefaultSettings());
		FsModel200Record record = dsl
				.selectFrom(FS_MODEL200)
				.where(FS_MODEL200.DOMAIN.equal(domain))
				.and(FS_MODEL200.YEAR.equal(year))
				.fetchOne();
		Mod200 mod200 = null;
		if (record != null) {
			mod200 = getMod200(record);
			fillDetail(mod200,dsl);
			fillRegistryLists(mod200,dsl);
		}
		return mod200;
	}

	private static void fillDetail(Mod200 mod200,DSLContext dsl) {
		
		Map<String,Double> map = new HashMap<String,Double>();
		Result<FsModel200DetailRecord> result = dsl.selectFrom(FS_MODEL200_DETAIL)
			 	.where(	FS_MODEL200_DETAIL.FS_MODEL200.equal(mod200.getId()))
			 	.fetch();
		for (FsModel200DetailRecord det : result) {
			map.put(det.getKey(),det.getValue());
		}

		DoubleVariable v = null;
		for (Mod200Key key : Mod200Key.values() ) {
			v = new DoubleVariable( key );
			if (map.containsKey(key.toString())) {
				v.setValue( map.get(key.toString()));
			} else {
				v.setValue( 0.0 );
			}
			mod200.addVariable(v);
		}

		BalanceType bt = null;
		if (map.containsKey( Mod200Key.C0050.toString() )) {
			bt = BalanceType.NORMAL;
		} else if (map.containsKey( Mod200Key.C0051.toString() )) {
			bt = BalanceType.ABREVIADO;
		} else if (map.containsKey( Mod200Key.C0052.toString() )) {
			bt = BalanceType.PYMES;
		}
		mod200.setBalanceType( bt );
		if (map.containsKey( Mod200Key.C0053.toString() )) {
			bt = BalanceType.NORMAL;
		} else if (map.containsKey( Mod200Key.C0054.toString() )) {
			bt = BalanceType.ABREVIADO;
		} else if (map.containsKey( Mod200Key.C0055.toString() )) {
			bt = BalanceType.PYMES;
		}
		mod200.setPygType( bt );
	}

	private static Mod200 getMod200(FsModel200Record record) {
		Mod200 mod200 = new Mod200();
		mod200 = new Mod200();
		mod200.setId(record.getId());
		mod200.setYear(record.getYear());
		mod200.setDomain(record.getDomain());
		mod200.setAdministration(record.getAdministration());
		mod200.setEnterprise(record.getEnterprise());
		mod200.setEnterpriseDocument(record.getDocument());
		mod200.setEnterpriseName(record.getName());
		mod200.setComplementary(record.getComplementary()==1);
		mod200.setComplementaryReceipt(record.getComplementaryReceipt());
		mod200.setCnae(record.getCnae());
		mod200.setPeriodEnd(record.getPeriodEnd());
		mod200.setPeriodStart(record.getPeriodStart());
		mod200.setPeriodType(record.getPeriodType());
		mod200.setReceipt(record.getReceipt());
		mod200.setComments(record.getComments());
		return mod200;
	}

	private static void fillRegistryLists(Mod200 mod200, DSLContext dsl) {
		List<CompanyAdministrator> administrators = new LinkedList<CompanyAdministrator>();
		List<CompanyParticipation> participationsIn = new LinkedList<CompanyParticipation>();
		List<CompanyParticipation> participationsOut = new LinkedList<CompanyParticipation>();
		Result<FsModel200RegistryRecord> res = 
				dsl.selectFrom(FS_MODEL200_REGISTRY)
			 	.where(	FS_MODEL200_REGISTRY.FS_MODEL200.equal(mod200.getId()))
			 	.fetch();
		CompanyAdministrator ca = null;
		CompanyParticipation cp = null;
		for (FsModel200RegistryRecord reg : res) {
			if (reg.getType() == 0) {
				ca = new CompanyAdministrator();
				ca.setDocument( reg.getDocument());
				ca.setName( reg.getName());
				ca.setRepresentative( reg.getRepresentative() == 1 );
				ca.setProvince( reg.getProvince() );
				administrators.add(ca);
			} else if (reg.getType() == 1) {
				cp = new CompanyParticipation();
				cp.setDocument(reg.getDocument());
				cp.setName(reg.getName());
				cp.setProvince( reg.getProvince() );
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
				participationsOut.add(cp);
			} else {
				cp = new CompanyParticipation();					
				cp.setDocument(reg.getDocument());
				cp.setName(reg.getName());
				cp.setProvince(reg.getProvince() );
				cp.setRepresentative( reg.getRepresentative() == 1 );
				cp.setPercent(reg.getPercent());
				cp.setNominalValue(reg.getNominalValue());
				participationsIn.add(cp);
			}
		}
		mod200.setAdministrators(administrators);
		mod200.setParticipationsIn(participationsIn);
		mod200.setParticipationsOut(participationsOut);
	}
	

}
