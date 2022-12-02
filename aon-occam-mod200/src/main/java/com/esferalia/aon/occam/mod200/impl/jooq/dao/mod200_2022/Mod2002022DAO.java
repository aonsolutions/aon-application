package com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2022;

import static com.esferalia.aon.jooq.tables.FsModel200.FS_MODEL200;
import static com.esferalia.aon.jooq.tables.FsModel200Detail.FS_MODEL200_DETAIL;
import static com.esferalia.aon.jooq.tables.FsModel200Registry.FS_MODEL200_REGISTRY;
import static com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2022.Mod2002022Initialization.INITIALIZE_EXPRESSION_MAP;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;

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
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ConfigurationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod202DAO;
import com.esferalia.aon.occam.mod200.api.model.BalanceType;
import com.esferalia.aon.occam.mod200.api.model.DoubleVariableEx;
import com.esferalia.aon.occam.mod200.api.model.EcpnType;
import com.esferalia.aon.occam.mod200.api.model.GroupEntitie;
import com.esferalia.aon.occam.mod200.api.model.IMod200Key;
import com.esferalia.aon.occam.mod200.api.model.MinorEntity;
import com.esferalia.aon.occam.mod200.api.model.Mod200CompanyAdministrator;
import com.esferalia.aon.occam.mod200.api.model.Mod200CompanyParticipation;
import com.esferalia.aon.occam.mod200.api.model.Secretary;
import com.esferalia.aon.occam.mod200.api.model.UteBase;
import com.esferalia.aon.occam.mod200.api.model.UteForeign;
import com.esferalia.aon.occam.mod200.api.model.UteParticipation;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Character;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022KeyDC;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.Mod200DAO;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2021.Mod2002021DAO;
import com.esferalia.aon.occam.mod200.server.format.Mod2002022Import2021;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod2002022DAO  {
	
	@FunctionalInterface
	private static interface IPopulater {
		boolean populate(Mod2002022 mod, FsModel200RegistryRecord reg);
	}

	private static enum Mod2002022RegistryType {
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
				.setAccountingElimination(reg.getCcValue())
				.setValuesElimination(reg.getDdValue())
				.setAdjustmentDecrease(reg.getEValue())
				.setCorrectionEffect(reg.getCValue())
				.setCorrectionsBalance(reg.getDValue())
				.setCapital(reg.getCapital())
				.setReserve(reg.getReserve())
				.setOtherAmounts(reg.getOtherAmounts())
				.setResult(reg.getResult())
				))
		,PARTICPATION_IN( 
			(mod,reg) -> mod.getParticipationsIn().add(new Mod200CompanyParticipation()				
				.setDocument(reg.getDocument())
				.setName(reg.getName())
				.setProvince(reg.getProvince() )
				.setCountry( reg.getCountry() )
				.setRepresentative( reg.getRepresentative() == 1 )
				.setNotary(reg.getNotary()) // F/J/O
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
			(mod,reg) -> mod.getGroupEntities().add(new GroupEntitie()				
					.setDocument(reg.getDocument())					
					.setCountry( reg.getCountry())))
		,ESTABLISHMENTS( 
			(mod,reg) -> mod.getEstablishments().add(reg.getDocument()))
		,MINOR_ENTITIES(
			(mod,reg) -> mod.getMinorEntities().add(new MinorEntity()				
					.setDocument(reg.getDocument())					
					.setName( reg.getName())))
		,FILM_PRODUCTIONS( 
			(mod,reg) -> mod.getFilmProductions().add(reg.getDocument()))
		;
		
		private IPopulater populater;
		
		private Mod2002022RegistryType( IPopulater populater){
			this.populater = populater;
		}
		
		private boolean accept(FsModel200RegistryRecord reg) {
			return reg.getType() == this.ordinal();
		}

		private void _populate(Mod2002022 mod,FsModel200RegistryRecord reg) {
			if ( accept(reg) ) {
				this.populater.populate(mod, reg);
			}
		}
		
		public static void populate(Mod2002022 mod,FsModel200RegistryRecord reg) {
			Mod2002022RegistryType type = Mod2002022RegistryType.safeValueOf(reg.getType());
			if (type != null) {
				type._populate(mod, reg);
			}
		}
		
		private static Mod2002022RegistryType safeValueOf( Byte i ) {
			if (i == null) return null;
			return safeValueOf( i.intValue() ); 
		}
		
		private static Mod2002022RegistryType safeValueOf( Integer i ) {
			if (i == null) return null;
			if (i < 0 || i >= Mod2002022RegistryType.values().length) return null;
			return Mod2002022RegistryType.values()[i];
		}

		private byte byteValue() {
			return (byte) ordinal();
		}
		
	}
	
	private static final IAccMiningKeyAccept ACCEPTER = new IAccMiningKeyAccept() {
		
		@Override
		public boolean acceptKey(Object key) {
			try {
				// Comprobar si la clave está en Mod2002022Key
				return (Mod2002022Key.valueOf((String) key) != null);	
			} catch (IllegalArgumentException e) {
				try {
					// Si no lo está, comprobar si está en Mod2002022KeyDC (Detalle correcciones)
					return (Mod2002022KeyDC.valueOf((String) key) != null);	
				} catch (IllegalArgumentException e2) {
					return false;
				}				
			}
		}
	};

	public static Mod2002022 save(AONContext ctx, Mod2002022 mod200) {
		try {
			ctx.log().info("------ [START] SAVE MOD 200");
			
			// Fechas inicio y fin de periodo para tipo de periodo = 1
			if (mod200.getPeriodType() == 1) {
				mod200.setPeriodStart(AonDateUtils.getYearFirstDay(2022));
				mod200.setPeriodEnd(AonDateUtils.getYearLastDay(2022));
			}
			
			// Casillas estados contables (Balance y PYG)
			mod200.setBooleanValue(Mod2002022Key.C0050, (mod200.getBalanceType() == BalanceType.NORMAL));
			mod200.setBooleanValue(Mod2002022Key.C0051, (mod200.getBalanceType() == BalanceType.ABREVIADO));
			mod200.setBooleanValue(Mod2002022Key.C0052, (mod200.getBalanceType() == BalanceType.PYMES));
			
			mod200.setBooleanValue(Mod2002022Key.C0053, (mod200.getPygType() == BalanceType.NORMAL));
			mod200.setBooleanValue(Mod2002022Key.C0054, (mod200.getPygType() == BalanceType.ABREVIADO));
			mod200.setBooleanValue(Mod2002022Key.C0055, (mod200.getPygType() == BalanceType.PYMES));
			
			if ( AonStringUtils.length(mod200.getDocument()) > 9)
				throw new AonCoreException("El documento del presentador no puede superar 9 caracteres.");
			if ( AonStringUtils.length(mod200.getName()) > 45)
				throw new AonCoreException("La raz\u00F3n social del presentador no puede superar 45 caracteres.");
			if ( AonStringUtils.length(mod200.getEnterprisePhone1()) > 9)
				throw new AonCoreException("El teléfono 1 del presentador no puede superar 9 caracteres.");
			if ( AonStringUtils.length(mod200.getEnterprisePhone2()) > 9)
				throw new AonCoreException("El teléfono 2 del presentador no puede superar 9 caracteres.");
			if ( AonStringUtils.length(mod200.getNumber()) > 13)
				throw new AonCoreException("El n\u00FAmero de declaraci\u00F3n no puede superar 13 caracteres.");
			if ( AonStringUtils.length(mod200.getReplacedNumber()) > 13)
				throw new AonCoreException("El n\u00FAmero de declaraci\u00F3n complementaria no puede superar 13 caracteres.");
			if ( AonStringUtils.length(mod200.getFiscalGroup()) > 9)
				throw new AonCoreException("El N\u00FAmero del grupo fiscal no puede superar 9 caracteres."); 			
			if ( AonStringUtils.length(mod200.getDominantDocument()) > 9)
				throw new AonCoreException("El NIF de la sociendad dominante no puede superar 9 caracteres.");
			if ( mod200.getSecretary() != null && AonStringUtils.length(mod200.getSecretary().getDocument()) > 9)
				throw new AonCoreException("El NIF del secretario no puede superar 9 caracteres.");
			if ( mod200.getSecretary() != null && AonStringUtils.length(mod200.getSecretary().getName()) > 25) 
				throw new AonCoreException("El nombre del secretario no puede superar 9 caracteres.");
			Mod2002022 mod = null;
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
	
	private static Mod2002022 insert(AONContext ctx, Mod2002022 mod200)  {
        mod200.setCreationUser(ctx.getUser());
		mod200.setCreationDate(new Timestamp(System.currentTimeMillis()));        
//		mod200.setFsModel(Mod200DAO.saveFsModel(ctx, mod200)); // Se utilizará a partir del ejercicio 2022
		FsModel200Record record = ctx.getDslContext()
			.insertInto(FS_MODEL200)
			 .set(FS_MODEL200.DOMAIN, mod200.getDomain() )
			 .set(FS_MODEL200.ENTERPRISE, mod200.getEnterprise())
			 .set(FS_MODEL200.YEAR, mod200.getYear())
			 .set(FS_MODEL200.ADMINISTRATION, mod200.getAdministration().value() )
			 .set(FS_MODEL200.STATUS, AonEnumUtils.getByte( mod200.getStatus() ) )
			 .set(FS_MODEL200.DOCUMENT, mod200.getDocument())
			 .set(FS_MODEL200.NAME, mod200.getName())
			 .set(FS_MODEL200.PHONE1, mod200.getEnterprisePhone1())
			 .set(FS_MODEL200.PHONE2, mod200.getEnterprisePhone2())
			 .set(FS_MODEL200.COMPLEMENTARY, (byte) (mod200.isComplementary()?1:0) )
			 .set(FS_MODEL200.RECEIPT,mod200.getNumber())
			 .set(FS_MODEL200.COMPLEMENTARY_RECEIPT,mod200.getReplacedNumber())
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
			 .set(FS_MODEL200.NRS_ANEXOV_RIC,mod200.getNrsAnexoVric())
			 .set(FS_MODEL200.JUST_ACTIVOS,mod200.getJustActivos())
			 .set(FS_MODEL200.ULTIMATE_DOCUMENT,mod200.getUltimateDocument())
			 .set(FS_MODEL200.ULTIMATE_DOCUMENT_COUNTRY, Country.safeIso2(mod200.getUltimateDocumentCountry()))
			 .set(FS_MODEL200.ULTIMATE_NAME,mod200.getUltimateName())
			 .set(FS_MODEL200.ULTIMATE_COUNTRY, Country.safeIso2(mod200.getUltimateCountry()))
			 .set(FS_MODEL200.CREATION_USER, mod200.getCreationUser())
			 .set(FS_MODEL200.CREATION_DATE, AonDateUtils.toTimestamp(mod200.getCreationDate()))
			 .set(FS_MODEL200.FS_MODEL, mod200.getFsModel())
			 .returning()
			 .fetchOne();
		mod200.setId(record.getValue(FS_MODEL200.ID));
		mod200.setCreationUser(record.getValue(FS_MODEL200.CREATION_USER));
		mod200.setCreationDate(record.getValue(FS_MODEL200.CREATION_DATE));
		ctx.log().info("------ MOD 200 INSERTED (" + mod200.getId() + ")");
		insertDetail(ctx, mod200);	
		insertRegistry(ctx, mod200);
		return mod200;
	}
	
	private static void insertRegistry(AONContext ctx,Mod2002022 mod200) {
		
		LinkedList<FsModel200RegistryRecord> list = new LinkedList<FsModel200RegistryRecord>();
		FsModel200RegistryRecord detail = null;
		
		// A. Relación de Administradores
		if (mod200.getAdministrators() != null) {
			for ( Mod200CompanyAdministrator ca : mod200.getAdministrators() ) {
				detail = new FsModel200RegistryRecord();
				detail.setFsModel200(mod200.getId());
				detail.setDomain(mod200.getDomain());
				detail.setType( Mod2002022RegistryType.ADMINISTRATOR.byteValue());
				detail.setDocument(AonStringUtils.substring(ca.getDocument(),0,9));
				detail.setName(AonStringUtils.substring(ca.getName(),0,45));
				detail.setRepresentative( (byte) (ca.isRepresentative()?1:0) );
				detail.setProvince( (byte) ca.getProvince() );
				detail.setResidence(AonStringUtils.substring(ca.getResidence(),0,45));
				list.add(detail);
			}
		}
		
		// B1. Participaciones directas de la declarante en otras sociedades
		if (mod200.getParticipationsOut() != null) {
			for ( Mod200CompanyParticipation cp : mod200.getParticipationsOut() ) {
				detail = new FsModel200RegistryRecord();
				detail.setFsModel200(mod200.getId());
				detail.setDomain(mod200.getDomain());
				detail.setType(Mod2002022RegistryType.PARTICPATION_OUT.byteValue());
				detail.setDocument(AonStringUtils.substring(cp.getDocument(),0,9));
				detail.setName(AonStringUtils.substring(cp.getName(),0,45));
				detail.setProvince( (byte) cp.getProvince() );
				detail.setCountry( cp.getCountry() );
				detail.setPercent(cp.getPercent());
				detail.setNominalValue(cp.getNominalValue());
				detail.setBookValue(cp.getBookValue());
				detail.setIncomes(cp.getIncomes());
				detail.setAValue(cp.getValueCorrection());				
				detail.setCcValue(cp.getAccountingElimination());
				detail.setCValue(cp.getCorrectionEffect());
				detail.setDValue(cp.getCorrectionsBalance());
				detail.setCapital(cp.getCapital());
				detail.setReserve(cp.getReserve());
				detail.setOtherAmounts(cp.getOtherAmounts());
				detail.setResult(cp.getResult());
				detail.setDdValue(cp.getValuesElimination());
				detail.setEValue(cp.getAdjustmentDecrease());
				list.add(detail);
			}
		}
		
		// B2. Participaciones de personas o entidades en la declarante
		if (mod200.getParticipationsIn() != null) {
			for ( Mod200CompanyParticipation cp : mod200.getParticipationsIn() ) {
				detail = new FsModel200RegistryRecord();
				detail.setFsModel200(mod200.getId());
				detail.setDomain(mod200.getDomain());
				detail.setType(Mod2002022RegistryType.PARTICPATION_IN.byteValue());
				detail.setDocument(AonStringUtils.substring(cp.getDocument(),0,9));
				detail.setName(AonStringUtils.substring(cp.getName(),0,45));
				detail.setProvince( (byte) cp.getProvince() );
				detail.setCountry( cp.getCountry() );
				detail.setRepresentative( (byte) (cp.isRepresentative()?1:0) );
				detail.setNotary(cp.getNotary());
				detail.setPercent(cp.getPercent());
				detail.setNominalValue(cp.getNominalValue());
				list.add(detail);
			}
		}
		
		// C. Entidades menores dependientes de diócesis, provincia religiosa o entidad eclesiástica integradas en la declaración, previamente autorizadas
		if (mod200.getMinorEntities() != null) {
			for ( MinorEntity me : mod200.getMinorEntities() ) {
				detail = new FsModel200RegistryRecord();
				detail.setFsModel200(mod200.getId());
				detail.setDomain(mod200.getDomain());
				detail.setType(Mod2002022RegistryType.MINOR_ENTITIES.byteValue());
				detail.setName(AonStringUtils.substring(me.getName(),0,40));
				detail.setDocument(AonStringUtils.substring(me.getDocument(),0,9));
				list.add(detail);
			}
		}
		
		// D. Informacion de detalle de EP o UTE que operen en el extranjero ...
		if (mod200.getUteForeign() != null) {
			for ( UteForeign ute : mod200.getUteForeign() ) {
				detail = new FsModel200RegistryRecord();
				detail.setFsModel200(mod200.getId());
				detail.setDomain(mod200.getDomain());
				detail.setType(Mod2002022RegistryType.UTE_FOREIGN.byteValue());
				detail.setName(AonStringUtils.substring(ute.getIdentification(),0,45));
				detail.setCountry( ute.getCountry() );
				detail.setAValue(ute.getVolume());
				detail.setBValue(ute.getPyg());
				detail.setCValue(ute.getAdjust());
				detail.setDValue(ute.getDeduction());
				list.add(detail);
			}
		}
		
		// Representantes legales de la entidad
		if (mod200.getRepresentatives() != null) {
			for ( LegalRepresentative lr : mod200.getRepresentatives() ) {
				detail = new FsModel200RegistryRecord();
				detail.setFsModel200(mod200.getId());
				detail.setDomain(mod200.getDomain());
				detail.setType(Mod2002022RegistryType.REPRESENTATIVE.byteValue());
				detail.setDocument(AonStringUtils.substring(lr.getDocument(),0,9));
				detail.setNotary(AonStringUtils.substring(lr.getNotary(),0,20));
				detail.setNotaryDate( lr.getNotaryDate()==null?null:new java.sql.Date( lr.getNotaryDate().getTime() ) );
				detail.setName(AonStringUtils.substring(lr.getName(),0,45));
				list.add(detail);
			}
		}
		
		// Agrupaciones de interes economico y UTES. Relacion de socios
		if (mod200.getUteParticipations() != null) {
			for ( UteParticipation ute : mod200.getUteParticipations() ) {
				detail = new FsModel200RegistryRecord();
				detail.setFsModel200(mod200.getId());
				detail.setDomain(mod200.getDomain());
				detail.setType(Mod2002022RegistryType.UTE_PARTICIPATION.byteValue());
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
				
		// Agrupaciones de interes economico y UTES. Deducción para evitar la doble imposicion 
		if (mod200.getUteBases() != null) {
			for ( UteBase ute : mod200.getUteBases() ) {
				detail = new FsModel200RegistryRecord();
				detail.setFsModel200(mod200.getId());
				detail.setDomain(mod200.getDomain());
				detail.setType(Mod2002022RegistryType.UTE_BASE.byteValue());
				detail.setNominalValue(ute.getBase());
				detail.setPercent(ute.getPercent());
				list.add(detail);
			}
		}
		
		//  NIF entidades del grupo (NIF y País)
		if (mod200.getGroupEntities() != null) {
			for ( GroupEntitie ge : mod200.getGroupEntities() ) {
				detail = new FsModel200RegistryRecord();
				detail.setFsModel200(mod200.getId());
				detail.setDomain(mod200.getDomain());
				detail.setType(Mod2002022RegistryType.GROUP_ENTITIES.byteValue());
				detail.setDocument(ge.getDocument());
				detail.setCountry(ge.getCountry());
				list.add(detail);
			}
		}

		// NIF establecimientos permanentes
		if (mod200.getEstablishments() != null) {
			for ( String es : mod200.getEstablishments() ) {
				if (AonStringUtils.isNotBlank(es)) {
					detail = new FsModel200RegistryRecord();
					detail.setFsModel200(mod200.getId());
					detail.setDomain(mod200.getDomain());
					detail.setType(Mod2002022RegistryType.ESTABLISHMENTS.byteValue());
					detail.setDocument(AonStringUtils.substring(es,0,9));
					list.add(detail);
				}
			}
		}
		
		// Información producciones cinematográficas
		if (mod200.getFilmProductions() != null) {
			for ( String fp : mod200.getFilmProductions() ) {
				if (AonStringUtils.isNotBlank(fp)) {
					detail = new FsModel200RegistryRecord();
					detail.setFsModel200(mod200.getId());
					detail.setDomain(mod200.getDomain());
					detail.setType(Mod2002022RegistryType.FILM_PRODUCTIONS.byteValue());
					detail.setDocument(AonStringUtils.substring(fp,0,9));
					list.add(detail);
				}
			}
		}		
		
		if (!list.isEmpty()) {
			ctx.getDslContext().batchStore(list).execute();
			ctx.log().info("\t\t MOD 200 REGISTRY (" + list.size() + " rows)");
		}
	}

	private static void insertDetail(AONContext ctx, Mod2002022 mod200) {
		LinkedList<FsModel200DetailRecord> list = new LinkedList<FsModel200DetailRecord>();
		FsModel200DetailRecord detail = null;
		// Insertamos los valores de Mod2002022Key
		for (Mod2002022Key k : Mod2002022Key.values()) {
			DoubleVariableEx dv = null;	
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
		// Insertamos los valores de Mod2002022KeyDC (Detalle correcciones)
		for (Mod2002022KeyDC k : Mod2002022KeyDC.values()) {
			DoubleVariableEx dv = null;	
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
	
	private static Mod2002022 update(AONContext ctx, Mod2002022 mod200)  {		
        mod200.setModificationUser(ctx.getUser());
		mod200.setModificationDate(new Timestamp( System.currentTimeMillis()));
//		mod200.setFsModel(Mod200DAO.saveFsModel(ctx, mod200)); // Se utilizará a partir del ejercicio 2022
		ctx.getDslContext().update(FS_MODEL200)
		 .set(FS_MODEL200.DOMAIN, mod200.getDomain() )
		 .set(FS_MODEL200.ENTERPRISE, mod200.getEnterprise())
		 .set(FS_MODEL200.YEAR, mod200.getYear())
		 .set(FS_MODEL200.ADMINISTRATION, mod200.getAdministration().value())
		 .set(FS_MODEL200.DOCUMENT, mod200.getDocument())
		 .set(FS_MODEL200.NAME, mod200.getName())
		 .set(FS_MODEL200.PHONE1, mod200.getEnterprisePhone1())
		 .set(FS_MODEL200.PHONE2, mod200.getEnterprisePhone2())
		 .set(FS_MODEL200.COMPLEMENTARY, (byte) (mod200.isComplementary()?1:0) )
		 .set(FS_MODEL200.RECEIPT,mod200.getNumber())
		 .set(FS_MODEL200.COMPLEMENTARY_RECEIPT,mod200.getReplacedNumber())
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
		 .set(FS_MODEL200.NRS_ANEXOV_RIC,mod200.getNrsAnexoVric())
		 .set(FS_MODEL200.JUST_ACTIVOS,mod200.getJustActivos())
		 .set(FS_MODEL200.ULTIMATE_DOCUMENT,mod200.getUltimateDocument())
		 .set(FS_MODEL200.ULTIMATE_DOCUMENT_COUNTRY, Country.safeIso2(mod200.getUltimateDocumentCountry()))
		 .set(FS_MODEL200.ULTIMATE_NAME,mod200.getUltimateName())
		 .set(FS_MODEL200.ULTIMATE_COUNTRY, Country.safeIso2(mod200.getUltimateCountry()))
		 .set(FS_MODEL200.STATUS, AonEnumUtils.getByte(mod200.getStatus()))
		 .set(FS_MODEL200.MODIFICATION_USER, mod200.getModificationUser())
		 .set(FS_MODEL200.MODIFICATION_DATE, AonDateUtils.toTimestamp(mod200.getModificationDate()))
		 .set(FS_MODEL200.FS_MODEL, mod200.getFsModel())
		 .where(FS_MODEL200.ID.equal(mod200.getId()))
		 .execute();
		ctx.log().info("\t\t MOD 200 UPDATED (" + mod200.getId() + ")");
		deleteDetail(ctx, mod200.getId());
		insertDetail(ctx, mod200);
		deleteRegistry(ctx, mod200.getId());
		insertRegistry(ctx, mod200);
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
	
	public static void delete(AONContext ctx, Mod2002022 mod200 )  {
		try {
			int id = mod200.getId();
			ctx.log().info("------ [START] DELETE MOD 200 ["+id+"]");
			deleteRegistry(ctx, id);
			deleteDetail(ctx, id);
			int count = ctx.getDslContext().delete(FS_MODEL200)
				.where(FS_MODEL200.ID.equal(id) )
				.execute();
//			Mod200DAO.deleteFsModel(ctx, mod200.getFsModel()); // Se utilizará a partir del ejercicio 2022
			ctx.log().info("------ [END OK] DELETE MOD 200 ["+id+"] (" + count +" rows )");
		} catch (Throwable t) {
			ctx.log().info("------ [END FAIL] DELETE MOD 200 [" + t.getMessage() + "]");
			throw t;
		}
			
	}

	public static Mod2002022 getById(AONContext ctx, int id ) {
		FsModel200Record record = ctx.getDslContext()
				.selectFrom(FS_MODEL200)
				.where(FS_MODEL200.ID.equal(id))
				.fetchOne();
		Mod2002022 mod200 = populateMod200(ctx,record);
		initializeActiveMap(mod200);
		return mod200;
	}
	
	public static Mod2002022 createNewMod200(AONContext ctx, int year) {
		try {
			ctx.log().info("------ [INI] createNewMod200");
			Mod2002022 mod200 = new Mod2002022();
			mod200.setDomain(ctx.getDomainId());
			mod200.setYear(year);
			mod200.setStatus(FiscalStatus.PENDING);
			initializeNewMod200(ctx,mod200);
			ctx.log().info("------ [END OK] createNewMod200");
			return mod200;
		} catch (Throwable t) {
			ctx.log().info("------ [END FAIL] createNewMod200 [" + t.getMessage() + "]");
			throw t;
		}
	}
	
	public static Mod2002022 getByYear(AONContext ctx, int year) {
		return getByYear(ctx, year, true);
	}
	
	public static Mod2002022 getByYear(AONContext ctx, int year, boolean initialize) {
		Result<FsModel200Record> result = ctx.getDslContext()
				.selectFrom(FS_MODEL200)
				.where(FS_MODEL200.DOMAIN.equal(ctx.getDomainId()))
				.and(FS_MODEL200.YEAR.equal(year))
				.fetch();
		FsModel200Record record = null;
		Mod2002022 mod200 = null; 
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

	private static Mod2002022 getMod200(FsModel200Record record) {
		
		Mod2002022 mod200 = new Mod2002022();
		mod200 = new Mod2002022();
		mod200.setId(record.getId());
		mod200.setYear(record.getYear());
		mod200.setDomain(record.getDomain());
		mod200.setAdministration(AonEnumUtils.enumValue(Administration.class, record.getAdministration()));
		mod200.setEnterprise(record.getEnterprise());
		mod200.setDocument(record.getDocument());
		mod200.setName(record.getName());
		mod200.setEnterprisePhone1(record.getPhone1());
		mod200.setEnterprisePhone2(record.getPhone2());
		mod200.setComplementary(record.getComplementary()==1);
		mod200.setReplacedNumber(record.getComplementaryReceipt());
		mod200.setCnae(record.getCnae());
		mod200.setPeriodEnd(record.getPeriodEnd());
		mod200.setPeriodStart(record.getPeriodStart());
		mod200.setPeriodType(record.getPeriodType());
		mod200.setNumber(record.getReceipt());
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
		mod200.setNrsAnexoVric(record.getNrsAnexovRic());
		mod200.setJustActivos(record.getJustActivos());
		mod200.setUltimateDocument(record.getUltimateDocument());
		mod200.setUltimateDocumentCountry(Country.safeValueOf(record.getUltimateDocumentCountry()));
		mod200.setUltimateName(record.getUltimateName());
		mod200.setUltimateCountry(Country.safeValueOf(record.getUltimateCountry()));
		mod200.setStatus(FiscalStatus.safeValueOf(record.getStatus()));
		mod200.setCreationUser(record.getCreationUser());
		mod200.setCreationDate(record.getCreationDate());
		mod200.setModificationUser(record.getModificationUser());
		mod200.setModificationDate(record.getModificationDate());
	    mod200.setFsModel(record.getFsModel());
		return mod200;
	}
	
	private static Mod2002022 populateMod200(AONContext ctx, FsModel200Record record) {
		Mod2002022 mod200 = null;
		if (record != null) {
			mod200 = getMod200(record);
			fillDetail(mod200,ctx);
			final Mod2002022 mod = mod200;
			ctx.getDslContext() 
				.selectFrom(FS_MODEL200_REGISTRY)
				.where(	FS_MODEL200_REGISTRY.FS_MODEL200.equal(mod200.getId()))
				.fetch()
				.stream()
				.forEach(reg -> Mod2002022RegistryType.populate(mod, reg));
		}
		return mod200;
	}
	
	private static void fillDetail(Mod2002022 mod200,AONContext ctx) {
		
		HashMap<String,Double> map = new HashMap<String,Double>();
		Result<FsModel200DetailRecord> result = ctx.getDslContext()
				.selectFrom(FS_MODEL200_DETAIL)
			 	.where(	FS_MODEL200_DETAIL.FS_MODEL200.equal(mod200.getId()))
			 	.fetch();
		for (FsModel200DetailRecord det : result) {
			try {
				// Comprobamos si la clave es de Mod2002022Key
				Mod2002022Key.valueOf(det.getKey());
				map.put(det.getKey(),det.getValue());
			} catch (IllegalArgumentException e) {
				try {
					// Comprobamos si la clave es de Mod2002022KeyDC (Detalle correcciones)
					Mod2002022KeyDC.valueOf(det.getKey());
					map.put(det.getKey(),det.getValue());
				} catch (IllegalArgumentException e2) {
					System.out.println( "WARNING: Clave "+ det.getKey()+" no encontrada!" );
				}
			}
		}

		DoubleVariableEx v = null;
		// Cargamos las claves de Mod2002022Key
		for (Mod2002022Key key : Mod2002022Key.values() ) {
			v = new DoubleVariableEx( key );
			if (map.containsKey(key.toString())) {
				v.setValue( map.get(key.toString()));
			} else {
				v.setValue( 0.0 );
			}
			mod200.addVariable(v);
		}
		
		// Cargamos las claves de Mod2002022KeyDC (Detalle correcciones)
		for (Mod2002022KeyDC key : Mod2002022KeyDC.values() ) {
			v = new DoubleVariableEx( key );
			if (map.containsKey(key.toString())) {
				v.setValue( map.get(key.toString()));
			} else {
				v.setValue( 0.0 );
			}
			mod200.addVariable(v);
		}

		BalanceType bt = null;
		if (map.containsKey( Mod2002022Key.C0050.toString() )) {
			bt = BalanceType.NORMAL;
		} else if (map.containsKey( Mod2002022Key.C0051.toString() )) {
			bt = BalanceType.ABREVIADO;
		} else if (map.containsKey( Mod2002022Key.C0052.toString() )) {
			bt = BalanceType.PYMES;
		}
		mod200.setBalanceType( bt );
		
		EcpnType et = EcpnType.NO_CONSTA;
		if (map.containsKey( Mod2002022Key.C0075.toString() )) {
			et = EcpnType.NORMAL;
		} else if (map.containsKey( Mod2002022Key.C0076.toString() )) {
			et = EcpnType.ABREVIADO;
		} else if (map.containsKey( Mod2002022Key.C0077.toString() )) {
			et = EcpnType.PYMES;
		}
		mod200.setEcpnType(et);
		
		bt = null;
		if (map.containsKey( Mod2002022Key.C0053.toString() )) {
			bt = BalanceType.NORMAL;
		} else if (map.containsKey( Mod2002022Key.C0054.toString() )) {
			bt = BalanceType.ABREVIADO;
		} else if (map.containsKey( Mod2002022Key.C0055.toString() )) {
			bt = BalanceType.PYMES;
		}
		mod200.setPygType( bt );
	}
	
	public static Mod2002022 initializeNewMod200(AONContext ctx, Mod2002022 mod200) {
		
		Mod2002021 old = Mod2002021DAO.getByYear(ctx, 2021, false);
		if (old != null && old.getId() != null) { 
			ctx.log().info("------ [START] INITIALIZE NEW MOD 200 FROM MOD 200 2021");
			mod200.setEnterprise(old.getEnterprise());
			Mod2002022Import2021.import2021(mod200,old);
			mod200.setInitializedFromLastYear(true);
		} else {
			ctx.log().info("------ [START] INITIALIZE NEW MOD 200 FROM CONFIGURATION");
			mod200.setPeriodType(1);
			mod200.setPeriodStart(AonDateUtils.getYearFirstDay(mod200.getYear()));
			mod200.setPeriodEnd(AonDateUtils.getYearLastDay(mod200.getYear()));
	
			mod200.setBalanceType( BalanceType.ABREVIADO );
			mod200.setEcpnType( EcpnType.NO_CONSTA );
			mod200.setPygType(BalanceType.ABREVIADO );
			
			AonConfiguration conf = ConfigurationDAO.getConfiguration(ctx);
			mod200.setEnterprise(conf.getCompany().getId());
			mod200.setDocument(conf.getCompany().getDocument());
			mod200.setName(conf.getCompany().getName());
			mod200.setEnterprisePhone1(conf.fiscal().getContactPhone());
			mod200.setEnterprisePhone2(conf.fiscal().getContactCellular());
			Administration adm = conf.fiscal().getAdministration(Administration.COMMON_TERRITORY);
			mod200.setAdministration(adm);
			mod200.setInitializedFromLastYear(false);
			
			// CNAE - Se coge de la actividad principal (solo si está indicado a 4 dígitos)			
			AonConfiguration configuration = ConfigurationDAO.getConfiguration(ctx);			
			if (configuration.getMainActivity() != null) {
				String cnae = configuration.getMainActivity().getCnaeCode();
				if (cnae != null && cnae.length()==4) {
					mod200.setCnae(cnae.substring(0, 2) + "." + cnae.substring(2));
				}
			}			
			
		}
		ctx.log().info("------ [END OK] INITIALIZE NEW MOD 200");
		return mod200;
	}
	
	public static Mod2002022 initializeMod200(AONContext ctx, Mod2002022 mod200) {
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
			
			// Datos grupo mercantil, no se graban si hemos desmarcado el caracter 0081
			if (mod200.isNotChecked(Mod2002022Key.C0081)) {			
				mod200.setUltimateDocument("");            // Grupo - Clave 00081 - Datos de la sociedad matriz última: NIF o equivalente.
				mod200.setUltimateDocumentCountry(null);   // Grupo - Clave 00081 - Datos de la sociedad matriz última: Código país
				mod200.setUltimateName("");				   // Grupo - Clave 00081 - Datos de la sociedad matriz última: Nombre o razón social
				mod200.setUltimateCountry(null); 		   // Grupo - Clave 00081 - Datos de la sociedad matriz última: País o jurisdicción		
		    }	
			
			// Grupos de sociedades, art. 42 código de comercio, incluidas entidades de crédito y aseguradoras
			// NIF de las entidades del grupo, dejarlo vacio si no se ha marcado el caracter 00039
			if (mod200.isNotChecked(Mod2002022Key.C0039)) {
				mod200.getGroupEntities().clear();
			}
			
			// No residentes con más de un establecimiento permanente
			// NIF establecimientos permanentes, dejarlo vacio si no se ha marcado el caracter 00021
			if (mod200.isNotChecked(Mod2002022Key.C0021)) {
				mod200.getEstablishments().clear(); 
			}
			
			// Agrupaciones de interés económico y UTES - Relación de Socios
			// Dejarlo vacio si no se ha marcado el carácter 00013 o 00014
			if (mod200.isNotChecked(Mod2002022Key.C0013) && mod200.isNotChecked(Mod2002022Key.C0014)) {
				mod200.getUteParticipations().clear(); 
			}
			
			// Inicialización estados contables (solo afecta a claves de Mod2002022Key)
			Mod2002022MVELContext mvelCtx = new Mod2002022MVELContext( mod200, ACCEPTER );		
			AccMiningParameters params = getParams(ctx,mod200);
			if (params != null) {
				mvelCtx.setAccounts( ACCOUNTING.getAccountBalances(ctx, params) );
			} else {
				mvelCtx.setAccounts( new HashMap<String,AccountBalance>() );
			}
			mvelCtx.setExpressionMap(INITIALIZE_EXPRESSION_MAP);
			addCharacters(mvelCtx,mod200);
			addBalanceCharacters(mvelCtx,mod200);
			DoubleVariableEx dv = null;
			for (Mod2002022Key k : INITIALIZE_EXPRESSION_MAP.keySet()) {
				String stringKey = k.toString();
				String expression = INITIALIZE_EXPRESSION_MAP.get(k);
				mvelCtx.put(stringKey, 0.0 );
				Object ret = mvelCtx.evaluateExpression(k,expression);
				mvelCtx.put(stringKey, ret );
				if (ret instanceof Double ) {
					dv = new DoubleVariableEx( k );
					dv.setValue( (Double) ret );
					mod200.addVariable( dv );
				} 
				if (ret instanceof Boolean) {
					dv = new DoubleVariableEx( k );
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
	
	private static void fillMod202(AONContext ctx,Mod2002022 mod200) {
		Mod202DAO.getMod202s(ctx, mod200.getDomain())
			.filter(mod -> mod.getYear() == mod200.getYear() && mod.getStatus() == FiscalStatus.FINISHED )
			
			.forEach( mod -> {
				Mod202 mod202 = Mod202DAO.getMod202(ctx, mod.getId());		
				Mod2002022Key key = null;
				if (mod202.getPeriod() == Period.T1) {
					key = Mod2002022Key.BN601;
				} else if (mod202.getPeriod() == Period.T2) {
					key = Mod2002022Key.BN603;
				} if (mod202.getPeriod() == Period.T3) {
					key = Mod2002022Key.BN605;
				}
				if (key != null) {
					DoubleVariableEx dv = new DoubleVariableEx( key );
					dv.setValue( (Double) mod202.getResult() );
					mod200.addVariable( dv );
				}
			});
	}

	public static Mod2002022 calculate(Mod2002022 mod200) {
		return calculate(mod200,true);		
	}
	
	private static Mod2002022 calculate(Mod2002022 mod200, boolean addToDraft) {
		try {
			Mod2002022MVELContext ctx = new Mod2002022MVELContext( mod200, ACCEPTER );
			
			// No asignamos Expression Map al contexto MVEL, para ganar en velocidad de cálculo del 
			// modelo, cada vez que se modifica una casilla (dado el numero de casillas que tiene el
			// modelo actualmente). Eso obliga a que en COMPUTE_EXPRESSION_MAP estén introducidos los
			// datos, siguiendo el orden de los cálculos que se deban realizar
//			ctx.setExpressionMap(Mod2002022Compute.COMPUTE_EXPRESSION_MAP);			
			
			// Añadir valores de keysMap
			for (DoubleVariableEx dv : mod200.getKeysMap().values()) {
				ctx.put(dv.getKey().toString(), dv.getValue());
			}
			addCharacters(ctx,mod200);
			addBalanceCharacters(ctx,mod200);
			
			// Añadir valores de draftMap (modificadas por el usuario)
			DoubleVariableEx d = null;
			for (IMod200Key key : mod200.getDraftMap().keySet() ) {
				d = mod200.getDraftMap().get(key);
				if (d.isChangedByUser()) {
					ctx.put(key.toString(), d.getValue());
				}
			}
			
			// Actualmente las casillas calculadas solo son de Mod2002022Key
			DoubleVariableEx v = null;
			for (Mod2002022Key k : Mod2002022Compute.COMPUTE_EXPRESSION_MAP.keySet()) {
				String stringKey = k.toString();
				DoubleVariableEx existingVariable = mod200.getVariable(k);
				Double existingValue = ( existingVariable == null )?0.0:existingVariable.getValue();
				ctx.put(stringKey, existingValue);
				Object ret = ctx.evaluateExpression(k,Mod2002022Compute.COMPUTE_EXPRESSION_MAP.get(k));
				if (ret instanceof Double) {
					Double calculated = (Double) ret;
					ctx.put(stringKey, calculated);
					if ( !AonMathUtils.equals( existingValue , calculated ) ) {
						v = new DoubleVariableEx( k );
						v.setValue( calculated );
						if (addToDraft) {
							mod200.addDraftVariable(v);	
						} else {
							mod200.addVariable(v);
						}
					}
				}
			}
			
			// Grabar Importe a ingresar o devolver (amount) y tipo de ingreso o devolucion (result_type, dev_type y pay_type)  
			v = mod200.getVariable(Mod2002022Key.BN621);
			mod200.setResultType(null);
			if (v == null || v.getValue() == 0) {
				// Cuota cero
				mod200.setResultType("N");
				mod200.setAmount( 0.0 );
				mod200.setDevType(null);
				mod200.setPayType(null);
			} else if (AonMathUtils.round(v.getValue()) < 0.0) {
				// Devolución
				mod200.setResultType("D");
				mod200.setAmount( AonMathUtils.round( v.getValue() * -1));
				mod200.setDevType(AonStringUtils.isEmpty(mod200.getDevType())?"D":mod200.getDevType());
				mod200.setPayType(null);
			} else {
				// Ingreso
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
	
	private static AccMiningParameters getParams(AONContext ctx,Mod2002022 mod200) throws AonCoreException {
		AccMiningParameters params = new AccMiningParameters();
		params.setDomain(mod200.getDomain());
		params.setYear(mod200.getYear());
		 
		AccountPeriod period =  AccountPeriodDAO.getPeriodByYear(ctx, mod200.getYear());
		if (period == null) {
			return null;
		}
		
		params.setPeriodId(period.getId());
		params.setStartDate(period.getInitiationDate());
		params.setEndDate(period.getDeadline());
		return params;
	}

	private static void addCharacters(Mod2002022MVELContext ctx, Mod2002022 mod200) {
		// Caracteres de la declaración, solo están en Mod2002022Key
		for (Mod2002022Key key : Mod2002022Character.CHARACTERS_KEYS) {
			DoubleVariableEx dv = mod200.getKeysMap().get(key);
			ctx.put(key.toString(), (dv != null && dv.getBooleanValue() )); 
		}		
	}
	
	private static void initializeActiveMap(Mod2002022 mod200) {
		// Actualmente la visualizacion de determinadas casillas solo afecta a Mod2002022Key
		Mod2002022MVELContext ctx = new Mod2002022MVELContext( mod200, ACCEPTER );
		ctx.setExpressionMap(Mod2002022Activation.ACTIVE_EXPRESSION_MAP);
		addCharacters(ctx,mod200);
		addBalanceCharacters(ctx,mod200);
		for (Mod2002022Key key : Mod2002022Key.values() ) {
			if (Mod2002022Activation.ACTIVE_EXPRESSION_MAP.containsKey(key) ) {
				Object ret = ctx.get( key.toString() );
				if (ret instanceof Boolean && ((Boolean) ret) ) {
					mod200.getVisibleMap().put(key,true);
				}
			} else {
				mod200.getVisibleMap().put(key,true);
			}
		}
	}

	private static void addBalanceCharacters(Mod2002022MVELContext ctx, Mod2002022 mod200) {
		ctx.put(Mod2002022Key.C0050.toString(), mod200.getBalanceType() == BalanceType.NORMAL);
		ctx.put(Mod2002022Key.C0051.toString(), mod200.getBalanceType() == BalanceType.ABREVIADO);
		ctx.put(Mod2002022Key.C0052.toString(), mod200.getBalanceType() == BalanceType.PYMES);
		
		ctx.put(Mod2002022Key.C0075.toString(), mod200.getEcpnType() == EcpnType.NORMAL);
		ctx.put(Mod2002022Key.C0076.toString(), mod200.getEcpnType() == EcpnType.ABREVIADO);
		ctx.put(Mod2002022Key.C0077.toString(), mod200.getEcpnType() == EcpnType.PYMES);

		ctx.put(Mod2002022Key.C0053.toString(), mod200.getPygType() == BalanceType.NORMAL);
		ctx.put(Mod2002022Key.C0054.toString(), mod200.getPygType() == BalanceType.ABREVIADO);
		ctx.put(Mod2002022Key.C0055.toString(), mod200.getPygType() == BalanceType.PYMES);
	}

	public static Mod2002022 importMod2002021(AONContext ctx, Mod2002022 mod200) {
		Mod2002021 old = Mod2002021DAO.getByYear(ctx, 2021);
		return Mod2002022Import2021.import2021(old);
	}
	
}
