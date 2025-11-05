package simulator.model;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public class SuperSheep extends Sheep {
	
	private boolean estadoEsp;
	private double time;
	private Animal.State _last_state;

	public SuperSheep(SelectionStrategy mate_strategy, SelectionStrategy danger_strategy, Vector2D pos) {
		super(mate_strategy, danger_strategy, pos);
		estadoEsp = false;
		time = 0.0;
		this._genetic_code = "Super sheep";
	}

	@Override
	public void update(double dt) {
		super.update(dt);
		_last_state = this.get_state();
		double r = Utils._rand.nextDouble();
		if(estadoEsp) {
			this._state = Animal.State.NORMAL;
			time+=dt;
			if(time >= 5.0) {
				estadoEsp = false;
				this._state = _last_state;//tras pasar los 5 segundos volvemos a poner el estado anterior
			}
		}
		else {
			if(r < 0.5) {
				estadoEsp = true;
				time = 0.0;
			}
		}
		
	}
	
	

}
