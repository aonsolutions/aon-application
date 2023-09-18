package com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2016;

import static com.esferalia.aon.jooq.tables.FsModel200.FS_MODEL200;
import static com.esferalia.aon.jooq.tables.FsModel200Detail.FS_MODEL200_DETAIL;
import static com.esferalia.aon.jooq.tables.FsModel200Registry.FS_MODEL200_REGISTRY;
import static com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2016.Mod2002016Initialization.INITIALIZE_EXPRESSION_MAP;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedList;

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
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.accounting.AccMiningParameters;
import com.esferalia.aon.occam.api.model.accounting.AccountBalance;
import com.esferalia.aon.occam.api.model.accounting.IAccMiningKeyAccept;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.LegalRepresentative;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ConfigurationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod202.Mod202DAO;
import com.esferalia.aon.occam.mod200.api.model.Mod200CompanyAdministrator;
import com.esferalia.aon.occam.mod200.api.model.Mod200CompanyParticipation;
import com.esferalia.aon.occam.mod200.api.model.Secretary;
import com.esferalia.aon.occam.mod200.api.model.UteBase;
import com.esferalia.aon.occam.mod200.api.model.UteForeign;
import com.esferalia.aon.occam.mod200.api.model.UteParticipation;
import com.esferalia.aon.occam.mod200.api.model.mod200_2015.Mod2002015;
import com.esferalia.aon.occam.mod200.api.model.mod200_2016.DoubleVariable2016;
import com.esferalia.aon.occam.mod200.api.model.mod200_2016.Mod2002016;
import com.esferalia.aon.occam.mod200.api.model.mod200_2016.Mod2002016.BalanceType;
import com.esferalia.aon.occam.mod200.api.model.mod200_2016.Mod2002016Character;
import com.esferalia.aon.occam.mod200.api.model.mod200_2016.Mod2002016Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2016.ValidationMessage2016;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.Mod200DAO;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2015.Mod2002015DAO;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2016.jaxb.MOD2002016;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2016.jaxb.Mod2002016toMOD2002016;
import com.esferalia.aon.occam.mod200.server.format.mod200_2016.Mod2002016Import2015;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class Mod2002016DAO  {
	
	@FunctionalInterface
	private static interface IPopulater {
		boolean populate(Mod2002016 mod,FsModel200RegistryRecord reg);
	}

	private static enum Mod2002016RegistryType {
		 ADMINISTRATOR ( 
			(mod,reg) -> mod.getAdministrators().add(new Mod200CompanyAdministrator()
				.setDocument( reg.getDocument())
				.setName( reg.getName())
				.setRepresentative( reg.getRepresentative() == 1 )
				.setResidence(reg.getResidence())
				.setProvince( reg.getProvince() )))
		,PARTICPATION_OUT( 
			(mod,reg) -> mod.getParticipationsOut().add(new Mod200CompanyParticipation()
				.setDocument(reg.getDocument())
				.setName(reg.getName())
				.setProvince( reg.getProvince() )
				.setCountry( reg.getCountry() )
				.setPercent(reg.getPercent())
				.setNominalValue(reg.getNominalValue())
				.setBookValue(reg.getBookValue())
				.setIncomes(reg.getIncomes())
				.setValueCorrection(reg.getAValue())
				.setLossReversion(reg.getBValue())
				.setAccountingElimination(reg.getCcValue())
				.setCorrectionEffect(reg.getCValue())
				.setCorrectionsBalance(reg.getDValue())
				.setCapital(reg.getCapital())
				.setReserve(reg.getReserve())
				.setOtherAmounts(reg.getOtherAmounts())
				.setResult(reg.getResult())))
		,PARTICPATION_IN( 
			(mod,reg) -> mod.getParticipationsIn().add(new Mod200CompanyParticipation()				
				.setDocument(reg.getDocument())
				.setName(reg.getName())
				.setProvince(reg.getProvince() )
				.setCountry( reg.getCountry() )
				.setRepresentative( reg.getRepresentative() == 1 )
				.setPercent(reg.getPercent())
				.setNominalValue(reg.getNominalValue())))
		,REPRESENTATIVE( 
			(mod,reg) -> mod.getRepresentatives().add(new LegalRepresentative()					
				.setDocument(reg.getDocument())
				.setName(reg.getName())
				.setNotary(reg.getNotary())
				.setNotaryDate(reg.getNotaryDate())))
		,UTE_PARTICIPATION( 
			(mod,reg) -> mod.getUteParticipations().add(new UteParticipation()					
				.setDocument(reg.getDocument())
				.setName(reg.getName())
				.setProvince(reg.getProvince() )
				.setCountry( reg.getCountry() )
				.setRepresentative( reg.getRepresentative() == 1 )
				.setPercent(reg.getPercent())
				.setBase(reg.getNominalValue())))
		,UTE_FOREIGN( 
			(mod,reg) -> mod.getUteForeign().add(new UteForeign()
			 	.setIdentification(reg.getName())
			 	.setCountry( reg.getCountry() )
				.setVolume(reg.getAValue())
				.setPyg(reg.getBValue())
				.setAdjust(reg.getCValue())
				.setDeduction(reg.getDValue())))
		,UTE_BASE( 
			(mod,reg) -> mod.getUteBases().add(new UteBase()
				.setPercent(reg.getPercent())
				.setBase(reg.getNominalValue())))
		,GROUP_ENTITIES( 
			(mod,reg) -> mod.getGroupEntities().add(reg.getDocument()))
		,ESTABLISHMENTS( 
			(mod,reg) -> mod.getEstablishments().add(reg.getDocument()))
		;
		
		private IPopulater populater;
		private Mod2002016RegistryType( IPopulater populater){
			this.populater = populater;
		}
		
		private boolean accept(FsModel200RegistryRecord reg) {
			return reg.getType() == this.ordinal();
		}
		private void _populate(Mod2002016 mod,FsModel200RegistryRecord reg) {
			if ( accept(reg) ) {
				this.populater.populate(mod, reg);
			}
		}
		
		public static void populate(Mod2002016 mod,FsModel200RegistryRecord reg) {
			Mod2002016RegistryType type = Mod2002016RegistryType.safeValueOf(reg.getType());
			if (type != null) {
				type._populate(mod, reg);
			}
		}
		
		private static Mod2002016RegistryType safeValueOf( Byte i ) {
			if (i == null) return null;
			return safeValueOf( i.intValue() ); 
		}
		private static Mod2002016RegistryType safeValueOf( Integer i ) {
			if (i == null) return null;
			if (i < 0 || i >= Mod2002016RegistryType.values().length) return null;
			return Mod2002016RegistryType.values()[i];
		}

		private byte byteValue() {
			return (byte) ordinal();
		}
		
	}
	
	private static final IAccMiningKeyAccept ACCEPTER = new IAccMiningKeyAccept() {
		
		@Override
		public boolean acceptKey(Object key) {
			try {
				return (Mod2002016Key.valueOf((String) key) != null);	
			} catch (IllegalArgumentException e) {
				return false;
			}
		}
	};

	public static Mod2002016 save(AONContext ctx, Mod2002016 mod200) {
		try {
			ctx.log().info("------ [START] SAVE MOD 200");
			if (mod200.getPeriodType() == 1) {
				mod200.setPeriodStart(AonDateUtils.getYearFirstDay(2016));
				mod200.setPeriodEnd(AonDateUtils.getYearLastDay(2016));
			}
			if ( AonStringUtils.length(mod200.getEnterpriseDocument()) > 9)
				throw new AonCoreException("El documento del presentador no puede superar 9 caracteres.");
			if ( AonStringUtils.length(mod200.getEnterpriseName()) > 45)
				throw new AonCoreException("La raz\u00F3n social del presentador no puede superar 45 caracteres.");
			if ( AonStringUtils.length(mod200.getEnterprisePhone1()) > 9)
				throw new AonCoreException("El teléfono 1 del presentador no puede superar 9 caracteres.");
			if ( AonStringUtils.length(mod200.getEnterprisePhone2()) > 9)
				throw new AonCoreException("El teléfono 2 del presentador no puede superar 9 caracteres.");
			if ( AonStringUtils.length(mod200.getReceipt()) > 13)
				throw new AonCoreException("El n\u00FAmero de declaraci\u00F3n no puede superar 13 caracteres.");
			if ( AonStringUtils.length(mod200.getComplementaryReceipt()) > 13)
				throw new AonCoreException("El n\u00FAmero de declaraci\u00F3n complementaria no puede superar 13 caracteres.");
			if ( AonStringUtils.length(mod200.getFiscalGroup()) > 9)
				throw new AonCoreException("El N\u00FAmero del grupo fiscal no puede superar 9 caracteres."); 			
			if ( AonStringUtils.length(mod200.getDominantDocument()) > 9)
				throw new AonCoreException("El NIF de la sociendad dominante no puede superar 9 caracteres.");
			if ( mod200.getSecretary() != null && AonStringUtils.length(mod200.getSecretary().getDocument()) > 9)
				throw new AonCoreException("El NIF del secretario no puede superar 9 caracteres.");
			if ( mod200.getSecretary() != null && AonStringUtils.length(mod200.getSecretary().getName()) > 25) 
				throw new AonCoreException("El nombre del secretario no puede superar 9 caracteres.");
			if ( AonStringUtils.length(mod200.getNrsAnexoIII()) > 30) 
				throw new AonCoreException("Documentaci\u00F3n presentada por el Anexo III (Ajustes y deducciones)");
			if ( AonStringUtils.length(mod200.getNrsAnexoIV()) > 30) 
				throw new AonCoreException("Documentaci\u00F3n presentada por el Anexo IV (Personal investigador)");
			if ( AonStringUtils.length(mod200.getNrsAnexoV()) > 30)
				throw new AonCoreException("Documento normalizado presentada por el Anexo V");
			if ( AonStringUtils.length(mod200.getJustCanarias()) > 30)
				throw new AonCoreException("N\u00FAmero de justificante identificativo de la declaraci\u00F3n informativa de ayudas R\u00E9gimen Econ\u00F3mico y Fiscal de Canarias"); 
			if ( AonStringUtils.length(mod200.getJustActivos()) > 30)
				throw new AonCoreException("N\u00FAmero de justificante identificativo autoliquidaci\u00F3n de la prestaci\u00F3n patrimonial por conversi\u00F3n de activos (DA 13a LIS)");
			Mod2002016 mod = null;
			if (mod200.getId() == null) {
				mod = insert(ctx, mod200);
			} else {
				mod = update(ctx, mod200);
			}
			ctx.log().info("------ [END OK] SAVE MOD 200");
			return mod;
		} catch (Throwable t) {
			ctx.log().info("------ [END FAIL] SAVE MOD 200 [" + t.getMessage() + "]");
			throw t;
		}
	}
	
	private static Mod2002016 insert(AONContext ctx, Mod2002016 mod200)  {
		FsModel200Record record = ctx.getDslContext()
			.insertInto(FS_MODEL200)
			 .set(FS_MODEL200.DOMAIN, mod200.getDomain() )
			 .set(FS_MODEL200.ENTERPRISE, mod200.getEnterprise())
			 .set(FS_MODEL200.YEAR, mod200.getYear())
			 .set(FS_MODEL200.ADMINISTRATION, mod200.getAdministration().getValue() )
			 .set(FS_MODEL200.STATUS, AonEnumUtils.getByte( mod200.getStatus() ) )
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
			 .set(FS_MODEL200.BIC,mod200.getBic())
			 .set(FS_MODEL200.NRS_ANEXOIII,mod200.getNrsAnexoIII())
			 .set(FS_MODEL200.JUST_CANARIAS,mod200.getJustCanarias())
			 .set(FS_MODEL200.NRS_ANEXOIV,mod200.getNrsAnexoIV())
			 .set(FS_MODEL200.NRS_ANEXOV,mod200.getNrsAnexoV())
			 .set(FS_MODEL200.JUST_ACTIVOS,mod200.getJustActivos())
			 .returning()
			 .fetchOne();
		mod200.setId(record.getValue(FS_MODEL200.ID));
		ctx.log().info("------ MOD 200 INSERTED (" + mod200.getId() + ")");
		insertDetail(ctx, mod200);	
		insertAdministrator(ctx, mod200);
		return mod200;
	}
	
	private static void insertAdministrator(AONContext ctx,Mod2002016 mod200) {
		LinkedList<FsModel200RegistryRecord> list = new LinkedList<FsModel200RegistryRecord>();
		FsModel200RegistryRecord detail = null;
		if (mod200.getAdministrators() != null) {
			for ( Mod200CompanyAdministrator ca : mod200.getAdministrators() ) {
				detail = new FsModel200RegistryRecord();
				detail.setFsModel200(mod200.getId());
				detail.setDomain(mod200.getDomain());
				detail.setType( Mod2002016RegistryType.ADMINISTRATOR.byteValue());
				detail.setDocument(AonStringUtils.substring(ca.getDocument(),0,9));
				detail.setName(AonStringUtils.substring(ca.getName(),0,45));
				detail.setRepresentative( (byte) (ca.isRepresentative()?1:0) );
				detail.setProvince( (byte) ca.getProvince() );
				detail.setResidence(AonStringUtils.substring(ca.getResidence(),0,45));
				list.add(detail);
			}
		}
		if (mod200.getParticipationsOut() != null) {
			for ( Mod200CompanyParticipation cp : mod200.getParticipationsOut() ) {
				detail = new FsModel200RegistryRecord();
				detail.setFsModel200(mod200.getId());
				detail.setDomain(mod200.getDomain());
				detail.setType(Mod2002016RegistryType.PARTICPATION_OUT.byteValue());
				detail.setDocument(AonStringUtils.substring(cp.getDocument(),0,9));
				detail.setName(AonStringUtils.substring(cp.getName(),0,45));
				detail.setProvince( (byte) cp.getProvince() );
				detail.setCountry( cp.getCountry() );
				detail.setPercent(cp.getPercent());
				detail.setNominalValue(cp.getNominalValue());
				detail.setBookValue(cp.getBookValue());
				detail.setIncomes(cp.getIncomes());
				detail.setAValue(cp.getValueCorrection());
				detail.setBValue(cp.getLossReversion());
				detail.setCcValue(cp.getAccountingElimination());
				detail.setCValue(cp.getCorrectionEffect());
				detail.setDValue(cp.getCorrectionsBalance());
				detail.setCapital(cp.getCapital());
				detail.setReserve(cp.getReserve());
				detail.setOtherAmounts(cp.getOtherAmounts());
				detail.setResult(cp.getResult());
				list.add(detail);
			}
		}
		if (mod200.getParticipationsIn() != null) {
			for ( Mod200CompanyParticipation cp : mod200.getParticipationsIn() ) {
				detail = new FsModel200RegistryRecord();
				detail.setFsModel200(mod200.getId());
				detail.setDomain(mod200.getDomain());
				detail.setType(Mod2002016RegistryType.PARTICPATION_IN.byteValue());
				detail.setDocument(AonStringUtils.substring(cp.getDocument(),0,9));
				detail.setName(AonStringUtils.substring(cp.getName(),0,45));
				detail.setProvince( (byte) cp.getProvince() );
				detail.setCountry( cp.getCountry() );
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
				detail.setType(Mod2002016RegistryType.REPRESENTATIVE.byteValue());
				detail.setDocument(AonStringUtils.substring(lr.getDocument(),0,9));
				detail.setNotary(AonStringUtils.substring(lr.getNotary(),0,20));
				detail.setNotaryDate( lr.getNotaryDate()==null?null:new java.sql.Date( lr.getNotaryDate().getTime() ) );
				detail.setName(AonStringUtils.substring(lr.getName(),0,45));
				list.add(detail);
			}
		}
		if (mod200.getUteParticipations() != null) {
			for ( UteParticipation ute : mod200.getUteParticipations() ) {
				detail = new FsModel200RegistryRecord();
				detail.setFsModel200(mod200.getId());
				detail.setDomain(mod200.getDomain());
				detail.setType(Mod2002016RegistryType.UTE_PARTICIPATION.byteValue());
				detail.setDocument(AonStringUtils.substring(ute.getDocument(),0,9));
				detail.setProvince( (byte) ute.getProvince() );
				detail.setCountry( ute.getCountry() );
				detail.setRepresentative( (byte) (ute.isRepresentative()?1:0) );
				detail.setName(AonStringUtils.substring(ute.getName(),0,45));
				detail.setNominalValue(ute.getBase());
				detail.setPercent(ute.getPercent());
				list.add(detail);
			}
		}
		
		if (mod200.getUteForeign() != null) {
			for ( UteForeign ute : mod200.getUteForeign() ) {
				detail = new FsModel200RegistryRecord();
				detail.setFsModel200(mod200.getId());
				detail.setDomain(mod200.getDomain());
				detail.setType(Mod2002016RegistryType.UTE_FOREIGN.byteValue());
				detail.setName(AonStringUtils.substring(ute.getIdentification(),0,45));
				detail.setCountry( ute.getCountry() );
				detail.setAValue(ute.getVolume());
				detail.setBValue(ute.getPyg());
				detail.setCValue(ute.getAdjust());
				detail.setDValue(ute.getDeduction());
				list.add(detail);
			}
		}
		if (mod200.getUteBases() != null) {
			for ( UteBase ute : mod200.getUteBases() ) {
				detail = new FsModel200RegistryRecord();
				detail.setFsModel200(mod200.getId());
				detail.setDomain(mod200.getDomain());
				detail.setType(Mod2002016RegistryType.UTE_BASE.byteValue());
				detail.setNominalValue(ute.getBase());
				detail.setPercent(ute.getPercent());
				list.add(detail);
			}
		}
		if (mod200.getGroupEntities() != null) {
			for ( String ge : mod200.getGroupEntities() ) {
				if (AonStringUtils.isNotBlank(ge)) {
					detail = new FsModel200RegistryRecord();
					detail.setFsModel200(mod200.getId());
					detail.setDomain(mod200.getDomain());
					detail.setType(Mod2002016RegistryType.GROUP_ENTITIES.byteValue());
					detail.setDocument(AonStringUtils.substring(ge,0,9));
					list.add(detail);
				}
			}
		}

		if (mod200.getEstablishments() != null) {
			for ( String es : mod200.getEstablishments() ) {
				if (AonStringUtils.isNotBlank(es)) {
					detail = new FsModel200RegistryRecord();
					detail.setFsModel200(mod200.getId());
					detail.setDomain(mod200.getDomain());
					detail.setType(Mod2002016RegistryType.ESTABLISHMENTS.byteValue());
					detail.setDocument(AonStringUtils.substring(es,0,9));
					list.add(detail);
				}
			}
		}
		if (!list.isEmpty()) {
			ctx.getDslContext().batchStore(list).execute();
			ctx.log().info("\t\t MOD 200 REGISTRY (" + list.size() + " rows)");
		}
	}

	private static void insertDetail(AONContext ctx, Mod2002016 mod200) {
		LinkedList<FsModel200DetailRecord> list = new LinkedList<FsModel200DetailRecord>();
		FsModel200DetailRecord detail = null;
		for (Mod2002016Key k : Mod2002016Key.values()) {
			DoubleVariable2016 dv = null;	
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
		ctx.log().info("\t\t MOD 200 DETAIL (" + list.size() + " rows)");
	}
	
	private static Mod2002016 update(AONContext ctx, Mod2002016 mod200)  {
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
		 .set(FS_MODEL200.BIC,mod200.getBic())
		 .set(FS_MODEL200.NRS_ANEXOIII,mod200.getNrsAnexoIII())
		 .set(FS_MODEL200.JUST_CANARIAS,mod200.getJustCanarias())
		 .set(FS_MODEL200.NRS_ANEXOIV,mod200.getNrsAnexoIV())
		 .set(FS_MODEL200.NRS_ANEXOV,mod200.getNrsAnexoV())
		 .set(FS_MODEL200.JUST_ACTIVOS,mod200.getJustActivos())
		 .where(FS_MODEL200.ID.equal(mod200.getId()))
		 .execute();
		ctx.log().info("\t\t MOD 200 UPDATED (" + mod200.getId() + ")");
		deleteDetail(ctx, mod200.getId());
		insertDetail(ctx, mod200);
		deleteRegistry(ctx, mod200.getId());
		insertAdministrator(ctx, mod200);
		return mod200;
	}

	private static void deleteDetail(AONContext ctx, int id ) {
		int count = ctx.getDslContext().delete(FS_MODEL200_DETAIL)
		   .where(FS_MODEL200_DETAIL.FS_MODEL200.equal(id) )
		   .execute();
		ctx.log().info("\t\t MOD 200 DETAIL DELETED (" + count + " rows)");
	}

	private static void deleteRegistry(AONContext ctx, int id ) {
		int count = ctx.getDslContext().delete(FS_MODEL200_REGISTRY)
		   .where(FS_MODEL200_REGISTRY.FS_MODEL200.equal(id) )
		   .execute();
		ctx.log().info("\t\t MOD 200 REGISTRY DELETED (" + count + " rows)");
	}
	
	public static void delete(AONContext ctx, int id )  {
		try {
			ctx.log().info("------ [START] DELETE MOD 200 ["+id+"]");
			deleteRegistry(ctx, id);
			deleteDetail(ctx, id);
			int count = ctx.getDslContext().delete(FS_MODEL200)
				.where(FS_MODEL200.ID.equal(id) )
				.execute();
			ctx.log().info("------ [END OK] DELETE MOD 200 ["+id+"] (" + count +" rows )");
		} catch (Throwable t) {
			ctx.log().info("------ [END FAIL] DELETE MOD 200 [" + t.getMessage() + "]");
			throw t;
		}
			
	}

	public static Mod2002016 getById(AONContext ctx, int id ) {
		FsModel200Record record = ctx.getDslContext()
				.selectFrom(FS_MODEL200)
				.where(FS_MODEL200.ID.equal(id))
				.fetchOne();
		Mod2002016 mod200 =  populateMod200(ctx,record);
		initializeActiveMap(mod200);
		return mod200;
	}
	
	public static Mod2002016 createNewMod200(AONContext ctx, int year) {
		try {
			ctx.log().info("------ [END OK] INITIALIZE NEW MOD 200");
			Mod2002016 mod200 = new Mod2002016();
			mod200.setDomain(ctx.getDomainId());
			mod200.setYear(year);
			mod200.setStatus(FiscalStatus.PENDING);
			initializeNewMod200(ctx,mod200);
			ctx.log().info("------ [END OK] INITIALIZE NEW MOD 200");
			return mod200;
		} catch (Throwable t) {
			ctx.log().info("------ [END FAIL] INITIALIZE NEW MOD 200 [" + t.getMessage() + "]");
			throw t;
		}
	}
	
	public static Mod2002016 getByYear(AONContext ctx, int year) {
		return getByYear(ctx, year, true);
	}
	
	public static Mod2002016 getByYear(AONContext ctx, int year, boolean initialize) {
		Result<FsModel200Record> result = ctx.getDslContext()
				.selectFrom(FS_MODEL200)
				.where(FS_MODEL200.DOMAIN.equal(ctx.getDomainId()))
				.and(FS_MODEL200.YEAR.equal(year))
				.fetch();
		FsModel200Record record = null;
		Mod2002016 mod200 = null; 
		if (result != null && result.isNotEmpty()) {
			record = result.get(0);
			mod200 = populateMod200(ctx,record);
		}
		if (initialize) {
			if (mod200 == null) {
				createNewMod200(ctx, year);
			} else {
				initializeActiveMap(mod200);
			}
		}
		return mod200;
	}

	private static Mod2002016 getMod200(FsModel200Record record) {
		
		Mod2002016 mod200 = new Mod2002016();
		mod200 = new Mod2002016();
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
		mod200.setBic(record.getBic());
		mod200.setNrsAnexoIII(record.getNrsAnexoiii());
		mod200.setJustCanarias(record.getJustCanarias());
		mod200.setNrsAnexoIV(record.getNrsAnexoiv());
		mod200.setNrsAnexoV(record.getNrsAnexov());
		mod200.setJustActivos(record.getJustActivos());
		return mod200;
	}
	
	private static Mod2002016 populateMod200(AONContext ctx, FsModel200Record record) {
		Mod2002016 mod200 = null;
		if (record != null) {
			mod200 = getMod200(record);
			fillDetail(mod200,ctx);
			final Mod2002016 mod = mod200;
			ctx.getDslContext() 
				.selectFrom(FS_MODEL200_REGISTRY)
				.where(	FS_MODEL200_REGISTRY.FS_MODEL200.equal(mod200.getId()))
				.fetch()
				.stream()
				.forEach(reg -> Mod2002016RegistryType.populate(mod, reg));
		}
		return mod200;
	}
	
	private static void fillDetail(Mod2002016 mod200,AONContext ctx) {
		
		HashMap<String,Double> map = new HashMap<String,Double>();
		Result<FsModel200DetailRecord> result = ctx.getDslContext()
				.selectFrom(FS_MODEL200_DETAIL)
			 	.where(	FS_MODEL200_DETAIL.FS_MODEL200.equal(mod200.getId()))
			 	.fetch();
		for (FsModel200DetailRecord det : result) {
			try {
				Mod2002016Key.valueOf(det.getKey());
				map.put(det.getKey(),det.getValue());
			} catch (IllegalArgumentException e) {
				System.out.println( "WARNING: Clave "+ det.getKey()+" no encontrada!" );
			}
		}

		DoubleVariable2016 v = null;
		for (Mod2002016Key key : Mod2002016Key.values() ) {
			v = new DoubleVariable2016( key );
			if (map.containsKey(key.toString())) {
				v.setValue( map.get(key.toString()));
			} else {
				v.setValue( 0.0 );
			}
			mod200.addVariable(v);
		}

		BalanceType bt = null;
		if (map.containsKey( Mod2002016Key.C0050.toString() )) {
			bt = BalanceType.NORMAL;
		} else if (map.containsKey( Mod2002016Key.C0051.toString() )) {
			bt = BalanceType.ABREVIADO;
		} else if (map.containsKey( Mod2002016Key.C0052.toString() )) {
			bt = BalanceType.PYMES;
		}
		mod200.setBalanceType( bt );
		if (map.containsKey( Mod2002016Key.C0053.toString() )) {
			bt = BalanceType.NORMAL;
		} else if (map.containsKey( Mod2002016Key.C0054.toString() )) {
			bt = BalanceType.ABREVIADO;
		} else if (map.containsKey( Mod2002016Key.C0055.toString() )) {
			bt = BalanceType.PYMES;
		}
		mod200.setPygType( bt );
	}
	
	public static Mod2002016 initializeNewMod200(AONContext ctx, Mod2002016 mod200) {
		Mod2002015 old= Mod2002015DAO.getByYear(ctx, 2015);
		if (old != null && old.getId() != null) { 
			mod200.setEnterprise(old.getEnterprise());
			Mod2002016Import2015.import2015(mod200,old);
			mod200.setInitializedFromLastYear(true);
		} else {
			mod200.setPeriodType(1);
			mod200.setPeriodStart(AonDateUtils.getYearFirstDay(mod200.getYear()));
			mod200.setPeriodEnd(AonDateUtils.getYearLastDay(mod200.getYear()));
	
			mod200.setBalanceType( BalanceType.ABREVIADO );
			mod200.setPygType(BalanceType.ABREVIADO );
			
			AonConfiguration conf = ConfigurationDAO.getConfiguration(ctx);
			mod200.setEnterprise(conf.getCompany().getId());
			mod200.setEnterpriseDocument(conf.getCompany().getDocument());
			mod200.setEnterpriseName(conf.getCompany().getName());
			mod200.setEnterprisePhone1(conf.fiscal().getContactPhone());
			mod200.setEnterprisePhone2(conf.fiscal().getContactCellular());
			Administration adm = conf.fiscal().getAdministration(Administration.COMMON_TERRITORY);
			mod200.setAdministration(adm);
			mod200.setInitializedFromLastYear(false);
		}
		return mod200;
	}

	
	public static Mod2002016 initializeMod200(AONContext ctx, Mod2002016 mod200) {
		try {
			ctx.log().info("------ [START] INITIALIZE MOD 200");
			if (!mod200.isInitializedFromLastYear()) {
				LinkedList<Mod200CompanyAdministrator> adms = Mod200DAO.getDirStaff(ctx, mod200.getDomain());
				if ( adms != null && adms.size() > 0 ) {
					for (Mod200CompanyAdministrator ca : adms ) {
						if (ca.isAdministrator()) {
							mod200.getAdministrators().add(ca);
						}
						if (ca.isShareholder()) {
							Mod200CompanyParticipation cp = new Mod200CompanyParticipation();
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
			}
	
			
			Mod2002016MVELContext mvelCtx = new Mod2002016MVELContext( mod200, ACCEPTER );		
			AccMiningParameters params = getParams(ctx,mod200);
			if (params != null) {
				mvelCtx.setAccounts( ACCOUNTING.getAccountBalances(ctx, params) );
			} else {
				mvelCtx.setAccounts( new HashMap<String,AccountBalance>() );
			}
			mvelCtx.setExpressionMap(INITIALIZE_EXPRESSION_MAP);
			addCharacters(mvelCtx,mod200);
			addBalanceCharacters(mvelCtx,mod200);
			DoubleVariable2016 dv = null;
			for (Mod2002016Key k : INITIALIZE_EXPRESSION_MAP.keySet()) {
				String stringKey = k.toString();
				String expression = INITIALIZE_EXPRESSION_MAP.get(k);
				mvelCtx.put(stringKey, 0.0 );
				Object ret = mvelCtx.evaluateExpression(k,expression);
				mvelCtx.put(stringKey, ret );
				if (ret instanceof Double ) {
					dv = new DoubleVariable2016( k );
					dv.setValue( (Double) ret );
					mod200.addVariable( dv );
				} 
				if (ret instanceof Boolean) {
					dv = new DoubleVariable2016( k );
					dv.setValue((Boolean) ret );
					mod200.addVariable( dv );
				}
			}		
			fillMod202(ctx,mod200);
			calculate(mod200,false);
			initializeActiveMap(mod200);
			ctx.log().info("------ [END OK] INITIALIZE MOD 200");
			return mod200;
		} catch (Throwable t) {
			ctx.log().info("------ [END FAIL] INITIALIZE MOD 200 [" + t.getMessage() + "]");
			throw t;
		}
	}
	
	private static void fillMod202(AONContext ctx,Mod2002016 mod200) {
		Mod202DAO.getMod202s(ctx, mod200.getDomain())
			.filter(mod -> mod.getYear() == mod200.getYear() && mod.getStatus() == FiscalStatus.FINISHED )
			
			.forEach( mod -> {
				Mod202 mod202 = Mod202DAO.getMod202(ctx, mod.getId());		
				Mod2002016Key key = null;
				if (mod202.getPeriod() == Period.T1) {
					key = Mod2002016Key.BN601;
				} else if (mod202.getPeriod() == Period.T2) {
					key = Mod2002016Key.BN603;
				} if (mod202.getPeriod() == Period.T3) {
					key = Mod2002016Key.BN605;
				}
				if (key != null) {
					DoubleVariable2016 dv = new DoubleVariable2016( key );
					dv.setValue( (Double) mod202.getResult() );
					mod200.addVariable( dv );
				}
			});
	}

	public static Mod2002016 calculate(Mod2002016 mod200) {
		return calculate(mod200,true);
	}
	
	public static Mod2002016 calculate(Mod2002016 mod200, boolean addToDraft) {
		try {
			Mod2002016MVELContext ctx = new Mod2002016MVELContext( mod200, ACCEPTER );
			ctx.setExpressionMap(Mod2002016Compute.COMPUTE_EXPRESSION_MAP);
			for (DoubleVariable2016 dv : mod200.getKeysMap().values()) {
				ctx.put(dv.getKey().toString(), dv.getValue());
			}
			addCharacters(ctx,mod200);
			addBalanceCharacters(ctx,mod200);
			
			DoubleVariable2016 d = null;
			for (Mod2002016Key key : mod200.getDraftMap().keySet() ) {
				d = mod200.getDraftMap().get(key);
				if (d.isChangedByUser()) {
					ctx.put(key.toString(), d.getValue());
				}
			}
			
			DoubleVariable2016 v = null;
			for (Mod2002016Key k : Mod2002016Compute.COMPUTE_EXPRESSION_MAP.keySet()) {
				String stringKey = k.toString();
				DoubleVariable2016 existingVariable = mod200.getVariable(k);
				Double existingValue = ( existingVariable == null )?0.0:existingVariable.getValue();
				ctx.put(stringKey, existingValue);
				Object ret = ctx.evaluateExpression(k,Mod2002016Compute.COMPUTE_EXPRESSION_MAP.get(k));
				if (ret instanceof Double) {
					Double calculated = (Double) ret;
					ctx.put(stringKey, calculated);
					if ( !AonMathUtils.equals( existingValue , calculated ) ) {
						v = new DoubleVariable2016( k );
						v.setValue( calculated );
						if (addToDraft) {
							mod200.addDraftVariable(v);	
						} else {
							mod200.addVariable(v);
						}
					}
				}
			}
			v = mod200.getVariable(Mod2002016Key.BN621);
			mod200.setResultType(null);
			if (v == null || v.getValue() == 0) {
				mod200.setResultType("N");
				mod200.setAmount( 0.0 );
				mod200.setDevType(null);
				mod200.setPayType(null);
			} else if (AonMathUtils.round(v.getValue()) < 0.0) {
				mod200.setResultType("D");
				mod200.setAmount( AonMathUtils.round( v.getValue() * -1));
				mod200.setDevType(AonStringUtils.isEmpty(mod200.getDevType())?"D":mod200.getDevType());
				mod200.setPayType(null);
			} else {
				mod200.setResultType("I");
				mod200.setAmount( v.getValue() );
				mod200.setPayType(AonStringUtils.isEmpty(mod200.getPayType())?"H":mod200.getPayType());
				mod200.setDevType(null);
			}
			return mod200;
		} catch (Throwable e) {
			System.out.println( "*** ERROR " + e.getMessage());
			throw new AonCoreException(e);
		}
	}
	
	private static AccMiningParameters getParams(AONContext ctx,Mod2002016 mod200) throws AonCoreException {
		AccMiningParameters params = new AccMiningParameters();
		params.setDomain(mod200.getDomain());
		params.setYear(mod200.getYear());
		 
		AccountPeriod period =  AccountPeriodDAO.getPeriodByYear(ctx, mod200.getYear());
		if (period == null) {
			return null;
		}
//		if (!period.isClosed()) {
//			throw new AonCoreException("Cierre el ejercicio contable '"+mod200.getYear()+"' para poder continuar.");
//		}
		params.setPeriodId(period.getId());
		params.setStartDate(period.getInitiationDate());
		params.setEndDate(period.getDeadline());
		return params;
	}

	private static void addCharacters(Mod2002016MVELContext ctx, Mod2002016 mod200) {
		for (Mod2002016Key key : Mod2002016Character.CHARACTERS_KEYS) {
			DoubleVariable2016 dv = mod200.getKeysMap().get(key);
			ctx.put(key.toString(), (dv != null && dv.getBooleanValue() )); 
		}
		
	}
	
	private static void initializeActiveMap(Mod2002016 mod200) {
		Mod2002016MVELContext ctx = new Mod2002016MVELContext( mod200, ACCEPTER );
		ctx.setExpressionMap(Mod2002016Activation.ACTIVE_EXPRESSION_MAP);
		addCharacters(ctx,mod200);
		addBalanceCharacters(ctx,mod200);
		for (Mod2002016Key key : Mod2002016Key.values() ) {
			if (Mod2002016Activation.ACTIVE_EXPRESSION_MAP.containsKey(key) ) {
				Object ret = ctx.get( key.toString() );
				if (ret instanceof Boolean && ((Boolean) ret) ) {
					mod200.getVisibleMap().put(key,true);
				}
			} else {
				mod200.getVisibleMap().put(key,true);
			}
		}
	}

	private static void addBalanceCharacters(Mod2002016MVELContext ctx, Mod2002016 mod200) {
		ctx.put(Mod2002016Key.C0050.toString(), mod200.getBalanceType() == BalanceType.NORMAL);
		ctx.put(Mod2002016Key.C0051.toString(), mod200.getBalanceType() == BalanceType.ABREVIADO);
		ctx.put(Mod2002016Key.C0052.toString(), mod200.getBalanceType() == BalanceType.PYMES);

		ctx.put(Mod2002016Key.C0053.toString(), mod200.getPygType() == BalanceType.NORMAL);
		ctx.put(Mod2002016Key.C0054.toString(), mod200.getPygType() == BalanceType.ABREVIADO);
		ctx.put(Mod2002016Key.C0055.toString(), mod200.getPygType() == BalanceType.PYMES);
	}
	
	public static Mod2002016 validate(Mod2002016 mod200) {
		mod200.setMessages(new LinkedList<ValidationMessage2016>());
//		LinkedList<ValidationMessage2016> list = mod200.getMessages();
		Mod2002016MVELContext ctx = new Mod2002016MVELContext( mod200, ACCEPTER );
		for (DoubleVariable2016 dv : mod200.getKeysMap().values()) {
			ctx.put(dv.getKey().toString(), dv.getValue());
		}
		DoubleVariable2016 d = null;
		for (Mod2002016Key key : mod200.getDraftMap().keySet() ) {
			d = mod200.getDraftMap().get(key);
			ctx.put(key.toString(), d.getValue());
		}
		addCharacters(ctx,mod200);
		Mod2002016Validation.validate(mod200);
//		for (ValidationMessage2016 validation : VALIDATION_EXPRESSION_LIST) {
//			boolean valid = ctx.validateExpression(validation.getKey(),validation.getExpression());
//			if (!valid) {
//				list.add(validation);
//			}
//		}
		return mod200;
	}
	
	public static String dumpAEAT(Mod2002016 mod200)  {
		try {
			MOD2002016 mod = Mod2002016toMOD2002016.getMOD2002016(mod200);
			StringWriter writer = new StringWriter();
			JAXBContext context = JAXBContext.newInstance(MOD2002016.class);
			Marshaller um = context.createMarshaller();
			um.setProperty("jaxb.encoding", "ISO-8859-1");
			um.marshal(mod,writer);
			return writer.toString();
		} catch (JAXBException e) {
			throw new AonCoreException(e.getMessage(),e);
		}				
	}

	public static Mod2002016 importMod2002015(AONContext ctx, Mod2002016 mod200) {
		Mod2002015 old= Mod2002015DAO.getByYear(ctx, 2015);
		return Mod2002016Import2015.import2015(old);
	}	
}
