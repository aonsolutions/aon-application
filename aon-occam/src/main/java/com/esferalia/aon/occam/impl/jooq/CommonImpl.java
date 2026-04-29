package com.esferalia.aon.occam.impl.jooq;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.ICommon;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.CertificateInfo;
import com.esferalia.aon.occam.api.model.Cnae2009;
import com.esferalia.aon.occam.api.model.Cno;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.DataRequest;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.DataResponseDetail;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.DomainGserviceaccountFilter;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.Filter.ApplicationParameterFilter;
import com.esferalia.aon.occam.api.model.Filter.AttachFilter;
import com.esferalia.aon.occam.api.model.Filter.DataRequestFilter;
import com.esferalia.aon.occam.api.model.Filter.DataResponseDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.DataResponseFilter;
import com.esferalia.aon.occam.api.model.Filter.DomainFilter;
import com.esferalia.aon.occam.api.model.Filter.GeoZoneFilter;
import com.esferalia.aon.occam.api.model.Filter.MailTemplateFilter;
import com.esferalia.aon.occam.api.model.Filter.PayrollWorkplaceFilter;
import com.esferalia.aon.occam.api.model.Filter.ProductTagFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryAddInfoFilter;
import com.esferalia.aon.occam.api.model.Filter.SeriesFilter;
import com.esferalia.aon.occam.api.model.Filter.TagFilter;
import com.esferalia.aon.occam.api.model.Filter.TaxFilter;
import com.esferalia.aon.occam.api.model.Filter.WorkgroupFilter;
import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.Iae;
import com.esferalia.aon.occam.api.model.MailTemplate;
import com.esferalia.aon.occam.api.model.PayrollWorkplace;
import com.esferalia.aon.occam.api.model.Series;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.WorkplaceFilter;
import com.esferalia.aon.occam.api.model.activity.ActivitySummaryObject;
import com.esferalia.aon.occam.api.model.activity.ActivitySummaryParams;
import com.esferalia.aon.occam.api.model.config.ConfigBlock;
import com.esferalia.aon.occam.api.model.config.ConfigParams;
import com.esferalia.aon.occam.api.model.finance.ApiConfiguration;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.payroll.Activity;
import com.esferalia.aon.occam.api.model.product.ProductTag;
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.tag.TagParams;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.impl.jooq.dao.ActivityDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ActivitySummaryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AppParamDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CertificateDAO;
import com.esferalia.aon.occam.impl.jooq.dao.Cnae2009DAO;
import com.esferalia.aon.occam.impl.jooq.dao.CnoDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ConfigurationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CostCenterDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DataRequestDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DataResponseDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;
import com.esferalia.aon.occam.impl.jooq.dao.GeoZoneDAO;
import com.esferalia.aon.occam.impl.jooq.dao.IAEDAO;
import com.esferalia.aon.occam.impl.jooq.dao.MailDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PayrollWorkplaceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ProductOldDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SeriesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.TagDAO;
import com.esferalia.aon.occam.impl.jooq.dao.TaxDAO;
import com.esferalia.aon.occam.impl.jooq.dao.WorkgroupDAO;
import com.esferalia.aon.occam.impl.jooq.dao.WorkplaceDAO;

public class CommonImpl implements ICommon {

	// --------------------------- CONFIGURATION
	@Override
	public AonConfiguration getConfiguration(AONContext ctx, Date atDate) {
		return getConfiguration(ctx, atDate, ConfigBlock.ALL);
	}
	@Override
	public AonConfiguration getConfiguration(AONContext ctx, Date atDate, ConfigBlock... blocks) {
		return ConfigurationDAO.getConfiguration(ctx, new ConfigParams().setAtDate(atDate).setBlocks(blocks));
	}
	@Override
	public ApplicationParameter saveApplicationParameter(AONContext ctx, ApplicationParameter ap) {
		return ctx.getDslContext().transactionResult(configuration -> 
			AppParamDAO.saveApplicationParameter(ctx, ap));
	}
	@Override
	public AonConfiguration getConfiguration(AONContext ctx, ConfigParams params) {
		return ConfigurationDAO.getConfiguration(ctx, params);
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
	
	// ------------------ ENTERPRISE
	@Override
	public Enterprise getEnterprise(AONContext ctx, int id) {
		return CompanyDAO.getEnterprise(ctx, id);
	}

//	@Override
//	public LinkedList<Enterprise> getParentEnterprises(AONContext ctx, String query) {
//		return CompanyDAO.getParentEnterprises(ctx, p -> (p.getDomainProperty()
//				.eq(ctx.getDomainId()).or(p.getParentDomainProperty().eq(
//				ctx.getDomainId()))).and(p.getNameProperty().like(query)
//				.or(p.getAliasProperty().like(query))
//				.or(p.getDocumentProperty().like(query))));
//	}

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
	@Override
	public Optional<Workplace> getWorkplace(AONContext ctx, Integer domainId, Integer workplaceId) {
		return WorkplaceDAO.getWorkplace(ctx, domainId, workplaceId);
	}
	@Override
	public Stream<Workplace> getWorkplaces(AONContext ctx, Integer domainId) {
		return WorkplaceDAO.getWorkplaces(ctx, domainId);
	}		
	
	@Override
	public Workplace saveWorkplace(AONContext ctx, Workplace workplace) {
		return ctx.getDslContext().transactionResult(
			configuration -> WorkplaceDAO.save(ctx, workplace));
	}
	
	@Override
	public void deleteWorkplace(AONContext ctx, Integer workplaceId) {
		ctx.getDslContext().transaction(
			configuration -> WorkplaceDAO.delete(ctx, workplaceId));
	}
	
	@Override
	@Deprecated
	public Workplace getWorkplace(AONContext ctx, WorkplaceFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> 
			WorkplaceDAO.getWorkplace(ctx, filter));
	}
	
