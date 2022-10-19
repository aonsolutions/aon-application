package net.aonsolutions.aon.api.servlet;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import com.esferalia.aon.occam.api.model.IJsonNames;

import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;

public class AonRouting {

	String path;
	AonApiData api;
	List<AonRoute> routes;
	
	public AonRouting() {
		this.routes = new LinkedList<>();	
	}

	public AonRouting(AonApiData api) {
		this.path = api.getPath();
		this.api = api;
		this.routes = new LinkedList<>();
	}
	
	public AonRouting(String path, AonApiData api) {
		this.path = path;
		this.api = api;
		this.routes = new LinkedList<>();
	}
	
	public String getPath() {
		return path;
	}
	
	public AonRouting setPath(String path) {
		this.path = path;
		return this;
	}
	
	public AonApiData getApi() {
		return api;
	}
	
	public AonRouting setApi(AonApiData api) {
		this.api = api;
		return this;
	}
	
	public List<AonRoute> getRoutes() {
		if(routes == null)
			routes = new LinkedList<>();
		return routes;
	}
	
	public AonRouting setRoutes(List<AonRoute> routes) {
		this.routes = routes;
		return this;
	}
	
	public AonRouting addRoute(String path, Function<AonApiData, Object> function) {
		getRoutes().add(new AonRoute(path, function));
		return this;
	}
	
	public Object apply() {
		for (AonRoute aonRoute : getRoutes()) {
			if(aonRoute.checkPath(getPath())) {				
				getApi().getData().put(IJsonNames.VARIABLES, aonRoute.getVariables());
				return aonRoute.getFunction().apply(getApi());
			}
		}
		throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
	}
}
