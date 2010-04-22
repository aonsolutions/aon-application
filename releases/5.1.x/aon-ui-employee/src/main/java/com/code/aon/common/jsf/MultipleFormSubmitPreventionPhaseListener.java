/**
 * 
 */
package com.code.aon.common.jsf;

import java.util.Date;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 28/09/2007
 *
 */
public class MultipleFormSubmitPreventionPhaseListener implements PhaseListener {

	  private static final Log logger = LogFactory.getLog(MultipleFormSubmitPreventionPhaseListener.class);

	  private static final String WARNING_MESSAGE = new String("Dude, don't mess with the browser!");
	  
	  private static final String KEYVAL_AJAX4JSF_REQUEST     = "AJAXREQUEST";
	  private static final String KEYVAL_AJAXANYWHERE_REQUEST   = "aaxmlrequest";
	  
	  public PhaseId getPhaseId() {
	    return PhaseId.RESTORE_VIEW;
	  }
	  
	  public void beforePhase(PhaseEvent phaseEvent) {
	    // No-op
	  }
	  
	  public void afterPhase(PhaseEvent phaseEvent) {
	    FacesContext facesContext = phaseEvent.getFacesContext();

	        if (isRequestedByAjax4JSF(facesContext)) {
	          if (logger.isDebugEnabled())
	            logger.debug("Request is an Ajax4JSF Request. Multiple-submit-check will not be done.");
	        }
	        else if (isRequestedByAjaxAnywhere(facesContext)) {
	          if (logger.isDebugEnabled())
	            logger.debug("Request is an AjaxAnywhere Request. Multiple-submit-check will not be done.");
	        }
	        else  {
	          checkForDoubleSubmit(facesContext);
	        }
	  }

	  private String getRequestURI() {
	    HttpServletRequest servletRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
	    return servletRequest.getRequestURI().substring(0, servletRequest.getRequestURI().lastIndexOf("."));
	  }
	    
	  private boolean isRequestedByAjax4JSF(FacesContext facesContext) {
	    String ajaxReqVal = (String) facesContext.getExternalContext().getRequestParameterMap().get(KEYVAL_AJAX4JSF_REQUEST);

	    if (StringUtils.isNotEmpty(ajaxReqVal))
	      return true;
	    return false;
	  }
	  
	  private boolean isRequestedByAjaxAnywhere(FacesContext facesContext) {
	    Map requestHeaderMap = facesContext.getExternalContext().getRequestHeaderMap();
	    
	    String ajaxAnywhereReq = (String) requestHeaderMap.get(KEYVAL_AJAXANYWHERE_REQUEST);
	    if ("true".equals(ajaxAnywhereReq)) {
	      return true;
	    }
	    return false;
	  }
	  
	  private void checkForDoubleSubmit(FacesContext facesContext) {
	    Map requestParamMap = facesContext.getExternalContext().getRequestParameterMap();
	    String submittedUniqueToken = (String) requestParamMap.get("uniqueToken");

	    if (!StringUtils.isEmpty(submittedUniqueToken)) {
	      if (logger.isDebugEnabled())
	        logger.debug("submitted UniqueToken: " + submittedUniqueToken);
	      
	      if (!isInVisitedTokenList(facesContext, submittedUniqueToken)) {
	        if (logger.isWarnEnabled())
	          logger.warn("Submitted token is not in visited token list. Preventing multiple submit. Rendering response with warn message.");        
	        
	        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_FATAL, WARNING_MESSAGE, WARNING_MESSAGE);
	        facesContext.addMessage(StringUtils.EMPTY, msg);
	        
	        facesContext.renderResponse();
	      }
	      else {
	        getVisitedTokenMap(facesContext).remove(getRequestURI());
	      }
	    }

	    String newUniqueToken = getUniqueToken(facesContext);
	      
	    if (logger.isDebugEnabled())
	      logger.debug("adding token: " + newUniqueToken + " to visited token list");
	      
	    getVisitedTokenMap(facesContext).put(getRequestURI(), newUniqueToken);
	  }

	  private String getUniqueToken(FacesContext facesContext) {
	    HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
	    return request.getSession().getId() + (new Date()).getTime();
	  }

	  private boolean isInVisitedTokenList(FacesContext context, String submittedUniqueToken) {
	    return getVisitedTokenMap(context).containsValue(submittedUniqueToken);
	  }

	  private Map getVisitedTokenMap(FacesContext context) {
	    return (Map) context.getApplication().createValueBinding("#{visitedTokenMap}").getValue(context);
	  }
	} 
	 

	 

