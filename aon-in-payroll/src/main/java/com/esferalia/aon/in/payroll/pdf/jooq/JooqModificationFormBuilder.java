package com.esferalia.aon.in.payroll.pdf.jooq;

import static com.esferalia.aon.occam.api.model.attachment.AttachType.REGISTRY;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Stream;

import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.maker.form.bean.ModificationForm;
import com.esferalia.aon.in.payroll.pdf.maker.modificationform.ContractModificationFormTemplate;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Filter.RegistryAddressFilter;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.Properties.RegistryAddressProperties;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.payroll.Contract;
import com.esferalia.aon.occam.api.model.payroll.ContractData;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.impl.jooq.dao.ContractDAO;
import com.esferalia.aon.occam.impl.jooq.dao.EmployeeDAO;
import com.esferalia.aon.occam.impl.jooq.dao.WorkplaceDAO;
import com.esferalia.aon.watson.util.AonStringUtils;

public class JooqModificationFormBuilder {
	
	public static void createModificationForm(OutputStream os,String domainName, Integer domainId, String login, Integer contractId, String title, String information, Date date) throws CanNotCreatePdfException {
		Contract contract = null;
		try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {			
			Stream<Contract> contractStream = ContractDAO.getContractStream(ctx, f -> f.getIdProperty().eq(contractId));
			if (contractStream != null) {
				contract = contractStream.findFirst().orElse(null);
			}
		}
		if (contract != null) {
			createModificationForm(os, domainName, domainId, login, contract, title, information, date);
		}
	}
	
	public static void createModificationForm(OutputStream os, String domainName, Integer domainId, String login, Contract contract, String title, String information, Date date) throws CanNotCreatePdfException {
		ModificationForm form = new ModificationForm();
		ModificationForm.ClientData employeeData = new ModificationForm.ClientData();
		ModificationForm.CompanyData companyData = new ModificationForm.CompanyData();
		form.setClientData(employeeData);
		form.setCompanyData(companyData);
		
		form.setModificationTitle(title);
		form.setClauses(information);
		form.setModificationDate(date);
		if (contract != null) {
			form.setContractStartDate(contract.getStartDate());
			employeeData.setSeniorityDate(contract.getSeniorityDate());
			companyData.setCcc(contract.getEnterpriseCCC());
			try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
				//----- SEPE ID -----
				ContractData[] contractData = EmployeeDAO.getData(ctx, domainName, contract.getId());
				if (contractData != null && contractData.length > 0) {
					ContractData sepeIdData = Arrays.stream(contractData).filter(cd -> "IDE".equals(cd.getName())).findFirst().orElse(null);
					if (sepeIdData != null && AonStringUtils.isNotBlank(sepeIdData.getExpression())) {
						form.setSepeId(sepeIdData.getExpression());
					}
				}
				
				//-------------------
				//----- EMPLOYEE ----
				//-------------------
				
				if (contract.getPerson() != null && contract.getPerson() > 0) {
					Integer personId = contract.getPerson();
					Optional<Person> optPerson = AON.getPerson(domainName, domainId, login, f -> f.getIdProperty().eq(personId));
					if (optPerson.isPresent()) {
						Person person = optPerson.get();
						employeeData
							.setName(person.getName())
							.setDocument(person.getDocument())
							.setSocialSecurityNum(person.getSocialSecurityNum());
					}
				}
				//--------------------
				//----- COMPANY -----
				Workplace workplace = WorkplaceDAO.get(ctx, contract.getWorkplace(), new Options().setSecurity(false));
				if (workplace != null) {
					CompanyFull companyFull = AON.getCompanyFull(domainName, domainId, login);
					Company company = companyFull.getRegistry()/*AON.getCompany(domainName, domainId, login, f -> f.getIdProperty().eq(workplace.getEnterprise()))*/;
					if (company != null) {
						RegistryAddress address = null;
						if (companyFull.getMainAddress() != null) {
							address = companyFull.getMainAddress();
						} else {
							address = AON.get(domainName, domainId, login, new RegistryAddressFilter() {
								@Override
								public Filter filter(RegistryAddressProperties properties) {
									return properties.getIdProperty().eq(workplace.getAddress() != null 
											? workplace.getAddress().getId() : null);
								}
							});
						}
						companyData
						.setName(company.getName())
						.setDocument(company.getDocument())
						.setAddress(address)
						.setLogo(getAttachBytes(ctx, RegistryAttachmentType.LOGO))
						.setSignature(getAttachBytes(ctx, RegistryAttachmentType.SIGNATURE))
						;
					}
				}
				//-------------------
			}
		}
		try (ContractModificationFormTemplate template = new ContractModificationFormTemplate(form)) {			
			template.print(os);
		} catch (Exception e) {
			throw new CanNotCreatePdfException(e);
		}
	}
	
	private static byte[] getAttachBytes(AONContext aonContext, RegistryAttachmentType attachType) {
		// LOGO
		Optional<InputStream> optLogo = Optional.empty();
		Attach attach = AON.getAttach(
			aonContext.getDomainName(),
			aonContext.getDomainId(),
			aonContext.getUser(),
			f -> f.getTypeProperty().eq(attachType.value()
		)
		.and(f.getDomainProperty()
		.eq(aonContext.getDomainId())), REGISTRY);
		if (attach != null && attach.getData() != null)
				optLogo = Optional.ofNullable(new ByteArrayInputStream(attach.getData()));
		return getBytes(optLogo);
	}
	
	private static byte[] getBytes(Optional<InputStream> optLogo) {
		try {
			return optLogo.isPresent() ? optLogo.get().readAllBytes() : null;
		} catch (IOException e1) {
			return null;
		}
	}
	
	public static void main(String[] args) {
		try (FileOutputStream fos = new FileOutputStream("./ContractModificationFormDBTest.pdf")) {
			createModificationForm(fos, "b72384936-ayudat.aonsolutions.net", 7138, "", 37775, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}
