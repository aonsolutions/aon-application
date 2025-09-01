package net.aonsolutions.aon.invoice.communication.visitor;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IInvoiceCommunicationTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationOperation;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicatorContext;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceCommunicationConfigurationDAO;
import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.aon.sii.SIIManager;
import net.aonsolutions.aon.tbai.InvoiceCommunication;
import net.aonsolutions.aon.tbai.LroeMain;
import net.aonsolutions.aon.tbai.TBAI;
import net.aonsolutions.aon.tbai.responses.LROEResponse;
import net.aonsolutions.aon.verifactu.VERIFACTU;

public class AcceptInvoiceCommunicationTypeVisitor extends BasicCommunicationInvoiceTypeVisitor implements IInvoiceCommunicationTypeVisitor {
		
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
			SIIManager manager = SIIManager.getInstance(getSiiConfiguration());
			
			AccountingReportParams params = new AccountingReportParams();
			params.setDomain(getOccam().getDomain());
			params.setInvoices(AonCollectionUtils.stream(getInvoices()).map(Invoice::getId).toArray(Integer[]::new));
			LinkedList<VatContext> contextList = FISCAL.getSiiVatContext(getOccam().getDomainName(), getOccam().getDomain(), getOccam().getUser(), params, "")
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
			try {
				TBAI.accept(getTbaiConfiguration(), getCompany(), getInvoice(), 
						getBlockchain(getCertificateId()));
				
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
					.setTbaiConfiguration(getTbaiConfiguration())
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
		try (CloseableAONContext ctx = AONContext.getAONContext(getOccam())) {
			InvoiceCommunicationConfiguration config = InvoiceCommunicationConfigurationDAO.get(ctx,ctx.getDomainId());
			config.setCertificate(getVerifactuConfiguration().getCertificate());
			InvoiceCommunicatorContext cc = new InvoiceCommunicatorContext( getDomain(), getUser(), null, getInvoices() );
			cc.setConfig(config);
			Company company = CompanyDAO.getByDomain(ctx, getOccam().getDomain());
			cc.setCompany(company);
			ctx.getDslContext().transaction(configuration -> VERIFACTU.accept(ctx, cc));
		}
	}

}
