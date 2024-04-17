package net.aonsolutions.aon.registry.report;

import java.io.OutputStream;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.registry.CreditorFull;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.aon.report.pdf.AbsReportTablePDF;
import net.aonsolutions.aon.report.pdf.AbsSimpleReportPDF;
import net.aonsolutions.aon.report.pdf.AonReportException;

public class CreditorReportPDF extends AbsSimpleReportPDF<CreditorFull> {


	public CreditorReportPDF(Occam occam) {
		super(occam);
	}
	
	
	private class PDFReport extends AbsReportTablePDF<CreditorFull> {

		public PDFReport(IHeader[] headers) {
			super(headers);
		}
		
		@Override
		public void accept(CreditorFull creditor) {
			addIntegerCell(creditor.getId());
			addStringCell(creditor.getRegistry().getDocument());
			addStringCell(creditor.getRegistry().getName());
			addStringCell(creditor.getRegistry().getAlias());
			addStringCell(
					AonCollectionUtils.stream(creditor.getMedias())
					.filter(Objects::nonNull)	
					.filter(rm -> rm.getValue() != null)
					.filter(rm -> rm.getMedia() == MediaType.FIXED_PHONE)
					.map(rm -> rm.getValue())
					.findFirst()
					.orElse(""));
			addStringCell(
				Optional.ofNullable(creditor.getRegistry().getStatus())
					.map(rs -> rs.getDescription())
					.orElse( "" ));
		}
		
	}


	@Override
	public void print(OutputStream outputStream, Stream<CreditorFull> stream) throws AonReportException {
		super.printReportPDF(outputStream, new PDFReport(RegistryReportHeader.values()), stream);
	}


	@Override
	protected String getTitle() {
		return "Listado de Acreedores";
	}

}
