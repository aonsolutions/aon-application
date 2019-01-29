package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.FsMod347.FS_MOD347;
import static com.esferalia.aon.jooq.tables.FsMod347Detail.FS_MOD347_DETAIL;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Record;
import org.jooq.exception.DataAccessException;

import com.esferalia.aon.jooq.tables.records.FsMod347Record;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelUtils;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod347;
import com.esferalia.aon.occam.api.model.fiscal.Mod347Asset;
import com.esferalia.aon.occam.api.model.fiscal.Mod347Declared;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.Mod347Key;
import com.esferalia.aon.occam.api.model.type.Province;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod347DAO {
	
	private static byte ZERO_BYTE = 0;
	private static byte ONE_BYTE = 1;
	
	// -------------------- MOD347 --------------------
	
	public static LinkedList<Mod347> getByDomain(AONContext ctx, int domain) {
		ctx.checkRead();
		return  ctx.getDslContext()
			.select(FS_MOD347.fields())
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
			return new Mod347() 
				.setId(record.getValue(FS_MOD347.ID))
				.setDomain(record.getValue(FS_MOD347.DOMAIN))				
				.setYear(record.getValue(FS_MOD347.YEAR))
				.setAdministration(Administration.safeValueOf(record.getValue(FS_MOD347.ADMINISTRATION)))
				.setComments(record.getValue(FS_MOD347.COMMENTS))				
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
				;
		}
		
	}
	
	public static Mod347 initialize(AONContext ctx) {	
		
		// Ponemos por defecto el año, según la fecha actual, si estamos en enero o febrero ponemos
		// el año anterior (se supone que queremos hacer el del ultimo periodo del año anterior)
		// en caso contrario ponemos el año actual
		Date today = new Date();
		int year = AonDateUtils.getYear(today);		
		if (AonDateUtils.getMonth(today) == 0 || AonDateUtils.getMonth(today) == 1) {
			year = year - 1;			
		}
		
		FiscalParameters params = AppParamDAO.getFiscalParameters(ctx);		
		
		Mod347 mod347 = new Mod347();
		mod347.setDomain(ctx.getDomainId());
		mod347.setYear(year);		
		mod347.setAdministration(params.getAdministration(Administration.COMMON_TERRITORY));
		mod347.setNumber("3470000000001");
		mod347.setDocument(params.getDocument());
		mod347.setName(AonStringUtils.left(params.getName(), FS_MOD347.NAME.getDataType().length()));
		mod347.setContactPhone(AonStringUtils.left(params.getContactPhone(), FS_MOD347.CONTACT_PHONE.getDataType().length()));
		mod347.setContactPerson(AonStringUtils.left(params.getContactPerson(), FS_MOD347.CONTACT_PERSON.getDataType().length()));
		mod347.setContactMail(AonStringUtils.left(params.getContactMail(), FS_MOD347.CONTACT_MAIL.getDataType().length()));
		mod347.setStatus(FiscalStatus.PENDING);
		mod347.setDeclared(new LinkedList<Mod347Declared>());
		mod347.setAssets(new LinkedList<Mod347Asset>());
		return mod347;
	}
	
	public static Mod347 saveComments(AONContext ctx, Mod347 fm) {
		try {
			ctx.checkWrite();
			if (fm.getId() != null) {
				ctx.getDslContext().update(FS_MOD347)
					.set(FS_MOD347.COMMENTS, fm.getComments())
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
	
	public static Mod347 changeStatusMod347(AONContext ctx, Mod347 mod347, FiscalStatus newStatus) {
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
	}

	private static Mod347 insert(AONContext ctx, Mod347 mod347) {
		return insert(ctx,mod347,true);
	}
	
	private static Mod347 insert(AONContext ctx, Mod347 mod347, boolean generateDetails) {
		validate(ctx,mod347);
		FsMod347Record record = ctx.getDslContext().insertInto(FS_MOD347)
			.set(FS_MOD347.DOMAIN,mod347.getDomain())
			.set(FS_MOD347.YEAR,mod347.getYear())
			.set(FS_MOD347.ADMINISTRATION, mod347.getAdministration().getValue())
			.set(FS_MOD347.COMMENTS,mod347.getComments())
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
		ctx.getDslContext().update(FS_MOD347)			
			.set(FS_MOD347.YEAR,mod347.getYear())
			.set(FS_MOD347.ADMINISTRATION, mod347.getAdministration().getValue())
			.set(FS_MOD347.COMMENTS,mod347.getComments())
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
		
		// Se comprueba que no exista otra declaración sustitutiva que sustituya a la misma anterior
		if (mod347.isReplacement()) {
						
			if (ctx.getDslContext().selectOne()
				.from(FS_MOD347)
				.where(FS_MOD347.DOMAIN.equal(mod347.getDomain())
						.and(FS_MOD347.YEAR.equal(mod347.getYear()))
						.and(FS_MOD347.ADMINISTRATION.equal(mod347.getAdministration().getValue()))
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
						.and(FS_MOD347.ADMINISTRATION.equal((byte) mod347.getAdministration().getValue()))
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
				.setVatAccrualAmount(record.getValue(FS_MOD347_DETAIL.VAT_ACCRUAL_AMOUNT));
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
			.where(FS_MOD347_DETAIL.ID.equal(declared.getId()))
			.execute();
	}
	
	private static void validateDeclared(AONContext ctx, Mod347Declared declared) {
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
					declared.setThirdQuarterAmount( AonMathUtils.round(declared.getAmount()
							- declared.getFirstQuarterAmount() - declared.getThirdQuarterAmount() - declared.getFourthQuarterAmount()));			
				} else if ( AonMathUtils.isNotZero(declared.getFirstQuarterAmount())) {
					declared.setThirdQuarterAmount( AonMathUtils.round(declared.getAmount()
							- declared.getSecondQuarterAmount() - declared.getThirdQuarterAmount() - declared.getFourthQuarterAmount()));			
				}
			}
		}
		 
		quarters = (declared.getFirstQuarterAmount() + declared.getSecondQuarterAmount() + declared.getThirdQuarterAmount() + declared.getFourthQuarterAmount());
		if (AonMathUtils.isNotZero(quarters)) {
			if (AonMathUtils.isNotZero(declared.getAmount() - quarters)) {
				System.out.println("ERROR");
			}
		}
		// Comprobaciones que deban realizarse para las lineas declared
		
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
		// - Se leen las facturas normales o ISP, que no lleven retencion, del ejercicio actual y del anterior (para las facturas RECC)
		// - Se asigna a todas las compras y gastos, el tipo "0" y a las ventas el "1"
		// - Se guardan en un mapa agrupandolas por Documento + Tipo + ISP + RECC
		// - Si la factura es RECC se acumula el importe total de la factura, si es del ejercicio actual y 
		//   además se acumula tambien el importe declarado según la regla RECC del IVA
		// - Se van leyendo y por cada Documento + Tipo + ISP + RECC, se va creando una linea de 
		//   declarado (si el total de operaciones de Documento + Tipo supera el valor mínimo)
		
		// Se pone solo el ejercicio actual, porque getVatBreakdown ya lee automaticamente las facturas RECC del ejercicio anterior
		Date fromDate = AonDateUtils.getYearFirstDay(mod347.getYear());
		Date toDate = AonDateUtils.getYearLastDay(mod347.getYear());
		
		Calendar cal = Calendar.getInstance();

		// Mapa que guardará los datos agrupando por Documento + Tipo + ISP + RECC
		Map<String,Mod347Declared> mapResult = new TreeMap<String, Mod347Declared>();
		
		// Obtenemos el desglose de facturas del ejercicio actual y el anterior (facturas RECC), usando VATDAO
		// Solo facturas Nacionales o ISP y sin retencion
		VATDAO.getVatBreakdown(ctx, fromDate, toDate, mod347)
				.filter(vat -> (!vat.hasRetention()) && (vat.getTransaction() == InvoiceTransactionType.NATIONAL || vat.getTransaction() == InvoiceTransactionType.OTHER_ISP))
				.peek( vat -> {					
					// Las compras y gastos, se ponen todas como compras
					vat.setInvoiceType( vat.getInvoiceType() == InvoiceType.SALES ? InvoiceType.SALES : InvoiceType.PURCHASE);
					// Ventas ISP, se ponen como Nacionales, por que no hay que marcar ISP en las ventas en el 347, solo en las compras
					if (vat.getInvoiceType() == InvoiceType.SALES && vat.getTransaction() == InvoiceTransactionType.OTHER_ISP)
						vat.setTransaction( InvoiceTransactionType.NATIONAL);
				})
				.forEach( vat -> {
					
					// Añadir la factura al registro que corresponda del declarado
					// Dado que es necesario separar las operaciones normales de las ISP y de las RECC, se usa como clave esos dos datos
					// además del NIF y el tipo, para posteriormente crear tantos registros como sea necesario en las lineas del 347
					String c = vat.getRegistryDocument() + ";" + vat.getInvoiceType() + ";" + vat.getTransaction() + ";" + vat.isVatAccrualRegime();						
					Mod347Declared declared = mapResult.get(c);
					if (declared == null) {
						
						declared = new Mod347Declared();
						declared.setDomain(mod347.getDomain());
						declared.setMod347(mod347.getId());

						String document = vat.getRegistryDocument();
						Country country = vat.getRegistryDocumentCountry();
						if (country == null || country == Country.ES) {

							if (AonStringUtils.length(document) > 9) {
								declared.setDocument(AonStringUtils.substring(document, 0, 9));
							} else {
								declared.setDocument(document);
							}

							// La provincia no la tengo en VATContext, se obtiene de RADRESS de la dirección principal 
							declared.setProvince(Province.safeValueOf(getRegistryMainAddressProvince(ctx, vat.getRegistry())));

						} else {

							declared.setOperatorNif(country.getIso2() + document);
							declared.setCountry(country);
							declared.setProvince(Province.NO_RESIDENTE);
							
						}

						String name = vat.getRegistryName();
						if (AonStringUtils.length(name) > 64) {
							name = AonStringUtils.substring(name, 0, 63);
						}
						declared.setName(name);

						declared.setType(vat.getInvoiceType() == InvoiceType.SALES ? Mod347Key.B : Mod347Key.A);

						declared.setVatAccrual(vat.isVatAccrualRegime());
						declared.setIsp(vat.getTransaction() == InvoiceTransactionType.OTHER_ISP);
					
						declared.setFirstQuarterAmount(0.0);
						declared.setSecondQuarterAmount(0.0);
						declared.setThirdQuarterAmount(0.0);
						declared.setFourthQuarterAmount(0.0);
						declared.setAmount(0.0);
						declared.setVatAccrualAmount(0.0);
						
						// Añadir el declarado al map
						mapResult.put(c, declared);
					}

					// Acumular el importe que se declara en el 347
					double amount = vat.getAmount347();

					if (vat.isVatAccrualRegime()) {
						
						// Factura Criterio de Caja
											
						// Acumular el importe según RECC (La base y las cuotas tienen lo declarado según los cobros/pagos realizados)
						declared.setVatAccrualAmount(AonMathUtils.round(declared.getVatAccrualAmount() + vat.getBase() + vat.getQuota() + vat.getSurchargeQuota()));
						
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
							declared.setFirstQuarterAmount(declared.getFirstQuarterAmount() + amount);
						} else if (quarter == 1) {
							declared.setSecondQuarterAmount(declared.getSecondQuarterAmount() + amount);
						} else if (quarter == 2) {
							declared.setThirdQuarterAmount(declared.getThirdQuarterAmount() + amount);
						} else if (quarter == 3) {
							declared.setFourthQuarterAmount(declared.getFourthQuarterAmount() + amount);
						}
						
					}
					
					// Acumular el total
					declared.setAmount(declared.getAmount() + amount);
					
				});
		
		String control = "";
		double acumulated = 0;
		double minAmount = 3005.06;		
		Map<String,Mod347Declared> map = new TreeMap<String, Mod347Declared>();
		
		for (Mod347Declared dec : mapResult.values()) {
					
			// El importe mínimo a declarar se controla por NIF y Tipo (Ventas o Compras)
			String c = dec.getDocument() + ";" + dec.getType();
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
			c = dec.getDocument() + ";" + dec.getType() + ";" + dec.isIsp() + ";" + dec.isVatAccrual();			
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
	
	private static Integer getRegistryMainAddressProvince(AONContext ctx, Integer registry) {
		
		// Obtenemos la provincia de la direccion principal de registry
		return ctx.getDslContext()
			.select(GEOZONE.CODE)
			.from(RADDRESS)
			.join(GEOZONE).on(RADDRESS.GEOZONE.equal(GEOZONE.ID))
			.where(RADDRESS.REGISTRY.equal(registry))
			.and(RADDRESS.TYPE.equal( ZERO_BYTE ))		// Dirección principal.
			.limit(1)
			.fetch()
			.stream()
			.mapToInt(rec -> {
				try {
					return Integer.parseInt(rec.getValue(GEOZONE.CODE) );	
				} catch (NumberFormatException e) {
					return 0;
				}
			})
			.findFirst()
			.orElse(0);
	}
	
	// --------------- INVOICES INFO --------------- 
	
	public static String getMod347Info(AONContext ctx, Mod347 mod347, Mod347Declared declared, FiscalModelKeyInfo infoKey) {
		
		String INFO_MSG = "<pre class='aon-fixed-font aon-font-medium aon-margin-bottom'>{0}<pre>";
		
		// Información Desglose de facturas
		if (infoKey == FiscalModelKeyInfo.INVOICE) {
			return MessageFormat.format(INFO_MSG, getInvoicesInfo(ctx, mod347, declared));			
		}
		
		return null;
	}
	
	private static String getInvoicesInfo(AONContext ctx, Mod347 mod347, Mod347Declared declared) {

		String title = "FACTURAS QUE AFECTAN A LA CONFECCI\u00D3N DEL MODELO " 
				+ FiscalModelUtils.getModelName(mod347)
				+ " DE " + mod347.getYear();
		
		String subtitle =  "Clave "+ (Mod347Key.safeValue(declared.getType()) == null ? "" : declared.getType().getValue()) +
				" - " + (AonStringUtils.isNotBlank(declared.getOperatorNif()) ? declared.getOperatorNif() : (declared.getDocument() == null ? "" : declared.getDocument())) +
				" - " + (declared.getName() == null ? "" : declared.getName());				
		
		return Mod347Formatter.formatInvoices347(title
				,subtitle
				,getVatBreakdown(ctx, mod347, declared).collect(Collectors.toCollection(LinkedList::new)),mod347.getYear(), declared.isVatAccrual());
		
	}
	
	private static Stream<VatContext> getVatBreakdown(final AONContext ctx, final Mod347 mod347, final Mod347Declared declared) {
		
		// Tipo de Facturas según la clave de la linea del modelo que se le pasa (se hace la operacion inversa que cuando se crea el modelo)
		final InvoiceType invoiceType1;
		final InvoiceType invoiceType2;
		
		// Tipo de transaccion según si está marcado o no ISP (solo compras)
		final InvoiceTransactionType invoiceTransaction1;
		final InvoiceTransactionType invoiceTransaction2;
		
		if (declared.getType() == Mod347Key.A) {       // Adquisiciones de bienes y servicios superiores a 3.005,06 euros (Compras y Gastos)
			invoiceType1 = InvoiceType.PURCHASE;
			invoiceType2 = InvoiceType.EXPENSES;			
			invoiceTransaction1 = declared.isIsp() ? InvoiceTransactionType.OTHER_ISP : InvoiceTransactionType.NATIONAL;
			invoiceTransaction2 = null;
	    }
	    else if (declared.getType() == Mod347Key.B) {  // Entregas de bienes y prestaciones de servicios superiores a 3.005,06 euros (Ventas)
	    	invoiceType1 = InvoiceType.SALES;
	    	invoiceType2 = null;
	    	invoiceTransaction1 = InvoiceTransactionType.NATIONAL;
	    	invoiceTransaction2 = InvoiceTransactionType.OTHER_ISP;
	    }
	    else {
	    	invoiceType1 = null;
	    	invoiceType2 = null;
	    	invoiceTransaction1 = null;
			invoiceTransaction2 = null;
	    }
		
		Date fromDate = AonDateUtils.getYearFirstDay(mod347.getYear());
		Date toDate = AonDateUtils.getYearLastDay(mod347.getYear());
		
		return VATDAO.getVatBreakdown(ctx, fromDate, toDate, mod347)
		    .filter( vat -> (!vat.hasRetention()) &&                                                                         // Facturas sin retención
		    		        (vat.getTransaction() == invoiceTransaction1 || vat.getTransaction() == invoiceTransaction2) &&  // Nacional o ISP 
		                    (vat.getInvoiceType() == invoiceType1 || vat.getInvoiceType() == invoiceType2) &&  				 // Tipo (Ventas o Compras/Gastos)		                    
		                    (AonStringUtils.equals(vat.getRegistryDocument(),declared.getDocument()))  &&                    // NIF
		                    (vat.isVatAccrualRegime() == declared.isVatAccrual())                                            // Criterio de caja		                    
		                    );  
		
	}
	
	// --------------- DUPLICAR MODELO ---------------
	
	public static Mod347 duplicateNextYear(AONContext ctx, int id) {
		
		Mod347 mod347 = getById(ctx, id);
		mod347.setYear( mod347.getYear() + 1 );
		mod347.setId(null);
		mod347 = save(ctx, mod347);
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
		return save(ctx, mod347);		
	
	}
	
	
}
