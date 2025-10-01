package com.code.aon.faces.component.richfaces.jsf.ui;

import static com.code.aon.faces.component.richfaces.jsf.ui.InsertMenuItemsHandler.isMenuItem;

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
import java.util.Objects;
import java.util.Optional;

import javax.faces.FacesException;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;

import com.code.aon.faces.component.util.FaceletUtil;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.TemplateClient;
import com.sun.facelets.tag.TagConfig;

import jakarta.el.ELException;

/**
 */
public final class InsertToolbarItemsHandler extends AbstractInsertHandler implements TemplateClient {
    /**
     * @param config
     */
    public InsertToolbarItemsHandler(TagConfig config) {
        super(config);
    }

    public void apply(FaceletContext ctx, UIComponent parent)
            throws IOException, FacesException, ELException {
        ctx.extendClient(this);
        boolean found = false;
        try {
			UIComponent delegateParent = new DelegateUIComponent(parent) {
				

				@Override
				public List<UIComponent> getChildren() {
					return new DelegateList<>(super.getChildren()) {
						@Override
						public boolean add(UIComponent e) {
							return !exists(parent, e) && filter(e) && super.add(customize(e));
						}
					};
				}
				

				private boolean filter(UIComponent e) {
					if ( filter == null )
						return true;
					ctx.setAttribute(elVar, e);
					return FaceletUtil.getBoolean(ctx, InsertToolbarItemsHandler.this.filter);
				}
			};
        	found = ctx.includeDefinition(delegateParent, this.name);
            
            
        } finally {
            ctx.popClient(this);
        }
        if (!found) {
            this.nextHandler.apply(ctx, parent);
        }
    }
    
    private static UIComponent customize(UIComponent e) {
		if (e instanceof UICommand uiCommand)
			return customize(uiCommand);
    	
    	return e;
    }
    
    private static UIComponent customize(UICommand e) {
    	Optional<String> value = Optional.ofNullable(e.getValue()).map(Object::toString).filter(AonStringUtils::isNotBlank); 
    	Optional<String> title = Optional.ofNullable(e.getAttributes().get("title")).map(Object::toString).filter(AonStringUtils::isNotBlank);
    	
    	// if value is not blank, and not at title yet
    	value.filter( v -> !AonStringUtils.containsIgnoreCase(title.orElse(null), v))
    	.ifPresent( v -> e.getAttributes().put("title", String.format("%s %s", v , title.orElse("") )) );
    	
    	
    	return e;
    }


    
}