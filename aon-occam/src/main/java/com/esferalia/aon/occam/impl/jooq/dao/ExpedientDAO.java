package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.ActivityType.ACTIVITY_TYPE;
import static com.esferalia.aon.jooq.tables.DailyTracking.DAILY_TRACKING;
import static com.esferalia.aon.jooq.tables.Delivery.DELIVERY;
import static com.esferalia.aon.jooq.tables.DeliveryDetail.DELIVERY_DETAIL;
import static com.esferalia.aon.jooq.tables.Income.INCOME;
import static com.esferalia.aon.jooq.tables.IncomeDetail.INCOME_DETAIL;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.JobType.JOB_TYPE;
import static com.esferalia.aon.jooq.tables.Offer.OFFER;
import static com.esferalia.aon.jooq.tables.OfferDetail.OFFER_DETAIL;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Project.PROJECT;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record10;
import org.jooq.Record7;
import org.jooq.SelectSeekStep1;
import org.jooq.SelectSeekStep2;
import org.jooq.SelectSeekStep3;
import org.jooq.SelectSeekStep4;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Expedient;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.ProjectFilter;
import com.esferalia.aon.occam.api.model.Properties.ProjectProperties;
import com.esferalia.aon.watson.util.AonStringUtils;

public class ExpedientDAO {
	private static final ProjectPropertiesDAO PROJECT_PROPERTIES = new ProjectPropertiesDAO();
	
