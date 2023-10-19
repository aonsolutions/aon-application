package com.code.aon.faces.component.richfaces.textField;

import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

public class HtmlTextField extends HtmlInputText {

    @Override
    protected Renderer getRenderer(FacesContext context) {
	Renderer renderer = super.getRenderer(context);
	return new TextFieldRenderer(renderer);
    }

}
