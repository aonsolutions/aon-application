package com.esferalia.aon.gwt.mod200.client.matrix;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.api.client.fiscal.JsFiscalMenuItem;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;
import com.google.gwt.logging.client.ConsoleLogHandler;

class MatrixData {

	private static final Logger LOGGER = Logger.getLogger(MatrixData.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	
	private Map<String, Map<Administration, Map<MatrixPeriodType, Map<String, Map<String, List<JsFiscalMenuItem>>>>>> map = new LinkedHashMap<>();
	
	protected boolean hasDomainData(String domainName) {
		return map.containsKey(domainName);
	}
	protected Map<String, Map<Administration, Map<MatrixPeriodType, Map<String, Map<String, List<JsFiscalMenuItem>>>>>> getMap() {
		return map;
	}
	
	protected MatrixData add(JsFiscalMenuItem item) {
		String domainName = item.getDomain() + "|" + item.getDomainName();
		String document = item.getDocument();
//		String n = item.getName();
//		String s = item.getSurname();
//		String name = AonStringUtils.join(new String[]{s,n}, AonStringUtils.isBlank(s)?"":", ");
//		name = AonStringUtils.abbreviate(AonStringUtils.join(new String[]{document,name}, " "), 50);
		String name = document;
		Administration admon = Optional
				.ofNullable( Administration.safeValueOf(item.getAdministration()))
				.orElse(Administration.UNKNOWN);
		FiscalModelType modelType = FiscalModelType.valueOf(item.getModel());
		Period period = Period.valueOf(item.getPeriod());
		MatrixPeriodType type = MatrixPeriodType.getPeriodType(period);
		
		Map<Administration, Map<MatrixPeriodType, Map<String, Map<String, List<JsFiscalMenuItem>>>>> admonMap = 
				map.computeIfAbsent(domainName, k -> new LinkedHashMap<>());
		Map<MatrixPeriodType, Map<String, Map<String, List<JsFiscalMenuItem>>>> periodMap = 
				admonMap.computeIfAbsent(admon , k -> new LinkedHashMap<>());
		Map<String, Map<String, List<JsFiscalMenuItem>>> modelMap = 
				periodMap.computeIfAbsent(type , k -> new LinkedHashMap<>());
		Map<String, List<JsFiscalMenuItem>> documentMap = 
				modelMap.computeIfAbsent(modelType.toString() , k -> new LinkedHashMap<>());
		List<JsFiscalMenuItem> list = 
				documentMap.computeIfAbsent(name , k -> {
					LinkedList<JsFiscalMenuItem> values = new LinkedList<>();
					for (int x = 0; x < type.getArraySize(); x++) {
						values.add(null);
					}
					return values;
				});
		list.set(type.getIndex(period), item);

		return this;
	}
	
	protected Set<String> getDomains() {
		return map.keySet();
	}
	
	protected Set<Administration> getAdministrations(String domKey) {
		return map
			.getOrDefault(domKey, new LinkedHashMap<>())
			.keySet();
	}
	protected Set<MatrixPeriodType> getPeriodTypes(String domainKey, Administration admKey) {
		return map
			.getOrDefault(domainKey, new LinkedHashMap<>())
			.getOrDefault(admKey, new LinkedHashMap<>())
			.keySet();
	}
	protected Set<String> getModels(String domainKey, Administration admKey, MatrixPeriodType perKey) {
		return map
			.getOrDefault(domainKey, new LinkedHashMap<>())
			.getOrDefault(admKey, new LinkedHashMap<>())
			.getOrDefault(perKey, new LinkedHashMap<>()) 
			.keySet();
	}
	
	protected Set<String> getDocs(String domainKey, Administration admKey, MatrixPeriodType perKey,String modKey) {
		return map
			.getOrDefault(domainKey, new LinkedHashMap<>())
			.getOrDefault(admKey, new LinkedHashMap<>())
			.getOrDefault(perKey, new LinkedHashMap<>())
			.getOrDefault(modKey, new LinkedHashMap<>())
			.keySet();
	}
	
	protected List<JsFiscalMenuItem> getItems(String domainKey, Administration admKey, MatrixPeriodType perKey,String modKey, String docKey) {
		return map
			.getOrDefault(domainKey, new LinkedHashMap<>())
			.getOrDefault(admKey, new LinkedHashMap<>())
			.getOrDefault(perKey, new LinkedHashMap<>())
			.getOrDefault(modKey, new LinkedHashMap<>())
			.getOrDefault(docKey, new LinkedList<>());
	}
	
}
