package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.FsActivity.FS_ACTIVITY;
import static com.esferalia.aon.jooq.tables.FsActivityInfo.FS_ACTIVITY_INFO;

import java.util.ArrayList;

import org.jooq.Record;

import com.esferalia.aon.jooq.tables.records.FsActivityRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivity;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivityInfo;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivityInfoKey;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivityInfoKeyType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivityModule;
import com.esferalia.aon.occam.api.model.fiscal.modules.Module;
import com.esferalia.aon.occam.api.model.fiscal.modules.Modules2015.Epigraph;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


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

	public static FiscalActivity getActivity(AONContext ctx,int id) {
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

	public static FiscalActivity getActivityFor(Epigraph epigraph, FiscalActivity fa) {
		fa.setInfo(null);
		fa.setInfoIRPF(null);
		fa.setModuleIRPF(null);
		fa.setInfoIVA(null);
		fa.setModuleIVA(null);
		int i = 0;
		for (FiscalActivityInfoKey key : FiscalActivityInfoKey.values() ){
			if (key.getType() == FiscalActivityInfoKeyType.INFO && key.accept(fa)) {
				fa.addInfo(new FiscalActivityInfo()
						.setInfoKey(key)
						.setValue(key.getDefaultValue())
						.setLine(i++));
			}
			if (epigraph.hasIrpfModules()) {
				if (key.getType() == FiscalActivityInfoKeyType.IRPF_INFO) {
					fa.addInfoIRPF(new FiscalActivityInfo()
						.setInfoKey(key)
						.setValue(key.getDefaultValue())
						.setLine(i++));
				}
			}
			if (epigraph.hasIvaModules()) {	
				if (key.getType() == FiscalActivityInfoKeyType.VAT_INFO) {
					fa.addInfoIVA(new FiscalActivityInfo()
						.setInfoKey(key)
						.setValue(key.getDefaultValue())
						.setLine(i++));
				}
			}
		}
		if (epigraph.hasIrpfModules()) {
			for (Module module : epigraph.getIrpfModules()) {
				fa.addModuleIRPF(new FiscalActivityModule()
				.setInfoKey(module.getKey())
				.setLine(module.getLine())
				.setFactor(module.getAmount())
				.setUnit(module.getUnit())
				.setValue(module.getKey().getDefaultValue()));
			}
		}
		if (epigraph.hasIvaModules()) {
			for (Module module : epigraph.getIvaModules()) {
				fa.addModuleIVA(new FiscalActivityModule()
					.setInfoKey(module.getKey())
					.setLine(module.getLine())
					.setFactor(module.getAmount())
					.setUnit(module.getUnit())
					.setValue(module.getKey().getDefaultValue()));
			}
		}
		return fa;
	}
	
	public static FiscalActivity save(AONContext ctx, FiscalActivity fa) {
		ctx.checkWrite();
		if (fa.getId() == null) {
			fa = insert(ctx, fa);
		} else {
			fa = update(ctx, fa);
		}
		return getActivity(ctx, fa.getId());
	}

	private static FiscalActivity insert(AONContext ctx, FiscalActivity fa) {
		FsActivityRecord record =  ctx.getDslContext()
			.insertInto(FS_ACTIVITY)
				.set(FS_ACTIVITY.DOMAIN, fa.getDomain())
				.set(FS_ACTIVITY.YEAR, fa.getYear())
				.set(FS_ACTIVITY.EPIGRAPH, fa.getEpigraph())
				.set(FS_ACTIVITY.DESCRIPTION, AonStringUtils.abbreviate(fa.getDescription(), 128 ) )
				.set(FS_ACTIVITY.FARMER,AonEnumUtils.getByte(fa.isFarmer()))
				.set(FS_ACTIVITY.MAX_PERSON, fa.getMaxPerson())
				.set(FS_ACTIVITY.MAX_IMPORT, fa.getMaxImport())
				.set(FS_ACTIVITY.VAT_PERCENT, fa.getVatPercent())
			.returning(FS_ACTIVITY.ID)
			.fetchOne();
		fa.setId(record.getId());
		insertDetails(ctx, fa);
		return fa;
	}
	
	private static FiscalActivity update(AONContext ctx, FiscalActivity fa) {
		ctx.getDslContext()
			.update(FS_ACTIVITY)
				.set(FS_ACTIVITY.DOMAIN, fa.getDomain())
				.set(FS_ACTIVITY.YEAR, fa.getYear())
				.set(FS_ACTIVITY.EPIGRAPH, fa.getEpigraph())
				.set(FS_ACTIVITY.DESCRIPTION, AonStringUtils.abbreviate(fa.getDescription(), 128 ) )
				.set(FS_ACTIVITY.FARMER,AonEnumUtils.getByte(fa.isFarmer()))
				.set(FS_ACTIVITY.MAX_PERSON, fa.getMaxPerson())
				.set(FS_ACTIVITY.MAX_IMPORT, fa.getMaxImport())
				.set(FS_ACTIVITY.VAT_PERCENT, fa.getVatPercent())
			.where(FS_ACTIVITY.ID.equal(fa.getId()))
			.execute();
		deleteDetails(ctx, fa);
		insertDetails(ctx, fa);
		return fa;
	}

	private static void insertDetails(AONContext ctx, FiscalActivity fa) {
		for (FiscalActivityInfo info: fa.getInfo()) {
			ctx.getDslContext()
			.insertInto(FS_ACTIVITY_INFO)
				.set(FS_ACTIVITY_INFO.DOMAIN,fa.getDomain())
				.set(FS_ACTIVITY_INFO.FS_ACTIVITY,fa.getId())
				.set(FS_ACTIVITY_INFO.INFO_KEY,info.getInfoKey().getKey())
				.set(FS_ACTIVITY_INFO.LINE,info.getLine())
				.set(FS_ACTIVITY_INFO.TYPE,info.getInfoKey().getType().getValue())
				.set(FS_ACTIVITY_INFO.VALUE,info.getValue())
			.execute();
		}
		for (FiscalActivityInfo info: fa.getInfoIRPF()) {
			ctx.getDslContext()
			.insertInto(FS_ACTIVITY_INFO)
				.set(FS_ACTIVITY_INFO.DOMAIN,fa.getDomain())
				.set(FS_ACTIVITY_INFO.FS_ACTIVITY,fa.getId())
				.set(FS_ACTIVITY_INFO.INFO_KEY,info.getInfoKey().getKey())
				.set(FS_ACTIVITY_INFO.LINE,info.getLine())
				.set(FS_ACTIVITY_INFO.TYPE,info.getInfoKey().getType().getValue())
				.set(FS_ACTIVITY_INFO.VALUE,info.getValue())
			.execute();
		}
		for (FiscalActivityInfo info: fa.getInfoIVA()) {
			ctx.getDslContext()
			.insertInto(FS_ACTIVITY_INFO)
				.set(FS_ACTIVITY_INFO.DOMAIN,fa.getDomain())
				.set(FS_ACTIVITY_INFO.FS_ACTIVITY,fa.getId())
				.set(FS_ACTIVITY_INFO.INFO_KEY,info.getInfoKey().getKey())
				.set(FS_ACTIVITY_INFO.LINE,info.getLine())
				.set(FS_ACTIVITY_INFO.TYPE,info.getInfoKey().getType().getValue())
				.set(FS_ACTIVITY_INFO.VALUE,info.getValue())
			.execute();
		}
		for (FiscalActivityModule module: fa.getModuleIVA()) {
			ctx.getDslContext()
			.insertInto(FS_ACTIVITY_INFO)
				.set(FS_ACTIVITY_INFO.DOMAIN,fa.getDomain())
				.set(FS_ACTIVITY_INFO.FS_ACTIVITY,fa.getId())
				.set(FS_ACTIVITY_INFO.INFO_KEY,module.getInfoKey().getKey())
				.set(FS_ACTIVITY_INFO.LINE,module.getLine())
				.set(FS_ACTIVITY_INFO.TYPE,module.getInfoKey().getType().getValue())
				.set(FS_ACTIVITY_INFO.VALUE,module.getValue())
				.set(FS_ACTIVITY_INFO.FACTOR,module.getFactor())
				.set(FS_ACTIVITY_INFO.BASE,module.getBase())
				.set(FS_ACTIVITY_INFO.UNIT,module.getUnit())
				.set(FS_ACTIVITY_INFO.MIN_VALUE,module.getMinValue())
				.set(FS_ACTIVITY_INFO.MAX_VALUE,module.getMaxValue())
			.execute();
		}
	}
	
	public static void delete(AONContext ctx, FiscalActivity fa) {
		ctx.checkWrite();
		deleteDetails(ctx, fa);
		ctx.getDslContext()
			.delete(FS_ACTIVITY)
				.where(FS_ACTIVITY.ID.equal(fa.getId()))
			.execute();
	}

	private static void deleteDetails(AONContext ctx, FiscalActivity fa) {
		ctx.getDslContext()
			.delete(FS_ACTIVITY_INFO)
			.where(FS_ACTIVITY_INFO.FS_ACTIVITY.equal(fa.getId()))
			.execute();
	}
}
