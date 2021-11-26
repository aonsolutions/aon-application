package com.esferalia.aon.occam.api.fiscal;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod202Key;

public interface IMODEL202 {

	public Mod202 getMod202(AONContext ctx, int id);
	public LinkedList<Mod202> getMod202s(AONContext ctx, int domain);
	public Mod202 calculate(AONContext ctx, Mod202 mod202);
	public Mod202 save(AONContext ctx, Mod202 mod202);
	public Mod202 initialize(AONContext ctx, Mod202 mod202);
	public void delete(AONContext ctx, Mod202 mod202);
	public Mod202 saveComments(AONContext ctx, Mod202 mod202);
	public Mod202 initializeForFinish(AONContext ctx, Mod202 mod202);
	public Mod202 markAsFinished(AONContext ctx, Mod202 mod202);
	public Mod202 markAsSent(AONContext ctx, Mod202 mod202);
	public Mod202 markAsPending(AONContext ctx, Mod202 mod202);
	public Mod202 markAsCustomerCheck(AONContext ctx, Mod202 mod202);
	public Mod202 create(AONContext ctx, Mod202 mod202);
	public String getInfo(AONContext ctx, Mod202 mod202, IModelScript<Mod202Key> script, FiscalModelKeyInfo infoKey);
	public Mod202 aeatPresentation(AONContext ctx, Mod202 mod202, String aeatResponse);
	
}
