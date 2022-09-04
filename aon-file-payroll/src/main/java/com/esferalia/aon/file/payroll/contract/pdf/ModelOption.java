package com.esferalia.aon.file.payroll.contract.pdf;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;
import com.esferalia.aon.file.payroll.contract.pdf.internship.InternshipModel;
import com.esferalia.aon.file.payroll.contract.pdf.model.IndefiniteModel;
import com.esferalia.aon.file.payroll.contract.pdf.model.LearningModel;
import com.esferalia.aon.file.payroll.contract.pdf.model.PracticeModel;
import com.esferalia.aon.file.payroll.contract.pdf.model.TemporaryModel;
import com.esferalia.aon.payroll.enumeration.ContractCode;


public enum ModelOption implements IResourceable {

	/**
	 * INDEFINIDO ORDINARIO (pag. 4)
	 */
	INDEFINITE_OPT1(ContractPdfModel.INDEFINITE, 4, ContractCode.C100, ContractCode.C200, ContractCode.C300),
	/**
	 * DE PERSONAS CON DISCAPACIDAD (pag. 5)
	 */
	INDEFINITE_OPT2(ContractPdfModel.INDEFINITE, 5,ContractCode.C130, ContractCode.C230, ContractCode.C330),
	/**
	 * DE PERSONAS CON DISCAPACIDAD EN CENTROS ESPECIALES DE EMPLEO (pag.6)
	 */
	INDEFINITE_OPT3(ContractPdfModel.INDEFINITE, 6,ContractCode.C150, ContractCode.C250, ContractCode.C350),
	/**
	 * DE PERSONAS CON DISCAPACIDAD PROCEDENTES DE ENCLAVES LABORALES (pag.7)
	 */
	INDEFINITE_OPT4(ContractPdfModel.INDEFINITE, 7, ContractCode.C130, ContractCode.C230, ContractCode.C330),
	/**
	 * DE APOYO A LOS EMPRENDEDORES (pag.8)
	 */
	INDEFINITE_OPT5(ContractPdfModel.INDEFINITE, 8, ContractCode.C100, ContractCode.C200, ContractCode.C300, ContractCode.C150, ContractCode.C250, ContractCode.C350),
	/**
	 * DE UN JÓVEN POR MICROEMPRESAS Y EMPRESARIOS AUTÓNOMOS (pag.9)
	 */
	INDEFINITE_OPT6(ContractPdfModel.INDEFINITE, 9, ContractCode.C100, ContractCode.C200),
	/**
	 * DE NUEVO PROYECTO DE EMPRENDIMIENTO JOVEN (pag.10)
	 */
	INDEFINITE_OPT7(ContractPdfModel.INDEFINITE, 10, ContractCode.C100, ContractCode.C200, ContractCode.C300),
	/**
	 * A TIEMPO PARCIAL CON VINCULACIÓN FORMATIVA (pag.11)
	 */
	INDEFINITE_OPT8(ContractPdfModel.INDEFINITE, 11, ContractCode.C200, ContractCode.C300),
	/**
	 * DE TRABAJADORES EN SITUACIÓN DE EXCLUSIÓN SOCIAL, VÍCTIMAS DE VIOLENCIA DE GÉNERO, DOMESTICA O VÍCTIMAS DE TERRORISMO (pag.12)
	 */
	INDEFINITE_OPT9(ContractPdfModel.INDEFINITE, 12, ContractCode.C150, ContractCode.C250, ContractCode.C350),
	/**
	 * DE EXCLUIDOS EN EMPRESAS DE INSERCIÓN (pag.13)
	 */
	INDEFINITE_OPT10(ContractPdfModel.INDEFINITE, 13, ContractCode.C150, ContractCode.C250, ContractCode.C350),
	/**
	 * DE MAYORES DE 52 AÑOS BENEFICIARIOS DE SUBSIDIOS POR DESEMPLEO (pag.14)
	 */
	INDEFINITE_OPT11(ContractPdfModel.INDEFINITE, 14, ContractCode.C100, ContractCode.C150, ContractCode.C300, ContractCode.C350),
	/**
	 * PROCENTE DE PRIMER EMPLEO JOVEN DE ETT. (pag.15)
	 */
	INDEFINITE_OPT12(ContractPdfModel.INDEFINITE, 15, ContractCode.C150, ContractCode.C250, ContractCode.C350),
	/**
	 * PROCEDENTE DE UN CONTRATO PARA LA FORMACIÓN Y EL APRENDIZAJE DE ETT (pag.16)
	 */
	INDEFINITE_OPT13(ContractPdfModel.INDEFINITE, 16, ContractCode.C100, ContractCode.C200, ContractCode.C300),
	/**
	 * PROCEDENTE DE UN CONTRATO EN PRÁCTICAS DE ETT. ( pág 17)
	 */
	INDEFINITE_OPT14(ContractPdfModel.INDEFINITE, 17, ContractCode.C150, ContractCode.C250, ContractCode.C350),
	/**
	 * DEL SERVICIO DEL HOGAR FAMILIAR (pag.18)
	 */
	INDEFINITE_OPT15(ContractPdfModel.INDEFINITE, 18, ContractCode.C100, ContractCode.C200),
	/**
	 * OTRAS SITUACIONES (pág19)
	 */
	INDEFINITE_OPT16(ContractPdfModel.INDEFINITE, 19, ContractCode.C990),
	/**
	 * CONVERSIÓN DE CONTRATO TEMPORAL EN CONTRATO INDEFINIDO (pag.20)
	 */
	INDEFINITE_OPT17(ContractPdfModel.INDEFINITE, 20, ContractCode.C109, ContractCode.C139, ContractCode.C189,
		ContractCode.C209, ContractCode.C239, ContractCode.C289,
		ContractCode.C309, ContractCode.C339, ContractCode.C389),
	
