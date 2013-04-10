package com.esferalia.aon.file.payroll.contract.pdf.annex;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryDirStaff;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.payroll.contract.pdf.UnsupportedContractDocumentException;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.TrainingCenter;
import com.esferalia.aon.payroll.TrainingCourse;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.TrainingModality;
import com.lowagie.text.pdf.PdfReader;



public class ModelPE230 extends AbstractAnnexModel {
	
	// HEADER FIELDS
	final static String PE230_CP_NO = "m1";
	final static String PE230_CP_YES = "m2";
	final static String PE230_FP_NO = "m3";
	final static String PE230_FP_YES = "m4";
	final static String PE230_CENTER_AVAILABLE_NO = "m5";
	final static String PE230_CENTER_AVAILABLE_YES = "m6";

	final static String PE230_CP_NAME = "certificado profesionalidad";
	final static String PE230_FP_TITLE = "titulo fp";
	
	// ENTERPRISE FIELDS
	final static String PE230_ENTERPRISE_NAME = "razon social";
	final static String PE230_ENTERPRISE_CIF = "cif";
	
	final static String PE230_ENTERPRISE_DIR_STAFF_NAME = "persona";
	final static String PE230_ENTERPRISE_DIR_STAFF_NIF = "cif 2";
	final static String PE230_ENTERPRISE_DIR_STAFF_CHARGE = "cargo";
	
	final static String PE230_ENTERPRISE_PHONE_1 = "1";
	final static String PE230_ENTERPRISE_PHONE_2 = "2";
	final static String PE230_ENTERPRISE_PHONE_3 = "3";
	final static String PE230_ENTERPRISE_PHONE_4 = "4";
	final static String PE230_ENTERPRISE_PHONE_5 = "5";
	final static String PE230_ENTERPRISE_PHONE_6 = "6";
	final static String PE230_ENTERPRISE_PHONE_7 = "7";
	final static String PE230_ENTERPRISE_PHONE_8 = "8";
	final static String PE230_ENTERPRISE_PHONE_9 = "9";
	final static String PE230_ENTERPRISE_EMAIL = "correo electronico";
	
	// EMPLOYEE FIELDS
	final static String PE230_EMPLOYEE_NAME = "persona 2";
	final static String PE230_EMPLOYEE_NIF = "cif_trabajador";

	// CONTRACT FIELDS
	final static String PE230_COTNRACT_ID_NUMBER_1 = "c1";
	final static String PE230_COTNRACT_ID_NUMBER_2 = "c2";
	final static String PE230_COTNRACT_ID_NUMBER_3 = "c3";
	final static String PE230_COTNRACT_ID_NUMBER_4 = "c4";
	final static String PE230_COTNRACT_ID_NUMBER_5 = "c5";
	final static String PE230_COTNRACT_ID_NUMBER_6 = "c6";
	final static String PE230_COTNRACT_ID_NUMBER_7 = "c7";
	final static String PE230_COTNRACT_ID_NUMBER_8 = "c8";
	final static String PE230_COTNRACT_ID_NUMBER_9 = "c9";
	final static String PE230_COTNRACT_ID_NUMBER_10 = "c10";
	final static String PE230_COTNRACT_ID_NUMBER_11 = "c11";
	final static String PE230_COTNRACT_ID_NUMBER_12 = "c12";
	final static String PE230_COTNRACT_ID_NUMBER_13 = "c13";
	final static String PE230_COTNRACT_ID_NUMBER_14 = "c14";
	final static String PE230_COTNRACT_ID_NUMBER_15 = "c15";
	final static String PE230_COTNRACT_ID_NUMBER_16 = "c16";
	
	final static String PE230_COTNRACT_START_DATE = "fecha";
	final static String PE230_COTNRACT_END_DATE = "fecha fin";
	
	final static String PE230_COTNRACT_OCCUPATION = "puesto trabajo";
	
