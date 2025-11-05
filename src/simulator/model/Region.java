package simulator.model;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

public abstract class Region implements Entity, FoodSupplier, RegionInfo {

	protected List<Animal> _animales_en_reg;

	public Region() {
		_animales_en_reg = new LinkedList<>();
	}

	final void add_animal(Animal a) {
		_animales_en_reg.add(a);
	}

	void remove_animal(Animal a) {
		_animales_en_reg.remove(a);
	}

	final List<Animal> getAnimals() {
		return Collections.unmodifiableList(_animales_en_reg);
	}

	public JSONObject as_JSON() {
		JSONObject j = new JSONObject();
		JSONArray j_a = new JSONArray();
		Iterator<Animal> it = _animales_en_reg.iterator();
		while (it.hasNext()) {
			Animal animal = it.next();
			j_a.put(animal.as_JSON());
		}
		j.put("animals", j_a);
		return j;
	}

	
	public List<AnimalInfo> getAnimalsInfo() {
		return new ArrayList<>(_animales_en_reg);
	}
}
