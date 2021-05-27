
package https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.consultalr;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.consultalr package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ConsultaLRFacturasRecibidas_QNAME = new QName("https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/ConsultaLR.xsd", "ConsultaLRFacturasRecibidas");
    private final static QName _ConsultaLRFacturasEmitidas_QNAME = new QName("https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/ConsultaLR.xsd", "ConsultaLRFacturasEmitidas");
    private final static QName _ConsultaLRFactInformadasCliente_QNAME = new QName("https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/ConsultaLR.xsd", "ConsultaLRFactInformadasCliente");
    private final static QName _ConsultaLRFactInformadasAgrupadasCliente_QNAME = new QName("https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/ConsultaLR.xsd", "ConsultaLRFactInformadasAgrupadasCliente");
    private final static QName _ConsultaLRFactInformadasProveedor_QNAME = new QName("https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/ConsultaLR.xsd", "ConsultaLRFactInformadasProveedor");
    private final static QName _ConsultaLRFactInformadasAgrupadasProveedor_QNAME = new QName("https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/ConsultaLR.xsd", "ConsultaLRFactInformadasAgrupadasProveedor");
    private final static QName _ConsultaLRBienesInversion_QNAME = new QName("https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/ConsultaLR.xsd", "ConsultaLRBienesInversion");
    private final static QName _ConsultaLRDetOperIntracomunitarias_QNAME = new QName("https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/ConsultaLR.xsd", "ConsultaLRDetOperIntracomunitarias");
    private final static QName _ConsultaLRCobrosMetalico_QNAME = new QName("https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/ConsultaLR.xsd", "ConsultaLRCobrosMetalico");
    private final static QName _ConsultaLRAgenciasViajes_QNAME = new QName("https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/ConsultaLR.xsd", "ConsultaLRAgenciasViajes");
    private final static QName _ConsultaCobros_QNAME = new QName("https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/ConsultaLR.xsd", "ConsultaCobros");
    private final static QName _ConsultaInmueblesAdicionales_QNAME = new QName("https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/ConsultaLR.xsd", "ConsultaInmueblesAdicionales");
    private final static QName _ConsultaPagos_QNAME = new QName("https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/ConsultaLR.xsd", "ConsultaPagos");
    private final static QName _ConsultaLROperacionesSeguros_QNAME = new QName("https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/ConsultaLR.xsd", "ConsultaLROperacionesSeguros");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.consultalr
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link LRFiltroAgenciasViajesType }
     * 
     */
    public LRFiltroAgenciasViajesType createLRFiltroAgenciasViajesType() {
        return new LRFiltroAgenciasViajesType();
    }

    /**
     * Create an instance of {@link LRFiltroCobrosMetalicoType }
     * 
     */
    public LRFiltroCobrosMetalicoType createLRFiltroCobrosMetalicoType() {
        return new LRFiltroCobrosMetalicoType();
    }

    /**
     * Create an instance of {@link LRFiltroOperacionesSegurosType }
     * 
     */
    public LRFiltroOperacionesSegurosType createLRFiltroOperacionesSegurosType() {
        return new LRFiltroOperacionesSegurosType();
    }

    /**
     * Create an instance of {@link LRConsultaRecibidasType }
     * 
     */
    public LRConsultaRecibidasType createLRConsultaRecibidasType() {
        return new LRConsultaRecibidasType();
    }

    /**
     * Create an instance of {@link LRConsultaEmitidasType }
     * 
     */
    public LRConsultaEmitidasType createLRConsultaEmitidasType() {
        return new LRConsultaEmitidasType();
    }

    /**
     * Create an instance of {@link ConsultaLRFactInformadasClienteType }
     * 
     */
    public ConsultaLRFactInformadasClienteType createConsultaLRFactInformadasClienteType() {
        return new ConsultaLRFactInformadasClienteType();
    }

    /**
     * Create an instance of {@link ConsultaLRFactInformadasAgrupadasClienteType }
     * 
     */
    public ConsultaLRFactInformadasAgrupadasClienteType createConsultaLRFactInformadasAgrupadasClienteType() {
        return new ConsultaLRFactInformadasAgrupadasClienteType();
    }

    /**
     * Create an instance of {@link ConsultaLRFactInformadasProveedorType }
     * 
     */
    public ConsultaLRFactInformadasProveedorType createConsultaLRFactInformadasProveedorType() {
        return new ConsultaLRFactInformadasProveedorType();
    }

    /**
     * Create an instance of {@link ConsultaLRFactInformadasAgrupadasProveedorType }
     * 
     */
    public ConsultaLRFactInformadasAgrupadasProveedorType createConsultaLRFactInformadasAgrupadasProveedorType() {
        return new ConsultaLRFactInformadasAgrupadasProveedorType();
    }

    /**
     * Create an instance of {@link LRConsultaBienesInversionType }
     * 
     */
    public LRConsultaBienesInversionType createLRConsultaBienesInversionType() {
        return new LRConsultaBienesInversionType();
    }

    /**
     * Create an instance of {@link LRConsultaDetOperIntracomunitariasType }
     * 
     */
    public LRConsultaDetOperIntracomunitariasType createLRConsultaDetOperIntracomunitariasType() {
        return new LRConsultaDetOperIntracomunitariasType();
    }

    /**
     * Create an instance of {@link LRConsultaCobrosMetalicoType }
     * 
     */
    public LRConsultaCobrosMetalicoType createLRConsultaCobrosMetalicoType() {
        return new LRConsultaCobrosMetalicoType();
    }

    /**
     * Create an instance of {@link LRConsultaAgenciasViajesType }
     * 
     */
    public LRConsultaAgenciasViajesType createLRConsultaAgenciasViajesType() {
        return new LRConsultaAgenciasViajesType();
    }

    /**
     * Create an instance of {@link ConsultaCobrosType }
     * 
     */
    public ConsultaCobrosType createConsultaCobrosType() {
        return new ConsultaCobrosType();
    }

    /**
     * Create an instance of {@link ConsultaInmueblesAdicionalesType }
     * 
     */
    public ConsultaInmueblesAdicionalesType createConsultaInmueblesAdicionalesType() {
        return new ConsultaInmueblesAdicionalesType();
    }

    /**
     * Create an instance of {@link ConsultaPagosType }
     * 
     */
    public ConsultaPagosType createConsultaPagosType() {
        return new ConsultaPagosType();
    }

    /**
     * Create an instance of {@link LRConsultaLROperacionesSegurosType }
     * 
     */
    public LRConsultaLROperacionesSegurosType createLRConsultaLROperacionesSegurosType() {
        return new LRConsultaLROperacionesSegurosType();
    }

    /**
     * Create an instance of {@link LRFiltroEmitidasType }
     * 
     */
    public LRFiltroEmitidasType createLRFiltroEmitidasType() {
        return new LRFiltroEmitidasType();
    }

    /**
     * Create an instance of {@link LRFiltroFactInformadasClienteType }
     * 
     */
    public LRFiltroFactInformadasClienteType createLRFiltroFactInformadasClienteType() {
        return new LRFiltroFactInformadasClienteType();
    }

    /**
     * Create an instance of {@link LRFiltroFactInformadasAgrupadasClienteType }
     * 
     */
    public LRFiltroFactInformadasAgrupadasClienteType createLRFiltroFactInformadasAgrupadasClienteType() {
        return new LRFiltroFactInformadasAgrupadasClienteType();
    }

    /**
     * Create an instance of {@link LRFiltroFactInformadasProveedorType }
     * 
     */
    public LRFiltroFactInformadasProveedorType createLRFiltroFactInformadasProveedorType() {
        return new LRFiltroFactInformadasProveedorType();
    }

    /**
     * Create an instance of {@link LRFiltroFactInformadasAgrupadasProveedorType }
     * 
     */
    public LRFiltroFactInformadasAgrupadasProveedorType createLRFiltroFactInformadasAgrupadasProveedorType() {
        return new LRFiltroFactInformadasAgrupadasProveedorType();
    }

    /**
     * Create an instance of {@link LRFiltroRecibidasType }
     * 
     */
    public LRFiltroRecibidasType createLRFiltroRecibidasType() {
        return new LRFiltroRecibidasType();
    }

    /**
     * Create an instance of {@link LRFiltroBienInversionType }
     * 
     */
    public LRFiltroBienInversionType createLRFiltroBienInversionType() {
        return new LRFiltroBienInversionType();
    }

    /**
     * Create an instance of {@link LRFiltroDetOperIntracomunitariasType }
     * 
     */
    public LRFiltroDetOperIntracomunitariasType createLRFiltroDetOperIntracomunitariasType() {
        return new LRFiltroDetOperIntracomunitariasType();
    }

    /**
     * Create an instance of {@link LRFiltroCobrosType }
     * 
     */
    public LRFiltroCobrosType createLRFiltroCobrosType() {
        return new LRFiltroCobrosType();
    }

    /**
     * Create an instance of {@link LRFiltroInmueblesAdicionalesType }
     * 
     */
    public LRFiltroInmueblesAdicionalesType createLRFiltroInmueblesAdicionalesType() {
        return new LRFiltroInmueblesAdicionalesType();
    }

    /**
     * Create an instance of {@link LRFiltroPagosType }
     * 
     */
    public LRFiltroPagosType createLRFiltroPagosType() {
        return new LRFiltroPagosType();
    }

    /**
     * Create an instance of {@link LRFiltroAgenciasViajesType.ClavePaginacion }
     * 
     */
    public LRFiltroAgenciasViajesType.ClavePaginacion createLRFiltroAgenciasViajesTypeClavePaginacion() {
        return new LRFiltroAgenciasViajesType.ClavePaginacion();
    }

    /**
     * Create an instance of {@link LRFiltroCobrosMetalicoType.ClavePaginacion }
     * 
     */
    public LRFiltroCobrosMetalicoType.ClavePaginacion createLRFiltroCobrosMetalicoTypeClavePaginacion() {
        return new LRFiltroCobrosMetalicoType.ClavePaginacion();
    }

    /**
     * Create an instance of {@link LRFiltroOperacionesSegurosType.ClavePaginacion }
     * 
     */
    public LRFiltroOperacionesSegurosType.ClavePaginacion createLRFiltroOperacionesSegurosTypeClavePaginacion() {
        return new LRFiltroOperacionesSegurosType.ClavePaginacion();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LRConsultaRecibidasType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link LRConsultaRecibidasType }{@code >}
     */
    @XmlElementDecl(namespace = "https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/ConsultaLR.xsd", name = "ConsultaLRFacturasRecibidas")
    public JAXBElement<LRConsultaRecibidasType> createConsultaLRFacturasRecibidas(LRConsultaRecibidasType value) {
        return new JAXBElement<LRConsultaRecibidasType>(_ConsultaLRFacturasRecibidas_QNAME, LRConsultaRecibidasType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LRConsultaEmitidasType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link LRConsultaEmitidasType }{@code >}
     */
    @XmlElementDecl(namespace = "https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/ConsultaLR.xsd", name = "ConsultaLRFacturasEmitidas")
    public JAXBElement<LRConsultaEmitidasType> createConsultaLRFacturasEmitidas(LRConsultaEmitidasType value) {
        return new JAXBElement<LRConsultaEmitidasType>(_ConsultaLRFacturasEmitidas_QNAME, LRConsultaEmitidasType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConsultaLRFactInformadasClienteType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ConsultaLRFactInformadasClienteType }{@code >}
     */
    @XmlElementDecl(namespace = "https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/ConsultaLR.xsd", name = "ConsultaLRFactInformadasCliente")
    public JAXBElement<ConsultaLRFactInformadasClienteType> createConsultaLRFactInformadasCliente(ConsultaLRFactInformadasClienteType value) {
        return new JAXBElement<ConsultaLRFactInformadasClienteType>(_ConsultaLRFactInformadasCliente_QNAME, ConsultaLRFactInformadasClienteType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConsultaLRFactInformadasAgrupadasClienteType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ConsultaLRFactInformadasAgrupadasClienteType }{@code >}
     */
    @XmlElementDecl(namespace = "https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/ConsultaLR.xsd", name = "ConsultaLRFactInformadasAgrupadasCliente")
    public JAXBElement<ConsultaLRFactInformadasAgrupadasClienteType> createConsultaLRFactInformadasAgrupadasCliente(ConsultaLRFactInformadasAgrupadasClienteType value) {
        return new JAXBElement<ConsultaLRFactInformadasAgrupadasClienteType>(_ConsultaLRFactInformadasAgrupadasCliente_QNAME, ConsultaLRFactInformadasAgrupadasClienteType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConsultaLRFactInformadasProveedorType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ConsultaLRFactInformadasProveedorType }{@code >}
     */
    @XmlElementDecl(namespace = "https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/ConsultaLR.xsd", name = "ConsultaLRFactInformadasProveedor")
    public JAXBElement<ConsultaLRFactInformadasProveedorType> createConsultaLRFactInformadasProveedor(ConsultaLRFactInformadasProveedorType value) {
        return new JAXBElement<ConsultaLRFactInformadasProveedorType>(_ConsultaLRFactInformadasProveedor_QNAME, ConsultaLRFactInformadasProveedorType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConsultaLRFactInformadasAgrupadasProveedorType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ConsultaLRFactInformadasAgrupadasProveedorType }{@code >}
     */
    @XmlElementDecl(namespace = "https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/ConsultaLR.xsd", name = "ConsultaLRFactInformadasAgrupadasProveedor")
    public JAXBElement<ConsultaLRFactInformadasAgrupadasProveedorType> createConsultaLRFactInformadasAgrupadasProveedor(ConsultaLRFactInformadasAgrupadasProveedorType value) {
        return new JAXBElement<ConsultaLRFactInformadasAgrupadasProveedorType>(_ConsultaLRFactInformadasAgrupadasProveedor_QNAME, ConsultaLRFactInformadasAgrupadasProveedorType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LRConsultaBienesInversionType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link LRConsultaBienesInversionType }{@code >}
     */
    @XmlElementDecl(namespace = "https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/ConsultaLR.xsd", name = "ConsultaLRBienesInversion")
    public JAXBElement<LRConsultaBienesInversionType> createConsultaLRBienesInversion(LRConsultaBienesInversionType value) {
        return new JAXBElement<LRConsultaBienesInversionType>(_ConsultaLRBienesInversion_QNAME, LRConsultaBienesInversionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LRConsultaDetOperIntracomunitariasType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link LRConsultaDetOperIntracomunitariasType }{@code >}
     */
    @XmlElementDecl(namespace = "https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/ConsultaLR.xsd", name = "ConsultaLRDetOperIntracomunitarias")
    public JAXBElement<LRConsultaDetOperIntracomunitariasType> createConsultaLRDetOperIntracomunitarias(LRConsultaDetOperIntracomunitariasType value) {
        return new JAXBElement<LRConsultaDetOperIntracomunitariasType>(_ConsultaLRDetOperIntracomunitarias_QNAME, LRConsultaDetOperIntracomunitariasType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LRConsultaCobrosMetalicoType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link LRConsultaCobrosMetalicoType }{@code >}
     */
    @XmlElementDecl(namespace = "https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/ConsultaLR.xsd", name = "ConsultaLRCobrosMetalico")
    public JAXBElement<LRConsultaCobrosMetalicoType> createConsultaLRCobrosMetalico(LRConsultaCobrosMetalicoType value) {
        return new JAXBElement<LRConsultaCobrosMetalicoType>(_ConsultaLRCobrosMetalico_QNAME, LRConsultaCobrosMetalicoType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LRConsultaAgenciasViajesType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link LRConsultaAgenciasViajesType }{@code >}
     */
    @XmlElementDecl(namespace = "https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/ConsultaLR.xsd", name = "ConsultaLRAgenciasViajes")
    public JAXBElement<LRConsultaAgenciasViajesType> createConsultaLRAgenciasViajes(LRConsultaAgenciasViajesType value) {
        return new JAXBElement<LRConsultaAgenciasViajesType>(_ConsultaLRAgenciasViajes_QNAME, LRConsultaAgenciasViajesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConsultaCobrosType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ConsultaCobrosType }{@code >}
     */
    @XmlElementDecl(namespace = "https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/ConsultaLR.xsd", name = "ConsultaCobros")
    public JAXBElement<ConsultaCobrosType> createConsultaCobros(ConsultaCobrosType value) {
        return new JAXBElement<ConsultaCobrosType>(_ConsultaCobros_QNAME, ConsultaCobrosType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConsultaInmueblesAdicionalesType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ConsultaInmueblesAdicionalesType }{@code >}
     */
    @XmlElementDecl(namespace = "https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/ConsultaLR.xsd", name = "ConsultaInmueblesAdicionales")
    public JAXBElement<ConsultaInmueblesAdicionalesType> createConsultaInmueblesAdicionales(ConsultaInmueblesAdicionalesType value) {
        return new JAXBElement<ConsultaInmueblesAdicionalesType>(_ConsultaInmueblesAdicionales_QNAME, ConsultaInmueblesAdicionalesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConsultaPagosType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ConsultaPagosType }{@code >}
     */
    @XmlElementDecl(namespace = "https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/ConsultaLR.xsd", name = "ConsultaPagos")
    public JAXBElement<ConsultaPagosType> createConsultaPagos(ConsultaPagosType value) {
        return new JAXBElement<ConsultaPagosType>(_ConsultaPagos_QNAME, ConsultaPagosType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LRConsultaLROperacionesSegurosType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link LRConsultaLROperacionesSegurosType }{@code >}
     */
    @XmlElementDecl(namespace = "https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/ConsultaLR.xsd", name = "ConsultaLROperacionesSeguros")
    public JAXBElement<LRConsultaLROperacionesSegurosType> createConsultaLROperacionesSeguros(LRConsultaLROperacionesSegurosType value) {
        return new JAXBElement<LRConsultaLROperacionesSegurosType>(_ConsultaLROperacionesSeguros_QNAME, LRConsultaLROperacionesSegurosType.class, null, value);
    }

}
