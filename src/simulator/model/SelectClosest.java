package simulator.model;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class SelectClosest implements SelectionStrategy {

	@Override
	public Animal select(Animal a, List<Animal> as) {
//		if(as.size() == 0) {
//			return null;
//		}
//		Iterator<Animal> it = as.iterator();
//		Animal closest = as.get(0);
//		double distanciaMin = a._pos.distanceTo(closest.get_position());
//		while (it.hasNext()) {
//			Animal animal = it.next();
//			if(!animal.equals(a)) {
//				if (animal._pos.distanceTo(a.get_position()) < distanciaMin) {
//					closest = animal;
//				}
//			}
//		}
//		
//		Animal cercano = as.stream().min((a1, a2)->Double.compare(a._pos.distanceTo(a1._pos), a._pos.distanceTo(a2._pos))).orElse(null);
//		
		return as.stream().min((a1,a2)->Double.compare(a.get_position().distanceTo(a1.get_position()), a.get_position().distanceTo(a2.get_position()))).orElse(null);
	}

}
