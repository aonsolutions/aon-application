package com.esferalia.aon.occam.impl.jooq;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IAttachment;
import com.esferalia.aon.occam.api.model.attachment.Rattach;
import com.esferalia.aon.occam.impl.jooq.dao.AttachmentDAO;

public class AttachmentImpl implements IAttachment{

	@Override
	public Rattach getRattach(AONContext ctx, Integer rattachId) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> AttachmentDAO.getRattach(ctx, rattachId));
	}

	
}
