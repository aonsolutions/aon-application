package com.esferalia.aon.occam.api.fiscal;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;

public interface IMODEL303 {
	public Mod303 getMod303(AONContext ctx, int id);
	public LinkedList<Mod303> getMod303s(AONContext ctx, int domain);
	public Mod303 calculateMod303(AONContext ctx, Mod303 mod303);
	public Mod303 saveMod303(AONContext ctx, Mod303 mod303);
	public Mod303 saveCommentsMod303(AONContext ctx, Mod303 mod303);
	public Mod303 initializeForFinishMod303(AONContext ctx, Mod303 mod303);
	public Mod303 markAsFinishedMod303(AONContext ctx, Mod303 mod303);
	public Mod303 markAsPendingMod303(AONContext ctx, Mod303 mod303);
	public Mod303 markAsSentMod303(AONContext ctx, Mod303 mod303);
	public Mod303 markAsCustomerCheckMod303(AONContext ctx, Mod303 mod303);
	public Mod303 initializeMod303(AONContext ctx, Mod303 mod303);
	public Mod303 createMod303(AONContext ctx, Mod303 mod303);
	public Mod303 declarationChanged(AONContext ctx, Mod303 mod303);
	public void deleteMod303(AONContext ctx, Mod303 mod303);
	public String getMod303Info(AONContext ctx, Mod303 mod303, IModelScript<Mod303Key> script, FiscalModelKeyInfo infoKey);
	public Mod303 aeatPresentationMod303(AONContext ctx, Mod303 mod303, String aeatResponse);
}
