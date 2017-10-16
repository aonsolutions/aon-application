package net.aonsolutions.aon.gwt.udapa.server;

import java.util.LinkedHashMap;
import java.util.Map;

import net.aonsolutions.aon.gwt.udapa.shared.quality.QualitySheetCode;

public class QualitySheetCompute {

	public static Map<String, String> COMPUTE_MAP = new LinkedHashMap<String, String>();

	static {
		COMPUTE_MAP.put(QualitySheetCode.UFQCC021.getName(),"(Qufqcc02/Qufqcc01)*100");
		COMPUTE_MAP.put(QualitySheetCode.UFQCC031.getName(),"(Qufqcc03/Qufqcc01)*100");
		COMPUTE_MAP.put(QualitySheetCode.UFQCC041.getName(),"(Qufqcc04/Qufqcc01)*100");
		COMPUTE_MAP.put(QualitySheetCode.UFQCC051.getName(),"(Qufqcc05/Qufqcc01)*100");
		COMPUTE_MAP.put(QualitySheetCode.UFQCC061.getName(),"(Qufqcc06/Qufqcc01)*100");
		COMPUTE_MAP.put(QualitySheetCode.UFQCC071.getName(),"(Qufqcc07/Qufqcc01)*100");
		COMPUTE_MAP.put(QualitySheetCode.UFQCC081.getName(),"(Qufqcc08/Qufqcc01)*100");
		COMPUTE_MAP.put(QualitySheetCode.UFQCC091.getName(),"(Qufqcc09/Qufqcc01)*100");
		COMPUTE_MAP.put(QualitySheetCode.UFQCC10.getName(),"PROPACO ? Qufqcc10 : (Qufqcc101*Qufqcc01)/100");
		COMPUTE_MAP.put(QualitySheetCode.UFQCC101.getName(),"(PROPACO || (Qufqcc101 == 0)) ? (Qufqcc10/Qufqcc01)*100 : Qufqcc101");
		COMPUTE_MAP.put(QualitySheetCode.UFQCC111.getName(),"(Qufqcc11/Qufqcc01)*100");
		COMPUTE_MAP.put(QualitySheetCode.UFQCC121.getName(),"(Qufqcc12/Qufqcc01)*100");
		COMPUTE_MAP.put(QualitySheetCode.UFQCC131.getName(),"(Qufqcc13/Qufqcc01)*100");
		COMPUTE_MAP.put(QualitySheetCode.UFQCC14.getName(),"PROPACO ? Qufqcc02+Qufqcc04+Qufqcc06+Qufqcc08+Qufqcc10"
																+ " : Qufqcc02+Qufqcc04+Qufqcc06+Qufqcc08");
		COMPUTE_MAP.put(QualitySheetCode.UFQCC141.getName(),"(Qufqcc14/Qufqcc01)*100");
		COMPUTE_MAP.put(QualitySheetCode.UFQCC15.getName(),"Qufqcc03+Qufqcc05+Qufqcc07"
														 + "+Qufqcc09+Qufqcc11+Qufqcc12"
														 + /*"+Qufqcc13*/"+Qufqcc16+Qufqcc17"
														 + "+Qufqcc18");
		COMPUTE_MAP.put(QualitySheetCode.UFQCC151.getName(),"(Qufqcc15/Qufqcc01)*100");
		COMPUTE_MAP.put(QualitySheetCode.UFQCC161.getName(),"(Qufqcc16/Qufqcc01)*100");
		COMPUTE_MAP.put(QualitySheetCode.UFQCC171.getName(),"(Qufqcc17/Qufqcc01)*100");
		COMPUTE_MAP.put(QualitySheetCode.UFQCC181.getName(),"(Qufqcc18/Qufqcc01)*100");

		COMPUTE_MAP.put(QualitySheetCode.UFQCD01.getName(),"Qufqcc01");
		
		COMPUTE_MAP.put(QualitySheetCode.UFQCD021.getName(),"(Qufqcd021 == 0 ? Qufqcd02/Qufqcd01)*100 : Qufqcd021");
		COMPUTE_MAP.put(QualitySheetCode.UFQCD031.getName(),"(Qufqcd031 == 0 ? Qufqcd03/Qufqcd01)*100 : Qufqcd031");
		COMPUTE_MAP.put(QualitySheetCode.UFQCD041.getName(),"(Qufqcd041 == 0 ? Qufqcd04/Qufqcd01)*100 : Qufqcd041");
		COMPUTE_MAP.put(QualitySheetCode.UFQCD051.getName(),"(Qufqcd051 == 0 ? Qufqcd05/Qufqcd01)*100 : Qufqcd051");
		COMPUTE_MAP.put(QualitySheetCode.UFQCD061.getName(),"(Qufqcd061 == 0 ? Qufqcd06/Qufqcd01)*100 : Qufqcd061");
		COMPUTE_MAP.put(QualitySheetCode.UFQCD071.getName(),"(Qufqcd071 == 0 ? Qufqcd07/Qufqcd01)*100 : Qufqcd071");
		COMPUTE_MAP.put(QualitySheetCode.UFQCD081.getName(),"(Qufqcd081 == 0 ? Qufqcd08/Qufqcd01)*100 : Qufqcd081");
		COMPUTE_MAP.put(QualitySheetCode.UFQCD091.getName(),"(Qufqcd091 == 0 ? Qufqcd09/Qufqcd01)*100 : Qufqcd091");
		COMPUTE_MAP.put(QualitySheetCode.UFQCD101.getName(),"(Qufqcd101 == 0 ? Qufqcd10/Qufqcd01)*100 : Qufqcd101");
		COMPUTE_MAP.put(QualitySheetCode.UFQCD121.getName(),"(Qufqcd121 == 0 ? Qufqcd12/Qufqcd01)*100 : Qufqcd121");
		COMPUTE_MAP.put(QualitySheetCode.UFQCD131.getName(),"(Qufqcd131 == 0 ? Qufqcd13/Qufqcd01)*100 : Qufqcd131");
		COMPUTE_MAP.put(QualitySheetCode.UFQCD141.getName(),"(Qufqcd141 == 0 ? Qufqcd14/Qufqcd01)*100 : Qufqcd141");
		COMPUTE_MAP.put(QualitySheetCode.UFQCD151.getName(),"(Qufqcd151 == 0 ? Qufqcd15/Qufqcd01)*100 : Qufqcd151");
		COMPUTE_MAP.put(QualitySheetCode.UFQCD161.getName(),"(Qufqcd161 == 0 ? Qufqcd16/Qufqcd01)*100 : Qufqcd161");
		
		COMPUTE_MAP.put(QualitySheetCode.UFQCD02.getName(),"(Qufqcd021*Qufqcd01)/100");
		COMPUTE_MAP.put(QualitySheetCode.UFQCD03.getName(),"(Qufqcd031*Qufqcd01)/100");
		COMPUTE_MAP.put(QualitySheetCode.UFQCD04.getName(),"(Qufqcd041*Qufqcd01)/100");
		COMPUTE_MAP.put(QualitySheetCode.UFQCD05.getName(),"(Qufqcd051*Qufqcd01)/100");
		COMPUTE_MAP.put(QualitySheetCode.UFQCD06.getName(),"(Qufqcd061*Qufqcd01)/100");
		COMPUTE_MAP.put(QualitySheetCode.UFQCD07.getName(),"(Qufqcd071*Qufqcd01)/100");
		COMPUTE_MAP.put(QualitySheetCode.UFQCD08.getName(),"(Qufqcd081*Qufqcd01)/100");
		COMPUTE_MAP.put(QualitySheetCode.UFQCD09.getName(),"(Qufqcd091*Qufqcd01)/100");
		COMPUTE_MAP.put(QualitySheetCode.UFQCD10.getName(),"(Qufqcd101*Qufqcd01)/100");
		COMPUTE_MAP.put(QualitySheetCode.UFQCD12.getName(),"(Qufqcd121*Qufqcd01)/100");
		COMPUTE_MAP.put(QualitySheetCode.UFQCD13.getName(),"(Qufqcd131*Qufqcd01)/100");
		COMPUTE_MAP.put(QualitySheetCode.UFQCD14.getName(),"(Qufqcd141*Qufqcd01)/100");
		COMPUTE_MAP.put(QualitySheetCode.UFQCD15.getName(),"(Qufqcd151*Qufqcd01)/100");
		COMPUTE_MAP.put(QualitySheetCode.UFQCD16.getName(),"(Qufqcd161*Qufqcd01)/100");
	
		COMPUTE_MAP.put(QualitySheetCode.UFQCD11.getName(),"PROPACO ? Qufqcd02+Qufqcd03+Qufqcd04+Qufqcd05+Qufqcd06+Qufqcd07+Qufqcd08+Qufqcd09+Qufqcd10+Qufqcd12+Qufqcd13+Qufqcd14+Qufqcd15+Qufqcd16"
																+ " : Qufqcd02+Qufqcd03+Qufqcd04+Qufqcd05+Qufqcd06+Qufqcd07+Qufqcd08+Qufqcd09+Qufqcd10+Qufqcc10+Qufqcd12+Qufqcd13+Qufqcd14+Qufqcd15+Qufqcd16");
		COMPUTE_MAP.put(QualitySheetCode.UFQCD111.getName(),"(Qufqcd11/Qufqcd01)*100");
	}
	
	

}
