package com.esferalia.aon.gwt.common.client.css;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.CellTable.Style;

public interface AonCellTable extends CellTable.Resources {

	@Source({CellTable.Style.DEFAULT_CSS, "aonCellTable.css"})
	Style cellTableStyle();
	
	@Source("images/aon-dataTable-header.png")
	ImageResource aonDataTableHeader();
	
}
