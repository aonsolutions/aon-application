package com.esferalia.aon.occam.mod200.impl.jooq;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.mod200.api.IMODEL2002025;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2025.Mod2002025DAO;

public class MODEL2002025Impl implements IMODEL2002025 {

	@Override
	public Mod2002025 createMod2002025(AONContext ctx, int year) {
		return Mod2002025DAO.createNewMod200(ctx,year);
	}
	
	@Override
	public Mod2002025 initializeMod2002025(AONContext ctx, Mod2002025 mod200) {
		return Mod2002025DAO.initializeMod200(ctx,mod200);
	}

	@Override
	public Mod2002025 getMod2002025ById(AONContext ctx, int id) {
		return Mod2002025DAO.getById(ctx,id);
	}
	@Override
	public Mod2002025 calculateMod2002025(Mod2002025 mod200) {
		return Mod2002025DAO.calculate(mod200);
	}
	@Override
	public Mod2002025 saveMod2002025(AONContext ctx, Mod2002025 mod200) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod2002025DAO.save(ctx, mod200));
	}
	@Override
	public void deleteMod2002025(AONContext ctx, Mod2002025 mod200) {
		ctx.getDslContext().transaction(
				configuration -> Mod2002025DAO.delete(ctx, mod200));
	}
	
	@Override
	public Mod2002025 aeatPresentation(AONContext ctx, Mod2002025 mod, String aeatResponse) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod2002025DAO.aeatPresentation(ctx, mod, aeatResponse));
	}
	
}
