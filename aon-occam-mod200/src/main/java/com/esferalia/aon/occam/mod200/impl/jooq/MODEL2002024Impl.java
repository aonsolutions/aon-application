package com.esferalia.aon.occam.mod200.impl.jooq;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.mod200.api.IMODEL2002024;
import com.esferalia.aon.occam.mod200.api.model.mod200_2024.Mod2002024;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2024.Mod2002024DAO;

public class MODEL2002024Impl implements IMODEL2002024 {

	@Override
	public Mod2002024 createMod2002024(AONContext ctx, int year) {
		return Mod2002024DAO.createNewMod200(ctx,year);
	}
	
	@Override
	public Mod2002024 initializeMod2002024(AONContext ctx, Mod2002024 mod200) {
		return Mod2002024DAO.initializeMod200(ctx,mod200);
	}

	@Override
	public Mod2002024 getMod2002024ById(AONContext ctx, int id) {
		return Mod2002024DAO.getById(ctx,id);
	}
	@Override
	public Mod2002024 calculateMod2002024(Mod2002024 mod200) {
		return Mod2002024DAO.calculate(mod200);
	}
	@Override
	public Mod2002024 saveMod2002024(AONContext ctx, Mod2002024 mod200) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod2002024DAO.save(ctx, mod200));
	}
	@Override
	public void deleteMod2002024(AONContext ctx, Mod2002024 mod200) {
		ctx.getDslContext().transaction(
				configuration -> Mod2002024DAO.delete(ctx, mod200));
	}
	
	@Override
	public Mod2002024 aeatPresentation(AONContext ctx, Mod2002024 mod, String aeatResponse) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod2002024DAO.aeatPresentation(ctx, mod, aeatResponse));
	}
	
}
