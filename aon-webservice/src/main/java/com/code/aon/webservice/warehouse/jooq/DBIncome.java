package com.code.aon.webservice.warehouse.jooq;

import java.util.Map;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;

import com.code.aon.webservice.common.MSG;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.IncomeProperties;
import com.esferalia.aon.occam.api.model.warehouse.Income;

public class DBIncome {
	
	public static JSONArray getIncomes(Domain domain,String login, Map<String, String[]> map){
	    JSONArray array = new JSONArray();
	    AON.getIncomeStream(domain.getName(), domain.getId(), login, f ->  incomeFilter(domain, map, f))
	    	.forEach(income -> array.put(new JSONObject(income.toJSON())));
	    return array;
	}
	
    public static JSONObject getIncome(Domain domain,String login, Integer id){
    	Optional<Income> income = AON.getIncome(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(id));
    	return income.isPresent() ? new JSONObject(income.get().toJSON()) : new JSONObject();
    }

	public static JSONArray getIncomeDetails(Domain domain,String login, Integer incomeId){
	    JSONArray array = new JSONArray();
	    AON.getIncomeDetailStream(domain.getName(), domain.getId(), login, f -> f.getIncomeProperty().eq(incomeId))
	    	.forEach(detail -> array.put(new JSONObject(detail.toJSON())));
	    return array;
	}
    
	public static Filter incomeFilter(Domain domain, Map<String, String[]> filterMap, IncomeProperties f) {
		Filter filter = f.getDomainProperty().eq(domain.getId());
		
		if(filterMap.containsKey(MSG.SUPPLIER)){
			Filter fsupplier = f.getSupplierProperty().eq(Integer.parseInt(filterMap.get(MSG.SUPPLIER)[0])); 
			for(Integer i = 1; i < filterMap.get(MSG.SUPPLIER).length ; i++){
				fsupplier = fsupplier.or(f.getSupplierProperty().eq(Integer.parseInt(filterMap.get(MSG.SUPPLIER)[i])));
			}
			filter = filter.and(fsupplier);
		} 
		
		if(filterMap.containsKey(MSG.CARRIER_PACKING)){
			Filter fcarrierPacking = f.getCarrierPackingProperty().eq(Integer.parseInt(filterMap.get(MSG.CARRIER_PACKING)[0])); 
			for(Integer i = 1; i < filterMap.get(MSG.CARRIER_PACKING).length ; i++){
				fcarrierPacking = fcarrierPacking.or(f.getCarrierPackingProperty().eq(Integer.parseInt(filterMap.get(MSG.CARRIER_PACKING)[i])));
			}
			filter = filter.and(fcarrierPacking);
		}
		
		return filter;
	}
}
