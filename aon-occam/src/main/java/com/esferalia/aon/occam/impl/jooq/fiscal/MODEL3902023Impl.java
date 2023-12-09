package com.esferalia.aon.occam.impl.jooq.fiscal;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.fiscal.IMODEL3902023;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902023;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod390.Mod3902023DAO;

public class MODEL3902023Impl implements IMODEL3902023 {

	@Override
	public Mod3902023 get(AONContext ctx, Mod390 mod390) {
		return Mod3902023DAO.getMod3902023(ctx, mod390);
	}

	@Override
	public Mod3902023 get(AONContext ctx, Integer id) {
		return Mod3902023DAO.getById(ctx, id);
	}

	@Override
	public String getXML(AONContext ctx, int id) {
		return Mod3902023DAO.getXMLContentById(ctx, id);
	}
	
	@Override
	public Mod3902023 save(AONContext ctx, Mod3902023 mod390) {
		return ctx.getDslContext().transactionResult(
			configuration -> Mod3902023DAO.save(ctx, mod390));
	}

	@Override
	public void delete(AONContext ctx, Mod3902023 mod390) {
		ctx.getDslContext().transaction(
			configuration -> Mod3902023DAO.delete(ctx, mod390));
	}
	
	@Override
	public Mod3902023 changeStatus(AONContext ctx, Mod3902023 mod390, FiscalStatus newStatus) {
		return ctx.getDslContext().transactionResult(
			configuration -> Mod3902023DAO.changeStatus(ctx, mod390, newStatus));		
	}
	
	@Override
	public Mod3902023 aeatPresentation(AONContext ctx, Mod3902023 mod, String aeatResponse) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod3902023DAO.aeatPresentation(ctx, mod, aeatResponse));
	}

}
