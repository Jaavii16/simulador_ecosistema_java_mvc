package simulator.model;

import simulator.misc.Utils;

public class DynamicSupplyRegion extends Region {

	private double _food;
	private double _factor;

	public DynamicSupplyRegion(double initFood, double incFact) {
		if (initFood <= 0) {
			throw new IllegalArgumentException("Init food debe ser positivo");
		}
		_food = initFood;
		if (incFact < 0) {
			throw new IllegalArgumentException("IncFact debe ser no negativo");
		}
		_factor = incFact;
	}

	@Override
	public double get_food(Animal a, double dt) {
		if (a.get_diet() == Animal.Diet.CARNIVORE) {
			return 0.0;
		} else {
			long n = getAnimals().stream().filter(animal -> animal.get_diet() == Animal.Diet.HERVIBORE).count();
			double give = Math.min(_food, 60.0 * Math.exp(-Math.max(0, n - 5.0) * 2.0) * dt);
			_food -= give;
			return give;
		}
	}

	@Override
	public void update(double dt) {
		int b = Utils._rand.nextInt(10) + 1;
		if (b <= 5) {
			_food = _food * dt * _factor;
		}
	}
	
	
	@Override
	public String toString() {
		return "dynamic";
	}


}
