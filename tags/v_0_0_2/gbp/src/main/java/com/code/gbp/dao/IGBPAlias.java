package com.code.gbp.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.gbp.Campaign;
import com.code.gbp.CampaignSupplier;
import com.code.gbp.GeoZone;
import com.code.gbp.Incidence;
import com.code.gbp.IncidenceType;
import com.code.gbp.OfferSignature;
import com.code.gbp.Offer;
import com.code.gbp.Office;
import com.code.gbp.ProFormaInvoice;
import com.code.gbp.ProFormaSignature;
import com.code.gbp.Requirement;
import com.code.gbp.Supplier;
import com.code.gbp.SupplierAddInfo;
import com.code.gbp.SupplierContact;
import com.code.gbp.SupplierEconomicData;
import com.code.gbp.SupplierObservation;

/** 
* Interface for holding entity properties constants.
*/ 
public interface IGBPAlias {



	/** 
	* DAOConstantsEntry for Campaign entity.
	*/ 
	DAOConstantsEntry CAMPAIGN_ENTRY = DAOConstants.getDAOConstant(Campaign.class);

	/** 
	* Alias value: Campaign_budget
	* Hibernate value: Campaign.budget
	*/
	String  CAMPAIGN_BUDGET = CAMPAIGN_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Campaign_code
	* Hibernate value: Campaign.code
	*/
	String  CAMPAIGN_CODE = CAMPAIGN_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Campaign_endDate
	* Hibernate value: Campaign.endDate
	*/
	String  CAMPAIGN_END_DATE = CAMPAIGN_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Campaign_id
	* Hibernate value: Campaign.id
	*/
	String  CAMPAIGN_ID = CAMPAIGN_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Campaign_name
	* Hibernate value: Campaign.name
	*/
	String  CAMPAIGN_NAME = CAMPAIGN_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Campaign_offerDueDate
	* Hibernate value: Campaign.offerDueDate
	*/
	String  CAMPAIGN_OFFER_DUE_DATE = CAMPAIGN_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Campaign_startDate
	* Hibernate value: Campaign.startDate
	*/
	String  CAMPAIGN_START_DATE = CAMPAIGN_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Campaign_status
	* Hibernate value: Campaign.status
	*/
	String  CAMPAIGN_STATUS = CAMPAIGN_ENTRY.getAliasNames()[7];



	/** 
	* DAOConstantsEntry for CampaignSupplier entity.
	*/ 
	DAOConstantsEntry CAMPAIGN_SUPPLIER_ENTRY = DAOConstants.getDAOConstant(CampaignSupplier.class);

	/** 
	* Alias value: CampaignSupplier_campaign_id
	* Hibernate value: CampaignSupplier.campaign.id
	*/
	String  CAMPAIGN_SUPPLIER_CAMPAIGN_ID = CAMPAIGN_SUPPLIER_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: CampaignSupplier_campaign_code
	* Hibernate value: CampaignSupplier.campaign.code
	*/
	String  CAMPAIGN_SUPPLIER_CAMPAIGN_CODE = CAMPAIGN_SUPPLIER_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: CampaignSupplier_campaign_startDate
	* Hibernate value: CampaignSupplier.campaign.startDate
	*/
	String  CAMPAIGN_SUPPLIER_CAMPAIGN_START_DATE = CAMPAIGN_SUPPLIER_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: CampaignSupplier_campaign_status
	* Hibernate value: CampaignSupplier.campaign.status
	*/
	String  CAMPAIGN_SUPPLIER_CAMPAIGN_STATUS = CAMPAIGN_SUPPLIER_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: CampaignSupplier_id
	* Hibernate value: CampaignSupplier.id
	*/
	String  CAMPAIGN_SUPPLIER_ID = CAMPAIGN_SUPPLIER_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: CampaignSupplier_supplier_id
	* Hibernate value: CampaignSupplier.supplier.id
	*/
	String  CAMPAIGN_SUPPLIER_SUPPLIER_ID = CAMPAIGN_SUPPLIER_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: CampaignSupplier_supplier_name
	* Hibernate value: CampaignSupplier.supplier.name
	*/
	String  CAMPAIGN_SUPPLIER_SUPPLIER_NAME = CAMPAIGN_SUPPLIER_ENTRY.getAliasNames()[6];



