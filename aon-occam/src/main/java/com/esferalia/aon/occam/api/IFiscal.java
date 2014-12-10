package com.esferalia.aon.occam.api;

import java.util.ArrayList;

import com.esferalia.aon.occam.api.model.Mod180;
import com.esferalia.aon.occam.api.model.Mod180Detail;
import com.esferalia.aon.occam.api.model.Mod190;
import com.esferalia.aon.occam.api.model.Mod190Detail;

public interface IFiscal {

	// 				   		  MOD180
	public ArrayList<Mod180> getMod180s(AONContext ctx,int domain);
	public Mod180 getMod180(AONContext ctx,Integer id);
	public Mod180 saveMod180(AONContext ctx,Mod180 mod180);
	public void deleteMod180(AONContext ctx,Mod180 mod180);
	public Mod180Detail getMod180Detail(AONContext ctx,Integer id);
	
	// 				   		  MOD190
	public ArrayList<Mod190> getMod190s(AONContext ctx,int domain);
	public Mod190 getMod190(AONContext ctx,Integer id);
	public Mod190 saveMod190(AONContext ctx,Mod190 mod190);
	public void deleteMod190(AONContext ctx,Mod190 mod190);
	public Mod190Detail getMod190Detail(AONContext ctx,Integer id);
	
}
