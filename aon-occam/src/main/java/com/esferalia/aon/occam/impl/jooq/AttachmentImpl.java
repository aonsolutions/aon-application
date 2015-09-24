package com.esferalia.aon.occam.impl.jooq;

import org.jooq.Condition;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IAttachment;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.impl.jooq.dao.AttachmentDAO;

public class AttachmentImpl implements IAttachment{

	@Override
	public Attach getRattach(AONContext ctx, Condition condition) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> AttachmentDAO.getRattach(ctx, condition));
	}

	@Override
	public void insertRattach(AONContext ctx, Attach attach) {
		ctx.getDslContext().transaction(configuration -> {
			AttachmentDAO.insertRattach(ctx, attach);
		} );			
	}

	@Override
	public void insertProjectAttach(AONContext ctx, Attach attach) {
		ctx.getDslContext().transaction(configuration -> {
			AttachmentDAO.insertProjectAttach(ctx, attach);
		} );
	}
}
