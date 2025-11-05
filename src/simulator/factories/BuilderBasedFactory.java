package simulator.factories;

import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import org.json.JSONObject;

public class BuilderBasedFactory<T> implements Factory<T> {

	Map<String, Builder<T>> _builder;
	private List<JSONObject> _builders_info;

	public BuilderBasedFactory() {
		_builder = new HashMap<>();
		_builders_info = new LinkedList<>();
	}

	public BuilderBasedFactory(List<Builder<T>> bs) {
		this();
		if(bs == null)throw new IllegalArgumentException("La lista de builders no puede ser nula");
		for (Builder<T> b : bs) {
			add_builder(b);
		}
	}

	public void add_builder(Builder<T> b) {
		 _builder.put(b.get_type_tag(), b);
		 
		_builders_info.add(b.get_info());
	}

	@Override
	public T create_instance(JSONObject info) {
		if (info == null) {
			throw new IllegalArgumentException("'info' cannot be null");
		}
		String tag = info.getString("type");
		Builder<T> b = null;
		if (_builder.containsKey(tag)) {
			b = _builder.get(tag);
			
		}
		if (b == null) {
			throw new IllegalArgumentException("Unrecognized ‘info’:" + info.toString());
		}
		T obj = b.create_instance(info.has("data") ? info.getJSONObject("data") : new JSONObject());
		if (obj == null) {
			throw new IllegalArgumentException("Unrecognized ‘info’:" + info.toString());
		}
		return obj;
		
	}

	@Override
	public List<JSONObject> get_info() {
		return Collections.unmodifiableList(_builders_info);
	}

}
