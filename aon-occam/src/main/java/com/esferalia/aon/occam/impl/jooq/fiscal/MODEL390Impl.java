package com.esferalia.aon.occam.impl.jooq.fiscal;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.fiscal.IMODEL390;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod390.Mod390DAO;

public class MODEL390Impl implements IMODEL390 {

	@Override
	public Mod390 getMod390(AONContext ctx, Integer id) {
		return Mod390DAO.getById(ctx, ctx.getDomainId(), id);
	}
	
	@Override
	public LinkedList<Mod390> getMod390s(AONContext ctx, int domain) {
		return Mod390DAO.getByDomain(ctx, domain);
	}

	@Override
	public Mod390 initialize(AONContext ctx, int year) {
		return Mod390DAO.initialize(ctx, year);
	}

//	@Override
//	public Mod390 create(AONContext ctx, Mod390 mod390) {
//		return Mod390DAO.create(ctx, mod390);
//	}
	
	@Override
	public Mod390 saveComments(AONContext ctx, Mod390 mod390) {
		return ctx.getDslContext().transactionResult (
			configuration -> Mod390DAO.saveComments(ctx, mod390));
	}
	
	@Override
	public void deleteMod390(AONContext ctx, Mod390 mod390) {
		ctx.getDslContext().transaction(
			configuration -> Mod390DAO.delete(ctx, mod390));
	}

}
