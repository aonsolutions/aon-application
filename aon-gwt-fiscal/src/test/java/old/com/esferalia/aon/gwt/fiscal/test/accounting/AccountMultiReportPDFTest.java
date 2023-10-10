package old.com.esferalia.aon.gwt.fiscal.test.accounting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedHashMap;

import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.accounting.BalanceType;
import com.itextpdf.text.DocumentException;

import net.aonsolutions.aon.accounting.report.AccountBalanceReportPDF;
import net.aonsolutions.aon.accounting.report.AccountJournalReportPDF;
import net.aonsolutions.aon.accounting.report.AccountLedgerReportPDF;
import net.aonsolutions.aon.accounting.report.AccountMultiCoverPDF;
import net.aonsolutions.aon.accounting.report.AccountMultiReportPDF;
import net.aonsolutions.aon.accounting.report.AccountOperatingReportPDF;
import net.aonsolutions.aon.accounting.report.AccountTrialBalanceReportPDF;
import net.aonsolutions.aon.accounting.report.IAccountReportPDF;
import net.aonsolutions.aon.accounting.report.VatReportPDF;

public class AccountMultiReportPDFTest {
	
	public static void main(String[] args) throws IOException, DocumentException, SQLException {
		
		final int DOMAIN_ID = 9253;  
		final String DOMAIN = "prueba-dsi.aonsolutions.dev";
		final String USER = "admin";
		
		// Filtros: Comunes a todos los listados (se podrían poner más si es necesario)
		AccountingReportParams params = new AccountingReportParams()
				.setDomain(DOMAIN_ID)
				.setDomainName(DOMAIN)
				.setUser(USER)
				.setFromDate(new Date("01/01/2023"))
				.setToDate(new Date("12/31/2023"))
				.setPeriod(21392)  // Ejercicio: Ejercicio 2023 (id de account_period)
		        ;		
		
		// Reports que se van a incluir
		LinkedHashMap<IAccountReportPDF,AccountingReportParams> reports = new LinkedHashMap<>();
		reports.put(new AccountMultiCoverPDF(), params.clone().setTitle("Listado Múltiple").setShowCover(true));  // Portada
		reports.put(new AccountJournalReportPDF(), params.clone().setTitle("Listado diario de movimientos")); 	  // Listado diario de movimientos 
		reports.put(new AccountLedgerReportPDF(), params.clone().setTitle("Mayor de cuentas"));  	              // Listado Mayor de Cuentas
		reports.put(new AccountTrialBalanceReportPDF(), params.clone().setTitle("Balance de sumas y saldos").setLevel(9));  // Balance de Sumas y saldos
		reports.put(new AccountOperatingReportPDF(), params.clone().setTitle("Cuenta de explotación").setLevel(4)); 	    // Listado PyG
		reports.put(new AccountBalanceReportPDF(), params.clone().setTitle(BalanceType.BALANCE_ABBREVIATE.getName()).setBalanceType(BalanceType.BALANCE_ABBREVIATE));  // Balances oficiales (Balance Abreviado)
		reports.put(new AccountBalanceReportPDF(), params.clone().setTitle(BalanceType.PYG_ABBREVIATE.getName()).setBalanceType(BalanceType.PYG_ABBREVIATE));          // Balances oficiales (Cuenta PyG Abreviada)
		reports.put(new VatReportPDF(), params.clone().setTitle("Listado de IVA"));   // Listado de IVA
				
		File pdfFile = new File("c:\\tmp\\prueba.pdf");		
		FileOutputStream pdfStream = new FileOutputStream(pdfFile);
	
		AccountMultiReportPDF multi = new AccountMultiReportPDF();
		multi.printMultiReportPDF(pdfStream, reports);
		
		File zipFile = new File("c:\\tmp\\prueba.zip");
		multi.createZip(zipFile, pdfFile);		
		
	}	
	
}
