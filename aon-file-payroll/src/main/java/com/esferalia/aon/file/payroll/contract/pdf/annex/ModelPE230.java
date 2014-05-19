package com.esferalia.aon.file.payroll.contract.pdf.annex;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.RegistryDirStaff;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.payroll.contract.pdf.UnsupportedContractDocumentException;
import com.esferalia.aon.file.payroll.contrata.IContrataParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.ContractInfo;
import com.esferalia.aon.payroll.ContractInfo.ContractVariable;
import com.esferalia.aon.payroll.TrainingCourse;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.TrainingModality;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;



public class ModelPE230 extends AbstractAnnexModel {
	
	// HEADER FIELDS
	public final static String PE230_CP_NO = "m1";
	public final static String PE230_CP_YES = "m2";
	public final static String PE230_FP_NO = "m3";
	public final static String PE230_FP_YES = "m4";
	public final static String PE230_CENTER_AVAILABLE_NO = "m5";
	public final static String PE230_CENTER_AVAILABLE_YES = "m6";

	public final static String PE230_CP_NAME = "certificado profesionalidad";
	public final static String PE230_FP_TITLE = "titulo fp";
	
	// ENTERPRISE FIELDS
	public final static String PE230_ENTERPRISE_NAME = "razon social";
	public final static String PE230_ENTERPRISE_CIF = "cif";
	
	public final static String PE230_ENTERPRISE_DIR_STAFF_NAME = "persona";
	public final static String PE230_ENTERPRISE_DIR_STAFF_NIF = "cif 2";
	public final static String PE230_ENTERPRISE_DIR_STAFF_CHARGE = "cargo";
	
	public final static String PE230_ENTERPRISE_PHONE_1 = "1";
	public final static String PE230_ENTERPRISE_PHONE_2 = "2";
	public final static String PE230_ENTERPRISE_PHONE_3 = "3";
	public final static String PE230_ENTERPRISE_PHONE_4 = "4";
	public final static String PE230_ENTERPRISE_PHONE_5 = "5";
	public final static String PE230_ENTERPRISE_PHONE_6 = "6";
	public final static String PE230_ENTERPRISE_PHONE_7 = "7";
	public final static String PE230_ENTERPRISE_PHONE_8 = "8";
	public final static String PE230_ENTERPRISE_PHONE_9 = "9";
	public final static String PE230_ENTERPRISE_EMAIL = "correo electronico";
	
	// EMPLOYEE FIELDS
	public final static String PE230_EMPLOYEE_NAME = "persona 2";
	public final static String PE230_EMPLOYEE_NIF = "cif_trabajador";

	// CONTRACT FIELDS
	public final static String PE230_CONTRACT_ID_NUMBER_1 = "c1";
	public final static String PE230_CONTRACT_ID_NUMBER_2 = "c2";
	public final static String PE230_CONTRACT_ID_NUMBER_3 = "c3";
	public final static String PE230_CONTRACT_ID_NUMBER_4 = "c4";
	public final static String PE230_CONTRACT_ID_NUMBER_5 = "c5";
	public final static String PE230_CONTRACT_ID_NUMBER_6 = "c6";
	public final static String PE230_CONTRACT_ID_NUMBER_7 = "c7";
	public final static String PE230_CONTRACT_ID_NUMBER_8 = "c8";
	public final static String PE230_CONTRACT_ID_NUMBER_9 = "c9";
	public final static String PE230_CONTRACT_ID_NUMBER_10 = "c10";
	public final static String PE230_CONTRACT_ID_NUMBER_11 = "c11";
	public final static String PE230_CONTRACT_ID_NUMBER_12 = "c12";
	public final static String PE230_CONTRACT_ID_NUMBER_13 = "c13";
	public final static String PE230_CONTRACT_ID_NUMBER_14 = "c14";
	public final static String PE230_CONTRACT_ID_NUMBER_15 = "c15";
	public final static String PE230_CONTRACT_ID_NUMBER_16 = "c16";
	
	public final static String PE230_CONTRACT_START_DATE = "fecha";
	public final static String PE230_CONTRACT_END_DATE = "fecha fin";
	
	public final static String PE230_CONTRACT_OCCUPATION = "puesto trabajo";
	
