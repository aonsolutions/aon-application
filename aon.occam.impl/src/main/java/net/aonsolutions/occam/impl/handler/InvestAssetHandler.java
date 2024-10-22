package net.aonsolutions.occam.impl.handler;

import static com.esferalia.aon.jooq.tables.Cnae2009.CNAE2009;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.Iae.IAE;
import static com.esferalia.aon.jooq.tables.InvestAsset.INVEST_ASSET;

import java.util.Date;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;

import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;

import net.aonsolutions.occam.api.model.InvestAsset;
import net.aonsolutions.occam.api.model.type.InvestAssetRegime;
import net.aonsolutions.occam.api.model.type.InvestAssetType;
import net.aonsolutions.occam.impl.AONContext;
import net.aonsolutions.occam.impl.handler.ActivityHandler.ActivityFiller;

class InvestAssetHandler {
	private InvestAssetHandler() {
		
	}

	static Stream<InvestAsset> stream(AONContext ctx, Integer domain, Date atDate) {
		ctx.checkRead();
		if (domain == null) throw new AonCoreException(AonError.NULL_FILTER.getMessage());
		Condition cond = INVEST_ASSET.DOMAIN.eq(domain);
		if (atDate != null) {
			cond.and(INVEST_ASSET.START_DATE.isNull().or(INVEST_ASSET.START_DATE.le(AonDateUtils.toSql(atDate))));
			cond.and(INVEST_ASSET.END_DATE.isNull().or(INVEST_ASSET.END_DATE.ge(AonDateUtils.toSql(atDate))));
		}
		return ctx.getDslContext()
			.select()
			.from(INVEST_ASSET)
			.leftOuterJoin(ENTERPRISE_ACTIVITY).on(ENTERPRISE_ACTIVITY.ID.eq(INVEST_ASSET.ACTIVITY))
			.leftOuterJoin(CNAE2009).on(CNAE2009.ID.eq(ENTERPRISE_ACTIVITY.CNAE2009))
			.leftOuterJoin(IAE).on(IAE.ID.eq(ENTERPRISE_ACTIVITY.IAE))
			.where(cond)
			.fetch()	
			.stream()
			.map(new InvestAssetFiller());
	}
	
	static class InvestAssetFiller extends Filler<InvestAsset> {

		@Override
		public InvestAsset apply(Record r) {
			return build(r);
		}

		public static InvestAsset build(Record r) {
	    	if (isNull(r, INVEST_ASSET.ID)) return null;
			return new InvestAsset()
				.setId(getValue(r, INVEST_ASSET.ID))
				.setDomain(getValue(r, INVEST_ASSET.DOMAIN))
				.setDescription(getValue(r, INVEST_ASSET.DESCRIPTION))
				.setActivity(ActivityFiller.build(r))						
				.setType(InvestAssetType.value(getValue(r, INVEST_ASSET.TYPE)).orElse(null))
				.setRegime(InvestAssetRegime.value(getValue(r, INVEST_ASSET.REGIME)).orElse(null))
				.setStartDate(getValue(r, INVEST_ASSET.START_DATE))
				.setEndDate(getValue(r, INVEST_ASSET.END_DATE))
				.setRetentionPercent(getDouble(r, INVEST_ASSET.RETENTION_PERCENT))
				.setVatPercent(getDouble(r, INVEST_ASSET.VAT_PERCENT))
				.setProperties(getValue(r, INVEST_ASSET.PROPERTIES));
		}
	}
	
}
