package com.esferalia.aon.occam.impl.jooq.fiscal;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.fiscal.IMODEL193;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod193.Mod193DAO;

public class MODEL193Impl implements IMODEL193 {

	@Override
	public LinkedList<Mod193> getMod193s(AONContext ctx, int domain) {
		return Mod193DAO.getByDomain(ctx, domain);
	}

	@Override
	public Mod193 get(AONContext ctx, Integer id) {
		return Mod193DAO.getById(ctx, id);
	}

	@Override
	public Mod193 initialize(AONContext ctx, int year) {
		return Mod193DAO.initialize(ctx, year);
	}
	@Override
	public Mod193 save(AONContext ctx, Mod193 mod193) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod193DAO.save(ctx, mod193));
	}

	@Override
	public void delete(AONContext ctx, Mod193 mod193) {
		ctx.getDslContext().transaction(
				configuration -> Mod193DAO.delete(ctx, mod193));
	}

	@Override
	public Mod193 saveComments(AONContext ctx, Mod193 mod193) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod193DAO.saveComments(ctx, mod193));		
	}
	@Override
	public Mod193 changeStatus(AONContext ctx, Mod193 mod193, FiscalStatus newStatus) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod193DAO.changeStatus(ctx, mod193, newStatus));		
	}
	@Override
	public Mod193 duplicate(AONContext ctx, Mod193 mod193) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod193DAO.duplicate(ctx, mod193));		
	}

}
