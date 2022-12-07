package net.aonsolutions.aon.api.servlet.warehouse;

import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.WarehouseJSON;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.WarehouseProperties;

import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;
import net.aonsolutions.aon.api.servlet.AonRouting;

@WebServlet(name = "AonApiWarehousesServlet", urlPatterns = {"/ms/api/warehouses/*"})
public class WarehousesServlet extends AonApiHttpServlet{
    private static final long serialVersionUID = 1L;
    
    private static final Logger LOGGER  = Logger.getLogger(WarehousesServlet.class.getName());
    
    public static final String WAREHOUSES = "/";
    public static final String WAREHOUSE = "/:id";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        get(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        put(req, resp);
    }
    
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        put(req, resp);
    }
    
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        delete(req, resp);
    }
    
    private void get(HttpServletRequest req, HttpServletResponse resp) {
        LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
        try {
            AonApiData api = initialize(req);
            
            Object object = new AonRouting(api)
                .addRoute(WAREHOUSES, WarehousesServlet::getWarehouses)
                .addRoute(WAREHOUSE, WarehousesServlet::getWarehouse)
                .apply();
            
            response(req, resp, object);
        } catch (Exception e) {
            error(req, resp, e);
        }
    }
    
    private void put(HttpServletRequest req, HttpServletResponse resp) {
        LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
        try {
            AonApiData api = initialize(req);
            
            Object object = new AonRouting(api)
                .addRoute(WAREHOUSES, WarehousesServlet::saveWarehouse)
                .addRoute(WAREHOUSE, WarehousesServlet::saveWarehouse)
                .apply();
            
            response(req, resp, object);
        } catch (Exception e) {
            error(req, resp, e);
        }
    }
    
    private void delete(HttpServletRequest req, HttpServletResponse resp) {
        LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
        try {
            AonApiData api = initialize(req);
            
            Object object = new AonRouting(api)
                .addRoute(WAREHOUSE, WarehousesServlet::deleteWarehouse)
                .apply();
            
            response(req, resp, object);
        } catch (Exception e) {
            error(req, resp, e);
        }
    }
    
    public static JSONArray getWarehouses(AonApiData api) {
        return WarehouseJSON.toJSON(AON.getWarehouseStream(
                api.getDomain().getName(),api.getDomain().getId(), api.getUser().getLogin(),
                f -> warehouseFilter(api, f)));
    }
    
    public static JSONObject getWarehouse(AonApiData api) {
        // TODO
        return new JSONObject();
    }
    
    public static JSONObject saveWarehouse(AonApiData api) {
        // TODO
        return new JSONObject();
    }
    
    public static JSONObject deleteWarehouse(AonApiData api) {
        // TODO
        return new JSONObject();
    }
    
    private static Filter warehouseFilter(AonApiData api, WarehouseProperties f) {
        Filter filter = f.getDomainProperty().eq(api.getDomain().getId()) ;
        return filter;
    }
    
}
