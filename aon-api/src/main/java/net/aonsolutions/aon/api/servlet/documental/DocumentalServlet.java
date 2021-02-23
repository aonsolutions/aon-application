package net.aonsolutions.aon.api.servlet.documental;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.AttachProperties;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.registry.Category;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.io.AonFileUtils;

import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;

@SuppressWarnings("serial")
@WebServlet(name = "AonApiDocumentalServlet", urlPatterns = { "/ms/api/documental/*" })
public class DocumentalServlet extends AonApiHttpServlet{
	
	private static final Logger LOGGER  = Logger.getLogger(DocumentalServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("Aon Api Documental Servlet - GET METHOD");	
		try {
			super.doGet(req, resp);
		
			switch (getPath()) {
			case "/files":
				response(req, resp, getFiles());
				break;
			default:
				throw new Exception("La ruta introducida es incorrecta.");
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("Aon Api Documental Servlet - POST METHOD");
		try {
			super.doPost(req, resp);
		
			switch (getPath()) {
			case "/":
				//response(req, resp, getResponseObject());
				break;
			default:
				throw new Exception("La ruta introducida es incorrecta.");
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("Aon Api Documental Servlet - DELETE METHOD");
		try {
			super.doDelete(req, resp);
		
			switch (getPath()) {
			case "/":
				//response(req, resp, getResponseObject());
				break;
			default:
				throw new Exception("La ruta introducida es incorrecta.");
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private JSONArray getFiles() {
		JSONArray array = new JSONArray();
		
		AON.getDocumentalAttachStream(getDomain().getName(), getDomain().getId(), getUser().getLogin(), 
				f -> attachFilter(f),AttachType.REGISTRY, false).forEach(a -> {
			array.put(attachToJSON(a));
		});
		return array;
	}
    
	private Filter attachFilter(AttachProperties f) {
		DomainUserRoles dur = SECURITY.getDomainUserRoles(getDomain(), getUser().getLogin(), getUser().getId());
		
		Integer[] scopes = null;
		try {
			scopes = AON.getUserScopes(getDomain().getName(), getDomain().getId(), getUser().getLogin(), getUser().getId());
		} catch (Exception e) {
			scopes = null;
		}
		Integer domainId = getDomain().getId();
		if(getParams().opt("parent") != null && getParams().optBoolean("parent") 
				&& getDomain().getParentId() != null) {
			domainId = getDomain().getParentId();
		}
    	
		Filter filter = f.getDomainProperty().eq(domainId);
    	
    	if(scopes != null) {
    		filter = filter.and(f.getScopeProperty().in(scopes).or(f.getScopeProperty().isNull()));
    	} else filter = filter.and(f.getScopeProperty().isNull());

    	if(getParams().opt("type") != null) {
        	if("system".equalsIgnoreCase(getParams().optString("type"))) {
        		filter = filter.and(f.getTypeProperty().eq(RegistryAttachmentType.SYSTEM_MESSAGE.value()));
        	} else if("enterprise".equalsIgnoreCase(getParams().optString("type"))) {
        		filter = filter.and(f.getTypeProperty().eq(RegistryAttachmentType.CORPORATE_IDENTITY.value()));
        	} else if("employee".equalsIgnoreCase(getParams().optString("type"))) {
        		filter = filter.and(f.getTypeProperty().eq(RegistryAttachmentType.DOCUMENTAL_EMPLOYEE.value()));
        	} else if("asesor".equalsIgnoreCase(getParams().optString("type"))) {
        		filter = filter.and(f.getTypeProperty().eq(RegistryAttachmentType.DOCUMENTAL_ASESOR.value()));
        	} else if("all".equalsIgnoreCase(getParams().optString("type"))) {
        		if(dur.isDocumentalManager()) {
            		filter = filter.and(f.getTypeProperty().eq(RegistryAttachmentType.CORPORATE_IDENTITY.value())
            			.or(f.getTypeProperty().eq(RegistryAttachmentType.DOCUMENTAL_ASESOR.value()))
            			.or(f.getTypeProperty().eq(RegistryAttachmentType.DOCUMENTAL_EMPLOYEE.value())));
            	} else if(dur.isDocumentalPortal()) {
            		filter = filter.and(f.getTypeProperty().eq(RegistryAttachmentType.CORPORATE_IDENTITY.value())
               			.or(f.getTypeProperty().eq(RegistryAttachmentType.DOCUMENTAL_EMPLOYEE.value())));
            	} else if(dur.isDocumental()) {
            		filter = filter.and(f.getTypeProperty().eq(RegistryAttachmentType.DOCUMENTAL_EMPLOYEE.value()));    		
            	} else filter = filter.and(f.getTypeProperty().isNull());
        	}
    	}
    	
    	if(!dur.isConfidential()) {
    		filter = filter.and(f.getSecurityLevelProperty().eq((byte) 0));
    	}
    	
    	if(getParams().opt("description") != null) {
    		filter = filter.and(f.getDescriptionProperty().like("%"+ getParams().optString("description") + "%"));
		}
    	
    	if(getParams().opt("category") != null) {
    		// TODO FILTRO CATEGORÍA MÚLTIPLE
    		filter = filter.and(f.getCategoryProperty().eq(getParams().optInt("category")));
    	}
    	
    	if(getParams().opt("tag") != null) {
    		// TODO FILTRO ETIQUETAS MÚLTIPLE
    		filter = filter.and(f.getTagProperty().eq(getParams().optInt("tag")));
    	}

		if(getParams().opt("per_page") != null){
			filter.perPage(getParams().optInt("per_page"));
		}
		if(getParams().opt("page") != null){
			filter.page(getParams().optInt("page"));
		}
		
		return filter;
    }
	
	public static JSONObject attachToJSON(Attach attach){	
		JSONArray tagArray = new JSONArray();
		if(attach.getTagList() != null)
			attach.getTagList().stream().forEach(r -> {
				tagArray.put(tagToJSON(r));
			});
		
		JSONObject f = new JSONObject();
	    String str = "domain="+ attach.getDomain().getId() + "&id=" + attach.getId() + "&attach_type=registry";
	    String result = Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));
	    String url =  "ms/download_attachment/"  + attach.getDomain().getName() + "/" + attach.getCreationUser() + "/" +  result;
	    f.put("url", url);
	    f.put("type", attach.getMimeType().getName());
			
	    String type = "enterprise";
	    if(RegistryAttachmentType.DOCUMENTAL_ASESOR.value() == attach.getType()){
	    	type = "asesor";
	    } else if(RegistryAttachmentType.DOCUMENTAL_EMPLOYEE.value() == attach.getType()) {
	    	type = "employee";
	    }
		return new JSONObject()
			.put("id", attach.getId())
			.put("domain", attach.getDomain().getId())
			.put("category", attach.getFullCategory() != null ? categoryToJSON(attach.getFullCategory()):new JSONObject())
			.put("scope", attach.getFullCategory() != null ? scopeToJSON(attach.getFullScope()): new JSONObject())
			.put("date", AonDateUtils.simpleFormat(attach.getDate()))
			.put("confidential", attach.getConfidential())
			.put("size", AonFileUtils.byteCountToDisplaySize( attach.getDparentId() != null ? Long.parseLong( attach.getDparentId()): 0))
			.put("title", attach.getDescription())
			.put("tags", tagArray)
			.put("type", type)
			.put("file", f);
	}
	
	public static JSONObject tagToJSON(Tag tag){	
		return new JSONObject()
			.put("id",tag.getId())
			.put("domain", tag.getDomain())
			.put("name", tag.getName())
			.put("type", tag.getType())
			.put("color", tag.getColor());
	}
	
	public static JSONObject categoryToJSON(Category category){	
		return new JSONObject()
			.put("id",category.getId())
			.put("domain", category.getDomain())
			.put("name", category.getName())
			.put("type", category.getType())
			.put("scope", category.getScope())
			.put("url", category.getUrl())
			.put("description", category.getDescription())
			.put("rattach", category.getRattach());
	}
	
	public static JSONObject scopeToJSON(Scope scope){	
		return new JSONObject()
			.put("id",scope.getId())
			.put("domain", scope.getDomain())
			.put("name", scope.getDescription());
	}
	
}
