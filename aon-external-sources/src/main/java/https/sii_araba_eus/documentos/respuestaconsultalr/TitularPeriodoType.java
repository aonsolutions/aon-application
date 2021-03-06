
package https.sii_araba_eus.documentos.respuestaconsultalr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import https.sii_araba_eus.documentos.suministroinformacion.ConsultaInformacion;


/**
 * <p>Clase Java para TitularPeriodoType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="TitularPeriodoType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Cabecera" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}ConsultaInformacion"/&gt;
 *         &lt;element name="PeriodoLiquidacion"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="Ejercicio" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}YearType"/&gt;
 *                   &lt;element name="Periodo" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}TipoPeriodoType"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="IndicadorPaginacion" type="{https://sii.araba.eus/documentos/RespuestaConsultaLR.xsd}IndicadorPaginacionType"/&gt;
 *         &lt;element name="ResultadoConsulta" type="{https://sii.araba.eus/documentos/RespuestaConsultaLR.xsd}ResultadoConsultaType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TitularPeriodoType", propOrder = {
    "cabecera",
    "periodoLiquidacion",
    "indicadorPaginacion",
    "resultadoConsulta"
})
public class TitularPeriodoType {

    @XmlElement(name = "Cabecera", required = true)
    protected ConsultaInformacion cabecera;
    @XmlElement(name = "PeriodoLiquidacion", required = true)
    protected TitularPeriodoType.PeriodoLiquidacion periodoLiquidacion;
    @XmlElement(name = "IndicadorPaginacion", required = true)
    @XmlSchemaType(name = "string")
    protected IndicadorPaginacionType indicadorPaginacion;
    @XmlElement(name = "ResultadoConsulta", required = true)
    @XmlSchemaType(name = "string")
    protected ResultadoConsultaType resultadoConsulta;

    /**
     * Obtiene el valor de la propiedad cabecera.
     * 
     * @return
     *     possible object is
     *     {@link ConsultaInformacion }
     *     
     */
    public ConsultaInformacion getCabecera() {
        return cabecera;
    }

    /**
     * Define el valor de la propiedad cabecera.
     * 
     * @param value
     *     allowed object is
     *     {@link ConsultaInformacion }
     *     
     */
    public void setCabecera(ConsultaInformacion value) {
        this.cabecera = value;
    }

    /**
     * Obtiene el valor de la propiedad periodoLiquidacion.
     * 
     * @return
     *     possible object is
     *     {@link TitularPeriodoType.PeriodoLiquidacion }
     *     
     */
    public TitularPeriodoType.PeriodoLiquidacion getPeriodoLiquidacion() {
        return periodoLiquidacion;
    }

    /**
     * Define el valor de la propiedad periodoLiquidacion.
     * 
     * @param value
     *     allowed object is
     *     {@link TitularPeriodoType.PeriodoLiquidacion }
     *     
     */
    public void setPeriodoLiquidacion(TitularPeriodoType.PeriodoLiquidacion value) {
        this.periodoLiquidacion = value;
    }

    /**
     * Obtiene el valor de la propiedad indicadorPaginacion.
     * 
     * @return
     *     possible object is
     *     {@link IndicadorPaginacionType }
     *     
     */
    public IndicadorPaginacionType getIndicadorPaginacion() {
        return indicadorPaginacion;
    }

    /**
     * Define el valor de la propiedad indicadorPaginacion.
     * 
     * @param value
     *     allowed object is
     *     {@link IndicadorPaginacionType }
     *     
     */
    public void setIndicadorPaginacion(IndicadorPaginacionType value) {
        this.indicadorPaginacion = value;
    }

    /**
     * Obtiene el valor de la propiedad resultadoConsulta.
     * 
     * @return
     *     possible object is
     *     {@link ResultadoConsultaType }
     *     
     */
    public ResultadoConsultaType getResultadoConsulta() {
        return resultadoConsulta;
    }

    /**
     * Define el valor de la propiedad resultadoConsulta.
     * 
     * @param value
     *     allowed object is
     *     {@link ResultadoConsultaType }
     *     
     */
    public void setResultadoConsulta(ResultadoConsultaType value) {
        this.resultadoConsulta = value;
    }


    /**
     *  Período al que corresponden los apuntes. todos los apuntes deben corresponder al mismo período impositivo 
     * 
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="Ejercicio" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}YearType"/&gt;
     *         &lt;element name="Periodo" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}TipoPeriodoType"/&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "ejercicio",
        "periodo"
    })
    public static class PeriodoLiquidacion {

        @XmlElement(name = "Ejercicio", required = true)
        protected String ejercicio;
        @XmlElement(name = "Periodo", required = true)
        protected String periodo;

        /**
         * Obtiene el valor de la propiedad ejercicio.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getEjercicio() {
            return ejercicio;
        }

        /**
         * Define el valor de la propiedad ejercicio.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setEjercicio(String value) {
            this.ejercicio = value;
        }

        /**
         * Obtiene el valor de la propiedad periodo.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPeriodo() {
            return periodo;
        }

        /**
         * Define el valor de la propiedad periodo.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPeriodo(String value) {
            this.periodo = value;
        }

    }

}
