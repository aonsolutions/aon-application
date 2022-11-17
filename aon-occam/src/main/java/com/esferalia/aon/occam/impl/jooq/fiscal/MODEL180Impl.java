package com.esferalia.aon.occam.impl.jooq.fiscal;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.fiscal.IMODEL180;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.Mod180Detail;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod180.Mod180DAO;

public class MODEL180Impl implements IMODEL180 {

	@Override
	public LinkedList<Mod180> getMod180s(AONContext ctx, int domain) {
		return Mod180DAO.getByDomain(ctx, domain);
	}

	@Override
	public Mod180 get(AONContext ctx, Integer id) {
		return Mod180DAO.getById(ctx, id);
	}

	@Override
	public Mod180 initialize(AONContext ctx, int year) {
		return Mod180DAO.initialize(ctx, year);
	}

	@Override
	public Mod180 save(AONContext ctx, Mod180 mod180) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod180DAO.save(ctx, mod180));
	}

	@Override
	public void delete(AONContext ctx, Mod180 mod180) {
		ctx.getDslContext().transaction(
				configuration -> Mod180DAO.delete(ctx, mod180));
	}

	@Override
	public Mod180Detail getDetail(AONContext ctx, Integer id) {
		return Mod180DAO.getDetail(ctx, id);
	}
	
	@Override
	public Mod180 saveComments(AONContext ctx, Mod180 mod180) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod180DAO.saveComments(ctx, mod180));		
	}
	@Override
	public Mod180 changeStatus(AONContext ctx, Mod180 mod180, FiscalStatus newStatus) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod180DAO.changeStatusMod180(ctx, mod180, newStatus));		
	}
	@Override
	public Mod180 duplicate(AONContext ctx, Mod180 mod180) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod180DAO.duplicate(ctx, mod180));		
	}


}
