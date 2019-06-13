package com.esferalia.aon.occam.api;

import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.fiscal.FiscalMatrixParams;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.IRPFParams;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
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
import com.esferalia.aon.occam.api.model.fiscal.Mod200;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.Mod347;
import com.esferalia.aon.occam.api.model.fiscal.Mod347Declared;
import com.esferalia.aon.occam.api.model.fiscal.Mod349;
import com.esferalia.aon.occam.api.model.fiscal.Mod349Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902014;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902015;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902018;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.fiscal.OperationBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.OperationParams;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.fiscal.VatParams;
import com.esferalia.aon.occam.api.model.fiscal.VatSummaryContext;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2017.Mod2002017;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.occam.api.model.type.Mod115Key;
import com.esferalia.aon.occam.api.model.type.Mod123Key;
import com.esferalia.aon.occam.api.model.type.Mod130Key;
import com.esferalia.aon.occam.api.model.type.Mod131Key;
import com.esferalia.aon.occam.api.model.type.Mod202Key;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.api.model.type.Mod390Key;

public interface IFiscal {
	// 			        IRPF
	public Stream<IrpfBreakdown> getIrpfBreakdownSummary(AONContext ctx, IRPFParams params);
	public Stream<IrpfBreakdown> getIrpfBreakdown(AONContext ctx, IRPFParams params);
	
	//					OPERATION (PANEL INGRESOS Y GASTOS)
	public Stream<OperationBreakdown> getOperationBreakdown(AONContext ctx, int domain, OperationParams params);
	
	// 			        VAT
	public Stream<VatSummaryContext> getVatSummaryContext(AONContext ctx, VatParams params);
	public Stream<VatContext> getVatContext(AONContext ctx, VatParams params);
	
	// 			        FISCAL PANEL
	public LinkedList<IFiscalModel> getFiscalPanel(AONContext ctx,int domain,FiscalMatrixParams params,int user);
	
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
	public Mod180 saveCommentsMod180(AONContext ctx, Mod180 mod180);
	public Mod180 changeStatusMod180(AONContext ctx, Mod180 mod180, FiscalStatus newStatus);
	public Mod180 duplicateNextYearMod180(AONContext ctx, Integer id);
	
	// 				   		  MOD190
	public LinkedList<Mod190> getMod190s(AONContext ctx,int domain);
	public Mod190 getMod190(AONContext ctx,Integer id);
	public Mod190 initializeMod190(AONContext ctx, int year);
	public Mod190 saveMod190(AONContext ctx,Mod190 mod190);
	public void deleteMod190(AONContext ctx,Mod190 mod190);
	public Mod190Detail getMod190Detail(AONContext ctx,Integer id);
	public Mod190 saveCommentsMod190(AONContext ctx, Mod190 mod190);
	public Mod190 changeStatusMod190(AONContext ctx, Mod190 mod190, FiscalStatus newStatus);
	public Mod190 duplicateNextYearMod190(AONContext ctx, Integer id);

	// 				   		  	MOD193
	public LinkedList<Mod193> getMod193s(AONContext ctx,int domain);
	public Mod193 getMod193(AONContext ctx,Integer id);
	public Mod193 initializeMod193(AONContext ctx, int year);
	public Mod193 saveMod193(AONContext ctx,Mod193 mod193);
	public void deleteMod193(AONContext ctx,Mod193 mod193);
	public Mod193 saveCommentsMod193(AONContext ctx, Mod193 mod193);
	public Mod193 changeStatusMod193(AONContext ctx, Mod193 mod193, FiscalStatus newStatus);
	public Mod193 duplicateNextYearMod193(AONContext ctx, Integer id);
	
	// 				   		  MOD184
	public LinkedList<Mod184> getMod184s(AONContext ctx,int domain);
	public Mod184 getMod184(AONContext ctx,Integer id);
	public Mod184 initializeMod184(AONContext ctx, int year);
	public Mod184 saveMod184(AONContext ctx,Mod184 mod184);
	public void deleteMod184(AONContext ctx,Mod184 mod184);
	public Mod184 saveCommentsMod184(AONContext ctx, Mod184 mod184);
	public Mod184 changeStatusMod184(AONContext ctx, Mod184 mod184, FiscalStatus newStatus);
	public Mod184 duplicateNextYearMod184(AONContext ctx, Integer id);

