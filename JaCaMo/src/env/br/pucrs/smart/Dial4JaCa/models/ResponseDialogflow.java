package br.pucrs.smart.Dial4JaCa.models;

import java.util.ArrayList;
import java.util.List;

public class ResponseDialogflow {
	
public ResponseDialogflow() {
	this.outputContexts = new ArrayList<OutputContexts>();
}
	private String fulfillmentText;
	private FollowupEventInput followupEventInput;
	private List<OutputContexts> outputContexts;

	public String getFulfillmentText() {
		return fulfillmentText;
	}

	public void setFulfillmentText(String fulfillmentText) {
		this.fulfillmentText = fulfillmentText;
	}

	public List<OutputContexts> getOutputContexts() {
		return outputContexts;
	}

	public void setOutputContexts(List<OutputContexts> outputContexts) {
		this.outputContexts = outputContexts;
	}

	public void addOutputContexts(OutputContexts outputContext) {
		this.outputContexts.add(outputContext);
	}

	public FollowupEventInput getFollowupEventInput() {
		return followupEventInput;
	}

	public void setFollowupEventInput(FollowupEventInput followupEventInput) {
		this.followupEventInput = followupEventInput;
	}
	
}
