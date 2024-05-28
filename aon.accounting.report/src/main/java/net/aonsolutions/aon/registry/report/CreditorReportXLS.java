package net.aonsolutions.aon.registry.report;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;

import org.apache.poi.ss.util.CellRangeAddress;

import com.esferalia.aon.occam.api.model.registry.CreditorFull;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.aon.report.poi.AbsExcelReport;

public class CreditorReportXLS extends AbsExcelReport implements Consumer<CreditorFull>{
		
	@Override
	protected void headerRow() {
		row = sheet.createRow(rowCount++);
		RegistryReportHeader.stream()
			.forEach(h -> createHeaderCell(h));
		sheet.setRepeatingRows(new CellRangeAddress(0, 1, 0, RegistryReportHeader.values().length));
	}


	
	@Override
	public void accept(CreditorFull creditor) {
		row = sheet.createRow(rowCount++);
		addCell(creditor.getId());
		addCell(creditor.getRegistry().getDocument());
		addCell(creditor.getRegistry().getName());
		addCell(creditor.getRegistry().getAlias());
		addCell(
			AonCollectionUtils.stream(  creditor.getMedias() )
				.map( rm -> rm.getValue())
				.findFirst()
				.orElse(null));
		addCell(
			Optional.ofNullable(creditor.getRegistry().getStatus())
				.map(rs -> rs.getDescription())
				.orElse( "" ));
		try {
			if (rowCount % 100 == 0) sheet.flushRows();
		} catch (IOException e) {
			throw new AonCoreException( e );
		}
	}



	@Override
	protected String getTitle() {
		return "Listado de Acreedores";
	}

}
