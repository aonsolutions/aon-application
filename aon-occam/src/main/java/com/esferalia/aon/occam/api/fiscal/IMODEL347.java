package com.esferalia.aon.occam.api.fiscal;

import java.io.Writer;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod347;
import com.esferalia.aon.occam.api.model.fiscal.Mod347Declared;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;

public interface IMODEL347 {

	public LinkedList<Mod347> getMod347s(AONContext ctx,int domain);
	public Mod347 get(AONContext ctx,Integer id);
	public Mod347 initialize(AONContext ctx);
	public Mod347 reset(AONContext ctx, Mod347 mod347);
	public Mod347 save(AONContext ctx,Mod347 mod347);
	public void delete(AONContext ctx,Mod347 mod347);
	public Mod347 saveComments(AONContext ctx, Mod347 mod347);
	public Mod347 changeStatus(AONContext ctx, Mod347 mod347, FiscalStatus newStatus);
	public String getInfo(AONContext ctx, Mod347 mod347, Mod347Declared declared, FiscalModelKeyInfo infoKey);
	public Mod347 duplicate(AONContext ctx, Mod347 mod347);
	public void writeMailMergeReport(AONContext ctx, Mod347 mod347, Writer writer);
	public Mod347 aeatPresentation(AONContext ctx, Mod347 mod, String aeatResponse);
	
}
