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
		COMPUTE_MAP.put(QualitySheetCode.UFQCC101.getName(),"PROPACO ? (Qufqcc10/Qufqcc01)*100 : Qufqcc101");
		COMPUTE_MAP.put(QualitySheetCode.UFQCC10.getName(),"PROPACO ? Qufqcc10 : (Qufqcc101*Qufqcc01)/100");
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
