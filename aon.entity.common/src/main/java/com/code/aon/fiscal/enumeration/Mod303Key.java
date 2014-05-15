package com.code.aon.fiscal.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;
import com.code.aon.config.enumeration.Administration;

public enum Mod303Key implements IFiscalModelKey, IResourceable, IStringEnum  {
	//          value    ,ejrc ,desc ,title,visib ,total ,l,admn,period
	H1       ("303-H1"   ,2014 ,false,true ,true  ,false ,0,null,null,null),
	CAG1     ("303-AG1"  ,2014 ,true ,true ,true  ,true  ,1,null,null,null),
		CAG1_V1  ("303-AG1V1",2014 ,false,false,false ,false ,1,null,null,CAG1),
		CAG1_V2  ("303-AG1V2",2014 ,false,false,false ,false ,1,null,null,CAG1),	
		CAG1_V3  ("303-AG1V3",2014 ,false,false,false ,false ,1,null,null,CAG1),	
		CAG1_V4  ("303-AG1V4",2014 ,false,false,false ,false ,1,null,new Period[]{Period.T1,Period.T2,Period.T3},CAG1),
		CAG1_V5  ("303-AG1V5",2014 ,false,false,false ,false ,2,null,new Period[]{Period.T1,Period.T2,Period.T3},CAG1),
		CAG1_V6  ("303-AG1V6",2014 ,false,false,false ,false ,1,null,new Period[]{Period.T4},CAG1),
		CAG1_V7  ("303-AG1V7",2014 ,false,false,false ,false ,2,null,new Period[]{Period.T4},CAG1),	
	CAG2     ("303-AG2"  ,2014 ,true ,true ,true  ,true  ,1,null,null,null),
		CAG2_V1  ("303-AG2V1",2014 ,false,false,false ,false ,1,null,null,CAG2),
		CAG2_V2  ("303-AG2V2",2014 ,false,false,false ,false ,1,null,null,CAG2),	
		CAG2_V3  ("303-AG2V3",2014 ,false,false,false ,false ,1,null,null,CAG2),	
		CAG2_V4  ("303-AG2V4",2014 ,false,false,false ,false ,1,null,new Period[]{Period.T1,Period.T2,Period.T3},CAG2),
		CAG2_V5  ("303-AG2V5",2014 ,false,false,false ,false ,2,null,new Period[]{Period.T1,Period.T2,Period.T3},CAG2),
		CAG2_V6  ("303-AG2V6",2014 ,false,false,false ,false ,1,null,new Period[]{Period.T4},CAG2),
		CAG2_V7  ("303-AG2V7",2014 ,false,false,false ,false ,2,null,new Period[]{Period.T4},CAG2),	
	CAG3     ("303-AG3"  ,2014 ,true ,true ,true  ,true  ,1,null,null,null),
		CAG3_V1  ("303-AG3V1",2014 ,false,false,false ,false ,1,null,null,CAG3),
		CAG3_V2  ("303-AG3V2",2014 ,false,false,false ,false ,1,null,null,CAG3),	
		CAG3_V3  ("303-AG3V3",2014 ,false,false,false ,false ,1,null,null,CAG3),	
		CAG3_V4  ("303-AG3V4",2014 ,false,false,false ,false ,1,null,new Period[]{Period.T1,Period.T2,Period.T3},CAG3),
		CAG3_V5  ("303-AG3V5",2014 ,false,false,false ,false ,2,null,new Period[]{Period.T1,Period.T2,Period.T3},CAG3),
		CAG3_V6  ("303-AG3V6",2014 ,false,false,false ,false ,1,null,new Period[]{Period.T4},CAG3),
		CAG3_V7  ("303-AG3V7",2014 ,false,false,false ,false ,2,null,new Period[]{Period.T4},CAG3),	
	CAG4     ("303-AG4"  ,2014 ,true ,true ,true  ,true  ,1,null,null,null),
		CAG4_V1  ("303-AG4V1",2014 ,false,false,false ,false ,1,null,null,CAG4),
		CAG4_V2  ("303-AG4V2",2014 ,false,false,false ,false ,1,null,null,CAG4),	
		CAG4_V3  ("303-AG4V3",2014 ,false,false,false ,false ,1,null,null,CAG4),	
		CAG4_V4  ("303-AG4V4",2014 ,false,false,false ,false ,1,null,new Period[]{Period.T1,Period.T2,Period.T3},CAG4),
		CAG4_V5  ("303-AG4V5",2014 ,false,false,false ,false ,2,null,new Period[]{Period.T1,Period.T2,Period.T3},CAG4),
		CAG4_V6  ("303-AG4V6",2014 ,false,false,false ,false ,1,null,new Period[]{Period.T4},CAG4),
		CAG4_V7  ("303-AG4V7",2014 ,false,false,false ,false ,2,null,new Period[]{Period.T4},CAG4),
	H2       ("303-H2"   ,2014 ,false,true ,true  ,false ,0,null,null,null),
	CAC1     ("303-AC1"  ,2014 ,true ,true ,true  ,false ,1,null,null,null),
		CAC1_M1U ("303-AC1M1U" ,2014 ,true ,false,false ,true  ,1,null,null,CAC1),
		CAC1_M1I ("303-AC1M1I" ,2014 ,true ,false,false ,true  ,1,null,null,CAC1),
		CAC1_M2U ("303-AC1M2U" ,2014 ,true ,false,false ,true  ,1,null,null,CAC1),
		CAC1_M2I ("303-AC1M2I" ,2014 ,true ,false,false ,true  ,1,null,null,CAC1),
		CAC1_M3U ("303-AC1M3U" ,2014 ,true ,false,false ,true  ,1,null,null,CAC1),
		CAC1_M3I ("303-AC1M3I" ,2014 ,true ,false,false ,true  ,1,null,null,CAC1),
		CAC1_M4U ("303-AC1M4U" ,2014 ,true ,false,false ,true  ,1,null,null,CAC1),
		CAC1_M4I ("303-AC1M4I" ,2014 ,true ,false,false ,true  ,1,null,null,CAC1),
		CAC1_M5U ("303-AC1M5U" ,2014 ,true ,false,false ,true  ,1,null,null,CAC1),
		CAC1_M5I ("303-AC1M5I" ,2014 ,true ,false,false ,true  ,1,null,null,CAC1),
		CAC1_M6U ("303-AC1M6U" ,2014 ,true ,false,false ,true  ,1,null,null,CAC1),
		CAC1_M6I ("303-AC1M6I" ,2014 ,true ,false,false ,true  ,1,null,null,CAC1),
		CAC1_M7U ("303-AC1M7U" ,2014 ,true ,false,false ,true  ,1,null,null,CAC1),
		CAC1_M7I ("303-AC1M7I" ,2014 ,true ,false,false ,true  ,1,null,null,CAC1),
		CAC1_C   ("303-AC1C"   ,2014 ,false,false,false ,true  ,1,null,new Period[]{Period.T1,Period.T2,Period.T3},CAC1),
		CAC1_D   ("303-AC1D"   ,2014 ,false,false,false ,true  ,1,null,new Period[]{Period.T1,Period.T2,Period.T3},CAC1),
		CAC1_Z   ("303-AC1Z"   ,2014 ,false,false,false ,true  ,1,null,new Period[]{Period.T1,Period.T2,Period.T3},CAC1),
		CAC1_ZA  ("303-AC1ZA"  ,2014 ,false,false,false ,true  ,1,null,new Period[]{Period.T1,Period.T2,Period.T3},CAC1),
		CAC1_ZD  ("303-AC1ZD"  ,2014 ,false,false,false ,true  ,1,null,new Period[]{Period.T1,Period.T2,Period.T3},CAC1),
		CAC1_E   ("303-AC1E"   ,2014 ,false,false,false ,true  ,1,null,new Period[]{Period.T1,Period.T2,Period.T3},CAC1),
		CAC1_F   ("303-AC1F"   ,2014 ,false,false,false ,false ,2,null,new Period[]{Period.T1,Period.T2,Period.T3},CAC1),
		CAC1_G   ("303-AC1G"   ,2014 ,false,false,false ,true  ,1,null,new Period[]{Period.T4},CAC1),
		CAC1_H   ("303-AC1H"   ,2014 ,false,false,false ,true  ,1,null,new Period[]{Period.T4},CAC1),
		CAC1_I   ("303-AC1I"   ,2014 ,false,false,false ,true  ,1,null,new Period[]{Period.T4},CAC1),
		CAC1_J   ("303-AC1J"   ,2014 ,false,false,false ,true  ,1,null,new Period[]{Period.T4},CAC1),
		CAC1_K   ("303-AC1K"   ,2014 ,false,false,false ,true  ,1,null,new Period[]{Period.T4},CAC1),
		CAC1_L   ("303-AC1L"   ,2014 ,false,false,false ,true  ,1,null,new Period[]{Period.T4},CAC1),
		CAC1_M   ("303-AC1M"   ,2014 ,false,false,false ,false ,2,null,new Period[]{Period.T4},CAC1),
	CAC2     ("303-AC2"  ,2014 ,true ,true ,true  ,false ,1,null,null,null),
		CAC2_M1U ("303-AC2M1U" ,2014 ,true ,false,false ,true  ,1,null,null,CAC2),
		CAC2_M1I ("303-AC2M1I" ,2014 ,true ,false,false ,true  ,1,null,null,CAC2),
		CAC2_M2U ("303-AC2M2U" ,2014 ,true ,false,false ,true  ,1,null,null,CAC2),
		CAC2_M2I ("303-AC2M2I" ,2014 ,true ,false,false ,true  ,1,null,null,CAC2),
		CAC2_M3U ("303-AC2M3U" ,2014 ,true ,false,false ,true  ,1,null,null,CAC2),
		CAC2_M3I ("303-AC2M3I" ,2014 ,true ,false,false ,true  ,1,null,null,CAC2),
		CAC2_M4U ("303-AC2M4U" ,2014 ,true ,false,false ,true  ,1,null,null,CAC2),
		CAC2_M4I ("303-AC2M4I" ,2014 ,true ,false,false ,true  ,1,null,null,CAC2),
		CAC2_M5U ("303-AC2M5U" ,2014 ,true ,false,false ,true  ,1,null,null,CAC2),
		CAC2_M5I ("303-AC2M5I" ,2014 ,true ,false,false ,true  ,1,null,null,CAC2),
		CAC2_M6U ("303-AC2M6U" ,2014 ,true ,false,false ,true  ,1,null,null,CAC2),
		CAC2_M6I ("303-AC2M6I" ,2014 ,true ,false,false ,true  ,1,null,null,CAC2),
		CAC2_M7U ("303-AC2M7U" ,2014 ,true ,false,false ,true  ,1,null,null,CAC2),
		CAC2_M7I ("303-AC2M7I" ,2014 ,true ,false,false ,true  ,1,null,null,CAC2),
		CAC2_C   ("303-AC2C"   ,2014 ,false,false,false ,true  ,1,null,new Period[]{Period.T1,Period.T2,Period.T3},CAC2),
		CAC2_D   ("303-AC2D"   ,2014 ,false,false,false ,true  ,1,null,new Period[]{Period.T1,Period.T2,Period.T3},CAC2),
		CAC2_Z   ("303-AC2Z"   ,2014 ,false,false,false ,true  ,1,null,new Period[]{Period.T1,Period.T2,Period.T3},CAC2),
		CAC2_ZA  ("303-AC2ZA"  ,2014 ,false,false,false ,true  ,1,null,new Period[]{Period.T1,Period.T2,Period.T3},CAC1),
		CAC2_ZD  ("303-AC2ZD"  ,2014 ,false,false,false ,true  ,1,null,new Period[]{Period.T1,Period.T2,Period.T3},CAC1),
		CAC2_E   ("303-AC2E"   ,2014 ,false,false,false ,true  ,1,null,new Period[]{Period.T1,Period.T2,Period.T3},CAC2),
		CAC2_F   ("303-AC2F"   ,2014 ,false,false,false ,false ,2,null,new Period[]{Period.T1,Period.T2,Period.T3},CAC2),
		CAC2_G   ("303-AC2G"   ,2014 ,false,false,false ,true  ,1,null,new Period[]{Period.T4},CAC2),
		CAC2_H   ("303-AC2H"   ,2014 ,false,false,false ,true  ,1,null,new Period[]{Period.T4},CAC2),
		CAC2_I   ("303-AC2I"   ,2014 ,false,false,false ,true  ,1,null,new Period[]{Period.T4},CAC2),
		CAC2_J   ("303-AC2J"   ,2014 ,false,false,false ,true  ,1,null,new Period[]{Period.T4},CAC2),
		CAC2_K   ("303-AC2K"   ,2014 ,false,false,false ,true  ,1,null,new Period[]{Period.T4},CAC2),
		CAC2_L   ("303-AC2L"   ,2014 ,false,false,false ,true  ,1,null,new Period[]{Period.T4},CAC2),
		CAC2_M   ("303-AC2M"   ,2014 ,false,false,false ,false ,2,null,new Period[]{Period.T4},CAC2),
	CAC3     ("303-AC3"  ,2014 ,true ,true ,true  ,false ,1,null,null,null),
		CAC3_M1U ("303-AC3M1U" ,2014 ,true ,false,false ,true  ,1,null,null,CAC3),
		CAC3_M1I ("303-AC3M1I" ,2014 ,true ,false,false ,true  ,1,null,null,CAC3),
		CAC3_M2U ("303-AC3M2U" ,2014 ,true ,false,false ,true  ,1,null,null,CAC3),
		CAC3_M2I ("303-AC3M2I" ,2014 ,true ,false,false ,true  ,1,null,null,CAC3),
		CAC3_M3U ("303-AC3M3U" ,2014 ,true ,false,false ,true  ,1,null,null,CAC3),
		CAC3_M3I ("303-AC3M3I" ,2014 ,true ,false,false ,true  ,1,null,null,CAC3),
		CAC3_M4U ("303-AC3M4U" ,2014 ,true ,false,false ,true  ,1,null,null,CAC3),
		CAC3_M4I ("303-AC3M4I" ,2014 ,true ,false,false ,true  ,1,null,null,CAC3),
		CAC3_M5U ("303-AC3M5U" ,2014 ,true ,false,false ,true  ,1,null,null,CAC3),
		CAC3_M5I ("303-AC3M5I" ,2014 ,true ,false,false ,true  ,1,null,null,CAC3),
		CAC3_M6U ("303-AC3M6U" ,2014 ,true ,false,false ,true  ,1,null,null,CAC3),
		CAC3_M6I ("303-AC3M6I" ,2014 ,true ,false,false ,true  ,1,null,null,CAC3),
		CAC3_M7U ("303-AC3M7U" ,2014 ,true ,false,false ,true  ,1,null,null,CAC3),
		CAC3_M7I ("303-AC3M7I" ,2014 ,true ,false,false ,true  ,1,null,null,CAC3),
		CAC3_C   ("303-AC3C"   ,2014 ,false,false,false ,true  ,1,null,new Period[]{Period.T1,Period.T2,Period.T3},CAC3),
		CAC3_D   ("303-AC3D"   ,2014 ,false,false,false ,true  ,1,null,new Period[]{Period.T1,Period.T2,Period.T3},CAC3),
		CAC3_Z   ("303-AC3Z"   ,2014 ,false,false,false ,true  ,1,null,new Period[]{Period.T1,Period.T2,Period.T3},CAC3),
		CAC3_ZA  ("303-AC3ZA"  ,2014 ,false,false,false ,true  ,1,null,new Period[]{Period.T1,Period.T2,Period.T3},CAC1),
		CAC3_ZD  ("303-AC3ZD"  ,2014 ,false,false,false ,true  ,1,null,new Period[]{Period.T1,Period.T2,Period.T3},CAC1),
		CAC3_E   ("303-AC3E"   ,2014 ,false,false,false ,true  ,1,null,new Period[]{Period.T1,Period.T2,Period.T3},CAC3),
		CAC3_F   ("303-AC3F"   ,2014 ,false,false,false ,false ,2,null,new Period[]{Period.T1,Period.T2,Period.T3},CAC3),
		CAC3_G   ("303-AC3G"   ,2014 ,false,false,false ,true  ,1,null,new Period[]{Period.T4},CAC3),
		CAC3_H   ("303-AC3H"   ,2014 ,false,false,false ,true  ,1,null,new Period[]{Period.T4},CAC3),
		CAC3_I   ("303-AC3I"   ,2014 ,false,false,false ,true  ,1,null,new Period[]{Period.T4},CAC3),
		CAC3_J   ("303-AC3J"   ,2014 ,false,false,false ,true  ,1,null,new Period[]{Period.T4},CAC3),
		CAC3_K   ("303-AC3K"   ,2014 ,false,false,false ,true  ,1,null,new Period[]{Period.T4},CAC3),
		CAC3_L   ("303-AC3L"   ,2014 ,false,false,false ,true  ,1,null,new Period[]{Period.T4},CAC3),
		CAC3_M   ("303-AC3M"   ,2014 ,false,false,false ,false ,2,null,new Period[]{Period.T4},CAC3),
	CAC4     ("303-AC4"  ,2014 ,true ,true ,true  ,false ,1,null,null,null),
		CAC4_M1U ("303-AC4M1U" ,2014 ,true ,false,false ,true  ,1,null,null,CAC4),
		CAC4_M1I ("303-AC4M1I" ,2014 ,true ,false,false ,true  ,1,null,null,CAC4),
		CAC4_M2U ("303-AC4M2U" ,2014 ,true ,false,false ,true  ,1,null,null,CAC4),
		CAC4_M2I ("303-AC4M2I" ,2014 ,true ,false,false ,true  ,1,null,null,CAC4),
		CAC4_M3U ("303-AC4M3U" ,2014 ,true ,false,false ,true  ,1,null,null,CAC4),
		CAC4_M3I ("303-AC4M3I" ,2014 ,true ,false,false ,true  ,1,null,null,CAC4),
		CAC4_M4U ("303-AC4M4U" ,2014 ,true ,false,false ,true  ,1,null,null,CAC4),
		CAC4_M4I ("303-AC4M4I" ,2014 ,true ,false,false ,true  ,1,null,null,CAC4),
		CAC4_M5U ("303-AC4M5U" ,2014 ,true ,false,false ,true  ,1,null,null,CAC4),
		CAC4_M5I ("303-AC4M5I" ,2014 ,true ,false,false ,true  ,1,null,null,CAC4),
		CAC4_M6U ("303-AC4M6U" ,2014 ,true ,false,false ,true  ,1,null,null,CAC4),
		CAC4_M6I ("303-AC4M6I" ,2014 ,true ,false,false ,true  ,1,null,null,CAC4),
		CAC4_M7U ("303-AC4M7U" ,2014 ,true ,false,false ,true  ,1,null,null,CAC4),
		CAC4_M7I ("303-AC4M7I" ,2014 ,true ,false,false ,true  ,1,null,null,CAC4),
		CAC4_C   ("303-AC4C"   ,2014 ,false,false,false ,true  ,1,null,new Period[]{Period.T1,Period.T2,Period.T3},CAC4),
		CAC4_D   ("303-AC4D"   ,2014 ,false,false,false ,true  ,1,null,new Period[]{Period.T1,Period.T2,Period.T3},CAC4),
		CAC4_Z   ("303-AC4Z"   ,2014 ,false,false,false ,true  ,1,null,new Period[]{Period.T1,Period.T2,Period.T3},CAC4),
		CAC4_ZA  ("303-AC4ZA"  ,2014 ,false,false,false ,true  ,1,null,new Period[]{Period.T1,Period.T2,Period.T3},CAC1),
		CAC4_ZD  ("303-AC4ZD"  ,2014 ,false,false,false ,true  ,1,null,new Period[]{Period.T1,Period.T2,Period.T3},CAC1),
		CAC4_E   ("303-AC4E"   ,2014 ,false,false,false ,true  ,1,null,new Period[]{Period.T1,Period.T2,Period.T3},CAC4),
		CAC4_F   ("303-AC4F"   ,2014 ,false,false,false ,false ,2,null,new Period[]{Period.T1,Period.T2,Period.T3},CAC4),
		CAC4_G   ("303-AC4G"   ,2014 ,false,false,false ,true  ,1,null,new Period[]{Period.T4},CAC4),
		CAC4_H   ("303-AC4H"   ,2014 ,false,false,false ,true  ,1,null,new Period[]{Period.T4},CAC4),
		CAC4_I   ("303-AC4I"   ,2014 ,false,false,false ,true  ,1,null,new Period[]{Period.T4},CAC4),
		CAC4_J   ("303-AC4J"   ,2014 ,false,false,false ,true  ,1,null,new Period[]{Period.T4},CAC4),
		CAC4_K   ("303-AC4K"   ,2014 ,false,false,false ,true  ,1,null,new Period[]{Period.T4},CAC4),
		CAC4_L   ("303-AC4L"   ,2014 ,false,false,false ,true  ,1,null,new Period[]{Period.T4},CAC4),
		CAC4_M   ("303-AC4M"   ,2014 ,false,false,false ,false ,2,null,new Period[]{Period.T4},CAC4),

