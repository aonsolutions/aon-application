package com.esferalia.aon.occam.impl.jooq.fiscal;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.fiscal.IMODEL390HF;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod390Key;
import com.esferalia.aon.occam.impl.jooq.dao.Mod390HFDAO;

public class MODEL390HFImpl implements IMODEL390HF {
	@Override
	public Mod390HF getMod390HF(AONContext ctx, int id) {
		return Mod390HFDAO.getMod390HF(ctx, id);
	}

	@Override
	public LinkedList<Mod390HF> getMod390HFs(AONContext ctx, int domain) {
		LinkedList<Mod390HF> list = new LinkedList<Mod390HF>();
		Mod390HFDAO.getMod390HFs(ctx, domain).forEach(list::add);
		return list;
	}

	@Override
	public Mod390HF calculateMod390HF(AONContext ctx, Mod390HF mod390HF) {
		return Mod390HFDAO.calculateMod390HF(ctx, mod390HF);
	}

	@Override
	public Mod390HF saveMod390HF(AONContext ctx, Mod390HF mod390HF) {
		return ctx.getDslContext().transactionResult(configuration -> Mod390HFDAO.saveMod390HF(ctx, mod390HF));
	}

	@Override
	public Mod390HF saveCommentsMod390HF(AONContext ctx, Mod390HF mod390HF) {
		return ctx.getDslContext().transactionResult(configuration -> Mod390HFDAO.saveCommentsMod390HF(ctx, mod390HF));
	}

	@Override
	public Mod390HF initializeForFinishMod390HF(AONContext ctx, Mod390HF mod390HF) {
		return Mod390HFDAO.initializeForFinish(ctx, mod390HF);
	}

	@Override
	public Mod390HF markAsFinishedMod390HF(AONContext ctx, Mod390HF mod390HF) {
		return ctx.getDslContext().transactionResult(configuration -> Mod390HFDAO.markAsFinished(ctx, mod390HF));
	}

	@Override
	public Mod390HF markAsPendingMod390HF(AONContext ctx, Mod390HF mod390HF) {
		return ctx.getDslContext().transactionResult(configuration -> Mod390HFDAO.markAsPending(ctx, mod390HF));
	}

	@Override
	public Mod390HF markAsSentMod390HF(AONContext ctx, Mod390HF mod390HF) {
		return ctx.getDslContext().transactionResult(configuration -> Mod390HFDAO.markAsSent(ctx, mod390HF));
	}

	@Override
	public Mod390HF markAsCustomerCheck(AONContext ctx, Mod390HF mod390HF) {
		return ctx.getDslContext().transactionResult(configuration -> Mod390HFDAO.markAsCustomerCheck(ctx, mod390HF));
	}

	@Override
	public void deleteMod390HF(AONContext ctx, Mod390HF mod390HF) {
		ctx.getDslContext().transaction(configuration -> Mod390HFDAO.delete(ctx, mod390HF));
	}

	@Override
	public Mod390HF initializeMod390HF(AONContext ctx, Mod390HF mod390HF) {
		return Mod390HFDAO.initializeMod390HF(ctx, mod390HF);
	}

	@Override
	public Mod390HF createMod390HF(AONContext ctx, Mod390HF mod390HF) {
		return Mod390HFDAO.createMod390HF(ctx, mod390HF);
	}

	@Override
	public Mod390HF declarationChanged(AONContext ctx, Mod390HF mod390HF) {
		return Mod390HFDAO.declarationChanged(ctx, mod390HF);
	}

	@Override
	public String getMod390HFInfo(AONContext ctx, Mod390HF mod390HF, IModelScript<Mod390Key> script,
			FiscalModelKeyInfo infoKey) {
		return Mod390HFDAO.getMod390HFInfo(ctx, mod390HF, script, infoKey);
	}

}
