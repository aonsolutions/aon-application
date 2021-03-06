
package eus.bizkaia.ogasuna.sii.documentos.suministroinformacion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import eus.bizkaia.ogasuna.sii.documentos.suministrolr.SuministroLRAgenciasViajes;
import eus.bizkaia.ogasuna.sii.documentos.suministrolr.SuministroLRBienesInversion;
import eus.bizkaia.ogasuna.sii.documentos.suministrolr.SuministroLRCobrosMetalico;
import eus.bizkaia.ogasuna.sii.documentos.suministrolr.SuministroLRDetOperacionIntracomunitaria;
import eus.bizkaia.ogasuna.sii.documentos.suministrolr.SuministroLRFacturasEmitidas;
import eus.bizkaia.ogasuna.sii.documentos.suministrolr.SuministroLRFacturasRecibidas;
import eus.bizkaia.ogasuna.sii.documentos.suministrolr.SuministroLROperacionesSeguros;


/**
 *  Sii - Suministro Inmediato de Información, compuesto por datos 
 * 			                              de contexto y una secuencia de 1 o más registros. 
 * 
 * <p>Clase Java para SuministroInformacion complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="SuministroInformacion"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Cabecera" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}CabeceraSii"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SuministroInformacion", propOrder = {
    "cabecera"
})
@XmlSeeAlso({
    SuministroLRDetOperacionIntracomunitaria.class,
    SuministroLROperacionesSeguros.class,
    SuministroLRCobrosMetalico.class,
    SuministroLRAgenciasViajes.class,
    SuministroLRBienesInversion.class,
    SuministroLRFacturasRecibidas.class,
    SuministroLRFacturasEmitidas.class
})
public class SuministroInformacion {

    @XmlElement(name = "Cabecera", required = true)
    protected CabeceraSii cabecera;

    /**
     * Obtiene el valor de la propiedad cabecera.
     * 
     * @return
     *     possible object is
     *     {@link CabeceraSii }
     *     
     */
    public CabeceraSii getCabecera() {
        return cabecera;
    }

    /**
     * Define el valor de la propiedad cabecera.
     * 
     * @param value
     *     allowed object is
     *     {@link CabeceraSii }
     *     
     */
    public void setCabecera(CabeceraSii value) {
        this.cabecera = value;
    }

}
