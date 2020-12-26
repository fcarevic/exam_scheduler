package reader;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import csp.Classroom;
import csp.Exam;
import csp.Term;
import csp.Time;
import csp.Triplet;

public class Formatter {
	private static final String [] TIMES = { "08:00", "11:30", "15:00", "18:30"};
	private  List<Classroom> listClassrooms;
	private  List<Exam> listExams;
		
	/** 
	 * Ucitavanje iz .json sala
	 * 
	 * @return List<Classroom> lista sala iz json
	 * */
	public  List<Classroom> readClassroomsFromFile(String filename){
		
		listClassrooms = new LinkedList<Classroom>();
		JSONParser parser = new JSONParser();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"));
			
			Object obj = parser.parse(reader);
			   JSONArray allClassrooms = (JSONArray) obj;
	            
	             
	            //Iterate over employee array
	            allClassrooms.forEach( json -> listClassrooms.add(parseClassrooomJSONObject((JSONObject)json)) );
	 
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return listClassrooms;
	}
	
	private Classroom parseClassrooomJSONObject(JSONObject json) {
		
		String name =(String)(json.get("naziv"));
		int capacity=Integer.parseInt(json.get("kapacitet")+"");
		boolean hasComputers = Integer.parseInt(json.get("racunari")+"")==1;
		int numberOfDutyProfessors= Integer.parseInt(json.get("dezurni")+"");
		boolean isAtEtf = Integer.parseInt(json.get("etf")+"")==1;
		
		
		return new Classroom(name, capacity, 
				hasComputers, numberOfDutyProfessors, isAtEtf);
		
		
	}
	
	
	/**
	 *  Ucitavanje roka iz json
	 *  
	 *  @return Term rok
	 *  
	 *  
	 *  
	 *  */
	
public  Term readTermFromFile(String filename){
		Term term = new Term();
		listExams = new LinkedList<Exam>();
		JSONParser parser = new JSONParser();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"));
			Object obj = parser.parse(reader);
			   JSONObject jsonTerm = (JSONObject) obj;
			   
			   term.setDuration(Integer.parseInt(jsonTerm.get("trajanje_u_danima")+""));
			   JSONArray allExams = (JSONArray)(jsonTerm.get("ispiti"));
	            
	             
	            //Iterate over employee array
	            allExams.forEach( json -> listExams.add(parseExamJSONObject((JSONObject)json)) );
	            	term.setExams(listExams);
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return term;
	}
	
	
	
	private Exam parseExamJSONObject(JSONObject json) {
	  String code = (String)(json.get("sifra"));
	  int accYear = Integer.parseInt(code.substring(0, 2));
	  char program = code.charAt(2);
	  int department = Integer.parseInt(code.substring(3,5));
	  int gradeYear = Integer.parseInt(code.substring(5, 6));
	  String name = code.substring(6);
	  int studentsNumber= Integer.parseInt(json.get("prijavljeni")+"");
	  boolean requiresComputers = Integer.parseInt(json.get("racunari")+"") == 1;
	  JSONArray jsonAvailableDepts = (JSONArray) (json.get("odseci"));
	  List<String> availableOnDepartments = new LinkedList<String>();
	  jsonAvailableDepts.forEach(obj->{ availableOnDepartments.add((String)obj);});
	  return new Exam(accYear, program, department, gradeYear, name,
			  studentsNumber, requiresComputers, availableOnDepartments, code);
	}
	
	
	/** 
	 * ispis resenja u csv formatu
	 * 
	 * @param solution Resenje
	 * @param Term rok
	 * @param allClassrooms sve ucionice
	 * 
	 * */
	
	public void printToCSV(String filename, Map<Exam, List<Triplet>> solution, Term term , List<Classroom> allClassrooms) {
		
		for(Exam exam: term.getExams()) {
			if(solution.get(exam) == null || solution.get(exam).isEmpty())
				System.err.println("NIJE PRONASAO RESENJE ZA " + exam.getCode());
			
		}
		
		Exam matrix [][][] = new Exam[term.getDuration()][4][allClassrooms.size()];
		
		for(int i= 0 ; i < matrix.length; i++)
			for(int j=0; j < matrix[i].length; j++)
				for(int k = 0 ; k < matrix[i][j].length; k++)
					matrix[i][j][k]=null;
		for(Exam exam : solution.keySet()) {
			int sum=0;
			List<Triplet> triplets = solution.get(exam);
			for(Triplet t: triplets) {
				sum+=t.getClassroom().getCapacity();
				matrix[t.getDay()][t.getTime().ordinal()][allClassrooms.indexOf(t.getClassroom())] = exam;
			}
			if(sum<exam.getStudentsNumber()) {
				System.err.println("MANJE");
			}
			
		}
			
			System.out.println(solution);
			try {
				System.out.println("****************RESENJE************************");
				Writer wiriter = new BufferedWriter(new OutputStreamWriter(
					    new FileOutputStream(filename), "UTF-8"));
				for(int i= 0 ; i < matrix.length; i++) {
					wiriter.write("Dan " + (i+1)  + ",");
					System.out.print("Dan " + (i+1));
					for(int x = 0 ; x < allClassrooms.size();x++) {
						 Classroom classroom = allClassrooms.get(x);
						wiriter.write(classroom.getName());
						if(x!= allClassrooms.size()-1)
							wiriter.write(',');
						
					System.out.print("\t" + classroom.getName());
					}
					wiriter.write('\n');
					System.out.println();
					for(int j=0; j < matrix[i].length; j++) {
						wiriter.write(TIMES[j] + ",");
						System.out.print(Time.values()[j]);
						for(int k = 0 ; k < matrix[i][j].length; k++)
						{
							if(matrix[i][j][k]==null){
								wiriter.write("X");
								
								System.out.print("\tX");
								
							}
							else {
								wiriter.write(matrix[i][j][k].getCode());
								System.out.print("\t"+matrix[i][j][k].getCode() );			
							}
							if(k!= matrix[i][j].length-1)
								wiriter.write(',');
						}
						wiriter.write('\n');
						System.out.println();
		
						}
					System.out.println();
					wiriter.write('\n');
					}
						
				wiriter.flush();
				wiriter.close();
										
			} catch ( IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
	
	
	}

	public static void main(String[] args) {
		
		 Formatter f= new Formatter();
		 List<Classroom> classrooms= f.readClassroomsFromFile("javni_testovi/sale1.json");
		 System.out.println(classrooms);
		 Term term = f.readTermFromFile("javni_testovi/rok1.json");
		 System.out.println(term);
		
		
		
	}
}
