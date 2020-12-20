package csp;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Domain {
	
	
	
	
	private List<List<Triplet>> allPosibilities = new ArrayList();
	
	
	
	private Map<Long, List<Triplet>> history = new HashMap<>();
	private List<Triplet> triplets = new LinkedList<Triplet>();
	
	public void addTriplet(Triplet t) {
			triplets.add(t);
		
	}
	
	public boolean deleteTriplet(Triplet t) {
		return triplets.removeIf(f->f.getDay()==t.getDay()&& f.getTime() == t.getTime() && f.getClassroom()==t.getClassroom());
	}

	public boolean isEmpty() {
		return triplets.isEmpty();
	}
	
	public void begin_transaction(long timestamp) {
		List<Triplet> to_save = new LinkedList<Triplet>(triplets);
		history.put(timestamp, to_save);
	}
	
	
	public void rollback(long timestamp) {
		if(history.get(timestamp)==null) return;
		triplets = history.remove(timestamp);
	}
	
	public List<Triplet> getTriplets() {
		return this.triplets; 
	}
	
	public void deleteByDay(int day) {
		triplets.removeIf(l->l.getDay()==day);
		
	}
	
	public boolean  checkAvailable(int numberOfStudents) {
		if( isEmpty()) return false;
		List<Triplet> temp = new LinkedList<Triplet>(triplets);
		temp.sort(new Comparator<Triplet>() {
		@Override
		public int compare(Triplet o1, Triplet o2) {
			return o1.getDay()*10 + o1.getTime().ordinal() - o2.getDay()*10 - o2.getTime().ordinal(); 
		}
		});
		
		int day=temp.get(0).getDay();
		Time time= temp.get(0).getTime();

		int available = 0;
		for(Triplet t: temp) {
			if(time == t.getTime() && day == t.getDay()) available += t.getClassroom().getCapacity();
			else {
				if(available>=numberOfStudents) return true;
				available = t.getClassroom().getCapacity();
				day = t.getDay();
				time= t.getTime();
				
			}
			
		}
		
		return available>=numberOfStudents;
			
		
	} 
	
	
	private void createAllPosibilities(int numberOfStudents, int i,  List<Triplet> list ,List<Triplet> triplets) {
		if(i== triplets.size()) return;
		if(numberOfStudents <= triplets.get(i).getClassroom().getCapacity()) {
			list.add(triplets.get(i));
			allPosibilities.add(new ArrayList<Triplet>(list));
			list.remove(triplets.get(i));
			return;
		}
		if(i== triplets.size()-1) return;
		
		//List<Triplet> new_list = new ArrayList<Triplet>(list);
		//new_list.add(triplets.get(i));
		createAllPosibilities(numberOfStudents, i+1, list, triplets);
		list.add(triplets.get(i));
		createAllPosibilities(numberOfStudents - triplets.get(i).getClassroom().getCapacity(), i+1, list, triplets);
		list.remove(triplets.get(i));
	}
	
	
 	public List<List<Triplet>> getAllTriplets(int numberOfStudents){
	    	
		List<Triplet> temp = new ArrayList();
		triplets.sort(new Comparator<Triplet>() {
		@Override
		public int compare(Triplet o1, Triplet o2) {
			return o1.getDay()*10 + o1.getTime().ordinal() - o2.getDay()*10 - o2.getTime().ordinal(); 
		}
		});
		
		int day=triplets.get(0).getDay();
		Time time= triplets.get(0).getTime();
		
		for(Triplet t: triplets) {
			if(time == t.getTime() && day == t.getDay()) {
				temp.add(t);
			}
			else {
				createAllPosibilities(numberOfStudents, 0, new ArrayList<Triplet>(), temp);
				//System.out.println(allPosibilities);
				temp.clear();
				day = t.getDay();
				time= t.getTime();
				temp.add(t);
				
			}
			
		}
		
		createAllPosibilities(numberOfStudents, 0, new ArrayList<Triplet>(), temp);
		
		
	
	 return allPosibilities;
	}
	
	

}