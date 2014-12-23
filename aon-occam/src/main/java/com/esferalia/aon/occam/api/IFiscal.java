package com.esferalia.aon.occam.api;

import java.util.ArrayList;

import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.Mod180Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.Mod390.Mod390Detail;

public interface IFiscal {

	// 				   		  MOD180
	public ArrayList<Mod180> getMod180s(AONContext ctx,int domain);
	public Mod180 getMod180(AONContext ctx,Integer id);
	public Mod180 initializeMod180(AONContext ctx, int year);
	public Mod180 saveMod180(AONContext ctx,Mod180 mod180);
	public void deleteMod180(AONContext ctx,Mod180 mod180);
	public Mod180Detail getMod180Detail(AONContext ctx,Integer id);
	
	// 				   		  MOD190
	public ArrayList<Mod190> getMod190s(AONContext ctx,int domain);
	public Mod190 getMod190(AONContext ctx,Integer id);
	public Mod190 initializeMod190(AONContext ctx, int year);
	public Mod190 saveMod190(AONContext ctx,Mod190 mod190);
	public void deleteMod190(AONContext ctx,Mod190 mod190);
	public Mod190Detail getMod190Detail(AONContext ctx,Integer id);

	// 				   		  MOD390
	public ArrayList<Mod390> getMod390s(AONContext ctx,int domain);
	public Mod390 getMod390(AONContext ctx,Integer id);
	public String getMod390XML(AONContext aonContext, int id);
	public Mod390 saveMod390(AONContext ctx,Mod390 mod390);
	public void deleteMod390(AONContext ctx,Mod390 mod390);
	public ArrayList<Mod390Detail> getMod390Details(AONContext aonContext,Mod390 mod390);
	public Mod390 initializeMod390(AONContext ctx, int year);
	
}