	final static String PE230_COTNRACT_CNO_1 = "n1";
	final static String PE230_COTNRACT_CNO_2 = "n2";
	final static String PE230_COTNRACT_CNO_3 = "n3";
	final static String PE230_COTNRACT_CNO_4 = "n4";
	final static String PE230_COTNRACT_CNO_5 = "n5";
	final static String PE230_COTNRACT_CNO_6 = "n6";
	final static String PE230_COTNRACT_CNO_7 = "n7";
	final static String PE230_COTNRACT_CNO_8 = "n8";
	
	// TRAINING CENTER FIELDS
	final static String PE230_TRAINING_CENTER_CODE = "numce";
	final static String PE230_TRAINING_CENTER_DIR_STAFF_NAME = "persona 3";
	final static String PE230_TRAINING_CENTER_DIR_STAFF_NIF = "cif 3";
	final static String PE230_TRAINING_CENTER_DIR_STAFF_CHARGE = "cargo 2";
	final static String PE230_TRAINING_CENTER_NAME = "centro formativo";
	final static String PE230_TRAINING_CENTER_NIF = "cif 4";
	final static String PE230_TRAINING_CENTER_ADDRESS = "direccion 2";
	final static String PE230_TRAINING_CENTER_ZIP_1 = "p1";
	final static String PE230_TRAINING_CENTER_ZIP_2 = "p2";
	final static String PE230_TRAINING_CENTER_ZIP_3 = "p3";
	final static String PE230_TRAINING_CENTER_ZIP_4 = "p4";
	final static String PE230_TRAINING_CENTER_ZIP_5 = "p5";
	final static String PE230_TRAINING_CENTER_TOWN = "municipio";
	final static String PE230_TRAINING_CENTER_PROVINCE = "provincia";
	final static String PE230_TRAINING_CENTER_PHONE_1 = "l1";
	final static String PE230_TRAINING_CENTER_PHONE_2 = "l2";
	final static String PE230_TRAINING_CENTER_PHONE_3 = "l3";
	final static String PE230_TRAINING_CENTER_PHONE_4 = "l4";
	final static String PE230_TRAINING_CENTER_PHONE_5 = "l5";
	final static String PE230_TRAINING_CENTER_PHONE_6 = "l6";
	final static String PE230_TRAINING_CENTER_PHONE_7 = "l7";
	final static String PE230_TRAINING_CENTER_PHONE_8 = "l8";
	final static String PE230_TRAINING_CENTER_PHONE_9 = "l9";
	final static String PE230_TRAINING_CENTER_EMAIL = "correo electronico 2";
	
	// TRAINING COURSE FIELDS
	final static String PE230_TRAINING_COURSE_CLASSROOM = "m7";
	final static String PE230_TRAINING_COURSE_DISTANCE = "m8";
	final static String PE230_TRAINING_COURSE_PHONE_LEARNING = "m9";
	final static String PE230_TRAINING_COURSE_MIXED = "m10";
	final static String PE230_TRAINING_COURSE_START_DATE = "fecha incio";
	final static String PE230_TRAINING_COURSE_END_DATE = "fecha final";
	final static String PE230_TRAINING_COURSE_SCHEDULE = "horario";
	final static String PE230_TRAINING_COURSE_FIRST_YEAR_MAIN_HOURS = "a1";
	final static String PE230_TRAINING_COURSE_FIRST_YEAR_COMPLEMENTARY_HOURS = "a2";
	final static String PE230_TRAINING_COURSE_NEXT_YEAR_MAIN_HOURS = "a3";
	final static String PE230_TRAINING_COURSE_NEXT_YEAR_COMPLEMENTARY_HOURS = "a4";

	// FOORTER FIELDS
	final static String PE230_SIGNATURE_PLACE = "lugar";
	final static String PE230_SIGNATURE_DAY = "dia";
	final static String PE230_SIGNATURE_MONTH = "mes";
	final static String PE230_SIGNATURE_YEAR = "año";
	final static String PE230_SIGNATURE_EMPLOYEE = "firma1";
	final static String PE230_SIGNATURE_EMPLOYEE_DIR_STAFF = "firma2";
	final static String PE230_SIGNATURE_ENTERPRISE_DIR_STAFF = "firma3";
	final static String PE230_SIGNATURE_TRAINING_CENTER_DIR_STAFF = "firma4";
	
