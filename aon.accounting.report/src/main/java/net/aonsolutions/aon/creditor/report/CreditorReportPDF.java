package net.aonsolutions.aon.creditor.report;

import java.io.OutputStream;
import java.util.Optional;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.registry.CreditorFull;
import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.aon.report.AbsReportTablePDF;
import net.aonsolutions.aon.report.AbsSimpleReportPDF;
import net.aonsolutions.aon.report.AonReportException;
import net.aonsolutions.aon.report.IHeader;

public class CreditorReportPDF extends AbsSimpleReportPDF<CreditorFull> {

	enum Header implements IHeader {
		 ID("C\u00F3digo",40)
		,DOC("N.I.F.",80)
		,NAME("Nombre",180)
		,ALIAS("Alias",85)
		,PHONE("Tel\u00E9fono",70)
		,STATUS("Estado",50)
		;
		
		private String label;
		private float width;
		
		private Header (String label, float width) {
			this.label = label;
			this.width = width;
		}
		@Override
		public String getLabel() {
			return label;
		}
		@Override
		public float getWidth() {
			return width;
		}
	}

	public CreditorReportPDF(Occam occam) {
		super(occam);
	}
	
	
	private class PDFReport extends AbsReportTablePDF<CreditorFull> {

		public PDFReport(IHeader[] headers) {
			super(headers);
		}
		
		@Override
		public void accept(CreditorFull creditor) {

			addStringCell(creditor.getId().toString());
			addStringCell(creditor.getRegistry().getDocument());
			addStringCell(creditor.getRegistry().getName());
			addStringCell(creditor.getRegistry().getAlias());
			addStringCell(
				AonCollectionUtils.stream(  creditor.getMedias() )
					.map( rm -> rm.getValue())
					.findFirst()
					.orElse(null));
			addStringCell(
				Optional.ofNullable(creditor.getRegistry().getStatus())
					.map(rs -> rs.getDescription())
					.orElse( "" ));
		}
		
	}


	@Override
	public void print(OutputStream outputStream, Stream<CreditorFull> stream) throws AonReportException {
		super.printReportPDF(outputStream, new PDFReport(Header.values()), stream);
	}


	@Override
	protected String getTitle() {
		return "Listado de acreedores";
	}

}
