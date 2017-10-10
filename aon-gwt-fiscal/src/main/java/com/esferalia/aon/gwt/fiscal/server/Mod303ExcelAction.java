package com.esferalia.aon.gwt.fiscal.server;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;

import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod303ExcelAction extends ModelVAExcelAction {

	public Mod303ExcelAction(Mod303 mod303) {
		super(mod303);
	}

	@Override
	protected String getTitle() {
		return "IVA. Autoliquidaci\u00F3n";
	}

	@Override
	protected String getDeclarationType() {
		return (model.getDeclarationType()!=null
				?model.getDeclarationType().getDescription()
				:AonStringUtils.EMPTY);
	}

	@Override
	protected void fillParticularityCell(Cell cell ,CellStyle style,Mod303Key key) {}
	
}
