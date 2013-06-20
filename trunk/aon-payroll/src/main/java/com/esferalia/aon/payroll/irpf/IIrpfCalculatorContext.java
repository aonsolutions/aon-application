package com.esferalia.aon.payroll.irpf;

import java.math.BigDecimal;

public interface IIrpfCalculatorContext {



	static enum Contrato  {
		/**
		 * General.
		 */
		UNO(1),
		/**
		 * Duración inferior a 1 año, excepto relaciones esporádicas (peonadas o
		 * jornales diarios).
		 */
		DOS(2),
		/**
		 * Relaciones laborales especiales de carácter dependiente (salvo
		 * penados y discapacitados).
		 */
		TRES(3),
		/**
		 * Relaciones esporádicas propias de retribuciones por peonadas o
		 * jornales diarios.
		 */
		CUATRO(4);

		private int value;

		private Contrato(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
		

	}

	static enum Discapacidad {
		/**
		 * Sin disacapacidad
		 */
		GRADO0,
		/**
		 * Superior o igual al 33% e inferior al 65%
		 */
		GRADO1,
		/**
		 * Superior o igual al 65%
		 */
		GRADO2;

	}

	static enum SituacionFamiliar {

		/**
		 * Soltero/a, viudo/a, divorciado/a o separado/a legalmente, con hijos
		 * solteros menores de 18 años o incapacitados judicialmente que
		 * convivan exclusivamente con el perceptor, sin convivir también con el
		 * otro progenitor, siempre que proceda consignar al menos un hijo o
		 * descendiente en el apartado "Ascendientes y Descendientes
		 */
		UNO,
		/**
		 * Perceptor casado y no separado legalmente cuyo cónyuge no obtenga
		 * rentas superiores a 1.500 euros anuales, excluidas las exentas.
		 */
		DOS,
		/**
		 * Perceptor cuya situación familiar es distinta de las dos anteriores
		 * (v. gr.: solteros sin hijos; casados cuyo cónyuge obtiene rentas
		 * superiores a 1.500 euros anuales, excluidas las exentas,
		 * etc.).También se marcará esta casilla cuando el perceptor no desee
		 * manifestar su situación familiar.
		 */
		TRES;

	}

	static enum SituacionLaboral {
		/**
		 * Empleado o trabajador en activo
		 */
		TRABAJADOR_ACTIVO,
		/**
		 * Pensionista de la Seguridad Social o clases pasivas
		 */
		PENSIONISTA,
		/**
		 * Desempleado
		 */
		DESEMPLEADO,
		/**
		 * Otras situaciones
		 */
		OTRASITUACION
	}

	static interface Ascendiente {
		static enum Convivencia {
			UNO(1), NUEVE(9);

			private int value;

			private Convivencia(int value) {
				this.value = value;
			}

			public int getValue() {
				return value;
			}

		}

		int getAñoNacimiento();

		Convivencia getConvivecia();

		/**
		 * Necesita ayuda de terceras personas o tiene movilidad reducida
		 * 
		 * @return true if tiene movilidad reducida.
		 */
		boolean getMovilidadReducida();

		Discapacidad getDiscapacidad();

	}

	static interface Descendiente {
		int getAñoNacimiento();

		int getAñoAdopcion();

		boolean getComputadoEntero();

		/**
		 * Necesita ayuda de terceras personas o tiene movilidad reducida
		 * 
		 * @return true if tiene movilidad reducida.
		 */
		boolean getMovilidadReducida();

		Discapacidad getDiscapacidad();

	}

	static enum CausaRegularizacion {

		/**
		 * Circunstancias que determinan variaciones en la base para calcular el
		 * tipo de retención.
		 */
		UNO(1),
		/**
		 * Circunstancias que determinan variaciones en el importe del mínimo
		 * personal y familiar para calcular el tipo de retención.
		 */
		DOS(2),
		/**
		 * Quedar obligado judicialmente el perceptor a satisfacer pensión
		 * compensatoria al cónyuge.
		 */
		TRES(3),
		/**
		 * Quedar obligado judicialmente el perceptor a satisfacer anualidades a
		 * favor de los hijos.
		 */
		CUATRO(4),
		/**
		 * Cambio de la situación familiar "2" a la situación familiar "3"
		 */
		CINCO(5),
		/**
		 * Pérdida de la condición de residente en Ceuta o Melilla.
		 */
		SEIS(6),
		/**
		 * Adquisición de la condición de residente en Ceuta o Melilla.
		 */
		SIETE(7),
		/**
		 * Comenzar a realizar trabajos fuera de Ceuta o Melilla por residentes
		 * en dichas ciudades.
		 */
		OCHO(8),
		/**
		 * El perceptor ha comunicado que realiza pagos por préstamos destinados
		 * a la adquisición o rehabilitación de su vivienda habitual.
		 */
		NUEVE(9),
		/**
		 * El perceptor ha comunicado la improcedencia de reducción del tipo de
		 * retención por pagos por préstamos destinados a la adquisición o
		 * rehabilitación de su vivienda habitual.
		 */
		DIEZ(10),
		/**
		 * Otras causas.
		 */
		ONCE(11);

		private int value;

		private CausaRegularizacion(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

	}

	boolean next();

	// ------------------------------------------------ TipoRetenedorEntrada2013
	String getRetenedorNif();

	String getRetenedorApellidosNombre();