	public final static String MODEL_NAME = "PE230";
	
	public ModelPE230(){
		super.documentName = MODEL_NAME;
	}
	
	@Override
	public void loadPdfFields(ContractCode code, Contract contract) throws UnsupportedContractDocumentException{
		try {
			Map<String, String>  map = getContractDataMap(contract);
			PdfReader reader = new PdfReader(getContractModelUrl(documentName+".pdf"));
			
			readPdfFields(reader);
			
			super.loadPdfCommonFields(contract);
			
			TrainingCourse trainingCourse = obtainTrainingCourse(map.get(ContextVariable.TRAINING_COURSE.getName()));
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
			getPdfFieldsMap().get(PE230_COTNRACT_ID_NUMBER_1).setValue("");
			getPdfFieldsMap().get(PE230_COTNRACT_ID_NUMBER_2).setValue("");
			getPdfFieldsMap().get(PE230_COTNRACT_ID_NUMBER_3).setValue("");
			getPdfFieldsMap().get(PE230_COTNRACT_ID_NUMBER_4).setValue("");
			getPdfFieldsMap().get(PE230_COTNRACT_ID_NUMBER_5).setValue("");
			getPdfFieldsMap().get(PE230_COTNRACT_ID_NUMBER_6).setValue("");
			getPdfFieldsMap().get(PE230_COTNRACT_ID_NUMBER_7).setValue("");
			getPdfFieldsMap().get(PE230_COTNRACT_ID_NUMBER_8).setValue("");
			getPdfFieldsMap().get(PE230_COTNRACT_ID_NUMBER_9).setValue("");
			getPdfFieldsMap().get(PE230_COTNRACT_ID_NUMBER_10).setValue("");
			getPdfFieldsMap().get(PE230_COTNRACT_ID_NUMBER_11).setValue("");
			getPdfFieldsMap().get(PE230_COTNRACT_ID_NUMBER_12).setValue("");
			getPdfFieldsMap().get(PE230_COTNRACT_ID_NUMBER_13).setValue("");
			getPdfFieldsMap().get(PE230_COTNRACT_ID_NUMBER_14).setValue("");
			getPdfFieldsMap().get(PE230_COTNRACT_ID_NUMBER_15).setValue("");
			getPdfFieldsMap().get(PE230_COTNRACT_ID_NUMBER_16).setValue("");
			
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			if(contract.getStartDate()!=null){
				getPdfFieldsMap().get(PE230_COTNRACT_START_DATE).setValue(formatter.format(contract.getStartDate()));
			}
			if(contract.getEndDate()!=null){
				getPdfFieldsMap().get(PE230_COTNRACT_END_DATE).setValue(formatter.format(contract.getEndDate()));
			}
			getPdfFieldsMap().get(PE230_COTNRACT_OCCUPATION).setValue(trainingCourse.getOccupationName());
			String cno = map.get(ContextVariable.CNO.getName());
			if( !StringUtils.isEmpty(cno) ){
				getPdfFieldsMap().get(PE230_COTNRACT_CNO_1).setValue(cno.substring(0, 1));
				getPdfFieldsMap().get(PE230_COTNRACT_CNO_2).setValue(cno.substring(1, 2));
				getPdfFieldsMap().get(PE230_COTNRACT_CNO_3).setValue(cno.substring(2, 3));
				getPdfFieldsMap().get(PE230_COTNRACT_CNO_4).setValue(cno.substring(3, 4));
				getPdfFieldsMap().get(PE230_COTNRACT_CNO_5).setValue("");
				getPdfFieldsMap().get(PE230_COTNRACT_CNO_6).setValue("");
				getPdfFieldsMap().get(PE230_COTNRACT_CNO_7).setValue("");
				getPdfFieldsMap().get(PE230_COTNRACT_CNO_8).setValue("");
			}
			
			TrainingCenter trainingCenter = obtainTrainingCenter(map.get(ContextVariable.TRAINING_CENTER.getName()));
			RegistryDirStaff trainingCenterDirStaff = null;
			if(trainingCenter != null){
				trainingCenterDirStaff = obtainRegistryDirStaff(trainingCenter.getRegistry());
				// TRAINING CENTER FIELDS
				getPdfFieldsMap().get(PE230_TRAINING_CENTER_CODE).setValue(trainingCenter.getCode());
				try {
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
				} catch (NullPointerException npe) {
					// do nothing
				}
				
				try {	
					getPdfFieldsMap().get(PE230_TRAINING_CENTER_NAME).setValue(trainingCenter.getRegistry().getFullName());
				} catch (NullPointerException npe) {
					// do nothing
				}
				try {	
					getPdfFieldsMap().get(PE230_TRAINING_CENTER_NIF).setValue(trainingCenter.getRegistry().getDocument());
				} catch (NullPointerException npe) {
					// do nothing
				}
				try {	
					getPdfFieldsMap().get(PE230_TRAINING_CENTER_ADDRESS).setValue(trainingCenter.getRegistry().getDefaultAddress().getFullAddress());
				} catch (NullPointerException npe) {
					// do nothing
				}
				try {	
					getPdfFieldsMap().get(PE230_TRAINING_CENTER_ZIP_1).setValue(trainingCenter.getRegistry().getDefaultAddress().getZip().substring(0, 1));
					getPdfFieldsMap().get(PE230_TRAINING_CENTER_ZIP_2).setValue(trainingCenter.getRegistry().getDefaultAddress().getZip().substring(1, 2));
					getPdfFieldsMap().get(PE230_TRAINING_CENTER_ZIP_3).setValue("");
					getPdfFieldsMap().get(PE230_TRAINING_CENTER_ZIP_3).setValue(trainingCenter.getRegistry().getDefaultAddress().getZip().substring(2, 3));
					getPdfFieldsMap().get(PE230_TRAINING_CENTER_ZIP_4).setValue(trainingCenter.getRegistry().getDefaultAddress().getZip().substring(3, 4));
					getPdfFieldsMap().get(PE230_TRAINING_CENTER_ZIP_5).setValue(trainingCenter.getRegistry().getDefaultAddress().getZip().substring(4, 5));
				} catch (StringIndexOutOfBoundsException aie) {
					// do nothing
				} catch (NullPointerException npe) {
					// do nothing
				}
				try {	
					getPdfFieldsMap().get(PE230_TRAINING_CENTER_TOWN).setValue(trainingCenter.getRegistry().getDefaultAddress().getCity());
				} catch (NullPointerException npe) {
					// do nothing
				}
				try {	
					getPdfFieldsMap().get(PE230_TRAINING_CENTER_PROVINCE).setValue(trainingCenter.getRegistry().getDefaultAddress().getGeozone().getName());
				} catch (NullPointerException npe) {
					// do nothing
				}
				
				try {
					String centerPhone = trainingCenter.getRegistry().getPhone().getValue();
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
					String centerEmail = trainingCenter.getRegistry().getEmail().getValue();
					getPdfFieldsMap().get(PE230_TRAINING_CENTER_EMAIL).setValue(centerEmail);
				} catch (NullPointerException npe) {
					// do nothing
				}
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
			getPdfFieldsMap().get(PE230_TRAINING_COURSE_START_DATE).setValue("");
			getPdfFieldsMap().get(PE230_TRAINING_COURSE_END_DATE).setValue("");
			getPdfFieldsMap().get(PE230_TRAINING_COURSE_SCHEDULE).setValue("");
			getPdfFieldsMap().get(PE230_TRAINING_COURSE_FIRST_YEAR_MAIN_HOURS).setValue("");
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
			getPdfFieldsMap().get(PE230_SIGNATURE_ENTERPRISE_DIR_STAFF).setValue(enterpriseDirStaff.getName());
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

	private TrainingCenter obtainTrainingCenter(String value) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(TrainingCenter.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.TRAINING_CENTER_ID), Integer.parseInt(value) );
			Iterator<ITransferObject> it = bean.getList(criteria).iterator();
			while( it.hasNext() ){
				return (TrainingCenter) it.next();
			}
		} catch (ManagerBeanException e) {
			// do nothing ...
		}
		return null;
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
			criteria.addNullExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_END_DATE));
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
	
}
	
	