	/** 
	* DAOConstantsEntry for GeoZone entity.
	*/ 
	DAOConstantsEntry GEO_ZONE_ENTRY = DAOConstants.getDAOConstant(GeoZone.class);

	/** 
	* Alias value: GeoZone_id
	* Hibernate value: GeoZone.id
	*/
	String  GEO_ZONE_ID = GEO_ZONE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: GeoZone_name
	* Hibernate value: GeoZone.name
	*/
	String  GEO_ZONE_NAME = GEO_ZONE_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for Incidence entity.
	*/ 
	DAOConstantsEntry INCIDENCE_ENTRY = DAOConstants.getDAOConstant(Incidence.class);

	/** 
	* Alias value: Incidence_campaign_id
	* Hibernate value: Incidence.campaign.id
	*/
	String  INCIDENCE_CAMPAIGN_ID = INCIDENCE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Incidence_description
	* Hibernate value: Incidence.description
	*/
	String  INCIDENCE_DESCRIPTION = INCIDENCE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Incidence_detail
	* Hibernate value: Incidence.detail
	*/
	String  INCIDENCE_DETAIL = INCIDENCE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Incidence_id
	* Hibernate value: Incidence.id
	*/
	String  INCIDENCE_ID = INCIDENCE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Incidence_incidenceDate
	* Hibernate value: Incidence.incidenceDate
	*/
	String  INCIDENCE_INCIDENCE_DATE = INCIDENCE_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Incidence_incidenceType_id
	* Hibernate value: Incidence.incidenceType.id
	*/
	String  INCIDENCE_INCIDENCE_TYPE_ID = INCIDENCE_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Incidence_source
	* Hibernate value: Incidence.source
	*/
	String  INCIDENCE_SOURCE = INCIDENCE_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Incidence_sourceId
	* Hibernate value: Incidence.sourceId
	*/
	String  INCIDENCE_SOURCE_ID = INCIDENCE_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Incidence_supplier_id
	* Hibernate value: Incidence.supplier.id
	*/
	String  INCIDENCE_SUPPLIER_ID = INCIDENCE_ENTRY.getAliasNames()[8];



	/** 
	* DAOConstantsEntry for IncidenceType entity.
	*/ 
	DAOConstantsEntry INCIDENCE_TYPE_ENTRY = DAOConstants.getDAOConstant(IncidenceType.class);

	/** 
	* Alias value: IncidenceType_description
	* Hibernate value: IncidenceType.description
	*/
	String  INCIDENCE_TYPE_DESCRIPTION = INCIDENCE_TYPE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: IncidenceType_id
	* Hibernate value: IncidenceType.id
	*/
	String  INCIDENCE_TYPE_ID = INCIDENCE_TYPE_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for OfferSignature entity.
	*/ 
	DAOConstantsEntry OFFER_SIGNATURE_ENTRY = DAOConstants.getDAOConstant(OfferSignature.class);

	/** 
	* Alias value: OfferSignature_id
	* Hibernate value: OfferSignature.id
	*/
	String  OFFER_SIGNATURE_ID = OFFER_SIGNATURE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: OfferSignature_offer_id
	* Hibernate value: OfferSignature.offer.id
	*/
	String  OFFER_SIGNATURE_OFFER_ID = OFFER_SIGNATURE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: OfferSignature_role
	* Hibernate value: OfferSignature.role
	*/
	String  OFFER_SIGNATURE_ROLE = OFFER_SIGNATURE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: OfferSignature_signatureDate
	* Hibernate value: OfferSignature.signatureDate
	*/
	String  OFFER_SIGNATURE_SIGNATURE_DATE = OFFER_SIGNATURE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: OfferSignature_user
	* Hibernate value: OfferSignature.user
	*/
	String  OFFER_SIGNATURE_USER = OFFER_SIGNATURE_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: OfferSignature_userName
	* Hibernate value: OfferSignature.userName
	*/
	String  OFFER_SIGNATURE_USER_NAME = OFFER_SIGNATURE_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for Offer entity.
	*/ 
	DAOConstantsEntry OFFER_ENTRY = DAOConstants.getDAOConstant(Offer.class);

