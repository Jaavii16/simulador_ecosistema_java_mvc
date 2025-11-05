package simulator.model;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class SelectFastest implements SelectionStrategy {

	@Override
	public Animal select(Animal a, List<Animal> as) {
//		Animal an = as.stream().max((a1,a2)->Double.compare(a1.get_speed(), a2.get_speed())).orElse(null);
		return as.stream().max(Comparator.comparing(Animal::get_speed)).orElse(null);
	}
	//DEVUELVE NULL SI LA LISTA ESTA VACIA Y SINO DEVUELVE EL MAS RAPIDO
}
