package br.pucrs.smart.Dial4JaCa.models;

import java.util.HashMap;

public class FollowupEventInput {
	String name;
	String languageCode;
	private HashMap<String, Object> parameters;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getLanguageCode() {
		return languageCode;
	}
	
	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}
	
	public HashMap<String, Object> getParameters() {
		return parameters;
	}
	
	public void setParameters(HashMap<String, Object> parameters) {
		this.parameters = parameters;
	}
}
