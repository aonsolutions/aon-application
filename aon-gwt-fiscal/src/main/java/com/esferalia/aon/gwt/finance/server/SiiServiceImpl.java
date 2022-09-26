package com.esferalia.aon.gwt.finance.server;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.SiiService;
import com.esferalia.aon.gwt.fiscal.shared.invoice.ICResponse;
import com.esferalia.aon.gwt.fiscal.shared.invoice.InvoiceParams;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationOperation;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.finance.InvoiceProperties;
import com.esferalia.aon.occam.api.model.finance.SiiConfiguration;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.occam.api.model.security.Certificate;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.tbai.InvoiceCommunication;
import net.aonsolutions.aon.tbai.LroeMain;
import net.aonsolutions.aon.tbai.TbaiMain;
import net.aonsolutions.aon.tbai.lroe.LROE140_1_1;
import net.aonsolutions.aon.tbai.lroe.LROE140_2_1;
import net.aonsolutions.aon.tbai.lroe.LROE240_1_1;
import net.aonsolutions.aon.tbai.lroe.LROE240_2;
import net.aonsolutions.aon.tbai.responses.LROEResponse;

@WebServlet(name = "Sii Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/sii" })
public class SiiServiceImpl extends AonStatelessRemoteServiceServlet implements SiiService {

	private static final long serialVersionUID = 1249978088517559976L;

	@Override
	public SiiConfiguration getSiiConfiguration(String domainName, int domainId, String user) {
		return AON.getSiiConfiguration(domainName, domainId, user);
	}

