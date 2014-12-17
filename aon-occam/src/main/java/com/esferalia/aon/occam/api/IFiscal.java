package com.esferalia.aon.occam.api;

import java.util.ArrayList;

import com.esferalia.aon.occam.api.model.Mod180;
import com.esferalia.aon.occam.api.model.Mod180Detail;
import com.esferalia.aon.occam.api.model.Mod190;
import com.esferalia.aon.occam.api.model.Mod190Detail;
import com.esferalia.aon.occam.api.model.Mod390;
import com.esferalia.aon.occam.api.model.Mod390.Mod303Results;
import com.esferalia.aon.occam.api.model.Mod390.Mod390Detail;

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

	// 				   		  MOD390
	public ArrayList<Mod390> getMod390s(AONContext ctx,int domain);
	public Mod390 getMod390(AONContext ctx,Integer id);
	public String getMod390XML(AONContext aonContext, int id);
	public Mod390 saveMod390(AONContext ctx,Mod390 mod390);
	public void deleteMod390(AONContext ctx,Mod390 mod390);
	public ArrayList<Mod390Detail> getMod390Details(AONContext aonContext,Mod390 mod390);
	public Mod303Results getMod303Results(AONContext aonContext, int year);
	
}
