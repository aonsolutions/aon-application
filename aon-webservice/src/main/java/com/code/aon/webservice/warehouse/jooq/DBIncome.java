package com.code.aon.webservice.warehouse.jooq;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.code.aon.webservice.common.MSG;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.IncomeDetailProperties;
import com.esferalia.aon.occam.api.model.Properties.IncomeProperties;
import com.esferalia.aon.occam.api.model.Properties.ProductProperties;
import com.esferalia.aon.occam.api.model.management.PurchaseDetail;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.IncomeStatus;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.api.model.warehouse.Income;
import com.esferalia.aon.occam.api.model.warehouse.IncomeDetail;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;
import com.esferalia.aon.watson.server.AonDateUtils;

public class DBIncome {
	
	public static JSONArray getIncomes(Domain domain,String login, Map<String, String[]> map){
	    JSONArray array = new JSONArray();
	    AON.getIncomeStream(domain.getName(), domain.getId(), login, f ->  incomeFilter(domain, map, f))
	    	.sorted((e1, e2) -> e2.getIssueDate().compareTo(e1.getIssueDate()))
	    .forEach(income -> {
	    	JSONObject json = incomeToJSON(income);
	    	Stream<IncomeDetail> s = AON.getIncomeDetailStream(domain.getName(), domain.getId(), login, f -> f.getIncomeProperty().eq(income.getId()));
	    	JSONArray array2 = new JSONArray();
	    	Integer[] cont = new Integer[] {0};
	    	s.forEach(id ->{
	    		array2.put(incomeDetailToJSON(id));
	    		cont[0] = cont[0] + 1;
	    	});
	    	json.put("details", array2);
    		json.put("detail_count", cont[0]);
	    	array.put(json);	
	    });
	    return array;
	}
	
	public static JSONArray getIncomesQ(Domain domain,String login, Map<String, String[]> map){
	    JSONArray array = new JSONArray();
	    AON.getIncomeStream(domain.getName(), domain.getId(), login, f ->  incomeFilter(domain, map, f))
	    	.sorted((e1, e2) -> e2.getIssueDate().compareTo(e1.getIssueDate()))
	    .forEach(income -> array.put(incomeToJSON(income)));
	    return array;
	}

    public static JSONObject getIncome(Domain domain,String login, Integer id){
    	Optional<Income> income = AON.getIncome(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(id));
    	return incomeToJSON(income);
    }

    public static JSONArray getIncomeDetails(Domain domain,String login, Map<String, String[]> map){
		JSONArray array = new JSONArray();
		if(map.containsKey("quality")) {
			Integer[] ids = AON.getDataResponseStream(domain.getName(), domain.getId(), login, DataResponseSource.QUALITY, f -> f.getDomainProperty().eq(domain.getId()).and(f.getSourceIdProperty().isNotNull()))
			.map(g -> g.getSourceId()).toArray(Integer[]::new);
			
			AON.getIncomeDetailStream(domain.getName(), domain.getId(), login, f -> 
				incomeDetailFilter(domain, map, f).and(f.getIdProperty().notIn(ids)))
			.forEach(detail -> array.put(incomeDetailToJSON(detail)));
		} else AON.getIncomeDetailStream(domain.getName(), domain.getId(), login, f -> incomeDetailFilter(domain, map, f))
			.forEach(detail -> array.put(incomeDetailToJSON(detail)));
		return array;
	}
    
    public static JSONArray getIncomeMovements(Domain domain,String login, Map<String, String[]> map){
		JSONArray array = new JSONArray();
		AON.getIncomeDetailStream(domain.getName(), domain.getId(), login, f -> incomeFilter(domain, map, f), f -> productFilter(domain, map, f))
			.forEach(detail -> array.put(incomeDetailFullToJSON(detail)));
		return array;
	}
    
