package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.InvestAsset.INVEST_ASSET;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;

import java.sql.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.Filter.InvestAssetFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.InvestAsset;
import com.esferalia.aon.occam.api.model.InvestAssetParams;
import com.esferalia.aon.occam.api.model.InvestAssetRegime;
import com.esferalia.aon.occam.api.model.InvestAssetType;
import com.esferalia.aon.occam.api.model.Properties.InvestAssetProperties;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO.EnterpriseActivityFiller;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class InvestAssetDAO {
	
	private InvestAssetDAO() {
		
	}

	private static final  InvestAssetPropertiesDAO INVEST_ASSET_PROPERTIES = new InvestAssetPropertiesDAO();

	protected static class InvestAssetPropertiesDAO implements InvestAssetProperties {
		protected Condition[] getConditions(InvestAssetFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}

		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(INVEST_ASSET.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(INVEST_ASSET.DOMAIN);}
		@Override public Property<Integer> getActivityProperty() {return new FilterDAO.PropertyDAO<>(INVEST_ASSET.ACTIVITY);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(INVEST_ASSET.DESCRIPTION);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(INVEST_ASSET.TYPE);}
		@Override public Property<Byte> getRegimeProperty() {return new FilterDAO.PropertyDAO<>(INVEST_ASSET.REGIME);}
		@Override public Property<Date> getStartDateProperty() {return new FilterDAO.PropertyDAO<>(INVEST_ASSET.START_DATE);}
		@Override public Property<Date> getEndDateProperty() {return new FilterDAO.PropertyDAO<>(INVEST_ASSET.END_DATE);}
		@Override public Property<Double> getVatPercentProperty() {return new FilterDAO.PropertyDAO<>(INVEST_ASSET.VAT_PERCENT);}
		@Override public Property<Double> getRetentionPercentProperty() {return new FilterDAO.PropertyDAO<>(INVEST_ASSET.RETENTION_PERCENT);}
	}

	public static InvestAsset getInvestAsset(CloseableAONContext ctx, Integer id) {
		Record investAssetRecord = ctx.getDslContext().select().from(INVEST_ASSET)
				.where(INVEST_ASSET.ID.eq(id))
				.fetchOne();
		
		return new InvestAssetFiller().apply(investAssetRecord);
	}
	
	public static List<InvestAsset> getInvestAssetList(CloseableAONContext ctx, InvestAssetParams params) {
		Condition condition = paramsToCondition(ctx, params);
		
		List<InvestAsset> investAssets = ctx.getDslContext().select().from(INVEST_ASSET)
			.where(condition)
			.fetch()
			.stream()
			.map(new InvestAssetFiller())
			.collect(Collectors.toList());
		
		return investAssets;
	}	
	
	private static Condition paramsToCondition(CloseableAONContext ctx, InvestAssetParams params) {
		Condition condition = INVEST_ASSET.DOMAIN.in(SecurityDAO.getInheritanceDomainIds(ctx));
		
		if(AonStringUtils.isNotBlank(params.getDescription()))
			condition = condition.and(INVEST_ASSET.DESCRIPTION.like("%" + params.getDescription() + "%"));
		
		if(null != params.getActivity())
			condition = condition.and(INVEST_ASSET.ACTIVITY.eq(params.getActivity()));
		
		if(null != params.getType())
			condition = condition.and(INVEST_ASSET.TYPE.eq(params.getType()));
		
		if(null != params.getRegime())
			condition = condition.and(INVEST_ASSET.REGIME.eq(params.getRegime()));
		
		if(null != params.getVatPercent() && params.getVatPercent() != 0.00)
			condition = condition.and(INVEST_ASSET.VAT_PERCENT.eq(params.getVatPercent()));
		
		if(null != params.getRetentionPercent() && params.getRetentionPercent() != 0.00)
			condition = condition.and(INVEST_ASSET.RETENTION_PERCENT.eq(params.getRetentionPercent()));
		
		if(null != params.getStartDate())
			condition = condition.and(INVEST_ASSET.START_DATE.eq(AonDateUtils.toSql(params.getStartDate())));
		
		if(null != params.getEndDate())
			condition = condition.and(INVEST_ASSET.END_DATE.eq(AonDateUtils.toSql(params.getEndDate())));
		
		return condition;
	}

	public static SelectConditionStep<Record> select(AONContext ctx, InvestAssetFilter filter) {
		return ctx.getDslContext().select().from(INVEST_ASSET)
				.leftOuterJoin(ENTERPRISE_ACTIVITY).on(ENTERPRISE_ACTIVITY.ID.eq(INVEST_ASSET.ACTIVITY))
				.where(INVEST_ASSET_PROPERTIES.getConditions(filter));
	}
	
	public static Stream<InvestAsset> getStream(AONContext ctx, InvestAssetFilter filter){
		return select(ctx, filter).fetch().stream().map(new InvestAssetFiller());
	}

	public static List<InvestAsset> getList(AONContext ctx, InvestAssetFilter filter) {
		return getStream(ctx, filter).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static InvestAsset get(AONContext ctx, InvestAssetFilter filter) {
		return select(ctx, filter).limit(1).fetch().stream().map(new InvestAssetFiller()).findFirst().orElse(new InvestAsset());
	}
	
	public static InvestAsset save(AONContext ctx, InvestAsset investAsset) {
		return investAsset.getId() != null
				? update(ctx, investAsset)
				: insert(ctx, investAsset);
	}
	
	public static InvestAsset insert(AONContext ctx, InvestAsset investAsset) {
		Integer id = ctx.getDslContext().insertInto(INVEST_ASSET)
				.set(INVEST_ASSET.DOMAIN, investAsset.getDomain())
				.set(INVEST_ASSET.DESCRIPTION, investAsset.getDescription())
				.set(INVEST_ASSET.ACTIVITY, investAsset.getActivity().getId())
				.set(INVEST_ASSET.TYPE, investAsset.getType().value())
				.set(INVEST_ASSET.REGIME, investAsset.getRegime().value())
				.set(INVEST_ASSET.START_DATE, AonDateUtils.toSql(investAsset.getStartDate()))
				.set(INVEST_ASSET.END_DATE, AonDateUtils.toSql(investAsset.getEndDate()))
				.set(INVEST_ASSET.VAT_PERCENT, investAsset.getVatPercent())
				.set(INVEST_ASSET.RETENTION_PERCENT, investAsset.getRetentionPercent())
				.returning(INVEST_ASSET.ID).fetchOne().getValue(INVEST_ASSET.ID);
		ctx.log().debug("INSERT INVEST_ASSET id: " +id);	
		return investAsset.setId(id);
	}
	
	public static InvestAsset update(AONContext ctx, InvestAsset investAsset) {
		ctx.getDslContext().update(INVEST_ASSET)
				.set(INVEST_ASSET.DOMAIN, investAsset.getDomain())
				.set(INVEST_ASSET.DESCRIPTION, investAsset.getDescription())
				.set(INVEST_ASSET.ACTIVITY, investAsset.getActivity().getId())
				.set(INVEST_ASSET.TYPE, investAsset.getType().value())
				.set(INVEST_ASSET.REGIME, investAsset.getRegime().value())
				.set(INVEST_ASSET.START_DATE, AonDateUtils.toSql(investAsset.getStartDate()))
				.set(INVEST_ASSET.END_DATE, AonDateUtils.toSql(investAsset.getEndDate()))
				.set(INVEST_ASSET.VAT_PERCENT, investAsset.getVatPercent())
				.set(INVEST_ASSET.RETENTION_PERCENT, investAsset.getRetentionPercent())
				.where(INVEST_ASSET.ID.eq(investAsset.getId())).execute();
		ctx.log().debug("UPDATE INVEST_ASSET id:" + investAsset.getId());
		return investAsset;
	}

	public static void delete(AONContext ctx, Integer id) {
		ctx.getDslContext().delete(INVEST_ASSET).where(INVEST_ASSET.ID.eq(id)).execute();
		ctx.log().debug("DELETE INVEST_ASSET id:" + id);
	}
	
	
	public static void assignInvestAsset2Invoice(AONContext ctx, Integer investAssetId, Invoice invoice) {
		ctx.getDslContext()
		.update(INVOICE)
		.set(INVOICE.INVEST_ASSET, investAssetId)
		.where(INVOICE.ID.eq(invoice.getId()))
		.execute();
		
		ctx.getDslContext()
		.update(INVOICE_DETAIL)
		.set(INVOICE_DETAIL.INVEST_ASSET, investAssetId)
		.where(INVOICE_DETAIL.INVOICE.eq(invoice.getId()))
		.execute();
	}

	public static class InvestAssetFiller extends Filler implements Function<Record, InvestAsset> {

		@Override
		public InvestAsset apply(Record r) {
			return build(r);
		}

		public static InvestAsset build(Record r) {
			return new InvestAsset()
				.setId(r.getValue(INVEST_ASSET.ID))
				.setDomain(r.getValue(INVEST_ASSET.DOMAIN))
				.setDescription(r.getValue(INVEST_ASSET.DESCRIPTION))
				.setActivity(checkField(r, ENTERPRISE_ACTIVITY.ID)
						? EnterpriseActivityFiller.build(r)
						: new EnterpriseActivity().setId(r.getValue(INVEST_ASSET.ACTIVITY)))						
				.setType(InvestAssetType.safeValueOf(r.getValue(INVEST_ASSET.TYPE)))
				.setRegime(InvestAssetRegime.safeValueOf(r.getValue(INVEST_ASSET.REGIME)))
				.setStartDate(r.getValue(INVEST_ASSET.START_DATE))
				.setEndDate(r.getValue(INVEST_ASSET.END_DATE))
				.setRetentionPercent(r.getValue(INVEST_ASSET.RETENTION_PERCENT))
				.setVatPercent(r.getValue(INVEST_ASSET.VAT_PERCENT))
				.setPercent(r.getValue(INVEST_ASSET.VAT_PERCENT));
		}
	}
}
