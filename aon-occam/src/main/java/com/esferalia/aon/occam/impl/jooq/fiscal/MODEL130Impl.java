package com.esferalia.aon.occam.impl.jooq.fiscal;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.fiscal.IMODEL130;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod130Key;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.FiscalModelDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod130.Mod130DAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod130.Mod130InfoDAO;

public class MODEL130Impl implements IMODEL130 {

	@Override
	public Mod130 get(AONContext ctx, int id) {
		return Mod130DAO.get(ctx, id);
	}
	@Override
	public LinkedList<Mod130> getMod130s(AONContext ctx, int domain) {
		LinkedList<Mod130> list = new LinkedList<>();
		Mod130DAO.getMod130s(ctx, domain)
			.forEach(list::add);
		return list;
	}
	@Override
	public Mod130 calculate(AONContext ctx, Mod130 mod130) {
		return Mod130DAO.calculate(ctx, mod130);
	}
	@Override
	public Mod130 save(AONContext ctx, Mod130 mod130) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod130DAO.save(ctx, mod130));		
	}
	@Override
	public Mod130 saveComments(AONContext ctx, Mod130 mod130) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod130DAO.saveComments(ctx, mod130));		
	}
	@Override
	public Mod130 initializeForFinish(AONContext ctx, Mod130 mod130){
		return Mod130DAO.initializeForFinish(ctx, mod130);
	}
	
	@Override
	public void delete(AONContext ctx, Mod130 mod130) {
		ctx.getDslContext().transaction(
				configuration -> FiscalModelDAO.delete(ctx, mod130));
	}

	@Override
	public Mod130 initialize(AONContext ctx, Mod130 mod130) {
		return Mod130DAO.initialize(ctx,mod130);
	}

	@Override
	public Mod130 create(AONContext ctx, Mod130 mod130) {
		return Mod130DAO.create(ctx,mod130);
	}
	
	@Override
	public Mod130 aeatPresentation(AONContext ctx, Mod130 mod130, String aeatResponse) {
		return Mod130DAO.aeatPresentation(ctx, mod130, aeatResponse);
	}

	@Override
	public Mod130 markAsFinished(AONContext ctx, Mod130 mod130){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod130DAO.markAsFinished(ctx, mod130));		
	}
	@Override
	public Mod130 markAsPending(AONContext ctx, Mod130 mod130){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod130DAO.markAsPending(ctx, mod130));		
	}
	@Override
	public Mod130 markAsSent(AONContext ctx, Mod130 mod130){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod130DAO.markAsSent(ctx, mod130));		
	}
	@Override
	public Mod130 markAsCustomerCheck(AONContext ctx, Mod130 mod130) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod130DAO.markAsCustomerCheck(ctx, mod130));		
	}
	
	@Override
	public Mod130 markAsCustomerAccepted(AONContext ctx, Mod130 mod130) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod130DAO.markAsCustomerAccepted(ctx, mod130));		
	}

	@Override
	public Mod130 markAsCustomerRejected(AONContext ctx, Mod130 mod130, String reason) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod130DAO.markAsCustomerRejected(ctx, mod130, reason));		
	}
	
	@Override
	public String getInfo(AONContext ctx, Mod130 mod130, IModelScript<Mod130Key> script, FiscalModelKeyInfo infoKey) {
		return Mod130InfoDAO.getInfo(ctx,mod130,script,infoKey);
	}

}
