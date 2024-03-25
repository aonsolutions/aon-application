package net.aonsolutions.aon.registry.report;

import java.io.OutputStream;
import java.util.Optional;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.registry.SupplierFull;
import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.aon.report.pdf.AbsReportTablePDF;
import net.aonsolutions.aon.report.pdf.AbsSimpleReportPDF;
import net.aonsolutions.aon.report.pdf.AonReportException;

public class SupplierReportPDF extends AbsSimpleReportPDF<SupplierFull> {


	public SupplierReportPDF(Occam occam) {
		super(occam);
	}
	
	
	private class PDFReport extends AbsReportTablePDF<SupplierFull> {

		public PDFReport(IHeader[] headers) {
			super(headers);
		}
		
		@Override
		public void accept(SupplierFull supplier) {
			addIntegerCell(supplier.getId());
			addStringCell(supplier.getRegistry().getDocument());
			addStringCell(supplier.getRegistry().getName());
			addStringCell(supplier.getRegistry().getAlias());
			addStringCell(
				AonCollectionUtils.stream(  supplier.getMedias() )
					.map( rm -> rm.getValue())
					.findFirst()
					.orElse(null));
			addStringCell(
				Optional.ofNullable(supplier.getRegistry().getStatus())
					.map(rs -> rs.getDescription())
					.orElse( "" ));
		}
		
	}


	@Override
	public void print(OutputStream outputStream, Stream<SupplierFull> stream) throws AonReportException {
		super.printReportPDF(outputStream, new PDFReport(RegistryReportHeader.values()), stream);
	}


	@Override
	protected String getTitle() {
		return "Listado de Proveedores";
	}

}
