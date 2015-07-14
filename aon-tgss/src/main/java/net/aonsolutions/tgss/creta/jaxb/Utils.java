package net.aonsolutions.tgss.creta.jaxb;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.time.Month;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Marshaller.Listener;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamWriter;

public class Utils {


	// -----------------------------------------------------------------------

	public static <T> void marshal(T t, Writer writer) throws JAXBException {
		newMarshaller(t.getClass()).marshal(t, writer);
	}

	public static <T> void marshal(T t, OutputStream os) throws JAXBException {
		newMarshaller(t.getClass()).marshal(t, os);
	}

	public static <T> void marshal(T t, OutputStream os, Listener listener) throws JAXBException {
		Marshaller marshaller = newMarshaller(t.getClass());
		marshaller.setListener(listener);
		marshaller.marshal(t, os);
	}

	public static <T> void marshal(T t, XMLStreamWriter xsw) throws JAXBException {
		newMarshaller(t.getClass()).marshal(t, xsw);
	}

	public static <T> void marshal(T t, XMLStreamWriter xsw, Listener listener) throws JAXBException {
		Marshaller marshaller = newMarshaller(t.getClass());
		marshaller.setListener(listener);
		marshaller.marshal(t, xsw);
	}
	// -----------------------------------------------------------------------

	@SuppressWarnings("unchecked")
	public static <T> T unmarshal(Class<T> clazz, InputStream is)
			throws JAXBException {
		return (T) newUnmarshaller(clazz).unmarshal(is);
	}

	@SuppressWarnings("unchecked")
	public static <T> T unmarshal(Class<T> clazz, Reader reader)
			throws JAXBException {
		return (T) newUnmarshaller(clazz).unmarshal(reader);
	}
	// ------------------------------------------------------------------------

	private static Marshaller newMarshaller(Class classToBeBound)
			throws JAXBException {
		Marshaller marshaller = newJAXBContext(classToBeBound)
				.createMarshaller();
//		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		return marshaller;
	}

	private static Unmarshaller newUnmarshaller(Class classToBeBound)
			throws JAXBException {
		return newJAXBContext(classToBeBound).createUnmarshaller();
	}

	private static JAXBContext newJAXBContext(Class classToBeBound)
			throws JAXBException {
		return JAXBContext.newInstance(classToBeBound);
	}

}
