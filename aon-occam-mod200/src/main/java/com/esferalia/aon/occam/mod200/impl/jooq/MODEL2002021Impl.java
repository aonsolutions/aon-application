package com.esferalia.aon.occam.mod200.impl.jooq;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.mod200.api.IMODEL2002021;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2021.Mod2002021DAO;

public class MODEL2002021Impl implements IMODEL2002021 {

	@Override
	public Mod2002021 createMod2002021(AONContext ctx, int year) {
		return Mod2002021DAO.createNewMod200(ctx,year);
	}
	
	@Override
	public Mod2002021 initializeMod2002021(AONContext ctx, Mod2002021 mod200) {
		return Mod2002021DAO.initializeMod200(ctx,mod200);
	}

	@Override
	public Mod2002021 getMod2002021ById(AONContext ctx, int id) {
		return Mod2002021DAO.getById(ctx,id);
	}
	@Override
	public Mod2002021 calculateMod2002021(Mod2002021 mod200) {
		return Mod2002021DAO.calculate(mod200);
	}
	@Override
	public Mod2002021 saveMod2002021(AONContext ctx, Mod2002021 mod200) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod2002021DAO.save(ctx, mod200));
	}
	@Override
	public void deleteMod2002021(AONContext ctx, Mod2002021 mod200) {
		ctx.getDslContext().transaction(
				configuration -> Mod2002021DAO.delete(ctx, mod200));
	}
	
}
