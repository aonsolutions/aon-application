package com.esferalia.aon.occam.api.fiscal;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;

public interface IMODEL303 {
	public Mod303 get(AONContext ctx, int id);
	public LinkedList<Mod303> getMod303s(AONContext ctx, int domain);
	public Mod303 calculate(AONContext ctx, Mod303 mod303);
	public Mod303 calculateProrrate(AONContext ctx, Mod303 mod303);
	public Mod303 save(AONContext ctx, Mod303 mod303);
	public Mod303 saveComments(AONContext ctx, Mod303 mod303);
	public Mod303 initializeForFinish(AONContext ctx, Mod303 mod303);
	public Mod303 initialize(AONContext ctx, Mod303 mod303);
	public Mod303 create(AONContext ctx, Mod303 mod303);
	public void delete(AONContext ctx, Mod303 mod303);
	public String getInfo(AONContext ctx, Mod303 mod303, IModelScript<Mod303Key> script, FiscalModelKeyInfo infoKey);
	public Mod303 aeatPresentation(AONContext ctx, Mod303 mod303, String aeatResponse);
	public Mod303 reset(AONContext ctx, Mod303 mod303);
	
	public Mod303 markAsFinished(AONContext ctx, Mod303 mod303);
	public Mod303 markAsPending(AONContext ctx, Mod303 mod303);
	public Mod303 markAsSent(AONContext ctx, Mod303 mod303);
	public Mod303 markAsCustomerCheck(AONContext ctx, Mod303 mod303);
	public Mod303 markAsCustomerAccepted(AONContext ctx, Mod303 mod303);
	public Mod303 markAsCustomerRejected(AONContext ctx, Mod303 mod303, String reason);
	
	public Mod303 doRecord(AONContext ctx, Mod303 mod303);
	public Mod303 unrecord(AONContext ctx, Mod303 mod303);
	
}
