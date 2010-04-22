package com.code.aon.cvitae.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.cvitae.EvaluateSummary;
import com.code.aon.cvitae.EvaluateType;
import com.code.aon.cvitae.Knowledge;
import com.code.aon.cvitae.Evaluate;
import com.code.aon.cvitae.Studies;
import com.code.aon.cvitae.WorkExperience;
import com.code.aon.cvitae.Curriculum;
import com.code.aon.cvitae.Language;

public class CVitaeAliasWriter {
	
	public static void main(String[] args) throws IOException {
		File file = new File("/PROYECTOS/aon-employee/src/com/code/aon/cvitae/dao/ICVitaeAlias.java");
		String[] classes = new String[8];
		classes[0] = Curriculum.class.getName();
		classes[1] = Studies.class.getName();
		classes[2] = WorkExperience.class.getName();
		classes[3] = Knowledge.class.getName();
		classes[4] = Language.class.getName();
		classes[5] = EvaluateType.class.getName();
		classes[6] = Evaluate.class.getName();
		classes[7] = EvaluateSummary.class.getName();
		AliasWriter writer = new AliasWriter("com.code.aon.cvitae.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}