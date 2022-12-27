package net.aonsolutions.aon.api.servlet;
import java.util.LinkedList;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.json.AuthJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.UserJSON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Properties.UserProperties;
import com.esferalia.aon.occam.api.model.aonsolutions.AonRole;
import com.esferalia.aon.occam.api.model.aonsolutions.UserAppRole;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.security.UserType;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.api.ewok.AonApiData;

@WebServlet(name = "AonUsersServlet", urlPatterns = {"/ms/api/users/*"})
public class UsersServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(UsersServlet.class.getName());

	public static final String USERS = "/";
    public static final String USER = "/:id";

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        get(req, resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        get(req, resp);
    }

    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp) {
        put(req, resp);
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        delete(req, resp);
    }

    private void get(HttpServletRequest req, HttpServletResponse resp) {
        LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
        try {
            AonApiData api = initialize(req);

            Object object = new AonRouting(api)
                    .addRoute(USERS, UsersServlet::getUsers)
                    .addRoute(USER, UsersServlet::getUser)
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
                    .addRoute(USERS, UsersServlet::saveUser)
                    .addRoute(USER, UsersServlet::saveUser)
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
                    .addRoute(USER, UsersServlet::deleteUser)
                    .apply();

            response(req, resp, object);
        } catch (Exception e) {
            error(req, resp, e);
        }
    }

    private static JSONArray getUsers(AonApiData api) {
        return UserJSON.toJSON(
            AON.getUserStream(api.getDomain(), api.getUser(),
                    f -> userFilter(api, f), api.getOptions()));
    }
    
    private static JSONObject getUser(AonApiData api) {
        return UserJSON.toJSON(
            AON.getUser(api.getDomain(), api.getUser(),
                    f -> userFilter(api, f)));
    }
    
    private static JSONObject saveUser(AonApiData api) {
        JSONObject authJSON = JsonUtils.getJSONObject(api.getData(), IJsonNames.AUTH);
        Auth auth = AON_SOLUTIONS.saveAuth(AuthJSON.fromJSON(authJSON));
        
        JSONObject userJSON = JsonUtils.getJSONObject(api.getData(), IJsonNames.USER);
        User user = UserJSON.fromJSON(userJSON).setAuth(auth.getUuid());
//        user = AON.saveUser(api.getDomain(), api.getUser(), user);
//        
//        
//        setUserapp
//        if(api.getDomain().isChild() || api.getDomain().isStandalone()) {
//            saveTaskHolder(api, user);
//        }
        
        
        return new JSONObject();
    }
    
    private static JSONObject deleteUser(AonApiData api) {
          
// TODO
//        if(api.getData().opt("user") != null) {
//            Integer userId = api.getData().optInt("user");
//            User user = AON.getUser(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getIdProperty().eq(userId));
//            SECURITY.delete(api.getDomain(), api.getUser().getLogin(), user);
//        }
        return new JSONObject();
    }	
	
	private static Filter userFilter(AonApiData api, UserProperties f) {
		JSONObject params = api.getData();
		
		Filter filter = f.getDomainProperty().eq(api.getDomain().getId());
		
		if(!api.getDomain().isParent() && params.opt("filter") != null 
				&& params.optString("filter").equals("entorno")) {
			filter = f.getDomainProperty().eq(api.getDomain().getParentId());
			if(api.getDomain().getScope() != null) {
				filter = filter.and(f.getScopeProperty().eq(api.getDomain().getScope()));
			}
		} else if(params.opt("filter") != null &&
				params.optString("filter").equals("shared")) {
			filter = f.getDomainProperty().eq(api.getDomain().getId())
					.and(f.getSharedProperty().eq((byte)1));
		} else if(params.opt("filter") != null &&
				params.optString("filter").equals("all")) {
			
			Filter all = f.getDomainProperty().eq(api.getDomain().getParentId());
			
			if(api.getDomain().getScope() != null) {
				all = all.and(f.getScopeProperty().eq(api.getDomain().getScope()));
			}
			
			filter = filter.or(all);
		}
		
		if(!AonStringUtils.isBlank(params.optString(IJsonNames.VALUE))) {
			String value = params.optString(IJsonNames.VALUE);
			Filter valueFilter = f.getLoginProperty().like("%" + value + "%")
					.or(f.getNameProperty().like("%" + value + "%"));
			filter = filter.and(valueFilter);
		}
		
		if(params.opt(IJsonNames.TYPE) != null) {
			filter = filter.and(f.getTypeProperty().eq(UserType.safeValueOf(params.getString(IJsonNames.TYPE)).value()));
		}
		
		if(params.opt(IJsonNames.WORKGROUP) != null) {
			filter = filter.and(f.getWorkgroupProperty().eq(params.optInt(IJsonNames.WORKGROUP)));
		}
		
		if(params.opt(IJsonNames.ID) != null) {
			filter = filter.and(f.getIdProperty().eq(JsonUtils.getInteger(params, IJsonNames.ID)));
		}
		
		
		if(params.opt("task_holder_empty") != null) {
			Filter newFilter = f.getTaskHolderProperty().isNull()
			.or(f.getTaskHolderActiveProperty().eq((byte)0))
			.or(f.getTaskHolderDomainProperty().eq(api.getDomain().getParentId()));
			
			filter = filter.and(newFilter);
		}
		
		return filter;
	}
	
	private JSONObject setUserAppRole(AonApiData api, User user){
        Domain domain = api.getDomain();
        String login = api.getUser().getLogin();
        Integer userId = user != null && user.getId() != null 
                ? user.getId() : JsonUtils.getInteger(api.getData(), IJsonNames.ID);

        LinkedList<AonRole> aRoles = AON_SOLUTIONS.getUserAppRole(domain.getName(), domain.getId(), "", f -> f.getUserIdProperty().eq(userId))
                .map(r -> r.getRole()).collect(Collectors.toCollection(LinkedList::new));
        
        JSONArray roles = JsonUtils.getJSONArray(api.getData(), "roles");
        LinkedList<AonRole> tRoles = new LinkedList<AonRole>();
        for(Integer i = 0; i < roles.length(); i++) {
            tRoles.add(AonRole.safeValueOf(roles.optString(i)));
        }

        AonRole.stream().forEach(role -> {  
            if(aRoles.contains(role) && !tRoles.contains(role)) {
                AON_SOLUTIONS.deleteUserAppRole(domain.getName(), domain.getId(), login, f -> 
                    f.getDomainProperty().eq(domain.getId())
                    .and(f.getUserIdProperty().eq(userId))
                    .and(f.getRoleProperty().eq(role.value())));
            }
            if(!aRoles.contains(role) && tRoles.contains(role)) {
                AON_SOLUTIONS.insertUserAppRole(api.getDomain().getName(), api.getDomain().getId(), "", new UserAppRole()
                        .setApp(null)
                        .setDomain(api.getDomain().getId())
                        .setRole(role)
                        .setUser(userId));
            }
        });
        return new JSONObject();
    }
}
