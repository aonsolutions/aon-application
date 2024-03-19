package net.aonsolutions.aon.supplier.report;

import java.io.OutputStream;
import java.util.Optional;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.api.model.registry.SupplierFull;
import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.aon.report.AbsReportTablePDF;
import net.aonsolutions.aon.report.AbsSimpleReportPDF;
import net.aonsolutions.aon.report.AonReportException;
import net.aonsolutions.aon.report.IHeader;

public class SupplierReportPDF extends AbsSimpleReportPDF<SupplierFull> {

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

	public SupplierReportPDF(Occam occam) {
		super(occam);
	}
	
	
	private class PDFReport extends AbsReportTablePDF<SupplierFull> {

		public PDFReport(IHeader[] headers) {
			super(headers);
		}
		
		@Override
		public void accept(SupplierFull supplier) {

			addStringCell(supplier.getId().toString());
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
		super.printReportPDF(outputStream, new PDFReport(Header.values()), stream);
	}


	@Override
	protected String getTitle() {
		return "Listado de proveedores";
	}

}
