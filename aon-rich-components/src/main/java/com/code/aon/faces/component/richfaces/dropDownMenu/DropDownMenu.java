package com.code.aon.faces.component.richfaces.dropDownMenu;

import java.util.List;

import javax.faces.component.UIComponent;

import org.richfaces.component.html.HtmlDropDownMenu;
import org.richfaces.component.html.HtmlMenuSeparator;

public class DropDownMenu extends HtmlDropDownMenu {
	@Override
	public boolean isRendered() {
		return getMenuItemCount() > 0 && super.isRendered();
	}
	
	public int getMenuItemCount() {
		int childCount = 0;
		List<UIComponent> children = getChildren();
		for(UIComponent child : children) {
			if (!(child instanceof HtmlMenuSeparator)) {
		        childCount++;
		    }		
		}
		return childCount;
	}
}