	// 				   		  	MOD390
	public LinkedList<Mod390> getMod390s(AONContext ctx, int domain);
	public Mod390 initialize(AONContext ctx, int year);
	public Mod390 create(AONContext ctx, Mod390 mod390);
	public Mod390 saveComments(AONContext ctx, Mod390 mod390);

	// 							MOD390 -- 2014
	public Mod3902014 getMod3902014(AONContext ctx,Mod390 mod390);
	public Mod3902014 getMod3902014(AONContext ctx,Integer id);
	public String getMod3902014XML(AONContext aonContext, int id);
	public Mod3902014 saveMod3902014(AONContext ctx,Mod3902014 mod390);
	public void deleteMod3902014(AONContext ctx,Mod3902014 mod390);

	// 							MOD390 -- 2015
	public Mod3902015 getMod3902015(AONContext ctx,Mod390 mod390);
	public Mod3902015 getMod3902015(AONContext ctx,Integer id);
	public String getMod3902015XML(AONContext aonContext, int id);
	public Mod3902015 saveMod3902015(AONContext ctx, Mod3902015 mod390);
	public void deleteMod3902015(AONContext ctx,Mod3902015 mod390);
	public Mod3902015 changeStatusMod3902015(AONContext ctx, Mod3902015 mod184, FiscalStatus newStatus);
	
	// 							MOD390 -- 2018
	public Mod3902018 getMod3902018(AONContext ctx,Mod390 mod390);
	public Mod3902018 getMod3902018(AONContext ctx,Integer id);
	public String getMod3902018XML(AONContext aonContext, int id);
	public Mod3902018 saveMod3902018(AONContext ctx, Mod3902018 mod390);
	public void deleteMod3902018(AONContext ctx,Mod3902018 mod390);
	public Mod3902018 changeStatusMod3902018(AONContext ctx, Mod3902018 mod184, FiscalStatus newStatus);

	// 				   		  MOD303
	public Mod303 getMod303(AONContext ctx, int id);
	public LinkedList<Mod303> getMod303s(AONContext ctx, int domain);
	public Mod303 calculateMod303(AONContext ctx, Mod303 mod303);
	public Mod303 saveMod303(AONContext ctx, Mod303 mod303);
	public Mod303 saveCommentsMod303(AONContext ctx, Mod303 mod303);
	public Mod303 initializeForFinishMod303(AONContext ctx, Mod303 mod303);
	public Mod303 markAsFinishedMod303(AONContext ctx, Mod303 mod303);
	public Mod303 markAsPendingMod303(AONContext ctx, Mod303 mod303);
	public Mod303 markAsSentMod303(AONContext ctx, Mod303 mod303);
	public Mod303 initializeMod303(AONContext ctx, Mod303 mod303);
	public Mod303 createMod303(AONContext ctx, Mod303 mod303);
	public Mod303 declarationChanged(AONContext ctx, Mod303 mod303);
	public void deleteMod303(AONContext ctx, Mod303 mod303);
	public String getMod303Info(AONContext ctx, Mod303 mod303, IModelScript<Mod303Key> script, FiscalModelKeyInfo infoKey);
	public void importMod303(AONContext ctx, int domain);

