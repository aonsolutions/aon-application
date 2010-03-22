package com.code.aon.ui.ecommerce.servlet;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.ui.ecommerce.controller.CartItem;
import com.code.aon.ui.ecommerce.controller.ShoppingCartMap;

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
//		String data = "M978900\r\n1\r\n1\r\ndesc_1\r\n1\r\n300\r\n2\r\ndesc_2\r\n2\r\n600\r\n";
		ShoppingCartMap sc = (ShoppingCartMap) getServletContext().getAttribute("map");
		final String LINE_BREAK = "\r\n";
		final String EURO_CODE = "M978";
		
		String data="";
		data += EURO_CODE+(sc.getTotal()*100)+LINE_BREAK;
		data += sc.getQuantity()+LINE_BREAK;
		
		for(CartItem ci: sc.getList()){
			data += ci.getItem().getId()+LINE_BREAK;
			data += ci.getItem().getDescription()+LINE_BREAK;
			data += ci.getQuantity()+LINE_BREAK;
			data += ci.getTotal()+LINE_BREAK;
		}
		
		resp.setContentType("text/html");
		resp.getWriter().write(data);
	}
	
	@Override
	public void service(ServletRequest req, ServletResponse res)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.service(req, res);
	}
	
}
