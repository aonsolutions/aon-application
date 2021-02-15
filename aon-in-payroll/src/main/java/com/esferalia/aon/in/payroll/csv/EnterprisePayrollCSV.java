package com.esferalia.aon.in.payroll.csv;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
public class EnterprisePayrollCSV {
	public static void write(OutputStream outputStream, Collection<IEnterprisePayroll> payrolls) throws JsonGenerationException, JsonMappingException, IOException {
		
		
		
		CsvSchema schema = CsvSchema.builder()
		        .addColumn("Empleado")
		        .addColumn("Centro de trabajo")
		        .addColumn("Bruto", CsvSchema.ColumnType.NUMBER)
		        .addColumn("Seg. Social empleado", CsvSchema.ColumnType.NUMBER)
		        .addColumn("IRPF", CsvSchema.ColumnType.NUMBER)
		        .addColumn("Líquido", CsvSchema.ColumnType.NUMBER)
		        .addColumn("Seg. Social empresa", CsvSchema.ColumnType.NUMBER)
		        .addColumn("Coste total", CsvSchema.ColumnType.NUMBER)
		        .addColumn("Seg. Social total", CsvSchema.ColumnType.NUMBER)
		        .addColumn("Bonificaciones", CsvSchema.ColumnType.NUMBER)
		        .addColumn("Base Cont. Comunes", CsvSchema.ColumnType.NUMBER)
		        .addColumn("Base IRPF", CsvSchema.ColumnType.NUMBER)
		        .setUseHeader(true)
		        .build();
		
		
		CsvMapper mapper = new CsvMapper();
		
		mapper.writer(schema).writeValue(outputStream, payrolls);
	}
}
