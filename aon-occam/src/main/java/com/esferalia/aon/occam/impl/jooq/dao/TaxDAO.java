package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.Tax.TAX;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.TaxFilter;
import com.esferalia.aon.occam.api.model.Properties.TaxProperties;
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.occam.impl.jooq.dao.AccountDAO.FullAccountFiller;
import com.esferalia.aon.occam.impl.jooq.validation.TaxAutoComplete;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;

public class TaxDAO {
	
	private static final com.esferalia.aon.jooq.tables.Account SALES_ACCOUNT = ACCOUNT.as("SALES_ACCOUNT");
	private static final com.esferalia.aon.jooq.tables.Account PURCHASE_ACCOUNT = ACCOUNT.as("PURCHASE_ACCOUNT");
	
	private static final TaxPropertiesDAO TAX_PROPERTIES = new TaxPropertiesDAO();

	private static class TaxPropertiesDAO implements TaxProperties {
		protected Condition getConditions(TaxFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return DSL.noCondition();
			return filterDAO.getCondition();
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(TAX.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(TAX.DOMAIN);}
		@Override public Property<String> getNameProperty(){return new FilterDAO.PropertyDAO<>(TAX.NAME);}
		@Override public Property<Double> getPercentageProperty() {return new FilterDAO.PropertyDAO<>(TAX.PERCENTAGE);}
		@Override public Property<Integer> getPurchaseAccountProperty() {return new FilterDAO.PropertyDAO<>(TAX.PURCHASE_ACCOUNT);}
		@Override public Property<Integer> getSalesAccountProperty() {return new FilterDAO.PropertyDAO<>(TAX.SALES_ACCOUNT);}
		@Override public Property<Double> getSurchargeProperty() {return new FilterDAO.PropertyDAO<>(TAX.SURCHARGE);}
		@Override public Property<Byte> getTaxTypeProperty() {return new FilterDAO.PropertyDAO<>(TAX.TAX_TYPE);}
		@Override public Property<Byte> getVatDeductionTypeProperty() {return new FilterDAO.PropertyDAO<>(TAX.VAT_DEDUCTION_TYPE);}
		@Override public Property<Byte> getWithholdingTypeProperty() {return new FilterDAO.PropertyDAO<>(TAX.WITHHOLDING_TYPE);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<>(TAX.CREATION_DATE);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<>(TAX.CREATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<>(TAX.MODIFICATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<>(TAX.MODIFICATION_USER);}
	}
	
	private TaxDAO() {
		
	}
	
	// *********************************************************
	// ****************************************** [READ] *******
	// *********************************************************
	private static SelectConditionStep<Record> select(AONContext ctx, Integer domainId){	
		return ctx.getDslContext()
			.select()
			.from(TAX)
			.leftOuterJoin(SALES_ACCOUNT).on(SALES_ACCOUNT.ID.eq(TAX.SALES_ACCOUNT))
			.leftOuterJoin(PURCHASE_ACCOUNT).on(PURCHASE_ACCOUNT.ID.eq(TAX.PURCHASE_ACCOUNT))
			.where(TAX.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx, domainId)));
		
	}
	
	public static Optional<Tax> get(AONContext ctx, Integer domainId, Integer taxId){
		return select(ctx, domainId)
			.and(TAX.ID.eq(taxId))
			.fetch()
			.stream()
			.map(new TaxFiller())
			.findFirst();
	}

	public static Stream<Tax> stream(AONContext ctx,Integer domainId , TaxFilter filter){
		return select(ctx, domainId)
			.and( TAX_PROPERTIES.getConditions(filter) )
			.fetch()
			.stream()
			.map(new TaxFiller());
	}

	public static Stream<Tax> stream(AONContext ctx, Integer domainId){
		return select(ctx, domainId)
			.fetch()
			.stream()
			.map(new TaxFiller());
	}

	public static Stream<Tax> getVatTaxes(AONContext ctx, Integer domainId){
		return stream(ctx, domainId)
			.filter(t -> t.getType() == TaxType.VAT);
	}

	public static Stream<Tax> getWithholdingTaxes(AONContext ctx, Integer domainId){
		return stream(ctx, domainId)
			.filter(t -> t.getType() == TaxType.RETENTION);
	}
	
	public static Stream<Tax> getVatTaxes(AONContext ctx, Integer domainId, Date atDate){
		if (atDate == null) return Stream.empty();
		return getVatTaxes(ctx, domainId)
			.filter( t -> AonDateUtils.isInRange(atDate, t.getStartDate(), null));
	}

	public static Stream<Tax> getWithholdingTaxes(AONContext ctx, Integer domainId, Date atDate){
		if (atDate == null) return Stream.empty();
		return getWithholdingTaxes(ctx, domainId)
			.filter( t -> AonDateUtils.isInRange(atDate, t.getStartDate(), null));
	}
	
