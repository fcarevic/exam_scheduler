package csp;

import java.util.List;

public class Term {
	
	private int duration;
	private List<Exam> exams;
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public List<Exam> getExams() {
		return exams;
	}
	public void setExams(List<Exam> exams) {
		this.exams = exams;
	}
	
	
	public String toString() {
		StringBuilder sb= new StringBuilder();
		sb.append("Trajanje: " +  duration+ "\n");
		for(Exam e: exams)
			sb.append(e.toString() +  " | ");
		
		return sb.toString();
		
		
		
		
	}
	

}
