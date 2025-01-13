package net.aonsolutions.occam.api.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Stream;

import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.occam.api.model.type.InvoiceErrorLevel;

public class InvoicePreRecord implements Serializable {
	
	private static final long serialVersionUID = 8897444490096530091L;
	
	private InvoiceHeader header;
	private LinkedList<InvoiceError> messages;
	
	
	public InvoicePreRecord() {
		this.header = new InvoiceHeader();
	}

	public InvoiceHeader getHeader() {
		return header;
	}
	public InvoicePreRecord setHeader(InvoiceHeader header) {
		this.header = header;
		return this;
	}
	
	public Stream<InvoiceError> messageStream() {
	    return AonCollectionUtils.stream(messages);
	}
	public boolean hasMessages() {
		return AonCollectionUtils.isNotEmpty(messages);
	}
	public InvoicePreRecord addMessage(InvoiceError message) {
		if (this.messages == null) this.messages = new LinkedList<>();
		this.messages.add(message);
	    return this;
	}
	public void clearMessages() {
		this.messages = new LinkedList<>();		
	}
	public Optional<InvoiceErrorLevel> getMoreSeriousLevel() {
		return messageStream()
			.map(ie -> ie.getLevel().ordinal())
			.max( Integer::compare )
			.flatMap( InvoiceErrorLevel::value )
		;
	}
}