	/** 
	* Alias value: Offer_additionalConditions
	* Hibernate value: Offer.additionalConditions
	*/
	String  OFFER_ADDITIONAL_CONDITIONS = OFFER_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Offer_campaign_id
	* Hibernate value: Offer.campaign.id
	*/
	String  OFFER_CAMPAIGN_ID = OFFER_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Offer_campaign_code
	* Hibernate value: Offer.campaign.code
	*/
	String  OFFER_CAMPAIGN_CODE = OFFER_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Offer_centralized
	* Hibernate value: Offer.centralized
	*/
	String  OFFER_CENTRALIZED = OFFER_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Offer_costType
	* Hibernate value: Offer.costType
	*/
	String  OFFER_COST_TYPE = OFFER_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Offer_defectiveGoods
	* Hibernate value: Offer.defectiveGoods
	*/
	String  OFFER_DEFECTIVE_GOODS = OFFER_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Offer_deliveryDate
	* Hibernate value: Offer.deliveryDate
	*/
	String  OFFER_DELIVERY_DATE = OFFER_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Offer_id
	* Hibernate value: Offer.id
	*/
	String  OFFER_ID = OFFER_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Offer_offerDate
	* Hibernate value: Offer.offerDate
	*/
	String  OFFER_OFFER_DATE = OFFER_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Offer_office_id
	* Hibernate value: Offer.office.id
	*/
	String  OFFER_OFFICE_ID = OFFER_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Offer_price
	* Hibernate value: Offer.price
	*/
	String  OFFER_PRICE = OFFER_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Offer_quality
	* Hibernate value: Offer.quality
	*/
	String  OFFER_QUALITY = OFFER_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Offer_status
	* Hibernate value: Offer.status
	*/
	String  OFFER_STATUS = OFFER_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Offer_stockProvision
	* Hibernate value: Offer.stockProvision
	*/
	String  OFFER_STOCK_PROVISION = OFFER_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Offer_supplier_id
	* Hibernate value: Offer.supplier.id
	*/
	String  OFFER_SUPPLIER_ID = OFFER_ENTRY.getAliasNames()[14];



	/** 
	* DAOConstantsEntry for Office entity.
	*/ 
	DAOConstantsEntry OFFICE_ENTRY = DAOConstants.getDAOConstant(Office.class);

	/** 
	* Alias value: Office_code
	* Hibernate value: Office.code
	*/
	String  OFFICE_CODE = OFFICE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Office_id
	* Hibernate value: Office.id
	*/
	String  OFFICE_ID = OFFICE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Office_name
	* Hibernate value: Office.name
	*/
	String  OFFICE_NAME = OFFICE_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for ProFormaInvoice entity.
	*/ 
	DAOConstantsEntry PRO_FORMA_INVOICE_ENTRY = DAOConstants.getDAOConstant(ProFormaInvoice.class);

