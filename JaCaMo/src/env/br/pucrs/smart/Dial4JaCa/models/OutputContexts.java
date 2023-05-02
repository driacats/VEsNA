package br.pucrs.smart.Dial4JaCa.models;

import java.util.HashMap;

public class OutputContexts {
	String name;
	int lifespanCount;
	private HashMap<String, Object> parameters;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getLifespanCount() {
		return lifespanCount;
	}
	
	public void setLifespanCount(int lifespanCount) {
		this.lifespanCount = lifespanCount;
	}
	
	public HashMap<String, Object> getParameters() {
		return parameters;
	}
	
	public void setParameters(HashMap<String, Object> parameters) {
		this.parameters = parameters;
	}

}
