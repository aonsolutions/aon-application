package com.esferalia.aon.occam.api.fiscal;

import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.IDAOCallback;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod115Key;

public interface IMODEL115 {

	public Mod115 get(AONContext ctx, int id);
	public LinkedList<Mod115> getMod115s(AONContext ctx, int domain);
	public Mod115 calculate(AONContext ctx, Mod115 mod115);
	public Mod115 save(AONContext ctx, Mod115 mod115);
	public Mod115 saveComments(AONContext ctx, Mod115 mod115);
	public Mod115 initializeForFinish(AONContext ctx, Mod115 mod115);
	public Mod115 initialize(AONContext ctx, Mod115 mod115);
	public Mod115 create(AONContext ctx, Mod115 mod115);
	public void delete(AONContext ctx, Mod115 mod115);
	
	public String getInfo(AONContext ctx, Mod115 mod115, IModelScript<Mod115Key> script, FiscalModelKeyInfo infoKey);
	public Stream<IrpfBreakdown> getInfo(CloseableAONContext ctx, Mod115 mod115, Mod115Key key,IDAOCallback daoCallback);
	
	public Mod115 aeatPresentationMod115(AONContext ctx, Mod115 mod115, String aeatResponse);
	public Mod115 reset(AONContext ctx, Mod115 mod115);
	public Mod115 markAsFinished(AONContext ctx, Mod115 mod115);
	public Mod115 markAsPending(AONContext ctx, Mod115 mod115);
	public Mod115 markAsSent(AONContext ctx, Mod115 mod115);
	public Mod115 markAsCustomerCheck(AONContext ctx, Mod115 mod115);
	public Mod115 markAsCustomerAccepted(AONContext ctx, Mod115 mod115);
	public Mod115 markAsCustomerRejected(AONContext ctx, Mod115 mod115, String reason);
}