	// 				   		  MOD390HF
	public Mod390HF getMod390HF(AONContext ctx, int id);
	public LinkedList<Mod390HF> getMod390HFs(AONContext ctx, int domain);
	public Mod390HF calculateMod390HF(AONContext ctx, Mod390HF mod303);
	public Mod390HF saveMod390HF(AONContext ctx, Mod390HF mod303);
	public Mod390HF saveCommentsMod390HF(AONContext ctx, Mod390HF mod303);
	public Mod390HF initializeForFinishMod390HF(AONContext ctx, Mod390HF mod303);
	public Mod390HF markAsFinishedMod390HF(AONContext ctx, Mod390HF mod303);
	public Mod390HF markAsPendingMod390HF(AONContext ctx, Mod390HF mod303);
	public Mod390HF markAsSentMod390HF(AONContext ctx, Mod390HF mod303);
	public Mod390HF initializeMod390HF(AONContext ctx, Mod390HF mod303);
	public Mod390HF createMod390HF(AONContext ctx, Mod390HF mod303);
	public Mod390HF declarationChanged(AONContext ctx, Mod390HF mod303);
	public void deleteMod390HF(AONContext ctx, Mod390HF mod303);
	public String getMod390HFInfo(AONContext ctx, Mod390HF mod303, IModelScript<Mod390Key> script, FiscalModelKeyInfo infoKey);

	// 				   		  MOD111
	public Mod111 getMod111(AONContext ctx, int id);
	public LinkedList<Mod111> getMod111s(AONContext ctx, int domain);
	public Mod111 calculate(AONContext ctx, Mod111 mod111);
	public Mod111 save(AONContext ctx, Mod111 mod111);
	public Mod111 saveComments(AONContext ctx, Mod111 mod111);
	public Mod111 initializeForFinish(AONContext ctx, Mod111 mod111);
	public Mod111 markAsFinished(AONContext ctx, Mod111 mod111);
	public Mod111 markAsPending(AONContext ctx, Mod111 mod111);
	public Mod111 markAsSent(AONContext ctx, Mod111 mod111);
	public Mod111 initialize(AONContext ctx, Mod111 mod111);
	public Mod111 create(AONContext ctx, Mod111 mod111);
	public void delete(AONContext ctx, Mod111 mod111);
	public String getInfo(AONContext ctx, Mod111 mod111, IModelScript<Mod111Key> script, FiscalModelKeyInfo infoKey);
	
	// 				   		  MOD115
	public Mod115 getMod115(AONContext ctx, int id);
	public LinkedList<Mod115> getMod115s(AONContext ctx, int domain);
	public Mod115 calculate(AONContext ctx, Mod115 mod115);
	public Mod115 save(AONContext ctx, Mod115 mod115);
	public Mod115 saveComments(AONContext ctx, Mod115 mod115);
	public Mod115 initializeForFinish(AONContext ctx, Mod115 mod115);
	public Mod115 markAsFinished(AONContext ctx, Mod115 mod115);
	public Mod115 markAsPending(AONContext ctx, Mod115 mod115);
	public Mod115 markAsSent(AONContext ctx, Mod115 mod115);
	public Mod115 initialize(AONContext ctx, Mod115 mod115);
	public Mod115 create(AONContext ctx, Mod115 mod115);
	public void delete(AONContext ctx, Mod115 mod115);
	public String getInfo(AONContext ctx, Mod115 mod115, IModelScript<Mod115Key> script, FiscalModelKeyInfo infoKey);

	// 				   		  MOD123
	public Mod123 getMod123(AONContext ctx, int id);
	public LinkedList<Mod123> getMod123s(AONContext ctx, int domain);
	public Mod123 calculate(AONContext ctx, Mod123 mod123);
	public Mod123 save(AONContext ctx, Mod123 mod123);
	public Mod123 saveComments(AONContext ctx, Mod123 mod123);
	public Mod123 initializeForFinish(AONContext ctx, Mod123 mod123);
	public Mod123 markAsFinished(AONContext ctx, Mod123 mod123);
	public Mod123 markAsSent(AONContext ctx, Mod123 mod123);
	public Mod123 markAsPending(AONContext ctx, Mod123 mod123);
	public Mod123 initialize(AONContext ctx, Mod123 mod123);
	public Mod123 create(AONContext ctx, Mod123 mod123);
	public void delete(AONContext ctx, Mod123 mod123);
	public String getInfo(AONContext ctx, Mod123 mod123, IModelScript<Mod123Key> script, FiscalModelKeyInfo infoKey);

