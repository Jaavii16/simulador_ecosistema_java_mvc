package simulator.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import simulator.model.Animal;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

public class StatsState implements EcoSysObserver{
	
	static class Info{
		double time;
		int n;
	}
	
	private Map<Animal.State, Info> stats;
	
	public StatsState() {
		stats = new HashMap<>();
		for(Animal.State s: Animal.State.values()){
		stats.put(s, new Info());
		}
	}
	
	public void show() {
		for(Animal.State s: Animal.State.values()){
			Info i = stats.get(s);
			System.out.println("Estado: " + s.name() + " Max: " + i.n + " en tiempo: " + String.format("%.3f",i.time));
		}
		System.out.println("--------------------------------");
	}

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		for(Animal.State s: Animal.State.values()){
			Info i = new Info();
			i.n = 0;
			i.time = 0.0;
			stats.put(s, i);
		}
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		for(Animal.State s: Animal.State.values()){
			Info i = stats.get(s);
			i.n = 0;
			i.time = 0.0;
		}
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		
	}

	@Override
	public void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		for(Animal.State s: Animal.State.values()){
			Info i = stats.get(s);
			int count = (int) animals.stream().filter(a->a.get_state().equals(s)).count();
			if(count >= i.n) {
				i.n = count;
				i.time = time;
			}
		}
		//show();
	}

}
