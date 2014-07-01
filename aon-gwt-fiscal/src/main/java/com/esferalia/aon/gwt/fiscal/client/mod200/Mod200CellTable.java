package com.esferalia.aon.gwt.fiscal.client.mod200;

import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.CellTable.Style;

public interface Mod200CellTable extends CellTable.Resources {

	@Source({CellTable.Style.DEFAULT_CSS, "Mod200CellTable.css"})
	Style cellTableStyle();
	
}
