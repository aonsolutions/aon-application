package com.esferalia.aon.gwt.payroll.shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ContractType {
	public class ModelRecord{
		private Integer id;
		private String description;
		
		public ModelRecord(Integer id, String description) {
			this.id = id;
			this.description = description;
		}
		
		public Integer getEnumeration(){
			return this.id;
		}
		
		public String getModelDescription(){
			return this.description;
		}
	}
	
	public class ContractTypeRecord{
		private String drescription;
		private List<ModelRecord> models;
		
		public ContractTypeRecord(String description) {
			this.drescription = description;
			this.models = new ArrayList<ContractType.ModelRecord>();
		}
		
		public void addNewModel(Integer id, String description){
			this.models.add(new ModelRecord(id, description));
		}
		
		public String getContractTypeDescription(){
			return this.drescription;
		}
		
		public List<ModelRecord> getContractModels(){
			return this.models;
		}
	}
	
	private Map<Integer, ContractTypeRecord> contractTypes;
	
	public ContractType() {
		this.contractTypes = new HashMap<Integer, ContractTypeRecord>();
		
		//Initialize static contact type map
		this.contractTypes.put(000, new ContractTypeRecord("BECARIO"));
		this.contractTypes.put(100, new ContractTypeRecord("INDEFINIDO, TIEMPO COMPLETO, ORDINARIO"));
		this.contractTypes.put(109, new ContractTypeRecord("INDEFINIDO, TIEMPO COMPLETO, FOMENTO CONTRATACION INDEFINIDA/EMPLEO, TRANSFORMACION CONTRATO TEMPORAL"));
		this.contractTypes.put(130, new ContractTypeRecord("INDEFINIDO, TIEMPO COMPLETO, MINUSVALIDOS"));
		this.contractTypes.put(139, new ContractTypeRecord("INDEFINIDO, TIEMPO COMPLETO, MINUSVALIDOS, TRANSFORMACION CONTRATO TEMPORAL"));
		this.contractTypes.put(150, new ContractTypeRecord("INDEFINIDO, TIEMPO COMPLETO, FOMENTO CONTRATACION INDEFINIDA/EMPLEO, INICIAL"));
		this.contractTypes.put(189, new ContractTypeRecord("INDEFINIDO, TIEMPO COMPLETO, TRANSFORMACION CONTRATO TEMPORAL"));
		this.contractTypes.put(200, new ContractTypeRecord("INDEFINIDO, TIEMPO PARCIAL, ORDINARIO"));
		this.contractTypes.put(209, new ContractTypeRecord("INDEFINIDO, TIEMPO PARCIAL, FOMENTO CONTRATACION INDEFINIDA/EMPLEO, TRANSFORMACION CONTRATO TEMPORAL"));
		this.contractTypes.put(230, new ContractTypeRecord("INDEFINIDO, TIEMPO PARCIAL, MINUSVALIDOS"));
		this.contractTypes.put(239, new ContractTypeRecord("INDEFINIDO, TIEMPO PARCIAL, MINUSVALIDOS, TRANSFORMACION CONTRATO TEMPORAL"));
		this.contractTypes.put(250, new ContractTypeRecord("INDEFINIDO, TIEMPO PARCIAL, FOMENTO CONTRATACION INDEFINIDA/EMPLEO, INICIAL"));
		this.contractTypes.put(289, new ContractTypeRecord("INDEFINIDO, TIEMPO PARCIAL, TRANSFORMACION CONTRATO TEMPORAL"));
		this.contractTypes.put(300, new ContractTypeRecord("INDEFINIDO, FIJO/DISCONTINUO"));
		this.contractTypes.put(309, new ContractTypeRecord("INDEFINIDO, FIJO/DISCONTINUO, FOMENTO CONTRATACION INDEFINIDA/EMPLEO, TRANSFORMACION CONTRATO TEMPORAL"));
		this.contractTypes.put(330, new ContractTypeRecord("INDEFINIDO, FIJO/DISCONTINUO, MINUSVALIDOS"));
		this.contractTypes.put(339, new ContractTypeRecord("INDEFINIDO, FIJO/DISCONTINUO, MINUSVALIDOS, TRANSFORMACION CONTRATO TEMPORAL"));
		this.contractTypes.put(350, new ContractTypeRecord("INDEFINIDO, FIJO/DISCONTINUO, FOMENTO CONTRATACION INDEFINIDA/EMPLEO, INICIAL"));
		this.contractTypes.put(389, new ContractTypeRecord("INDEFINIDO, FIJO/DISCONTINUO, TRANSFORMACION CONTRATO TEMPORAL"));
		this.contractTypes.put(401, new ContractTypeRecord("DURACION DETERMINADA, TIEMPO COMPLETO, OBRA O SERVICIO DETERMINADO"));
		this.contractTypes.put(402, new ContractTypeRecord("DURACION DETERMINADA, TIEMPO COMPLETO, EVENTUAL POR CIRCUNSTANCIAS DE LA PORDUCCION"));
		this.contractTypes.put(403, new ContractTypeRecord("DURACION DETERMINADA, TIEMPO COMPLETO, INSERCION"));
		this.contractTypes.put(408, new ContractTypeRecord("TEMPORAL, TIEMPO COMPLETO, CARACTER ADMINISTRATIVO"));
		this.contractTypes.put(410, new ContractTypeRecord("DURACION DETERMINADA, TIEMPO COMPLETO, INTERINIDAD"));
		this.contractTypes.put(418, new ContractTypeRecord("DURACION DETERMINADA, TIEMPO COMPLETO, INTERINIDAD, CARACTER ADMINISTRATIVO"));
		this.contractTypes.put(420, new ContractTypeRecord("TEMPORAL, TIEMPO COMPLETO, PRACTICAS"));
		this.contractTypes.put(421, new ContractTypeRecord("TEMPORAL, TIEMPO COMPLETO, FORMACION"));
		this.contractTypes.put(430, new ContractTypeRecord("TEMPORAL, TIEMPO COMPLETO, MINUSVALIDOS"));
		this.contractTypes.put(441, new ContractTypeRecord("TEMPORAL, TIEMPO COMPLETO, RELEVO"));
		this.contractTypes.put(450, new ContractTypeRecord("TEMPORAL, TIEMPO COMPLETO, FOMENTO CONTRATACION INDEFINIDA"));
		this.contractTypes.put(452, new ContractTypeRecord("TEMPORAL, TIEMPO COMPLETO, FOMENTO DEL EMPLEO"));
		this.contractTypes.put(501, new ContractTypeRecord("DURACION DETERMINADA, TIEMPO PARCIAL, OBRA O SERVICIO DETERMINADO"));
		this.contractTypes.put(502, new ContractTypeRecord("DURACION DETERMINADA, TIEMPO PARCIAL, EVENTUAL POR CIRCUNSTANCIAS"));
		this.contractTypes.put(503, new ContractTypeRecord("DURACION DETERMINADA, TIEMPO PARCIAL, INSERCION"));
		this.contractTypes.put(508, new ContractTypeRecord("TEMPORAL, TIEMPO PARCIAL, CARACTER ADMINISTRATIVO"));
		this.contractTypes.put(510, new ContractTypeRecord("DURACION DETERMINADA, TIEMPO PARCIAL, INTERINIDAD"));
		this.contractTypes.put(518, new ContractTypeRecord("DURACION DETERMINADA, TIEMPO PARCIAL, INTERINIDAD, CARACTER ADMINISTRATIVO"));
		this.contractTypes.put(520, new ContractTypeRecord("TEMPORAL, TIEMPO PARCIAL, PRACTICAS"));
		this.contractTypes.put(530, new ContractTypeRecord("TEMPORAL, TIEMPO PARCIAL, MINUSVALIDOS"));
		this.contractTypes.put(540, new ContractTypeRecord("TEMPORAL, TIEMPO PARCIAL, JUBILADO PARCIAL"));
		this.contractTypes.put(541, new ContractTypeRecord("TEMPORAL, TIEMPO PARCIAL, RELEVO"));
		this.contractTypes.put(550, new ContractTypeRecord("TEMPORAL, TIEMPO PARCIAL, FOMENTO CONTRATACION INDEFINIDA/EMPLEO ESTABLE"));
		this.contractTypes.put(552, new ContractTypeRecord("TEMPORAL, TIEMPO PARCIAL, FOMENTO DEL EMPLEO"));
		this.contractTypes.put(970, new ContractTypeRecord("ADSCRIPCION A COLABORACION SOCIAL"));
		this.contractTypes.put(980, new ContractTypeRecord("JUBILACION ESPECIAL A LOS 64 A" + String.valueOf("\u00D1") + "OS"));
		this.contractTypes.put(990, new ContractTypeRecord("OTROS CONTRATOS"));
		
		//Initialice models of contract
		this.contractTypes.get(100).addNewModel(0, "INDEFINIDO ORDINARIO");
		this.contractTypes.get(200).addNewModel(0, "INDEFINIDO ORDINARIO");
		this.contractTypes.get(300).addNewModel(0, "INDEFINIDO ORDINARIO");
		this.contractTypes.get(130).addNewModel(1, "INDEFINIDO DE PERSONAS CON DISCAPACIDAD");
		this.contractTypes.get(230).addNewModel(1, "INDEFINIDO DE PERSONAS CON DISCAPACIDAD");
		this.contractTypes.get(330).addNewModel(1, "INDEFINIDO DE PERSONAS CON DISCAPACIDAD");
		this.contractTypes.get(150).addNewModel(2, "INDEFINIDO DE PERSONAS CON DISCAPACIDAD EN CENTROS ESPECIALES DE EMPLEO");
		this.contractTypes.get(250).addNewModel(2, "INDEFINIDO DE PERSONAS CON DISCAPACIDAD EN CENTROS ESPECIALES DE EMPLEO");
		this.contractTypes.get(350).addNewModel(2, "INDEFINIDO DE PERSONAS CON DISCAPACIDAD EN CENTROS ESPECIALES DE EMPLEO");
		this.contractTypes.get(130).addNewModel(3, "INDEFINIDO DE PERSONAS CON DISCAPACIDAD PROCEDENTES DE ENCLAVES LABORALES");
		this.contractTypes.get(230).addNewModel(3, "INDEFINIDO DE PERSONAS CON DISCAPACIDAD PROCEDENTES DE ENCLAVES LABORALES");
		this.contractTypes.get(330).addNewModel(3, "INDEFINIDO DE PERSONAS CON DISCAPACIDAD PROCEDENTES DE ENCLAVES LABORALES");
		this.contractTypes.get(100).addNewModel(4, "INDEFINIDO DE APOYO A LOS EMPRENDEDORES");
		this.contractTypes.get(200).addNewModel(4, "INDEFINIDO DE APOYO A LOS EMPRENDEDORES");
		this.contractTypes.get(300).addNewModel(4, "INDEFINIDO DE APOYO A LOS EMPRENDEDORES");
		this.contractTypes.get(150).addNewModel(4, "INDEFINIDO DE APOYO A LOS EMPRENDEDORES");
		this.contractTypes.get(250).addNewModel(4, "INDEFINIDO DE APOYO A LOS EMPRENDEDORES");
		this.contractTypes.get(350).addNewModel(4, "INDEFINIDO DE APOYO A LOS EMPRENDEDORES");
		this.contractTypes.get(100).addNewModel(5, "INDEFINIDO DE UN JOVEN POR MICROEMPRESAS Y EMPRESARIOS AUTONOMOS");
		this.contractTypes.get(200).addNewModel(5, "INDEFINIDO DE UN JOVEN POR MICROEMPRESAS Y EMPRESARIOS AUTONOMOS");
		this.contractTypes.get(100).addNewModel(6, "INDEFINIDO DE NUEVO PROYECTO DE EMPRENDIMIENTO JOVEN");
		this.contractTypes.get(200).addNewModel(6, "INDEFINIDO DE NUEVO PROYECTO DE EMPRENDIMIENTO JOVEN");
		this.contractTypes.get(300).addNewModel(6, "INDEFINIDO DE NUEVO PROYECTO DE EMPRENDIMIENTO JOVEN");
		this.contractTypes.get(200).addNewModel(7, "INDEFINIDO A TIEMPO PARCIAL CON VINCULACION FORMATIVA");
		this.contractTypes.get(300).addNewModel(7, "INDEFINIDO A TIEMPO PARCIAL CON VINCULACION FORMATIVA");
		this.contractTypes.get(150).addNewModel(8, "INDEFINIDO DE TRABAJADORES EN SITUACION DE EXCLUSION SOCIAL, VICTIMAS DE VIOLENCIA DE GENERO, DOMESTICA O VICTIMAS DE TERRORISMO");
		this.contractTypes.get(250).addNewModel(8, "INDEFINIDO DE TRABAJADORES EN SITUACION DE EXCLUSION SOCIAL, VICTIMAS DE VIOLENCIA DE GENERO, DOMESTICA O VICTIMAS DE TERRORISMO");
		this.contractTypes.get(350).addNewModel(8, "INDEFINIDO DE TRABAJADORES EN SITUACION DE EXCLUSION SOCIAL, VICTIMAS DE VIOLENCIA DE GENERO, DOMESTICA O VICTIMAS DE TERRORISMO");
		this.contractTypes.get(150).addNewModel(9, "INDEFINIDO DE EXCLUIDOS EN EMPRESAS DE INSERCION");
		this.contractTypes.get(250).addNewModel(9, "INDEFINIDO DE EXCLUIDOS EN EMPRESAS DE INSERCION");
		this.contractTypes.get(350).addNewModel(9, "INDEFINIDO DE EXCLUIDOS EN EMPRESAS DE INSERCION");
		this.contractTypes.get(100).addNewModel(10, "INDEFINIDO DE MAYORES DE 52 A" + String.valueOf("\u00D1") + "OS BENEFICIARIOS DE SUBSIDIOS POR DESEMPLEO");
		this.contractTypes.get(150).addNewModel(10, "INDEFINIDO DE MAYORES DE 52 A" + String.valueOf("\u00D1") + "OS BENEFICIARIOS DE SUBSIDIOS POR DESEMPLEO");
		this.contractTypes.get(300).addNewModel(10, "INDEFINIDO DE MAYORES DE 52 A" + String.valueOf("\u00D1") + "OS BENEFICIARIOS DE SUBSIDIOS POR DESEMPLEO");
		this.contractTypes.get(350).addNewModel(10, "INDEFINIDO DE MAYORES DE 52 A" + String.valueOf("\u00D1") + "OS BENEFICIARIOS DE SUBSIDIOS POR DESEMPLEO");
		this.contractTypes.get(150).addNewModel(11, "INDEFINIDO PROCENTE DE PRIMER EMPLEO JOVEN DE ETT");
		this.contractTypes.get(250).addNewModel(11, "INDEFINIDO PROCENTE DE PRIMER EMPLEO JOVEN DE ETT");
		this.contractTypes.get(350).addNewModel(11, "INDEFINIDO PROCENTE DE PRIMER EMPLEO JOVEN DE ETT");
		this.contractTypes.get(100).addNewModel(12, "INDEFINIDO PROCEDENTE DE UN CONTRATO PARA LA FORMACION Y EL APRENDIZAJE DE ETT");
		this.contractTypes.get(200).addNewModel(12, "INDEFINIDO PROCEDENTE DE UN CONTRATO PARA LA FORMACION Y EL APRENDIZAJE DE ETT");
		this.contractTypes.get(300).addNewModel(12, "INDEFINIDO PROCEDENTE DE UN CONTRATO PARA LA FORMACION Y EL APRENDIZAJE DE ETT");
		this.contractTypes.get(150).addNewModel(13, "INDEFINIDO PROCEDENTE DE UN CONTRATO EN PRACTICAS DE ETT");
		this.contractTypes.get(250).addNewModel(13, "INDEFINIDO PROCEDENTE DE UN CONTRATO EN PRACTICAS DE ETT");
		this.contractTypes.get(350).addNewModel(13, "INDEFINIDO PROCEDENTE DE UN CONTRATO EN PRACTICAS DE ETT");
		this.contractTypes.get(100).addNewModel(14, "INDEFINIDO DEL SERVICIO DEL HOGAR FAMILIAR");
		this.contractTypes.get(200).addNewModel(14, "INDEFINIDO DEL SERVICIO DEL HOGAR FAMILIAR");
		this.contractTypes.get(990).addNewModel(15, "INDEFINIDO OTRAS SITUACIONES");
		this.contractTypes.get(109).addNewModel(16, "CONVERSION DE CONTRATO TEMPORAL EN CONTRATO INDEFINIDO");
		this.contractTypes.get(139).addNewModel(16, "CONVERSION DE CONTRATO TEMPORAL EN CONTRATO INDEFINIDO");
		this.contractTypes.get(189).addNewModel(16, "CONVERSION DE CONTRATO TEMPORAL EN CONTRATO INDEFINIDO");
		this.contractTypes.get(209).addNewModel(16, "CONVERSION DE CONTRATO TEMPORAL EN CONTRATO INDEFINIDO");
		this.contractTypes.get(239).addNewModel(16, "CONVERSION DE CONTRATO TEMPORAL EN CONTRATO INDEFINIDO");
		this.contractTypes.get(289).addNewModel(16, "CONVERSION DE CONTRATO TEMPORAL EN CONTRATO INDEFINIDO");
		this.contractTypes.get(309).addNewModel(16, "CONVERSION DE CONTRATO TEMPORAL EN CONTRATO INDEFINIDO");
		this.contractTypes.get(339).addNewModel(16, "CONVERSION DE CONTRATO TEMPORAL EN CONTRATO INDEFINIDO");
		this.contractTypes.get(389).addNewModel(16, "CONVERSION DE CONTRATO TEMPORAL EN CONTRATO INDEFINIDO");
		this.contractTypes.get(421).addNewModel(17, "FORMACION Y APRENDIZAJE ( ORDINARIO )");
		this.contractTypes.get(450).addNewModel(18, "FORMACION DE TRABAJADORES EN SITUACION DE EXCLUSION SOCIAL, VICTIMAS DE VIOLENCIA DE GENERO, DOMESTICA O VICTIMA DE TERRORISMO");
		this.contractTypes.get(421).addNewModel(19, "FORMACION DE PERSONAS CON DISCAPACIDAD EN CENTROS ESPECIALES DE EMPLEO");
		this.contractTypes.get(421).addNewModel(20, "FORMACION DE TRABAJOS DE INTERES SOCIAL/FOMENTO DE EMPLEO AGRARIO");
		this.contractTypes.get(420).addNewModel(21, "PRACTICAS ( ORDINARIO )");
		this.contractTypes.get(520).addNewModel(21, "PRACTICAS ( ORDINARIO )");
		this.contractTypes.get(450).addNewModel(22, "PRACTICAS DE TRABAJADORES EN SITUACION DE EXCLUSIÓN SOCIAL, VICTIMAS DE VIOLENCIA DE GENERO, DOMESTICA O VICTIMA DE TERRORISMO");
		this.contractTypes.get(550).addNewModel(22, "PRACTICAS DE TRABAJADORES EN SITUACION DE EXCLUSIÓN SOCIAL, VICTIMAS DE VIOLENCIA DE GENERO, DOMESTICA O VICTIMA DE TERRORISMO");
		this.contractTypes.get(420).addNewModel(23, "PRACTICAS DE TRABAJADORES MAYORES DE 52 A" + String.valueOf("\u00D1") + "OS BENEFICIARIOS DE LOS SUBSIDIOS POR DESEMPLEO");
		this.contractTypes.get(420).addNewModel(24, "PRACTICAS DE PERSONAS CON DISCAPACIDAD EN CENTROS ESPECIALES DE EMPLEO");
		this.contractTypes.get(520).addNewModel(24, "PRACTICAS DE PERSONAS CON DISCAPACIDAD EN CENTROS ESPECIALES DE EMPLEO");
		this.contractTypes.get(420).addNewModel(25, "PRACTICAS DE TRABAJOS DE INTERES SOCIAL/FOMENTO DE EMPLEO AGRARIO");
		this.contractTypes.get(520).addNewModel(25, "PRACTICAS DE TRABAJOS DE INTERES SOCIAL/FOMENTO DE EMPLEO AGRARIO");
		this.contractTypes.get(401).addNewModel(26, "OBRA O SERVICIO DETERMINADO");
		this.contractTypes.get(501).addNewModel(26, "OBRA O SERVICIO DETERMINADO");
		this.contractTypes.get(402).addNewModel(27, "EMPORAL EVENTUAL POR CIRCUNSTANCIAS DE LA PRODUCCION");
		this.contractTypes.get(502).addNewModel(27, "EMPORAL EVENTUAL POR CIRCUNSTANCIAS DE LA PRODUCCION");
		this.contractTypes.get(410).addNewModel(28, "TEMPORAL INTERINIDAD");
		this.contractTypes.get(510).addNewModel(28, "TEMPORAL INTERINIDAD");
		this.contractTypes.get(402).addNewModel(29, "TEMPORAL PRIMER EMPLEO JOVEN");
		this.contractTypes.get(502).addNewModel(29, "TEMPORAL PRIMER EMPLEO JOVEN");
		this.contractTypes.get(450).addNewModel(30, "TEMPORAL DE TRABAJADORES EN SITUACION DE EXCLUSION SOCIAL, VICTIMAS DE VIOLENCIA DE GENERO, DOMESTICA O VICTIMA DE TERRORISMO");
		this.contractTypes.get(550).addNewModel(30, "TEMPORAL DE TRABAJADORES EN SITUACION DE EXCLUSION SOCIAL, VICTIMAS DE VIOLENCIA DE GENERO, DOMESTICA O VICTIMA DE TERRORISMO");
		this.contractTypes.get(450).addNewModel(31, "TEMPORAL DE TRABAJADORES EN SITUACION DE EXCLUSION SOCIAL POR EMPRESA DE INSERCION");
		this.contractTypes.get(452).addNewModel(31, "TEMPORAL DE TRABAJADORES EN SITUACION DE EXCLUSION SOCIAL POR EMPRESA DE INSERCION");
		this.contractTypes.get(550).addNewModel(31, "TEMPORAL DE TRABAJADORES EN SITUACION DE EXCLUSION SOCIAL POR EMPRESA DE INSERCION");
		this.contractTypes.get(552).addNewModel(31, "TEMPORAL DE TRABAJADORES EN SITUACION DE EXCLUSION SOCIAL POR EMPRESA DE INSERCION");
		this.contractTypes.get(401).addNewModel(32, "TEMPORAL DE TRABAJADORES MAYORES DE 52 AÑOS BENEFICIARIOS DE LOS SUBSIDIOS POR DESEMPLEO");
		this.contractTypes.get(402).addNewModel(32, "TEMPORAL DE TRABAJADORES MAYORES DE 52 AÑOS BENEFICIARIOS DE LOS SUBSIDIOS POR DESEMPLEO");
		this.contractTypes.get(410).addNewModel(32, "TEMPORAL DE TRABAJADORES MAYORES DE 52 AÑOS BENEFICIARIOS DE LOS SUBSIDIOS POR DESEMPLEO");
		this.contractTypes.get(990).addNewModel(32, "TEMPORAL DE TRABAJADORES MAYORES DE 52 AÑOS BENEFICIARIOS DE LOS SUBSIDIOS POR DESEMPLEO");
		this.contractTypes.get(540).addNewModel(33, "TEMPORAL SITUACIÓN DE JUBILACION PARCIAL");
		this.contractTypes.get(441).addNewModel(34, "TEMPORAL RELEVO");
		this.contractTypes.get(541).addNewModel(34, "TEMPORAL RELEVO");
		this.contractTypes.get(501).addNewModel(35, "TEMPORAL A TIEMPO PARCIAL CON VINCULACION FORMATIVA");
		this.contractTypes.get(502).addNewModel(35, "TEMPORAL A TIEMPO PARCIAL CON VINCULACION FORMATIVA");
		this.contractTypes.get(401).addNewModel(36, "TEMPORAL DE TRABAJOS DE INTERES SOCIAL/FOMENTO DE EMPLEO AGRARIO");
		this.contractTypes.get(402).addNewModel(36, "TEMPORAL DE TRABAJOS DE INTERES SOCIAL/FOMENTO DE EMPLEO AGRARIO");
		this.contractTypes.get(410).addNewModel(36, "TEMPORAL DE TRABAJOS DE INTERES SOCIAL/FOMENTO DE EMPLEO AGRARIO");
		this.contractTypes.get(450).addNewModel(36, "TEMPORAL DE TRABAJOS DE INTERES SOCIAL/FOMENTO DE EMPLEO AGRARIO");
		this.contractTypes.get(990).addNewModel(36, "TEMPORAL DE TRABAJOS DE INTERES SOCIAL/FOMENTO DE EMPLEO AGRARIO");
		this.contractTypes.get(501).addNewModel(36, "TEMPORAL DE TRABAJOS DE INTERES SOCIAL/FOMENTO DE EMPLEO AGRARIO");
		this.contractTypes.get(502).addNewModel(36, "TEMPORAL DE TRABAJOS DE INTERES SOCIAL/FOMENTO DE EMPLEO AGRARIO");
		this.contractTypes.get(510).addNewModel(36, "TEMPORAL DE TRABAJOS DE INTERES SOCIAL/FOMENTO DE EMPLEO AGRARIO");
		this.contractTypes.get(550).addNewModel(36, "TEMPORAL DE TRABAJOS DE INTERES SOCIAL/FOMENTO DE EMPLEO AGRARIO");
		this.contractTypes.get(401).addNewModel(37, "TEMPORAL DE TRABAJADORES DEL SERVICIO DEL HOGAR FAMILIAR");
		this.contractTypes.get(410).addNewModel(37, "TEMPORAL DE TRABAJADORES DEL SERVICIO DEL HOGAR FAMILIAR");
		this.contractTypes.get(501).addNewModel(37, "TEMPORAL DE TRABAJADORES DEL SERVICIO DEL HOGAR FAMILIAR");
		this.contractTypes.get(510).addNewModel(37, "TEMPORAL DE TRABAJADORES DEL SERVICIO DEL HOGAR FAMILIAR");
		this.contractTypes.get(430).addNewModel(38, "TEMPORAL DE PERSONAS CON DISCAPACIDAD");
		this.contractTypes.get(530).addNewModel(38, "TEMPORAL DE PERSONAS CON DISCAPACIDAD");
		this.contractTypes.get(401).addNewModel(39, "TEMPORAL DE PERSONAS CON DISCAPACIDAD EN CENTROS ESPECIALES DE EMPLEO");
		this.contractTypes.get(402).addNewModel(39, "TEMPORAL DE PERSONAS CON DISCAPACIDAD EN CENTROS ESPECIALES DE EMPLEO");
		this.contractTypes.get(410).addNewModel(39, "TEMPORAL DE PERSONAS CON DISCAPACIDAD EN CENTROS ESPECIALES DE EMPLEO");
		this.contractTypes.get(430).addNewModel(39, "TEMPORAL DE PERSONAS CON DISCAPACIDAD EN CENTROS ESPECIALES DE EMPLEO");
		this.contractTypes.get(441).addNewModel(39, "TEMPORAL DE PERSONAS CON DISCAPACIDAD EN CENTROS ESPECIALES DE EMPLEO");
		this.contractTypes.get(990).addNewModel(39, "TEMPORAL DE PERSONAS CON DISCAPACIDAD EN CENTROS ESPECIALES DE EMPLEO");
		this.contractTypes.get(501).addNewModel(39, "TEMPORAL DE PERSONAS CON DISCAPACIDAD EN CENTROS ESPECIALES DE EMPLEO");
		this.contractTypes.get(502).addNewModel(39, "TEMPORAL DE PERSONAS CON DISCAPACIDAD EN CENTROS ESPECIALES DE EMPLEO");
		this.contractTypes.get(510).addNewModel(39, "TEMPORAL DE PERSONAS CON DISCAPACIDAD EN CENTROS ESPECIALES DE EMPLEO");
		this.contractTypes.get(530).addNewModel(39, "TEMPORAL DE PERSONAS CON DISCAPACIDAD EN CENTROS ESPECIALES DE EMPLEO");
		this.contractTypes.get(540).addNewModel(39, "TEMPORAL DE PERSONAS CON DISCAPACIDAD EN CENTROS ESPECIALES DE EMPLEO");
		this.contractTypes.get(541).addNewModel(39, "TEMPORAL DE PERSONAS CON DISCAPACIDAD EN CENTROS ESPECIALES DE EMPLEO");
		this.contractTypes.get(401).addNewModel(40, "TEMPORAL DE INVESTIGADORES");
		this.contractTypes.get(420).addNewModel(40, "TEMPORAL DE INVESTIGADORES");
		this.contractTypes.get(501).addNewModel(40, "TEMPORAL DE INVESTIGADORES");
		this.contractTypes.get(520).addNewModel(40, "TEMPORAL DE INVESTIGADORES");
		this.contractTypes.get(450).addNewModel(41, "TEMPORAL DE TRABAJADOES/AS PENADOS EN INSTITUCIONES PENITENCIARIAS");
		this.contractTypes.get(550).addNewModel(41, "TEMPORAL DE TRABAJADOES/AS PENADOS EN INSTITUCIONES PENITENCIARIAS");
		this.contractTypes.get(450).addNewModel(42, "TEMPORAL DE MENORES Y JOVENES EN CENTROS DE MENORES. ( SOMETIDOS A MEDIDADAS DE INTERNAMIENTO PREVISTAS EN LA LEY ORGANICA 5/2000 DE 21 DE ENERO )");
		this.contractTypes.get(550).addNewModel(42, "TEMPORAL DE MENORES Y JOVENES EN CENTROS DE MENORES. ( SOMETIDOS A MEDIDADAS DE INTERNAMIENTO PREVISTAS EN LA LEY ORGANICA 5/2000 DE 21 DE ENERO )");
		this.contractTypes.get(990).addNewModel(43, "TEMPORAL OTRAS SITUACIONES");
		this.contractTypes.get(000).addNewModel(44, "BECARIO");
	}
	
	public Map<Integer, ContractTypeRecord> getContractTypes(){
		return this.contractTypes;
	}
	
	public ContractTypeRecord getContractType(int contractType){
		return this.contractTypes.get(contractTypes);
	}
	
	public List<ModelRecord> getModelsContractType(int contractType){
		return this.contractTypes.get(contractType).getContractModels();
	}
	
	public Integer getContractTypeId(String contractTypeDescription){
		for(Entry<Integer, ContractTypeRecord> e : this.contractTypes.entrySet()){
			if(e.getValue().getContractTypeDescription() == contractTypeDescription)
				return e.getKey();
		}
		return -1;
	}
	
	public Integer getContractTypeIndex(Integer contractTypeCode){
		Integer index = 0;
		for( Integer key : this.contractTypes.keySet()){
			if(key.equals(contractTypeCode)){
				return index;
			}
			index++;
		}
		return -1;
	}
	
	public Integer getContractModelIndex(Integer contractTypeCode, Integer contactModelCode){
		if(null == contactModelCode)
			return null;
		else {
			Integer index = 0;
			
			for (ModelRecord model : this.contractTypes.get(contractTypeCode).getContractModels()){
				if(model.getEnumeration() == contactModelCode)
					return index;
				index++;
			}
			return 0;
		}
		
	}

	public Integer getContractModelId(Integer contractTypeId, String contractModelDescription) {
		for (ModelRecord model : this.contractTypes.get(contractTypeId).getContractModels()){
			if(model.getModelDescription().equals(contractModelDescription))
				return model.getEnumeration();
		}
		return -1;
	}
}
