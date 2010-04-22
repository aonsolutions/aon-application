package com.esferalia.aon.payroll;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.ITransferObject;
import com.esferalia.aon.payroll.core.IEmpleado;
import com.esferalia.aon.payroll.core.INomina;
import com.esferalia.aon.payroll.core.enumeration.TipoNomina;

@Entity
@Table(name = "nomina")
public class Nomina implements ITransferObject, INomina {

	private static final long serialVersionUID = 1L;

	private Integer cdg;
	private Integer mes;
	private Integer year;
	private Integer diasNomina;
	private String tipoNomina;
	private IEmpleado empleado;

	private Double baseAccidentesTrabajo;
	private Double baseHorasExtrasEstructurales;
	private Double baseHorasExtrasNoEstrcturales;
	private Double baseContingenciasGenerales;
	private Double baseAccidentesTrabajoSinHorasExtras;

	//	private Integer orden;
//	private String nomemp;
//	private Date fecemi;
//	private String nomper;
//	private String direccion;
//	private String localidad;
//	private String descat;
//	private String profesion;
//	private Integer nummat;
//	private Date fecant;
//	private Date fecini;
//	private Date fecfin;
//	private BigDecimal totalDevengos;
//	private BigDecimal totalDeducir;
//	private BigDecimal totalLiquido;
//	private Date feccob;
//	private BigDecimal baseConcom;
//	private BigDecimal baseProext;
//	private BigDecimal baseConIt;
//	private BigDecimal baseAccIt;
//	private BigDecimal baseConMat;
//	private BigDecimal baseAccMat;
//	private BigDecimal baseConMatNo;
//	private BigDecimal baseAccMatNo;
//	private BigDecimal baseFogasa;
//	private BigDecimal baseFp;
//	private BigDecimal baseDesempleo;
//	private BigDecimal baseExceso;
//	private BigDecimal baseNocotiza;
//	private BigDecimal baseEspecie;
//	private BigDecimal baseIrpf;
//	private BigDecimal baseIrpfEspecie;
//	private BigDecimal baseIrpfNocotiza;
//	private BigDecimal baseHorascom;
//	private BigDecimal basePerdes;
//	private BigDecimal remuneracion;
//	private BigDecimal baseIt;
//	private BigDecimal total1;
//	private String codbas;
//	private BigDecimal baseAcc;
//	private BigDecimal prcCg;
//	private BigDecimal prcAcc;
//	private BigDecimal prcHex;
//	private BigDecimal prcHexno;
//	private BigDecimal importeCg;
//	private BigDecimal importeAcc;
//	private BigDecimal importeHex;
//	private BigDecimal importeHexno;
//	private BigDecimal mincg;
//	private BigDecimal maxcg;
//	private BigDecimal minacc;
//	private BigDecimal maxacc;
//	private BigDecimal cuotaEmpresa;
//	private BigDecimal importeCuotas;
//	private BigDecimal prcIrpf;
//	private BigDecimal importeIrpf;
//	private Date fecnew;
//	private Date hornew;
//	private Date fecmod;
//	private Date hormod;
//	private Integer diastrab;
//	private Integer diasefec;
//	private BigDecimal baseant;
//	private String proret;
//	private String procot;
//	private String codcon;
//	private String codpct;
//	private Date feccobreal;
//	private BigDecimal baseIrpfAnt;
//	private BigDecimal importeIrpfAnt;
//	private BigDecimal importeCuotasAnt;
//	private BigDecimal baseCgPts;
//	private BigDecimal baseAccPts;
//	private BigDecimal baseAccSinHPts;
//  private Divisa divisa;

