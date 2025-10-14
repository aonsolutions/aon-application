package com.esferalia.aon.occam.impl.jooq.fiscal;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.fiscal.IMODEL4252025;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.mod425.Mod4252025;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod425.Mod4252025DAO;

public class MODEL4252025Impl implements IMODEL4252025 {

	@Override
	public Mod4252025 get(AONContext ctx, Mod390 mod425) {
		return Mod4252025DAO.getMod4252025(ctx, mod425);
	}

	@Override
	public Mod4252025 get(AONContext ctx, Integer id) {
		return Mod4252025DAO.getById(ctx, id);
	}

	@Override
	public String getXML(AONContext ctx, int id) {
		return Mod4252025DAO.getXMLContentById(ctx, id);
	}
	
	@Override
	public Mod4252025 save(AONContext ctx, Mod4252025 mod425) {
		return ctx.getDslContext().transactionResult(
			configuration -> Mod4252025DAO.save(ctx, mod425));
	}

	@Override
	public void delete(AONContext ctx, Mod4252025 mod425) {
		ctx.getDslContext().transaction(
			configuration -> Mod4252025DAO.delete(ctx, mod425));
	}
	
	@Override
	public Mod4252025 changeStatus(AONContext ctx, Mod4252025 mod425, FiscalStatus newStatus) {
		return ctx.getDslContext().transactionResult(
			configuration -> Mod4252025DAO.changeStatus(ctx, mod425, newStatus));		
	}
	
	@Override
	public Mod4252025 aeatPresentation(AONContext ctx, Mod4252025 mod, String aeatResponse) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod4252025DAO.aeatPresentation(ctx, mod, aeatResponse));
	}

}
