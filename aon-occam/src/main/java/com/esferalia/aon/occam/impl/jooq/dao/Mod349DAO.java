package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.FsMod349.FS_MOD349;
import static com.esferalia.aon.jooq.tables.FsMod349Detail.FS_MOD349_DETAIL;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.FsMod349Record;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelUtils;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod349;
import com.esferalia.aon.occam.api.model.fiscal.Mod349Detail;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.Mod349Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.impl.jooq.dao.Mod349Formatter.Mod349DetailInfo;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod349DAO {
	
	private static byte ZERO_BYTE = 0;
	private static byte ONE_BYTE = 1;
	
	public static LinkedList<Mod349> getByDomain(AONContext ctx, int domain) {
		ctx.checkRead();
		return  ctx.getDslContext()
			.select(FS_MOD349.fields())
			.from(FS_MOD349)
			.join(DOMAIN).on(FS_MOD349.DOMAIN.equal(DOMAIN.ID))
			.where(FS_MOD349.DOMAIN.equal(domain).or(DOMAIN.PARENT.equal(domain)))
			.orderBy(FS_MOD349.YEAR.desc()
					,getDueMonth().desc()  // Se ordena por el mes hasta del periodo, por que en un mismo ejercicio podrían coincidir varias periodicidades (trimestral y mensual, por ejemplo)
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
	}

	private static Mod349 insert(AONContext ctx, Mod349 mod349) {
		validate(ctx,mod349);
		FsMod349Record record = ctx.getDslContext().insertInto(FS_MOD349)
			.set(FS_MOD349.DOMAIN,mod349.getDomain())
			.set(FS_MOD349.YEAR,mod349.getYear())
			.set(FS_MOD349.PERIOD,mod349.getPeriod().getValue())			
			.set(FS_MOD349.ADMINISTRATION, mod349.getAdministration().getValue())
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
			.set(FS_MOD349.CREATION_USER,ctx.getUser())
			.set(FS_MOD349.CREATION_DATE, new Timestamp( System.currentTimeMillis()) )						
		.returning(FS_MOD349.ID)
		.fetchOne();
		mod349.setId(record.getId());
		// Se rellena el modelo leyendo de las facturas intracomunitarias
		insertDetailsFromInvoice(ctx, mod349);
		return mod349;
	}

	private static Mod349 update(AONContext ctx, Mod349 mod349) {
		ctx.getDslContext().update(FS_MOD349)			
			.set(FS_MOD349.YEAR,mod349.getYear())
			.set(FS_MOD349.PERIOD,mod349.getPeriod().getValue())			
			.set(FS_MOD349.ADMINISTRATION, mod349.getAdministration().getValue())
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
			.set(FS_MOD349.MODIFICATION_USER,ctx.getUser())
			.set(FS_MOD349.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )			
			.where(FS_MOD349.ID.equal(mod349.getId()))
		.execute();
		return mod349;
	}

	public static void delete(AONContext ctx, Mod349 mod349) {
		ctx.checkWrite();
		deleteDetails(ctx, mod349);
		ctx.getDslContext().delete(FS_MOD349)
			.where(FS_MOD349.ID.equal(mod349.getId()))
			.execute();
	}
	
	private static void validate(AONContext ctx, Mod349 mod349) {
		
		// Comprobar que está cumplimentado el ejercicio y el periodo
		if (mod349.getYear() == 0 || mod349.getPeriod() == null)
			throw new AonCoreException("Debe cumplimentar Ejercicio y Periodo.");			
		
		// Se comprueba que no exista otra declaración sustitutiva que sustituya a la misma anterior
		if (mod349.isReplacement()) {
						
			if (ctx.getDslContext().selectOne()
				.from(FS_MOD349)
				.where(FS_MOD349.DOMAIN.equal(mod349.getDomain())
						.and(FS_MOD349.YEAR.equal(mod349.getYear()))
						.and(FS_MOD349.PERIOD.equal(mod349.getPeriod().getValue()))
						.and(FS_MOD349.ADMINISTRATION.equal(mod349.getAdministration().getValue()))
						.and(FS_MOD349.REPLACEMENT.equal( ONE_BYTE ))
						.and(FS_MOD349.REPLACED_NUMBER.equal(mod349.getReplacedNumber())))
				.fetch()
				.stream()
				.findFirst()
				.isPresent()) 
				throw new AonCoreException(AonError.FISCAL_DECLARATION_ALREADY_REPLACED.getMessage());			
		}
		else if (mod349.isReplacement() || mod349.isComplementary()) {
			// Se comprueba que exista la declaración sustituida/complementada
			// *** Esto no se comprueba, pues no podríamos crear una complementaria/sustitutiva si 
			// *** antes no tenemos creada la declaración a la que sustituye, ademas no veo que se 
			// *** cumplimente "number" en ningún momento, con el número real de presentación
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
			// Se comprueba que no exista ya una declaración, para el perido indicado
			if (ctx.getDslContext().selectOne()
				.from(FS_MOD349)
				.where(FS_MOD349.DOMAIN.equal(mod349.getDomain())
						.and(FS_MOD349.YEAR.equal(mod349.getYear()))
						.and(FS_MOD349.PERIOD.equal(mod349.getPeriod().getValue()))
						.and(FS_MOD349.ADMINISTRATION.equal((byte) mod349.getAdministration().getValue()))
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
				deleteDetail(ctx, detail);
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
	
	private static void deleteDetail(AONContext ctx, Mod349Detail detail) {
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
		
		// Se comprueba unicamente si está cumplimentado el apartado de rectificaciones
		// para verificar que está indicado ejercicio y periodo rectificado
		if (detail.getRectifiedYear() != null || detail.getRectifiedPeriod() != null || detail.getRectifiedAmount() != 0) {
			if ((detail.getRectifiedYear() != null && detail.getRectifiedPeriod() == null) || 
			    (detail.getRectifiedYear() == null && detail.getRectifiedPeriod() != null) ||
			    (detail.getRectifiedAmount() != 0 && detail.getRectifiedYear() == null && detail.getRectifiedPeriod() == null)) {
				throw new AonCoreException("Si indica rectificaciones, debe indicar los datos A\u00F1o y Periodo");			
			}
		}
		
		// Además se marca el campo "rectificado", si es una rectificación
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
		
		// Ponemos por defecto el año, según la fecha actual, si estamos en enero ponemos
		// el año anterior (se supone que queremos hacer el del ultimo periodo del año anterior)
		// en caso contrario ponemos el año actual
		Date today = new Date();
		int year = AonDateUtils.getYear(today);		
		if (AonDateUtils.getMonth(today) == 0) {
			year = year - 1;			
		}
		
		FiscalParameters params = AppParamDAO.getFiscalParameters(ctx);
		
		Mod349 mod349 = new Mod349();
		mod349.setDomain(ctx.getDomainId());
		mod349.setYear(year);		
		mod349.setAdministration(params.getAdministration(Administration.COMMON_TERRITORY));
		mod349.setNumber("3490000000001");
		mod349.setDocument(params.getDocument());
		mod349.setName(AonStringUtils.left(params.getName(), FS_MOD349.NAME.getDataType().length()));
		mod349.setContactPhone(AonStringUtils.left(params.getContactPhone(), FS_MOD349.CONTACT_PHONE.getDataType().length()));
		mod349.setContactPerson(AonStringUtils.left(params.getContactPerson(), FS_MOD349.CONTACT_PERSON.getDataType().length()));
		mod349.setContactMail(AonStringUtils.left(params.getContactMail(), FS_MOD349.CONTACT_MAIL.getDataType().length()));		
		mod349.setStatus(FiscalStatus.PENDING);
	    mod349.setDiffEnabled(!params.isMod303ByDifferenceDisabled());				
		mod349.setDetails(new LinkedList<Mod349Detail>());
		return mod349;
	}
	
	private static class Mod349Filler implements Function<Record, Mod349> {

		@Override
		public Mod349 apply(Record record) {
			return new Mod349() 
				.setId(record.getValue(FS_MOD349.ID))
				.setDomain(record.getValue(FS_MOD349.DOMAIN))				
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
	
	// --------------- INSERT DETAILS FROM INVOICE ---------------
	
	private static void insertDetailsFromInvoice(AONContext ctx , final Mod349 mod349) {
		
		// Lo que se hacía antes en el modelo viejo (está en Mod349Manager):
		// - Se leen facturas intracomunitarias (transaccion=1) desde el inicio del ejercicio hasta el final del periodo
		// - Por pantalla se piden si se desea:
		// 		- Usar la fecha de IVA en lugar de la fecha de emisión de la factura
		//		- Agrupar solo por Clave+NIF en vez de Clave+idregistry+NIF
		// - La suma de la base de todas las facturas, es el importe acumulado
		// - El importe declarado anteriormente se obtiene leyendo lo declarado en mod349detail de periodos anteriores
		// - El importe (amount) es la diferencia (acumulado-declarado), que es lo que se declara en este periodo
		
		// Lo que se hace ahora:
		// - Se coge como referencia la fecha de IVA de la factura, que es lo mismo que se hace en el 303
		// - Se agrupa siempre por Clave + Pais + NIF
		// - Si se ha indicado la creación del modelo por diferencias:
		// 		- Se lee todo el año, y se le resta lo declarado anteriormente (igual que antes)
		// 		- El importe declarado anteriormente se obtiene acumulando declarado - rectificado, de los periodos anteriores del ejercicio
		
		// Obtenemos el desglose de facturas intracomunitarias del periodo o acumulado anual, usando VATDAO
		getVatBreakdown(ctx, mod349)
		
		// Agrupamos por tipo factura + esServicio + pais + documento + nombre (acumulando la base)
		.collect(Collectors.groupingBy(VatContext::getInvoiceType,
				   Collectors.groupingBy(VatContext::isService,
				     Collectors.groupingBy(VatContext::getRegistryDocumentCountry,
					   Collectors.groupingBy(VatContext::getRegistryDocument,
					     Collectors.groupingBy(VatContext::getRegistryName, Collectors.summingDouble(VatContext::getBase) ))))))
		
		// Grabamos los datos en la tabla de lineas del modelo 349
		.forEach( (invoiceType,b) -> {
			
			for (Boolean isService : b.keySet())
			 for (Country country : b.get(isService).keySet())
			  for (String document : b.get(isService).get(country).keySet())
			   for (String name : b.get(isService).get(country).get(document).keySet()) {
				   
				   Mod349Key keyOperation = null;  // Clave Modelo 349 según tipo de la factura (igual que antes en el modelo viejo)
				   
				   if (invoiceType == InvoiceType.PURCHASE) {				
						keyOperation = Mod349Key.A; // // Compras (Adquisiciones intracomunitarias de bienes) 
				   }
				   else if (invoiceType == InvoiceType.SALES) {
						if (isService)
							keyOperation = Mod349Key.S; // Ventas (Prestaciones Intracomunitarias de Servicios)
						else keyOperation = Mod349Key.E; // Ventas (Entregas intracomunitarias de bienes)
				   }				
				   else keyOperation = Mod349Key.I;  // Gastos (Adquisiciones intracomunitarias de servicios)
				  
				   // Acumulado
				   double accumulated = b.get(isService).get(country).get(document).get(name).doubleValue(); 
				  
				   // Obtener declarado si el calculo es por diferencias
				   double declared = 0.0;
				   if (mod349.isDiffEnabled()) {
					   declared = getDeclaredModels(ctx, mod349, keyOperation, country, document)
							   .mapToDouble(p -> p.getAmount() - p.getRectifiedAmount())
							   .sum();
				   }
				  
				   // Diferencia
				   double amount = accumulated - declared; 
				  
				   // Añadir el registro, si el importe a declarar es distinto de cero
				   if (amount != 0) {
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
					  insertDetail(ctx,det);
				   }	
			   }
		});
			
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
					// Solo se asignan los campos que luego se muestran en la información
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
				.and(getDueMonthDetail().lessThan((byte) mod349.getPeriod().getDueMonth()))   // Se compara con el mes hasta del periodo, por que pueden coincidir varias periodicidades en el mismo año (por ejemplo de trimestral a mensual o viceversa)
				.and(FS_MOD349_DETAIL.TYPE.equal(key.getValue()))
				.and(FS_MOD349_DETAIL.COUNTRY.equal(country.getIso2()))
				.and(FS_MOD349_DETAIL.DOCUMENT.equal(document))
				.fetch()
				.stream()
				.map( rec -> {
					// Solo se asignan los campos que luego se muestran en la información
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
	
	// Obtiene el desglose de las facturas intracomunitarias, para el periodo del modelo, según sea por diferencias o no
	public static Stream<VatContext> getVatBreakdown(final AONContext ctx, final Mod349 mod349, final boolean isDiffEnabled) {
		
		Date fromDate = isDiffEnabled ? AonDateUtils.getYearFirstDay(mod349.getYear()) : FiscalUtils.getPeriodStart(mod349);
		Date toDate = FiscalUtils.getPeriodEnd(mod349);
		
		// Obtenemos el desglose de las facturas intracomunitarias entre las fechas indicadas
		return VATDAO.getVatBreakdown(ctx,fromDate,toDate)
				.filter(mod -> mod.getTransaction() == InvoiceTransactionType.INTRACOMMUNITY)
				.peek(vat -> vat.setInsidePeriod(mod349==null ? false : FiscalUtils.isInPeriodRange(mod349, vat.getTaxDate())));
		
	}
	
	public static Stream<VatContext> getVatBreakdown(final AONContext ctx, final Mod349 mod349) {
		return getVatBreakdown(ctx, mod349, mod349.isDiffEnabled());		
	}
	
	public static Stream<VatContext> getVatBreakdown(final AONContext ctx, final Mod349 mod349, final Mod349Detail detail, final boolean isDiffEnabled) {
		
		// Facturas a localizar según la clave de la linea del modelo que se le pasa (se hace la operacion inversa que cuando se crea el modelo)
		final InvoiceType invoiceType1;
		final InvoiceType invoiceType2;
		final boolean isService;
		
		if (detail.getType() == Mod349Key.A) {       // Compras (Adquisiciones intracomunitarias de bienes)
			invoiceType1 = InvoiceType.PURCHASE;
			invoiceType2 = null;
			isService = false;
	    }
	    else if (detail.getType() == Mod349Key.S) {  // Ventas (Prestaciones Intracomunitarias de Servicios)
	    	invoiceType1 = InvoiceType.SALES;
	    	invoiceType2 = null;
	    	isService = true;
	    }
	    else if (detail.getType() == Mod349Key.E ) { // Ventas (Entregas intracomunitarias de bienes)
	    	invoiceType1 = InvoiceType.SALES;
	    	invoiceType2 = null;
	    	isService = false;
	    }				
	    else if (detail.getType() == Mod349Key.I) {  // Gastos (Adquisiciones intracomunitarias de servicios)
	    	invoiceType1 = InvoiceType.EXPENSES;
	    	invoiceType2 = InvoiceType.UNDEDUCTIBLE;
	    	isService = true;
	    }
	    else {
	    	invoiceType1 = null;
	    	invoiceType2 = null;
	    	isService = false;
	    }
		
		return getVatBreakdown(ctx, mod349, isDiffEnabled)  
			   .filter( p -> (p.getInvoiceType() == invoiceType1 || p.getInvoiceType() == invoiceType2) && p.isService() == isService && p.getRegistryDocumentCountry() == detail.getCountry() && AonStringUtils.equals(p.getRegistryDocument(),detail.getDocument()));
		
	}
	
	// --------------- INVOICES INFO --------------- 
	
	public static String getMod349Info(AONContext ctx, Mod349 mod349, Mod349Detail detail, FiscalModelKeyInfo infoKey) {
		
		String INFO_MSG = "<pre class='aon-fixed-font aon-font-medium aon-margin-bottom'>{0}<pre>";
		
		// Información Desglose de facturas
		if (infoKey == FiscalModelKeyInfo.INVOICE) {
			return MessageFormat.format(INFO_MSG, getInvoicesInfo(ctx, mod349, detail));			
		}
		
		// Información Desglose del calculo por diferencias
		if (infoKey == FiscalModelKeyInfo.DIFF_INVOICE) {
			return MessageFormat.format(INFO_MSG, getDiffInvoicesInfo(ctx, mod349, detail));
		}
		
		return null;
	}
	
	private static String getSubtitle(final Mod349Detail detail ) {
		return "Clave "+detail.getType().getValue()+
				" - " + detail.getCountry().getIso2() + " " + detail.getDocument() +
				" - " + detail.getName();
	}
	
	private static String getInvoicesInfo(AONContext ctx, Mod349 mod349, Mod349Detail detail) {
		
		String title = "FACTURAS QUE AFECTAN A LA CONFECCI\u00D3N DEL MODELO " 
				+ FiscalModelUtils.getModelName(mod349) 
				+ " DEL " + mod349.getPeriod().getDescription()
				+ " DE " + mod349.getYear();
		
		return VATFormatter.formatInvoices(title
				,getSubtitle(detail)
				,getVatBreakdown(ctx, mod349, detail, false).collect(Collectors.toCollection(LinkedList::new)));
		
	}
	
	private static String getDiffInvoicesInfo(AONContext ctx, final Mod349 mod349, final Mod349Detail detail) {

		String title = "DETALLE DEL C\u00C1LCULO POR DIFERENCIA DEL MODELO "
				+ FiscalModelUtils.getModelName(mod349) 
				+ " DEL " + mod349.getPeriod().getDescription()
				+ " DE " + mod349.getYear();
		
		return Mod349Formatter.formatDiffInvoicesMod349(title
				,getSubtitle(detail)
				,mod349.getPeriod()			
				,getDeclaredModels(ctx, mod349, detail.getType(), detail.getCountry(), detail.getDocument()).collect(Collectors.toCollection(LinkedList::new))			 	
				,getVatBreakdown(ctx, mod349, detail, true).collect(Collectors.toCollection(LinkedList::new)));
		
	}
	
	
}
