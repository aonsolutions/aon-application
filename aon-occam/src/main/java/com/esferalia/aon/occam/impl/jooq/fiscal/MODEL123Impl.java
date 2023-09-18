package com.esferalia.aon.occam.impl.jooq.fiscal;

import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.IDAOCallback;
import com.esferalia.aon.occam.api.fiscal.IMODEL123;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod123Key;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.FiscalModelDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod123.Mod123DAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod123.Mod123InfoDAO;

public class MODEL123Impl implements IMODEL123 {

	@Override
	public Mod123 get(AONContext ctx, int id) {
		return Mod123DAO.get(ctx, id);
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
		return Mod123DAO.calculate(mod123);
	}
	@Override
	public Mod123 save(AONContext ctx, Mod123 mod123) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod123DAO.save(ctx, mod123));		
	}
	@Override
	public Mod123 saveComments(AONContext ctx, Mod123 mod123) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod123DAO.saveComments(ctx, mod123));		
	}
	@Override
	public Mod123 initializeForFinish(AONContext ctx, Mod123 mod123){
		return Mod123DAO.initializeForFinish(ctx, mod123);
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
	public Mod123 markAsCustomerAccepted(AONContext ctx, Mod123 mod123) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod123DAO.markAsCustomerAccepted(ctx, mod123));		
	}
	@Override
	public Mod123 markAsCustomerRejected(AONContext ctx, Mod123 mod123, String reason) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod123DAO.markAsCustomerRejected(ctx, mod123, reason));		
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
		return ctx.getDslContext().transactionResult(
				configuration -> Mod123DAO.initialize(ctx,mod123));
	}

	@Override
	public Mod123 create(AONContext ctx, Mod123 mod123) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod123DAO.create(ctx,mod123));
	}
	@Override
	public String getInfo(AONContext ctx, Mod123 mod123, IModelScript<Mod123Key> script, FiscalModelKeyInfo infoKey) {
		return Mod123InfoDAO.getInfo(ctx,mod123,script,infoKey);
	}
	@Override
	public Stream<IrpfBreakdown> getInfo(CloseableAONContext ctx, Mod123 mod123, Mod123Key key, IDAOCallback callback) {
		return  Mod123InfoDAO.getModelInvoicesInfo(ctx, mod123, key)
			.onClose(() -> {
				if (callback != null) {
					callback.onFinish();
				}
			});
	}
	@Override
	public Mod123 aeatPresentation(AONContext ctx, Mod123 mod123, String aeatResponse) {
		return ctx.getDslContext().transactionResult(
			configuration -> Mod123DAO.aeatPresentation(ctx, mod123, aeatResponse));
	}

}
