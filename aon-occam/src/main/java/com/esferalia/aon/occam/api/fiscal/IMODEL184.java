package com.esferalia.aon.occam.api.fiscal;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;

public interface IMODEL184 {

	public LinkedList<Mod184> getMod184s(AONContext ctx,int domain);
	public Mod184 get(AONContext ctx,Integer id);
	public Mod184 initialize(AONContext ctx, int year);
	public Mod184 save(AONContext ctx,Mod184 mod184);
	public void delete(AONContext ctx,Mod184 mod184);
	public Mod184 saveComments(AONContext ctx, Mod184 mod184);
	public Mod184 changeStatus(AONContext ctx, Mod184 mod184, FiscalStatus newStatus);
	public Mod184 duplicate(AONContext ctx, Mod184 mod184);
	public Mod184 aeatPresentation(AONContext ctx, Mod184 mod, String aeatResponse);
	
}
