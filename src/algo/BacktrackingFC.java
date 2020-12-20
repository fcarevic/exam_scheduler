package algo;

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

public class BacktrackingFC {
	long TIMESTAMP=0;
	
	Time times[] = { Time._08_00, Time._11_30, Time._15_00, Time._18_30 };
	private List<Exam> allExams;
	private Map<Exam, Domain> map = new HashMap<Exam, Domain>();
	private Map<Exam, List<Triplet>> selected = new HashMap<Exam, List<Triplet>>();

	public void initialize(Term term, List<Classroom> classrooms) {
		long timestamp = TIMESTAMP++;
		allExams= term.getExams();
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

	private boolean backtracking(int i) {
		if(i==allExams.size()) return true;
		Exam exam = allExams.get(i);
		Domain domain = map.get(exam);
		
		List<List<Triplet>> allPosibilities= domain.getAllTriplets(exam.getStudentsNumber());
		long timestamp = TIMESTAMP++;
		for(List<Triplet> posibility : allPosibilities) {
			if(checkConsistency(exam, posibility)) //does rollback also
				{
				selected.put(exam, posibility);
				if(backtracking(i+1))
					return true;
				else rollback_all_transactions(timestamp);
				}
			
			
		}
		
		return false;
	}
	
	
	public boolean fc_backtracking() {
		return backtracking(0);
	}
	
	public Map<Exam, List<Triplet>> getSolution(){
		return selected;
	}
	
	
	public static void main(String [] args) {
		Formatter f = new Formatter();
		int i = 1;
		String base ="javni_testovi/";
		Term term = f.readTermFromFile(base+"rok" +i + ".json");
		List<Classroom> allClassrooms = f.readClassroomsFromFile(base +"sale"+i+".json");
		
		BacktrackingFC bfc = new BacktrackingFC();
		bfc.initialize(term, allClassrooms);
		bfc.fc_backtracking();
		Map<Exam, List<Triplet>> solution=bfc.getSolution();
		f.printToCSV("resenja/resenje" + i + ".csv" , solution, term, allClassrooms);
		
	}



}
