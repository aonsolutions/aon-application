package com.esferalia.aon.occam.api;

import java.util.ArrayList;

import com.esferalia.aon.occam.api.model.Mod180;
import com.esferalia.aon.occam.api.model.Mod180Detail;

public interface IFiscal {

	// 				   		  MOD180
	public ArrayList<Mod180> getMod180s(AONContext ctx,int domain);
	public Mod180 getMod180(AONContext ctx,Integer id);
	public Mod180 saveMod180(AONContext ctx,Mod180 mod180);
	public void deleteMod180(AONContext ctx,Mod180 mod180);
	public Mod180Detail getMod180Detail(AONContext ctx,Integer id);
	
	
}