	@Override
	public List<Invoice> getInvoices(String domainName, int domainId, String user, InvoiceParams params) {
		if(params.isTbaiDeleted()) return getTbaiDeletedInvoices(domainName, domainId, user);
		else return AON_SOLUTIONS.getInvoices(domainName, domainId, user, f -> getFilter(f, params))
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	
	public List<Invoice> getTbaiDeletedInvoices(String domainName, int domainId, String user) {
		List<Invoice> invoices = AON_SOLUTIONS.getTbaiDeletedInvoices(domainName, domainId, user);
		return invoices;
	}
	
	public Filter getFilter(InvoiceProperties f, InvoiceParams params) {
		Filter filter =  f.getDomainProperty().eq(params.getDomain())
				.and(f.getNumberProperty().gt(0));

		if(params.getFrom() != null) {
			filter = filter.and(f.getStartIssueDateProperty().ge(params.getFrom()));
		}
		
		if(params.getTo() != null) {
			filter = filter.and(f.getStartIssueDateProperty().le(params.getTo()));			
		}
		
		if(!params.getType().isEmpty()) {
			Filter aux = f.getTypeProperty().eq(params.getType().get(0).value());
			for (Integer i = 1; i < params.getType().size(); i++) {
				aux = aux.or(f.getTypeProperty().eq(params.getType().get(i).value()));
			}
			filter = filter.and(aux);
		}
		
    	if(!AonStringUtils.isBlank(params.getValue())) {
    		filter = filter.and(
   				f.getReferenceCodeProperty().like("%" + params.getValue() + "%")
   				.or(f.getRegistryNameProperty().like("%" + params.getValue() + "%")));
    	}
    	
    	if(params.getCommunicationType() != null) {
    		filter = filter.and(f.getInvoiceInfoTypeProperty().eq(params.getCommunicationType().value()).or(f.getInvoiceInfoTypeProperty().isNull()));
    	}
    	
    	if(params.getCommunicationStatus() != null && params.getCommunicationStatus().isPending()) {
    		filter = filter.and(f.getInvoiceInfoStatusProperty().eq(params.getCommunicationStatus().value()).or(f.getInvoiceInfoStatusProperty().isNull()));
    	} else if(params.getCommunicationStatus() != null) {
    		filter = filter.and(f.getInvoiceInfoStatusProperty().eq(params.getCommunicationStatus().value())); 
    	}
    	
    	filter = filter.page(params.getPage());
    	filter = filter.perPage(params.getPerPage());
		return filter;
    }

	@Override
	public ICResponse altaLroe140(String domainName, int domainId, String user, InvoiceCommunicationType communicationType, Invoice invoice, AEATParams aeatParams) {
		try {
			Domain domain = AON.getDomain(domainName, domainId, user);
			Company company = AON.getCompanyForDomain(domainName, domainId, user);
			Person person = AON.getPerson(domain, user, f -> f.getIdProperty().eq(company.getId()));
			TbaiConfiguration tbaiConfiguration = AON.getTbaiConfiguration(domain, user);
			Certificate cert = AON.getCertificates(domain, new User().setLogin(user), f -> f.getIdProperty().eq(aeatParams.getCertificateId())).findFirst().orElse(new Certificate());
			tbaiConfiguration.setCertificate(cert);
			invoice = AON_SOLUTIONS.getInvoice(domain.getName(), domain.getId(), user, invoice.getId());
			EnterpriseActivity ea = AON.getEnterpriseActivity(company.getDomain().getName(),
			company.getDomain().getId(), "", invoice.getActivity().getId());
			if(ea == null || ea.getId() == null) {
				ea = AON.getEnterpriseActivities(company.getDomain().getName(),
						company.getDomain().getId(), "").filter(f -> f.isPrincipal()).findFirst().orElse(new EnterpriseActivity());
			}
			invoice.setEpigraph(ea.getIae().getFullEpigraph());
			InvoiceCommunication ic = new InvoiceCommunication()
					.setCompany(company)
					.setPerson(person)
					.setInvoice(invoice)
					.setModel(FiscalModelType.M140)
					.setOperation(InvoiceCommunicationOperation.REGISTER)
					.setTbaiConfiguration(tbaiConfiguration)
					.setType(communicationType);
			
			if(InvoiceCommunicationType.LROE_1_1.equals(communicationType)) {
				TbaiMain tbai = new TbaiMain();
				tbai.createEmisionLROE(company, invoice, tbaiConfiguration);
				return new ICResponse().setError(false);
			} else {
				LroeMain lroe = new LroeMain();
				LROEResponse resp = lroe.alta(ic);
				ICResponse icResponse = new ICResponse();
				icResponse.setError(resp.isError());
				icResponse.setErrorMessage(resp.getErrorMessage());
				return icResponse;
			}
		} catch (Exception e) {
			e.printStackTrace();
			ICResponse icResponse = new ICResponse();
			icResponse.setError(true);
			icResponse.setErrorMessage(e.getMessage());
			return icResponse;
		}
	}

	@Override
	public String bajaLroe140(String domainName, int domainId, String user, InvoiceCommunicationType communicationType, Invoice invoice, AEATParams aeatParams) throws Exception {
		try {
			Domain domain = AON.getDomain(domainName, domainId, user);
			Company company = AON.getCompanyForDomain(domainName, domainId, user);
			Person person = AON.getPerson(domain, user, f -> f.getIdProperty().eq(company.getId()));
			TbaiConfiguration tbaiConfiguration = AON.getTbaiConfiguration(domain, user);
			
			if(InvoiceCommunicationType.LROE_1_1.equals(communicationType)) {
				TbaiMain tbai = new TbaiMain();
				tbai.createAnulacionTBAI(company, invoice, tbaiConfiguration);
			} else if(InvoiceCommunicationType.LROE_2_1.equals(communicationType)) {
				LROE140_2_1 lroe = new LROE140_2_1();
				lroe.anulacion(person, tbaiConfiguration, invoice);
			}
			
			return null;	
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	@Override
	public ICResponse altaLroe240(String domainName, int domainId, String user, InvoiceCommunicationType communicationType, Invoice invoice, AEATParams aeatParams) {
		try {
			Domain domain = AON.getDomain(domainName, domainId, user);
			Company company = AON.getCompanyForDomain(domainName, domainId, user);
			TbaiConfiguration tbaiConfiguration = AON.getTbaiConfiguration(domain, user);
			Certificate cert = AON.getCertificates(domain, new User().setLogin(user), f -> f.getIdProperty().eq(aeatParams.getCertificateId())).findFirst().orElse(new Certificate());
			tbaiConfiguration.setCertificate(cert);
			invoice = AON_SOLUTIONS.getInvoice(domain.getName(), domain.getId(), user, invoice.getId());
			InvoiceCommunication ic = new InvoiceCommunication()
					.setCompany(company)
					.setInvoice(invoice)
					.setModel(FiscalModelType.M240)
					.setOperation(InvoiceCommunicationOperation.REGISTER)
					.setTbaiConfiguration(tbaiConfiguration)
					.setType(communicationType);
			
			if(InvoiceCommunicationType.LROE_1_1.equals(communicationType)) {
				TbaiMain tbai = new TbaiMain();
				tbai.createEmisionLROE(company, invoice, tbaiConfiguration);
				return new ICResponse().setError(false);
			} else {
				LroeMain lroe = new LroeMain();
				LROEResponse resp = lroe.alta(ic);
				ICResponse icResponse = new ICResponse();
				icResponse.setError(resp.isError());
				icResponse.setErrorMessage(resp.getErrorMessage());
				return icResponse;
			}
		} catch (Exception e) {
			e.printStackTrace();
			ICResponse icResponse = new ICResponse();
			icResponse.setError(true);
			icResponse.setErrorMessage(e.getMessage());
			return icResponse;
		}
	}

	@Override
	public String bajaLroe240(String domainName, int domainId, String user, InvoiceCommunicationType communicationType, Invoice invoice, AEATParams aeatParams) throws Exception {
		try {
			Domain domain = AON.getDomain(domainName, domainId, user);
			Company company = AON.getCompanyForDomain(domainName, domainId, user);
			TbaiConfiguration tbaiConfiguration = AON.getTbaiConfiguration(domain, user);
			Certificate cert = AON.getCertificates(domain, new User().setLogin(user), f -> f.getIdProperty().eq(aeatParams.getCertificateId())).findFirst().orElse(new Certificate());
			tbaiConfiguration.setCertificate(cert);
			if(InvoiceCommunicationType.LROE_1_1.equals(communicationType)) {
				TbaiMain tbai = new TbaiMain();
				tbai.createAnulacionTBAI(company, invoice, tbaiConfiguration);
			} else if(InvoiceCommunicationType.LROE_2_1.equals(communicationType)) {
				LROE240_2 lroe = new LROE240_2();
				lroe.anulacion(company, tbaiConfiguration, invoice);
			}
			return null;	
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	@Override
	public String altaSii(String domainName, int domainId, String user, InvoiceCommunicationType communicationType, Invoice invoice, AEATParams aeatParams) {
		return null;
	}

	@Override
	public String bajaSii(String domainName, int domainId, String user, InvoiceCommunicationType communicationType, Invoice invoice, AEATParams aeatParams) {
		return null;
	}

	@Override
	public Boolean refresh140(String domainName, int domainId, String user, Invoice invoice, AEATParams aeatParams) {
		Domain domain = AON.getDomain(domainName, domainId, user);
		Company company = AON.getCompanyForDomain(domainName, domainId, user);
		Person person = AON.getPerson(domain, user, f -> f.getIdProperty().eq(company.getId()));
		TbaiConfiguration tbaiConfiguration = AON.getTbaiConfiguration(domain, user);
		Certificate cert = AON.getCertificates(domain, new User().setLogin(user), f -> f.getIdProperty().eq(aeatParams.getCertificateId())).findFirst().orElse(new Certificate());
		tbaiConfiguration.setCertificate(cert);
		LROE140_1_1 lroe = new LROE140_1_1();
		return lroe.consulta(tbaiConfiguration, person, invoice);
	}

	@Override
	public Boolean refresh240(String domainName, int domainId, String user, Invoice invoice, AEATParams aeatParams) {
		Domain domain = AON.getDomain(domainName, domainId, user);
		Company company = AON.getCompanyForDomain(domainName, domainId, user);
		TbaiConfiguration tbaiConfiguration = AON.getTbaiConfiguration(domain, user);
		Certificate cert = AON.getCertificates(domain, new User().setLogin(user), f -> f.getIdProperty().eq(aeatParams.getCertificateId())).findFirst().orElse(new Certificate());
		tbaiConfiguration.setCertificate(cert);
		LROE240_1_1 lroe = new LROE240_1_1();
		return lroe.consulta(tbaiConfiguration, company, invoice);	
	}
	

}
