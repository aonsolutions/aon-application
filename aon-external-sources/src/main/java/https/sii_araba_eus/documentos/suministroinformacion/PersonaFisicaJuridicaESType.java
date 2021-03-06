
package https.sii_araba_eus.documentos.suministroinformacion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Datos de una persona física o jurídica Española con un NIF asociado
 * 
 * <p>Clase Java para PersonaFisicaJuridicaESType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="PersonaFisicaJuridicaESType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="NombreRazon" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}TextMax120Type"/&gt;
 *         &lt;element name="NIFRepresentante" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}NIFType" minOccurs="0"/&gt;
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
@XmlType(name = "PersonaFisicaJuridicaESType", propOrder = {
    "nombreRazon",
    "nifRepresentante",
    "nif"
})
public class PersonaFisicaJuridicaESType {

    @XmlElement(name = "NombreRazon", required = true)
    protected String nombreRazon;
    @XmlElement(name = "NIFRepresentante")
    protected String nifRepresentante;
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
     * Obtiene el valor de la propiedad nifRepresentante.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNIFRepresentante() {
        return nifRepresentante;
    }

    /**
     * Define el valor de la propiedad nifRepresentante.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNIFRepresentante(String value) {
        this.nifRepresentante = value;
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
