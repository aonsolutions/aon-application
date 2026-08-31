package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod347;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.FsMod347.FS_MOD347;
import static com.esferalia.aon.jooq.tables.FsMod347Detail.FS_MOD347_DETAIL;

import java.io.IOException;
import java.io.Writer;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Record;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.FsMod347Record;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod347;
import com.esferalia.aon.occam.api.model.fiscal.Mod347Asset;
import com.esferalia.aon.occam.api.model.fiscal.Mod347Declared;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryFull;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.Mod347Key;
import com.esferalia.aon.occam.api.model.type.Province;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.occam.impl.jooq.dao.ConfigurationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CreditorDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DataResponseDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryAddressDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SupplierDAO;
import com.esferalia.aon.occam.impl.jooq.dao.vat.VATDAO;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod347DAO {
	
	private static final String FLAG_SEPARATOR = "|#|";
	private static final byte ZERO_BYTE = 0;
	private static final byte ONE_BYTE = 1;
	
	// -------------------- MOD347 --------------------
	
	public static Stream<Mod347> getHeaders(AONContext ctx, int domain) {
		return getHeaders(ctx, domain, null);
	}
	public static Stream<Mod347> getHeaders(AONContext ctx, int domain, Integer scope) {
		ctx.checkRead();
		return  ctx.getDslContext()
			.select(FS_MOD347.fields())
			.select(DOMAIN.DESCRIPTION)
			.from(FS_MOD347)
			.join(DOMAIN).on(FS_MOD347.DOMAIN.equal(DOMAIN.ID))
			.where(FS_MOD347.DOMAIN.equal(domain).or(DOMAIN.PARENT.equal(domain)))
			.and( scope == null ? DSL.trueCondition() : DOMAIN.SCOPE.equal(scope))
			.orderBy(FS_MOD347.YEAR.desc()
					,FS_MOD347.NAME.asc()
					,FS_MOD347.COMPLEMENTARY.asc()
					,FS_MOD347.REPLACEMENT.asc())
			.fetch()
			.stream()
			.map(new Mod347Filler());
	}

	public static LinkedList<Mod347> getByDomain(AONContext ctx, int domain) {
		ctx.checkRead();
		return  ctx.getDslContext()
			.select(FS_MOD347.fields())
			.select(DOMAIN.DESCRIPTION)
			.from(FS_MOD347)
			.join(DOMAIN).on(FS_MOD347.DOMAIN.equal(DOMAIN.ID))
			.where(FS_MOD347.DOMAIN.equal(domain).or(DOMAIN.PARENT.equal(domain)))
			.orderBy(FS_MOD347.YEAR.desc()
					,FS_MOD347.NAME.asc()
					,FS_MOD347.COMPLEMENTARY.asc()
					,FS_MOD347.REPLACEMENT.asc())
			.fetch()
			.stream()
			.map(new Mod347Filler())
			.peek( mod347 -> mod347.setDeclared( getDeclared(ctx, mod347.getId()) ))
			.peek( mod347 -> mod347.setAssets( getAssets(ctx, mod347.getId()) ))
			.collect(Collectors.toCollection(LinkedList::new));
	}

	public static Mod347 getById(AONContext ctx, int id) {		
		ctx.checkRead();
		return ctx.getDslContext()
			.select(FS_MOD347.fields())
			.select(DOMAIN.DESCRIPTION)
			.from(FS_MOD347)
			.join(DOMAIN).on(FS_MOD347.DOMAIN.equal(DOMAIN.ID))
			.where(FS_MOD347.DOMAIN.equal(ctx.getDomainId()).or(DOMAIN.PARENT.equal(ctx.getDomainId())))
			.and(FS_MOD347.ID.equal(id))
			.fetch()
			.stream()
			.map(new Mod347Filler())
			.peek( mod347 -> mod347.setDeclared( getDeclared(ctx, mod347.getId()) ))
			.peek( mod347 -> mod347.setAssets( getAssets(ctx, mod347.getId()) ))
			.findFirst()
			.orElse(null);
	}
	
	private static class Mod347Filler implements Function<Record, Mod347> {

		@Override
		public Mod347 apply(Record record) {
			String comments = record.getValue(FS_MOD347.COMMENTS);
			String flags = AonStringUtils.substringAfter(comments, FLAG_SEPARATOR);
			comments = AonStringUtils.substringBefore(comments, FLAG_SEPARATOR); 
			return new Mod347() 
				.setId(record.getValue(FS_MOD347.ID))
				.setDomain(record.getValue(FS_MOD347.DOMAIN))				
				.setDomainName(record.getValue(DOMAIN.DESCRIPTION))
				.setYear(record.getValue(FS_MOD347.YEAR))
				.setAdministration(Administration.safeValueOf(record.getValue(FS_MOD347.ADMINISTRATION)))
				.setComments(comments)				
				.setStatus(com.esferalia.aon.watson.util.AonEnumUtils.enumValue(FiscalStatus.class,record.getValue(FS_MOD347.STATUS)))
				.setConfidential(AonEnumUtils.getBoolean(record.getValue(FS_MOD347.SECURITY_LEVEL)))				
				.setComplementary( record.getValue(FS_MOD347.COMPLEMENTARY)==1 )
				.setReplacement( record.getValue(FS_MOD347.REPLACEMENT)==1 )				
				.setNumber(record.getValue(FS_MOD347.NUMBER))
				.setReplacedNumber(record.getValue(FS_MOD347.REPLACED_NUMBER))
				.setDocument(record.getValue(FS_MOD347.DOCUMENT))
				.setName(record.getValue(FS_MOD347.NAME))
				.setContactPerson(record.getValue(FS_MOD347.CONTACT_PERSON))
				.setContactPhone(record.getValue(FS_MOD347.CONTACT_PHONE))
				.setContactMail(record.getValue(FS_MOD347.CONTACT_MAIL))
				.setRepresentativeDocument(record.getValue(FS_MOD347.REPRESENTATIVE_DOCUMENT))
				.setCreationUser(record.getValue(FS_MOD347.CREATION_USER))
				.setCreationDate(record.getValue(FS_MOD347.CREATION_DATE))
				.setModificationUser(record.getValue(FS_MOD347.MODIFICATION_USER))
				.setModificationDate(record.getValue(FS_MOD347.MODIFICATION_DATE))
				.setExcludeInputNationalZero(ensureFlag(0,flags))
				.setExcludeOutputNationalZero(ensureFlag(1,flags))
				.setExcludeMod180Declared(ensureFlag(2,flags))
				.setExcludeMod190Declared(ensureFlag(3,flags))
				.setExcludeRetention(ensureFlag(4,flags))
				.setExcludeIntracommunity(ensureFlag(5,flags))
				;
		}

		private boolean ensureFlag(int i, String flags) {
			if (AonStringUtils.isBlank(flags)) return false;
			String[] tokens = AonStringUtils.split(flags, ',');
			if (i < tokens.length)
				return Boolean.parseBoolean(tokens[i]);
			else return false;			
		}
	}
	
	public static Mod347 initialize(AONContext ctx, int year) {	
		
		AonConfiguration conf = ConfigurationDAO.getConfiguration(ctx);		
		Mod347 mod347 = new Mod347();
		mod347.setDomain(ctx.getDomainId());
		mod347.setYear(year);		
		mod347.setAdministration(conf.fiscal().getAdministration(Administration.COMMON_TERRITORY));
		mod347.setNumber("3470000000001");
		mod347.setDocument(conf.getCompany().getDocument());
		mod347.setName(AonStringUtils.left(conf.getCompany().getName(), FS_MOD347.NAME.getDataType().length()));
		mod347.setContactPhone(AonStringUtils.left(conf.fiscal().getContactPhone(), FS_MOD347.CONTACT_PHONE.getDataType().length()));
		mod347.setContactPerson(AonStringUtils.left(conf.fiscal().getContactPerson(), FS_MOD347.CONTACT_PERSON.getDataType().length()));
		mod347.setContactMail(AonStringUtils.left(conf.fiscal().getContactMail(), FS_MOD347.CONTACT_MAIL.getDataType().length()));
		mod347.setStatus(FiscalStatus.PENDING);
		mod347.setDeclared(new LinkedList<Mod347Declared>());
		mod347.setAssets(new LinkedList<Mod347Asset>());
		mod347.setExcludeOutputNationalZero(false);
		mod347.setExcludeInputNationalZero(true);
		mod347.setExcludeMod180Declared(true);
		mod347.setExcludeMod190Declared(true);
		mod347.setExcludeRetention(true);
		mod347.setExcludeIntracommunity(true);
		return mod347;
	}
	
	public static Mod347 saveComments(AONContext ctx, Mod347 fm) {
		try {
			ctx.checkWrite();
			String comments = 
				  AonStringUtils.defaultString( fm.getComments() ) 
				+ FLAG_SEPARATOR
				+ fm.isExcludeInputNationalZero() + ","
				+ fm.isExcludeOutputNationalZero() + ","
				+ fm.isExcludeMod180Declared() + ","
				+ fm.isExcludeMod190Declared() + ","
				+ fm.isExcludeRetention() + ","
				+ fm.isExcludeIntracommunity();
			if (fm.getId() != null) {
				ctx.getDslContext().update(FS_MOD347)
					.set(FS_MOD347.COMMENTS, comments)
					.where(FS_MOD347.ID.equal(fm.getId()))
					.execute();
			}
			return fm;
		} catch (DataAccessException t) {
			throw new AonCoreException(t.getCause()!=null?t.getCause().getMessage():t.getMessage());
		} catch (Throwable t) {
			throw new AonCoreException(t.getMessage());
		}
	}
	
	public static Mod347 changeStatus(AONContext ctx, Mod347 mod347, FiscalStatus newStatus) {
		try {
			ctx.checkWrite();
			if (mod347.getId() != null) {
				mod347.setStatus(newStatus);
				ctx.getDslContext().update(FS_MOD347)
					.set(FS_MOD347.STATUS,AonEnumUtils.getByte( mod347.getStatus()))
					.where(FS_MOD347.ID.equal(mod347.getId()))
					.execute();
			}
			return mod347;
		} catch (DataAccessException t) {
			throw new AonCoreException(t.getCause()!=null?t.getCause().getMessage():t.getMessage());
		} catch (Throwable t) {
			throw new AonCoreException(t.getMessage());
		}
	}
	
	public static Mod347 save(AONContext ctx, Mod347 mod347) {
		try {	
			ctx.checkWrite();
			if (mod347.getId() == null) {
				mod347 = insert(ctx, mod347); 
				 
			} else {
				mod347 = update(ctx, mod347);
			}
			for (Mod347Declared declared : mod347.getDeclared()) {
				saveDeclared(ctx,mod347,declared);
			}
			for (Mod347Asset asset : mod347.getAssets()) {
				saveAsset(ctx,mod347,asset);
			}
			return getById(ctx, mod347.getId());
		} catch (Throwable t) {
			throw new AonCoreException(t.getCause()!=null?t.getCause().getMessage():t.getMessage());
		}
	}

	private static Mod347 insert(AONContext ctx, Mod347 mod347) {
		return insert(ctx,mod347,true);
	}
	
	private static Mod347 insert(AONContext ctx, Mod347 mod347, boolean generateDetails) {
		validate(ctx,mod347);
		String comments = 
				  AonStringUtils.defaultString( mod347.getComments() ) 
				+ FLAG_SEPARATOR
				+ mod347.isExcludeInputNationalZero() + ","
				+ mod347.isExcludeOutputNationalZero() + ","
     			+ mod347.isExcludeMod180Declared() + ","
				+ mod347.isExcludeMod190Declared() + ","
				+ mod347.isExcludeRetention() + ","
				+ mod347.isExcludeIntracommunity();
		FsMod347Record record = ctx.getDslContext().insertInto(FS_MOD347)
			.set(FS_MOD347.DOMAIN,mod347.getDomain())
			.set(FS_MOD347.YEAR,mod347.getYear())
			.set(FS_MOD347.ADMINISTRATION, mod347.getAdministration().value())
			.set(FS_MOD347.COMMENTS,comments)
			.set(FS_MOD347.STATUS, ZERO_BYTE )
			.set(FS_MOD347.SECURITY_LEVEL, AonEnumUtils.getByte(mod347.isConfidential()) )			
			.set(FS_MOD347.COMPLEMENTARY, AonEnumUtils.getByte(mod347.isComplementary()))
			.set(FS_MOD347.REPLACEMENT, AonEnumUtils.getByte(mod347.isReplacement()))
			.set(FS_MOD347.NUMBER,mod347.getNumber())
			.set(FS_MOD347.REPLACED_NUMBER,mod347.getReplacedNumber())
			.set(FS_MOD347.DOCUMENT,mod347.getDocument())
			.set(FS_MOD347.NAME,mod347.getName())
			.set(FS_MOD347.CONTACT_PHONE,mod347.getContactPhone())
			.set(FS_MOD347.CONTACT_PERSON,mod347.getContactPerson())
			.set(FS_MOD347.CONTACT_MAIL,mod347.getContactMail())
			.set(FS_MOD347.REPRESENTATIVE_DOCUMENT,mod347.getRepresentativeDocument())
			.set(FS_MOD347.CREATION_USER,ctx.getUser())
			.set(FS_MOD347.CREATION_DATE, new Timestamp(System.currentTimeMillis()))
		.returning(FS_MOD347.ID)
		.fetchOne();
		mod347.setId(record.getId());
		if (generateDetails) {
			insertDetailsFromInvoice(ctx, mod347); // Se rellena el modelo leyendo de las facturas
		}
		return mod347;
	}

	private static Mod347 update(AONContext ctx, Mod347 mod347) {
		String comments = 
				  AonStringUtils.defaultString( mod347.getComments() ) 
				+ FLAG_SEPARATOR
				+ mod347.isExcludeInputNationalZero() + ","
				+ mod347.isExcludeOutputNationalZero() + ","
				+ mod347.isExcludeMod180Declared() + ","
				+ mod347.isExcludeMod190Declared() + ","
				+ mod347.isExcludeRetention() + ","
				+ mod347.isExcludeIntracommunity();
		
		ctx.getDslContext().update(FS_MOD347)			
			.set(FS_MOD347.YEAR,mod347.getYear())
			.set(FS_MOD347.ADMINISTRATION, mod347.getAdministration().value())
			.set(FS_MOD347.COMMENTS,comments)
			.set(FS_MOD347.STATUS, AonEnumUtils.getByte( mod347.getStatus() ) )
			.set(FS_MOD347.SECURITY_LEVEL,AonEnumUtils.getByte(mod347.isConfidential()) )			
			.set(FS_MOD347.COMPLEMENTARY,AonEnumUtils.getByte(mod347.isComplementary()))
			.set(FS_MOD347.REPLACEMENT,AonEnumUtils.getByte(mod347.isReplacement()))
			.set(FS_MOD347.NUMBER,mod347.getNumber())
			.set(FS_MOD347.REPLACED_NUMBER,mod347.getReplacedNumber())
			.set(FS_MOD347.DOCUMENT,mod347.getDocument())
			.set(FS_MOD347.NAME,mod347.getName())
			.set(FS_MOD347.CONTACT_PHONE,mod347.getContactPhone())
			.set(FS_MOD347.CONTACT_PERSON,mod347.getContactPerson())
			.set(FS_MOD347.CONTACT_MAIL,mod347.getContactMail())
			.set(FS_MOD347.REPRESENTATIVE_DOCUMENT,mod347.getRepresentativeDocument())
			.set(FS_MOD347.MODIFICATION_USER,ctx.getUser())
			.set(FS_MOD347.MODIFICATION_DATE, new Timestamp( System.currentTimeMillis()) )
			.where(FS_MOD347.ID.equal(mod347.getId()))
		.execute();
		return mod347;
	}

	public static void delete(AONContext ctx, Mod347 mod347) {
		ctx.checkWrite();
		deleteDetails(ctx, mod347);
		ctx.getDslContext().delete(FS_MOD347)
			.where(FS_MOD347.ID.equal(mod347.getId()))
			.execute();
	}
	
	private static void deleteDetails(AONContext ctx, Mod347 mod347) {
		ctx.getDslContext().delete(FS_MOD347_DETAIL)
			.where(FS_MOD347_DETAIL.FS_MOD347.equal(mod347.getId()))
			.execute();
	}
	
	private static void validate(AONContext ctx, Mod347 mod347) {
		
		// Comprobar que está cumplimentado el ejercicio
		if (mod347.getYear() == 0)
			throw new AonCoreException("Debe cumplimentar el Ejercicio.");
		
		// Canarias solo a partir del ejercicio 2025
		if (mod347.getAdministration() == Administration.CANARIAS && mod347.getYear() < 2025)
			throw new AonCoreException("El modelo 415 en Canarias solo est\u00E1 disponible a partir del ejercicio 2025.");
		
		// NIF Declarante debe estar cumplimentado y de longitud menor de 9
		if (AonStringUtils.isBlank(mod347.getDocument()) || mod347.getDocument().length() > 9)
			throw new AonCoreException("El NIF del Declarante debe estar cumplimentado y su longitud no puede ser mayor de 9 caracteres.");		
		
		// Se comprueba que no exista otra declaración sustitutiva que sustituya a la misma anterior
		if (mod347.isReplacement()) {
						
			if (ctx.getDslContext().selectOne()
				.from(FS_MOD347)
				.where(FS_MOD347.DOMAIN.equal(mod347.getDomain())
						.and(FS_MOD347.YEAR.equal(mod347.getYear()))
						.and(FS_MOD347.ADMINISTRATION.equal(mod347.getAdministration().value()))
						.and(FS_MOD347.REPLACEMENT.equal(ONE_BYTE))
						.and(FS_MOD347.REPLACED_NUMBER.equal(mod347.getReplacedNumber()))
						)
				.fetch()
				.stream()
				.findFirst()
				.isPresent()) 
				throw new AonCoreException(AonError.FISCAL_DECLARATION_ALREADY_REPLACED.getMessage());			
		}
		else if (!mod347.isComplementary()) {
			// Se comprueba que no exista ya una declaración, para el perido indicado
			if (ctx.getDslContext().selectOne()
				.from(FS_MOD347)
				.where(FS_MOD347.DOMAIN.equal(mod347.getDomain())
						.and(FS_MOD347.YEAR.equal(mod347.getYear()))
						.and(FS_MOD347.ADMINISTRATION.equal((byte) mod347.getAdministration().value()))
						.and(FS_MOD347.REPLACEMENT.equal(ZERO_BYTE))
						.and(FS_MOD347.COMPLEMENTARY.equal(ZERO_BYTE)))				
				.fetch()
				.stream()
				.findFirst()
				.isPresent()) 
				throw new AonCoreException(
						AonError.FISCAL_DECLARATION_ALREADY_EXISTS.getMessage());
		}
	}
	
	// -------------------- MOD347DECLARED --------------------

	private static LinkedList<Mod347Declared> getDeclared(AONContext ctx, int mod347) {
		ctx.checkRead();
		return ctx.getDslContext()
			.selectFrom(FS_MOD347_DETAIL)
			.where(FS_MOD347_DETAIL.FS_MOD347.equal(mod347))
			.and(FS_MOD347_DETAIL.SHEET.eq("D"))
			.fetch()
			.stream()
			.map( new Mod347DeclaredFiller() )
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	private static class Mod347DeclaredFiller implements Function<Record, Mod347Declared> {
		
		@Override
		public Mod347Declared apply(Record record) {
			return new Mod347Declared()					
				.setId(record.getValue(FS_MOD347_DETAIL.ID))				
				.setType(Mod347Key.safeValueOf(record.getValue(FS_MOD347_DETAIL.TYPE)))
				.setDocument(record.getValue(FS_MOD347_DETAIL.DOCUMENT))
				.setRepresentativeDocument(record.getValue(FS_MOD347_DETAIL.REPRESENTATIVE_DOCUMENT))
				.setRegistry(record.getValue(FS_MOD347_DETAIL.REGISTRY))
				.setName(record.getValue(FS_MOD347_DETAIL.NAME))
				.setProvince(Province.safeValueOf(record.getValue(FS_MOD347_DETAIL.PROVINCE)))
				.setCountry(Country.safeValueOf(record.getValue(FS_MOD347_DETAIL.COUNTRY)))
				.setAmount(record.getValue(FS_MOD347_DETAIL.AMOUNT))
				.setFirstQuarterAmount(record.getValue(FS_MOD347_DETAIL.FIRST_QUARTER_AMOUNT))
				.setSecondQuarterAmount(record.getValue(FS_MOD347_DETAIL.SECOND_QUARTER_AMOUNT))
				.setThirdQuarterAmount(record.getValue(FS_MOD347_DETAIL.THIRD_QUARTER_AMOUNT))
				.setFourthQuarterAmount(record.getValue(FS_MOD347_DETAIL.FOURTH_QUARTER_AMOUNT))
				.setAssetAmount(record.getValue(FS_MOD347_DETAIL.ASSET_AMOUNT))
				.setAssetFirstQuarterAmount(record.getValue(FS_MOD347_DETAIL.ASSET_FIRST_QUARTER_AMOUNT))
				.setAssetSecondQuarterAmount(record.getValue(FS_MOD347_DETAIL.ASSET_SECOND_QUARTER_AMOUNT))
				.setAssetThirdQuarterAmount(record.getValue(FS_MOD347_DETAIL.ASSET_THIRD_QUARTER_AMOUNT))
				.setAssetFourthQuarterAmount(record.getValue(FS_MOD347_DETAIL.ASSET_FOURTH_QUARTER_AMOUNT))
				.setCashAmount(record.getValue(FS_MOD347_DETAIL.CASH_AMOUNT))
				.setCashYear(record.getValue(FS_MOD347_DETAIL.CASH_YEAR))			
				.setInsuranceOperation(AonEnumUtils.getBoolean(record.getValue(FS_MOD347_DETAIL.INSURANCE_OPERATION)))
				.setBusinessPremiseRental(AonEnumUtils.getBoolean(record.getValue(FS_MOD347_DETAIL.BUSINESS_PREMISE_RENTAL)))
				.setOperatorNif(record.getValue(FS_MOD347_DETAIL.OPERATOR_NIF))
				.setVatAccrual(AonEnumUtils.getBoolean(record.getValue(FS_MOD347_DETAIL.VAT_ACCRUAL)))
				.setIsp(AonEnumUtils.getBoolean(record.getValue(FS_MOD347_DETAIL.ISP)))
				.setDepositRegime(AonEnumUtils.getBoolean(record.getValue(FS_MOD347_DETAIL.DEPOSIT_REGIME)))
				.setVatAccrualAmount(record.getValue(FS_MOD347_DETAIL.VAT_ACCRUAL_AMOUNT))
				.setBdns(record.getValue(FS_MOD347_DETAIL.BDNS))
				.setRentalAmount(record.getValue(FS_MOD347_DETAIL.RENTAL_AMOUNT))
				.setFirstQuarterRentalAmount(record.getValue(FS_MOD347_DETAIL.FIRST_QUARTER_RENTAL_AMOUNT))
				.setSecondQuarterRentalAmount(record.getValue(FS_MOD347_DETAIL.SECOND_QUARTER_RENTAL_AMOUNT))
				.setThirdQuarterRentalAmount(record.getValue(FS_MOD347_DETAIL.THIRD_QUARTER_RENTAL_AMOUNT))
				.setFourthQuarterRentalAmount(record.getValue(FS_MOD347_DETAIL.FOURTH_QUARTER_RENTAL_AMOUNT))
				;
		}
	}

	private static void saveDeclared(AONContext ctx, Mod347 mod347, Mod347Declared declared){
		ctx.checkWrite();
		if (declared.getId() == null) {
			if (!declared.isDeleted()) {
				declared.setDomain(mod347.getDomain());
				declared.setMod347(mod347.getId());
				insertDeclared(ctx, declared);
			}
		} else {
			if (declared.isDeleted()) {
				deleteDeclared(ctx, declared);
			} else {
				// Antes no se guardaba el dominio en las lineas, cuando se generaba el modelo, así
				// que hago que siempre que se guarde, se ponga el dominio en las lineas
				declared.setDomain(mod347.getDomain());
				updateDeclared(ctx, declared);
			}
		}
	}
	
	private static void insertDeclared(AONContext ctx, Mod347Declared declared) {
		validateDeclared(ctx, declared);		
		ctx.getDslContext().insertInto(FS_MOD347_DETAIL)
			.set(FS_MOD347_DETAIL.DOMAIN,declared.getDomain())
			.set(FS_MOD347_DETAIL.FS_MOD347,declared.getMod347())
			.set(FS_MOD347_DETAIL.SHEET,"D")
			.set(FS_MOD347_DETAIL.TYPE, Mod347Key.safeValue(declared.getType()))
			.set(FS_MOD347_DETAIL.DOCUMENT, declared.getDocument())
			.set(FS_MOD347_DETAIL.REPRESENTATIVE_DOCUMENT, declared.getRepresentativeDocument())
			.set(FS_MOD347_DETAIL.REGISTRY, declared.getRegistry())
			.set(FS_MOD347_DETAIL.NAME, declared.getName())
			.set(FS_MOD347_DETAIL.PROVINCE, Province.safeValue(declared.getProvince()))
			.set(FS_MOD347_DETAIL.COUNTRY, Country.safeIso2(declared.getCountry()))
			.set(FS_MOD347_DETAIL.AMOUNT, declared.getAmount())
			.set(FS_MOD347_DETAIL.FIRST_QUARTER_AMOUNT,declared.getFirstQuarterAmount())
			.set(FS_MOD347_DETAIL.SECOND_QUARTER_AMOUNT,declared.getSecondQuarterAmount())
			.set(FS_MOD347_DETAIL.THIRD_QUARTER_AMOUNT,declared.getThirdQuarterAmount())
			.set(FS_MOD347_DETAIL.FOURTH_QUARTER_AMOUNT,declared.getFourthQuarterAmount())
			.set(FS_MOD347_DETAIL.ASSET_AMOUNT,declared.getAssetAmount())
			.set(FS_MOD347_DETAIL.ASSET_FIRST_QUARTER_AMOUNT,declared.getAssetFirstQuarterAmount())
			.set(FS_MOD347_DETAIL.ASSET_SECOND_QUARTER_AMOUNT,declared.getAssetSecondQuarterAmount())
			.set(FS_MOD347_DETAIL.ASSET_THIRD_QUARTER_AMOUNT,declared.getAssetThirdQuarterAmount())
			.set(FS_MOD347_DETAIL.ASSET_FOURTH_QUARTER_AMOUNT,declared.getAssetFourthQuarterAmount())
			.set(FS_MOD347_DETAIL.CASH_AMOUNT,declared.getCashAmount())
			.set(FS_MOD347_DETAIL.CASH_YEAR,declared.getCashYear())			
			.set(FS_MOD347_DETAIL.INSURANCE_OPERATION,AonEnumUtils.getByte(declared.isInsuranceOperation()))
			.set(FS_MOD347_DETAIL.BUSINESS_PREMISE_RENTAL, AonEnumUtils.getByte(declared.isBusinessPremiseRental()))
			.set(FS_MOD347_DETAIL.OPERATOR_NIF,declared.getOperatorNif())
			.set(FS_MOD347_DETAIL.VAT_ACCRUAL,AonEnumUtils.getByte(declared.isVatAccrual()))
			.set(FS_MOD347_DETAIL.ISP,AonEnumUtils.getByte(declared.isIsp()))
			.set(FS_MOD347_DETAIL.DEPOSIT_REGIME,AonEnumUtils.getByte(declared.isDepositRegime()))
			.set(FS_MOD347_DETAIL.VAT_ACCRUAL_AMOUNT, declared.getVatAccrualAmount())
			.set(FS_MOD347_DETAIL.BDNS, declared.getBdns())
			.set(FS_MOD347_DETAIL.RENTAL_AMOUNT, declared.getRentalAmount())
			.set(FS_MOD347_DETAIL.FIRST_QUARTER_RENTAL_AMOUNT, declared.getFirstQuarterRentalAmount())
			.set(FS_MOD347_DETAIL.SECOND_QUARTER_RENTAL_AMOUNT, declared.getSecondQuarterRentalAmount())
			.set(FS_MOD347_DETAIL.THIRD_QUARTER_RENTAL_AMOUNT, declared.getThirdQuarterRentalAmount())
			.set(FS_MOD347_DETAIL.FOURTH_QUARTER_RENTAL_AMOUNT, declared.getFourthQuarterRentalAmount())
			.execute();
		
	}

	private static void updateDeclared(AONContext ctx, Mod347Declared declared) {
		validateDeclared(ctx, declared);
		ctx.getDslContext().update(FS_MOD347_DETAIL)		
			// Antes no se guardaba el dominio en las lineas, cuando se generaba el modelo, así
			// que hago que siempre que se guarde, se ponga el dominio en las lineas
			.set(FS_MOD347_DETAIL.DOMAIN,declared.getDomain())
			.set(FS_MOD347_DETAIL.TYPE, Mod347Key.safeValue(declared.getType()))
			.set(FS_MOD347_DETAIL.DOCUMENT, declared.getDocument())
			.set(FS_MOD347_DETAIL.REPRESENTATIVE_DOCUMENT, declared.getRepresentativeDocument())
			.set(FS_MOD347_DETAIL.REGISTRY, declared.getRegistry())
			.set(FS_MOD347_DETAIL.NAME, declared.getName())
			.set(FS_MOD347_DETAIL.PROVINCE, Province.safeValue(declared.getProvince()))
			.set(FS_MOD347_DETAIL.COUNTRY, Country.safeIso2(declared.getCountry()))
			.set(FS_MOD347_DETAIL.AMOUNT, declared.getAmount())
			.set(FS_MOD347_DETAIL.FIRST_QUARTER_AMOUNT,declared.getFirstQuarterAmount())
			.set(FS_MOD347_DETAIL.SECOND_QUARTER_AMOUNT,declared.getSecondQuarterAmount())
			.set(FS_MOD347_DETAIL.THIRD_QUARTER_AMOUNT,declared.getThirdQuarterAmount())
			.set(FS_MOD347_DETAIL.FOURTH_QUARTER_AMOUNT,declared.getFourthQuarterAmount())
			.set(FS_MOD347_DETAIL.ASSET_AMOUNT,declared.getAssetAmount())
			.set(FS_MOD347_DETAIL.ASSET_FIRST_QUARTER_AMOUNT,declared.getAssetFirstQuarterAmount())
			.set(FS_MOD347_DETAIL.ASSET_SECOND_QUARTER_AMOUNT,declared.getAssetSecondQuarterAmount())
			.set(FS_MOD347_DETAIL.ASSET_THIRD_QUARTER_AMOUNT,declared.getAssetThirdQuarterAmount())
			.set(FS_MOD347_DETAIL.ASSET_FOURTH_QUARTER_AMOUNT,declared.getAssetFourthQuarterAmount())
			.set(FS_MOD347_DETAIL.CASH_AMOUNT,declared.getCashAmount())
			.set(FS_MOD347_DETAIL.CASH_YEAR,declared.getCashYear())			
			.set(FS_MOD347_DETAIL.INSURANCE_OPERATION,AonEnumUtils.getByte(declared.isInsuranceOperation()))
			.set(FS_MOD347_DETAIL.BUSINESS_PREMISE_RENTAL, AonEnumUtils.getByte(declared.isBusinessPremiseRental()))
			.set(FS_MOD347_DETAIL.OPERATOR_NIF,declared.getOperatorNif())
			.set(FS_MOD347_DETAIL.VAT_ACCRUAL,AonEnumUtils.getByte(declared.isVatAccrual()))
			.set(FS_MOD347_DETAIL.ISP,AonEnumUtils.getByte(declared.isIsp()))
			.set(FS_MOD347_DETAIL.DEPOSIT_REGIME,AonEnumUtils.getByte(declared.isDepositRegime()))
			.set(FS_MOD347_DETAIL.VAT_ACCRUAL_AMOUNT, declared.getVatAccrualAmount())
			.set(FS_MOD347_DETAIL.BDNS, declared.getBdns())
			.set(FS_MOD347_DETAIL.RENTAL_AMOUNT, declared.getRentalAmount())
			.set(FS_MOD347_DETAIL.FIRST_QUARTER_RENTAL_AMOUNT, declared.getFirstQuarterRentalAmount())
			.set(FS_MOD347_DETAIL.SECOND_QUARTER_RENTAL_AMOUNT, declared.getSecondQuarterRentalAmount())
			.set(FS_MOD347_DETAIL.THIRD_QUARTER_RENTAL_AMOUNT, declared.getThirdQuarterRentalAmount())
			.set(FS_MOD347_DETAIL.FOURTH_QUARTER_RENTAL_AMOUNT, declared.getFourthQuarterRentalAmount())
			.where(FS_MOD347_DETAIL.ID.equal(declared.getId()))
			.execute();
	}
	
	private static void validateDeclared(AONContext ctx, Mod347Declared declared) {
		if (AonStringUtils.length(declared.getDocument()) > 9) {									
			throw new AonCoreException(
				MessageFormat.format("La longitud del num. documento del Declarado no puede ser mayor de 9 caracteres. [{0} - {1}]"
				,declared.getDocument(),declared.getName()));
		}		
		if (AonStringUtils.length(declared.getOperatorNif()) > 17) {									
			throw new AonCoreException(
				MessageFormat.format("La longitud del NIF Operador Comunitario/Extracomunitario no puede ser mayor de 17 caracteres. [{0} - {1}]"
				,declared.getOperatorNif(),declared.getName()));
			
		}
		declared.setFirstQuarterAmount( AonMathUtils.round(declared.getFirstQuarterAmount()));
		declared.setSecondQuarterAmount( AonMathUtils.round(declared.getSecondQuarterAmount()));
		declared.setThirdQuarterAmount( AonMathUtils.round(declared.getThirdQuarterAmount()));
		declared.setFourthQuarterAmount( AonMathUtils.round(declared.getFourthQuarterAmount()));
		declared.setAmount( AonMathUtils.round(declared.getAmount()));
		
		double quarters = (declared.getFirstQuarterAmount() + declared.getSecondQuarterAmount() + declared.getThirdQuarterAmount() + declared.getFourthQuarterAmount());
		if (AonMathUtils.isNotZero(quarters)) {
			if (AonMathUtils.isNotZero(declared.getAmount() - quarters)) {
				if ( AonMathUtils.isNotZero(declared.getFourthQuarterAmount())) {
					declared.setFourthQuarterAmount( AonMathUtils.round(declared.getAmount()
							- declared.getFirstQuarterAmount() - declared.getSecondQuarterAmount() - declared.getThirdQuarterAmount()));			
				} else if ( AonMathUtils.isNotZero(declared.getThirdQuarterAmount())) {
					declared.setThirdQuarterAmount( AonMathUtils.round(declared.getAmount()
							- declared.getFirstQuarterAmount() - declared.getSecondQuarterAmount() - declared.getFourthQuarterAmount()));			
				} else if ( AonMathUtils.isNotZero(declared.getSecondQuarterAmount())) {
					declared.setSecondQuarterAmount( AonMathUtils.round(declared.getAmount()
							- declared.getFirstQuarterAmount() - declared.getThirdQuarterAmount() - declared.getFourthQuarterAmount()));			
				} else if ( AonMathUtils.isNotZero(declared.getFirstQuarterAmount())) {
					declared.setFirstQuarterAmount( AonMathUtils.round(declared.getAmount()
							- declared.getSecondQuarterAmount() - declared.getThirdQuarterAmount() - declared.getFourthQuarterAmount()));			
				}
			}
		}
	}
		
	private static void deleteDeclared(AONContext ctx, Mod347Declared declared) {
		ctx.getDslContext().delete(FS_MOD347_DETAIL)
			.where(FS_MOD347_DETAIL.ID.equal(declared.getId()))
			.execute();		
	}
	
	// -------------------- MOD347ASSET --------------------
	
	private static LinkedList<Mod347Asset> getAssets(AONContext ctx, int mod347) {
		ctx.checkRead();
		return ctx.getDslContext()
			.selectFrom(FS_MOD347_DETAIL)
			.where(FS_MOD347_DETAIL.FS_MOD347.equal(mod347))
			.and(FS_MOD347_DETAIL.SHEET.eq("I"))
			.fetch()
			.stream()
			.map( new Mod347AssetFiller() )
			.collect(Collectors.toCollection(LinkedList::new));
	}
	
	private static class Mod347AssetFiller implements Function<Record, Mod347Asset> {
		
		@Override
		public Mod347Asset apply(Record record) {
			return new Mod347Asset()					
				.setId(record.getValue(FS_MOD347_DETAIL.ID))				
				.setDocument(record.getValue(FS_MOD347_DETAIL.DOCUMENT))
				.setRepresentativeDocument(record.getValue(FS_MOD347_DETAIL.REPRESENTATIVE_DOCUMENT))
				.setRegistry(record.getValue(FS_MOD347_DETAIL.REGISTRY))
				.setName(record.getValue(FS_MOD347_DETAIL.NAME))
				.setAmount(record.getValue(FS_MOD347_DETAIL.AMOUNT))
				.setAssetLocation(record.getValue(FS_MOD347_DETAIL.ASSET_LOCATION))
				.setCadasdralReference(record.getValue(FS_MOD347_DETAIL.CADASDRAL_REFERENCE))
				.setAssetStreetType(record.getValue(FS_MOD347_DETAIL.ASSET_STREET_TYPE))
				.setAssetStreet(record.getValue(FS_MOD347_DETAIL.ASSET_STREET))
				.setAssetStreetNumberType(record.getValue(FS_MOD347_DETAIL.ASSET_STREET_NUMBER_TYPE))
				.setAssetStreetNumber(record.getValue(FS_MOD347_DETAIL.ASSET_STREET_NUMBER))
				.setAssetStreetNumberSuffix(record.getValue(FS_MOD347_DETAIL.ASSET_STREET_NUMBER_SUFFIX))
				.setAssetStreetBlock(record.getValue(FS_MOD347_DETAIL.ASSET_STREET_BLOCK))
				.setAssetStreetHall(record.getValue(FS_MOD347_DETAIL.ASSET_STREET_HALL))
				.setAssetStreetStair(record.getValue(FS_MOD347_DETAIL.ASSET_STREET_STAIR))
				.setAssetStreetFloor(record.getValue(FS_MOD347_DETAIL.ASSET_STREET_FLOOR))
				.setAssetStreetDoor(record.getValue(FS_MOD347_DETAIL.ASSET_STREET_DOOR))
				.setAssetStreetComplement(record.getValue(FS_MOD347_DETAIL.ASSET_STREET_COMPLEMENT))
				.setAssetStreetCity(record.getValue(FS_MOD347_DETAIL.ASSET_STREET_CITY))
				.setAssetStreetTown(record.getValue(FS_MOD347_DETAIL.ASSET_STREET_TOWN))
				.setAssetStreetTownCode(record.getValue(FS_MOD347_DETAIL.ASSET_STREET_TOWN_CODE))
				.setAssetStreetProvince(record.getValue(FS_MOD347_DETAIL.ASSET_STREET_PROVINCE))
				.setAssetStreetZip(record.getValue(FS_MOD347_DETAIL.ASSET_STREET_ZIP));
		}
	}
	
	private static void saveAsset(AONContext ctx, Mod347 mod347, Mod347Asset asset){
		ctx.checkWrite();
		if (asset.getId() == null) {
			if (!asset.isDeleted()) {
				asset.setDomain(mod347.getDomain());
				asset.setMod347(mod347.getId());
				insertAsset(ctx, asset);
			}
		} else {
			if (asset.isDeleted()) {
				deleteAsset(ctx, asset);
			} else {
				updateAsset(ctx, asset);
			}
		}
	}
	
	private static void insertAsset(AONContext ctx, Mod347Asset asset) {
		validateAsset(ctx, asset);		
		ctx.getDslContext().insertInto(FS_MOD347_DETAIL)
			.set(FS_MOD347_DETAIL.DOMAIN,asset.getDomain())
			.set(FS_MOD347_DETAIL.FS_MOD347,asset.getMod347())
			.set(FS_MOD347_DETAIL.SHEET, "I")
			.set(FS_MOD347_DETAIL.TYPE, (String)null)
			.set(FS_MOD347_DETAIL.DOCUMENT, asset.getDocument())
			.set(FS_MOD347_DETAIL.REPRESENTATIVE_DOCUMENT, asset.getRepresentativeDocument())
			.set(FS_MOD347_DETAIL.REGISTRY, asset.getRegistry())
			.set(FS_MOD347_DETAIL.NAME, asset.getName())
			.set(FS_MOD347_DETAIL.AMOUNT, asset.getAmount())
			.set(FS_MOD347_DETAIL.ASSET_LOCATION,asset.getAssetLocation())
			.set(FS_MOD347_DETAIL.CADASDRAL_REFERENCE,asset.getCadasdralReference())
			.set(FS_MOD347_DETAIL.ASSET_STREET_TYPE,asset.getAssetStreetType())
			.set(FS_MOD347_DETAIL.ASSET_STREET,asset.getAssetStreet())
			.set(FS_MOD347_DETAIL.ASSET_STREET_NUMBER_TYPE,asset.getAssetStreetNumberType())
			.set(FS_MOD347_DETAIL.ASSET_STREET_NUMBER,asset.getAssetStreetNumber())
			.set(FS_MOD347_DETAIL.ASSET_STREET_NUMBER_SUFFIX,asset.getAssetStreetNumberSuffix())
			.set(FS_MOD347_DETAIL.ASSET_STREET_BLOCK,asset.getAssetStreetBlock())
			.set(FS_MOD347_DETAIL.ASSET_STREET_HALL,asset.getAssetStreetHall())
			.set(FS_MOD347_DETAIL.ASSET_STREET_STAIR,asset.getAssetStreetStair())
			.set(FS_MOD347_DETAIL.ASSET_STREET_FLOOR,asset.getAssetStreetFloor())
			.set(FS_MOD347_DETAIL.ASSET_STREET_DOOR,asset.getAssetStreetDoor())
			.set(FS_MOD347_DETAIL.ASSET_STREET_COMPLEMENT,asset.getAssetStreetComplement())
			.set(FS_MOD347_DETAIL.ASSET_STREET_CITY,asset.getAssetStreetCity())
			.set(FS_MOD347_DETAIL.ASSET_STREET_TOWN,asset.getAssetStreetTown())
			.set(FS_MOD347_DETAIL.ASSET_STREET_TOWN_CODE,asset.getAssetStreetTownCode())
			.set(FS_MOD347_DETAIL.ASSET_STREET_PROVINCE,asset.getAssetStreetProvince())
			.set(FS_MOD347_DETAIL.ASSET_STREET_ZIP,asset.getAssetStreetZip())
			.execute();
	}

	private static void updateAsset(AONContext ctx, Mod347Asset asset) {
		validateAsset(ctx, asset);
		ctx.getDslContext().update(FS_MOD347_DETAIL)
			.set(FS_MOD347_DETAIL.DOCUMENT, asset.getDocument())
			.set(FS_MOD347_DETAIL.REPRESENTATIVE_DOCUMENT, asset.getRepresentativeDocument())
			.set(FS_MOD347_DETAIL.REGISTRY, asset.getRegistry())
			.set(FS_MOD347_DETAIL.NAME, asset.getName())
			.set(FS_MOD347_DETAIL.AMOUNT, asset.getAmount())
			.set(FS_MOD347_DETAIL.ASSET_LOCATION,asset.getAssetLocation())
			.set(FS_MOD347_DETAIL.CADASDRAL_REFERENCE,asset.getCadasdralReference())
			.set(FS_MOD347_DETAIL.ASSET_STREET_TYPE,asset.getAssetStreetType())
			.set(FS_MOD347_DETAIL.ASSET_STREET,asset.getAssetStreet())
			.set(FS_MOD347_DETAIL.ASSET_STREET_NUMBER_TYPE,asset.getAssetStreetNumberType())
			.set(FS_MOD347_DETAIL.ASSET_STREET_NUMBER,asset.getAssetStreetNumber())
			.set(FS_MOD347_DETAIL.ASSET_STREET_NUMBER_SUFFIX,asset.getAssetStreetNumberSuffix())
			.set(FS_MOD347_DETAIL.ASSET_STREET_BLOCK,asset.getAssetStreetBlock())
			.set(FS_MOD347_DETAIL.ASSET_STREET_HALL,asset.getAssetStreetHall())
			.set(FS_MOD347_DETAIL.ASSET_STREET_STAIR,asset.getAssetStreetStair())
			.set(FS_MOD347_DETAIL.ASSET_STREET_FLOOR,asset.getAssetStreetFloor())
			.set(FS_MOD347_DETAIL.ASSET_STREET_DOOR,asset.getAssetStreetDoor())
			.set(FS_MOD347_DETAIL.ASSET_STREET_COMPLEMENT,asset.getAssetStreetComplement())
			.set(FS_MOD347_DETAIL.ASSET_STREET_CITY,asset.getAssetStreetCity())
			.set(FS_MOD347_DETAIL.ASSET_STREET_TOWN,asset.getAssetStreetTown())
			.set(FS_MOD347_DETAIL.ASSET_STREET_TOWN_CODE,asset.getAssetStreetTownCode())
			.set(FS_MOD347_DETAIL.ASSET_STREET_PROVINCE,asset.getAssetStreetProvince())
			.set(FS_MOD347_DETAIL.ASSET_STREET_ZIP,asset.getAssetStreetZip())
			.where(FS_MOD347_DETAIL.ID.equal(asset.getId()))
			.execute();
	}
	
	private static void validateAsset(AONContext ctx, Mod347Asset asset) {
		
		// Comprobaciones que deban realizarse para las lineas asset
		
	}

	private static void deleteAsset(AONContext ctx, Mod347Asset asset) {
		ctx.getDslContext().delete(FS_MOD347_DETAIL)
			.where(FS_MOD347_DETAIL.ID.equal(asset.getId()))
			.execute();		
	}
	
	// --------------- INSERT DETAILS FROM INVOICE ---------------
	
	private static void insertDetailsFromInvoice(AONContext ctx , final Mod347 mod347) {
		
		// PROCEDIMIENTO A SEGUIR:
		// - Se leen las facturas, según los filtros, del ejercicio actual y del anterior (para las facturas RECC)
		// - Se asigna a todas las compras y gastos, el tipo "0" y a las ventas el "1"
		// - Se guardan en un mapa agrupandolas por "Documento + Tipo + ISP + RECC"
		// - Si la factura es RECC se acumula el importe total de la factura, si es del ejercicio actual y 
		//   además se acumula tambien el importe declarado según la regla RECC del IVA
		// - Se van leyendo y por cada "Documento + Tipo + ISP + RECC", se va creando una linea de 
		//   declarado (si el total de operaciones de "Documento + Tipo" supera el valor mínimo)
		
		// Se pone solo el ejercicio actual, porque getVatBreakdown ya lee automaticamente las facturas RECC del ejercicio anterior
		Date fromDate = AonDateUtils.getYearFirstDay(mod347.getYear());
		Date toDate = AonDateUtils.getYearLastDay(mod347.getYear());
		
		Calendar cal = Calendar.getInstance();

		// Mapa que guardará los datos agrupando por "Documento + Tipo + ISP + RECC"
		Map<String,Mod347Declared> mapResult = new TreeMap<String, Mod347Declared>();
		
		// Obtenemos el desglose de facturas del ejercicio actual y el anterior (facturas RECC), usando VATDAO
		// Solo facturas Nacionales o ISP o Intracomunitarias (con y sin retencion) o Servicios Extracomunitarios o Servicios Canarias Ceuta o Melilla, que cumplan los filtros 
		getInvoiceBreakdown(ctx, fromDate, toDate, mod347)
				.filter(vat -> (vat.getTransaction() == InvoiceTransactionType.NATIONAL || 
				                vat.getTransaction() == InvoiceTransactionType.OTHER_ISP || 
				                vat.getTransaction() == InvoiceTransactionType.INTRACOMMUNITY || 
				                (vat.getTransaction() == InvoiceTransactionType.EXTRACOMMUNITY && vat.isService()) ||
				                (vat.getTransaction() == InvoiceTransactionType.CAN_CEU_MEL && vat.isService()) )) 
				.peek( vat -> {					
					// Las compras y gastos, se ponen todas como compras
					vat.setInvoiceType( vat.getInvoiceType() == InvoiceType.SALES ? InvoiceType.SALES : InvoiceType.PURCHASE);
					// Ventas se ponen como Nacionales, por que no hay que marcar ISP en las ventas en el 347, solo en las compras
					if (vat.getInvoiceType() == InvoiceType.SALES) {
						vat.setTransaction( InvoiceTransactionType.NATIONAL);
					}
					// Compras extracomunitarias de servicios se tratan como si fueran ISP
					if (vat.getInvoiceType() == InvoiceType.PURCHASE && vat.getTransaction() == InvoiceTransactionType.EXTRACOMMUNITY && vat.isService()) {
						vat.setTransaction( InvoiceTransactionType.OTHER_ISP );
					}
				})
				.forEach( vat -> {
						// Añadir la factura al registro que corresponda del declarado
						// Dado que es necesario separar las operaciones normales de las ISP y de las RECC, se usa como clave esos dos datos
						// además del NIF y el tipo, para posteriormente crear tantos registros como sea necesario en las lineas del 347						
						String document = AonStringUtils.trimToEmpty(vat.getRegistryDocument());
						String name = AonStringUtils.trimToEmpty(vat.getRegistryName());
						String c = document + ";" + vat.getInvoiceType() + ";" + vat.getTransaction() + ";" + vat.isVatAccrualRegime();						
						Mod347Declared declared = mapResult.get(c);
						if (declared == null) {
							
							declared = new Mod347Declared();
							declared.setDomain(mod347.getDomain());
							declared.setMod347(mod347.getId());
							
							Country country = vat.getRegistryDocumentCountry();
							if (country == null || country == Country.ES) {
	
								declared.setDocument(document);
									
								// La provincia no la tengo en VATContext, se obtiene de RADRESS de la dirección principal 
								declared.setProvince(Province.safeValueOf(
									RegistryAddressDAO.getMainAddressProvince(ctx, vat.getRegistry()))
								);
	
							} else {
								
								declared.setOperatorNif(AonStringUtils.substring((country.getIso2() + document), 0, 17));
								declared.setCountry(country);
								declared.setProvince(Province.NO_RESIDENTE);								
								
							}	
							
							if (AonStringUtils.length(name) > 64) {
								name = AonStringUtils.substring(name, 0, 63);
							}
							declared.setName(name);
	
							declared.setType(vat.getInvoiceType() == InvoiceType.SALES ? Mod347Key.B : Mod347Key.A);
	
							declared.setVatAccrual(vat.isVatAccrualRegime());
							declared.setIsp( vat.getTransaction() == InvoiceTransactionType.OTHER_ISP );
						
							declared.setFirstQuarterAmount(0.0);
							declared.setSecondQuarterAmount(0.0);
							declared.setThirdQuarterAmount(0.0);
							declared.setFourthQuarterAmount(0.0);
							declared.setAmount(0.0);
							declared.setVatAccrualAmount(0.0);
							
							// Canarias: Inicializar importes trimestrales arrendamientos
							if (mod347.isCanarias()) {
								declared.setFirstQuarterRentalAmount(0.0);
								declared.setSecondQuarterRentalAmount(0.0);
								declared.setThirdQuarterRentalAmount(0.0);
								declared.setFourthQuarterRentalAmount(0.0);
								declared.setRentalAmount(0.0);								
							}
							
							// Añadir el declarado al map
							mapResult.put(c, declared);
						}
	
						// Acumular el importe que se declara en el 347
						double amount = vat.getAmount347();
						
						// Arrendamientos en Canarias (van en campos separados)
						boolean isCanariasRental = mod347.isCanarias() && vat.hasRetention() && vat.getWithholdingType() == WithholdingType.RENTING;
	
						if (vat.isVatAccrualRegime()) {
							
							// Factura Criterio de Caja
												
							// Acumular el importe según RECC (La base y las cuotas tienen lo declarado según los cobros/pagos realizados)
							if (!vat.isFinancePending()) {
								declared.setVatAccrualAmount(AonMathUtils.round(declared.getVatAccrualAmount() + vat.getBase() + vat.getQuota() + vat.getSurchargeQuota()));
							}
							
							// Si la factura es del ejercicio anterior, no se tiene en cuenta el importe, para el minimo a declarar
							// ni aparece el importe en el 347				
							if (vat.getTaxDate().compareTo(AonDateUtils.getYearFirstDay(mod347.getYear())) < 0) {
								amount = 0;						
							}
							
						} else if (mod347.getDocument() != null && !mod347.getDocument().startsWith("H")) {
	
							// No es factura RECC, ni NIF declarante empieza por "H", se acumula por trimestres
							
							cal.setTime(vat.getTaxDate());
							int quarter = (cal.get(Calendar.MONTH) / 3);
							if (quarter == 0) {
								if (isCanariasRental) {
									declared.setFirstQuarterRentalAmount(declared.getFirstQuarterRentalAmount() + amount);
								} else {
									declared.setFirstQuarterAmount(declared.getFirstQuarterAmount() + amount);
								}
							} else if (quarter == 1) {
								if (isCanariasRental) {
									declared.setSecondQuarterRentalAmount(declared.getSecondQuarterRentalAmount() + amount);
								} else {
									declared.setSecondQuarterAmount(declared.getSecondQuarterAmount() + amount);
								}
							} else if (quarter == 2) {
								if (isCanariasRental) {
									declared.setThirdQuarterRentalAmount(declared.getThirdQuarterRentalAmount() + amount);
								} else {
									declared.setThirdQuarterAmount(declared.getThirdQuarterAmount() + amount);
								}
							} else if (quarter == 3) {
								if (isCanariasRental) {
									declared.setFourthQuarterRentalAmount(declared.getFourthQuarterRentalAmount() + amount);
								} else {
									declared.setFourthQuarterAmount(declared.getFourthQuarterAmount() + amount);
								}
							}
							
						}
						
						// Acumular el total
						if (isCanariasRental) {
							declared.setRentalAmount(declared.getRentalAmount() + amount);
						} else {
							declared.setAmount(declared.getAmount() + amount);
						}
				});
		
		String control = "";
		double acumulated = 0;
		double minAmount = 3005.06;		
		Map<String,Mod347Declared> map = new TreeMap<String, Mod347Declared>();
		
		for (Mod347Declared dec : mapResult.values()) {
			// El importe mínimo a declarar se controla por NIF y Tipo (Ventas o Compras)
			String document = AonStringUtils.isNotBlank(dec.getOperatorNif())?dec.getOperatorNif():dec.getDocument(); 
			String c = document + ";" + dec.getType();
			if (!control.equals(c)) {

				// Añadir el bloque a la base de datos, si supera el importe minimo
				if (Math.abs(acumulated) > minAmount) {
					for (Mod347Declared declared : map.values()) {
						insertDeclared(ctx, declared);
					}
				}

				control = c;
				acumulated = 0;
				map.clear();
			}
						
			// Añadir la factura al registro que corresponda del bloque actual
			// Dado que es necesario separar las operaciones normales de las ISP y de las RECC, se usa como clave esos dos datos
			// además del NIF y el tipo, para posteriormente crear tantos registros como sea necesario en las lineas del 347
			c = document + ";" + dec.getType() + ";" + dec.isIsp() + ";" + dec.isVatAccrual();			
			map.put(c, dec);
									
			// Acumular el importe para ver si al final supera el minimo a declarar (por NIF + Tipo)
			acumulated = acumulated + dec.getAmount();

		}

		// Añadir ultimo bloque de map, si existe
		if (Math.abs(acumulated) > minAmount) {
			for (Mod347Declared declared : map.values()) {
				insertDeclared(ctx, declared);
			}
		}		
			
	}
	
	protected static Stream<VatContext> getInvoiceBreakdown(AONContext ctx, Date fromDate, Date toDate,Mod347 mod347) {
		return Stream.concat(
				 VATDAO.getVatBreakdown(ctx, mod347)
				,VATDAO.getPeriodCritCajaVatBreakdown(ctx, mod347)
			)			
			.filter(vat ->  !(mod347.isExcludeOutputNationalZero() && vat.isSales() && vat.getTransaction() == InvoiceTransactionType.NATIONAL && AonMathUtils.isZero(vat.getPercentage())) )				
			.filter(vat ->  !(mod347.isExcludeInputNationalZero() && !vat.isSales() && vat.getTransaction() == InvoiceTransactionType.NATIONAL && AonMathUtils.isZero(vat.getPercentage())) )
			.filter(vat ->  !(mod347.isExcludeRetention() && vat.hasRetention()))
			.filter(vat ->  !(mod347.isExcludeIntracommunity() && vat.getTransaction() == InvoiceTransactionType.INTRACOMMUNITY));
		
	}
	
	public static Mod347 duplicate(AONContext ctx, Mod347 mod347) {
		
		int id = mod347.getId();
		mod347.setId(null);
		mod347.setStatus(FiscalStatus.PENDING);
		mod347.setNumber(null);
		mod347 = insert(ctx, mod347, false);
		
		if (!mod347.isComplementary()) {
			Mod347 original = getById(ctx, id);
			for (Mod347Declared declared : original.getDeclared()) {
				declared.setId(null);
				declared.setMod347(mod347.getId());
				mod347.getDeclared().add(declared);
			}
			for (Mod347Asset asset : original.getAssets()) {
				asset.setId(null);
				asset.setMod347(mod347.getId());
				mod347.getAssets().add(asset);
			}
		}
		return save(ctx, mod347);		
	
	}
	
	public static Mod347 reset(AONContext ctx, Mod347 mod347) {
		deleteDetails(ctx, mod347);		
		insertDetailsFromInvoice(ctx, mod347);
		return getById(ctx, mod347.getId()) ;
	}
	
    private static final char CSV_DELIMITER = ',';
    private static final char CSV_QUOTE = '"';
    private static final String CSV_QUOTE_STR = String.valueOf(CSV_QUOTE);
    private static final char[] CSV_SEARCH_CHARS = new char[] {CSV_DELIMITER, CSV_QUOTE, '\r', '\n'};
    
    private static void write(final Writer out, final CharSequence input) throws IOException {
        if (AonStringUtils.containsNone(input.toString(), CSV_SEARCH_CHARS)) {
            out.write(input.toString());
        } else {
            out.write(CSV_QUOTE);
            out.write(AonStringUtils.replace(input.toString(), CSV_QUOTE_STR, CSV_QUOTE_STR + CSV_QUOTE_STR));
            out.write(CSV_QUOTE);
        }
    }
    
	public static void writeMailMergeReport(AONContext ctx, Mod347 mod347, Writer wr) {
		try {
			String[] headers = new String[] { "id", "tipo", "documento", "razon_social", "importe_trimestre_1",
					"importe_trimestre_2", "importe_trimestre_3", "importe_trimestre_4", "importe_anual", "email",
					"direccion_tipo", "direccion", "direccion_numero", "direccion_complemento1",
					"direccion_complemento2", "direccion_codigo_postal", "direccion_ciudad", "direccion_provincia" };
			for (String header : headers) {
				wr.append(header);
				wr.append(CSV_DELIMITER);
			}
			wr.append(System.lineSeparator());
			RegistryFull<?> registry = null;
			String type = null;
			for (Mod347Declared declared : mod347.getDeclared()) {
				if (Mod347Key.B == declared.getType()) {
					registry = CustomerDAO.getStream(ctx, 
							p -> p.getDocumentProperty().eq(declared.getDocument())
							.and(p.getDomainProperty().eq(mod347.getDomain())))
						.map(cust -> cust.getId())
						.map(id -> CustomerDAO.getFull(ctx, id))
						.findFirst()
						.orElse(null);
					type = "Cliente";
				} else if (Mod347Key.A == declared.getType()) {
					registry = CreditorDAO.getStream(ctx, 
							p -> p.getDocumentProperty().eq(declared.getDocument())
							.and(p.getDomainProperty().eq(mod347.getDomain())))
						.map(cred -> cred.getId())
						.map(id -> CreditorDAO.getFull(ctx, id))
						.findFirst()
						.orElse(null);
					if ( registry != null ) {
						type = "Acreedor";
					} else {
						registry = SupplierDAO.getStream(ctx, 
								p -> p.getDocumentProperty().eq(declared.getDocument())
								.and(p.getDomainProperty().eq(mod347.getDomain())))
							.map(supp -> supp.getId())
							.map(id -> SupplierDAO.getFull(ctx, id))
							.findFirst()
							.orElse(null);
						type = "Proveedor";	
					}
				}
				if ( registry != null ) {
					RegistryAddress address = registry.getMainAddress();
					List<RegistryMedia> emails = registry.getEmailMedias();
					RegistryMedia email = (emails != null && !emails.isEmpty())
							?emails.get(0)
							:null;
					wr.append( AonNumberUtils.toString( registry.getId() ));
					wr.append(CSV_DELIMITER);
					write(wr,type);
					wr.append(CSV_DELIMITER);
					write(wr,AonStringUtils.defaultString(declared.getDocument()));
					wr.append(CSV_DELIMITER);
					write(wr,declared.getName());
					wr.append(CSV_DELIMITER);
					wr.append( AonNumberUtils.toString( declared.getFirstQuarterAmount() ));
					wr.append(CSV_DELIMITER);
					wr.append( AonNumberUtils.toString( declared.getSecondQuarterAmount() ));
					wr.append(CSV_DELIMITER);
					wr.append( AonNumberUtils.toString( declared.getThirdQuarterAmount() ));
					wr.append(CSV_DELIMITER);
					wr.append( AonNumberUtils.toString( declared.getFourthQuarterAmount() ));
					wr.append(CSV_DELIMITER);
					wr.append( AonNumberUtils.toString( declared.getAmount() ));
					wr.append(CSV_DELIMITER);
					write(wr,AonStringUtils.defaultString(email == null?null:email.getValue()));
					wr.append(CSV_DELIMITER);
					write(wr,AonStringUtils.defaultString((address == null || address.getStreetType()==null)?null:address.getStreetType().getDescription()));
					wr.append(CSV_DELIMITER);
					write(wr,AonStringUtils.defaultString(address == null?null:address.getAddress()));
					wr.append(CSV_DELIMITER);
					write(wr,AonStringUtils.defaultString(address == null?null:address.getNumber()));
					wr.append(CSV_DELIMITER);
					write(wr,AonStringUtils.defaultString(address == null?null:address.getAddress2()));
					wr.append(CSV_DELIMITER);
					write(wr,AonStringUtils.defaultString(address == null?null:address.getAddress3()));
					wr.append(CSV_DELIMITER);
					write(wr,AonStringUtils.defaultString(address == null?null:address.getZip()));
					wr.append(CSV_DELIMITER);
					write(wr,AonStringUtils.defaultString(address == null?null:address.getCity()));
					wr.append(CSV_DELIMITER);
					write(wr,AonStringUtils.defaultString(address == null?null:address.getGeozoneName()));
					wr.append(System.lineSeparator());
				}
			}
			
		} catch (IOException e) {
			throw new AonCoreException(e);
		}
	}
	
    // Grabar resultado y pdf en response y marcar el modelo como enviado
	public static Mod347 aeatPresentation(AONContext ctx, Mod347 mod, String aeatResponse) {
		if (AonStringUtils.isNotBlank(aeatResponse)) {			
			
			// Antes de nada se borra la presentación anterior
			DataResponseDAO.deleteAEATResponse(ctx, mod);			
			
			// Grabar los datos en data_response y sus tablas asociadas
			DataResponseDAO.insertAEATResponse(ctx, mod, aeatResponse);
			
			// Marcar el modelo como enviado					
			if (mod != null && mod.getId() != null) {
				ctx.getDslContext().update(FS_MOD347)					
					.set(FS_MOD347.STATUS, FiscalStatus.SENT.value())
					.where(FS_MOD347.ID.equal(mod.getId()))
					.execute();
				return getById(ctx, mod.getId());
			}
		}
		return mod;
	}	
	
}
