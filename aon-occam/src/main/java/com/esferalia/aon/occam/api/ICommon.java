package com.esferalia.aon.occam.api;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.CertificateInfo;
import com.esferalia.aon.occam.api.model.Cnae;
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
import com.esferalia.aon.occam.api.model.Filter.MailAccountFilter;
import com.esferalia.aon.occam.api.model.Filter.MailTemplateFilter;
import com.esferalia.aon.occam.api.model.Filter.PayrollWorkplaceFilter;
import com.esferalia.aon.occam.api.model.Filter.ProductTagFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryAddInfoFilter;
import com.esferalia.aon.occam.api.model.Filter.SeriesFilter;
import com.esferalia.aon.occam.api.model.Filter.SignatureFilter;
import com.esferalia.aon.occam.api.model.Filter.TagFilter;
import com.esferalia.aon.occam.api.model.Filter.TaxFilter;
import com.esferalia.aon.occam.api.model.Filter.WorkgroupFilter;
import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.Iae;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.MailTemplate;
import com.esferalia.aon.occam.api.model.PayrollWorkplace;
import com.esferalia.aon.occam.api.model.Series;
import com.esferalia.aon.occam.api.model.Signature;
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
import com.esferalia.aon.occam.api.model.registry.RDirStaff;
import com.esferalia.aon.occam.api.model.registry.RecordData;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistrySource;
import com.esferalia.aon.occam.api.model.tag.TagParams;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.watson.error.AonCoreException;

public interface ICommon {

	// --------------------------------------------
	// CONFIGURATION
	// --------------------------------------------
	public AonConfiguration getConfiguration(AONContext ctx, Date atDate);
	public AonConfiguration getConfiguration(AONContext ctx, Date atDate, ConfigBlock ... block );
	public AonConfiguration getConfiguration(AONContext ctx, ConfigParams params);
	public ApplicationParameter saveApplicationParameter(AONContext ctx, ApplicationParameter ap);
	
	// --------------------------------------------
	// APPLICATION PARATEMER
	// --------------------------------------------
	public Stream<ApplicationParameter> getApplicationParameterStream(AONContext ctx, ApplicationParameterFilter filter);
	public void deleteApplicationParameter(AONContext ctx, ApplicationParameterFilter filter);
	
	public ApplicationParameter fetchOne(AONContext ctx, AppParam param);
	public ApplicationParameter insertApplicationParameter(AONContext ctx, String param, String value);
	public ApplicationParameter insertApplicationParameter(AONContext ctx, ApplicationParameter applicationParameter);
	public ApplicationParameter updateApplicationParameter(AONContext ctx, ApplicationParameter applicationParameter, ApplicationParameterFilter filter);

	// --------------------------------------------
	// ENTERPRISE
	// --------------------------------------------
	public Enterprise getEnterprise(AONContext ctx, int id);
	public LinkedList<CompanyBank> getCompanyBanks(AONContext ctx, int enterprise);
	public Company getCompany(AONContext ctx, int domain);
	public LinkedList<CompanyBank> getCompanyBanks(AONContext ctx);

	// --------------------------------------------
	// ENTERPRISE ACTIVITY
	// --------------------------------------------

	public EnterpriseActivity getEnterpriseActivity(AONContext ctx, Integer id);
	public Stream<EnterpriseActivity> getEnterpriseActivities(AONContext ctx, Integer domainId, Date atDate);
	
	// --------------------------------------------
	// WORKPLACE
	// --------------------------------------------
	
	public Stream<Workplace> getWorkplaces(AONContext ctx, Integer domainId);
	public Optional<Workplace> getWorkplace(AONContext ctx, Integer domainId, Integer workplaceId);	
	public Workplace saveWorkplace(AONContext ctx, Workplace workplace);
	public void deleteWorkplace(AONContext ctx, Integer workplaceId);

	@Deprecated	public Workplace getWorkplace(AONContext ctx, WorkplaceFilter filter);
	@Deprecated	public LinkedList<Workplace> getWorkplaceList(AONContext ctx, WorkplaceFilter filter);
	
	// --------------------------------------------
	// PAYROLL WORKPLACE
	// --------------------------------------------
	
	public PayrollWorkplace savePayrollWorkplace(AONContext ctx, PayrollWorkplace workplace);
	public PayrollWorkplace getPayrollWorkpalce(CloseableAONContext ctx, PayrollWorkplaceFilter filter);
	
	// --------------------------------------------
	// PRODUCT
	// --------------------------------------------
	public Stream<ProductTag> getProductTagStream(AONContext ctx, ProductTagFilter filter);
	public List<String> getProductTags(AONContext ctx);

	public Map<Integer, String[]> getProductTagMap(AONContext ctx);

	// --------------------------------------------
	// DOMAIN
	// --------------------------------------------
	
