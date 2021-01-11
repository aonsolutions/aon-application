package com.esferalia.aon.occam.impl.jooq.dao;


import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.Creditor.CREDITOR;
import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rmedia.RMEDIA;
import static com.esferalia.aon.jooq.tables.Supplier.SUPPLIER;

import java.sql.Timestamp;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Param;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.AccountingRegistryFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.AccountingRegistryProperties;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType;
import com.esferalia.aon.occam.api.model.registry.IAccountingRegistryTypeVisitor;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AccountingRegistryDAO {
	
	private static final Field<Integer> TYP_FIELD = DSL.field("typ", Integer.class);
	private static final Field<Byte> STA_FIELD    = DSL.field("sta", Byte.class);
	private static final Field<Integer> REG_FIELD = DSL.field("reg", REGISTRY.ID.getType() ); 
	private static final Field<Integer> ACC_FIELD = DSL.field("acc", ACCOUNT.ID.getType() );
	private static final Field<Integer> SCP_FIELD = DSL.field("scp", CUSTOMER.SCOPE.getType() );
	private static final Field<Byte> ITR_FIELD    = DSL.field("itr", CUSTOMER.TRANSACTION.getType() );
	private static final Field<Byte> WTH_FIELD    = DSL.field("wth", CUSTOMER.WITHHOLDING.getType() );
	private static final Field<Byte> WTF_FIELD    = DSL.field("wtf", SUPPLIER.WITHHOLDING_FARMER.getType() );
	private static final Field<Byte> SUR_FIELD    = DSL.field("sur", CUSTOMER.SURCHARGE.getType() );
	private static final Field<Byte> VAP_FIELD    = DSL.field("vap", CUSTOMER.SURCHARGE.getType() );
	
	private static final Param<Byte> FALSE_TYPE  = DSL.val( (byte) 0 );
	private static final Param<Integer> CUS_TYPE  = DSL.val( AccountingRegistryType.CUSTOMER.ordinal());
	private static final Param<Integer> SUP_TYPE  = DSL.val( AccountingRegistryType.SUPPLIER.ordinal());
	private static final Param<Integer> CRE_TYPE  = DSL.val( AccountingRegistryType.CREDITOR.ordinal());

	private static final AccountingRegistryPropertiesDAO ACCOUNTING_REGISTRY_PROPERTIES = new AccountingRegistryPropertiesDAO();
	private static class AccountingRegistryPropertiesDAO implements AccountingRegistryProperties {
		private Condition[] getConditions(AccountingRegistryFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(REGISTRY.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(REGISTRY.DOMAIN);}
		@Override public Property<String> getDocumentProperty() {return new FilterDAO.PropertyDAO<String>(REGISTRY.DOCUMENT);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<String>(REGISTRY.NAME);}
		@Override public Property<String> getAliasProperty() {return new FilterDAO.PropertyDAO<String>(REGISTRY.ALIAS);}
		@Override public Property<Byte> getSecurityLevelProperty() {return new FilterDAO.PropertyDAO<Byte>(REGISTRY.SECURITY_LEVEL);}
		@Override public Property<String> getAccountCodeProperty() {return new FilterDAO.PropertyDAO<String>(ACCOUNT.CODE);}
		@Override public Property<String> getAccountDescriptionProperty() {return new FilterDAO.PropertyDAO<String>(ACCOUNT.DESCRIPTION);}
		@Override public Property<Byte> getDocumentTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(REGISTRY.DOCUMENT_TYPE);}
		@Override public Property<String> getDocumentCountryProperty() {return new FilterDAO.PropertyDAO<String>(REGISTRY.DOCUMENT_COUNTRY);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<Byte>(STA_FIELD);}
	}
	
	public static Condition[] getConditions(AccountingRegistryFilter filter) {
		return ACCOUNTING_REGISTRY_PROPERTIES.getConditions(filter);		
	}

	public static Stream<AccountingRegistry> getAccountingRegistries(AONContext ctx, AccountingRegistryFilter filter) {
		ctx.checkRead();
		String AR = "accRegTable";
		
		return 	ctx.getDslContext().select( 
				 TYP_FIELD,REG_FIELD,ACC_FIELD
				,SCP_FIELD,ITR_FIELD,WTH_FIELD
				,WTF_FIELD,SUR_FIELD,VAP_FIELD,STA_FIELD
				,REGISTRY.ID,REGISTRY.DOCUMENT,REGISTRY.DOCUMENT_TYPE
				,REGISTRY.DOCUMENT_COUNTRY,REGISTRY.NAME,REGISTRY.ALIAS
				,REGISTRY.NATIONALITY
				,ACCOUNT.ID,ACCOUNT.CODE,ACCOUNT.DESCRIPTION
				)
			.from(
					ctx.getDslContext().select(SUP_TYPE.as(TYP_FIELD)
						,SUPPLIER.REGISTRY.as(REG_FIELD)
						,SUPPLIER.ACCOUNT.as(ACC_FIELD)
						,SUPPLIER.SCOPE.as(SCP_FIELD)
						,SUPPLIER.TRANSACTION.as(ITR_FIELD)
						,SUPPLIER.WITHHOLDING.as(WTH_FIELD)
						,SUPPLIER.WITHHOLDING_FARMER.as(WTF_FIELD)
						,FALSE_TYPE.as(SUR_FIELD)
						,SUPPLIER.VAT_ACCRUAL_PAYMENT.as(VAP_FIELD)
						,SUPPLIER.STATUS.as(STA_FIELD)
						)
					.from(SUPPLIER)
					.where(SUPPLIER.DOMAIN.eq(ctx.getDomainId())
					.and(SecurityDAO.getUserScopesCondition(ctx,ctx.getUser(),SUPPLIER.SCOPE)))
				.unionAll(
				ctx.getDslContext().select(CUS_TYPE.as(TYP_FIELD)
						,CUSTOMER.REGISTRY.as(REG_FIELD)
						,CUSTOMER.ACCOUNT.as(ACC_FIELD)
						,CUSTOMER.SCOPE.as(SCP_FIELD)
						,CUSTOMER.TRANSACTION.as(ITR_FIELD)
						,CUSTOMER.WITHHOLDING.as(WTH_FIELD)
						,FALSE_TYPE.as(WTF_FIELD)
						,CUSTOMER.SURCHARGE.as(SUR_FIELD)
						,FALSE_TYPE.as(VAP_FIELD)
						,CUSTOMER.STATUS.as(STA_FIELD)
						)
					.from(CUSTOMER)
					.where(CUSTOMER.DOMAIN.eq(ctx.getDomainId())
					.and(SecurityDAO.getUserScopesCondition(ctx,ctx.getUser(),CUSTOMER.SCOPE))))
				.unionAll(
				ctx.getDslContext().select(CRE_TYPE.as(TYP_FIELD)
						,CREDITOR.REGISTRY.as(REG_FIELD)
						,CREDITOR.ACCOUNT.as(ACC_FIELD)
						,CREDITOR.SCOPE.as(SCP_FIELD)
						,CREDITOR.TRANSACTION.as(ITR_FIELD)
						,CREDITOR.WITHHOLDING.as(WTH_FIELD)
						,FALSE_TYPE.as(WTF_FIELD)
						,FALSE_TYPE.as(SUR_FIELD)
						,CREDITOR.VAT_ACCRUAL_PAYMENT.as(VAP_FIELD)
						,CREDITOR.STATUS.as(STA_FIELD)
						)
					.from(CREDITOR)
					.where(CREDITOR.DOMAIN.eq(ctx.getDomainId())
					.and(SecurityDAO.getUserScopesCondition(ctx,ctx.getUser(),CREDITOR.SCOPE))))
				.asTable(AR)
				)
			.join(REGISTRY).on(REGISTRY.ID.eq(REG_FIELD))
			.leftOuterJoin(ACCOUNT).on(ACCOUNT.ID.eq(ACC_FIELD))
			.where(ACCOUNTING_REGISTRY_PROPERTIES.getConditions(filter))
			.and(SecurityDAO.getSecurityLevelCondition(ctx, ctx.getUser(), REGISTRY.SECURITY_LEVEL))
			.orderBy(REGISTRY.NAME)
			.limit(30)
			.fetch()
			.stream()
			.map(rec -> new AccountingRegistry()
					.setId( rec.getValue(REGISTRY.ID) )
					.setScope(rec.getValue(SCP_FIELD))
					.setAccountId(rec.getValue(ACCOUNT.ID))
					.setAccountCode(rec.getValue(ACCOUNT.CODE))
					.setAccountDescription(rec.getValue(ACCOUNT.DESCRIPTION))
					.setAlias(rec.getValue(REGISTRY.ALIAS))
					.setNationality(Country.safeValueOf(rec.getValue(REGISTRY.NATIONALITY)))
					.setDocument(rec.getValue(REGISTRY.DOCUMENT))
					.setDocumentType(DocumentType.safeValueOf(rec.getValue(REGISTRY.DOCUMENT_TYPE)))
					.setDocumentCountry(Country.safeValueOf(rec.getValue(REGISTRY.DOCUMENT_COUNTRY)))
					.setName(rec.getValue(REGISTRY.NAME))
					.setType( AccountingRegistryType.values()[rec.getValue(TYP_FIELD)] )
					.setTransaction( InvoiceTransactionType.safeValueOf(rec.getValue(ITR_FIELD)))
					.setWithholding( AonEnumUtils.getBoolean(rec.getValue(WTH_FIELD)))
					.setWithholdingFarmer( AonEnumUtils.getBoolean(rec.getValue(WTF_FIELD)))
					.setSurcharge( AonEnumUtils.getBoolean(rec.getValue(SUR_FIELD)))
					.setVatAccrualPayment( AonEnumUtils.getBoolean(rec.getValue(VAP_FIELD)))
					);			
		
	}
	
	public static AccountingRegistry insert(AONContext ctx, AccountingRegistry reg) {
		if (reg == null) throw new AonCoreException("No se puede grabar. Es nulo. (Error Interno)");
		if (reg.getType() == null) throw new AonCoreException("No se puede determinar el tipo. Es nulo. (Error Interno)");
		ctx.checkWrite();

		final Integer registryId = RegistryDAO.insert(ctx, new Registry()
			.setId(reg.getDomain())
			.setDomain(new Domain().setId(reg.getDomain()))
			.setDocument(reg.getDocument())
			.setDocumentCountry(reg.getDocumentCountry())
			.setDocumentType(reg.getDocumentType())
			.setName(reg.getName())
			.setAlias(reg.getAlias())
			.setNationality(reg.getNationality())
			.setSecurityLevel(SecurityLevel.OFFICIAL)
			.setLegalPerson( AonDocumentUtil.isEntity(reg.getDocument()) )
			);
		reg.setId(registryId);
		
		
		ctx.getDslContext().insertInto(RADDRESS)
			.set(RADDRESS.DOMAIN,reg.getDomain())
			.set(RADDRESS.REGISTRY, reg.getId())
			.set(RADDRESS.TYPE, (byte) 0)
			.set(RADDRESS.STREET_TYPE,reg.getAddressStreetType()==null?null:reg.getAddressStreetType().getAeatCode())
			.set(RADDRESS.ADDRESS,reg.getAddress())
			.set(RADDRESS.NUMBER,reg.getAddressNumber())
			.set(RADDRESS.ZIP,reg.getAddressZIP())
			.set(RADDRESS.CITY,reg.getAddressTown())
			.set(RADDRESS.GEOZONE,reg.getGeozone())
			.execute();
		if (!AonStringUtils.isBlank(reg.getPhone())) {
			ctx.getDslContext().insertInto(RMEDIA)
				.set(RMEDIA.DOMAIN,reg.getDomain())
				.set(RMEDIA.REGISTRY, reg.getId())
				.set(RMEDIA.MEDIA, MediaType.FIXED_PHONE.value())
				.set(RMEDIA.VALUE,reg.getPhone())
				.set(RMEDIA.COMMENT,reg.getPhoneComments())
				.set(RMEDIA.ADMINISTRATIVE,AonEnumUtils.getByte(true))
				.set(RMEDIA.COMMERCIAL,AonEnumUtils.getByte(true))
				.set(RMEDIA.TECHNICAL,AonEnumUtils.getByte(true))
				.execute();
		}
		if (!AonStringUtils.isBlank(reg.getCellular())) {
			ctx.getDslContext().insertInto(RMEDIA)
				.set(RMEDIA.DOMAIN,reg.getDomain())
				.set(RMEDIA.REGISTRY, reg.getId())
				.set(RMEDIA.MEDIA, MediaType.CELLULAR.value())
				.set(RMEDIA.VALUE,reg.getCellular())
				.set(RMEDIA.COMMENT,reg.getCellularComments())
				.set(RMEDIA.ADMINISTRATIVE,AonEnumUtils.getByte(true))
				.set(RMEDIA.COMMERCIAL,AonEnumUtils.getByte(true))
				.set(RMEDIA.TECHNICAL,AonEnumUtils.getByte(true))
				.execute();
		}
		if (!AonStringUtils.isBlank(reg.getFax())) {
			ctx.getDslContext().insertInto(RMEDIA)
				.set(RMEDIA.DOMAIN,reg.getDomain())
				.set(RMEDIA.REGISTRY, reg.getId())
				.set(RMEDIA.MEDIA, MediaType.FAX.value())
				.set(RMEDIA.VALUE,reg.getFax())
				.set(RMEDIA.COMMENT,reg.getFaxComments())
				.set(RMEDIA.ADMINISTRATIVE,AonEnumUtils.getByte(true))
				.set(RMEDIA.COMMERCIAL,AonEnumUtils.getByte(true))
				.set(RMEDIA.TECHNICAL,AonEnumUtils.getByte(true))
				.execute();
		}
		if (!AonStringUtils.isBlank(reg.getEmail())) {
			ctx.getDslContext().insertInto(RMEDIA)
				.set(RMEDIA.DOMAIN,reg.getDomain())
				.set(RMEDIA.REGISTRY, reg.getId())
				.set(RMEDIA.MEDIA, MediaType.EMAIL.value())
				.set(RMEDIA.VALUE,reg.getEmail())
				.set(RMEDIA.ADMINISTRATIVE,AonEnumUtils.getByte(true))
				.set(RMEDIA.COMMERCIAL,AonEnumUtils.getByte(true))
				.set(RMEDIA.TECHNICAL,AonEnumUtils.getByte(true))
				.execute();
		}
		if (!AonStringUtils.isBlank(reg.getWeb())) {
			ctx.getDslContext().insertInto(RMEDIA)
				.set(RMEDIA.DOMAIN,reg.getDomain())
				.set(RMEDIA.REGISTRY, reg.getId())
				.set(RMEDIA.MEDIA, MediaType.WEB.value())
				.set(RMEDIA.VALUE,reg.getWeb())
				.set(RMEDIA.ADMINISTRATIVE,AonEnumUtils.getByte(true))
				.set(RMEDIA.COMMERCIAL,AonEnumUtils.getByte(true))
				.set(RMEDIA.TECHNICAL,AonEnumUtils.getByte(true))
				.execute();
		}
		reg.getType().visit(reg, new IAccountingRegistryTypeVisitor() {
			
			@Override
			public void visitSupplier(AccountingRegistry reg) {
				ctx.getDslContext().insertInto(SUPPLIER)
					.set(SUPPLIER.REGISTRY, reg.getId())
					.set(SUPPLIER.DOMAIN,reg.getDomain())
					.set(SUPPLIER.WITHHOLDING, AonEnumUtils.getByte(reg.isWithholding()))
					.set(SUPPLIER.WITHHOLDING_FARMER, AonEnumUtils.getByte(reg.isWithholdingFarmer()))
					.set(SUPPLIER.VAT_ACCRUAL_PAYMENT, AonEnumUtils.getByte(reg.isVatAccrualPayment()))
					.set(SUPPLIER.TRANSACTION, reg.getTransaction() == null 
						? InvoiceTransactionType.NATIONAL.value() 
						: reg.getTransaction().value() )
					.set(SUPPLIER.STATUS, RegistryStatus.ACTIVE.value() )
					.set(SUPPLIER.SCOPE, reg.getScope() )
					.set(SUPPLIER.ACCOUNT, reg.getAccountId() )
					.set(SUPPLIER.CREATION_USER, ctx.getUser() )
					.set(SUPPLIER.CREATION_DATE, new Timestamp( System.currentTimeMillis()) )
					.execute();
			}
			
			@Override
			public void visitCustomer(AccountingRegistry reg) {
				ctx.getDslContext().insertInto(CUSTOMER)
					.set(CUSTOMER.REGISTRY, reg.getId())
					.set(CUSTOMER.DOMAIN,reg.getDomain())
					.set(CUSTOMER.SURCHARGE, AonEnumUtils.getByte(reg.isSurcharge()))
					.set(CUSTOMER.WITHHOLDING, AonEnumUtils.getByte(reg.isWithholding()))
					.set(CUSTOMER.TRANSACTION, reg.getTransaction() == null 
						? InvoiceTransactionType.NATIONAL.value() 
						: reg.getTransaction().value() )
					.set(CUSTOMER.STATUS, RegistryStatus.ACTIVE.value() )
					.set(CUSTOMER.SCOPE, reg.getScope() )
					.set(CUSTOMER.ACCOUNT, reg.getAccountId() )
					.set(CUSTOMER.CREATION_USER, ctx.getUser() )
					.set(CUSTOMER.CREATION_DATE, new Timestamp( System.currentTimeMillis()) )
					.execute();
			}
			
			@Override
			public void visitCreditor(AccountingRegistry reg) {
				ctx.getDslContext().insertInto(CREDITOR)
				.set(CREDITOR.REGISTRY, reg.getId())
				.set(CREDITOR.DOMAIN,reg.getDomain())
				.set(CREDITOR.WITHHOLDING, AonEnumUtils.getByte(reg.isWithholding()))
				.set(CREDITOR.VAT_ACCRUAL_PAYMENT, AonEnumUtils.getByte(reg.isVatAccrualPayment()))
				.set(CREDITOR.TRANSACTION, reg.getTransaction() == null 
					? InvoiceTransactionType.NATIONAL.value() 
					: reg.getTransaction().value() )
				.set(CREDITOR.STATUS, RegistryStatus.ACTIVE.value() )
				.set(CREDITOR.SCOPE, reg.getScope() )
				.set(CREDITOR.ACCOUNT, reg.getAccountId() )
				.set(CREDITOR.CREATION_USER, ctx.getUser() )
				.set(CREDITOR.CREATION_DATE, new Timestamp( System.currentTimeMillis()) )
				.execute();
			}
			
			@Override
			public void visitUndedCreditor(AccountingRegistry reg) {
				visitCreditor(reg);
			}
		}); 
		return reg;
	}
	
	public static AccountingRegistry update(AONContext ctx, AccountingRegistry reg) {
		if (reg == null) throw new AonCoreException("No se puede grabar. Es nulo. (Error Interno)");
		if (reg.getType() == null) throw new AonCoreException("No se puede determinar el tipo. Es nulo. (Error Interno)");
		ctx.checkWrite();
		if(reg.getId() == null) {
			return insert(ctx, reg);
		} else {
			ctx.getDslContext().update(REGISTRY)
			.set(REGISTRY.DOCUMENT_COUNTRY, reg.getDocumentCountry().getIso2())
			.set(REGISTRY.DOCUMENT_TYPE, reg.getDocumentType().value())
			.set(REGISTRY.NAME, reg.getName())
			.set(REGISTRY.ALIAS, reg.getAlias())
			.set(REGISTRY.NATIONALITY, reg.getNationality().getIso2())
			.set(REGISTRY.SECURITY_LEVEL, SecurityLevel.OFFICIAL.value())
			.set(REGISTRY.TYPE, AonEnumUtils.getByte( AonDocumentUtil.isEntity(reg.getDocument())))
			.where(REGISTRY.ID.eq(reg.getId()))
			.execute();
			
			if(reg.getAddressId() != null) {
				ctx.getDslContext().update(RADDRESS)
				.set(RADDRESS.DOMAIN,reg.getDomain())
				.set(RADDRESS.REGISTRY, reg.getId())
				.set(RADDRESS.TYPE, (byte) 0)
				.set(RADDRESS.STREET_TYPE,reg.getAddressStreetType()==null?null:reg.getAddressStreetType().getAeatCode())
				.set(RADDRESS.ADDRESS,reg.getAddress())
				.set(RADDRESS.NUMBER,reg.getAddressNumber())
				.set(RADDRESS.ZIP,reg.getAddressZIP())
				.set(RADDRESS.CITY,reg.getAddressTown())
				.set(RADDRESS.GEOZONE,reg.getGeozone())
				.where(RADDRESS.ID.eq(reg.getAddressId()))
				.execute();
			} else  {
				ctx.getDslContext().insertInto(RADDRESS)
				.set(RADDRESS.DOMAIN,reg.getDomain())
				.set(RADDRESS.REGISTRY, reg.getId())
				.set(RADDRESS.TYPE, (byte) 0)
				.set(RADDRESS.STREET_TYPE,reg.getAddressStreetType()==null?null:reg.getAddressStreetType().getAeatCode())
				.set(RADDRESS.ADDRESS,reg.getAddress())
				.set(RADDRESS.NUMBER,reg.getAddressNumber())
				.set(RADDRESS.ZIP,reg.getAddressZIP())
				.set(RADDRESS.CITY,reg.getAddressTown())
				.set(RADDRESS.GEOZONE,reg.getGeozone())
				.execute();
			}

			if (!AonStringUtils.isBlank(reg.getPhone())) {
				RegistryMedia rm = RegistryDAO.getRMediaStream(ctx, f -> 
					f.getRegistryProperty().eq(reg.getId())
					.and(f.getValueProperty().eq(reg.getPhone()))).findFirst().orElse(null);
				if(rm == null) {	
					ctx.getDslContext().insertInto(RMEDIA)
						.set(RMEDIA.DOMAIN,reg.getDomain())
						.set(RMEDIA.REGISTRY, reg.getId())
						.set(RMEDIA.MEDIA, MediaType.FIXED_PHONE.value())
						.set(RMEDIA.VALUE,reg.getPhone())
						.set(RMEDIA.COMMENT,reg.getPhoneComments())
						.set(RMEDIA.ADMINISTRATIVE,AonEnumUtils.getByte(true))
						.set(RMEDIA.COMMERCIAL,AonEnumUtils.getByte(true))
						.set(RMEDIA.TECHNICAL,AonEnumUtils.getByte(true))
						.execute();
				}
			}
			if (!AonStringUtils.isBlank(reg.getCellular())) {
				RegistryMedia rm = RegistryDAO.getRMediaStream(ctx, f -> 
					f.getRegistryProperty().eq(reg.getId())
					.and(f.getValueProperty().eq(reg.getCellular()))).findFirst().orElse(null);
				if(rm == null) {	
					ctx.getDslContext().insertInto(RMEDIA)
						.set(RMEDIA.DOMAIN,reg.getDomain())
						.set(RMEDIA.REGISTRY, reg.getId())
						.set(RMEDIA.MEDIA, MediaType.CELLULAR.value())
						.set(RMEDIA.VALUE,reg.getCellular())
						.set(RMEDIA.COMMENT,reg.getCellularComments())
						.set(RMEDIA.ADMINISTRATIVE,AonEnumUtils.getByte(true))
						.set(RMEDIA.COMMERCIAL,AonEnumUtils.getByte(true))
						.set(RMEDIA.TECHNICAL,AonEnumUtils.getByte(true))
						.execute();
				}
			}
			if (!AonStringUtils.isBlank(reg.getFax())) {
				RegistryMedia rm = RegistryDAO.getRMediaStream(ctx, f -> 
					f.getRegistryProperty().eq(reg.getId())
					.and(f.getValueProperty().eq(reg.getFax()))).findFirst().orElse(null);
				if(rm == null) {	
					ctx.getDslContext().insertInto(RMEDIA)
						.set(RMEDIA.DOMAIN,reg.getDomain())
						.set(RMEDIA.REGISTRY, reg.getId())
						.set(RMEDIA.MEDIA, MediaType.FAX.value())
						.set(RMEDIA.VALUE,reg.getFax())
						.set(RMEDIA.COMMENT,reg.getFaxComments())
						.set(RMEDIA.ADMINISTRATIVE,AonEnumUtils.getByte(true))
						.set(RMEDIA.COMMERCIAL,AonEnumUtils.getByte(true))
						.set(RMEDIA.TECHNICAL,AonEnumUtils.getByte(true))
						.execute();
				}
			}
			if (!AonStringUtils.isBlank(reg.getEmail())) {
				RegistryMedia rm = RegistryDAO.getRMediaStream(ctx, f -> 
					f.getRegistryProperty().eq(reg.getId())
					.and(f.getValueProperty().eq(reg.getEmail()))).findFirst().orElse(null);
				if(rm == null) {	
					ctx.getDslContext().insertInto(RMEDIA)
						.set(RMEDIA.DOMAIN,reg.getDomain())
						.set(RMEDIA.REGISTRY, reg.getId())
						.set(RMEDIA.MEDIA, MediaType.EMAIL.value())
						.set(RMEDIA.VALUE,reg.getEmail())
						.set(RMEDIA.ADMINISTRATIVE,AonEnumUtils.getByte(true))
						.set(RMEDIA.COMMERCIAL,AonEnumUtils.getByte(true))
						.set(RMEDIA.TECHNICAL,AonEnumUtils.getByte(true))
						.execute();
				}
			}
			if (!AonStringUtils.isBlank(reg.getWeb())) {
				RegistryMedia rm = RegistryDAO.getRMediaStream(ctx, f -> 
					f.getRegistryProperty().eq(reg.getId())
					.and(f.getValueProperty().eq(reg.getWeb()))).findFirst().orElse(null);
				if(rm == null) {	
					ctx.getDslContext().insertInto(RMEDIA)
						.set(RMEDIA.DOMAIN,reg.getDomain())
						.set(RMEDIA.REGISTRY, reg.getId())
						.set(RMEDIA.MEDIA, MediaType.WEB.value())
						.set(RMEDIA.VALUE,reg.getWeb())
						.set(RMEDIA.ADMINISTRATIVE,AonEnumUtils.getByte(true))
						.set(RMEDIA.COMMERCIAL,AonEnumUtils.getByte(true))
						.set(RMEDIA.TECHNICAL,AonEnumUtils.getByte(true))
						.execute();
				}
			}
			
			reg.getType().visit(reg, new IAccountingRegistryTypeVisitor() {
				
				@Override
				public void visitSupplier(AccountingRegistry reg) {
					ctx.getDslContext().insertInto(SUPPLIER)
						.set(SUPPLIER.REGISTRY, reg.getId())
						.set(SUPPLIER.DOMAIN,reg.getDomain())
						.set(SUPPLIER.WITHHOLDING, AonEnumUtils.getByte(reg.isWithholding()))
						.set(SUPPLIER.WITHHOLDING_FARMER, AonEnumUtils.getByte(reg.isWithholdingFarmer()))
						.set(SUPPLIER.VAT_ACCRUAL_PAYMENT, AonEnumUtils.getByte(reg.isVatAccrualPayment()))
						.set(SUPPLIER.TRANSACTION, reg.getTransaction() == null 
							? InvoiceTransactionType.NATIONAL.value() 
							: reg.getTransaction().value() )
						.set(SUPPLIER.STATUS, RegistryStatus.ACTIVE.value() )
						.set(SUPPLIER.SCOPE, reg.getScope() )
						.set(SUPPLIER.ACCOUNT, reg.getAccountId() )
						.set(SUPPLIER.CREATION_USER, ctx.getUser() )
						.set(SUPPLIER.CREATION_DATE, new Timestamp( System.currentTimeMillis()) )
						.execute();
				}
			
				@Override
				public void visitCustomer(AccountingRegistry reg) {
					ctx.getDslContext().insertInto(CUSTOMER)
						.set(CUSTOMER.REGISTRY, reg.getId())
						.set(CUSTOMER.DOMAIN,reg.getDomain())
						.set(CUSTOMER.SURCHARGE, AonEnumUtils.getByte(reg.isSurcharge()))
						.set(CUSTOMER.WITHHOLDING, AonEnumUtils.getByte(reg.isWithholding()))
						.set(CUSTOMER.TRANSACTION, reg.getTransaction() == null 
							? InvoiceTransactionType.NATIONAL.value() 
							: reg.getTransaction().value() )
						.set(CUSTOMER.STATUS, RegistryStatus.ACTIVE.value() )
						.set(CUSTOMER.SCOPE, reg.getScope() )
						.set(CUSTOMER.ACCOUNT, reg.getAccountId() )
						.set(CUSTOMER.CREATION_USER, ctx.getUser() )
						.set(CUSTOMER.CREATION_DATE, new Timestamp( System.currentTimeMillis()) )
						.execute();
				}
			
				@Override
				public void visitCreditor(AccountingRegistry reg) {
					ctx.getDslContext().insertInto(CREDITOR)
						.set(CREDITOR.REGISTRY, reg.getId())
						.set(CREDITOR.DOMAIN,reg.getDomain())
						.set(CREDITOR.WITHHOLDING, AonEnumUtils.getByte(reg.isWithholding()))
						.set(CREDITOR.VAT_ACCRUAL_PAYMENT, AonEnumUtils.getByte(reg.isVatAccrualPayment()))
						.set(CREDITOR.TRANSACTION, reg.getTransaction() == null 
							? InvoiceTransactionType.NATIONAL.value() 
							: reg.getTransaction().value() )
						.set(CREDITOR.STATUS, RegistryStatus.ACTIVE.value() )
						.set(CREDITOR.SCOPE, reg.getScope() )
						.set(CREDITOR.ACCOUNT, reg.getAccountId() )
						.set(CREDITOR.CREATION_USER, ctx.getUser() )
						.set(CREDITOR.CREATION_DATE, new Timestamp( System.currentTimeMillis()) )
						.execute();
				}
			
				@Override
				public void visitUndedCreditor(AccountingRegistry reg) {
					visitCreditor(reg);
				}	
			}); 
		}
		return reg;
	}
	
}
