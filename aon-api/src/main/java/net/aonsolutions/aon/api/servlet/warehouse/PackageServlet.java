package net.aonsolutions.aon.api.servlet.warehouse;

import java.util.logging.Logger;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.StockJSON;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.warehouse.Stock;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;
import net.aonsolutions.aon.api.servlet.AonRouting;

@WebServlet(name = "AonApiPackageServlet", urlPatterns = {"/ms/api/package/*"})
public class PackageServlet extends AonApiHttpServlet {
	
    private static final long serialVersionUID = 1L;
    
    private static final Logger LOGGER  = Logger.getLogger(PackageServlet.class.getName());
    
    public static final String ADD_STOCK = "/stock/add";
    public static final String MOVE_STOCK = "/stock/move";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        put(req, resp);
    }
    
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        put(req, resp);
    }
    
    
    private void put(HttpServletRequest req, HttpServletResponse resp) {
        LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
        try {
            AonApiData api = initialize(req);
            
            Object object = new AonRouting(api)
                .addRoute(ADD_STOCK, PackageServlet::addPackageStock)
                .addRoute(MOVE_STOCK, PackageServlet::movePackageStock)
                .apply();
            
            response(req, resp, object);
        } catch (Exception e) {
            error(req, resp, e);
        }
    }
    
    public static JSONObject addPackageStock(AonApiData api) {
    	Integer item = JsonUtils.getInteger(api.getData(), IJsonNames.ITEM);
    	Integer warehouse = JsonUtils.getInteger(api.getData(), IJsonNames.WAREHOUSE);
    	AON.addPackageStock(api.getOccam(), item, warehouse);
        return new JSONObject();
    }
    
    public static JSONObject movePackageStock(AonApiData api) {
    	Integer item = JsonUtils.getInteger(api.getData(), IJsonNames.ITEM);
    	Integer sourceWarehouse = JsonUtils.getInteger(api.getData(), IJsonNames.SOURCE_WAREHOUSE);
    	Integer destinyWarehouse = JsonUtils.getInteger(api.getData(), IJsonNames.DESTINY_WAREHOUSE);
    	AON.movePackageStock(api.getOccam(), item, sourceWarehouse, destinyWarehouse);
        return new JSONObject();
    }
    
}
