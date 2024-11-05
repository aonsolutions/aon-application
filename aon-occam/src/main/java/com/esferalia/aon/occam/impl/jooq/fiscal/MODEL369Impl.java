package com.esferalia.aon.occam.impl.jooq.fiscal;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.fiscal.IMODEL369;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod369;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod369.Mod369DAO;

public class MODEL369Impl implements IMODEL369 {

	@Override
	public LinkedList<Mod369> getMod369s(AONContext ctx, int domain) {
		return Mod369DAO.getByDomain(ctx, domain);
	}

	@Override
	public Mod369 get(AONContext ctx, Integer id) {
		return Mod369DAO.getById(ctx, id);
	}

	@Override
	public Mod369 initialize(AONContext ctx, int year, Period period) {
		return Mod369DAO.initialize(ctx, year, period);
	}
	@Override
	public Mod369 save(AONContext ctx, Mod369 mod369) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod369DAO.save(ctx, mod369));
	}

	@Override
	public void delete(AONContext ctx, Mod369 mod369) {
		ctx.getDslContext().transaction(
				configuration -> Mod369DAO.delete(ctx, mod369));
	}

	@Override
	public Mod369 saveComments(AONContext ctx, Mod369 mod369) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod369DAO.saveComments(ctx, mod369));		
	}
	@Override
	public Mod369 changeStatus(AONContext ctx, Mod369 mod369, FiscalStatus newStatus) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod369DAO.changeStatusMod369(ctx, mod369, newStatus));		
	}
	@Override
	public Mod369 duplicate(AONContext ctx, Mod369 mod369) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod369DAO.duplicate(ctx, mod369));		
	}

}
