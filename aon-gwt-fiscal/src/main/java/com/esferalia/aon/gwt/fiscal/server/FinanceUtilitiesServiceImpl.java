package com.esferalia.aon.gwt.fiscal.server;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.finance.utilities.FinanceUtilitiesService;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.FinanceTracking;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.utilities.FinanceUtilitiesParams;
import com.esferalia.aon.occam.api.model.finance.utilities.FinanceUtilitiesResult;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FinanceTrackingDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

@WebServlet(name = "Finance Utilities Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/FinanceUtilities" })
public class FinanceUtilitiesServiceImpl extends AonStatelessRemoteServiceServlet implements FinanceUtilitiesService {

	private static final long serialVersionUID = 1L;

	@Override
	public Domain getDomain(String domainName, String user, int domain) throws AonCoreException {
		return AON.getDomain(domainName, domain, user);
	}

	@Override
	public LinkedList<Domain> getChildDomains(String domainName, String user, int domain) throws AonCoreException {
		return AON.getDomainList(domainName, domain, user, p -> p.getParentProperty().eq(domain));
	}

	@Override
	public FinanceUtilitiesResult missingFinanceInvoices(String domainName, String user, Domain domain,FinanceUtilitiesParams params)
			throws AonCoreException {
		return AON.missingFinanceInvoices(domainName, user, domain.getId(),params);
	}

	@Override
	public Invoice missingFinanceInvoicesFix(String domainName, String user, Integer domain,
			Integer invoice) throws AonCoreException {
		return AON.missingFinanceInvoicesFix(domainName, user, domain,invoice);
	}
	@Override
	public FinanceUtilitiesResult financeInvoiceIntegrity(String domainName, String user, Domain domain)
			throws AonCoreException {
		return AON.financeInvoiceIntegrity(domainName, user, domain);
	}
	@Override
	public Finance financeInvoiceIntegrityFix(String domainName, String user, Integer domain, Finance finance)
			throws AonCoreException {
		return AON.financeInvoiceIntegrityFix(domainName, user, domain, finance);
	}
	
	// ***************************************************	
	// **************** FIx AYUDAT BOORAR ****************
	// ***************************************************
	
