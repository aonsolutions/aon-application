package com.code.aon.academy.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.academy.Absence;
import com.code.aon.academy.AcademicSkill;
import com.code.aon.academy.AcademicYear;
import com.code.aon.academy.AlumnLoan;
import com.code.aon.academy.Course;
import com.code.aon.academy.CourseAcademicSkill;
import com.code.aon.academy.CourseAlumn;
import com.code.aon.academy.CourseEvaluation;
import com.code.aon.academy.CourseInstructor;
import com.code.aon.academy.CourseLevel;
import com.code.aon.academy.CourseObservation;
import com.code.aon.academy.CourseSchedule;
import com.code.aon.academy.CourseSubject;
import com.code.aon.academy.EvaluationObservation;
import com.code.aon.academy.Mark;
import com.code.aon.academy.Observation;
import com.code.aon.academy.Qualification;
import com.code.aon.academy.QualitySkill;

/** 
* Interface for holding entity properties constants.
*/ 
public interface IAcademyAlias {



	/** 
	* DAOConstantsEntry for Absence entity.
	*/ 
	DAOConstantsEntry ABSENCE_ENTRY = DAOConstants.getDAOConstant(Absence.class);

	/** 
	* Alias value: Absence_absenceDate
	* Hibernate value: Absence.absenceDate
	*/
	String  ABSENCE_ABSENCE_DATE = ABSENCE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Absence_comments
	* Hibernate value: Absence.comments
	*/
	String  ABSENCE_COMMENTS = ABSENCE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Absence_courseAlumn_id
	* Hibernate value: Absence.courseAlumn.id
	*/
	String  ABSENCE_COURSE_ALUMN_ID = ABSENCE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Absence_evaluation
	* Hibernate value: Absence.evaluation
	*/
	String  ABSENCE_EVALUATION = ABSENCE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Absence_id
	* Hibernate value: Absence.id
	*/
	String  ABSENCE_ID = ABSENCE_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for AcademicSkill entity.
	*/ 
	DAOConstantsEntry ACADEMIC_SKILL_ENTRY = DAOConstants.getDAOConstant(AcademicSkill.class);

	/** 
	* Alias value: AcademicSkill_code
	* Hibernate value: AcademicSkill.code
	*/
	String  ACADEMIC_SKILL_CODE = ACADEMIC_SKILL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: AcademicSkill_description
	* Hibernate value: AcademicSkill.description
	*/
	String  ACADEMIC_SKILL_DESCRIPTION = ACADEMIC_SKILL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: AcademicSkill_id
	* Hibernate value: AcademicSkill.id
	*/
	String  ACADEMIC_SKILL_ID = ACADEMIC_SKILL_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for AcademicYear entity.
	*/ 
	DAOConstantsEntry ACADEMIC_YEAR_ENTRY = DAOConstants.getDAOConstant(AcademicYear.class);

	/** 
	* Alias value: AcademicYear_description
	* Hibernate value: AcademicYear.description
	*/
	String  ACADEMIC_YEAR_DESCRIPTION = ACADEMIC_YEAR_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: AcademicYear_id
	* Hibernate value: AcademicYear.id
	*/
	String  ACADEMIC_YEAR_ID = ACADEMIC_YEAR_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for AlumnLoan entity.
	*/ 
	DAOConstantsEntry ALUMN_LOAN_ENTRY = DAOConstants.getDAOConstant(AlumnLoan.class);

	/** 
	* Alias value: AlumnLoan_comments
	* Hibernate value: AlumnLoan.comments
	*/
	String  ALUMN_LOAN_COMMENTS = ALUMN_LOAN_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: AlumnLoan_customer_id
	* Hibernate value: AlumnLoan.customer.id
	*/
	String  ALUMN_LOAN_CUSTOMER_ID = ALUMN_LOAN_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: AlumnLoan_endDate
	* Hibernate value: AlumnLoan.endDate
	*/
	String  ALUMN_LOAN_END_DATE = ALUMN_LOAN_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: AlumnLoan_id
	* Hibernate value: AlumnLoan.id
	*/
	String  ALUMN_LOAN_ID = ALUMN_LOAN_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: AlumnLoan_loanDate
	* Hibernate value: AlumnLoan.loanDate
	*/
	String  ALUMN_LOAN_LOAN_DATE = ALUMN_LOAN_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: AlumnLoan_material
	* Hibernate value: AlumnLoan.material
	*/
	String  ALUMN_LOAN_MATERIAL = ALUMN_LOAN_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for Course entity.
	*/ 
	DAOConstantsEntry COURSE_ENTRY = DAOConstants.getDAOConstant(Course.class);