	public Nomina() {

		diasNomina = 0;
		baseAccidentesTrabajo = 0.0;
		baseHorasExtrasEstructurales = 0.0;
		baseHorasExtrasNoEstrcturales = 0.0;
		baseContingenciasGenerales = 0.0;
		baseAccidentesTrabajoSinHorasExtras = 0.0;

//		Date now = new Date();
//		fecemi = now;
//		fecmod = now;
//		totalDevengos = new BigDecimal(0);
//		totalDeducir = new BigDecimal(0);
//		totalLiquido = new BigDecimal(0);
//		baseConcom = new BigDecimal(0);
//		baseProext = new BigDecimal(0);
//		baseConIt = new BigDecimal(0);
//		baseAccIt = new BigDecimal(0);
//		baseConMat = new BigDecimal(0);
//		baseAccMat = new BigDecimal(0);
//		baseConMatNo = new BigDecimal(0);
//		baseAccMatNo = new BigDecimal(0);
//		baseFogasa = new BigDecimal(0);
//		baseFp = new BigDecimal(0);
//		baseDesempleo = new BigDecimal(0);
//		baseExceso = new BigDecimal(0);
//		baseNocotiza = new BigDecimal(0);
//		baseEspecie = new BigDecimal(0);
//		baseIrpf = new BigDecimal(0);
//		baseIrpfEspecie = new BigDecimal(0);
//		baseIrpfNocotiza = new BigDecimal(0);
//		baseHorascom = new BigDecimal(0);
//		basePerdes = new BigDecimal(0);
//		remuneracion = new BigDecimal(0);
//		baseIt = new BigDecimal(0);
//		total1 = new BigDecimal(0);
//		baseAcc = new BigDecimal(0);
//		prcCg = new BigDecimal(0);
//		prcAcc = new BigDecimal(0);
//		prcHex = new BigDecimal(0);
//		prcHexno = new BigDecimal(0);
//		importeCg = new BigDecimal(0);
//		importeAcc = new BigDecimal(0);
//		importeHex = new BigDecimal(0);
//		importeHexno = new BigDecimal(0);
//		mincg = new BigDecimal(0);
//		maxcg = new BigDecimal(0);
//		minacc = new BigDecimal(0);
//		maxacc = new BigDecimal(0);
//		cuotaEmpresa = new BigDecimal(0);
//		importeCuotas = new BigDecimal(0);
//		prcIrpf = new BigDecimal(0);
//		importeIrpf = new BigDecimal(0);
//		diastrab = 0;
//		diasefec = 0;
//		baseant = new BigDecimal(0);
//		baseIrpfAnt = new BigDecimal(0);
//		importeIrpfAnt = new BigDecimal(0);
//		importeCuotasAnt = new BigDecimal(0);
//		baseCgPts = new BigDecimal(0);
//		baseAccPts = new BigDecimal(0);
//		baseAccSinHPts = new BigDecimal(0);

	}

	@Id
	@Column(name = "cdg", unique = true, nullable = false, length = 4)
	public Integer getCdg() {
		return this.cdg;
	}

	public void setCdg(Integer cdg) {
		this.cdg = cdg;
	}

	@Column(name = "mes", nullable = false, length = 2)
	public Integer getMes() {
		return this.mes;
	}

	public void setMes(Integer mes) {
		this.mes = mes;
	}

	@Column(name = "anio", nullable = false, length = 2)
	@Override
	public Integer getYear() {
		return this.year;
	}

	@Override
	public void setYear(Integer year) {
		this.year = year;
	}

//	@Column(name = "orden", nullable = false, length = 2)
//	public Integer getOrden() {
//		return this.orden;
//	}
//
//	public void setOrden(Integer orden) {
//		this.orden = orden;
//	}

	@Column(name = "tipo", nullable = false, length = 1)
	public String getTipoNomina() {
		return this.tipoNomina;
	}

