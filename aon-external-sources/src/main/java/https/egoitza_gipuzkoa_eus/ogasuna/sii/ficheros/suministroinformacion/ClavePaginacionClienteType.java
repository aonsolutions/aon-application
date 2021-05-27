
package https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 *  Clave Paginacion de la consulta de facturas informadas por el cliente 
 * 
 * <p>Clase Java para ClavePaginacionClienteType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ClavePaginacionClienteType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="IDEmisorFactura"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="NombreRazon" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}TextMax120Type"/&gt;
 *                   &lt;element name="NIF" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}NIFType"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="NumSerieFacturaEmisor" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}TextoIDFacturaType"/&gt;
 *         &lt;element name="FechaExpedicionFacturaEmisor" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}fecha"/&gt;
 *         &lt;element name="Cliente" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}PersonaFisicaJuridicaUnicaESType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClavePaginacionClienteType", propOrder = {
    "idEmisorFactura",
    "numSerieFacturaEmisor",
    "fechaExpedicionFacturaEmisor",
    "cliente"
})
public class ClavePaginacionClienteType {

    @XmlElement(name = "IDEmisorFactura", required = true)
    protected ClavePaginacionClienteType.IDEmisorFactura idEmisorFactura;
    @XmlElement(name = "NumSerieFacturaEmisor", required = true)
    protected String numSerieFacturaEmisor;
    @XmlElement(name = "FechaExpedicionFacturaEmisor", required = true)
    protected String fechaExpedicionFacturaEmisor;
    @XmlElement(name = "Cliente", required = true)
    protected PersonaFisicaJuridicaUnicaESType cliente;

    /**
     * Obtiene el valor de la propiedad idEmisorFactura.
     * 
     * @return
     *     possible object is
     *     {@link ClavePaginacionClienteType.IDEmisorFactura }
     *     
     */
    public ClavePaginacionClienteType.IDEmisorFactura getIDEmisorFactura() {
        return idEmisorFactura;
    }

    /**
     * Define el valor de la propiedad idEmisorFactura.
     * 
     * @param value
     *     allowed object is
     *     {@link ClavePaginacionClienteType.IDEmisorFactura }
     *     
     */
    public void setIDEmisorFactura(ClavePaginacionClienteType.IDEmisorFactura value) {
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
     * Obtiene el valor de la propiedad cliente.
     * 
     * @return
     *     possible object is
     *     {@link PersonaFisicaJuridicaUnicaESType }
     *     
     */
    public PersonaFisicaJuridicaUnicaESType getCliente() {
        return cliente;
    }

    /**
     * Define el valor de la propiedad cliente.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonaFisicaJuridicaUnicaESType }
     *     
     */
    public void setCliente(PersonaFisicaJuridicaUnicaESType value) {
        this.cliente = value;
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
     *         &lt;element name="NombreRazon" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}TextMax120Type"/&gt;
     *         &lt;element name="NIF" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}NIFType"/&gt;
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
        "nif"
    })
    public static class IDEmisorFactura {

        @XmlElement(name = "NombreRazon", required = true)
        protected String nombreRazon;
        @XmlElement(name = "NIF", required = true)
        protected String nif;

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

    }

}
