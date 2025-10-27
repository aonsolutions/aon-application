package net.aonsolutions.aon.api.servlet.warehouse;

import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.StockJSON;
import com.esferalia.aon.occam.api.model.IJsonNames;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;
import net.aonsolutions.aon.api.servlet.AonRouting;

@WebServlet(name = "AonApiStockServlet", urlPatterns = {"/ms/api/stock/*"})
public class StockServlet extends AonApiHttpServlet {
	
    private static final long serialVersionUID = 1L;
    
    private static final Logger LOGGER  = Logger.getLogger(StockServlet.class.getName());
    
    public static final String ITEM_STOCK = "/item/:id";


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        get(req, resp);
    }
    
    private void get(HttpServletRequest req, HttpServletResponse resp) {
        LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
        try {
            AonApiData api = initialize(req);
            
            Object object = new AonRouting(api)
                .addRoute(ITEM_STOCK, StockServlet::getItemStock)
                .apply();
            
            response(req, resp, object);
        } catch (Exception e) {
            error(req, resp, e);
        }
    }
    
    public static JSONArray getItemStock(AonApiData api) {
		JSONObject vars = JsonUtils.getJSONObject(api.getData(), IJsonNames.VARIABLES);
		Integer itemId = JsonUtils.getInteger(vars, IJsonNames.ID);
		return StockJSON.toJSON(AON.getStockStream(api.getOccam(), f -> 
			f.getDomainProperty().eq(api.getDomain().getId())
			.and(f.getItemProperty().eq(itemId))));
    }
    
    public static JSONObject movePackageStock(AonApiData api) {
    	Integer item = JsonUtils.getInteger(api.getData(), IJsonNames.ITEM);
    	Integer sourceWarehouse = JsonUtils.getInteger(api.getData(), IJsonNames.SOURCE_WAREHOUSE);
    	Integer destinyWarehouse = JsonUtils.getInteger(api.getData(), IJsonNames.DESTINY_WAREHOUSE);
    	AON.movePackageStock(api.getOccam(), item, sourceWarehouse, destinyWarehouse);
        return new JSONObject();
    }
    
}
