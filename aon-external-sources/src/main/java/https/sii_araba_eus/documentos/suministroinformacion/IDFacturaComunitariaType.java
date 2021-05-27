
package https.sii_araba_eus.documentos.suministroinformacion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 *  Datos de identificación de factura 
 * 
 * <p>Clase Java para IDFacturaComunitariaType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="IDFacturaComunitariaType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="IDEmisorFactura"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="NombreRazon" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}TextMax120Type"/&gt;
 *                   &lt;choice&gt;
 *                     &lt;element name="NIF" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}NIFType"/&gt;
 *                     &lt;element name="IDOtro" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}IDOtroType"/&gt;
 *                   &lt;/choice&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="NumSerieFacturaEmisor" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}TextoIDFacturaType"/&gt;
 *         &lt;element name="FechaExpedicionFacturaEmisor" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}fecha"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IDFacturaComunitariaType", propOrder = {
    "idEmisorFactura",
    "numSerieFacturaEmisor",
    "fechaExpedicionFacturaEmisor"
})
public class IDFacturaComunitariaType {

    @XmlElement(name = "IDEmisorFactura", required = true)
    protected IDFacturaComunitariaType.IDEmisorFactura idEmisorFactura;
    @XmlElement(name = "NumSerieFacturaEmisor", required = true)
    protected String numSerieFacturaEmisor;
    @XmlElement(name = "FechaExpedicionFacturaEmisor", required = true)
    protected String fechaExpedicionFacturaEmisor;

    /**
     * Obtiene el valor de la propiedad idEmisorFactura.
     * 
     * @return
     *     possible object is
     *     {@link IDFacturaComunitariaType.IDEmisorFactura }
     *     
     */
    public IDFacturaComunitariaType.IDEmisorFactura getIDEmisorFactura() {
        return idEmisorFactura;
    }

    /**
     * Define el valor de la propiedad idEmisorFactura.
     * 
     * @param value
     *     allowed object is
     *     {@link IDFacturaComunitariaType.IDEmisorFactura }
     *     
     */
    public void setIDEmisorFactura(IDFacturaComunitariaType.IDEmisorFactura value) {
        this.idEmisorFactura = value;
    }

    /**
     * Obtiene el valor de la propiedad numSerieFacturaEmisor.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumSerieFacturaEmisor() {
        return numSerieFacturaEmisor;
    }

    /**
     * Define el valor de la propiedad numSerieFacturaEmisor.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumSerieFacturaEmisor(String value) {
        this.numSerieFacturaEmisor = value;
    }

    /**
     * Obtiene el valor de la propiedad fechaExpedicionFacturaEmisor.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFechaExpedicionFacturaEmisor() {
        return fechaExpedicionFacturaEmisor;
    }

    /**
     * Define el valor de la propiedad fechaExpedicionFacturaEmisor.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFechaExpedicionFacturaEmisor(String value) {
        this.fechaExpedicionFacturaEmisor = value;
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
     *         &lt;element name="NombreRazon" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}TextMax120Type"/&gt;
     *         &lt;choice&gt;
     *           &lt;element name="NIF" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}NIFType"/&gt;
     *           &lt;element name="IDOtro" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}IDOtroType"/&gt;
     *         &lt;/choice&gt;
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
        "nombreRazon",
        "nif",
        "idOtro"
    })
    public static class IDEmisorFactura {

        @XmlElement(name = "NombreRazon", required = true)
        protected String nombreRazon;
        @XmlElement(name = "NIF")
        protected String nif;
        @XmlElement(name = "IDOtro")
        protected IDOtroType idOtro;

        /**
         * Obtiene el valor de la propiedad nombreRazon.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getNombreRazon() {
            return nombreRazon;
        }

        /**
         * Define el valor de la propiedad nombreRazon.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setNombreRazon(String value) {
            this.nombreRazon = value;
        }

        /**
         * Obtiene el valor de la propiedad nif.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getNIF() {
            return nif;
        }

        /**
         * Define el valor de la propiedad nif.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setNIF(String value) {
            this.nif = value;
        }

        /**
         * Obtiene el valor de la propiedad idOtro.
         * 
         * @return
         *     possible object is
         *     {@link IDOtroType }
         *     
         */
        public IDOtroType getIDOtro() {
            return idOtro;
        }

        /**
         * Define el valor de la propiedad idOtro.
         * 
         * @param value
         *     allowed object is
         *     {@link IDOtroType }
         *     
         */
        public void setIDOtro(IDOtroType value) {
            this.idOtro = value;
        }

    }

}
