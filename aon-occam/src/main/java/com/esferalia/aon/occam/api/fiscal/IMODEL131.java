package com.esferalia.aon.occam.api.fiscal;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod131Activity;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod131Key;

public interface IMODEL131 {

	public Mod131 get(AONContext ctx, int id);
	public LinkedList<Mod131> getMod131s(AONContext ctx, int domain);
	public Mod131 calculate(AONContext ctx, Mod131 mod131);
	public Mod131Activity calculateActivity(AONContext ctx, Mod131 mod131, Mod131Activity activity);
	public Mod131 save(AONContext ctx, Mod131 mod131);
	public Mod131 saveComments(AONContext ctx, Mod131 mod131);
	public Mod131 initializeForFinish(AONContext ctx, Mod131 mod131);
	public Mod131 markAsFinished(AONContext ctx, Mod131 mod131);
	public Mod131 markAsSent(AONContext ctx, Mod131 mod131);
	public Mod131 markAsPending(AONContext ctx, Mod131 mod131);
	public Mod131 markAsCustomerCheck(AONContext ctx, Mod131 mod131);
	public Mod131 initialize(AONContext ctx, Mod131 mod131);
	public Mod131 create(AONContext ctx, Mod131 mod131);
	public void delete(AONContext ctx, Mod131 mod131);
	public String getInfo(AONContext ctx, Mod131 mod131, IModelScript<Mod131Key> script, FiscalModelKeyInfo infoKey);
	public Mod131 aeatPresentation(AONContext ctx, Mod131 mod131, String aeatResponse);
	
}
