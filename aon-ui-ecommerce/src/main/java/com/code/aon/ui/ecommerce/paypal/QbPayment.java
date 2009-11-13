package com.code.aon.ui.ecommerce.paypal;


import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.ui.ecommerce.controller.CartItem;
import com.code.aon.ui.ecommerce.controller.CreditCardController;
import com.code.aon.ui.ecommerce.controller.ShoppingCartController;
import com.code.aon.ui.ecommerce.controller.ShoppingCartMap;
import com.code.aon.ui.ecommerce.servlet.ECommerceServlet;
import com.code.aon.ui.ecommerce.util.IECommerceConstants;
import com.code.aon.ui.util.AonUtil;

public class QbPayment extends ECommerceServlet {
	
	@Override
	public void init() throws ServletException {
		super.init();
	}
	
	@Override
	public void destroy() {
		super.destroy();
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doGet(req, resp);
//		super.doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doPost(req, resp);
	}
	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
//		String url;
//		url = "https://tpv2.4b.es/simulador/teargral.exe";
//		req.getAttributeNames();
		req.getParameterNames();
		

		
		
		
//		String data = "M978900\r\n1\r\n1\r\ndesc_1\r\n1\r\n300\r\n2\r\ndesc_2\r\n2\r\n600\r\n";
		
		CreditCardController ccc = (CreditCardController) getBean(req, resp, IECommerceConstants.CREDIT_CARD_CONTROLLER);
		
//		ShoppingCartController scc = (ShoppingCartController) AonUtil.getRegisteredBean(IECommerceConstants.SHOPPING_CART_CONTROLLER);
//		String data = ccc.get4bData();

		ShoppingCartMap sc = (ShoppingCartMap) getServletContext().getAttribute("map");
		
//		ShoppingCartController scc = (ShoppingCartController) getBean(req, resp, IECommerceConstants.SHOPPING_CART_CONTROLLER);
		final String LINE_BREAK = "\r\n";
		final String EURO_CODE = "M978";
		
		String data="";
		
		data += EURO_CODE+(sc.getTotal()*100)+LINE_BREAK;
//		data += sc.getQuantity()+LINE_BREAK;
		data += 1+LINE_BREAK;
		
		for(CartItem ci: sc.getList()){
			data += ci.getItem().getId()+LINE_BREAK;
			data += ci.getItem().getDescription()+LINE_BREAK;
			data += ci.getQuantity()+LINE_BREAK;
			data += ci.getTotal()+LINE_BREAK;
		}
		
		
		
		
		
		
//		data = "M978900\r\n1\r\n1\r\ndesc_1\r\n1\r\n300\r\n2\r\ndesc_2\r\n2\r\n600\r\n";
		
		resp.setContentType("text/html");
//		resp.getWriter().println();
		resp.getWriter().write(data);
		
//		super.service(req, resp);
		
	}
	
	@Override
	public void service(ServletRequest req, ServletResponse res)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.service(req, res);
	}
	
}
