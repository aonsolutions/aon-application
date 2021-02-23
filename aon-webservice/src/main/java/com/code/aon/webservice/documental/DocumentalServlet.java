package com.code.aon.webservice.documental;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;
import java.util.logging.Logger;

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
import com.esferalia.aon.occam.api.model.Properties.AttachProperties;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RattachTag;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.registry.Category;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.CategoryType;
import com.esferalia.aon.occam.api.model.type.TagType;
import com.esferalia.aon.watson.util.AonStringUtils;

@SuppressWarnings("serial")
@WebServlet(name = "DocumentalServlet", urlPatterns = { "/ms/api/attachment/*",
														"/aon_gwt_aio/ms/attachment/*",
														"/aon_gwt_fiscal/ms/attachment/*"})
public class DocumentalServlet extends HttpServlet{
	
	private static final Logger LOGGER  = Logger.getLogger(DocumentalServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("Documental Servlet - GET METHOD");
		String[] pathInfo = req.getPathInfo().split("/");
		String domainName = pathInfo[1]; 
		String userName = pathInfo[2];

		Integer domainId = Integer.parseInt(req.getParameter(MSG.DOMAIN));

		Domain domain = AON.getDomain(domainName, domainId, userName);
				
		Object object = new Object();
		JSONObject meta = new JSONObject();

		switch (pathInfo[3]) {
		case "file":
			object = getAttachJSON(domain, userName, Integer.parseInt(pathInfo[4]));
			break;
		case "files":
			object = getAttachJSON(domain, userName, req.getParameterMap());
			break;
		case "certificates":
			object = getCertificateAttachJSON(domain, userName);
			break;
		case "quality":
			object = getQualityImagesJSON(domain, userName, req.getParameter(MSG.ID));
			break;
		case "category":
			object = getCategoryJSON(domain, userName);
			break;
		case "tag":
			object = getTagJSON(domain, userName);
			break;
		case "scope":
			object = getScopeJSON(domain, userName);
			break;
		default:
			break;
		}				
			
		Utils.giveBack(req, resp, object, meta);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("Documental Servlet - POST METHOD");
		
		JSONObject json = Utils.getRequestJSON(req);
		
		String[] pathInfo = req.getPathInfo().split("/");
		String userName = pathInfo[2];
		String domainName = pathInfo[1]; 
		Domain domain = AON.getDomain(domainName, 1, userName, f->f.getNameProperty().eq(domainName));
		
		if(pathInfo.length > 3){				
			Object object = new Object();
			switch (pathInfo[3]) {
			case "remove": case "delete":
				object = removeAttach(domain, userName, json);
				break;
			case "file":
				object = updateAttachJSON(domain, userName, Integer.parseInt(pathInfo[4]), json);
				break;
			case "files":
				object = updateFiles(domain, userName, json);
				break;
			case "category":
				if("create".equals(pathInfo[4])) {
					object = createCategory(domain, userName, json);
				} else if("edit".equals(pathInfo[4])) {
					object = editCategory(domain, userName, Integer.parseInt(pathInfo[5]), json);
				} else if("delete".equals(pathInfo[4])) {
					object = deleteCategory(domain, userName, Integer.parseInt(pathInfo[5]));
				}
				break;
			case "tag":
				if("create".equals(pathInfo[4])) {
					object = createTag(domain, userName, json);
				} else if("edit".equals(pathInfo[4])) {
					object = editTag(domain, userName, Integer.parseInt(pathInfo[5]), json);
				} else if("delete".equals(pathInfo[4])) {
					object = deleteTag(domain, userName, Integer.parseInt(pathInfo[5]));
				}
				break;
			default:
				break;
			}
			
			resp.setContentType("application/json;charset=UTF-8");
			Utils.addCorsHeader(resp);
			PrintStream os = new PrintStream(resp.getOutputStream(), false, "UTF-8");
			os.println(object.toString());
			os.flush();
			os.close();
		}
	}
	
	private JSONObject removeAttach(Domain domain, String login, JSONObject json) {
		JSONArray array = json.getJSONArray("id");
		AttachType attachType = AttachType.getAttachType(json.getString("attach_type"));
		Integer[] ids = new Integer[array.length()];
		for(Integer i = 0; i < array.length(); i++) {
			Integer id = array.getInt(i);
			ids[i] = id;
			if(AttachType.REGISTRY.equals(attachType)) {
				AON.deleteRegistryAttachTag(domain.getName(), domain.getId(), login, id);
			}
		}
		AON.deleteAttach(domain.getName(), domain.getId(), login, f -> f.getIdProperty().in(ids), attachType);
		return json;
	}
	
