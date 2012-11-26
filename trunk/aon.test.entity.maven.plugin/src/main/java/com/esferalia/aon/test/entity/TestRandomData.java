package com.esferalia.aon.test.entity;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Random;

import org.apache.commons.lang.RandomStringUtils;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.enumeration.IStringEnum;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.product.util.DiscountExpression;

public class TestRandomData {
	
	public static String getString(int length) {
		return RandomStringUtils.randomAlphanumeric(length);
	}
	
	public static Date getDate() {
		return new Date();
	}
	
	public static double getDouble() {
		Random random = new Random();
		return CommonUtil.round(random.nextDouble() * 1000);
	}
	
	public static int getInt() {
		Random random = new Random();
		return random.nextInt(999999);
	}
	
	public static boolean getBoolean() {
		Random random = new Random();
		return random.nextBoolean();
	}
	
	public static int getEnumOrdinal(Class<?> enumeration) {
		Field[] fields = enumeration.getDeclaredFields();
		int i =  0;
		for (Field f : fields) {
			if (f.isEnumConstant()) {
			    i++;
		    }
		}
		Random random = new Random();
		return random.nextInt(i);
	}

	public static String getValueFor(Class<?> clazz, int length) {
		if (String.class.equals(clazz)) {
			return "\"" + TestRandomData.getString(length) + "\"";

		} else if (boolean.class.equals( clazz )) {
			return Boolean.toString( TestRandomData.getBoolean());
		
		} else if (int.class.equals( clazz )) {
			return Integer.toString( TestRandomData.getInt());
		
		} else if (Integer.class.equals( clazz )) {
			return Integer.toString( TestRandomData.getInt());
		
		} else if (int.class.equals( clazz )) {
			return Double.toString( TestRandomData.getInt());
		
		} else if (double.class.equals( clazz )) {
			return Double.toString( TestRandomData.getDouble());
		
		} else if (Double.class.equals( clazz )) {
			return Double.toString( TestRandomData.getDouble());
		
		} else if (Date.class.equals( clazz )) {
			return "new java.util.Date()";
		
		} else if (ITransferObject.class.isAssignableFrom(clazz)) {
			return "new TestEntity" + clazz.getSimpleName() + "().getEntity()"; 
//		} else if (IStringEnum.class.isAssignableFrom(clazz)) {
//			int ordinal = TestRandomData.getEnumOrdinal(clazz);
//			return clazz.getName()+".values()[" + ordinal + "].getValue()";
		} else if (Enum.class.isAssignableFrom(clazz)) {
			int ordinal = TestRandomData.getEnumOrdinal(clazz);
			return clazz.getName()+".values()[" + ordinal + "]"; 
		} else if (DiscountExpression.class.equals(clazz)) {
			Random random = new Random();
			int dto =  random.nextInt(100);
			return "new com.code.aon.product.util.DiscountExpression(\""+dto+"\")"; 
		}
		throw new IllegalArgumentException("No hay valor por defecto para " + clazz.getName() + "(" + clazz + ")");
	}
	
}
