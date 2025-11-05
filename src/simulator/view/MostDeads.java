package simulator.view;

import java.util.List;

import simulator.model.Animal;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

public class MostDeads implements EcoSysObserver{

	static int max = 0;
	static double time = 0.0;
	
	public void show() {
		System.out.println("Max. deads:" + max + " at time: " + String.format("%.3f", time) + "\n");
	}
	
	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		max = 0;
		time = 0.0;
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
	}

	@Override
	public void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		int deads = (int) animals.stream().filter(a->a.get_state().equals(Animal.State.DEAD)).count();
		if(deads >= max) {
			max = deads;
			this.time = time;
		}
	}

}