	// 				   		  MOD130
	public Mod130 getMod130(AONContext ctx, int id);
	public LinkedList<Mod130> getMod130s(AONContext ctx, int domain);
	public Mod130 calculate(AONContext ctx, Mod130 mod130);
	public Mod130 save(AONContext ctx, Mod130 mod130);
	public Mod130 saveComments(AONContext ctx, Mod130 mod130);
	public Mod130 initializeForFinish(AONContext ctx, Mod130 mod130);
	public Mod130 markAsFinished(AONContext ctx, Mod130 mod130);
	public Mod130 markAsSent(AONContext ctx, Mod130 mod130);
	public Mod130 markAsPending(AONContext ctx, Mod130 mod130);
	public Mod130 initialize(AONContext ctx, Mod130 mod130);
	public Mod130 create(AONContext ctx, Mod130 mod130);
	public void delete(AONContext ctx, Mod130 mod130);
	public String getInfo(AONContext ctx, Mod130 mod130, IModelScript<Mod130Key> script, FiscalModelKeyInfo infoKey);
	
	// 				   		  MOD131
	public Mod131 getMod131(AONContext ctx, int id);
	public LinkedList<Mod131> getMod131s(AONContext ctx, int domain);
	public Mod131 calculate(AONContext ctx, Mod131 mod131);
	public Mod131Activity calculateActivity(AONContext ctx, Mod131Activity activity);
	public Mod131 save(AONContext ctx, Mod131 mod131);
	public Mod131 saveComments(AONContext ctx, Mod131 mod131);
	public Mod131 initializeForFinish(AONContext ctx, Mod131 mod131);
	public Mod131 markAsFinished(AONContext ctx, Mod131 mod131);
	public Mod131 markAsSent(AONContext ctx, Mod131 mod131);
	public Mod131 markAsPending(AONContext ctx, Mod131 mod131);
	public Mod131 initialize(AONContext ctx, Mod131 mod131);
	public Mod131 create(AONContext ctx, Mod131 mod131);
	public void delete(AONContext ctx, Mod131 mod131);
	public String getInfo(AONContext ctx, Mod131 mod131, IModelScript<Mod131Key> script, FiscalModelKeyInfo infoKey);

	// 				   		  MOD202
	public Mod202 getMod202(AONContext ctx, int id);
	public LinkedList<Mod202> getMod202s(AONContext ctx, int domain);
	public Mod202 calculate(AONContext ctx, Mod202 mod202);
	public Mod202 save(AONContext ctx, Mod202 mod202);
	public Mod202 initialize(AONContext ctx, Mod202 mod202);
	public void delete(AONContext ctx, Mod202 mod202);
	public Mod202 saveComments(AONContext ctx, Mod202 mod202);
	public Mod202 initializeForFinish(AONContext ctx, Mod202 mod202);
	public Mod202 markAsFinished(AONContext ctx, Mod202 mod202);
	public Mod202 markAsSent(AONContext ctx, Mod202 mod202);
	public Mod202 markAsPending(AONContext ctx, Mod202 mod202);
	public Mod202 create(AONContext ctx, Mod202 mod202);
	public String getInfo(AONContext ctx, Mod202 mod202, IModelScript<Mod202Key> script, FiscalModelKeyInfo infoKey);
	
	//		  					MOD200 
	public LinkedList<Mod200> getMod200s(AONContext ctx, int domainId);
	// 				   		  MOD200 - 2013
	public Mod2002013 createMod2002013(AONContext ctx, int year);
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
	
	// 				   		  MOD200 - 2016
	public Mod2002016 createMod2002016(AONContext ctx, int year);
	public Mod2002016 initializeNewMod2002016(AONContext ctx, Mod2002016 mod200);
	public Mod2002016 initializeMod2002016(AONContext ctx, Mod2002016 mod200);
	public Mod2002016 getMod2002016ByYear(AONContext ctx, int year);
	public Mod2002016 getMod2002016ById(AONContext ctx, int id);
	public Mod2002016 calculateMod2002016(Mod2002016 mod200);
	public Mod2002016 validateMod2002016(Mod2002016 mod200);
	public Mod2002016 saveMod2002016(AONContext ctx, Mod2002016 mod200);
	public void deleteMod2002016(AONContext ctx, int id);
	public String dumpAEATMod2002016(Mod2002016 mod200);
	public Mod2002016 importMod2002015(AONContext ctx, Mod2002016 mod200);

