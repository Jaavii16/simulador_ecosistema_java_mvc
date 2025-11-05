package simulator.factories;

import org.json.JSONObject;

import simulator.model.SelectYoungest;
import simulator.model.SelectionStrategy;

public class SelectYoungestBuilder extends Builder<SelectionStrategy> {

	public SelectYoungestBuilder() {
		super("youngest", "{}");
	}

	public SelectYoungestBuilder(String type_tag, String desc) {
		super("youngest", "{}");
		if (type_tag != "youngest")
			throw new IllegalArgumentException("Tag incorrect");
	}

	@Override
	protected SelectionStrategy create_instance(JSONObject data) {
		if (!data.isEmpty()) {
			throw new IllegalArgumentException("Data deberia ser vacio");
		}
		return new SelectYoungest();
	}

}
