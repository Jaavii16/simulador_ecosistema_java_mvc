package simulator.model;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class SelectYoungest implements SelectionStrategy {

	@Override
	public Animal select(Animal a, List<Animal> as) {
//		if(as.size() == 0) {
//			return null;
//		}
//		Iterator<Animal> it = as.iterator();
//		Animal youngest = as.get(0);
//		while (it.hasNext()) {
//			Animal animal = it.next();
//			if (animal.get_age() < youngest.get_age()) {
//				youngest = animal;
//			}
//		}
//		
//		
		return as.stream().min(Comparator.comparing(Animal::get_age)).orElse(null);
	}

}
