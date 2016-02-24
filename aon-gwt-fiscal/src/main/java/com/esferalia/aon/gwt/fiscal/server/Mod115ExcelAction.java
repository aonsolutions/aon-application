package com.esferalia.aon.gwt.fiscal.server;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;

import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.type.Mod115Key;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod115ExcelAction extends ModelIRPFExcelAction<Mod115,Mod115Key> {

	public Mod115ExcelAction(Mod115 mod115) {
		super(mod115);
	}

	@Override
	protected String getTitle() {
		return "Retenciones e ingresos a cuenta. " 
			+ "Rentas o rendimientos procedentes del arrendamiento "
			+ "o subarrendamiento de inmuebles urbanos.";
	}

	@Override
	protected String getDeclarationType() {
		return (model.getDeclarationType()!=null
				?model.getDeclarationType().getDescription()
				:AonStringUtils.EMPTY);
	}

	@Override
	protected void fillParticularityCell(Cell cell ,CellStyle style,Mod115Key key) {
		double amount = model.ensureDetail(key).getAmount();
		style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		cell.setCellType(Cell.CELL_TYPE_STRING);
		if (key == Mod115Key.AR_907) {
			cell.setCellValue(amount == 1?"SI":"NO");
		} else if (key == Mod115Key.AR_908) {
			style.setFont(smallFont);
			cell.setCellValue(amount == 1?"Preconsursal"
				:(amount == 2?"Postconsursal":" "));
		} else if (key == Mod115Key.AR_909) {
			String date = model.ensureDetail(key).getDescription();		
			cell.setCellValue(AonStringUtils.defaultString(date));
			row = sheet.createRow(rowCount++);
		}
	}
}
