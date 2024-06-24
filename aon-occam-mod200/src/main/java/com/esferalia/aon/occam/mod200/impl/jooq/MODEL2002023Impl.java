package com.esferalia.aon.occam.mod200.impl.jooq;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.mod200.api.IMODEL2002023;
import com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2023.Mod2002023DAO;

public class MODEL2002023Impl implements IMODEL2002023 {

	@Override
	public Mod2002023 createMod2002023(AONContext ctx, int year) {
		return Mod2002023DAO.createNewMod200(ctx,year);
	}
	
	@Override
	public Mod2002023 initializeMod2002023(AONContext ctx, Mod2002023 mod200) {
		return Mod2002023DAO.initializeMod200(ctx,mod200);
	}

	@Override
	public Mod2002023 getMod2002023ById(AONContext ctx, int id) {
		return Mod2002023DAO.getById(ctx,id);
	}
	@Override
	public Mod2002023 calculateMod2002023(Mod2002023 mod200) {
		return Mod2002023DAO.calculate(mod200);
	}
	@Override
	public Mod2002023 saveMod2002023(AONContext ctx, Mod2002023 mod200) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod2002023DAO.save(ctx, mod200));
	}
	@Override
	public void deleteMod2002023(AONContext ctx, Mod2002023 mod200) {
		ctx.getDslContext().transaction(
				configuration -> Mod2002023DAO.delete(ctx, mod200));
	}
	
	@Override
	public Mod2002023 aeatPresentation(AONContext ctx, Mod2002023 mod, String aeatResponse) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod2002023DAO.aeatPresentation(ctx, mod, aeatResponse));
	}
	
}
