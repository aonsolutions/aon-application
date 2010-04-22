package com.code.aon.cvitae.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.cvitae.Curriculum;
import com.code.aon.cvitae.Studies;
import com.code.aon.cvitae.WorkExperience;
import com.code.aon.cvitae.Knowledge;
import com.code.aon.cvitae.Language;
import com.code.aon.cvitae.EvaluateType;
import com.code.aon.cvitae.Evaluate;
import com.code.aon.cvitae.EvaluateSummary;

/** 
* Interface for holding entity properties constants.
*/ 
public interface ICVitaeAlias {



	/** 
	* DAOConstantsEntry for Curriculum entity.
	*/ 
	DAOConstantsEntry CURRICULUM_ENTRY = DAOConstants.getDAOConstant(Curriculum.class);

	/** 
	* Alias value: Curriculum_address
	* Hibernate value: Curriculum.address
	*/
	String  CURRICULUM_ADDRESS = CURRICULUM_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Curriculum_birthDate
	* Hibernate value: Curriculum.birthDate
	*/
	String  CURRICULUM_BIRTH_DATE = CURRICULUM_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Curriculum_birthPlace
	* Hibernate value: Curriculum.birthPlace
	*/
	String  CURRICULUM_BIRTH_PLACE = CURRICULUM_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Curriculum_city
	* Hibernate value: Curriculum.city
	*/
	String  CURRICULUM_CITY = CURRICULUM_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Curriculum_driverLicenses
	* Hibernate value: Curriculum.driverLicenses
	*/
	String  CURRICULUM_DRIVER_LICENSES = CURRICULUM_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Curriculum_driverLiceseDate
	* Hibernate value: Curriculum.driverLiceseDate
	*/
	String  CURRICULUM_DRIVER_LICESE_DATE = CURRICULUM_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Curriculum_entryDate
	* Hibernate value: Curriculum.entryDate
	*/
	String  CURRICULUM_ENTRY_DATE = CURRICULUM_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Curriculum_gender
	* Hibernate value: Curriculum.gender
	*/
	String  CURRICULUM_GENDER = CURRICULUM_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Curriculum_geoZone_id
	* Hibernate value: Curriculum.geoZone.id
	*/
	String  CURRICULUM_GEO_ZONE_ID = CURRICULUM_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Curriculum_id
	* Hibernate value: Curriculum.id
	*/
	String  CURRICULUM_ID = CURRICULUM_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Curriculum_phone
	* Hibernate value: Curriculum.phone
	*/
	String  CURRICULUM_PHONE = CURRICULUM_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Curriculum_postcategory
	* Hibernate value: Curriculum.postcategory
	*/
	String  CURRICULUM_POSTCATEGORY = CURRICULUM_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Curriculum_registry_id
	* Hibernate value: Curriculum.registry.id
	*/
	String  CURRICULUM_REGISTRY_ID = CURRICULUM_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Curriculum_residencePlace
	* Hibernate value: Curriculum.residencePlace
	*/
	String  CURRICULUM_RESIDENCE_PLACE = CURRICULUM_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Curriculum_zip
	* Hibernate value: Curriculum.zip
	*/
	String  CURRICULUM_ZIP = CURRICULUM_ENTRY.getAliasNames()[14];



	/** 
	* DAOConstantsEntry for Studies entity.
	*/ 
	DAOConstantsEntry STUDIES_ENTRY = DAOConstants.getDAOConstant(Studies.class);

	/** 
	* Alias value: Studies_centre
	* Hibernate value: Studies.centre
	*/
	String  STUDIES_CENTRE = STUDIES_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Studies_curriculum_id
	* Hibernate value: Studies.curriculum.id
	*/
	String  STUDIES_CURRICULUM_ID = STUDIES_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Studies_degree
	* Hibernate value: Studies.degree
	*/
	String  STUDIES_DEGREE = STUDIES_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Studies_endDate
	* Hibernate value: Studies.endDate
	*/
	String  STUDIES_END_DATE = STUDIES_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Studies_id
	* Hibernate value: Studies.id
	*/
	String  STUDIES_ID = STUDIES_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Studies_speciality
	* Hibernate value: Studies.speciality
	*/
	String  STUDIES_SPECIALITY = STUDIES_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Studies_startDate
	* Hibernate value: Studies.startDate
	*/
	String  STUDIES_START_DATE = STUDIES_ENTRY.getAliasNames()[6];



	/** 
	* DAOConstantsEntry for WorkExperience entity.
	*/ 
	DAOConstantsEntry WORK_EXPERIENCE_ENTRY = DAOConstants.getDAOConstant(WorkExperience.class);

	/** 
	* Alias value: WorkExperience_company
	* Hibernate value: WorkExperience.company
	*/
	String  WORK_EXPERIENCE_COMPANY = WORK_EXPERIENCE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: WorkExperience_curriculum_id
	* Hibernate value: WorkExperience.curriculum.id
	*/
	String  WORK_EXPERIENCE_CURRICULUM_ID = WORK_EXPERIENCE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: WorkExperience_endDate
	* Hibernate value: WorkExperience.endDate
	*/
	String  WORK_EXPERIENCE_END_DATE = WORK_EXPERIENCE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: WorkExperience_id
	* Hibernate value: WorkExperience.id
	*/
	String  WORK_EXPERIENCE_ID = WORK_EXPERIENCE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: WorkExperience_job
	* Hibernate value: WorkExperience.job
	*/
	String  WORK_EXPERIENCE_JOB = WORK_EXPERIENCE_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: WorkExperience_startDate
	* Hibernate value: WorkExperience.startDate
	*/
	String  WORK_EXPERIENCE_START_DATE = WORK_EXPERIENCE_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for Knowledge entity.
	*/ 
	DAOConstantsEntry KNOWLEDGE_ENTRY = DAOConstants.getDAOConstant(Knowledge.class);

