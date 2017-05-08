package com.esferalia.aon.file.seres.connect;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.code.aon.file.format.core.DiskRegisterLoader;
import com.code.aon.file.format.model.AbstractFileFiller;
import com.code.aon.file.format.model.Fd0Exception;
import com.esferalia.aon.file.seres.connect.delivery.v4.data.RECTL;
import com.esferalia.aon.file.seres.connect.delivery.v4.data.SEH1B;
import com.esferalia.aon.file.seres.connect.delivery.v4.data.SEH1D;
import com.esferalia.aon.file.seres.connect.delivery.v4.data.SEH1G;
import com.esferalia.aon.file.seres.connect.delivery.v4.data.SEH1L;

public class ConnectDelivery extends AbstractFileFiller {
	
	private static String RECTL = "RECTL";
	private static String SEH1C = "SEH1C";
	private static String SEH1D = "SEH1D";
	private static String SEH1P = "SEH1P";
	private static String SEH1L = "SEH1L";
	private static String SEH1G = "SEH1G";
	private static String SEH1B = "SEH1B";
	
	private RECTL rectl;
	private Map<Integer, List<Integer>> seh1pMap;
	
	public ConnectDelivery(RECTL rectl, Map<Integer, List<Integer>> seh1pMap, PrintWriter writer) throws FileNotFoundException, UnsupportedEncodingException {
		super(writer);
		if (rectl == null)  {
			throw new IllegalArgumentException("El registro RECTL no puede ser nulo!");
		}
		this.rectl = rectl;
		this.seh1pMap = seh1pMap;
		
		InputStream input = null;
		input = ConnectDelivery.class.getResourceAsStream("/com/esferalia/aon/file/seres/connect/delivery/v4/xml/RECTL.xml");
		DiskRegisterLoader.load(input, manager);
		input = ConnectDelivery.class.getResourceAsStream("/com/esferalia/aon/file/seres/connect/delivery/v4/xml/SEH1C.xml");
		DiskRegisterLoader.load(input, manager);
		input = ConnectDelivery.class.getResourceAsStream("/com/esferalia/aon/file/seres/connect/delivery/v4/xml/SEH1D.xml");
		DiskRegisterLoader.load(input, manager);
		input = ConnectDelivery.class.getResourceAsStream("/com/esferalia/aon/file/seres/connect/delivery/v4/xml/SEH1P.xml");
		DiskRegisterLoader.load(input, manager);
		input = ConnectDelivery.class.getResourceAsStream("/com/esferalia/aon/file/seres/connect/delivery/v4/xml/SEH1L.xml");
		DiskRegisterLoader.load(input, manager);
		input = ConnectDelivery.class.getResourceAsStream("/com/esferalia/aon/file/seres/connect/delivery/v4/xml/SEH1G.xml");
		DiskRegisterLoader.load(input, manager);
		input = ConnectDelivery.class.getResourceAsStream("/com/esferalia/aon/file/seres/connect/delivery/v4/xml/SEH1B.xml");
		DiskRegisterLoader.load(input, manager);
	}
	
	@Override
	public ArrayList<Exception> create() {
		try{
			Map<String,Object> properties = new HashMap<String,Object>();
			
			properties.put(RECTL, rectl);
			createLine(RECTL, properties);
			
			if(rectl.seh1c==null) {
				Fd0Exception e = new Fd0Exception( "SEH1C", "Cabecera. La entidad 'SEH1C' es obligatoria");
				exceptions.add (e);
			} else {
				properties.put(SEH1C , rectl.seh1c);
				createLine(SEH1C, properties);
			}
			
			if(rectl.seh1dList==null || rectl.seh1dList.isEmpty()) {
				Fd0Exception e = new Fd0Exception( "SEH1D", "Información de partes. La entidad 'SEH1D' es obligatoria");
				exceptions.add (e);
			} else {
				for (SEH1D value: rectl.seh1dList) {
					properties.put(SEH1D, value);
					createLine(SEH1D, properties);
				}
			}
			
			if(rectl.seh1pList==null || rectl.seh1pList.isEmpty()) {
				Fd0Exception e = new Fd0Exception( "SEH1P", "Secuencia de embalajes. La entidad 'SEH1P' es obligatoria");
				exceptions.add (e);
			} else {
				if(seh1pMap==null || seh1pMap.isEmpty()) {
					Fd0Exception e = new Fd0Exception( "SEH1P", "Secuencia de embalajes. La entidad 'SEH1P' es obligatoria");
					exceptions.add (e);
				} else {
//					System.out.println(seh1pMap);
					List<Integer> containerList = new ArrayList<>();
					containerList.addAll(seh1pMap.keySet());
					
					seh1pMap.keySet().stream().sorted().forEach(key -> {
						List<Integer> list = seh1pMap.get(key);
						containerList.removeAll(list);	
					});
//					System.out.println(containerList);
					
					
//					SEH1P palet = rectl.seh1pList.stream()
//						.filter(o -> o.getTipoDeEmbalaje_Codificado().equals("201"))
//						.findFirst().orElse(null);
//					
//					List<SEH1P> embases = rectl.seh1pList.stream()
//							.filter(o -> !o.getTipoDeEmbalaje_Codificado().equals("201"))
//							.collect(Collectors.toList());
					
					
//					properties.put(SEH1P, palet);
//					createLine(SEH1P, properties);
					
					int linesCount = rectl.seh1lList.size();
					containerList.stream().sorted().forEach(containerKey -> {
						
						int index = containerKey - linesCount;
						properties.put(SEH1P, rectl.seh1pList.get(index-1));
						createLine(SEH1P, properties);
						
						seh1pMap.get(containerKey).stream().sorted().forEach(containerKey2 -> {
							int index2 = containerKey2 - linesCount;
							
							properties.put(SEH1P, rectl.seh1pList.get(index2-1));
//							properties.put(SEH1P, embases.get(index2-1));
							createLine(SEH1P, properties);
							
							for (Integer index3: seh1pMap.get(containerKey2)) {
								SEH1L value = rectl.seh1lList.get(index3-1);
								properties.put(SEH1L, value);
								createLine(SEH1L, properties);
							}
						});
						
					});
				}				
			}
			
			for (SEH1G value: rectl.seh1gList) {
				properties.put(SEH1G, value);
				createLine(SEH1G, properties);
			}
			
			for (SEH1B value: rectl.seh1bList) {
				properties.put(SEH1B, value);
				createLine(SEH1B, properties);
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			if ( ex instanceof Fd0Exception ) {
				exceptions.add (ex);
			} else {
				Fd0Exception e = new Fd0Exception( ex.getMessage(),rectl.toString());
				exceptions.add (e);
			}
		}
		output.flush();
//		writeErrorsFile();
		return exceptions;
	}
	
}
