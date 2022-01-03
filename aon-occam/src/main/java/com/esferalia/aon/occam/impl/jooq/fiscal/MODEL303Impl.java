package com.esferalia.aon.occam.impl.jooq.fiscal;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.fiscal.IMODEL303;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.impl.jooq.dao.FiscalModelDAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod303DAO;

public class MODEL303Impl implements IMODEL303 {

	@Override
	public Mod303 getMod303(AONContext ctx, int id) {
		return Mod303DAO.getMod303(ctx, id);
	}
	@Override
	public LinkedList<Mod303> getMod303s(AONContext ctx, int domain) {
		LinkedList<Mod303> list = new LinkedList<>();
		Mod303DAO.getMod303s(ctx, domain)
			.forEach(list::add);
		return list;
	}
	@Override
	public Mod303 calculate(AONContext ctx, Mod303 mod303) {
		return Mod303DAO.calculate(ctx, mod303);
	}
	@Override
	public Mod303 calculateProrrate(AONContext ctx, Mod303 mod303) {
		return Mod303DAO.calculateProrrate(mod303);
	}
	@Override
	public Mod303 save(AONContext ctx, Mod303 mod303) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod303DAO.saveMod303(ctx, mod303));		
	}
	@Override
	public Mod303 saveComments(AONContext ctx, Mod303 mod303) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod303DAO.saveCommentsMod303(ctx, mod303));		
	}
	@Override
	public Mod303 initializeForFinish(AONContext ctx, Mod303 mod303){
		return FiscalModelDAO.initializeForFinish(ctx, mod303);
	}
	@Override
	public Mod303 markAsFinished(AONContext ctx, Mod303 mod303){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod303DAO.markAsFinished(ctx, mod303));		
	}
	@Override
	public Mod303 markAsPending(AONContext ctx, Mod303 mod303){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod303DAO.markAsPending(ctx, mod303));		
	}
	@Override
	public Mod303 markAsSent(AONContext ctx, Mod303 mod303){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod303DAO.markAsSent(ctx, mod303));		
	}
	@Override
	public Mod303 markAsCustomerCheck(AONContext ctx, Mod303 mod303) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod303DAO.markAsCustomerCheckMod303(ctx, mod303));		
	}

	@Override
	public void delete(AONContext ctx, Mod303 mod303) {
		ctx.getDslContext().transaction(
				configuration -> FiscalModelDAO.delete(ctx, mod303));
	}

	@Override
	public Mod303 initialize(AONContext ctx, Mod303 mod303) {
		return Mod303DAO.initialize(ctx,mod303);
	}

	@Override
	public Mod303 create(AONContext ctx, Mod303 mod303) {
		return Mod303DAO.create(ctx,mod303);
	}
	@Override
	public Mod303 reset(AONContext ctx, Mod303 mod303) {
		return Mod303DAO.reset(ctx,mod303);
	}
	@Override
	public Mod303 declarationChanged(AONContext ctx, Mod303 mod303) {
		return Mod303DAO.declarationChanged(ctx,mod303);
	}

	@Override
	public String getInfo(AONContext ctx, Mod303 mod303, IModelScript<Mod303Key> script, FiscalModelKeyInfo infoKey) {
		return Mod303DAO.getInfo(ctx,mod303,script,infoKey);
	}
	
	@Override
	public Mod303 aeatPresentation(AONContext ctx, Mod303 mod303, String aeatResponse) {
		return Mod303DAO.aeatPresentation(ctx, mod303, aeatResponse);
	}

}