	private JSONArray getAttachJSON(Domain domain, String login, Map<String, String[]> filterMap) {
		JSONArray array = new JSONArray();
		
		AON.getDocumentalAttachStream(domain.getName(), domain.getId(), login, 
				f -> attachFilter(domain, filterMap, f),AttachType.REGISTRY, false).forEach(a -> {
			array.put(ToJSON.attachToJSON(a));
		});
		return array;
	}
	
	private JSONObject getAttachJSON(Domain domain, String login, Integer id) {
		return ToJSON.attachToJSON(AON.getDocumentalAttachStream(domain.getName(), domain.getId(), login,
				f -> f.getIdProperty().eq(id), AttachType.REGISTRY, false).findFirst().orElse(new Attach()));
	}
	
	private JSONObject updateFiles(Domain domain, String login, JSONObject json) {
		JSONArray documentArray = json.getJSONArray("documents");
		Integer scopeId = json.opt("scope") != null && !AonStringUtils.isEmpty(json.optString("scope"))? json.optInt("scope") : null;
		Integer categoryId = json.opt("category") != null && !AonStringUtils.isEmpty(json.optString("category"))? json.optInt("category") : null;
		RegistryAttachmentType rat = RegistryAttachmentType.CORPORATE_IDENTITY;
		if(json.opt("type") != null) { 
			String type = json.optString("type");
			if(type.equalsIgnoreCase("asesor")) {
				rat = RegistryAttachmentType.DOCUMENTAL_ASESOR;
			} else if(type.equalsIgnoreCase("employee")) {
				rat = RegistryAttachmentType.DOCUMENTAL_EMPLOYEE;
			}
		}
		for (int i = 0; i < documentArray.length(); i++) {
			JSONObject doc = documentArray.getJSONObject(i);
			Integer attachId = doc.getInt("id");
			if(scopeId != null || categoryId != null) {
				Attach attach = AON.getAttach(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(attachId), AttachType.REGISTRY, true);
				attach.setCategory(categoryId);
				attach.setScope(scopeId);
				attach.setType(rat.value());
				AON.updateAttach(domain.getName(), domain.getId(), login, attach);				
			}
			
			if(json.opt("tag") != null && !AonStringUtils.isEmpty(json.optString("tag"))) {
				Integer tagId = json.optInt("tag");
	        	AON.save(domain, login, new RattachTag()
	        			.setDomain(domain.getId())
	        			.setRattach(attachId)
	        			.setTag(new Tag().setId(tagId)));
			}
		}
		return new JSONObject();
	}


	private JSONObject updateAttachJSON(Domain domain, String login, Integer id, JSONObject json) {
		Attach attach = AON.getAttach(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(id), AttachType.REGISTRY, true);
		attach.setDescription(json.getString("name"));
		try {
			attach.setConfidential(json.optBoolean("confidential"));
		}catch (Exception e) {
			attach.setConfidential(Boolean.toString(true).equals(json.getString("confidential")));
		}
		
		if(json.opt("type") != null) { 
			String type = json.optString("type");
			if(type.equalsIgnoreCase("asesor")) {
				attach.setType(RegistryAttachmentType.DOCUMENTAL_ASESOR.value());
			} else if(type.equalsIgnoreCase("employee")) {
				attach.setType(RegistryAttachmentType.DOCUMENTAL_EMPLOYEE.value());
			} else attach.setType(RegistryAttachmentType.CORPORATE_IDENTITY.value());
		}
		
		if(json.opt("category") != null) { 
			Integer category;
			try {
				category = json.getInt("category");
			}catch (Exception e) {
				String categoryStr = json.getString("category");
				category = !"".equals(categoryStr) ?  Integer.parseInt(categoryStr) : null;
			}
			attach.setCategory(category);
		}
		if(json.opt("scope") != null) {
			Integer scope;
			try {
				scope = json.getInt("scope");
			}catch (Exception e) {
				String scopeStr = json.getString("scope");
				scope = !"".equals(scopeStr) ?  Integer.parseInt(scopeStr) : null;
			}
			attach.setScope(scope);
		}
		if(json.opt("tag") != null || json.opt("tags") != null) {
			try {
				JSONArray arr = json.optJSONArray("tags");
				for (Integer index = 0; index < arr.length(); index++) {
					Integer tagId =	arr.getInt(index);
					AON.insertRegistryAttachTag(domain.getName(), domain.getId(), login, attach.getId(), tagId);
				}
			} catch (Exception e) {
				String tagStr = json.getString("tag");
				String[] tags = tagStr.split(",");
				AON.deleteRegistryAttachTag(domain.getName(), domain.getId(), login, attach.getId());
				for(Integer i = 0; i< tags.length; i++) {
					if(!"".equals(tags[i])) {
						Integer tagId = Integer.parseInt(tags[i]);
						AON.insertRegistryAttachTag(domain.getName(), domain.getId(), login, attach.getId(), tagId);
					}
				}
			}

		}
		AON.updateAttach(domain.getName(), domain.getId(), login, attach);
		User user = AON.getUser(domain.getName(), domain.getId(), login);
		//SendNotification.sendGmail(domain, user, attach, false);
		return ToJSON.attachToJSON(attach);
	}
	
