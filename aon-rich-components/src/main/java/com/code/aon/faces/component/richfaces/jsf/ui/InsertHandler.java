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
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlCommandLink;

import com.code.aon.faces.component.util.FaceletUtil;
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
public final class InsertHandler extends TagHandler implements TemplateClient {
	
	
	private final String name;
	private final String elVar;
    private final TagAttribute filter ;

    /**
     * @param config
     */
    public InsertHandler(TagConfig config) {
        super(config);
        this.elVar = "el";
        TagAttribute attr = this.getAttribute("name");
        if (attr != null) {
            if (!attr.isLiteral()) {
                throw new TagAttributeException(this.tag, attr, "Must be Literal");
            }
            this.name = attr.getValue();
        } else {
            this.name = null;
        }
        this.filter = this.getAttribute("filter");
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
							return !exists(parent, e) && filter(e) && super.add(e);
						}
						
					};
				}
				

				private boolean filter(UIComponent e) {
					if ( filter == null )
						return true;
					ctx.setAttribute(elVar, e);
					return FaceletUtil.getBoolean(ctx, InsertHandler.this.filter);
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

    @Override
    public boolean apply(FaceletContext ctx, UIComponent parent, String name)
    		throws IOException, FacesException, FaceletException, ELException {
        if (Objects.equals(this.name, name)) {
            this.nextHandler.apply(ctx, parent);
            return true;
        }
        return false;
    }
    
	private static boolean exists(UIComponent parent, UIComponent e) {
		if ( Objects.equals(parent.getId(), e.getId())) {
			return true;
		} else {
			for ( UIComponent child: parent.getChildren() ) {
				if ( exists(child, e)) {
					return true;
				}
			}
			return false;
		}
	}
   
    
}