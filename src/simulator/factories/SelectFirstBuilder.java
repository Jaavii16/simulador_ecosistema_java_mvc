package simulator.factories;

import org.json.JSONObject;

import simulator.model.SelectFirst;
import simulator.model.SelectionStrategy;

public class SelectFirstBuilder extends Builder<SelectionStrategy> {

	public SelectFirstBuilder() {
		super("first", "{}");
	}

	public SelectFirstBuilder(String type_tag, String desc) {
		super("first", "{}");
		if (type_tag != "first")
			throw new IllegalArgumentException("Tag incorrect");
	}

	@Override
	protected SelectionStrategy create_instance(JSONObject data) {
		if (!data.isEmpty()) {
			throw new IllegalArgumentException("Data deberia ser vacio");
		}
		return new SelectFirst();
	}

}
