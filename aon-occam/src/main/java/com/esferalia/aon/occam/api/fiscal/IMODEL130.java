package com.esferalia.aon.occam.api.fiscal;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod130Key;

public interface IMODEL130 {

	public Mod130 getMod130(AONContext ctx, int id);
	public LinkedList<Mod130> getMod130s(AONContext ctx, int domain);
	public Mod130 calculate(AONContext ctx, Mod130 mod130);
	public Mod130 save(AONContext ctx, Mod130 mod130);
	public Mod130 saveComments(AONContext ctx, Mod130 mod130);
	public Mod130 initializeForFinish(AONContext ctx, Mod130 mod130);
	public Mod130 markAsFinished(AONContext ctx, Mod130 mod130);
	public Mod130 markAsSent(AONContext ctx, Mod130 mod130);
	public Mod130 markAsPending(AONContext ctx, Mod130 mod130);
	public Mod130 markAsCustomerCheck(AONContext ctx, Mod130 mod130);
	public Mod130 initialize(AONContext ctx, Mod130 mod130);
	public Mod130 create(AONContext ctx, Mod130 mod130);
	public void delete(AONContext ctx, Mod130 mod130);
	public String getInfo(AONContext ctx, Mod130 mod130, IModelScript<Mod130Key> script, FiscalModelKeyInfo infoKey);
	
}
