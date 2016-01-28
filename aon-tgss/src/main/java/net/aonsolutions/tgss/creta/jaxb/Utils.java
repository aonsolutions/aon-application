package net.aonsolutions.tgss.creta.jaxb;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Calendar;
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

	public static <T> void marshal(T t, OutputStream os, Listener ...listeners) throws JAXBException {
		Marshaller marshaller = newMarshaller(t.getClass());
		marshaller.setListener(new CompositeListener(listeners));
		marshaller.marshal(t, os);
	}

	public static <T> void marshal(T t, XMLStreamWriter xsw) throws JAXBException {
		newMarshaller(t.getClass()).marshal(t, xsw);
	}

	public static <T> void marshal(T t, XMLStreamWriter xsw, Listener ...listeners) throws JAXBException {
		Marshaller marshaller = newMarshaller(t.getClass());
		marshaller.setListener(new CompositeListener(listeners));
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
	
	// -------------------------------------------------------------------------
	
	private static class CompositeListener extends Listener {
		
		private Listener [] listeners;
		
		public CompositeListener(Listener ...listeners) {
			this.listeners = listeners;
		}
		
		@Override
		public void afterMarshal(Object source) {
			for ( Listener  listener: listeners )
				listener.afterMarshal(source);
		}
		
		@Override
		public void beforeMarshal(Object source) {
			for ( Listener  listener: listeners )
				listener.beforeMarshal(source);
		}
	}

	public static String createReferenciaExterna() {
		return UUID.randomUUID().toString().substring(0, 8);
	}

	public static Calendar toCalendar(Periodo periodo) {
		return Utils.toCalendar(periodo.getAnho(), periodo.getMes());
	}

	public static Calendar toCalendar(String anho, String mes) {
		int month = Integer.parseInt(mes) - 1;
		int year = Integer.parseInt(anho);
	
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
	
		// Reset time
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
	
		return calendar;
	}

	public static String toString(CtaCot ctaCot) {
		return String.format("%s%s%s", ctaCot.getProvincia(),
				ctaCot.getRegimen(), ctaCot.getNumero());
	
	}
	
	
	

}
