package com.code.aon.ui.finance;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;

import com.code.aon.common.AonException;

public class DBFWriter {

	private BufferedOutputStream stream;

	private DBFField fields[];

	private String encoding;

	public DBFWriter(OutputStream outputstream, DBFField aDBFField[], int recCount)
			throws AonException {
		stream = null;
		fields = null;
		encoding = null;
		init(outputstream, aDBFField, recCount);
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	private void init(OutputStream outputstream, DBFField aDBFField[], int recCount)
			throws AonException {
		fields = aDBFField;
		try {
			stream = new BufferedOutputStream(outputstream);
			writeHeader( recCount );
			for (int i = 0; i < aDBFField.length; i++) {
				writeFieldHeader(aDBFField[i]);

			}
			stream.write(13);
			stream.flush();
		} catch (Exception exception) {
			throw new AonException(exception);
		}
	}

	private void writeHeader( int recCount ) throws IOException {
		byte abyte0[] = new byte[16];
		abyte0[0] = 3;
		Calendar calendar = Calendar.getInstance();
		abyte0[1] = (byte) (calendar.get(1) - 1900);
		abyte0[2] = (byte) (calendar.get(2) + 1);
		abyte0[3] = (byte) calendar.get(5);
		abyte0[4] = (byte) (recCount % 256);
		abyte0[5] = (byte) ((recCount / 256) % 256);
		abyte0[6] = (byte) ((recCount / 0x10000) % 256);
		abyte0[7] = (byte) ((recCount / 0x1000000) % 256);
		int i = (fields.length + 1) * 32 + 1;
		abyte0[8] = (byte) (i % 256);
		abyte0[9] = (byte) (i / 256);
		int j = 1;
		for (int k = 0; k < fields.length; k++) {
			j += fields[k].getLength();

		}
		abyte0[10] = (byte) (j % 256);
		abyte0[11] = (byte) (j / 256);
		abyte0[12] = 0;
		abyte0[13] = 0;
		abyte0[14] = 0;
		abyte0[15] = 0;
		stream.write(abyte0, 0, abyte0.length);
		for (int l = 0; l < 16; l++) {
			abyte0[l] = 0;

		}
		stream.write(abyte0, 0, abyte0.length);
	}

	private void writeFieldHeader(DBFField DBFField) throws IOException {
		byte abyte0[] = new byte[16];
		String s = DBFField.getName();
		int i = s.length();
		if (i > 10) {
			i = 10;
		}
		for (int j = 0; j < i; j++) {
			abyte0[j] = (byte) s.charAt(j);

		}
		for (int k = i; k <= 10; k++) {
			abyte0[k] = 0;

		}
		abyte0[11] = (byte) DBFField.getType();
		abyte0[12] = 0;
		abyte0[13] = 0;
		abyte0[14] = 0;
		abyte0[15] = 0;
		stream.write(abyte0, 0, abyte0.length);
		for (int l = 0; l < 16; l++) {
			abyte0[l] = 0;

		}
		abyte0[0] = (byte) DBFField.getLength();
		abyte0[1] = (byte) DBFField.getDecimalCount();
		stream.write(abyte0, 0, abyte0.length);
	}

	public void addRecord(Object aobj[]) throws AonException {
		if (aobj.length != fields.length) {
			throw new AonException(
					"Error adding record: Wrong number of values. Expected "
							+ fields.length + ", got " + aobj.length + ".");
		}
		int i = 0;
		for (int j = 0; j < fields.length; j++) {
			i += fields[j].getLength();

		}
		byte abyte0[] = new byte[i];
		int k = 0;
		for (int l = 0; l < fields.length; l++) {
			String s = fields[l].format(aobj[l]);
			byte abyte1[];
			try {
				if (encoding != null) {
					abyte1 = s.getBytes(encoding);
				} else {
					abyte1 = s.getBytes();
				}
			} catch (UnsupportedEncodingException unsupportedencodingexception) {
				throw new AonException(unsupportedencodingexception);
			}
			for (int i1 = 0; i1 < fields[l].getLength(); i1++) {
				abyte0[k + i1] = abyte1[i1];

			}
			k += fields[l].getLength();
		}

		try {
			stream.write(32);
			stream.write(abyte0, 0, abyte0.length);
			stream.flush();
		} catch (IOException ioexception) {
			throw new AonException(ioexception);
		}
	}

	public void close() throws AonException {
		try {
			stream.write(26);
			stream.close();
		} catch (IOException ioexception) {
			throw new AonException(ioexception);
		}
	}
}
