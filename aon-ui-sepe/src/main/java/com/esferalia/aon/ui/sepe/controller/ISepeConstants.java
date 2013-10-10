package com.esferalia.aon.ui.sepe.controller;

import com.esferalia.aon.payroll.enumeration.ContrataFileType;

public interface ISepeConstants {
	
	
	// ************************************************************
	// CONTROLLERS
	// ************************************************************
	
	String CERTIFICA2_BATCH_ATTACH_CONTROLLER_NAME = "certifica2BatchAttach";
	String CERTIFICA2_BATCH_DETAIL_CONTROLLER_NAME = "certifica2BatchDetail";
	String CERTIFICA2_BATCH_CONTROLLER_NAME = "certifica2Batch";
	String CERTIFICA2_LIST_CONTROLLER_NAME = "certifica2List";
	
	String CERTIFICATE_FILE_WIZARD = "certifica2FileWizard";
		
	String CONTRACT_CERTIFICADOS_CONTROLLER_NAME = "contractCertificados";
	String CONTRACT_CONTRATA_CONTROLLER_NAME = "contractContrata";
	
	String CONTRATA_BATCH_ATTACH_CONTROLLER_NAME = "contrataBatchAttach";
	String CONTRATA_BATCH_CONTROLLER_NAME = "contrataBatch";
	String CONTRATA_BATCH_DETAIL_CONTROLLER_NAME = "contrataBatchDetail";
	String CONTRATA_COLLECTIONS_CONTROLLER_NAME = "contrataCollections";
	String CONTRATA_LIST_CONTROLLER_NAME = "contrataList";
	
	String EXTENSION_CONTRATA_CONTROLLER_NAME = "extensionContrata";
	
	String SEPE_APP_PARAMS_CONTROLLER_NAME = "sepeAppParams";

	
	// ************************************************************
	// OTHER
	// ************************************************************
	public static final String[] AVAILABLE_CONTRACT_CODE_COMMUNICATION = {
	 "100", "150",  
	 "100", "200", 
	 "401", "402", "410", "501", "502", "510", "540",
	 "410", "510", 
	 "420", "520", 
	 "421", 
	 "430", "530",
	};
	
	public static final String[] AVAILABLE_TRANSFORM_CODE_COMMUNICATION = {
//	"109", 139",
//	"189", 209",
//	"239", 289",
//	"309", 389",
	};
	
	public static final ContrataFileType[] AVAILABLE_CONTRATA_FILE_TYPES = {ContrataFileType.CONTRACT};
	

}
