package simulator.model;

public class DefaultRegion extends Region {

	@Override
	public double get_food(Animal a, double dt) {
		if (a.get_diet() == Animal.Diet.CARNIVORE) {
			return 0.0;
		} else {
			long n = getAnimals().stream().filter(animal -> animal.get_diet() == Animal.Diet.HERVIBORE).count();
			return 60.0 * Math.exp(-Math.max(0, n - 5.0) * 2.0) * dt;
		}
	}

	@Override
	public void update(double dt) {
	}

	@Override
	public String toString() {
		return "default";
	}

	
	
}