	public static JSONArray getIncomeDetails(Domain domain,String login, Integer incomeId){
	    JSONArray array = new JSONArray();
	    AON.getIncomeDetailStream(domain.getName(), domain.getId(), login, f -> f.getIncomeProperty().eq(incomeId))
	    	.forEach(detail -> array.put(incomeDetailToJSON(detail)));
	    return array;
	}
	
	public static JSONObject insertIncome(Domain domain, String login, JSONObject json) {
		Optional<Supplier> supplier = AON.getSupplier(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(json.getInt("supplier")));
		Date date = AonDateUtils.dateTimeParse(json.getString("issue_time"));
		Income income = new Income()
				.setCarrierPacking(json.getInt("carrier_packing"))
				.setDomain(domain.getId())
				.setIssueDate(date != null ? date : new Date())
				.setReferenceCode(json.getString("reference_code"))
				.setScope(supplier.get().getScope())
				.setSupplier(json.getInt("supplier"))
				.setWorkplace(json.getInt("workplace"))
				.setAddress(json.optInt("address") != 0 ? json.optInt("address") : null)
				// Por Defecto ¿?
				.setStatus(IncomeStatus.PENDING)
				.setSecurityLevel(SecurityLevel.OFFICIAL.ordinal())
				.setNumberOfPymnts(1)
				.setDaysToFirstPymnt(0)
				.setDaysBetweenPymnt(0)
				.setPymntDays("");
		
		Optional<Income> result = AON.insertIncome(domain.getName(), domain.getId(), login, income);
		return  incomeToJSON(result);
	}
    
