package com.code.aon.ui.ecommerce.controller;

import java.util.Set;
import java.util.WeakHashMap;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

public class ShoppingCartMap {

		private WeakHashMap<String, CartItem> map;
		
		private ShoppingCartMap( ) {
				map = new WeakHashMap<String, CartItem>();
		}

		public static ShoppingCartMap getInstance( ServletContext sc ) {
//			FacesContext.getCurrentInstance().getExternalContext().
			ShoppingCartMap scm = (ShoppingCartMap) sc.getAttribute( "cartMap" );
			if (scm == null) {
				scm = new ShoppingCartMap();
				sc.setAttribute( "cartMap", scm );
			}
			return scm;
		}

		public void put( String key,CartItem value) {
			map.put(key,value);
		}

		public Set<String> keySet() {
			return map.keySet();
		}

		public CartItem get( String key ){
			return (CartItem) map.get( key );
		}


}