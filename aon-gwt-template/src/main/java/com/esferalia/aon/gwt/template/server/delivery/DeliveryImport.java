package com.esferalia.aon.gwt.template.server.delivery;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.registry.RegistryPayMethod;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.CustomerStatus;
import com.esferalia.aon.occam.api.model.type.DeliveryStatus;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;

public class DeliveryImport {

	public static DeliveryImport getInstance() {
		return new DeliveryImport();
	}
	
	public DeliveryImport() {
	
	}

	Clientes cli = new Clientes();
	Albv albv = new Albv();
	AlbvDet albvDet = new AlbvDet();

	public DeliveryInfo importation(byte[] data){
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			HSSFWorkbook workbook = new HSSFWorkbook(bais);
			
			HSSFSheet customerSheet = workbook.getSheet("CLIENTES");
			HSSFSheet deliverySheet = workbook.getSheet("ALBV");
			HSSFSheet deliveryDetailSheet = workbook.getSheet("ALBVDET");
			
			// CUSTOMER
			
			LinkedList<String> titleList = new LinkedList<>();
			LinkedList<Clientes> clientList = new LinkedList<>();
			Iterator<Row> rowIterator = customerSheet.iterator();
			Iterable<Row> rowIterable = () -> rowIterator;
			Stream<Row> rowStream = StreamSupport.stream(rowIterable.spliterator(),false);
			
			rowStream.forEach(row ->{
				Iterator<Cell> cellIterator = row.cellIterator();
				Iterable<Cell> cellIterable = () -> cellIterator;
				Stream<Cell> cellStream = StreamSupport.stream(cellIterable.spliterator(),false);
				cli = new Clientes();
				cellStream.forEach(cell -> {
					if(row.getRowNum() == 0) {
						titleList.add(cell.getStringCellValue());	
					} else {
						String title = titleList.get(cell.getColumnIndex());
						checkClientes(title, cell);
					}
				});
				if(row.getRowNum() != 0) clientList.add(cli);
			});
			
			// ALBV
			
			LinkedList<String> titleList2 = new LinkedList<>();
			LinkedList<Albv> albvList = new LinkedList<>();
			Iterator<Row> rowIterator2 = deliverySheet.iterator();
			Iterable<Row> rowIterable2 = () -> rowIterator2;
			Stream<Row> rowStream2 = StreamSupport.stream(rowIterable2.spliterator(),false);
						
			rowStream2.forEach(row ->{
				Iterator<Cell> cellIterator = row.cellIterator();
				Iterable<Cell> cellIterable = () -> cellIterator;
				Stream<Cell> cellStream = StreamSupport.stream(cellIterable.spliterator(),false);
				albv = new Albv();
				cellStream.forEach(cell -> {
					if(row.getRowNum() == 0) {
						titleList2.add(cell.getStringCellValue());	
					} else {
						String title = titleList2.get(cell.getColumnIndex());
						checkAlbv(title, cell);
					}
				});
				if(row.getRowNum() != 0) albvList.add(albv);
			});

			// ALBVDET
			
			LinkedList<String> titleList3 = new LinkedList<>();
			LinkedList<AlbvDet> albvDetList = new LinkedList<>();
			Iterator<Row> rowIterator3 = deliveryDetailSheet.iterator();
			Iterable<Row> rowIterable3 = () -> rowIterator3;
			Stream<Row> rowStream3 = StreamSupport.stream(rowIterable3.spliterator(),false);
						
			rowStream3.forEach(row ->{
				Iterator<Cell> cellIterator = row.cellIterator();
				Iterable<Cell> cellIterable = () -> cellIterator;
				Stream<Cell> cellStream = StreamSupport.stream(cellIterable.spliterator(),false);
				albvDet = new AlbvDet();
				cellStream.forEach(cell -> {
					if(row.getRowNum() == 0) {
						titleList3.add(cell.getStringCellValue());	
					} else {
						String title = titleList3.get(cell.getColumnIndex());
						checkAlbvDet(title, cell);
					}
				});
				if(row.getRowNum() != 0) albvDetList.add(albvDet);
			});
			return new DeliveryInfo().setClientList(clientList)
				.setAlbvList(albvList)
				.setAlbvDetList(albvDetList);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public com.esferalia.aon.gwt.template.shared.Error insertDelivery(Domain domain, User user, DeliveryInfo di, com.esferalia.aon.gwt.template.shared.Error error) {
		try {
			HashMap<String, Integer> clientes = importClientes(domain, user, di.getClientList());
			HashMap<Integer, Delivery> albv = importAlbv(domain, user, di.getAlbvList(), clientes);
			importAlbvDet(domain, user, di.getAlbvDetList(), albv);
		} catch (Exception e) {
			
			error.setError(false);
			error.setTextError(e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
		}
		return error;
	}
	
	private void checkClientes(String title, Cell cell) {
		if("razonSocial".equalsIgnoreCase(title)) {
			cli.setRazonSocial(cell.getStringCellValue());
			return;
		}
		if("alias".equalsIgnoreCase(title)) {
			cli.setAlias(cell.getStringCellValue());
			return;
		}
		if("tipoDocumento".equalsIgnoreCase(title)) {
			cli.setTipoDocumento(((Double)cell.getNumericCellValue()).intValue());
			return;
		}
		if("paisDocumento".equalsIgnoreCase(title)) {
			cli.setPaisDocumento(cell.getStringCellValue());
			return;
		}
		if("documento".equalsIgnoreCase(title)) {
			cli.setDocumento(cell.getStringCellValue());
			return;
		}
		if("nacionalidad".equalsIgnoreCase(title)) {
			cli.setNacionalidad(cell.getStringCellValue());
			return;
		}
		if("cuenta".equalsIgnoreCase(title)) {
			cli.setCuenta(cell.getStringCellValue());
			return;
		}
		if("re".equalsIgnoreCase(title)) {
			cli.setRe(((Double)cell.getNumericCellValue()).intValue());
			return;
		}
		if("transaccion".equalsIgnoreCase(title)) {
			cli.setTransaccion(((Double)cell.getNumericCellValue()).intValue());
			return;
		}
		if("retencion".equalsIgnoreCase(title)) {
			cli.setRetencion(((Double)cell.getNumericCellValue()).intValue());
			return;
		}
		if("facturarAlbaranesAgrupados".equalsIgnoreCase(title)) {
			cli.setFacturarAlbaranesAgrupados(((Double)cell.getNumericCellValue()).intValue());
			return;
		}
		if("aliasDireccion".equalsIgnoreCase(title)) {
			cli.setAliasDireccion(cell.getStringCellValue());
			return;
		}
		if("tipoVia".equalsIgnoreCase(title)) {
			cli.setTipoVia(cell.getStringCellValue());
			return;
		}
		if("direccion".equalsIgnoreCase(title)) {
			cli.setDireccion(cell.getStringCellValue());
			return;
		}
		if("numero".equalsIgnoreCase(title)) {
			cli.setNumero(cell.getStringCellValue());
			return;
		}
		if("direccion2".equalsIgnoreCase(title)) {
			cli.setDireccion2(cell.getStringCellValue());
			return;
		}
		if("direccion3".equalsIgnoreCase(title)) {
			cli.setDireccion3(cell.getStringCellValue());
			return;
		}
		if("cp".equalsIgnoreCase(title)) {
			cli.setCp(cell.getStringCellValue());
			return;
		}
		if("ciudad".equalsIgnoreCase(title)) {
			cli.setCiudad(cell.getStringCellValue());
			return;
		}
		if("provincia".equalsIgnoreCase(title)) {
			cli.setProvincia(cell.getStringCellValue());
			return;
		}
		if("nombreProvincia".equalsIgnoreCase(title)) {
			cli.setNombreProvincia(cell.getStringCellValue());
			return;
		}
		if("pais".equalsIgnoreCase(title)) {
			cli.setPais(cell.getStringCellValue());
			return;
		}
		
		if("telefono1".equalsIgnoreCase(title)) {
			cli.setTelefono1(cell.getStringCellValue());
			return;
		}
		if("telefono2".equalsIgnoreCase(title)) {
			cli.setTelefono2(cell.getStringCellValue());
			return;
		}
		if("fax".equalsIgnoreCase(title)) {
			cli.setFax(cell.getStringCellValue());
			return;
		}
		if("email".equalsIgnoreCase(title)) {
			cli.setEmail(cell.getStringCellValue());
			return;
		}
		if("web".equalsIgnoreCase(title)) {
			cli.setWeb(cell.getStringCellValue());
			return;
		}
		if("banco".equalsIgnoreCase(title)) {
			cli.setBanco(cell.getStringCellValue());
			return;
		}
		if("bic".equalsIgnoreCase(title)) {
			cli.setBic(cell.getStringCellValue());
			return;
		}
		if("cuentaBanco".equalsIgnoreCase(title)) {
			cli.setCuentaBanco(cell.getStringCellValue());
			return;
		}
		if("formaPago".equalsIgnoreCase(title)) {
			cli.setFormaPago(cell.getStringCellValue());
			return;
		}
		if("numeroVtos".equalsIgnoreCase(title)) {
			cli.setNumeroVtos(((Double)cell.getNumericCellValue()).intValue());
			return;
		}
		if("diasAlPrimerVto".equalsIgnoreCase(title)) {
			cli.setDiasAlPrimerVto(((Double)cell.getNumericCellValue()).intValue());
			return;
		}
		if("diasEntreVtos".equalsIgnoreCase(title)) {
			cli.setDiasEntreVtos(((Double)cell.getNumericCellValue()).intValue());
			return;
		}
		if("diasPago".equalsIgnoreCase(title)) {
			cli.setDiasPago(cell.getStringCellValue());
			return;
		}
		if("segmento".equalsIgnoreCase(title)) {
			cli.setSegmento(cell.getStringCellValue());
			return;
		}

	}
	
	private void checkAlbv(String title, Cell cell) {
		if("id".equalsIgnoreCase(title)) {
			albv.setId(((Double)cell.getNumericCellValue()).intValue());
			return;
		}
		if("serie".equalsIgnoreCase(title)) {
			albv.setSerie(cell.getStringCellValue());
			return;
		}
		if("numero".equalsIgnoreCase(title)) {
			System.out.println(cell);
			albv.setNumero(((Double)cell.getNumericCellValue()).intValue());
			return;
		}
		if("documento".equalsIgnoreCase(title)) {
			albv.setDocumento(cell.getStringCellValue());
			return;
		}
		if("fecha".equalsIgnoreCase(title)) {
			albv.setFecha(cell.getDateCellValue());
			return;
		}
		if("centroTrabajo".equalsIgnoreCase(title)) {
			albv.setCentroTrabajo(cell.getStringCellValue());
			return;
		}
		if("almacen".equalsIgnoreCase(title)) {
			albv.setAlmacen(cell.getStringCellValue());
			return;
		}
		if("expediente".equalsIgnoreCase(title)) {
			albv.setExpediente(cell.getStringCellValue());
			return;
		}
		if("aliasDireccion".equalsIgnoreCase(title)) {
			albv.setAliasDireccion(cell.getStringCellValue());
			return;
		}
		if("tipoVia".equalsIgnoreCase(title)) {
			albv.setTipoVia(cell.getStringCellValue());
			return;
		}
		if("direccion".equalsIgnoreCase(title)) {
			albv.setDireccion(cell.getStringCellValue());
			return;
		}
		if("numeroDir".equalsIgnoreCase(title)) {
			albv.setNumeroDir(cell.getStringCellValue());
			return;
		}
		if("direccion2".equalsIgnoreCase(title)) {
			albv.setDireccion2(cell.getStringCellValue());
			return;
		}
		if("direccion3".equalsIgnoreCase(title)) {
			albv.setDireccion3(cell.getStringCellValue());
			return;
		}
		if("cp".equalsIgnoreCase(title)) {
			albv.setCp(cell.getStringCellValue());
			return;
		}
		if("ciudad".equalsIgnoreCase(title)) {
			albv.setCiudad(cell.getStringCellValue());
			return;
		}
		if("provincia".equalsIgnoreCase(title)) {
			albv.setProvincia(cell.getStringCellValue());
			return;
		}
		if("nombreProvincia".equalsIgnoreCase(title)) {
			albv.setNombreProvincia(cell.getStringCellValue());
			return;
		}
		if("banco".equalsIgnoreCase(title)) {
			albv.setBanco(cell.getStringCellValue());
			return;
		}
		if("bic".equalsIgnoreCase(title)) {
			albv.setBic(cell.getStringCellValue());
			return;
		}
		if("cuentaBanco".equalsIgnoreCase(title)) {
			albv.setCuentaBanco(cell.getStringCellValue());
			return;
		}
		if("formaPago".equalsIgnoreCase(title)) {
			albv.setFormaPago(cell.getStringCellValue());
			return;
		}
		if("numeroVtos".equalsIgnoreCase(title)) {
			albv.setNumeroVtos(((Double)cell.getNumericCellValue()).intValue());
			return;
		}
		if("diasAlPrimerVto".equalsIgnoreCase(title)) {
			albv.setDiasAlPrimerVto(((Double)cell.getNumericCellValue()).intValue());
			return;
		}
		if("diasEntreVtos".equalsIgnoreCase(title)) {
			albv.setDiasEntreVtos(((Double)cell.getNumericCellValue()).intValue());
			return;
		}
		if("diasPago".equalsIgnoreCase(title)) {
			albv.setDiasPago(cell.getStringCellValue());
			return;
		}
		if("segmento".equalsIgnoreCase(title)) {
			albv.setSegmento(cell.getStringCellValue());
			return;
		}
	}
	
	private void checkAlbvDet(String title, Cell cell) {
		if("albv".equalsIgnoreCase(title)) {
			albvDet.setAlbv(((Double)cell.getNumericCellValue()).intValue());
			return;
		}
		if("linea".equalsIgnoreCase(title)) {
			albvDet.setLinea(((Double)cell.getNumericCellValue()).intValue());
			return;
		}
		if("articulo".equalsIgnoreCase(title)) {
			albvDet.setArticulo(cell.getStringCellValue());
			return;
		}
		if("detalle".equalsIgnoreCase(title)) {
			albvDet.setDetalle(cell.getStringCellValue());
			return;
		}
		if("detalle2".equalsIgnoreCase(title)) {
			albvDet.setDetalle2(cell.getStringCellValue());
			return;
		}
		if("detalle3".equalsIgnoreCase(title)) {
			albvDet.setDetalle3(cell.getStringCellValue());
			return;
		}
		if("concepto".equalsIgnoreCase(title)) {
			albvDet.setConcepto(cell.getStringCellValue());
			return;
		}
		if("cantidad".equalsIgnoreCase(title)) {
			albvDet.setCantidad(cell.getNumericCellValue());
			return;
		}
		if("precio".equalsIgnoreCase(title)) {
			albvDet.setPrecio(cell.getNumericCellValue());
			return;
		}
		if("descuentos".equalsIgnoreCase(title)) {
			albvDet.setDescuentos(cell.getStringCellValue());
			return;
		}
	}
	
	private HashMap<String, Integer> importClientes(Domain domain, User user, LinkedList<Clientes> clientes) {
		HashMap<String, Integer> map = new HashMap<>();
		Integer[] scps = AON.getUserScopes(domain.getName(), domain.getId(), user.getLogin(), user.getId());
		if(scps == null) scps =  AON.getUserScopes(domain.getName(), user.getDomain(), user.getLogin(), user.getId());
		Integer scope = scps != null ? scps[0] : null;
		clientes.stream().forEach(r -> {
			Customer customer = AON.getCustomer(domain.getName(), domain.getId(), user.getLogin(), f -> f.getDocumentProperty().eq(r.getDocumento()));

			if(customer.getId() == null) {
				Registry registry = new Registry()
						.setAlias(r.getAlias())
						.setDocument(r.getDocumento())
						.setDocumentCountry(Country.safeValueOf(r.getPaisDocumento()))
						.setDocumentType(DocumentType.safeValueOf(r.getTipoDocumento()))
						.setDomain(domain.getId())
						.setName(r.getRazonSocial())
						.setNationality(Country.safeValueOf(r.getNacionalidad()));
				registry = AON.insertRegistry(domain.getName(), domain.getId(), user.getLogin(), registry);
				Integer account = null;
				if(r.getCuenta() != null) {
					account = ACCOUNTING.getAccount(domain.getName(), domain.getId(), user.getLogin(), r.getCuenta()).getId();
				}
				customer = new Customer()
						.setDomain(domain.getId())
						.setRegistry(registry)
						.setScope(scope)
						.setStatus(CustomerStatus.ACTIVE)
						.setTransaction(r.getTransaccion()!= null ? r.getTransaccion().byteValue() : 0)
						.setAccount(account)
						.setSurcharge(r.getRe() != null ? r.getRe().byteValue() : 0)
						.setWithholding(r.getRetencion() != null ? r.getRetencion().byteValue() : 0)
						.setDeliveryGrouped(r.getFacturarAlbaranesAgrupados() != null ? r.getFacturarAlbaranesAgrupados().byteValue() : 1);
				customer = AON.insertCustomer(domain.getName(), domain.getId(), user.getLogin(), customer);				
			}
			Integer customerID = customer.getId();
			if(r.getAliasDireccion() != null) {
				RAddress address = AON.getRAddress(domain.getName(), domain.getId(), user.getLogin(), f -> f.getAliasProperty().eq(r.getAliasDireccion()).and(f.getRegistryProperty().eq(customerID)));
				if(address.getId() == null) {
					address = new RAddress()
						.setType((byte)0)
						.setRegistry(customerID)
						.setAddress(r.getDireccion())
						.setAddress2(r.getDireccion2())
						.setAddress3(r.getDireccion3())
						.setAlias(r.getAliasDireccion())
						.setCity(r.getCiudad())
						.setDomain(domain.getId())
						.setNumber(r.getNumero())
						.setStreet_type(r.getTipoVia())
						.setZip(r.getCp());
					AON.insertRAddress(domain.getName(), domain.getId(), user.getLogin(), address);				
				}
			}
			
			if(r.getCuentaBanco() != null) {
				RegistryBank rbank = new RegistryBank()
						.setDomain(domain.getId())
						.setRegistry(customerID)
						.setBankAccount(r.getCuentaBanco())
						.setBic(r.getBic())
						.setSuffix("")
						.setAlias(r.getBanco())
						.setActive(true)
						.setAccount(customer.getAccount());
				AON.insertRBank(domain.getName(), domain.getId(), user.getLogin(), rbank);
			}
			
			if(r.getFormaPago() != null) {
				RegistryPayMethod rpm = AON.getRPayMethod(domain.getName(), domain.getId(), user.getLogin(), f -> f.getRegistryProperty().eq(customerID));
				if(rpm.getId() == null) {
					PayMethod pm = AON.getPayMethod(domain.getName(), domain.getId(), user.getLogin(), r.getFormaPago());
					if(domain.isEnableHeredity() && pm.getId() == null) pm = AON.getPayMethod(domain.getName(), domain.getParentId(), user.getLogin(), r.getFormaPago());
					if(pm.getId() != null) {
						RegistryPayMethod rpaymethod = new RegistryPayMethod()
								.setDomain(domain.getId())
								.setRegistry(customerID)
								.setPayMethod(pm.getId())
								.setNumberOfPymnts(r.getNumeroVtos().shortValue())
								.setDaysToFirstPymnt(r.getDiasAlPrimerVto().shortValue())
								.setDaysBetwenPymnts(r.getDiasEntreVtos().shortValue())
								.setPymnt_days(r.getDiasPago()); 
						AON.insertRPayMethod(domain.getName(), domain.getId(), user.getLogin(), rpaymethod);
					}
				}
			}
			map.put(r.getDocumento(), customerID);
		});
		return map;
	}
	
	private HashMap<Integer, Delivery> importAlbv(Domain domain, User user, LinkedList<Albv> albv, HashMap<String, Integer> clientes) {
		HashMap<Integer, Delivery> map = new HashMap<>();
		Integer[] scps = AON.getUserScopes(domain.getName(), domain.getId(), user.getLogin(), user.getId());
		if(scps == null) scps =  AON.getUserScopes(domain.getName(), user.getDomain(), user.getLogin(), user.getId());
		Integer scope = scps != null ? scps[0] : null;
		albv.stream().forEach(r -> {
			Integer customerID = null;
			Integer raddress = null;
			if(clientes.containsKey(r.getDocumento())) {
				customerID = clientes.get(r.getDocumento());
				if(r.getAliasDireccion() != null) {
					RAddress address = AON.getRAddress(domain.getName(), domain.getId(), user.getLogin(), f -> f.getAliasProperty().eq(r.getAliasDireccion()).and(f.getRegistryProperty().eq(clientes.get(r.getDocumento()))));
					if(address == null) {
						address = new RAddress()
							.setAddress(r.getDireccion())
							.setAddress2(r.getDireccion2())
							.setAddress3(r.getDireccion3())
							.setAlias(r.getAliasDireccion())
							.setCity(r.getCiudad())
							.setDomain(domain.getId())
							.setNumber(r.getNumeroDir())
							.setStreet_type(r.getTipoVia())
							.setZip(r.getCp());
						AON.insertRAddress(domain.getName(), domain.getId(), user.getLogin(), address);				
					}
					if(address != null) raddress = address.getId();
				}
			}
			Warehouse warehouse = AON.getWarehouse(domain.getName(), domain.getId(), user.getLogin(), f -> f.getDomainProperty().eq(domain.getId()).and(f.getNameProperty().eq(r.getAlmacen())));
			
			PayMethod pm = new PayMethod();
			Project p = new Project();
			if(r.getFormaPago() != null) pm = AON.getPayMethod(domain.getName(), domain.getId(), user.getLogin(), r.getFormaPago());
			if(domain.isEnableHeredity() && pm.getId() == null) pm = AON.getPayMethod(domain.getName(), domain.getParentId(), user.getLogin(), r.getFormaPago());
			if(r.getExpediente() != null) p = AON.getProject(domain.getName(), domain.getId(), user.getLogin(), f -> f.getDomainProperty().eq(domain.getId()).and(f.getNameProperty().eq(r.getExpediente())));
			
			Delivery delivery = new Delivery()
					.setDomain(domain.getId())
					.setSeries(r.getSerie())
					.setScope(scope)
					.setStatus(DeliveryStatus.PENDING)
					.setNumber(r.getNumero())
					.setCustomer(customerID)
					.setIssueTime(r.getFecha())
					.setWorkplace(warehouse.getWorkplace())
					.setAddress(raddress)
					.setBankAccount(r.getCuentaBanco())
					.setBic(r.getBanco())
					.setPayMethod(pm.getId())
					.setNumberOfPymnts(r.getNumeroVtos() != null ? r.getNumeroVtos().shortValue(): 0)
					.setDaysToFirstPymnt(r.getDiasAlPrimerVto() != null ? r.getDiasAlPrimerVto().shortValue() : 0)
					.setDaysBetweenPymnt(r.getDiasEntreVtos() != null ? r.getDiasEntreVtos().shortValue() : 0)
					.setPymntDays(r.getDiasPago() != null ? r.getDiasPago() : "0")					
					.setTotalPackages(0.0)
					.setTotalWeight(0.0)
					.setProject(p);
			delivery = AON.insertDelivery(domain.getName(), domain.getId(), user.getLogin(), delivery);
			
			map.put(r.getId(), new Delivery().setId(delivery.getId()).setNumber(warehouse.getId()));
		});
		return map;
	}
	
	private void importAlbvDet(Domain domain, User user, LinkedList<AlbvDet> albvDet, HashMap<Integer, Delivery> albv) {
		albvDet.stream().forEach(r -> {
			Product product =  AON.getProduct(domain.getName(), domain.getId(), user.getLogin(), f -> f.getDomainProperty().eq(domain.getId()).and(f.getCodeProperty().eq(r.getArticulo())));			
			Item item = AON.getItem(domain.getName(), domain.getId(), user.getLogin(), f -> f.getProductProperty().eq(product.getId())
					.and(r.getDetalle() != null ? f.getDetailProperty().eq(r.getDetalle()) :
						f.getDetailProperty().eq("").or(f.getDetailProperty().isNull()))
					.and(r.getDetalle2() != null ? f.getDetail2Property().eq(r.getDetalle2()) :
						f.getDetail2Property().eq("").or(f.getDetail2Property().isNull()))
					.and(r.getDetalle3() != null ? f.getDetail3Property().eq(r.getDetalle3()):
						f.getDetail3Property().eq("").or(f.getDetail3Property().isNull())));
			
			
			if(item != null) {
				DeliveryDetail dd = new DeliveryDetail()
						.setDomain(domain.getId())
						.setDelivery(albv.get(r.getAlbv()))
						.setWarehouse(albv.get(r.getAlbv()).getNumber())
						.setLine(r.getLinea().shortValue())
						.setItem(item)
						.setDescription(r.getConcepto())
						.setPrice(r.getPrecio())
						.setDiscountExpression(r.getDescuentos() != null ? r.getDescuentos() : "0.0");
				AON.insertDeliveryDetail(domain.getName(), domain.getId(), user.getLogin(), dd);
			}
		});
	}
}