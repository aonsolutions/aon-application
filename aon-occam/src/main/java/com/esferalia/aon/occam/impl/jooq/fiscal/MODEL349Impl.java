package com.esferalia.aon.occam.impl.jooq.fiscal;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.fiscal.IMODEL349;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod349;
import com.esferalia.aon.occam.api.model.fiscal.Mod349Detail;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod349.Mod349DAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod349.Mod349InfoDAO;

public class MODEL349Impl implements IMODEL349 {

	@Override
	public LinkedList<Mod349> getMod349s(AONContext ctx, int domain) {
		return Mod349DAO.getByDomain(ctx, domain);
	}

	@Override
	public Mod349 get(AONContext ctx, Integer id) {
		return Mod349DAO.getById(ctx, id);
	}

	@Override
	public Mod349 initialize(AONContext ctx, int year, Period period) {
		return Mod349DAO.initialize(ctx, year, period);		
	}

	@Override
	public Mod349 save(AONContext ctx, Mod349 mod349) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod349DAO.save(ctx, mod349));
	}

	@Override
	public Mod349 reset(AONContext ctx, Mod349 mod349) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod349DAO.reset(ctx, mod349));
	}

	@Override
	public void delete(AONContext ctx, Mod349 mod349) {
		ctx.getDslContext().transaction(
				configuration -> Mod349DAO.delete(ctx, mod349));
	}

	@Override
	public Mod349Detail getDetail(AONContext ctx, Integer id) {
		return Mod349DAO.getDetail(ctx, id);
	}
	
	@Override
	public Mod349 saveComments(AONContext ctx, Mod349 mod349) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod349DAO.saveComments(ctx, mod349));		
	}
	@Override
	public Mod349 changeStatus(AONContext ctx, Mod349 mod349, FiscalStatus newStatus) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod349DAO.changeStatusMod349(ctx, mod349, newStatus));		
	}
	@Override
	public String getInfo(AONContext ctx, Mod349 mod349, Mod349Detail detail, FiscalModelKeyInfo infoKey) {
		return Mod349InfoDAO.getInfo(ctx, mod349, detail, infoKey);
	}
	@Override
	public Mod349 duplicate(AONContext ctx, Mod349 mod349) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod349DAO.duplicate(ctx, mod349));
	}
	
	@Override
	public Mod349 aeatPresentation(AONContext ctx, Mod349 mod, String aeatResponse) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod349DAO.aeatPresentation(ctx, mod, aeatResponse));
	}

}