	public LinkedList<Domain> getDriveDomainList(AONContext ctx);
	
	public Domain getDomain(AONContext ctx, Integer domainId);
	public Domain getCompanyDomain(AONContext ctx, String document);
	public Domain getDomain(AONContext ctx, DomainFilter filter);
	public LinkedList<Domain> getDomainList(AONContext ctx, DomainFilter filter);
	public List<Domain> getDomainsByIds(CloseableAONContext ctx, Collection<Integer> domainIds);
	
	public Domain insertDomain(AONContext ctx, Integer parentDomain,
			String document, String name, List<String> messages);
	public Domain insertDomain(AONContext ctx, Domain domain, Registry registry) throws Exception;
	
	public void updateDomainScope(AONContext ctx, Domain domain);
	public void updateDomainOwner(AONContext ctx, String domainName, Integer domainId, String owner);
	public void updateDomainScopeValue(AONContext ctx, String domainName, Integer domainId, Integer scope);

	public DomainGserviceaccount getDomainGserviceaccount(AONContext ctx);
	public DomainGserviceaccount getGeneralDomainGserviceaccount(AONContext ctx);
	public HashMap<Integer, DomainGserviceaccount> getDomainGserviceaccountMap(AONContext ctx, Integer parent);
	public LinkedList<DomainGserviceaccount> getDomainGserviceaccountList(AONContext ctx);
	public DomainGserviceaccount getDomainGserviceaccount(AONContext ctx, DomainGserviceaccountFilter filter);
	public void updateDomainGserviceaccount(AONContext ctx, String googleAccount);
	public void updateDomainGserviceaccount(AONContext ctx, DomainGserviceaccount dgsa);
	public void deleteDomainGserviceaccount(AONContext ctx);
	public void insertDomainGserviceaccount(AONContext ctx, DomainGserviceaccount dgsa);	

	public Integer[] getSonsDomains(AONContext ctx);
	
	// TAG
	public Stream<Tag> getTagStream(AONContext ctx, TagFilter filter);
	public void deleteTag(AONContext ctx, TagFilter filter);
	
	public LinkedList<Tag> getTagList(CloseableAONContext ctx, TagParams params);
	public Tag saveTag(CloseableAONContext ctx, Tag tag);
	
	//TAX
	public Optional<Tax> getTax(AONContext ctx, Integer domainId, Integer taxId);
	public Stream<Tax> getTaxStream(AONContext ctx, Integer domainId);
	public Stream<Tax> getTaxStream(AONContext ctx, Integer domainId, TaxFilter filter);
	public Stream<Tax> getVatStream(AONContext ctx, Integer domainId);
	public Stream<Tax> getWithholdingStream(AONContext ctx, Integer domainId);

	// DATA RESPONSE
	
	public DataRequest getDataRequest(AONContext ctx,DataRequestFilter filter);
	public Stream<DataRequest> getDataRequestStream(AONContext ctx, DataRequestFilter filter);
	public DataRequest saveDataRequest(AONContext ctx, DataRequest dataRequest);
	
	// DATA RESPONSE
	public DataResponse getDataResponse(AONContext ctx, DataResponseFilter filter);
	public DataResponse getLastDataResponse(AONContext ctx, DataResponseFilter filter);
	public Stream<DataResponse> getDataResponseStream(AONContext ctx, DataResponseSource source, DataResponseFilter filter);
	public DataResponse insertDataResponse(AONContext ctx, DataResponse dataResponse);
	public Integer updateDataResponse(AONContext ctx, DataResponse dataResponse, DataResponseFilter filter);
	public DataResponse deleteDataResponse(AONContext ctx, DataResponseFilter filter);
	
	public Stream<DataResponseDetail> getDataResponseDetailStream(AONContext ctx, DataResponseDetailFilter filter);
	public Stream<DataResponseDetail> getLastDataResponseDetailStream(AONContext ctx, DataResponseFilter filter);
	public DataResponseDetail insertDataResponseDetail(AONContext ctx, DataResponseDetail dataResponseDetail);
	public DataResponseDetail updateDataResponseDetail(AONContext ctx, DataResponseDetail dataResponseDetail, DataResponseDetailFilter filter);
	public DataResponseDetail deleteDataResponseDetail(AONContext ctx, DataResponseDetailFilter filter);

	// MAIL TEMPLATE
	
	public Stream<MailTemplate> getMailTemplateStream(AONContext ctx, MailTemplateFilter filter);

	// GEOZONE
	
	public GeoZone get(AONContext ctx, GeoZoneFilter filter);
	
	
	//WORKGROUP
	public Workgroup getWorkgroup(AONContext ctx, WorkgroupFilter filter);
	public Stream<Workgroup> getWorkgroupStream(AONContext ctx, WorkgroupFilter filter);
	public Stream<Workgroup> getWorkgroupByTaskHolderStream(AONContext ctx, WorkgroupFilter filter, Integer taskHolder);
	public List<Workgroup> getWorkgroupList(AONContext ctx, WorkgroupFilter filter);
	public Workgroup saveWorkgroup(AONContext ctx, Workgroup workgroup);
	public void deleteWorkgroup(AONContext ctx, Integer id);
	
