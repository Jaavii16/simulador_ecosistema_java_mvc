package simulator.factories;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Utils;
import simulator.misc.Vector2D;
import simulator.model.*;

public class SheepBuilder extends Builder<Animal> {

	private Factory<SelectionStrategy> ssf;


	public SheepBuilder(Factory<SelectionStrategy> selection_strategy_factory) {
		super("sheep", "I am a sheep");
		ssf = selection_strategy_factory;
	}

	@Override
	protected Sheep create_instance(JSONObject data) {
		SelectionStrategy mate_s = null;
		if(data.has("mate_strategy")) {
			mate_s = ssf.create_instance(data.getJSONObject("mate_strategy"));
		}
		else mate_s = new SelectFirst();
		SelectionStrategy danger_s = null;
		if(data.has("danger_strategy")) {
			danger_s = ssf.create_instance(data.getJSONObject("danger_strategy"));
		}
		else danger_s = new SelectFirst();
		
		Vector2D _pos = null;
		if (data.has("pos")) {
			JSONObject pos = data.getJSONObject("pos");
			JSONArray x_r = pos.getJSONArray("x_range");
			JSONArray y_r = pos.getJSONArray("y_range");
			Double x = Utils._rand.nextDouble(x_r.getDouble(0), x_r.getDouble(1));
			Double y = Utils._rand.nextDouble(y_r.getDouble(0), y_r.getDouble(1));
			_pos = new Vector2D(x, y);
		}

		return new Sheep(mate_s, danger_s, _pos);
	}

	
}
