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
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod131.Mod131DAO;

public class MODEL131Impl implements IMODEL131 {

	@Override
	public Mod131 get(AONContext ctx, int id) {
		return Mod131DAO.get(ctx, id);
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
		return Mod131DAO.calculate(ctx, mod131);
	}
	@Override
	public Mod131Activity calculateActivity(AONContext ctx, Mod131 mod131, Mod131Activity activity) {
		return Mod131DAO.calculateActivity(ctx, mod131, activity);
	}
	
	@Override
	public Mod131 save(AONContext ctx, Mod131 mod131) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod131DAO.save(ctx, mod131));		
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
		return Mod131DAO.initialize(ctx,mod131);
	}

	@Override
	public Mod131 create(AONContext ctx, Mod131 mod131) {
		return Mod131DAO.create(ctx,mod131);
	}
	
	@Override
	public Mod131 reset(AONContext ctx, Mod131 mod131) {
		return Mod131DAO.reset(ctx,mod131);
	}

	@Override
	public String getInfo(AONContext ctx, Mod131 mod131, IModelScript<Mod131Key> script, FiscalModelKeyInfo infoKey) {
		return Mod131DAO.getInfo(ctx,mod131,script,infoKey);
	}
	@Override
	public Mod131 aeatPresentation(AONContext ctx, Mod131 mod131, String aeatResponse) {
		return Mod131DAO.aeatPresentation(ctx, mod131, aeatResponse);
	}


}
