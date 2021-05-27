
package https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.respuestaconsultalr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion.ConsultaInformacion;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion.IDFacturaRecibidaNombreBCType;


/**
 * <p>Clase Java para RespuestaConsultaFacturaPagosType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="RespuestaConsultaFacturaPagosType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}ConsultaInformacion"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="IDFactura" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}IDFacturaRecibidaNombreBCType"/&gt;
 *         &lt;element name="IndicadorPaginacion" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/RespuestaConsultaLR.xsd}IndicadorPaginacionType"/&gt;
 *         &lt;element name="ResultadoConsulta" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/RespuestaConsultaLR.xsd}ResultadoConsultaType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RespuestaConsultaFacturaPagosType", propOrder = {
    "idFactura",
    "indicadorPaginacion",
    "resultadoConsulta"
})
@XmlSeeAlso({
    RespuestaConsultaPagosType.class
})
public class RespuestaConsultaFacturaPagosType
    extends ConsultaInformacion
{

    @XmlElement(name = "IDFactura", required = true)
    protected IDFacturaRecibidaNombreBCType idFactura;
    @XmlElement(name = "IndicadorPaginacion", required = true)
    @XmlSchemaType(name = "string")
    protected IndicadorPaginacionType indicadorPaginacion;
    @XmlElement(name = "ResultadoConsulta", required = true)
    @XmlSchemaType(name = "string")
    protected ResultadoConsultaType resultadoConsulta;

    /**
     * Obtiene el valor de la propiedad idFactura.
     * 
     * @return
     *     possible object is
     *     {@link IDFacturaRecibidaNombreBCType }
     *     
     */
    public IDFacturaRecibidaNombreBCType getIDFactura() {
        return idFactura;
    }

    /**
     * Define el valor de la propiedad idFactura.
     * 
     * @param value
     *     allowed object is
     *     {@link IDFacturaRecibidaNombreBCType }
     *     
     */
    public void setIDFactura(IDFacturaRecibidaNombreBCType value) {
        this.idFactura = value;
    }

    /**
     * Obtiene el valor de la propiedad indicadorPaginacion.
     * 
     * @return
     *     possible object is
     *     {@link IndicadorPaginacionType }
     *     
     */
    public IndicadorPaginacionType getIndicadorPaginacion() {
        return indicadorPaginacion;
    }

    /**
     * Define el valor de la propiedad indicadorPaginacion.
     * 
     * @param value
     *     allowed object is
     *     {@link IndicadorPaginacionType }
     *     
     */
    public void setIndicadorPaginacion(IndicadorPaginacionType value) {
        this.indicadorPaginacion = value;
    }

    /**
     * Obtiene el valor de la propiedad resultadoConsulta.
     * 
     * @return
     *     possible object is
     *     {@link ResultadoConsultaType }
     *     
     */
    public ResultadoConsultaType getResultadoConsulta() {
        return resultadoConsulta;
    }

    /**
     * Define el valor de la propiedad resultadoConsulta.
     * 
     * @param value
     *     allowed object is
     *     {@link ResultadoConsultaType }
     *     
     */
    public void setResultadoConsulta(ResultadoConsultaType value) {
        this.resultadoConsulta = value;
    }

}