	C47      ("303-47"   ,2014 ,false,false,true  ,true  ,1,null,new Period[]{Period.T1,Period.T2,Period.T3},null),
	
	C48      ("303-48"   ,2014 ,false,false,true  ,false ,1,null,new Period[]{Period.T4},null),
	C49      ("303-49"   ,2014 ,false,false,true  ,false ,1,null,new Period[]{Period.T4},null),
	C50      ("303-50"   ,2014 ,false,false,true  ,true  ,1,null,new Period[]{Period.T4},null),
	
	H3       ("303-H3"   ,2014 ,false,true ,true  ,false ,0,null,null,null),
	C51      ("303-51"   ,2014 ,false,false,true  ,false ,1,null,null,null),
	C52      ("303-52"   ,2014 ,false,false,true  ,false ,1,null,null,null),
	C53      ("303-53"   ,2014 ,false,false,true  ,false ,1,null,null,null),
	C54      ("303-54"   ,2014 ,false,false,true  ,true  ,1,null,null,null),
	H4       ("303-H4"   ,2014 ,false,true ,true  ,false ,0,null,null,null),
	C55      ("303-55"   ,2014 ,false,false,true  ,false ,1,null,null,null),
	C56      ("303-56"   ,2014 ,false,false,true  ,false ,1,null,null,null),
	C57      ("303-57"   ,2014 ,false,false,true  ,true  ,1,null,null,null),
	C58      ("303-58"   ,2014 ,false,false,true  ,true  ,1,null,null,null),
	H5       ("303-H5"   ,2014 ,false,true ,true  ,false ,0,null,null,null),	
	C64      ("303-64"   ,2014 ,false,false,true  ,true  ,1,null,null,null),
	C65      ("303-65"   ,2014 ,false,false,true  ,false ,1,null,null,null),
	C66      ("303-66"   ,2014 ,false,false,true  ,true  ,1,null,null,null),
	C67      ("303-67"   ,2014 ,false,false,true  ,false ,1,null,null,null),
	C68      ("303-68"   ,2014 ,false,false,true  ,false ,1,null,null,null),
	C69      ("303-69"   ,2014 ,false,false,true  ,true  ,1,null,null,null),
	C70      ("303-70"   ,2014 ,false,false,true  ,false ,1,null,null,null),
	C71      ("303-71"   ,2014 ,false,false,true  ,true  ,1,null,null,null),
	PBK  	 ("303-PBK"  ,2014 ,false,true,false ,true ,0,null,null,null)
	;
	
