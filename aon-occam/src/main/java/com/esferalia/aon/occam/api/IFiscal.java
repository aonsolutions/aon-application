package com.esferalia.aon.occam.api;

import java.util.ArrayList;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.FiscalActivity;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelMatrix;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.Mod180Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
import com.esferalia.aon.occam.api.model.fiscal.Mod193Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.Mod390.Mod390Detail;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014;
import com.esferalia.aon.occam.api.model.fiscal.modules.Modules2015.Epigraph;

public interface IFiscal {
	// 			        FISCAL PANEL
	public FiscalModelMatrix getFiscalPanel(AONContext ctx,int domain,int year,int user);
	public LinkedList<IFiscalModel> getAllModels(AONContext ctx,int domain,int user);
	public LinkedList<IFiscalModel> getAllModels(AONContext ctx,int domain,int year,int user);
	
	// 			   FISCAL ACTIVITIES
	public FiscalActivity calculate(AONContext ctx, FiscalActivity fa);
	public FiscalActivity save(AONContext ctx, FiscalActivity fa);
	public void delete(AONContext ctx, FiscalActivity fa);
	public FiscalActivity getActivity(AONContext ctx, int id);
	public ArrayList<FiscalActivity> getActivities(AONContext ctx, int domainId);
	public FiscalActivity getActivityFor(AONContext ctx,Epigraph epigraph, FiscalActivity fa);

	// 			   FISCAL MODEL
	public FiscalModel save(AONContext ctx, FiscalModel fm);
	public void delete(AONContext ctx, FiscalModel fm);
	public FiscalModel getModel(AONContext ctx, int id);
	public LinkedList<FiscalModel> getModels(AONContext ctx, int domainId);
	
	// 				   		  MOD180
	public LinkedList<Mod180> getMod180s(AONContext ctx,int domain);
	public Mod180 getMod180(AONContext ctx,Integer id);
	public Mod180 initializeMod180(AONContext ctx, int year);
	public Mod180 saveMod180(AONContext ctx,Mod180 mod180);
	public void deleteMod180(AONContext ctx,Mod180 mod180);
	public Mod180Detail getMod180Detail(AONContext ctx,Integer id);
	
	// 				   		  MOD190
	public LinkedList<Mod190> getMod190s(AONContext ctx,int domain);
	public Mod190 getMod190(AONContext ctx,Integer id);
	public Mod190 initializeMod190(AONContext ctx, int year);
	public Mod190 saveMod190(AONContext ctx,Mod190 mod190);
	public void deleteMod190(AONContext ctx,Mod190 mod190);
	public Mod190Detail getMod190Detail(AONContext ctx,Integer id);

	// 				   		  MOD193
	public ArrayList<Mod193> getMod193s(AONContext ctx,int domain);
	public Mod193 getMod193(AONContext ctx,Integer id);
	public Mod193 initializeMod193(AONContext ctx, int year);
	public Mod193 saveMod193(AONContext ctx,Mod193 mod193);
	public void deleteMod193(AONContext ctx,Mod193 mod193);
	public Mod193Detail getMod193Detail(AONContext ctx,Integer id);
	
	// 				   		  MOD184
	public ArrayList<Mod184> getMod184s(AONContext ctx,int domain);
	public Mod184 getMod184(AONContext ctx,Integer id);
	public Mod184 initializeMod184(AONContext ctx, int year);
	public Mod184 saveMod184(AONContext ctx,Mod184 mod184);
	public void deleteMod184(AONContext ctx,Mod184 mod184);

	// 				   		  MOD390
	public ArrayList<Mod390> getMod390s(AONContext ctx,int domain);
	public Mod390 getMod390(AONContext ctx,Integer id);
	public String getMod390XML(AONContext aonContext, int id);
	public Mod390 saveMod390(AONContext ctx,Mod390 mod390);
	public void deleteMod390(AONContext ctx,Mod390 mod390);
	public ArrayList<Mod390Detail> getMod390Details(AONContext aonContext,Mod390 mod390);
	public Mod390 initializeMod390(AONContext ctx, int year);

	// 				   		  MOD131
	public Mod131 getMod131(AONContext ctx, int id);
	public LinkedList<Mod131> getMod131s(AONContext ctx, int domain);
	public Mod131 calculateMod131(AONContext ctx, Mod131 mod131);
	public Mod131 saveMod131(AONContext ctx, Mod131 mod131);
	public void deleteMod131(AONContext ctx, Mod131 mod131);
	public Mod131 initializeMod131(AONContext ctx, Mod131 mod131);

	// 				   		  MOD202
	public Mod202 getMod202(AONContext ctx, int id);
	public LinkedList<Mod202> getMod202s(AONContext ctx, int domain);
	public Mod202 calculateMod202(AONContext ctx, Mod202 mod202);
	public Mod202 saveMod202(AONContext ctx, Mod202 mod202);
	public Mod202 initializeMod202(AONContext ctx, Mod202 mod202);
	public void deleteMod202(AONContext ctx, Mod202 mod202);

	// 				   		  MOD200 - 2013
	public Mod2002013 initializeNewMod2002013(AONContext ctx, Mod2002013 mod200);
	public Mod2002013 initializeMod2002013(AONContext ctx, Mod2002013 mod200);
	public Mod2002013 getMod2002013ByYear(AONContext ctx, int year);
	public Mod2002013 getMod2002013ById(AONContext ctx, int id);
	public Mod2002013 calculateMod2002013(Mod2002013 mod200);
	public Mod2002013 validateMod2002013(Mod2002013 mod200);
	public Mod2002013 saveMod2002013(AONContext ctx, Mod2002013 mod200);
	public void deleteMod2002013(AONContext ctx, int id);
	public String dumpAEATMod2002013(Mod2002013 mod200);

	// 				   		  MOD200 - 2014
	public Mod2002014 createMod2002014(AONContext ctx, int year);
	public Mod2002014 initializeNewMod2002014(AONContext ctx, Mod2002014 mod200);
	public Mod2002014 initializeMod2002014(AONContext ctx, Mod2002014 mod200);
	public Mod2002014 getMod2002014ByYear(AONContext ctx, int year);
	public Mod2002014 getMod2002014ById(AONContext ctx, int id);
	public Mod2002014 calculateMod2002014(Mod2002014 mod200);
	public Mod2002014 validateMod2002014(Mod2002014 mod200);
	public Mod2002014 saveMod2002014(AONContext ctx, Mod2002014 mod200);
	public void deleteMod2002014(AONContext ctx, int id);
	public String dumpAEATMod2002014(Mod2002014 mod200);
	public Mod2002014 importMod2002013(AONContext ctx, Mod2002014 mod200);
	

}
