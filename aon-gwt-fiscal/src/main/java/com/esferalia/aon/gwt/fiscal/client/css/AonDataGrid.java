package com.esferalia.aon.gwt.fiscal.client.css;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.DataGrid.Style;

public interface AonDataGrid extends DataGrid.Resources {

	@Source({DataGrid.Style.DEFAULT_CSS, "aonDataGrid.css"})
	Style dataGridStyle();
	
	@Source("../images/aon-dataTable-header.png")
	ImageResource aonDataTableHeader();
	
}
