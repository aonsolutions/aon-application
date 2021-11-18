package com.esferalia.aon.occam.impl.jooq.fiscal;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.fiscal.IMODEL131;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod131Activity;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod131Key;
import com.esferalia.aon.occam.impl.jooq.dao.FiscalModelDAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod131DAO;

public class MODEL131Impl implements IMODEL131 {

	@Override
	public Mod131 getMod131(AONContext ctx, int id) {
		return Mod131DAO.getMod131(ctx, id);
	}
	@Override
	public LinkedList<Mod131> getMod131s(AONContext ctx, int domain) {
		LinkedList<Mod131> list = new LinkedList<>();
		Mod131DAO.getMod131s(ctx, domain)
			.forEach(list::add);
		return list;
	}
	@Override
	public Mod131 calculate(AONContext ctx, Mod131 mod131) {
		return Mod131DAO.calculateMod131(ctx, mod131);
	}
	@Override
	public Mod131Activity calculateActivity(AONContext ctx, Mod131 mod131, Mod131Activity activity) {
		return Mod131DAO.calculateMod131Activity(ctx, mod131, activity);
	}
	
	@Override
	public Mod131 save(AONContext ctx, Mod131 mod131) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod131DAO.saveMod131(ctx, mod131));		
	}
	@Override
	public Mod131 saveComments(AONContext ctx, Mod131 mod131) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod131DAO.saveCommentsMod131(ctx, mod131));		
	}
	@Override
	public Mod131 initializeForFinish(AONContext ctx, Mod131 mod131){
		return FiscalModelDAO.initializeForFinish(ctx, mod131);
	}
	@Override
	public Mod131 markAsFinished(AONContext ctx, Mod131 mod131){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod131DAO.markAsFinished(ctx, mod131));		
	}
	@Override
	public Mod131 markAsSent(AONContext ctx, Mod131 mod131){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod131DAO.markAsSent(ctx, mod131));		
	}
	@Override
	public Mod131 markAsCustomerCheck(AONContext ctx, Mod131 mod131){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod131DAO.markAsCustomerCheck(ctx, mod131));		
	}
	@Override
	public Mod131 markAsPending(AONContext ctx, Mod131 mod131){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod131DAO.markAsPending(ctx, mod131));		
	}
	
	@Override
	public void delete(AONContext ctx, Mod131 mod131) {
		ctx.getDslContext().transaction(
				configuration -> FiscalModelDAO.delete(ctx, mod131));
	}

	@Override
	public Mod131 initialize(AONContext ctx, Mod131 mod131) {
		return Mod131DAO.initializeMod131(ctx,mod131);
	}

	@Override
	public Mod131 create(AONContext ctx, Mod131 mod131) {
		return Mod131DAO.createMod131(ctx,mod131);
	}
	@Override
	public String getInfo(AONContext ctx, Mod131 mod131, IModelScript<Mod131Key> script, FiscalModelKeyInfo infoKey) {
		return Mod131DAO.getMod131Info(ctx,mod131,script,infoKey);
	}

}
