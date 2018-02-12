package org.davidespadini.gerrit;

import java.util.HashMap;

public class Change {
	private HashMap<String, Integer> totComments;
	private HashMap<String, String> bodyComments;
	public Change(HashMap<String, Integer> totComments, HashMap<String, String> bodyComments) {
		super();
		this.totComments = totComments;
		this.bodyComments = bodyComments;
	}
	public HashMap<String, Integer> getTotComments() {
		return totComments;
	}
	public void setTotComments(HashMap<String, Integer> totComments) {
		this.totComments = totComments;
	}
	public HashMap<String, String> getBodyComments() {
		return bodyComments;
	}
	public void setBodyComments(HashMap<String, String> bodyComments) {
		this.bodyComments = bodyComments;
	}
	@Override
	public String toString() {
		return "Change [totComments=" + totComments + ", bodyComments=" + bodyComments + "]";
	}
	
	
}
