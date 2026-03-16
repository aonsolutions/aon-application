package com.esferalia.aon.gwt.fiscal.server.fiscal.mod421;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;

import com.esferalia.aon.gwt.fiscal.server.ModelVAExcelAction;
import com.esferalia.aon.occam.api.model.fiscal.Mod421;
import com.esferalia.aon.occam.api.model.type.Mod421Key;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod421ExcelAction extends ModelVAExcelAction<Mod421,Mod421Key> {

	public Mod421ExcelAction(Mod421 mod421) {
		super(mod421);
	}

	@Override
	protected String getTitle() {
		return "I.G.I.C. R\u00E9gimen Simplificado. Autoliquidaci\u00F3n Trimestral.";
	}

	@Override
	protected String getDeclarationType() {
		return (model.getDeclarationResultType()!=null
				?model.getDeclarationResultType().getDescription()
				:AonStringUtils.EMPTY);
	}

	@Override
	protected void fillParticularityCell(Cell cell ,CellStyle style,Mod421Key key) {}
	
}