	/** 
	* Alias value: Course_academicYear_id
	* Hibernate value: Course.academicYear.id
	*/
	String  COURSE_ACADEMIC_YEAR_ID = COURSE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Course_alumnLimit
	* Hibernate value: Course.alumnLimit
	*/
	String  COURSE_ALUMN_LIMIT = COURSE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Course_code
	* Hibernate value: Course.code
	*/
	String  COURSE_CODE = COURSE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Course_comments
	* Hibernate value: Course.comments
	*/
	String  COURSE_COMMENTS = COURSE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Course_courseLevel_id
	* Hibernate value: Course.courseLevel.id
	*/
	String  COURSE_COURSE_LEVEL_ID = COURSE_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Course_courseSubject_id
	* Hibernate value: Course.courseSubject.id
	*/
	String  COURSE_COURSE_SUBJECT_ID = COURSE_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Course_description
	* Hibernate value: Course.description
	*/
	String  COURSE_DESCRIPTION = COURSE_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Course_endDate
	* Hibernate value: Course.endDate
	*/
	String  COURSE_END_DATE = COURSE_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Course_id
	* Hibernate value: Course.id
	*/
	String  COURSE_ID = COURSE_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Course_startDate
	* Hibernate value: Course.startDate
	*/
	String  COURSE_START_DATE = COURSE_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Course_status
	* Hibernate value: Course.status
	*/
	String  COURSE_STATUS = COURSE_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Course_workPlace_id
	* Hibernate value: Course.workPlace.id
	*/
	String  COURSE_WORK_PLACE_ID = COURSE_ENTRY.getAliasNames()[11];



	/** 
	* DAOConstantsEntry for CourseAcademicSkill entity.
	*/ 
	DAOConstantsEntry COURSE_ACADEMIC_SKILL_ENTRY = DAOConstants.getDAOConstant(CourseAcademicSkill.class);

	/** 
	* Alias value: CourseAcademicSkill_academicSkill_id
	* Hibernate value: CourseAcademicSkill.academicSkill.id
	*/
	String  COURSE_ACADEMIC_SKILL_ACADEMIC_SKILL_ID = COURSE_ACADEMIC_SKILL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: CourseAcademicSkill_course_id
	* Hibernate value: CourseAcademicSkill.course.id
	*/
	String  COURSE_ACADEMIC_SKILL_COURSE_ID = COURSE_ACADEMIC_SKILL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: CourseAcademicSkill_id
	* Hibernate value: CourseAcademicSkill.id
	*/
	String  COURSE_ACADEMIC_SKILL_ID = COURSE_ACADEMIC_SKILL_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: CourseAcademicSkill_weight
	* Hibernate value: CourseAcademicSkill.weight
	*/
	String  COURSE_ACADEMIC_SKILL_WEIGHT = COURSE_ACADEMIC_SKILL_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for CourseAlumn entity.
	*/ 
	DAOConstantsEntry COURSE_ALUMN_ENTRY = DAOConstants.getDAOConstant(CourseAlumn.class);

