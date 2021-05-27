package com.esferalia.aon.occam.impl.jooq.dao.d2_deposit;

import java.util.Vector;

public class CodeGenerator {
	
	public static Vector<Integer> ba22(){
		Vector<Integer> v = new Vector<Integer>();
		for(Integer i = 32000; i < 32800 ; i+=100 ){
			v.add(i);
			if(i == 32300){
				v.add(32320);
				v.add(32330);
				v.add(32390);
			}
			if(i ==32500){
				v.add(32580);
				v.add(32581);
				v.add(32582);
				v.add(32590);
			}
		}
		v.add(30000);
		
		Vector<Integer> v9 = with9(v);
		Vector<Integer> v98 = with98(v);
		v.addAll(v9);
		v.addAll(v98);
		
		return v;
	}
	
	public static Vector<Integer> ba21(){
		Vector<Integer> v = new Vector<Integer>();
		v.add(20000);
		
		for(Integer i =21000;i < 22000 ;i+=100 ){
			v.add(i);
			if(i == 21100){
				v.add(21110);
				v.add(21120);
			}
		}
		v.add(22000);
		v.add(23000);
		
		for(Integer j = 31000 ; j< 31800; j+=100){
			v.add(j);
			if(j == 31200){
				v.add(31220);
				v.add(31230);
				v.add(31290);
			}
		}
		
		Vector<Integer> v9 = with9(v);
		Vector<Integer> v98 = with98(v);
		v.addAll(v9);
		v.addAll(v98);
		
		return v;
	}
	
	public static Vector<Integer> pa(){
		Vector<Integer> v = new Vector<Integer>();
		
		for(Integer i = 40100; i<42000; i +=100){
			v.add(i);
			if(i == 41400){
				v.add(41430);
				v.add(41490);
			}
		}
		v.add(42100);
		v.add(42110);
		v.add(42120);
		v.add(42130);
		for(Integer j = 49100 ; j <49600; j +=100){
			if(j != 49400)
				v.add(j);
		}
		
		Vector<Integer> v9 = with9(v);
		Vector<Integer> v98 = with98(v);
		v.addAll(v9);
		v.addAll(v98);
		
		return v;
	}
 	
	public static Vector<Integer> pna1(){
		Vector<Integer> v = new Vector<Integer>();
		for(Integer i = 59100 ; i<59500; i +=100){
			v.add(i);
		}
		for(Integer i = 50010 ; i<50140; i +=10){
			v.add(i);
		}
		
		Vector<Integer> v9 = with9(v);
		Vector<Integer> v98 = with98(v);
		v.addAll(v9);
		v.addAll(v98);
		
		return v;
	}
	
	public static Vector<Integer> pna2(){
		Vector<Integer> v = new Vector<Integer>();
		
		for(Integer i = 511; i<519 ;  i++){
			v.addAll(from01To13(i));
		}
		v.addAll(from01To13(526));
		v.addAll(from01To13(524));
		v.addAll(from01To13(531));
		v.addAll(from01To13(532));
		
		Vector<Integer> v9 = with9(v);
		v.addAll(v9);
		v.addAll(from01To13(525));
		return v;
	}
	
	public static Vector<Integer> a1(){
		Vector<Integer> v = new Vector<Integer>();
		Integer a = 8001000;
		Integer b = 8002000;
		Integer c = 8003000;
		Integer d =8004000;
		Integer e = 8005000;
		Integer f = 8006000;
		Integer g = 8007000;
		Integer h = 8008000;
		
		for(Integer i = 0; i<25; i++){
			v.add(a+i);
			v.add(b+i);
			v.add(c+i);
			v.add(d+i);
			v.add(e+i);
			v.add(f+i);
			v.add(g+i);
			v.add(h+i);
		}
		return v;
	}
	public static Vector<Integer> a11(){
		Vector<Integer> v = new Vector<Integer>();
		Integer a = 8001000;
		Integer b = 8002000;
		Integer c = 8003000;
		Integer d =8004000;
		Integer e = 8005000;
		Integer f = 8006000;
		Integer g = 8007000;
		Integer h = 8008000;
		
		for(Integer i = 25; i<56; i++){
			v.add(a+i);
			v.add(b+i);
			v.add(c+i);
			v.add(d+i);
			v.add(e+i);
			v.add(f+i);
			v.add(g+i);
			v.add(h+i);
		}
		return v;
	}
	public static Vector<Integer> a2(){
		Vector<Integer> v = new Vector<Integer>();
		Integer a = 8021000;
		Integer b = 8022000;
		
		for(Integer i = 0; i< 33; i++){
			v.add(a+i);
			v.add(b+i);
		}
		
		return v;
	}
	