	public void setTipoNomina(String tipoNomina) {
		this.tipoNomina = tipoNomina;
	}

//	@Column(name = "nomemp", nullable = false, length = 60)
//	public String getNomemp() {
//		return this.nomemp;
//	}
//
//	public void setNomemp(String nomemp) {
//		this.nomemp = nomemp;
//	}
//
//	@Temporal(TemporalType.DATE)
//	@Column(name = "fecemi", nullable = false, length = 10)
//	public Date getFecemi() {
//		return this.fecemi;
//	}
//
//	public void setFecemi(Date fecemi) {
//		this.fecemi = fecemi;
//	}
//
//	@Column(name = "nomper", nullable = false, length = 60)
//	public String getNomper() {
//		return this.nomper;
//	}
//
//	public void setNomper(String nomper) {
//		this.nomper = nomper;
//	}
//
//	@Column(name = "direccion", nullable = false, length = 60)
//	public String getDireccion() {
//		return this.direccion;
//	}
//
//	public void setDireccion(String direccion) {
//		this.direccion = direccion;
//	}
//
//	@Column(name = "localidad", nullable = false, length = 60)
//	public String getLocalidad() {
//		return this.localidad;
//	}
//
//	public void setLocalidad(String localidad) {
//		this.localidad = localidad;
//	}
//
//	@Column(name = "descat", length = 35)
//	public String getDescat() {
//		return this.descat;
//	}
//
//	public void setDescat(String descat) {
//		this.descat = descat;
//	}
//
//	@Column(name = "profesion", nullable = false, length = 25)
//	public String getProfesion() {
//		return this.profesion;
//	}
//
//	public void setProfesion(String profesion) {
//		this.profesion = profesion;
//	}
//
//	@Column(name = "nummat", length = 2)
//	public Integer getNummat() {
//		return this.nummat;
//	}
//
//	public void setNummat(Integer nummat) {
//		this.nummat = nummat;
//	}
//
//	@Temporal(TemporalType.DATE)
//	@Column(name = "fecant", nullable = false, length = 10)
//	public Date getFecant() {
//		return this.fecant;
//	}
//
//	public void setFecant(Date fecant) {
//		this.fecant = fecant;
//	}
//
//	@Temporal(TemporalType.DATE)
//	@Column(name = "fecini", nullable = false, length = 10)
//	public Date getFecini() {
//		return this.fecini;
//	}
//
//	public void setFecini(Date fecini) {
//		this.fecini = fecini;
//	}
//
//	@Temporal(TemporalType.DATE)
//	@Column(name = "fecfin", nullable = false, length = 10)
//	public Date getFecfin() {
//		return this.fecfin;
//	}
//
//	public void setFecfin(Date fecfin) {
//		this.fecfin = fecfin;
//	}

	@Column(name = "diasnomina", nullable = false, length = 2)
	public Integer getDiasNomina() {
		return this.diasNomina;
	}

	public void setDiasNomina(Integer diasNomina) {
		this.diasNomina = diasNomina;
	}

//	@Column(name = "total_devengos", nullable = false, scale = 2, precision = 11)
//	public BigDecimal getTotalDevengos() {
//		return this.totalDevengos;
//	}
//
//	public void setTotalDevengos(BigDecimal totalDevengos) {
//		this.totalDevengos = totalDevengos;
//	}
//
//	@Column(name = "total_deducir", nullable = false, scale = 2, precision = 11)
//	public BigDecimal getTotalDeducir() {
//		return this.totalDeducir;
//	}
//
//	public void setTotalDeducir(BigDecimal totalDeducir) {
//		this.totalDeducir = totalDeducir;
//	}
//
//	@Column(name = "total_liquido", nullable = false, scale = 2, precision = 11)
//	public BigDecimal getTotalLiquido() {
//		return this.totalLiquido;
//	}
//
//	public void setTotalLiquido(BigDecimal totalLiquido) {
//		this.totalLiquido = totalLiquido;
//	}
//
//	@Temporal(TemporalType.DATE)
//	@Column(name = "feccob", nullable = false, length = 10)
//	public Date getFeccob() {
//		return this.feccob;
//	}
//
//	public void setFeccob(Date feccob) {
//		this.feccob = feccob;
//	}
//
//	@Column(name = "base_concom", nullable = false, scale = 2, precision = 11)
//	public BigDecimal getBaseConcom() {
//		return this.baseConcom;
//	}
//
//	public void setBaseConcom(BigDecimal baseConcom) {
//		this.baseConcom = baseConcom;
//	}

