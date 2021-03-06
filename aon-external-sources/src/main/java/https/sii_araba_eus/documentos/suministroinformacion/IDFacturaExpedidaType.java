
package https.sii_araba_eus.documentos.suministroinformacion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 *  Datos de identificación de factura 
 * 
 * <p>Clase Java para IDFacturaExpedidaType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="IDFacturaExpedidaType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="IDEmisorFactura"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="NIF" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}NIFType"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="NumSerieFacturaEmisor" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}TextoIDFacturaType"/&gt;
 *         &lt;element name="NumSerieFacturaEmisorResumenFin" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}TextoIDFacturaType" minOccurs="0"/&gt;
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
@XmlType(name = "IDFacturaExpedidaType", propOrder = {
    "idEmisorFactura",
    "numSerieFacturaEmisor",
    "numSerieFacturaEmisorResumenFin",
    "fechaExpedicionFacturaEmisor"
})
public class IDFacturaExpedidaType {

    @XmlElement(name = "IDEmisorFactura", required = true)
    protected IDFacturaExpedidaType.IDEmisorFactura idEmisorFactura;
    @XmlElement(name = "NumSerieFacturaEmisor", required = true)
    protected String numSerieFacturaEmisor;
    @XmlElement(name = "NumSerieFacturaEmisorResumenFin")
    protected String numSerieFacturaEmisorResumenFin;
    @XmlElement(name = "FechaExpedicionFacturaEmisor", required = true)
    protected String fechaExpedicionFacturaEmisor;

    /**
     * Obtiene el valor de la propiedad idEmisorFactura.
     * 
     * @return
     *     possible object is
     *     {@link IDFacturaExpedidaType.IDEmisorFactura }
     *     
     */
    public IDFacturaExpedidaType.IDEmisorFactura getIDEmisorFactura() {
        return idEmisorFactura;
    }

    /**
     * Define el valor de la propiedad idEmisorFactura.
     * 
     * @param value
     *     allowed object is
     *     {@link IDFacturaExpedidaType.IDEmisorFactura }
     *     
     */
    public void setIDEmisorFactura(IDFacturaExpedidaType.IDEmisorFactura value) {
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
     * Obtiene el valor de la propiedad numSerieFacturaEmisorResumenFin.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumSerieFacturaEmisorResumenFin() {
        return numSerieFacturaEmisorResumenFin;
    }

    /**
     * Define el valor de la propiedad numSerieFacturaEmisorResumenFin.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumSerieFacturaEmisorResumenFin(String value) {
        this.numSerieFacturaEmisorResumenFin = value;
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
     *         &lt;element name="NIF" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}NIFType"/&gt;
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
        "nif"
    })
    public static class IDEmisorFactura {

        @XmlElement(name = "NIF", required = true)
        protected String nif;

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

    }

}
