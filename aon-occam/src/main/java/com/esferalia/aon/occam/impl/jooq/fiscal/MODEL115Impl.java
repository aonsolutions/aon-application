package com.esferalia.aon.occam.impl.jooq.fiscal;

import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.IDAOCallback;
import com.esferalia.aon.occam.api.fiscal.IMODEL115;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod115Key;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.FiscalModelDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod115.Mod115DAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod115.Mod115InfoDAO;

public class MODEL115Impl implements IMODEL115 {

	@Override
	public Mod115 get(AONContext ctx, int id) {
		return Mod115DAO.get(ctx, id);
	}
	@Override
	public LinkedList<Mod115> getMod115s(AONContext ctx, int domain) {
		LinkedList<Mod115> list = new LinkedList<>();
		Mod115DAO.getMod115s(ctx, domain)
			.forEach(list::add);
		return list;
	}
	@Override
	public Mod115 calculate(AONContext ctx, Mod115 mod115) {
		return Mod115DAO.calculate(mod115);
	}
	@Override
	public Mod115 save(AONContext ctx, Mod115 mod115) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod115DAO.save(ctx, mod115));		
	}
	@Override
	public Mod115 saveComments(AONContext ctx, Mod115 mod115) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod115DAO.saveComments(ctx, mod115));		
	}
	@Override
	public Mod115 initializeForFinish(AONContext ctx, Mod115 mod115){
		return Mod115DAO.initializeForFinish(ctx, mod115);
	}
	@Override
	public Mod115 markAsFinished(AONContext ctx, Mod115 mod115){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod115DAO.markAsFinished(ctx, mod115));		
	}
	@Override
	public Mod115 markAsSent(AONContext ctx, Mod115 mod115){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod115DAO.markAsSent(ctx, mod115));		
	}
	
	@Override
	public Mod115 markAsCustomerCheck(AONContext ctx, Mod115 mod115) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod115DAO.markAsCustomerCheck(ctx, mod115));		
	}

	@Override
	public Mod115 markAsCustomerAccepted(AONContext ctx, Mod115 mod115) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod115DAO.markAsCustomerAccepted(ctx, mod115));		
	}

	@Override
	public Mod115 markAsCustomerRejected(AONContext ctx, Mod115 mod115, String reason) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod115DAO.markAsCustomerRejected(ctx, mod115, reason));		
	}

	@Override
	public Mod115 markAsPending(AONContext ctx, Mod115 mod115){
		return ctx.getDslContext().transactionResult(
				configuration -> Mod115DAO.markAsPending(ctx, mod115));		
	}
	
	@Override
	public void delete(AONContext ctx, Mod115 mod115) {
		ctx.getDslContext().transaction(
				configuration -> FiscalModelDAO.delete(ctx, mod115));
	}

	@Override
	public Mod115 initialize(AONContext ctx, Mod115 mod115) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod115DAO.initialize(ctx,mod115));
	}

	@Override
	public Mod115 create(AONContext ctx, Mod115 mod115) {
		return ctx.getDslContext().transactionResult(
				configuration -> Mod115DAO.create(ctx,mod115));
	}
	
	@Override
	public Mod115 reset(AONContext ctx, Mod115 mod115) {
		return ctx.getDslContext().transactionResult(
			configuration -> Mod115DAO.reset(ctx,mod115));
	}
	
	@Override
	public String getInfo(AONContext ctx, Mod115 mod115, IModelScript<Mod115Key> script, FiscalModelKeyInfo infoKey) {
		return Mod115InfoDAO.getInfo(ctx,mod115,script,infoKey);
	}
	@Override
	public Stream<IrpfBreakdown> getInfo(CloseableAONContext ctx, Mod115 mod115, Mod115Key key, IDAOCallback callback) {
		return  Mod115InfoDAO.getModelInvoicesInfo(ctx, mod115, key)
			.onClose(() -> {
				if (callback != null) {
					callback.onFinish();
				}
			});
	}
	@Override
	public Mod115 aeatPresentationMod115(AONContext ctx, Mod115 mod115, String aeatResponse) {
		return ctx.getDslContext().transactionResult(
			configuration -> Mod115DAO.aeatPresentation(ctx, mod115, aeatResponse));
	}

}
