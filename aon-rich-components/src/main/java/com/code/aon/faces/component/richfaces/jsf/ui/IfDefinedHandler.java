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

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagAttributeException;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagHandler;

import jakarta.el.ELException;

public final class IfDefinedHandler extends TagHandler {

    private final String name;


    /**
     * @param config
     */
    public IfDefinedHandler(TagConfig config) {
        super(config);
        TagAttribute attr = this.getRequiredAttribute("name");
        if (!attr.isLiteral()) {
            throw new TagAttributeException(this.tag, attr, "Must be Literal");
        }
        this.name = attr.getValue();
    }

    public void apply(FaceletContext ctx, UIComponent parent)
            throws IOException, FacesException, ELException {
    	
    	UIComponent mockUIComponent = new UIComponentBase() {
			@Override
			public String getFamily() {
				return "Mock";
			}
		
    	};
		
		boolean found = ctx.includeDefinition(mockUIComponent, name); 
		
		if ( found ) {		
			this.nextHandler.apply(ctx, parent);
		}
    }
    

}