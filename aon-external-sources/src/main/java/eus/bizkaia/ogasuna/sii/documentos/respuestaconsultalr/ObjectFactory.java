
package eus.bizkaia.ogasuna.sii.documentos.respuestaconsultalr;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the eus.bizkaia.ogasuna.sii.documentos.respuestaconsultalr package. 
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

    private final static QName _RespuestaConsultaLRFacturasRecibidas_QNAME = new QName("http://www.bizkaia.eus/ogasuna/sii/documentos/RespuestaConsultaLR.xsd", "RespuestaConsultaLRFacturasRecibidas");
    private final static QName _RespuestaConsultaLRFactInformadasCliente_QNAME = new QName("http://www.bizkaia.eus/ogasuna/sii/documentos/RespuestaConsultaLR.xsd", "RespuestaConsultaLRFactInformadasCliente");
    private final static QName _RespuestaConsultaLRFactInformadasAgrupadasCliente_QNAME = new QName("http://www.bizkaia.eus/ogasuna/sii/documentos/RespuestaConsultaLR.xsd", "RespuestaConsultaLRFactInformadasAgrupadasCliente");
    private final static QName _RespuestaConsultaLRFactInformadasProveedor_QNAME = new QName("http://www.bizkaia.eus/ogasuna/sii/documentos/RespuestaConsultaLR.xsd", "RespuestaConsultaLRFactInformadasProveedor");
    private final static QName _RespuestaConsultaLRFactInformadasAgrupadasProveedor_QNAME = new QName("http://www.bizkaia.eus/ogasuna/sii/documentos/RespuestaConsultaLR.xsd", "RespuestaConsultaLRFactInformadasAgrupadasProveedor");
    private final static QName _RespuestaConsultaLRFacturasEmitidas_QNAME = new QName("http://www.bizkaia.eus/ogasuna/sii/documentos/RespuestaConsultaLR.xsd", "RespuestaConsultaLRFacturasEmitidas");
    private final static QName _RespuestaConsultaLRBienesInversion_QNAME = new QName("http://www.bizkaia.eus/ogasuna/sii/documentos/RespuestaConsultaLR.xsd", "RespuestaConsultaLRBienesInversion");
    private final static QName _RespuestaConsultaLRDetOperIntracomunitarias_QNAME = new QName("http://www.bizkaia.eus/ogasuna/sii/documentos/RespuestaConsultaLR.xsd", "RespuestaConsultaLRDetOperIntracomunitarias");
    private final static QName _RespuestaConsultaLRCobrosMetalico_QNAME = new QName("http://www.bizkaia.eus/ogasuna/sii/documentos/RespuestaConsultaLR.xsd", "RespuestaConsultaLRCobrosMetalico");
    private final static QName _RespuestaConsultaLRAgenciasViajes_QNAME = new QName("http://www.bizkaia.eus/ogasuna/sii/documentos/RespuestaConsultaLR.xsd", "RespuestaConsultaLRAgenciasViajes");
    private final static QName _RespuestaConsultaLROperacionesSeguros_QNAME = new QName("http://www.bizkaia.eus/ogasuna/sii/documentos/RespuestaConsultaLR.xsd", "RespuestaConsultaLROperacionesSeguros");
    private final static QName _RespuestaConsultaCobros_QNAME = new QName("http://www.bizkaia.eus/ogasuna/sii/documentos/RespuestaConsultaLR.xsd", "RespuestaConsultaCobros");
    private final static QName _RespuestaConsultaInmueblesAdicionales_QNAME = new QName("http://www.bizkaia.eus/ogasuna/sii/documentos/RespuestaConsultaLR.xsd", "RespuestaConsultaInmueblesAdicionales");
    private final static QName _RespuestaConsultaPagos_QNAME = new QName("http://www.bizkaia.eus/ogasuna/sii/documentos/RespuestaConsultaLR.xsd", "RespuestaConsultaPagos");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: eus.bizkaia.ogasuna.sii.documentos.respuestaconsultalr
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link FacturaRespuestaType }
     * 
     */
    public FacturaRespuestaType createFacturaRespuestaType() {
        return new FacturaRespuestaType();
    }

    /**
     * Create an instance of {@link FacturaRespuestaInformadaProveedorType }
     * 
     */
    public FacturaRespuestaInformadaProveedorType createFacturaRespuestaInformadaProveedorType() {
        return new FacturaRespuestaInformadaProveedorType();
    }

    /**
     * Create an instance of {@link FacturaRespuestaExpedidaType }
     * 
     */
    public FacturaRespuestaExpedidaType createFacturaRespuestaExpedidaType() {
        return new FacturaRespuestaExpedidaType();
    }

    /**
     * Create an instance of {@link RespuestaConsultaLRFacturasType }
     * 
     */
    public RespuestaConsultaLRFacturasType createRespuestaConsultaLRFacturasType() {
        return new RespuestaConsultaLRFacturasType();
    }

    /**
     * Create an instance of {@link RegistroRespuestaConsultaFactInformadasProveedorType }
     * 
     */
    public RegistroRespuestaConsultaFactInformadasProveedorType createRegistroRespuestaConsultaFactInformadasProveedorType() {
        return new RegistroRespuestaConsultaFactInformadasProveedorType();
    }

    /**
     * Create an instance of {@link RegistroRespuestaConsultaFactInformadasClienteType }
     * 
     */
    public RegistroRespuestaConsultaFactInformadasClienteType createRegistroRespuestaConsultaFactInformadasClienteType() {
        return new RegistroRespuestaConsultaFactInformadasClienteType();
    }

    /**
     * Create an instance of {@link TitularPeriodoType }
     * 
     */
    public TitularPeriodoType createTitularPeriodoType() {
        return new TitularPeriodoType();
    }

    /**
     * Create an instance of {@link RespuestaConsultaLRFacturasRecibidasType }
     * 
     */
    public RespuestaConsultaLRFacturasRecibidasType createRespuestaConsultaLRFacturasRecibidasType() {
        return new RespuestaConsultaLRFacturasRecibidasType();
    }

    /**
     * Create an instance of {@link RespuestaConsultaLRFactInformadasClienteType }
     * 
     */
    public RespuestaConsultaLRFactInformadasClienteType createRespuestaConsultaLRFactInformadasClienteType() {
        return new RespuestaConsultaLRFactInformadasClienteType();
    }

    /**
     * Create an instance of {@link RespuestaConsultaLRFactInformadasAgrupadasClienteType }
     * 
     */
    public RespuestaConsultaLRFactInformadasAgrupadasClienteType createRespuestaConsultaLRFactInformadasAgrupadasClienteType() {
        return new RespuestaConsultaLRFactInformadasAgrupadasClienteType();
    }

    /**
     * Create an instance of {@link RespuestaConsultaLRFactInformadasProveedorType }
     * 
     */
    public RespuestaConsultaLRFactInformadasProveedorType createRespuestaConsultaLRFactInformadasProveedorType() {
        return new RespuestaConsultaLRFactInformadasProveedorType();
    }

    /**
     * Create an instance of {@link RespuestaConsultaLRFactInformadasAgrupadasProveedorType }
     * 
     */
    public RespuestaConsultaLRFactInformadasAgrupadasProveedorType createRespuestaConsultaLRFactInformadasAgrupadasProveedorType() {
        return new RespuestaConsultaLRFactInformadasAgrupadasProveedorType();
    }

    /**
     * Create an instance of {@link RespuestaConsultaLRFacturasEmitidasType }
     * 
     */
    public RespuestaConsultaLRFacturasEmitidasType createRespuestaConsultaLRFacturasEmitidasType() {
        return new RespuestaConsultaLRFacturasEmitidasType();
    }

    /**
     * Create an instance of {@link RespuestaConsultaLRBienesInversionType }
     * 
     */
    public RespuestaConsultaLRBienesInversionType createRespuestaConsultaLRBienesInversionType() {
        return new RespuestaConsultaLRBienesInversionType();
    }

    /**
     * Create an instance of {@link RespuestaConsultaLRDetOperIntracomunitariasType }
     * 
     */
    public RespuestaConsultaLRDetOperIntracomunitariasType createRespuestaConsultaLRDetOperIntracomunitariasType() {
        return new RespuestaConsultaLRDetOperIntracomunitariasType();
    }

    /**
     * Create an instance of {@link RespuestaConsultaLRCobrosMetalicoType }
     * 
     */
    public RespuestaConsultaLRCobrosMetalicoType createRespuestaConsultaLRCobrosMetalicoType() {
        return new RespuestaConsultaLRCobrosMetalicoType();
    }

    /**
     * Create an instance of {@link RespuestaConsultaLRAgenciasViajesType }
     * 
     */
    public RespuestaConsultaLRAgenciasViajesType createRespuestaConsultaLRAgenciasViajesType() {
        return new RespuestaConsultaLRAgenciasViajesType();
    }

    /**
     * Create an instance of {@link RespuestaConsultaLROperacionesSegurosType }
     * 
     */
    public RespuestaConsultaLROperacionesSegurosType createRespuestaConsultaLROperacionesSegurosType() {
        return new RespuestaConsultaLROperacionesSegurosType();
    }

    /**
     * Create an instance of {@link RespuestaConsultaCobrosType }
     * 
     */
    public RespuestaConsultaCobrosType createRespuestaConsultaCobrosType() {
        return new RespuestaConsultaCobrosType();
    }

    /**
     * Create an instance of {@link RespuestaConsultaInmueblesAdicionalesType }
     * 
     */
    public RespuestaConsultaInmueblesAdicionalesType createRespuestaConsultaInmueblesAdicionalesType() {
        return new RespuestaConsultaInmueblesAdicionalesType();
    }

    /**
     * Create an instance of {@link RespuestaConsultaPagosType }
     * 
     */
    public RespuestaConsultaPagosType createRespuestaConsultaPagosType() {
        return new RespuestaConsultaPagosType();
    }

    /**
     * Create an instance of {@link RespuestaConsultaLRFacturasClienteType }
     * 
     */
    public RespuestaConsultaLRFacturasClienteType createRespuestaConsultaLRFacturasClienteType() {
        return new RespuestaConsultaLRFacturasClienteType();
    }

    /**
     * Create an instance of {@link RespuestaConsultaLRFacturasAgrupadasClienteType }
     * 
     */
    public RespuestaConsultaLRFacturasAgrupadasClienteType createRespuestaConsultaLRFacturasAgrupadasClienteType() {
        return new RespuestaConsultaLRFacturasAgrupadasClienteType();
    }

    /**
     * Create an instance of {@link RespuestaConsultaLRFacturasProveedorType }
     * 
     */
    public RespuestaConsultaLRFacturasProveedorType createRespuestaConsultaLRFacturasProveedorType() {
        return new RespuestaConsultaLRFacturasProveedorType();
    }

    /**
     * Create an instance of {@link RespuestaConsultaLRFacturasAgrupadasProveedorType }
     * 
     */
    public RespuestaConsultaLRFacturasAgrupadasProveedorType createRespuestaConsultaLRFacturasAgrupadasProveedorType() {
        return new RespuestaConsultaLRFacturasAgrupadasProveedorType();
    }

    /**
     * Create an instance of {@link EstadoFacturaType }
     * 
     */
    public EstadoFacturaType createEstadoFacturaType() {
        return new EstadoFacturaType();
    }

    /**
     * Create an instance of {@link EstadoFacturaImputacionType }
     * 
     */
    public EstadoFacturaImputacionType createEstadoFacturaImputacionType() {
        return new EstadoFacturaImputacionType();
    }

    /**
     * Create an instance of {@link DatosDescuadreContraparteType }
     * 
     */
    public DatosDescuadreContraparteType createDatosDescuadreContraparteType() {
        return new DatosDescuadreContraparteType();
    }

    /**
     * Create an instance of {@link EstadoFactura2Type }
     * 
     */
    public EstadoFactura2Type createEstadoFactura2Type() {
        return new EstadoFactura2Type();
    }

    /**
     * Create an instance of {@link RegistroRespuestaConsultaEmitidasType }
     * 
     */
    public RegistroRespuestaConsultaEmitidasType createRegistroRespuestaConsultaEmitidasType() {
        return new RegistroRespuestaConsultaEmitidasType();
    }

    /**
     * Create an instance of {@link RegistroRespuestaConsultaRecibidasType }
     * 
     */
    public RegistroRespuestaConsultaRecibidasType createRegistroRespuestaConsultaRecibidasType() {
        return new RegistroRespuestaConsultaRecibidasType();
    }

    /**
     * Create an instance of {@link RegistroRespuestaConsultaFactInformadasAgrupadasClienteType }
     * 
     */
    public RegistroRespuestaConsultaFactInformadasAgrupadasClienteType createRegistroRespuestaConsultaFactInformadasAgrupadasClienteType() {
        return new RegistroRespuestaConsultaFactInformadasAgrupadasClienteType();
    }

    /**
     * Create an instance of {@link RegistroRespuestaConsultaFactInformadasAgrupadasProveedorType }
     * 
     */
    public RegistroRespuestaConsultaFactInformadasAgrupadasProveedorType createRegistroRespuestaConsultaFactInformadasAgrupadasProveedorType() {
        return new RegistroRespuestaConsultaFactInformadasAgrupadasProveedorType();
    }

    /**
     * Create an instance of {@link RegistroRespuestaConsultaBienesType }
     * 
     */
    public RegistroRespuestaConsultaBienesType createRegistroRespuestaConsultaBienesType() {
        return new RegistroRespuestaConsultaBienesType();
    }

    /**
     * Create an instance of {@link RegistroRespuestaConsultaDetOperIntracomunitariasType }
     * 
     */
    public RegistroRespuestaConsultaDetOperIntracomunitariasType createRegistroRespuestaConsultaDetOperIntracomunitariasType() {
        return new RegistroRespuestaConsultaDetOperIntracomunitariasType();
    }

    /**
     * Create an instance of {@link RegistroRespuestaConsultaCobrosMetalicoType }
     * 
     */
    public RegistroRespuestaConsultaCobrosMetalicoType createRegistroRespuestaConsultaCobrosMetalicoType() {
        return new RegistroRespuestaConsultaCobrosMetalicoType();
    }

    /**
     * Create an instance of {@link RegistroRespuestaConsultaAgenciasViajesType }
     * 
     */
    public RegistroRespuestaConsultaAgenciasViajesType createRegistroRespuestaConsultaAgenciasViajesType() {
        return new RegistroRespuestaConsultaAgenciasViajesType();
    }

    /**
     * Create an instance of {@link RegistroRespuestaConsultaOperacionesSegurosType }
     * 
     */
    public RegistroRespuestaConsultaOperacionesSegurosType createRegistroRespuestaConsultaOperacionesSegurosType() {
        return new RegistroRespuestaConsultaOperacionesSegurosType();
    }

    /**
     * Create an instance of {@link RegistroRespuestaConsultaCobrosType }
     * 
     */
    public RegistroRespuestaConsultaCobrosType createRegistroRespuestaConsultaCobrosType() {
        return new RegistroRespuestaConsultaCobrosType();
    }

    /**
     * Create an instance of {@link RegistroRespuestaConsultaInmueblesAdicionalesType }
     * 
     */
    public RegistroRespuestaConsultaInmueblesAdicionalesType createRegistroRespuestaConsultaInmueblesAdicionalesType() {
        return new RegistroRespuestaConsultaInmueblesAdicionalesType();
    }

    /**
     * Create an instance of {@link RegistroRespuestaConsultaPagosType }
     * 
     */
    public RegistroRespuestaConsultaPagosType createRegistroRespuestaConsultaPagosType() {
        return new RegistroRespuestaConsultaPagosType();
    }

    /**
     * Create an instance of {@link RespuestaDetOperIntracomunitariaType }
     * 
     */
    public RespuestaDetOperIntracomunitariaType createRespuestaDetOperIntracomunitariaType() {
        return new RespuestaDetOperIntracomunitariaType();
    }

    /**
     * Create an instance of {@link RespuestaCobrosMetalicoType }
     * 
     */
    public RespuestaCobrosMetalicoType createRespuestaCobrosMetalicoType() {
        return new RespuestaCobrosMetalicoType();
    }

    /**
     * Create an instance of {@link RespuestaOperacionesSegurosType }
     * 
     */
    public RespuestaOperacionesSegurosType createRespuestaOperacionesSegurosType() {
        return new RespuestaOperacionesSegurosType();
    }

    /**
     * Create an instance of {@link RespuestaConsultaFacturaCobrosType }
     * 
     */
    public RespuestaConsultaFacturaCobrosType createRespuestaConsultaFacturaCobrosType() {
        return new RespuestaConsultaFacturaCobrosType();
    }

    /**
     * Create an instance of {@link RespuestaConsultaInmueblesType }
     * 
     */
    public RespuestaConsultaInmueblesType createRespuestaConsultaInmueblesType() {
        return new RespuestaConsultaInmueblesType();
    }

    /**
     * Create an instance of {@link RespuestaConsultaFacturaPagosType }
     * 
     */
    public RespuestaConsultaFacturaPagosType createRespuestaConsultaFacturaPagosType() {
        return new RespuestaConsultaFacturaPagosType();
    }

    /**
     * Create an instance of {@link FacturaRespuestaRecibidaType }
     * 
     */
    public FacturaRespuestaRecibidaType createFacturaRespuestaRecibidaType() {
        return new FacturaRespuestaRecibidaType();
    }

    /**
     * Create an instance of {@link FacturaRespuestaInformadaClienteType }
     * 
     */
    public FacturaRespuestaInformadaClienteType createFacturaRespuestaInformadaClienteType() {
        return new FacturaRespuestaInformadaClienteType();
    }

    /**
     * Create an instance of {@link FacturaRespuestaType.FacturasAgrupadas }
     * 
     */
    public FacturaRespuestaType.FacturasAgrupadas createFacturaRespuestaTypeFacturasAgrupadas() {
        return new FacturaRespuestaType.FacturasAgrupadas();
    }

    /**
     * Create an instance of {@link FacturaRespuestaType.FacturasRectificadas }
     * 
     */
    public FacturaRespuestaType.FacturasRectificadas createFacturaRespuestaTypeFacturasRectificadas() {
        return new FacturaRespuestaType.FacturasRectificadas();
    }

    /**
     * Create an instance of {@link FacturaRespuestaInformadaProveedorType.DatosInmueble }
     * 
     */
    public FacturaRespuestaInformadaProveedorType.DatosInmueble createFacturaRespuestaInformadaProveedorTypeDatosInmueble() {
        return new FacturaRespuestaInformadaProveedorType.DatosInmueble();
    }

    /**
     * Create an instance of {@link FacturaRespuestaInformadaProveedorType.TipoDesglose }
     * 
     */
    public FacturaRespuestaInformadaProveedorType.TipoDesglose createFacturaRespuestaInformadaProveedorTypeTipoDesglose() {
        return new FacturaRespuestaInformadaProveedorType.TipoDesglose();
    }

    /**
     * Create an instance of {@link FacturaRespuestaExpedidaType.DatosInmueble }
     * 
     */
    public FacturaRespuestaExpedidaType.DatosInmueble createFacturaRespuestaExpedidaTypeDatosInmueble() {
        return new FacturaRespuestaExpedidaType.DatosInmueble();
    }

    /**
     * Create an instance of {@link FacturaRespuestaExpedidaType.TipoDesglose }
     * 
     */
    public FacturaRespuestaExpedidaType.TipoDesglose createFacturaRespuestaExpedidaTypeTipoDesglose() {
        return new FacturaRespuestaExpedidaType.TipoDesglose();
    }

    /**
     * Create an instance of {@link RespuestaConsultaLRFacturasType.PeriodoLiquidacion }
     * 
     */
    public RespuestaConsultaLRFacturasType.PeriodoLiquidacion createRespuestaConsultaLRFacturasTypePeriodoLiquidacion() {
        return new RespuestaConsultaLRFacturasType.PeriodoLiquidacion();
    }

    /**
     * Create an instance of {@link RegistroRespuestaConsultaFactInformadasProveedorType.PeriodoLiquidacion }
     * 
     */
    public RegistroRespuestaConsultaFactInformadasProveedorType.PeriodoLiquidacion createRegistroRespuestaConsultaFactInformadasProveedorTypePeriodoLiquidacion() {
        return new RegistroRespuestaConsultaFactInformadasProveedorType.PeriodoLiquidacion();
    }

    /**
     * Create an instance of {@link RegistroRespuestaConsultaFactInformadasClienteType.PeriodoLiquidacion }
     * 
     */
    public RegistroRespuestaConsultaFactInformadasClienteType.PeriodoLiquidacion createRegistroRespuestaConsultaFactInformadasClienteTypePeriodoLiquidacion() {
        return new RegistroRespuestaConsultaFactInformadasClienteType.PeriodoLiquidacion();
    }

    /**
     * Create an instance of {@link TitularPeriodoType.PeriodoLiquidacion }
     * 
     */
    public TitularPeriodoType.PeriodoLiquidacion createTitularPeriodoTypePeriodoLiquidacion() {
        return new TitularPeriodoType.PeriodoLiquidacion();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RespuestaConsultaLRFacturasRecibidasType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RespuestaConsultaLRFacturasRecibidasType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.bizkaia.eus/ogasuna/sii/documentos/RespuestaConsultaLR.xsd", name = "RespuestaConsultaLRFacturasRecibidas")
    public JAXBElement<RespuestaConsultaLRFacturasRecibidasType> createRespuestaConsultaLRFacturasRecibidas(RespuestaConsultaLRFacturasRecibidasType value) {
        return new JAXBElement<RespuestaConsultaLRFacturasRecibidasType>(_RespuestaConsultaLRFacturasRecibidas_QNAME, RespuestaConsultaLRFacturasRecibidasType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RespuestaConsultaLRFactInformadasClienteType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RespuestaConsultaLRFactInformadasClienteType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.bizkaia.eus/ogasuna/sii/documentos/RespuestaConsultaLR.xsd", name = "RespuestaConsultaLRFactInformadasCliente")
    public JAXBElement<RespuestaConsultaLRFactInformadasClienteType> createRespuestaConsultaLRFactInformadasCliente(RespuestaConsultaLRFactInformadasClienteType value) {
        return new JAXBElement<RespuestaConsultaLRFactInformadasClienteType>(_RespuestaConsultaLRFactInformadasCliente_QNAME, RespuestaConsultaLRFactInformadasClienteType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RespuestaConsultaLRFactInformadasAgrupadasClienteType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RespuestaConsultaLRFactInformadasAgrupadasClienteType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.bizkaia.eus/ogasuna/sii/documentos/RespuestaConsultaLR.xsd", name = "RespuestaConsultaLRFactInformadasAgrupadasCliente")
    public JAXBElement<RespuestaConsultaLRFactInformadasAgrupadasClienteType> createRespuestaConsultaLRFactInformadasAgrupadasCliente(RespuestaConsultaLRFactInformadasAgrupadasClienteType value) {
        return new JAXBElement<RespuestaConsultaLRFactInformadasAgrupadasClienteType>(_RespuestaConsultaLRFactInformadasAgrupadasCliente_QNAME, RespuestaConsultaLRFactInformadasAgrupadasClienteType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RespuestaConsultaLRFactInformadasProveedorType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RespuestaConsultaLRFactInformadasProveedorType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.bizkaia.eus/ogasuna/sii/documentos/RespuestaConsultaLR.xsd", name = "RespuestaConsultaLRFactInformadasProveedor")
    public JAXBElement<RespuestaConsultaLRFactInformadasProveedorType> createRespuestaConsultaLRFactInformadasProveedor(RespuestaConsultaLRFactInformadasProveedorType value) {
        return new JAXBElement<RespuestaConsultaLRFactInformadasProveedorType>(_RespuestaConsultaLRFactInformadasProveedor_QNAME, RespuestaConsultaLRFactInformadasProveedorType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RespuestaConsultaLRFactInformadasAgrupadasProveedorType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RespuestaConsultaLRFactInformadasAgrupadasProveedorType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.bizkaia.eus/ogasuna/sii/documentos/RespuestaConsultaLR.xsd", name = "RespuestaConsultaLRFactInformadasAgrupadasProveedor")
    public JAXBElement<RespuestaConsultaLRFactInformadasAgrupadasProveedorType> createRespuestaConsultaLRFactInformadasAgrupadasProveedor(RespuestaConsultaLRFactInformadasAgrupadasProveedorType value) {
        return new JAXBElement<RespuestaConsultaLRFactInformadasAgrupadasProveedorType>(_RespuestaConsultaLRFactInformadasAgrupadasProveedor_QNAME, RespuestaConsultaLRFactInformadasAgrupadasProveedorType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RespuestaConsultaLRFacturasEmitidasType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RespuestaConsultaLRFacturasEmitidasType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.bizkaia.eus/ogasuna/sii/documentos/RespuestaConsultaLR.xsd", name = "RespuestaConsultaLRFacturasEmitidas")
    public JAXBElement<RespuestaConsultaLRFacturasEmitidasType> createRespuestaConsultaLRFacturasEmitidas(RespuestaConsultaLRFacturasEmitidasType value) {
        return new JAXBElement<RespuestaConsultaLRFacturasEmitidasType>(_RespuestaConsultaLRFacturasEmitidas_QNAME, RespuestaConsultaLRFacturasEmitidasType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RespuestaConsultaLRBienesInversionType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RespuestaConsultaLRBienesInversionType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.bizkaia.eus/ogasuna/sii/documentos/RespuestaConsultaLR.xsd", name = "RespuestaConsultaLRBienesInversion")
    public JAXBElement<RespuestaConsultaLRBienesInversionType> createRespuestaConsultaLRBienesInversion(RespuestaConsultaLRBienesInversionType value) {
        return new JAXBElement<RespuestaConsultaLRBienesInversionType>(_RespuestaConsultaLRBienesInversion_QNAME, RespuestaConsultaLRBienesInversionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RespuestaConsultaLRDetOperIntracomunitariasType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RespuestaConsultaLRDetOperIntracomunitariasType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.bizkaia.eus/ogasuna/sii/documentos/RespuestaConsultaLR.xsd", name = "RespuestaConsultaLRDetOperIntracomunitarias")
    public JAXBElement<RespuestaConsultaLRDetOperIntracomunitariasType> createRespuestaConsultaLRDetOperIntracomunitarias(RespuestaConsultaLRDetOperIntracomunitariasType value) {
        return new JAXBElement<RespuestaConsultaLRDetOperIntracomunitariasType>(_RespuestaConsultaLRDetOperIntracomunitarias_QNAME, RespuestaConsultaLRDetOperIntracomunitariasType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RespuestaConsultaLRCobrosMetalicoType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RespuestaConsultaLRCobrosMetalicoType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.bizkaia.eus/ogasuna/sii/documentos/RespuestaConsultaLR.xsd", name = "RespuestaConsultaLRCobrosMetalico")
    public JAXBElement<RespuestaConsultaLRCobrosMetalicoType> createRespuestaConsultaLRCobrosMetalico(RespuestaConsultaLRCobrosMetalicoType value) {
        return new JAXBElement<RespuestaConsultaLRCobrosMetalicoType>(_RespuestaConsultaLRCobrosMetalico_QNAME, RespuestaConsultaLRCobrosMetalicoType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RespuestaConsultaLRAgenciasViajesType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RespuestaConsultaLRAgenciasViajesType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.bizkaia.eus/ogasuna/sii/documentos/RespuestaConsultaLR.xsd", name = "RespuestaConsultaLRAgenciasViajes")
    public JAXBElement<RespuestaConsultaLRAgenciasViajesType> createRespuestaConsultaLRAgenciasViajes(RespuestaConsultaLRAgenciasViajesType value) {
        return new JAXBElement<RespuestaConsultaLRAgenciasViajesType>(_RespuestaConsultaLRAgenciasViajes_QNAME, RespuestaConsultaLRAgenciasViajesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RespuestaConsultaLROperacionesSegurosType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RespuestaConsultaLROperacionesSegurosType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.bizkaia.eus/ogasuna/sii/documentos/RespuestaConsultaLR.xsd", name = "RespuestaConsultaLROperacionesSeguros")
    public JAXBElement<RespuestaConsultaLROperacionesSegurosType> createRespuestaConsultaLROperacionesSeguros(RespuestaConsultaLROperacionesSegurosType value) {
        return new JAXBElement<RespuestaConsultaLROperacionesSegurosType>(_RespuestaConsultaLROperacionesSeguros_QNAME, RespuestaConsultaLROperacionesSegurosType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RespuestaConsultaCobrosType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RespuestaConsultaCobrosType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.bizkaia.eus/ogasuna/sii/documentos/RespuestaConsultaLR.xsd", name = "RespuestaConsultaCobros")
    public JAXBElement<RespuestaConsultaCobrosType> createRespuestaConsultaCobros(RespuestaConsultaCobrosType value) {
        return new JAXBElement<RespuestaConsultaCobrosType>(_RespuestaConsultaCobros_QNAME, RespuestaConsultaCobrosType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RespuestaConsultaInmueblesAdicionalesType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RespuestaConsultaInmueblesAdicionalesType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.bizkaia.eus/ogasuna/sii/documentos/RespuestaConsultaLR.xsd", name = "RespuestaConsultaInmueblesAdicionales")
    public JAXBElement<RespuestaConsultaInmueblesAdicionalesType> createRespuestaConsultaInmueblesAdicionales(RespuestaConsultaInmueblesAdicionalesType value) {
        return new JAXBElement<RespuestaConsultaInmueblesAdicionalesType>(_RespuestaConsultaInmueblesAdicionales_QNAME, RespuestaConsultaInmueblesAdicionalesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RespuestaConsultaPagosType }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RespuestaConsultaPagosType }{@code >}
     */
    @XmlElementDecl(namespace = "http://www.bizkaia.eus/ogasuna/sii/documentos/RespuestaConsultaLR.xsd", name = "RespuestaConsultaPagos")
    public JAXBElement<RespuestaConsultaPagosType> createRespuestaConsultaPagos(RespuestaConsultaPagosType value) {
        return new JAXBElement<RespuestaConsultaPagosType>(_RespuestaConsultaPagos_QNAME, RespuestaConsultaPagosType.class, null, value);
    }

}
