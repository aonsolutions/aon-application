package com.esferalia.aon.occam.impl.jooq.fiscal;

import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.IDAOCallback;
import com.esferalia.aon.occam.api.fiscal.IMODEL390HF;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod390Key;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.FiscalModelDAO;
import com.esferalia.aon.occam.impl.jooq.dao.mod390HF.Mod390HFDAO;
import com.esferalia.aon.occam.impl.jooq.dao.mod390HF.Mod390HFInfoDAO;

public class MODEL390HFImpl implements IMODEL390HF {
	
	@Override
	public Mod390HF get(AONContext ctx, int id) {
		return Mod390HFDAO.get(ctx, id);
	}

	@Override
	public LinkedList<Mod390HF> getMod390HFs(AONContext ctx, int domain) {
		return Mod390HFDAO.getMod390HFs(ctx, domain)
			.collect(Collectors.toCollection(LinkedList::new));
	}

	@Override
	public Mod390HF calculate(AONContext ctx, Mod390HF mod390HF) {
		return Mod390HFDAO.calculate(mod390HF);
	}

	@Override
	public Mod390HF calculateProrrate(CloseableAONContext ctx, Mod390HF mod) {
		return Mod390HFDAO.calculateProrrate(mod);
	}

	@Override
	public Mod390HF save(AONContext ctx, Mod390HF mod390HF) {
		return ctx.getDslContext().transactionResult(conf -> Mod390HFDAO.save(ctx, mod390HF));
	}

	@Override
	public Mod390HF saveComments(AONContext ctx, Mod390HF mod390HF) {
		return ctx.getDslContext().transactionResult(conf -> Mod390HFDAO.saveComments(ctx, mod390HF));
	}

	@Override
	public Mod390HF initializeForFinish(AONContext ctx, Mod390HF mod390HF) {
		return ctx.getDslContext().transactionResult(conf -> Mod390HFDAO.initializeForFinish(ctx, mod390HF));
	}
	
	@Override
	public Mod390HF initialize(AONContext ctx, Mod390HF mod390HF) {
		return ctx.getDslContext().transactionResult(conf -> Mod390HFDAO.initialize(ctx, mod390HF));
	}
	
	@Override
	public Mod390HF create(AONContext ctx, Mod390HF mod390HF) {
		return ctx.getDslContext().transactionResult(conf -> Mod390HFDAO.create(ctx, mod390HF));
	}
	
	@Override
	public void delete(AONContext ctx, Mod390HF mod390HF) {
		ctx.getDslContext().transaction(conf -> FiscalModelDAO.delete(ctx, mod390HF));
	}

	@Override
	public String getInfo(AONContext ctx, Mod390HF mod390HF, IModelScript<Mod390Key> script, FiscalModelKeyInfo infoKey) {
		return Mod390HFInfoDAO.getInfo(ctx, mod390HF, script, infoKey);
	}

	@Override
	public Stream<VatContext> getInfo(CloseableAONContext ctx, Mod390HF mod, Mod390Key key, IDAOCallback callback) {
		return  Mod390HFInfoDAO.getModelInvoicesInfo(ctx, mod, key)
			.onClose(() -> {
				if (callback != null) {
					callback.onFinish();
				}
			});
		
	}

	@Override
	public Mod390HF reset(AONContext ctx, Mod390HF mod390HF) {
		return ctx.getDslContext().transactionResult(conf -> Mod390HFDAO.reset(ctx, mod390HF));
	}
	
	@Override
	public Mod390HF markAsFinished(AONContext ctx, Mod390HF mod390HF) {
		return ctx.getDslContext().transactionResult(configuration -> Mod390HFDAO.markAsFinished(ctx, mod390HF));
	}
	
	@Override
	public Mod390HF markAsPending(AONContext ctx, Mod390HF mod390HF) {
		return ctx.getDslContext().transactionResult(configuration -> Mod390HFDAO.markAsPending(ctx, mod390HF));
	}
	
	@Override
	public Mod390HF markAsSent(AONContext ctx, Mod390HF mod390HF) {
		return ctx.getDslContext().transactionResult(configuration -> Mod390HFDAO.markAsSent(ctx, mod390HF));
	}
	
	@Override
	public Mod390HF markAsCustomerCheck(AONContext ctx, Mod390HF mod390HF) {
		return ctx.getDslContext().transactionResult(configuration -> Mod390HFDAO.markAsCustomerCheck(ctx, mod390HF));
	}
	
	@Override
	public Mod390HF markAsCustomerAccepted(AONContext ctx, Mod390HF mod390HF) {
		return ctx.getDslContext().transactionResult(configuration -> Mod390HFDAO.markAsCustomerAccepted(ctx, mod390HF));
	}

	@Override
	public Mod390HF markAsCustomerRejected(AONContext ctx, Mod390HF mod390HF, String reason) {
		return ctx.getDslContext().transactionResult(configuration -> Mod390HFDAO.markAsCustomerRejected(ctx, mod390HF, reason));
	}


}
