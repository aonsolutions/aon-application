package com.code.aon.ui.ecommerce.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import javax.servlet.ServletContext;

public class ShoppingCartMap {

		private WeakHashMap<String, CartItem> map;
		private Double total;
		
		private ShoppingCartMap( ) {
				map = new WeakHashMap<String, CartItem>();
		}

		public static ShoppingCartMap getInstance( ServletContext sc ) {
			ShoppingCartMap scm = (ShoppingCartMap) sc.getAttribute( "cartMap" );
			if (scm == null) {
				scm = new ShoppingCartMap();
				scm.setTotal(0.0);
				sc.setAttribute( "cartMap", scm );
			}
			return scm;
		}
	
		public List<CartItem> getList() {
			return new ArrayList<CartItem>(map.values());
		}

		public void put(CartItem value) {
			map.put(value.getItem().getId().toString(),value);
		}

		public CartItem get( String key ){
			return (CartItem) map.get( key );
		}

		public Double getTotal() {
			return total;
		}

		public void setTotal(Double total) {
			this.total = total;
		}

		public Integer getQuantity() {
			return map.size();
		}


}