	/** 
	* Alias value: ProFormaInvoice_amount
	* Hibernate value: ProFormaInvoice.amount
	*/
	String  PRO_FORMA_INVOICE_AMOUNT = PRO_FORMA_INVOICE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ProFormaInvoice_campaign_id
	* Hibernate value: ProFormaInvoice.campaign.id
	*/
	String  PRO_FORMA_INVOICE_CAMPAIGN_ID = PRO_FORMA_INVOICE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ProFormaInvoice_campaign_code
	* Hibernate value: ProFormaInvoice.campaign.code
	*/
	String  PRO_FORMA_INVOICE_CAMPAIGN_CODE = PRO_FORMA_INVOICE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ProFormaInvoice_centralized
	* Hibernate value: ProFormaInvoice.centralized
	*/
	String  PRO_FORMA_INVOICE_CENTRALIZED = PRO_FORMA_INVOICE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: ProFormaInvoice_costType
	* Hibernate value: ProFormaInvoice.costType
	*/
	String  PRO_FORMA_INVOICE_COST_TYPE = PRO_FORMA_INVOICE_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: ProFormaInvoice_id
	* Hibernate value: ProFormaInvoice.id
	*/
	String  PRO_FORMA_INVOICE_ID = PRO_FORMA_INVOICE_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: ProFormaInvoice_invoiceDate
	* Hibernate value: ProFormaInvoice.invoiceDate
	*/
	String  PRO_FORMA_INVOICE_INVOICE_DATE = PRO_FORMA_INVOICE_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: ProFormaInvoice_number
	* Hibernate value: ProFormaInvoice.number
	*/
	String  PRO_FORMA_INVOICE_NUMBER = PRO_FORMA_INVOICE_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: ProFormaInvoice_office_id
	* Hibernate value: ProFormaInvoice.office.id
	*/
	String  PRO_FORMA_INVOICE_OFFICE_ID = PRO_FORMA_INVOICE_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: ProFormaInvoice_status
	* Hibernate value: ProFormaInvoice.status
	*/
	String  PRO_FORMA_INVOICE_STATUS = PRO_FORMA_INVOICE_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: ProFormaInvoice_supplier_id
	* Hibernate value: ProFormaInvoice.supplier.id
	*/
	String  PRO_FORMA_INVOICE_SUPPLIER_ID = PRO_FORMA_INVOICE_ENTRY.getAliasNames()[10];



	/** 
	* DAOConstantsEntry for ProFormaSignature entity.
	*/ 
	DAOConstantsEntry PRO_FORMA_SIGNATURE_ENTRY = DAOConstants.getDAOConstant(ProFormaSignature.class);

	/** 
	* Alias value: ProFormaSignature_id
	* Hibernate value: ProFormaSignature.id
	*/
	String  PRO_FORMA_SIGNATURE_ID = PRO_FORMA_SIGNATURE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ProFormaSignature_proFormaInvoice_id
	* Hibernate value: ProFormaSignature.proFormaInvoice.id
	*/
	String  PRO_FORMA_SIGNATURE_PRO_FORMA_INVOICE_ID = PRO_FORMA_SIGNATURE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ProFormaSignature_role
	* Hibernate value: ProFormaSignature.role
	*/
	String  PRO_FORMA_SIGNATURE_ROLE = PRO_FORMA_SIGNATURE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ProFormaSignature_signatureDate
	* Hibernate value: ProFormaSignature.signatureDate
	*/
	String  PRO_FORMA_SIGNATURE_SIGNATURE_DATE = PRO_FORMA_SIGNATURE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: ProFormaSignature_user
	* Hibernate value: ProFormaSignature.user
	*/
	String  PRO_FORMA_SIGNATURE_USER = PRO_FORMA_SIGNATURE_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: ProFormaSignature_userName
	* Hibernate value: ProFormaSignature.userName
	*/
	String  PRO_FORMA_SIGNATURE_USER_NAME = PRO_FORMA_SIGNATURE_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for Requirement entity.
	*/ 
	DAOConstantsEntry REQUIREMENT_ENTRY = DAOConstants.getDAOConstant(Requirement.class);

