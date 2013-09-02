package com.esferalia.aon.file.payroll.contrata;


import com.esferalia.aon.sepe.api.contract.model.IContratoType;
import com.esferalia.aon.sepe.api.contract.model.ITransformacionType;
import com.esferalia.aon.sepe.api.contrata.prorrogas.PRORROGATIPOTYPE;
import com.esferalia.aon.payroll.enumeration.ContractCode;

public class ContrataFactory {
	
	
	public IContratoType createContratoModel(String code) {
		com.esferalia.aon.sepe.api.contrata.contratos.ObjectFactory factory = new com.esferalia.aon.sepe.api.contrata.contratos.ObjectFactory();
		if (code.equals(ContractCode.C100.getValue())) {
			return factory.createCONTRATO100TYPE();
		} else if (code.equals(ContractCode.C130.getValue())) {
			return factory.createCONTRATO130TYPE();
		} else if (code.equals(ContractCode.C150.getValue())) {
			return factory.createCONTRATO150TYPE();
		} else if (code.equals(ContractCode.C200.getValue())) {
			return factory.createCONTRATO200TYPE();
		} else if (code.equals(ContractCode.C230.getValue())) {
			return factory.createCONTRATO230TYPE();
		} else if (code.equals(ContractCode.C250.getValue())) {
			return factory.createCONTRATO250TYPE();
		} else if (code.equals(ContractCode.C300.getValue())) {
			return factory.createCONTRATO300TYPE();
		} else if (code.equals(ContractCode.C330.getValue())) {
			return factory.createCONTRATO330TYPE();
		} else if (code.equals(ContractCode.C350.getValue())) {
			return factory.createCONTRATO350TYPE();
		} else if (code.equals(ContractCode.C401.getValue())) {
			return factory.createCONTRATO401TYPE();
		} else if (code.equals(ContractCode.C402.getValue())) {
			return factory.createCONTRATO402TYPE();
		} else if (code.equals(ContractCode.C403.getValue())) {
			return factory.createCONTRATO403TYPE();
		} else if (code.equals(ContractCode.C410.getValue())) {
			return factory.createCONTRATO410TYPE();
		} else if (code.equals(ContractCode.C420.getValue())) {
			return factory.createCONTRATO420TYPE();
		} else if (code.equals(ContractCode.C421.getValue())) {
			return factory.createCONTRATO421TYPE();
		} else if (code.equals(ContractCode.C430.getValue())) {
			return factory.createCONTRATO430TYPE();
		} else if (code.equals(ContractCode.C441.getValue())) {
			return factory.createCONTRATO441TYPE();
		} else if (code.equals(ContractCode.C450.getValue())) {
			return factory.createCONTRATO450TYPE();
		} else if (code.equals(ContractCode.C452.getValue())) {
			return factory.createCONTRATO452TYPE();
		} else if (code.equals(ContractCode.C501.getValue())) {
			return factory.createCONTRATO501TYPE();
		} else if (code.equals(ContractCode.C502.getValue())) {
			return factory.createCONTRATO502TYPE();
		} else if (code.equals(ContractCode.C503.getValue())) {
			return factory.createCONTRATO503TYPE();
		} else if (code.equals(ContractCode.C510.getValue())) {
			return factory.createCONTRATO510TYPE();
		} else if (code.equals(ContractCode.C520.getValue())) {
			return factory.createCONTRATO520TYPE();
		} else if (code.equals(ContractCode.C530.getValue())) {
			return factory.createCONTRATO530TYPE();
		} else if (code.equals(ContractCode.C540.getValue())) {
			return factory.createCONTRATO540TYPE();
		} else if (code.equals(ContractCode.C541.getValue())) {
			return factory.createCONTRATO541TYPE();
		} else if (code.equals(ContractCode.C550.getValue())) {
			return factory.createCONTRATO550TYPE();
		} else if (code.equals(ContractCode.C552.getValue())) {
			return factory.createCONTRATO552TYPE();
		} else if (code.equals(ContractCode.C970.getValue())) {
			return factory.createCONTRATO970TYPE();
		} else if (code.equals(ContractCode.C980.getValue())) {
			return factory.createCONTRATO980TYPE();
		} else if (code.equals(ContractCode.C990.getValue())) {
			return factory.createCONTRATO990TYPE();
		}
		return null;
	}

	
	public ITransformacionType createTransformacionesType(String code) {
		com.esferalia.aon.sepe.api.contrata.transformaciones.ObjectFactory factory = new com.esferalia.aon.sepe.api.contrata.transformaciones.ObjectFactory();
		if (code.equals(ContractCode.C109.getValue())) {
			return factory.createTRANSFORMACION109TYPE();
		} else if (code.equals(ContractCode.C139.getValue())) {
			return factory.createTRANSFORMACION139TYPE();
		} else if (code.equals(ContractCode.C189.getValue())) {
			return factory.createTRANSFORMACION189TYPE();
		} else if (code.equals(ContractCode.C209.getValue())) {
			return factory.createTRANSFORMACION209TYPE();
		} else if (code.equals(ContractCode.C239.getValue())) {
			return factory.createTRANSFORMACION239TYPE();
		} else if (code.equals(ContractCode.C289.getValue())) {
			return factory.createTRANSFORMACION289TYPE();
		} else if (code.equals(ContractCode.C309.getValue())) {
			return factory.createTRANSFORMACION309TYPE();
// TODO: nueva clave de contrato - Boletin Noticias RED 2012/05
//		} else if (code.equals(ContractCode.C339.getValue())) {
//			return factory.createTRANSFORMACION339TYPE();
		} else if (code.equals(ContractCode.C389.getValue())) {
			return factory.createTRANSFORMACION389TYPE();
		}
		return null;
	}
	

	public PRORROGATIPOTYPE createProrrogasType(String code) {
		com.esferalia.aon.sepe.api.contrata.prorrogas.ObjectFactory factory = new com.esferalia.aon.sepe.api.contrata.prorrogas.ObjectFactory();
		return factory.createPRORROGATIPOTYPE();
	}
	
	
}
