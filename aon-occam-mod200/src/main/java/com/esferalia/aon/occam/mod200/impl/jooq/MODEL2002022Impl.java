package com.esferalia.aon.occam.mod200.impl.jooq;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.mod200.api.IMODEL2002022;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2022.Mod2002022DAO;

public class MODEL2002022Impl implements IMODEL2002022 {

	@Override
	public Mod2002022 createMod2002022(AONContext ctx, int year) {
		return Mod2002022DAO.createNewMod200(ctx,year);
	}
	
	@Override
	public Mod2002022 initializeMod2002022(AONContext ctx, Mod2002022 mod200) {
		return Mod2002022DAO.initializeMod200(ctx,mod200);
	}

	@Override
	public Mod2002022 getMod2002022ById(AONContext ctx, int id) {
		return Mod2002022DAO.getById(ctx,id);
	}
	@Override
	public Mod2002022 calculateMod2002022(Mod2002022 mod200) {
		return Mod2002022DAO.calculate(mod200);
	}
	@Override
	public Mod2002022 saveMod2002022(AONContext ctx, Mod2002022 mod200) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod2002022DAO.save(ctx, mod200));
	}
	@Override
	public void deleteMod2002022(AONContext ctx, Mod2002022 mod200) {
		ctx.getDslContext().transaction(
				configuration -> Mod2002022DAO.delete(ctx, mod200));
	}
	
	@Override
	public Mod2002022 aeatPresentation(AONContext ctx, Mod2002022 mod, String aeatResponse) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod2002022DAO.aeatPresentation(ctx, mod, aeatResponse));
	}
	
}
