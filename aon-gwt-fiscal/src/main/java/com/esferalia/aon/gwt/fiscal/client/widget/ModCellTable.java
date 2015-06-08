package com.esferalia.aon.gwt.fiscal.client.widget;

import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.CellTable.Style;

public interface ModCellTable extends CellTable.Resources {
	@Source({CellTable.Style.DEFAULT_CSS, "ModCellTable.css"})
	Style cellTableStyle();
}
