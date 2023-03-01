package com.esferalia.aon.occam.api.fiscal;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;

public interface IMODEL190 {

	public LinkedList<Mod190> getMod190s(AONContext ctx,int domain);
	public Mod190 get(AONContext ctx,Integer id);
	public Mod190 initialize(AONContext ctx, int year);
	public Mod190 save(AONContext ctx,Mod190 mod190);
	public void delete(AONContext ctx,Mod190 mod190);
	public Mod190Detail getDetail(AONContext ctx,Integer id);
	public Mod190 saveComments(AONContext ctx, Mod190 mod190);
	public Mod190 changeStatus(AONContext ctx, Mod190 mod190, FiscalStatus newStatus);
	public Mod190 duplicate(AONContext ctx, Mod190 mod190);
	public LinkedList<Mod190Detail> validateSalaries(AONContext ctx, Mod190 mod190);
	public Mod190 aeatPresentation(AONContext ctx, Mod190 mod, String aeatResponse);
	
}
