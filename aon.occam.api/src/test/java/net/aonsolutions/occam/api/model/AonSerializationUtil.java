package net.aonsolutions.occam.api.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

class AonSerializationUtil {

	static <T extends Serializable> void test(T expected, Class<T> clazz) throws IOException, ClassNotFoundException {
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
	    	oos.writeObject(expected);
	    }
	    byte[] bytes =  baos.toByteArray();
	    ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
	    try (ObjectInputStream ois = new ObjectInputStream(bais)) {
	    	Object o = ois.readObject();
	    	T actual = clazz.cast(o);
	    	AonAsserts.assertClassEquals(expected, actual);
	    }
	}
}
