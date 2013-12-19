package com.esferalia.aon.gwt.fiscal.client.css;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.CellList.Style;

public interface AonCellList extends CellList.Resources {

	@Override
	@Source({CellList.Style.DEFAULT_CSS, "aonCellList.css"})
	public Style cellListStyle();
	
	@Source("../images/aon-icon-list-data.png")
	ImageResource aonListData();
	
}
