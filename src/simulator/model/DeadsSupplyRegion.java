package simulator.model;

import java.util.List;

public class DeadsSupplyRegion extends DynamicSupplyRegion{

	private double _food;
	private double _factor;
	private List<AnimalInfo> _animals;
	
	public DeadsSupplyRegion(double initFood, double incFact) {
		super(initFood, incFact);
	}
	//ESTA REGION SI EL ANIMAL ES CARNIVORO REPARTE LA COMIDA ENTRE EL TOTAL DE CARNIVOROS Y SINO ACTUA NORMAL
	//A SU VEZ, CADA VEZ QUE MUERA UN ANIMAL DEBE INCREMENTAR LA COMIDA

	@Override
	public double get_food(Animal a, double dt) {
		return super.get_food(a,dt);
	}

	@Override
	public void update(double dt) {
		super.update(dt);
		//NO PODEMOS CONTAR AQUI LOS MUERTOS PORQUE YA SE HAN ELIMINADO DE LA REGION
		//SE HACE EN EL REMOVE_ANIMAL
		int muertos = (int) _animales_en_reg.stream().filter(a->a.get_state().equals(Animal.State.DEAD)).count();
		_food+=muertos*2;
	}

	@Override
	void remove_animal(Animal a) {
		if(a.get_state().equals(Animal.State.DEAD))_food+=10;
	}
	
	@Override
	public String toString() {
		return "deaths dynamic";
	}
	

}
