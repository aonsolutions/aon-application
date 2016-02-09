package net.aonsolutions.tgss.creta.jaxb;

import java.time.Month;
import java.util.ArrayList;
import java.util.List;



public abstract class AbstractLiquidacionBuilder<B extends AbstractLiquidacionBuilder<B,L,C,P>,L extends Liquidacion<?,C,P,?,?>, C extends CtaCot, P extends Periodo> {


	private String ccc;
	private String cccConcertado;
	private String tipo;
	private int anhoDesde;
	private Month mesDesde;
	private int anhoHasta;
	private Month mesHasta;
	private int anhoControl;
	private Month mesControl;
	
	private List<L> liquidaciones;
	
	public AbstractLiquidacionBuilder() {
		liquidaciones = new ArrayList<L>();
	}
	
	abstract  protected C newCtaCot();
	abstract  protected P newPeriodo();
	abstract  protected L newLiquidacion();

	public L createLiquidacion() {
		
		L liquidacion = newLiquidacion();
		
		liquidacion.setTipo(tipo);

		C ctaCot = newCtaCot();
		ctaCot.setRegimen(ccc.substring(0,4));
		ctaCot.setProvincia(ccc.substring(4,6));
		ctaCot.setNumero(ccc.substring(6));
		liquidacion.setCcc(ctaCot);
		
		P periodoDesde = newPeriodo();
		periodoDesde.setAnho(String.format("%d",anhoDesde));
		periodoDesde.setMes(String.format("%02d",mesDesde.getValue()));
		liquidacion.setPeriodoDesde(periodoDesde);
		
		P periodoHasta = newPeriodo();
		periodoHasta.setAnho(String.format("%d",anhoHasta));
		periodoHasta.setMes(String.format("%02d",mesHasta.getValue()));
		liquidacion.setPeriodoHasta(periodoHasta);


		if ( "L03,C03".indexOf(tipo) >= 0 ) {
			P periodoControl = newPeriodo();
			periodoControl.setAnho(String.format("%d",anhoControl));
			periodoControl.setMes(String.format("%02d",mesControl.getValue()));
			liquidacion.setPeriodoHasta(periodoControl);
		}
		
		if ( "C00, C02, C03, C13, C90 y C91".indexOf(tipo) >= 0 ) {
			C ctaCotConertado = newCtaCot();
			ctaCotConertado.setRegimen(cccConcertado.substring(0,4));
			ctaCotConertado.setProvincia(cccConcertado.substring(4,6));
			ctaCotConertado.setNumero(cccConcertado.substring(6));
			liquidacion.setCcc(ctaCotConertado);
		}
		
		return liquidacion;
	}

	public List<L> getLiquidaciones() {
		return liquidaciones;
	}
	
	
	
	public B addLiquidacion() {
		L liquidacion = createLiquidacion();
		liquidaciones.add(liquidacion);
		return (B) this;
	}


	public B setTipo(String tipo) {
		this.tipo = tipo;
		return (B) this;
	}

	public B setCCC(String ccc) {
		this.ccc = ccc;
		return (B) this;
	}
	
	public B setMesDesde(Month mesDesde) {
		this.mesDesde = mesDesde;
		return (B) this;
	}
	
	public B setAnhoDesde(int anhoDesde) {
		this.anhoDesde = anhoDesde;
		return (B) this;
	}
	
	public B setMesHasta(Month mesHasta) {
		this.mesHasta = mesHasta;
		return (B) this;
	}

	public B setAnhoHasta(int anhoHasta) {
		this.anhoHasta = anhoHasta;
		return (B) this;
	}
	
	public B setMesControl(Month mesControl) {
		this.mesControl = mesControl;
		return (B) this;
	}

	public B setAnhoControl(int anhoControl) {
		this.anhoControl = anhoControl;
		return (B) this;
	}

	public B setCCCConcertado(String cccConcertado) {
		this.cccConcertado = cccConcertado;
		return (B) this;
	}

	

}