	/**
	 * FORMACIÓN Y APRENDIZAJE ( ORDINARIO ). ( pág.4 )
	 */
	LEARNING_OPT1(ContractPdfModel.LEARNING, 4, ContractCode.C421),
	/**
	 * DE TRABAJADORES EN SITUACIÓN DE EXCLUSIÓN SOCIAL, VÍCTIMAS DE VIOLENCIA DE GÉNERO, DOMÉSTICA O VÍCTIMA DE TERRORISMO . ( pág.5 )
	 */
	LEARNING_OPT2(ContractPdfModel.LEARNING, 5, ContractCode.C450),
	/**
	 * DE PERSONAS CON DISCAPACIDAD EN CENTROS ESPECIALES DE EMPLEO. ( pág.6 )
	 */
	LEARNING_OPT3(ContractPdfModel.LEARNING, 6, ContractCode.C421),
	/**
	 * DE TRABAJOS DE INTERÉS SOCIAL/FOMENTO DE EMPLEO AGRARIO. ( pág.7 )
	 */
	LEARNING_OPT4(ContractPdfModel.LEARNING, 7, ContractCode.C421),
	
	
	/**
	 * PRÁCTICAS ( ORDINARIO ). (pag. 4)
	 */
	PRACTICE_OPT1(ContractPdfModel.PRACTICE, 4, ContractCode.C420, ContractCode.C520),
	/**
	 * DE TRABAJADORES EN SITUACIÓN DE EXCLUSIÓN SOCIAL, VÍCTIMAS DE VIOLENCIA DE GÉNERO, DOMESTICA O VÍCTIMA DE TERRORISMO .(pag.5)
	 */
	PRACTICE_OPT2(ContractPdfModel.PRACTICE, 5, ContractCode.C450, ContractCode.C550),
	/**
	 * DE TRABAJADORES MAYORES DE 52 AÑOS BENEFICIARIOS DE LOS SUBSIDIOS POR DESEMPLEO (pag.6)
	 */
	PRACTICE_OPT3(ContractPdfModel.PRACTICE, 6, ContractCode.C420),
	/**
	 * DE PERSONAS CON DISCAPACIDAD EN CENTROS ESPECIALES DE EMPLEO. (pag.7)
	 */
	PRACTICE_OPT4(ContractPdfModel.PRACTICE, 7, ContractCode.C420, ContractCode.C520),
	/**
	 * DE TRABAJOS DE INTERES SOCIAL/FOMENTO DE EMPLEO AGRARIO. (pag.8)
	 */
	PRACTICE_OPT5(ContractPdfModel.PRACTICE, 8, ContractCode.C420, ContractCode.C520),
	
	
	/**
	 * OBRA O SERVICIO DETERMINADO. ( pág.4 )
	 */
	TEMPORARY_OPT1(ContractPdfModel.TEMPORARY, 4, ContractCode.C401, ContractCode.C501),
	/**
	 * EVENTUAL POR CIRCUNSTANCIAS DE LA PRODUCCIÓN. (pág.5 )
	 */
	TEMPORARY_OPT2(ContractPdfModel.TEMPORARY, 5, ContractCode.C402, ContractCode.C502),
	/**
	 * INTERINIDAD. ( pág.6 )
	 */
	TEMPORARY_OPT3(ContractPdfModel.TEMPORARY, 6, ContractCode.C410, ContractCode.C510),
	/**
	 * PRIMER EMPLEO JOVEN. ( pág.7 )
	 */
	TEMPORARY_OPT4(ContractPdfModel.TEMPORARY, 7, ContractCode.C402, ContractCode.C502),
	/**
	 * DE TRABAJADORES EN SITUACIÓN DE EXCLUSIÓN SOCIAL, VÍCTIMAS DE VIOLENCIA DE GÉNERO, DOMÉSTICA O VÍCTIMA DE TERRORISMO. ( pág.8 )
	 */
	TEMPORARY_OPT5(ContractPdfModel.TEMPORARY, 8, ContractCode.C450, ContractCode.C550),
	/**
	 * DE TRABAJADORES EN SITUACIÓN DE EXCLUSIÓN SOCIAL POR EMPRESA DE INSERCIÓN. ( pág.9 )
	 */
	TEMPORARY_OPT6(ContractPdfModel.TEMPORARY, 9, ContractCode.C450, ContractCode.C452, ContractCode.C550, ContractCode.C552),
	/**
	 * DE TRABAJADORES MAYORES DE 52 AÑOS BENEFICIARIOS DE LOS SUBSIDIOS POR DESEMPLEO. ( pág.10 )
	 */
	TEMPORARY_OPT7(ContractPdfModel.TEMPORARY, 10, ContractCode.C401, ContractCode.C402, ContractCode.C410, ContractCode.C990),
	/**
	 * SITUACIÓN DE JUBILACIÓN PARCIAL. ( pág.11 )
	 */
	TEMPORARY_OPT8(ContractPdfModel.TEMPORARY, 11, ContractCode.C540),
	/**
	 * RELEVO. ( pág.12 )
	 */
	TEMPORARY_OPT9(ContractPdfModel.TEMPORARY, 12, ContractCode.C441, ContractCode.C541),
	/**
	 * A TIEMPO PARCIAL CON VINCULACIÓN FORMATIVA. ( pág.13 )
	 */
	TEMPORARY_OPT10(ContractPdfModel.TEMPORARY, 13, ContractCode.C501, ContractCode.C502),
	/**
	 * DE TRABAJOS DE INTERÉS SOCIAL/FOMENTO DE EMPLEO AGRARIO. ( pág.14 )
	 */
	TEMPORARY_OPT11(ContractPdfModel.TEMPORARY, 14, ContractCode.C401, ContractCode.C402, ContractCode.C410, ContractCode.C450, ContractCode.C990,
			ContractCode.C501, ContractCode.C502, ContractCode.C510, ContractCode.C550, ContractCode.C990),
	/**
	 * DE TRABAJADORES DEL SERVICIO DEL HOGAR FAMILIAR. (pág.15 )
	 */
	TEMPORARY_OPT12(ContractPdfModel.TEMPORARY, 15, ContractCode.C401, ContractCode.C410, ContractCode.C501, ContractCode.C510),
	/**
	 * DE PERSONAS CON DISCAPACIDAD. (pág.16 )
	 */
	TEMPORARY_OPT13(ContractPdfModel.TEMPORARY, 16, ContractCode.C430, ContractCode.C530),
	/**
	 * DE PERSONAS CON DISCAPACIDAD EN CENTROS ESPECIALES DE EMPLEO. (pág.17 )
	 */
	TEMPORARY_OPT14(ContractPdfModel.TEMPORARY, 17, ContractCode.C401, ContractCode.C402, ContractCode.C410, ContractCode.C430, ContractCode.C441, ContractCode.C990,
			ContractCode.C501, ContractCode.C502, ContractCode.C510, ContractCode.C530, ContractCode.C540, ContractCode.C541, ContractCode.C990),
	/**
	 * DE INVESTIGADORES. ( pág.18 )
	 */
	TEMPORARY_OPT15(ContractPdfModel.TEMPORARY, 18, ContractCode.C401, ContractCode.C420, ContractCode.C501, ContractCode.C520),
	/**
	 * DE TRABAJADOES/AS PENADOS EN INSTITUCIONES PENITENCIARIAS. (pág.19 )
	 */
	TEMPORARY_OPT16(ContractPdfModel.TEMPORARY, 19, ContractCode.C450, ContractCode.C550),
	/**
	 * DE MENORES Y JÓVENES EN CENTROS DE MENORES. ( SOMETIDOS A MEDIDADAS DE INTERNAMIENTO PREVISTAS EN LA LEY ORGÁNICA 5/2000 DE 21 DE ENERO ). ( pág.20 )
	 */
	TEMPORARY_OPT17(ContractPdfModel.TEMPORARY, 20, ContractCode.C450, ContractCode.C550),
	/**
	 * OTRAS SITUACIONES. ( pág.21 )
	 */
	TEMPORARY_OPT18(ContractPdfModel.TEMPORARY, 21, ContractCode.C990),
	
