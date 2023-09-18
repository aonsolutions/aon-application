package com.esferalia.aon.occam.api.fiscal;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;

public interface IMODEL193 {

	public LinkedList<Mod193> getMod193s(AONContext ctx,int domain);
	public Mod193 get(AONContext ctx,Integer id);
	public Mod193 initialize(AONContext ctx, int year);
	public Mod193 save(AONContext ctx,Mod193 mod193);
	public void delete(AONContext ctx,Mod193 mod193);
	public Mod193 saveComments(AONContext ctx, Mod193 mod193);
	public Mod193 changeStatus(AONContext ctx, Mod193 mod193, FiscalStatus newStatus);
	public Mod193 duplicate(AONContext ctx, Mod193 mod193);
	public Mod193 aeatPresentation(AONContext ctx, Mod193 mod, String aeatResponse);
	
}
