package simulator.control;

import java.io.OutputStream;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.PrintStream;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;

import java.util.ArrayList;
import simulator.model.MapInfo;
import simulator.model.Region;
import simulator.model.Simulator;
import simulator.view.SimpleObjectViewer;
import simulator.view.SimpleObjectViewer.ObjInfo;

public class Controller {
	private Simulator _sim;

	public Controller(Simulator sim) {
		if(sim == null)throw new IllegalArgumentException("El simulador no puede ser nulo");
		_sim = sim;
	}

	public void load_data(JSONObject data) {
		set_regions(data);
		
		JSONArray animals = data.getJSONArray("animals");
		for(Object  a: animals) {
			int N = ((JSONObject)a).getInt("amount");
			JSONObject O = ((JSONObject)a).getJSONObject("spec");
			for(int i=0;i<N;i++) {
				_sim.add_animal(O);
			}
		}
	}
	
	
	
	
	private List<ObjInfo> to_animals_info(List<? extends AnimalInfo> animals) {
		List<ObjInfo> ol = new ArrayList<>(animals.size());
		for (AnimalInfo a : animals)
			ol.add(new ObjInfo(a.get_genetic_code(),
						(int) a.get_position().getX(),
							(int) a.get_position().getY(),(int)Math.round(a.get_age())+2));
		return ol;
	}

	public void run(double t, double dt, boolean sv, OutputStream out) {
		SimpleObjectViewer view = null;
		if (sv) {
			MapInfo m = _sim.get_map_info();
			view = new SimpleObjectViewer("[ECOSYSTEM]",
			m.get_width(), m.get_height(),
			m.get_cols(), m.get_rows());
			view.update(to_animals_info(_sim.get_animals()), _sim.get_time(), dt);
		}
		
		JSONObject init_state = _sim.as_JSON();
		while(_sim.get_time() < t) {
			_sim.advance(dt);
			if (sv) view.update(to_animals_info(_sim.get_animals()), _sim.get_time(), dt);
		}
		JSONObject final_state = _sim.as_JSON();
		
		JSONObject out_j = new JSONObject();
		out_j.put("in", init_state);
		out_j.put("out", final_state);
		
		PrintStream p = new PrintStream(out);
		p.println(out_j.toString(1));

		
		if (sv) view.close();
	}
	
	
	 public void reset(int cols, int rows, int width, int height) {
		 _sim.reset(cols, rows, width, height);
	 }
	 
	 public void set_regions(JSONObject rs) {
		 if (rs.has("regions")) {
				JSONArray regs = rs.getJSONArray("regions");
				for(Object  r: regs) {
					JSONArray row = ((JSONObject) r).getJSONArray("row");
					JSONArray col = ((JSONObject)r).getJSONArray("col");
					JSONObject O = ((JSONObject)r).getJSONObject("spec");
					
					int rt, rf, ct, cf;
					rf = row.getInt(0);
					rt = row.getInt(1);
					cf = col.getInt(0);
					ct = col.getInt(1);
					for(int R = rf; R<rt;R++) {
						for(int C=cf; C<ct;C++) {
							_sim.set_region(R, C, O);
						}
					}
				}
		}
	 }
	 
	 public void advance(double dt) {
		 _sim.advance(dt);
	 }
	 
	 public void addObserver(EcoSysObserver o) {
		 _sim.addObserver(o);
	 }
	 
	 public void removeObserver(EcoSysObserver o) {
		 _sim.removeObserver(o);
	 }

}
