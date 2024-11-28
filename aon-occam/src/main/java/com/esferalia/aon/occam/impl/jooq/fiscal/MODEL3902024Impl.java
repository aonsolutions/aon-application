package com.esferalia.aon.occam.impl.jooq.fiscal;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.fiscal.IMODEL3902024;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902024;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod390.Mod3902024DAO;

public class MODEL3902024Impl implements IMODEL3902024 {

	@Override
	public Mod3902024 get(AONContext ctx, Mod390 mod390) {
		return Mod3902024DAO.getMod3902024(ctx, mod390);
	}

	@Override
	public Mod3902024 get(AONContext ctx, Integer id) {
		return Mod3902024DAO.getById(ctx, id);
	}

	@Override
	public String getXML(AONContext ctx, int id) {
		return Mod3902024DAO.getXMLContentById(ctx, id);
	}
	
	@Override
	public Mod3902024 save(AONContext ctx, Mod3902024 mod390) {
		return ctx.getDslContext().transactionResult(
			configuration -> Mod3902024DAO.save(ctx, mod390));
	}

	@Override
	public void delete(AONContext ctx, Mod3902024 mod390) {
		ctx.getDslContext().transaction(
			configuration -> Mod3902024DAO.delete(ctx, mod390));
	}
	
	@Override
	public Mod3902024 changeStatus(AONContext ctx, Mod3902024 mod390, FiscalStatus newStatus) {
		return ctx.getDslContext().transactionResult(
			configuration -> Mod3902024DAO.changeStatus(ctx, mod390, newStatus));		
	}
	
	@Override
	public Mod3902024 aeatPresentation(AONContext ctx, Mod3902024 mod, String aeatResponse) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod3902024DAO.aeatPresentation(ctx, mod, aeatResponse));
	}

}
