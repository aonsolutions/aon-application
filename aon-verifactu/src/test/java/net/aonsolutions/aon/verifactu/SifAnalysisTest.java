package net.aonsolutions.aon.verifactu;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Date;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.finance.InvoiceCollectionInfo;
import com.esferalia.aon.occam.api.model.finance.InvoiceConsoleParams;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationStatus;
import com.esferalia.aon.occam.api.model.invoice.InvoiceConsoleAnalysis;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceConsoleDAO;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

class SifAnalysisTest extends AbstractVerifactuTest {

	@Override protected Environment getEnvironment() { return SIF_ENV; }

	@Test void sif_analysis_test( ) { analysis_test( SIF_ENV ); }
	@Test void no_verifactu_analysis_test( ) { analysis_test( NO_VERIFACTU_ENV ); }
	
	private void analysis_test( Environment env) {
		Date today = AonDateUtils.today();
		Date startDate = AonDateUtils.getYearFirstDay(today);
		Date endDate = AonDateUtils.getYearLastDay(today);
		InvoiceConsoleParams params = new InvoiceConsoleParams()
			.setDomain( env.getDomainId() )
			.setFromDate(startDate)
			.setToDate(endDate)
			.setAnnulled(false)
		;
		InvoiceConsoleAnalysis a = InvoiceConsoleDAO.analyze( env.getCtx(), params);
		assertNotNull( a );
		assertNotNull( a.getInvoiceInfo() );
		format("TOTAL FACTURAS",a.getInvoiceInfo());
		assertNotNull( a.getProformaInfo() );
		format("TOTAL PROFORMA",a.getProformaInfo());
		
		
		assertNotNull( a.getTypesInfo() );
		AonCollectionUtils.keysStream( a.getTypesInfo())
			.peek( type -> title(type == null ? "SIN COMUNICACION" : type.name()))
			.flatMap( type -> AonCollectionUtils.stream(a.getTypesInfo().get(type))) 
			.forEach( entry -> format(entry.getKey(), entry.getValue()) )
		;
			
	}
	

	private void title(String title) {
		if (title == null) title = "¡NO LABEL!";
		System.out.println( "\n" + title );
		System.out.println( AonStringUtils.repeat( "-", title.length() ) );
	}
	private void format(InvoiceCommunicationStatus key, InvoiceCollectionInfo value) {
		format( key == null ? "SIN ESTADO" : key.name(), value );
	}
	private void format(String label, InvoiceCollectionInfo info) {
		if (info == null) {
			System.out.println( "\n" + label + ": No Data" );
			return;
		}
		title(label);
		format("\tNº Facturas",info.getTotalCount());
		format("\tImporte Total",info.getTotalAmount());
		format("\tImporte Total VAT",info.getTotalVAT() );
		format("\tImporte Total IRPF",info.getTotalRetention() );
		format("\tFacturas con Suplidos",info.getTotalPrepaymentCount() );
	}

	private void format(String label, Number amount) {
		if (amount == null) amount = 0;
		if (label == null) label = "";
		System.out.println( 
			AonStringUtils.rightPad(label,50,'.') 
			+ ": "
			+ AonStringUtils.leftPad( AonNumberUtils.toString(amount),10)
		); 
	}

	
}