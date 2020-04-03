package com.esferalia.aon.gwt.template.server.delivery;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.esferalia.aon.gwt.template.jooq.DBProduct;
import com.esferalia.aon.gwt.template.shared.Error;
import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductStatus;
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.RegistryPayMethod;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.CustomerStatus;
import com.esferalia.aon.occam.api.model.type.DeliveryStatus;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.type.ProductType;
import com.esferalia.aon.occam.api.model.type.StreetType;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;
import com.esferalia.aon.watson.server.AonDateUtils;

public class DeliveryImport {

	public static DeliveryImport getInstance() {
		return new DeliveryImport();
	}

	public DeliveryImport() {

	}

	Clientes cli = new Clientes();
	Albv albv = new Albv();
	AlbvDet albvDet = new AlbvDet();
	DeliveryInfo di;
	public DeliveryInfo importation(byte[] data){
		HSSFWorkbook workbook = null;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			workbook = new HSSFWorkbook(bais);

			HSSFSheet customerSheet = workbook.getSheet("CLIENTES");
			HSSFSheet deliverySheet = workbook.getSheet("ALBV");
			HSSFSheet deliveryDetailSheet = workbook.getSheet("ALBVDET");

			di = new DeliveryInfo();
			di.setError(new Error().setError(true).setTextError(new LinkedList<>()).setTextWarning(new LinkedList<>()));
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
						try {
							checkClientes(title, cell);
						} catch (Exception e) {
							e.printStackTrace();
							di.getError().getTextError().add(e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
							di.getError().setError(false);
						}
					}
				});
				if(row.getRowNum() != 0) {
					if(hasClientRequiredParameters()) {
						clientList.add(cli);
					} else {
						di.getError().getTextError().add("ERROR! CLIENTES: linea " + (row.getRowNum() + 1) + " -  Faltan datos obligatorios.");
						di.getError().setError(false);
					}
				}
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
						try {
							checkAlbv(title, cell);
						} catch (Exception e) {
							e.printStackTrace();
							di.getError().getTextError().add(e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
							di.getError().setError(false);
						}
					}
				});
				if(row.getRowNum() != 0) {
					if(hasAlbvRequiredParameters()) {
						albvList.add(albv);
					} else {
						di.getError().getTextError().add("ERROR! ALBV: linea " + (row.getRowNum() + 1)  + " -  Faltan datos obligatorios.");
						di.getError().setError(false);
					}
				}
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
						try {
							checkAlbvDet(title, cell);
						} catch (Exception e) {
							e.printStackTrace();
							di.getError().getTextError().add(e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
							di.getError().setError(false);
						}
					}
				});
				if(row.getRowNum() != 0) {
					if(hasAlbvDetRequiredParameters()) {
						albvDetList.add(albvDet);
					} else {
						di.getError().getTextError().add("ERROR! ALBVDET: linea " + (row.getRowNum() + 1) + " -  Faltan datos obligatorios.");
						di.getError().setError(false);
					}
				}
			});
			return di.setClientList(clientList)
				.setAlbvList(albvList)
				.setAlbvDetList(albvDetList);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	public DeliveryInfo importationX(byte[] data){
		XSSFWorkbook workbook = null;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			workbook = new XSSFWorkbook(bais);

			XSSFSheet customerSheet = workbook.getSheet("CLIENTES");
			XSSFSheet deliverySheet = workbook.getSheet("ALBV");
			XSSFSheet deliveryDetailSheet = workbook.getSheet("ALBVDET");

			di = new DeliveryInfo();
			di.setError(new Error().setError(true).setTextError(new LinkedList<>()).setTextWarning(new LinkedList<>()));
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
						try {
							checkClientes(title, cell);
						} catch (Exception e) {
							e.printStackTrace();
							di.getError().getTextError().add(e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
							di.getError().setError(false);
						}
					}
				});
				if(row.getRowNum() != 0) {
					if(hasClientRequiredParameters()) {
						clientList.add(cli);
					} else {
						di.getError().getTextError().add("ERROR! CLIENTES: linea " + (row.getRowNum() + 1) + " -  Faltan datos obligatorios.");
						di.getError().setError(false);
					}
				}
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
						try {
							checkAlbv(title, cell);
						} catch (Exception e) {
							e.printStackTrace();
							di.getError().getTextError().add(e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
							di.getError().setError(false);
						}
					}
				});
				if(row.getRowNum() != 0) {
					if(hasAlbvRequiredParameters()) {
						albvList.add(albv);
					} else {
						di.getError().getTextError().add("ERROR! ALBV: linea " + (row.getRowNum() + 1)  + " -  Faltan datos obligatorios.");
						di.getError().setError(false);
					}
				}
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
						try {
							checkAlbvDet(title, cell);
						} catch (Exception e) {
							e.printStackTrace();
							di.getError().getTextError().add(e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
							di.getError().setError(false);
						}
					}
				});
				if(row.getRowNum() != 0) {
					if(hasAlbvDetRequiredParameters()) {
						albvDetList.add(albvDet);
					} else {
						di.getError().getTextError().add("ERROR! ALBVDET: linea " + (row.getRowNum() + 1) + " -  Faltan datos obligatorios.");
						di.getError().setError(false);
					}
				}
			});
			return di.setClientList(clientList)
				.setAlbvList(albvList)
				.setAlbvDetList(albvDetList);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public DeliveryInfo insertDelivery(Domain domain, User user, DeliveryInfo dinfo, com.esferalia.aon.gwt.template.shared.Error error) {
		di = dinfo;
		try {
			HashMap<String, Integer> clientes = importClientes(domain, user);
			HashMap<Integer, Delivery> albv = importAlbv(domain, user, clientes);
			importAlbvDet(domain, user, albv);
		} catch (Exception e) {
			e.printStackTrace();
			di.getError().setError(false);
			di.getError().setTextError(e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
		}
		return di;
	}

	private Boolean hasClientRequiredParameters() {
		return cli.getRazonSocial() != null && cli.getTipoDocumento() != null
			&& cli.getPaisDocumento() != null && cli.getDocumento() != null
			&& cli.getNacionalidad() != null;
	}

	private Object getObjectValue(Cell cell){
		if(CellType.STRING == cell.getCellTypeEnum())
			return cell.getStringCellValue();
		if(CellType.NUMERIC == cell.getCellTypeEnum())
			return cell.getNumericCellValue();
		if(CellType.FORMULA == cell.getCellTypeEnum())
			return cell.getCellFormula();
		if(CellType.BOOLEAN == cell.getCellTypeEnum()) {
			return cell.getBooleanCellValue() ? 1.0 : 0.0;
		}
		return null;
	}

	private void checkClientes(String title, Cell cell) {
		Object o = getObjectValue(cell);
		if(o == null) return;
		String column = getColumn(cell.getColumnIndex());
		Integer row = cell.getRowIndex() + 1;
		if("razonSocial".equalsIgnoreCase(title)) {
			if(o.toString().length() > 64) {
				di.getError().getTextWarning().add("WARNING! CLIENTES: linea " + row + " columna " + column + " - " + title + " Longitud erronea > 64");
				cli.setRazonSocial(o.toString().substring(0,64));
			} else cli.setRazonSocial(o.toString());
			return;
		}
		if("alias".equalsIgnoreCase(title)) {
			if(o.toString().length() > 32) {
				di.getError().getTextWarning().add("WARNING! CLIENTES: linea " + row + " columna " + column + " - " + title + " Longitud erronea > 32");
				cli.setAlias(o.toString().substring(0, 32));
			} else cli.setAlias(o.toString());
			return;
		}
		if("tipoDocumento".equalsIgnoreCase(title)) {
			Double d = -1.0;
			try {
				d = Double.parseDouble(o.toString());
			} catch (Exception e) {
				di.getError().getTextError().add("ERROR! CLIENTES: linea " + row + " columna " + column + " - " + title + " El valor introducido no es num�rico.");
				di.getError().setError(false);
				return;
			}

			Integer val = d.intValue();
			if(val >= 0 && val < 7) {
				cli.setTipoDocumento(val);
			} else {
				di.getError().getTextWarning().add("WARNING! CLIENTES: linea " +row + " columna " + column + " - " + title + " Fuera de Rango - Por defecto: 6 - OTROS");
				cli.setTipoDocumento(6); // 6 - OTROS.
			}
			return;
		}
		if("paisDocumento".equalsIgnoreCase(title)) {
			Country c = Country.safeValueOf(o.toString());
			if(c != null) {
				cli.setPaisDocumento(cell.getStringCellValue());
			} else {
				di.getError().getTextError().add("ERROR! CLIENTES: linea " + row + " columna " + column + " - " + title + " incorrecto");
				di.getError().setError(false);
			}
			return;
		}
		if("documento".equalsIgnoreCase(title)) {
			if(o.toString().length() > 16) {
				di.getError().getTextError().add("ERROR! CLIENTES: linea " + row + " columna " + column + " - " + title + " Longitud erronea > 16");
				di.getError().setError(false);
			} else cli.setDocumento(o.toString());
			return;
		}
		if("nacionalidad".equalsIgnoreCase(title)) {
			Country c = Country.safeValueOf(o.toString());
			if(c != null) {
				cli.setNacionalidad(cell.getStringCellValue());
			} else {
				di.getError().getTextError().add("ERROR! CLIENTES: linea " + row + " columna " + column + " - " + title + " incorrecto");
				di.getError().setError(false);
			}
			return;
		}
		if("cuenta".equalsIgnoreCase(title)) {
			if(o.toString().length() == 0) return;
			if(o.toString().length() != 9) {
				di.getError().getTextWarning().add("WARNING! CLIENTES: linea " + row + " columna " + column + " - " + title + " Longitud erronea /= 9");
			} else cli.setCuenta(cell.getStringCellValue());
			return;
		}
		if("re".equalsIgnoreCase(title)) {
			Double d = -1.0;
			try {
				d = Double.parseDouble(o.toString());
			} catch (Exception e) {
				di.getError().getTextError().add("ERROR! CLIENTES: linea " + row + " columna " + column + " - " + title + " El valor introducido no es num�rico.");
				di.getError().setError(false);
				return;
			}
			Integer val = d.intValue();
			if(val.equals(1) || val.equals(0))
				cli.setRe(val);
			else {
				di.getError().getTextWarning().add("WARNING! CLIENTES: linea " + row + " columna " + column + " - " + title + " Fuera de Rango - Por defecto: 0 - No");
				cli.setRe(0);
			}
			return;
		}
		if("transaccion".equalsIgnoreCase(title)) {
			Double d = -1.0;
			try {
				d = Double.parseDouble(o.toString());
			} catch (Exception e) {
				di.getError().getTextError().add("ERROR! CLIENTES: linea " + row + " columna " + column + " - " + title + " El valor introducido no es num�rico.");
				di.getError().setError(false);
				return;
			}
			Integer val = d.intValue();
			if(val >= 0 && val < 5) {
				cli.setTransaccion(val);
			} else {
				di.getError().getTextWarning().add("WARNING! CLIENTES: linea " + row + " columna " + column + " - " + title + " Fuera de Rango - Por defecto: 0 - NACIONAL");
				cli.setTransaccion(0);
			}
			return;
		}
		if("retencion".equalsIgnoreCase(title)) {
			Double d = -1.0;
			try {
				d = Double.parseDouble(o.toString());
			} catch (Exception e) {
				di.getError().getTextError().add("ERROR! CLIENTES: linea " + row + " columna " + column + " - " + title + " El valor introducido no es num�rico.");
				di.getError().setError(false);
				return;
			}
			Integer val = d.intValue();
			if(val.equals(1) || val.equals(0)) {
				cli.setRetencion(val);
			} else {
				di.getError().getTextWarning().add("WARNING! CLIENTES: linea " + row + " columna " + column + " - " + title + " Fuera de Rango - Por defecto: 0 - No");
				cli.setRetencion(0);
			}
			return;
		}
		if("facturarAlbaranesAgrupados".equalsIgnoreCase(title)) {
			if(o.toString().length() == 0) return;
			Double d = -1.0;
			try {
				d = Double.parseDouble(o.toString());
			} catch (Exception e) {
				di.getError().getTextWarning().add("WARNING! CLIENTES: linea " + row + " columna " + column + " - " + title + " El valor introducido no es num�rico. - Por defecto: 1 - Si");
				cli.setFacturarAlbaranesAgrupados(1);
				return;
			}
			Integer val = d.intValue();
			if(val.equals(1) || val.equals(0)) {
				cli.setFacturarAlbaranesAgrupados(val);
			} else {
				di.getError().getTextWarning().add("WARNING! CLIENTES: linea " + row + " columna " + column + " - " + title + " Fuera de Rango - Por defecto: 1 - Si");
				cli.setFacturarAlbaranesAgrupados(1);
			}
			return;
		}
		if("aliasDireccion".equalsIgnoreCase(title)) {
			if(o.toString().length() > 13) {
				di.getError().getTextWarning().add("WARNING! CLIENTES: linea " + row + " columna " + column + " - " + title + " Longitud erronea > 13");
				cli.setAliasDireccion(o.toString().substring(0,13));
			} else cli.setAliasDireccion(o.toString());
			return;
		}
		if("tipoVia".equalsIgnoreCase(title)) {
			if(o.toString().length() == 0) return;
			StreetType st = StreetType.safeValueOf(o.toString());
			if(st != null) {
				cli.setTipoVia(st.getAeatCode());
			} else {
				di.getError().getTextWarning().add("WARNING! CLIENTES: linea " + row + " columna " + column + " - " + title + " incorrecto");
			}
			return;
		}
		if("direccion".equalsIgnoreCase(title)) {
			if(o.toString().length() > 128) {
				di.getError().getTextWarning().add("WARNING! CLIENTES: linea " + row + " columna " + column + " - " + title + " Longitud erronea > 128");
				cli.setDireccion(o.toString().substring(0, 128));
			} else cli.setDireccion(o.toString());
			return;
		}
		if("numero".equalsIgnoreCase(title)) {
			if(o.toString().length() > 6) {
				di.getError().getTextWarning().add("WARNING! CLIENTES: linea " + row + " columna " + column + " - " + title + " Longitud erronea > 6");
				cli.setNumero(o.toString().substring(0, 6));
			} else cli.setNumero(o.toString());
			return;
		}
		if("direccion2".equalsIgnoreCase(title)) {
			if(o.toString().length() > 128) {
				di.getError().getTextWarning().add("WARNING! CLIENTES: linea " + row + " columna " + column + " - " + title + " Longitud erronea > 128");
				cli.setDireccion2(o.toString().substring(0, 128));
			} else cli.setDireccion2(o.toString());
			return;
		}
		if("direccion3".equalsIgnoreCase(title)) {
			if(o.toString().length() > 128) {
				di.getError().getTextWarning().add("WARNING! CLIENTES: linea " + row + " columna " + column + " - " + title + " Longitud erronea > 128");
				cli.setDireccion3(o.toString().substring(0, 128));
			} else cli.setDireccion3(o.toString());
			return;
		}
		if("cp".equalsIgnoreCase(title)) {
			if(o.toString().length() > 16) {
				di.getError().getTextWarning().add("WARNING! CLIENTES: linea " + row + " columna " + column + " - " + title + " Longitud erronea > 16");
				cli.setCp(o.toString().substring(0, 16));
			} else cli.setCp(o.toString());
			return;
		}
		if("ciudad".equalsIgnoreCase(title)) {
			if(o.toString().length() > 64) {
				di.getError().getTextWarning().add("WARNING! CLIENTES: linea " + row + " columna " + column + " - " + title + " Longitud erronea > 64");
				cli.setCiudad(o.toString().substring(0, 64));
			} else cli.setCiudad(o.toString());
			return;
		}
		if("provincia".equalsIgnoreCase(title)) {
			cli.setProvincia(cell.getStringCellValue());
			return;
		}
		if("nombreProvincia".equalsIgnoreCase(title)) {
			if(o.toString().length() > 32) {
				di.getError().getTextWarning().add("WARNING! CLIENTES: linea " + row + " columna " + column + " - " + title + " Longitud erronea > 32");
				cli.setNombreProvincia(o.toString().substring(0, 32));
			} else cli.setNombreProvincia(o.toString());
			return;
		}
		if("pais".equalsIgnoreCase(title)) {
			cli.setPais(cell.getStringCellValue());
			return;
		}

		if("telefono1".equalsIgnoreCase(title)) {
			if(o.toString().length() > 64) {
				di.getError().getTextWarning().add("WARNING! CLIENTES: linea " + row + " columna " + column + " - " + title + " Longitud erronea > 64");
			} else cli.setTelefono1(o.toString());
			return;
		}
		if("telefono2".equalsIgnoreCase(title)) {
			if(o.toString().length() > 64) {
				di.getError().getTextWarning().add("WARNING! CLIENTES: linea " + row + " columna " + column + " - " + title + " Longitud erronea > 64");
			} else cli.setTelefono2(o.toString());
			return;
		}
		if("fax".equalsIgnoreCase(title)) {
			if(o.toString().length() > 64) {
				di.getError().getTextWarning().add("WARNING! CLIENTES: linea " + row + " columna " + column + " - " + title + " Longitud erronea > 64");
			} else cli.setFax(o.toString());
			return;
		}
		if("email".equalsIgnoreCase(title)) {
			if(o.toString().length() > 64) {
				di.getError().getTextWarning().add("WARNING! CLIENTES: linea " + row + " columna " + column + " - " + title + " Longitud erronea > 64");
			} else cli.setEmail(o.toString());
			return;
		}
		if("web".equalsIgnoreCase(title)) {
			if(o.toString().length() > 64) {
				di.getError().getTextWarning().add("WARNING! CLIENTES: linea " + row + " columna " + column + " - " + title + " Longitud erronea > 64");
			} else cli.setEmail(o.toString());
			return;
		}
		if("banco".equalsIgnoreCase(title)) {
			if(o.toString().length() > 64) {
				di.getError().getTextWarning().add("WARNING! CLIENTES: linea " + row + " columna " + column + " - " + title + " Longitud erronea > 64");
				cli.setBanco(o.toString().substring(0,64));
			} else cli.setBanco(o.toString());
			return;
		}

		if("bic".equalsIgnoreCase(title)) {
			if(o.toString().length() == 0) return;
			if(o.toString().length() == 11) {
				cli.setBic(o.toString());
			} else {
				di.getError().getTextWarning().add("WARNING! CLIENTES: linea " + row + " columna " + column + " - " + title + " Longitud erronea /= 11");
			}
			return;
		}
		if("cuentaBanco".equalsIgnoreCase(title)) {
			if(o.toString().length() > 34) {
				di.getError().getTextWarning().add("WARNING! CLIENTES: linea " + row + " columna " + column + " - " + title + " Longitud erronea > 34");
			} else cli.setCuentaBanco(o.toString());
			return;
		}
		if("formaPago".equalsIgnoreCase(title)) {
			if(o.toString().length() > 32) {
				di.getError().getTextWarning().add("WARNING! CLIENTES: linea " + row + " columna " + column + " - " + title + " Longitud erronea > 32");
			} else cli.setFormaPago(o.toString());
			return;
		}
		if("numeroVtos".equalsIgnoreCase(title)) {
			if(o.toString().length() == 0) return;
			Double d = -1.0;
			try {
				d = Double.parseDouble(o.toString());
			} catch (Exception e) {
				di.getError().getTextWarning().add("WARNING! CLIENTES: linea " + row + " columna " + column + " - " + title + " El valor introducido no es num�rico.");
				return;
			}
			Integer val = d.intValue();
			cli.setNumeroVtos(val);
			return;
		}
		if("diasAlPrimerVto".equalsIgnoreCase(title)) {
			if(o.toString().length() == 0) return;
			Double d = -1.0;
			try {
				d = Double.parseDouble(o.toString());
			} catch (Exception e) {
				di.getError().getTextWarning().add("WARNING! CLIENTES: linea " + row + " columna " + column + " - " + title + " El valor introducido no es num�rico.");
				return;
			}
			Integer val = d.intValue();
			cli.setDiasAlPrimerVto(val);
			return;
		}
		if("diasEntreVtos".equalsIgnoreCase(title)) {
			if(o.toString().length() == 0) return;
			Double d = -1.0;
			try {
				d = Double.parseDouble(o.toString());
			} catch (Exception e) {
				di.getError().getTextWarning().add("WARNING! CLIENTES: linea " + row + " columna " + column + " - " + title + " El valor introducido no es num�rico.");
				return;
			}
			Integer val = d.intValue();
			cli.setDiasEntreVtos(val);
			return;
		}
		if("diasPago".equalsIgnoreCase(title)) {
			if(o.toString().length() == 0) return;
			if(o.toString().length() > 8) {
				di.getError().getTextWarning().add("WARNING! CLIENTES: linea " + row + " columna " + column + " - " + title + " Longitud erronea > 8");
			} else cli.setDiasPago(o.toString());
			return;
		}
		if("segmento".equalsIgnoreCase(title)) {
			cli.setSegmento(cell.getStringCellValue());
			return;
		}
	}

	private Boolean hasAlbvRequiredParameters() {
		return albv.getId() != null && albv.getNumero() != null
			&& albv.getDocumento() != null && albv.getFecha() != null;
	}

	private void checkAlbv(String title, Cell cell) {
		Object o = getObjectValue(cell);
		if(o == null) return;
		String column = getColumn(cell.getColumnIndex());
		Integer row = cell.getRowIndex() + 1;
		if("id".equalsIgnoreCase(title)) {
			Double d = -1.0;
			try {
				d = Double.parseDouble(o.toString());
			} catch (Exception e) {
				di.getError().getTextError().add("ERROR! ALBV: linea " + row + " columna " + column + " - " + title + " El valor introducido no es num�rico.");
				di.getError().setError(false);
				return;
			}
			albv.setId(d.intValue());
			return;
		}
		if("serie".equalsIgnoreCase(title)) {
			if(o.toString().length() > 5) {
				di.getError().getTextWarning().add("WARNING! ALBV: linea " + row + " columna " + column + " - " + title + " Longitud erronea > 5");
			} else albv.setSerie(o.toString());
			return;
		}
		if("numero".equalsIgnoreCase(title)) {
			Double d = -1.0;
			try {
				d = Double.parseDouble(o.toString());
			} catch (Exception e) {
				di.getError().getTextError().add("ERROR! ALBV: linea " + row + " columna " + column + " - " + title + " El valor introducido no es num�rico.");
				di.getError().setError(false);
				return;
			}
			albv.setNumero(d.intValue());
			return;
		}
		if("documento".equalsIgnoreCase(title)) {
			if(o.toString().length() > 16) {
				di.getError().getTextError().add("ERROR! ALBV: linea " + row + " columna " + column + " - " + title + " Longitud erronea > 16");
				di.getError().setError(false);
			} else albv.setDocumento(o.toString());
			return;
		}
		if("fecha".equalsIgnoreCase(title)) {
			Date date = new Date();
			try{
				date = cell.getDateCellValue();
			} catch (Exception e) {
				date = AonDateUtils.parse(o.toString(), "dd/MM/yyyy");
			}
			albv.setFecha(date);
			return;
		}
		if("centroTrabajo".equalsIgnoreCase(title)) {
			if(o.toString().length() > 32) {
				di.getError().getTextWarning().add("WARNING! ALBV: linea " + row + " columna " + column + " - " + title + " Longitud erronea > 32");
				albv.setCentroTrabajo(o.toString().substring(0,32));
			} else albv.setCentroTrabajo(o.toString());
			return;
		}
		if("almacen".equalsIgnoreCase(title)) {
			if(o.toString().length() > 32) {
				di.getError().getTextWarning().add("WARNING! ALBV: linea " + row + " columna " + column + " - " + title + " Longitud erronea > 32");
				albv.setAlmacen(o.toString().substring(0,32));
			} else albv.setAlmacen(o.toString());
			return;
		}
		if("expediente".equalsIgnoreCase(title)) {
			if(o.toString().length() > 64) {
				di.getError().getTextWarning().add("WARNING! ALBV: linea " + row + " columna " + column + " - " + title + " Longitud erronea > 64");
				albv.setExpediente(o.toString().substring(0,64));
			} else albv.setExpediente(o.toString());
			return;
		}
		if("aliasDireccion".equalsIgnoreCase(title)) {
			if(o.toString().length() > 13) {
				di.getError().getTextWarning().add("WARNING! ALBV: linea " + row + " columna " + column + " - " + title + " Longitud erronea > 13");
				albv.setAliasDireccion(o.toString().substring(0,13));
			} else albv.setAliasDireccion(o.toString());
			return;
		}
		if("tipoVia".equalsIgnoreCase(title)) {
			if(o.toString().length() == 0) return;
			StreetType st = StreetType.safeValueOf(o.toString());
			if(st != null) {
				albv.setTipoVia(st.getAeatCode());
			} else {
				di.getError().getTextWarning().add("WARNING! ALBV: linea " + row + " columna " + column + " - " + title + " incorrecto");
			}
			return;
		}
		if("direccion".equalsIgnoreCase(title)) {
			if(o.toString().length() > 128) {
				di.getError().getTextWarning().add("WARNING! ALBV: linea " + row + " columna " + column + " - " + title + " Longitud erronea > 128");
				albv.setDireccion(o.toString().substring(0, 128));
			} else albv.setDireccion(o.toString());
			return;
		}
		if("numeroDir".equalsIgnoreCase(title)) {
			albv.setNumeroDir(cell.getStringCellValue());
			return;
		}
		if("direccion2".equalsIgnoreCase(title)) {
			if(o.toString().length() > 128) {
				di.getError().getTextWarning().add("WARNING! ALBV: linea " + row + " columna " + column + " - " + title + " Longitud erronea > 128");
				albv.setDireccion2(o.toString().substring(0, 128));
			} else albv.setDireccion2(o.toString());
			return;
		}
		if("direccion3".equalsIgnoreCase(title)) {
			if(o.toString().length() > 128) {
				di.getError().getTextWarning().add("WARNING! ALBV: linea " + row + " columna " + column + " - " + title + " Longitud erronea > 128");
				albv.setDireccion3(o.toString().substring(0, 128));
			} else albv.setDireccion3(o.toString());
			return;
		}
		if("cp".equalsIgnoreCase(title)) {
			if(o.toString().length() > 16) {
				di.getError().getTextWarning().add("WARNING! ALBV: linea " + row + " columna " + column + " - " + title + " Longitud erronea > 16");
				albv.setCp(o.toString().substring(0, 16));
			} else albv.setCp(o.toString());
			return;
		}
		if("ciudad".equalsIgnoreCase(title)) {
			if(o.toString().length() > 64) {
				di.getError().getTextWarning().add("WARNING! ALBV: linea " + row + " columna " + column + " - " + title + " Longitud erronea > 64");
				albv.setCiudad(o.toString().substring(0, 64));
			} else albv.setCiudad(o.toString());
			return;
		}
		if("provincia".equalsIgnoreCase(title)) {
			albv.setProvincia(cell.getStringCellValue());
			return;
		}
		if("nombreProvincia".equalsIgnoreCase(title)) {
			if(o.toString().length() > 32) {
				di.getError().getTextWarning().add("WARNING! ALBV: linea " + row + " columna " + column + " - " + title + " Longitud erronea > 32");
				albv.setCiudad(o.toString().substring(0, 64));
			} else albv.setNombreProvincia(o.toString());
			return;
		}
		if("banco".equalsIgnoreCase(title)) {
			if(o.toString().length() > 64) {
				di.getError().getTextWarning().add("WARNING! ALBV: linea " + row + " columna " + column + " - " + title + " Longitud erronea > 64");
				albv.setBanco(o.toString().substring(0, 64));
			} else albv.setBanco(o.toString());
			albv.setBanco(cell.getStringCellValue());
			return;
		}
		if("bic".equalsIgnoreCase(title)) {
			if(o.toString().length() == 0) return;
			if(o.toString().length() != 11) {
				di.getError().getTextWarning().add("WARNING! ALBV: linea " + row + " columna " + column + " - " + title + " Longitud erronea /= 11");
			} else albv.setBic(o.toString());
			return;
		}
		if("cuentaBanco".equalsIgnoreCase(title)) {
			if(o.toString().length() > 34) {
				di.getError().getTextWarning().add("WARNING! ALBV: linea " + row + " columna " + column + " - " + title + " Longitud erronea > 34");
			} else albv.setCuentaBanco(o.toString());
			return;
		}
		if("formaPago".equalsIgnoreCase(title)) {
			if(o.toString().length() > 32) {
				di.getError().getTextWarning().add("WARNING! ALBV: linea " + row + " columna " + column + " - " + title + " Longitud erronea > 32");
			} else albv.setFormaPago(o.toString());
			return;
		}
		if("numeroVtos".equalsIgnoreCase(title)) {
			if(o.toString().length() == 0) return;
			Double d = -1.0;
			try {
				d = Double.parseDouble(o.toString());
			} catch (Exception e) {
				di.getError().getTextWarning().add("WARNING! ALBV: linea " + row + " columna " + column + " - " + title + " El valor introducido no es num�rico.");
				return;
			}
			albv.setNumeroVtos(d.intValue());
			return;
		}
		if("diasAlPrimerVto".equalsIgnoreCase(title)) {
			if(o.toString().length() == 0) return;
			Double d = -1.0;
			try {
				d = Double.parseDouble(o.toString());
			} catch (Exception e) {
				di.getError().getTextWarning().add("WARNING! ALBV: linea " + row + " columna " + column + " - " + title + " El valor introducido no es num�rico.");
				return;
			}
			albv.setDiasAlPrimerVto(d.intValue());
			return;
		}
		if("diasEntreVtos".equalsIgnoreCase(title)) {
			if(o.toString().length() == 0) return;
			Double d = -1.0;
			try {
				d = Double.parseDouble(o.toString());
			} catch (Exception e) {
				di.getError().getTextWarning().add("WARNING! ALBV: linea " + row + " columna " + column + " - " + title + " El valor introducido no es num�rico.");
				return;
			}
			albv.setDiasEntreVtos(d.intValue());
			return;
		}
		if("diasPago".equalsIgnoreCase(title)) {
			if(o.toString().length() > 8) {
				di.getError().getTextWarning().add("WARNING! ALBV: linea " + row + " columna " + column + " - " + title + " Longitud erronea > 8");
			} else albv.setDiasPago(o.toString());
			return;
		}
		if("segmento".equalsIgnoreCase(title)) {
			albv.setSegmento(cell.getStringCellValue());
			return;
		}
	}

	private Boolean hasAlbvDetRequiredParameters() {
		return albvDet.getAlbv() != null && albvDet.getLinea() != null
			&& albvDet.getArticulo() != null && albvDet.getConcepto() != null
			&& albvDet.getCantidad() != null && albvDet.getPrecio() != null;
	}

	private void checkAlbvDet(String title, Cell cell) {
		Object o = getObjectValue(cell);
		if(o == null) return;
		String column = getColumn(cell.getColumnIndex());
		Integer row = cell.getRowIndex() + 1;
		if("albv".equalsIgnoreCase(title)) {
			Double d = -1.0;
			try {
				d = Double.parseDouble(o.toString());
			} catch (Exception e) {
				di.getError().getTextError().add("ERROR! ALBV: linea " + row + " columna " + column + " - " + title + " El valor introducido no es num�rico.");
				di.getError().setError(false);
				return;
			}
			albvDet.setAlbv(d.intValue());
			return;
		}
		if("linea".equalsIgnoreCase(title)) {
			Double d = -1.0;
			try {
				d = Double.parseDouble(o.toString());
			} catch (Exception e) {
				di.getError().getTextError().add("ERROR! ALBV: linea " + row + " columna " + column + " - " + title + " El valor introducido no es num�rico.");
				di.getError().setError(false);
				return;
			}
			albvDet.setLinea(d.intValue());
			return;
		}
		if("articulo".equalsIgnoreCase(title)) {
			if(o.toString().length() > 15) {
				di.getError().getTextWarning().add("WARNING! ALBVDET: linea " + row + " columna " + column + " - " + title + " Longitud erronea > 15");
				albvDet.setArticulo(o.toString().substring(0,15));
			} else albvDet.setArticulo(o.toString());
			return;
		}
		if("detalle".equalsIgnoreCase(title)) {
			if(o.toString().length() > 15) {
				di.getError().getTextWarning().add("WARNING! ALBVDET: linea " + row + " columna " + column + " - " + title + " Longitud erronea > 15");
			} else albvDet.setDetalle(o.toString());
			return;
		}
		if("detalle2".equalsIgnoreCase(title)) {
			if(o.toString().length() > 15) {
				di.getError().getTextWarning().add("WARNING! ALBVDET: linea " + row + " columna " + column + " - " + title + " Longitud erronea > 15");
			} else albvDet.setDetalle2(o.toString());
			return;
		}
		if("detalle3".equalsIgnoreCase(title)) {
			if(o.toString().length() > 15) {
				di.getError().getTextWarning().add("WARNING! ALBVDET: linea " + row + " columna " + column + " - " + title + " Longitud erronea > 15");
			} else albvDet.setDetalle3(o.toString());
			return;
		}
		if("concepto".equalsIgnoreCase(title)) {
			if(o.toString().length() > 64) {
				di.getError().getTextWarning().add("WARNING! ALBVDET: linea " + row + " columna " + column + " - " + title + " Longitud erronea > 64");
			} else albvDet.setConcepto(o.toString());
			return;
		}
		if("cantidad".equalsIgnoreCase(title)) {
			Double d = -1.0;
			try {
				d = Double.parseDouble(o.toString());
			} catch (Exception e) {
				di.getError().getTextError().add("ERROR! ALBV: linea " + row + " columna " + column + " - " + title + " El valor introducido no es num�rico.");
				di.getError().setError(false);
				return;
			}
			albvDet.setCantidad(d);
			return;
		}
		if("precio".equalsIgnoreCase(title)) {
			Double d = -1.0;
			try {
				d = Double.parseDouble(o.toString());
			} catch (Exception e) {
				di.getError().getTextError().add("ERROR! ALBV: linea " + row + " columna " + column + " - " + title + " El valor introducido no es num�rico.");
				di.getError().setError(false);
				return;
			}
			if(Double.isNaN(d)){

			} else albvDet.setPrecio(d);
			return;
		}
		if("precioCoste".equalsIgnoreCase(title)) {
			if(o.toString().length() == 0) return;
			Double d = -1.0;
			try {
				d = Double.parseDouble(o.toString());
			} catch (Exception e) {
				di.getError().getTextWarning().add("WARNING! ALBV: linea " + row + " columna " + column + " - " + title + " El valor introducido no es num�rico.");
				return;
			}
			albvDet.setPrecioCoste(d);
			return;
		}
		if("descuentos".equalsIgnoreCase(title)) {
			if(o.toString().length() > 16) {
				di.getError().getTextWarning().add("WARNING! ALBVDET: linea " + row + " columna " + column + " - " + title + " Longitud erronea > 16");
			} else albvDet.setConcepto(o.toString());
			return;
		}
		if("iva".equalsIgnoreCase(title)) {
			Double d = -1.0;
			try {
				d = Double.parseDouble(o.toString());
			} catch (Exception e) {
				di.getError().getTextError().add("ERROR! ALBV: linea " + row + " columna " + column + " - " + title + " El valor introducido no es num�rico.");
				di.getError().setError(false);
				return;
			}
			albvDet.setIva(d);
			return;
		}
	}

	private HashMap<String, Integer> importClientes(Domain domain, User user) {
		HashMap<String, Integer> map = new HashMap<>();
		Integer[] scps = AON.getUserScopes(domain.getName(), domain.getId(), user.getLogin(), user.getId());
		if(scps == null) scps =  AON.getUserScopes(domain.getName(), user.getDomain(), user.getLogin(), user.getId());
		Integer scope = scps != null ? scps[0] : null;
		di.getClientList().stream().forEach(r -> {
			Customer customer = AON.getCustomer(domain.getName(), domain.getId(), user.getLogin(), f -> f.getDomainProperty().eq(domain.getId()).and(f.getDocumentProperty().eq(r.getDocumento())));

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
						.setDeliveryGrouped(r.getFacturarAlbaranesAgrupados() != null ? r.getFacturarAlbaranesAgrupados().byteValue() : 1)
						.setDeliveryValuated((byte) 1)
						.setProjectGrouped((byte) 1)
						.seteInvoice((byte) 0);

				AON.insertCustomer(domain.getName(), domain.getId(), user.getLogin(), customer);

				if(r.getAliasDireccion() != null) {
					RAddress address = new RAddress()
						.setType((byte)0)
						.setRegistry(registry.getId())
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


				if(r.getTelefono1() != null) {
					RegistryMedia rm = new RegistryMedia()
							.setDomain(domain.getId())
							.setRegistry(new Registry().setId(registry.getId()))
							.setMedia(MediaType.FIXED_PHONE.value())
							.setValue(r.getTelefono1());
					AON.insertRMedia(domain.getName(), domain.getId(), user.getLogin(), rm);
				}

				if(r.getTelefono2() != null) {
					RegistryMedia rm = new RegistryMedia()
							.setDomain(domain.getId())
							.setRegistry(new Registry().setId(registry.getId()))
							.setMedia(MediaType.FIXED_PHONE.value())
							.setValue(r.getTelefono2());
					AON.insertRMedia(domain.getName(), domain.getId(), user.getLogin(), rm);
				}

				if(r.getFax() != null) {
					RegistryMedia rm = new RegistryMedia()
							.setDomain(domain.getId())
							.setRegistry(new Registry().setId(registry.getId()))
							.setMedia(MediaType.FAX.value())
							.setValue(r.getFax());
					AON.insertRMedia(domain.getName(), domain.getId(), user.getLogin(), rm);
				}

				if(r.getEmail() != null) {
					RegistryMedia rm = new RegistryMedia()
							.setDomain(domain.getId())
							.setRegistry(new Registry().setId(registry.getId()))
							.setMedia(MediaType.EMAIL.value())
							.setValue(r.getTelefono1());
					AON.insertRMedia(domain.getName(), domain.getId(), user.getLogin(), rm);
				}

				if(r.getWeb() != null) {
					RegistryMedia rm = new RegistryMedia()
							.setDomain(domain.getId())
							.setRegistry(new Registry().setId(registry.getId()))
							.setMedia(MediaType.WEB.value())
							.setValue(r.getWeb());
					AON.insertRMedia(domain.getName(), domain.getId(), user.getLogin(), rm);
				}

				if(r.getCuentaBanco() != null) {
					BankAccount bankAccount = new BankAccount(r.getCuentaBanco()); 
					RegistryBank rbank = new RegistryBank()
							.setDomain(domain.getId())
							.setRegistry(registry.getId())
							.setBankAccount(bankAccount)
							.setBic(r.getBic())
							.setSuffix("")
							.setAlias(r.getBanco())
							.setActive(true)
							.setAccount(customer.getAccount());
					AON.insertRBank(domain.getName(), domain.getId(), user.getLogin(), rbank);
				}

				if(r.getFormaPago() != null) {
					PayMethod pm = AON.getPayMethod(domain.getName(), domain.getId(), user.getLogin(), r.getFormaPago());
					if(domain.isEnableHeredity() && pm.getId() == null) pm = AON.getPayMethod(domain.getName(), domain.getParentId(), user.getLogin(), r.getFormaPago());
					if(pm.getId() != null) {
						RegistryPayMethod rpaymethod = new RegistryPayMethod()
								.setDomain(domain.getId())
								.setRegistry(registry.getId())
								.setPayMethod(pm.getId())
								.setNumberOfPymnts(r.getNumeroVtos() != null ? r.getNumeroVtos().shortValue(): 1)
								.setDaysToFirstPymnt(r.getDiasAlPrimerVto() != null ? r.getDiasAlPrimerVto().shortValue() : 0)
								.setDaysBetwenPymnts(r.getDiasEntreVtos() != null ? r.getDiasEntreVtos().shortValue() : 0)
								.setPymnt_days(r.getDiasPago() != null ? getDiasPago(r.getDiasPago())  : "");
						AON.insertRPayMethod(domain.getName(), domain.getId(), user.getLogin(), rpaymethod);
					}
				}
			}

			map.put(r.getDocumento(), customer.getRegistry().getId());
		});
		return map;
	}

	private HashMap<Integer, Delivery> importAlbv(Domain domain, User user, HashMap<String, Integer> clientes) {
		HashMap<Integer, Delivery> map = new HashMap<>();
		Integer[] scps = AON.getUserScopes(domain.getName(), domain.getId(), user.getLogin(), user.getId());
		if(scps == null) scps =  AON.getUserScopes(domain.getName(), user.getDomain(), user.getLogin(), user.getId());
		Integer scope = scps != null ? scps[0] : null;
		di.getAlbvList().stream().forEach(r -> {
			Delivery delivery = AON.getDelivery(domain.getName(), domain.getId(), user.getLogin(), f ->
					f.getDomainProperty().eq(domain.getId())
					.and(f.getNumberProperty().eq(r.getNumero()))
					.and(f.getSeriesProperty().eq(r.getSerie())));
			if(delivery.getId() == null) {
				Integer customerID = null;
				Integer raddress = null;
				if(clientes.containsKey(r.getDocumento())) {
					customerID = clientes.get(r.getDocumento());
					if(r.getAliasDireccion() != null) {
						RAddress address = AON.getRAddress(domain.getName(), domain.getId(), user.getLogin(), f -> f.getAliasProperty().eq(r.getAliasDireccion()).and(f.getRegistryProperty().eq(clientes.get(r.getDocumento()))));
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
									.setNumber(r.getNumeroDir())
									.setStreet_type(r.getTipoVia())
									.setZip(r.getCp());
							address = AON.insertRAddress(domain.getName(), domain.getId(), user.getLogin(), address);
						}
						if(address != null) raddress = address.getId();
					}
				}
				Warehouse warehouse = AON.getWarehouse(domain.getName(), domain.getId(), user.getLogin(), f -> f.getDomainProperty().eq(domain.getId()).and(f.getNameProperty().eq(r.getAlmacen())));
				if(warehouse == null) {
					di.getError().getTextError().add("ERROR! ALBV: El almac�n " + r.getAlmacen() + " del albar�n " + r.getSerie() + "/" + r.getNumero()  + " no existe.");
					di.getError().setError(false);
				} else {
					PayMethod pm = new PayMethod();
					Project p = new Project();
					if(r.getFormaPago() != null) pm = AON.getPayMethod(domain.getName(), domain.getId(), user.getLogin(), r.getFormaPago());
					if(domain.isEnableHeredity() && pm.getId() == null) pm = AON.getPayMethod(domain.getName(), domain.getParentId(), user.getLogin(), r.getFormaPago());
					if(r.getExpediente() != null) {
						p = AON.getProject(domain.getName(), domain.getId(), user.getLogin(), f -> f.getDomainProperty().eq(domain.getId()).and(f.getNameProperty().eq(r.getExpediente())));
						if(p.getId() == null) {
							p = new Project().setActive(true)
									.setAlias("")
									.setDate(new Date())
									.setDomain(domain.getId())
									.setName(r.getExpediente())
									.setRegistryId(customerID);
							Integer id = AON.insertProject(domain.getName(), domain.getId(), user.getLogin(), p);
							p.setId(id);
						}
					}

					delivery = new Delivery()
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
							.setBic(r.getBic())
							.setBankAlias(r.getBanco())
							.setPayMethod(pm.getId())
							.setNumberOfPymnts(r.getNumeroVtos() != null ? r.getNumeroVtos().shortValue(): 1)
							.setDaysToFirstPymnt(r.getDiasAlPrimerVto() != null ? r.getDiasAlPrimerVto().shortValue() : 0)
							.setDaysBetweenPymnt(r.getDiasEntreVtos() != null ? r.getDiasEntreVtos().shortValue() : 0)
							.setPymntDays(r.getDiasPago() != null ? getDiasPago(r.getDiasPago()) : "")
							.setTotalPackages(0.0)
							.setTotalWeight(0.0)
							.setProject(p);
					delivery = AON.insertDelivery(domain.getName(), domain.getId(), user.getLogin(), delivery);

					map.put(r.getId(), new Delivery().setId(delivery.getId()).setNumber(warehouse.getId()));
				}
			} else {
				di.getError().getTextError().add("ERROR! ALBV: El albar�n de venta " + r.getSerie() + "/" + r.getNumero()  + " ya existe.");
				di.getError().setError(false);
			}
		});
		return map;
	}

	private String getDiasPago(String dp){
		try {
			StringTokenizer strTknzr = new StringTokenizer(dp, " ");
    		for (int i = 0; i < strTknzr.countTokens(); i++){
    			Integer val = Integer.parseInt(strTknzr.nextToken());
    			if(val == 0) return "";
    		}
    		return dp;
		} catch (Exception e) {
			return "";
		}
	}


	private void importAlbvDet(Domain domain, User user, HashMap<Integer, Delivery> albv) {
		di.getAlbvDetList().stream().forEach(r -> {
			if(albv.containsKey(r.getAlbv())) {
				Product product =  AON.getProduct(domain.getName(), domain.getId(), user.getLogin(), f -> f.getDomainProperty().eq(domain.getId()).and(f.getCodeProperty().eq(r.getArticulo())));

				if(product.getId() == null) {
					Tax vat = DBProduct.getIVAName(domain.getName(), domain.getId(), user.getLogin(), r.getIva() != null ? r.getIva() : 21.0);
					product = new Product()
						.setDomain(domain.getId())
						.setName(r.getConcepto())
						.setCode(r.getArticulo())
						.setKind((byte) 2)
						.setType(ProductType.COMMERCIAL_PRODUCT.value())
						.setVat(vat.getId() != null ? vat.getId() : null)
						.setInventoriable(false)
						.setComposition(false)
						.setCompositionPrice(false)
						.setPackaged(false)
						.setStatus(ProductStatus.ACTIVE.value());
					product = AON.insertProduct(domain.getName(), domain.getId(), user.getLogin(), product);
				}
				Integer productId = product.getId();

				Item item = AON.getItem(domain.getName(), domain.getId(), user.getLogin(), f -> f.getProductProperty().eq(productId)
					.and(r.getDetalle() != null ? f.getDetailProperty().eq(r.getDetalle()) :
						f.getDetailProperty().eq("").or(f.getDetailProperty().isNull()))
					.and(r.getDetalle2() != null ? f.getDetail2Property().eq(r.getDetalle2()) :
						f.getDetail2Property().eq("").or(f.getDetail2Property().isNull()))
					.and(r.getDetalle3() != null ? f.getDetail3Property().eq(r.getDetalle3()):
						f.getDetail3Property().eq("").or(f.getDetail3Property().isNull())));

				if(item.getId() == null) {
					item = new Item()
						.setDomain(domain.getId())
						.setActive(true)
						.setProduct(product)
						.setProductId(productId)
						.setDetail(r.getDetalle())
						.setDetail2(r.getDetalle2())
						.setDetail3(r.getDetalle3())
						.setDescription(r.getConcepto())
						.setPrice(r.getPrecio())
						.setPurchasePrice(r.getPrecioCoste() != null ? r.getPrecioCoste() : 0.0);
					item = AON.insertItem(domain.getName(), domain.getId(), user.getLogin(), item);
				}

				DeliveryDetail dd = new DeliveryDetail()
					.setDomain(domain.getId())
					.setDelivery(albv.get(r.getAlbv()))
					.setWarehouse(albv.get(r.getAlbv()).getNumber())
					.setLine(r.getLinea().shortValue())
					.setItem(item)
					.setDescription(r.getConcepto())
					.setQuantity(r.getCantidad())
					.setPrice(r.getPrecio())
					.setDiscountExpression(r.getDescuentos() != null ? r.getDescuentos() : "0.0");
				AON.insertDeliveryDetail(domain.getName(), domain.getId(), user.getLogin(), dd);
			}
		});
	}

	private String getColumn(Integer c) {
		if(c == 0) return "A";
		if(c == 1) return "B";
		if(c == 2) return "C";
		if(c == 3) return "D";
		if(c == 4) return "E";
		if(c == 5) return "F";
		if(c == 6) return "G";
		if(c == 7) return "H";
		if(c == 8) return "I";
		if(c == 9) return "J";
		if(c == 10) return "K";
		if(c == 11) return "L";
		if(c == 12) return "M";
		if(c == 13) return "N";
		if(c == 14) return "O";
		if(c == 15) return "P";
		if(c == 16) return "Q";
		if(c == 17) return "R";
		if(c == 18) return "S";
		if(c == 19) return "T";
		if(c == 20) return "U";
		if(c == 21) return "V";
		if(c == 22) return "W";
		if(c == 23) return "X";
		if(c == 24) return "Y";
		if(c == 25) return "Z";
		if(c == 26) return "AA";
		if(c == 27) return "AB";
		if(c == 28) return "AC";
		if(c == 29) return "AD";
		if(c == 30) return "AE";
		if(c == 31) return "AF";
		if(c == 32) return "AG";
		if(c == 33) return "AH";
		if(c == 34) return "AI";
		if(c == 35) return "AJ";
		if(c == 36) return "AK";
		if(c == 37) return "AL";
		if(c == 38) return "AM";
		if(c == 39) return "AN";
		if(c == 40) return "AO";
		if(c == 41) return "AP";
		if(c == 42) return "AQ";
		if(c == 43) return "AR";
		if(c == 44) return "AS";
		if(c == 45) return "AT";
		if(c == 46) return "AU";
		if(c == 47) return "AV";
		if(c == 48) return "AW";
		if(c == 49) return "AX";
		if(c == 50) return "AY";
		if(c == 51) return "AZ";
		return c.toString();
	}
}