	@Column(name = "base_acctra", nullable = false, scale = 2, precision = 11)
	public Double getBaseAccidentesTrabajo() {
		return this.baseAccidentesTrabajo;
	}

	public void setBaseAccidentesTrabajo(Double baseAccidentesTrabajo) {
		this.baseAccidentesTrabajo = baseAccidentesTrabajo;
	}

//	@Column(name = "base_proext", nullable = false, scale = 2, precision = 11)
//	public BigDecimal getBaseProext() {
//		return this.baseProext;
//	}
//
//	public void setBaseProext(BigDecimal baseProext) {
//		this.baseProext = baseProext;
//	}
//
//	@Column(name = "base_con_it", nullable = false, scale = 2, precision = 11)
//	public BigDecimal getBaseConIt() {
//		return this.baseConIt;
//	}
//
//	public void setBaseConIt(BigDecimal baseConIt) {
//		this.baseConIt = baseConIt;
//	}
//
//	@Column(name = "base_acc_it", nullable = false, scale = 2, precision = 11)
//	public BigDecimal getBaseAccIt() {
//		return this.baseAccIt;
//	}
//
//	public void setBaseAccIt(BigDecimal baseAccIt) {
//		this.baseAccIt = baseAccIt;
//	}
//
//	@Column(name = "base_con_mat", nullable = false, scale = 2, precision = 11)
//	public BigDecimal getBaseConMat() {
//		return this.baseConMat;
//	}
//
//	public void setBaseConMat(BigDecimal baseConMat) {
//		this.baseConMat = baseConMat;
//	}
//
//	@Column(name = "base_acc_mat", nullable = false, scale = 2, precision = 11)
//	public BigDecimal getBaseAccMat() {
//		return this.baseAccMat;
//	}
//
//	public void setBaseAccMat(BigDecimal baseAccMat) {
//		this.baseAccMat = baseAccMat;
//	}
//
//	@Column(name = "base_con_mat_no", nullable = false, scale = 2, precision = 11)
//	public BigDecimal getBaseConMatNo() {
//		return this.baseConMatNo;
//	}
//
//	public void setBaseConMatNo(BigDecimal baseConMatNo) {
//		this.baseConMatNo = baseConMatNo;
//	}
//
//	@Column(name = "base_acc_mat_no", nullable = false, scale = 2, precision = 11)
//	public BigDecimal getBaseAccMatNo() {
//		return this.baseAccMatNo;
//	}
//
//	public void setBaseAccMatNo(BigDecimal baseAccMatNo) {
//		this.baseAccMatNo = baseAccMatNo;
//	}
//
//	@Column(name = "base_fogasa", nullable = false, scale = 2, precision = 11)
//	public BigDecimal getBaseFogasa() {
//		return this.baseFogasa;
//	}
//
//	public void setBaseFogasa(BigDecimal baseFogasa) {
//		this.baseFogasa = baseFogasa;
//	}
//
//	@Column(name = "base_fp", nullable = false, scale = 2, precision = 11)
//	public BigDecimal getBaseFp() {
//		return this.baseFp;
//	}
//
//	public void setBaseFp(BigDecimal baseFp) {
//		this.baseFp = baseFp;
//	}
//
//	@Column(name = "base_desempleo", nullable = false, scale = 2, precision = 11)
//	public BigDecimal getBaseDesempleo() {
//		return this.baseDesempleo;
//	}
//
//	public void setBaseDesempleo(BigDecimal baseDesempleo) {
//		this.baseDesempleo = baseDesempleo;
//	}

	@Column(name = "base_hextras", nullable = false, scale = 2, precision = 11)
	public Double getBaseHorasExtrasEstructurales() {
		return this.baseHorasExtrasEstructurales;
	}

	public void setBaseHorasExtrasEstructurales(
			Double baseHorasExtrasEstructurales) {
		this.baseHorasExtrasEstructurales = baseHorasExtrasEstructurales;
	}

