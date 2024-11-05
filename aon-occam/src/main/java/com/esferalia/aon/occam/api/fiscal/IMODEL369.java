package com.esferalia.aon.occam.api.fiscal;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod369;
import com.esferalia.aon.occam.api.model.type.Period;

public interface IMODEL369 {

	public LinkedList<Mod369> getMod369s(AONContext ctx,int domain);
	public Mod369 get(AONContext ctx,Integer id);
	public Mod369 initialize(AONContext ctx, int year, Period period);
	public Mod369 save(AONContext ctx,Mod369 mod369);
	public void delete(AONContext ctx,Mod369 mod369);
	public Mod369 saveComments(AONContext ctx, Mod369 mod369);
	public Mod369 changeStatus(AONContext ctx, Mod369 mod369, FiscalStatus newStatus);
	public Mod369 duplicate(AONContext ctx, Mod369 mod369);
	
}
