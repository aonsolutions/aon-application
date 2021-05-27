package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.Tax.TAX;

import java.sql.Timestamp;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.TaxFilter;
import com.esferalia.aon.occam.api.model.Properties.TaxProperties;
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.watson.server.AonEnumUtils;

public class TaxDAO {
	private static com.esferalia.aon.jooq.tables.Account SALES_ACCOUNT = ACCOUNT.as("SALES_ACCOUNT");
	private static com.esferalia.aon.jooq.tables.Account PURCHASE_ACCOUNT = ACCOUNT.as("PURCHASE_ACCOUNT");
	
	private static final TaxPropertiesDAO TAX_PROPERTIES = new TaxPropertiesDAO();

	protected static class TaxPropertiesDAO implements TaxProperties {
		protected Condition[] getConditions(TaxFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(TAX.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(TAX.DOMAIN);}
		@Override public Property<String> getNameProperty(){return new FilterDAO.PropertyDAO<String>(TAX.NAME);}
		@Override public Property<Double> getPercentageProperty() {return new FilterDAO.PropertyDAO<Double>(TAX.PERCENTAGE);}
		@Override public Property<Integer> getPurchaseAccountProperty() {return new FilterDAO.PropertyDAO<Integer>(TAX.PURCHASE_ACCOUNT);}
		@Override public Property<Integer> getSalesAccountProperty() {return new FilterDAO.PropertyDAO<Integer>(TAX.SALES_ACCOUNT);}
		@Override public Property<Double> getSurchargeProperty() {return new FilterDAO.PropertyDAO<Double>(TAX.SURCHARGE);}
		@Override public Property<Byte> getTaxTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(TAX.TAX_TYPE);}
		@Override public Property<Byte> getVatDeductionTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(TAX.VAT_DEDUCTION_TYPE);}
		@Override public Property<Byte> getWithholdingTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(TAX.WITHHOLDING_TYPE);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(TAX.CREATION_DATE);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<String>(TAX.CREATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(TAX.MODIFICATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<String>(TAX.MODIFICATION_USER);}
	}
	
	public static Tax getTax(AONContext ctx, TaxFilter filter){
		return getTaxs(ctx, filter)
			.findFirst()
			.orElse(new Tax());
	}
	
	
	public static Stream<Tax> getTaxs(AONContext ctx, TaxFilter filter){
		
		return ctx.getDslContext()
			.select()
			.from(TAX)
			.leftOuterJoin(SALES_ACCOUNT).on(SALES_ACCOUNT.ID.eq(TAX.SALES_ACCOUNT))
			.leftOuterJoin(PURCHASE_ACCOUNT).on(PURCHASE_ACCOUNT.ID.eq(TAX.PURCHASE_ACCOUNT))
			.where(TAX_PROPERTIES.getConditions(filter))
			.fetch()
			.stream()
			.map(new FullTaxFiller());
	}
	public static Stream<Tax> getVatTaxs(AONContext ctx, Date atDate){
		// TODO Implementar la búsqueda en una fecha concreta.
		return TaxDAO.getTaxs(ctx, p -> 
					p.getDomainProperty().in(SecurityDAO.getInheritanceDomainIds(ctx))
					.and(p.getTaxTypeProperty().eq(TaxType.VAT.value())));
	}
	public static Stream<Tax> getWithholdingTaxs(AONContext ctx, Date atDate){
		// TODO Implementar la búsqueda en una fecha concreta.
		return TaxDAO.getTaxs(ctx, p -> 
					p.getDomainProperty().in(SecurityDAO.getInheritanceDomainIds(ctx))
					.and(p.getTaxTypeProperty().eq(TaxType.RETENTION.value())));
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
}
