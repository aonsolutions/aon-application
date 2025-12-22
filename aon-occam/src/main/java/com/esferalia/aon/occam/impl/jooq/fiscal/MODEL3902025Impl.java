package com.esferalia.aon.occam.impl.jooq.fiscal;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.fiscal.IMODEL3902025;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902025;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod390.Mod3902025DAO;

public class MODEL3902025Impl implements IMODEL3902025 {

	@Override
	public Mod3902025 get(AONContext ctx, Mod390 mod390) {
		return Mod3902025DAO.getMod3902025(ctx, mod390);
	}

	@Override
	public Mod3902025 get(AONContext ctx, Integer id) {
		return Mod3902025DAO.getById(ctx, id);
	}

	@Override
	public String getXML(AONContext ctx, int id) {
		return Mod3902025DAO.getXMLContentById(ctx, id);
	}
	
	@Override
	public Mod3902025 save(AONContext ctx, Mod3902025 mod390) {
		return ctx.getDslContext().transactionResult(
			configuration -> Mod3902025DAO.save(ctx, mod390));
	}

	@Override
	public void delete(AONContext ctx, Mod3902025 mod390) {
		ctx.getDslContext().transaction(
			configuration -> Mod3902025DAO.delete(ctx, mod390));
	}
	
	@Override
	public Mod3902025 changeStatus(AONContext ctx, Mod3902025 mod390, FiscalStatus newStatus) {
		return ctx.getDslContext().transactionResult(
			configuration -> Mod3902025DAO.changeStatus(ctx, mod390, newStatus));		
	}
	
	@Override
	public Mod3902025 aeatPresentation(AONContext ctx, Mod3902025 mod, String aeatResponse) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod3902025DAO.aeatPresentation(ctx, mod, aeatResponse));
	}

}
