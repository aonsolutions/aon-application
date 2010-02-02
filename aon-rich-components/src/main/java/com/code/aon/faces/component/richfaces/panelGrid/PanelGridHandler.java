package com.code.aon.faces.component.richfaces.panelGrid;

import javax.faces.component.html.HtmlPanelGrid;

import org.apache.commons.lang.StringUtils;

import com.code.aon.faces.component.AonComponentHandler;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.jsf.ComponentConfig;

public class PanelGridHandler extends AonComponentHandler {

	public PanelGridHandler(ComponentConfig config) {
		super(config);
	}

	@Override
	protected void setAttributes( FaceletContext ctx, Object instance ) {
		super.setAttributes(ctx, instance);
		HtmlPanelGrid panelGrid = (HtmlPanelGrid) instance;
		String columnClasses = panelGrid.getColumnClasses();
		if (! StringUtils.isEmpty(columnClasses) ) {
			int columns = panelGrid.getColumns();
			if ( columns > 0 ) {
				String[] classes = StringUtils.split(columnClasses, " ,");
				if ( (classes != null) && (columns > classes.length) ) {
					StringBuffer newColumnClasses = new StringBuffer();
					for( int i = 0, n = 0; i < columns; ) {
						newColumnClasses.append( classes[n++] );
						if ( ++i != columns ) {
							newColumnClasses.append(",");
						}
						if ( n == classes.length ) {
							n = 0;
						}
					}
					panelGrid.setColumnClasses(newColumnClasses.toString());
				}
			}
		
		}
	}
	
}
