package com.esferalia.aon.occam.impl.jooq.fiscal;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.fiscal.IMODEL123;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod123Key;
import com.esferalia.aon.occam.impl.jooq.dao.FiscalModelDAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod123DAO;

public class MODEL123Impl implements IMODEL123 {

	@Override
	public Mod123 getMod123(AONContext ctx, int id) {
		return Mod123DAO.getMod123(ctx, id);
	}
	@Override
	public LinkedList<Mod123> getMod123s(AONContext ctx, int domain) {
		LinkedList<Mod123> list = new LinkedList<>();
		Mod123DAO.getMod123s(ctx, domain)
			.forEach(list::add);
		return list;
	}
	@Override
	public Mod123 calculate(AONContext ctx, Mod123 mod123) {
		return Mod123DAO.calculateMod123(ctx, mod123);
	}
	@Override
	public Mod123 save(AONContext ctx, Mod123 mod123) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod123DAO.saveMod123(ctx, mod123));		
	}
	@Override
	public Mod123 saveComments(AONContext ctx, Mod123 mod123) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod123DAO.saveCommentsMod123(ctx, mod123));		
	}
	@Override
	public Mod123 initializeForFinish(AONContext ctx, Mod123 mod123){
		return FiscalModelDAO.initializeForFinish(ctx, mod123);
	}
	@Override
	public Mod123 markAsFinished(AONContext ctx, Mod123 mod123){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod123DAO.markAsFinished(ctx, mod123));		
	}
	@Override
	public Mod123 markAsSent(AONContext ctx, Mod123 mod123){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod123DAO.markAsSent(ctx, mod123));		
	}
	@Override
	public Mod123 markAsCustomerCheck(AONContext ctx, Mod123 mod123) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod123DAO.markAsCustomerCheck(ctx, mod123));		
	}
	@Override
	public Mod123 markAsPending(AONContext ctx, Mod123 mod123){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod123DAO.markAsPending(ctx, mod123));		
	}
	
	@Override
	public void delete(AONContext ctx, Mod123 mod123) {
		ctx.getDslContext().transaction(
				configuration -> FiscalModelDAO.delete(ctx, mod123));
	}

	@Override
	public Mod123 initialize(AONContext ctx, Mod123 mod123) {
		return Mod123DAO.initializeMod123(ctx,mod123);
	}

	@Override
	public Mod123 create(AONContext ctx, Mod123 mod123) {
		return Mod123DAO.createMod123(ctx,mod123);
	}
	@Override
	public Mod123 reset(AONContext ctx, Mod123 mod123) {
		return Mod123DAO.resetMod123(ctx,mod123);
	}
	@Override
	public String getInfo(AONContext ctx, Mod123 mod123, IModelScript<Mod123Key> script, FiscalModelKeyInfo infoKey) {
		return Mod123DAO.getMod123Info(ctx,mod123,script,infoKey);
	}
	@Override
	public Mod123 aeatPresentation(AONContext ctx, Mod123 mod123, String aeatResponse) {
		return Mod123DAO.aeatPresentation(ctx, mod123, aeatResponse);
	}

}
