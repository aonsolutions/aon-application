package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.FsActivity.FS_ACTIVITY;
import static com.esferalia.aon.jooq.tables.FsActivityInfo.FS_ACTIVITY_INFO;

import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Record;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivity;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivityInfo;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivityInfoKey;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivityInfoKeyType;
import com.esferalia.aon.occam.api.model.fiscal.modules.Module;
import com.esferalia.aon.occam.api.model.fiscal.modules.Modules2016.Epigraph;
import com.esferalia.aon.watson.server.AonEnumUtils;


public class FiscalActivityDAO {
	
	public static FiscalActivity calculate(AONContext ctx,FiscalActivity fa) {
		return fa;
	}
	
	private static void fillFiscalActivityInfo(AONContext ctx,FiscalActivity fa) {
		ctx.getDslContext().select(
				 FS_ACTIVITY_INFO.ID
				,FS_ACTIVITY_INFO.FS_ACTIVITY
				,FS_ACTIVITY_INFO.DOMAIN
				,FS_ACTIVITY_INFO.INFO_KEY
				,FS_ACTIVITY_INFO.LINE
				,FS_ACTIVITY_INFO.TYPE
				,FS_ACTIVITY_INFO.BASE
				,FS_ACTIVITY_INFO.FACTOR
				,FS_ACTIVITY_INFO.MAX_VALUE
				,FS_ACTIVITY_INFO.MIN_VALUE
				,FS_ACTIVITY_INFO.UNIT
				,FS_ACTIVITY_INFO.VALUE
			)
		.from(FS_ACTIVITY_INFO)
		.where(FS_ACTIVITY_INFO.FS_ACTIVITY.eq(fa.getId()))
		.orderBy(FS_ACTIVITY_INFO.LINE)
		.fetch()
		.stream()
		.forEach(record ->   
			fa.add(new FiscalActivityInfo()
				.setId(record.getValue(FS_ACTIVITY_INFO.ID))
				.setFiscalActivity(record.getValue(FS_ACTIVITY_INFO.FS_ACTIVITY))
				.setInfoKey(FiscalActivityInfoKey.safeValueOf(record.getValue(FS_ACTIVITY_INFO.INFO_KEY)) )
				.setInfoType(com.esferalia.aon.watson.util.AonEnumUtils.enumValue(
						FiscalActivityInfoKeyType.class
						, record.getValue(FS_ACTIVITY_INFO.TYPE)))
				.setLine(record.getValue(FS_ACTIVITY_INFO.LINE))
				.setValue(record.getValue(FS_ACTIVITY_INFO.VALUE))
				.setFactor(record.getValue(FS_ACTIVITY_INFO.FACTOR))
				.setBase(record.getValue(FS_ACTIVITY_INFO.BASE))
				.setUnit(record.getValue(FS_ACTIVITY_INFO.UNIT))
				.setMinValue(record.getValue(FS_ACTIVITY_INFO.MIN_VALUE))
				.setMaxValue(record.getValue(FS_ACTIVITY_INFO.MAX_VALUE))
		));
	}

	public static Stream<FiscalActivity> getActivities(AONContext ctx,int domain) {
		ctx.checkRead();
		return ctx.getDslContext().select(
				 FS_ACTIVITY.ID
				,FS_ACTIVITY.DOMAIN
				,FS_ACTIVITY.YEAR
				,FS_ACTIVITY.DESCRIPTION
				,FS_ACTIVITY.EPIGRAPH
				,FS_ACTIVITY.FARMER
				,FS_ACTIVITY.MAX_IMPORT
				,FS_ACTIVITY.MAX_PERSON
				,FS_ACTIVITY.VAT_PERCENT
			)
			.from(FS_ACTIVITY)
			.where(FS_ACTIVITY.DOMAIN.eq(domain))
			.orderBy(FS_ACTIVITY.YEAR.desc(),FS_ACTIVITY.EPIGRAPH.asc())
			.fetch()
			.stream()
			.map( new FullFiscalActivityFiller() )
			.peek(fa -> fillFiscalActivityInfo(ctx,fa) )
			;
		
	}
	
