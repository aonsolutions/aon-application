package com.esferalia.aon.gwt.fiscal.server.fiscal.mod111;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import com.esferalia.aon.gwt.fiscal.server.ModelIRPFExcelAction;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod111ExcelAction extends ModelIRPFExcelAction<Mod111,Mod111Key> {

	public Mod111ExcelAction(Mod111 mod111) {
		super(mod111);
	}

	@Override
	protected String getTitle() {
		return "Retenciones e ingresos a cuenta. " 
			+ "Rendimientos del trabajo y de actividades econ\u00F3micas.";
	}

	@Override
	protected String getDeclarationType() {
		return (model.getDeclarationResultType()!=null
				?model.getDeclarationResultType().getDescription()
				:AonStringUtils.EMPTY);
	}

	@Override
	protected void fillParticularityCell(Cell cell ,CellStyle style,Mod111Key key) {
		double amount = model.ensureDetail(key).getAmount();
		style.setAlignment(HorizontalAlignment.LEFT);
		if (key == Mod111Key.AR_907) {
			cell.setCellValue(amount == 1?"SI":"NO");
		} else if (key == Mod111Key.AR_908) {
			style.setFont(smallFont);
			String content = " ";
			if (amount == 1) content = "Preconsursal";
			else if (amount == 2) content = "Postconsursal"; 
			cell.setCellValue(content);
		} else if (key == Mod111Key.AR_909) {
			String date = model.ensureDetail(key).getDescription();		
			cell.setCellValue(AonStringUtils.defaultString(date));
			row = sheet.createRow(rowCount++);
		}
	}
}
