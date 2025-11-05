package simulator.factories;

import org.json.JSONObject;

import simulator.model.SelectClosest;
import simulator.model.SelectionStrategy;

public class SelectClosestBuilder extends Builder<SelectionStrategy> {

	public SelectClosestBuilder() {
		super("closest", "{}");
	}

	public SelectClosestBuilder(String type_tag, String desc) {
		super("closest", "{}");
		if (type_tag != "closest")
			throw new IllegalArgumentException("Tag incorrect");
	}

	@Override
	protected SelectionStrategy create_instance(JSONObject data) {
		if (!data.isEmpty()) {
			throw new IllegalArgumentException("Data deberia ser vacio");
		}
		return new SelectClosest();
	}

}