	private static class FullFiscalActivityFiller implements Function<Record, FiscalActivity> {
		@Override
		public FiscalActivity apply(Record record) {
			return new FiscalActivity()
				.setId(record.getValue(FS_ACTIVITY.ID))
				.setDomain(record.getValue(FS_ACTIVITY.DOMAIN))
				.setYear(record.getValue(FS_ACTIVITY.YEAR))
				.setDescription(record.getValue(FS_ACTIVITY.DESCRIPTION))
				.setEpigraph(record.getValue(FS_ACTIVITY.EPIGRAPH))
				.setFarmer(AonEnumUtils.getBoolean(record.getValue(FS_ACTIVITY.FARMER)))
				.setMaxImport(record.getValue(FS_ACTIVITY.MAX_IMPORT))
				.setMaxPerson(record.getValue(FS_ACTIVITY.MAX_PERSON))
				.setVatPercent(record.getValue(FS_ACTIVITY.VAT_PERCENT));
		}
	}
	
	public static FiscalActivity getActivityFor(AONContext ctx,Epigraph epigraph, FiscalActivity fa) {
		fa.getMap().clear();
		int i = 0;
		for (FiscalActivityInfoKey key : FiscalActivityInfoKey.values() ){
			if (key.accept(epigraph,fa)) {
				fa.add(new FiscalActivityInfo()
						.setInfoKey(key)
						.setInfoType(key.getType())
						.setValue(key.getDefaultValue())
						.setLine(i++));
			}
		}
		if (epigraph.hasIRPFModules()) {
			for (Module module : epigraph.getIRPFModules()) {
				fa.add(new FiscalActivityInfo()
					.setInfoKey(module.getKey())
					.setInfoType(FiscalActivityInfoKeyType.IRPF_MODULE)
					.setLine(module.getLine())
					.setFactor(module.getAmount())
					.setUnit(module.getUnit())
					.setValue(module.getKey().getDefaultValue())
					);
				if (module.getKey().getDetailKeys() != null) {
					for (FiscalActivityInfoKey detailKey : module.getKey().getDetailKeys() ){
						fa.add(new FiscalActivityInfo()
							.setInfoKey(detailKey)
							.setInfoType(FiscalActivityInfoKeyType.MODULE_DETAIL)
							.setValue(detailKey.getDefaultValue())
							.setLine(i++));
					}
				}
			}
		}
		if (epigraph.hasVATModules()) {
			for (Module module : epigraph.getVATModules()) {
				fa.add(new FiscalActivityInfo()
					.setInfoKey(module.getKey())
					.setInfoType(FiscalActivityInfoKeyType.VAT_MODULE)
					.setLine(module.getLine())
					.setFactor(module.getAmount())
					.setUnit(module.getUnit())
					.setValue(module.getKey().getDefaultValue()));
				if (module.getKey().getDetailKeys() != null) {
					for (FiscalActivityInfoKey detailKey : module.getKey().getDetailKeys() ){
						fa.add(new FiscalActivityInfo()
							.setInfoKey(detailKey)
							.setInfoType(FiscalActivityInfoKeyType.MODULE_DETAIL)
							.setValue(detailKey.getDefaultValue())
							.setLine(i++));
					}
				}
			}
		}
		
		return calculate(ctx, fa);
	}
	
//	public static FiscalActivity save(AONContext ctx, FiscalActivity fa) {
//		ctx.checkWrite();
//		FiscalActivityValidation.validate(ctx,fa);
//		if (fa.getId() == null) {
//			fa = insert(ctx, fa);
//		} else {
//			fa = update(ctx, fa);
//		}
//		return getActivity(ctx, fa.getId());
//	}

//	private static FiscalActivity insert(AONContext ctx, FiscalActivity fa) {
//		FsActivityRecord record =  ctx.getDslContext()
//			.insertInto(FS_ACTIVITY)
//				.set(FS_ACTIVITY.DOMAIN, fa.getDomain())
//				.set(FS_ACTIVITY.YEAR, fa.getYear())
//				.set(FS_ACTIVITY.EPIGRAPH, fa.getEpigraph())
//				.set(FS_ACTIVITY.DESCRIPTION, AonStringUtils.abbreviate(fa.getDescription(), 128 ) )
//				.set(FS_ACTIVITY.FARMER,AonEnumUtils.getByte(fa.isFarmer()))
//				.set(FS_ACTIVITY.MAX_PERSON, fa.getMaxPerson())
//				.set(FS_ACTIVITY.MAX_IMPORT, fa.getMaxImport())
//				.set(FS_ACTIVITY.VAT_PERCENT, fa.getVatPercent())
//			.returning(FS_ACTIVITY.ID)
//			.fetchOne();
//		fa.setId(record.getId());
//		insertDetails(ctx, fa);
//		return fa;
//	}
	
//	private static FiscalActivity update(AONContext ctx, FiscalActivity fa) {
//		ctx.getDslContext()
//			.update(FS_ACTIVITY)
//				.set(FS_ACTIVITY.DOMAIN, fa.getDomain())
//				.set(FS_ACTIVITY.YEAR, fa.getYear())
//				.set(FS_ACTIVITY.EPIGRAPH, fa.getEpigraph())
//				.set(FS_ACTIVITY.DESCRIPTION, AonStringUtils.abbreviate(fa.getDescription(), 128 ) )
//				.set(FS_ACTIVITY.FARMER,AonEnumUtils.getByte(fa.isFarmer()))
//				.set(FS_ACTIVITY.MAX_PERSON, fa.getMaxPerson())
//				.set(FS_ACTIVITY.MAX_IMPORT, fa.getMaxImport())
//				.set(FS_ACTIVITY.VAT_PERCENT, fa.getVatPercent())
//			.where(FS_ACTIVITY.ID.equal(fa.getId()))
//			.execute();
//		deleteDetails(ctx, fa);
//		insertDetails(ctx, fa);
//		return fa;
//	}

//	private static void insertDetails(AONContext ctx, FiscalActivity fa) {
//		for (Integer type : fa.getMap().keySet()) {
//			Map<Integer, FiscalActivityInfo> map = fa.getMap().get(type);
//			for (FiscalActivityInfo info: map.values()) {
//				ctx.getDslContext().insertInto(FS_ACTIVITY_INFO)
//					.set(FS_ACTIVITY_INFO.DOMAIN,fa.getDomain())
//					.set(FS_ACTIVITY_INFO.FS_ACTIVITY,fa.getId())
//					.set(FS_ACTIVITY_INFO.INFO_KEY,info.getInfoKey().getKey())
//					.set(FS_ACTIVITY_INFO.LINE,info.getLine())
//					.set(FS_ACTIVITY_INFO.TYPE,info.getInfoType().getValue())
//					.set(FS_ACTIVITY_INFO.VALUE,info.getValue())
//					.set(FS_ACTIVITY_INFO.FACTOR,info.getFactor())
//					.set(FS_ACTIVITY_INFO.BASE,info.getBase())
//					.set(FS_ACTIVITY_INFO.UNIT,info.getUnit())
//					.set(FS_ACTIVITY_INFO.MIN_VALUE,info.getMinValue())
//					.set(FS_ACTIVITY_INFO.MAX_VALUE,info.getMaxValue())
//				.execute();
//			}
//		}
//	}
	
//	public static void delete(AONContext ctx, FiscalActivity fa) {
//		ctx.checkWrite();
//		deleteDetails(ctx, fa);
//		ctx.getDslContext()
//			.delete(FS_ACTIVITY)
//				.where(FS_ACTIVITY.ID.equal(fa.getId()))
//			.execute();
//	}

//	private static void deleteDetails(AONContext ctx, FiscalActivity fa) {
//		ctx.getDslContext()
//			.delete(FS_ACTIVITY_INFO)
//			.where(FS_ACTIVITY_INFO.FS_ACTIVITY.equal(fa.getId()))
//			.execute();
//	}

//	public static FiscalActivity getActivity(AONContext ctx,int id) {
//	ctx.checkRead();
//	Record record = ctx.getDslContext().select(
//			 FS_ACTIVITY.ID
//			,FS_ACTIVITY.DOMAIN
//			,FS_ACTIVITY.YEAR
//			,FS_ACTIVITY.DESCRIPTION
//			,FS_ACTIVITY.EPIGRAPH
//			,FS_ACTIVITY.FARMER
//			,FS_ACTIVITY.MAX_IMPORT
//			,FS_ACTIVITY.MAX_PERSON
//			,FS_ACTIVITY.VAT_PERCENT
//		)
//		.from(FS_ACTIVITY)
//		.where(FS_ACTIVITY.ID.eq(id))
//		.fetchOne();
//	if (record != null) {
//		FiscalActivity fa = new FullFiscalActivityFiller().apply(record) ;
//		fillFiscalActivityInfo(ctx,fa);
//		return fa;
//	}
//	return null;
//}
}