	protected static class ProjectPropertiesDAO implements ProjectProperties {
		protected Condition[] getConditions(ProjectFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(PROJECT.ID);} 
		@Override public Property<Byte> getActiveProperty() {return new FilterDAO.PropertyDAO<Byte>(PROJECT.ACTIVE);}
		@Override public Property<String> getAliasProperty() {return new FilterDAO.PropertyDAO<String>(PROJECT.ALIAS);}
		@Override public Property<Byte> getCommercialProperty() {return new FilterDAO.PropertyDAO<Byte>(PROJECT.COMMERCIAL);}
		@Override public Property<Date> getDateProperty() {return new FilterDAO.PropertyDAO<Date>(PROJECT.DATE);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(PROJECT.DOMAIN);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<String>(PROJECT.NAME);}
		@Override public Property<Integer> getProjectTypeProperty() {return new FilterDAO.PropertyDAO<Integer>(PROJECT.PROJECT_TYPE);}
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<Integer>(PROJECT.REGISTRY);}
		@Override public Property<Byte> getReservationProperty() {return new FilterDAO.PropertyDAO<Byte>(PROJECT.RESERVATION);}
		@Override public Property<Byte> getTasProperty() {return new FilterDAO.PropertyDAO<Byte>(PROJECT.TAS);}
	}
	public static Stream<Expedient> resumeExpedient(AONContext ctx, Integer domain, ProjectFilter filter) {
		return resumeInvoice(ctx, domain, filter)
		.union(resumeIncome(ctx, domain, filter))
		.union(resumeDelivery(ctx, domain, filter))
		.union(resumeOffer(ctx, domain, filter))
		.union(resumeJob(ctx, domain, filter))
		.fetch().stream().map(new ResumeExpedientFiller());
	}
	
	public static Stream<Expedient> fullExpedient(AONContext ctx, Integer domain, ProjectFilter filter) {
		return fullInvoice(ctx, domain, filter)
		.union(fullIncome(ctx, domain, filter))
		.union(fullDelivery(ctx, domain, filter))
		.union(fullOffer(ctx, domain, filter))
		.union(fullJob(ctx, domain, filter))
		.fetch().stream().map(new FullExpedientFiller());
	}
	
	private static SelectSeekStep4<Record7<Integer, String, String, BigDecimal, Integer, String, String>, Integer, Integer, Byte, String> resumeInvoice(AONContext ctx, Integer domain, ProjectFilter filter) {
		return ctx.getDslContext().select(
				DSL.year(INVOICE.ISSUE_DATE).as("Year"),
				DSL.when(INVOICE.TYPE.eq((byte)0), "Fra.Compras")
					.when(INVOICE.TYPE.eq((byte)1), "Fra.Ventas")
					.when(INVOICE.TYPE.eq((byte)2), "Fra.Gastos")
					.when(INVOICE.TYPE.eq((byte)3), "Fra.No.deducible").as("Tipo"), 
				PRODUCT.NAME.as("Concept"), DSL.sum(INVOICE_DETAIL.TAXABLE_BASE).as("Base"), 
				INVOICE_DETAIL.PROJECT.as("Expediente"), PROJECT.ALIAS, PROJECT.NAME)
		.from(INVOICE).join(INVOICE_DETAIL).on(INVOICE.ID.eq(INVOICE_DETAIL.INVOICE))
			.join(ITEM).on(INVOICE_DETAIL.ITEM.eq(ITEM.ID))
			.join(PRODUCT).on(ITEM.PRODUCT.eq(PRODUCT.ID))
			.join(PROJECT).on(INVOICE_DETAIL.PROJECT.eq(PROJECT.ID))
		.where(PROJECT_PROPERTIES.getConditions(filter))
		.groupBy(PROJECT.ID, DSL.year(INVOICE.ISSUE_DATE), INVOICE.TYPE, PRODUCT.NAME)
		.orderBy(PROJECT.ID, DSL.year(INVOICE.ISSUE_DATE), INVOICE.TYPE, PRODUCT.NAME);
	}
	
	private static SelectSeekStep3<Record7<Integer, String, String, BigDecimal, Integer, String, String>, Integer, Integer, String> resumeIncome(AONContext ctx, Integer domain, ProjectFilter filter) {
		return ctx.getDslContext().select(
				DSL.year(INCOME.ISSUE_TIME).as("Year"),
				DSL.inline("Alb.Compras").as("Tipo"),
				PRODUCT.NAME.as("Concept"),
				DSL.sum(INCOME_DETAIL.PRICE).as("Base"),
				INCOME_DETAIL.PROJECT.as("Expediente"), PROJECT.ALIAS, PROJECT.NAME)
		.from(INCOME).join(INCOME_DETAIL).on(INCOME.ID.eq(INCOME_DETAIL.INCOME).and(INCOME.STATUS.eq((byte)0)))
			.join(ITEM).on(INCOME_DETAIL.ITEM.eq(ITEM.ID))
			.join(PRODUCT).on(ITEM.PRODUCT.eq(PRODUCT.ID))
			.join(PROJECT).on(INCOME_DETAIL.PROJECT.eq(PROJECT.ID))
		.where(PROJECT_PROPERTIES.getConditions(filter))
		.groupBy(PROJECT.ID, DSL.year(INCOME.ISSUE_TIME), PRODUCT.NAME)
		.orderBy(PROJECT.ID, DSL.year(INCOME.ISSUE_TIME), PRODUCT.NAME);
	}
	
	private static SelectSeekStep3<Record7<Integer, String, String, BigDecimal, Integer, String, String>, Integer, Integer, String> resumeDelivery(AONContext ctx, Integer domain, ProjectFilter filter) {
		return ctx.getDslContext().select(
				DSL.year(DELIVERY.ISSUE_TIME).as("Year"),
				DSL.inline("Alb.Ventas").as("Tipo"),
				PRODUCT.NAME.as("Concept"), 
				DSL.sum(DELIVERY_DETAIL.PRICE).as("Base"), 
				DELIVERY.PROJECT.as("Expediente"), PROJECT.ALIAS, PROJECT.NAME)
		.from(DELIVERY).join(DELIVERY_DETAIL).on(DELIVERY.ID.eq(DELIVERY_DETAIL.DELIVERY).and(DELIVERY.STATUS.eq((byte) 0)))
			.join(ITEM).on(DELIVERY_DETAIL.ITEM.eq(ITEM.ID))
			.join(PRODUCT).on(ITEM.PRODUCT.eq(PRODUCT.ID))
			.join(PROJECT).on(DELIVERY.PROJECT.eq(PROJECT.ID))
		.where(PROJECT_PROPERTIES.getConditions(filter))
		.groupBy(PROJECT.ID, DSL.year(DELIVERY.ISSUE_TIME), PRODUCT.NAME)
		.orderBy(PROJECT.ID, DSL.year(DELIVERY.ISSUE_TIME), PRODUCT.NAME);
	}
	
	private static SelectSeekStep3<Record7<Integer, String, String, BigDecimal, Integer, String, String>, Integer, Integer, String> resumeOffer(AONContext ctx, Integer domain, ProjectFilter filter) {
		return ctx.getDslContext().select(
				DSL.year(OFFER.ISSUE_DATE).as("Year"),
				DSL.inline("Ppto.Ventas").as("Tipo"),
				PRODUCT.NAME.as("Concept"),
				DSL.sum(OFFER_DETAIL.PRICE).as("Base"),
				OFFER.PROJECT.as("Expediente"), PROJECT.ALIAS, PROJECT.NAME)
		.from(OFFER).join(OFFER_DETAIL).on(OFFER.ID.eq(OFFER_DETAIL.OFFER))
			.join(ITEM).on(OFFER_DETAIL.ITEM.eq(ITEM.ID))
			.join(PRODUCT).on(ITEM.PRODUCT.eq(PRODUCT.ID))
			.join(PROJECT).on(OFFER.PROJECT.eq(PROJECT.ID))
		.where(PROJECT_PROPERTIES.getConditions(filter))
		.groupBy(PROJECT.ID, DSL.year(OFFER.ISSUE_DATE), PRODUCT.NAME)
		.orderBy(PROJECT.ID, DSL.year(OFFER.ISSUE_DATE), PRODUCT.NAME);
	}
	
	private static SelectSeekStep3<Record7<Integer, String, String, BigDecimal, Integer, String, String>,Integer, Integer, String> resumeJob(AONContext ctx, Integer domain, ProjectFilter filter) {
		return ctx.getDslContext().select(
				DSL.year(DAILY_TRACKING.TRACKING_DATE).as("Year"),
				DSL.inline("Mano.Obra").as("Tipo"),
				JOB_TYPE.DESCRIPTION.as("Concept"),
				DSL.sum(DAILY_TRACKING.COST.mul(DAILY_TRACKING.TRACKING_DURATION)).as("Base"),
				DAILY_TRACKING.PROJECT.as("Expediente"), PROJECT.ALIAS, PROJECT.NAME)
		.from(DAILY_TRACKING).join(JOB_TYPE).on(DAILY_TRACKING.JOB_TYPE.eq(JOB_TYPE.ID))
			.join(PROJECT).on(DAILY_TRACKING.PROJECT.eq(PROJECT.ID))
			.leftOuterJoin(ACTIVITY_TYPE).on(DAILY_TRACKING.ACTIVITY_TYPE.eq(ACTIVITY_TYPE.ID))
		.where(PROJECT_PROPERTIES.getConditions(filter))
		.groupBy(PROJECT.ID, DSL.year(DAILY_TRACKING.TRACKING_DATE), JOB_TYPE.DESCRIPTION)
		.orderBy(PROJECT.ID,DSL.year(DAILY_TRACKING.TRACKING_DATE), JOB_TYPE.DESCRIPTION);
	}
	
	private static SelectSeekStep2<Record10<String, String, java.sql.Date, Integer, Double, String, Integer, String, String, Integer>, Byte, java.sql.Date> fullInvoice(AONContext ctx, Integer domain, ProjectFilter filter) {
		return ctx.getDslContext().select(DSL.when(INVOICE.TYPE.eq((byte)0), "Fra.Compras")
											.when(INVOICE.TYPE.eq((byte)1), "Fra.Ventas")
											.when(INVOICE.TYPE.eq((byte)2), "Fra.Gastos")
											.when(INVOICE.TYPE.eq((byte)3), "Fra.No.deducible").as("Tipo"), 
				INVOICE.REFERENCE_CODE.as("Document"),
				INVOICE.ISSUE_DATE.as("Date"), INVOICE_DETAIL.PROJECT.as("Expediente"), INVOICE_DETAIL.TAXABLE_BASE.as("Base"), PRODUCT.NAME.as("Concept"),
				INVOICE.NUMBER.as("Number"), PROJECT.ALIAS, PROJECT.NAME, DSL.year(INVOICE.ISSUE_DATE).as("Year"))
		.from(INVOICE).join(INVOICE_DETAIL).on(INVOICE.ID.eq(INVOICE_DETAIL.INVOICE))
			.join(ITEM).on(INVOICE_DETAIL.ITEM.eq(ITEM.ID))
			.join(PRODUCT).on(ITEM.PRODUCT.eq(PRODUCT.ID))
			.join(PROJECT).on(INVOICE_DETAIL.PROJECT.eq(PROJECT.ID))
		.where(PROJECT_PROPERTIES.getConditions(filter))
		.orderBy(INVOICE.TYPE, INVOICE.ISSUE_DATE);
	}
	
	private static SelectSeekStep1<Record10<String, String, java.sql.Date, Integer, Double, String, Integer, String, String, Integer>, java.sql.Date> fullIncome(AONContext ctx, Integer domain, ProjectFilter filter) {
		return ctx.getDslContext().select(DSL.inline("Alb.Compras").as("Tipo"),
				INCOME.REFERENCE_CODE.as("Document"),
				INCOME.ISSUE_TIME.as("Date"), INCOME_DETAIL.PROJECT.as("Expedient"), 
				INCOME_DETAIL.PRICE.as("Base"), PRODUCT.NAME.as("Concept"),
				INCOME.ID.as("Number"), PROJECT.ALIAS, PROJECT.NAME, DSL.year(INCOME.ISSUE_TIME).as("Year"))
		.from(INCOME).join(INCOME_DETAIL).on(INCOME.ID.eq(INCOME_DETAIL.INCOME).and(INCOME.STATUS.eq((byte) 0)))
			.join(ITEM).on(INCOME_DETAIL.ITEM.eq(ITEM.ID))
			.join(PRODUCT).on(ITEM.PRODUCT.eq(PRODUCT.ID))
			.join(PROJECT).on(INCOME_DETAIL.PROJECT.eq(PROJECT.ID))
		.where(PROJECT_PROPERTIES.getConditions(filter))
		.orderBy(INCOME.ISSUE_TIME);
	}
	
	private static SelectSeekStep1<Record10<String, String, java.sql.Date, Integer, Double, String, Integer, String, String, Integer>, java.sql.Date> fullDelivery(AONContext ctx, Integer domain, ProjectFilter filter) {
		return ctx.getDslContext().select(DSL.inline("Alb.Ventas").as("Tipo"),
				DSL.concat(DELIVERY.SERIES, DSL.inline("/")).as("Document"),
				DSL.date(DELIVERY.ISSUE_TIME).as("Date"), DELIVERY.PROJECT.as("Expedient"), 
				DELIVERY_DETAIL.PRICE.as("Base"), PRODUCT.NAME.as("Concept"),
				DELIVERY.NUMBER.as("Number"), PROJECT.ALIAS, PROJECT.NAME, DSL.year(DELIVERY.ISSUE_TIME).as("Year"))
		.from(DELIVERY).join(DELIVERY_DETAIL).on(DELIVERY.ID.eq(DELIVERY_DETAIL.DELIVERY).and(DELIVERY.STATUS.eq((byte) 0)))
			.join(ITEM).on(DELIVERY_DETAIL.ITEM.eq(ITEM.ID))
			.join(PRODUCT).on(ITEM.PRODUCT.eq(PRODUCT.ID))
			.join(PROJECT).on(DELIVERY.PROJECT.eq(PROJECT.ID))
		.where(PROJECT_PROPERTIES.getConditions(filter))
		.orderBy(DSL.date(DELIVERY.ISSUE_TIME));
	}
	
	private static SelectSeekStep1<Record10<String, String, java.sql.Date, Integer, Double, String, Integer, String, String, Integer>, java.sql.Date> fullOffer(AONContext ctx, Integer domain, ProjectFilter filter) {
		return ctx.getDslContext().select(DSL.inline("Ppto.Ventas").as("Tipo"), 
				DSL.concat(OFFER.SERIES, DSL.inline("/")).as("Document"),
				OFFER.ISSUE_DATE.as("Date"), OFFER.PROJECT.as("Expedient"), OFFER_DETAIL.PRICE.as("Base"), PRODUCT.NAME.as("Concept"),
				OFFER.NUMBER.as("Number"), PROJECT.ALIAS, PROJECT.NAME, DSL.year(OFFER.ISSUE_DATE).as("Year"))
		.from(OFFER).join(OFFER_DETAIL).on(OFFER.ID.eq(OFFER_DETAIL.OFFER))
			.join(ITEM).on(OFFER_DETAIL.ITEM.eq(ITEM.ID))
			.join(PRODUCT).on(ITEM.PRODUCT.eq(PRODUCT.ID))
			.join(PROJECT).on(OFFER.PROJECT.eq(PROJECT.ID))
		.where(PROJECT_PROPERTIES.getConditions(filter))
		.orderBy(OFFER.ISSUE_DATE);
	}
	
	private static SelectSeekStep1<Record10<String, String, java.sql.Date, Integer, Double, String, Integer, String, String, Integer>, java.sql.Date> fullJob(AONContext ctx, Integer domain, ProjectFilter filter) {
		return ctx.getDslContext().select(DSL.inline("Mano.Obra").as("Tipo"), DSL.concat(ACTIVITY_TYPE.DESCRIPTION, "").as("Document"), DAILY_TRACKING.TRACKING_DATE.as("Date"), DAILY_TRACKING.PROJECT.as("Expedient"),
				DAILY_TRACKING.COST.mul(DAILY_TRACKING.TRACKING_DURATION).as("Base"), JOB_TYPE.DESCRIPTION.as("Concept"),
				DAILY_TRACKING.ID.as("Number"), PROJECT.ALIAS, PROJECT.NAME, DSL.year(DAILY_TRACKING.TRACKING_DATE).as("Year"))
		.from(DAILY_TRACKING).join(JOB_TYPE).on(DAILY_TRACKING.JOB_TYPE.eq(JOB_TYPE.ID))
			.join(PROJECT).on(DAILY_TRACKING.PROJECT.eq(PROJECT.ID))
			.leftOuterJoin(ACTIVITY_TYPE).on(DAILY_TRACKING.ACTIVITY_TYPE.eq(ACTIVITY_TYPE.ID))
		.where(PROJECT_PROPERTIES.getConditions(filter))
		.orderBy(DAILY_TRACKING.TRACKING_DATE);
	}
	
	private static class ResumeExpedientFiller implements Function<Record7<Integer, String, String, BigDecimal, Integer, String, String>, Expedient> {
		
		@Override
		public Expedient apply(Record7<Integer, String, String, BigDecimal, Integer, String, String> r) {
			return new Expedient()
				.setYear(r.value1())
				.setType(r.value2())
				.setConcept(r.value3())
				.setBase(r.value4().doubleValue())
				.setExpendient(r.value5())
				.setAlias(r.value6())
				.setName(r.value7());
		}
	}
	
	private static class FullExpedientFiller implements Function<Record10<String, String, java.sql.Date, Integer, Double, String, Integer, String, String, Integer>, Expedient> {
		
		@Override
		public Expedient apply(Record10<String, String, java.sql.Date, Integer, Double, String, Integer, String, String, Integer> r) {
			String document = r.value2();
			if(document != null && "/".equals(document.substring(document.length()-1))) {
				String n = AonStringUtils.leftPad(r.value7().toString(), 5, "0");
				if(document.length() > 1) {
					document = document + n;
				} else document = n;
			}
			return new Expedient()
				.setYear(r.value10())	
				.setType(r.value1())
				.setDocument(document)
				.setDate(r.value3())
				.setExpendient(r.value4())
				.setBase(r.value5())
				.setConcept(r.value6())
				.setAlias(r.value8())
				.setName(r.value9());
		}
	}
}
