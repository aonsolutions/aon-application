package com.esferalia.aon.occam.api.fiscal;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.Mod180Detail;

public interface IMODEL180 {

	public LinkedList<Mod180> getMod180s(AONContext ctx,int domain);
	public Mod180 get(AONContext ctx,Integer id);
	public Mod180 initialize(AONContext ctx, int year);
	public Mod180 save(AONContext ctx,Mod180 mod180);
	public void delete(AONContext ctx,Mod180 mod180);
	public Mod180Detail getDetail(AONContext ctx,Integer id);
	public Mod180 saveComments(AONContext ctx, Mod180 mod180);
	public Mod180 changeStatus(AONContext ctx, Mod180 mod180, FiscalStatus newStatus);
	public Mod180 duplicate(AONContext ctx, Mod180 mod180);
	public Mod180 aeatPresentation(AONContext ctx, Mod180 mod, String aeatResponse);
	
}
