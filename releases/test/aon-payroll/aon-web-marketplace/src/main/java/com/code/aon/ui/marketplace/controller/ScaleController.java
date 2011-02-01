package com.code.aon.ui.marketplace.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.temp.SeriesNumberUtil;
import com.code.aon.company.WorkPlace;
import com.code.aon.customer.Customer;
import com.code.aon.marketplace.Scale;
import com.code.aon.marketplace.ScaleRelation;
import com.code.aon.marketplace.dao.IMarketplaceAlias;
import com.code.aon.marketplace.enumeration.ScaleModel;
import com.code.aon.marketplace.plu.BokaData;
import com.code.aon.marketplace.plu.BokaIf;
import com.code.aon.marketplace.plu.FieldUtils;
import com.code.aon.marketplace.plu.HwstData;
import com.code.aon.marketplace.plu.HwstIf;
import com.code.aon.marketplace.plu.PlstData;
import com.code.aon.marketplace.plu.PlstIf;
import com.code.aon.marketplace.plu.PluItem;
import com.code.aon.marketplace.plu.PluItemManager;
import com.code.aon.marketplace.plu.PluItemSet;
import com.code.aon.marketplace.plu.Ticket;
import com.code.aon.marketplace.plu.TicketManager;
import com.code.aon.marketplace.plu.TicketSet;
import com.code.aon.marketplace.plu.WgstData;
import com.code.aon.marketplace.plu.WgstIf;
import com.code.aon.product.Item;
import com.code.aon.product.ItemPos;
import com.code.aon.product.Product;
import com.code.aon.product.ProductCategory;
import com.code.aon.product.ProductCategoryGroup;
import com.code.aon.product.Tax;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.sales.PointOfSale;
import com.code.aon.sales.Sales;
import com.code.aon.sales.SalesDetail;
import com.code.aon.sales.enumeration.SalesDetailStatus;
import com.code.aon.sales.enumeration.SalesStatus;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.product.util.ItemPriceProvider;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.Warehouse;
import com.code.aon.warehouse.enumeration.DeliveryStatus;

/*
 * BIZERBA NOTES
 * 
 * CONFIG & INFO FILES TO CREATE ASCII FILES TO MACHINE /IF/*_tm.if
 * CONFIG & INFO FILES TO READ ASCII FILES FROM MACHINE /IF/*_mt.if
 * 
 * ASCCI OUTPUT FILES TO MACHINE /Ascii/*_TM.TXT
 * ASCCI INPUT FILES FROM MACHINE /Ascii/*_MT.TXT
 * 
 */
public class ScaleController extends ExtendedScaleController {

	private static final String DATEFORMAT = "dd/MM/yyyy";
	Locale locale = new Locale("en","EN");
	private List<SelectItem> scaleModels;
	private List<SelectItem> euroscaleTiendas;
	private List<SelectItem> euroscaleSeccion;
	private List<SelectItem> customers;
	private List<SelectItem> pos;
	private List<HwstData> listBizerbaCategory; 
	private int maxBizerbaCategory = 0;
	private List<WgstData> listBizerbaSubCategory; 
	private int maxBizerbaSubCategory = 0;
	private List<PlstData> listBizerbaItem; 
	
	private Sales sales;
	
	@SuppressWarnings("unused")
	public void onTest(ActionEvent event) {
		if (testConnection(false)) onAccept(event);
		else AonUtil.addErrorMessage("ERROR: Se produjo un error en la comprobación, revise la ruta y pulse grabar."); 
	}

