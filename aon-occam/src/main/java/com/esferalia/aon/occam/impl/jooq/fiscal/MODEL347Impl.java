package com.esferalia.aon.occam.impl.jooq.fiscal;

import java.io.Writer;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.fiscal.IMODEL347;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod347;
import com.esferalia.aon.occam.api.model.fiscal.Mod347Declared;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.impl.jooq.dao.Mod347DAO;

public class MODEL347Impl implements IMODEL347 {

	@Override
	public LinkedList<Mod347> getMod347s(AONContext ctx, int domain) {
		return Mod347DAO.getByDomain(ctx, domain);
	}

	@Override
	public Mod347 get(AONContext ctx, Integer id) {
		return Mod347DAO.getById(ctx, id);
	}

	@Override
	public Mod347 initialize(AONContext ctx, int year) {
		return Mod347DAO.initialize(ctx, year);		
	}
	@Override
	public Mod347 reset(AONContext ctx, Mod347 mod347) {
		return ctx.getDslContext().transactionResult(
			configuration -> Mod347DAO.reset(ctx, mod347));
	}

	@Override
	public Mod347 save(AONContext ctx, Mod347 mod347) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod347DAO.save(ctx, mod347));
	}

	@Override
	public void delete(AONContext ctx, Mod347 mod347) {
		ctx.getDslContext().transaction(
				configuration -> Mod347DAO.delete(ctx, mod347));
	}

	@Override
	public Mod347 saveComments(AONContext ctx, Mod347 mod347) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod347DAO.saveComments(ctx, mod347));		
	}
	@Override
	public Mod347 changeStatus(AONContext ctx, Mod347 mod347, FiscalStatus newStatus) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod347DAO.changeStatus(ctx, mod347, newStatus));		
	}
	@Override
	public String getInfo(AONContext ctx, Mod347 mod347, Mod347Declared declared, FiscalModelKeyInfo infoKey) {
		return Mod347DAO.getInfo(ctx, mod347, declared, infoKey);
	}
	@Override
	public Mod347 duplicate(AONContext ctx, Mod347 mod347) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod347DAO.duplicate(ctx, mod347));		
	}

	@Override
	public void writeMailMergeReport(AONContext ctx, Mod347 mod347, Writer writer) {
		Mod347DAO.writeMailMergeReport(ctx, mod347,writer);
	}
	
	@Override
	public Mod347 aeatPresentation(AONContext ctx, Mod347 mod, String aeatResponse) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod347DAO.aeatPresentation(ctx, mod, aeatResponse));
	}
	
}
