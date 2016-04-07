package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Tax.TAX;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.function.Function;

import org.jooq.Condition;

import com.esferalia.aon.jooq.tables.records.TaxRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.TaxFilter;
import com.esferalia.aon.occam.api.model.Properties.TaxProperties;
import com.esferalia.aon.occam.api.model.product.Tax;

public class TaxDAO {
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
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(TAX.CREATION_DATE);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<String>(TAX.CREATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(TAX.MODIFICATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<String>(TAX.MODIFICATION_USER);}
		@Override public Property<Double> getPercentageProperty() {return new FilterDAO.PropertyDAO<Double>(TAX.PERCENTAGE);}
		@Override public Property<Integer> getPurchaseAccountProperty() {return new FilterDAO.PropertyDAO<Integer>(TAX.PURCHASE_ACCOUNT);}
		@Override public Property<Integer> getSalesAccountProperty() {return new FilterDAO.PropertyDAO<Integer>(TAX.SALES_ACCOUNT);}
		@Override public Property<Date> getStartDateProperty() {return new FilterDAO.PropertyDAO<Date>(TAX.START_DATE);}
		@Override public Property<Double> getSurchargeProperty() {return new FilterDAO.PropertyDAO<Double>(TAX.SURCHARGE);}
		@Override public Property<Byte> getTaxTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(TAX.TAX_TYPE);}
		@Override public Property<Byte> getVatDeductionTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(TAX.VAT_DEDUCTION_TYPE);}
		@Override public Property<Byte> getWithholdingTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(TAX.WITHHOLDING_TYPE);}
	}
	
	public static Tax getTax(AONContext ctx, TaxFilter filter){
		return ctx.getDslContext().select().from(TAX).where(TAX_PROPERTIES.getConditions(filter)).limit(1)
				.fetchInto(TAX).stream().map(new FullTaxFiller()).findFirst().orElse(new Tax());
	}
	
	
	private static class FullTaxFiller implements Function<TaxRecord, Tax> {
		@Override
		public Tax apply(TaxRecord r) {
			return new Tax();		
		}
	}
}
