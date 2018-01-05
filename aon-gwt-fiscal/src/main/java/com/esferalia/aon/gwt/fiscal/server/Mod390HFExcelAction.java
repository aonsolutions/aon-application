package com.esferalia.aon.gwt.fiscal.server;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;

import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.type.Mod390Key;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod390HFExcelAction extends ModelVAExcelAction<Mod390HF,Mod390Key> {

	public Mod390HFExcelAction(Mod390HF mod390) {
		super(mod390);
	}

	@Override
	protected String getTitle() {
		return "IVA. Declaraci\u00F3n-liquidaci\u00F3n anual";
	}

	@Override
	protected String getDeclarationType() {
		return (model.getDeclarationType()!=null
				?model.getDeclarationType().getDescription()
				:AonStringUtils.EMPTY);
	}

	@Override
	protected void fillParticularityCell(Cell cell ,CellStyle style,Mod390Key key) {}
	
}
