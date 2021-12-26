package com.esferalia.aon.occam.api;

import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.fiscal.FiscalMatrixParams;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.IRPFParams;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod200;
import com.esferalia.aon.occam.api.model.fiscal.Mod347;
import com.esferalia.aon.occam.api.model.fiscal.Mod347Declared;
import com.esferalia.aon.occam.api.model.fiscal.Mod349;
import com.esferalia.aon.occam.api.model.fiscal.Mod349Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902015;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902018;
import com.esferalia.aon.occam.api.model.fiscal.OperationBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.OperationParams;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.fiscal.VatSummaryContext;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2017.Mod2002017;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2019.Mod2002019;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;

public interface IFiscal {
	// 			        IRPF
	public Stream<IrpfBreakdown> getIrpfBreakdownSummary(AONContext ctx, IRPFParams params);
	public Stream<IrpfBreakdown> getIrpfBreakdown(AONContext ctx, IRPFParams params);
	
	//					OPERATION (PANEL INGRESOS Y GASTOS)
	public Stream<OperationBreakdown> getOperationBreakdown(AONContext ctx, int domain, OperationParams params);
	
	// 			        VAT
	public Stream<VatSummaryContext> getVatSummaryContext(AONContext ctx, AccountingReportParams params);
	public Stream<VatContext> getVatContext(AONContext ctx, AccountingReportParams params);
	
	// 			        FISCAL PANEL
	public LinkedList<IFiscalModel> getFiscalPanel(AONContext ctx,int domain,FiscalMatrixParams params,int user);
	
	// 			   FISCAL MODEL
	public FiscalModel save(AONContext ctx, FiscalModel fm);
	public void delete(AONContext ctx, FiscalModel fm);
	public FiscalModel getModel(AONContext ctx, int id);
	
	// 				   		  	MOD390
	public Mod390 getMod390(AONContext ctx, int domain, Integer id);
	public LinkedList<Mod390> getMod390s(AONContext ctx, int domain);
	public Mod390 initialize(AONContext ctx, int year);
	public Mod390 create(AONContext ctx, Mod390 mod390);
	public Mod390 saveComments(AONContext ctx, Mod390 mod390);
	public void deleteMod390(AONContext ctx, Mod390 mod390);

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

	//		  					MOD200 
	public Mod200 getMod200(AONContext ctx, int domainId, Integer id);
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
	
	// 				   		  MOD200 - 2019
	public Mod2002019 createMod2002019(AONContext ctx, int year);
	public Mod2002019 initializeNewMod2002019(AONContext ctx, Mod2002019 mod200);
	public Mod2002019 initializeMod2002019(AONContext ctx, Mod2002019 mod200);
	public Mod2002019 getMod2002019ByYear(AONContext ctx, int year);
	public Mod2002019 getMod2002019ById(AONContext ctx, int id);
	public Mod2002019 calculateMod2002019(Mod2002019 mod200);
	public Mod2002019 validateMod2002019(Mod2002019 mod200);
	public Mod2002019 saveMod2002019(AONContext ctx, Mod2002019 mod200);
	public void deleteMod2002019(AONContext ctx, int id);
	public String dumpAEATMod2002019(Mod2002019 mod200);
	public Mod2002019 importMod2002018(AONContext ctx, Mod2002019 mod200);
	
	// 				   		  MOD200 - 2020
	public Mod2002020 createMod2002020(AONContext ctx, int year);
	public Mod2002020 initializeNewMod2002020(AONContext ctx, Mod2002020 mod200);
	public Mod2002020 initializeMod2002020(AONContext ctx, Mod2002020 mod200);
	public Mod2002020 getMod2002020ByYear(AONContext ctx, int year);
	public Mod2002020 getMod2002020ById(AONContext ctx, int id);
	public Mod2002020 calculateMod2002020(Mod2002020 mod200);
	public Mod2002020 validateMod2002020(Mod2002020 mod200);
	public Mod2002020 saveMod2002020(AONContext ctx, Mod2002020 mod200);
	public void deleteMod2002020(AONContext ctx, int id);
	public String dumpAEATMod2002020(Mod2002020 mod200);
	public Mod2002020 importMod2002019(AONContext ctx, Mod2002020 mod200);
	
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
	public Mod349 duplicateMod349(AONContext ctx,Mod349 mod349);

	// 						SII
	Stream<VatContext> getSiiVatContext(AONContext ctx, AccountingReportParams params, String sii);
	
	//		  MOD347
	public LinkedList<Mod347> getMod347s(AONContext ctx,int domain);
	public Mod347 getMod347(AONContext ctx,Integer id);
	public Mod347 initializeMod347(AONContext ctx);
	public Mod347 saveMod347(AONContext ctx,Mod347 mod347);
	public void deleteMod347(AONContext ctx,Mod347 mod347);
	public Mod347 saveCommentsMod347(AONContext ctx, Mod347 mod347);
	public Mod347 changeStatusMod347(AONContext ctx, Mod347 mod347, FiscalStatus newStatus);
	public String getMod347Info(AONContext ctx, Mod347 mod347, Mod347Declared declared, FiscalModelKeyInfo infoKey);
	public Mod347 duplicateMod347(AONContext ctx, Mod347 mod347);
	
	
	
}