	public final static String PE230_CONTRACT_CNO_1 = "n1";
	public final static String PE230_CONTRACT_CNO_2 = "n2";
	public final static String PE230_CONTRACT_CNO_3 = "n3";
	public final static String PE230_CONTRACT_CNO_4 = "n4";
	public final static String PE230_CONTRACT_CNO_5 = "n5";
	public final static String PE230_CONTRACT_CNO_6 = "n6";
	public final static String PE230_CONTRACT_CNO_7 = "n7";
	public final static String PE230_CONTRACT_CNO_8 = "n8";
	
	// TRAINING CENTER FIELDS
	public final static String PE230_TRAINING_CENTER_CODE = "numce";
	public final static String PE230_TRAINING_CENTER_DIR_STAFF_NAME = "persona 3";
	public final static String PE230_TRAINING_CENTER_DIR_STAFF_NIF = "cif 3";
	public final static String PE230_TRAINING_CENTER_DIR_STAFF_CHARGE = "cargo 2";
	public final static String PE230_TRAINING_CENTER_NAME = "centro formativo";
	public final static String PE230_TRAINING_CENTER_NIF = "cif 4";
	public final static String PE230_TRAINING_CENTER_ADDRESS = "direccion 2";
	public final static String PE230_TRAINING_CENTER_ZIP_1 = "p1";
	public final static String PE230_TRAINING_CENTER_ZIP_2 = "p2";
	public final static String PE230_TRAINING_CENTER_ZIP_3 = "p3";
	public final static String PE230_TRAINING_CENTER_ZIP_4 = "p4";
	public final static String PE230_TRAINING_CENTER_ZIP_5 = "p5";
	public final static String PE230_TRAINING_CENTER_TOWN = "municipio";
	public final static String PE230_TRAINING_CENTER_PROVINCE = "provincia";
	public final static String PE230_TRAINING_CENTER_PHONE_1 = "l1";
	public final static String PE230_TRAINING_CENTER_PHONE_2 = "l2";
	public final static String PE230_TRAINING_CENTER_PHONE_3 = "l3";
	public final static String PE230_TRAINING_CENTER_PHONE_4 = "l4";
	public final static String PE230_TRAINING_CENTER_PHONE_5 = "l5";
	public final static String PE230_TRAINING_CENTER_PHONE_6 = "l6";
	public final static String PE230_TRAINING_CENTER_PHONE_7 = "l7";
	public final static String PE230_TRAINING_CENTER_PHONE_8 = "l8";
	public final static String PE230_TRAINING_CENTER_PHONE_9 = "l9";
	public final static String PE230_TRAINING_CENTER_EMAIL = "correo electronico 2";
	
	// TRAINING COURSE FIELDS
	public final static String PE230_TRAINING_COURSE_CLASSROOM = "m7";
	public final static String PE230_TRAINING_COURSE_DISTANCE = "m8";
	public final static String PE230_TRAINING_COURSE_PHONE_LEARNING = "m9";
	public final static String PE230_TRAINING_COURSE_MIXED = "m10";
	public final static String PE230_TRAINING_COURSE_START_DATE = "fecha incio";
	public final static String PE230_TRAINING_COURSE_END_DATE = "fecha final";
	public final static String PE230_TRAINING_COURSE_SCHEDULE = "horario";
	public final static String PE230_TRAINING_COURSE_FIRST_YEAR_MAIN_HOURS = "a1";
	public final static String PE230_TRAINING_COURSE_FIRST_YEAR_COMPLEMENTARY_HOURS = "a2";
	public final static String PE230_TRAINING_COURSE_NEXT_YEAR_MAIN_HOURS = "a3";
	public final static String PE230_TRAINING_COURSE_NEXT_YEAR_COMPLEMENTARY_HOURS = "a4";

	// FOORTER FIELDS
	public final static String PE230_SIGNATURE_PLACE = "lugar";
	public final static String PE230_SIGNATURE_DAY = "dia";
	public final static String PE230_SIGNATURE_MONTH = "mes";
	public final static String PE230_SIGNATURE_YEAR = "año";
	public final static String PE230_SIGNATURE_EMPLOYEE = "firma1";
	public final static String PE230_SIGNATURE_EMPLOYEE_DIR_STAFF = "firma2";
	public final static String PE230_SIGNATURE_ENTERPRISE_DIR_STAFF = "firma3";
	public final static String PE230_SIGNATURE_TRAINING_CENTER_DIR_STAFF = "firma4";
	
