package com.esferalia.aon.occam.api.fiscal;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod390Key;

public interface IMODEL390HF {

	public Mod390HF getMod390HF(AONContext ctx, int id);
	public LinkedList<Mod390HF> getMod390HFs(AONContext ctx, int domain);
	public Mod390HF calculateMod390HF(AONContext ctx, Mod390HF mod303);
	public Mod390HF saveMod390HF(AONContext ctx, Mod390HF mod303);
	public Mod390HF saveCommentsMod390HF(AONContext ctx, Mod390HF mod303);
	public Mod390HF initializeForFinishMod390HF(AONContext ctx, Mod390HF mod303);
	public Mod390HF markAsFinishedMod390HF(AONContext ctx, Mod390HF mod303);
	public Mod390HF markAsPendingMod390HF(AONContext ctx, Mod390HF mod303);
	public Mod390HF markAsSentMod390HF(AONContext ctx, Mod390HF mod303);
	public Mod390HF markAsCustomerCheck(AONContext ctx, Mod390HF mod);
	public Mod390HF initializeMod390HF(AONContext ctx, Mod390HF mod303);
	public Mod390HF createMod390HF(AONContext ctx, Mod390HF mod303);
	public Mod390HF declarationChanged(AONContext ctx, Mod390HF mod303);
	public void deleteMod390HF(AONContext ctx, Mod390HF mod303);
	public String getMod390HFInfo(AONContext ctx, Mod390HF mod303, IModelScript<Mod390Key> script, FiscalModelKeyInfo infoKey);
	public Mod390HF resetMod390HF(AONContext ctx, Mod390HF mod);
	
}
