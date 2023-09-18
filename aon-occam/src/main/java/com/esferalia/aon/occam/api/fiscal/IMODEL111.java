package com.esferalia.aon.occam.api.fiscal;

import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.IDAOCallback;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod111Key;

public interface IMODEL111 {

	public Mod111 get(AONContext ctx, int id);
	public LinkedList<Mod111> getMod111s(AONContext ctx, int domain);
	public Mod111 calculate(AONContext ctx, Mod111 mod111);
	public Mod111 save(AONContext ctx, Mod111 mod111);
	public Mod111 saveComments(AONContext ctx, Mod111 mod111);
	public Mod111 initializeForFinish(AONContext ctx, Mod111 mod111);
	public Mod111 initialize(AONContext ctx, Mod111 mod111);
	public Mod111 create(AONContext ctx, Mod111 mod111);
	public void delete(AONContext ctx, Mod111 mod111);
	
	public String getInfo(AONContext ctx, Mod111 mod111, IModelScript<Mod111Key> script, FiscalModelKeyInfo infoKey);
	public Stream<IrpfBreakdown> getInfo(CloseableAONContext ctx, Mod111 mod111, Mod111Key key,IDAOCallback daoCallback);
	
	public Mod111 aeatPresentation(AONContext ctx, Mod111 mod111, String aeatResponse);
	public Mod111 markAsFinished(AONContext ctx, Mod111 mod111);
	public Mod111 markAsPending(AONContext ctx, Mod111 mod111);
	public Mod111 markAsSent(AONContext ctx, Mod111 mod111);
	public Mod111 markAsCustomerCheck(AONContext ctx, Mod111 mod111);
	public Mod111 markAsCustomerAccepted(AONContext ctx, Mod111 mod111);
	public Mod111 markAsCustomerRejected(AONContext ctx, Mod111 mod111, String reason);
	
	
}
