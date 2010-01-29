package com.code.aon.report;

import java.util.Map;

/**
 * @author ecastellano
 *
 */
public interface IReportDynamicParamsProvider {

	/**
	 * @return map
	 */
	public Map<String,Object> getDynamicParamsMap();
	
}
