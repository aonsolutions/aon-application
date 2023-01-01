package com.esferalia.aon.occam.impl.jooq.fiscal;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.fiscal.IMODEL3902022;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902022;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod390.Mod3902022DAO;

public class MODEL3902022Impl implements IMODEL3902022 {

	@Override
	public Mod3902022 get(AONContext ctx, Mod390 mod390) {
		return Mod3902022DAO.getMod3902022(ctx, mod390);
	}

	@Override
	public Mod3902022 get(AONContext ctx, Integer id) {
		return Mod3902022DAO.getById(ctx, id);
	}

	@Override
	public String getXML(AONContext ctx, int id) {
		return Mod3902022DAO.getXMLContentById(ctx, id);
	}
	
	@Override
	public Mod3902022 save(AONContext ctx, Mod3902022 mod390) {
		return ctx.getDslContext().transactionResult(
			configuration -> Mod3902022DAO.save(ctx, mod390));
	}

	@Override
	public void delete(AONContext ctx, Mod3902022 mod390) {
		ctx.getDslContext().transaction(
			configuration -> Mod3902022DAO.delete(ctx, mod390));
	}
	
	@Override
	public Mod3902022 changeStatus(AONContext ctx, Mod3902022 mod390, FiscalStatus newStatus) {
		return ctx.getDslContext().transactionResult(
			configuration -> Mod3902022DAO.changeStatus(ctx, mod390, newStatus));		
	}
	
	@Override
	public Mod3902022 aeatPresentation(AONContext ctx, Mod3902022 mod, String aeatResponse) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod3902022DAO.aeatPresentation(ctx, mod, aeatResponse));
	}

}
