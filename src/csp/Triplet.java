package csp;

public class Triplet {
	
	private int day;
	private Time time;
	private Classroom classroom;
	
	
	public Triplet(int day, Time time, Classroom classroom) {
		super();
		this.day = day;
		this.time = time;
		this.classroom = classroom;
	}
	public int getDay() {
		return day;
	}
	public Time getTime() {
		return time;
	}
	public Classroom getClassroom() {
		return classroom;
	}
	
	public String toString() {
		return day + " " + time+  " " + classroom.getName()+ "/" + classroom.getCapacity();
		
	}
	

}
