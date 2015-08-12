package com.esferalia.aon.occam.api;

import com.esferalia.aon.occam.api.model.attachment.Rattach;


public interface IAttachment {
	public Rattach getRattach(AONContext ctx, Integer rattachId);
	
}
