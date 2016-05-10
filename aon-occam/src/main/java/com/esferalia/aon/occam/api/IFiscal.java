package com.esferalia.aon.occam.api;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelMatrix;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod131Activity;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.Mod180Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902014;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902015;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.occam.api.model.type.Mod115Key;
import com.esferalia.aon.occam.api.model.type.Mod123Key;
import com.esferalia.aon.occam.api.model.type.Mod130Key;
import com.esferalia.aon.occam.api.model.type.Mod131Key;

public interface IFiscal {
	// 			        FISCAL PANEL
	public FiscalModelMatrix getFiscalPanel(AONContext ctx,int domain,int year,int user);
	public LinkedList<IFiscalModel> getAllModels(AONContext ctx,int domain,int user);
	public LinkedList<IFiscalModel> getAllModels(AONContext ctx,int domain,int year,int user);
	
	// 			   FISCAL MODEL
	public FiscalModel save(AONContext ctx, FiscalModel fm);
	public void delete(AONContext ctx, FiscalModel fm);
	public FiscalModel getModel(AONContext ctx, int id);
	
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
	public LinkedList<Mod193> getMod193s(AONContext ctx,int domain);
	public Mod193 getMod193(AONContext ctx,Integer id);
	public Mod193 initializeMod193(AONContext ctx, int year);
	public Mod193 saveMod193(AONContext ctx,Mod193 mod193);
	public void deleteMod193(AONContext ctx,Mod193 mod193);
	
	// 				   		  MOD184
	public LinkedList<Mod184> getMod184s(AONContext ctx,int domain);
	public Mod184 getMod184(AONContext ctx,Integer id);
	public Mod184 initializeMod184(AONContext ctx, int year);
	public Mod184 saveMod184(AONContext ctx,Mod184 mod184);
	public void deleteMod184(AONContext ctx,Mod184 mod184);

	// 				   		  MOD390
	public LinkedList<Mod390> getMod390s(AONContext ctx, int domain);
	// 2014
	public Mod3902014 getMod3902014(AONContext ctx,Integer id);
	public String getMod3902014XML(AONContext aonContext, int id);
	public Mod3902014 saveMod3902014(AONContext ctx,Mod3902014 mod390);
	public void deleteMod3902014(AONContext ctx,Mod3902014 mod390);
	public Mod3902014 initializeMod3902014(AONContext ctx, int year);
	// 2015
	public Mod3902015 getMod3902015(AONContext ctx,Integer id);
	public String getMod3902015XML(AONContext aonContext, int id);
	public Mod3902015 saveMod3902015(AONContext ctx,Mod3902015 mod390);
	public void deleteMod3902015(AONContext ctx,Mod3902015 mod390);
	public Mod3902015 initializeMod3902015(AONContext ctx, int year);
	
	// 				   		  MOD111
	public Mod111 getMod111(AONContext ctx, int id);
	public LinkedList<Mod111> getMod111s(AONContext ctx, int domain);
	public Mod111 calculateMod111(AONContext ctx, Mod111 mod111);
	public Mod111 saveMod111(AONContext ctx, Mod111 mod111);
	public Mod111 saveCommentsMod111(AONContext ctx, Mod111 mod111);
	public Mod111 initializeForFinishMod111(AONContext ctx, Mod111 mod111);
	public Mod111 finishMod111(AONContext ctx, Mod111 mod111);
	public Mod111 reopenMod111(AONContext ctx, Mod111 mod111);
	public Mod111 initializeMod111(AONContext ctx, Mod111 mod111);
	public Mod111 createMod111(AONContext ctx, Mod111 mod111);
	public void deleteMod111(AONContext ctx, Mod111 mod111);
	public String getMod111Info(AONContext ctx, Mod111 mod111, IModelScript<Mod111Key> script, FiscalModelKeyInfo infoKey);
	
	// 				   		  MOD115
	public Mod115 getMod115(AONContext ctx, int id);
	public LinkedList<Mod115> getMod115s(AONContext ctx, int domain);
	public Mod115 calculateMod115(AONContext ctx, Mod115 mod115);
	public Mod115 saveMod115(AONContext ctx, Mod115 mod115);
	public Mod115 saveCommentsMod115(AONContext ctx, Mod115 mod115);
	public Mod115 initializeForFinishMod115(AONContext ctx, Mod115 mod115);
	public Mod115 finishMod115(AONContext ctx, Mod115 mod115);
	public Mod115 reopenMod115(AONContext ctx, Mod115 mod115);
	public Mod115 initializeMod115(AONContext ctx, Mod115 mod115);
	public Mod115 createMod115(AONContext ctx, Mod115 mod115);
	public void deleteMod115(AONContext ctx, Mod115 mod115);
	public String getMod115Info(AONContext ctx, Mod115 mod115, IModelScript<Mod115Key> script, FiscalModelKeyInfo infoKey);

