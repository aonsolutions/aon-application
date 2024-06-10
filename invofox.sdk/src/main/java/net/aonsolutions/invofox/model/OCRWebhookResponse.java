package net.aonsolutions.invofox.model;

import java.util.Optional;

public class OCRWebhookResponse extends OCRResponse {

	private static final long serialVersionUID = 1L;
	
	private OCRWebhook webhook;

	@Override
	public OCRWebhookResponse setHttpCode(Integer httpCode) {
		super.setHttpCode(httpCode);
		return this;
	}

	@Override
	public OCRWebhookResponse setError(OCRError error) {
		super.setError(error);
		return this;
	}

	@Override
	public OCRWebhookResponse setSkip(Integer skip) {
		super.setSkip(skip);
		return this;
	}

	@Override
	public OCRWebhookResponse setLimit(Integer limit) {
		super.setLimit(limit);
		return this;
	}

	@Override
	public OCRWebhookResponse setCount(Integer count) {
		super.setCount(count);
		return this;
	}

	public Optional<OCRWebhook> getWebhook() {
		return Optional.ofNullable(webhook);
	}

	public OCRWebhookResponse setWebhook(OCRWebhook webhook) {
		this.webhook = webhook;
		return this;
	}
}