	@Column(name = "base_hextras_no", nullable = false, scale = 2, precision = 11)
	public Double getBaseHorasExtrasNoEstructurales() {
		return this.baseHorasExtrasNoEstrcturales;
	}

	public void setBaseHorasExtrasNoEstructurales(
			Double baseHorasExtrasNoEstrcturales) {
		this.baseHorasExtrasNoEstrcturales = baseHorasExtrasNoEstrcturales;
	}

//	@Column(name = "base_exceso", nullable = false, scale = 2, precision = 11)
//	public BigDecimal getBaseExceso() {
//		return this.baseExceso;
//	}
//
//	public void setBaseExceso(BigDecimal baseExceso) {
//		this.baseExceso = baseExceso;
//	}
//
//	@Column(name = "base_nocotiza", nullable = false, scale = 2, precision = 11)
//	public BigDecimal getBaseNocotiza() {
//		return this.baseNocotiza;
//	}
//
//	public void setBaseNocotiza(BigDecimal baseNocotiza) {
//		this.baseNocotiza = baseNocotiza;
//	}
//
//	@Column(name = "base_especie", nullable = false, scale = 2, precision = 11)
//	public BigDecimal getBaseEspecie() {
//		return this.baseEspecie;
//	}
//
//	public void setBaseEspecie(BigDecimal baseEspecie) {
//		this.baseEspecie = baseEspecie;
//	}
//
//	@Column(name = "base_irpf", nullable = false, scale = 2, precision = 11)
//	public BigDecimal getBaseIrpf() {
//		return this.baseIrpf;
//	}
//
//	public void setBaseIrpf(BigDecimal baseIrpf) {
//		this.baseIrpf = baseIrpf;
//	}
//
//	@Column(name = "base_irpf_especie", nullable = false, scale = 2, precision = 11)
//	public BigDecimal getBaseIrpfEspecie() {
//		return this.baseIrpfEspecie;
//	}
//
//	public void setBaseIrpfEspecie(BigDecimal baseIrpfEspecie) {
//		this.baseIrpfEspecie = baseIrpfEspecie;
//	}
//
//	@Column(name = "base_irpf_nocotiza", nullable = false, scale = 2, precision = 11)
//	public BigDecimal getBaseIrpfNocotiza() {
//		return this.baseIrpfNocotiza;
//	}
//
//	public void setBaseIrpfNocotiza(BigDecimal baseIrpfNocotiza) {
//		this.baseIrpfNocotiza = baseIrpfNocotiza;
//	}
//
//	@Column(name = "base_horascom", nullable = false, scale = 2, precision = 11)
//	public BigDecimal getBaseHorascom() {
//		return this.baseHorascom;
//	}
//
//	public void setBaseHorascom(BigDecimal baseHorascom) {
//		this.baseHorascom = baseHorascom;
//	}
//
//	@Column(name = "base_perdes", nullable = false, scale = 2, precision = 11)
//	public BigDecimal getBasePerdes() {
//		return this.basePerdes;
//	}
//
//	public void setBasePerdes(BigDecimal basePerdes) {
//		this.basePerdes = basePerdes;
//	}
//
//	@Column(name = "remuneracion", nullable = false, scale = 2, precision = 11)
//	public BigDecimal getRemuneracion() {
//		return this.remuneracion;
//	}
//
//	public void setRemuneracion(BigDecimal remuneracion) {
//		this.remuneracion = remuneracion;
//	}

//	@Column(name = "base_it", nullable = false, scale = 2, precision = 11)
//	public BigDecimal getBaseIt() {
//		return this.baseIt;
//	}
//
//	public void setBaseIt(BigDecimal baseIt) {
//		this.baseIt = baseIt;
//	}
//
//	@Column(name = "total_1", nullable = false, scale = 2, precision = 11)
//	public BigDecimal getTotal1() {
//		return this.total1;
//	}
//
//	public void setTotal1(BigDecimal total1) {
//		this.total1 = total1;
//	}
//
//	@Column(name = "codbas", nullable = false, length = 2)
//	public String getCodbas() {
//		return this.codbas;
//	}
//
//	public void setCodbas(String codbas) {
//		this.codbas = codbas;
//	}

