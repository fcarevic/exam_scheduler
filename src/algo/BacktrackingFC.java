package algo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import csp.Classroom;
import csp.Domain;
import csp.Exam;
import csp.Term;
import csp.Time;
import csp.Triplet;
import reader.Formatter;
import reader.StepWriter;

public class BacktrackingFC {
	private static int ID;
	private int id=ID++;
	long TIMESTAMP=0;
	
	Time times[] = { Time._08_00, Time._11_30, Time._15_00, Time._18_30 };
	private List<Exam> allExams;
	private Map<Exam, Domain> map = new HashMap<Exam, Domain>();
	private Map<Exam, List<Triplet>> selected = new HashMap<Exam, List<Triplet>>();

	
	private StepWriter stepWriter= new StepWriter();
	
	
	/** 
	 * fja za inicijalizaciju algoritma
	 * 
	 * */
	
	public void initialize(Term term, List<Classroom> classrooms) {
		try {
			stepWriter.init("log"+id+".txt");
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		long timestamp = TIMESTAMP++;
		allExams= term.getExams();
		allExams.sort((e1,e2)->{
			
			return   e2.getStudentsNumber() - e1.getStudentsNumber();
		});
		for (Exam exam : term.getExams()) {
			Domain domain = new Domain();
			for (Classroom classroom : classrooms) {
				if (classroom.isHasComputers() && exam.isRequiresComputers() || !exam.isRequiresComputers())
					for (int i = 0; i < term.getDuration(); i++) {

						for (int j = 0; j < times.length; j++) {
							Triplet triplet = new Triplet(i, times[j], classroom);
							domain.addTriplet(triplet);

						}

					}
			}
			domain.begin_transaction(timestamp);
			map.put(exam, domain);
		}
	}
  /**  
   * 
   *  FC consistency check fja za proveru konzistentnosti sa ostialim promenljivama i azuriranje njihovih domena
   *  
   *  @return boolean Da li je zadovoljena konzistencija
   *  
   *  */
	private boolean checkConsistency(Exam exam, List<Triplet> selectedTriplets) {
		long timestamp = TIMESTAMP++;
		boolean rollback = false;
		List<Exam> toCommit = new LinkedList<Exam>();
		boolean needsRollback= false;
		for (Exam e : map.keySet()) {
			if(e==exam) continue;
			if (selected.get(e) == null) {
				Domain domain = map.get(e);
				domain.begin_transaction(timestamp);
				for (Triplet t : selectedTriplets) {
					domain.deleteTriplet(t);
				}

				if (exam.checkSameYearAndDepartment(e)) {
					int day = selectedTriplets.get(0).getDay();
					domain.deleteByDay(day);
				}
				
				if(exam.checkSequentialYears(e)) {
					int day = selectedTriplets.get(0).getDay();
					Time time = selectedTriplets.get(0).getTime();
					domain.deleteByDayAndTime(day, time);
					
				}

				toCommit.add(e);
				
				
				if (!domain.checkAvailable(e.getStudentsNumber())) {
					
					needsRollback=true;
					break;

				}
			}

		}
		if(needsRollback) {
			for(Domain domain: map.values()) {
				domain.rollback(timestamp);
			}
			return false;
		}
		
		return true;

	}

	private void start_all_transactions(long timestamp) {
		
		for(Domain d : map.values()) {
			d.begin_transaction(timestamp);
			
		}
	}
	
	private void rollback_all_transactions(long timestamp) {
		for(Domain d : map.values()) {
				d.rollback(timestamp);
		}
		
		
		
	}
  
	/**
	 * fja algoritma
	 *  
	 *  @return boolean Da li je pronadjeno resenje
	 *  */
	
	
	private boolean backtracking(int i) {
		if(i==allExams.size()) return true;
		Exam exam = allExams.get(i);
		Domain domain = map.get(exam);
		
		
		List<List<Triplet>> allPosibilities= domain.getAllTriplets(exam.getStudentsNumber());
		
//		allPosibilities.sort((c1,c2)->{
//			int sum1 =0;
//			int sum2=0;
//			for(Triplet t:c1) {
//					sum1 = t.getClassroom().getCapacity();
////				sum1+= t.getClassroom().getNumberOfDutyProfessors();
////				if(!t.getClassroom().isAtEtf()) {
////					sum1+=1.2;
////				}
//			}
//			for(Triplet t:c2) {
//				sum2=t.getClassroom().getCapacity();
//			
////				sum2+= t.getClassroom().getNumberOfDutyProfessors();
////				if(!t.getClassroom().isAtEtf()) {
////					sum2+=1.2;
////				}
//			}
//
//			return sum2 - sum1;
////			if(sum1==sum2) return 0;
////			return  sum1>sum2? -1:1;
//		});
		
		
		long timestamp = TIMESTAMP++;
		for(List<Triplet> posibility : allPosibilities) {
			start_all_transactions(timestamp);
			try {
				stepWriter.writeSolution(exam, posibility);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(checkConsistency(exam, posibility)) //does rollback also
				{
				
				
				selected.put(exam, posibility);
				if(backtracking(i+1))
					return true;
				else { 
				selected.remove(exam);
				}
			
				}
			
			try {
				
				stepWriter.writeString("BACTRACKING NA PROMENLJIVOJ  " + exam.getCode()+ "  za  " + posibility+ "\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			rollback_all_transactions(timestamp);
		}
		return false;
	}
	
	public  void closeLogger() {
		try {
			stepWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	
	public boolean fc_backtracking() {
		return backtracking(0);
	}
	
	public Map<Exam, List<Triplet>> getSolution(){
		return selected;
	}
	
	
	public static void main(String [] args) {
		Formatter f = new Formatter();
		int i = 0;
		String base ="javni_testovi/";
		Term term = f.readTermFromFile(base+"rok" +i + ".json");
		List<Classroom> allClassrooms = f.readClassroomsFromFile(base +"sale"+i+".json");
		
		BacktrackingFC bfc = new BacktrackingFC();
		bfc.initialize(term, allClassrooms);
		bfc.fc_backtracking();
		bfc.closeLogger();
		Map<Exam, List<Triplet>> solution=bfc.getSolution();
		f.printToCSV("resenja/resenje" + i + ".csv" , solution, term, allClassrooms);
		
	}



}
