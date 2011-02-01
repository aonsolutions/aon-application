package com.code.aon.ui.marketplace.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.ActionEvent;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.customer.dao.ICustomerAlias;
import com.code.aon.marketplace.plu.PluItem;
import com.code.aon.marketplace.plu.PluItemManager;
import com.code.aon.marketplace.plu.PluItemSet;
import com.code.aon.marketplace.plu.Ticket;
import com.code.aon.marketplace.plu.TicketManager;
import com.code.aon.product.Item;
import com.code.aon.product.ItemPos;
import com.code.aon.product.Product;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.config.controller.ApplicationParameterController;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.product.controller.ItemController;

public class ItemDumpController extends ItemController {

	private String msg;
	private UploadedFile updateFile;
	private Customer customer;
	
	public ItemDumpController() {
		super();
	}
	
	/**
	 * @return the msg
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * @param msg the msg to set
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}

	/**
	 * @return the updateFile
	 */
	public UploadedFile getUpdateFile() {
		return updateFile;
	}

	/**
	 * @param updateFile the updateFile to set
	 */
	public void setUpdateFile(UploadedFile updateFile) {
		this.updateFile = updateFile;
	}

	
	
	/**
	 * @return the customer
	 */
	public Customer getCustomer() {
		return customer;
	}

	/**
	 * @param customer the customer to set
	 */
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	@SuppressWarnings("unused")
	public void onDumpItems(ActionEvent event) {
		this.msg = "";
		String path = System.getProperty("user.dir") + File.separator + "mysql";
		File prv_directory = new File(path);
		if (!prv_directory.exists()) {
			prv_directory.mkdir();
		}
		path += File.separator + "dump";
		prv_directory = new File(path);
		if (!prv_directory.exists()) {
			prv_directory.mkdir();
		}
		GregorianCalendar gc = new GregorianCalendar();
		String file = "C:/Archivos de programa/Hydra/prueba.dat"; 
/*			path +
			File.separator + 
			"plu_" + 
			gc.get(GregorianCalendar.DATE) + "_" +
			(gc.get(GregorianCalendar.MONTH)+1) + "_" +
			gc.get(GregorianCalendar.YEAR) + "_" +
			gc.get(GregorianCalendar.HOUR) + "_" +
			gc.get(GregorianCalendar.MINUTE) + "_" +
			gc.get(GregorianCalendar.SECOND) +
			".dat";*/
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(file);
		} catch (FileNotFoundException e) {
		}
		