	@Column(name = "base_cg", nullable = false, scale = 2, precision = 11)
	public Double getBaseContingenciasGenerales() {
		return this.baseContingenciasGenerales;
	}

	public void setBaseContingenciasGenerales(Double baseContingenciasGenerales) {
		this.baseContingenciasGenerales = baseContingenciasGenerales;
	}

//	@Column(name = "base_acc", nullable = false, scale = 2, precision = 11)
//	public BigDecimal getBaseAcc() {
//		return this.baseAcc;
//	}
//
//	public void setBaseAcc(BigDecimal baseAcc) {
//		this.baseAcc = baseAcc;
//	}
//
//	@Column(name = "prc_cg", nullable = false, scale = 2, precision = 5)
//	public BigDecimal getPrcCg() {
//		return this.prcCg;
//	}
//
//	public void setPrcCg(BigDecimal prcCg) {
//		this.prcCg = prcCg;
//	}

//	@Column(name = "prc_acc", nullable = false, scale = 2, precision = 5)
//	public BigDecimal getPrcAcc() {
//		return this.prcAcc;
//	}
//
//	public void setPrcAcc(BigDecimal prcAcc) {
//		this.prcAcc = prcAcc;
//	}
//
//	@Column(name = "prc_hex", nullable = false, scale = 2, precision = 5)
//	public BigDecimal getPrcHex() {
//		return this.prcHex;
//	}
//
//	public void setPrcHex(BigDecimal prcHex) {
//		this.prcHex = prcHex;
//	}
//
//	@Column(name = "prc_hexno", nullable = false, scale = 2, precision = 5)
//	public BigDecimal getPrcHexno() {
//		return this.prcHexno;
//	}
//
//	public void setPrcHexno(BigDecimal prcHexno) {
//		this.prcHexno = prcHexno;
//	}
//
//	@Column(name = "importe_cg", nullable = false, scale = 2, precision = 11)
//	public BigDecimal getImporteCg() {
//		return this.importeCg;
//	}
//
//	public void setImporteCg(BigDecimal importeCg) {
//		this.importeCg = importeCg;
//	}
//
//	@Column(name = "importe_acc", nullable = false, scale = 2, precision = 11)
//	public BigDecimal getImporteAcc() {
//		return this.importeAcc;
//	}
//
//	public void setImporteAcc(BigDecimal importeAcc) {
//		this.importeAcc = importeAcc;
//	}
//
//	@Column(name = "importe_hex", nullable = false, scale = 2, precision = 11)
//	public BigDecimal getImporteHex() {
//		return this.importeHex;
//	}
//
//	public void setImporteHex(BigDecimal importeHex) {
//		this.importeHex = importeHex;
//	}
//
//	@Column(name = "importe_hexno", nullable = false, scale = 2, precision = 11)
//	public BigDecimal getImporteHexno() {
//		return this.importeHexno;
//	}
//
//	public void setImporteHexno(BigDecimal importeHexno) {
//		this.importeHexno = importeHexno;
//	}
//
//	@Column(name = "mincg", nullable = false, scale = 2, precision = 11)
//	public BigDecimal getMincg() {
//		return this.mincg;
//	}
//
//	public void setMincg(BigDecimal mincg) {
//		this.mincg = mincg;
//	}
//
//	@Column(name = "maxcg", nullable = false, scale = 2, precision = 11)
//	public BigDecimal getMaxcg() {
//		return this.maxcg;
//	}
//
//	public void setMaxcg(BigDecimal maxcg) {
//		this.maxcg = maxcg;
//	}
//
//	@Column(name = "minacc", nullable = false, scale = 2, precision = 11)
//	public BigDecimal getMinacc() {
//		return this.minacc;
//	}
//
//	public void setMinacc(BigDecimal minacc) {
//		this.minacc = minacc;
//	}
//
//	@Column(name = "maxacc", nullable = false, scale = 2, precision = 11)
//	public BigDecimal getMaxacc() {
//		return this.maxacc;
//	}
//
//	public void setMaxacc(BigDecimal maxacc) {
//		this.maxacc = maxacc;
//	}
//
//	@Column(name = "cuota_empresa", scale = 2, precision = 11)
//	public BigDecimal getCuotaEmpresa() {
//		return this.cuotaEmpresa;
//	}
//
//	public void setCuotaEmpresa(BigDecimal cuotaEmpresa) {
//		this.cuotaEmpresa = cuotaEmpresa;
//	}