	@Override
	@Deprecated
	public LinkedList<Workplace> getWorkplaceList(AONContext ctx, WorkplaceFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> 
			WorkplaceDAO.getWorkplaceList(ctx, filter));
	}

	
	// ---------- PAYROLL WORKPLACE
	
	@Override
	public PayrollWorkplace savePayrollWorkplace(AONContext ctx, PayrollWorkplace payrollWorkplace) {
		return ctx.getDslContext().transactionResult(
			configuration -> PayrollWorkplaceDAO.save(ctx, payrollWorkplace));
	}
	

	@Override
	public PayrollWorkplace getPayrollWorkpalce(CloseableAONContext ctx, PayrollWorkplaceFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> PayrollWorkplaceDAO.get(ctx, filter));
	}
	
	
	// ------------------ PRODUCT
	@Override
	public List<String> getProductTags(AONContext ctx) {
		return ProductOldDAO.getProductTags(ctx);
	}

	@Override
	public Stream<ProductTag> getProductTagStream(AONContext ctx, ProductTagFilter filter) {
		return ProductOldDAO.getProductTagStream(ctx, filter);
	}

	
	@Override
	public Map<Integer, String[]> getProductTagMap(AONContext ctx) {
		return ProductOldDAO.getProductTagMap(ctx);
	}

	// ------------------ DOMAIN
	
	@Override
	public Domain insertDomain(AONContext ctx, Integer parentDomain,
			String document, String name, List<String> messages) {
		return DomainDAO.insertDomain(ctx, parentDomain, document, name, messages);

	}
	
	@Override
	public Domain insertDomain(AONContext ctx, Domain domain, Registry registry) throws Exception {
		return DomainDAO.insertDomain(ctx, domain, registry);

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
	public void deleteTag(AONContext ctx, TagFilter filter){
		 ctx.getDslContext().transaction(configuration -> 
		 	TagDAO.deleteTag(ctx, filter));
	}
	
	@Override
	public LinkedList<Tag> getTagList(CloseableAONContext ctx, TagParams params) {
		return ctx.getDslContext().transactionResult(
				configuration -> TagDAO.getList(ctx, params));
	}
	
	@Override
	public Tag saveTag(CloseableAONContext ctx, Tag tag) {
		return ctx.getDslContext().transactionResult(
				configuration -> TagDAO.save(ctx, tag));
	}

	// ------------------ TAX
	
	@Override
	public Stream<Tax> getTaxStream(AONContext ctx, Integer domainId){
		return ctx.getDslContext().transactionResult(
			configuration -> TaxDAO.stream(ctx, domainId ));
	}
	
	@Override
	public Stream<Tax> getTaxStream(AONContext ctx, Integer domainId, TaxFilter filter){
		return ctx.getDslContext().transactionResult(
			configuration -> TaxDAO.stream(ctx, domainId, filter));
	}

	@Override
	public Stream<Tax> getVatStream(AONContext ctx, Integer domainId){
		return ctx.getDslContext().transactionResult(
			configuration -> TaxDAO.getVatTaxes(ctx, domainId));
	}
	
	@Override
	public Stream<Tax> getWithholdingStream(AONContext ctx, Integer domainId){
		return ctx.getDslContext().transactionResult(
			configuration -> TaxDAO.getWithholdingTaxes(ctx, domainId));
	}
	
	@Override
	public Optional<Tax> getTax(AONContext ctx, Integer domainId, Integer taxId) {
		return ctx.getDslContext().transactionResult(
			configuration -> TaxDAO.get(ctx, domainId, taxId));
	}

	// ------------------ DATA REQUEST
	
	@Override
	public DataRequest getDataRequest(AONContext ctx, DataRequestFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> DataRequestDAO.get(ctx, filter));	
	}
	
	@Override
	public Stream<DataRequest> getDataRequestStream(AONContext ctx, DataRequestFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> DataRequestDAO.getStream(ctx, filter));	
	}
	
	@Override
	public DataRequest saveDataRequest(AONContext ctx, DataRequest dataRequest) {
		return ctx.getDslContext().transactionResult(
				configuration -> DataRequestDAO.save(ctx, dataRequest));	
	}
	
	
	// ------------------ DATA RESPONSE

	@Override
	public DataResponse getLastDataResponse(AONContext ctx, DataResponseFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> DataResponseDAO.getLast(ctx, filter));
	}
	
	@Override
	public DataResponse getDataResponse(AONContext ctx, DataResponseFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> DataResponseDAO.get(ctx, filter));
	}
	
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
	
	@Override
	public void updateDomainOwner(AONContext ctx, String domainName, Integer domainId, String owner){
		 ctx.getDslContext().transaction(configuration -> 
		 	DomainDAO.updateDomainOwner(ctx, domainName, domainId, owner));
	}
	
	@Override
	public void updateDomainScopeValue(AONContext ctx, String domainName, Integer domainId, Integer scope){
		 ctx.getDslContext().transaction(configuration -> 
		 	DomainDAO.updateDomainScopeValue(ctx, domainName, domainId, scope));
	}

	@Override
	public GeoZone get(AONContext ctx, GeoZoneFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> 
			GeoZoneDAO.get(ctx, filter));
	}

	@Override
	public EnterpriseActivity getEnterpriseActivity(AONContext ctx, Integer id) {
		return ctx.getDslContext().transactionResult(configuration -> 
			CompanyDAO.getEnterpriseActivity(ctx, id));
	}
	
	@Override
	public Stream<EnterpriseActivity> getEnterpriseActivities(AONContext ctx, Integer domainId, Date atDate) {
		return ctx.getDslContext().transactionResult(configuration -> 
			CompanyDAO.getEnterpriseActivities(ctx, domainId, atDate));
	}

	@Override
	public Workgroup getWorkgroup(AONContext ctx, WorkgroupFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> 
		WorkgroupDAO.get(ctx, filter));
	}

	@Override
	public Stream<Workgroup> getWorkgroupStream(AONContext ctx, WorkgroupFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> 
		WorkgroupDAO.getStream(ctx, filter));
	}
	
	@Override
	public Stream<Workgroup> getWorkgroupByTaskHolderStream(AONContext ctx, WorkgroupFilter filter, Integer taskHolder) {
		return ctx.getDslContext().transactionResult(configuration -> 
		WorkgroupDAO.getWorkgroupByTaskHolderStream(ctx, filter, taskHolder));
	}

	@Override
	public List<Workgroup> getWorkgroupList(AONContext ctx, WorkgroupFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> 
		WorkgroupDAO.getList(ctx, filter));
	}

	@Override
	public Workgroup saveWorkgroup(AONContext ctx, Workgroup workgroup) {
		return ctx.getDslContext().transactionResult(configuration -> 
		WorkgroupDAO.save(ctx, workgroup));
	}

	@Override
	public void deleteWorkgroup(AONContext ctx, Integer id) {
		 ctx.getDslContext().transaction(configuration -> 
		 WorkgroupDAO.delete(ctx, id));
	}
	
	// -------------------- Certificate
	
	@Override
	public List<Certificate> getCertificates(AONContext ctx, Integer domainId, Integer userId) throws IllegalArgumentException {
		return ctx.getDslContext().transactionResult(configuration -> 
		CertificateDAO.getList(ctx, domainId, userId));
	}
	
	@Override
	public Stream<Certificate> getAEATCertificates(AONContext ctx, int domain, String user) {
		return CertificateDAO.getAEATCertificates(ctx, domain, user);
	}
	
	@Override
	public List<Certificate> getCertificatesWithParent(AONContext ctx, Integer domainId, Integer parentDomainId, Integer userId) throws IllegalArgumentException {
		return ctx.getDslContext().transactionResult(configuration -> 
		CertificateDAO.getListWithParent(ctx, domainId, parentDomainId, userId));
	}
	
	@Override
	public Certificate getCertificate(AONContext ctx, AttachFilter attachFilter) {
		return ctx.getDslContext().transactionResult(configuration -> 
		CertificateDAO.get(ctx, attachFilter));
	}
	
	@Override
	public CertificateInfo getCertificateInfo(AONContext ctx, AttachFilter attachFilter) throws IllegalArgumentException {
		return ctx.getDslContext().transactionResult(configuration -> 
		CertificateDAO.getInfo(ctx, attachFilter));
	}
	
	@Override
	public CertificateInfo getCertificateInfo(byte[] data, String password) throws IllegalArgumentException {
		return CertificateDAO.verifyCertificate(data, password);
	}
	
	@Override
	public void deleteCertificate(AONContext ctx, Integer attachId, AttachFilter attachFilter, RegistryAddInfoFilter raddinfoFilter) {
		ctx.getDslContext().transaction(configuration -> 
		CertificateDAO.delete(ctx, attachId, attachFilter, raddinfoFilter));
	}
	
	@Override
	public void saveCertificate(AONContext ctx, Integer domainId, Integer userId, Certificate certificate) {
		ctx.getDslContext().transaction(configuration -> 
		CertificateDAO.save(ctx, domainId, userId, certificate));
	}
	
	// -------------------- Cno
	
	@Override
	public List<Cno> getCno(CloseableAONContext ctx) {
		return ctx.getDslContext().transactionResult(configuration -> CnoDAO.getList(ctx));
	}
	
	// -------------------- Cost Center
	
	@Override
	public List<ApplicationParameter> getCostCenters(CloseableAONContext ctx) {
		return ctx.getDslContext().transactionResult(configuration -> CostCenterDAO.getList(ctx));
	}
	
	@Override
	public void saveCostCenter(CloseableAONContext ctx, ApplicationParameter costCenter) {
		ctx.getDslContext().transaction(configuration -> 
		CostCenterDAO.save(ctx, costCenter));
	}
	
	@Override
	public void deleteCostCenter(CloseableAONContext ctx, Integer id) {
		ctx.getDslContext().transaction(configuration -> 
		CostCenterDAO.delete(ctx, id));
	}
	
	// -------------------- Series

	@Override
	public LinkedList<Series> getSeriesDeliveryList(AONContext ctx, Integer scopeId){
		return ctx.getDslContext().transactionResult(configuration ->
				SeriesDAO.getSeriesDeliveryList(ctx, scopeId));
	}
	
	@Override
	public Stream<Series> getSeriesStream(AONContext ctx, SeriesFilter filter){
		return ctx.getDslContext().transactionResult(configuration ->
				SeriesDAO.getSeries(ctx, filter));
	}
	
	// -------------------- ACTIVITY SUMMARY
	
	@Override
	public List<ActivitySummaryObject> getActivitySummary(CloseableAONContext ctx, Integer domainId, Integer parentDomainId, Integer userId,  ActivitySummaryParams params) {
		return ctx.getDslContext().transactionResult(configuration ->
			ActivitySummaryDAO.getActivitySummary(ctx, domainId, parentDomainId, userId, params));
	}
	
	// API CONFIGURATION
	@Override
	public ApiConfiguration getApiConfiguration(AONContext ctx, Integer domainId) {
		return ConfigurationDAO.getApiConfiguration(ctx, domainId);
	}
	
	@Override
	public List<Activity> getEnterpriseActivities(CloseableAONContext ctx, Integer registry) {
		return ctx.getDslContext().transactionResult(configuration -> ActivityDAO.getList(ctx, f -> f.getEnterpriseProperty().eq(registry)));
	}
	
	@Override
	public Activity saveEnterpriseActivity(CloseableAONContext ctx, Activity enterpriseActivity) {
		return ctx.getDslContext().transactionResult(configuration -> ActivityDAO.save(ctx, enterpriseActivity));
	}
	
	@Override
	public void deleteEnterpriseActivity(CloseableAONContext ctx, Integer id) {
		ctx.getDslContext().transaction(configuration -> ActivityDAO.delete(ctx, id));
	}
	
	@Override
	public List<Cnae2009> getCnae2009List(CloseableAONContext ctx, Integer domainId) {
		return ctx.getDslContext().transactionResult(configuration -> Cnae2009DAO.getStream(ctx, f -> f.getIdProperty().ge(0)).collect(Collectors.toList()));
	}
	
	@Override
	public List<Iae> getIaeList(CloseableAONContext ctx, Integer domainId) {
		return ctx.getDslContext().transactionResult(configuration -> IAEDAO.getStream(ctx, f -> f.getIdProperty().ge(0)).collect(Collectors.toList()));
	}
	
}