	// ------------------------------------------------- TipoRetenidoEntrada2013
	String getNif();

	String getApellidosNombre();

	int getAñoNacimiento();

	String getComunidadAutonoma();

	SituacionLaboral getSituacionLaboral();

	Contrato getContrato();

	/**
	 * Movilidad geográfica
	 * 
	 * @return true if Movilidad geográfica.
	 */
	boolean getMovilidadGeografica();

	/**
	 * Prolongación de la actividad laboral.
	 * 
	 * @return
	 */
	boolean getProlongacionLaboral();

	SituacionFamiliar getSituacionFamiliar();

	String getNifConyuge();

	/**
	 * Necesita ayuda de terceras personas o tiene movilidad reducida
	 * 
	 * @return true if tiene movilidad reducida.
	 */
	boolean getMovilidadReducida();

	Discapacidad getDiscapacidad();

	/**
	 * Residencia habitual en Ceuta o Melilla.
	 * 
	 * @return true if Residencia habitual en Ceuta o Melilla.
	 */
	boolean getResidenciaCeutaMelilla();

	/**
	 * Rendimientos obtenidos en Ceuta o Melilla.
	 * 
	 * @return true if Rendimientos obtenidos en Ceuta o Melilla.
	 */
	boolean getRdtosObtenidosCeutaMelilla();

	/**
	 * Retribuciones totales (dinerarias y en especie).
	 * 
	 * @return Retribuciones totales
	 */
	BigDecimal getRetribAnuales();

	/**
	 * Gastos deducibles (Art. 19.2, letras a, b y c de la LIRPF: Seguridad
	 * Social, Mutualidades de funcionarios, derechos pasivos, colegios de
	 * huérfanos o instituciones similares).
	 * 
	 * @return Gastos deducibles
	 */
	BigDecimal getGastosAnuales();
	
	/**
	 * Reducciones por irregularidad (Art. 18.2 LIRPF).
	 * @return Reducciones
	 */
	BigDecimal getIrregularidad1();
	
	/**
	 * Reducciones por irregularidad (Art. 18.3; Disposiciones transitorias 11ª y 12ª de la LIRPF).
	 * @return Reducciones
	 */
	BigDecimal getIrregularidad2();
	
	/**
	 * Pensión compensatoria a favor del cónyuge. Importe fijado judicialmente.
	 * 
	 * @return Pensión compensatoria
	 */
	BigDecimal getPensionCompensatoria();

	/**
	 * Anualidades por alimentos en favor de los hijos. Importe fijado
	 * judicialmente.
	 * 
	 * @return Anualidades por alimentos en favor de los hijos.
	 */
	BigDecimal getAnualidadesHijos();

	/**
	 * El perceptor ha comunicado que efectúa pagos por préstamos para la
	 * adquisición o rehabilitación de su vivienda habitual con derecho a
	 * deducción en el IRPF y que sus retribuciones íntegras totales son
	 * inferiores a 33.007,2 euros anuales
	 * 
	 * @return true if efectúa pagos por préstamos para la adquisición o
	 *         rehabilitación de su vivienda habitual.
	 */
	boolean getPagoPrestamosVivienda();

	Iterable<Ascendiente> getAscendientes();

	Iterable<Descendiente> getDescendientes();

	/**
	 * Retribuciones ya satisfechas con anterioridad a la regularización.
	 * 
	 * @return Retribuciones ya satisfechas
	 */
	BigDecimal getRetribSatisfechas();

	/**
	 * Retenciones e ingresos a cuenta ya practicados con anterioridad a la
	 * regularización.
	 * 
	 * @return Retenciones e ingresos a cuenta ya practicados
	 */
	BigDecimal getRetencionPracticada();

	/**
	 * Retribuciones anuales consideradas con anterioridad a la regularización.
	 * 
	 * @return Retribuciones anuales iniciales.
	 */
	BigDecimal getRetribAnualesIniciales();

	/**
	 * Importe anual de las retenciones e ingresos a cuenta determinadas antes
	 * de la regularización
	 * 
	 * @return Retenciones e ingresos a cuenta iniciales.
	 */
	BigDecimal getRetencionAnualInicial();

	/**
	 * Los rendimientos anteriores a la regularización fueron obtenidos en Ceuta
	 * o Melilla.
	 * 
	 * @return true Obtenidos en Ceuta o Melilla.
	 */
	boolean getResidenciaInicialCeutaMelilla();

	/**
	 * Base para calcular el tipo de retención determinado antes de la
	 * regularización.
	 * 
	 * @return Base para calcular el tipo de retención.
	 */
	BigDecimal getBaseRetencion();

	/**
	 * Mínimo personal y familiar para calcular el tipo de retención determinado
	 * antes de la regularización.
	 * 
	 * @return Mínimo personal y familiar
	 */
	BigDecimal getMinimoPersonalFamiliarInicial();

	/**
	 * Importe de la minoración por pagos de préstamos para la vivienda
	 * determinado antes de la regularización.
	 * 
	 * @return Minoración por pagos de préstamos para la vivienda.
	 */
	BigDecimal getMinoracionPrestamosVivienda();

	/**
	 * Tipo de retención aplicado con anterioridad a la regularización
	 * 
	 * @return Tipo de retención.
	 */
	BigDecimal getTipoRetencion();

	CausaRegularizacion getCausaRegularizacion();
	
	

}