	/** 
	* Alias value: CourseAlumn_course_id
	* Hibernate value: CourseAlumn.course.id
	*/
	String  COURSE_ALUMN_COURSE_ID = COURSE_ALUMN_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: CourseAlumn_customer_id
	* Hibernate value: CourseAlumn.customer.id
	*/
	String  COURSE_ALUMN_CUSTOMER_ID = COURSE_ALUMN_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: CourseAlumn_customer_registry_name
	* Hibernate value: CourseAlumn.customer.registry.name
	*/
	String  COURSE_ALUMN_CUSTOMER_REGISTRY_NAME = COURSE_ALUMN_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: CourseAlumn_customer_registry_surname
	* Hibernate value: CourseAlumn.customer.registry.surname
	*/
	String  COURSE_ALUMN_CUSTOMER_REGISTRY_SURNAME = COURSE_ALUMN_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: CourseAlumn_id
	* Hibernate value: CourseAlumn.id
	*/
	String  COURSE_ALUMN_ID = COURSE_ALUMN_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: CourseAlumn_course_code
	* Hibernate value: CourseAlumn.course.code
	*/
	String  COURSE_ALUMN_COURSE_CODE = COURSE_ALUMN_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: CourseAlumn_course_academicYear_id
	* Hibernate value: CourseAlumn.course.academicYear.id
	*/
	String  COURSE_ALUMN_COURSE_ACADEMIC_YEAR_ID = COURSE_ALUMN_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: CourseAlumn_course_startDate
	* Hibernate value: CourseAlumn.course.startDate
	*/
	String  COURSE_ALUMN_COURSE_START_DATE = COURSE_ALUMN_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: CourseAlumn_course_endDate
	* Hibernate value: CourseAlumn.course.endDate
	*/
	String  COURSE_ALUMN_COURSE_END_DATE = COURSE_ALUMN_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: CourseAlumn_course_status
	* Hibernate value: CourseAlumn.course.status
	*/
	String  COURSE_ALUMN_COURSE_STATUS = COURSE_ALUMN_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: CourseAlumn_status
	* Hibernate value: CourseAlumn.status
	*/
	String  COURSE_ALUMN_STATUS = COURSE_ALUMN_ENTRY.getAliasNames()[10];



	/** 
	* DAOConstantsEntry for CourseEvaluation entity.
	*/ 
	DAOConstantsEntry COURSE_EVALUATION_ENTRY = DAOConstants.getDAOConstant(CourseEvaluation.class);

	/** 
	* Alias value: CourseEvaluation_course_id
	* Hibernate value: CourseEvaluation.course.id
	*/
	String  COURSE_EVALUATION_COURSE_ID = COURSE_EVALUATION_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: CourseEvaluation_evaluation
	* Hibernate value: CourseEvaluation.evaluation
	*/
	String  COURSE_EVALUATION_EVALUATION = COURSE_EVALUATION_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: CourseEvaluation_id
	* Hibernate value: CourseEvaluation.id
	*/
	String  COURSE_EVALUATION_ID = COURSE_EVALUATION_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: CourseEvaluation_qualitySkill_id
	* Hibernate value: CourseEvaluation.qualitySkill.id
	*/
	String  COURSE_EVALUATION_QUALITY_SKILL_ID = COURSE_EVALUATION_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: CourseEvaluation_quantity
	* Hibernate value: CourseEvaluation.quantity
	*/
	String  COURSE_EVALUATION_QUANTITY = COURSE_EVALUATION_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for CourseInstructor entity.
	*/ 
	DAOConstantsEntry COURSE_INSTRUCTOR_ENTRY = DAOConstants.getDAOConstant(CourseInstructor.class);

	/** 
	* Alias value: CourseInstructor_id
	* Hibernate value: CourseInstructor.id
	*/
	String  COURSE_INSTRUCTOR_ID = COURSE_INSTRUCTOR_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: CourseInstructor_course_id
	* Hibernate value: CourseInstructor.course.id
	*/
	String  COURSE_INSTRUCTOR_COURSE_ID = COURSE_INSTRUCTOR_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: CourseInstructor_course_code
	* Hibernate value: CourseInstructor.course.code
	*/
	String  COURSE_INSTRUCTOR_COURSE_CODE = COURSE_INSTRUCTOR_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: CourseInstructor_course_status
	* Hibernate value: CourseInstructor.course.status
	*/
	String  COURSE_INSTRUCTOR_COURSE_STATUS = COURSE_INSTRUCTOR_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: CourseInstructor_employee_id
	* Hibernate value: CourseInstructor.employee.id
	*/
	String  COURSE_INSTRUCTOR_EMPLOYEE_ID = COURSE_INSTRUCTOR_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: CourseInstructor_type
	* Hibernate value: CourseInstructor.type
	*/
	String  COURSE_INSTRUCTOR_TYPE = COURSE_INSTRUCTOR_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for CourseLevel entity.
	*/ 
	DAOConstantsEntry COURSE_LEVEL_ENTRY = DAOConstants.getDAOConstant(CourseLevel.class);