	@Column(name = "base_acc_sin_hex", scale = 2, precision = 11)
	public Double getBaseAccidentesTrabajoSinHorasExtras() {
		return this.baseAccidentesTrabajoSinHorasExtras;
	}
	public void setBaseAccidentesTrabajoSinHorasExtras(Double baseAccidentesTrabajoSinHorasExtras) {
		this.baseAccidentesTrabajoSinHorasExtras = baseAccidentesTrabajoSinHorasExtras;
	}

//	@Column(name = "importe_cuotas", nullable = false, scale = 2, precision = 11)
//	public BigDecimal getImporteCuotas() {
//		return this.importeCuotas;
//	}
//
//	public void setImporteCuotas(BigDecimal importeCuotas) {
//		this.importeCuotas = importeCuotas;
//	}
//
//	@Column(name = "prc_irpf", nullable = false, scale = 2, precision = 5)
//	public BigDecimal getPrcIrpf() {
//		return this.prcIrpf;
//	}
//
//	public void setPrcIrpf(BigDecimal prcIrpf) {
//		this.prcIrpf = prcIrpf;
//	}
//
//	@Column(name = "importe_irpf", nullable = false, scale = 2, precision = 11)
//	public BigDecimal getImporteIrpf() {
//		return this.importeIrpf;
//	}
//
//	public void setImporteIrpf(BigDecimal importeIrpf) {
//		this.importeIrpf = importeIrpf;
//	}
//
//	@Temporal(TemporalType.DATE)
//	@Column(name = "fecnew", length = 10)
//	public Date getFecnew() {
//		return this.fecnew;
//	}
//
//	public void setFecnew(Date fecnew) {
//		this.fecnew = fecnew;
//	}
//
//	@Temporal(TemporalType.TIME)
//	@Column(name = "hornew", length = 8)
//	public Date getHornew() {
//		return this.hornew;
//	}
//
//	public void setHornew(Date hornew) {
//		this.hornew = hornew;
//	}
//
//	@Temporal(TemporalType.DATE)
//	@Column(name = "fecmod", length = 10)
//	public Date getFecmod() {
//		return this.fecmod;
//	}
//
//	public void setFecmod(Date fecmod) {
//		this.fecmod = fecmod;
//	}
//
//	@Temporal(TemporalType.TIME)
//	@Column(name = "hormod", length = 8)
//	public Date getHormod() {
//		return this.hormod;
//	}
//
//	public void setHormod(Date hormod) {
//		this.hormod = hormod;
//	}
//
//	@Column(name = "diastrab", length = 2)
//	public Integer getDiastrab() {
//		return this.diastrab;
//	}
//
//	public void setDiastrab(Integer diastrab) {
//		this.diastrab = diastrab;
//	}
//
//	@Column(name = "diasefec", length = 2)
//	public Integer getDiasefec() {
//		return this.diasefec;
//	}
//
//	public void setDiasefec(Integer diasefec) {
//		this.diasefec = diasefec;
//	}
//
//	@Column(name = "baseant", scale = 2, precision = 8)
//	public BigDecimal getBaseant() {
//		return this.baseant;
//	}
//
//	public void setBaseant(BigDecimal baseant) {
//		this.baseant = baseant;
//	}
//
//	@Column(name = "proret", length = 1)
//	public String getProret() {
//		return this.proret;
//	}
//
//	public void setProret(String proret) {
//		this.proret = proret;
//	}
//
//	@Column(name = "procot", length = 1)
//	public String getProcot() {
//		return this.procot;
//	}
//
//	public void setProcot(String procot) {
//		this.procot = procot;
//	}
//
//	@Column(name = "codcon", length = 2)
//	public String getCodcon() {
//		return this.codcon;
//	}
//
//	public void setCodcon(String codcon) {
//		this.codcon = codcon;
//	}
//
//	@Column(name = "codpct", length = 8)
//	public String getCodpct() {
//		return this.codpct;
//	}
//
//	public void setCodpct(String codpct) {
//		this.codpct = codpct;
//	}
//
//	@Temporal(TemporalType.DATE)
//	@Column(name = "feccobreal", length = 10)
//	public Date getFeccobreal() {
//		return this.feccobreal;
//	}
//
//	public void setFeccobreal(Date feccobreal) {
//		this.feccobreal = feccobreal;
//	}
//
//	@Column(name = "base_irpf_ant", scale = 2, precision = 11)
//	public BigDecimal getBaseIrpfAnt() {
//		return this.baseIrpfAnt;
//	}
//
//	public void setBaseIrpfAnt(BigDecimal baseIrpfAnt) {
//		this.baseIrpfAnt = baseIrpfAnt;
//	}
//
//	@Column(name = "importe_irpf_ant", scale = 2, precision = 11)
//	public BigDecimal getImporteIrpfAnt() {
//		return this.importeIrpfAnt;
//	}
//
//	public void setImporteIrpfAnt(BigDecimal importeIrpfAnt) {
//		this.importeIrpfAnt = importeIrpfAnt;
//	}
//
//	@Column(name = "importe_cuotas_ant", scale = 2, precision = 11)
//	public BigDecimal getImporteCuotasAnt() {
//		return this.importeCuotasAnt;
//	}
//
//	public void setImporteCuotasAnt(BigDecimal importeCuotasAnt) {
//		this.importeCuotasAnt = importeCuotasAnt;
//	}
//
//	@Column(name = "base_cg_pts", scale = 2, precision = 11)
//	public BigDecimal getBaseCgPts() {
//		return this.baseCgPts;
//	}
//
//	public void setBaseCgPts(BigDecimal baseCgPts) {
//		this.baseCgPts = baseCgPts;
//	}
//
//	@Column(name = "base_acc_pts", scale = 2, precision = 11)
//	public BigDecimal getBaseAccPts() {
//		return this.baseAccPts;
//	}
//
//	public void setBaseAccPts(BigDecimal baseAccPts) {
//		this.baseAccPts = baseAccPts;
//	}
//
//	@Column(name = "base_acc_sin_h_pts", scale = 2, precision = 11)
//	public BigDecimal getBaseAccSinHPts() {
//		return this.baseAccSinHPts;
//	}
//
//	public void setBaseAccSinHPts(BigDecimal baseAccSinHPts) {
//		this.baseAccSinHPts = baseAccSinHPts;
//	}
//
//	@ManyToOne(fetch = FetchType.EAGER)
//	@JoinColumn(name = "divisa")
//	public Divisa getDivisa() {
//		return this.divisa;
//	}
//
//	public void setDivisa(Divisa divisa) {
//		this.divisa = divisa;
//	}

	@ManyToOne(targetEntity = Empleado.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "numero", nullable = false)
	public IEmpleado getEmpleado() {
		return this.empleado;
	}

	public void setEmpleado(IEmpleado empleado) {
		this.empleado = empleado;
	}

	@Override
	@Transient
	public TipoNomina getTipo() {
		return ("1".equals(getTipoNomina()))?TipoNomina.ATRASO:TipoNomina.NORMAL;
	}

	@Override
	public void setTipo(TipoNomina tipoNomina) {
		setTipoNomina(tipoNomina==TipoNomina.ATRASO?"1":"0");
	}
}
