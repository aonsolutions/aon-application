package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.Tax.TAX;

import java.sql.Timestamp;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.TaxFilter;
import com.esferalia.aon.occam.api.model.Properties.TaxProperties;
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.occam.impl.jooq.validation.TaxAutoComplete;
import com.esferalia.aon.watson.server.AonEnumUtils;

public class TaxDAO {
	
	private static final com.esferalia.aon.jooq.tables.Account SALES_ACCOUNT = ACCOUNT.as("SALES_ACCOUNT");
	private static final com.esferalia.aon.jooq.tables.Account PURCHASE_ACCOUNT = ACCOUNT.as("PURCHASE_ACCOUNT");
	
	private static final TaxPropertiesDAO TAX_PROPERTIES = new TaxPropertiesDAO();

	protected static class TaxPropertiesDAO implements TaxProperties {
		protected Condition[] getConditions(TaxFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
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
	
	public static SelectConditionStep<Record> select(AONContext ctx, TaxFilter filter){	
		return ctx.getDslContext()
				.select()
				.from(TAX)
				.leftOuterJoin(SALES_ACCOUNT).on(SALES_ACCOUNT.ID.eq(TAX.SALES_ACCOUNT))
				.leftOuterJoin(PURCHASE_ACCOUNT).on(PURCHASE_ACCOUNT.ID.eq(TAX.PURCHASE_ACCOUNT))
				.where(TAX_PROPERTIES.getConditions(filter));
	}
	
	public static Stream<Tax> getStream(AONContext ctx, TaxFilter filter){
		return select(ctx, filter)
			.fetch()
			.stream()
			.map(new FullTaxFiller());
	}

	public static Tax getTax(AONContext ctx, TaxFilter filter){
		return select(ctx, filter)
			.limit(1).fetch().stream().map(new FullTaxFiller())
			.findFirst()
			.orElse(new Tax());
	}

	public static Stream<Tax> getVatTaxes(AONContext ctx){
		return getVatTaxes(ctx, null);
	}

	public static Stream<Tax> getWithholdingTaxes(AONContext ctx){
		return getWithholdingTaxes(ctx, null);
	}
	
	public static Stream<Tax> getVatTaxes(AONContext ctx, Date atDate){
		// TODO Implementar la búsqueda en una fecha concreta.
		return getStream(ctx, p -> 
			p.getDomainProperty().in(SecurityDAO.getInheritanceDomainIds(ctx))
			.and(p.getTaxTypeProperty().eq(TaxType.VAT.value())));
	}

	public static Stream<Tax> getWithholdingTaxes(AONContext ctx, Date atDate){
		// TODO Implementar la búsqueda en una fecha concreta.
		return getStream(ctx, p -> 
			p.getDomainProperty().in(SecurityDAO.getInheritanceDomainIds(ctx))
			.and(p.getTaxTypeProperty().eq(TaxType.RETENTION.value())));
	}
	
	public static Tax save(AONContext ctx, Tax tax) {
		TaxAutoComplete.autoComplete(ctx, tax);
		return tax.getId() != null
			? update(ctx, tax)
			: insert(ctx, tax); 
	}
	
	public static Tax update(AONContext ctx, Tax tax) {
		Timestamp now = new Timestamp(new java.util.Date().getTime());
		ctx.checkWrite();
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
			.set(TAX.MODIFICATION_DATE, now)
			.where(TAX.ID.eq(tax.getId()))
			.execute();	
		return tax;
	}
	
	public static Tax insert(AONContext ctx, Tax tax) {
		Timestamp now = new Timestamp(new java.util.Date().getTime());
		ctx.checkWrite();
		Integer id = ctx.getDslContext().insertInto(TAX)
			.set(TAX.DOMAIN, tax.getDomain())
			.set(TAX.NAME, tax.getName())
			.set(TAX.TAX_TYPE, tax.getType().value())
			.set(TAX.PERCENTAGE, tax.getPercentage())
			.set(TAX.SURCHARGE, tax.getSurcharge())
			.set(TAX.START_DATE, new java.sql.Date(tax.getStartDate().getTime()))
			.set(TAX.VAT_DEDUCTION_TYPE, tax.getVatDeductionType() != null ? tax.getVatDeductionType().value() : null)
			.set(TAX.WITHHOLDING_TYPE, tax.getWithholdingType() != null ? tax.getWithholdingType().value() : null)
			.set(TAX.SALES_ACCOUNT, tax.getSalesAccount() != null ? tax.getSalesAccount().getId() : null)
			.set(TAX.PURCHASE_ACCOUNT, tax.getPurchaseAccount()!= null ? tax.getPurchaseAccount().getId() : null)
			.set(TAX.CREATION_USER, ctx.getUser())
			.set(TAX.CREATION_DATE, now)
			.set(TAX.MODIFICATION_USER, ctx.getUser())
			.set(TAX.MODIFICATION_DATE, now)
			.returning(TAX.ID).fetchOne().getValue(TAX.ID);
			
		return tax.setId(id);
	}

	private static class FullTaxFiller implements Function<Record, Tax> {
		@Override
		public Tax apply(Record rec) {
			return new Tax()
					.setId(rec.getValue(TAX.ID))
					.setDomain(rec.getValue(TAX.DOMAIN))
					.setName(rec.getValue(TAX.NAME))
					.setType(TaxType.safeValueOf( rec.getValue(TAX.TAX_TYPE)))
					.setPercentage(rec.getValue(TAX.PERCENTAGE))
					.setSurcharge(rec.getValue(TAX.SURCHARGE))
					.setStartDate(rec.getValue(TAX.START_DATE))
					.setVatDeductionType( VatDeductionType.safeValueOf( rec.getValue(TAX.VAT_DEDUCTION_TYPE)))
					.setWithholdingType(WithholdingType.safeValueOf( rec.getValue(TAX.WITHHOLDING_TYPE)))
					.setSalesAccount( rec.getValue(SALES_ACCOUNT.ID) == null
							? null //new Account().setId(rec.getValue(TAX.SALES_ACCOUNT))
							: new Account()
								.setId(rec.getValue(SALES_ACCOUNT.ID))
								.setDomain(rec.getValue(SALES_ACCOUNT.DOMAIN))
								.setCode(rec.getValue(SALES_ACCOUNT.CODE))
								.setDescription(rec.getValue(SALES_ACCOUNT.DESCRIPTION))
								.setAlias(rec.getValue(SALES_ACCOUNT.ALIAS))
								.setEntryEnabled( AonEnumUtils.getBoolean(rec.getValue(SALES_ACCOUNT.ENTRYENABLED)))
								.setLevel(rec.getValue(SALES_ACCOUNT.LEVEL))
								.setActive(AonEnumUtils.getBoolean(rec.getValue(SALES_ACCOUNT.ACTIVE)))
								.setCostCenter(rec.getValue(SALES_ACCOUNT.COST_CENTER)))
					.setPurchaseAccount( rec.getValue(PURCHASE_ACCOUNT.ID) == null
							? null //new Account().setId(rec.getValue(TAX.PURCHASE_ACCOUNT))
							: new Account()
								.setId(rec.getValue(PURCHASE_ACCOUNT.ID))
								.setDomain(rec.getValue(PURCHASE_ACCOUNT.DOMAIN))
								.setCode(rec.getValue(PURCHASE_ACCOUNT.CODE))
								.setDescription(rec.getValue(PURCHASE_ACCOUNT.DESCRIPTION))
								.setAlias(rec.getValue(PURCHASE_ACCOUNT.ALIAS))
								.setEntryEnabled( AonEnumUtils.getBoolean(rec.getValue(PURCHASE_ACCOUNT.ENTRYENABLED)))
								.setLevel(rec.getValue(PURCHASE_ACCOUNT.LEVEL))
								.setActive(AonEnumUtils.getBoolean(rec.getValue(PURCHASE_ACCOUNT.ACTIVE)))
								.setCostCenter(rec.getValue(PURCHASE_ACCOUNT.COST_CENTER)))
					.setCreationUser(rec.getValue(TAX.CREATION_USER))
					.setCreationDate(rec.getValue(TAX.CREATION_DATE))
					.setModificationUser(rec.getValue(TAX.MODIFICATION_USER))
					.setModificationDate(rec.getValue(TAX.MODIFICATION_DATE));					
		}
	}
	
	public static class TaxFiller implements Function<Record, Tax> {

		@Override
		public Tax apply(Record r) {
			return build(r, TAX);					
		}
		
		public static Tax build(Record r, com.esferalia.aon.jooq.tables.Tax t) {
			if(r.getValue(t.ID) == null) return new Tax();
			return new Tax()
			.setId(r.getValue(t.ID))
			.setDomain(r.getValue(t.DOMAIN))
			.setName(r.getValue(t.NAME))
			.setType(TaxType.safeValueOf( r.getValue(t.TAX_TYPE)))
			.setPercentage(r.getValue(t.PERCENTAGE))
			.setSurcharge(r.getValue(t.SURCHARGE))
			.setStartDate(r.getValue(t.START_DATE))
			.setVatDeductionType( VatDeductionType.safeValueOf( r.getValue(t.VAT_DEDUCTION_TYPE)))
			.setWithholdingType(WithholdingType.safeValueOf( r.getValue(t.WITHHOLDING_TYPE)))
			.setPurchaseAccount(new Account().setId(r.getValue(t.PURCHASE_ACCOUNT)))
			.setSalesAccount(new Account().setId(r.getValue(t.SALES_ACCOUNT)))
			.setCreationUser(r.getValue(t.CREATION_USER))
			.setCreationDate(r.getValue(t.CREATION_DATE))
			.setModificationUser(r.getValue(t.MODIFICATION_USER))
			.setModificationDate(r.getValue(t.MODIFICATION_DATE));
		}
	}
}