	// --------------------------------------------
	// CERTIFICATE
	// --------------------------------------------
	
	public List<Certificate> getCertificates(AONContext ctx, Integer domainId, Integer userId);
	public Stream<Certificate> getAEATCertificates(AONContext ctx, int domain, String user);	
	public List<Certificate> getCertificatesWithParent(AONContext ctx, Integer domainId, Integer parentDomainId, Integer userId);
	public Certificate getCertificate(AONContext ctx, AttachFilter attachFilter);
	public CertificateInfo getCertificateInfo(AONContext ctx, AttachFilter attachFilter) throws IllegalArgumentException;
	public CertificateInfo getCertificateInfo(byte[] data, String password) throws IllegalArgumentException;
	public void deleteCertificate(AONContext ctx, Integer attachId, AttachFilter attachFilter, RegistryAddInfoFilter raddinfoFilter);
	public void saveCertificate(AONContext ctx, Integer domainId, Integer userId, Certificate certificate);
	
	//WORKGROUP
	public List<Cno> getCno(CloseableAONContext ctx);
	
	// COST CENTER
	public List<ApplicationParameter> getCostCenters(CloseableAONContext ctx);
	public void saveCostCenter(CloseableAONContext ctx, ApplicationParameter costCenter);
	public void deleteCostCenter(CloseableAONContext ctx, Integer id);
	
	// SERIES
	Stream<Series> getSeriesStream(AONContext ctx, SeriesFilter filter);
	LinkedList<Series> getSeriesDeliveryList(AONContext ctx, Integer scopeId);
	
	// ACTIVITY SUMMARY
	public List<ActivitySummaryObject> getActivitySummary(CloseableAONContext ctx, Integer domainId, Integer parentDomainId, Integer userId,  ActivitySummaryParams params);
	
	// API CONFIGURATION
	public ApiConfiguration getApiConfiguration(AONContext ctx, Integer domainId);
	
	public List<Activity> getEnterpriseActivities(CloseableAONContext ctx, Integer registry);
	public Activity saveEnterpriseActivity(CloseableAONContext ctx, Activity enterpriseActivity);
	public void deleteEnterpriseActivity(CloseableAONContext ctx, Integer id);
	public List<Cnae2009> getCnae2009List(CloseableAONContext ctx, Integer domainId);
	public List<Cnae> getCnae2025List(CloseableAONContext ctx, Integer domainId);
	public List<Iae> getIaeList(CloseableAONContext ctx, Integer domainId);
	
	public List<RDirStaff> getRDirStaffs(CloseableAONContext ctx, Integer domain, Integer registry);
	public RDirStaff saveRDirStaff(CloseableAONContext ctx, RDirStaff rDirStaff);
	public void deleteRDirStaff(CloseableAONContext ctx, Integer id);
	
	public List<RecordData> getRecordDatas(CloseableAONContext ctx, Integer domain, Integer registry,boolean witdhData);
	public RecordData getFullRecordData(CloseableAONContext ctx, Integer domain, Integer recordDataId);	
	public RecordData saveRecordData(CloseableAONContext ctx, Integer domain, RecordData recordData) throws AonCoreException;
	public void deleteRecordData(CloseableAONContext ctx, Integer domain, Integer id, boolean deleteData);
	public void updateRecordDataAttach(CloseableAONContext ctx, Integer domain, Integer recordDataId, Integer attachId);
	public void deleteRecordDataAttach(CloseableAONContext ctx, Integer domain, Integer recordDataId);
	
	public Account createRegistryAccount(CloseableAONContext ctx, Integer domain, String registryName, String registryAlias, RegistrySource registrySource);
	
	public LinkedList<Signature> getSignatures(CloseableAONContext ctx, SignatureFilter filter);
	public void deleteSignature(CloseableAONContext ctx, Integer id);
	public Signature saveSignature(CloseableAONContext ctx, Signature signature);
	public Signature getSignature(CloseableAONContext ctx, SignatureFilter filter);
	public Signature getSignature(CloseableAONContext ctx, Integer signatureId);
	
	public LinkedList<MailAccount> getMailAccounts(CloseableAONContext ctx, MailAccountFilter filter);
	public void deleteMailAccount(CloseableAONContext ctx, Integer id);
	public MailAccount saveMailAccount(CloseableAONContext ctx, MailAccount mailAccount);
	public MailAccount getMailAccount(CloseableAONContext ctx, Integer signatureId);
	public MailAccount getMailAccount(CloseableAONContext ctx, MailAccountFilter filter);

}
