package simulator.model;

import java.util.List;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public class Wolf extends Animal {


	private Animal _hunt_target;
	private SelectionStrategy _hunting_strategy;
	private final static double default_sight_r = 50.0;
	private final static double default_speed = 60.0;
	private final static double default_desire = 30.0;
	private final static double default_energy = 18.0;

	
	public Wolf(SelectionStrategy mate_strategy, SelectionStrategy hunting_strategy, Vector2D pos) {
		super("wolf", Diet.CARNIVORE, default_sight_r, default_speed, mate_strategy, pos);
		if(hunting_strategy == null)throw new IllegalArgumentException("hunting strategy no puede ser nulo");
		_hunting_strategy = hunting_strategy;
	}

	protected Wolf(Wolf p1, Animal p2) {
		super(p1, p2);
		_hunting_strategy = p1._hunting_strategy;
		_hunt_target = null;
	}

	
	@Override
	public void update(double dt) {
		if (_state != State.DEAD) {
			switch(_state) {
			case NORMAL:
				super.moverNormal();
				super.avanzar(dt, default_energy, default_desire, 1.0);
				//CAMBIO DE ESTADO
				cambioEstadoNormal();
				break;
				
			case HUNGER:
				if ((_hunt_target == null) || (_hunt_target != null && (_hunt_target.get_state() == State.DEAD
				|| _pos.distanceTo(_hunt_target.get_position()) > _sight_range))) {
					buscarPresa();
				}
				if (_hunt_target == null) {
					super.moverNormal();
					super.avanzar(dt, default_energy, default_desire, 1.0);
				} else {
					_dest = _hunt_target.get_position();
					super.avanzar(dt, default_energy * 1.2, default_desire, 3.0);
					if (_pos.distanceTo(_hunt_target.get_position()) < 8.0) {
						cazar();
					}
				}
				cambioEstadoHunger();
				break;
				
			case MATE:
				if (_mate_target != null && (_mate_target.get_state() == State.DEAD
				|| _pos.distanceTo(_mate_target.get_position()) > _sight_range)) {
					_mate_target = null;
				}
				if (_mate_target == null) {
					List<Animal> l_mate_target = _region_mngr.get_animals_in_range(this,
							animal -> animal.get_genetic_code() == "wolf");
					if (l_mate_target.size() != 0) {
						_mate_target = _mate_strategy.select(this, l_mate_target);
					} else {
						super.moverNormal();
						super.avanzar(dt, default_energy, default_desire, 1.0);
					}
				}
				if (_mate_target != null) {
					_dest = _mate_target.get_position();
					super.avanzar(dt, default_energy * 1.2, default_desire, 3.0);
					emparejar();
				}
				cambioEstadoMate();
				break;
			default:
				break;
			}
			//despues de actualizar estado
			ajustarYComer(dt, 14.0);
		}
	}

	

	private void cambioEstadoMate() {
		if (_energy < 50.0) {
			_state = State.HUNGER;
			_mate_target = null;
		} else if (_energy >= 50 && _desire < 65.0) {
			_state = State.NORMAL;
			_hunt_target = null;
			_mate_target = null;
		}
	}

	private void cambioEstadoHunger() {
		if (_energy > 50.0) {
			if (_desire < 65.0) {
				_state = State.NORMAL;
				_hunt_target = null;
				_mate_target = null;
			} else {
				_state = State.MATE;
				_hunt_target = null;
			}

		}
	}

	private void cazar() {
		_hunt_target._state = State.DEAD;
		_hunt_target = null;
		_energy += 50.0;
		if (_energy > 100) {
			_energy = 100;
		}
	}

	private void buscarPresa() {
		List<Animal> l_hunt_target = _region_mngr.get_animals_in_range(this,
				animal -> animal.get_genetic_code() == "sheep");
		_hunt_target = _hunting_strategy.select(this, l_hunt_target);
	}

	private void emparejar() {
		if (_pos.distanceTo(_mate_target.get_position()) < 8.0) {
			_desire = 0.0;
			_mate_target._desire = 0.0;
			if (!is_pregnant()) {
				int b = Utils._rand.nextInt(10) + 1;
				if (b <= 9) {
					_baby = new Wolf(this, _mate_target);
				}
				_energy -= 10.0;
				if (_energy < 0) {
					_energy = 0;
				}
				_mate_target = null;
			}
		}
	}
	
	private void cambioEstadoNormal() {
		if (_energy < 50.0) {
			_state = State.HUNGER;
			_mate_target = null;
		} else {
			if (_desire > 65.0) {
				_state = State.MATE;
				_hunt_target = null;
			}
		}
	}

}