	// 				   		  MOD200 - 2017
	public Mod2002017 createMod2002017(AONContext ctx, int year);
	public Mod2002017 initializeNewMod2002017(AONContext ctx, Mod2002017 mod200);
	public Mod2002017 initializeMod2002017(AONContext ctx, Mod2002017 mod200);
	public Mod2002017 getMod2002017ByYear(AONContext ctx, int year);
	public Mod2002017 getMod2002017ById(AONContext ctx, int id);
	public Mod2002017 calculateMod2002017(Mod2002017 mod200);
	public Mod2002017 validateMod2002017(Mod2002017 mod200);
	public Mod2002017 saveMod2002017(AONContext ctx, Mod2002017 mod200);
	public void deleteMod2002017(AONContext ctx, int id);
	public String dumpAEATMod2002017(Mod2002017 mod200);
	public Mod2002017 importMod2002016(AONContext ctx, Mod2002017 mod200);
	
	// 				   		  MOD200 - 2018
	public Mod2002018 createMod2002018(AONContext ctx, int year);
	public Mod2002018 initializeNewMod2002018(AONContext ctx, Mod2002018 mod200);
	public Mod2002018 initializeMod2002018(AONContext ctx, Mod2002018 mod200);
	public Mod2002018 getMod2002018ByYear(AONContext ctx, int year);
	public Mod2002018 getMod2002018ById(AONContext ctx, int id);
	public Mod2002018 calculateMod2002018(Mod2002018 mod200);
	public Mod2002018 validateMod2002018(Mod2002018 mod200);
	public Mod2002018 saveMod2002018(AONContext ctx, Mod2002018 mod200);
	public void deleteMod2002018(AONContext ctx, int id);
	public String dumpAEATMod2002018(Mod2002018 mod200);
	public Mod2002018 importMod2002017(AONContext ctx, Mod2002018 mod200);
	
	// 				   		  MOD349
	public LinkedList<Mod349> getMod349s(AONContext ctx,int domain);
	public Mod349 getMod349(AONContext ctx,Integer id);
	public Mod349 initializeMod349(AONContext ctx);
	public Mod349 saveMod349(AONContext ctx,Mod349 mod349);
	public void deleteMod349(AONContext ctx,Mod349 mod349);
	public Mod349Detail getMod349Detail(AONContext ctx, Integer id);
	public Mod349 saveCommentsMod349(AONContext ctx, Mod349 mod349);
	public Mod349 changeStatusMod349(AONContext ctx, Mod349 mod349, FiscalStatus newStatus);
	public String getMod349Info(AONContext ctx, Mod349 mod349, Mod349Detail detail, FiscalModelKeyInfo infoKey);

	// 						SII
	Stream<VatContext> getSiiVatContext(AONContext ctx, VatParams params, String sii);
	
	//		  MOD347
	public LinkedList<Mod347> getMod347s(AONContext ctx,int domain);
	public Mod347 getMod347(AONContext ctx,Integer id);
	public Mod347 initializeMod347(AONContext ctx);
	public Mod347 saveMod347(AONContext ctx,Mod347 mod347);
	public void deleteMod347(AONContext ctx,Mod347 mod347);
	public Mod347 saveCommentsMod347(AONContext ctx, Mod347 mod347);
	public Mod347 changeStatusMod347(AONContext ctx, Mod347 mod347, FiscalStatus newStatus);
	public String getMod347Info(AONContext ctx, Mod347 mod347, Mod347Declared declared, FiscalModelKeyInfo infoKey);
	public Mod347 duplicateNextYearMod347(AONContext ctx, Integer id);
	
	
}