	public static Vector<Integer> a3(){
		Vector<Integer> v = new Vector<Integer>();
		Integer a = 8031000;
		Integer b = 8032000;
		Integer c = 8033000;
		Integer d = 8034000;
		for(Integer i = 0; i< 33; i++){
			v.add(a+i);
			v.add(b+i);
			v.add(c+i);
			v.add(d+i);
		}
		
		return v;
	}
	
	public static Vector<Integer> a4(){
		Vector<Integer> v = new Vector<Integer>();
		Integer a = 8041000;
		Integer b = 8042000;
		Integer c = 8043000;
		Integer d = 8044000;
		for(Integer i = 0; i< 33; i++){
			v.add(a+i);
			v.add(b+i);
			v.add(c+i);
			v.add(d+i);
		}
		
		return v;
	}
	
	public static Vector<Integer> a5(){
		Vector<Integer> v = new Vector<Integer>();
		Integer a = 8051000;
		Integer b = 8052000;
		Integer c = 8053000;

		for(Integer i = 0; i< 33; i++){
			v.add(a+i);
			v.add(b+i);
			v.add(c+i);
		}
		
		return v;
	}
	
	public static Vector<Integer> a6(){
		Vector<Integer> v = new Vector<Integer>();
		Integer a = 8061000;
		Integer b = 8062000;
		Integer c = 8063000;

		for(Integer i = 0; i< 33; i++){
			v.add(a+i);
			v.add(b+i);
			v.add(c+i);
		}
		
		return v;
	}
	
	public static Vector<Integer> a7(){
		Vector<Integer> v = new Vector<Integer>();
		Integer a = 8071000;
		Integer b = 8072000;
		Integer c = 8073000;
		Integer d = 8074000;
		Integer e = 8075000;
		
		for(Integer i = 0; i< 33; i++){
			v.add(a+i);
			v.add(b+i);
			v.add(c+i);
			v.add(d+i);
			v.add(e+i);
		}
		
		return v;
	}

	public static Vector<Integer> pr(){
		Vector<Integer> v = new Vector<Integer>();
		Integer a = 80810001;
		for(Integer i = 0; i < 4;i++){
			v.add(a+i);
		}
		Integer b = 8081201;
		for(Integer i = 0; i < 9;i++){
			v.add(b+i);
		}
		return v;
	}
	
	public static Vector<Integer> h(){
		Vector<Integer> v = new Vector<Integer>();
		Integer a = 8091001;
		for(Integer i = 0 ; i< 10 ; i++ ){
			v.add(a+i);
		}
		return v;
	}
	
	public static Vector<Integer> from01To13(Integer n){
		Vector<Integer> v = new Vector<Integer>();
		for(Integer i = 1; i<14; i++){
			Integer e = n*100+i;
			v.add(e);
		}
		return v;
	}
	
	public static Vector<Integer> with9(Vector<Integer> v){
		Vector<Integer> vector = new Vector<Integer>();
 		for ( Integer i : v) {
			Integer e = i*10+9;
			vector.add(e);
		}
 		return vector;
	}
	
	public static Vector<Integer> with98(Vector<Integer> v){
		Vector<Integer> vector = new Vector<Integer>();
 		for ( Integer i : v) {
			Integer e = i*100+98;
			vector.add(e);
		}
 		return vector;
	}
	
	public static void printKey(Integer i, String part){
		System.out.println(","+part+i+"("+i+",\""+part+i+"\")");
	}
	
	public static void printConstant(Integer i, String part){
		System.out.println(",D2DepositKey."+part+i);
	}
	public static void printHeaderConstant(Integer i, String part){
		System.out.println(",D2DepositHeaderKey."+part+i);
	}
	
	public static void printFooterConstant(Integer i, String part){
		System.out.println(",D2DepositFooterKey."+part+i);
	}
	
	public static void main(String[] args) {
		
		//Vector<Integer> v = ba21();String part = "BA21";
		//Vector<Integer> v =ba22();String part = "BA22";
		//Vector<Integer> v = pa();String part = "PA";
		//Vector<Integer> v = pna1();String part = "PNA1";
		//Vector<Integer> v = pna2();String part = "PNA2";
		//Vector<Integer> v = a1();String part="A1";
		//Vector<Integer> v = a11();String part="A11";
		//Vector<Integer> v = a2();String part = "A2"; 
		//Vector<Integer> v = a3(); String part = "A3";
		//Vector<Integer> v = a4(); String part = "A4";
		//Vector<Integer> v = a5(); String part = "A5";
		//Vector<Integer> v = a6(); String part = "A6";
		//Vector<Integer> v = a7(); String part = "A7";
		//Vector<Integer> v = pr(); String part = "PR";
		Vector<Integer> v = h(); String part = "H";
		
		for (Integer i : v) {
			//printKey(i, part);
			//printConstant(i,part);
			//printHeaderConstant(i, part);
			printFooterConstant(i, part);
		}

	
		
		
		
	}
	
	
}
