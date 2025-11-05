package simulator.factories;

import org.json.JSONObject;

import simulator.model.DefaultRegion;
import simulator.model.Region;

public class DefaultRegionBuilder extends Builder<Region> {

	public DefaultRegionBuilder() {
		super("default", "Infinite food supply");
	}

	public DefaultRegionBuilder(String type_tag, String desc) {
		super(type_tag, desc);
		if (type_tag != "default")
			throw new IllegalArgumentException("Tag incorrect");
	}

	@Override
	protected Region create_instance(JSONObject data) {
		if (!data.isEmpty()) {
			throw new IllegalArgumentException("Data deberia ser vacio");
		}
		return new DefaultRegion();
	}

	@Override
	protected void fill_in_data(JSONObject o) {
	}
	
	

}
