package com.esferalia.aon.occam.impl.jooq.fiscal;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.fiscal.IMODEL111;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.FiscalModelDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod111.Mod111DAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod111.Mod111InfoDAO;

public class MODEL111Impl implements IMODEL111 {

	@Override
	public Mod111 get(AONContext ctx, int id) {
		return Mod111DAO.get(ctx, id);
	}
	@Override
	public LinkedList<Mod111> getMod111s(AONContext ctx, int domain) {
		LinkedList<Mod111> list = new LinkedList<>();
		Mod111DAO.getMod111s(ctx, domain)
			.forEach(list::add);
		return list;
	}
	@Override
	public Mod111 calculate(AONContext ctx, Mod111 mod111) {
		return Mod111DAO.calculate(mod111);
	}
	@Override
	public Mod111 save(AONContext ctx, Mod111 mod111) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod111DAO.save(ctx, mod111));		
	}
	@Override
	public Mod111 saveComments(AONContext ctx, Mod111 mod111) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod111DAO.saveComments(ctx, mod111));		
	}
	@Override
	public Mod111 initializeForFinish(AONContext ctx, Mod111 mod111){
		return Mod111DAO.initializeForFinish(ctx, mod111);
	}
	@Override
	public Mod111 markAsFinished(AONContext ctx, Mod111 mod111){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod111DAO.markAsFinished(ctx, mod111));		
	}
	@Override
	public Mod111 markAsPending(AONContext ctx, Mod111 mod111){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod111DAO.markAsPending(ctx, mod111));		
	}
	
	@Override
	public Mod111 markAsSent(AONContext ctx, Mod111 mod111){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod111DAO.markAsSent(ctx, mod111));		
	}

	@Override
	public Mod111 markAsCustomerCheck(AONContext ctx, Mod111 mod111) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod111DAO.markAsCustomerCheck(ctx, mod111));		
	}

	@Override
	public Mod111 markAsCustomerAccepted(AONContext ctx, Mod111 mod111) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod111DAO.markAsCustomerAccepted(ctx, mod111));		
	}

	@Override
	public Mod111 markAsCustomerRejected(AONContext ctx, Mod111 mod111, String reason) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod111DAO.markAsCustomerRejected(ctx, mod111, reason));		
	}

	@Override
	public void delete(AONContext ctx, Mod111 mod111) {
		ctx.getDslContext().transaction(
				configuration -> FiscalModelDAO.delete(ctx, mod111));
	}

	@Override
	public Mod111 initialize(AONContext ctx, Mod111 mod111) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod111DAO.initialize(ctx,mod111));
	}

	@Override
	public Mod111 create(AONContext ctx, Mod111 mod111) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod111DAO.create(ctx,mod111));
	}
	@Override
	public String getInfo(AONContext ctx, Mod111 mod111, IModelScript<Mod111Key> script, FiscalModelKeyInfo infoKey) {
		return Mod111InfoDAO.getInfo(ctx,mod111,script,infoKey);
	}
	@Override
	public Mod111 aeatPresentation(AONContext ctx, Mod111 mod111, String aeatResponse) {
		return ctx.getDslContext().transactionResult(
			configuration -> Mod111DAO.aeatPresentation(ctx, mod111, aeatResponse));
	}

}
