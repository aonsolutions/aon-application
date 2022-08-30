package com.esferalia.aon.occam.mod200.api;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.mod200.api.model.mod200_2013.Mod2002013;
import com.esferalia.aon.occam.mod200.api.model.mod200_2014.Mod2002014;
import com.esferalia.aon.occam.mod200.api.model.mod200_2015.Mod2002015;
import com.esferalia.aon.occam.mod200.api.model.mod200_2016.Mod2002016;
import com.esferalia.aon.occam.mod200.api.model.mod200_2017.Mod2002017;
import com.esferalia.aon.occam.mod200.api.model.mod200_2018.Mod2002018;
import com.esferalia.aon.occam.mod200.api.model.mod200_2019.Mod2002019;

public interface IFiscal {

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
	
	
}
