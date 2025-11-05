package simulator.model;

import java.util.List;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public class Sheep extends Animal {

	private Animal _danger_source;
	private SelectionStrategy _danger_strategy;
	private static double default_sight_r = 40.0;
	private static double default_speed = 35.0;
	private static double default_desire = 40.0;
	private static double default_energy = 20.0;

	public Sheep(SelectionStrategy mate_strategy, SelectionStrategy danger_strategy, Vector2D pos) {
		super("sheep", Diet.HERVIBORE, default_sight_r, default_speed, mate_strategy, pos);
		if(danger_strategy == null)throw new IllegalArgumentException("danger strategy no puede ser nulo");
		_danger_strategy = danger_strategy;
	}

	protected Sheep(Sheep p1, Animal p2) {
		super(p1, p2);
		_danger_strategy = p1._danger_strategy;
		_danger_source = null;
	}

	@Override
	public void update(double dt) {
		if (this.get_state() != State.DEAD) {
			// ---------------------------------------------
			switch (_state) {
			case NORMAL: 
				super.moverNormal();
				super.avanzar(dt, default_energy, default_desire, 1.0);
				if (_danger_source == null) {
					// buscar animal que se considere peligroso
					buscar_peligroso();
				}
				cambioEstadoNormal();
				break;
				
				case DANGER:
					if (_danger_source != null && _danger_source.get_state() == State.DEAD) {
						_danger_source = null;
					} else if (_danger_source == null) {
						super.moverNormal();
						super.avanzar(dt, default_energy * 1.2, default_desire, 2.0);
					} else if (_danger_source != null) {
						huir(dt);
					}
					// 3.cambio de estado
					cambioEstadoDanger();
					break;
					
				case MATE:
					if (_mate_target != null) {
						if (_mate_target.get_state() == State.DEAD
								|| _pos.distanceTo(_mate_target.get_position()) > _sight_range) {
							_mate_target = null;
						}
					}
					if (_mate_target == null) {
						List<Animal> l_mate_target = _region_mngr.get_animals_in_range(this,
								animal -> animal.get_genetic_code() == "sheep");
						if (l_mate_target.size() != 0) {
							_mate_target = _mate_strategy.select(this, l_mate_target);
						}
						else {
							super.moverNormal();
							super.avanzar(dt, default_energy * 1.2, default_desire, 2.0);
						}
					}
					
					if (_mate_target != null) {
						_dest = _mate_target.get_position();
						super.avanzar(dt, default_energy * 1.2, default_desire, 2.0);
						emparejar();
					}
					if (_danger_source == null) {
						buscar_peligroso();
					}
					cambioEstadoMate();
	
					break;
				default:
					break;
				}
				//despues de actualizar estado
				ajustarYComer(dt, 8.0);
		}	
				
	}

	

	protected void emparejar() {
		if (_pos.distanceTo(_mate_target.get_position()) < 8.0) {
			_desire = 0.0;
			_mate_target._desire = 0.0;
			if (!is_pregnant()) {
				int b = Utils._rand.nextInt(10) + 1;
				if (b <= 9) {
					_baby = new Sheep(this, _mate_target);
				}
				_mate_target = null;
			}
		}
	}
	
	
	private void buscar_peligroso() {
		List<Animal> l_dangerous = _region_mngr.get_animals_in_range(this,
				animal -> animal.get_diet() == Diet.CARNIVORE);
		_danger_source = _danger_strategy.select(this, l_dangerous);
	}
	
	private void huir(double dt) {
		_dest = _pos.plus(_pos.minus(_danger_source.get_position()).direction());
		super.move(2.0 * _speed * dt * Math.exp((_energy - 100.0) * 0.007));
		_age += dt;
		_energy -= default_energy * 1.2 * dt;
		if (_energy <= 0.0)
			_state = State.DEAD;
		_desire += default_desire * dt;
		if (_desire > 100)
			_desire = 100;
	}
	
	private void cambioEstadoDanger() {
		List<Animal> l_danger_source = _region_mngr.get_animals_in_range(this,
				animal -> animal.get_diet() == Diet.CARNIVORE);
		if (_danger_source == null || _pos.distanceTo(_danger_source.get_position()) > _sight_range) {
			_danger_source = _danger_strategy.select(this, l_danger_source);
		}
		if (_danger_source == null) {
			if (_desire < 65.0) {
				_state = State.NORMAL;
				_mate_target = null;
			} else {
				_state = State.MATE;

			}
		}
	}
	
	
	private void cambioEstadoNormal() {
		if (_danger_source != null) {
			_state = State.DANGER;
			_mate_target = null;
		}
		else if (_danger_source == null && _desire > 65.0) {
			_state = State.MATE;
		} 
	}
	
	private void cambioEstadoMate() {
		if (_danger_source == null) {
			buscar_peligroso();
		}
		if (_danger_source != null) {
			_state = State.DANGER;
			_mate_target = null;
		} else if (_danger_source == null && _desire < 65.0) {
			_state = State.NORMAL;
			_mate_target = null;
		}
	}
	
	

}