	/** 
	* Alias value: CourseLevel_description
	* Hibernate value: CourseLevel.description
	*/
	String  COURSE_LEVEL_DESCRIPTION = COURSE_LEVEL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: CourseLevel_id
	* Hibernate value: CourseLevel.id
	*/
	String  COURSE_LEVEL_ID = COURSE_LEVEL_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for CourseObservation entity.
	*/ 
	DAOConstantsEntry COURSE_OBSERVATION_ENTRY = DAOConstants.getDAOConstant(CourseObservation.class);

	/** 
	* Alias value: CourseObservation_course_id
	* Hibernate value: CourseObservation.course.id
	*/
	String  COURSE_OBSERVATION_COURSE_ID = COURSE_OBSERVATION_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: CourseObservation_id
	* Hibernate value: CourseObservation.id
	*/
	String  COURSE_OBSERVATION_ID = COURSE_OBSERVATION_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: CourseObservation_observation
	* Hibernate value: CourseObservation.observation
	*/
	String  COURSE_OBSERVATION_OBSERVATION = COURSE_OBSERVATION_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for CourseSchedule entity.
	*/ 
	DAOConstantsEntry COURSE_SCHEDULE_ENTRY = DAOConstants.getDAOConstant(CourseSchedule.class);

	/** 
	* Alias value: CourseSchedule_course_id
	* Hibernate value: CourseSchedule.course.id
	*/
	String  COURSE_SCHEDULE_COURSE_ID = COURSE_SCHEDULE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: CourseSchedule_day
	* Hibernate value: CourseSchedule.day
	*/
	String  COURSE_SCHEDULE_DAY = COURSE_SCHEDULE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: CourseSchedule_endTime
	* Hibernate value: CourseSchedule.endTime
	*/
	String  COURSE_SCHEDULE_END_TIME = COURSE_SCHEDULE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: CourseSchedule_id
	* Hibernate value: CourseSchedule.id
	*/
	String  COURSE_SCHEDULE_ID = COURSE_SCHEDULE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: CourseSchedule_startTime
	* Hibernate value: CourseSchedule.startTime
	*/
	String  COURSE_SCHEDULE_START_TIME = COURSE_SCHEDULE_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for CourseSubject entity.
	*/ 
	DAOConstantsEntry COURSE_SUBJECT_ENTRY = DAOConstants.getDAOConstant(CourseSubject.class);

	/** 
	* Alias value: CourseSubject_description
	* Hibernate value: CourseSubject.description
	*/
	String  COURSE_SUBJECT_DESCRIPTION = COURSE_SUBJECT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: CourseSubject_id
	* Hibernate value: CourseSubject.id
	*/
	String  COURSE_SUBJECT_ID = COURSE_SUBJECT_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for EvaluationObservation entity.
	*/ 
	DAOConstantsEntry EVALUATION_OBSERVATION_ENTRY = DAOConstants.getDAOConstant(EvaluationObservation.class);

	/** 
	* Alias value: EvaluationObservation_alumn_id
	* Hibernate value: EvaluationObservation.alumn.id
	*/
	String  EVALUATION_OBSERVATION_ALUMN_ID = EVALUATION_OBSERVATION_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: EvaluationObservation_comments
	* Hibernate value: EvaluationObservation.comments
	*/
	String  EVALUATION_OBSERVATION_COMMENTS = EVALUATION_OBSERVATION_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: EvaluationObservation_evaluation
	* Hibernate value: EvaluationObservation.evaluation
	*/
	String  EVALUATION_OBSERVATION_EVALUATION = EVALUATION_OBSERVATION_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: EvaluationObservation_id
	* Hibernate value: EvaluationObservation.id
	*/
	String  EVALUATION_OBSERVATION_ID = EVALUATION_OBSERVATION_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for Mark entity.
	*/ 
	DAOConstantsEntry MARK_ENTRY = DAOConstants.getDAOConstant(Mark.class);

