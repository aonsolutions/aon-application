package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;

import java.util.stream.Stream;

import com.esferalia.aon.jooq.tables.records.FsModelRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.type.Mod131Key;
import com.esferalia.aon.occam.server.fiscal.calc.Aeat2013Mod131Calculator;


public class Mod131DAO extends FiscalModelDAO {
	
	public static Mod131 calculateMod131(AONContext ctx, Mod131 mod131) {
		return Aeat2013Mod131Calculator.calculate(ctx, mod131);
	}
	
	public static Mod131 initializeMod131(AONContext ctx,Mod131 mod131) {
		initializeFiscalModel(ctx, mod131);
		for (Mod131Key key : Mod131Key.values()) {
			mod131.ensureDetail(key);
		}
		return mod131;
	}

	public static Mod131 saveMod131(AONContext ctx, Mod131 mod131) {
		FiscalModel fm = save(ctx, mod131);
		return getMod131(ctx, fm.getId());
	}

	public static Stream<Mod131> getMod131s(AONContext ctx, int domain) {
		return ctx.getDslContext().selectFrom(FS_MODEL)
				.where(FS_MODEL.DOMAIN.eq(domain))
				.and(FS_MODEL.MODEL.eq( FiscalModelType.M131.getValue() ))
				.orderBy(FS_MODEL.YEAR.desc(),FS_MODEL.MODEL.asc(),FS_MODEL.PERIOD.desc())
				.fetch()
				.stream()
				.map( record -> Mod131DAO.map(new Mod131(),record));
	}
	
	public static Mod131 getMod131(AONContext ctx,int id) {
		ctx.checkRead();
		FsModelRecord record = ctx.getDslContext().selectFrom(FS_MODEL)
			.where(FS_MODEL.ID.eq(id))
			.fetchOne();
		if (record != null) {
			Mod131 fm = new Mod131(); 
			populate(fm,record);
			fillModelDetails(ctx,fm);
			return fm;
		}
		return null;
	}
}
