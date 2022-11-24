package com.esferalia.aon.occam.api.fiscal;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod390Key;

public interface IMODEL390HF {

	public Mod390HF get(AONContext ctx, int id);
	public LinkedList<Mod390HF> getMod390HFs(AONContext ctx, int domain);
	public Mod390HF calculate(AONContext ctx, Mod390HF mod303);
	public Mod390HF save(AONContext ctx, Mod390HF mod303);
	public Mod390HF saveComments(AONContext ctx, Mod390HF mod303);
	public Mod390HF initializeForFinish(AONContext ctx, Mod390HF mod303);
	public Mod390HF initialize(AONContext ctx, Mod390HF mod303);
	public Mod390HF create(AONContext ctx, Mod390HF mod303);
	public void delete(AONContext ctx, Mod390HF mod303);
	public String getInfo(AONContext ctx, Mod390HF mod303, IModelScript<Mod390Key> script, FiscalModelKeyInfo infoKey);
	public Mod390HF reset(AONContext ctx, Mod390HF mod);
	public Mod390HF calculateProrrate(CloseableAONContext ctx, Mod390HF mod);
	
	public Mod390HF markAsFinished(AONContext ctx, Mod390HF mod303);
	public Mod390HF markAsPending(AONContext ctx, Mod390HF mod303);
	public Mod390HF markAsSent(AONContext ctx, Mod390HF mod303);
	public Mod390HF markAsCustomerCheck(AONContext ctx, Mod390HF mod);
	public Mod390HF markAsCustomerAccepted(AONContext ctx, Mod390HF mod);
	public Mod390HF markAsCustomerRejected(AONContext ctx, Mod390HF mod, String reason);
	
	
}
