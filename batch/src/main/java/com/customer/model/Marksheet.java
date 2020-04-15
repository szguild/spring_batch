package com.customer.model;
public class Marksheet {
	private long rollNum;	
	private String stdName;
	private int totalMarks;
	public Marksheet(long rollNum, String stdName, int totalMarks){
		this.rollNum = rollNum;
		this.stdName = stdName;
		this.totalMarks = totalMarks;
	}
	public long getRollNum() {
		return rollNum;
	}
	public void setRollNum(long rollNum) {
		this.rollNum = rollNum;
	}
	public String getStdName() {
		return stdName;
	}
	public void setStdName(String stdName) {
		this.stdName = stdName;
	}
	public int getTotalMarks() {
		return totalMarks;
	}
	public void setTotalMarks(int totalMarks) {
		this.totalMarks = totalMarks;
	}
}