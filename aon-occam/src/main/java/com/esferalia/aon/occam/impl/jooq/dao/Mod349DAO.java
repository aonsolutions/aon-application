package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Alcatraz.ALCATRAZ;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.FsMod349.FS_MOD349;
import static com.esferalia.aon.jooq.tables.FsMod349Detail.FS_MOD349_DETAIL;
import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static com.esferalia.aon.jooq.tables.Iae.IAE;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.Invoice;
import com.esferalia.aon.jooq.tables.records.FsMod349Record;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.finance.FinanceUtil;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelUtils;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod349;
import com.esferalia.aon.occam.api.model.fiscal.Mod349Detail;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.Mod349Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.VATRegime;
import com.esferalia.aon.occam.impl.jooq.dao.Mod349Formatter.Mod349DetailInfo;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.AlcatrazDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.AlcatrazDAO.Alcatraz;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod349DAO {
	
	private static byte ZERO_BYTE = 0;
	private static byte ONE_BYTE = 1;
	
	public static Stream<Mod349> getHeaders(AONContext ctx, int domain) {
		return getHeaders(ctx, domain, null);
	}
	
	public static Stream<Mod349> getHeaders(AONContext ctx, int domain, Integer scope) {
		ctx.checkRead();
		return  ctx.getDslContext()
			.select(FS_MOD349.fields())
			.select(DOMAIN.DESCRIPTION)
			.from(FS_MOD349)
			.join(DOMAIN).on(FS_MOD349.DOMAIN.equal(DOMAIN.ID))
			.where(FS_MOD349.DOMAIN.equal(domain).or(DOMAIN.PARENT.equal(domain)))
			.and( scope == null ? DSL.trueCondition() : DOMAIN.SCOPE.equal(scope))
			.orderBy(FS_MOD349.YEAR.desc()
					,getDueMonth().desc()  // Se ordena por el mes hasta del periodo, por que en un mismo ejercicio podrian coincidir varias periodicidades (trimestral y mensual, por ejemplo)
					,FS_MOD349.NAME.asc()
					,FS_MOD349.COMPLEMENTARY.asc()
					,FS_MOD349.REPLACEMENT.asc())
			.fetch()
			.stream()
			.map(new Mod349Filler());
	}

	public static LinkedList<Mod349> getByDomain(AONContext ctx, int domain) {
		ctx.checkRead();
		return  ctx.getDslContext()
			.select(FS_MOD349.fields())
			.select(DOMAIN.DESCRIPTION)
			.from(FS_MOD349)
			.join(DOMAIN).on(FS_MOD349.DOMAIN.equal(DOMAIN.ID))
			.where(FS_MOD349.DOMAIN.equal(domain).or(DOMAIN.PARENT.equal(domain)))
			.orderBy(FS_MOD349.YEAR.desc()
					,getDueMonth().desc()  // Se ordena por el mes hasta del periodo, por que en un mismo ejercicio podrian coincidir varias periodicidades (trimestral y mensual, por ejemplo)
					,FS_MOD349.NAME.asc()
					,FS_MOD349.COMPLEMENTARY.asc()
					,FS_MOD349.REPLACEMENT.asc())
			.fetch()
			.stream()
			.map(new Mod349Filler())
			.peek( mod349 -> mod349.setDetails( getDetails(ctx, mod349.getId()) ))
			.collect(Collectors.toCollection(LinkedList::new));
	}

	public static Mod349 getById(AONContext ctx, int id) {		
		ctx.checkRead();
		return ctx.getDslContext()
			.select(FS_MOD349.fields())
			.select(DOMAIN.DESCRIPTION)
			.from(FS_MOD349)
			.join(DOMAIN).on(FS_MOD349.DOMAIN.equal(DOMAIN.ID))
			.where(FS_MOD349.DOMAIN.equal(ctx.getDomainId()).or(DOMAIN.PARENT.equal(ctx.getDomainId())))
			.and(FS_MOD349.ID.equal(id))
			.fetch()
			.stream()
			.map(new Mod349Filler())
			.peek( mod349 -> mod349.setDetails( getDetails(ctx, mod349.getId()) ))
			.findFirst()
			.orElse(null);
	}
	
	public static Mod349 saveComments(AONContext ctx, Mod349 fm) {
		try {
			ctx.checkWrite();
			if (fm.getId() != null) {
				ctx.getDslContext().update(FS_MOD349)
					.set(FS_MOD349.COMMENTS,fm.getComments())
					.where(FS_MOD349.ID.equal(fm.getId()))
					.execute();
			}
			return fm;
		} catch (DataAccessException t) {
			throw new AonCoreException(t.getCause()!=null?t.getCause().getMessage():t.getMessage());
		} catch (Throwable t) {
			throw new AonCoreException(t.getMessage());
		}
	}
	
	public static Mod349 save(AONContext ctx, Mod349 mod349) {
		try {
			ctx.checkWrite();
			if (mod349.getId() == null) {
				mod349 = insert(ctx, mod349); 
				 
			} else {
				mod349 = update(ctx, mod349);
			}
			for (Mod349Detail detail : mod349.getDetails()) {
				saveDetail(ctx,mod349,detail);
			}
			return getById(ctx, mod349.getId());			 
		} catch (Throwable t) {
			t.printStackTrace();
			throw new AonCoreException(t.getCause()!=null?t.getCause().getMessage():t.getMessage());
		}
	}
	
	private static Mod349 insert(AONContext ctx, Mod349 mod349) {
		return insert(ctx, mod349, true);
	}

	private static Mod349 insert(AONContext ctx, Mod349 mod349, boolean generateDetails) {
		validate(ctx, mod349);
		mod349.setCreationUser(ctx.getUser());
		mod349.setCreationDate(new Timestamp(System.currentTimeMillis()));
		mod349.setFsModel(saveFsModel(ctx, mod349)); 
		FsMod349Record record = ctx.getDslContext().insertInto(FS_MOD349)
			.set(FS_MOD349.DOMAIN,mod349.getDomain())
			.set(FS_MOD349.YEAR,mod349.getYear())
			.set(FS_MOD349.PERIOD,mod349.getPeriod().value())			
			.set(FS_MOD349.ADMINISTRATION, mod349.getAdministration().value())
			.set(FS_MOD349.COMMENTS,mod349.getComments())
			.set(FS_MOD349.STATUS, ZERO_BYTE )
			.set(FS_MOD349.SECURITY_LEVEL, AonEnumUtils.getByte(mod349.isConfidential()) )			
			.set(FS_MOD349.COMPLEMENTARY, AonEnumUtils.getByte(mod349.isComplementary()))
			.set(FS_MOD349.REPLACEMENT, AonEnumUtils.getByte(mod349.isReplacement()))
			.set(FS_MOD349.NUMBER,mod349.getNumber())
			.set(FS_MOD349.REPLACED_NUMBER,mod349.getReplacedNumber())
			.set(FS_MOD349.DOCUMENT,mod349.getDocument())
			.set(FS_MOD349.REPRESENTATIVE_DOCUMENT,mod349.getRepresentativeDocument())
			.set(FS_MOD349.NAME,mod349.getName())
			.set(FS_MOD349.CONTACT_PHONE,mod349.getContactPhone())
	    	.set(FS_MOD349.CONTACT_PERSON,mod349.getContactPerson())
			.set(FS_MOD349.CONTACT_MAIL,mod349.getContactMail())			
			.set(FS_MOD349.PERIODICITY_CHANGE,AonEnumUtils.getByte(mod349.isPeriodicityChange()))
			.set(FS_MOD349.DIFF_ENABLED, AonEnumUtils.getByte(mod349.isDiffEnabled()) )			
			.set(FS_MOD349.CREATION_USER, mod349.getCreationUser())
			.set(FS_MOD349.CREATION_DATE, AonDateUtils.toTimestamp(mod349.getCreationDate()))
			.set(FS_MOD349.FS_MODEL, mod349.getFsModel())			
		.returning(FS_MOD349.ID)
		.fetchOne();
		mod349.setId(record.getId());
		if (generateDetails && !mod349.isManualDeclaration()) {			
			insertDetailsFromInvoice(ctx, mod349); // Se rellena el modelo leyendo de las facturas intracomunitarias
		}
		return mod349;
	}

	private static Mod349 update(AONContext ctx, Mod349 mod349) {
		mod349.setModificationUser(ctx.getUser());
		mod349.setModificationDate(new Timestamp(System.currentTimeMillis()));
		mod349.setFsModel(saveFsModel(ctx, mod349));
		ctx.getDslContext().update(FS_MOD349)			
			.set(FS_MOD349.YEAR,mod349.getYear())
			.set(FS_MOD349.PERIOD,mod349.getPeriod().value())			
			.set(FS_MOD349.ADMINISTRATION, mod349.getAdministration().value())
			.set(FS_MOD349.COMMENTS,mod349.getComments())
			.set(FS_MOD349.STATUS, AonEnumUtils.getByte( mod349.getStatus() ) )
			.set(FS_MOD349.SECURITY_LEVEL,AonEnumUtils.getByte(mod349.isConfidential()) )			
			.set(FS_MOD349.COMPLEMENTARY,AonEnumUtils.getByte(mod349.isComplementary()))
			.set(FS_MOD349.REPLACEMENT,AonEnumUtils.getByte(mod349.isReplacement()))
			.set(FS_MOD349.NUMBER,mod349.getNumber())
			.set(FS_MOD349.REPLACED_NUMBER,mod349.getReplacedNumber())
			.set(FS_MOD349.DOCUMENT,mod349.getDocument())
			.set(FS_MOD349.NAME,mod349.getName())
			.set(FS_MOD349.CONTACT_PHONE,mod349.getContactPhone())
			.set(FS_MOD349.CONTACT_PERSON,mod349.getContactPerson())
			.set(FS_MOD349.CONTACT_MAIL,mod349.getContactMail())
			.set(FS_MOD349.PERIODICITY_CHANGE,AonEnumUtils.getByte(mod349.isPeriodicityChange()))
			.set(FS_MOD349.DIFF_ENABLED, AonEnumUtils.getByte(mod349.isDiffEnabled()) )
			.set(FS_MOD349.REPRESENTATIVE_DOCUMENT,mod349.getRepresentativeDocument())
			.set(FS_MOD349.MODIFICATION_USER, mod349.getModificationUser())
			.set(FS_MOD349.MODIFICATION_DATE, AonDateUtils.toTimestamp(mod349.getModificationDate()))
			.set(FS_MOD349.FS_MODEL, mod349.getFsModel())
			.where(FS_MOD349.ID.equal(mod349.getId()))
		.execute();
		return mod349;
	}

	public static void delete(AONContext ctx, Mod349 mod349) {
		ctx.checkWrite();
		
		// Borrar filas en fs_mod349_detail
		deleteDetails(ctx, mod349);
		
		// Borrar filas vinculadas al modelo en alcatraz
		AlcatrazDAO.deleteFiscalModel(ctx, getFiscalModel(mod349)); // NO SE PUEDE HACER ASI PORQUE EL ID QUE HAY QUE BORRAR ES EL DE FS_MODEL, NO EL DE FS_MOD349
		  	
		// Borrar fila en fs_mod349
		ctx.getDslContext().delete(FS_MOD349)
			.where(FS_MOD349.ID.equal(mod349.getId()))
			.execute();
		
		// Borrar fila en fs_model
		deleteFsModel(ctx, mod349.getFsModel());  
	}
	
	private static void validate(AONContext ctx, Mod349 mod349) {
		
		// Comprobar que esta cumplimentado el ejercicio y el periodo
		if (mod349.getYear() == 0 || mod349.getPeriod() == null)
			throw new AonCoreException("Debe cumplimentar Ejercicio y Periodo.");
		
		// NIF Declarante debe estar cumplimentado y de longitud menor de 9
		if (AonStringUtils.isBlank(mod349.getDocument()) || mod349.getDocument().length() > 9)
			throw new AonCoreException("El NIF del Declarante debe estar cumplimentado y su longitud no puede ser mayor de 9 caracteres.");
		
		// Se comprueba que no exista otra declaracion sustitutiva que sustituya a la misma anterior
		if (mod349.isReplacement()) {
						
			if (ctx.getDslContext().selectOne()
				.from(FS_MOD349)
				.where(FS_MOD349.DOMAIN.equal(mod349.getDomain())
						.and(FS_MOD349.YEAR.equal(mod349.getYear()))
						.and(FS_MOD349.PERIOD.equal(mod349.getPeriod().value()))
						.and(FS_MOD349.ADMINISTRATION.equal(mod349.getAdministration().value()))
						.and(FS_MOD349.REPLACEMENT.equal( ONE_BYTE ))
						.and(FS_MOD349.REPLACED_NUMBER.equal(mod349.getReplacedNumber())))
				.fetch()
				.stream()
				.findFirst()
				.isPresent()) 
				throw new AonCoreException(AonError.FISCAL_DECLARATION_ALREADY_REPLACED.getMessage());			
		}
		else if (mod349.isReplacement() || mod349.isComplementary()) {
			// Se comprueba que exista la declaracion sustituida/complementada
			// *** Esto no se comprueba, pues no podriamos crear una complementaria/sustitutiva si 
			// *** antes no tenemos creada la declaracion a la que sustituye, ademas no veo que se 
			// *** cumplimente "number" en ningun momento, con el numero real de presentacion
//			if (!ctx.getDslContext().selectOne()
//					.from(FS_MOD349)
//					.where(FS_MOD349.YEAR.equal(mod349.getYear())
//							.and(FS_MOD349.PERIOD.equal(mod349.getPeriod().getValue()))
//							.and(FS_MOD349.ADMINISTRATION.equal(mod349.getAdministration().getValue()))					
//							.and(FS_MOD349.NUMBER.equal(mod349.getReplacedNumber())))
//					.fetch()
//					.stream()
//					.findFirst()
//					.isPresent()) 
//				throw new AonCoreException(
//						AonError.FISCAL_NO_REPLACED_DECLARATION.getMessage());

		}
		else {
			// Se comprueba que no exista ya una declaracion, para el perido indicado
			if (ctx.getDslContext().selectOne()
				.from(FS_MOD349)
				.where(FS_MOD349.DOMAIN.equal(mod349.getDomain())
						.and(FS_MOD349.YEAR.equal(mod349.getYear()))
						.and(FS_MOD349.PERIOD.equal(mod349.getPeriod().value()))
						.and(FS_MOD349.ADMINISTRATION.equal((byte) mod349.getAdministration().value()))
						.and(FS_MOD349.REPLACEMENT.equal(ZERO_BYTE))
						.and(FS_MOD349.COMPLEMENTARY.equal(ZERO_BYTE)))				
				.fetch()
				.stream()
				.findFirst()
				.isPresent()) 
				throw new AonCoreException(
						AonError.FISCAL_DECLARATION_ALREADY_EXISTS.getMessage());
		}
	}
	
	public static Mod349Detail getDetail(AONContext ctx, int id) {
		ctx.checkRead();
		return ctx.getDslContext()
			.select(FS_MOD349_DETAIL.fields())
			.from(FS_MOD349_DETAIL)
			.where(FS_MOD349_DETAIL.ID.equal(id))
			.fetch()
			.stream()
			.map(new Mod349DetailFiller())
			.findFirst()
			.orElse(null);
	}

	public static LinkedList<Mod349Detail> getDetails(AONContext ctx, int mod349) {
		ctx.checkRead();
		return ctx.getDslContext()
			.selectFrom(FS_MOD349_DETAIL)
			.where(FS_MOD349_DETAIL.FS_MOD349.equal(mod349))
			.fetch()
			.stream()
			.map( new Mod349DetailFiller() )
			.collect(Collectors.toCollection(LinkedList::new));
	}

	public static void saveDetail(AONContext ctx, Mod349 mod349, Mod349Detail detail){
		ctx.checkWrite();
		if (detail.getId() == null) {
			if (!detail.isDeleted()) {
				detail.setDomain(mod349.getDomain());
				detail.setMod349(mod349.getId());
				insertDetail(ctx, detail);
			}
		} else {
			if (detail.isDeleted()) {
				deleteDetail(ctx, detail, mod349);
			} else {
				updateDetail(ctx, detail);
			}
		}
	}
	
	private static void insertDetail(AONContext ctx, Mod349Detail detail) {
		validateDetail(ctx, detail);		
		ctx.getDslContext().insertInto(FS_MOD349_DETAIL)
			.set(FS_MOD349_DETAIL.DOMAIN,detail.getDomain())
			.set(FS_MOD349_DETAIL.FS_MOD349,detail.getMod349())
			.set(FS_MOD349_DETAIL.RECTIFICATION, AonEnumUtils.getByte(detail.isRectification()))
			.set(FS_MOD349_DETAIL.TYPE, Mod349Key.safeValue(detail.getType()))			
			.set(FS_MOD349_DETAIL.DOCUMENT, detail.getDocument())
			.set(FS_MOD349_DETAIL.REGISTRY, detail.getRegistry())
			.set(FS_MOD349_DETAIL.NAME, detail.getName())
			.set(FS_MOD349_DETAIL.COUNTRY, Country.safeIso2(detail.getCountry()))
			.set(FS_MOD349_DETAIL.ACCUMULATED, detail.getAccumulated())
			.set(FS_MOD349_DETAIL.DECLARED, detail.getDeclared())
			.set(FS_MOD349_DETAIL.AMOUNT, detail.getAmount())			
			.set(FS_MOD349_DETAIL.RECTIFIED_YEAR, detail.getRectifiedYear())
			.set(FS_MOD349_DETAIL.RECTIFIED_PERIOD, AonEnumUtils.getByte(detail.getRectifiedPeriod()))
			.set(FS_MOD349_DETAIL.RECTIFIED_AMOUNT, detail.getRectifiedAmount())			
			.execute();
	}

	private static void updateDetail(AONContext ctx, Mod349Detail detail) {
		validateDetail(ctx, detail);
		ctx.getDslContext().update(FS_MOD349_DETAIL)			
			.set(FS_MOD349_DETAIL.RECTIFICATION, AonEnumUtils.getByte(detail.isRectification()))
			.set(FS_MOD349_DETAIL.TYPE, Mod349Key.safeValue(detail.getType()))			
			.set(FS_MOD349_DETAIL.DOCUMENT, detail.getDocument())
			.set(FS_MOD349_DETAIL.REGISTRY, detail.getRegistry())
			.set(FS_MOD349_DETAIL.NAME, detail.getName())
			.set(FS_MOD349_DETAIL.COUNTRY, Country.safeIso2(detail.getCountry()))
			.set(FS_MOD349_DETAIL.ACCUMULATED, detail.getAccumulated())
			.set(FS_MOD349_DETAIL.DECLARED, detail.getDeclared())
			.set(FS_MOD349_DETAIL.AMOUNT, detail.getAmount())			
			.set(FS_MOD349_DETAIL.RECTIFIED_YEAR, detail.getRectifiedYear())
			.set(FS_MOD349_DETAIL.RECTIFIED_PERIOD, AonEnumUtils.getByte(detail.getRectifiedPeriod()))
			.set(FS_MOD349_DETAIL.RECTIFIED_AMOUNT, detail.getRectifiedAmount())			 
			.where(FS_MOD349_DETAIL.ID.equal(detail.getId()))
			.execute();
	}
	
	private static void deleteDetail(AONContext ctx, Mod349Detail detail, Mod349 mod349) {
		
		// Se comprueba si el modelo tiene facturas vinculadas en Alcatraz, para borrar 
		// las facturas que se supone que están vinculadas a la linea
		// Si acumulado y declarado son cero, se asume que es una linea manual, en cuyo caso no tendrá vinculadas facturas en alcatraz
		if ((detail.getAccumulated() != 0 || detail.getDeclared() != 0) && hasAlcatrazInvoices(ctx, mod349.getFsModel())) {			
			getVatBreakdownInfo(ctx, mod349, detail, true)  
			 .filter( p -> isInvoiceDeclared(ctx, p.getInvoice(), mod349.getFsModel())) 
			 .forEach( p -> ctx.getDslContext()
								.delete(ALCATRAZ)
								.where(ALCATRAZ.FS_MODEL.equal(mod349.getFsModel())).and(ALCATRAZ.INVOICE.equal(p.getInvoice()))
								.execute()
					 );			
		}
				
		// Borrar la fila en fs_mod349_detail
		ctx.getDslContext().delete(FS_MOD349_DETAIL)
			.where(FS_MOD349_DETAIL.ID.equal(detail.getId()))
			.execute();
		
	}

	private static void deleteDetails(AONContext ctx, Mod349 mod349) {
		ctx.getDslContext().delete(FS_MOD349_DETAIL)
			.where(FS_MOD349_DETAIL.FS_MOD349.equal(mod349.getId()))
			.execute();
	}
	
	private static void validateDetail(AONContext ctx, Mod349Detail detail) {
		
		// Se comprueba unicamente si esta cumplimentado el apartado de rectificaciones
		// para verificar que esta indicado ejercicio y periodo rectificado
		if (detail.getRectifiedYear() != null || detail.getRectifiedPeriod() != null || detail.getRectifiedAmount() != 0) {
			if (detail.getRectifiedYear() == null || detail.getRectifiedPeriod() == null) {
				throw new AonCoreException("Si indica rectificaciones, debe indicar los datos A\u00F1o y Periodo");			
			}
		}
		
		// Ademas se marca el campo "rectificado", si es una rectificacion
		detail.setRectification(detail.getRectifiedYear()!=null);
		
	}
	
	// Mes hasta del periodo de la cabecera (meses de 0 -enero- a 11 -diciembre-)
	public static Field<Byte> getDueMonth() {
		return DSL.decode()
		   .when(FS_MOD349.PERIOD.equal((byte) 12), (byte) 2)   // 1T
		   .when(FS_MOD349.PERIOD.equal((byte) 13), (byte) 5)   // 2T
		   .when(FS_MOD349.PERIOD.equal((byte) 14), (byte) 8)   // 3T
		   .when(FS_MOD349.PERIOD.equal((byte) 15), (byte) 11)  // 4T
		   .when(FS_MOD349.PERIOD.equal((byte) 16), (byte) 11)  // Anual
		   .otherwise(FS_MOD349.PERIOD);  						// Mensual
	}
	
	// Mes hasta del periodo de la linea (meses de 0 -enero- a 11 -diciembre-)
	public static Field<Byte> getDueMonthDetail() {
		return DSL.decode()
		   .when(FS_MOD349_DETAIL.RECTIFIED_PERIOD.equal((byte) 12), (byte) 2)   // 1T
		   .when(FS_MOD349_DETAIL.RECTIFIED_PERIOD.equal((byte) 13), (byte) 5)   // 2T
		   .when(FS_MOD349_DETAIL.RECTIFIED_PERIOD.equal((byte) 14), (byte) 8)   // 3T
		   .when(FS_MOD349_DETAIL.RECTIFIED_PERIOD.equal((byte) 15), (byte) 11)  // 4T
		   .when(FS_MOD349_DETAIL.RECTIFIED_PERIOD.equal((byte) 16), (byte) 11)  // Anual
		   .otherwise(FS_MOD349_DETAIL.RECTIFIED_PERIOD);  					     // Mensual
	}
	
	public static Mod349 initialize(AONContext ctx) {	
		
		// Ponemos por defecto el año, segun la fecha actual, si estamos en enero ponemos
		// el año anterior (se supone que queremos hacer el del ultimo periodo del año anterior)
		// en caso contrario ponemos el año actual
		Date today = new Date();
		int year = AonDateUtils.getYear(today);		
		if (AonDateUtils.getMonth(today) == 0) {
			year = year - 1;			
		}
		
		AonConfiguration conf = ConfigurationDAO.getConfiguration(ctx);		
		Mod349 mod349 = new Mod349();
		mod349.setDomain(ctx.getDomainId());
		mod349.setYear(year);		
		mod349.setAdministration(conf.fiscal().getAdministration(Administration.COMMON_TERRITORY));
		mod349.setNumber("3490000000001");
		mod349.setDocument(conf.getCompany().getDocument());
		mod349.setName(AonStringUtils.left(conf.getCompany().getName(), FS_MOD349.NAME.getDataType().length()));
		mod349.setContactPhone(AonStringUtils.left(conf.fiscal().getContactPhone(), FS_MOD349.CONTACT_PHONE.getDataType().length()));
		mod349.setContactPerson(AonStringUtils.left(conf.fiscal().getContactPerson(), FS_MOD349.CONTACT_PERSON.getDataType().length()));
		mod349.setContactMail(AonStringUtils.left(conf.fiscal().getContactMail(), FS_MOD349.CONTACT_MAIL.getDataType().length()));		
		mod349.setStatus(FiscalStatus.PENDING);
	    mod349.setDiffEnabled(!conf.fiscal().isMod303ByDifferenceDisabled());				
		mod349.setDetails(new LinkedList<Mod349Detail>());
		return mod349;
	}
	
	private static class Mod349Filler implements Function<Record, Mod349> {

		@Override
		public Mod349 apply(Record record) {
			return new Mod349() 
				.setId(record.getValue(FS_MOD349.ID))
				.setDomain(record.getValue(FS_MOD349.DOMAIN))
				.setDomainName(record.getValue(DOMAIN.DESCRIPTION))
				.setYear(record.getValue(FS_MOD349.YEAR))
				.setPeriod(Period.safeValueOf(record.getValue(FS_MOD349.PERIOD)))
				.setAdministration(Administration.safeValueOf(record.getValue(FS_MOD349.ADMINISTRATION)))
				.setComments(record.getValue(FS_MOD349.COMMENTS))				
				.setStatus(com.esferalia.aon.watson.util.AonEnumUtils.enumValue(FiscalStatus.class,record.getValue(FS_MOD349.STATUS)))
				.setConfidential(AonEnumUtils.getBoolean(record.getValue(FS_MOD349.SECURITY_LEVEL)))				
				.setComplementary( record.getValue(FS_MOD349.COMPLEMENTARY)==1 )
				.setReplacement( record.getValue(FS_MOD349.REPLACEMENT)==1 )				
				.setNumber(record.getValue(FS_MOD349.NUMBER))
				.setReplacedNumber(record.getValue(FS_MOD349.REPLACED_NUMBER))
				.setDocument(record.getValue(FS_MOD349.DOCUMENT))
				.setRepresentativeDocument(record.getValue(FS_MOD349.REPRESENTATIVE_DOCUMENT))
				.setName(record.getValue(FS_MOD349.NAME))
				.setContactPerson(record.getValue(FS_MOD349.CONTACT_PERSON))
				.setContactPhone(record.getValue(FS_MOD349.CONTACT_PHONE))
				.setContactMail(record.getValue(FS_MOD349.CONTACT_MAIL))
			    .setPeriodicityChange(AonEnumUtils.getBoolean(record.getValue(FS_MOD349.PERIODICITY_CHANGE)))
			    .setDiffEnabled(AonEnumUtils.getBoolean(record.getValue(FS_MOD349.DIFF_ENABLED)))				
				.setCreationUser(record.getValue(FS_MOD349.CREATION_USER))
				.setCreationDate(record.getValue(FS_MOD349.CREATION_DATE))
				.setModificationUser(record.getValue(FS_MOD349.MODIFICATION_USER))
				.setModificationDate(record.getValue(FS_MOD349.MODIFICATION_DATE))
				.setFsModel(record.getValue(FS_MOD349.FS_MODEL))
				;
		}
	}

	private static class Mod349DetailFiller implements Function<Record, Mod349Detail> {
		
		@Override
		public Mod349Detail apply(Record record) {
			return new Mod349Detail()
				.setId(record.getValue(FS_MOD349_DETAIL.ID))
				.setRectification(AonEnumUtils.getBoolean(record.getValue(FS_MOD349_DETAIL.RECTIFICATION)))
				.setType(Mod349Key.safeValueOf(record.getValue(FS_MOD349_DETAIL.TYPE)))
				.setDocument(record.getValue(FS_MOD349_DETAIL.DOCUMENT))
				.setRegistry(record.getValue(FS_MOD349_DETAIL.REGISTRY))				
				.setName(record.getValue(FS_MOD349_DETAIL.NAME))				
				.setCountry(Country.safeValueOf(record.getValue(FS_MOD349_DETAIL.COUNTRY)))
				.setAccumulated(record.getValue(FS_MOD349_DETAIL.ACCUMULATED))
				.setDeclared(record.getValue(FS_MOD349_DETAIL.DECLARED))
				.setAmount(record.getValue(FS_MOD349_DETAIL.AMOUNT))
				.setRectifiedYear(record.getValue(FS_MOD349_DETAIL.RECTIFIED_YEAR))
				.setRectifiedPeriod(Period.safeValueOf(record.getValue(FS_MOD349_DETAIL.RECTIFIED_PERIOD) ) )
				.setRectifiedAmount(record.getValue(FS_MOD349_DETAIL.RECTIFIED_AMOUNT));
		}
	}
	
	public static Mod349 changeStatusMod349(AONContext ctx, Mod349 mod349, FiscalStatus newStatus) {
		try {
			ctx.checkWrite();
			if (mod349.getId() != null) {
				mod349.setStatus(newStatus);
				ctx.getDslContext().update(FS_MOD349)
					.set(FS_MOD349.STATUS,AonEnumUtils.getByte( mod349.getStatus()))
					.where(FS_MOD349.ID.equal(mod349.getId()))
					.execute();
			}
			return mod349;
		} catch (DataAccessException t) {
			throw new AonCoreException(t.getCause()!=null?t.getCause().getMessage():t.getMessage());
		} catch (Throwable t) {
			throw new AonCoreException(t.getMessage());
		}
	}
	
	public static Mod349 duplicate(AONContext ctx, Mod349 mod349) {
		
		int id = mod349.getId();
		mod349.setId(null);
		mod349.setDiffEnabled(false);
		mod349.setStatus(FiscalStatus.PENDING);
		mod349.setNumber(null);
		mod349 = insert(ctx, mod349, false);
		
		if (!mod349.isComplementary()) {
			Mod349 original = getById(ctx, id);
			for (Mod349Detail detail : original.getDetails()) {
				detail.setId(null);
				detail.setMod349(mod349.getId());
				detail.setDeclared(0.0);
				detail.setAccumulated(0.0);				
				mod349.getDetails().add(detail);
			}
		}
		return save(ctx, mod349);		
	
	}
	
	public static Mod349 reset(AONContext ctx, Mod349 mod349) {
		deleteDetails(ctx, mod349);		
		insertDetailsFromInvoice(ctx, mod349);
		return getById(ctx, mod349.getId()) ;
	}
	
	// --------------- INSERT DETAILS FROM INVOICE ---------------
	
	private static void insertDetailsFromInvoice(AONContext ctx, final Mod349 mod349) {
		
		// Lo que se hacia antes en el modelo viejo (esta en Mod349Manager):
		// - Se leen facturas intracomunitarias (transaccion=1) desde el inicio del ejercicio hasta el final del periodo
		// - Por pantalla se piden si se desea:
		// 		- Usar la fecha de IVA en lugar de la fecha de emision de la factura
		//		- Agrupar solo por Clave+NIF en vez de Clave+idregistry+NIF
		// - La suma de la base de todas las facturas, es el importe acumulado
		// - El importe declarado anteriormente se obtiene leyendo lo declarado en mod349detail de periodos anteriores
		// - El importe (amount) es la diferencia (acumulado-declarado), que es lo que se declara en este periodo
		
		// Lo que se hace ahora:
		// - Se coge como referencia la fecha de IVA de la factura, que es lo mismo que se hace en el 303
		// - Se agrupa siempre por Clave + Pais + NIF, teniendo en cuenta facturas rectificadas que tambien van aparte
		// - Si se ha indicado la creacion del modelo por diferencias:
		// 		- Se lee todo el año, y se le resta lo declarado anteriormente (igual que antes)
		// 		- El importe declarado anteriormente se obtiene acumulando declarado - rectificado, de los periodos anteriores del ejercicio
		
		// Mapa que guarda los nombres de cada NIF
		Map<String, String> mapNames = new TreeMap<String, String>();
		
		// Facturas vinculadas que se guardarán en alcatraz
		Set<Alcatraz> invoices = new HashSet<>();
		
		// Obtenemos el desglose de facturas intracomunitarias del periodo o acumulado anual
		getVatBreakdown(ctx, mod349)
		
		// Ordenamos por fecha de factura y guardamos todos los nombres de las facturas en un map, por pais+nif. Así 
		// si algunas facturas tienen el mismo nif, pero distinto nombre, se agrupará mas tarde por nif y el nombre
		// se cogerá el de la última factura
		.sorted(Comparator.comparing(VatContext::getIssueDate))
		
		.peek(vat -> {
			mapNames.put(vat.getRegistryDocumentCountry() + vat.getRegistryDocument(), vat.getRegistryName());			
			
			// De igual forma guardamos las facturas que se incluyen en el modelo para luego grabarlas en alcatraz
			// Se empieza a usar Alcatraz a partir del 2023, en marzo aproximadamente, por lo tanto cuando se genere 
			// el primer modelo 349 por diferencias del 2023, con alcatraz, se vincularán todas las facturas desde el 
			// inicio del ejercicio
			// Solo se graba la factura en alcatraz, si no está ya vinculada a otro periodo del modelo 349			
			if (mod349.getYear() >= 2023 && !isInvoiceDeclared(ctx, vat.getInvoice()))			
				invoices.add( new Alcatraz().setInvoice(vat.getInvoice()) );				
		})
		
		// Agrupamos por tipo factura + esServicio + pais + documento + año_rectificado + periodo_rectificado (acumulando la base)		
		.collect(Collectors.groupingBy(VatContext::getInvoiceType,
				   Collectors.groupingBy(VatContext::isService,
				     Collectors.groupingBy(VatContext::getRegistryDocumentCountry,
					   Collectors.groupingBy(VatContext::getRegistryDocument,
					     Collectors.groupingBy(VatContext::getRectificateYear,
						   Collectors.groupingBy(VatContext::getRectificatePeriod, Collectors.summingDouble(VatContext::getBase) )))))))
		
		// Grabamos los datos en la tabla de lineas del modelo 349
		.forEach( (invoiceType,b) -> {
			
			for (Boolean isService : b.keySet())
			 for (Country country : b.get(isService).keySet())
			  for (String document : b.get(isService).get(country).keySet())
			   for (int year : b.get(isService).get(country).get(document).keySet())
   			    for (Period period : b.get(isService).get(country).get(document).get(year).keySet()) {
				   
				   Mod349Key keyOperation = getMod349Key(invoiceType, isService);				   
				  
				   // Acumulado
				   double accumulated = b.get(isService).get(country).get(document).get(year).get(period).doubleValue();
				   
				   String name = mapNames.get(country+document);
				  
				   // Obtener total declarado en el ejercicio si el calculo es por diferencias y no es rectificacion
				   double declared = 0.0;				   
				   if (year == 0 && mod349.isDiffEnabled()) {
					   declared = getDeclaredModels(ctx, mod349, keyOperation, country, document)
							   .mapToDouble(p -> p.getAmount() - p.getRectifiedAmount())
							   .sum();
				   }
				   
				   // Diferencia
				   double amount = accumulated - declared; 
				  
				   // Añadir el registro, si el importe a declarar es distinto de cero
				   if (amount != 0 || year != 0) {
					  Mod349Detail det = new Mod349Detail()
			     			   .setDomain(mod349.getDomain())
			     			   .setMod349(mod349.getId())
			     			   .setType(keyOperation)
			     			   .setCountry(country)				
			     			   .setDocument(document)
			     			   .setName(name)
			     			   .setAccumulated(accumulated)
			     			   .setDeclared(declared)
			     			   .setAmount(amount);
					  
					  // Rectificacion: Obtener lo declarado anteriormente y añadir los datos a la linea del 349
					  if (year != 0) {
						  double declaredAmount = getOldDeclaredAmount(ctx, mod349, year, period, keyOperation, country, document);
						  det.setRectification(true);
						  det.setRectifiedYear(year);
						  det.setRectifiedPeriod(period);
						  det.setRectifiedAmount(declaredAmount);
						  det.setAmount(declaredAmount+amount);						  
					  }
					   
					  // Grabar la linea del 349
					  insertDetail(ctx,det);
				   }	
			   }
		});
		
		// Grabar en alcatraz las facturas vinculadas al modelo que se está generando
		AlcatrazDAO.deleteFiscalModel(ctx, getFiscalModel(mod349));
		AlcatrazDAO.saveModelInvoices(ctx, getFiscalModel(mod349), invoices); 
			
	}
	
	// Devuelve cierto si la factura está vinculada en Alcatraz en algún modelo 349
	private static boolean isInvoiceDeclared(AONContext ctx, Integer invoiceId) {
		return ctx.getDslContext()
			.select()
			.from(ALCATRAZ)
			.leftOuterJoin(FS_MODEL).on(FS_MODEL.ID.equal(ALCATRAZ.FS_MODEL))			
			.where(ALCATRAZ.INVOICE.eq(invoiceId)).and(FS_MODEL.MODEL.eq("349"))			
			.limit(1)
			.fetch()
			.stream()
			.findAny()
			.isPresent();		
	}
	
	// Devuelve cierto si el modelo tiene facturas vinculadas en Alcatraz
	private static boolean hasAlcatrazInvoices(AONContext ctx, Integer fsModel) {
		return ctx.getDslContext()
			.select()
			.from(ALCATRAZ)
			.leftOuterJoin(FS_MODEL).on(FS_MODEL.ID.equal(ALCATRAZ.FS_MODEL))			
			.where(ALCATRAZ.FS_MODEL.eq(fsModel)).and(FS_MODEL.MODEL.eq("349"))			
			.limit(1)
			.fetch()
			.stream()
			.findAny()
			.isPresent();		
	}
	
	// Devuelve cierto si la factura está vinculada en alcatraz al modelo 349 que se le pasa
	private static boolean isInvoiceDeclared(AONContext ctx, Integer invoiceId, Integer fsModel) {
		return ctx.getDslContext()
			.select()
			.from(ALCATRAZ)
			.leftOuterJoin(FS_MODEL).on(FS_MODEL.ID.equal(ALCATRAZ.FS_MODEL))			
			.where(ALCATRAZ.INVOICE.eq(invoiceId)).and(ALCATRAZ.FS_MODEL.eq(fsModel)).and(FS_MODEL.MODEL.eq("349"))			
			.limit(1)
			.fetch()
			.stream()
			.findAny()
			.isPresent();		
	}
	
	// Devuelve cierto si la factura está vinculada en alcatraz a algún modelo 349 de periodo menor o igual al que se le pasa 
	public static boolean isInvoiceDeclared(AONContext ctx, Integer invoiceId, byte period) {
		return ctx.getDslContext()
			.select()
			.from(ALCATRAZ)
			.leftOuterJoin(FS_MODEL).on(FS_MODEL.ID.equal(ALCATRAZ.FS_MODEL))
			.where(ALCATRAZ.INVOICE.eq(invoiceId)).and(FS_MODEL.PERIOD.lessOrEqual(period)).and(FS_MODEL.MODEL.eq("349"))
			.limit(1)
			.fetch()
			.stream()
			.findAny()
			.isPresent();		
	}
	
	private static Stream<VatContext> getVatBreakdown(final AONContext ctx, final Mod349 mod349) {
		return getVatBreakdown(ctx, mod349, mod349.isDiffEnabled());		
	}

	// Obtiene el desglose de las facturas intracomunitarias, para el periodo del modelo, segun sea por diferencias o no
	private static Stream<VatContext> getVatBreakdown(final AONContext ctx, final Mod349 mod349, final boolean isDiffEnabled) {
		
		Date fromDate = isDiffEnabled ? AonDateUtils.getYearFirstDay(mod349.getYear()) : FiscalUtils.getPeriodStart(mod349);
		Date toDate = FiscalUtils.getPeriodEnd(mod349);
		
		// Obtenemos el desglose de las facturas intracomunitarias entre las fechas indicadas
		return getVatBreakdown(ctx, fromDate, toDate)
				.peek(vat -> { 
					
					vat.setInsidePeriod(mod349==null ? false : FiscalUtils.isInPeriodRange(mod349, vat.getTaxDate()));
					
					// NIF Operador Intracomunitario debe estar cumplimentado y de longitud menor de 15
					if (vat.getRegistryDocumentCountry() == null || AonStringUtils.isBlank(vat.getRegistryDocument()) || vat.getRegistryDocument().length() > 15)
						throw new AonCoreException("Campos Pais o NIF vacios o longitud NIF mayor de 15. ["+vat.getRegistryDocument()+" - "+vat.getRegistryName()+" - Factura "+vat.getDocumentNumber()+"]");

					// Valores por defecto para estos campos, para las facturas no rectificativas, para que luego se agrupen todas juntas
					vat.setRectificateYear(0);
					vat.setRectificatePeriod(Period.M01);	
					
					// Comprobar si el nombre está cumplimentado y no supera los 64 caracteres
					if (AonStringUtils.isBlank(vat.getRegistryName()))
						vat.setRegistryName("");
					
					if (vat.getRegistryName().length() > 64) 
						vat.setRegistryName(AonStringUtils.left(vat.getRegistryName(), 64));
					
					// Si es una factura rectificativa, hay que obtener el periodo de la factura rectificada
					// para poder guardarlo posteriormente como rectificacion en el modelo 349
					int rectificateYear = 0;
					Period rectificatePeriod = null;
					if (vat.isInsidePeriod() && vat.getRectificationType() != null && (vat.getRectificationType() == RectificationType.NORMAL_RECTIFIER || vat.getRectificationType() == RectificationType.SPECIAL_RECTIFIER) && vat.getRectificateInvoiceTaxDate() != null) {
						// Buscar posible periodo donde se declaro la factura rectificada (mensual, trimestral), segun su fecha de IVA, si es distinto del que se esta declarando, si el periodo es el mismo no se crea linea para la rectificacion en el modelo 349
						rectificateYear = AonDateUtils.getYear(vat.getRectificateInvoiceTaxDate());
						// Primero probamos con posible periodo mensual
						rectificatePeriod = Period.getMonthlyPeriod(AonDateUtils.getMonth(vat.getRectificateInvoiceTaxDate()));
						
						Double oldDeclaredAmount = getOldDeclaredAmount(ctx, mod349, rectificateYear, rectificatePeriod, getMod349Key(vat.getInvoiceType(), vat.isService()), vat.getRegistryDocumentCountry(), vat.getRegistryDocument());						
						if (oldDeclaredAmount == null) {
							// Si no se encuentra periodo mensual, probamos con posible periodo trimestral
							rectificatePeriod = Period.getQuarterlyPeriod(AonDateUtils.getMonth(vat.getRectificateInvoiceTaxDate()));
							oldDeclaredAmount = getOldDeclaredAmount(ctx, mod349, rectificateYear, rectificatePeriod, getMod349Key(vat.getInvoiceType(), vat.isService()), vat.getRegistryDocumentCountry(), vat.getRegistryDocument());								
						}
						
						// Se ha encontrado una declaracion en el periodo de la factura rectificada, se guarda para crear posteriormente la linea de rectificacion en el modelo 349
						if (oldDeclaredAmount != null && (rectificateYear != mod349.getYear() || rectificatePeriod != mod349.getPeriod())) {
							vat.setRectificateYear(rectificateYear);
							vat.setRectificatePeriod(rectificatePeriod);							
						}
					}
					
				});
		
	}
	
	private static Stream<VatContext> getVatBreakdown(AONContext ctx, Date fromDate, Date toDate) {
		
		Invoice rectificationInvoice = INVOICE.as("rectificationInvoice"); // Enlace factura rectificada/rectificativa
		
		java.sql.Date firstDay = AonDateUtils.toSql( fromDate );
		java.sql.Date lastDay = AonDateUtils.toSql( toDate);
		
		// Leemos la base imponible de invoice_tax, porque no nos podemos fiar del importe que 
		// tiene el campo taxable_base de invoice, pues no es la base imponible exactamente 
		// sino que lleva tambien los suplidos por ejemplo
		Field<BigDecimal> sumBase = DSL.sum(INVOICE_TAX.BASE);
		
		return ctx.getDslContext().select(
				 INVOICE.ID
				,INVOICE.SERIES
				,INVOICE.NUMBER
				,INVOICE.REFERENCE_CODE
				,INVOICE.RDOCUMENT
				,INVOICE.RDOCUMENT_TYPE
				,INVOICE.RDOCUMENT_COUNTRY
				,INVOICE.RNAME
				,INVOICE.ISSUE_DATE
				,INVOICE.TAX_DATE
				,INVOICE.TYPE
				,INVOICE.RECTIFICATION_TYPE
				,INVOICE.SERVICE
				,INVOICE.TRANSACTION
				,INVOICE.INVESTMENT
				,INVOICE.WITHHOLDING_FARMER
				,INVOICE.VAT_ACCRUAL_PAYMENT
				,ENTERPRISE_ACTIVITY.ID
				,ENTERPRISE_ACTIVITY.DESCRIPTION
				,ENTERPRISE_ACTIVITY.VAT_REGIME
				,ENTERPRISE_ACTIVITY.SURCHARGE				
				,IAE.EPIGRAPH
				,sumBase
				,rectificationInvoice.TAX_DATE
				)
				.from(INVOICE_TAX)
				.join(INVOICE_DETAIL).on(INVOICE_TAX.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID))
				.join(INVOICE).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
				.leftOuterJoin(ENTERPRISE_ACTIVITY).on(ENTERPRISE_ACTIVITY.ID.equal(INVOICE.ACTIVITY))
				.leftOuterJoin(IAE).on(IAE.ID.equal(ENTERPRISE_ACTIVITY.IAE))
				.leftOuterJoin(rectificationInvoice).on(rectificationInvoice.ID.equal(INVOICE.RECTIFICATION_INVOICE))
				.where(INVOICE.DOMAIN.equal(ctx.getDomainId()))
				.and(INVOICE.TAX_DATE.between(AonDateUtils.toSql(firstDay),AonDateUtils.toSql(lastDay)))
				.and(INVOICE.TRANSACTION.eq(InvoiceTransactionType.INTRACOMMUNITY.value()))
				.and(INVOICE_TAX.TAX_TYPE.equal((byte) 1))
				.groupBy(INVOICE.ID)
				.orderBy(InvoiceOLDDAO.getOrderedType(),INVOICE.SERIES,INVOICE.NUMBER )				
				.fetch()
				.stream()
				.map( rec -> {
					return new VatContext()
							.setInvoice(rec.getValue(INVOICE.ID))
							.setActivity(rec.getValue(ENTERPRISE_ACTIVITY.ID))
							.setActivityDescription(rec.getValue(ENTERPRISE_ACTIVITY.DESCRIPTION))
							.setEpigraph(rec.getValue(IAE.EPIGRAPH))
							.setVatRegime( VATRegime.safeValueOf( rec.getValue(ENTERPRISE_ACTIVITY.VAT_REGIME) ))
							.setVatSurchargeRegime( AonEnumUtils.getBoolean(rec.getValue(ENTERPRISE_ACTIVITY.SURCHARGE) ) )		
							.setDocumentNumber(FinanceUtil.getDocumentNumber(InvoiceType.safeValueOf(rec.getValue(INVOICE.TYPE)), rec.getValue(INVOICE.SERIES), rec.getValue(INVOICE.NUMBER))) 
							.setReferenceCode(rec.getValue(INVOICE.REFERENCE_CODE))
							.setRegistryDocument(rec.getValue(INVOICE.RDOCUMENT))
							.setRegistryDocumentType(DocumentType.safeValueOf(rec.getValue(INVOICE.RDOCUMENT_TYPE)))
							.setRegistryDocumentCountry(Country.safeValueOf(rec.getValue(INVOICE.RDOCUMENT_COUNTRY)))
							.setRegistryName(rec.getValue(INVOICE.RNAME))	
							.setIssueDate(rec.getValue(INVOICE.ISSUE_DATE))
							.setTaxDate(rec.getValue(INVOICE.TAX_DATE))
							.setInvoiceType(InvoiceType.safeValueOf(rec.getValue(INVOICE.TYPE)))
							.setRectificationType(RectificationType.safeValueOf(rec.getValue(INVOICE.RECTIFICATION_TYPE)))
							.setService(rec.getValue(INVOICE.SERVICE) == 1 || InvoiceType.safeValueOf(rec.getValue(INVOICE.TYPE)) == InvoiceType.EXPENSES)
							.setTransaction(InvoiceTransactionType.safeValueOf(rec.getValue(INVOICE.TRANSACTION)))
							.setInvestment(rec.getValue(INVOICE.INVESTMENT) == 1)
							.setVatAccrualRegime(rec.getValue(INVOICE.VAT_ACCRUAL_PAYMENT) == 1)
							.setFarmerRegime(rec.getValue(INVOICE.WITHHOLDING_FARMER) == 1)							
							.setBase(rec.getValue(sumBase).doubleValue())
							.setRectificateInvoiceTaxDate(rec.getValue(rectificationInvoice.TAX_DATE))
						; 
				})
				;
	}
	
	private static Stream<Mod349DetailInfo> getDeclaredModels(AONContext ctx, final Mod349 mod349, final Mod349Key key, final Country country, final String document) {
		
		// Leer primero los registros que no llevan rectificaciones
		Stream<Mod349DetailInfo> s1 = ctx.getDslContext()
				.select(FS_MOD349.PERIOD, FS_MOD349.COMPLEMENTARY, FS_MOD349.REPLACEMENT, FS_MOD349_DETAIL.AMOUNT, FS_MOD349_DETAIL.RECTIFIED_AMOUNT, FS_MOD349_DETAIL.RECTIFICATION, FS_MOD349_DETAIL.RECTIFIED_PERIOD )
				.from(FS_MOD349_DETAIL)
				.join(FS_MOD349).on(FS_MOD349_DETAIL.FS_MOD349.equal(FS_MOD349.ID))
				.where(FS_MOD349.DOMAIN.equal(mod349.getDomain()))
				.and(FS_MOD349.YEAR.equal(mod349.getYear()))
				.and(FS_MOD349_DETAIL.RECTIFIED_YEAR.isNull())
				.and(getDueMonth().lessThan((byte) mod349.getPeriod().getDueMonth()))  // Se compara con el mes hasta del periodo, por que pueden coincidir varias periodicidades en el mismo año (por ejemplo de trimestral a mensual o viceversa)
				.and(FS_MOD349_DETAIL.TYPE.equal(key.getValue()))
				.and(FS_MOD349_DETAIL.COUNTRY.equal(country.getIso2()))
				.and(FS_MOD349_DETAIL.DOCUMENT.equal(document))
				.fetch()
				.stream()
				.map( rec -> {
					// Solo se asignan los campos que luego se muestran en la informacion
					return new Mod349DetailInfo()
							.setPeriod(Period.safeValueOf(rec.getValue(FS_MOD349.PERIOD)))
							.setComplementary(AonEnumUtils.getBoolean(rec.getValue(FS_MOD349.COMPLEMENTARY)))
							.setReplacement(AonEnumUtils.getBoolean(rec.getValue(FS_MOD349.REPLACEMENT)))							
							.setAmount(rec.getValue(FS_MOD349_DETAIL.AMOUNT))
							.setRectification(AonEnumUtils.getBoolean(rec.getValue(FS_MOD349_DETAIL.RECTIFICATION)))
							.setRectifiedPeriod(Period.safeValueOf(rec.getValue(FS_MOD349_DETAIL.RECTIFIED_PERIOD)))
							.setRectifiedAmount(rec.getValue(FS_MOD349_DETAIL.RECTIFIED_AMOUNT))
							.setPeriod(Period.safeValueOf(rec.getValue(FS_MOD349.PERIOD)));
				});
				
		// Leer ahora los registros que llevan rectificaciones
		Stream<Mod349DetailInfo> s2 = ctx.getDslContext()
				.select(FS_MOD349.PERIOD, FS_MOD349.COMPLEMENTARY, FS_MOD349.REPLACEMENT, FS_MOD349_DETAIL.AMOUNT, FS_MOD349_DETAIL.RECTIFIED_AMOUNT, FS_MOD349_DETAIL.RECTIFICATION, FS_MOD349_DETAIL.RECTIFIED_PERIOD )
				.from(FS_MOD349_DETAIL)
				.join(FS_MOD349).on(FS_MOD349_DETAIL.FS_MOD349.equal(FS_MOD349.ID))
				.where(FS_MOD349.DOMAIN.equal(mod349.getDomain()))
				.and(FS_MOD349_DETAIL.RECTIFIED_YEAR.equal(mod349.getYear()))
				.and(getDueMonth().lessThan((byte) mod349.getPeriod().getDueMonth()))
				.and(getDueMonthDetail().lessThan((byte) mod349.getPeriod().getDueMonth()))   
				.and(FS_MOD349_DETAIL.TYPE.equal(key.getValue()))
				.and(FS_MOD349_DETAIL.COUNTRY.equal(country.getIso2()))
				.and(FS_MOD349_DETAIL.DOCUMENT.equal(document))
				.fetch()
				.stream()
				.map( rec -> {
					// Solo se asignan los campos que luego se muestran en la informacion
					return new Mod349DetailInfo()
							.setPeriod(Period.safeValueOf(rec.getValue(FS_MOD349.PERIOD)))
							.setComplementary(AonEnumUtils.getBoolean(rec.getValue(FS_MOD349.COMPLEMENTARY)))
							.setReplacement(AonEnumUtils.getBoolean(rec.getValue(FS_MOD349.REPLACEMENT)))							
							.setAmount(rec.getValue(FS_MOD349_DETAIL.AMOUNT))
							.setRectification(AonEnumUtils.getBoolean(rec.getValue(FS_MOD349_DETAIL.RECTIFICATION)))
							.setRectifiedPeriod(Period.safeValueOf(rec.getValue(FS_MOD349_DETAIL.RECTIFIED_PERIOD)))
							.setRectifiedAmount(rec.getValue(FS_MOD349_DETAIL.RECTIFIED_AMOUNT))
							.setPeriod(Period.safeValueOf(rec.getValue(FS_MOD349.PERIOD)));
				});
		
		return Stream.concat(s1, s2);				
	}	
	
	private static Double getOldDeclaredAmount(AONContext ctx, Mod349 mod349, int oldYear, Period oldPeriod, Mod349Key key, Country country, String document) {		
		
		// Buscar primero alguna posible linea rectificada de ese periodo
		Double amount = ctx.getDslContext()
							.select(FS_MOD349_DETAIL.AMOUNT)
							.from(FS_MOD349_DETAIL)
							.join(FS_MOD349).on(FS_MOD349_DETAIL.FS_MOD349.equal(FS_MOD349.ID))
							.where(FS_MOD349.DOMAIN.equal(mod349.getDomain()))
							.and(FS_MOD349.YEAR.lessThan(mod349.getYear()).or(FS_MOD349.YEAR.eq(mod349.getYear()).and(getDueMonth().lessThan((byte) mod349.getPeriod().getDueMonth()))))
							.and(FS_MOD349_DETAIL.RECTIFIED_YEAR.equal(oldYear))
							.and(FS_MOD349_DETAIL.RECTIFIED_PERIOD.eq((byte) oldPeriod.value())) 
							.and(FS_MOD349_DETAIL.TYPE.equal(key.getValue()))
							.and(FS_MOD349_DETAIL.COUNTRY.equal(country.getIso2()))
							.and(FS_MOD349_DETAIL.DOCUMENT.equal(document))							
							.orderBy(FS_MOD349.ID.desc())
							.fetchAny(FS_MOD349_DETAIL.AMOUNT);
		
		// Si no se encuentran rectificaciones, se buscan declaraciones
		if (amount == null) {
			amount = ctx.getDslContext()
						.select(FS_MOD349_DETAIL.AMOUNT)
						.from(FS_MOD349_DETAIL)
						.join(FS_MOD349).on(FS_MOD349_DETAIL.FS_MOD349.equal(FS_MOD349.ID))
						.where(FS_MOD349.DOMAIN.equal(mod349.getDomain()))						
						.and(FS_MOD349.YEAR.equal(oldYear))
						.and(FS_MOD349.PERIOD.eq((byte) oldPeriod.value())) 
						.and(FS_MOD349_DETAIL.TYPE.equal(key.getValue()))
						.and(FS_MOD349_DETAIL.COUNTRY.equal(country.getIso2()))
						.and(FS_MOD349_DETAIL.DOCUMENT.equal(document))
						.and(FS_MOD349_DETAIL.RECTIFIED_YEAR.isNull())
						.fetchAny(FS_MOD349_DETAIL.AMOUNT);
		}
		
		return amount;
	}
	
	private static Mod349Key getMod349Key(InvoiceType invoiceType, boolean isService) {
		   if (invoiceType == InvoiceType.PURCHASE) {
			   if (isService)
				   return Mod349Key.I; // Compras (Adquisiciones intracomunitarias de servicios)
			   else return Mod349Key.A; // Compras (Adquisiciones intracomunitarias de bienes) 
		   }
		   else if (invoiceType == InvoiceType.SALES) {
				if (isService)
					return Mod349Key.S; // Ventas (Prestaciones Intracomunitarias de Servicios)
				else return Mod349Key.E; // Ventas (Entregas intracomunitarias de bienes)
		   }				
		   else return Mod349Key.I;  // Gastos (Adquisiciones intracomunitarias de servicios)
	}
	
	
	// --------------- INVOICES INFO --------------- 
	
	public static String getMod349Info(AONContext ctx, Mod349 mod349, Mod349Detail detail, FiscalModelKeyInfo infoKey) {
		
		String INFO_MSG = "<pre class='aon-fixed-font aon-font-medium aon-margin-bottom'>{0}<pre>";
		
		// Informacion Desglose de facturas
		if (infoKey == FiscalModelKeyInfo.INVOICE) {
			return MessageFormat.format(INFO_MSG, getInvoicesInfo(ctx, mod349, detail));			
		}
		
		// Informacion Desglose del calculo por diferencias
		if (infoKey == FiscalModelKeyInfo.DIFF_INVOICE) {
			return MessageFormat.format(INFO_MSG, getDiffInvoicesInfo(ctx, mod349, detail));
		}
		
		return null;
	}
	
	private static String getInvoicesInfo(AONContext ctx, Mod349 mod349, Mod349Detail detail) {
		
		String title = "FACTURAS QUE AFECTAN A LA CONFECCI\u00D3N DEL MODELO " 
				+ FiscalModelUtils.getModelName(mod349) 
				+ " DEL " + mod349.getPeriod().getDescription()
				+ " DE " + mod349.getYear();
		
		// Se comprueba si el modelo tiene facturas vinculadas en Alcatraz, para mostrar la información en base a esas facturas vinculadas
		// si no tiene facturas vinculadas en Alcatraz, se asume que es un modelo anterior a la puesta en marcha de Alcatraz en este modelo
		// y por lo tanto la información saldrá como salía antes
		boolean hasAlcatraz = hasAlcatrazInvoices(ctx, mod349.getFsModel());
		
		return VATFormatter.formatInvoices(title
				,getSubtitle(detail)
				//,getVatBreakdownInfo(ctx, mod349, detail, false)
				,getVatBreakdownInfo(ctx, mod349, detail, hasAlcatraz )  // Si el modelo tiene facturas vinculadas en Alcatraz, entonces se lee todo el año  
				 .filter( p -> hasAlcatraz ? isInvoiceDeclared(ctx, p.getInvoice(), mod349.getFsModel()) : true) 
				 .collect(Collectors.toCollection(LinkedList::new)));
		
	}
	
	private static String getDiffInvoicesInfo(AONContext ctx, final Mod349 mod349, final Mod349Detail detail) {

		String title = "DETALLE DEL C\u00C1LCULO POR DIFERENCIA DEL MODELO "
				+ FiscalModelUtils.getModelName(mod349) 
				+ " DEL " + mod349.getPeriod().getDescription()
				+ " DE " + mod349.getYear();
		
		// Se comprueba si el modelo tiene facturas vinculadas en Alcatraz, para mostrar la información en base a esas facturas vinculadas
		boolean hasAlcatraz = hasAlcatrazInvoices(ctx, mod349.getFsModel());
		
		return Mod349Formatter.formatDiffInvoicesMod349(title
				,getSubtitle(detail)
				,mod349.getPeriod()			
				,getDeclaredModels(ctx, mod349, detail.getType(), detail.getCountry(), detail.getDocument()).collect(Collectors.toCollection(LinkedList::new))			 	
				,getVatBreakdownInfo(ctx, mod349, detail, true)
				 .filter( p -> hasAlcatraz ? (!p.isInsidePeriod() && isInvoiceDeclared(ctx, p.getInvoice(), mod349.getPeriod().value()) ) || // Si la factura no es del periodo debe estar vinculada en algun modelo 349 del mismo periodo o anterior
					                         (p.isInsidePeriod() && isInvoiceDeclared(ctx, p.getInvoice(), mod349.getFsModel())) // Si la factura es del periodo, debe estar vinculada al modelo del que se saca la info
					                       : true)
				 .collect(Collectors.toCollection(LinkedList::new)));
		
	}
	
	private static String getSubtitle(final Mod349Detail detail ) {
		return "Clave "+detail.getType().getValue()+
				" - " + detail.getCountry().getIso2() + " " + detail.getDocument() +
				" - " + detail.getName();
	}
	
	private static Stream<VatContext> getVatBreakdownInfo(final AONContext ctx, final Mod349 mod349, final Mod349Detail detail, final boolean isDiffEnabled) {
		
		// Facturas a localizar segun la clave de la linea del modelo que se le pasa (se hace la operacion inversa que cuando se crea el modelo)
		final InvoiceType invoiceType1;
		final InvoiceType invoiceType2;
		final InvoiceType invoiceType3;
		final boolean isService;
		
		if (detail.getType() == Mod349Key.A) {       // Compras (Adquisiciones intracomunitarias de bienes)
			invoiceType1 = InvoiceType.PURCHASE;
			invoiceType2 = null;
			invoiceType3 = null;
			isService = false;
	    }
	    else if (detail.getType() == Mod349Key.S) {  // Ventas de Servicios (Prestaciones Intracomunitarias de Servicios)
	    	invoiceType1 = InvoiceType.SALES;
	    	invoiceType2 = null;
	    	invoiceType3 = null;
	    	isService = true;
	    }
	    else if (detail.getType() == Mod349Key.E ) { // Ventas (Entregas intracomunitarias de bienes)
	    	invoiceType1 = InvoiceType.SALES;
	    	invoiceType2 = null;
	    	invoiceType3 = null;
	    	isService = false;
	    }				
	    else if (detail.getType() == Mod349Key.I) {  // Gastos y Compras de Servicios (Adquisiciones intracomunitarias de servicios)
	    	invoiceType1 = InvoiceType.EXPENSES;
	    	invoiceType2 = InvoiceType.UNDEDUCTIBLE;
	    	invoiceType3 = InvoiceType.PURCHASE;
	    	isService = true;
	    }
	    else {
	    	invoiceType1 = null;
	    	invoiceType2 = null;
	    	invoiceType3 = null;
	    	isService = false;
	    }
		
		boolean isRectification = detail.isRectification();
		
		return getVatBreakdown(ctx, mod349, isDiffEnabled)  
			   .filter( p -> (!p.isInsidePeriod() || p.isRectification() == isRectification || (!isRectification && p.getRectificateYear() == 0)) && (p.getInvoiceType() == invoiceType1 || p.getInvoiceType() == invoiceType2 || p.getInvoiceType() == invoiceType3) && p.isService() == isService && p.getRegistryDocumentCountry() == detail.getCountry() && AonStringUtils.equals(p.getRegistryDocument(), detail.getDocument()));
		
	}
	
    // Grabar resultado y pdf en response y marcar el modelo como enviado
	public static Mod349 aeatPresentation(AONContext ctx, Mod349 mod, String aeatResponse) {
		if (AonStringUtils.isNotBlank(aeatResponse)) {			
			
			// Antes de nada se borra la presentación anterior
			DataResponseDAO.deleteAEATResponse(ctx, mod);			
			
			// Grabar los datos en data_response y sus tablas asociadas
			DataResponseDAO.insertAEATResponse(ctx, mod, aeatResponse);
			
			// Marcar el modelo como enviado
			if (mod != null && mod.getId() != null) {
				ctx.getDslContext().update(FS_MOD349)					
					.set(FS_MOD349.STATUS, FiscalStatus.SENT.value())
					.where(FS_MOD349.ID.equal(mod.getId()))
					.execute();
				return getById(ctx, mod.getId());
			}
		}
		return mod;
	}
	
	// Mantenimiento de la fila en fs_model 
	
	private static Integer saveFsModel(AONContext ctx, Mod349 mod349) {

		// Comprobar si es necesario añadir o actualizar el registro en fs_model
		if (mod349.getFsModel() == null)
			return insertFsModel(ctx, mod349);
		else 
			return updateFsModel(ctx, mod349);
		
	}
	
	private static void deleteFsModel(AONContext ctx, Integer idFsModel) {
		
		if (idFsModel != null) {
			ctx.getDslContext()
				.delete(FS_MODEL)
				.where(FS_MODEL.ID.equal(idFsModel))
				.execute();
		}

	}
	
	private static Integer insertFsModel(AONContext ctx, Mod349 mod349) {
		
		Integer id = ctx.getDslContext()
			.insertInto(FS_MODEL)
				.set(FS_MODEL.DOMAIN, mod349.getDomain())
				.set(FS_MODEL.YEAR, mod349.getYear())
				.set(FS_MODEL.PERIOD, mod349.getPeriod().value())
				.set(FS_MODEL.ADMINISTRATION, mod349.getAdministration().value())
				.set(FS_MODEL.STATUS, AonEnumUtils.getByte(mod349.getStatus()))
				.set(FS_MODEL.SECURITY_LEVEL,AonEnumUtils.getByte( mod349.isConfidential() ))
				.set(FS_MODEL.COMPLEMENTARY, AonEnumUtils.getByte( mod349.isComplementary() ))
				.set(FS_MODEL.REPLACEMENT, AonEnumUtils.getByte( mod349.isReplacement() )) 
//				.set(FS_MODEL.WITHOUTACTIVITY,AonEnumUtils.getByte( mod349.isWithoutActivity()  ))
				.set(FS_MODEL.MODEL, mod349.getModel().getValue() )
				.set(FS_MODEL.NUMBER, mod349.getNumber())
				.set(FS_MODEL.REPLACED_NUMBER, mod349.getReplacedNumber())
				.set(FS_MODEL.COMMENTS, mod349.getComments())
				.set(FS_MODEL.FINANCE, mod349.getFinance() == null?null:mod349.getFinance().getId())
				.set(FS_MODEL.DOCUMENT, mod349.getDocument() )
				.set(FS_MODEL.SURNAME, mod349.getSurname())
				.set(FS_MODEL.NAME, mod349.getName())
//				.set(FS_MODEL.STREET_INITIAL,mod349.getStreetInitial())
//				.set(FS_MODEL.STREET_NAME,mod349.getStreetName())
//				.set(FS_MODEL.STREET_NUMBER,mod349.getStreetNumber())
//				.set(FS_MODEL.STREET_STAIR,mod349.getStreetStair())
//				.set(FS_MODEL.STREET_FLOOR,mod349.getStreetFloor())
//				.set(FS_MODEL.STREET_DOOR,mod349.getStreetDoor())
//				.set(FS_MODEL.PHONE,mod349.getPhone())
//				.set(FS_MODEL.TOWN,mod349.getTown())
//				.set(FS_MODEL.PROVINCE,mod349.getProvince())
//				.set(FS_MODEL.ZIP,mod349.getZip())
//				.set(FS_MODEL.ADMON_AEAT,mod349.getAdmonAeat())
				.set(FS_MODEL.CONTACT_PERSON,mod349.getContactPerson())
				.set(FS_MODEL.CONTACT_PHONE,mod349.getContactPhone())
//				.set(FS_MODEL.CONTACT_CELLULAR,mod349.getContactCellular())
				.set(FS_MODEL.CONTACT_EMAIL,mod349.getContactMail())
				.set(FS_MODEL.RESULT, mod349.getDeclarationResult())
				.set(FS_MODEL.DECLARATION_TYPE, AonEnumUtils.getByte( mod349.getDeclarationResultType() ) )
//				.set(FS_MODEL.ACCOUNT_ENTRY, mod349.getAccountEntry())
				.set(FS_MODEL.CREATION_USER, mod349.getCreationUser())
				.set(FS_MODEL.CREATION_DATE, AonDateUtils.toTimestamp(mod349.getCreationDate()))
				.set(FS_MODEL.MODIFICATION_USER, mod349.getModificationUser())
				.set(FS_MODEL.MODIFICATION_DATE, AonDateUtils.toTimestamp(mod349.getModificationDate()))				
			.returning(FS_MODEL.ID)
			.fetchOne()
			.getValue(FS_MODEL.ID);
		return id;
		
	}	
	
	private static Integer updateFsModel(AONContext ctx, Mod349 mod349) {
		
		ctx.getDslContext().update(FS_MODEL)
			.set(FS_MODEL.DOMAIN, mod349.getDomain())
			.set(FS_MODEL.YEAR, mod349.getYear())
			.set(FS_MODEL.ADMINISTRATION, mod349.getAdministration().value())
			.set(FS_MODEL.STATUS, AonEnumUtils.getByte(mod349.getStatus()))
			.set(FS_MODEL.SECURITY_LEVEL,AonEnumUtils.getByte( mod349.isConfidential() ))
			.set(FS_MODEL.COMPLEMENTARY, AonEnumUtils.getByte( mod349.isComplementary()))
			.set(FS_MODEL.REPLACEMENT,AonEnumUtils.getByte( mod349.isReplacement() ))  // Modelo 349 solo hay complementaria
//			.set(FS_MODEL.WITHOUTACTIVITY,AonEnumUtils.getByte( mod349.isWithoutActivity()  ))
			.set(FS_MODEL.MODEL, mod349.getModel().getValue())
			.set(FS_MODEL.NUMBER, mod349.getNumber())
			.set(FS_MODEL.REPLACED_NUMBER, mod349.getReplacedNumber())
			.set(FS_MODEL.COMMENTS, mod349.getComments())
			.set(FS_MODEL.FINANCE, mod349.getFinance() == null?null:mod349.getFinance().getId())
			.set(FS_MODEL.DOCUMENT, mod349.getDocument())
			.set(FS_MODEL.SURNAME, mod349.getSurname())
			.set(FS_MODEL.NAME, mod349.getName())
//			.set(FS_MODEL.STREET_INITIAL,mod349.getStreetInitial())
//			.set(FS_MODEL.STREET_NAME,mod349.getStreetName())
//			.set(FS_MODEL.STREET_NUMBER,mod349.getStreetNumber())
//			.set(FS_MODEL.STREET_STAIR,mod349.getStreetStair())
//			.set(FS_MODEL.STREET_FLOOR,mod349.getStreetFloor())
//			.set(FS_MODEL.STREET_DOOR,mod349.getStreetDoor())
//			.set(FS_MODEL.PHONE,mod349.getPhone())
//			.set(FS_MODEL.TOWN,mod349.getTown())
//			.set(FS_MODEL.PROVINCE,mod349.getProvince())
//			.set(FS_MODEL.ZIP,mod349.getZip())
//			.set(FS_MODEL.ADMON_AEAT,mod349.getAdmonAeat())
			.set(FS_MODEL.CONTACT_PERSON,mod349.getContactPerson())
			.set(FS_MODEL.CONTACT_PHONE,mod349.getContactPhone())
//			.set(FS_MODEL.CONTACT_CELLULAR,mod349.getContactCellular())
			.set(FS_MODEL.CONTACT_EMAIL,mod349.getContactMail())
			.set(FS_MODEL.RESULT, mod349.getDeclarationResult())
			.set(FS_MODEL.DECLARATION_TYPE,AonEnumUtils.getByte( mod349.getDeclarationResultType() ) )
//			.set(FS_MODEL.ACCOUNT_ENTRY,mod349.getAccountEntry())
			.set(FS_MODEL.MODIFICATION_USER, mod349.getModificationUser())
			.set(FS_MODEL.MODIFICATION_DATE, AonDateUtils.toTimestamp(mod349.getModificationDate()))
		.where(FS_MODEL.ID.equal(mod349.getFsModel()))
		.execute();
		return mod349.getFsModel();
		
	}
	
	private static FiscalModel getFiscalModel(Mod349 mod349) {
		return new FiscalModel()
		         	.setId(mod349.getFsModel())
		         	.setDomain(mod349.getDomain())
		         	.setModel(mod349.getModel());
		
	}
	
}