	// 				   		  MOD123
	public Mod123 getMod123(AONContext ctx, int id);
	public LinkedList<Mod123> getMod123s(AONContext ctx, int domain);
	public Mod123 calculateMod123(AONContext ctx, Mod123 mod123);
	public Mod123 saveMod123(AONContext ctx, Mod123 mod123);
	public Mod123 saveCommentsMod123(AONContext ctx, Mod123 mod123);
	public Mod123 initializeForFinishMod123(AONContext ctx, Mod123 mod123);
	public Mod123 finishMod123(AONContext ctx, Mod123 mod123);
	public Mod123 reopenMod123(AONContext ctx, Mod123 mod123);
	public Mod123 initializeMod123(AONContext ctx, Mod123 mod123);
	public Mod123 createMod123(AONContext ctx, Mod123 mod123);
	public void deleteMod123(AONContext ctx, Mod123 mod123);
	public String getMod123Info(AONContext ctx, Mod123 mod123, IModelScript<Mod123Key> script, FiscalModelKeyInfo infoKey);

	// 				   		  MOD130
	public Mod130 getMod130(AONContext ctx, int id);
	public LinkedList<Mod130> getMod130s(AONContext ctx, int domain);
	public Mod130 calculateMod130(AONContext ctx, Mod130 mod130);
	public Mod130 saveMod130(AONContext ctx, Mod130 mod130);
	public Mod130 saveCommentsMod130(AONContext ctx, Mod130 mod130);
	public Mod130 initializeForFinishMod130(AONContext ctx, Mod130 mod130);
	public Mod130 finishMod130(AONContext ctx, Mod130 mod130);
	public Mod130 reopenMod130(AONContext ctx, Mod130 mod130);
	public Mod130 initializeMod130(AONContext ctx, Mod130 mod130);
	public Mod130 createMod130(AONContext ctx, Mod130 mod130);
	public void deleteMod130(AONContext ctx, Mod130 mod130);
	public String getMod130Info(AONContext ctx, Mod130 mod130, IModelScript<Mod130Key> script, FiscalModelKeyInfo infoKey);
	
	// 				   		  MOD131
	public Mod131 getMod131(AONContext ctx, int id);
	public LinkedList<Mod131> getMod131s(AONContext ctx, int domain);
	public Mod131 calculateMod131(AONContext ctx, Mod131 mod131);
	public Mod131Activity calculateMod131Activity(AONContext ctx, Mod131Activity activity);
	public Mod131 saveMod131(AONContext ctx, Mod131 mod131);
	public Mod131 saveCommentsMod131(AONContext ctx, Mod131 mod131);
	public Mod131 initializeForFinishMod131(AONContext ctx, Mod131 mod131);
	public Mod131 finishMod131(AONContext ctx, Mod131 mod131);
	public Mod131 reopenMod131(AONContext ctx, Mod131 mod131);
	public Mod131 initializeMod131(AONContext ctx, Mod131 mod131);
	public Mod131 createMod131(AONContext ctx, Mod131 mod131);
	public void deleteMod131(AONContext ctx, Mod131 mod131);
	public String getMod131Info(AONContext ctx, Mod131 mod131, IModelScript<Mod131Key> script, FiscalModelKeyInfo infoKey);

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
	
	// 				   		  MOD200 - 2015
	public Mod2002015 createMod2002015(AONContext ctx, int year);
	public Mod2002015 initializeNewMod2002015(AONContext ctx, Mod2002015 mod200);
	public Mod2002015 initializeMod2002015(AONContext ctx, Mod2002015 mod200);
	public Mod2002015 getMod2002015ByYear(AONContext ctx, int year);
	public Mod2002015 getMod2002015ById(AONContext ctx, int id);
	public Mod2002015 calculateMod2002015(Mod2002015 mod200);
	public Mod2002015 validateMod2002015(Mod2002015 mod200);
	public Mod2002015 saveMod2002015(AONContext ctx, Mod2002015 mod200);
	public void deleteMod2002015(AONContext ctx, int id);
	public String dumpAEATMod2002015(Mod2002015 mod200);
	public Mod2002015 importMod2002014(AONContext ctx, Mod2002015 mod200);

}
