package com.esferalia.aon.gwt.fiscal.server;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;

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
		return (model.getDeclarationType()!=null
				?model.getDeclarationType().getDescription()
				:AonStringUtils.EMPTY);
	}

	@Override
	protected void fillParticularityCell(Cell cell ,CellStyle style,Mod111Key key) {
		double amount = model.ensureDetail(key).getAmount();
		style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		cell.setCellType(Cell.CELL_TYPE_STRING);
		if (key == Mod111Key.AR_907) {
			cell.setCellValue(amount == 1?"SI":"NO");
		} else if (key == Mod111Key.AR_908) {
			style.setFont(smallFont);
			cell.setCellValue(amount == 1?"Preconsursal"
				:(amount == 2?"Postconsursal":" "));
		} else if (key == Mod111Key.AR_909) {
			String date = model.ensureDetail(key).getDescription();		
			cell.setCellValue(AonStringUtils.defaultString(date));
			row = sheet.createRow(rowCount++);
		}
	}
}
