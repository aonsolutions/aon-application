
package https.sii_araba_eus.documentos.respuestasuministro;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import https.sii_araba_eus.documentos.suministroinformacion.CabeceraSiiCobrosPagos;
import https.sii_araba_eus.documentos.suministroinformacion.DatosPresentacionType;


/**
 *  Respuesta a un envío Sii de baja
 * 
 * <p>Clase Java para RespuestaInmueblesType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="RespuestaInmueblesType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CSV" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="DatosPresentacion" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}DatosPresentacionType" minOccurs="0"/&gt;
 *         &lt;element name="Cabecera" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}CabeceraSiiCobrosPagos"/&gt;
 *         &lt;element name="EstadoEnvio" type="{https://sii.araba.eus/documentos/RespuestaSuministro.xsd}EstadoEnvioType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RespuestaInmueblesType", propOrder = {
    "csv",
    "datosPresentacion",
    "cabecera",
    "estadoEnvio"
})
@XmlSeeAlso({
    RespuestaLRInmueblesType.class
})
public class RespuestaInmueblesType {

    @XmlElement(name = "CSV")
    protected String csv;
    @XmlElement(name = "DatosPresentacion")
    protected DatosPresentacionType datosPresentacion;
    @XmlElement(name = "Cabecera", required = true)
    protected CabeceraSiiCobrosPagos cabecera;
    @XmlElement(name = "EstadoEnvio", required = true)
    @XmlSchemaType(name = "string")
    protected EstadoEnvioType estadoEnvio;

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
     * Obtiene el valor de la propiedad datosPresentacion.
     * 
     * @return
     *     possible object is
     *     {@link DatosPresentacionType }
     *     
     */
    public DatosPresentacionType getDatosPresentacion() {
        return datosPresentacion;
    }

    /**
     * Define el valor de la propiedad datosPresentacion.
     * 
     * @param value
     *     allowed object is
     *     {@link DatosPresentacionType }
     *     
     */
    public void setDatosPresentacion(DatosPresentacionType value) {
        this.datosPresentacion = value;
    }

    /**
     * Obtiene el valor de la propiedad cabecera.
     * 
     * @return
     *     possible object is
     *     {@link CabeceraSiiCobrosPagos }
     *     
     */
    public CabeceraSiiCobrosPagos getCabecera() {
        return cabecera;
    }

    /**
     * Define el valor de la propiedad cabecera.
     * 
     * @param value
     *     allowed object is
     *     {@link CabeceraSiiCobrosPagos }
     *     
     */
    public void setCabecera(CabeceraSiiCobrosPagos value) {
        this.cabecera = value;
    }

    /**
     * Obtiene el valor de la propiedad estadoEnvio.
     * 
     * @return
     *     possible object is
     *     {@link EstadoEnvioType }
     *     
     */
    public EstadoEnvioType getEstadoEnvio() {
        return estadoEnvio;
    }

    /**
     * Define el valor de la propiedad estadoEnvio.
     * 
     * @param value
     *     allowed object is
     *     {@link EstadoEnvioType }
     *     
     */
    public void setEstadoEnvio(EstadoEnvioType value) {
        this.estadoEnvio = value;
    }

}