	/** 
	* Alias value: Requirement_amount
	* Hibernate value: Requirement.amount
	*/
	String  REQUIREMENT_AMOUNT = REQUIREMENT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Requirement_applicationDate
	* Hibernate value: Requirement.applicationDate
	*/
	String  REQUIREMENT_APPLICATION_DATE = REQUIREMENT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Requirement_documentType
	* Hibernate value: Requirement.documentType
	*/
	String  REQUIREMENT_DOCUMENT_TYPE = REQUIREMENT_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Requirement_id
	* Hibernate value: Requirement.id
	*/
	String  REQUIREMENT_ID = REQUIREMENT_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Requirement_managerNumber
	* Hibernate value: Requirement.managerNumber
	*/
	String  REQUIREMENT_MANAGER_NUMBER = REQUIREMENT_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Requirement_supervisorNumber
	* Hibernate value: Requirement.supervisorNumber
	*/
	String  REQUIREMENT_SUPERVISOR_NUMBER = REQUIREMENT_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Requirement_system
	* Hibernate value: Requirement.system
	*/
	String  REQUIREMENT_SYSTEM = REQUIREMENT_ENTRY.getAliasNames()[6];



	/** 
	* DAOConstantsEntry for Supplier entity.
	*/ 
	DAOConstantsEntry SUPPLIER_ENTRY = DAOConstants.getDAOConstant(Supplier.class);

	/** 
	* Alias value: Supplier_address
	* Hibernate value: Supplier.address
	*/
	String  SUPPLIER_ADDRESS = SUPPLIER_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Supplier_bankAccount
	* Hibernate value: Supplier.bankAccount
	*/
	String  SUPPLIER_BANK_ACCOUNT = SUPPLIER_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Supplier_city
	* Hibernate value: Supplier.city
	*/
	String  SUPPLIER_CITY = SUPPLIER_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Supplier_document
	* Hibernate value: Supplier.document
	*/
	String  SUPPLIER_DOCUMENT = SUPPLIER_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Supplier_employeeNumber
	* Hibernate value: Supplier.employeeNumber
	*/
	String  SUPPLIER_EMPLOYEE_NUMBER = SUPPLIER_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Supplier_geozone_id
	* Hibernate value: Supplier.geozone.id
	*/
	String  SUPPLIER_GEOZONE_ID = SUPPLIER_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Supplier_id
	* Hibernate value: Supplier.id
	*/
	String  SUPPLIER_ID = SUPPLIER_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Supplier_name
	* Hibernate value: Supplier.name
	*/
	String  SUPPLIER_NAME = SUPPLIER_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Supplier_passWord
	* Hibernate value: Supplier.passWord
	*/
	String  SUPPLIER_PASS_WORD = SUPPLIER_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Supplier_zip
	* Hibernate value: Supplier.zip
	*/
	String  SUPPLIER_ZIP = SUPPLIER_ENTRY.getAliasNames()[9];



	/** 
	* DAOConstantsEntry for SupplierAddInfo entity.
	*/ 
	DAOConstantsEntry SUPPLIER_ADD_INFO_ENTRY = DAOConstants.getDAOConstant(SupplierAddInfo.class);

	/** 
	* Alias value: SupplierAddInfo_id
	* Hibernate value: SupplierAddInfo.id
	*/
	String  SUPPLIER_ADD_INFO_ID = SUPPLIER_ADD_INFO_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: SupplierAddInfo_supplier_id
	* Hibernate value: SupplierAddInfo.supplier.id
	*/
	String  SUPPLIER_ADD_INFO_SUPPLIER_ID = SUPPLIER_ADD_INFO_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: SupplierAddInfo_type
	* Hibernate value: SupplierAddInfo.type
	*/
	String  SUPPLIER_ADD_INFO_TYPE = SUPPLIER_ADD_INFO_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: SupplierAddInfo_value
	* Hibernate value: SupplierAddInfo.value
	*/
	String  SUPPLIER_ADD_INFO_VALUE = SUPPLIER_ADD_INFO_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for SupplierContact entity.
	*/ 
	DAOConstantsEntry SUPPLIER_CONTACT_ENTRY = DAOConstants.getDAOConstant(SupplierContact.class);

