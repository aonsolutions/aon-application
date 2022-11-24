package com.esferalia.aon.occam.impl.jooq.fiscal;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.fiscal.IMODEL184;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod184.Mod184DAO;

public class MODEL184Impl implements IMODEL184 {

	@Override
	public LinkedList<Mod184> getMod184s(AONContext ctx, int domain) {
		return Mod184DAO.getByDomain(ctx, domain);
	}

	@Override
	public Mod184 get(AONContext ctx, Integer id) {
		return Mod184DAO.getById(ctx, id);
	}

	@Override
	public Mod184 initialize(AONContext ctx, int year) {
		return Mod184DAO.initialize(ctx, year);
	}
	@Override
	public Mod184 save(AONContext ctx, Mod184 mod184) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod184DAO.save(ctx, mod184));
	}

	@Override
	public void delete(AONContext ctx, Mod184 mod184) {
		ctx.getDslContext().transaction(
				configuration -> Mod184DAO.delete(ctx, mod184));
	}

	@Override
	public Mod184 saveComments(AONContext ctx, Mod184 mod184) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod184DAO.saveComments(ctx, mod184));		
	}
	@Override
	public Mod184 changeStatus(AONContext ctx, Mod184 mod184, FiscalStatus newStatus) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod184DAO.changeStatusMod184(ctx, mod184, newStatus));		
	}
	@Override
	public Mod184 duplicate(AONContext ctx, Mod184 mod184) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod184DAO.duplicate(ctx, mod184));		
	}

}
