package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.type.CNAE2009;
import com.esferalia.aon.occam.api.model.type.Mod202Key;
import com.esferalia.aon.occam.server.fiscal.calc.Aeat2015Mod202Calculator;
import com.esferalia.aon.occam.server.fiscal.calc.Aeat2016Mod202Calculator;
import com.esferalia.aon.watson.util.AonStringUtils;


public class Mod202DAO extends FiscalModelDAO {
	
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("ddMMyyyy");

	public static Stream<Mod202> getMod202s(AONContext ctx,int domain) {
			return ctx.getDslContext().selectFrom(FS_MODEL)
					.where(FS_MODEL.DOMAIN.eq(domain))
					.and(FS_MODEL.MODEL.eq( FiscalModelType.M202.getValue() ))
					.orderBy(FS_MODEL.YEAR.desc(),FS_MODEL.MODEL.asc(),FS_MODEL.PERIOD.desc())
					.fetch()
					.stream()
					.map( record -> map202(new Mod202(),record));
		}
		
	public static Mod202 getMod202(AONContext ctx,int id) {
		ctx.checkRead();
		Mod202 mod202 = getModelRecord(ctx, id)
				.map( record -> map202(new Mod202(),record));
		if (mod202 != null) {
			getModelDetails(ctx,mod202).forEach( detail -> mod202.put( detail));	
		}
		onFillFiscalModel(mod202);
		return mod202;
		
	}
	
	public static Mod202 saveMod202(AONContext ctx, Mod202 mod202) {
		ensureDetail(mod202);
		FiscalModel fm = save(ctx, mod202);
		return getMod202(ctx, fm.getId());
	}
	
	public static Mod202 calculateMod202(AONContext ctx, Mod202 mod202) {
		return  mod202.getYear()<2016
					?Aeat2015Mod202Calculator.calculate(ctx, mod202)
					:Aeat2016Mod202Calculator.calculate(ctx, mod202);
	}

	public static Mod202 initializeMod202(AONContext ctx,Mod202 mod202) {
		initializeFiscalModel(ctx, mod202);
		for (Mod202Key key : Mod202Key.values()) {
			mod202.ensureDetail(key);
		}
		return mod202;
	}
	
	protected static void onFillFiscalModel(Mod202 mod202) {
		for (Mod202Key key : Mod202Key.values()) {
			if (key == Mod202Key.P02) {
				String c = mod202.getDescription(Mod202Key.P02);
				Date initialDate = null;
				if (AonStringUtils.isNotEmpty( c )) {
					try {
						initialDate = DATE_FORMAT.parse(c);
					} catch (ParseException e) {
					}
				}
				mod202.setInitialDate(initialDate);
			} else if (key == Mod202Key.P03) {
				String c = mod202.getDescription(Mod202Key.P03);
				if (AonStringUtils.isNotEmpty( c )) {
					CNAE2009 cnae = CNAE2009.valueOfCode(c); 
					mod202.setCnae(cnae==null?null:cnae.getCode());
					mod202.setCnaeDescription(cnae==null?null:cnae.getDescription());
				} else {
					mod202.setCnae(null);
				}
			}
		}
	}
	protected static void ensureDetail(Mod202 mod202) {
		for (Mod202Key key : Mod202Key.values()) {
			if (key == Mod202Key.P02) {
				if (mod202.getInitialDate() != null) {
					try {
						String date = DATE_FORMAT.format(mod202.getInitialDate());
						mod202.putDescription(Mod202Key.P02,date);
					} catch (NumberFormatException e) {
						mod202.putDescription(Mod202Key.P02,null);
					}
				} else {
					mod202.putDescription(Mod202Key.P02,null);
				}
			} else if (key == Mod202Key.P03) {
				if (AonStringUtils.isNotEmpty( mod202.getCnae())) {
					try {
						mod202.putDescription(Mod202Key.P03, mod202.getCnae());
					} catch (NumberFormatException e) {
						mod202.putDescription(Mod202Key.P03,null);
					}
				} else {
					mod202.putDescription(Mod202Key.P03,null);
				}
			}
		}
	}
}