	// **********************************************************
	// ****************************************** [WRITE] *******
	// **********************************************************
	public static Tax save(AONContext ctx, Tax tax) {
		ctx.checkWrite();
		TaxAutoComplete.autoComplete(ctx, tax);
		return tax.getId() != null
			? update(ctx, tax)
			: insert(ctx, tax); 
	}
	
	private static Tax update(AONContext ctx, Tax tax) {
		ctx.getDslContext().update(TAX)
			.set(TAX.DOMAIN, tax.getDomain())
			.set(TAX.NAME, tax.getName())
			.set(TAX.TAX_TYPE, tax.getType().value())
			.set(TAX.PERCENTAGE, tax.getPercentage())
			.set(TAX.SURCHARGE, tax.getSurcharge())
			.set(TAX.START_DATE, new java.sql.Date(tax.getStartDate().getTime()))
			.set(TAX.VAT_DEDUCTION_TYPE, tax.getVatDeductionType().value())
			.set(TAX.WITHHOLDING_TYPE, tax.getWithholdingType().value())
			.set(TAX.SALES_ACCOUNT, tax.getSalesAccount().getId())
			.set(TAX.PURCHASE_ACCOUNT, tax.getPurchaseAccount().getId())
			.set(TAX.MODIFICATION_USER, ctx.getUser())
			.set(TAX.MODIFICATION_DATE, new Timestamp(new java.util.Date().getTime()))
			.where(TAX.ID.eq(tax.getId()))
			.execute();	
		return tax;
	}
	
	private static Tax insert(AONContext ctx, Tax tax) {
		Integer id = ctx.getDslContext().insertInto(TAX)
			.set(TAX.DOMAIN, tax.getDomain())
			.set(TAX.NAME, tax.getName())
			.set(TAX.TAX_TYPE, tax.getType().value())
			.set(TAX.PERCENTAGE, tax.getPercentage())
			.set(TAX.SURCHARGE, tax.getSurcharge())
			.set(TAX.START_DATE, new java.sql.Date(tax.getStartDate().getTime()))
			.set(TAX.VAT_DEDUCTION_TYPE, AonEnumUtils.getByte( tax.getVatDeductionType() ))
			.set(TAX.WITHHOLDING_TYPE, AonEnumUtils.getByte(tax.getWithholdingType()))
			.set(TAX.SALES_ACCOUNT, tax.getSalesAccount() != null ? tax.getSalesAccount().getId() : null)
			.set(TAX.PURCHASE_ACCOUNT, tax.getPurchaseAccount()!= null ? tax.getPurchaseAccount().getId() : null)
			.set(TAX.CREATION_USER, ctx.getUser())
			.set(TAX.CREATION_DATE, new Timestamp(new java.util.Date().getTime()))
			.returning(TAX.ID)
			.fetchOne()
			.getValue(TAX.ID);
		return tax.setId(id);
	}

	static class TaxFiller extends Filler implements Function<Record, Tax> {
		
		@Override
		public Tax apply(Record r) {
			return build(r, TAX);					
		}
		
		public static Tax build(Record r, com.esferalia.aon.jooq.tables.Tax t) {
			Account salesAccount = null;
			if (!checkField(r, SALES_ACCOUNT.ID)) {
				salesAccount = new Account().setId(r.getValue(t.SALES_ACCOUNT)); 
			} else if (!isNull(r, SALES_ACCOUNT.ID)) {
				salesAccount = FullAccountFiller.build(r, SALES_ACCOUNT);
			}
			Account purchaseAccount = null;
			if (!checkField(r, PURCHASE_ACCOUNT.ID)) {
				purchaseAccount = new Account().setId(r.getValue(t.PURCHASE_ACCOUNT)); 
			} else if (!isNull(r, PURCHASE_ACCOUNT.ID)) {
				purchaseAccount = FullAccountFiller.build(r, PURCHASE_ACCOUNT);
			}
			return new Tax()
				.setId(getValue(r, t.ID))
				.setDomain(getInteger(r, t.DOMAIN))
				.setName(getValue(r, t.NAME))
				.setType(TaxType.safeValueOf( getValue(r, t.TAX_TYPE)))
				.setPercentage(getDouble(r, t.PERCENTAGE))
				.setSurcharge(getDouble(r, t.SURCHARGE))
				.setStartDate(getValue(r, t.START_DATE))
				.setVatDeductionType( VatDeductionType.safeValueOf( getValue(r, t.VAT_DEDUCTION_TYPE)))
				.setWithholdingType(WithholdingType.safeValueOf( getValue(r, t.WITHHOLDING_TYPE)))
				.setSalesAccount( salesAccount )
				.setPurchaseAccount( purchaseAccount )
				.setCreationUser(getValue(r, t.CREATION_USER))
				.setCreationDate(getValue(r, t.CREATION_DATE))
				.setModificationUser(getValue(r, t.MODIFICATION_USER))
				.setModificationDate(getValue(r, t.MODIFICATION_DATE));					
		}
	}
	
}
