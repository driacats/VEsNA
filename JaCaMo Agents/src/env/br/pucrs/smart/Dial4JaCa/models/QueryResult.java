package br.pucrs.smart.Dial4JaCa.models;

import java.util.HashMap;
import java.util.List;

public class QueryResult {
	private String queryText;
	private String action;
	private HashMap<String, Object> parameters;
	private boolean allRequiredParamsPresent;
	private String fulfillmentText;
	List<Object> fulfillmentMessages;
	List<OutputContexts> outputContexts;
	Intent intent;
	private float intentDetectionConfidence;
	DiagnosticInfo diagnosticInfo;
	private String languageCode;

	// Getter Methods

	public String getQueryText() {
		return queryText;
	}
	
	public String getAction() {
		return action;
	}

	public boolean getAllRequiredParamsPresent() {
		return allRequiredParamsPresent;
	}

	public String getFulfillmentText() {
		return fulfillmentText;
	}

	public List<Object> getFulfillmentMessages() {
		return fulfillmentMessages;
	}
	
	public List<OutputContexts> getOutputContexts() {
		return outputContexts;
	}
	
	public Intent getIntent() {
		return intent;
	}

	public float getIntentDetectionConfidence() {
		return intentDetectionConfidence;
	}

	public DiagnosticInfo getDiagnosticInfo() {
		return diagnosticInfo;
	}

	public String getLanguageCode() {
		return languageCode;
	}	

	public HashMap<String, Object> getParameters() {
		return parameters;
	}

	// Setter Methods

	public void setQueryText(String queryText) {
		this.queryText = queryText;
	}	

	public void setAction(String action) {
		this.action = action;
	}

	public void setAllRequiredParamsPresent(boolean allRequiredParamsPresent) {
		this.allRequiredParamsPresent = allRequiredParamsPresent;
	}

	public void setFulfillmentText(String fulfillmentText) {
		this.fulfillmentText = fulfillmentText;
	}
	
	public void setFulfillmentMessages(List<Object> fulfillmentMessages) {
		this.fulfillmentMessages = fulfillmentMessages;
	}

	public void addFulfillmentMessages(Object fulfillmentMessage) {
		this.fulfillmentMessages.add(fulfillmentMessage);
	}

	public void setOutputContexts(List<OutputContexts> outputContexts) {
		this.outputContexts = outputContexts;
	}

	public void addOutputContexts(OutputContexts outputContext) {
		this.outputContexts.add(outputContext);
	}
	
	public void setIntent(Intent intentObject) {
		this.intent = intentObject;
	}

	public void setIntentDetectionConfidence(float intentDetectionConfidence) {
		this.intentDetectionConfidence = intentDetectionConfidence;
	}

	public void setDiagnosticInfo(DiagnosticInfo diagnosticInfoObject) {
		this.diagnosticInfo = diagnosticInfoObject;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public void setParameters(HashMap<String, Object> parameters) {
		this.parameters = parameters;
	}
}