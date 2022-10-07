package com.esferalia.aon.occam.impl.jooq.console;


import java.io.PrintStream;

import com.esferalia.aon.occam.api.json.ConsoleMessageJSON;
import com.esferalia.aon.occam.api.model.console.ConsoleDomainMessage;
import com.esferalia.aon.occam.api.model.console.ConsoleMessage;
import com.esferalia.aon.occam.api.model.console.ConsoleMessageType;
import com.esferalia.aon.watson.util.AonMathUtils;


class ConsoleMessageUtils {
	
	private ConsoleMessageUtils() {
	}
	
	private static ConsoleMessage get(String id,ConsoleMessageType type,String msg) {
		return new ConsoleMessage()
			.setProcessId(id)
			.setType(type)
			.setMessage(msg);
	}

	public static ConsoleMessage error(String id,ConsoleDomainMessage msg) {
		return get(id,ConsoleMessageType.CONSOLE_MESSAGE,msg.getMessage())
			.setConsoleDomainMessage(msg);
	}

	public static ConsoleMessage error(String id,String msg) {
		return get(id,ConsoleMessageType.ERROR,msg);
	}

	public static ConsoleMessage warning(String id,String msg) {
		return get(id,ConsoleMessageType.WARNING,msg);
	}

	public static ConsoleMessage ok(String id,String msg) {
		return get(id,ConsoleMessageType.OK,msg);
	}

	public static ConsoleMessage title(String id,String msg) {
		return get(id,ConsoleMessageType.TITLE,msg);
	}
	
	public static ConsoleMessage subtitle(String id,String msg) {
		return get(id,ConsoleMessageType.SUBTITLE,msg);
	}

	public static ConsoleMessage message(String id,String msg) {
		return get(id,ConsoleMessageType.MESSAGE,msg);
	}
	public static ConsoleMessage progress(String id,int count, int progress, String msg) {
		return progress(id, count, progress)
			.setMessage(msg);
	}

	public static ConsoleMessage progress(String id,int count, int progress) {
		double percent = AonMathUtils.round( (double) progress * 100 / count);
		return get(id,ConsoleMessageType.PROGRESS,"")
			.setCount(count)
			.setProgress(progress)
			.setPercent(percent);
	}
	
	public static ConsoleMessage mainProgress(String id,int count, int progress) {
		double percent = AonMathUtils.round( (double) progress * 100 / count);
		return get(id,ConsoleMessageType.MAIN_PROGRESS,"")
			.setCount(count)
			.setProgress(progress)
			.setPercent(percent);
	}

	static void print(PrintStream stream, ConsoleMessage msg) {
		String json = ConsoleMessageJSON.toJSON(msg).toString();
		stream.print( json );
		stream.println( "," );
		stream.flush();
	}
	
	static void start(PrintStream stream) {
		stream.println( "[" );
	}

	static void end(PrintStream stream) {
		stream.println( "]" );
	}

}
