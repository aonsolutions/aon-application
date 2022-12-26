package com.esferalia.aon.occam.impl.jooq.fiscal;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.fiscal.IMODEL3902021;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902021;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod390.Mod3902021DAO;

public class MODEL3902021Impl implements IMODEL3902021 {

	@Override
	public Mod3902021 get(AONContext ctx, Mod390 mod390) {
		return Mod3902021DAO.getMod3902021(ctx, mod390);
	}

	@Override
	public Mod3902021 get(AONContext ctx, Integer id) {
		return Mod3902021DAO.getById(ctx, id);
	}

	@Override
	public String getXML(AONContext ctx, int id) {
		return Mod3902021DAO.getXMLContentById(ctx, id);
	}
	
	@Override
	public Mod3902021 save(AONContext ctx, Mod3902021 mod390) {
		return ctx.getDslContext().transactionResult(
			configuration -> Mod3902021DAO.save(ctx, mod390));
	}

	@Override
	public void delete(AONContext ctx, Mod3902021 mod390) {
		ctx.getDslContext().transaction(
			configuration -> Mod3902021DAO.delete(ctx, mod390));
	}
	
	@Override
	public Mod3902021 changeStatus(AONContext ctx, Mod3902021 mod390, FiscalStatus newStatus) {
		return ctx.getDslContext().transactionResult(
			configuration -> Mod3902021DAO.changeStatus(ctx, mod390, newStatus));		
	}
	
	@Override
	public Mod3902021 aeatPresentation(AONContext ctx, Mod3902021 mod, String aeatResponse) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod3902021DAO.aeatPresentation(ctx, mod, aeatResponse));
	}

}
