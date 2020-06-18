package com.esferalia.aon.occam.impl.jooq;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.ICommon;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.DataResponseDetail;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.DomainGserviceaccountFilter;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.Filter.ApplicationParameterFilter;
import com.esferalia.aon.occam.api.model.Filter.DataResponseDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.DataResponseFilter;
import com.esferalia.aon.occam.api.model.Filter.DomainFilter;
import com.esferalia.aon.occam.api.model.Filter.MailTemplateFilter;
import com.esferalia.aon.occam.api.model.Filter.ProductTagFilter;
import com.esferalia.aon.occam.api.model.Filter.TagFilter;
import com.esferalia.aon.occam.api.model.Filter.TaxFilter;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.MailTemplate;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.WorkplaceFilter;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.product.ProductTag;
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.impl.jooq.dao.AppParamDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ConfigurationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DataResponseDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;
import com.esferalia.aon.occam.impl.jooq.dao.MailDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ProductDAO;
import com.esferalia.aon.occam.impl.jooq.dao.TagDAO;
import com.esferalia.aon.occam.impl.jooq.dao.TaxDAO;
import com.esferalia.aon.occam.impl.jooq.dao.WorkplaceDAO;

public class CommonImpl implements ICommon {

	// --------------------------- CONFIGURATION
	@Override
	public AonConfiguration getConfiguration(AONContext ctx, Date atDate) {
		return ConfigurationDAO.getConfiguration(ctx, atDate);
	}
	
	// ------------------ APPLICATION PARAMETERS
	@Override
	public Stream<ApplicationParameter> getApplicationParameterStream(AONContext ctx, ApplicationParameterFilter filter) {
		return AppParamDAO.getApplicationParameterStream(ctx, filter);
	}
	
	@Override
	public void deleteApplicationParameter(AONContext ctx, ApplicationParameterFilter filter) {
		ctx.getDslContext().transaction(configuration -> 
				AppParamDAO.deleteApplicationParameter(ctx, filter));
	}
	
	@Override
	public ApplicationParameter fetchOne(AONContext ctx, AppParam param) {
		return AppParamDAO.fetchOne(ctx, param.getValue());
	}

	@Override
	public ApplicationParameter insertApplicationParameter(AONContext ctx, String param, String value) {
		return AppParamDAO.insertApplicationParameter(ctx, param, value);
	}
	
	@Override
	public ApplicationParameter insertApplicationParameter(AONContext ctx, ApplicationParameter applicationParameter) {
		return AppParamDAO.insertApplicationParameter(ctx, applicationParameter);
	}
	
	@Override
	public ApplicationParameter updateApplicationParameter(AONContext ctx, ApplicationParameter applicationParameter, ApplicationParameterFilter filter) {
		return AppParamDAO.updateApplicationParameter(ctx, applicationParameter, filter);
	}
	
	// ------------------ FISCAL PARAMETERS

	@Override
	public FiscalParameters getFiscalParameters(AONContext ctx) {
		return AppParamDAO.getFiscalParameters(ctx);
	}

	// ------------------ ENTERPRISE
	@Override
	public Enterprise getEnterprise(AONContext ctx, int id) {
		return CompanyDAO.getEnterprise(ctx, id);
	}

	@Override
	public LinkedList<Enterprise> getParentEnterprises(AONContext ctx, String query) {
		return CompanyDAO.getParentEnterprises(ctx, p -> (p.getDomainProperty()
				.eq(ctx.getDomainId()).or(p.getParentDomainProperty().eq(
				ctx.getDomainId()))).and(p.getNameProperty().like(query)
				.or(p.getAliasProperty().like(query))
				.or(p.getDocumentProperty().like(query))));
	}

	@Override
	public LinkedList<CompanyBank> getCompanyBanks(AONContext ctx, int enterprise) {
		return CompanyDAO.getBanks(ctx, enterprise);
	}

	@Override
	public LinkedList<CompanyBank> getCompanyBanks(AONContext ctx) {
		Company company = CompanyDAO.getCompany(ctx, ctx.getDomainId());
		return CompanyDAO.getBanks(ctx, company.getId());
	}