    private static final String MSG_KEY_PREFIX = "aon_enum_mod";

    public static Mod303Key getKeyWithValue( String value ) {
    	for (Mod303Key key : Mod303Key.values() ) {
    		if (StringUtils.equals(value, key.getValue())) {
    			return key;
    		}
    	}
    	throw new IllegalArgumentException("No enum constant " + Mod303Key.class.getName() + " for value " + value);
    }
    
    public static final String ACTIVITIES_PREFIX = "303-AC";
    public static final String FARMING_ACTIVITIES_PREFIX = "303-AG";
    
    private String value;
    private boolean descriptionEnabled;
    private boolean title;
    private boolean visibleInList;
    private boolean total;
    private int level;
    private Administration[] administrations;
    private Period[] periods;
    private Mod303Key parentKey;
    
	private Mod303Key(String value, int year, boolean descriptionEnabled,
			boolean title, boolean visibleInList, boolean total, int level,
			Administration[] administrations, Period[] periods,
			Mod303Key parentKey) {
		this.value = value;
		this.descriptionEnabled = descriptionEnabled;
		this.level = level;
		this.title = title;
		this.visibleInList = visibleInList;
		this.total = total;
		this.administrations = administrations;
		this.periods = periods;
		this.parentKey = parentKey;
	}
    
	public boolean isFarmer(){
		return getValue().startsWith(FARMING_ACTIVITIES_PREFIX);
	}
	
	public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + value);
    }

	public boolean isDescriptionEnabled() {
		return descriptionEnabled;
	}

	public boolean isDifEnabled() {
		return false;
	}
	
	public boolean isTitle() {
		return title;
	}
	
	public boolean isVisibleInList() {
		return visibleInList;
	}
	
	public Period[] getPeriods() {
		return periods;
	}
	
	public Mod303Key getParentKey() {
		return parentKey;
	}
	
	public boolean isTotal() {
		return total;
	}

	public int getLevel() {
		return level;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public boolean accept(Administration administration, Period period) {
		boolean ret = true;
		if (administrations != null) {
			ret = false;
			for (Administration admin : administrations) {
				if (admin == administration) {
					ret = true;
				}
			}
		}
		if (ret && periods != null) {
			ret = false;
			for (Period per : periods) {
				if (per == period) {
					ret = true;
				}
			}
		}
		return ret;
	}
}