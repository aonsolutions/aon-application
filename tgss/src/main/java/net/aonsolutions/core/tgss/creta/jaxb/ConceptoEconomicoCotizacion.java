package net.aonsolutions.core.tgss.creta.jaxb;

public  enum  ConceptoEconomicoCotizacion {
	
	C300("PERCEPCIONES"){
		@Override
		public <T> T visit(IVisitor<T> visitor) {
			return visitor.visitPerception(this);
		}
	},
	C500("CONTINGENCIAS COMUNES"){
		@Override
		public <T> T visit(IVisitor<T> visitor) {
			return visitor.visitCommonContingency(this);
		}
	},
	C501("HORAS EXTRAS FUERZA MAYOR"){
		@Override
		public <T> T visit(IVisitor<T> visitor) {
			return visitor.visitForceOvertime(this);
		}
	},
	C502("OTRAS HORAS EXTRAS"){
		@Override
		public <T> T visit(IVisitor<T> visitor) {
			return visitor.visitOtherOvertime(this);
		}
	},
	C509("CONTING.COM.COTIZ.EMPRESARIAL"){
		@Override
		public <T> T visit(IVisitor<T> visitor) {
			return visitor.visitEnterpriseCommonContingency(this);
		}
	},
	C535("CONT. COM. MATERNIDAD T. PARCIAL"){
		@Override
		public <T> T visit(IVisitor<T> visitor) {
			return visitor.visitMaternityPartCommonContingency(this);
		}
	},
	C536("CONT. COM. EXP. REG. EMPLEO T. PARCIAL"){
		@Override
		public <T> T visit(IVisitor<T> visitor) {
			return visitor.visitErePartCommonContingency(this);
		}
	},
	C537("HORAS COMPLEMENTARIAS"){
		@Override
		public <T> T visit(IVisitor<T> visitor) {
			return visitor.visitAdditionalHours(this);
		}
	},
	C563("COMPENSACION IT ENFERMEDAD COMUN"){
		@Override
		public <T> T visit(IVisitor<T> visitor) {
			return visitor.visitCommonIT(this);
		}
	},
	C601("IT DE ACCIDENTES DE TRABAJO"){
		@Override
		public <T> T visit(IVisitor<T> visitor) {
			return visitor.visitWorkIT(this);
		}
	},
	C603("CUOTA IT DE AT Y EP DE SITUACIONES ESPEC"){
		@Override
		public <T> T visit(IVisitor<T> visitor) {
			return visitor.visitSpecialWorkIT(this);
		}
	},
	C611("IMS DE ACCIDENTES DE TRABAJO"){
		@Override
		public <T> T visit(IVisitor<T> visitor) {
			return visitor.visitWorkIMS(this);
		}
	},
	C613("CUOTA IMS DE AT Y EP DE SITUACIONES ESPE"){
		@Override
		public <T> T visit(IVisitor<T> visitor) {
			return visitor.visitSpecialWorkIMS(this);
		}
	},
	C634("IMS DE AT MATERNIDAD PARCIAL"){
		@Override
		public <T> T visit(IVisitor<T> visitor) {
			return visitor.visitMaternityPartIMS(this);
		}
	},
	C635("IT DE AT MATERNIDAD PARCIAL"){
		@Override
		public <T> T visit(IVisitor<T> visitor) {
			return visitor.visitMaternityPartIT(this);
		}
	},
	C636("IT DE AT REGULACION DE EMPLEO PARCIAL"){
		@Override
		public <T> T visit(IVisitor<T> visitor) {
			return visitor.visitErePartIT(this);
		}
	},
	C637("IMS DE AT REGULACION DE EMPLEO PARCIAL"){
		@Override
		public <T> T visit(IVisitor<T> visitor) {
			return visitor.visitErePartIMS(this);
		}
	},
	C663("COMP.IT POR ACCIDENTE DE TRABAJO"){
		@Override
		public <T> T visit(IVisitor<T> visitor) {
			return visitor.visitWorkITComplement(this);
		}
	},
	C701("CUOTAS POR DESEMPLEO"){
		@Override
		public <T> T visit(IVisitor<T> visitor) {
			return visitor.visitUnemployment(this);
		}
	},
	C705("CUOTA DESEMPLEO COTIZACION EMPRESARIAL"){
		@Override
		public <T> T visit(IVisitor<T> visitor) {
			return visitor.visitEnterpriseUnemployment(this);
		}
	},
	C708("DESEMPLEO MATERNIDAD TIEMPO PARCIAL"){
		@Override
		public <T> T visit(IVisitor<T> visitor) {
			return visitor.visitMaternityPartUnemployment(this);
		}
	},
	C711("DESEMPLEO ERE TIEMPO PARCIAL"){
		@Override
		public <T> T visit(IVisitor<T> visitor) {
			return visitor.visitErePartUnemployment(this);
		}
	},
	C763("BONIFICACION FORMACION CONTINUA"){
		@Override
		public <T> T visit(IVisitor<T> visitor) {
			return visitor.visitEducationBonus(this);
		}
	},
	C737("BONIFICACIÓN TUTORÍA"){
		@Override
		public <T> T visit(IVisitor<T> visitor) {
			return visitor.visitTutorshipBonus(this);
		}
	},
	;
	
	
	private String description ;

