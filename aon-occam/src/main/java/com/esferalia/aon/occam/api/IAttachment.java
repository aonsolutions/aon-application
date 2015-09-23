package com.esferalia.aon.occam.api;

import org.jooq.Condition;

import com.esferalia.aon.occam.api.model.attachment.Attach;


public interface IAttachment {
	public Attach getRattach(AONContext ctx, Condition condition);
	
}