	private JSONArray getCertificateAttachJSON(Domain domain, String login) {
		JSONArray array = new JSONArray();
		Integer[] ds = {domain.getId(), domain.getParentId()};
		Integer[] d = {domain.getId()};
		User user = AON.getUser(domain.getName(), domain.getId(), login);
		AON.getAttachStream(domain.getName(), domain.getId(), login, 
				f -> f.getDomainProperty().in(domain.getParentId() != null && user.getDomain().equals(domain.getParentId())? ds : d)
				.and(f.getTypeProperty().eq(RegistryAttachmentType.DIGITAL_CERTIFICATE.value())
				.page(1)
				.perPage(30)
			), AttachType.REGISTRY, false).forEach(a -> {
				array.put(ToJSON.attachToJSON(a));
			});
		
		return array;
	}
	
	private JSONArray getQualityImagesJSON(Domain domain, String login, String idStr) {
		Integer id = Integer.parseInt(idStr);
		JSONArray array = new JSONArray();
		AON.getAttachStream(domain.getName(), domain.getId(), login, 
				f -> f.getDomainProperty().eq(domain.getId())
				.and(f.getSourceTypeProperty().eq((byte)0)).and(f.getSourceBatchProperty().eq(id))
				.page(1)
				.perPage(30)
			, AttachType.DATA, false).forEach(a -> {
				JSONObject json = ToJSON.attachToJSON(a);
				String url = "/aon_gwt_aio/ms/image_servlet?type=data&id="+ a.getId() +"&domain="+ domain.getName() +"&login="+ login;
				json.put("attach_type", "data");
				json.put("url", url);
				array.put(json);
			});
		return array;
	}
	
	private JSONArray getCategoryJSON(Domain domain, String login) {
		JSONArray array = new JSONArray();
		if(!domain.isParent() && domain.isEnableHeredity()) {
			AON.getCategoryStream(domain.getName(), domain.getId(), login, 
					f -> (f.getDomainProperty().eq(domain.getId()).or(f.getDomainProperty().eq(domain.getParentId())))
					.and(f.getTypeProperty().eq(CategoryType.REGISTRY_ATTACHMENT.value())
				)).forEach(a -> array.put(ToJSON.categoryToJSON(a)));
		}else {
			AON.getCategoryStream(domain.getName(), domain.getId(), login, 
				f -> f.getDomainProperty().eq(domain.getId())
				.and(f.getTypeProperty().eq(CategoryType.REGISTRY_ATTACHMENT.value())
			)).forEach(a -> array.put(ToJSON.categoryToJSON(a)));
		}
		return array;
	}
	
	private JSONObject createCategory(Domain domain, String login, JSONObject json) {
		Category category = new Category().setName(json.getString("name"))
				.setType(CategoryType.REGISTRY_ATTACHMENT.value())
				.setDomain(domain.getId());
		
		category = AON.insertCategory(domain.getName(), domain.getId(), login, category);
		return ToJSON.categoryToJSON(category);
	}
	
	private JSONObject editCategory(Domain domain, String login, Integer catId, JSONObject json) {
		Category cat = AON.getCategory(domain.getName(), domain.getId(), login, catId);
		cat.setName(json.getString("name"));
		AON.updateCategory(domain.getName(), domain.getId(), login, cat);
		return ToJSON.categoryToJSON(cat);
	}
	
	private JSONObject deleteCategory(Domain domain, String login, Integer catId) {
		Category category = AON.deleteCategory(domain.getName(), domain.getId(), login, catId);	
		return ToJSON.categoryToJSON(category);
	}
	
	private JSONArray getTagJSON(Domain domain, String login) {
		JSONArray array = new JSONArray();
		if(!domain.isParent() && domain.isEnableHeredity()) {
			AON.getTagStream(domain.getName(), domain.getId(), login, 
					f -> (f.getDomainProperty().eq(domain.getId()).or(f.getDomainProperty().eq(domain.getParentId())))
					.and(f.getTypeProperty().eq(TagType.RATTACH.value())
				)).forEach(a -> array.put(ToJSON.tagToJSON(a)));
		} else {
			AON.getTagStream(domain.getName(), domain.getId(), login, 
				f -> f.getDomainProperty().eq(domain.getId())
				.and(f.getTypeProperty().eq(TagType.RATTACH.value())
			)).forEach(a -> array.put(ToJSON.tagToJSON(a)));
		}
		return array;
	}
	