	@Override
	public Company getCompany(AONContext ctx, int domain) {
		return CompanyDAO.getCompany(ctx, domain);
	}

	// ------------------ WORKPLACE
	
	public Workplace getWorkplace(AONContext ctx, WorkplaceFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> 
			WorkplaceDAO.getWorkplace(ctx, filter));
	}
	
	public LinkedList<Workplace> getWorkplaceList(AONContext ctx, WorkplaceFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> 
			WorkplaceDAO.getWorkplaceList(ctx, filter));
	}
	
	@Override
	public void updateWorkplace(AONContext ctx, Workplace workplace) {
		ctx.getDslContext().transaction(
			configuration -> WorkplaceDAO.updateWorkplace(ctx, workplace));
	}
	// ------------------ PRODUCT
	@Override
	public List<String> getProductTags(AONContext ctx) {
		return ProductDAO.getProductTags(ctx);
	}

	@Override
	public Stream<ProductTag> getProductTagStream(AONContext ctx, ProductTagFilter filter) {
		return ProductDAO.getProductTagStream(ctx, filter);
	}

	
	@Override
	public Map<Integer, String[]> getProductTagMap(AONContext ctx) {
		return ProductDAO.getProductTagMap(ctx);
	}

	// ------------------ DOMAIN

	@Override
	public Stream<Domain> getDomainStream(AONContext ctx) {
		return ctx.getDslContext().transactionResult(
				configuration -> DomainDAO.getDomainStream(ctx));
	}
	
	@Override
	public Domain insertDomain(AONContext ctx, Integer parentDomain,
			String document, String name, List<String> messages) {
		return DomainDAO.insertDomain(ctx, parentDomain, document, name, messages);

	}

	@Override
	public Domain getDomain(AONContext ctx, Integer domainId) {
		return DomainDAO.getDomain(ctx, domainId);
	}
	
	@Override
	public Domain getCompanyDomain(AONContext ctx, String document) {
		return DomainDAO.getCompanyDomain(ctx, document);
	}
	
	@Override
	public Domain getDomain(AONContext ctx, DomainFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> DomainDAO.getDomain(ctx, filter));
	}
	
	@Override
	public LinkedList<Domain> getDomainList(AONContext ctx, DomainFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> DomainDAO.getDomainList(ctx, filter));
	}

	@Override
	public LinkedList<Domain> getDriveDomainList(AONContext ctx) {
		return ctx.getDslContext().transactionResult(
				configuration -> DomainDAO.getDriveDomainList(ctx));
	}
	
	@Override
	public DomainGserviceaccount getDomainGserviceaccount(AONContext ctx) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> DomainDAO.getDomainGserviceaccount(ctx));
	}
	
	@Override
	public DomainGserviceaccount getGeneralDomainGserviceaccount(AONContext ctx) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> DomainDAO.getGeneralDomainGserviceaccount(ctx));
	}
	
	@Override
	public HashMap<Integer,DomainGserviceaccount> getDomainGserviceaccountMap(AONContext ctx, Integer parent) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> DomainDAO.getDomainGserviceaccountMap(ctx, parent));
	}
	
	@Override
	public LinkedList<DomainGserviceaccount> getDomainGserviceaccountList(AONContext ctx) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> DomainDAO.getDomainGserviceaccountList(ctx));
	}
	
	@Override
	public DomainGserviceaccount getDomainGserviceaccount(AONContext ctx, DomainGserviceaccountFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> DomainDAO.getDomainGserviceaccount(ctx, filter));
	}
	
	@Override
	public void updateDomainGserviceaccount(AONContext ctx, String googleAccount) {
		ctx.getDslContext().transaction(
			configuration -> DomainDAO.updateDomainGserviceaccount(ctx, googleAccount));
	}
	
	@Override
	public void updateDomainGserviceaccount(AONContext ctx, DomainGserviceaccount dgsa) {
		ctx.getDslContext().transaction(
			configuration -> DomainDAO.updateDomainGserviceaccount(ctx, dgsa));
	}
	
	@Override
	public void deleteDomainGserviceaccount(AONContext ctx) {
		ctx.getDslContext().transaction(
			configuration -> DomainDAO.deleteDomainGserviceaccount(ctx));
	}
	@Override
	public void insertDomainGserviceaccount(AONContext ctx, DomainGserviceaccount dgsa){
		ctx.getDslContext().transaction(
			configuration -> DomainDAO.insertDomainGserviceaccount(ctx, dgsa));
	}
	
	@Override
	public Integer[] getSonsDomains(AONContext ctx){
		return ctx.getDslContext().transactionResult( 
				configuration -> DomainDAO.getSonsDomains(ctx));
	}
	
	// ------------------ TAG
	
	@Override
	public Stream<Tag> getTagStream(AONContext ctx, TagFilter filter){
		return ctx.getDslContext().transactionResult(
				configuration -> TagDAO.getTagStream(ctx, filter));
	}

	@Override
	public Tag insertTag(AONContext ctx, Tag tag){
		return ctx.getDslContext().transactionResult(configuration -> 
		 	TagDAO.insertTag(ctx, tag));
	}
	
	@Override
	public void updateTag(AONContext ctx, Tag tag){
		 ctx.getDslContext().transaction(configuration -> 
		 	TagDAO.updateTag(ctx, tag));
	}
	
	@Override
	public void deleteTag(AONContext ctx, TagFilter filter){
		 ctx.getDslContext().transaction(configuration -> 
		 	TagDAO.deleteTag(ctx, filter));
	}

	// ------------------ TAX
	
	@Override
	public Stream<Tax> getTaxStream(AONContext ctx, TaxFilter filter){
		return ctx.getDslContext().transactionResult(
				configuration -> TaxDAO.getTaxs(ctx, filter));
	}

	// ------------------ DATA RESPONSE

	@Override
	public Stream<DataResponse> getDataResponseStream(AONContext ctx, DataResponseSource source, DataResponseFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> DataResponseDAO.getDataResponseStream(ctx, source, filter));
	}
	
	@Override
	public DataResponse insertDataResponse(AONContext ctx, DataResponse dataResponse) {
		return ctx.getDslContext().transactionResult(configuration -> 
					DataResponseDAO.insertDataResponse(ctx, dataResponse));
	}

	@Override
	public Integer updateDataResponse(AONContext ctx, DataResponse dataResponse, DataResponseFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> 
					DataResponseDAO.updateDataResponse(ctx, dataResponse, filter));
	}

	@Override
	public DataResponse deleteDataResponse(AONContext ctx, DataResponseFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> 
					DataResponseDAO.deleteDataResponse(ctx, filter));
	}

	@Override
	public Stream<DataResponseDetail> getDataResponseDetailStream(AONContext ctx, DataResponseDetailFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> DataResponseDAO.getDataResponseDetailStream(ctx, filter));
	}
	
	@Override
	public Stream<DataResponseDetail> getLastDataResponseDetailStream(AONContext ctx, DataResponseFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> DataResponseDAO.getLastDataResponseDetailStream(ctx, filter));
	}

	@Override
	public DataResponseDetail insertDataResponseDetail(AONContext ctx, DataResponseDetail dataResponseDetail) {
		return ctx.getDslContext().transactionResult(configuration -> 
					DataResponseDAO.insertDataResponseDetail(ctx, dataResponseDetail));
	}

	@Override
	public DataResponseDetail updateDataResponseDetail(AONContext ctx, DataResponseDetail dataResponseDetail, DataResponseDetailFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> 
					DataResponseDAO.updateDataResponseDetail(ctx, dataResponseDetail, filter));
	}

	@Override
	public DataResponseDetail deleteDataResponseDetail(AONContext ctx, DataResponseDetailFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> 
					DataResponseDAO.deleteDataResponseDetail(ctx, filter));
	}
	
	@Override
	public Stream<MailTemplate> getMailTemplateStream(AONContext ctx, MailTemplateFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> 
			MailDAO.getMailTemplateStream(ctx, filter));
	}

	
	@Override
	public void updateDomainScope(AONContext ctx, Domain domain){
		 ctx.getDslContext().transaction(configuration -> 
		 	DomainDAO.updateDomainScope(ctx, domain));
	}
}
