
package https.sii_araba_eus.documentos.consultalr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import https.sii_araba_eus.documentos.suministroinformacion.FacturaModificadaType;
import https.sii_araba_eus.documentos.suministroinformacion.RangoFechaPresentacionType;


/**
 * <p>Clase Java para LRFiltroVentaBienesConsignaType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="LRFiltroVentaBienesConsignaType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Ejercicio" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}YearType"/&gt;
 *         &lt;element name="Periodo" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}TipoPeriodoType"/&gt;
 *         &lt;element name="IdRegistro" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}TextMax60Type" minOccurs="0"/&gt;
 *         &lt;element name="FechaPresentacion" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}RangoFechaPresentacionType" minOccurs="0"/&gt;
 *         &lt;element name="OperacionModificada" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}FacturaModificadaType" minOccurs="0"/&gt;
 *         &lt;element name="ClavePaginacion" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="IdRegistro" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}TextMax60Type"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LRFiltroVentaBienesConsignaType", propOrder = {
    "ejercicio",
    "periodo",
    "idRegistro",
    "fechaPresentacion",
    "operacionModificada",
    "clavePaginacion"
})
public class LRFiltroVentaBienesConsignaType {

    @XmlElement(name = "Ejercicio", required = true)
    protected String ejercicio;
    @XmlElement(name = "Periodo", required = true)
    protected String periodo;
    @XmlElement(name = "IdRegistro")
    protected String idRegistro;
    @XmlElement(name = "FechaPresentacion")
    protected RangoFechaPresentacionType fechaPresentacion;
    @XmlElement(name = "OperacionModificada")
    @XmlSchemaType(name = "string")
    protected FacturaModificadaType operacionModificada;
    @XmlElement(name = "ClavePaginacion")
    protected LRFiltroVentaBienesConsignaType.ClavePaginacion clavePaginacion;

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

    /**
     * Obtiene el valor de la propiedad idRegistro.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdRegistro() {
        return idRegistro;
    }

    /**
     * Define el valor de la propiedad idRegistro.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdRegistro(String value) {
        this.idRegistro = value;
    }

    /**
     * Obtiene el valor de la propiedad fechaPresentacion.
     * 
     * @return
     *     possible object is
     *     {@link RangoFechaPresentacionType }
     *     
     */
    public RangoFechaPresentacionType getFechaPresentacion() {
        return fechaPresentacion;
    }

    /**
     * Define el valor de la propiedad fechaPresentacion.
     * 
     * @param value
     *     allowed object is
     *     {@link RangoFechaPresentacionType }
     *     
     */
    public void setFechaPresentacion(RangoFechaPresentacionType value) {
        this.fechaPresentacion = value;
    }

    /**
     * Obtiene el valor de la propiedad operacionModificada.
     * 
     * @return
     *     possible object is
     *     {@link FacturaModificadaType }
     *     
     */
    public FacturaModificadaType getOperacionModificada() {
        return operacionModificada;
    }

    /**
     * Define el valor de la propiedad operacionModificada.
     * 
     * @param value
     *     allowed object is
     *     {@link FacturaModificadaType }
     *     
     */
    public void setOperacionModificada(FacturaModificadaType value) {
        this.operacionModificada = value;
    }

    /**
     * Obtiene el valor de la propiedad clavePaginacion.
     * 
     * @return
     *     possible object is
     *     {@link LRFiltroVentaBienesConsignaType.ClavePaginacion }
     *     
     */
    public LRFiltroVentaBienesConsignaType.ClavePaginacion getClavePaginacion() {
        return clavePaginacion;
    }

    /**
     * Define el valor de la propiedad clavePaginacion.
     * 
     * @param value
     *     allowed object is
     *     {@link LRFiltroVentaBienesConsignaType.ClavePaginacion }
     *     
     */
    public void setClavePaginacion(LRFiltroVentaBienesConsignaType.ClavePaginacion value) {
        this.clavePaginacion = value;
    }


    /**
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="IdRegistro" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}TextMax60Type"/&gt;
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
        "idRegistro"
    })
    public static class ClavePaginacion {

        @XmlElement(name = "IdRegistro", required = true)
        protected String idRegistro;

        /**
         * Obtiene el valor de la propiedad idRegistro.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getIdRegistro() {
            return idRegistro;
        }

        /**
         * Define el valor de la propiedad idRegistro.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setIdRegistro(String value) {
            this.idRegistro = value;
        }

    }

}