	@Override
	public FinanceUtilitiesResult ayudatFix(String domainName, String user, Domain domain) throws AonCoreException {
		if (domain.getId() != 7138) {
			throw new AonCoreException( "El dominio no es el 7138");
		}
		InputStream fis = null;
		try {
			AONContext ctx = AONContext.getAONContext(domainName, domain.getId(), user);
			fis = FinanceUtilitiesServiceImpl.class.getResourceAsStream("/com/esferalia/aon/gwt/fiscal/ayudat/AyudaTFixFinances.xls");
			LinkedList<String> ret = read(ctx, fis);
			
			FinanceUtilitiesResult result = new FinanceUtilitiesResult();		
			for (String msg : ret ) {
				result.addMessage(msg);
			}
			return result;
		} catch (IOException e) {
			throw new AonCoreException( e.getMessage());
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private static class ExcelRow {
		private String fecha;
		private String razónSocial;
		private String nif;
		private double totalFacturado;
		private double anticipado;
		private double compensado;
		private double recobro;
		private double totalRemesar;
		private String facturas;
		private double devolucionParcial;

		public String getFecha() {
			return fecha;
		}

		public ExcelRow setFecha(String fecha) {
			this.fecha = fecha;
			return this;
		}

		public String getRazónSocial() {
			return razónSocial;
		}

		public ExcelRow setRazónSocial(String razónSocial) {
			this.razónSocial = razónSocial;
			return this;
		}

		public String getNif() {
			return nif;
		}

		public ExcelRow setNif(String nIF) {
			this.nif = nIF;
			return this;
		}

		public double getTotalFacturado() {
			return totalFacturado;
		}

		public ExcelRow setTotalFacturado(double totalFacturado) {
			this.totalFacturado = totalFacturado;
			return this;
		}

		public double getAnticipado() {
			return anticipado;
		}

		public ExcelRow setAnticipado(double anticipado) {
			this.anticipado = anticipado;
			return this;
		}

		public double getCompensado() {
			return compensado;
		}

		public ExcelRow setCompensado(double compensado) {
			this.compensado = compensado;
			return this;
		}

		public double getRecobro() {
			return recobro;
		}

		public ExcelRow setRecobro(double recobro) {
			this.recobro = recobro;
			return this;
		}

		public double getTotalRemesar() {
			return totalRemesar;
		}

		public ExcelRow setTotalRemesar(double totalRemesar) {
			this.totalRemesar = totalRemesar;
			return this;
		}

		public String getFacturas() {
			return facturas;
		}

		public ExcelRow setFacturas(String facturas) {
			this.facturas = facturas;
			return this;
		}

		public double getDevolucionParcial() {
			return devolucionParcial;
		}

		public ExcelRow setDevolucionParcial(double devolucionParcial) {
			this.devolucionParcial = devolucionParcial;
			return this;
		}

	}

	private static LinkedList<String> read(AONContext ctx, InputStream input) throws IOException {
		LinkedList<String> ret = new LinkedList<String>();
		Workbook workbook = new HSSFWorkbook(input);
		Sheet firstSheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = firstSheet.iterator();
		if (iterator.hasNext())  iterator.next();
		int i = 2;
		while (iterator.hasNext()) {
			Row nextRow = iterator.next();
			ExcelRow excelRow = new ExcelRow();
			boolean hasFactura = nextRow.getCell(10).getCellTypeEnum() == CellType.STRING; 
			excelRow.setFecha			(nextRow.getCell(0).getStringCellValue())
				.setRazónSocial			(nextRow.getCell(1).getStringCellValue())	
				.setNif					(nextRow.getCell(2).getStringCellValue())
//				.setSubtotal			(nextRow.getCell(3).getNumericCellValue())	
//				.setImpuesto			(nextRow.getCell(4).getNumericCellValue())
				.setTotalFacturado		(nextRow.getCell(5).getNumericCellValue())
				.setAnticipado			(nextRow.getCell(6).getNumericCellValue())	
				.setCompensado			(nextRow.getCell(7).getNumericCellValue())	
				.setRecobro				(nextRow.getCell(8).getNumericCellValue())
				.setTotalRemesar		(nextRow.getCell(9).getNumericCellValue())	
				.setFacturas			(hasFactura?nextRow.getCell(10).getStringCellValue():"NO_NUMBER")
//				.setEstado				(nextRow.getCell(11).getStringCellValue())
				.setDevolucionParcial	(nextRow.getCell(12).getNumericCellValue());
			
			ret.addAll( checkExcelRow( ctx, i , excelRow ) );
			i++;
		}
		workbook.close();
		return ret;
	}

	private static LinkedList<String> checkExcelRow(AONContext ctx, int i, ExcelRow row) {
		boolean fractionable = AonMathUtils.isZero(( row .getDevolucionParcial()));
		LinkedList<String> ret = new LinkedList<String>();
		LinkedList<Invoice> invoices = InvoiceDAO.getInvoiceStream(ctx, 
				p -> p.getTypeProperty().eq(InvoiceType.SALES.value())
				.and(p.getReferenceCodeProperty().eq(row.getFacturas()))
				.and(p.getDomainProperty().eq(ctx.getDomainId()))
				)
			.peek(inv -> inv.setFinances( FinanceDAO.getInvoiceFinances(ctx, inv.getId())))
			.collect(Collectors.toCollection(LinkedList::new));
		if (invoices == null || invoices.size() == 0) {
			ret.add(log( i, row, "Factura no encontrada"));
		} else if (invoices.size() > 1) {
			ret.add(log( i, row, "Factura encontrada " + invoices.size() + " veces"));
		} else {
			boolean valid = true;
			Invoice inv = invoices.get(0);
			if (!AonMathUtils.equals(row.getTotalFacturado(), inv.getTotal())) {
				ret.add(log( i, row, "El total factura no coincide con lo grabado: (Excel: " + row.getTotalFacturado() + ") <> (aon: " + inv.getTotal()+")"));
				valid = false;
			}
			if (inv.getFinances() == null || inv.getFinances().size() == 0) {
				ret.add(log( i, row, "La factura no tiene vencimientos en AON "));
				valid = false;
			} else if (inv.getFinances().size() > 1) {
				ret.add(log( i, row, "La factura tiene más de un vencimiento en AON (" + inv.getFinances().size() + " vencimientos)"));
				valid = false;
			}
			Finance fin = inv.getFinances().get(0);
			if (valid) {
				if (!AonMathUtils.equals(row.getTotalFacturado(), fin.getAmount())) {
					ret.add(log( i, row, "El total vto. no coincide con lo grabado: (Excel: " + row.getTotalFacturado() + ") <> (aon: " + fin.getAmount()+")"));
					valid = false;
				} else if (!fin.isSettled()) {
					ret.add(log( i, row, "El estado del vto. no es \"Saldado\""));
					valid = false;
				}
			}
			if (fractionable) {
				if (AonMathUtils.isZero(row.getTotalRemesar())) {
					ret.add(log( i, row, "El dato total a remesar es zero"));
					valid = false;
				} else if (!AonMathUtils.equals(row.getTotalFacturado(),AonMathUtils.round(row.getAnticipado()+row.getCompensado()+row.getRecobro()+row.getTotalRemesar()))) {
					ret.add(log( i, row, "Los valores indicados en la excel no suman el total facturado"));
					valid = false;
				}
			}
			if (valid) {
				System.out.println( "ROW " + i + " " + row.getRazónSocial());
				ctx.transaction( conf ->{
					String d = row.getFecha();
					int year = AonNumberUtils.toint(AonStringUtils.substringBefore(d, "-"));
					int month = AonNumberUtils.toint(AonStringUtils.substringAfter(d, "-"));
					Date date = Date.from(LocalDateTime.of(year, month, 1, 0, 0).atZone(ZoneId.systemDefault()).toInstant());
					date = AonDateUtils.getMonthLastDay(date);
					if (fractionable) {
						LinkedList<Finance> fractions = new LinkedList<Finance>();
						if (AonMathUtils.isNotZero(row.getTotalRemesar())) {
							Finance fraction0 = copy( fin );
							fraction0.setAmount(row.getTotalRemesar());
							fractions.add(fraction0);	
						}
						if (AonMathUtils.isNotZero(row.getAnticipado())) {
							Finance fraction1 = copy( fin );
							fraction1.setAmount(row.getAnticipado());
							fractions.add(fraction1);	
						}
						if (AonMathUtils.isNotZero(row.getCompensado())) {
							Finance fraction2 = copy( fin );
							fraction2.setAmount(row.getCompensado());
							fractions.add(fraction2);	
						}
						if (AonMathUtils.isNotZero(row.getRecobro())) {
							Finance fraction3 = copy( fin );
							fraction3.setAmount(row.getRecobro());
							fractions.add(fraction3);	
						}
						FinanceTrackingDAO.undo(ctx, fin.getId());
						fractions = FinanceDAO.fraction(ctx, fin, fractions);
						for (Finance fraction : fractions) {
							if (AonMathUtils.equals(row.getTotalRemesar() , fraction.getAmount())) {
								FinanceTracking tracking = new FinanceTracking()
										.setDomain(fraction.getDomain())
										.setFinance(fraction)
										.setTrackingDate( date )
										.setAmount(fraction.getAmount() )
										.setRecorded(false)
										;
								tracking = FinanceTrackingDAO.pay(ctx, tracking);
								tracking = FinanceTrackingDAO.returnFinance(ctx, tracking);
							} else {
								FinanceTrackingDAO.settle(ctx, fraction.getId());
							}
						}
					} else {
						FinanceTrackingDAO.undo(ctx, fin.getId());
						FinanceTracking tracking = new FinanceTracking()
								.setDomain(fin.getDomain())
								.setFinance(fin)
								.setTrackingDate( date )
								.setAmount(fin.getAmount() )
								.setRecorded(false)
								;
						tracking = FinanceTrackingDAO.pay(ctx, tracking);
						tracking = FinanceTrackingDAO.returnFinance(ctx, tracking);
					}
				});
			}
		}
		return ret;
	}

	private static Finance copy(Finance original) {
		return new Finance()	
				.setInvoice(original.getInvoice())
				.setPayMethod(original.getPayMethod())
				.setPayMethodType(original.getPayMethodType())
				.setPayMethodName(original.getPayMethodName())
				.setRegistry(original.getRegistry())
				.setScope(original.getScope())
				.setDomain(original.getDomain())
				.setPayment(original.isPayment())
				.setRegistryDocument(original.getRegistryDocument())
				.setRegistryDocumentType(original.getRegistryDocumentType())
				.setRegistryDocumentCountry(original.getRegistryDocumentCountry())
				.setRegistryName(original.getRegistryName())
				.setRegistryAccountId(original.getRegistryAccountId())
				.setRegistryAccountCode(original.getRegistryAccountCode())
				.setRegistryAccountDescription(original.getRegistryAccountDescription())
				.setConcept(original.getConcept())
				.setDueDate(original.getDueDate())
				.setBankAccount(original.getBankAccount())
				.setBankAlias(original.getBankAlias())
				.setBic(original.getBic())
				.setChequeNumber(original.getChequeNumber())
				.setFinanceStatus(original.getFinanceStatus())
				.setSecurityLevel(original.getSecurityLevel())
				.setRemarks(original.getRemarks())
				.setManual(original.isManual())
				.setAdvance(original.isAdvance())
				.setPayroll(original.isPayroll())
				.setPrepayment(original.isPrepayment())
				.setSourceId(original.getSourceId())
				;
	}

	private static String log(int i, ExcelRow row, String message) {
		String msg = AonStringUtils.rightPad("[Row " + i + "]",12) +
				AonStringUtils.rightPad(row.getNif(),15) +
				AonStringUtils.rightPad(AonStringUtils.abbreviate(row.getRazónSocial(), 50),53) +
				AonStringUtils.rightPad(row.getFacturas(),15) +
				" "  + message;
		return msg;
	}
	// ***************************************************	
	// *************FIN FIx AYUDAT BOORAR ****************
	// ***************************************************
	
}
