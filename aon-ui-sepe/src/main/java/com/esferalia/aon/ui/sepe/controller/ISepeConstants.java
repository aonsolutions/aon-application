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
	String TRANSFORM_CONTRATA_CONTROLLER_NAME = "transformContrata";
	
	String SEPE_APP_PARAMS_CONTROLLER_NAME = "sepeAppParams";

	
	// ************************************************************
	// OTHER
	// ************************************************************
	public static final String[] AVAILABLE_CONTRACT_CODE_COMMUNICATION = {
		// INDEF. TC
		"100", "150",
//		"130",
		// INDEF. TP
		"200",
//		"230", "250",
		// FIJO DISCONT.
//		"300","330", "350",
		// TEMP. TC
		"401", "402", "410", "420", "421", "430",
//		"441", "450", "452"
		// TEMP. TP
		"501", "502", "510", "520", "530", "540",
//		"541", "550", "552" 		
		// OTROS 
//		"970", "980", "990"
		};
	
	public static final String[] AVAILABLE_TRANSFORM_CODE_COMMUNICATION = {
		// INDEF. TC
		"189",
//		"109", "139", 
		// INDEF. TP
		"289",
//		"209", "239", 
		// FIJO DISCONT.
//		"309", "339", "389",
		};
	
	public static final ContrataFileType[] AVAILABLE_CONTRATA_FILE_TYPES = {
		ContrataFileType.CONTRACT, 
//		ContrataFileType.EXTENSION, 
//		ContrataFileType.TRANSFORMATION,
//		ContrataFileType.INDEFINITE_CALL,
//		ContrataFileType.BASIC_COPY,
//		ContrataFileType.GROUP_CONTRACT,
//		ContrataFileType.ADDITIONAL_HOURS,
//		ContrataFileType.OFFICE_CONTRACT,
//		ContrataFileType.LEARNING_ANNEX,
		// CORRECCIONES
//		ContrataFileType.CORRECTION_CONTRACT,
//		ContrataFileType.CORRECTION_EXTENSION,
//		ContrataFileType.CORRECTION_TRANSFORMATION,
//		ContrataFileType.CORRECTION_INDEFINITE_CALL,
//		ContrataFileType.CORRECTION_ADDITIONAL_HOURS,
		};
	

}
