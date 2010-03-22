package com.code.aon.academy.dao;

import java.io.File;
import java.io.IOException;

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
import com.code.aon.common.dao.AliasWriter;

public class AcademyAliasWriter {

	public static void main(String[] args) throws IOException {
		File file = new File("/AON-PROJECT/aon-academy/src/main/java/com/code/aon/academy/dao/IAcademyAlias.java");
        String[] classes = new String[18]; 
        classes[0] = Absence.class.getName();
        classes[1] = AcademicSkill.class.getName();
        classes[2] = AcademicYear.class.getName();
        classes[3] = AlumnLoan.class.getName();
        classes[4] = Course.class.getName();
        classes[5] = CourseAcademicSkill.class.getName();
		classes[6] = CourseAlumn.class.getName();
        classes[7] = CourseEvaluation.class.getName();
        classes[8] = CourseInstructor.class.getName();
        classes[9] = CourseLevel.class.getName();
        classes[10] = CourseObservation.class.getName();
		classes[11] = CourseSchedule.class.getName();
		classes[12] = CourseSubject.class.getName();
        classes[13] = EvaluationObservation.class.getName();
        classes[14] = Mark.class.getName();
        classes[15] = Observation.class.getName();
        classes[16] = Qualification.class.getName();
        classes[17] = QualitySkill.class.getName();
		AliasWriter writer = new AliasWriter("com.code.aon.academy.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}
