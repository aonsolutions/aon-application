package com.esferalia.aon.dsi.nominas;

import static com.esferalia.aon.dsi.nominas.Traspaso.getGeozone;
import static com.esferalia.aon.dsi.nominas.Traspaso.getMunicipalityCode;
import static com.esferalia.aon.dsi.nominas.Traspaso.getParentDomain;
import static com.esferalia.aon.dsi.nominas.TraspasoCalendarios.buscarCalendario;
import static com.esferalia.aon.jooq.tables.AgreementData.AGREEMENT_DATA;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.AgreementLevelData.AGREEMENT_LEVEL_DATA;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.ContractDeduction.CONTRACT_DEDUCTION;
import static com.esferalia.aon.jooq.tables.ContractInfo.CONTRACT_INFO;
import static com.esferalia.aon.jooq.tables.ContractPayment.CONTRACT_PAYMENT;
import static com.esferalia.aon.jooq.tables.IrpfData.IRPF_DATA;
import static com.esferalia.aon.jooq.tables.IrpfDataAscendants.IRPF_DATA_ASCENDANTS;
import static com.esferalia.aon.jooq.tables.IrpfDataDescendients.IRPF_DATA_DESCENDIENTS;
import static com.esferalia.aon.jooq.tables.PayMethod.PAY_METHOD;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Rbank.RBANK;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rmedia.RMEDIA;
import static com.esferalia.aon.jooq.tables.Rpaymethod.RPAYMETHOD;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import org.jooq.DSLContext;

