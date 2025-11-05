package simulator.model;

import simulator.misc.Vector2D;

public class LoboTop extends Wolf{
	
	private Double time;

	public LoboTop(SelectionStrategy mate_strategy, SelectionStrategy hunting_strategy, Vector2D pos) {
		super(mate_strategy, hunting_strategy, pos);
		time = 0.0;
		this._genetic_code = "lobo folleti";
	}

	public LoboTop(LoboTop LoboTop, LoboTop LoboTop2) {
		super(LoboTop, LoboTop2);
		time = 0.0;
		this._genetic_code = "lobo folleti";
	}

	@Override
	public void update(double dt) {
		super.update(dt);
		if(time >= 5.0) {
			this._baby = new LoboTop(this, this);
			time = 0.0;
		}
		else {
			time += dt;
		}
	}
	
	//ME FALTA EL OVERRIDE DEL EMPAREJAR PARA QUE AL EMPAREJARSE NAZCAN LOBOS DE 
	//ESTE TIPO Y NO NORMALES
	

}
