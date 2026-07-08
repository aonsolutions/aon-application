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
import java.util.List;

import javax.faces.FacesException;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.component.html.HtmlOutputLink;

import org.ajax4jsf.component.html.HtmlAjaxCommandLink;
import org.richfaces.component.html.HtmlMenuItem;

import com.code.aon.faces.component.util.FaceletUtil;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.TemplateClient;
import com.sun.facelets.tag.TagConfig;

import jakarta.el.ELException;
import jakarta.el.ValueExpression;

/**
 */
public final class InsertMenuItemsHandler extends AbstractInsertHandler implements TemplateClient {
	
	

    /**
     * @param config
     */
    public InsertMenuItemsHandler(TagConfig config) {
        super(config);
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
							if ( !filter(e) )
								return false;
							
							if ( exists(e) ) 
								return false;
							
							if ( !isMenuItem(e))
								return false;
							
							if (e instanceof UICommand uiCommand)
								return super.add(createHtmlMenuItem(uiCommand));

							if (e instanceof HtmlOutputLink outputLink)
								return super.add(createHtmlMenuItem(outputLink));
									
							return super.add(createHiddenHtmlMenuItem(e));
						}

						private boolean exists(UIComponent e) {
							return stream().anyMatch( m -> AonStringUtils.equals(m.getId(), getMenuItemId(e)));
						}
						
					};
				}


				private boolean filter(UIComponent e) {
					if ( filter == null )
						return true;
					ctx.setAttribute(elVar, e);
					return FaceletUtil.getBoolean(ctx, InsertMenuItemsHandler.this.filter);
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

    private static HtmlMenuItem  createHtmlMenuItem( UICommand uiCommand) {
    	

    	HtmlMenuItem htmlMenuItem = new HtmlMenuItem();
    	htmlMenuItem.setIconStyle("display:none");
    	// FIX ReRender ---> [RedMine #60873]
    	// htmlMenuItem.setRendered(uiCommand.isRendered());
    	ValueExpression renderedVE = uiCommand.getValueExpression("rendered");
    	if (renderedVE != null) {
    		htmlMenuItem.setValueExpression("rendered", renderedVE);
    	} else {
    		htmlMenuItem.setRendered(uiCommand.isRendered());
    	}
    	// End FIX
    	htmlMenuItem.setId( getMenuItemId(uiCommand));

    	htmlMenuItem.getChildren().add(uiCommand);
    	
    	return htmlMenuItem;
    }
    
    private static HtmlMenuItem  createHtmlMenuItem( HtmlOutputLink htmlOutputLink) {
    	

    	HtmlMenuItem htmlMenuItem = new HtmlMenuItem();
    	htmlMenuItem.setIconStyle("display:none");
    	// FIX ReRender ---> [RedMine #60873]
    	// htmlMenuItem.setRendered(htmlOutputLink.isRendered());
    	ValueExpression renderedVE = htmlOutputLink.getValueExpression("rendered");
    	if (renderedVE != null) {
    		htmlMenuItem.setValueExpression("rendered", renderedVE);
    	} else {
    		htmlMenuItem.setRendered(htmlOutputLink.isRendered());
    	}
    	// End FIX
    	htmlMenuItem.setId( getMenuItemId(htmlOutputLink));

    	htmlMenuItem.getChildren().add(htmlOutputLink);
    	
    	return htmlMenuItem;
    }

    private static String getMenuItemId(UIComponent uiComponent ) {
    	return uiComponent.getId() + "MenuItem";
    }
    
    
    private static HtmlMenuItem  createHiddenHtmlMenuItem(UIComponent uiComponent) {
    	HtmlMenuItem htmlMenuItem = new HtmlMenuItem();
    	htmlMenuItem.setStyle("display:none");
    	htmlMenuItem.setId( getMenuItemId(uiComponent));
    	htmlMenuItem.getChildren().add(uiComponent);
    	
    	return htmlMenuItem;
    }
    
    
    
    private static boolean isBlank(String html) {
    	String str = AonStringUtils.replace(html, Character.toString((char)160), "");
    	return  AonStringUtils.isBlank(str);
    }
    
    
    public static String getStyleClass( UIComponent uiComponent, FaceletContext ctx){
    	
    	if ( uiComponent instanceof HtmlCommandLink htmlCommandLink) {
    		return htmlCommandLink.getStyleClass();
    	} else if ( uiComponent instanceof HtmlAjaxCommandLink htmlAjaxCommandLink) {
    		return htmlAjaxCommandLink.getStyleClass();
    	}
    		
    	ValueExpression ve = uiComponent.getValueExpression("styleClass");
    	if (ve != null) {
    	    String value = null;
    	    
    	    try {
    			value = (String) ve.getValue(ctx);
    	    } catch (ELException e) {
    			throw new FacesException(e);
    	    }
    	    
    	    return value;
    	} 

        return null;
    	

    }
    
    public static boolean isMenuItem(UIComponent e) {
    	if ( e instanceof UICommand uiCommand )
    		return !isBlank((String) uiCommand.getValue());
    	if ( e instanceof HtmlOutputLink outputLink)
    		return outputLink.getChildCount() > 0;
    	
    	return false;
    }
    
}