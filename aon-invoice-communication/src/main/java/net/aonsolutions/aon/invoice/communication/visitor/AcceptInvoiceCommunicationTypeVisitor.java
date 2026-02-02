package net.aonsolutions.aon.invoice.communication.visitor;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationOperation;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType.InvoiceCommunicationTypeVisitor;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.aon.sii.SIIManager;
import net.aonsolutions.aon.tbai.InvoiceCommunication;
import net.aonsolutions.aon.tbai.LroeMain;
import net.aonsolutions.aon.tbai.TBAI;
import net.aonsolutions.aon.tbai.responses.LROEResponse;

public class AcceptInvoiceCommunicationTypeVisitor extends BasicCommunicationInvoiceTypeVisitor implements InvoiceCommunicationTypeVisitor {
		
	public AcceptInvoiceCommunicationTypeVisitor(Occam occam, List<Invoice> invoices) {
		super(occam, invoices);
	}
	
	public AcceptInvoiceCommunicationTypeVisitor(Domain domain, User user, Invoice invoice) {
		super(domain, user, invoice);
	}
	
	public AcceptInvoiceCommunicationTypeVisitor(Domain domain, User user, Invoice invoice, Integer certificateId) {
		super(domain, user, invoice, certificateId);
	}

	@Override
	public void visitSII() throws InvoiceCommunicationException {
		try {
			SIIManager manager = SIIManager.getInstance(getConfiguration());
			
			AccountingReportParams params = new AccountingReportParams();
			params.setDomain(getOccam().getDomain());
			params.setInvoices(AonCollectionUtils.stream(getInvoices()).map(Invoice::getId).toArray(Integer[]::new));
			LinkedList<VatContext> contextList = FISCAL.getSiiVatContext(getOccam(), params, "")
					.collect(Collectors.toCollection(LinkedList::new));		
			manager.suministroFacturas(getDomain(), getOccam().getUser(), getCompany(), getInvoice(), contextList, null);
		} catch (Exception e) {
			if (e instanceof InvoiceCommunicationException ice) {
				throw ice;
			} else {
				throw new InvoiceCommunicationException( e );
			}
		}
	}

	@Override
	public void visitTBAI() throws InvoiceCommunicationException {
		if(InvoiceType.SALES.equals(getInvoice().getType())) {
			try (CloseableAONContext ctx = AONContext.getAONContext(getOccam())) {
				TBAI.accept(ctx, getConfiguration(), getCompany(), getInvoice());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void visitLROE() throws InvoiceCommunicationException {
		if(InvoiceType.SALES.equals(getInvoice().getType())) visitTBAI();
		else {
			Company company = getCompany();
			InvoiceCommunication ic = new InvoiceCommunication()
				.setDomain(company.getDomain())
				.setCompany(company)
				.setInvoice(getInvoice())
				.setOperation(InvoiceCommunicationOperation.REGISTER)
				.setConfiguration(getConfiguration())
				.setType(InvoiceCommunicationType.LROE)
				.setPerson(isPersonaFisica(company.getDocument()) 
						? getPerson(company.getId()) : null)
				.setModel(isPersonaFisica(company.getDocument()) 
						? FiscalModelType.M140 : FiscalModelType.M240);
			
			LroeMain lroe = new LroeMain();
			LROEResponse resp = lroe.alta(ic);
			if(resp.isError()) {
				throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_9000, resp.getErrorMessage());
			}
		}
	}
	
	@Override
	public void visitSERES() {		
		// Not implemented
	}
	
	@Override
	public void visitEMAIL() {		
		// Not implemented
	}
	
	@Override
	public void visitCLOSING() {		
		// Not implemented
	}

	@Override
	public void visitVERIFACTU() {
		throw new AonCoreException("Use InvoiceCommunicator class for VERIFACTU support");
//		try (CloseableAONContext ctx = AONContext.getAONContext(getOccam())) {
//			InvoiceCommunicationConfiguration config = InvoiceCommunicationDAO.get(ctx,ctx.getDomainId());
//			config.setCertificate(getConfiguration().getCertificate());
//			InvoiceCommunicatorContext cc = new InvoiceCommunicatorContext( getDomain(), getUser(), null, getInvoices() );
//			cc.setConfig(config);
//			Company company = CompanyDAO.getByDomain(ctx, getOccam().getDomain());
//			cc.setCompany(company);
//			ctx.getDslContext().transaction(configuration -> VERIFACTU.accept(ctx, cc));
//		}
	}

	@Override
	public void visitNO_VERIFACTU() throws InvoiceCommunicationException {
		// Not implemented
	}

	@Override
	public void visitSIF() throws InvoiceCommunicationException {
		// Not implemented
	}

	@Override
	public void visitFACTURAE() throws InvoiceCommunicationException {
		// Not implemented
	}

}