import com.esferalia.aon.dsi.nominas.dao.TrabajadorDAO;
import com.esferalia.aon.dsi.nominas.model.Ascendiente;
import com.esferalia.aon.dsi.nominas.model.Calendario;
import com.esferalia.aon.dsi.nominas.model.Categoria;
import com.esferalia.aon.dsi.nominas.model.Centro;
import com.esferalia.aon.dsi.nominas.model.Complemento;
import com.esferalia.aon.dsi.nominas.model.Concepto;
import com.esferalia.aon.dsi.nominas.model.Descendiente;
import com.esferalia.aon.dsi.nominas.model.Empresa;
import com.esferalia.aon.dsi.nominas.model.Paga;
import com.esferalia.aon.dsi.nominas.model.Trabajador;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class TraspasoTrabajadores {
	
	private static DSLContext ctx;
	
	public static void execute(Connection dsiConn, AONContext aonContext, Empresa empresa, int domain) throws SQLException {
		
		ctx = aonContext.getDslContext();
		
		// Para cada empresa se leen sus trabajadores (sin fecha de baja o fecha de baja >= 01/01/2021)
		LinkedList<Trabajador> trabajadores = TrabajadorDAO.select(dsiConn, empresa.getSscod(), empresa.getSsnum());
					
		int totalTra = 0;
		if (trabajadores.size() == 0) {
			Traspaso.info("No hay trabajadores");
		}				
		else {
			String nifTrabajador = null;
			String categoriaOmega = null;
			Date fechaBaja = null;
			int registry = 0;
			int contract = 0;
			for (Trabajador trabajador : trabajadores) {
				totalTra++;
				Traspaso.info(trabajador.toString());
				
				// DATOS GENERALES ----------------------------------------------------------------
				
				// Se añade registry cuando cambia el nif del trabajador
				if (nifTrabajador == null || !nifTrabajador.equals(trabajador.getDni())) {
					registry = addRegistry(domain, trabajador);
					nifTrabajador = trabajador.getDni();
					categoriaOmega = null;
					fechaBaja = null;
				} 
				
				// Añadir o actualizar datos de la persona (nombre, direccion, teléfono, etc.)
				addPerson(domain, registry, trabajador);
				
				// Se añade contract cuando cambia la categoría del trabajador, sino se crea un solo contract con los distintos
				// valores de cada uno de los campos, grabando en contract_data, con startDate=fecha_alta y endDate=fecha_baja
				// Tambien se crea un nuevo contrato, cuando no hay continuidad entre los distintos movimientos del trabajador
				if (categoriaOmega == null || !categoriaOmega.equals(AonStringUtils.trimToEmpty(trabajador.getCateg())) || 
						(fechaBaja != null && !trabajador.getFalta().equals(AonDateUtils.addDays(fechaBaja, 1)))) {
					contract = addContract(domain, registry, empresa, trabajador);
					categoriaOmega = AonStringUtils.trimToEmpty(trabajador.getCateg());
				} else {
					// Si no cambia categoria, simplemente se actualizan algunos datos de contract, para que
					// el registro en AON, se quede con los datos de la última ficha
					updateContract(domain, contract, empresa, trabajador);
				}
				
				// Se guarda la fecha de baja del trabajador para comprobar la siguiente iteracion
				fechaBaja = trabajador.getFbaja();
				
				// Añadir contract_data
				addContractData(domain, contract, empresa, trabajador);
				
				// Añadir calendario (festivos)
				addCalendar(domain, contract, empresa, trabajador);
				
				// Añadir irpf_data
				addIrpfData(domain, contract, trabajador);
				
				// Obtengo los datos de Omega de la categoria del trabajador, necesarios para traspasar
				// los conceptos, pagas extras y antiguedad
				Categoria categoria = null;
				if (AonStringUtils.isNotBlank(trabajador.getConven()) && AonStringUtils.isNotBlank(trabajador.getCateg())) {
					categoria = TraspasoConvenios.buscarCategoria(trabajador.getConven(), trabajador.getCateg());
				}
				
				// CONCEPTOS SALARIALES Y ANTIGUEDAD ----------------------------------------------
				
				// Si no hay concepto con clave 02 (antiguedad) y hay tabla de antiguedad, entonces
				// se añade un concepto con clave 02, para que se añada la antiguedad a AON en el trabajador,
				// si es necesario.
				if (!existeConcepto(trabajador, "02") && !trabajador.getAonFormulaAntiguedad().isEmpty()) {
					trabajador.getConceptos().add( new Concepto()
													.setClave("02")
								                	.setNombre("ANTIGÜEDAD")
								                	.setSs("S")
								                	.setIrpf("S")
								                	.setPag("")
								                	.setEnf("")
								                	.setAcc("")
								                	.setTipo("G")
								                	.setClaveCRA("0001")
								                	.setImporte(0)
								                	.setCobro(""));
				}
				
				ArrayList<String> listaConceptosPagas = new ArrayList<String>();
				ArrayList<String> listaConceptosEnfermedad = new ArrayList<String>();
				ArrayList<String> listaConceptosAccidente = new ArrayList<String>();
				
				for (Concepto conceptoTra : trabajador.getConceptos()) {
					
					// Conceptos que forman parte de las pagas extras por días (por si hay que añadir alguna paga extra en el trabajador)
					if ("S".equals(conceptoTra.getPag())) {
						if (!listaConceptosPagas.contains(conceptoTra.getAonCode()))
							listaConceptosPagas.add(conceptoTra.getAonCode());					
					}
					
					// Conceptos que forman parte de los complementos por enfermedad
					if ("S".equals(conceptoTra.getEnf())) {
						if (!listaConceptosEnfermedad.contains(conceptoTra.getAonCode()))
							listaConceptosEnfermedad.add(conceptoTra.getAonCode());
					}
					
					// Conceptos que forman parte de los complementos por accidente
					if ("S".equals(conceptoTra.getAcc())) {
						if (!listaConceptosAccidente.contains(conceptoTra.getAonCode()))
							listaConceptosAccidente.add(conceptoTra.getAonCode());
					}
					
					if (conceptoTra.esDescuento()) {
						// Conceptos de descuentos, siempre se añaden al trabajador, pues 
						// en el convenio de AON no se pueden poner
						addContractDeduction(domain, contract, trabajador, conceptoTra);
					} else if ("02".equals(conceptoTra.getClave()) && !trabajador.getAonFormulaAntiguedad().isEmpty()) {
						// Concepto de antiguedad, que lleva tabla asociada, hay que comprobar si la tabla
						// es igual o distinta que la de la categoría (si hay categoria indicada), 
						// si es distinta, se añade en el trabajador, si son iguales, no se hace nada
						if (categoria == null || !AonStringUtils.equals(trabajador.getAonFormulaAntiguedad(),categoria.getAonFormulaAntiguedad())) {
							addContractPayment(domain, contract, trabajador, conceptoTra);
						
							// Añadir variable, si la tabla de antiguedad lleva solo una linea, si no es 
							// así, la formula irá en la expresión del concepto												
							double importe = 0;						
							if (trabajador.getAonFormulaAntiguedad().contains("IMPORTE_ANTIGUEDAD")) {
								// Tabla de una sola línea
								if ("P".equals(trabajador.getAntiguedades().get(0).getTipo()))
									importe = trabajador.getAntiguedades().get(0).getImporte()/100;
								else 
									// Importe mensual o diario, tener en cuenta coeficiente de parcialidad en contratos a tiempo parcial
									importe = AonMathUtils.round(trabajador.getAntiguedades().get(0).getImporte() / trabajador.getCoeficienteParcialidad(), 2);
								
								addContractData(domain, contract, "IMPORTE_ANTIGUEDAD", importe, trabajador.getFalta(), trabajador.getFbaja());
							}
						} else if (categoria != null) {
							// Formulas de la antiguedad y del trabajador, son iguales, hay que comprobar 
							// si es una tabla de antiguedad de una sola linea, que lleva variable, para ver 
							// si el importe o cobro es distinto en el trabajador y en la categoria
							if (trabajador.getAonFormulaAntiguedad().contains("IMPORTE_ANTIGUEDAD")) {
								double importeCat = 0;
								double importeTra = 0;

								// Importe mensual o diario, tener en cuenta coeficiente de parcialidad en contratos a tiempo parcial
								if ("P".equals(categoria.getAntiguedades().get(0).getTipo()))
									importeCat = categoria.getAntiguedades().get(0).getImporte()/100;
								else
									importeCat = AonMathUtils.round(categoria.getAntiguedades().get(0).getImporte() * trabajador.getCoeficienteParcialidad(), 4);
								
								if ("P".equals(trabajador.getAntiguedades().get(0).getTipo()))
									importeTra = trabajador.getAntiguedades().get(0).getImporte()/100;
								else 									
									importeTra = AonMathUtils.round(trabajador.getAntiguedades().get(0).getImporte(), 4);
								
								// Si el importe es distinto, se añade variable al trabajador
								if (importeTra != importeCat) {
									addContractData(domain, contract, categoria.getAonSeniorityVariable(), importeTra, trabajador.getFalta(), trabajador.getFbaja());
								}																	
							}
							 
						}
					} else if ("02".equals(conceptoTra.getClave()) && categoria != null && !categoria.getAonFormulaAntiguedad().isEmpty()) {
						// Concepto de antiguedad, sin tabla asociada, pero la categoria si que lleva tabla asociada
						// en la antiguedad, así que no se hace nada, para que se aplique la de la categoria
					} else {
						// Resto de conceptos
						// Comprobar si el concepto existe en la categoría (según su clave)
						Concepto conceptoCat = buscarConcepto(categoria, conceptoTra.getClave());
						
						// Si el concepto no existe, o no es igual que el de la categoria (importe o cobro)
						// se crea directamente en el trabajador
						String variable = null;
						if (conceptoCat == null) {
							// Si el concepto no existe en la categoria, se añade el concepto y la variable en el trabajador
							addContractPayment(domain, contract, trabajador, conceptoTra);
							variable = conceptoTra.getAonVariable();
						} else {
							// El concepto existe en la categoria, se comprueba si es el mismo (importe y cobro) que en el trabajador
							// tener en cuenta el coeficiente de parcialidad del trabajador, para comparar los importes en los conceptos de cobro MTDVS
							double importeCat = "MTDVS".contains(conceptoCat.getCobro()) ? AonMathUtils.round(conceptoCat.getImporte() * trabajador.getCoeficienteParcialidad(), 4) : conceptoCat.getImporte();
							double importeTra = "MTDVS".contains(conceptoTra.getCobro()) ? AonMathUtils.round(conceptoTra.getImporte(), 4) : conceptoTra.getImporte();
							
							// Si el cobro es distinto, se añade el concepto al trabajador
							if (!AonStringUtils.equals(conceptoTra.getCobro(), conceptoCat.getCobro())) {
								addContractPayment(domain, contract, trabajador, conceptoTra);
								variable = conceptoTra.getAonVariable();
							} else if (importeCat != importeTra) {
								// Si solo el importe es distinto, se añade solo la variable
								variable = conceptoTra.getAonVariable();
							}							
						}
							
						// Añadir variable
						if (variable != null) {							
							// Tener en cuenta coeficiente de parcialidad en cobros M, T, D, V o S, hay que grabar el 
							// concepto como si fuese para jornada completa
							double importe = conceptoTra.getImporte();
							if ("MTDVS".contains(conceptoTra.getCobro())) {
								importe = AonMathUtils.round(importe / trabajador.getCoeficienteParcialidad(), 2);
							} 
							if (AonStringUtils.isNotBlank(conceptoTra.getCobro()) || "75".equals(conceptoTra.getClave()))
								addContractData(domain, contract, conceptoTra.getAonVariable(), importe, trabajador.getFalta(), trabajador.getFbaja());
						}
													
					}					
				}
				
				// PAGAS EXTRAS -------------------------------------------------------------------
				
				// Conceptos para las pagas extras por días
				String conceptosPagas = "";
				for (int i = 0; i < listaConceptosPagas.size(); i++) {
					conceptosPagas = conceptosPagas + (i > 0 ? "+" : "") + listaConceptosPagas.get(i);
				}
				
				for (Paga pagaTra : trabajador.getPagas()) {
					
					// Buscar la paga extra en la categoría (por el mes)
					Paga pagaCat = buscarPaga(categoria, pagaTra.getMes());
					
					if (pagaCat == null) {
						// Si la paga no existe en la categoría, se añade el concepto al trabajador con la paga
						addContractPayment(domain, contract, trabajador, pagaTra, conceptosPagas);
						// Añadir variable
						if (AonStringUtils.isNotBlank(pagaTra.getAonVariable(false)))
							addContractData(domain, contract, pagaTra.getAonVariable(false), AonMathUtils.round(pagaTra.getImporte() / trabajador.getCoeficienteParcialidad(), 2), trabajador.getFalta(), trabajador.getFbaja());
					} else {
						// Si existe, se comprueba si es distinta (importe y tipo), para ver si hay que añadir concepto y/o variable al trabajador
						// Tener en cuenta el coeficiente de parcialidad en las pagas por importe, para comparar los importes entre categoria y trabajador
						String tipoPagaCat = "P".equals(pagaCat.getTipo()) ? "P" : "";
						String tipoPagaTra = "P".equals(pagaTra.getTipo()) ? "P" : "";
						double importeCat = pagaCat.esPorDias() ? pagaCat.getImporte() : AonMathUtils.round(pagaCat.getImporte() * trabajador.getCoeficienteParcialidad(), 2);
						double importeTra = pagaTra.esPorDias() ? pagaTra.getImporte() : AonMathUtils.round(pagaTra.getImporte(), 2);
						
						if (importeTra != importeCat || !tipoPagaTra.equals(tipoPagaCat)) {
							// Si la paga es por días, o el tipo es distinto, siempre se añade al concepto
							if (pagaTra.esPorDias() || pagaCat.esPorDias() || !tipoPagaTra.equals(tipoPagaCat))
								addContractPayment(domain, contract, trabajador, pagaTra, conceptosPagas);
							// Añadir variable
							if (AonStringUtils.isNotBlank(pagaTra.getAonVariable(false)))
								addContractData(domain, contract, pagaTra.getAonVariable(false), AonMathUtils.round(pagaTra.getImporte() / trabajador.getCoeficienteParcialidad(), 2), trabajador.getFalta(), trabajador.getFbaja());								
						}												
					}
					
				}
				
				// COMPLEMENTOS E/A ---------------------------------------------------------------
				
				// Conceptos para los complementos por enfermedad
				String conceptosEnf = "";
				for (int i = 0; i < listaConceptosEnfermedad.size(); i++) {
					conceptosEnf = conceptosEnf + (i > 0 ? "+" : "") + listaConceptosEnfermedad.get(i);
				}
				
				// Conceptos para los complementos por accidente
				String conceptosAcc = "";
				for (int i = 0; i < listaConceptosAccidente.size(); i++) {
					conceptosAcc = conceptosAcc + (i > 0 ? "+" : "") + listaConceptosAccidente.get(i);
				}

				// Solo si el trabajador no tiene indicado categoria, se traspasan los complementos E/A de la empresa
				if (categoria == null) {
					LinkedList<Complemento> complementos = Traspaso.comprobarComplementosEnfAcc(empresa.getComplementos(), empresa.getTipoComple());
					for (Complemento complemento : complementos) {
						if (AonStringUtils.isNotBlank(complemento.getTipo()) && AonStringUtils.isNotBlank(complemento.getCalculo())) {
							if (complemento.getTipo().equals("E") || complemento.getTipo().equals("A"))
								addContractPayment(domain, contract, trabajador, complemento, conceptosEnf, conceptosAcc);  
						}				
					}
				}
				
			}
			Traspaso.info("TOTAL TRABAJADORES = "+totalTra);
		}

		
	}
	
	private static int addRegistry(int domain, Trabajador trabajador) {

		// Añadir registry
		return ctx.insertInto(REGISTRY)
					.set(REGISTRY.DOMAIN, domain)
					.set(REGISTRY.DOCUMENT, trabajador.getDni())
					.set(REGISTRY.DOCUMENT_TYPE, getDocumentType(trabajador.getIdentif()))
					.set(REGISTRY.DOCUMENT_COUNTRY, getCountry(trabajador.getPaisemi()))						
					.set(REGISTRY.NAME, trabajador.getNombreCompleto())
					.set(REGISTRY.NATIONALITY, getCountry(trabajador.getNaciona()))
					.returning(REGISTRY.ID)
					.fetchOne()
					.getId();
		
	}
	
	private static void addPerson(int domain, int registry, Trabajador trabajador) {

		// Añadir o actualizar direccion
		Integer address = ctx.selectFrom(RADDRESS)
							.where(RADDRESS.REGISTRY.eq(registry))
							.fetchAny(RADDRESS.ID);
		
		String address2 = "";
		if (AonStringUtils.isNotBlank(trabajador.getEsc())) 
			address2 = address2 + "Esc. " + trabajador.getEsc();
		if (AonStringUtils.isNotBlank(trabajador.getPiso()))
			address2 = address2 + " Piso " + trabajador.getPiso();
		if (AonStringUtils.isNotBlank(trabajador.getPuerta()))
			address2 = address2 + " Puerta " + trabajador.getPuerta();
		address2 = address2.trim();
		
		if (address == null) {
			address = ctx.insertInto(RADDRESS)
						.set(RADDRESS.DOMAIN, domain)
						.set(RADDRESS.REGISTRY, registry)
						.set(RADDRESS.TYPE, (byte) 0)  
						.set(RADDRESS.STREET_TYPE, trabajador.getSg())
						.set(RADDRESS.ADDRESS, trabajador.getDirecci())
						.set(RADDRESS.NUMBER, trabajador.getNumero())
						.set(RADDRESS.ADDRESS2, address2)
						.set(RADDRESS.ZIP, trabajador.getCp())
						.set(RADDRESS.CITY, trabajador.getPoblaci())
						.set(RADDRESS.GEOZONE, getGeozone(ctx, trabajador.getCp(), trabajador.getProvin()))
						.set(RADDRESS.MUNICIPALITY_CODE, getMunicipalityCode(trabajador.getPoblaci(), trabajador.getCp()))
						.returning(RADDRESS.ID)
						.fetchOne()
						.getId();	
		} else {
			ctx.update(RADDRESS)
				.set(RADDRESS.STREET_TYPE, trabajador.getSg())
				.set(RADDRESS.ADDRESS, trabajador.getDirecci())
				.set(RADDRESS.NUMBER, trabajador.getNumero())
				.set(RADDRESS.ADDRESS2, address2)
				.set(RADDRESS.ZIP, trabajador.getCp())
				.set(RADDRESS.CITY, trabajador.getPoblaci())
				.set(RADDRESS.GEOZONE, getGeozone(ctx, trabajador.getCp(), trabajador.getProvin()))
				.set(RADDRESS.MUNICIPALITY_CODE, getMunicipalityCode(trabajador.getPoblaci(), trabajador.getCp()))
				.where(RADDRESS.REGISTRY.eq(registry))
				.execute();
		}
		
		// Añadir o actualizar teléfono
		Integer id = ctx.selectFrom(RMEDIA)
				.where(RMEDIA.REGISTRY.eq(registry))
				.fetchAny(RMEDIA.ID);
		
		if (id == null) {
			if (AonStringUtils.isNotBlank(trabajador.getTelef()))
				ctx.insertInto(RMEDIA)
					.set(RMEDIA.DOMAIN, domain)
					.set(RMEDIA.REGISTRY, registry)			
					.set(RMEDIA.MEDIA, trabajador.getTelef().startsWith("6") ? (byte) 2 : (byte) 1)
					.set(RMEDIA.VALUE, trabajador.getTelef())
					.set(RMEDIA.RADDRESS, address)
					.execute();			
		} else {
			if (AonStringUtils.isNotBlank(trabajador.getTelef()))
				ctx.update(RMEDIA)
					.set(RMEDIA.VALUE, trabajador.getTelef())
					.set(RMEDIA.MEDIA, trabajador.getTelef().startsWith("6") ? (byte) 2 : (byte) 1)
					.where(RMEDIA.REGISTRY.eq(registry))
					.execute();
			else 
				ctx.delete(RMEDIA)
					.where(RMEDIA.REGISTRY.eq(registry))
					.execute();		
		}

		// Añadir o actualizar banco
		Integer rbank = ctx.selectFrom(RBANK)
				.where(RBANK.REGISTRY.eq(registry))
				.fetchAny(RBANK.ID);
		
		if (rbank == null) {
			if (AonStringUtils.isNotBlank(trabajador.getIban()))
				rbank = ctx.insertInto(RBANK)
							.set(RBANK.DOMAIN, domain)
							.set(RBANK.REGISTRY, registry)
							.set(RBANK.BANK_ACCOUNT, trabajador.getIban())				
							.set(RBANK.BIC, trabajador.getBic())
							.set(RBANK.ALIAS, trabajador.getBanco())
							.returning(RBANK.ID)
							.fetchOne()
							.getId();			
		} else {
			if (AonStringUtils.isNotBlank(trabajador.getIban())) {
				ctx.update(RBANK)
					.set(RBANK.BANK_ACCOUNT, trabajador.getIban())				
					.set(RBANK.BIC, trabajador.getBic())
					.set(RBANK.ALIAS, trabajador.getBanco())
					.where(RBANK.REGISTRY.eq(registry))
					.execute();
			}
			else {
				ctx.update(RPAYMETHOD)
					.set(RPAYMETHOD.RBANK, (Integer) null)
					.where(RPAYMETHOD.REGISTRY.eq(registry)
					.and(RPAYMETHOD.RBANK.eq(rbank)))
					.execute();

				ctx.delete(RBANK)
					.where(RBANK.REGISTRY.eq(registry))
					.execute();
				rbank = null;
			}
		}
		
		// Añadir o actualizar forma de pago
		id = ctx.selectFrom(RPAYMETHOD)
				.where(RPAYMETHOD.REGISTRY.eq(registry))
				.fetchAny(RPAYMETHOD.ID);
		
		if (id == null) {
			if (getPayMethod(trabajador) != null)
				ctx.insertInto(RPAYMETHOD)
					.set(RPAYMETHOD.DOMAIN, domain)
					.set(RPAYMETHOD.REGISTRY, registry)
					.set(RPAYMETHOD.PAY_METHOD, getPayMethod(trabajador))
					.set(RPAYMETHOD.NUMBER_OF_PYMNTS, (short) 1)
					.set(RPAYMETHOD.PYMNT_DAYS, "")
					.set(RPAYMETHOD.RBANK, rbank)
					.execute();			
		} else {
			if (getPayMethod(trabajador) != null)
				ctx.update(RPAYMETHOD)
					.set(RPAYMETHOD.PAY_METHOD, getPayMethod(trabajador))
					.set(RPAYMETHOD.RBANK, rbank)
					.where(RPAYMETHOD.REGISTRY.eq(registry))
					.execute();
			else 
				ctx.delete(RPAYMETHOD)
					.where(RPAYMETHOD.REGISTRY.eq(registry))
					.execute();		
		}
		
		// Añadir o actualizar person
		id = ctx.selectFrom(PERSON)
				.where(PERSON.REGISTRY.eq(registry))
				.fetchAny(PERSON.REGISTRY);
		
		if (id == null) {
			ctx.insertInto(PERSON)
				.set(PERSON.REGISTRY, registry)
				.set(PERSON.DOMAIN, domain)
				.set(PERSON.BIRTH_DATE, AonDateUtils.toSql(trabajador.getFnac()))
				.set(PERSON.GENDER, getGender(trabajador.getSexo()))
				.set(PERSON.MARITAL_STATUS, getMaritalStatus(trabajador.getEcivil2()))
				.set(PERSON.SOCIAL_SECURITY_NUM, trabajador.getSscod()+trabajador.getSsnum()+AonStringUtils.trimToEmpty(trabajador.getSsctrl()))
				.set(PERSON.NAME, trabajador.getNombre())
				.set(PERSON.FIRST_SURNAME, trabajador.getApell1())
				.set(PERSON.SECOND_SURNAME, trabajador.getApell2())
				.execute();
		} else {
			ctx.update(PERSON)
				.set(PERSON.BIRTH_DATE, AonDateUtils.toSql(trabajador.getFnac()))
				.set(PERSON.GENDER, getGender(trabajador.getSexo()))
				.set(PERSON.MARITAL_STATUS, getMaritalStatus(trabajador.getEcivil2()))
				.set(PERSON.SOCIAL_SECURITY_NUM, trabajador.getSscod()+trabajador.getSsnum()+AonStringUtils.trimToEmpty(trabajador.getSsctrl()))
				.set(PERSON.NAME, trabajador.getNombre())
				.set(PERSON.FIRST_SURNAME, trabajador.getApell1())
				.set(PERSON.SECOND_SURNAME, trabajador.getApell2())
				.where(PERSON.REGISTRY.eq(registry))
				.execute();
		}
		
	}
	
	// Devuelve tipo identificador de AON, según valor de Omega
	private static byte getDocumentType(String tipo) {
		tipo = AonStringUtils.trimToEmpty(tipo);
		switch (tipo) {
			case "1": // DNI
				return 0;
			case "2": // Pasaporte
				return 3;
			case "6": // NIE
				return 2;
			default:
				return 6; // Otro
		}
	}
	
	// Devuelve valor sexo de AON, según sexo de Omega
	private static byte getGender(String sexo) {
		sexo = AonStringUtils.trimToEmpty(sexo);
		switch (sexo) {
			case "H":
				return 0; // Hombre
			case "M":
				return 1; // Mujer 
			default:
				return 2; // Desconocido
		}
	}
	
	// Devuelve el estado civil de AON, según el estado civil de Omega
	private static byte getMaritalStatus(String estado) {
		estado = AonStringUtils.trimToEmpty(estado);
		switch (estado) {
			case "S": // Soltero
				return 0;
			case "C": // Casado
				return 1;
			case "V": // Viudo
				return 4;
			case "A": // Separado
				return 3;
			case "D": // Divorciado""
				return 2;
			default:
				return 5;
		}
	}
	
	// Devuelve el pais de AON (ISO2) según el pais de Omega (ISO_CODE)
	private static String getCountry(String pais) {
		if (AonStringUtils.isBlank(pais)) {
			return null;
		}
		for (Country country : Country.values()) {
			if (country.getIsoCode() == Integer.parseInt(pais))
				return country.getIso2();
		}
		return null;
	}
	
	// Devuelve el id de la forma de pago de AON, según si el trabajador en Omega se le paga por Talon o Transferencia
	private static Integer getPayMethod(Trabajador trabajador) {

		// Si en el trabajador está cumplimentado "Banco Transferencia" o "Banco Talon", se asume 
		// pago por transferencia o por talón, para lo cual se busca en el dominio padre, una forma 
		// de pago que sea "TRANSFERENCIA" o "TALON o CHEQUE", si no se encuentra, se crea, ya que 
		// si no se pone la forma de pago en AON, no se verá el IBAN aunque esté grabado en la tabla
		
		Integer payMethod = null;
		
		if (AonStringUtils.isNotBlank(trabajador.getTransf())) {
			
			// Pago por transferencia
			
			payMethod = ctx.select(PAY_METHOD.ID)
							.from(PAY_METHOD)
							.where(PAY_METHOD.DOMAIN.equal(getParentDomain()))
							.and(PAY_METHOD.NAME.equalIgnoreCase("TRANSFERENCIA"))				
							.fetchAny(PAY_METHOD.ID);
			
			if (payMethod == null)
				payMethod = ctx.insertInto(PAY_METHOD)
								.set(PAY_METHOD.DOMAIN, getParentDomain())
								.set(PAY_METHOD.NAME, "TRANSFERENCIA")
								.set(PAY_METHOD.TYPE, (byte) 5)
								.returning(PAY_METHOD.ID)
								.fetchOne()
								.getId();
				
		} else if (AonStringUtils.isNotBlank(trabajador.getTalon())) {
			
			// Pago por talon
			
			payMethod = ctx.select()
							.from(PAY_METHOD)
							.where(PAY_METHOD.DOMAIN.equal(getParentDomain()))
							.and(PAY_METHOD.NAME.equalIgnoreCase("TALON").or(PAY_METHOD.NAME.equalIgnoreCase("CHEQUE")))				
							.fetchAny(PAY_METHOD.ID);

			if (payMethod == null)
				payMethod = ctx.insertInto(PAY_METHOD)
								.set(PAY_METHOD.DOMAIN, getParentDomain())
								.set(PAY_METHOD.NAME, "TALON")
								.set(PAY_METHOD.TYPE, (byte) 4)
								.returning(PAY_METHOD.ID)
								.fetchOne()
								.getId();
			
		}
		
		return payMethod;
		
	}
	
	private static int addContract(int domain, int registry, Empresa empresa, Trabajador trabajador) {
		
		// Fecha Fin: se pone la fecha de baja o la de fin de contrato, si la de baja está vacia 
		Date endDate = trabajador.getFbaja();
		if (endDate == null && trabajador.getFfincto() != null && (trabajador.getFfincto().equals(trabajador.getFalta()) || trabajador.getFfincto().after(trabajador.getFalta())))
			endDate = trabajador.getFfincto();
		
		int contract = ctx.insertInto(CONTRACT)
					.set(CONTRACT.DOMAIN, domain)
					.set(CONTRACT.PERSON, registry)      
					.set(CONTRACT.WORKPLACE, getWorkplace(trabajador.getCentro(), empresa))       
					.set(CONTRACT.ENTERPRISE_CCC, empresa.getEnterpriseCCC())        
					.set(CONTRACT.START_DATE, AonDateUtils.toSql(trabajador.getFalta()))       
					.set(CONTRACT.END_DATE, AonDateUtils.toSql(endDate))  
					.set(CONTRACT.SENIORITY_DATE, AonDateUtils.toSql(trabajador.getFantig()))   
					.set(CONTRACT.ENTERPRISE_ACTIVITY, empresa.getEnterpriseActivity())       
					.set(CONTRACT.SS_REGIME, getSsRegime(trabajador))       
					.set(CONTRACT.CATEGORY_DESCRIPTION, trabajador.getNomcat())     
					.set(CONTRACT.AGREEMENT_LEVEL, getAgreementLevel(trabajador.getConven(), trabajador.getCateg()))      
					.returning(CONTRACT.ID)
					.fetchOne()
					.getId();
		
		// Cuando es autonomo, tambien hay que añadir un registro a contract_info con RETA = true
		if ("S".equals(trabajador.getAutono())) {
			ctx.insertInto(CONTRACT_INFO)
				.set(CONTRACT_INFO.DOMAIN, domain)
				.set(CONTRACT_INFO.CONTRACT, contract)
				.set(CONTRACT_INFO.NAME, "RETA")
				.set(CONTRACT_INFO.EXPRESSION, "true")
				.set(CONTRACT_INFO.START_DATE, AonDateUtils.toSql(trabajador.getFalta()))
				.set(CONTRACT_INFO.END_DATE, AonDateUtils.toSql(trabajador.getFbaja()))
				.execute();
		}
		
		return contract;
	
	}
	
	private static void updateContract(int domain, int contract, Empresa empresa, Trabajador trabajador) {
		
		// Fecha Fin: se pone la fecha de baja o la de fin de contrato, si la de baja está vacia 
		Date endDate = trabajador.getFbaja();
		if (endDate == null && trabajador.getFfincto() != null && (trabajador.getFfincto().equals(trabajador.getFalta()) || trabajador.getFfincto().after(trabajador.getFalta())))
			endDate = trabajador.getFfincto();
		
		ctx.update(CONTRACT)
				.set(CONTRACT.WORKPLACE, getWorkplace(trabajador.getCentro(), empresa))       
				.set(CONTRACT.END_DATE, AonDateUtils.toSql(endDate))  
				.set(CONTRACT.SENIORITY_DATE, AonDateUtils.toSql(trabajador.getFantig()))     
				.set(CONTRACT.SS_REGIME, getSsRegime(trabajador))       
				.set(CONTRACT.CATEGORY_DESCRIPTION, trabajador.getNomcat())  
				.where(CONTRACT.ID.eq(contract))
				.execute();
		
		// Cuando es autonomo, tambien hay que añadir un registro a contract_info con RETA = true
		if ("S".equals(trabajador.getAutono())) {
			ctx.insertInto(CONTRACT_INFO)
				.set(CONTRACT_INFO.DOMAIN, domain)
				.set(CONTRACT_INFO.CONTRACT, contract)
				.set(CONTRACT_INFO.NAME, "RETA")
				.set(CONTRACT_INFO.EXPRESSION, "true")
				.set(CONTRACT_INFO.START_DATE, AonDateUtils.toSql(trabajador.getFalta()))
				.set(CONTRACT_INFO.END_DATE, AonDateUtils.toSql(trabajador.getFbaja()))
				.execute();			
		} else {
			// Si no está marcado como autónomo, borro el posible registro que se hubiera 
			// añadido por un movimiento anterior, que va al mismo contrat de AON, para 
			// que el registro contract de AON, se quede con el último movimiento de Omega
			ctx.delete(CONTRACT_INFO)
				.where(CONTRACT_INFO.ID.eq(contract))
				.execute();
		}		
	
	}
	
	// Devuelve el id del centro de trabajo de AON, del centro de trabajo del 
	// trabajador de Omega o del centro de trabajo de la empresa de Omega
	private static Integer getWorkplace(String codigoCentro, Empresa empresa) {
		
		Integer workplace = null;
		
		// Buscamos el id en el centro de trabajo (si el trabajador lo tiene indicado)
		if (AonStringUtils.isNotBlank(codigoCentro)) {
			for (Centro centro : empresa.getCentros()) {
				if (centro.getCodigo().equals(codigoCentro))
					workplace = centro.getWorkplace();
			}
		}
		
		// El trabajador no tiene centro de trabajo, o el que tiene no existe
		// se coge el id del centro de trabajo que se crea para la empresa
		if (workplace == null) {
			workplace = empresa.getWorkplace();
		}
			
		return workplace;
		
	}
	
	// Devuelve el regimen (tipo de cotización) de AON, según algunos campos del trabajador de Omega
	private static byte getSsRegime(Trabajador trabajador) {
		
		if ("S".equals(trabajador.getAutono()))
			return 3; // RETA
		else if ("S".equals(trabajador.getSocio()))
			return 1; // SOCIOS COOP
		else if ("S".equals(trabajador.getCespsol()))
			return 2; // JUBILACION ACTIVA
		else if ("S".equals(trabajador.getSngj()))
			return 4; // GARANTIA JUVENIL
		else 
			return 0; // COMUN
		
	}
	
	// Devuelve el id de AON de la categoría de Omega
	private static Integer getAgreementLevel(String convenio, String categoria) {
		
		if (AonStringUtils.isBlank(convenio) || AonStringUtils.isBlank(categoria)) {
			return null;
		}
		
		int agreement = ctx.select(AGREEMENT_DATA.AGREEMENT)
							.from(AGREEMENT_DATA)
							.where(AGREEMENT_DATA.DOMAIN.eq(getParentDomain())
							.and(AGREEMENT_DATA.NAME.equal("CODIGO_OMEGA")
							.and(AGREEMENT_DATA.EXPRESSION.equal(TraspasoConvenios.getCodigoOmega(convenio)))))
							.fetchOne(AGREEMENT_DATA.AGREEMENT);
		
		return ctx.select(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL)
					.from(AGREEMENT_LEVEL_DATA)
					.join(AGREEMENT_LEVEL).on(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL.eq(AGREEMENT_LEVEL.ID))
					.where(AGREEMENT_LEVEL.AGREEMENT.eq(agreement)
					.and(AGREEMENT_LEVEL_DATA.NAME.equal("CODIGO_OMEGA")
					.and(AGREEMENT_LEVEL_DATA.EXPRESSION.equal(TraspasoConvenios.getCodigoOmega(categoria)))))
					.fetchOne(AGREEMENT_LEVEL_DATA.AGREEMENT_LEVEL);
		
	}
	
	private static void addContractData(int domain, int contract, Empresa empresa, Trabajador trabajador) {
		
		Date startDate = trabajador.getFalta();
		Date endDate = trabajador.getFbaja(); 
		
		// Coeficiente de parcialidad (se pone en tanto por 1)
		if (trabajador.getCoeficienteParcialidad() < 1) {
			addContractData(domain, contract, "COEFICIENTE_PARCIALIDAD", trabajador.getCoeficienteParcialidad(), startDate, endDate);	
		}			
		
		// Grupo de Cotización: Si es autónomo y está vacio, se pone por defecto "01", pues si no 
		// luego en AON no se calcula el borrador si falta el grupo de cotización, aunque sea autonomo
		if ("S".equals(trabajador.getAutono()) && AonStringUtils.isBlank(trabajador.getGrupo()))
			addContractData(domain, contract, "GRUPO_COTIZACION", "01", startDate, endDate); 	
		else 
			addContractData(domain, contract, "GRUPO_COTIZACION", trabajador.getGrupo(), startDate, endDate); 	//		F20GRUPO    	CHAR( 2)     	name	expression"	name = GRUPO_COTIZACION, expression va entre comillas dobles
		
		// Contrato y Ocupacion, solo se graban si no es autónomo
		if (!"S".equals(trabajador.getAutono())) {
			addContractData(domain, contract, "TC2", trabajador.getClcto(), startDate, endDate);	//		F20CLCTO    	CHAR( 3)     	name	expression"	"name = TC2, expression va entre comillas dobles	En Aon cuando es un contrato a tiempo parcial se graba tambien un registro con name = TIEMPO_COMPLETO y expression = false, aunque en AON a un contrato a tiempo parcial, se le puede decir que es a tiempo completo en este caso TIEMPO_COMPLETO = true ??"
			addContractData(domain, contract, "OCUPACION", trabajador.getCotatep(), startDate, endDate);	//		F20COTATEP  	CHAR( 6)     	name	expression"	name = OCUPACION, expression va entre comillas dobles
		}
		
		addContractData(domain, contract, "RLCE", trabajador.getRelaesp(), startDate, endDate);	//		F20RELAESP  	CHAR(4)      	name, expression	RLCE, expressión entre comillas dobles		
		
		if ("S".equals(trabajador.getSocio())) {
			addContractData(domain, contract, "PECULIARITY_TYPE", 2, startDate, endDate);	//		F20SOCIO    	CHAR( 1)     	name, expression	PECULIARITY_TYPE=2	Ademas hay que grabar tambien con 0 (cero), las peculiaridades de cotizacion PORCENTAJE_DESMPL, PORCENTAJE_FOGASA, PORCENTAJE_DESMPL_E"
			addContractData(domain, contract, "PORCENTAJE_DESMPL"  , 0.0, startDate, endDate);
			addContractData(domain, contract, "PORCENTAJE_FOGASA"  , 0.0, startDate, endDate); 
			addContractData(domain, contract, "PORCENTAJE_DESMPL_E", 0.0, startDate, endDate);
		}
		
		if ("S".equals(trabajador.getMculto())) {
			addContractData(domain, contract, "PECULIARITY_TYPE", 6, startDate, endDate);	//		F20MCULTO   	CHAR( 1)     	name, expression	PECULIARITY_TYPE=6	Ademas hay que grabar tambien con 0 (cero), las peculiaridades de cotizacion PORCENTAJE_DESMPL, PORCENTAJE_FP, PORCENTAJE_FOGASA, PORCENTAJE_FP_E, PORCENTAJE_DESMPL_E"
			addContractData(domain, contract, "PORCENTAJE_DESMPL"  , 0.0, startDate, endDate); 
			addContractData(domain, contract, "PORCENTAJE_FP"      , 0.0, startDate, endDate); 
			addContractData(domain, contract, "PORCENTAJE_FOGASA"  , 0.0, startDate, endDate); 
			addContractData(domain, contract, "PORCENTAJE_FP_E"    , 0.0, startDate, endDate); 
			addContractData(domain, contract, "PORCENTAJE_DESMPL_E", 0.0, startDate, endDate);
		}
		
		if ("S".equals(trabajador.getPfrdl1493())) {
			addContractData(domain, contract, "PECULIARITY_TYPE", 3, startDate, endDate);	//		F20PFRDL1493 	CHAR(1)       	name, expression	PECULIARITY_TYPE=3	Ademas hay que grabar tambien con 0 (cero), las peculiaridades de cotizacion PORCENTAJE_CGC, PORCENTAJE_DESMPL, PORCENTAJE_FP, PORCENTAJE_CGC_E, PORCENTAJE_IT, PORCENTAJE_IMS, PORCENTAJE_FOGASA, PORCENTAJE_FP_E, PORCENTAJE_DESMPL_E"
			addContractData(domain, contract, "PORCENTAJE_CGC"     , 0.0, startDate, endDate);
			addContractData(domain, contract, "PORCENTAJE_DESMPL"  , 0.0, startDate, endDate);
			addContractData(domain, contract, "PORCENTAJE_FP"      , 0.0, startDate, endDate);
			addContractData(domain, contract, "PORCENTAJE_CGC_E"   , 0.0, startDate, endDate);
			addContractData(domain, contract, "PORCENTAJE_IT"      , 0.0, startDate, endDate);
			addContractData(domain, contract, "PORCENTAJE_IMS"     , 0.0, startDate, endDate);
			addContractData(domain, contract, "PORCENTAJE_FOGASA"  , 0.0, startDate, endDate);
			addContractData(domain, contract, "PORCENTAJE_FP_E"    , 0.0, startDate, endDate);
			addContractData(domain, contract, "PORCENTAJE_DESMPL_E", 0.0, startDate, endDate);
		}
		
		if ("S".equals(trabajador.getCespsol())) {
			addContractData(domain, contract, "PECULIARITY_TYPE", 1, startDate, endDate);	//		F20CESPSOL   	CHAR(1)       	name, expression	PECULIARITY_TYPE=1	Además se deben grabar las siguientes peculiaridades de cotización:	PORCENTAJE_CGC = 2.25, PORCENTAJE_DESMPL = 0, PORCENTAJE_FP = 0, PORCENTAJE_CGC_E = 7.25, PORCENTAJE_FOGASA = 0, PORCENTAJE_FP_E = 0, PORCENTAJE_DESMPL_E = 0"
			addContractData(domain, contract, "PORCENTAJE_CGC"     , 2.25, startDate, endDate);
			addContractData(domain, contract, "PORCENTAJE_DESMPL"  , 0.0 , startDate, endDate);
			addContractData(domain, contract, "PORCENTAJE_FP"      , 0.0 , startDate, endDate);
			addContractData(domain, contract, "PORCENTAJE_CGC_E"   , 7.25, startDate, endDate);
			addContractData(domain, contract, "PORCENTAJE_FOGASA"  , 0.0 , startDate, endDate);
			addContractData(domain, contract, "PORCENTAJE_FP_E"    , 0.0 , startDate, endDate);
			addContractData(domain, contract, "PORCENTAJE_DESMPL_E", 0.0 , startDate, endDate);
		}
		
		if ("S".equals(trabajador.getBonif65()) && trabajador.getFini65() != null) {
			addContractData(domain, contract, "PECULIARITY_TYPE"   , 5   , trabajador.getFini65(), null);	//		F20BONIF65  	CHAR(1)      	name, expression	PECULIARITY_TYPE = 5	Además hay que grabar las siguientes peculiaridades de cotización:	PORCENTAJE_CGC = 0.25	PORCENTAJE_DESMPL = 0	PORCENTAJE_FP = 0	PORCENTAJE_CGC_E = 1.25	PORCENTAJE_FOGASA = 0	PORCENTAJE_FP_E = 0	PORCENTAJE_DESMPL_E = 0"
			addContractData(domain, contract, "PORCENTAJE_CGC"     , 0.25, trabajador.getFini65(), null);
			addContractData(domain, contract, "PORCENTAJE_DESMPL"  , 0.0 , trabajador.getFini65(), null);
			addContractData(domain, contract, "PORCENTAJE_FP"      , 0.0 , trabajador.getFini65(), null);
			addContractData(domain, contract, "PORCENTAJE_CGC_E"   , 1.25, trabajador.getFini65(), null);
			addContractData(domain, contract, "PORCENTAJE_FOGASA"  , 0.0 , trabajador.getFini65(), null);
			addContractData(domain, contract, "PORCENTAJE_FP_E"    , 0.0 , trabajador.getFini65(), null);
			addContractData(domain, contract, "PORCENTAJE_DESMPL_E", 0.0 , trabajador.getFini65(), null);
		}
		
		if ("S".equals(trabajador.getExcldes())) {
			addContractData(domain, contract, "PORCENTAJE_DESMPL", 0.0, startDate, endDate);	//		F20EXCLDES   	CHAR(1)       	name, expression	PORCENTAJE_DESMPL = 0			
		}
		
		if ("S".equals(trabajador.getExclfgs())) {
			addContractData(domain, contract, "PORCENTAJE_FOGASA", 0.0, startDate, endDate);	//		F20EXCLFGS   	CHAR(1)       	name, expression	PORCENTAJE_FOGASA = 0	
		}
		
		if ("S".equals(trabajador.getExclfp())) {
			addContractData(domain, contract, "PORCENTAJE_FP", 0.0, startDate, endDate);	//		F20EXCLFP    	CHAR(1)       	name, expression	PORCENTAJE_FP = 0	
		}
		
		// Si porcentaje de IRPF fijo
		if ("F".equals(trabajador.getIrpfijo())) {
			addContractData(domain, contract, "PORCENTAJE_IRPF", trabajador.getIrpf(), startDate, endDate);	//		F20IRPFIJO  	CHAR( 1)     	name, expression	Si porcentaje de retención fijo está marcado entonces PORCENTAJE_IRPF = <valor>			
		}
		
		if (trabajador.getDias() != 0) {
			addContractData(domain, contract, "DIAS_MES", trabajador.getDias(), startDate, endDate);	//		F20DIAS     	NUMERIC(5,2) 	name	expression"	"DIAS_MES = <valor>	No sé si esto se pondría así y se utilizaría esta variable. He hecho una prueba poniendola en la base de datos y no me aparece."			
		} else if ("M".equals(trabajador.getRetrib())) {
			byte grupo = 0;
			if (trabajador.getGrupo() != null) {
				grupo = Byte.parseByte(trabajador.getGrupo());
				if (grupo >= 8 && grupo <= 11) {
					// Retribucion mensual en un trabajador de grupo diario, hay que poner una variable con DIAS_MES = 30
					addContractData(domain, contract, "DIAS_MES", 30, startDate, endDate);
				}
			}			
		}

		// Contratos a tiempo parcial 
		if (trabajador.getHorasl() != 0 || trabajador.getHorasm() != 0 || trabajador.getHorasx() != 0 || trabajador.getHorasj() != 0 || trabajador.getHorasv() != 0 || trabajador.getHorass() != 0 || trabajador.getHorasd() != 0) {			
			if (trabajador.getHorasl() != 0) 
				addContractData(domain, contract, "HORAS_LUNES", trabajador.getHorasl(), startDate, endDate);	//		F20HORASL    	NUMERIC(6,2)  	name	expression"	HORAS_LUNES
			if (trabajador.getHorasm() != 0)
				addContractData(domain, contract, "HORAS_MARTES", trabajador.getHorasm(), startDate, endDate);	//		F20HORASM    	NUMERIC(6,2)  	name	expression"	HORAS_MARTES
			if (trabajador.getHorasx() != 0)
				addContractData(domain, contract, "HORAS_MIERCOLES", trabajador.getHorasx(), startDate, endDate);	//		F20HORASX    	NUMERIC(6,2)  	name	expression"	HORAS_MIERCOLES
			if (trabajador.getHorasj() != 0)
				addContractData(domain, contract, "HORAS_JUEVES", trabajador.getHorasj(), startDate, endDate);	//		F20HORASJ    	NUMERIC(6,2)  	name	expression"	HORAS_JUEVES
			if (trabajador.getHorasv() != 0)
				addContractData(domain, contract, "HORAS_VIERNES", trabajador.getHorasv(), startDate, endDate);	//		F20HORASV    	NUMERIC(6,2)  	name	expression"	HORAS_VIERNES
			if (trabajador.getHorass() != 0)
				addContractData(domain, contract, "HORAS_SABADO", trabajador.getHorass(), startDate, endDate);	//		F20HORASS    	NUMERIC(6,2)  	name	expression"	HORAS_SABADO
			if (trabajador.getHorasd() != 0)
				addContractData(domain, contract, "HORAS_DOMINGO", trabajador.getHorasd(), startDate, endDate);	//		F20HORASD    	NUMERIC(6,2)  	name	expression"	HORAS_DOMINGO	"
			
			addContractData(domain, contract, "TIEMPO_COMPLETO", false, startDate, endDate);
		} else if (trabajador.getHoras() != 0) {
			double horas = 0;
			String diasTra = AonStringUtils.trimToEmpty(trabajador.getDiastra());
			// Si esta vacio dias de trabajo, se asume que trabaja de lunes a viernes y 
			// que las horas estan puestas en base a 7 dias 
			if (diasTra.isEmpty()) {
				diasTra = "LMXJV";
				horas = AonMathUtils.round(trabajador.getHoras() * 7 / 5);					
			}
			
			if (diasTra.contains("L"))
				addContractData(domain, contract, "HORAS_LUNES", horas, startDate, endDate);	
			if (diasTra.contains("M"))
				addContractData(domain, contract, "HORAS_MARTES", horas, startDate, endDate);	
			if (diasTra.contains("X"))
				addContractData(domain, contract, "HORAS_MIERCOLES", horas, startDate, endDate);	
			if (diasTra.contains("J"))
				addContractData(domain, contract, "HORAS_JUEVES", horas, startDate, endDate);	
			if (diasTra.contains("V"))
				addContractData(domain, contract, "HORAS_VIERNES", horas, startDate, endDate);	
			if (diasTra.contains("S"))
				addContractData(domain, contract, "HORAS_SABADO", horas, startDate, endDate);	
			if (diasTra.contains("D"))
				addContractData(domain, contract, "HORAS_DOMINGO", horas, startDate, endDate);
			
			addContractData(domain, contract, "TIEMPO_COMPLETO", false, startDate, endDate);
		} else {
			// Si no están especificadas horas, se asume jornada tiempo completo
			addContractData(domain, contract, "TIEMPO_COMPLETO", true, startDate, endDate);
		}
		
		// Empresas Regimen Agrario
		if ("5".equals(empresa.getTipo())) {
			int cotizacionAgrario = "J".equals(trabajador.getReamodcot()) ? 2 : 1; 
			addContractData(domain, contract, "MODELO_COTIZACION_AGRARIO", cotizacionAgrario, startDate, endDate);	//		F20REAMODCOT	CHAR(1)      	name, expression	MODELO_COTIZACION_AGRARIO = 1 (Mensual)	MODELO_COTIZACION_AGRARIO = 2 (Jornadas Reales)"
			if (!"S".equals(trabajador.getAgrdes())) {
				addContractData(domain, contract, "PORCENTAJE_DESMPL", 0.0, startDate, endDate);  // F20AGRDES   	CHAR( 1)     	name, expression	Se pondrá como peculiaridades de cotización, si es REA, aunque en Omega hay otro campo además tambien para ver si cotiza a desempleo, supongo que si es REA será este campo el que haya que tomar como referencia.
			}			
		}
		
	}
	
	private static void addContractData(int domain, int contract, String name, String expression, Date startDate, Date endDate) {
		// En contract_data los datos que son de tipo String, se guardan entre comillas dobles
		if (AonStringUtils.isNotBlank(expression)) {
			saveContractData(domain, contract, name, "\"" + AonStringUtils.trimToEmpty(expression) + "\"", startDate, endDate);	
		}		
	}
	
	private static void addContractData(int domain, int contract, String name, int expression, Date startDate, Date endDate) {		
		saveContractData(domain, contract, name, Integer.toString(expression), startDate, endDate);	
	}
	
	private static void addContractData(int domain, int contract, String name, double expression, Date startDate, Date endDate) {		
		saveContractData(domain, contract, name, Double.toString(expression), startDate, endDate);	
	}
	
	private static void addContractData(int domain, int contract, String name, boolean expression, Date startDate, Date endDate) {		
		saveContractData(domain, contract, name, Boolean.toString(expression), startDate, endDate);	
	}
	
	private static void saveContractData(int domain, int contract, String name, String expression, Date startDate, Date endDate) {
		
		if (AonStringUtils.isNotBlank(expression)) {
			ctx.insertInto(CONTRACT_DATA)
				.set(CONTRACT_DATA.DOMAIN, domain)
				.set(CONTRACT_DATA.CONTRACT, contract)
				.set(CONTRACT_DATA.NAME, name)       
				.set(CONTRACT_DATA.EXPRESSION, expression)   
				.set(CONTRACT_DATA.START_DATE, AonDateUtils.toSql(startDate))  
				.set(CONTRACT_DATA.END_DATE, AonDateUtils.toSql(endDate)) 
				.execute();
		}
		
	}

	// Añade los festivos del calendario de Omega al calendario del trabajador
	// Para ello hay que añadir los festivos como "no laborables" en contract_data del trabajador
	private static void addCalendar(int domain, int contract, Empresa empresa, Trabajador trabajador) {
		
		// Primero se borran los festivos que estuvieran grabados, por si 
		// hay varias fichas en Omega del mismo trabajador, que van al mismo contract
		ctx.delete(CONTRACT_DATA)
			.where(CONTRACT_DATA.CONTRACT.eq(contract)
			.and(CONTRACT_DATA.NAME.eq("NO_LABORABLE")))
			.execute();
		
		// Ahora se añaden los festivos del calendario de Omega, solo si el calendario 
		// del trabajador está cumplimentado y es distinto del calendario de la empresa
		if (AonStringUtils.isNotBlank(trabajador.getCalend()) && !AonStringUtils.equals(trabajador.getCalend(), empresa.getCalend())) {

			// Fecha de Alta del contrato actual grabado en AON
			Date startDate = ctx.select(CONTRACT.START_DATE)
								.from(CONTRACT)
								.where(CONTRACT.ID.eq(contract))
								.fetchOne(CONTRACT.START_DATE);
			
			// Grabar los festivos del calendario de Omega, cuya fecha es superior a 
			// la fecha de alta del contrato actual en AON
			Calendario calendario = buscarCalendario(trabajador.getCalend());
			if (calendario != null) {
				for (Date date : calendario.getFestivos()) {
					if (date.equals(startDate) || date.after(startDate)) {
						addContractData(domain, contract, "NO_LABORABLE", 1, date, date);
					}
				}
			}
		}
	}
	
	private static void addIrpfData(int domain, int contract, Trabajador trabajador) {
		
		// Añadir el registro a irpf_data
		
		Byte disabilityLevel = null;
		byte dependence = 0;		
		if (AonStringUtils.equals(trabajador.getGradomi(), "1")) {
			if ("S".equals(trabajador.getAyudami())) {
				disabilityLevel = 1; // 33-65 con necesidad de ayuda
				dependence = 1;
			}
			else {
				disabilityLevel = 0; // 33-65 sin necesidad de ayuda
			}
		} else if (AonStringUtils.equals(trabajador.getGradomi(), "2")) {
			disabilityLevel = 2;
		}	
		
		int id = ctx.insertInto(IRPF_DATA)
					.set(IRPF_DATA.DOMAIN, domain)
					.set(IRPF_DATA.CONTRACT, contract)
					.set(IRPF_DATA.START_DATE, AonDateUtils.toSql(trabajador.getFalta()))  
					.set(IRPF_DATA.END_DATE, AonDateUtils.toSql(trabajador.getFbaja()))
					.set(IRPF_DATA.ISSUE_DATE, AonDateUtils.toSql(trabajador.getFalta()))
					.set(IRPF_DATA.FAMILY_SITUATION, (byte) (Integer.parseInt(trabajador.getEcivil())-1)) 
					.set(IRPF_DATA.SPOUSE_DOCUMENT, trabajador.getNifcony ())         
					.set(IRPF_DATA.DISABILITY_LEVEL, disabilityLevel)         
					.set(IRPF_DATA.DEPENDENCE, dependence)   
					.set(IRPF_DATA.MOVING_DATE, "S".equals(trabajador.getRedmovi()) ? AonDateUtils.toSql(trabajador.getFtrasl()) : null)         
					.set(IRPF_DATA.LABOUR_PROLONGATION, (byte) ("S".equals(trabajador.getRedacti()) ? 1 : 0))  
					.set(IRPF_DATA.DEDUCT_HOME_LOAN, ("S".equals(trabajador.getPresviv()) ? (byte) 1 : null))  
					.set(IRPF_DATA.SPOUSAL_SUPPORT, trabajador.getPension ())         
					.set(IRPF_DATA.FOOD_ANNUITY, trabajador.getAnualid ())
					.returning(IRPF_DATA.ID)
					.fetchOne()
					.getId();
		
		// Descendientes
		
		for (Descendiente descendiente : trabajador.getDescendientes()) {
			
			disabilityLevel = null;
			dependence = 0;		
			if (AonStringUtils.equals(descendiente.getMi(), "1")) {
				disabilityLevel = 0; // 33-65 
				if ("S".equals(descendiente.getMr())) {					
					dependence = 1;
				}
			} else if (AonStringUtils.equals(descendiente.getMi(), "2")) {
				disabilityLevel = 2;
			}	
			
			ctx.insertInto(IRPF_DATA_DESCENDIENTS)
				.set(IRPF_DATA_DESCENDIENTS.DOMAIN, domain)
				.set(IRPF_DATA_DESCENDIENTS.IRPF_DATA, id)		
				.set(IRPF_DATA_DESCENDIENTS.BIRTH_YEAR, Integer.parseInt(descendiente.getAn())) 	
				.set(IRPF_DATA_DESCENDIENTS.ADOPTION_YEAR, AonNumberUtils.toInteger(descendiente.getAd())) 	
				.set(IRPF_DATA_DESCENDIENTS.UNIQUE_PARENT, (byte) ("S".equals(descendiente.getEn()) ? 1 : 0))  
				.set(IRPF_DATA_DESCENDIENTS.DISABILITY_LEVEL, disabilityLevel) 
				.set(IRPF_DATA_DESCENDIENTS.DEPENDENCE, dependence)  
				.execute();			
			
		}
		
		// Ascendientes
		
		for (Ascendiente ascendiente : trabajador.getAscendientes()) {
			
			disabilityLevel = null;
			dependence = 0;		
			if (AonStringUtils.equals(ascendiente.getMi(), "1")) {
				disabilityLevel = 0; // 33-65 
				if ("S".equals(ascendiente.getMr())) {					
					dependence = 1;
				}
			} else if (AonStringUtils.equals(ascendiente.getMi(), "2")) {
				disabilityLevel = 2;
			}	
			
			ctx.insertInto(IRPF_DATA_ASCENDANTS)
				.set(IRPF_DATA_ASCENDANTS.DOMAIN, domain)
				.set(IRPF_DATA_ASCENDANTS.IRPF_DATA, id)
				.set(IRPF_DATA_ASCENDANTS.BIRTH_YEAR, Integer.parseInt(ascendiente.getAn())) 
				.set(IRPF_DATA_ASCENDANTS.ANOTHER_DESCENDIENT, (byte) ((ascendiente.getCo() == null || Integer.parseInt(ascendiente.getCo()) > 1) ? 1 : 0)) 
				.set(IRPF_DATA_ASCENDANTS.DISABILITY_LEVEL, disabilityLevel)  
				.set(IRPF_DATA_ASCENDANTS.DEPENDENCE, dependence)  
				.execute();
			
		}
				
	}
	
	private static void addContractDeduction(int domain, int contract, Trabajador trabajador, Concepto concepto) {

		// Expression
		// 70, 71 : (NOMINA)?(/*user*/186/**/):REMOVE(), esto sería una deducción de 186 euros en nomina
		// 80, 81 : (EXTRA)?(/*user*/186/**/):REMOVE(), esto sería una deducción de 186 euros en paga extra, aunque las pruebas que he hecho poniendo el concepto en el trabajador, no me lo lleva al borrador de la extra
		// 90, 91 : (FINIQUITO)?(/*user*/186/**/):REMOVE(), esto sería una deducción de 186 euros en finiquito
		
		String prefix = "";
		if (concepto.getClave().equals("90") || concepto.getClave().equals("91"))
			prefix = "(FINIQUITO)";
		else if (concepto.getClave().equals("80") || concepto.getClave().equals("81"))
			prefix = "(EXTRA)";
		else 
			prefix = "(NOMINA)"; 
		
		String expression = prefix + "?(/*user*/" + concepto.getAonExpression() + "/**/):REMOVE()";

		// Añadir concepto
		ctx.insertInto(CONTRACT_DEDUCTION)
			.set(CONTRACT_DEDUCTION.DOMAIN, domain)
			.set(CONTRACT_DEDUCTION.CONTRACT, contract)
			.set(CONTRACT_DEDUCTION.TYPE, (byte) 9)  // Otras deducciones
			.set(CONTRACT_DEDUCTION.DESCRIPTION, concepto.getNombre())			
			.set(CONTRACT_DEDUCTION.EXPRESSION, expression)   
			.set(CONTRACT_DEDUCTION.START_DATE, AonDateUtils.toSql(trabajador.getFalta()))  
			.set(CONTRACT_DEDUCTION.END_DATE, AonDateUtils.toSql(trabajador.getFbaja())) 
			.execute();
		
		// Añadir variable
		double importe = concepto.getImporte();
		if ("MTDVS".contains(concepto.getCobro())) {
			importe = AonMathUtils.round(importe / trabajador.getCoeficienteParcialidad(), 4);
		}
		if (AonStringUtils.isNotBlank(concepto.getCobro()))
			addContractData(domain, contract, concepto.getAonVariable(), importe, trabajador.getFalta(), trabajador.getFbaja());

	}
	
	// Añadir concepto de AON para los conceptos salariales de Omega
	private static void addContractPayment(int domain, int contract, Trabajador trabajador, Concepto concepto) {

		// Buscar o añadir el concepto a payment_concept
		int paymentConcept = Traspaso.getPaymentConcept(ctx, concepto);
		
		// Expresion que se usa para el concepto
		String expression = concepto.getAonExpression();
		
		// Controlar concepto de antiguedad con tabla asociada
		if ("02".equals(concepto.getClave()) && !trabajador.getAonFormulaAntiguedad().isEmpty()) {
			expression = trabajador.getAonSeniorityExpression(); 			
		}
		
		// Añadir concepto a contract_payment
		ctx.insertInto(CONTRACT_PAYMENT)
			.set(CONTRACT_PAYMENT.DOMAIN, domain)
			.set(CONTRACT_PAYMENT.CONTRACT, contract)
			.set(CONTRACT_PAYMENT.TYPE, concepto.getAonClaveCRA())  
			.set(CONTRACT_PAYMENT.PAYMENT_CONCEPT, paymentConcept)
			.set(CONTRACT_PAYMENT.DESCRIPTION, concepto.getAonDescription())			
			.set(CONTRACT_PAYMENT.EXPRESSION, expression)
			.set(CONTRACT_PAYMENT.IRPF_EXPRESSION, concepto.getAonIrpfExpression())
			.set(CONTRACT_PAYMENT.QUOTE_EXPRESSION, concepto.getAonQuoteExpression())
			.set(CONTRACT_PAYMENT.SALARY_TYPE, (byte) 0)
			.set(CONTRACT_PAYMENT.START_DATE, AonDateUtils.toSql(trabajador.getFalta()))  
			.set(CONTRACT_PAYMENT.END_DATE, AonDateUtils.toSql(trabajador.getFbaja())) 
			.execute();
	}
	
	// Añadir concepto de AON para las pagas extras de Omega
	private static void addContractPayment(int domain, int contract, Trabajador trabajador, Paga paga, String conceptosPagas) {
		
		// Buscar o añadir el concepto a payment_concept
		int paymentConcept = Traspaso.getPaymentConcept(ctx, paga);
		
		// Añadir concepto a contract_payment
		ctx.insertInto(CONTRACT_PAYMENT)
			.set(CONTRACT_PAYMENT.DOMAIN, domain)
			.set(CONTRACT_PAYMENT.CONTRACT, contract)
			.set(CONTRACT_PAYMENT.TYPE, (byte) 4)  
			.set(CONTRACT_PAYMENT.PAYMENT_CONCEPT, paymentConcept)
			.set(CONTRACT_PAYMENT.DESCRIPTION, paga.getAonDescription())			
			.set(CONTRACT_PAYMENT.EXPRESSION, paga.getAonExpression(conceptosPagas, false))
			.set(CONTRACT_PAYMENT.IRPF_EXPRESSION, "_P")
			.set(CONTRACT_PAYMENT.QUOTE_EXPRESSION, "_P")
			.set(CONTRACT_PAYMENT.SALARY_TYPE, (byte) 0)
			.set(CONTRACT_PAYMENT.MONTH, paga.getAonMonth())
			.set(CONTRACT_PAYMENT.START_DATE, AonDateUtils.toSql(trabajador.getFalta()))  
			.set(CONTRACT_PAYMENT.END_DATE, AonDateUtils.toSql(trabajador.getFbaja()))			
			.execute();
		
	}
	
	// Añadir concepto de AON para los complementos E/A de Omega
	private static void addContractPayment(int domain, int contract, Trabajador trabajador, Complemento complemento, String conceptosEnf, String conceptosAcc) {

		// Buscar o añadir el concepto a payment_concept
		int paymentConcept = Traspaso.getPaymentConcept(ctx, complemento);
		
		// Añadir concepto a contract_payment
		ctx.insertInto(CONTRACT_PAYMENT)
			.set(CONTRACT_PAYMENT.DOMAIN, domain)
			.set(CONTRACT_PAYMENT.CONTRACT, contract)
			.set(CONTRACT_PAYMENT.TYPE, complemento.getAonClaveCRA())  
			.set(CONTRACT_PAYMENT.PAYMENT_CONCEPT, paymentConcept)
			.set(CONTRACT_PAYMENT.DESCRIPTION, complemento.getAonDescription())			
			.set(CONTRACT_PAYMENT.EXPRESSION, complemento.getAonExpression(conceptosEnf, conceptosAcc))
			.set(CONTRACT_PAYMENT.IRPF_EXPRESSION, "_P")
			.set(CONTRACT_PAYMENT.SALARY_TYPE, (byte) 0)
			.set(CONTRACT_PAYMENT.START_DATE, AonDateUtils.toSql(trabajador.getFalta()))  
			.set(CONTRACT_PAYMENT.END_DATE, AonDateUtils.toSql(trabajador.getFbaja())) 
			.execute();
	}
	
	// Comprueba si existe el concepto (según su clave), en los conceptos salariales del trabajador
	private static boolean existeConcepto(Trabajador trabajador, String clave) {		
		for(Concepto concepto : trabajador.getConceptos()) {
			if (clave.equals(concepto.getClave()))
				return true;
		}
		return false;
	}
	
	// Busca el concepto (segun su clave) en la categoria indicada
	private static Concepto buscarConcepto(Categoria categoria, String claveConcepto) {
		
		if (categoria != null) {
			for (Concepto concepto : categoria.getConceptos()) { 
				if (AonStringUtils.equals(concepto.getClave(), claveConcepto))
					return concepto;
			}
		}
		return null;
		
	}
	
	// Busca la paga extra (segun su mes) en la categoria indicada
	private static Paga buscarPaga(Categoria categoria, String mesPaga) {
		
		if (categoria != null) {
			for (Paga paga : categoria.getPagas()) {
				if (AonStringUtils.equals(paga.getMes(), mesPaga))
					return paga;			
			}			
		}			
		return null;
		
	}	
	
}
