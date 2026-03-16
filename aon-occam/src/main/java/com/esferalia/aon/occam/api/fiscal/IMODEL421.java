package com.esferalia.aon.occam.api.fiscal;

import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.IDAOCallback;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod421;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod421Key;

public interface IMODEL421 {
	public Mod421 get(AONContext ctx, int id);
	public LinkedList<Mod421> getMod421s(AONContext ctx, int domain);
	public Mod421 calculate(AONContext ctx, Mod421 mod421);
	public Mod421 save(AONContext ctx, Mod421 mod421);
	public Mod421 saveComments(AONContext ctx, Mod421 mod421);
	public Mod421 initializeForFinish(AONContext ctx, Mod421 mod421);
	public Mod421 initialize(AONContext ctx, Mod421 mod421);
	public Mod421 create(AONContext ctx, Mod421 mod421);
	public void delete(AONContext ctx, Mod421 mod421);
	public String getInfo(AONContext ctx, Mod421 mod421, IModelScript<Mod421Key> script, FiscalModelKeyInfo infoKey);
	public Stream<VatContext> getInfo(CloseableAONContext ctx, Mod421 mod421, Mod421Key key,IDAOCallback daoCallback);
	public Mod421 markAsFinished(AONContext ctx, Mod421 mod421);
	public Mod421 markAsPending(AONContext ctx, Mod421 mod421);
	public Mod421 markAsSent(AONContext ctx, Mod421 mod421);
	public Mod421 markAsCustomerCheck(AONContext ctx, Mod421 mod421);
	public Mod421 markAsCustomerAccepted(AONContext ctx, Mod421 mod421);
	public Mod421 markAsCustomerRejected(AONContext ctx, Mod421 mod421, String reason);
	public Mod421 doRecord(AONContext ctx, Mod421 mod421);
	public Mod421 unrecord(AONContext ctx, Mod421 mod421);
	
}