	/**
	 * BECARIOS
	 */
	INTERNSHIP(ContractPdfModel.INTERNSHIP, null),
	
	/**
	 *  NUEVO FAMILIARES CONTRATADO DE AUTONOMOS. (pág 17 no se si esto esta bien)
	 */
	INDEFINITE_OPT18(ContractPdfModel.INDEFINITE, 17, ContractCode.C250, ContractCode.C350),
	
	/**
	 *  PARADOS DE LARGA DURACION. (pág 17 no se si esto esta bien)
	 */
	INDEFINITE_OPT19(ContractPdfModel.INDEFINITE, 17, ContractCode.C150, ContractCode.C250),
	;
	
	private ContractPdfModel pdfModel;
	private Integer pageNumber;
	private ContractCode[] codes;
	
	private ModelOption(ContractPdfModel pdfModel, Integer pageNumber, ContractCode... codes) {
		this.pdfModel = pdfModel;
		this.pageNumber = pageNumber;
		this.codes = codes;
	}
	
	public Integer getPageNumber() {
		return pageNumber;
	}
	
	public ContractCode[] getCodes() {
		return codes;
	}
	
	public String getPdfModel(){
		if(pdfModel==ContractPdfModel.INDEFINITE){
			return IndefiniteModel.MODEL_NAME;
		} else if(pdfModel==ContractPdfModel.LEARNING){
			return LearningModel.MODEL_NAME;
		} else if(pdfModel==ContractPdfModel.PRACTICE){
			return PracticeModel.MODEL_NAME;
		} else if(pdfModel==ContractPdfModel.TEMPORARY){
			return TemporaryModel.MODEL_NAME;
		} else if(pdfModel==ContractPdfModel.INTERNSHIP){
			return InternshipModel.MODEL_NAME;
		}
		return null;
	}
	
	/** Message key prefix. */
	private static final String MSG_KEY_PREFIX = "aon_enum_contract_model_";
	
	@Override
	public String getName(Locale locale) {
	    ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
	}
	
}
	
	