package com.esferalia.aon.occam.api.fiscal;

import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.IDAOCallback;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod123Key;

public interface IMODEL123 {

	public Mod123 get(AONContext ctx, int id);
	public LinkedList<Mod123> getMod123s(AONContext ctx, int domain);
	public Mod123 calculate(AONContext ctx, Mod123 mod123);
	public Mod123 save(AONContext ctx, Mod123 mod123);
	public Mod123 saveComments(AONContext ctx, Mod123 mod123);
	public Mod123 initializeForFinish(AONContext ctx, Mod123 mod123);
	public Mod123 initialize(AONContext ctx, Mod123 mod123);
	public Mod123 create(AONContext ctx, Mod123 mod123);
	public void delete(AONContext ctx, Mod123 mod123);
	
	public String getInfo(AONContext ctx, Mod123 mod123, IModelScript<Mod123Key> script, FiscalModelKeyInfo infoKey);
	public Stream<IrpfBreakdown> getInfo(CloseableAONContext ctx, Mod123 mod123, Mod123Key key,IDAOCallback daoCallback);
	
	public Mod123 aeatPresentation(AONContext ctx, Mod123 mod123, String aeatResponse);
	public Mod123 markAsFinished(AONContext ctx, Mod123 mod123);
	public Mod123 markAsSent(AONContext ctx, Mod123 mod123);
	public Mod123 markAsPending(AONContext ctx, Mod123 mod123);
	public Mod123 markAsCustomerCheck(AONContext ctx, Mod123 mod123);
	public Mod123 markAsCustomerAccepted(AONContext ctx, Mod123 mod123);
	public Mod123 markAsCustomerRejected(AONContext ctx, Mod123 mod123, String reason);
}
