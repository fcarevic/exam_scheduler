package csp;

public class Classroom {
	
	private String name;
	private int capacity;
	private boolean hasComputers;
	private int numberOfDutyProfessors;
	private boolean isAtEtf;
	
	
	
	
	public Classroom(String name, int capacity, boolean hasComputers, int numberOfDutyProfessors, boolean isAtEtf) {
		super();
		this.name = name;
		this.capacity = capacity;
		this.hasComputers = hasComputers;
		this.numberOfDutyProfessors = numberOfDutyProfessors;
		this.isAtEtf = isAtEtf;
	}
	public String getName() {
		return name;
	}
	public int getCapacity() {
		return capacity;
	}
	public boolean isHasComputers() {
		return hasComputers;
	}
	public int getNumberOfDutyProfessors() {
		return numberOfDutyProfessors;
	}
	public boolean isAtEtf() {
		return isAtEtf;
	}
	
	public String toString() {
		
		return name;
		
		
	}
	
	
	
	
	

}
