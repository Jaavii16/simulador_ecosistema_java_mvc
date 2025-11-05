package simulator.model;

import java.util.List;
import java.util.ArrayList;


import org.json.JSONObject;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import simulator.factories.Factory;

public class Simulator implements Observable<EcoSysObserver>{

	private Factory<Animal> _animal_factory;
	private Factory<Region> _region_factory;
	private RegionManager _reg_mngr;
	private List<Animal> _animals;
	private double _act_time; // tiempo actual
	private List<EcoSysObserver> _obs;

	public Simulator(int cols, int rows, int width, int height, Factory<Animal> animals_factory,
			Factory<Region> regions_factory) {
		if(cols <= 0)throw new IllegalArgumentException("no puede haber 0 columnas");
		if(rows <= 0)throw new IllegalArgumentException("no puede haber 0 filas");
		if(width <= 0)throw new IllegalArgumentException("la anchura tiene que ser positiva");
		if(height <= 0)throw new IllegalArgumentException("la altura tiene que ser positiva");
		if(animals_factory == null)throw new IllegalArgumentException("La factoria de animales no puede ser nula");
		if(regions_factory == null)throw new IllegalArgumentException("La factoria de regiones no puede ser nula");
		_animal_factory = animals_factory;
		_region_factory = regions_factory;
		_animals = new LinkedList<>();
		_reg_mngr = new RegionManager(cols, rows, width, height);
		_act_time = 0.0;
		_obs = new LinkedList<>();
	}

	private void set_region(int row, int col, Region r) {
		_reg_mngr.set_region(row, col, r);
	}

	public void set_region(int row, int col, JSONObject r_json) {
		Region R = _region_factory.create_instance(r_json);
		set_region(row, col, R);
		for(EcoSysObserver o: _obs) {
			o.onRegionSet(row, col,_reg_mngr, R);
		}
	}

	private void add_animal(Animal a) {
		_animals.add(a);
		_reg_mngr.register_animal(a);
		List<AnimalInfo> animals = new ArrayList<>(_animals);
		for(EcoSysObserver o: _obs) {
			o.onAnimalAdded(_act_time, _reg_mngr, animals, a);
		}
	}

	public void add_animal(JSONObject a_json) {
		Animal A = _animal_factory.create_instance(a_json);
		add_animal(A);
	}

	public MapInfo get_map_info() {
		return _reg_mngr;
	}

	public List<? extends AnimalInfo> get_animals() {
		return Collections.unmodifiableList(_animals);
	}

	public double get_time() {
		return _act_time;
	}

	public void advance(double dt) {
		_act_time += dt;
		List<Animal> bebes = new LinkedList<>();
		List<Animal> muertos = new LinkedList<>();
		for(Animal a: _animals) {
			if(a.get_state() == Animal.State.DEAD) {
				_reg_mngr.unregister_animal(a);
				muertos.add(a);
			}
		}
		_animals.removeAll(muertos);
		
		Iterator<Animal> it = _animals.iterator();
		while (it.hasNext()) {
			Animal a = it.next();
			a.update(dt);
			_reg_mngr.update_animal_region(a);
			if (a.is_pregnant()) {
				bebes.add(a.deliver_baby());
			}
		}
		_animals.addAll(bebes);
		for(Animal an: bebes) {
			add_animal(an);
		}
		_reg_mngr.update_all_regions(dt);
		List<AnimalInfo> animals = new ArrayList<>(_animals);
		for(EcoSysObserver o: _obs) {
			o.onAvanced(_act_time, _reg_mngr, animals, dt);
		}
	}
	
	public void reset(int cols, int rows, int width, int height) {
		_animals.clear();
		new RegionManager(cols, rows, width, height);
		_act_time = 0.0;
		List<AnimalInfo> animals = new ArrayList<>(_animals);
		for(EcoSysObserver o: _obs) {
			o.onReset(_act_time, _reg_mngr, animals);
		}
	}

	public JSONObject as_JSON() {
		JSONObject j = new JSONObject();
		j.put("time", _act_time);
		j.put("state", _reg_mngr.as_JSON());
		return j;
	}
	

	@Override
	public void addObserver(EcoSysObserver o) {
		if(!_obs.contains(o)) {
			_obs.add(o);
		}
		List<AnimalInfo> animals = new ArrayList<>(_animals);
		o.onRegister(_act_time, _reg_mngr, animals);
	}

	@Override
	public void removeObserver(EcoSysObserver o) {
		_obs.remove(o);
	}
}
