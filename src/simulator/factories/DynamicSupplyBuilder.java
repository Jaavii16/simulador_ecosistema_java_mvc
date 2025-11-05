package simulator.factories;

import org.json.JSONObject;

import simulator.model.DynamicSupplyRegion;
import simulator.model.Region;

public class DynamicSupplyBuilder extends Builder<Region> {
	private static final double FACTOR = 2.0;
	private static final double FOOD = 1000.0;

	public DynamicSupplyBuilder() {
		super("dynamic", "Dynamic food supply");
	}

	public DynamicSupplyBuilder(String type_tag, String desc) {
		super(type_tag, desc);
		if (type_tag != "dynamic")
			throw new IllegalArgumentException("Tag incorrect");
	}

	@Override
	protected Region create_instance(JSONObject data) {
		if (data.isEmpty()) {
			throw new IllegalArgumentException("Data no deberia ser vacio");
		}
		double factor = data.optDouble("factor", FACTOR);

		double food = data.optDouble("food", FOOD);

		return new DynamicSupplyRegion(food, factor);
	}

	@Override
	protected void fill_in_data(JSONObject o) {
		o.put("factor", "food increase factor (optional, default 2.0)");
		o.put("food", "initial amount of food (optional, default 100.0)");
	}
	
	

}
