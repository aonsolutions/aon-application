package com.esferalia.aon.occam.api.fiscal;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod349;
import com.esferalia.aon.occam.api.model.fiscal.Mod349Detail;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Period;

public interface IMODEL349 {

	public LinkedList<Mod349> getMod349s(AONContext ctx,int domain);
	public Mod349 get(AONContext ctx,Integer id);
	public Mod349 initialize(AONContext ctx, int year, Period period);
	public Mod349 save(AONContext ctx,Mod349 mod349);
	public Mod349 reset(AONContext ctx,Mod349 mod349);
	public void delete(AONContext ctx,Mod349 mod349);
	public Mod349Detail getDetail(AONContext ctx, Integer id);
	public Mod349 saveComments(AONContext ctx, Mod349 mod349);
	public Mod349 changeStatus(AONContext ctx, Mod349 mod349, FiscalStatus newStatus);
	public String getInfo(AONContext ctx, Mod349 mod349, Mod349Detail detail, FiscalModelKeyInfo infoKey);
	public Mod349 duplicate(AONContext ctx,Mod349 mod349);
	public Mod349 aeatPresentation(AONContext ctx, Mod349 mod, String aeatResponse);
	
}
