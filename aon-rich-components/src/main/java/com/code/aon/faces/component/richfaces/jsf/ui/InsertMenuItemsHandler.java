package com.code.aon.faces.component.richfaces.jsf.ui;

/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlCommandLink;

import org.ajax4jsf.component.html.HtmlAjaxCommandLink;
import org.richfaces.component.html.HtmlMenuItem;

import com.esferalia.aon.watson.util.AonStringUtils;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.TemplateClient;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagAttributeException;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagHandler;

import jakarta.el.ELException;

/**
 */
public final class InsertMenuItemsHandler extends TagHandler implements TemplateClient {
	
	
	private final String name;

    /**
     * @param config
     */
    public InsertMenuItemsHandler(TagConfig config) {
        super(config);
        TagAttribute attr = this.getAttribute("name");
        if (attr != null) {
            if (!attr.isLiteral()) {
                throw new TagAttributeException(this.tag, attr, "Must be Literal");
            }
            this.name = attr.getValue();
        } else {
            this.name = null;
        }
    }

    public void apply(FaceletContext ctx, UIComponent parent)
            throws IOException, FacesException, ELException {
        ctx.extendClient(this);
        boolean found = false;
        try {
			UIComponent dropDownMenu = new DelegateUIComponent(parent) {
				@Override
				public List<UIComponent> getChildren() {
					return new DelegateList<>(super.getChildren()) {
						@Override
						public boolean add(UIComponent e) {
							return (e instanceof HtmlCommandLink htmlCommandLink && super.add(createHtmlMenuItem(htmlCommandLink)))
//									|| (e instanceof HtmlAjaxCommandLink htmlAjaxCommandLink && super.add(createHtmlMenuItem(htmlAjaxCommandLink)))
									;
						}
					};
				}
			};
        	found = ctx.includeDefinition(dropDownMenu, this.name);
            
            
        } finally {
            ctx.popClient(this);
        }
        if (!found) {
            this.nextHandler.apply(ctx, parent);
        }
    }

    @Override
    public boolean apply(FaceletContext ctx, UIComponent parent, String name)
    		throws IOException, FacesException, FaceletException, ELException {
        if (Objects.equals(this.name, name)) {
            this.nextHandler.apply(ctx, parent);
            return true;
        }
        return false;
    }
    
    private static HtmlMenuItem  createHtmlMenuItem(HtmlCommandLink htmlCommandLink) {
    	
		//		<aon:htmlCommandLink id="itemCatalogueReport-excel-report" value="&#160;" 
		//				target="_new" action="#{report.onExecute}"
		//				accesskey="#{bundle.aon_key_print}"
		//				styleClass="aon-finding-toolbar-item aon-icon-excel">
		//				<f:param name="reportKey" value="itemCatalogueReport" />
		//				<f:param name="outputFormat" value="MS Excel" />
		//		</aon:htmlCommandLink>
    	
		//    	<aon:menuItem id="ProductHistory" 
		//				action="product_stats" 
    	//				actionListener="#{item.onProductHistory}"
		//				value="#{bundle.aon_history}"
		//				iconClass="aon-icon-task-assume">
		//		</aon:menuItem>
    	

    	HtmlMenuItem htmlMenuItem = new HtmlMenuItem();
    	htmlMenuItem.setIconStyle("display:none");

    	htmlCommandLink.setId(null);
    	if ( isBlank((String) htmlCommandLink.getValue())) {
    		getDefaultValue(htmlCommandLink.getStyleClass())
    		.ifPresent(htmlCommandLink::setValue);
    	}
    	htmlMenuItem.getChildren().add(htmlCommandLink);
    	
    	return htmlMenuItem;
    }
    
    private static HtmlMenuItem  createHtmlMenuItem(HtmlAjaxCommandLink htmlCommandLink) {
		//        <aon:commandLink 
		//        		id="resume-expedient-export" 
		//        		value="Analítica"
		//                onclick="resumeExpedient('#{projectExport.code}', '#{projectExport.name}', '#{projectExport.alias}', '#{projectSearchListener.registry.id}', '#{projectSearchListene>
		//                styleClass="aon-finding-toolbar-item aon-icon-excel">
		//        </aon:commandLink>
    	
    	HtmlMenuItem htmlMenuItem = new HtmlMenuItem();
    	htmlMenuItem.setIconStyle("display:none");

    	htmlCommandLink.setId(null);
    	htmlMenuItem.getChildren().add(htmlCommandLink);
    	
    	return htmlMenuItem;
    }
    
    
    
    
    private static boolean isBlank(String html) {
    	String str = AonStringUtils.replace(html, Character.toString((char)160), "");
    	return  AonStringUtils.isBlank(str);
    }
    
    
    private static Optional<String> getDefaultValue(String styleClass) {
    	Map<String,String> map = new HashMap<>();
    	map.put("aon-icon-pdf", "PDF");
    	map.put("aon-icon-excel", "Excel");
    	for (Entry<String,String> entry: map.entrySet()) {
			if ( AonStringUtils.contains(styleClass, entry.getKey() )) {
				return Optional.of(entry.getValue());
			}
		}
    	return Optional.empty();
    }
    
    
}