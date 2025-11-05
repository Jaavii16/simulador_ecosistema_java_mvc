package simulator.factories;

import org.json.JSONObject;

import simulator.model.SelectFastest;
import simulator.model.SelectionStrategy;

public class SelectFastestBuilder extends Builder<SelectionStrategy>{	
	
	public SelectFastestBuilder() {
		super("fastest", "{}");
	}

	public SelectFastestBuilder(String type_tag, String desc) {
		super("fastest", "{}");
		if (type_tag != "fastest")
			throw new IllegalArgumentException("Tag incorrect");
	}

	@Override
	protected SelectionStrategy create_instance(JSONObject data) {
		if (!data.isEmpty()) {
			throw new IllegalArgumentException("Data deberia ser vacio");
		}
		return new SelectFastest();
	}

}
