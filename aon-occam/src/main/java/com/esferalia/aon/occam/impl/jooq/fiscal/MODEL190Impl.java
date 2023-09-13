package com.esferalia.aon.occam.impl.jooq.fiscal;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.fiscal.IMODEL190;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod190.Mod190DAO;

public class MODEL190Impl implements IMODEL190 {

	@Override
	public LinkedList<Mod190> getMod190s(AONContext ctx, int domain) {
		return Mod190DAO.getByDomain(ctx, domain);
	}

	@Override
	public Mod190 get(AONContext ctx, Integer id) {
		return Mod190DAO.getById(ctx, id);
	}

	@Override
	public Mod190 initialize(AONContext ctx, int year) {
		return Mod190DAO.initialize(ctx, year);
	}
	@Override
	public Mod190 save(AONContext ctx, Mod190 mod190) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod190DAO.save(ctx, mod190));
	}

	@Override
	public void delete(AONContext ctx, Mod190 mod190) {
		ctx.getDslContext().transaction(
				configuration -> Mod190DAO.delete(ctx, mod190));
	}

	@Override
	public Mod190Detail getDetail(AONContext ctx, Integer id) {
		return Mod190DAO.getDetail(ctx, id);
	}
	
	@Override
	public Mod190 saveComments(AONContext ctx, Mod190 mod190) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod190DAO.saveComments(ctx, mod190));		
	}
	@Override
	public Mod190 changeStatus(AONContext ctx, Mod190 mod190, FiscalStatus newStatus) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod190DAO.changeStatus(ctx, mod190, newStatus));		
	}
	@Override
	public Mod190 duplicate(AONContext ctx, Mod190 mod190) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod190DAO.duplicate(ctx, mod190));		
	}

	@Override
	public LinkedList<Mod190Detail> validateSalaries(AONContext ctx, Mod190 mod190) {
		return ctx.getDslContext().transactionResult(
			configuration -> Mod190DAO.validateSalaries(ctx, mod190));		
	}
	
	@Override
	public Mod190 aeatPresentation(AONContext ctx, Mod190 mod, String aeatResponse) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod190DAO.aeatPresentation(ctx, mod, aeatResponse));
	}

}
