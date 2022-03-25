package com.esferalia.aon.occam.impl.jooq.fiscal;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.fiscal.IMODEL2002020;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020;
import com.esferalia.aon.occam.impl.jooq.dao.mod200_2020.Mod2002020DAO;

public class MODEL2002020Impl implements IMODEL2002020 {

	@Override
	public Mod2002020 createMod2002020(AONContext ctx, int year) {
		return Mod2002020DAO.createNewMod200(ctx,year);
	}
	@Override
	public Mod2002020 initializeNewMod2002020(AONContext ctx, Mod2002020 mod200) {
		return Mod2002020DAO.initializeNewMod200(ctx,mod200);
	}
	
	@Override
	public Mod2002020 initializeMod2002020(AONContext ctx, Mod2002020 mod200) {
		return Mod2002020DAO.initializeMod200(ctx,mod200);
	}

	@Override
	public Mod2002020 getMod2002020ByYear(AONContext ctx, int year) {
		return Mod2002020DAO.getByYear(ctx,year);
	}

	@Override
	public Mod2002020 getMod2002020ById(AONContext ctx, int id) {
		return Mod2002020DAO.getById(ctx,id);
	}
	@Override
	public Mod2002020 calculateMod2002020(Mod2002020 mod200) {
		return Mod2002020DAO.calculate(mod200);
	}
	@Override
	public Mod2002020 validateMod2002020(Mod2002020 mod200) {
		return Mod2002020DAO.validate(mod200);
	}
	@Override
	public Mod2002020 saveMod2002020(AONContext ctx, Mod2002020 mod200) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod2002020DAO.save(ctx, mod200));
	}
	@Override
	public void deleteMod2002020(AONContext ctx, int id) {
		ctx.getDslContext().transaction(
				configuration -> Mod2002020DAO.delete(ctx, id));
	}

	@Override
	public String dumpAEATMod2002020(Mod2002020 mod200) {
		return Mod2002020DAO.dumpAEAT(mod200);
	}

	@Override
	public Mod2002020 importMod2002019(AONContext ctx, Mod2002020 mod200) {
		return Mod2002020DAO.importMod2002019(ctx,mod200);
	}	
	
}