	private JSONObject createTag(Domain domain, String login, JSONObject json) {
		Tag tag = new Tag().setName(json.getString("name"))
				.setType(TagType.RATTACH.value())
				.setDomain(domain.getId());
		
		tag = AON.insertTag(domain.getName(), domain.getId(), login, tag);
		return ToJSON.tagToJSON(tag);
	}
	
	private JSONObject editTag(Domain domain, String login, Integer tagId, JSONObject json) {
		Tag tag = AON.getTag(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(tagId));
		tag.setName(json.getString("name"));
		AON.updateTag(domain.getName(), domain.getId(), login, tag);
		return ToJSON.tagToJSON(tag);
	}
	
	private JSONObject deleteTag(Domain domain, String login, Integer tagId) {
		AON.deleteTagRegistryAttach(domain.getName(), domain.getId(), login, tagId);
		AON.deleteTag(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(tagId));
		return ToJSON.tagToJSON(new Tag());
	}
	
	private JSONArray getScopeJSON(Domain domain, String login) {
		User user = AON.getUser(domain.getName(), domain.getId(), login);
		JSONArray array = new JSONArray();
		if(!domain.isParent() && domain.isEnableHeredity()) {
			if(user.getDomain().equals(domain.getId())) {
				AON.getUserScopeStream(domain.getName(), domain.getId(), login, user.getId(), 
						f -> f.getDomainProperty().eq(domain.getId()).or(f.getDomainProperty().eq(domain.getParentId())))
					.forEach(s -> array.put(ToJSON.scopeToJSON(s)));
			} else {
				AON.getScopeStream(domain.getName(), domain.getId(), login, f -> f.getDomainProperty().eq(domain.getId()))
					.forEach(s -> array.put(ToJSON.scopeToJSON(s)));
				AON.getUserScopeStream(domain.getName(), domain.getId(), login, user.getId(), f -> f.getDomainProperty().eq(domain.getParentId()))
					.forEach(s -> array.put(ToJSON.scopeToJSON(s)));
			}
		} else {
			if(user.getDomain().equals(domain.getId())) {
				AON.getUserScopeStream(domain.getName(), domain.getId(), login, user.getId(), f -> f.getDomainProperty().eq(domain.getId()))
				.forEach(s -> array.put(ToJSON.scopeToJSON(s)));
			} else {
				AON.getScopeStream(domain.getName(), domain.getId(), login, f -> f.getDomainProperty().eq(domain.getId()))
					.forEach(s -> array.put(ToJSON.scopeToJSON(s)));
			}
		}
		return array;
	}
	
    private Filter attachFilter(Domain domain, Map<String, String[]> filterMap, AttachProperties f) {
    	Integer domainId = filterMap.containsKey("type") && filterMap.get("type")[0].equals("parent") ?
    			domain.getParentId() : domain.getId();
    	Filter filter = f.getDomainProperty().eq(domainId);

    	if(filterMap.containsKey("type")) {
    		String type = filterMap.get("type")[0];
    		filter = filter.and("system".equals(type)
  				? f.getTypeProperty().eq(RegistryAttachmentType.SYSTEM_MESSAGE.value())
   				: f.getTypeProperty().eq(RegistryAttachmentType.CORPORATE_IDENTITY.value()));
    		if("parent".equals(type)) {
    			filter = filter.and(f.getSecurityLevelProperty().eq((byte) 0));
    		}
    	}
    	
    	if(filterMap.containsKey("description")){
    		String description = filterMap.get("description")[0];
    		filter = filter.and(f.getDescriptionProperty().like("%"+ description + "%"));
		}
    	
    	if(filterMap.containsKey("category")){
    		Filter fcategory = f.getCategoryProperty().eq(Integer.parseInt(filterMap.get("category")[0])); 
			for(Integer i = 1; i < filterMap.get("category").length ; i++){
				fcategory = fcategory.or(f.getCategoryProperty().eq(Integer.parseInt(filterMap.get("category")[i])));
			}
			filter = filter.and(fcategory);
		}
    	
    	if(filterMap.containsKey("tag")){
    		Filter ftag = f.getTagProperty().eq(Integer.parseInt(filterMap.get("tag")[0])); 
			for(Integer i = 1; i < filterMap.get("tag").length ; i++){
				ftag = ftag.or(f.getTagProperty().eq(Integer.parseInt(filterMap.get("tag")[i])));
			}
			filter = filter.and(ftag);
    	}
    	
    	if(filterMap.containsKey("scope")){
    		Filter fscope = f.getScopeProperty().eq(Integer.parseInt(filterMap.get("scope")[0])); 
			for(Integer i = 1; i < filterMap.get("scope").length ; i++){
				fscope = fscope.or(f.getScopeProperty().eq(Integer.parseInt(filterMap.get("scope")[i])));
			}
			filter = filter.and(fscope);
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
    
}