	/** 
	* Alias value: SupplierContact_comment
	* Hibernate value: SupplierContact.comment
	*/
	String  SUPPLIER_CONTACT_COMMENT = SUPPLIER_CONTACT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: SupplierContact_id
	* Hibernate value: SupplierContact.id
	*/
	String  SUPPLIER_CONTACT_ID = SUPPLIER_CONTACT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: SupplierContact_supplier_id
	* Hibernate value: SupplierContact.supplier.id
	*/
	String  SUPPLIER_CONTACT_SUPPLIER_ID = SUPPLIER_CONTACT_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: SupplierContact_type
	* Hibernate value: SupplierContact.type
	*/
	String  SUPPLIER_CONTACT_TYPE = SUPPLIER_CONTACT_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: SupplierContact_value
	* Hibernate value: SupplierContact.value
	*/
	String  SUPPLIER_CONTACT_VALUE = SUPPLIER_CONTACT_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for SupplierEconomicData entity.
	*/ 
	DAOConstantsEntry SUPPLIER_ECONOMIC_DATA_ENTRY = DAOConstants.getDAOConstant(SupplierEconomicData.class);

	/** 
	* Alias value: SupplierEconomicData_data
	* Hibernate value: SupplierEconomicData.data
	*/
	String  SUPPLIER_ECONOMIC_DATA_DATA = SUPPLIER_ECONOMIC_DATA_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: SupplierEconomicData_date
	* Hibernate value: SupplierEconomicData.date
	*/
	String  SUPPLIER_ECONOMIC_DATA_DATE = SUPPLIER_ECONOMIC_DATA_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: SupplierEconomicData_description
	* Hibernate value: SupplierEconomicData.description
	*/
	String  SUPPLIER_ECONOMIC_DATA_DESCRIPTION = SUPPLIER_ECONOMIC_DATA_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: SupplierEconomicData_fileName
	* Hibernate value: SupplierEconomicData.fileName
	*/
	String  SUPPLIER_ECONOMIC_DATA_FILE_NAME = SUPPLIER_ECONOMIC_DATA_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: SupplierEconomicData_id
	* Hibernate value: SupplierEconomicData.id
	*/
	String  SUPPLIER_ECONOMIC_DATA_ID = SUPPLIER_ECONOMIC_DATA_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: SupplierEconomicData_mimeType
	* Hibernate value: SupplierEconomicData.mimeType
	*/
	String  SUPPLIER_ECONOMIC_DATA_MIME_TYPE = SUPPLIER_ECONOMIC_DATA_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: SupplierEconomicData_supplier_id
	* Hibernate value: SupplierEconomicData.supplier.id
	*/
	String  SUPPLIER_ECONOMIC_DATA_SUPPLIER_ID = SUPPLIER_ECONOMIC_DATA_ENTRY.getAliasNames()[6];



	/** 
	* DAOConstantsEntry for SupplierObservation entity.
	*/ 
	DAOConstantsEntry SUPPLIER_OBSERVATION_ENTRY = DAOConstants.getDAOConstant(SupplierObservation.class);

	/** 
	* Alias value: SupplierObservation_id
	* Hibernate value: SupplierObservation.id
	*/
	String  SUPPLIER_OBSERVATION_ID = SUPPLIER_OBSERVATION_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: SupplierObservation_observation
	* Hibernate value: SupplierObservation.observation
	*/
	String  SUPPLIER_OBSERVATION_OBSERVATION = SUPPLIER_OBSERVATION_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: SupplierObservation_observationDate
	* Hibernate value: SupplierObservation.observationDate
	*/
	String  SUPPLIER_OBSERVATION_OBSERVATION_DATE = SUPPLIER_OBSERVATION_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: SupplierObservation_supplier_id
	* Hibernate value: SupplierObservation.supplier.id
	*/
	String  SUPPLIER_OBSERVATION_SUPPLIER_ID = SUPPLIER_OBSERVATION_ENTRY.getAliasNames()[3];


}