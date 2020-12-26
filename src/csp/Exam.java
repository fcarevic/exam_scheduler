package csp;

import java.util.List;

public class Exam {
	
	
	private int accYear;
	private char program;
	private int department;
	private int gradeYear;
	private String name;
	private int studentsNumber;
	private boolean requiresComputers;
	private List<String> availableOnDepartments;
	private String code;
	
	
	
	public int getAccYear() {
		return accYear;
	}
	public char getProgram() {
		return program;
	}
	public int getDepartment() {
		return department;
	}
	public int getGradeYear() {
		return gradeYear;
	}
	public String getName() {
		return name;
	}
	public int getStudentsNumber() {
		return studentsNumber;
	}
	public boolean isRequiresComputers() {
		return requiresComputers;
	}
	
	
	public Exam(int accYear, char program, int department, int gradeYear, String name, int studentsNumber,
			boolean requiresComputers, List<String> availableOnDepartments, String code) {
		super();
		this.accYear = accYear;
		this.program = program;
		this.department = department;
		this.gradeYear = gradeYear;
		this.name = name;
		this.studentsNumber = studentsNumber;
		this.requiresComputers = requiresComputers;
		this.availableOnDepartments = availableOnDepartments;
		this.code=code;
	}
	public List<String> getAvailableOnDepartments() {
		return availableOnDepartments;
	}
	
	/** 
	 * 
	 * provera ogranicenja da li su ispiti na istom odsuku i iste godine
	 * 
	 * */
	
	
	public boolean checkSameYearAndDepartment(Exam e) {
		boolean flag=false;
		for(String dept: availableOnDepartments) {
			for(String dept2 : e.getAvailableOnDepartments()) {
				if(dept.equals(dept2)) {
					flag=true;
					break;
				}
			}
		 if(flag) break;
		}
		
		return flag &&  e.getGradeYear() == this.getGradeYear() ;
		//return flag &&  Math.abs(e.getGradeYear() -this.getGradeYear() ) <=1;
		
	}
	
	
	
	/** 
	 * Provera ogranicenja da li su ispiti na istom odseku u susednim godinama
	 * 
	 * 
	 * 
	 * */
	
	public boolean checkSequentialYears(Exam e) {
		boolean flag=false;
		for(String dept: availableOnDepartments) {
			for(String dept2 : e.getAvailableOnDepartments()) {
				if(dept.equals(dept2)) {
					flag=true;
					break;
				}
			}
		 if(flag) break;
		}
		
		//return flag &&  e.getGradeYear() == this.getGradeYear() ;
		return flag &&  Math.abs(e.getGradeYear() -this.getGradeYear() ) <=1;
		
	}
	
	public String toString() {
		return code;
		
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}


	
	

}
