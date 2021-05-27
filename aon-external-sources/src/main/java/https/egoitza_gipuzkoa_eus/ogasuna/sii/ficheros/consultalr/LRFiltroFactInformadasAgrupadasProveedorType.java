
package https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.consultalr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion.PersonaFisicaJuridicaUnicaESType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion.RegistroSiiImputacion;


/**
 * <p>Clase Java para LRFiltroFactInformadasAgrupadasProveedorType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="LRFiltroFactInformadasAgrupadasProveedorType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}RegistroSiiImputacion"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Proveedor" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}PersonaFisicaJuridicaUnicaESType" minOccurs="0"/&gt;
 *         &lt;element name="EstadoCuadre" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}EstadoCuadreImputacionType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LRFiltroFactInformadasAgrupadasProveedorType", propOrder = {
    "proveedor",
    "estadoCuadre"
})
public class LRFiltroFactInformadasAgrupadasProveedorType
    extends RegistroSiiImputacion
{

    @XmlElement(name = "Proveedor")
    protected PersonaFisicaJuridicaUnicaESType proveedor;
    @XmlElement(name = "EstadoCuadre")
    protected String estadoCuadre;

    /**
     * Obtiene el valor de la propiedad proveedor.
     * 
     * @return
     *     possible object is
     *     {@link PersonaFisicaJuridicaUnicaESType }
     *     
     */
    public PersonaFisicaJuridicaUnicaESType getProveedor() {
        return proveedor;
    }

    /**
     * Define el valor de la propiedad proveedor.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonaFisicaJuridicaUnicaESType }
     *     
     */
    public void setProveedor(PersonaFisicaJuridicaUnicaESType value) {
        this.proveedor = value;
    }

    /**
     * Obtiene el valor de la propiedad estadoCuadre.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEstadoCuadre() {
        return estadoCuadre;
    }

    /**
     * Define el valor de la propiedad estadoCuadre.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEstadoCuadre(String value) {
        this.estadoCuadre = value;
    }

}