	public final static String MODEL_NAME = "PE230";
	
	private TrainingCourse trainingCourse;
	private Contract contract;
	
	public ModelPE230(){
		super.documentName = MODEL_NAME;
	}
	
	public void loadPdfFieldValues(ContractCode code, Contract contract, IContrataParams contrataParams) throws UnsupportedContractDocumentException{
		this.contract = contract;
		try {
			Map<String, String> dataMap = getContractDataMap(contract);
			Map<String, String> infoMap = getContractInfoMap(contract);
			PdfReader reader = new PdfReader(getContractModelUrl(documentName+".pdf"));
			
			readPdfFields(reader);
			
			super.loadPdfCommonFields(contract);
			
			trainingCourse = obtainTrainingCourse(infoMap.get(ContractVariable.TRAINING_COURSE.getValue()));
			
			if(trainingCourse != null){
				// HEADER FIELDS
				if(trainingCourse.isProfessionalCertificate()){
					getPdfFieldsMap().get(PE230_CP_YES).setValue("true");
				} else {
					getPdfFieldsMap().get(PE230_CP_NO).setValue("true");
				}
				if(trainingCourse.isFpTitle()){
					getPdfFieldsMap().get(PE230_FP_YES).setValue("true");
				} else {
					getPdfFieldsMap().get(PE230_FP_NO).setValue("true");
				}
				if(trainingCourse.isCenterAvailable()){
					getPdfFieldsMap().get(PE230_CENTER_AVAILABLE_YES).setValue("true");
				} else {
					getPdfFieldsMap().get(PE230_CENTER_AVAILABLE_NO).setValue("true");
				}
				
				getPdfFieldsMap().get(PE230_CP_NAME).setValue(trainingCourse.getCertificationName());
				getPdfFieldsMap().get(PE230_FP_TITLE).setValue(trainingCourse.getFpTitleName());
			}
			
			// ENTERPRISE FIELDS
			getPdfFieldsMap().get(PE230_ENTERPRISE_NAME).setValue(contract.getWorkPlace().getEnterprise().getRegistry().getFullName());
			getPdfFieldsMap().get(PE230_ENTERPRISE_CIF).setValue(contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
			RegistryDirStaff enterpriseDirStaff = obtainRegistryDirStaff(contract.getWorkPlace().getEnterprise().getRegistry()); 
			try {
				getPdfFieldsMap().get(PE230_ENTERPRISE_DIR_STAFF_NAME).setValue(enterpriseDirStaff.getName());
				getPdfFieldsMap().get(PE230_ENTERPRISE_DIR_STAFF_NIF).setValue(enterpriseDirStaff.getDocument());
				String rDirStaddCharge = null;
				if ( enterpriseDirStaff.isShareHolder() ){
					rDirStaddCharge = "Socio";
				} else if ( enterpriseDirStaff.isRepresentative() ){
					rDirStaddCharge = "Apoderado";
				} else if( enterpriseDirStaff.isDirector() ){
					rDirStaddCharge = "Administrador";
				} else if ( enterpriseDirStaff.isRepresentativeLabor() ){
					rDirStaddCharge = "Representante laboral";
				}
				getPdfFieldsMap().get(PE230_ENTERPRISE_DIR_STAFF_CHARGE).setValue(rDirStaddCharge);
			} catch (NullPointerException npe) {
				// do nothing
			}
			
			String phone = contract.getWorkPlace().getEnterprise().getRegistry().getPhone().getValue();
			try {
				phone = phone.replaceAll("[^0-9]", "");
				getPdfFieldsMap().get(PE230_ENTERPRISE_PHONE_1).setValue(phone.substring(0, 1));
				getPdfFieldsMap().get(PE230_ENTERPRISE_PHONE_2).setValue(phone.substring(1, 2));
				getPdfFieldsMap().get(PE230_ENTERPRISE_PHONE_3).setValue(phone.substring(2, 3));
				getPdfFieldsMap().get(PE230_ENTERPRISE_PHONE_4).setValue(phone.substring(3, 4));
				getPdfFieldsMap().get(PE230_ENTERPRISE_PHONE_5).setValue(phone.substring(4, 5));
				getPdfFieldsMap().get(PE230_ENTERPRISE_PHONE_6).setValue(phone.substring(5, 6));
				getPdfFieldsMap().get(PE230_ENTERPRISE_PHONE_7).setValue(phone.substring(6, 7));
				getPdfFieldsMap().get(PE230_ENTERPRISE_PHONE_8).setValue(phone.substring(7, 8));
				getPdfFieldsMap().get(PE230_ENTERPRISE_PHONE_9).setValue(phone.substring(8, 9));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			String email = contract.getWorkPlace().getEnterprise().getRegistry().getEmail().getValue();
			getPdfFieldsMap().get(PE230_ENTERPRISE_EMAIL).setValue(email);
			
			// EMPLOYEE FIELDS
			getPdfFieldsMap().get(PE230_EMPLOYEE_NAME).setValue(contract.getPerson().getFullName());
			getPdfFieldsMap().get(PE230_EMPLOYEE_NIF).setValue(contract.getPerson().getRegistry().getDocument());
			
			// CONTRACT FIELDS
			try {
				getPdfFieldsMap().get(PE230_CONTRACT_ID_NUMBER_1).setValue(infoMap.get(ContractVariable.SEPE_CONTRACT_ID.getValue()).substring(0, 1));
				getPdfFieldsMap().get(PE230_CONTRACT_ID_NUMBER_2).setValue(infoMap.get(ContractVariable.SEPE_CONTRACT_ID.getValue()).substring(1, 2));
				getPdfFieldsMap().get(PE230_CONTRACT_ID_NUMBER_3).setValue(infoMap.get(ContractVariable.SEPE_CONTRACT_ID.getValue()).substring(2, 3));
				getPdfFieldsMap().get(PE230_CONTRACT_ID_NUMBER_4).setValue(infoMap.get(ContractVariable.SEPE_CONTRACT_ID.getValue()).substring(3, 4));
				getPdfFieldsMap().get(PE230_CONTRACT_ID_NUMBER_5).setValue(infoMap.get(ContractVariable.SEPE_CONTRACT_ID.getValue()).substring(4, 5));
				getPdfFieldsMap().get(PE230_CONTRACT_ID_NUMBER_6).setValue(infoMap.get(ContractVariable.SEPE_CONTRACT_ID.getValue()).substring(5, 6));
				getPdfFieldsMap().get(PE230_CONTRACT_ID_NUMBER_7).setValue(infoMap.get(ContractVariable.SEPE_CONTRACT_ID.getValue()).substring(6, 7));
				getPdfFieldsMap().get(PE230_CONTRACT_ID_NUMBER_8).setValue(infoMap.get(ContractVariable.SEPE_CONTRACT_ID.getValue()).substring(7, 8));
				getPdfFieldsMap().get(PE230_CONTRACT_ID_NUMBER_9).setValue(infoMap.get(ContractVariable.SEPE_CONTRACT_ID.getValue()).substring(8, 9));
				getPdfFieldsMap().get(PE230_CONTRACT_ID_NUMBER_10).setValue(infoMap.get(ContractVariable.SEPE_CONTRACT_ID.getValue()).substring(9, 10));
				getPdfFieldsMap().get(PE230_CONTRACT_ID_NUMBER_11).setValue(infoMap.get(ContractVariable.SEPE_CONTRACT_ID.getValue()).substring(10, 11));
				getPdfFieldsMap().get(PE230_CONTRACT_ID_NUMBER_12).setValue(infoMap.get(ContractVariable.SEPE_CONTRACT_ID.getValue()).substring(11, 12));
				getPdfFieldsMap().get(PE230_CONTRACT_ID_NUMBER_13).setValue(infoMap.get(ContractVariable.SEPE_CONTRACT_ID.getValue()).substring(12, 13));
				getPdfFieldsMap().get(PE230_CONTRACT_ID_NUMBER_14).setValue(infoMap.get(ContractVariable.SEPE_CONTRACT_ID.getValue()).substring(13, 14));
				getPdfFieldsMap().get(PE230_CONTRACT_ID_NUMBER_15).setValue(infoMap.get(ContractVariable.SEPE_CONTRACT_ID.getValue()).substring(14, 15));
				getPdfFieldsMap().get(PE230_CONTRACT_ID_NUMBER_16).setValue(infoMap.get(ContractVariable.SEPE_CONTRACT_ID.getValue()).substring(15, 16));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			if(contract.getStartDate()!=null){
				getPdfFieldsMap().get(PE230_CONTRACT_START_DATE).setValue(formatter.format(contract.getStartDate()));
			}
			if(contract.getEndDate()!=null){
				getPdfFieldsMap().get(PE230_CONTRACT_END_DATE).setValue(formatter.format(contract.getEndDate()));
			}
			if(trainingCourse != null){
				getPdfFieldsMap().get(PE230_CONTRACT_OCCUPATION).setValue(trainingCourse.getOccupationName());
				String cno = dataMap.get(ContextVariable.CNO.getName());
				if( !StringUtils.isEmpty(cno) ){
					getPdfFieldsMap().get(PE230_CONTRACT_CNO_1).setValue(cno.substring(0, 1));
					getPdfFieldsMap().get(PE230_CONTRACT_CNO_2).setValue(cno.substring(1, 2));
					getPdfFieldsMap().get(PE230_CONTRACT_CNO_3).setValue(cno.substring(2, 3));
					getPdfFieldsMap().get(PE230_CONTRACT_CNO_4).setValue(cno.substring(3, 4));
					getPdfFieldsMap().get(PE230_CONTRACT_CNO_5).setValue("");
					getPdfFieldsMap().get(PE230_CONTRACT_CNO_6).setValue("");
					getPdfFieldsMap().get(PE230_CONTRACT_CNO_7).setValue("");
					getPdfFieldsMap().get(PE230_CONTRACT_CNO_8).setValue("");
				}
			}
			
			// TRAINING CENTER FIELDS
			RegistryDirStaff trainingCenterDirStaff = null;
			if(trainingCourse != null){
				getPdfFieldsMap().get(PE230_TRAINING_CENTER_CODE).setValue(trainingCourse.getTrainingCenter().getCode());
				trainingCenterDirStaff = obtainRegistryDirStaff(trainingCourse.getTrainingCenter().getRegistry());
				try {
					if(trainingCenterDirStaff != null){
						getPdfFieldsMap().get(PE230_TRAINING_CENTER_DIR_STAFF_NAME).setValue(trainingCenterDirStaff.getName());
						getPdfFieldsMap().get(PE230_TRAINING_CENTER_DIR_STAFF_NIF).setValue(trainingCenterDirStaff.getDocument());
						String rDirStaddCharge = null;
						if ( enterpriseDirStaff.isShareHolder() ){
							rDirStaddCharge = "Socio";
						} else if ( enterpriseDirStaff.isRepresentative() ){
							rDirStaddCharge = "Apoderado";
						} else if( enterpriseDirStaff.isDirector() ){
							rDirStaddCharge = "Administrador";
						} else if ( enterpriseDirStaff.isRepresentativeLabor() ){
							rDirStaddCharge = "Representante laboral";
						}
						getPdfFieldsMap().get(PE230_TRAINING_CENTER_DIR_STAFF_CHARGE).setValue(rDirStaddCharge);
					}
				} catch (NullPointerException npe) {
					// do nothing
				}
				
				try {	
					getPdfFieldsMap().get(PE230_TRAINING_CENTER_NAME).setValue(trainingCourse.getTrainingCenter().getRegistry().getFullName());
				} catch (NullPointerException npe) {
					// do nothing
				}
				try {	
					getPdfFieldsMap().get(PE230_TRAINING_CENTER_NIF).setValue(trainingCourse.getTrainingCenter().getRegistry().getDocument());
				} catch (NullPointerException npe) {
					// do nothing
				}
				try {	
					getPdfFieldsMap().get(PE230_TRAINING_CENTER_ADDRESS).setValue(trainingCourse.getTrainingCenter().getRegistry().getDefaultAddress().getFullAddress());
				} catch (NullPointerException npe) {
					// do nothing
				}
				try {	
					getPdfFieldsMap().get(PE230_TRAINING_CENTER_ZIP_1).setValue(trainingCourse.getTrainingCenter().getRegistry().getDefaultAddress().getZip().substring(0, 1));
					getPdfFieldsMap().get(PE230_TRAINING_CENTER_ZIP_2).setValue(trainingCourse.getTrainingCenter().getRegistry().getDefaultAddress().getZip().substring(1, 2));
					getPdfFieldsMap().get(PE230_TRAINING_CENTER_ZIP_3).setValue(trainingCourse.getTrainingCenter().getRegistry().getDefaultAddress().getZip().substring(2, 3));
					getPdfFieldsMap().get(PE230_TRAINING_CENTER_ZIP_4).setValue(trainingCourse.getTrainingCenter().getRegistry().getDefaultAddress().getZip().substring(3, 4));
					getPdfFieldsMap().get(PE230_TRAINING_CENTER_ZIP_5).setValue(trainingCourse.getTrainingCenter().getRegistry().getDefaultAddress().getZip().substring(4, 5));
				} catch (StringIndexOutOfBoundsException aie) {
					// do nothing
				} catch (NullPointerException npe) {
					// do nothing
				}
				try {	
					getPdfFieldsMap().get(PE230_TRAINING_CENTER_TOWN).setValue(trainingCourse.getTrainingCenter().getRegistry().getDefaultAddress().getCity());
				} catch (NullPointerException npe) {
					// do nothing
				}
				try {	
					getPdfFieldsMap().get(PE230_TRAINING_CENTER_PROVINCE).setValue(trainingCourse.getTrainingCenter().getRegistry().getDefaultAddress().getGeozone().getName());
				} catch (NullPointerException npe) {
					// do nothing
				}
				
				try {
					String centerPhone = trainingCourse.getTrainingCenter().getRegistry().getPhone().getValue();
					centerPhone = centerPhone.replaceAll("[^0-9]", "");
					getPdfFieldsMap().get(PE230_TRAINING_CENTER_PHONE_1).setValue(centerPhone.substring(0, 1));
					getPdfFieldsMap().get(PE230_TRAINING_CENTER_PHONE_2).setValue(centerPhone.substring(1, 2));
					getPdfFieldsMap().get(PE230_TRAINING_CENTER_PHONE_3).setValue(centerPhone.substring(2, 3));
					getPdfFieldsMap().get(PE230_TRAINING_CENTER_PHONE_4).setValue(centerPhone.substring(3, 4));
					getPdfFieldsMap().get(PE230_TRAINING_CENTER_PHONE_5).setValue(centerPhone.substring(4, 5));
					getPdfFieldsMap().get(PE230_TRAINING_CENTER_PHONE_6).setValue(centerPhone.substring(5, 6));
					getPdfFieldsMap().get(PE230_TRAINING_CENTER_PHONE_7).setValue(centerPhone.substring(6, 7));
					getPdfFieldsMap().get(PE230_TRAINING_CENTER_PHONE_8).setValue(centerPhone.substring(7, 8));
					getPdfFieldsMap().get(PE230_TRAINING_CENTER_PHONE_9).setValue(centerPhone.substring(8, 9));
				} catch (StringIndexOutOfBoundsException aie) {
					// do nothing
				} catch (NullPointerException npe) {
					// do nothing
				}
				
				try {	
					String centerEmail = trainingCourse.getTrainingCenter().getRegistry().getEmail().getValue();
					getPdfFieldsMap().get(PE230_TRAINING_CENTER_EMAIL).setValue(centerEmail);
				} catch (NullPointerException npe) {
					// do nothing
				}
				
				// TRAINING COURSE FIELDS
				if(trainingCourse.getModality()==TrainingModality.CLASSROOM){
					getPdfFieldsMap().get(PE230_TRAINING_COURSE_CLASSROOM).setValue("true");
				} else if(trainingCourse.getModality()==TrainingModality.DISTANCE){
					getPdfFieldsMap().get(PE230_TRAINING_COURSE_DISTANCE).setValue("true");
				} else if(trainingCourse.getModality()==TrainingModality.PHONE){
					getPdfFieldsMap().get(PE230_TRAINING_COURSE_PHONE_LEARNING).setValue("true");
				} else if(trainingCourse.getModality()==TrainingModality.MIX){
					getPdfFieldsMap().get(PE230_TRAINING_COURSE_MIXED).setValue("true");
				}
				ContractInfo trainingCourseInfo = obtainContractInfo(contract, ContractVariable.TRAINING_COURSE.getValue());
				if(trainingCourseInfo.getStartDate()!=null){
					getPdfFieldsMap().get(PE230_TRAINING_COURSE_START_DATE).setValue(formatter.format(trainingCourseInfo.getStartDate()));
				}
				if(trainingCourseInfo.getEndDate()!=null){
					getPdfFieldsMap().get(PE230_TRAINING_COURSE_END_DATE).setValue(formatter.format(trainingCourseInfo.getEndDate()));
				}
			}
			getPdfFieldsMap().get(PE230_TRAINING_COURSE_SCHEDULE).setValue(infoMap.get(ContractVariable.TRAINING_SCHEDULE.getValue()));
			if(contrataParams!=null){
				
				Integer durationInMonths = getMonthsBetweenDates(contract.getStartDate(), contract.getEndDate());
				
				if(durationInMonths!=null){
					
					Integer horasFormacion = (durationInMonths<6)?(258):(516);
					
					getPdfFieldsMap().get(PE230_TRAINING_COURSE_FIRST_YEAR_MAIN_HOURS).setValue(String.valueOf(horasFormacion));
				}
			}
			getPdfFieldsMap().get(PE230_TRAINING_COURSE_FIRST_YEAR_COMPLEMENTARY_HOURS).setValue("");
			getPdfFieldsMap().get(PE230_TRAINING_COURSE_NEXT_YEAR_MAIN_HOURS).setValue("");
			getPdfFieldsMap().get(PE230_TRAINING_COURSE_NEXT_YEAR_COMPLEMENTARY_HOURS).setValue("");

			// FOORTER FIELDS
			getPdfFieldsMap().get(PE230_SIGNATURE_PLACE).setValue(contract.getWorkPlace().getAddress().getCity());
			getPdfFieldsMap().get(PE230_SIGNATURE_DAY).setValue(String.valueOf(CommonUtil.getDay(new Date())));
			
			formatter.applyPattern("MMMM");
			getPdfFieldsMap().get(PE230_SIGNATURE_MONTH).setValue(formatter.format(new Date()) );
			formatter.applyPattern("yy");
			getPdfFieldsMap().get(PE230_SIGNATURE_YEAR).setValue(formatter.format(new Date()));
			getPdfFieldsMap().get(PE230_SIGNATURE_EMPLOYEE).setValue(contract.getPerson().getFullName());
			getPdfFieldsMap().get(PE230_SIGNATURE_EMPLOYEE_DIR_STAFF).setValue("");
			try {	
				getPdfFieldsMap().get(PE230_SIGNATURE_ENTERPRISE_DIR_STAFF).setValue(enterpriseDirStaff.getName());
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {	
				getPdfFieldsMap().get(PE230_SIGNATURE_TRAINING_CENTER_DIR_STAFF).setValue(trainingCenterDirStaff.getName());
			} catch (NullPointerException npe) {
				// do nothing
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void afterBuildPdf(PdfReader reader, PdfStamper stamp){
		try {
			if(trainingCourse!=null && trainingCourse.getId()!=null){
				RegistryAttachment attach = obtainRegistrySignature(trainingCourse.getTrainingCenter().getRegistry());
				if(attach!=null && attach.getId()!=null && (attach.getSize()>0)){
					PdfContentByte content = stamp.getOverContent(reader.getNumberOfPages());
					Image image = Image.getInstance(attach.getData());
					AcroFields form = stamp.getAcroFields();
					float absoluteX = form.getFieldPositions(PE230_SIGNATURE_TRAINING_CENTER_DIR_STAFF)[1];
					float absoluteY = form.getFieldPositions(PE230_SIGNATURE_TRAINING_CENTER_DIR_STAFF)[2]; 
					image.setAbsolutePosition(absoluteX, absoluteY+10);
					image.scaleToFit(110, 110);
					content.addImage(image);
				}
			}
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			if(trainingCourse!=null && trainingCourse.getId()!=null){
				RegistryAttachment attach = obtainRegistrySignature(contract.getWorkPlace().getEnterprise().getRegistry());
				if(attach!=null && attach.getId()!=null && (attach.getSize()>0)){
					PdfContentByte content = stamp.getOverContent(reader.getNumberOfPages());
					Image image = Image.getInstance(attach.getData());
					AcroFields form = stamp.getAcroFields();
					float absoluteX = form.getFieldPositions(PE230_SIGNATURE_ENTERPRISE_DIR_STAFF)[1];
					float absoluteY = form.getFieldPositions(PE230_SIGNATURE_ENTERPRISE_DIR_STAFF)[2]; 
					image.setAbsolutePosition(absoluteX, absoluteY+10);
					image.scaleToFit(110, 110);
					content.addImage(image);
				}
			}
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private TrainingCourse obtainTrainingCourse(String value) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(TrainingCourse.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.TRAINING_COURSE_ID), Integer.parseInt(value) );
			Iterator<ITransferObject> it = bean.getList(criteria).iterator();
			while( it.hasNext() ){
				return (TrainingCourse) it.next();
			}
		} catch (ManagerBeanException e) {
			// do nothing ...
		}
		return null;
	}

	public Map<String, String> getContractDataMap(Contract contract) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_CONTRACT_ID), contract.getId());
			criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_START_DATE), contract.getStartDate());
			if(contract.getEndDate()!=null){
				criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_END_DATE), contract.getEndDate());
			} else {
				criteria.addNullExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_END_DATE));
			}
			for(ITransferObject to: bean.getList(criteria)){
				ContractData data = (ContractData) to;
				map.put(data.getName(), data.getExpression().replace('"', ' ').trim());
			}
		} catch (ManagerBeanException e) {
			// NADA, se devuelve un mapa vacio
			return map;
		}
		return map;
	}
	
	public Map<String, String> getContractInfoMap(Contract contract) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractInfo.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_INFO_CONTRACT_ID), contract.getId());
			for(ITransferObject to: bean.getList(criteria)){
				ContractInfo info = (ContractInfo) to;
				map.put(info.getName(), info.getExpression()!=null?info.getExpression().replace('"', ' ').trim():null);
			}
		} catch (ManagerBeanException e) {
			// NADA, se devuelve un mapa vacio
			return map;
		}
		return map;
	}
	
	private ContractData obtainContractData(Contract contract, String name) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_CONTRACT_ID), contract.getId() );
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_NAME), name );
			List<ITransferObject> list = bean.getList(criteria);
			if( !list.isEmpty() ){
				return (ContractData) list.get(0);
			}
		} catch (ManagerBeanException e) {
			// do nothing ...
		}
		return null;
	}
	
	private ContractInfo obtainContractInfo(Contract contract, String name) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractInfo.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_INFO_CONTRACT_ID), contract.getId() );
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_INFO_NAME), name );
			List<ITransferObject> list = bean.getList(criteria);
			if( !list.isEmpty() ){
				return (ContractInfo) list.get(0);
			}
		} catch (ManagerBeanException e) {
			// do nothing ...
		}
		return null;
	}
	
	private Integer getMonthsBetweenDates(Date startDate, Date endDate) {
		if(startDate!=null && endDate!=null){
			return (int) ((CommonUtil.getDaysBetweenDates(startDate, endDate, true))/30);
		}
		return null;
	}
	
	public RegistryAttachment obtainRegistrySignature(Registry registry) throws ManagerBeanException {
		IManagerBean registryAttachBean = BeanManager.getManagerBean(RegistryAttachment.class);
		Criteria criteria = new Criteria();
		String alias = registryAttachBean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ID);
		criteria.addEqualExpression(alias, registry.getId());
		String type = registryAttachBean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE);
		criteria.addEqualExpression(type, RegistryAttachmentType.SIGNATURE);
		Iterator<ITransferObject> iter = registryAttachBean.getList(criteria).iterator();
		if(iter.hasNext()){
			return ((RegistryAttachment)iter.next());
		}
		return null;
	}
	
}
	
	