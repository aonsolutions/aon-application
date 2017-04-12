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
		COMPUTE_MAP.put(QualitySheetCode.UFQCC101.getName(),"(Qufqcc10/Qufqcc01)*100");
		COMPUTE_MAP.put(QualitySheetCode.UFQCC111.getName(),"(Qufqcc11/Qufqcc01)*100");
		COMPUTE_MAP.put(QualitySheetCode.UFQCC121.getName(),"(Qufqcc12/Qufqcc01)*100");
		COMPUTE_MAP.put(QualitySheetCode.UFQCC131.getName(),"(Qufqcc13/Qufqcc01)*100");
		COMPUTE_MAP.put(QualitySheetCode.UFQCC14.getName(),"Qufqcc02+Qufqcc04+Qufqcc06"
														 + "+Qufqcc08+Qufqcc10");
		COMPUTE_MAP.put(QualitySheetCode.UFQCC141.getName(),"(Qufqcc14/Qufqcc01)*100");
		COMPUTE_MAP.put(QualitySheetCode.UFQCC15.getName(),"Qufqcc03+Qufqcc05+Qufqcc07"
														 + "+Qufqcc09+Qufqcc11+Qufqcc12+Qufqcc12");
		COMPUTE_MAP.put(QualitySheetCode.UFQCC151.getName(),"(Qufqcc15/Qufqcc01)*100");

		COMPUTE_MAP.put(QualitySheetCode.UFQCD01.getName(),"Qufqcc01");
		COMPUTE_MAP.put(QualitySheetCode.UFQCD021.getName(),"(Qufqcd02/Qufqcd01)*100");
		COMPUTE_MAP.put(QualitySheetCode.UFQCD031.getName(),"(Qufqcd03/Qufqcd01)*100");
		COMPUTE_MAP.put(QualitySheetCode.UFQCD041.getName(),"(Qufqcd04/Qufqcd01)*100");
		COMPUTE_MAP.put(QualitySheetCode.UFQCD051.getName(),"(Qufqcd05/Qufqcd01)*100");
		COMPUTE_MAP.put(QualitySheetCode.UFQCD061.getName(),"(Qufqcd06/Qufqcd01)*100");
		COMPUTE_MAP.put(QualitySheetCode.UFQCD071.getName(),"(Qufqcd07/Qufqcd01)*100");
		COMPUTE_MAP.put(QualitySheetCode.UFQCD081.getName(),"(Qufqcd08/Qufqcd01)*100");
		COMPUTE_MAP.put(QualitySheetCode.UFQCD091.getName(),"(Qufqcd09/Qufqcd01)*100");
		COMPUTE_MAP.put(QualitySheetCode.UFQCD101.getName(),"(Qufqcd10/Qufqcd01)*100");

		COMPUTE_MAP.put(QualitySheetCode.UFQCD11.getName(),"Qufqcd02+Qufqcd03+Qufqcd04"
				 + "+Qufqcd05+Qufqcd06+Qufqcd07+Qufqcd08+Qufqcd09+Qufqcd10");
		COMPUTE_MAP.put(QualitySheetCode.UFQCD111.getName(),"(Qufqcd15/Qufqcd01)*100");
	}
	
	

}