	public static Filter incomeFilter(Domain domain, Map<String, String[]> filterMap, IncomeProperties f) {
		Filter filter = f.getDomainProperty().eq(domain.getId());
		
		if(filterMap.containsKey(MSG.FROM)){
			Date date = AonDateUtils.getDateWithoutTime(new Date(Long.parseLong(filterMap.get(MSG.FROM)[0])));
			filter = filter.and(f.getIssueTimeProperty().ge(AonDateUtils.toSql(date)));
		}

		if(filterMap.containsKey(MSG.TO)){
			Date date = AonDateUtils.getDateWithoutTime(new Date(Long.parseLong(filterMap.get(MSG.TO)[0])));
			filter = filter.and(f.getIssueTimeProperty().le(AonDateUtils.toSql(date)));
		}
		
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
	
	public static Filter incomeDetailFilter(Domain domain, Map<String, String[]> filterMap, IncomeDetailProperties f) {
		Filter filter = f.getDomainProperty().eq(domain.getId());
		
		if(filterMap.containsKey("income")){
			Integer income = Integer.parseInt(filterMap.get("income")[0]);
			filter = filter.and(f.getIncomeProperty().eq(income));
		}
		
		if(filterMap.containsKey(MSG.CARRIER_PACKING)){
			Integer[] array = AON.getIncomeStream(domain.getName(), domain.getId(), "", h -> 
				h.getCarrierPackingProperty().eq(Integer.parseInt(filterMap.get(MSG.CARRIER_PACKING)[0])))
			.map(i -> i.getId()).toArray(Integer[]::new);
			Filter fcarrierPacking = f.getIncomeProperty().in(array); 
			filter = filter.and(fcarrierPacking);
		}
		
		if(filterMap.containsKey("cpnotnull")){
			Integer[] array = AON.getIncomeStream(domain.getName(), domain.getId(), "", h -> 
				h.getCarrierPackingProperty().isNotNull())
			.map(i -> i.getId()).toArray(Integer[]::new);
			Filter fcarrierPacking = f.getIncomeProperty().in(array); 
			filter = filter.and(fcarrierPacking);
		}
		
		return filter;
	}
	
	public static Filter productFilter(Domain domain, Map<String, String[]> filterMap, ProductProperties f) {
		Filter filter = f.getDomainProperty().eq(domain.getId());
		
		if(filterMap.containsKey(MSG.PRODUCT)){
			filter = filter.and(f.getIdProperty().eq(Integer.parseInt(filterMap.get(MSG.PRODUCT)[0])));
		}
		
		if(filterMap.containsKey(MSG.CATEGORY)){
			String[] categories = filterMap.get(MSG.CATEGORY);
			filter = filter.and(f.getCategoryProperty().in(Arrays.asList(categories).toArray(new Integer[categories.length])));
		}
		
		return filter;
	}

	
	public static JSONObject insertIncomeDetail(Domain domain,String login, JSONObject json){
		Item item = AON.getItem(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(json.getInt("item")));
		Product product = AON.getProduct(domain.getName(), domain.getId(), login, f-> f.getIdProperty().eq(item.getProductId()));
		Integer incomeId = json.getInt("income");
		Integer purchaseDetailId = json.getInt("purchase_detail");
		Optional<IncomeDetail> incomeDetail = AON.getIncomeDetail(domain.getName(), domain.getId(), login, f ->
			f.getIncomeProperty().eq(incomeId)
			.and(f.getPurchaseDetailProperty().eq(purchaseDetailId)));
		IncomeDetail iDetail = new IncomeDetail();
		Integer workplaceId = json.getInt("workplace");
		Warehouse warehouse = AON.getWarehouse(domain.getName(), domain.getId(), login, f -> f.getWorkplaceProperty().eq(workplaceId));
		if(incomeDetail.isPresent()){
			iDetail = incomeDetail.get();
			iDetail.setQuantity(iDetail.getQuantity() + json.getDouble("quantity"));
			AON.updateIncomeDetail(domain.getName(), domain.getId(), login, iDetail);
		} else  {
			PurchaseDetail pd = AON.getPurchaseDetail(domain.getName(), domain.getId(), login, purchaseDetailId);
			String description = json.getString("description");
			if(product.isLotable()) {
				description = description + " #" + item.getSerialNumber();
			}
			iDetail = new IncomeDetail()
					.setDescription(description)
					.setDiscountExpression(pd.getDiscountExpression())
					.setDomain(domain.getId())
					.setIncome(new Income().setId(incomeId))
					.setItem(new Item().setId(json.getInt("item")))
					.setPurchaseDetail(purchaseDetailId)
					.setQuantity(json.getDouble("quantity"))
					.setWarehouse(warehouse.getId())
					.setPrice(pd.getPrice());
			
			AON.insertIncomeDetail(domain.getName(), domain.getId(), login, iDetail);
		}
		if(product.isInventoriable()){
			AON.addStock(domain, login, json.getInt("item"), json.getDouble("quantity"), warehouse.getId());
		}
		return new JSONObject();	
	}
	    
	public static JSONObject updateIncomeDetail(Domain domain,String login, JSONObject json){
		// TODO HACER PARA TODOS LOS CASOS!!!!
		Double quantity = json.getDouble("quantity");
		Integer id = json.getInt("id");
		Optional<IncomeDetail> incomeDetail = AON.getIncomeDetail(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(id));
		if(incomeDetail.isPresent()){
			incomeDetail = AON.updateIncomeDetail(domain.getName(), domain.getId(), login, incomeDetail.get().setQuantity(quantity));
			Integer itemId = incomeDetail.get().getItem().getId();
			Item item = AON.getItem(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(itemId));
			Product product = AON.getProduct(domain.getName(), domain.getId(), login, f-> f.getIdProperty().eq(item.getProductId()));
			if(product.isInventoriable()){
				Integer warehouse = incomeDetail.get().getWarehouse();
				AON.addStock(domain, login, json.getInt("item"), quantity, warehouse);
			}
			return incomeDetailToJSON(incomeDetail);
		}
		return new JSONObject();
	}
	    
	public static JSONObject deleteIncomeDetail(Domain domain,String login, JSONObject json){
		Integer id = json.getInt("id");
		Optional<IncomeDetail> incomeDetail = AON.deleteIncomeDetail(domain.getName(), domain.getId(), login, id);
		if(incomeDetail.isPresent()){
			Item item = AON.getItem(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(incomeDetail.get().getItem().getId()));
			Product product = AON.getProduct(domain.getName(), domain.getId(), login, f-> f.getIdProperty().eq(item.getProductId()));
			if(product.isInventoriable()){
				Double q = AON.substractStock(domain, login, incomeDetail.get().getItem().getId(), incomeDetail.get().getQuantity(), incomeDetail.get().getWarehouse());
				if(product.isLotable() && q == 0.0){
					AON.deleteItem(domain.getName(), domain.getId(), login, item);
				}
			}
		}
		return incomeDetailToJSON(incomeDetail);
	}
	
	public static JSONObject deleteIncome(Domain domain,String login, JSONObject json){
		Integer id = json.getInt("id");
		Optional<Income> income = AON.deleteIncome(domain.getName(), domain.getId(), login, id);
		return incomeToJSON(income);
	}
	
	public static JSONObject incomeToJSON(Optional<Income> income){
		return income.isPresent() ? incomeToJSON(income.get()) : new JSONObject();
	}
	
	public static JSONObject incomeDetailToJSON(Optional<IncomeDetail> incomeDetail){
		return incomeDetail.isPresent() ? incomeDetailToJSON(incomeDetail.get()) : new JSONObject();
	}
	
	private static Integer number;
	public static JSONObject getIncomeLastLote(Domain domain, String login, String referenceCode) {
		number = 0;
		AON.getIncomeStream(domain.getName(), domain.getId(), login, f -> f.getReferenceCodeProperty().like(referenceCode + "%").and(f.getDomainProperty().eq(domain.getId()))).forEach(r->{
			Integer n = Integer.parseInt(r.getReferenceCode().substring(6));
			number = n >= number ? n + 1 : number;
		});
		
		JSONObject json = new JSONObject();
		json.put(MSG.NAME, referenceCode + (number.toString().length() == 1 ? "0" + number : number));
		return json;
	}
	
	public static JSONObject  incomeToJSON(Income income){
		JSONObject json = new JSONObject();
		if(income != null){
			json.put(MSG.ID, income.getId());
			json.put(MSG.DOMAIN, income.getDomain());
			json.put(MSG.PROJECT, income.getProject());
			
			JSONObject registry = new JSONObject();
			registry.put(MSG.ID, income.getSupplier());
			registry.put(MSG.NAME, income.getSupplierName());
			json.put(MSG.REGISTRY, registry);
			
			json.put(MSG.REFERENCE_CODE, income.getReferenceCode());
			json.put(MSG.ADDRESS, income.getAddress());
			json.put(MSG.ISSUE_DATE, AonDateUtils.dateTimeFormat(income.getIssueDate()));
			json.put(MSG.PAY_METHOD, income.getPayMethod());
			json.put(MSG.CONFIDENTIAL, income.isConfidential());
			
			if(income.getStatus() != null ){
				JSONObject status = new JSONObject();
				status.put(MSG.ID, income.getStatus().ordinal());
				status.put(MSG.NAME, income.getStatus().getName());
				json.put(MSG.STATUS, status);
			}
			
			json.put(MSG.COMMENTS, income.getComments());
			json.put(MSG.REMARKS, income.getRemarks());
			json.put(MSG.WORKPLACE, income.getWorkplace());
			json.put(MSG.SCOPE, income.getScope());
			json.put(MSG.NUMBER_OF_PYMNTS, income.getNumberOfPymnts());
			json.put(MSG.DAYS_TO_FIRST_PYMNT, income.getDaysToFirstPymnt());
			json.put(MSG.DAYS_BETWEEN_PYMNTS, income.getDaysBetweenPymnt());
			json.put(MSG.PYMNT_DAYS, income.getPymntDays());
			json.put(MSG.BANK_ACCOUNT, income.getBankAccount());
			json.put(MSG.BANK_ALIAS, income.getBankAlias());
			json.put(MSG.BIC, income.getBic());
			json.put(MSG.CARRIER_PACKING, income.getCarrierPacking());
			json.put(MSG.SERIES_NUMBER, "");
			json.put(MSG.ORDER_TYPE, "income");
			json.put(MSG.REFERENCE, income.getReferenceCode() != null ? income.getReferenceCode() : " ");
			
			json.put(MSG.CREATION_DATE, income.getCreationDate());
			json.put(MSG.CREATION_USER, income.getCreationUser());
			json.put(MSG.MODIFICATION_DATE, income.getModificationDate());
			json.put(MSG.MODIFICATION_USER, income.getModificationUser());
		}
		return json;
	}

	public static JSONObject  incomeDetailToJSON(IncomeDetail incomeDetail){
		JSONObject json = new JSONObject();
		if(incomeDetail != null){
			json.put(MSG.ID, incomeDetail.getId());
			json.put(MSG.DOMAIN, incomeDetail.getDomain());
			json.put(MSG.PROJECT, incomeDetail.getProject());
			json.put(MSG.INCOME, incomeDetail.getIncome().getId());
			json.put(MSG.LINE, incomeDetail.getLine());
			json.put(MSG.ITEM, incomeDetail.getItem().getId());
			json.put(MSG.DESCRIPTION, incomeDetail.getDescription());
			json.put(MSG.QUANTITY, incomeDetail.getQuantity());
			json.put(MSG.PRICE, incomeDetail.getPrice());
			json.put(MSG.PURCHASE_DETAIL, incomeDetail.getPurchaseDetail());
			json.put(MSG.DISCOUNT_EXPR, incomeDetail.getDiscountExpression());
			
			json.put(MSG.CREATION_DATE, incomeDetail.getCreationDate());
			json.put(MSG.CREATION_USER, incomeDetail.getCreationUser());
			json.put(MSG.MODIFICATION_DATE, incomeDetail.getModificationDate());
			json.put(MSG.MODIFICATION_USER, incomeDetail.getModificationUser());
		}
		return json;
	}
	
	public static JSONObject  incomeDetailFullToJSON(IncomeDetail incomeDetail){
		JSONObject json = new JSONObject();
		if(incomeDetail != null){
			json.put(MSG.ID, incomeDetail.getId());
			json.put(MSG.DOMAIN, incomeDetail.getDomain());
			json.put(MSG.PROJECT, incomeDetail.getProject());
			json.put(MSG.INCOME,
					new JSONObject()
					.put(MSG.ID, incomeDetail.getIncome().getId())
					.put(MSG.REGISTRY, new JSONObject()
										.put(MSG.ID, incomeDetail.getIncome().getSupplier2().getId())
										.put(MSG.NAME, incomeDetail.getIncome().getSupplier2().getName()))
					.put(MSG.REFERENCE_CODE, incomeDetail.getIncome().getReferenceCode())
					.put(MSG.ISSUE_DATE, AonDateUtils.dateTimeFormat(incomeDetail.getIncome().getIssueDate())));
			json.put(MSG.LINE, incomeDetail.getLine());
			json.put(MSG.ITEM, incomeDetail.getItem().getId());
			json.put(MSG.DESCRIPTION, incomeDetail.getDescription());
			json.put(MSG.QUANTITY, incomeDetail.getQuantity());
			json.put(MSG.PRICE, incomeDetail.getPrice());
			json.put(MSG.PURCHASE_DETAIL, incomeDetail.getPurchaseDetail());
			json.put(MSG.DISCOUNT_EXPR, incomeDetail.getDiscountExpression());
			
			json.put(MSG.CREATION_DATE, incomeDetail.getCreationDate());
			json.put(MSG.CREATION_USER, incomeDetail.getCreationUser());
			json.put(MSG.MODIFICATION_DATE, incomeDetail.getModificationDate());
			json.put(MSG.MODIFICATION_USER, incomeDetail.getModificationUser());
		}
		return json;
	}
}
