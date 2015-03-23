package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.FsActivity.FS_ACTIVITY;
import static com.esferalia.aon.jooq.tables.FsActivityInfo.FS_ACTIVITY_INFO;

import java.util.ArrayList;

import org.jooq.Record;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivity;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivityInfo;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivityInfoKey;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivityInfoKeyType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivityModule;
import com.esferalia.aon.occam.api.model.fiscal.modules.Module;
import com.esferalia.aon.occam.api.model.fiscal.modules.Modules2015.Epigraph;
import com.esferalia.aon.watson.server.AonEnumUtils;


public class FiscalActivityDAO {
	
	enum FiscalActivityInfoType {
		INFO{
			@Override
			public void accept(Record record, FiscalActivity fa) {
				fa.addInfo(new FiscalActivityInfo()
					.setId(record.getValue(FS_ACTIVITY_INFO.ID))
					.setFiscalActivity(record.getValue(FS_ACTIVITY_INFO.FS_ACTIVITY))
					.setInfoKey(FiscalActivityInfoKey.safeValueOf(record.getValue(FS_ACTIVITY_INFO.INFO_KEY)) )
					.setLine(record.getValue(FS_ACTIVITY_INFO.LINE))
					.setValue(record.getValue(FS_ACTIVITY_INFO.VALUE)));				
			}
		}
		,VAT_MODULE {
			@Override
			public void accept(Record record, FiscalActivity fa) {
				fa.addModuleIVA(new FiscalActivityModule()
				.setFactor(record.getValue(FS_ACTIVITY_INFO.FACTOR))
				.setBase(record.getValue(FS_ACTIVITY_INFO.BASE))
				.setUnit(record.getValue(FS_ACTIVITY_INFO.UNIT))
				.setMinValue(record.getValue(FS_ACTIVITY_INFO.MIN_VALUE))
				.setMaxValue(record.getValue(FS_ACTIVITY_INFO.MAX_VALUE))
				.setId(record.getValue(FS_ACTIVITY_INFO.ID))
				.setFiscalActivity(record.getValue(FS_ACTIVITY_INFO.FS_ACTIVITY))
				.setInfoKey(FiscalActivityInfoKey.safeValueOf(record.getValue(FS_ACTIVITY_INFO.INFO_KEY)) )
				.setLine(record.getValue(FS_ACTIVITY_INFO.LINE))
				.setValue(record.getValue(FS_ACTIVITY_INFO.VALUE))
				);				
			}
		}
		,IRPF_MODULE{
			@Override
			public void accept(Record record, FiscalActivity fa) {
				fa.addModuleIRPF(new FiscalActivityModule()
					.setFactor(record.getValue(FS_ACTIVITY_INFO.FACTOR))
					.setBase(record.getValue(FS_ACTIVITY_INFO.BASE))
					.setUnit(record.getValue(FS_ACTIVITY_INFO.UNIT))
					.setMinValue(record.getValue(FS_ACTIVITY_INFO.MIN_VALUE))
					.setMaxValue(record.getValue(FS_ACTIVITY_INFO.MAX_VALUE))
					.setId(record.getValue(FS_ACTIVITY_INFO.ID))
					.setFiscalActivity(record.getValue(FS_ACTIVITY_INFO.FS_ACTIVITY))
					.setInfoKey(FiscalActivityInfoKey.safeValueOf(record.getValue(FS_ACTIVITY_INFO.INFO_KEY)) )
					.setLine(record.getValue(FS_ACTIVITY_INFO.LINE))
					.setValue(record.getValue(FS_ACTIVITY_INFO.VALUE))
					);				
			}
		}
		,VAT_INFO{
			@Override
			public void accept(Record record, FiscalActivity fa) {
				fa.addInfoIVA(new FiscalActivityInfo()
				.setId(record.getValue(FS_ACTIVITY_INFO.ID))
				.setFiscalActivity(record.getValue(FS_ACTIVITY_INFO.FS_ACTIVITY))
				.setInfoKey(FiscalActivityInfoKey.safeValueOf(record.getValue(FS_ACTIVITY_INFO.INFO_KEY)) )
				.setLine(record.getValue(FS_ACTIVITY_INFO.LINE))
				.setValue(record.getValue(FS_ACTIVITY_INFO.VALUE)));				
			}
		}
		,IRPF_INFO{
			@Override
			public void accept(Record record, FiscalActivity fa) {
				fa.addInfoIRPF(new FiscalActivityInfo()
				.setId(record.getValue(FS_ACTIVITY_INFO.ID))
				.setFiscalActivity(record.getValue(FS_ACTIVITY_INFO.FS_ACTIVITY))
				.setInfoKey(FiscalActivityInfoKey.safeValueOf(record.getValue(FS_ACTIVITY_INFO.INFO_KEY)) )
				.setLine(record.getValue(FS_ACTIVITY_INFO.LINE))
				.setValue(record.getValue(FS_ACTIVITY_INFO.VALUE)));				
			}
		}
		,MODULE_DETAIL{
			@Override
			public void accept(Record record, FiscalActivity fa) {}
		}
		;
		
