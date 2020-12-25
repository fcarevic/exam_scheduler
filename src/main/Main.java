package main;

import java.util.List;
import java.util.Map;

import algo.BacktrackingFC;
import csp.Classroom;
import csp.Exam;
import csp.Term;
import csp.Triplet;
import reader.Formatter;

public class Main {
	
	public static void main(String[] args) {
		
	
		for(int i = 1; i<=5;i++) {
			System.out.println();
			System.out.println();
		Formatter f = new Formatter();
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

}
