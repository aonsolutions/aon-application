package com.esferalia.aon.gwt.payroll.util;

import com.esferalia.aon.watson.util.AonStringUtils;

public class PayrollUtils {

	public static int getDeductionPDFType(int type) {
		switch (type) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 13:
			return 1;
		case 6:
			return 2;
		case 7:
			return 3;
		case 8:
			return 4;
		default:
			return 5;
		}
	}
	
	public static String getDeductionNameDescription(String name) {
	    if ( AonStringUtils.equals("MEI", name)) {
		return "Mecanismo de Equidad Intergeneracional (MEI)";
	    }
	    return null;
	}
	
	public static String getDeductionTypeDescription(int type) {

		switch (type) {
		case 0:
			return "Contingencias Comunes";
		case 2:
			return "Desempleo";
		case 3:
			return "Formación Profesional";
		case 4:
			return "Horas Extraordinarias (Estruc.)";
		case 5:
			return "Horas Extraordinarias (No Estruc.)";
		case 6:
			return "Retribuciones Dinerarias";
		case 7:
			return "Anticipo";
		case 8:
			return "En Especie";
		case 10:
			return "Embargo";
		case 13:
			return "Mecanismo de Equidad Intergeneracional (MEI)";
		default:
			return "Otras deducciones";
		}

	}
	
//	public static String getDeductionTypeDescription(DeductionType type) {
//		if (type == null) {
//			return "Otras deducciones";
//		}
//		StringBuilder retBuild = new StringBuilder("");
//		type.accept(new DeductionTypeVisitor() {
//
//			@Override
//			public void visitCommonContigency(DeductionType deductionType) {
//				retBuild.append("Contingencias comunes");
//			}
//
//			@Override
//			public void visitProfessionalContigency(DeductionType deductionType) {
//				retBuild.append("Otras deducciones");
//				
//			}
//
//			@Override
//			public void visitUnemployent(DeductionType deductionType) {
//				retBuild.append("Desempleo");
//			}
//
//			@Override
//			public void visitJobTraining(DeductionType deductionType) {
//				retBuild.append("Formación profesional");
//			}
//
//			@Override
//			public void visitStructuralOvertime(DeductionType deductionType) {
//				retBuild.append("Horas extraordinarias (Estruc.)");
//			}
//
//			@Override
//			public void visitNonStructuralOvertime(DeductionType deductionType) {
//				retBuild.append("Horas extraordinarias (No "
//						+ "Estruc.)");
//			}
//
//			@Override
//			public void visitIrpf(DeductionType deductionType) {
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			public void visitAdvancePayment(DeductionType deductionType) {
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			public void visitInkind(DeductionType deductionType) {
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			public void visitOther(DeductionType deductionType) {
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			public void visitFogasa(DeductionType deductionType) {
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			public void visitEmbargo(DeductionType deductionType) {
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			public void visitBonus(DeductionType deductionType) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//		});
//
//	}
//	

}