	/** 
	* Alias value: Knowledge_curriculum_id
	* Hibernate value: Knowledge.curriculum.id
	*/
	String  KNOWLEDGE_CURRICULUM_ID = KNOWLEDGE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Knowledge_experience
	* Hibernate value: Knowledge.experience
	*/
	String  KNOWLEDGE_EXPERIENCE = KNOWLEDGE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Knowledge_id
	* Hibernate value: Knowledge.id
	*/
	String  KNOWLEDGE_ID = KNOWLEDGE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Knowledge_lastUse
	* Hibernate value: Knowledge.lastUse
	*/
	String  KNOWLEDGE_LAST_USE = KNOWLEDGE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Knowledge_level
	* Hibernate value: Knowledge.level
	*/
	String  KNOWLEDGE_LEVEL = KNOWLEDGE_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Knowledge_name
	* Hibernate value: Knowledge.name
	*/
	String  KNOWLEDGE_NAME = KNOWLEDGE_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for Language entity.
	*/ 
	DAOConstantsEntry LANGUAGE_ENTRY = DAOConstants.getDAOConstant(Language.class);

	/** 
	* Alias value: Language_curriculum_id
	* Hibernate value: Language.curriculum.id
	*/
	String  LANGUAGE_CURRICULUM_ID = LANGUAGE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Language_id
	* Hibernate value: Language.id
	*/
	String  LANGUAGE_ID = LANGUAGE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Language_language
	* Hibernate value: Language.language
	*/
	String  LANGUAGE_LANGUAGE = LANGUAGE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Language_read
	* Hibernate value: Language.read
	*/
	String  LANGUAGE_READ = LANGUAGE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Language_spoken
	* Hibernate value: Language.spoken
	*/
	String  LANGUAGE_SPOKEN = LANGUAGE_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Language_wrote
	* Hibernate value: Language.wrote
	*/
	String  LANGUAGE_WROTE = LANGUAGE_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for EvaluateType entity.
	*/ 
	DAOConstantsEntry EVALUATE_TYPE_ENTRY = DAOConstants.getDAOConstant(EvaluateType.class);

	/** 
	* Alias value: EvaluateType_id
	* Hibernate value: EvaluateType.id
	*/
	String  EVALUATE_TYPE_ID = EVALUATE_TYPE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: EvaluateType_name
	* Hibernate value: EvaluateType.name
	*/
	String  EVALUATE_TYPE_NAME = EVALUATE_TYPE_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for Evaluate entity.
	*/ 
	DAOConstantsEntry EVALUATE_ENTRY = DAOConstants.getDAOConstant(Evaluate.class);

	/** 
	* Alias value: Evaluate_curriculum_id
	* Hibernate value: Evaluate.curriculum.id
	*/
	String  EVALUATE_CURRICULUM_ID = EVALUATE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Evaluate_evaluateType_id
	* Hibernate value: Evaluate.evaluateType.id
	*/
	String  EVALUATE_EVALUATE_TYPE_ID = EVALUATE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Evaluate_id
	* Hibernate value: Evaluate.id
	*/
	String  EVALUATE_ID = EVALUATE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Evaluate_value
	* Hibernate value: Evaluate.value
	*/
	String  EVALUATE_VALUE = EVALUATE_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for EvaluateSummary entity.
	*/ 
	DAOConstantsEntry EVALUATE_SUMMARY_ENTRY = DAOConstants.getDAOConstant(EvaluateSummary.class);

	/** 
	* Alias value: EvaluateSummary_comments
	* Hibernate value: EvaluateSummary.comments
	*/
	String  EVALUATE_SUMMARY_COMMENTS = EVALUATE_SUMMARY_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: EvaluateSummary_curriculum_id
	* Hibernate value: EvaluateSummary.curriculum.id
	*/
	String  EVALUATE_SUMMARY_CURRICULUM_ID = EVALUATE_SUMMARY_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: EvaluateSummary_id
	* Hibernate value: EvaluateSummary.id
	*/
	String  EVALUATE_SUMMARY_ID = EVALUATE_SUMMARY_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: EvaluateSummary_profile
	* Hibernate value: EvaluateSummary.profile
	*/
	String  EVALUATE_SUMMARY_PROFILE = EVALUATE_SUMMARY_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: EvaluateSummary_strengths
	* Hibernate value: EvaluateSummary.strengths
	*/
	String  EVALUATE_SUMMARY_STRENGTHS = EVALUATE_SUMMARY_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: EvaluateSummary_weaknesses
	* Hibernate value: EvaluateSummary.weaknesses
	*/
	String  EVALUATE_SUMMARY_WEAKNESSES = EVALUATE_SUMMARY_ENTRY.getAliasNames()[5];


}