		try {
			List<ITransferObject> items;
			IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(itemBean.getFieldName(IProductAlias.ITEM_STATUS),ProductStatus.ACTIVE);
			items = itemBean.getList(criteria);
			Iterator iter = items.iterator();
			
			while (iter.hasNext()){
				Item  item = (Item)iter.next();
				
				List<ITransferObject> itemposlst;
				IManagerBean itemPosBean = BeanManager.getManagerBean(ItemPos.class);
				Criteria criteriaip = new Criteria();
				criteriaip.addEqualExpression(itemPosBean.getFieldName(IProductAlias.ITEM_POS_ITEM_ID),item.getId());
				itemposlst = itemPosBean.getList(criteriaip);
				Iterator itempositer = itemposlst.iterator(); 
				if (itempositer.hasNext()){
					String cad = zeroFill(""+item.getId(), 6);
					ItemPos itemPos = (ItemPos)itempositer.next();
					cad += zeroFill(itemPos.getPlu(), 4);
					cad += zeroFill(MOSTRADOR, 4); //Obligatorio en la carga
					//cad += item.getProduct().getCategory().getName(); //SI o NO
					cad += spaceFill(itemPos.getShortDescription(), 21);
					cad += zeroFill(formatDecimal(getProvider().getRealTotalPrice(item)),7);
					pw.println(cad);
					pw.flush();
				}

			}
					
			pw.close();
			this.msg = "FICHERO "+file;
		} catch (ManagerBeanException e) {
			pw.close();
			this.msg = "ERROR "+e.getCause();
		}
		
	}

	private String formatDecimal(double realTotalPrice) {
		DecimalFormat df = new DecimalFormat("0.00");
		String ret = df.format(realTotalPrice);
		ret = ret.replaceAll(",","");
		return ret;
	}

	public String zeroFill(String ret, int num){
		if (ret==null){
			return null;
		}
		while (ret.length() < num){
			ret = "0" + ret;
		}
		return ret;
	}

	public String spaceFill(String ret, int num){
		if (ret==null){
			return null;
		}
		while (ret.length() < num){
			ret = ret + " ";
		}
		return ret;
	}

	public void onResetRecover(MenuEvent event){
		customer = null;
		try{
			FacesContext ctx = FacesContext.getCurrentInstance(); 
			ValueBinding vb = ctx.getApplication().createValueBinding( "#{appParams}" );
			ApplicationParameterController paramsController = (ApplicationParameterController) vb.getValue(ctx);
			String customerId = paramsController.getParameters().get(CLIENT).getValue();
			IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
			Criteria criteria = new Criteria();
			criteria.addExpression(customerBean.getFieldName(ICustomerAlias.CUSTOMER_ID),customerId);
			List<ITransferObject> customerLst = customerBean.getList(criteria);
			if (customerLst.size()>0)
				customer = (Customer)customerLst.get(0);
		}catch (ManagerBeanException e) {
		} catch (ExpressionException e) {
		}
	}
	
	
	@SuppressWarnings("unused")
	public void onRecoverItems(ActionEvent event) throws IOException {
		InputStream is = updateFile.getInputStream();
		//InputStream is = new FileInputStream("C:/Archivos de programa/Hydra/prueba.dat");
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line = null;
		PluItem item;
		PluItemManager im = new PluItemManager();
		try{
			while ((line = br.readLine()) != null){
				if (line.length() == 42){
					List<String> l = new ArrayList<String>();
					l.add(line.substring(0,5));
					l.add(line.substring(6,9));
					l.add(line.substring(10,13));
					l.add(line.substring(14,34));
					l.add(line.substring(35,41));
					item = parseItem(l);
					im.addItem(item);
				}
			}
			Collection<PluItemSet> pisLst = im.getAll();
			Iterator<PluItemSet> iter = pisLst.iterator();
			while (iter.hasNext()){
				PluItemSet pis = iter.next();
				List<PluItem> itemLst = pis.getPluItemlist();
				Iterator<PluItem> itemIter = itemLst.iterator(); 
				while (itemIter.hasNext()){
					createItem(itemIter.next());
				}
			}
		}catch (Exception e) {
			this.msg = "ERROR, FORMATO ERRONEO <"+(line==null?"":line)+"> "+e.getMessage();
		}
	}

	@SuppressWarnings("unused")
	public void onRecoverTickets(ActionEvent event) throws IOException {
		InputStream is = updateFile.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line = null;
		Ticket ticket;
		TicketManager tm = new TicketManager();
/*		try{
			while ((line = br.readLine()) != null){
				if (line.length() == 42){
					List<String> l = new ArrayList<String>();
					l.add(line.substring(0,5));
					l.add(line.substring(6,9));
					l.add(line.substring(6,9));
					l.add(line.substring(6,9));
					l.add(line.substring(6,9));
					while (st.hasMoreElements()){
						String next = st.nextToken(); 
						if (!SEPARATOR.equals(next))
							l.add(next);
					}
					ticket = parseTicket(l);
					tm.addTicket(ticket);
				}
			}
			List<TicketSet> tsLst = tm.getAll();
			Iterator<TicketSet> iter = tsLst.iterator();
			while (iter.hasNext()){
				TicketSet ts = iter.next();
				List<Ticket> ticketLst = ts.getTicketlist();
				Iterator<Ticket> ticketIter = ticketLst.iterator(); 
				while (ticketIter.hasNext()){
					createDelivery(ticketIter.next());
				}
			}
		}catch (Exception e) {
			this.msg = "ERROR, FORMATO ERRONEO <"+(line==null?"":line)+"> "+e.getMessage();
		}*/
	}

	private void createItem(PluItem pluitem) throws ManagerBeanException, ExpressionException {
		FacesContext ctx = FacesContext.getCurrentInstance(); 
		ValueBinding vb = ctx.getApplication().createValueBinding( "#{product}" );
		BasicController productController = (BasicController)vb.getValue(ctx);
		productController.onReset(null);
		Product product = (Product)productController.getTo();
		product.setId(new Integer(pluitem.getCod()));
		product.setName(pluitem.getTxt(false));
		productController.onAccept(null);
		
		vb = ctx.getApplication().createValueBinding( "#{item}" );
		ItemController itemController = (ItemController)vb.getValue(ctx);
		itemController.onReset(null);
		Item item = (Item)itemController.getTo();
		item.setProduct(product);
		item.setDescription(pluitem.getTxt(false));
		item.setPrice(pluitem.getPrc());
		itemController.onAccept(null);

		vb = ctx.getApplication().createValueBinding( "#{itemPos}" );
		BasicController itemPosController = (BasicController)vb.getValue(ctx);
		itemPosController.onReset(null);
		ItemPos itemPos = (ItemPos)itemPosController.getTo();
		itemPos.setItem(item);
		itemPos.setPlu(""+pluitem.getPlu());
		itemPos.setShortDescription(pluitem.getTxt(true));
		itemPosController.onAccept(null);
	}

	private PluItem parseItem(List<String> l) throws ParseException{
		PluItem item = new PluItem();
		item.setCod(Integer.parseInt(l.get(0)));
		item.setPlu(Integer.parseInt(l.get(1)));
		item.setMos(Integer.parseInt(l.get(2)));
		item.setTxt(l.get(3));
		item.setPrc(Double.parseDouble(l.get(4))==0?0:Double.parseDouble(l.get(4))/100);
		return item;
	}

	private static final String CLIENT = "CLIENTE_BALANZA";
	private static final String MOSTRADOR = "1";
}
