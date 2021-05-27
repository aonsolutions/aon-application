
package eus.bizkaia.ogasuna.sii.documentos.respuestasuministro;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.PersonaFisicaJuridicaType;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.RegistroDuplicadoType;


/**
 *  Respuesta a un envío Sii 
 * 
 * <p>Clase Java para RespuestaMetalicoType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="RespuestaMetalicoType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="PeriodoLiquidacion"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="Ejercicio" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}YearType"/&gt;
 *                   &lt;element name="Periodo" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}TipoPeriodoType"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Contraparte" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}PersonaFisicaJuridicaType"/&gt;
 *         &lt;element name="EstadoRegistro" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/RespuestaSuministro.xsd}EstadoRegistroType"/&gt;
 *         &lt;element name="CodigoErrorRegistro" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/RespuestaSuministro.xsd}ErrorDetalleType" minOccurs="0"/&gt;
 *         &lt;element name="DescripcionErrorRegistro" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}TextMax500Type" minOccurs="0"/&gt;
 *         &lt;element name="CSV" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="RegistroDuplicado" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}RegistroDuplicadoType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RespuestaMetalicoType", propOrder = {
    "periodoLiquidacion",
    "contraparte",
    "estadoRegistro",
    "codigoErrorRegistro",
    "descripcionErrorRegistro",
    "csv",
    "registroDuplicado"
})
public class RespuestaMetalicoType {

    @XmlElement(name = "PeriodoLiquidacion", required = true)
    protected RespuestaMetalicoType.PeriodoLiquidacion periodoLiquidacion;
    @XmlElement(name = "Contraparte", required = true)
    protected PersonaFisicaJuridicaType contraparte;
    @XmlElement(name = "EstadoRegistro", required = true)
    @XmlSchemaType(name = "string")
    protected EstadoRegistroType estadoRegistro;
    @XmlElement(name = "CodigoErrorRegistro")
    protected BigInteger codigoErrorRegistro;
    @XmlElement(name = "DescripcionErrorRegistro")
    protected String descripcionErrorRegistro;
    @XmlElement(name = "CSV")
    protected String csv;
    @XmlElement(name = "RegistroDuplicado")
    protected RegistroDuplicadoType registroDuplicado;

    /**
     * Obtiene el valor de la propiedad periodoLiquidacion.
     * 
     * @return
     *     possible object is
     *     {@link RespuestaMetalicoType.PeriodoLiquidacion }
     *     
     */
    public RespuestaMetalicoType.PeriodoLiquidacion getPeriodoLiquidacion() {
        return periodoLiquidacion;
    }

    /**
     * Define el valor de la propiedad periodoLiquidacion.
     * 
     * @param value
     *     allowed object is
     *     {@link RespuestaMetalicoType.PeriodoLiquidacion }
     *     
     */
    public void setPeriodoLiquidacion(RespuestaMetalicoType.PeriodoLiquidacion value) {
        this.periodoLiquidacion = value;
    }

    /**
     * Obtiene el valor de la propiedad contraparte.
     * 
     * @return
     *     possible object is
     *     {@link PersonaFisicaJuridicaType }
     *     
     */
    public PersonaFisicaJuridicaType getContraparte() {
        return contraparte;
    }

    /**
     * Define el valor de la propiedad contraparte.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonaFisicaJuridicaType }
     *     
     */
    public void setContraparte(PersonaFisicaJuridicaType value) {
        this.contraparte = value;
    }

    /**
     * Obtiene el valor de la propiedad estadoRegistro.
     * 
     * @return
     *     possible object is
     *     {@link EstadoRegistroType }
     *     
     */
    public EstadoRegistroType getEstadoRegistro() {
        return estadoRegistro;
    }

    /**
     * Define el valor de la propiedad estadoRegistro.
     * 
     * @param value
     *     allowed object is
     *     {@link EstadoRegistroType }
     *     
     */
    public void setEstadoRegistro(EstadoRegistroType value) {
        this.estadoRegistro = value;
    }

    /**
     * Obtiene el valor de la propiedad codigoErrorRegistro.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCodigoErrorRegistro() {
        return codigoErrorRegistro;
    }

    /**
     * Define el valor de la propiedad codigoErrorRegistro.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCodigoErrorRegistro(BigInteger value) {
        this.codigoErrorRegistro = value;
    }

    /**
     * Obtiene el valor de la propiedad descripcionErrorRegistro.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescripcionErrorRegistro() {
        return descripcionErrorRegistro;
    }

    /**
     * Define el valor de la propiedad descripcionErrorRegistro.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescripcionErrorRegistro(String value) {
        this.descripcionErrorRegistro = value;
    }

    /**
     * Obtiene el valor de la propiedad csv.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCSV() {
        return csv;
    }

    /**
     * Define el valor de la propiedad csv.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCSV(String value) {
        this.csv = value;
    }

    /**
     * Obtiene el valor de la propiedad registroDuplicado.
     * 
     * @return
     *     possible object is
     *     {@link RegistroDuplicadoType }
     *     
     */
    public RegistroDuplicadoType getRegistroDuplicado() {
        return registroDuplicado;
    }

    /**
     * Define el valor de la propiedad registroDuplicado.
     * 
     * @param value
     *     allowed object is
     *     {@link RegistroDuplicadoType }
     *     
     */
    public void setRegistroDuplicado(RegistroDuplicadoType value) {
        this.registroDuplicado = value;
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
     *         &lt;element name="Ejercicio" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}YearType"/&gt;
     *         &lt;element name="Periodo" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}TipoPeriodoType"/&gt;
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