	private ConceptoEconomicoCotizacion(String descripcion) {
		this.description = descripcion;
	}
	
	public String getDescription() {
		return description;
	}
	
	public static ConceptoEconomicoCotizacion conceptoOf(String codigo){
		String name = String.format("C%s", codigo);
		return ConceptoEconomicoCotizacion.valueOf(name);
	}

	public static ConceptoEconomicoCotizacion conceptoOf(int codigo){
		String name = String.format("C%d", codigo);
		return ConceptoEconomicoCotizacion.valueOf(name);
	}
	
	public abstract <T> T visit(IVisitor<T> visitor);
	
	public static interface IVisitor<T> {
		
		T visitPerception(ConceptoEconomicoCotizacion cec);
		T visitCommonContingency(ConceptoEconomicoCotizacion cec);
		T visitForceOvertime(ConceptoEconomicoCotizacion cec);
		T visitOtherOvertime(ConceptoEconomicoCotizacion cec);
		T visitEnterpriseCommonContingency(ConceptoEconomicoCotizacion cec);
		T visitMaternityPartCommonContingency(ConceptoEconomicoCotizacion cec);
		T visitErePartCommonContingency(ConceptoEconomicoCotizacion cec);
		T visitAdditionalHours(ConceptoEconomicoCotizacion cec);
		T visitCommonIT(ConceptoEconomicoCotizacion cec);
		T visitWorkIT(ConceptoEconomicoCotizacion cec);
		T visitSpecialWorkIT(ConceptoEconomicoCotizacion cec);
		T visitWorkIMS(ConceptoEconomicoCotizacion cec);
		T visitSpecialWorkIMS(ConceptoEconomicoCotizacion cec);
		T visitMaternityPartIT(ConceptoEconomicoCotizacion cec);
		T visitMaternityPartIMS(ConceptoEconomicoCotizacion cec);
		T visitErePartIT(ConceptoEconomicoCotizacion cec);
		T visitErePartIMS(ConceptoEconomicoCotizacion cec);
		T visitWorkITComplement(ConceptoEconomicoCotizacion cec);
		T visitUnemployment(ConceptoEconomicoCotizacion cec);
		T visitEnterpriseUnemployment(ConceptoEconomicoCotizacion cec);
		T visitMaternityPartUnemployment(ConceptoEconomicoCotizacion cec);
		T visitErePartUnemployment(ConceptoEconomicoCotizacion cec);
		T visitEducationBonus(ConceptoEconomicoCotizacion cec);
		T visitTutorshipBonus(ConceptoEconomicoCotizacion cec);
		
	}
	
	
}
