/**
 * 
 */
package com.code.aon.common.jsf;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.http.HttpServletRequest;

import org.apache.myfaces.renderkit.html.HtmlFormRenderer;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 28/09/2007
 *
 */
public class AonFormRenderer extends HtmlFormRenderer {

	  public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
		    ResponseWriter writer = context.getResponseWriter();
		    
		    super.encodeBegin(context, component);
		    renderUniqueToken(writer, component);
		  }
		  
		  private String getRequestURI() {
		    HttpServletRequest servletRequest = 
		    	(HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		    return servletRequest.getRequestURI().substring(0, servletRequest.getRequestURI().lastIndexOf("."));
		  }
		  
		  private void renderUniqueToken(ResponseWriter writer, UIComponent component) throws IOException {
		    writer.startElement("input", component);
		    writer.writeAttribute("type", "hidden", "type");
		    writer.writeAttribute("name", "uniqueToken", "name");
		    String uniqueToken = getUniqueTokenFromSession(FacesContext.getCurrentInstance());
		    writer.writeAttribute("value", uniqueToken == null ? "" : uniqueToken, "value");
		    writer.endElement("input");
		  }
		  
		  private String getUniqueTokenFromSession(FacesContext facesContext) {
		    String token = null;
		    Map tokenMap = (Map) facesContext.getApplication().createValueBinding("#{visitedTokenMap}").getValue(facesContext);
		    
		    if (tokenMap != null)
		      token = (String) tokenMap.get(getRequestURI());
		    
		    return token;
		  }
}
