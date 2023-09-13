package com.esferalia.aon.occam.impl.jooq.fiscal;

import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.IDAOCallback;
import com.esferalia.aon.occam.api.fiscal.IMODEL303;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.FiscalModelDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.Mod303DAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.Mod303InfoDAO;

public class MODEL303Impl implements IMODEL303 {

	@Override
	public Mod303 get(AONContext ctx, int id) {
		return Mod303DAO.get(ctx, id);
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
		return Mod303DAO.calculate(mod303);
	}
	@Override
	public Mod303 calculateProrrate(AONContext ctx, Mod303 mod303) {
		return Mod303DAO.calculateProrrate(mod303);
	}
	@Override
	public Mod303 save(AONContext ctx, Mod303 mod303) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod303DAO.save(ctx, mod303));		
	}
	@Override
	public Mod303 saveComments(AONContext ctx, Mod303 mod303) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod303DAO.saveComments(ctx, mod303));		
	}
	@Override
	public Mod303 initializeForFinish(AONContext ctx, Mod303 mod303){
		return Mod303DAO.initializeForFinish(ctx, mod303);
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
				configuration -> Mod303DAO.markAsCustomerCheck(ctx, mod303));		
	}
	@Override
	public Mod303 markAsCustomerAccepted(AONContext ctx, Mod303 mod303) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod303DAO.markAsCustomerAccepted(ctx, mod303));		
	}
	@Override
	public Mod303 markAsCustomerRejected(AONContext ctx, Mod303 mod303, String reason) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod303DAO.markAsCustomerRejected(ctx, mod303, reason));		
	}

	@Override
	public void delete(AONContext ctx, Mod303 mod303) {
		ctx.getDslContext().transaction(
				configuration -> FiscalModelDAO.delete(ctx, mod303));
	}

	@Override
	public Mod303 initialize(AONContext ctx, Mod303 mod303) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod303DAO.initialize(ctx,mod303));
	}

	@Override
	public Mod303 create(AONContext ctx, Mod303 mod303) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod303DAO.create(ctx,mod303));
	}
	@Override
	public Mod303 reset(AONContext ctx, Mod303 mod303) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod303DAO.reset(ctx,mod303));
	}

	@Override
	public String getInfo(AONContext ctx, Mod303 mod303, IModelScript<Mod303Key> script, FiscalModelKeyInfo infoKey) {
		return Mod303InfoDAO.getInfo(ctx,mod303,script,infoKey);
	}
	
	@Override
	public Stream<VatContext> getInfo(CloseableAONContext ctx, Mod303 mod303, Mod303Key key, IDAOCallback callback) {
		return  Mod303InfoDAO.getModelInvoicesInfo(ctx, mod303, key)
			.onClose(() -> {
				if (callback != null) {
					callback.onFinish();
				}
			});
	}
	
	@Override
	public Mod303 aeatPresentation(AONContext ctx, Mod303 mod303, String aeatResponse) {
		return ctx.getDslContext().transactionResult(
			configuration -> Mod303DAO.aeatPresentation(ctx, mod303, aeatResponse));
	}

	@Override
	public Mod303 doRecord(AONContext ctx, Mod303 mod303){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod303DAO.doRecord(ctx, mod303));		
	}
	@Override
	public Mod303 unrecord(AONContext ctx, Mod303 mod303){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod303DAO.unrecord(ctx, mod303));		
	}
}