	//CATEGORIES
	@SuppressWarnings("unused")
	public void onGetCategory(ActionEvent event) {
		onAccept(event);
		try {
	        ArrayList<List> categories = new ArrayList<List>();

			if (getScale().getScaleModel() == ScaleModel.BIZERBA) {
				try {
					categories = getCategoryFromText();
				} catch (IOException e) {
					AonUtil.addErrorMessage(" ERR: Se produjo un error intentando acceder a los archivos de datos.");
				}
			}
			else {
				if (getScale().getScaleModel() == ScaleModel.DIBAL || getScale().getScaleModel() == ScaleModel.EUROSCALE) {
					categories = getCategoryFromDatabase();
				}
				else return;
			}

			Iterator<List> iter = categories.iterator();
			if (!iter.hasNext()) AonUtil.addWarningMessage(" INFO: No se han encontrado categorias."); 
			while (iter.hasNext()){
				List category = iter.next();
				String family = "";
				String subfamily = "";
				try {
					int fam = Integer.parseInt("" + category.get(0));
					family = "" + category.get(1);
					int subfam = Integer.parseInt("" + category.get(2));
					subfamily = "" + category.get(3);
					if (family == null || family.trim().equals("")) family = "" + fam;
					if (subfamily == null || subfamily.trim().equals("")) {
						subfamily = "" + fam + " - " + subfam;
					}
					if (existsCategory(fam, subfam)) {
						AonUtil.addInfoMessage(" INFO: La categoria " + family + " - " + subfamily + " ya existe.");
					}
					else {
						try {
							if (existsFamily(fam)) {
								createCategoryInFamily(fam, subfam, subfamily);
								AonUtil.addInfoMessage(" INS: La categoria " + family + " - " + subfamily + " ha sido introducida con exito.");
							}
							else {
								createFamily(fam, family);
								AonUtil.addInfoMessage(" INS: La familia " + family + " ha sido introducida con exito.");
								createCategoryInFamily(fam, subfam, subfamily);
								AonUtil.addInfoMessage(" INS: La categoria " + family + " - " + subfamily + " ha sido introducida con exito.");
							}
						}
						catch (Exception e) {
							e.printStackTrace();
							AonUtil.addErrorMessage(" ERR: Se produjo un error en la insercion de la categoria " + family + " - " + subfamily + "");
							if (e.getMessage() != null) AonUtil.addWarningMessage(e.getMessage());
						}
					}
				}
				catch (Exception e) {
					e.printStackTrace();
					AonUtil.addErrorMessage(" ERR: Se produjo un error en la localizacion de la categoria " + family + " - " + subfamily + "");
		    		if (e.getMessage() != null) AonUtil.addWarningMessage(e.getMessage());
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			AonUtil.addErrorMessage(" ERR: Se produjo un error durante la conexion a la base de datos: '" + getDatabasePath(getScale()) + "'");
    		if (e.getMessage() != null) AonUtil.addWarningMessage(e.getMessage());
		}
	}

	public ArrayList<List> getCategoryFromDatabase() throws SQLException {
		String database = getDatabasePath(getScale());
		String select = getScale().getScaleModel().getSelectCategory(locale);
		Connection c = getConnection(database);
		PreparedStatement ps = c.prepareStatement(select);
	    ResultSet rs = ps.executeQuery();
        ArrayList<List> categories = new ArrayList<List>();
	    while (rs.next()) {
			List<String> l = new ArrayList<String>();
			/*SELECT codi_sub, "", codi_fam, "" FROM familia WHERE borrado = 0*/
			l.add(""+rs.getInt(1));
			l.add(rs.getString(2));
			l.add(""+rs.getInt(3));
			l.add(rs.getString(4));
			categories.add(l);
		}
	    rs.close();
	    ps.close();
	    c.close();
	    return categories;
	}

	public ArrayList<List> getCategoryFromText() throws SQLException, IOException {
        ArrayList<List> categories = new ArrayList<List>();
		//Montamos un hashmap con los HWST (category_group)
		HashMap map = new HashMap();
		//Abrimos el fichero a leer en este caso el HWST donde al menos tiene que estar HWGN(2) y HWGT (21)
		File f = new File(getScale().getProgramPath() + "IF/Hwst_mt.if");
		if (f.exists()) {
			HwstIf hwst = getHwstFieldsZiel(f);
			if (hwst.isValid()) {
				f = new File(getScale().getProgramPath() + "Ascii/HWST_MT.TXT");
				if (f.exists()) {
					map = getHwstData(f, hwst);
				}
				else {
					AonUtil.addErrorMessage(" ERROR: No existe el fichero de datos de familias: '" + getScale().getProgramPath() + "Ascii/HWST_MT.TXT'");
				}
			}
			else {
				AonUtil.addErrorMessage(" ERROR: Falta algun campo en el fichero de configuracion de familias (HWGN y HWGT son requeridos): '" + getScale().getProgramPath() + "IF/Hwst_mt.if'");
			}
		}
		else {
			AonUtil.addErrorMessage(" ERROR: No existe el fichero de configuracion de familias: '" + getScale().getProgramPath() + "IF/Hwst_mt.if'");
		}
		ArrayList<WgstData> wgstList = new ArrayList<WgstData>();
		if (!map.isEmpty()) {
			//Abrimos el fichero a leer en este caso el HGST donde al menos tiene que estar WGNU(4), HWGN (2) y WGTE (21)
			f = new File(getScale().getProgramPath() + "IF/Wgst_mt.if");
			if (f.exists()) {
				WgstIf wgst = getWgstFieldsZiel(f);
				if (wgst.isValid()) {
					f = new File(getScale().getProgramPath() + "Ascii/WGST_MT.TXT");
					if (f.exists()) {
						//OBTENER SUBFAMILIAS
						wgstList = getWgstData(f, wgst);
					}
					else {
						AonUtil.addErrorMessage(" ERROR: No existe el fichero de datos de familias: '" + getScale().getProgramPath() + "Ascii/WGST_MT.TXT'");
					}
				}
				else {
					AonUtil.addErrorMessage(" ERROR: Falta algun campo en el fichero de configuracion de familias (WGNU, HWGT y WGTE son requeridos): '" + getScale().getProgramPath() + "IF/Wgst_mt.if'");
				}
			}
			else {
				AonUtil.addErrorMessage(" ERROR: No existe el fichero de configuracion de familias: '" + getScale().getProgramPath() + "IF/Wgst_mt.if'");
			}
		}
		else {
			AonUtil.addErrorMessage(" ERROR: No se han encontrado familias en el fichero: '" + getScale().getProgramPath() + "Ascii/WGST_MT.TXT'");
		}
		/*codigo de categoria, nombre de categoria, codigo de familia, nombre de familia*/
		for (int i=0;i<wgstList.size();i++) {
			WgstData wd = wgstList.get(i);
			String family = "" + map.get(wd.getHWGN());
			List<String> l = new ArrayList<String>();
			l.add("" + wd.getHWGN()); 
			l.add(family);
			l.add("" + wd.getWGNU());
			l.add(wd.getWGTE());
			categories.add(l);
		}
	    return categories;
	}

	@SuppressWarnings("unused")
	public void onSetCategory(ActionEvent event) throws ManagerBeanException, SQLException {
		onAccept(event);
		IManagerBean pcgBean = BeanManager.getManagerBean(ProductCategoryGroup.class);
		List<ITransferObject> pcgLst = pcgBean.getList(null);
		ProductCategoryGroup pcg = new ProductCategoryGroup();
		if (getScale().getScaleModel() == ScaleModel.BIZERBA) initBizerba();
		for (int i=0;i < pcgLst.size();i++) {
			pcg = (ProductCategoryGroup)pcgLst.get(i);
			try {
				int id = pcg.getId();
				String desc = pcg.getName();
				if (existsScaleCategory(id)) {
					try {
						updateScaleCategory(id, desc);
						AonUtil.addInfoMessage(" BLCACT: La familia " + id + " ha sido actualizada con exito.");
					}
					catch (Exception e) {
						e.printStackTrace();
						AonUtil.addErrorMessage(" BLCERR: Se produjo un error en la actualizacion de la familia " + id + "");
			    		if (e.getMessage() != null) AonUtil.addWarningMessage(e.getMessage());
					}
				}
				else {
					try {
						createScaleCategory(id, desc);
						AonUtil.addInfoMessage(" BLCINS: La familia " + id + " ha sido introducida con exito.");
					}
					catch (Exception e) {
						e.printStackTrace();
						AonUtil.addErrorMessage(" BLCERR: Se produjo un error en la insercion de la familia " + id + "");
			    		if (e.getMessage() != null) AonUtil.addWarningMessage(e.getMessage());
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				AonUtil.addErrorMessage(" BLCERR: Se produjo un error en la insercion de la familia " + pcg.getId() + "");
	    		if (e.getMessage() != null) AonUtil.addWarningMessage(e.getMessage());
			}

			IManagerBean pcBean = BeanManager.getManagerBean(ProductCategory.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(pcBean.getFieldName(IProductAlias.PRODUCT_CATEGORY_CATEGORY_GROUP_ID), pcg.getId());
			List<ITransferObject> pcLst = pcBean.getList(criteria);
			ProductCategory pc = new ProductCategory();
			for (int j=0;j < pcLst.size();j++) {
				pc = (ProductCategory)pcLst.get(j);
				try {
					int id = pc.getId();
					String desc = pc.getName();
					if (existsScaleSubcategory(pcg.getId(), id)) {
						try {
							updateScaleSubcategory(pcg.getId(), id, desc);
							AonUtil.addInfoMessage(" BLCACT: La subfamilia " + id + " ha sido actualizada con exito.");
						}
						catch (Exception e) {
							e.printStackTrace();
							AonUtil.addErrorMessage(" BLCERR: Se produjo un error en la actualizacion de la subfamilia " + id + "");
				    		if (e.getMessage() != null) AonUtil.addWarningMessage(e.getMessage());
						}
					}
					else {
						try {
							createScaleSubcategory(pcg.getId(), id, desc);
							AonUtil.addInfoMessage(" BLCINS: La subfamilia " + id + " ha sido introducida con exito.");
						}
						catch (Exception e) {
							e.printStackTrace();
							AonUtil.addErrorMessage(" BLCERR: Se produjo un error en la insercion de la subfamilia " + id + "");
				    		if (e.getMessage() != null) AonUtil.addWarningMessage(e.getMessage());
						}
					}
				}
				catch (Exception e) {
					e.printStackTrace();
					AonUtil.addErrorMessage(" BLCERR: Se produjo un error en la insercion de la subfamilia " + pcg.getId() + "");
		    		if (e.getMessage() != null) AonUtil.addWarningMessage(e.getMessage());
				}
			}
		}
		if (getScale().getScaleModel() == ScaleModel.BIZERBA) {
			createBizerbaCategoryFile();
			createBizerbaSubCategoryFile();
		}
	}

	private void initBizerba() {
		listBizerbaCategory = new ArrayList<HwstData>();
		maxBizerbaCategory = 0;
		listBizerbaSubCategory = new ArrayList<WgstData>();
		maxBizerbaSubCategory = 0;
		listBizerbaItem = new ArrayList<PlstData>();
	}

	private void createBizerbaCategoryFile() {
		File f = new File(getScale().getProgramPath() + "IF/Hwst_tm.if");
		if (f.exists()) {
			try {
				HwstIf hwst = getHwstFieldsQuelle(f);
				if (hwst.isValid()) {
					f = new File(getScale().getProgramPath() + "Ascii/HWST_TM.TXT");
					if (f.exists()) f.delete();
					PrintWriter pw = new PrintWriter(f);
					for (int i=0;i < listBizerbaCategory.size(); i++) {
						HwstData hwstData = listBizerbaCategory.get(i);
						pw.print(hwstData.getLine(hwst));
					}
					pw.flush();
					pw.close();
				}
				else {
					AonUtil.addErrorMessage(" ERROR: Falta algun campo en el fichero de configuracion de familias (HWGN y HWGT son requeridos): '" + getScale().getProgramPath() + "IF/Hwst_tm.if'");
				}
			}
			catch (IOException e) {
				AonUtil.addErrorMessage(" ERROR: Se produjo un error intentando acceder al los archivos de datos.");
			}
		}
		else {
			AonUtil.addErrorMessage(" ERROR: No existe el fichero de configuracion de familias: '" + getScale().getProgramPath() + "IF/Hwst_tm.if'");
		}
	}

	private void createBizerbaSubCategoryFile() {
		File f = new File(getScale().getProgramPath() + "IF/Wgst_tm.if");
		if (f.exists()) {
			try {
				WgstIf wgst = getWgstFieldsQuelle(f);
				if (wgst.isValid()) {
					f = new File(getScale().getProgramPath() + "Ascii/WGST_TM.TXT");
					if (f.exists()) f.delete();
					PrintWriter pw = new PrintWriter(f);
					for (int i=0;i < listBizerbaSubCategory.size(); i++) {
						WgstData wgstData = listBizerbaSubCategory.get(i);
						pw.print(wgstData.getLine(wgst));
					}
					pw.flush();
					pw.close();
				}
				else {
					AonUtil.addErrorMessage(" ERROR: Falta algun campo en el fichero de configuracion de familias (WGNU, HWGN y WGTE son requeridos): '" + getScale().getProgramPath() + "IF/Wgst_tm.if'");
				}
			}
			catch (IOException e) {
				AonUtil.addErrorMessage(" ERROR: Se produjo un error intentando acceder al los archivos de datos.");
			}
		}
		else {
			AonUtil.addErrorMessage(" ERROR: No existe el fichero de configuracion de familias: '" + getScale().getProgramPath() + "IF/Wgst_tm.if'");
		}
	}

	private void createBizerbaItemFile() {
		File f = new File(getScale().getProgramPath() + "IF/Plst_tm.if");
		if (f.exists()) {
			try {
				PlstIf plst = getPlstFieldsQuelle(f);
				if (plst.isValid()) {
					f = new File(getScale().getProgramPath() + "Ascii/PLST_TM.TXT");
					if (f.exists()) f.delete();
					PrintWriter pw = new PrintWriter(f);
					for (int i=0;i < listBizerbaItem.size(); i++) {
						PlstData plstData = listBizerbaItem.get(i);
						pw.print(plstData.getLine(plst));
					}
					pw.flush();
					pw.close();
				}
				else {
					AonUtil.addErrorMessage(" ERROR: Falta algun campo en el fichero de configuracion de articulos (PNUM, GPR1, WGNU, ECO1 y PLTE son requeridos): '" + getScale().getProgramPath() + "IF/Plst_tm.if'");
				}
			}
			catch (IOException e) {
				AonUtil.addErrorMessage(" ERROR: Se produjo un error intentando acceder al los archivos de datos.");
			}
		}
		else {
			AonUtil.addErrorMessage(" ERROR: No existe el fichero de configuracion de articulos: '" + getScale().getProgramPath() + "IF/Plst_tm.if'");
		}
	}

	private boolean existsScaleCategory(int id) throws SQLException, ManagerBeanException, ExpressionException {
    	int scale_id = getScaleCategoryId(id);
        boolean ret = false;
		if (getScale().getScaleModel() == ScaleModel.BIZERBA) {
			if (scale_id > 0) ret = true;
		}
		else {
	    	String database = getDatabasePath(getScale());
	    	String select = getScale().getScaleModel().existsCategory(locale);
			Connection c = getConnection(database);
			PreparedStatement ps = c.prepareStatement(select);
	        if (getScale().getScaleModel() == ScaleModel.EUROSCALE) {
		        int sec = getSec(getScale().getCode2());
	    		ps.setInt(1,sec);
	    		ps.setInt(2,scale_id);
	        }
	        else {
	            if (getScale().getScaleModel() == ScaleModel.DIBAL) {
	        		ps.setInt(1,scale_id);
	            }
	        }
	        ResultSet rs = ps.executeQuery();
	        if (rs.next()) ret = true;
	        rs.close();
	        ps.close();
			c.close();
		}
		return ret;
	}

	private boolean existsScaleSubcategory(int family, int id) throws SQLException, ManagerBeanException, ExpressionException {
    	int scale_id1 = getScaleCategoryId(family);
    	int scale_id2 = getScaleSubcategoryId(family, id);
        boolean ret = false;
		if (getScale().getScaleModel() == ScaleModel.BIZERBA) {
			if (scale_id1 > 0 && scale_id2 > 0) ret = true;
		}
		else {
	    	String database = getDatabasePath(getScale());
	    	String select = getScale().getScaleModel().existsSubcategory(locale);
			Connection c = getConnection(database);
			PreparedStatement ps = c.prepareStatement(select);
	        if (getScale().getScaleModel() == ScaleModel.EUROSCALE) {
		        int sec = getSec(getScale().getCode2());
		        int mos = getMos(getScale().getCode2());
	    		ps.setInt(1,sec);
	    		ps.setInt(2,mos);
	    		ps.setInt(3,scale_id1);
	    		ps.setInt(4,scale_id2);
	        }
	        else {
	            if (getScale().getScaleModel() == ScaleModel.DIBAL) {
	        		ps.setInt(1,scale_id2);
	            }
	        }
	        ResultSet rs = ps.executeQuery();
	        if (rs.next()) ret = true;
	        rs.close();
	        ps.close();
			c.close();
		}
		return ret;
	}

	private void createScaleCategory(int id, String desc) throws SQLException, ManagerBeanException, ExpressionException {
        int newcode = getMaxCategory();
		if (getScale().getScaleModel() == ScaleModel.BIZERBA) {
			listBizerbaCategory.add(new HwstData(newcode, desc));
		}
		else {
	    	String database = getDatabasePath(getScale());
	        String insert = getScale().getScaleModel().getInsertCategory(locale);
			Connection c = getConnection(database);
	        PreparedStatement ps = c.prepareStatement(insert);
	        if (getScale().getScaleModel() == ScaleModel.EUROSCALE) {
		        int sec = getSec(getScale().getCode2());
		        ps.setInt(1, sec);
		        ps.setInt(2, newcode);
	        }
	        else {
	            if (getScale().getScaleModel() == ScaleModel.DIBAL) {
	    	        ps.setInt(1, newcode);
	            	ps.setString(2, desc);
	            }
	        }
	        ps.executeUpdate();
	        ps.close();
	        c.close();
		}
        createFamilyRelation(newcode, id);
	}

	private void createScaleSubcategory(int category, int id, String desc) throws SQLException, ManagerBeanException, ExpressionException {
        int scale_category = getScaleCategoryId(category);
        int newcode = getMaxSubcategory(scale_category);
		if (getScale().getScaleModel() == ScaleModel.BIZERBA) {
			listBizerbaSubCategory.add(new WgstData(newcode, scale_category, desc));
		}
		else {
	    	String database = getDatabasePath(getScale());
	        String insert = getScale().getScaleModel().getInsertSubcategory(locale);
			Connection c = getConnection(database);
	        PreparedStatement ps = c.prepareStatement(insert);
	        if (getScale().getScaleModel() == ScaleModel.EUROSCALE) {
		        int sec = getSec(getScale().getCode2());
		        int mos = getMos(getScale().getCode2());
		        ps.setInt(1, sec);
		        ps.setInt(2, mos);
		        ps.setInt(3, scale_category);
		        ps.setInt(4, newcode);
	        }
	        else {
	            if (getScale().getScaleModel() == ScaleModel.DIBAL) {
	    	        ps.setInt(1, scale_category);
	    	        ps.setInt(2, newcode);
	            	ps.setString(3, desc);
	            }
	        }
	        ps.executeUpdate();
	        ps.close();
	        c.close();
		}
        createCategoryRelation(scale_category, newcode, id);
	}

	private void updateScaleCategory(int id, String desc) throws SQLException, ManagerBeanException, ExpressionException {
    	int scale_id = getScaleCategoryId(id);
		if (getScale().getScaleModel() == ScaleModel.BIZERBA) {
			listBizerbaCategory.add(new HwstData(scale_id, desc));
		}
		else {
	    	String database = getDatabasePath(getScale());
	        String update = getScale().getScaleModel().getUpdateCategory(locale);
			Connection c = getConnection(database);
	        if (getScale().getScaleModel() == ScaleModel.DIBAL) {
	            PreparedStatement ps = c.prepareStatement(update);
		        ps.setString(1, desc);
		        ps.setInt(2, scale_id);
		        ps.executeUpdate();
		        ps.close();
	        }
	        c.close();
		}
	}

	private void updateScaleSubcategory(int family, int id, String desc) throws SQLException, ManagerBeanException, ExpressionException {
    	int scale_id1 = getScaleCategoryId(family);
    	int scale_id2 = getScaleSubcategoryId(family, id);
		if (getScale().getScaleModel() == ScaleModel.BIZERBA) {
			listBizerbaSubCategory.add(new WgstData(scale_id2, scale_id1, desc));
		}
		else {
	    	String database = getDatabasePath(getScale());
	        String update = getScale().getScaleModel().getUpdateSubcategory(locale);
			Connection c = getConnection(database);
	        if (getScale().getScaleModel() == ScaleModel.DIBAL) {
	        	PreparedStatement ps = c.prepareStatement(update);
	        	if (desc.lastIndexOf(" - ") > 0) desc = desc.substring(desc.lastIndexOf(" - ") + 3, desc.length());
		        ps.setString(1, desc);
		        ps.setInt(2, scale_id1);
		        ps.setInt(3, scale_id2);
		        ps.executeUpdate();
		        ps.close();
	        }
	        c.close();
		}
	}

	private int getScaleCategoryId(int id) throws ManagerBeanException, ExpressionException {
		IManagerBean srBean = BeanManager.getManagerBean(ScaleRelation.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(srBean.getFieldName(IMarketplaceAlias.SCALE_RELATION_AON_ID), "" + id);
		criteria.addExpression(srBean.getFieldName(IMarketplaceAlias.SCALE_RELATION_TYPE), "F");
		criteria.addExpression(srBean.getFieldName(IMarketplaceAlias.SCALE_RELATION_SCALE_MODEL), "" + getScale().getScaleModel().ordinal());
		List<ITransferObject> srLst = srBean.getList(criteria);
		ScaleRelation sr = new ScaleRelation();
		if (srLst.size() > 0) {
			sr = (ScaleRelation)srLst.get(0);
			return Integer.parseInt(sr.getScale_id1());
		}
		else return 0;
	}

	private int getScaleSubcategoryId(int family, int id) throws ManagerBeanException, ExpressionException {
		int scale_id1 = getScaleCategoryId(family);
		IManagerBean srBean = BeanManager.getManagerBean(ScaleRelation.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(srBean.getFieldName(IMarketplaceAlias.SCALE_RELATION_AON_ID), "" + id);
		criteria.addExpression(srBean.getFieldName(IMarketplaceAlias.SCALE_RELATION_SCALE_ID1), "" + scale_id1);
		criteria.addExpression(srBean.getFieldName(IMarketplaceAlias.SCALE_RELATION_TYPE), "C");
		criteria.addExpression(srBean.getFieldName(IMarketplaceAlias.SCALE_RELATION_SCALE_MODEL), "" + getScale().getScaleModel().ordinal());
		List<ITransferObject> srLst = srBean.getList(criteria);
		ScaleRelation sr = new ScaleRelation();
		if (srLst.size() > 0) {
			sr = (ScaleRelation)srLst.get(0);
			return Integer.parseInt(sr.getScale_id2());
		}
		else return 0;
	}

	private int getAonCategoryId(int id) throws ManagerBeanException, ExpressionException {
		IManagerBean srBean = BeanManager.getManagerBean(ScaleRelation.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(srBean.getFieldName(IMarketplaceAlias.SCALE_RELATION_SCALE_ID1), "" + id);
		criteria.addExpression(srBean.getFieldName(IMarketplaceAlias.SCALE_RELATION_TYPE), "F");
		criteria.addExpression(srBean.getFieldName(IMarketplaceAlias.SCALE_RELATION_SCALE_MODEL), "" + getScale().getScaleModel().ordinal());
		List<ITransferObject> srLst = srBean.getList(criteria);
		ScaleRelation sr = new ScaleRelation();
		if (srLst.size() > 0) {
			sr = (ScaleRelation)srLst.get(0);
			return sr.getAon_id();
		}
		else return 0;
	}

	private int getAonSubcategoryId(int family, int id) throws ManagerBeanException, ExpressionException {
		ScaleRelation sr = new ScaleRelation();
		IManagerBean srBean = BeanManager.getManagerBean(ScaleRelation.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(srBean.getFieldName(IMarketplaceAlias.SCALE_RELATION_SCALE_ID2), "" + id);
		criteria.addExpression(srBean.getFieldName(IMarketplaceAlias.SCALE_RELATION_TYPE), "C");
		criteria.addExpression(srBean.getFieldName(IMarketplaceAlias.SCALE_RELATION_SCALE_MODEL), "" + getScale().getScaleModel().ordinal());
		if (getScale().getScaleModel() == ScaleModel.BIZERBA) {
			//Nothing
		}
		else {
			criteria.addExpression(srBean.getFieldName(IMarketplaceAlias.SCALE_RELATION_SCALE_ID1), "" + family);
		}
		List<ITransferObject> srLst = srBean.getList(criteria);
		if (srLst.size() > 0) {
			sr = (ScaleRelation)srLst.get(0);
			return sr.getAon_id();
		}
		else return 0;
	}

	// ITEMS
	@SuppressWarnings("unused")
	public void onGetItems(ActionEvent event) throws NumberFormatException, ManagerBeanException, ExpressionException {
		onAccept(event);
		PluItem item;
		PluItemManager im = new PluItemManager();
		try {
			if (getScale().getScaleModel() == ScaleModel.BIZERBA) {
				try {
					im = getItemsFromText();					
				} catch (IOException e) {
					AonUtil.addErrorMessage(" ERR: Se produjo un error intentando acceder a los archivos de datos.");
				}
			}
			else {
				if (getScale().getScaleModel() == ScaleModel.DIBAL || getScale().getScaleModel() == ScaleModel.EUROSCALE) {
					im = getItemsFromDatabase();
				}
				else return;
			}

			Collection<PluItemSet> pisLst = im.getAll();
			Iterator<PluItemSet> iter = pisLst.iterator();
			if (!iter.hasNext()) AonUtil.addWarningMessage(" INFO: No se han encontrado articulos.");
			while (iter.hasNext()){
				PluItemSet pis = iter.next();
				List<PluItem> itemLst = pis.getPluItemlist();
				Iterator<PluItem> itemIter = itemLst.iterator();
				String plu = "";
				while (itemIter.hasNext()){
					try {
						PluItem pi = (PluItem)itemIter.next();
						plu = ""+pi.getPlu();
						if (existsItem(pi)) {
							try {
								updateItem(pi);
								AonUtil.addInfoMessage(" ACT: El artículo con PLU " + plu + " ha sido actualizado con exito.");
							}
							catch (Exception e) {
								AonUtil.addErrorMessage(" ERR: Se produjo un error en la actualizacion del articulo con PLU " + plu + "");
							}
						}
						else {
							try {
								createItem(pi);
								AonUtil.addInfoMessage(" INS: El artículo con PLU " + plu + " ha sido introducido con exito.");
							}
							catch (ManagerBeanException mbe) {
								AonUtil.addErrorMessage(" ERR: Se produjo un error en la insercion del articulo con PLU " + plu + "");
					    		if (mbe.getMessage() != null) {
					    			AonUtil.addWarningMessage(mbe.getMessage());
					    			if (mbe.getMessage().indexOf("category") > 0) AonUtil.addWarningMessage("Puede que no exista la categoria / familia del articulo.");
					    		}
							}
							catch (Exception e) {
								AonUtil.addErrorMessage(" ERR: Se produjo un error en la insercion del articulo con PLU " + plu + "");
					    		if (e.getMessage() != null) AonUtil.addWarningMessage(e.getMessage());
							}
						}
					}
					catch (Exception e) {
						AonUtil.addErrorMessage(" ERR: Se produjo un error en la localizacion del articulo con PLU " + plu + "");
			    		if (e.getMessage() != null) AonUtil.addWarningMessage(e.getMessage());
					}
				}
			}
		} catch (SQLException e) {
			AonUtil.addErrorMessage(" ERR: Se produjo un error durante la conexion a la base de datos: '" + getDatabasePath(getScale()) + "'");
    		if (e.getMessage() != null) AonUtil.addWarningMessage(e.getMessage());
		}
	}

	private PluItemManager getItemsFromDatabase() throws SQLException, NumberFormatException, ManagerBeanException, ExpressionException {
		PluItem item;
		PluItemManager im = new PluItemManager();
    	String database = getDatabasePath(getScale());
		String select = getScale().getScaleModel().getSelectItem(locale);
		Connection c = getConnection(database);
		PreparedStatement ps = c.prepareStatement(select);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
			List<String> l = new ArrayList<String>();
			l.add(""+rs.getInt(1)); //0
			l.add(""+rs.getInt(2)); //1
			l.add(""+rs.getInt(3)); //2
			l.add(""+rs.getInt(4)); //3
			l.add(""+getSec(getScale().getCode2())); //4
			l.add(""+getMos(getScale().getCode2())); //5
			l.add(""+getCat(getScale().getCode3())); //6
			l.add(rs.getString(6)); //7
			l.add(""+rs.getDouble(5)); //8
			l.add(rs.getString(7)); //9
			l.add(rs.getString(8)); //10
			item = parseItem(l);
			im.addItem(item);
		}
        rs.close();
        ps.close();
        c.close();
        return im;
	}

	private PluItemManager getItemsFromText() throws NumberFormatException, ManagerBeanException, ExpressionException, IOException {
		PluItem item;
		PluItemManager im = new PluItemManager();
		ArrayList<PlstData> plstList = new ArrayList<PlstData>();
		File f = new File(getScale().getProgramPath() + "IF/Plst_mt.if");
		if (f.exists()) {
			PlstIf plst = getPlstFieldsZiel(f);
			if (plst.isValid()) {
				f = new File(getScale().getProgramPath() + "Ascii/PLST_MT.TXT");
				if (f.exists()) {
					plstList = getPlstData(f, plst);
				}
				else {
					AonUtil.addErrorMessage(" ERROR: No existe el fichero de datos de familias: '" + getScale().getProgramPath() + "Ascii/PLST_MT.TXT'");
				}
			}
			else {
				AonUtil.addErrorMessage(" ERROR: Falta algun campo en el fichero de configuracion de familias (HWGN y HWGT son requeridos): '" + getScale().getProgramPath() + "IF/Plst_mt.if'");
			}
		}
		else {
			AonUtil.addErrorMessage(" ERROR: No existe el fichero de configuracion de familias: '" + getScale().getProgramPath() + "IF/Plst_mt.if'");
		}
		/* cod_articulo, cod_familia, cod_subfamilia, cod_rapido, precio, nombre, ean_scanner */
		for (int i=0;i<plstList.size();i++) {
			PlstData pd = plstList.get(i);
			List<String> l = new ArrayList<String>();
			l.add(""+pd.getPNUM());
			l.add("0"); //1
			l.add(""+pd.getWGNU());
			l.add(""+pd.getPNUM());
			l.add(""+getSec(getScale().getCode2()));
			l.add(""+getMos(getScale().getCode2()));
			l.add(""+getCat(getScale().getCode3()));
			l.add(pd.getPLTE());
			l.add(""+pd.getGPR1());
			l.add(pd.getECO1());
			l.add("0");
			item = parseItem(l);
			im.addItem(item);
		}
	    return im;
	}

	private PluItem parseItem(List<String> l) throws NumberFormatException, ManagerBeanException, ExpressionException {
		PluItem item = new PluItem();
		item.setCod(Integer.parseInt(l.get(0)));
		item.setFam(getAonCategoryId(Integer.parseInt(l.get(1))));
		item.setSubfam(getAonSubcategoryId(Integer.parseInt(l.get(1)), Integer.parseInt(l.get(2))));
		item.setPlu(Integer.parseInt(l.get(3)));
		item.setSec(Integer.parseInt(l.get(4)));
		item.setMos(Integer.parseInt(l.get(5)));
		item.setCat(Integer.parseInt(l.get(6)));
		item.setTxt(l.get(7));
		double price = Double.parseDouble(l.get(8));
		item.setPrc(price);
		item.setBar(l.get(9));
		item.setTyp(l.get(10));
		return item;
	}

	@SuppressWarnings("unused")
	public void onSetItems(ActionEvent event) throws ManagerBeanException, SQLException {
		onAccept(event);
		IManagerBean itemPosBean = BeanManager.getManagerBean(ItemPos.class);
		List<ITransferObject> itemPosLst = itemPosBean.getList(null);
		ItemPos ip = new ItemPos();
		if (getScale().getScaleModel() == ScaleModel.BIZERBA) initBizerba();
		for (int i=0;i < itemPosLst.size();i++) {
			ip = (ItemPos)itemPosLst.get(i);
			try {
				int plu = -999;
				try {
					plu = Integer.parseInt(ip.getPlu());
				}
				catch (NumberFormatException nfe) {
					//Nothing
				}

				/*codi_ident, secc_maqui, codigo, plu, euros, des_plu1*/
				int sec = getSec(getScale().getCode2());
				int mos = getMos(getScale().getCode2());
				int pcg = ip.getItem().getProduct().getCategory().getGroup().getId();
				int pc = ip.getItem().getProduct().getCategory().getId();
				int fam = getScaleCategoryId(pcg);
				int subfam = getScaleSubcategoryId(pcg, pc);
				int cod = Integer.parseInt(ip.getItem().getProduct().getCode());
				ItemPriceProvider ipp = new ItemPriceProvider();
				double prc = ipp.getRealTotalPrice(ip.getItem());
				String txt = ip.getItem().getProduct().getName();
				String bar = ip.getBarcode();
				String typ = ip.getPluProductType().ordinal() == 0?"W":"U";
				if (existsScaleItem(cod)) {
					try {
						updateScaleItem(prc, txt, fam, subfam, bar, cod, typ);
						AonUtil.addInfoMessage(" BLCACT: El artículo con PLU " + ip.getPlu() + " ha sido actualizado con exito.");
					}
					catch (Exception e) {
						e.printStackTrace();
						AonUtil.addErrorMessage(" BLCERR: Se produjo un error en la actualizacion del articulo con PLU " + ip.getPlu() + "");
			    		if (e.getMessage() != null) AonUtil.addWarningMessage(e.getMessage());
					}
				}
				else {
					if (plu >= -1) {
						try {
							createScaleItem(sec, mos, fam, subfam, cod, plu, prc, txt, bar, typ);
							AonUtil.addInfoMessage(" BLCINS: El artículo con PLU " + plu + " ha sido introducido con exito.");
						}
						catch (Exception e) {
							e.printStackTrace();
							AonUtil.addErrorMessage(" BLCERR: Se produjo un error en la insercion del articulo con PLU " + plu + "");
				    		if (e.getMessage() != null) AonUtil.addWarningMessage(e.getMessage());
						}
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				AonUtil.addErrorMessage(" BLCERR: Se produjo un error en la insercion del articulo con PLU " + ip.getPlu() + "");
	    		if (e.getMessage() != null) AonUtil.addWarningMessage(e.getMessage());
	    		else AonUtil.addWarningMessage("Es posible que no pertenezca a ninguna categoria");
			}
		}
		if (getScale().getScaleModel() == ScaleModel.BIZERBA) {
			createBizerbaItemFile();
		}
	}

	private void createScaleItem(int sec, int mos, int fam, int subfam, int cod, int plu, double prc, String txt, String bar, String typ) throws SQLException {
		if (getScale().getScaleModel() == ScaleModel.BIZERBA) {
			listBizerbaItem.add(new PlstData(cod, prc, subfam, bar, txt));
		}
		else {
	    	String database = getDatabasePath(getScale());
	        String insert = getScale().getScaleModel().getInsertItem(locale);
			Connection c = getConnection(database);
	        PreparedStatement ps = c.prepareStatement(insert);
	        if (getScale().getScaleModel() == ScaleModel.EUROSCALE) {
	        	Date d = new Date();
	        	String act = (new SimpleDateFormat("yymmddhhMM")).format(d);
	            ps.setInt(1, sec);
	            ps.setInt(2, mos);
	            ps.setInt(3,fam);
	            ps.setInt(4,subfam);
	            ps.setInt(5, cod);
	            ps.setInt(6, plu);
	            ps.setDouble(7, prc);
	            ps.setString(8, txt);
	            ps.setString(9, typ);
	            ps.setString(10, bar);
	            ps.setInt(11, new Integer(act).intValue());
	            /*
	            	codi_ident,secc_maqui,codi_sub,codi_fam,codigo,plu,euros,des_plu1,codi_pes,balenv,art_cb,prc3
	            */
	        }
	        else {
	            if (getScale().getScaleModel() == ScaleModel.DIBAL) {
	                ps.setInt(1,fam);
	                ps.setInt(2,subfam);
	                ps.setInt(3, cod);
	                ps.setInt(4, plu);
	                ps.setDouble(5, prc);
	                ps.setString(6, txt);
	                ps.setString(7, bar);
	            }
	        }
	        ps.executeUpdate();
	        ps.close();
	        c.close();
		}
	}

	private void updateScaleItem(double prc, String txt, int fam, int subfam, String bar, int cod, String typ) throws SQLException {
		if (getScale().getScaleModel() == ScaleModel.BIZERBA) {
			listBizerbaItem.add(new PlstData(cod, prc, subfam, bar, txt));
		}
		else {
			String database = getDatabasePath(getScale());
	        String update = getScale().getScaleModel().getUpdateItem(locale);
			Connection c = getConnection(database);
	        PreparedStatement ps = c.prepareStatement(update);
			if (getScale().getScaleModel() == ScaleModel.EUROSCALE) {
		        ps.setDouble(1, prc);
		        ps.setString(2, txt);
		        ps.setString(3, typ);
		        ps.setInt(4, cod);
			}
			else {
		        ps.setDouble(1, prc);
		        ps.setString(2, txt);
		        ps.setInt(3, cod);
			}
	        ps.executeUpdate();
	        ps.close();
	        c.close();
		}
	}

	private boolean existsScaleItem(int cod) throws SQLException {
        boolean ret = false;
		if (getScale().getScaleModel() == ScaleModel.BIZERBA) {
			HashMap<Integer,String> map = new HashMap<Integer,String>();
			try {
				File f = new File(getScale().getProgramPath() + "IF/Plst_mt.if");
				if (f.exists()) {
					PlstIf plst = getPlstFieldsZiel(f);
					if (plst.isValid()) {
						f = new File(getScale().getProgramPath() + "Ascii/PLST_MT.TXT");
						if (f.exists()) {
							map = getPlstDataMap(f, plst);
						}
						else {
							AonUtil.addErrorMessage(" ERROR: No existe el fichero de datos de familias: '" + getScale().getProgramPath() + "Ascii/HWST_MT.TXT'");
						}
					}
					else {
						AonUtil.addErrorMessage(" ERROR: Falta algun campo en el fichero de configuracion de familias (HWGN y HWGT son requeridos): '" + getScale().getProgramPath() + "IF/Hwst_mt.if'");
					}
				}
				else {
					AonUtil.addErrorMessage(" ERROR: No existe el fichero de configuracion de familias: '" + getScale().getProgramPath() + "IF/Hwst_mt.if'");
				}
			} catch (IOException e) {
				AonUtil.addErrorMessage(" ERROR: No se puede tener acceso a los archivos de datos.");
			}

			if (map != null && !map.isEmpty()) {
				if (map.containsKey(cod)) ret = true;
			}
		}
		else {
	    	String database = getDatabasePath(getScale());
			String select = getScale().getScaleModel().existsItem(locale);
			Connection c = getConnection(database);
			PreparedStatement ps = c.prepareStatement(select);
			ps.setInt(1,cod);
	        ResultSet rs = ps.executeQuery();
	        if (rs.next()) ret = true;
	        rs.close();
	        ps.close();
			c.close();
		}
		return ret;
	}

	@SuppressWarnings("unused")
	public void onGetTickets(ActionEvent event) throws ManagerBeanException, ExpressionException {
		onAccept(event);
		if (getScale().getScaleModel() == ScaleModel.EUROSCALE && (getScale().getCode5() == null || "".equals(getScale().getCode5()))) {
			AonUtil.addErrorMessage(" ERR: Debe indicar una tienda de la que importar los tickets para la inserción de las ventas.");
			return;
		}
		if (getScale().getCode3() == null || "".equals(getScale().getCode3())) {
			AonUtil.addErrorMessage(" ERR: Debe indicar un punto de venta por defecto para la inserción de las ventas.");
			return;
		}
		if (getScale().getCode4() == null || "".equals(getScale().getCode4())) {
			AonUtil.addErrorMessage(" ERR: Debe indicar un cliente por defecto para la inserción de las ventas.");
			return;
		}
		Ticket ticket;
		TicketManager tm = new TicketManager();
		try {
			if (getScale().getScaleModel() == ScaleModel.BIZERBA) {
				try {
					tm = getTicketsFromText();					
				} catch (IOException e) {
					AonUtil.addErrorMessage(" ERR: Se produjo un error intentando acceder a los archivos de datos.");
				}
			}
			else {
				if (getScale().getScaleModel() == ScaleModel.DIBAL || getScale().getScaleModel() == ScaleModel.EUROSCALE) {
					tm = getTicketsFromDatabase();
				}
				else return;
			}

			Iterator iter = tm.getAll();
			if (!iter.hasNext()) AonUtil.addWarningMessage(" INFO: No se han encontrado tickets.");
			while (iter.hasNext()){
				TicketSet ts = (TicketSet)iter.next();
				List<Ticket> tLst = ts.getTicketlist();
				Delivery d = new Delivery();
				Iterator<Ticket> tIter = tLst.iterator();
				String plu = "";
				boolean createDelivery = true;
				while (tIter.hasNext()){
					Ticket t = (Ticket)tIter.next();
					if (createDelivery) d = createDelivery(t);
					createDelivery = false;
					plu = ""+t.getPlu();
					try {
						createDeliveryDetail(t,d);
						AonUtil.addInfoMessage(" INS: El artículo con PLU " + plu + " ha sido introducido en ventas.");
					}
					catch (Exception e) {
						e.printStackTrace();
						AonUtil.addErrorMessage(" ERR: Se produjo un error en la insercion de ventas del articulo con PLU " + plu + "");
			    		if (e.getMessage() != null) AonUtil.addWarningMessage(e.getMessage());
					}
				}
				//FACTURAR *******
			}
		} catch (SQLException e) {
			e.printStackTrace();
			AonUtil.addErrorMessage(" ERR: Se produjo un error durante la conexion a la base de datos: '" + getDatabasePathForTickets(getScale()) + "'");
    		if (e.getMessage() != null) AonUtil.addWarningMessage(e.getMessage());
		}
		if (getScale().getScaleModel() == ScaleModel.EUROSCALE) getOtherTickets(); 
	}
	
	@SuppressWarnings("unchecked")
	private TicketManager getTicketsFromText() throws IOException {
		Ticket ticket;
		TicketManager tm = new TicketManager();
		ArrayList<BokaData> bokaList = new ArrayList<BokaData>();
		File f = new File(getScale().getProgramPath() + "IF/Boka_mt.if");
		if (f.exists()) {
			BokaIf boka = getBokaFieldsZiel(f);
			if (boka.isValid()) {
				f = new File(getScale().getProgramPath() + "Ascii/BOKA_MT.TXT");
				if (f.exists()) {
					bokaList = getBokaData(f, boka);
				}
				else {
					AonUtil.addErrorMessage(" ERROR: No existe el fichero de datos de tickets: '" + getScale().getProgramPath() + "Ascii/BOKA_MT.TXT'");
				}
			}
			else {
				AonUtil.addErrorMessage(" ERROR: Falta algun campo en el fichero de configuracion de tickets (BONU, STYP, SNR1, BT10, BT20, POS1, GEW1 y DZEIS son requeridos): '" + getScale().getProgramPath() + "IF/Boka_mt.if'");
			}
		}
		else {
			AonUtil.addErrorMessage(" ERROR: No existe el fichero de configuracion de tickets: '" + getScale().getProgramPath() + "IF/Boka_mt.if'");
		}
		HashMap<Date, HashMap> map = getMapFromTicketList(bokaList);
		if (map != null && !map.isEmpty()) {
			Object[] keyArray = map.keySet().toArray();
			Arrays.sort(keyArray, new Comparator() {
					public int compare(final Object o1, final Object o2){
						final Date d1 = (Date)o1;
						final Date d2 = (Date)o2;
						if (d2.after(d1)) return 1;
						return -1;
					}
				}
			);
			Iterator iter = (Arrays.asList(keyArray)).iterator();
			while (iter.hasNext()) {
				Date date = (Date)iter.next();
				HashMap<Integer, HashMap> plu_map = map.get(date);
				if (!plu_map.isEmpty()) {
					Object[] pluArray = plu_map.keySet().toArray();
					Iterator plu_iter = (Arrays.asList(pluArray)).iterator();
					while (plu_iter.hasNext()) {
						int plu = new Integer(plu_iter.next().toString()).intValue();
						HashMap<Double, ArrayList> price_map = plu_map.get(plu);
						if (!price_map.isEmpty()) {
							Object[] priceArray = price_map.keySet().toArray();
							Iterator price_iter = (Arrays.asList(priceArray)).iterator();
							while (price_iter.hasNext()) {
								double unit_price = new Double(price_iter.next().toString()).doubleValue();
								ArrayList array = price_map.get(unit_price);
								int quantity = Integer.parseInt("" + array.get(1));
								double weight = Double.parseDouble("" + array.get(2));
								double price = Double.parseDouble("" + array.get(3));
								List<String> l = new ArrayList<String>();
								/*SELECT d_fecha, code, plu, item, price, SUM(amount) AS cant, SUM(weight) AS peso, SUM(units) AS unidad FROM tickets WHERE d_fecha BETWEEN ? AND ? AND lincan = '0' GROUP BY d_fecha, code, plu, item, price ORDER BY d_fecha*/
								l.add(""+plu); //0 Codigo
								l.add(""+plu); //1 PLU
								l.add(""+unit_price); //2 Precio
								double amount = weight;
								if (amount == 0) amount = quantity;
								l.add(""+amount); // 3 Cantidad
								l.add(new SimpleDateFormat(DATEFORMAT).format(date)); // 4 Fecha
								l.add(getScale().getCode4()); // 5 Cliente
								l.add(""+price); // 6 Total
								l.add(""); // 7 Texto (no existe en el fichero)
								l.add(getScale().getSerie()); // 8 Serie
								ticket = parseTicket(l); 
								tm.addTicket(ticket);
							}
						}
					}
				}
			}
		}
	    return tm;
	}

	@SuppressWarnings("unchecked")
	private HashMap<Date,HashMap> getMapFromTicketList(ArrayList<BokaData> bokaList) {
		HashMap<Date,HashMap> date_map = new HashMap<Date,HashMap>();
		for (int i=0;i<bokaList.size();i++) {
			BokaData bd = bokaList.get(i);
			int code = bd.getBONU(); 
			int plu = bd.getSNR1(); 
			double unit_price = bd.getBT10(); 
			double price = bd.getBT20(); 
			int quantity = bd.getPOS1(); 
			double weight = bd.getGEW1(); 
			Date date = bd.getZEIS();
			if ( date.before(getScale().getEnddate()) && date.after(getScale().getInidate()) || date.equals(getScale().getInidate()) || date.equals(getScale().getEnddate())) {
				if (date_map.containsKey(date)) {
					HashMap<Integer,HashMap> plu_map = date_map.get(date);
					if (plu_map.containsKey(plu)) {
						HashMap<Double,ArrayList> price_map = plu_map.get(plu);
						if (price_map.containsKey(unit_price)) {
							ArrayList data = price_map.get(unit_price);
							data.set(1, Integer.parseInt("" + data.get(1)) + quantity);
							data.set(2, Double.parseDouble("" + data.get(2)) + weight);
							data.set(3, Double.parseDouble("" + data.get(3)) + price);
						}
						else {
							ArrayList<Object> data = new ArrayList<Object>();
							data.add(code);
							data.add(quantity);
							data.add(weight);
							data.add(price);
							price_map.put(new Double(unit_price), data);
						}
					}
					else {
						HashMap<Double,ArrayList> price_map = new HashMap<Double,ArrayList>();
						ArrayList<Object> data = new ArrayList<Object>();
						data.add(code);
						data.add(quantity);
						data.add(weight);
						data.add(price);
						price_map.put(unit_price, data);
						plu_map.put(plu, price_map);
					}
				}
				else {
					HashMap<Integer,HashMap> plu_map = new HashMap<Integer,HashMap>();
					HashMap<Double,ArrayList> price_map = new HashMap<Double,ArrayList>();
					ArrayList<Object> data = new ArrayList<Object>();
					data.add(code);
					data.add(quantity);
					data.add(weight);
					data.add(price);
					price_map.put(unit_price, data);
					plu_map.put(plu, price_map);
					date_map.put(date, plu_map);
				}
			}
		}
		return date_map;
	}

	private TicketManager getTicketsFromDatabase() throws SQLException {
		Ticket ticket;
		TicketManager tm = new TicketManager();
		String database = getDatabasePathForTickets(getScale());
		String select = getScale().getScaleModel().getSelectTicket(locale);
		Connection c = getConnection(database);
		PreparedStatement ps = c.prepareStatement(select);
		ps.setTimestamp(1, (Timestamp)getScale().getInidate());
		ps.setTimestamp(2, (Timestamp)getScale().getEnddate());
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
			List<String> l = new ArrayList<String>();
			/*SELECT d_fecha, code, plu, item, price, SUM(amount) AS cant, SUM(weight) AS peso, SUM(units) AS unidad FROM tickets WHERE d_fecha BETWEEN ? AND ? AND lincan = '0' GROUP BY d_fecha, code, plu, item, price ORDER BY d_fecha*/
			l.add(""+rs.getInt(2)); //0 Codigo
			l.add(""+rs.getInt(3)); //1 PLU
			l.add(""+rs.getDouble(5)); //2 Precio
			double amount = rs.getDouble(7);
			if (amount == 0) amount = rs.getDouble(8);
			l.add(""+amount); // 3 Cantidad
			l.add(new SimpleDateFormat(DATEFORMAT).format(rs.getDate(1))); // 4 Fecha
			l.add(getScale().getCode4()); // 5 Cliente
			l.add(""+rs.getDouble(6)); // 6 Total
			l.add(""+rs.getString(4)); // 7 Texto
			l.add(getScale().getSerie()); // 8 Serie
			ticket = parseTicket(l); 
			tm.addTicket(ticket);
		}
        rs.close();
        ps.close();
        c.close();
        return tm;
	}

	private void getOtherTickets() throws ManagerBeanException, ExpressionException {
		Ticket ticket;
		TicketManager tm = new TicketManager();
    	String database = getOtherDatabasePathForTickets(getScale(), getScale().getInidate());
    	AonUtil.addWarningMessage(" INFO: Buscando en base de datos alternativa '" + database + "'.");
		try {
			String select = getScale().getScaleModel().getSelectTicket(locale);
			Connection c = getConnection(database);
			PreparedStatement ps = c.prepareStatement(select);
			ps.setTimestamp(1, (Timestamp)getScale().getInidate());
			ps.setTimestamp(2, (Timestamp)getScale().getEnddate());
	        ResultSet rs = ps.executeQuery();
	        while (rs.next()) {
				List<String> l = new ArrayList<String>();
				/*SELECT d_fecha, code, plu, price, SUM(amount), SUM(weight), SUM(units) FROM tickets WHERE lincan = "0" AND d_fecha BETWEEN ? AND ? GROUP BY d_fecha, code, plu, price*/
				l.add(""+rs.getInt(2)); //0 Codigo
				l.add(""+rs.getInt(3)); //1 PLU
				l.add(""+rs.getDouble(5)); //2 Precio
				double amount = rs.getDouble(7);
				if (amount == 0) amount = rs.getDouble(8);
				l.add(""+amount); // 3 Cantidad
				l.add(new SimpleDateFormat(DATEFORMAT).format(rs.getDate(1))); // 4 Fecha
				l.add(getScale().getCode4()); // 5 Cliente
				l.add(""+rs.getDouble(6)); // 6 Total
				l.add(""+rs.getString(4)); // 7 Total
				l.add(getScale().getSerie()); // 8 Serie
				ticket = parseTicket(l); 
				tm.addTicket(ticket);
			}
	        rs.close();
	        ps.close();
	        c.close();
			Iterator iter = tm.getAll();
			while (iter.hasNext()) {
				TicketSet ts = (TicketSet)iter.next();
				List<Ticket> tLst = ts.getTicketlist();
				Delivery d = new Delivery();
				Iterator<Ticket> tIter = tLst.iterator();
				String plu = "";
				boolean createDelivery = true;
				while (tIter.hasNext()){
					Ticket t = (Ticket)tIter.next();
					if (createDelivery) d = createDelivery(t);
					createDelivery = false;
					plu = ""+t.getPlu();
					try {
						createDeliveryDetail(t,d);
						AonUtil.addInfoMessage(" B_INS: El artículo con PLU " + plu + " ha sido introducido en ventas.");
					}
					catch (Exception e) {
						e.printStackTrace();
						AonUtil.addErrorMessage(" B_ERR: Se produjo un error en la insercion de ventas del articulo con PLU " + plu + "");
			    		if (e.getMessage() != null) AonUtil.addWarningMessage(e.getMessage());
					}
				}
				//FACTURAR *******
			}
		} catch (SQLException e) {
			AonUtil.addErrorMessage(" B_ERR: No existe la base de datos '" + database + "' para las fechas indicadas.'");
		}
	}
	
	private Ticket parseTicket(List<String> l) {
		Ticket ticket = new Ticket();
		ticket.setNumber(Integer.parseInt(l.get(0)));
		ticket.setPlu(l.get(1));
		ticket.setPrice(Double.parseDouble(l.get(2)));
		ticket.setQuantity(Double.parseDouble(l.get(3)));
		DateFormat df = new SimpleDateFormat(DATEFORMAT);
		try {
			ticket.setSaleDate(df.parse(l.get(4)));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		ticket.setCustomer(Integer.parseInt(l.get(5)));
		ticket.setTotal(Double.parseDouble(l.get(6)));
		ticket.setDescription(l.get(7));
		ticket.setSerie(l.get(8));
		return ticket;
	}

	private String getDatabasePath(Scale b) {
		String path = b.getProgramPath();
		String subdirectory = getScale().getCode1();
		if (subdirectory == null || subdirectory.equals("")) subdirectory = "t01";
		if (getScale().getScaleModel() == ScaleModel.EUROSCALE) path += "" + subdirectory + "/dbase.mdb";
		else if (getScale().getScaleModel() == ScaleModel.DIBAL) path += "Dibal.mdb";
		return path;
	}

	private String getDatabasePathForTickets(Scale b) {
		String path = b.getProgramPath();
		String subdirectory = getScale().getCode5();
		if (subdirectory == null || subdirectory.equals("")) subdirectory = "t01";
		if (getScale().getScaleModel() == ScaleModel.EUROSCALE) path += "" + subdirectory + "/dbase.mdb";
		else if (getScale().getScaleModel() == ScaleModel.DIBAL) path += "Dibal.mdb";
		return path;
	}

	private String getOtherDatabasePathForTickets(Scale b, Date d) {
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(d);
		String year = "" + gc.get(Calendar.YEAR);
		String month = "" + (gc.get(Calendar.MONTH) + 1);
		if (month.length() == 1) month = "0" + month;
		String path = b.getProgramPath();
		if (getScale().getScaleModel() == ScaleModel.EUROSCALE) path += "" + getScale().getCode5() + "/S/" + year + "" + month + ".mdb";
		
		return path;
	}

	public void onAccept(ActionEvent event) {
		if (getScale().getScaleModel() == ScaleModel.NONE) {
			getScale().setProgramPath("");
			getScale().setVerified(false);
		}
		else {
			if (getScale().getProgramPath() == null || getScale().getProgramPath().equals("")) {
				AonUtil.addErrorMessage(" ERROR: Debe rellenar la ruta del programa");
				return;
			}
			else {
				getScale().setProgramPath(getScale().getProgramPath().replace('\\','/'));
				if (!(getScale().getProgramPath().substring(getScale().getProgramPath().length()-1)).equals("/"))
					getScale().setProgramPath(getScale().getProgramPath() + "/");
				File files = new File(getScale().getProgramPath());
			    if (files.exists()) { 
			    	AonUtil.addInfoMessage(" OK: La ruta del programa es correcta. Testeando la conexion con la base de datos...");
		    		testConnection(true);
			    }
			    else {
			    	AonUtil.addErrorMessage(" ERROR: La ruta del programa no es valida: '" + getScale().getProgramPath() + "'");
			    }
			}
		}
		super.onAccept(event);
		try {
			initializeModel();
			if(this.getModel().getRowCount() > 0){
				this.getModel().setRowIndex(0);
				onSelect(null);
			}
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	private boolean testConnection(boolean info) {
	    File file = new File(getScale().getProgramPath());
	    boolean ret = false;
	    if (file.exists()) {
	    	//Si se trata de la balanza de Bizerba entonces no existe BD, si ya existe la ruta entonces OK
	    	if (getScale().getScaleModel() == ScaleModel.BIZERBA) {
	    	    file = new File(getScale().getProgramPath() + "IF/");
	    	    if (!file.exists()) {
	    	    	if (info) AonUtil.addErrorMessage(" ERROR: La ruta de configuracion del programa no se encuentra: '" + getScale().getProgramPath() + "IF/'"); 
	    	    	return false;
	    	    }
	    	    file = new File(getScale().getProgramPath() + "Ascii/");
	    	    if (!file.exists()) {
	    	    	if (info) AonUtil.addErrorMessage(" ERROR: La ruta de datos del programa no se encuentra: '" + getScale().getProgramPath() + "Ascii/'"); 
	    	    	return false;
	    	    }
	    		if (info) AonUtil.addErrorMessage(" OK: La ruta es correcta."); 
	    		getScale().setVerified(true);
	    		return true;
	    	}
	    	String database = getDatabasePath(getScale());
	    	Connection c = null;
			try {
				c = getConnection(database);
	    		if (info) AonUtil.addInfoMessage(" OK: La conexion con la base de datos en la ruta: '" + getDatabasePath(getScale()) + "' es correcta.");
	    		ret = true;
			}
	    	catch (SQLException sqle){
	    		if (info) AonUtil.addErrorMessage(" ERROR: Base de datos no encontrada en la ruta: '" + getDatabasePath(getScale()) + "'");
	    	}
	    	if (c != null) {
	    		getScale().setVerified(true);
	    		try {
					c.close();
				} catch (SQLException e) {}
	    	}
	    }
	    else {
	    	ret = false;
	    	if (info) AonUtil.addErrorMessage(" ERROR: La ruta del programa no es valida: '" + getScale().getProgramPath() + "'"); 
	    }
 
	    return ret;
	}

	@SuppressWarnings("unused")
	public void onLoad(MenuEvent event) throws ManagerBeanException {
		try {
			initializeModel();
			if(this.getModel().getRowCount() > 0){
				this.getModel().setRowIndex(0);
				onSelect(null);
			}else{
				this.onReset(null);
			}
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	public void onCancel(ActionEvent event) {
		super.onCancel(event);
		try {
			initializeModel();
			if(this.getModel().getRowCount() > 0){
				this.getModel().setRowIndex(0);
				onSelect(null);
			}else{
				this.onReset(null);
			}
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	private boolean existsFamily(int fam) throws ManagerBeanException, ExpressionException {
		IManagerBean scalerelationBean = BeanManager.getManagerBean(ScaleRelation.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(scalerelationBean.getFieldName(IMarketplaceAlias.SCALE_RELATION_SCALE_ID1), "" + fam);
		criteria.addExpression(scalerelationBean.getFieldName(IMarketplaceAlias.SCALE_RELATION_TYPE), "F");
		criteria.addExpression(scalerelationBean.getFieldName(IMarketplaceAlias.SCALE_RELATION_SCALE_MODEL), "" + getScale().getScaleModel().ordinal());
		List<ITransferObject> scalerelationLst = scalerelationBean.getList(criteria);
		if (scalerelationLst.size()>0) return true;
		return false;
	}

	private boolean existsCategory(int fam, int subfam) throws ManagerBeanException, ExpressionException {
		IManagerBean scalerelationBean = BeanManager.getManagerBean(ScaleRelation.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(scalerelationBean.getFieldName(IMarketplaceAlias.SCALE_RELATION_SCALE_ID1), "" + fam);
		criteria.addExpression(scalerelationBean.getFieldName(IMarketplaceAlias.SCALE_RELATION_SCALE_ID2), "" + subfam);
		criteria.addExpression(scalerelationBean.getFieldName(IMarketplaceAlias.SCALE_RELATION_TYPE), "C");
		criteria.addExpression(scalerelationBean.getFieldName(IMarketplaceAlias.SCALE_RELATION_SCALE_MODEL), "" + getScale().getScaleModel().ordinal());
		List<ITransferObject> scalerelationLst = scalerelationBean.getList(criteria);
		if (scalerelationLst.size()>0) return true;
		return false;
	}


	private boolean existsItem(PluItem pluitem) throws ManagerBeanException, ExpressionException {
		IManagerBean productBean = BeanManager.getManagerBean(Product.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(productBean.getFieldName(IProductAlias.PRODUCT_CODE),""+pluitem.getCod());
		List<ITransferObject> productLst = productBean.getList(criteria);
		if (productLst.size()>0) return true;
		return false;
	}

	private void createFamily(int id, String description) throws ManagerBeanException, ExpressionException {
		IManagerBean pcgBean = BeanManager.getManagerBean(ProductCategoryGroup.class);
		ProductCategoryGroup pcg = new ProductCategoryGroup();
		if (description.length() > 20) description = description.substring(0, 20);
		pcg.setName(description);
		pcgBean.insert(pcg);
		createFamilyRelation(id, pcg.getId());
	}

	private void createFamilyRelation(int id, int pcg) throws ManagerBeanException, ExpressionException {
		IManagerBean srBean = BeanManager.getManagerBean(ScaleRelation.class);
		ScaleRelation sr = new ScaleRelation();
		sr.setAon_id(pcg);
		sr.setScale_id1("" + id);
		sr.setType("F");
		sr.setScaleModel(getScale().getScaleModel());
		srBean.insert(sr);
	}

	private void createCategoryInFamily(int family, int id, String description) throws ManagerBeanException, ExpressionException {
		IManagerBean srBean = BeanManager.getManagerBean(ScaleRelation.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(srBean.getFieldName(IMarketplaceAlias.SCALE_RELATION_SCALE_ID1), "" + family);
		criteria.addExpression(srBean.getFieldName(IMarketplaceAlias.SCALE_RELATION_TYPE), "F");
		criteria.addExpression(srBean.getFieldName(IMarketplaceAlias.SCALE_RELATION_SCALE_MODEL), "" + getScale().getScaleModel().ordinal());
		List<ITransferObject> srLst = srBean.getList(criteria);
		ScaleRelation sr = new ScaleRelation();
		if (srLst.size()>0)  sr = (ScaleRelation)srLst.get(0);
		else return;

		IManagerBean pcgBean = BeanManager.getManagerBean(ProductCategoryGroup.class);
		Criteria criteria_pcg = new Criteria();
		criteria_pcg.addExpression(pcgBean.getFieldName(IProductAlias.PRODUCT_CATEGORY_GROUP_ID), sr.getAon_id().toString());
		List<ITransferObject> pcgLst = pcgBean.getList(criteria_pcg);
		ProductCategoryGroup pcg = new ProductCategoryGroup();
		if (pcgLst.size()>0)  pcg = (ProductCategoryGroup)pcgLst.get(0);
		else return;

		IManagerBean pcBean = BeanManager.getManagerBean(ProductCategory.class);
		ProductCategory pc = new ProductCategory();
		pc.setGroup(pcg);
		if (description.length() > 32) description = description.substring(0, 32);
		pc.setName(description);
		pcBean.insert(pc);
		
		createCategoryRelation(family, id, pc.getId());
	}

	private void createCategoryRelation(int family, int id, int pc) throws ManagerBeanException, ExpressionException {
		IManagerBean srBean = BeanManager.getManagerBean(ScaleRelation.class);
		ScaleRelation sr = new ScaleRelation();
		sr.setAon_id(pc);
		sr.setScale_id1("" + family);
		sr.setScale_id2("" + id);
		sr.setType("C");
		sr.setScaleModel(getScale().getScaleModel());
		srBean.insert(sr);
	}
	
	private void updateItem(PluItem pluitem) throws ManagerBeanException, ExpressionException {
		IManagerBean productBean = BeanManager.getManagerBean(Product.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(productBean.getFieldName(IProductAlias.PRODUCT_CODE),""+pluitem.getCod());
		List<ITransferObject> productLst = productBean.getList(criteria);
		Product p = new Product();
		if (productLst.size()>0) {
			p = (Product)productLst.get(0);
			p.setName(pluitem.getTxt(false));
			productBean.update(p);
		}

		if (p.getId() != null) {
			IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
			criteria = new Criteria();
			criteria.addExpression(itemBean.getFieldName(IProductAlias.ITEM_PRODUCT_ID), ""+p.getId());
			List<ITransferObject> itemLst = itemBean.getList(criteria);
			Item i = new Item();
			if (itemLst.size()>0) {
				i = (Item)itemLst.get(0);
				i.setDescription(pluitem.getTxt(false));
				double price = pluitem.getPrc();
				if (price > 0) {
					ItemPriceProvider ipp = new ItemPriceProvider();
					double per = ipp.getTax(i);
					if (ipp.isSurcharge()) per = per + ipp.getSurcharge(i);
					price = (price / ((per/100) + 1));
				}
				i.setPrice(price);
				itemBean.update(i);
			}
			else {
				i.setProduct(p);
				i.setDescription(pluitem.getTxt(false));
				double per = 7;
				double price = pluitem.getPrc();
				if (price > 0) {
					ItemPriceProvider ipp = new ItemPriceProvider();
					if (ipp.isSurcharge()) per = per + 1;
					price = (price / ((per/100) + 1));
				}
				i.setPrice(price);
				itemBean.insert(i);
			}

			if (i.getId() != null) {
				IManagerBean itemPosBean = BeanManager.getManagerBean(ItemPos.class);
				criteria = new Criteria();
				criteria.addExpression(itemPosBean.getFieldName(IProductAlias.ITEM_POS_ITEM_ID), ""+i.getId());
				List<ITransferObject> itemPosLst = itemPosBean.getList(criteria);
				ItemPos ip = new ItemPos();
				if (itemPosLst.size()>0) {
					ip = (ItemPos)itemPosLst.get(0);
					ip.setShortDescription(pluitem.getTxt(true));
					ip.setPluProductType(pluitem.getTyp());
					itemPosBean.update(ip);
				}
				else {
					ip.setItem(i);
					String plu = "" + p.getId();
					String barcode = "" + p.getId();
					if (pluitem.getPlu() < 0) {
						plu = FieldUtils.obtainPluCode(p.getId());
					}
					else plu = "" + pluitem.getPlu();
					if (pluitem.getBar() != null) {
						barcode = pluitem.getBar();
					}
					ip.setPlu(plu);
					ip.setBarcode(barcode);
					ip.setShortDescription(pluitem.getTxt(true));
					ip.setPluProductType(pluitem.getTyp());
					itemPosBean.insert(ip);
				}
			}
		}
	}

	private void createItem(PluItem pluitem) throws ManagerBeanException, ExpressionException {
		IManagerBean productBean = BeanManager.getManagerBean(Product.class);
		Product p = new Product();
		p.setCode(""+pluitem.getCod());
		p.setName(pluitem.getTxt(false));
		p.setVat(new Tax(1));
		p.setCategory(new ProductCategory(pluitem.getSubfam()));
		p.setStatus(ProductStatus.ACTIVE);
		p.setInventoriable(true);
		productBean.insert(p);

		IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
		Item i = new Item();
		i.setProduct(p);
		i.setDescription(pluitem.getTxt(false));
		double per = 7;
		double price = pluitem.getPrc();
		if (price > 0) {
			ItemPriceProvider ipp = new ItemPriceProvider();
			if (ipp.isSurcharge()) per = per + 1;
			price = (price / ((per/100) + 1));
		}
		i.setPrice(price);
		i.setStatus(ProductStatus.ACTIVE);
		itemBean.insert(i);

		IManagerBean itemPosBean = BeanManager.getManagerBean(ItemPos.class);
		ItemPos ip = new ItemPos();
		ip.setItem(i);
		String plu = "" + p.getId();
		String barcode = "" + p.getId();
		if (pluitem.getPlu() < 0) {
			plu = FieldUtils.obtainPluCode(p.getId());
		}
		else plu = "" + pluitem.getPlu();
		if (pluitem.getBar() != null) {
			barcode = pluitem.getBar();
		}
		ip.setPlu(plu);
		ip.setBarcode(barcode);
		ip.setShortDescription(pluitem.getTxt(true));
		ip.setPluProductType(pluitem.getTyp());
		itemPosBean.insert(ip);
	}
	
	private Delivery createDelivery(Ticket t) throws ManagerBeanException, ExpressionException {
		Delivery d = new Delivery();
		IManagerBean deliveryBean = BeanManager.getManagerBean(Delivery.class);
		Customer c = new Customer();
		c.setId(Integer.parseInt(getScale().getCode4()));
		d.setCustomer(c);
		d.setIssueTime(t.getSaleDate());
		d.setStatus(DeliveryStatus.PENDING);
		d.setSecurityLevel(SecurityLevel.OFFICIAL);
		d.setSeries(t.getSerie());
		d.setNumber(SeriesNumberUtil.obtainNumber(d.getSeries(), "Delivery"));
		deliveryBean.insert(d);
		sales = createSales(d);
		return d;
	}

	private void createDeliveryDetail(Ticket ticket, Delivery d) throws ManagerBeanException, ExpressionException {
		DeliveryDetail dd = new DeliveryDetail();
		IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);

		IManagerBean productBean = BeanManager.getManagerBean(Product.class);
		Criteria criteria_product = new Criteria();
		criteria_product.addExpression(productBean.getFieldName(IProductAlias.PRODUCT_CODE),"" + ticket.getNumber());
		List<ITransferObject> productLst = productBean.getList(criteria_product);
		Product product = new Product();
		if (productLst.size()>0) product = (Product)productLst.get(0);

		IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
		Criteria criteria_item = new Criteria();
		criteria_item.addEqualExpression(itemBean.getFieldName(IProductAlias.ITEM_PRODUCT_ID),product.getId());
		List<ITransferObject> itemLst = itemBean.getList(criteria_item);
		Item item = new Item();
		if (itemLst.size()>0) item = (Item)itemLst.get(0);
		
		IManagerBean itemposBean = BeanManager.getManagerBean(ItemPos.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(itemposBean.getFieldName(IProductAlias.ITEM_POS_ITEM_ID), item.getId());
		List<ITransferObject> itemposLst = itemposBean.getList(criteria);
		ItemPos itempos = new ItemPos();
		if (itemposLst.size()>0) itempos = (ItemPos)itemposLst.get(0);
		String description = ticket.getDescription();
		if (description.equals("")) {
			description = itempos.getItem().getProduct().getName();
		}
		dd.setItem(itempos.getItem());
		dd.setDelivery(d);
		dd.setLine(1);
		dd.setDescription(description);
		double price = ticket.getPrice();
		if (price > 0) {
			ItemPriceProvider ipp = new ItemPriceProvider();
			double per = ipp.getTax(item);
			if (ipp.isSurcharge()) per = per + ipp.getSurcharge(item);
			price = (price / ((per/100) + 1));
		}
		dd.setPrice(price);
		dd.setQuantity(ticket.getQuantity());
		Warehouse w = new Warehouse();
		w.setId(1);
		dd.setWarehouse(w);
		dd.setDiscountExpression(new DiscountExpression("0.0"));
		SalesDetail sd = createSalesDetail(dd);
		dd.setSalesDetail(sd);
		deliveryDetailBean.insert(dd);
	}
	
	
	private Sales createSales(Delivery delivery) {
		Sales sales = new Sales();
		sales.setCustomer((delivery.getCustomer()));
		sales.setStatus(SalesStatus.PENDING);
		sales.setSeries(delivery.getSeries());
		sales.setNumber(SeriesNumberUtil.obtainNumber(delivery.getSeries(), "Sales"));
		sales.setPayMethod(null);
		sales.setIssueDate(delivery.getIssueTime());
		WorkPlace workPlace = new WorkPlace(); 
		try {
			IManagerBean workPlaceBean = BeanManager.getManagerBean(WorkPlace.class);
			List<ITransferObject> wpLst = workPlaceBean.getList(null);
			if (wpLst.size() > 0) {
				workPlace = (WorkPlace)wpLst.get(0);
			}
		} catch (ManagerBeanException e) {}
		sales.setWorkPlace(workPlace);
		PointOfSale pos = new PointOfSale();
		pos.setId(Integer.parseInt(getScale().getCode3()));
		sales.setPos(pos);
		IManagerBean salesBean;
		try {
			salesBean = BeanManager.getManagerBean(Sales.class);
			sales = (Sales)salesBean.insert(sales);
			return sales;
		} catch (ManagerBeanException e) {}
		return null;
	}
	
	private SalesDetail createSalesDetail(DeliveryDetail deliveryDetail) {
		SalesDetail salesDetail = new SalesDetail();
		salesDetail.setSales(sales);
		salesDetail.setItem(deliveryDetail.getItem());
        salesDetail.setDescription(deliveryDetail.getDescription());
		salesDetail.setPrice(deliveryDetail.getPrice());
		salesDetail.setQuantity(deliveryDetail.getQuantity());
		salesDetail.setDiscountExpression(deliveryDetail.getDiscountExpression());
		salesDetail.setSalesDetailStatus(SalesDetailStatus.PENDING);
		IManagerBean salesDetailBean;
		try {
			salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
			salesDetail = (SalesDetail)salesDetailBean.insert(salesDetail);
			return salesDetail;
		} catch (ManagerBeanException e) {}
		return null;
	}

	private Connection getConnection(String database_path) throws SQLException {
        try {
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
	        // Set this to a MS Access DB you have on your machine
	        String connection_url = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=";
	        connection_url += database_path.trim() + ";}"; // add on to the end DriverID=22;READONLY=true 
	        // now we can get the connection from the DriverManager
	        Connection c = DriverManager.getConnection(connection_url, "", "");
	        return c;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	private int getSec(String code) {
		if (getScale().getScaleModel() == ScaleModel.EUROSCALE) {
			String temp = code.substring(0, code.indexOf("|")); 
			return Integer.parseInt((temp==null||temp.equals(""))?"0":temp);
		}
		else return 0; 
	}

	private int getMos(String code) {
		if (getScale().getScaleModel() == ScaleModel.EUROSCALE) {
			String temp = code.substring(code.indexOf("|")+1); 
			return Integer.parseInt((temp==null||temp.equals(""))?"0":temp);
		}
		else return 0;
	}

	private int getCat(String code) {
		if (getScale().getScaleModel() == ScaleModel.EUROSCALE || getScale().getScaleModel() == ScaleModel.DIBAL) {
			return Integer.parseInt((code==null||code.equals(""))?"0":code);			
		}
		return 0; 
	}

	/**
     * Devuelve una lista con los tipos de direcciones.
     * 
     * @return
     */
    public List<SelectItem> getScaleModels() {
        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
	    scaleModels = new LinkedList<SelectItem>();
        for( ScaleModel bm : ScaleModel.values() ) {
            String name = bm.getName(locale); 
            SelectItem item = new SelectItem(bm, name);
            scaleModels.add( item );
        }
        return scaleModels;
    }

	/**
     * Devuelve una lista con los tipos de direcciones.
     * 
     * @return
     */
    public List<SelectItem> getEuroscaleTiendas() {
	    euroscaleTiendas = new LinkedList<SelectItem>();
	    File files = new File(getScale().getProgramPath());
	    if (files.exists()) {
			String templist[] = files.list();
			Arrays.sort(templist, String.CASE_INSENSITIVE_ORDER);
			for (int i=0;i<templist.length;++i) {
				File temp = new File(getScale().getProgramPath() + templist[i]);
				if (temp.isDirectory() && temp.getName().startsWith("t0")) {
		            SelectItem item = new SelectItem(temp.getName(), "Tienda " + temp.getName());
		            euroscaleTiendas.add( item );
				}
			}
	    }
        return euroscaleTiendas;
    }

	/**
     * Devuelve una lista con los tipos de direcciones.
     * 
     * @return
	 * @throws SQLException 
     */
    public List<SelectItem> getEuroscaleSeccion() {
    	try {
		    euroscaleSeccion = new LinkedList<SelectItem>();
			String select = getScale().getScaleModel().getSelectScale(locale);
			Connection c = getConnection(getDatabasePath(getScale()));
			PreparedStatement ps = c.prepareStatement(select);
	        ResultSet rs = ps.executeQuery();
	        while (rs.next()) {
	        	SelectItem item = new SelectItem(rs.getInt(1) + "|" + rs.getInt(2), rs.getString(3) );
	            euroscaleSeccion.add( item );
			}
	        rs.close();
	        ps.close();
	        c.close();
    	}
    	catch (SQLException sqle){
    		AonUtil.addErrorMessage(" ERROR: La ruta del programa no es valida: '" + getScale().getProgramPath() + "'");
    	}
        return euroscaleSeccion;
    }

	/**
     * Devuelve una lista de consumidores/clientes
     * 
     * @return
	 * @throws ManagerBeanException 
	 * @throws SQLException 
     */
    public List<SelectItem> getCustomers() throws ManagerBeanException {
	    customers = new LinkedList<SelectItem>();
		IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
		List<ITransferObject> customerLst = customerBean.getList(null);
		for (int i=0;i < customerLst.size();i++) {
			Customer c = (Customer)customerLst.get(i);
			SelectItem item = new SelectItem(""+c.getId(), c.getRegistry().getSurname() + ", " + c.getRegistry().getName() );
            customers.add( item );
		}
        return customers;
    }

	/**
     * Devuelve una lista de Puntos de venta
     * 
     * @return
	 * @throws ManagerBeanException 
	 * @throws SQLException 
     */
    public List<SelectItem> getPos() throws ManagerBeanException {
	    pos = new LinkedList<SelectItem>();
		IManagerBean posBean = BeanManager.getManagerBean(PointOfSale.class);
		List<ITransferObject> posLst = posBean.getList(null);
		for (int i=0;i < posLst.size();i++) {
			PointOfSale p = (PointOfSale)posLst.get(i);
			SelectItem item = new SelectItem(""+p.getId(), p.getDescription() );
            pos.add( item );
		}
        return pos;
    }

	public boolean isEuroscale() {
		return (getScale().getScaleModel() == ScaleModel.EUROSCALE && getScale().isVerified() && getScale().getProgramPath() != null);
	}

	public boolean isDibal() {
		return (getScale().getScaleModel() == ScaleModel.DIBAL && getScale().isVerified() && getScale().getProgramPath() != null);
	}

	public boolean isBizerba() {
		return (getScale().getScaleModel() == ScaleModel.BIZERBA && getScale().isVerified() && getScale().getProgramPath() != null);
	}

    private Scale getScale() {
    	return (Scale)getTo();
    }

	public List<SelectItem> getScaleCategories() throws ManagerBeanException, SQLException {
		List<SelectItem> categories = new LinkedList<SelectItem>();  
		if (getScale().getScaleModel() == ScaleModel.BIZERBA) {
        	//Obtener un HashMap de subfamilias con codigo de familia - subfamila como KEY y descripcion familia - descripcion subfamilia como CONTENT
			ArrayList<List> relation = new ArrayList<List>();
			try {
				relation = getCategoryFromText();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Iterator<List> iter = relation.iterator();
			if (!iter.hasNext()) AonUtil.addWarningMessage(" INFO: No se han encontrado categorias.");
			while (iter.hasNext()){
				List category = iter.next();
				int code1 = Integer.parseInt("" + category.get(0));
				String text1 = "" + category.get(1);
				if (text1 == null || text1.trim().equals("")) text1 = "" + code1;
				int code2 = Integer.parseInt("" + category.get(2));
				String text2 = "" + category.get(3);
				if (text2 == null || text2.trim().equals("")) text2 = "" + code2;
	
				SelectItem item = new SelectItem("" + code1 + " - " + code2, text1 + " - " + text2);
				categories.add(item);
			}
        }
        else {
	    	String database = getDatabasePath(getScale());
			String select = getScale().getScaleModel().getSelectCategory(locale);
			Connection c = getConnection(database);
			PreparedStatement ps = c.prepareStatement(select);
	        ResultSet rs = ps.executeQuery();
	        while (rs.next()) {
				int code1 = rs.getInt(1);
				String text1 = rs.getString(2);
				if (text1 == null || text1.trim().equals("")) text1 = "" + code1;
				int code2 = rs.getInt(3);
				String text2 = rs.getString(4);
				if (text2 == null || text2.trim().equals("")) text2 = "" +code2;
	
				SelectItem item = new SelectItem("" + code1 + " - " + code2, text1 + " - " + text2);
				categories.add(item);
	        }
	        rs.close();
	        ps.close();
	        c.close();
        }
		return categories;
	}

	public String getScaleRelationDescription(String id1, String id2) throws SQLException {
        String text = "";
		if (getScale().getScaleModel() == ScaleModel.BIZERBA) {
        	//Obtener un HashMap de subfamilias con codigo de familia - subfamila como KEY y descripcion familia - descripcion subfamilia como CONTENT
			ArrayList<List> relation = new ArrayList<List>();
			try {
				relation = getCategoryFromText();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Iterator<List> iter = relation.iterator();
			if (!iter.hasNext()) AonUtil.addWarningMessage(" INFO: No se han encontrado categorias.");
			while (iter.hasNext()){
				List category = iter.next();
				String code1 = "" + category.get(0);
				String code2 = "" + category.get(2);
				if (code1.equals(id1) && code2.equals(id2)) {
					text = "" + category.get(1) + " - " + category.get(3) + "";
					break;
				}
			}
        }
        else {
			String database = getDatabasePath(getScale());
			String select = getScale().getScaleModel().getGetScaleCategoryDescription(locale);
			Connection c = getConnection(database);
			PreparedStatement ps = c.prepareStatement(select);
			ps.setInt(1,Integer.parseInt(id1));
			ps.setInt(2,Integer.parseInt(id2));
			ResultSet rs = ps.executeQuery();
	        if (getScale().getScaleModel() == ScaleModel.EUROSCALE) {
	        	if (rs.next()) text = "" + rs.getInt(1) + " - " + rs.getInt(2) + "";
	        }
	        else {
	            if (getScale().getScaleModel() == ScaleModel.DIBAL) {
	            	if (rs.next()) text = "" + rs.getString(1) + " - " + rs.getString(2) + "";
	            }
	        }
	        rs.close();
	        ps.close();
	        c.close();
        }
		return text;
	}


	public String getAonRelationDescription(int id) throws SQLException, ManagerBeanException {
		IManagerBean pcBean = BeanManager.getManagerBean(ProductCategory.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(pcBean.getFieldName(IProductAlias.PRODUCT_CATEGORY_ID), id);
		List<ITransferObject> pcLst = pcBean.getList(criteria);
		ProductCategory pc = new ProductCategory();
		if (pcLst.size() > 0) pc = (ProductCategory)pcLst.get(0);
		String text = "" + pc.getGroup().getName() + " -> " + pc.getName();
		return text;
	}

	@SuppressWarnings("unchecked")
	private int getMaxCategory() throws SQLException {
	    int code = 0;
		if (getScale().getScaleModel() == ScaleModel.BIZERBA) {
			if (maxBizerbaCategory > 0) return ++maxBizerbaCategory;
			HashMap<Integer,String> map = new HashMap<Integer,String>();
			try {
				//Abrimos el fichero a leer en este caso el HWST donde al menos tiene que estar HWGN(2) y HWGT (21)
				File f = new File(getScale().getProgramPath() + "IF/Hwst_mt.if");
				if (f.exists()) {
					HwstIf hwst = getHwstFieldsZiel(f);
					if (hwst.isValid()) {
						f = new File(getScale().getProgramPath() + "Ascii/HWST_MT.TXT");
						if (f.exists()) {
							map = getHwstData(f, hwst);
						}
						else {
							AonUtil.addErrorMessage(" ERROR: No existe el fichero de datos de familias: '" + getScale().getProgramPath() + "Ascii/HWST_MT.TXT'");
						}
					}
					else {
						AonUtil.addErrorMessage(" ERROR: Falta algun campo en el fichero de configuracion de familias (HWGN y HWGT son requeridos): '" + getScale().getProgramPath() + "IF/Hwst_mt.if'");
					}
				}
				else {
					AonUtil.addErrorMessage(" ERROR: No existe el fichero de configuracion de familias: '" + getScale().getProgramPath() + "IF/Hwst_mt.if'");
				}
			} catch (IOException e) {
				AonUtil.addErrorMessage(" ERROR: No se puede tener acceso a los archivos de datos.");
			}

			if (map != null && !map.isEmpty()) {
				Object[] keyArray = map.keySet().toArray();
				Arrays.sort(keyArray, new Comparator() {
						public int compare(final Object o1, final Object o2){
							final int i1 = Integer.parseInt("" + o1);
							final int i2 = Integer.parseInt("" + o2);
							if (i2 < i1) return 1;
							return -1;
						}
					}
				);
				Iterator iter = (Arrays.asList(keyArray)).iterator();
				while (iter.hasNext()) {
					code = Integer.parseInt("" + iter.next());
				}
				maxBizerbaCategory = code + 1;
			}
		}
		else {
			String database = getDatabasePath(getScale());
			String select = getScale().getScaleModel().getMaxCategory(locale);
			Connection c = getConnection(database);
			PreparedStatement ps = c.prepareStatement(select);
			ResultSet rs = ps.executeQuery();
	       	if (rs.next()) code = rs.getInt(1);
	       	rs.close();
	       	ps.close();
	       	c.close();
		}
		return (code + 1);
	}

	@SuppressWarnings("unchecked")
	private int getMaxSubcategory(int id) throws SQLException {
	    int code = 0;
		if (getScale().getScaleModel() == ScaleModel.BIZERBA) {
			if (maxBizerbaSubCategory > 0) return ++maxBizerbaSubCategory;
			ArrayList<WgstData> map = new ArrayList<WgstData>();
			try {
				//Abrimos el fichero a leer en este caso el HWST donde al menos tiene que estar HWGN(2) y HWGT (21)
				File f = new File(getScale().getProgramPath() + "IF/Wgst_mt.if");
				if (f.exists()) {
					WgstIf wgst = getWgstFieldsZiel(f);
					if (wgst.isValid()) {
						f = new File(getScale().getProgramPath() + "Ascii/WGST_MT.TXT");
						if (f.exists()) {
							map = getWgstData(f, wgst);
						}
						else {
							AonUtil.addErrorMessage(" ERROR: No existe el fichero de datos de familias: '" + getScale().getProgramPath() + "Ascii/WGST_MT.TXT'");
						}
					}
					else {
						AonUtil.addErrorMessage(" ERROR: Falta algun campo en el fichero de configuracion de familias (WGNU, HWGN y WGTE son requeridos): '" + getScale().getProgramPath() + "IF/Wgst_mt.if'");
					}
				}
				else {
					AonUtil.addErrorMessage(" ERROR: No existe el fichero de configuracion de familias: '" + getScale().getProgramPath() + "IF/Wgst_mt.if'");
				}
			} catch (IOException e) {
				AonUtil.addErrorMessage(" ERROR: No se puede tener acceso a los archivos de datos.");
			}

			if (map != null && !map.isEmpty()) {
				Object[] keyArray = map.toArray();
				Arrays.sort(keyArray, new Comparator() {
						public int compare(final Object o1, final Object o2){
							final int i1 = ((WgstData)o1).getWGNU();
							final int i2 = ((WgstData)o2).getWGNU();
							if (i2 < i1) return 1;
							return -1;
						}
					}
				);
				Iterator iter = (Arrays.asList(keyArray)).iterator();
				while (iter.hasNext()) {
					code = ((WgstData)iter.next()).getWGNU();
				}
				maxBizerbaSubCategory = code + 1;
			}
		}
		else {
			String database = getDatabasePath(getScale());
			String select = getScale().getScaleModel().getMaxSubcategory(locale);
			Connection c = getConnection(database);
			PreparedStatement ps = c.prepareStatement(select);
			ResultSet rs = ps.executeQuery();
	       	if (rs.next()) code = rs.getInt(1);
	       	rs.close();
	        ps.close();
	        c.close();
		}
		return code + 1;
	}

}