		public byte getOrdinal() {
			return (byte) this.ordinal();
		}
		
		public abstract void accept(Record record, FiscalActivity fa);

		public static void acceptRecord(Record record, FiscalActivity fa) {
			for ( FiscalActivityInfoType type : FiscalActivityInfoType.values()) {
				if (record.getValue(FS_ACTIVITY_INFO.TYPE) == type.getOrdinal()) {
					type.accept(record, fa);	
				}
			}
		}
	}	

	public static FiscalActivity getActivity(AONContext ctx,int domain,int id) {
		ctx.checkRead();
		Record record = ctx.getDslContext().select(
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
			.where(FS_ACTIVITY.ID.eq(id))
			.fetchOne();
		if (record != null) {
			FiscalActivity fa = populate(record);
			fillFiscalActivityInfo(ctx,fa);
			return fa;
		}
		
		return null;
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
		.forEach(record -> FiscalActivityInfoType.acceptRecord(record,fa))
		;
	}

	public static ArrayList<FiscalActivity> getActivities(AONContext ctx,int domain) {
		ctx.checkRead();
		ArrayList<FiscalActivity> list = new ArrayList<FiscalActivity>();
		ctx.getDslContext().select(
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
			.forEach( record -> list.add( populate(record) ) );
		return list;
	}

	private static FiscalActivity populate(Record record) {
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

	public static FiscalActivity getActivityFor(Epigraph epigraph, Integer year) {
		FiscalActivity fa = new FiscalActivity()
				.setYear(year)
				.setEpigraph(epigraph.getEpigraph())
				.setDescription(epigraph.getDescription())
				.setMaxPerson(epigraph.getLimPers())
				.setMaxImport(epigraph.getLimExceso())
				.setVatPercent(epigraph.getPorcMin());
		int i = 0;
		for (FiscalActivityInfoKey key : FiscalActivityInfoKey.values() ){
			if (key.getType() == FiscalActivityInfoKeyType.INFO) {
				fa.addInfo(new FiscalActivityInfo().setInfoKey(key).setLine(i++));
			}
			if (key.getType() == FiscalActivityInfoKeyType.IRPF_INFO) {
				fa.addInfoIRPF(new FiscalActivityInfo().setInfoKey(key).setLine(i++));
			}
			if (key.getType() == FiscalActivityInfoKeyType.VAT_INFO) {
				fa.addInfoIVA(new FiscalActivityInfo().setInfoKey(key).setLine(i++));
			}
		}
		for (Module module : epigraph.getIrpfModules()) {
			fa.addModuleIRPF(new FiscalActivityModule()
				.setLine(module.getLine())
				.setFactor(module.getAmount())
				.setUnit(module.getUnit()));
		}
		for (Module module : epigraph.getIvaModules()) {
			fa.addModuleIRPF(new FiscalActivityModule()
				.setLine(module.getLine())
				.setFactor(module.getAmount())
				.setUnit(module.getUnit()));
		}
		return fa;
	}
	
}
