package com.code.aon.webservice.product;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.code.aon.webservice.common.MSG;
import com.code.aon.webservice.common.Utils;
import com.code.aon.webservice.util.ToJSON;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.ItemProperties;
import com.esferalia.aon.occam.api.model.Properties.ProductProperties;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.ItemAddInfo;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductStatus;

@SuppressWarnings("serial")
@WebServlet(name = "ProductServlet", urlPatterns = { "/product/*",
													 "/aon_gwt_aio/product/*"})
public class ProductServlet extends HttpServlet{
		
	String h = "http://";
	
	public static ProductServlet getInstance(){
		return new ProductServlet();
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		System.out.println("GET METHOD");
		if(req.getServerPort() == 80) h = "http://";
		else if(req.getServerPort() == 443) h = "https://";
		String accessToken = req.getParameter("access_token");
		String[] pathInfo = req.getPathInfo().split("/");
		String userName = pathInfo[2];
		String domainName = pathInfo[1]; 
		String md5 = Utils.getMd5(userName+domainName);
		
		if(accessToken.equals(md5)){
			Domain domain = AON.getDomain(domainName, 1, userName, f->f.getNameProperty().eq(domainName));
			if(pathInfo.length > 3){
				Object object = new Object();
				JSONObject meta = new JSONObject();
				switch (pathInfo[3]) {
				case "category": // PRODUCT CATEGPRY
					if(pathInfo.length > 4){
						
					} else {// LISTA DE PRODUCT CATEGPRY
						object = getCategoryList(domain, userName);
					}
					break;
				case MSG.ITEM:
					if(pathInfo.length > 4){
						object = getItem(domain, userName, Integer.parseInt(pathInfo[4]));
					} else {
						object = getElaborableItemList(domain, userName, req.getParameterMap());
					}
					break;
				case MSG.PRODUCT:
					object = getProductList(domain, userName);
					break;
				case "products": 
					object = getProductList(domain, userName, req.getParameterMap());
					break;
				case "dataSheet":
					if(pathInfo.length >4) {
						object = getPaturpatProductInfo(domain, userName, Integer.parseInt(pathInfo[4]));
					}
				default:
					break;
				}
				
				Utils.giveBack(req, resp, object, meta);
			}
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("POST METHOD");
		
		JSONObject json = Utils.getRequestJSON(req);

		String[] pathInfo = req.getPathInfo().split("/");
		String domainName = pathInfo[1]; 
		String userName = pathInfo[2];
		Domain domain = AON.getDomain(domainName, 1, userName, f->f.getNameProperty().eq(domainName));
		if(pathInfo.length > 3){				
			Object object = new Object();
			if(MSG.ITEM.equals(pathInfo[3])){
				object = insertItem(domain, userName, json);	
			} else if("dataSheet".equals(pathInfo[3])) {
				object = insertPaturpatProductInfo(domain, userName,json);
			}
			resp.setContentType("application/json;charset=UTF-8");
			Utils.addCorsHeader(resp);
			PrintStream os = new PrintStream(resp.getOutputStream(), false, "UTF-8");
			os.println(object.toString());
			os.flush();
			os.close();
		}
	}

    private JSONArray getCategoryList(Domain domain, String login){
    	JSONArray array = new JSONArray();
    	AON.getProductCategoryStream(domain.getName(), domain.getId(), login,
    			f -> f.getDomainProperty().eq(domain.getId()))
    		.forEach(pc -> array.put(ToJSON.productCategoryToJSON(pc)));
    	return array;
    }
    
    private Object getItem(Domain domain, String userName, int id) {
    	return ToJSON.itemToJSON(AON.getItem(domain.getName(), domain.getId(), userName, id));
    }
    
    private JSONArray getProductList(Domain domain, String login){
    	JSONArray array = new JSONArray();
    	AON.getProductList(domain.getName(), domain.getId(), login,
    			f -> f.getDomainProperty().eq(domain.getId())
    			.and(f.getStatusProperty().eq(ProductStatus.ACTIVE.value())))
    		.forEach(product -> {
				JSONObject json = new JSONObject();
				json.put("id", product.getId());
				json.put("name", product.getName());
				json.put("code", product.getCode());
				array.put(json);
    	});
    	return array;    	
    }
    
    private JSONArray getProductList(Domain domain, String login, Map<String, String[]> map){
    	JSONArray array = new JSONArray();
    	AON.getProductList(domain.getName(), domain.getId(), login,
    			f -> productFilter(domain, map, f))
    		.forEach(product -> {
				JSONObject json = new JSONObject();
				json.put("id", product.getId());
				json.put("name", product.getName());
				json.put("code", product.getCode());
				array.put(json);
    	});
    	return array;    	
    }
    
    private JSONArray getPaturpatProductInfo(Domain domain, String login, Integer product){
    	JSONArray array = new JSONArray();
    	JSONObject json = new JSONObject();
    	Item item = AON.getItem(domain.getName(), domain.getId(), login, f -> f.getProductProperty().eq(product)
    			.and(f.getSerialDateProperty().isNull()).and(f.getSerialNumberProperty().isNull()));
    	AON.getItemAddInfoStream(domain.getName(), domain.getId(), login, f -> f.getProductProperty().eq(product)
    			.and(f.getItemProperty().eq(item.getId())).and(f.getDomainProperty().eq(domain.getId()))
    			.and(f.getAttributeProperty().like("system_%")))
    	.forEach(i -> {
    		String attr = i.getAttribute().replace("system_", "");
    		json.put(attr, i.getValue());
    	});
    	json.put("product", product);
    	json.put("item", item.getId());
    	array.put(json);
    	return array;
    }
    
    private JSONObject insertPaturpatProductInfo(Domain domain, String login, JSONObject json) {
    	String attr = json.getString("attribute");
    	String value = json.getString("value");
    	Integer item = Integer.parseInt(json.getString("item"));
    	Integer product = Integer.parseInt(json.getString("product"));
    	
    	ItemAddInfo iai = new ItemAddInfo()
    			.setAttribute("system_" + attr)
    			.setDate(new Date())
    			.setDomain(domain.getId())
    			.setItem(item)
    			.setProduct(product)
    			.setValue(value);
    	
    	Optional<ItemAddInfo> o = AON.getItemAddInfo(domain.getName(), domain.getId(), login, f -> f.getDomainProperty().eq(domain.getId())
    			.and(f.getProductProperty().eq(iai.getProduct()))
    			.and(f.getItemProperty().eq(iai.getItem()))
    			.and(f.getAttributeProperty().eq(iai.getAttribute())));
    	if((o.isPresent())) {
    		AON.updateItemAddInfo(domain.getName(), domain.getId(), login, iai.setId(o.get().getId()));
    	} else AON.insertItemAddInfo(domain.getName(), domain.getId(), login, iai);
    	
    	return new JSONObject();
    }
    
    private Filter productFilter(Domain domain, Map<String, String[]> filterMap, ProductProperties f) {
		Filter filter = f.getDomainProperty().eq(domain.getId())
				.and(f.getStatusProperty().eq(ProductStatus.ACTIVE.value()));

		if(filterMap.containsKey("text")){
			filter = filter.and(
					f.getNameProperty().like("%" + filterMap.get("text")[0] + "%")
					.or(f.getCodeProperty().like("%" + filterMap.get("text")[0] + "%"))
				);
		}
		
		if(filterMap.containsKey("manufactured")) {
			Integer manufactured = Integer.parseInt(filterMap.get("manufactured")[0]);
			filter = filter.and(f.getManufacturedProperty().eq(manufactured.byteValue()));
		}
		
		if(filterMap.containsKey("per_page")){
			String per_page = filterMap.get("per_page")[0];
			Integer perPage = Integer.parseInt(per_page);
			filter.perPage(perPage);
		}
		if(filterMap.containsKey("page")){
			String page_str = filterMap.get("page")[0];
			Integer page = Integer.parseInt(page_str);
			filter.page(page);
		}

		return filter;
    }
    
    private JSONArray getElaborableItemList(Domain domain, String login, Map<String, String[]> map){
    	JSONArray array = new JSONArray();
    	
    	Integer[] productIds = AON.getProductList(domain.getName(), domain.getId(), login,
    			f -> elaborableProductFilter(domain, map, f))
    			.stream().mapToInt(Product::getId).boxed().toArray(Integer[]::new);
    			
    	AON.getFullItemList(domain.getName(), domain.getId(), login,
    			f -> elaborableItemFilter(domain, map, f, productIds))
    			.forEach(i -> array.put(ToJSON.itemToJSON(i)));
    	return array;
    }
    
	private Filter elaborableProductFilter(Domain domain,
			Map<String, String[]> filterMap, ProductProperties f) {
		Filter filter = f.getDomainProperty().eq(domain.getId());
		filter = filter.and(f.getManufacturedProperty().eq((byte) 1));

		if (filterMap.containsKey(MSG.DESCRIPTION)) {
			filter = filter.and(f
					.getCodeProperty()
					.like("%" + filterMap.get(MSG.DESCRIPTION)[0] + "%")
					.or(f.getNameProperty().like(
							"%" + filterMap.get(MSG.DESCRIPTION)[0] + "%")));
		}
		return filter;
	}
    
	private Filter elaborableItemFilter(Domain domain,
			Map<String, String[]> filterMap, ItemProperties f,
			Integer[] productIds) {
		Filter filter = f.getDomainProperty().eq(domain.getId());

		if (filterMap.containsKey("only_base_item")) {
			boolean isOnlyBaseItem = new Boolean(filterMap.get("only_base_item")[0]);
			if (isOnlyBaseItem)
				filter = filter.and(f.getSerialNumberProperty().isNull());
		}
		if(productIds!=null){
			filter = filter.and(f.getProductProperty().in(productIds));
		}
		return filter;
	}
    
    
    public JSONObject insertItem(Domain domain, String login, JSONObject json) {
    	Integer itemId = json.getInt("item_id");
    	String lote = json.getString("lote");
    	Item item = AON.getItem(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(itemId));
    	Integer productId = item.getProductId();
    	Item itemLote = AON.getItem(domain.getName(), domain.getId(), login, f -> f.getProductProperty().eq(productId).and(f.getSerialNumberProperty().eq(lote)));
    	if(itemLote.getId() == null) {
    		item.setBarcode(null);
    		item.setSerialNumber(lote);
    		item = AON.insertItem(domain.getName(), domain.getId(), login, item);
    	}
    	return ToJSON.itemToJSON(item);
    }
}