	/** 
	* Alias value: Mark_alumn_course_code
	* Hibernate value: Mark.alumn.course.code
	*/
	String  MARK_ALUMN_COURSE_CODE = MARK_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Mark_alumn_course_academicYear_id
	* Hibernate value: Mark.alumn.course.academicYear.id
	*/
	String  MARK_ALUMN_COURSE_ACADEMIC_YEAR_ID = MARK_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Mark_alumn_course_courseLevel_id
	* Hibernate value: Mark.alumn.course.courseLevel.id
	*/
	String  MARK_ALUMN_COURSE_COURSE_LEVEL_ID = MARK_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Mark_alumn_course_courseSubject_id
	* Hibernate value: Mark.alumn.course.courseSubject.id
	*/
	String  MARK_ALUMN_COURSE_COURSE_SUBJECT_ID = MARK_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Mark_alumn_course_id
	* Hibernate value: Mark.alumn.course.id
	*/
	String  MARK_ALUMN_COURSE_ID = MARK_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Mark_alumn_customer_registry_name
	* Hibernate value: Mark.alumn.customer.registry.name
	*/
	String  MARK_ALUMN_CUSTOMER_REGISTRY_NAME = MARK_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Mark_alumn_customer_registry_surname
	* Hibernate value: Mark.alumn.customer.registry.surname
	*/
	String  MARK_ALUMN_CUSTOMER_REGISTRY_SURNAME = MARK_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Mark_alumn_id
	* Hibernate value: Mark.alumn.id
	*/
	String  MARK_ALUMN_ID = MARK_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Mark_evaluation
	* Hibernate value: Mark.evaluation
	*/
	String  MARK_EVALUATION = MARK_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Mark_id
	* Hibernate value: Mark.id
	*/
	String  MARK_ID = MARK_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Mark_mark
	* Hibernate value: Mark.mark
	*/
	String  MARK_MARK = MARK_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Mark_subject_academicSkill_id
	* Hibernate value: Mark.subject.academicSkill.id
	*/
	String  MARK_SUBJECT_ACADEMIC_SKILL_ID = MARK_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Mark_subject_id
	* Hibernate value: Mark.subject.id
	*/
	String  MARK_SUBJECT_ID = MARK_ENTRY.getAliasNames()[12];



	/** 
	* DAOConstantsEntry for Observation entity.
	*/ 
	DAOConstantsEntry OBSERVATION_ENTRY = DAOConstants.getDAOConstant(Observation.class);

	/** 
	* Alias value: Observation_description
	* Hibernate value: Observation.description
	*/
	String  OBSERVATION_DESCRIPTION = OBSERVATION_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Observation_id
	* Hibernate value: Observation.id
	*/
	String  OBSERVATION_ID = OBSERVATION_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for Qualification entity.
	*/ 
	DAOConstantsEntry QUALIFICATION_ENTRY = DAOConstants.getDAOConstant(Qualification.class);

	/** 
	* Alias value: Qualification_code
	* Hibernate value: Qualification.code
	*/
	String  QUALIFICATION_CODE = QUALIFICATION_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Qualification_description
	* Hibernate value: Qualification.description
	*/
	String  QUALIFICATION_DESCRIPTION = QUALIFICATION_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Qualification_id
	* Hibernate value: Qualification.id
	*/
	String  QUALIFICATION_ID = QUALIFICATION_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Qualification_maxValue
	* Hibernate value: Qualification.maxValue
	*/
	String  QUALIFICATION_MAX_VALUE = QUALIFICATION_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Qualification_minValue
	* Hibernate value: Qualification.minValue
	*/
	String  QUALIFICATION_MIN_VALUE = QUALIFICATION_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for QualitySkill entity.
	*/ 
	DAOConstantsEntry QUALITY_SKILL_ENTRY = DAOConstants.getDAOConstant(QualitySkill.class);

	/** 
	* Alias value: QualitySkill_code
	* Hibernate value: QualitySkill.code
	*/
	String  QUALITY_SKILL_CODE = QUALITY_SKILL_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: QualitySkill_description
	* Hibernate value: QualitySkill.description
	*/
	String  QUALITY_SKILL_DESCRIPTION = QUALITY_SKILL_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: QualitySkill_id
	* Hibernate value: QualitySkill.id
	*/
	String  QUALITY_SKILL_ID = QUALITY_SKILL_ENTRY.getAliasNames()[2];


}