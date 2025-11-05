package simulator.model;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public class SlowSheep extends Sheep{
	
	private boolean enEstado;
	private double time;
	private double aux_speed;

	public SlowSheep(SelectionStrategy mate_strategy, SelectionStrategy danger_strategy, Vector2D pos) {
		super(mate_strategy, danger_strategy, pos);
		enEstado = false;
		time = 0.0;
		this._genetic_code = "slow sheep";
		this.aux_speed = this._speed;
	}

	public SlowSheep(SlowSheep slowSheep, Animal _mate_target) {
		super(slowSheep, _mate_target);
		enEstado = false;
		time = 0.0;
		this._genetic_code = "slow sheep";
		this.aux_speed = this._speed;
	}

	@Override
	public void update(double dt) {
		super.update(dt);
		if(enEstado) {
			_speed = aux_speed*0.4;
			time += dt;
			if(time>=2.0) {
				enEstado = false;
				time = 0.0;
				_speed = aux_speed;
			}
		}
		else {
			Double r = Utils._rand.nextDouble();
			if(r <= 0.4) {
				enEstado = true;
			}
		}
	}

	@Override
	protected void emparejar() {
		if (_pos.distanceTo(_mate_target.get_position()) < 8.0) {
			_desire = 0.0;
			_mate_target._desire = 0.0;
			if (!is_pregnant()) {
				int b = Utils._rand.nextInt(10) + 1;
				if (b <= 9) {
					_baby = new SlowSheep(this, _mate_target);
				}
				_mate_target = null;
			}
		}
	}
	
}
