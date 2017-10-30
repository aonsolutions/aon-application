package com.esferalia.aon.ui.sepe.controller;

import com.esferalia.aon.file.payroll.contract.pdf.ModelOption;
import com.esferalia.aon.payroll.enumeration.ContrataFileType;

public interface ISepeConstants {
	
	
	// ************************************************************
	// CONTROLLERS
	// ************************************************************
	
	String CERTIFICA2_BATCH_ATTACH_CONTROLLER_NAME = "certifica2BatchAttach";
	String CERTIFICA2_BATCH_CONTROLLER_NAME = "certifica2Batch";
	String CERTIFICA2_BATCH_DETAIL_CONTROLLER_NAME = "certifica2BatchDetail";
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
	String TRANSFORM_CONTRATA_CONTROLLER_NAME = "transformContrata";
	String COLLECTIONS_CONTROLLER_NAME = "sepeCollections";

	
	// ************************************************************
	// OTHER
	// ************************************************************
	public static final String[] AVAILABLE_CONTRACT_CODE_COMMUNICATION = {
		// INDEF. TC
		"100", "150",
//		"130",
		// INDEF. TP
		"200","250",
//		"230",
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
		"109", "139", "189",
		// INDEF. TP
		"209", "239", "289",
		// FIJO DISCONT.
		"309", "339", "389",
		};
	
	public static final ContrataFileType[] AVAILABLE_CONTRATA_FILE_TYPES = {
		ContrataFileType.CONTRACT,
		ContrataFileType.EXTENSION,
		ContrataFileType.TRANSFORMATION,
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
	
	public static final ModelOption[] AVAILABLE_CONTRACT_MODEL_OPTIONS = {
		ModelOption.INDEFINITE_OPT1,
		ModelOption.INDEFINITE_OPT2,
//		ModelOption.INDEFINITE_OPT3,
//		ModelOption.INDEFINITE_OPT4,
		ModelOption.INDEFINITE_OPT5,
		ModelOption.INDEFINITE_OPT6,
		ModelOption.INDEFINITE_OPT7,
//		ModelOption.INDEFINITE_OPT8,
//		ModelOption.INDEFINITE_OPT9,
//		ModelOption.INDEFINITE_OPT10,
//		ModelOption.INDEFINITE_OPT11,
//		ModelOption.INDEFINITE_OPT12,
//		ModelOption.INDEFINITE_OPT13,
//		ModelOption.INDEFINITE_OPT14,
		ModelOption.INDEFINITE_OPT15,
//		ModelOption.INDEFINITE_OPT16,
		ModelOption.INDEFINITE_OPT17, 
		ModelOption.LEARNING_OPT1,
//		ModelOption.LEARNING_OPT2,
//		ModelOption.LEARNING_OPT3,
//		ModelOption.LEARNING_OPT4,
		ModelOption.PRACTICE_OPT1,
//		ModelOption.PRACTICE_OPT2,
		ModelOption.PRACTICE_OPT3,
		ModelOption.PRACTICE_OPT4,
		ModelOption.PRACTICE_OPT5,
		ModelOption.TEMPORARY_OPT1,
		ModelOption.TEMPORARY_OPT2,
		ModelOption.TEMPORARY_OPT3,
//		ModelOption.TEMPORARY_OPT4,
//		ModelOption.TEMPORARY_OPT5,
//		ModelOption.TEMPORARY_OPT6,
//		ModelOption.TEMPORARY_OPT7,
		ModelOption.TEMPORARY_OPT8,
//		ModelOption.TEMPORARY_OPT9,
		ModelOption.TEMPORARY_OPT10,
		ModelOption.TEMPORARY_OPT11,
		ModelOption.TEMPORARY_OPT12,
		ModelOption.TEMPORARY_OPT13,
		ModelOption.TEMPORARY_OPT14,
//		ModelOption.TEMPORARY_OPT15,
//		ModelOption.TEMPORARY_OPT16,
//		ModelOption.TEMPORARY_OPT17,
//		ModelOption.TEMPORARY_OPT18,
		ModelOption.INTERNSHIP,
	};

}
