package com.esferalia.aon.occam.impl.jooq.fiscal;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.fiscal.IMODEL202;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod202Key;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.FiscalModelDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod202.Mod202DAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod202.Mod202InfoDAO;

public class MODEL202Impl implements IMODEL202 {

	@Override
	public Mod202 get(AONContext ctx, int id) {
		return Mod202DAO.get(ctx, id);
	}
	@Override
	public LinkedList<Mod202> getMod202s(AONContext ctx, int domain) {
		LinkedList<Mod202> list = new LinkedList<>();
		Mod202DAO.getMod202s(ctx, domain)
			.forEach(list::add);
		return list;
	}
	
	@Override
	public Mod202 calculate(AONContext ctx, Mod202 mod202) {
		return Mod202DAO.calculate( ctx, mod202 );
	}
	
	@Override
	public Mod202 save(AONContext ctx, Mod202 mod202) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod202DAO.save(ctx, mod202));
	}
	@Override
	public Mod202 saveComments(AONContext ctx, Mod202 mod202) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod202DAO.saveComments(ctx, mod202));		
	}
	
	@Override
	public Mod202 initializeForFinish(AONContext ctx, Mod202 mod202){
		return Mod202DAO.initializeForFinish(ctx, mod202);
	}
	
	@Override
	public Mod202 initialize(AONContext ctx, Mod202 mod202) {
		return ctx.getDslContext().transactionResult(
			configuration -> Mod202DAO.initialize(ctx,mod202));
	}
	
	@Override
	public Mod202 create(AONContext ctx, Mod202 mod202) {
		return Mod202DAO.create(ctx,mod202);
	}
	
	@Override
	public void delete(AONContext ctx, Mod202 mod202) {
		ctx.getDslContext().transaction(
				configuration -> FiscalModelDAO.delete(ctx, mod202));
	}

	@Override
	public String getInfo(AONContext ctx, Mod202 mod202, IModelScript<Mod202Key> script, FiscalModelKeyInfo infoKey) {
		return Mod202InfoDAO.getInfo(ctx,mod202,script,infoKey);
	}
	
	
	@Override
	public Mod202 markAsFinished(AONContext ctx, Mod202 mod202){
		return ctx.getDslContext().transactionResult(
			configuration -> Mod202DAO.markAsFinished(ctx, mod202));		
	}
	@Override
	public Mod202 markAsSent(AONContext ctx, Mod202 mod202){
		return ctx.getDslContext().transactionResult(
			configuration -> Mod202DAO.markAsSent(ctx, mod202));		
	}
	@Override
	public Mod202 markAsCustomerCheck(AONContext ctx, Mod202 mod202){
		return ctx.getDslContext().transactionResult(
			configuration -> Mod202DAO.markAsCustomerCheck(ctx, mod202));		
	}
	@Override
	public Mod202 markAsCustomerAccepted(AONContext ctx, Mod202 mod202){
		return ctx.getDslContext().transactionResult(
			configuration -> Mod202DAO.markAsCustomerAccepted(ctx, mod202));		
	}
	@Override
	public Mod202 markAsCustomerRejected(AONContext ctx, Mod202 mod202, String reason) {
		return ctx.getDslContext().transactionResult(
			configuration -> Mod202DAO.markAsCustomerRejected(ctx, mod202, reason));
	}
	
	@Override
	public Mod202 markAsPending(AONContext ctx, Mod202 mod202){
		return ctx.getDslContext().transactionResult(
			configuration -> Mod202DAO.markAsPending(ctx, mod202));		
	}

	@Override
	public Mod202 aeatPresentation(AONContext ctx, Mod202 mod202, String aeatResponse) {
		return Mod202DAO.aeatPresentation(ctx, mod202, aeatResponse);
	}
}
