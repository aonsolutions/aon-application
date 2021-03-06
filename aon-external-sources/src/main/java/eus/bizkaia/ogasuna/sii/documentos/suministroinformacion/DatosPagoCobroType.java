
package eus.bizkaia.ogasuna.sii.documentos.suministroinformacion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para DatosPagoCobroType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DatosPagoCobroType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Fecha" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}fecha"/&gt;
 *         &lt;element name="Importe" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}ImporteSgn12.2Type"/&gt;
 *         &lt;element name="Medio" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}MedioPagoType"/&gt;
 *         &lt;element name="Cuenta_O_Medio" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}TextMax34Type" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DatosPagoCobroType", propOrder = {
    "fecha",
    "importe",
    "medio",
    "cuentaOMedio"
})
public class DatosPagoCobroType {

    @XmlElement(name = "Fecha", required = true)
    protected String fecha;
    @XmlElement(name = "Importe", required = true)
    protected String importe;
    @XmlElement(name = "Medio", required = true)
    protected String medio;
    @XmlElement(name = "Cuenta_O_Medio")
    protected String cuentaOMedio;

    /**
     * Obtiene el valor de la propiedad fecha.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFecha() {
        return fecha;
    }

    /**
     * Define el valor de la propiedad fecha.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFecha(String value) {
        this.fecha = value;
    }

    /**
     * Obtiene el valor de la propiedad importe.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImporte() {
        return importe;
    }

    /**
     * Define el valor de la propiedad importe.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImporte(String value) {
        this.importe = value;
    }

    /**
     * Obtiene el valor de la propiedad medio.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMedio() {
        return medio;
    }

    /**
     * Define el valor de la propiedad medio.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMedio(String value) {
        this.medio = value;
    }

    /**
     * Obtiene el valor de la propiedad cuentaOMedio.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCuentaOMedio() {
        return cuentaOMedio;
    }

    /**
     * Define el valor de la propiedad cuentaOMedio.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCuentaOMedio(String value) {
        this.cuentaOMedio = value;
    }

}
