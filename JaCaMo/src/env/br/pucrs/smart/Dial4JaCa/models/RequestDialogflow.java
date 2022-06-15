package br.pucrs.smart.Dial4JaCa.models;

public class RequestDialogflow {
	private String responseId;
	QueryResult queryResult;
	WebhookStatus webhookStatus;
	OriginalDetectIntentRequest originalDetectIntentRequest;
	String session;

	// Getter Methods

	public String getResponseId() {
		return responseId;
	}

	public QueryResult getQueryResult() {
		return queryResult;
	}

	public WebhookStatus getWebhookStatus() {
		return webhookStatus;
	}

	// Setter Methods

	public void setResponseId(String responseId) {
		this.responseId = responseId;
	}

	public void setQueryResult(QueryResult queryResultObject) {
		this.queryResult = queryResultObject;
	}

	public void setWebhookStatus(WebhookStatus webhookStatusObject) {
		this.webhookStatus = webhookStatusObject;
	}

	public OriginalDetectIntentRequest getOriginalDetectIntentRequest() {
		return originalDetectIntentRequest;
	}

	public void setOriginalDetectIntentRequest(OriginalDetectIntentRequest originalDetectIntentRequest) {
		this.originalDetectIntentRequest = originalDetectIntentRequest;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

}
