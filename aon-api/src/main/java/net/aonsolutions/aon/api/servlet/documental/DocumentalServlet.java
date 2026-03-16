package net.aonsolutions.aon.api.servlet.documental;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.TagJSON;
import com.esferalia.aon.occam.api.json.invoice.FinanceJSON;
import com.esferalia.aon.occam.api.model.FBatchParams;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.Properties.AttachProperties;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.finance.FBatch;
import com.esferalia.aon.occam.api.model.finance.FBatchDetail;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.registry.Category;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.io.AonFileUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.ewok.IConstants;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;

@SuppressWarnings("serial")
@WebServlet(name = "AonApiDocumentalServlet", urlPatterns = { "/ms/api/documental/*" })
public class DocumentalServlet extends AonApiHttpServlet{
	
	private static final Logger LOGGER  = Logger.getLogger(DocumentalServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("Aon Api Documental Servlet - GET METHOD");	
		try {
			AonApiData api = initialize(req);
		
			switch (api.getPath()) {
			case "/":
				response(req, resp, getFile(api));
				break;
			case "/files":
				response(req, resp, getFiles(api));
				break;
			case "/files/sepa":
				response(req, resp, getSepaFiles(api));
				break;
			case "/file/sepa":
				response(req, resp, getSepaFile(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("Aon Api Documental Servlet - POST METHOD");
		try {
			AonApiData api = initialize(req);
			
			switch (api.getPath()) {
			case "/":
				//response(req, resp, getResponseObject());
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("Aon Api Documental Servlet - DELETE METHOD");
		try {
			AonApiData api = initialize(req);
		
			switch (api.getPath()) {
			case "/":
				//response(req, resp, getResponseObject());
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}

	private JSONObject getFile(AonApiData api) throws Exception {
		if(api.getData().opt(IConstants.ID) != null) {
			Attach attach = AON.getDocumentalAttachStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), 
					f -> f.getIdProperty().eq(api.getData().optInt(IConstants.ID)),AttachType.REGISTRY, false)
			.findFirst().orElse(new Attach());
			return attachToJSON(attach);
		} else {
			throw new Exception("No se ha especificado el identificador del documento");
		}
	}
	
	private JSONArray getFiles(AonApiData api) {
		JSONArray array = new JSONArray();
		
		Integer page = JsonUtils.getInteger(api.getData(), com.esferalia.aon.occam.api.model.IJsonNames.PAGE);
		Integer perPage = JsonUtils.getInteger(api.getData(), "per_page");
		Options options = new Options()
				.setPage(page).setPerPage(perPage);
		
		AON.getDocumentalAttachStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), 
				f -> attachFilter(api, f), AttachType.REGISTRY, false, options)
//		.sorted((o1, o2) -> o2.getDate().compareTo(o1.getDate()))
		.forEach(a -> {
			array.put(attachToJSON(a));
		});
		return array;
	}
	
	private JSONArray getSepaFiles(AonApiData api) {
		JSONArray array = new JSONArray();
		
		Integer page = JsonUtils.getInteger(api.getData(), com.esferalia.aon.occam.api.model.IJsonNames.PAGE);
		Integer perPage = JsonUtils.getInteger(api.getData(), "per_page");
		
		FBatchParams params = new FBatchParams()
				.setDomainName(api.getDomain().getName())
				.setDomain(api.getDomain().getId())
				.setType((byte)10);
		
		AON.getFBatches(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), params, perPage * (page -1), perPage)
			.forEach(a -> array.put(fbatchToJSON(a)));

		return array;
	}
	
	private JSONObject getSepaFile(AonApiData api) {
		Integer fbatch = JsonUtils.getInteger(api.getData(), "fbatch");
		FBatch fbatchObj = AON.getFBatch(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), fbatch);
		return fbatchToJSON(fbatchObj);
	}

	private Filter attachFilter(AonApiData api, AttachProperties f) {
		DomainUserRoles dur = SECURITY.getDomainUserRoles(api.getDomain(), api.getUser().getLogin(), api.getUser().getId());
		
		Integer[] scopes = null;
		try {
			scopes = AON.getUserScopes(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), api.getUser().getId());
			if(api.getUser().getDomain().getId().equals(api.getDomain().getParentId())) {
				Integer[] scopes2 = AON.getScopeStream(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), 
						r -> r.getDomainProperty().eq(api.getDomain().getId())).map(r -> r.getId()).toArray(Integer[]::new);
				
				Integer[] scopes3 = AON.getUserScopes(api.getDomain().getName(), api.getDomain().getParentId(), api.getUser().getLogin(), api.getUser().getId());
				
				if(scopes == null && scopes3 == null) {
					scopes = scopes2;
				} else if(scopes == null) {
					scopes = new Integer[scopes2.length + scopes3.length];
					for (Integer i = 0; i< scopes2.length; i++) {
						scopes[i] = scopes2[i];
					}
					for (Integer i = 0; i< scopes3.length; i++) {
						scopes[i + scopes2.length] = scopes3[i];
					}
				}

			}
		} catch (Exception e) {
			scopes = null;
		}
		
		Integer domainId = api.getDomain().getId();
		if(api.getData().opt("parent") != null && api.getData().optBoolean("parent") && api.getDomain().getParentId() != null) {
			domainId = api.getDomain().getParentId();
		}
    	
		Filter filter = f.getDomainProperty().eq(domainId);
    	
    	if(scopes != null) {
    		filter = filter.and(f.getScopeProperty().in(scopes).or(f.getScopeProperty().isNull()));
    	} else filter = filter.and(f.getScopeProperty().isNull());

    	if(api.getData().opt("type") != null) {
        	if("system".equalsIgnoreCase(api.getData().optString("type"))) {
        		filter = filter.and(f.getTypeProperty().eq(RegistryAttachmentType.SYSTEM_MESSAGE.value()));
        	} else if("enterprise".equalsIgnoreCase(api.getData().optString("type"))) {
        		filter = filter.and(f.getTypeProperty().eq(RegistryAttachmentType.CORPORATE_IDENTITY.value()));
        	} else if("employee".equalsIgnoreCase(api.getData().optString("type"))) {
        		filter = filter.and(f.getTypeProperty().eq(RegistryAttachmentType.DOCUMENTAL_EMPLOYEE.value()));
        	} else if("asesor".equalsIgnoreCase(api.getData().optString("type"))) {
        		filter = filter.and(f.getTypeProperty().eq(RegistryAttachmentType.DOCUMENTAL_ASESOR.value()));
        	} else if("all".equalsIgnoreCase(api.getData().optString("type"))) {
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
    	
    	if(api.getData().opt(IJsonNames.DESCRIPTION) != null) {
    		filter = filter.and(f.getDescriptionProperty().like("%"+ api.getData().optString(IJsonNames.DESCRIPTION) + "%"));
		}
    	
    	if(api.getData().opt(IJsonNames.CATEGORY) != null) {
    		// TODO FILTRO CATEGORÍA MÚLTIPLE
    		filter = filter.and(f.getCategoryProperty().eq(api.getData().optInt(IJsonNames.CATEGORY)));
    	}
    	
    	if(api.getData().opt(IJsonNames.TAG) != null) {
    		// TODO FILTRO ETIQUETAS MÚLTIPLE
    		filter = filter.and(f.getTagProperty().eq(api.getData().optInt(IJsonNames.TAG)));
    	}
    	
		if(api.getData().opt(IJsonNames.PAGE) != null){
			filter.page(api.getData().optInt(IJsonNames.PAGE));
		}

		if(api.getData().opt("per_page") != null){
			filter.perPage(api.getData().optInt("per_page"));
		}
		
		return filter;
    }
	
	private JSONObject fbatchToJSON(FBatch fBatch) {
		return new JSONObject()
				.put(com.esferalia.aon.occam.api.model.IJsonNames.ID, fBatch.getId())
				.put(com.esferalia.aon.occam.api.model.IJsonNames.DOMAIN, fBatch.getDomain())
				.put(com.esferalia.aon.occam.api.model.IJsonNames.DESCRIPTION, fBatch.getDescription())
				.put(com.esferalia.aon.occam.api.model.IJsonNames.ISSUE_DATE, AonDateUtils.simpleFormat(fBatch.getIssueDate()))
				.put(com.esferalia.aon.occam.api.model.IJsonNames.TYPE, fBatch.getType())
				.put(com.esferalia.aon.occam.api.model.IJsonNames.STATUS, fBatch.getStatus().getDescription())
				.put("bank", null == fBatch.getRbank() ? null : fBatch.getRbank().getFullName())
				.put("bank_statement_link", fBatch.getBankStatementLink())
				.put("payment", fBatch.getPayment())
				.put(com.esferalia.aon.occam.api.model.IJsonNames.SECURITY_LEVEL, fBatch.getSecurityLevel().getName())
				.put(com.esferalia.aon.occam.api.model.IJsonNames.RATTACH, fBatch.getRattach())
				.put("fbatch_details", fbatchDetailToJSON(fBatch.getBatchDetails()))
				.put(com.esferalia.aon.occam.api.model.IJsonNames.CREATION_USER, fBatch.getCreationUser())
				.put(com.esferalia.aon.occam.api.model.IJsonNames.CREATION_DATE, AonDateUtils.simpleFormat(fBatch.getCreationDate()))
				.put(com.esferalia.aon.occam.api.model.IJsonNames.MODIFICATION_USER, fBatch.getModificationUser())
				.put(com.esferalia.aon.occam.api.model.IJsonNames.MODIFICATION_DATE, AonDateUtils.simpleFormat(fBatch.getModificationDate()))
				;
	}
	
	private JSONArray fbatchDetailToJSON(List<FBatchDetail> fbatchDetails) {
		JSONArray array = new JSONArray();
		fbatchDetails.forEach(fbatchDetail -> array.put(fbatchDetailToJSON(fbatchDetail)));
		return array;
	}

	private JSONObject fbatchDetailToJSON(FBatchDetail fbatchDetail) {
		return new JSONObject()
				.put(com.esferalia.aon.occam.api.model.IJsonNames.ID, fbatchDetail.getId())
				.put(com.esferalia.aon.occam.api.model.IJsonNames.DOMAIN, fbatchDetail.getDomain())
				.put("fbatch", fbatchDetail.getFbatch())
				.put("finance", FinanceJSON.toJSON(fbatchDetail.getFinance()))
				.put(com.esferalia.aon.occam.api.model.IJsonNames.AMOUNT, fbatchDetail.getAmount())
				.put(com.esferalia.aon.occam.api.model.IJsonNames.STATUS, fbatchDetail.getStatus())
				.put(com.esferalia.aon.occam.api.model.IJsonNames.CREATION_USER, fbatchDetail.getCreationUser())
				.put(com.esferalia.aon.occam.api.model.IJsonNames.CREATION_DATE, AonDateUtils.simpleFormat(fbatchDetail.getCreationDate()))
				.put(com.esferalia.aon.occam.api.model.IJsonNames.MODIFICATION_USER, fbatchDetail.getModificationUser())
				.put(com.esferalia.aon.occam.api.model.IJsonNames.MODIFICATION_DATE, AonDateUtils.simpleFormat(fbatchDetail.getModificationDate()))
				
				;
	}

	public static JSONObject attachToJSON(Attach attach){	
		JSONArray tagArray = new JSONArray();
		if(attach.getTagList() != null)
			attach.getTagList().stream().forEach(r -> {
				tagArray.put(TagJSON.toJSON(r));
			});
		
		JSONObject f = new JSONObject();
	    String str = "domain="+ attach.getDomain().getId() + "&id=" + attach.getId() + "&attach_type=registry";
	    String result = Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));
	    String url =  "ms/download_attachment/"  + attach.getDomain().getName() + "/" + attach.getCreationUser() + "/" +  result;
	    f.put("url", url);
	    f.put(IJsonNames.TYPE, attach.getMimeType().getName());
			
	    String type = "enterprise";
	    if(RegistryAttachmentType.DOCUMENTAL_ASESOR.value() == attach.getType()){
	    	type = "asesor";
	    } else if(RegistryAttachmentType.DOCUMENTAL_EMPLOYEE.value() == attach.getType()) {
	    	type = "employee";
	    }
		return new JSONObject()
			.put(IJsonNames.ID, attach.getId())
			.put(IJsonNames.DOMAIN, attach.getDomain().getId())
			.put(IJsonNames.CATEGORY, attach.getFullCategory() != null ? categoryToJSON(attach.getFullCategory()):new JSONObject())
			.put(IJsonNames.SCOPE, attach.getFullCategory() != null ? scopeToJSON(attach.getFullScope()): new JSONObject())
			.put(IJsonNames.DATE, AonDateUtils.simpleFormat(attach.getDate()))
			.put(IJsonNames.CONFIDENTIAL, attach.getConfidential())
			.put(IJsonNames.SIZE, AonFileUtils.byteCountToDisplaySize( attach.getDparentId() != null ? Long.parseLong( attach.getDparentId()): 0))
			.put(IJsonNames.TITLE, attach.getDescription())
			.put(IJsonNames.TAGS, tagArray)
			.put(IJsonNames.TYPE, type)
			.put(IJsonNames.FILE, f);
	}
	
	public static JSONObject categoryToJSON(Category category){	
		return new JSONObject()
			.put(IJsonNames.ID,category.getId())
			.put(IJsonNames.DOMAIN, category.getDomain())
			.put(IJsonNames.NAME, category.getName())
			.put(IJsonNames.TYPE, category.getType())
			.put(IJsonNames.SCOPE, category.getScope())
			.put(IJsonNames.URL, category.getUrl())
			.put(IJsonNames.DESCRIPTION, category.getDescription())
			.put("rattach", category.getRattach());
	}
	
	public static JSONObject scopeToJSON(Scope scope){	
		return new JSONObject()
			.put(IJsonNames.ID,scope.getId())
			.put(IJsonNames.DOMAIN, scope.getDomain())
			.put(IJsonNames.NAME, scope.getDescription());
	}
	
}
