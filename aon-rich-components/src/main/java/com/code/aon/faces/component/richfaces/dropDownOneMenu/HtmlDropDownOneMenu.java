package com.code.aon.faces.component.richfaces.dropDownOneMenu;

import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

public class HtmlDropDownOneMenu extends HtmlSelectOneMenu {

    @Override
    protected Renderer getRenderer(FacesContext context) {
	Renderer renderer = super.getRenderer(context);
	return new DropDownOneMenuRenderer(renderer);
    }

}
