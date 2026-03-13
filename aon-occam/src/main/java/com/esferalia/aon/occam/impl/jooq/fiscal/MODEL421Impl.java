package com.esferalia.aon.occam.impl.jooq.fiscal;

import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.IDAOCallback;
import com.esferalia.aon.occam.api.fiscal.IMODEL421;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod421;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod421Key;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.FiscalModelDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod421.Mod421DAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod421.Mod421InfoDAO;

public class MODEL421Impl implements IMODEL421 {

	@Override
	public Mod421 get(AONContext ctx, int id) {
		return Mod421DAO.get(ctx, id);
	}
	@Override
	public LinkedList<Mod421> getMod421s(AONContext ctx, int domain) {
		LinkedList<Mod421> list = new LinkedList<>();
		Mod421DAO.getMod421s(ctx, domain)
			.forEach(list::add);
		return list;
	}
	@Override
	public Mod421 calculate(AONContext ctx, Mod421 mod421) {
		return Mod421DAO.calculate(mod421);
	}
	@Override
	public Mod421 save(AONContext ctx, Mod421 mod421) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod421DAO.save(ctx, mod421));		
	}
	@Override
	public Mod421 saveComments(AONContext ctx, Mod421 mod421) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod421DAO.saveComments(ctx, mod421));		
	}
	@Override
	public Mod421 initializeForFinish(AONContext ctx, Mod421 mod421){
		return Mod421DAO.initializeForFinish(ctx, mod421);
	}
	@Override
	public Mod421 markAsFinished(AONContext ctx, Mod421 mod421){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod421DAO.markAsFinished(ctx, mod421));		
	}
	@Override
	public Mod421 markAsPending(AONContext ctx, Mod421 mod421){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod421DAO.markAsPending(ctx, mod421));		
	}
	@Override
	public Mod421 markAsSent(AONContext ctx, Mod421 mod421){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod421DAO.markAsSent(ctx, mod421));		
	}
	@Override
	public Mod421 markAsCustomerCheck(AONContext ctx, Mod421 mod421) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod421DAO.markAsCustomerCheck(ctx, mod421));		
	}
	@Override
	public Mod421 markAsCustomerAccepted(AONContext ctx, Mod421 mod421) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod421DAO.markAsCustomerAccepted(ctx, mod421));		
	}
	@Override
	public Mod421 markAsCustomerRejected(AONContext ctx, Mod421 mod421, String reason) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod421DAO.markAsCustomerRejected(ctx, mod421, reason));		
	}

	@Override
	public void delete(AONContext ctx, Mod421 mod421) {
		ctx.getDslContext().transaction(
				configuration -> FiscalModelDAO.delete(ctx, mod421));
	}

	@Override
	public Mod421 initialize(AONContext ctx, Mod421 mod421) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod421DAO.initialize(ctx,mod421));
	}

	@Override
	public Mod421 create(AONContext ctx, Mod421 mod421) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod421DAO.create(ctx,mod421));
	}
	@Override
	public String getInfo(AONContext ctx, Mod421 mod421, IModelScript<Mod421Key> script, FiscalModelKeyInfo infoKey) {
		return Mod421InfoDAO.getInfo(ctx,mod421,script,infoKey);
	}
	
	@Override
	public Stream<VatContext> getInfo(CloseableAONContext ctx, Mod421 mod421, Mod421Key key, IDAOCallback callback) {
		return  Mod421InfoDAO.getModelInvoicesInfo(ctx, mod421, key)
			.onClose(() -> {
				if (callback != null) {
					callback.onFinish();
				}
			});
	}

	@Override
	public Mod421 doRecord(AONContext ctx, Mod421 mod421){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod421DAO.doRecord(ctx, mod421));		
	}
	@Override
	public Mod421 unrecord(AONContext ctx, Mod421 mod421){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod421DAO.unrecord(ctx, mod421));		
	}
}
