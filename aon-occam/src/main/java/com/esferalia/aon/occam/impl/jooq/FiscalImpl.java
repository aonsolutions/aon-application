package com.esferalia.aon.occam.impl.jooq;

import java.util.ArrayList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IFiscal;
import com.esferalia.aon.occam.api.model.Mod180;
import com.esferalia.aon.occam.api.model.Mod180Detail;
import com.esferalia.aon.occam.impl.jooq.dao.Mod180DAO;

public class FiscalImpl implements IFiscal {

	@Override
	public ArrayList<Mod180> getMod180s(AONContext ctx, int domain) {
		return Mod180DAO.getByDomain(ctx, domain);
	}

	@Override
	public Mod180 getMod180(AONContext ctx, Integer id) {
		return Mod180DAO.getById(ctx, id);
	}

	@Override
	public Mod180 saveMod180(AONContext ctx, Mod180 mod180) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod180DAO.save(ctx, mod180));
	}

	@Override
	public void deleteMod180(AONContext ctx, Mod180 mod180) {
		ctx.getDslContext().transaction(
				configuration -> Mod180DAO.delete(ctx, mod180));

	}

	@Override
	public Mod180Detail getMod180Detail(AONContext ctx, Integer id) {
		return Mod180DAO.getDetail(ctx, id);
	}


}
