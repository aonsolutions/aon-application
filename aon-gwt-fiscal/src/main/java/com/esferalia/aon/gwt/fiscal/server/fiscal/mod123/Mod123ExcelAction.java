package com.esferalia.aon.gwt.fiscal.server.fiscal.mod123;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import com.esferalia.aon.gwt.fiscal.server.ModelIRPFExcelAction;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.type.Mod123Key;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod123ExcelAction extends ModelIRPFExcelAction<Mod123,Mod123Key> {

	public Mod123ExcelAction(Mod123 mod123) {
		super(mod123);
	}

	@Override
	protected String getTitle() {
		return "Retenciones e ingresos a cuenta. "
			+ "Determinados rendimientos del capital mobiliario o determinadas rentas.";
	}

	@Override
	protected String getDeclarationType() {
		return (model.getDeclarationResultType()!=null
				?model.getDeclarationResultType().getDescription()
				:AonStringUtils.EMPTY);
	}

	@Override
	protected void fillParticularityCell(Cell cell ,CellStyle style,Mod123Key key) {
		double amount = model.ensureDetail(key).getAmount();
		style.setAlignment(HorizontalAlignment.LEFT);
		cell.setCellType(CellType.STRING);
		if (key == Mod123Key.AR_907 || key == Mod123Key.AR_930) {
			cell.setCellValue(amount == 1?"SI":"NO");
		} else if (key == Mod123Key.AR_908) {
			style.setFont(smallFont);
			cell.setCellValue(amount == 1?"Preconsursal"
				:(amount == 2?"Postconsursal":" "));
		} else if (key == Mod123Key.AR_909) {
			String date = model.ensureDetail(key).getDescription();		
			cell.setCellValue(AonStringUtils.defaultString(date));
			row = sheet.createRow(rowCount++);
		}
	}
	
}
