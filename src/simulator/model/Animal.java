package simulator.model;

import org.json.JSONObject;

import org.json.JSONArray;

import simulator.misc.Utils;
import simulator.misc.Vector2D;
import simulator.model.Animal.State;

public abstract class Animal implements Entity, AnimalInfo {
	protected String _genetic_code;
	protected Diet _diet;
	protected State _state;
	protected Vector2D _pos;
	protected Vector2D _dest;
	protected double _speed;
	protected double _energy;
	protected double _age;
	protected double _desire;
	protected double _sight_range;
	protected Animal _mate_target;
	protected Animal _baby;
	protected AnimalMapView _region_mngr;
	protected SelectionStrategy _mate_strategy;



public enum State{
		NORMAL, MATE, HUNGER, DANGER, DEAD
	}
	
	public enum Diet{
		HERVIBORE, CARNIVORE
	}

	protected Animal(String genetic_code, Diet diet, double sight_range, double init_speed,
			SelectionStrategy mate_strategy, Vector2D pos) {
		if (genetic_code == "")
			throw new IllegalArgumentException("El codigo genetico no puede ser vacio");
		if (sight_range < 0)
			throw new IllegalArgumentException("El campo de vision debe ser un valor positivo");
		if (init_speed < 0)
			throw new IllegalArgumentException("La velocidad inicial debe ser un valor positivo");
		if (mate_strategy == null)
			throw new IllegalArgumentException("Mate strategy no puede ser nulo");
		_genetic_code = genetic_code;
		_diet = diet;
		_sight_range = sight_range;
		_dest = null;
		_pos = pos;
		_mate_strategy = mate_strategy;
		_speed = Utils.get_randomized_parameter(init_speed, 0.1);
		_state = State.NORMAL;
		_energy = 100.0;
		_desire = 0.0;
		_mate_target = null;
		_baby = null;
		_region_mngr = null;
	}

	protected Animal(Animal p1, Animal p2) {
		_dest = null;
		_mate_target = null;
		_baby = null;
		_region_mngr = null;
		_state = State.NORMAL;
		_desire = 0.0;
		_genetic_code = p1.get_genetic_code();
		_diet = p1.get_diet();
		_mate_strategy = p2._mate_strategy;
		_energy = (p1.get_energy() + p2.get_energy()) / 2;
		_pos = p1.get_position().plus(Vector2D.get_random_vector(-1, 1).scale(60.0 * (Utils._rand.nextGaussian() + 1)));
		_sight_range = Utils.get_randomized_parameter((p1.get_sight_range() + p2.get_sight_range()) / 2, 0.2);
		_speed = Utils.get_randomized_parameter((p1.get_speed() + p2.get_speed()) / 2, 0.2);
	}

	void init(AnimalMapView reg_mngr) {
		_region_mngr = reg_mngr;
		if (_pos == null) {
			double x = Utils._rand.nextDouble(_region_mngr.get_width());
			double y = Utils._rand.nextDouble(_region_mngr.get_height());
			_pos = new Vector2D(x, y);
		} else {
			_pos = ajustarPos(_pos);
		}
		double z = Utils._rand.nextDouble(_region_mngr.get_width());
		double w = Utils._rand.nextDouble(_region_mngr.get_height());
		_dest = new Vector2D(z, w);
	}

	public Animal deliver_baby() {
		Animal baby_aux = _baby;
		_baby = null;
		return baby_aux;
	}

	protected boolean estaDentro(Vector2D nuevaPos) {
		return (nuevaPos.getX() <= _region_mngr.get_width() && nuevaPos.getY() <= _region_mngr.get_height() && nuevaPos.getX() >= 0 && nuevaPos.getY() >= 0);
	}

	protected void move(double speed) {
		_pos = _pos.plus(_dest.minus(_pos).direction().scale(speed));
		if (!estaDentro(_pos)) {
			_pos = ajustarPos(_pos);
		}
	}

	protected Vector2D ajustarPos(Vector2D pos) {
		double x = pos.getX();
		double y = pos.getY();
		while (x >= _region_mngr.get_width())
			x = (x - _region_mngr.get_width());
		while (x < 0)
			x = (x + _region_mngr.get_width());
		while (y >= _region_mngr.get_height())
			y = (y - _region_mngr.get_height());
		while (y < 0)
			y = (y + _region_mngr.get_height());
		pos = new Vector2D(x, y);
		return pos;
	}

	public JSONObject as_JSON() {
		JSONObject j = new JSONObject();
		JSONArray j_a = _pos.asJSONArray();
		j.put("pos", j_a);
		j.put("gcode", this.get_genetic_code());
		j.put("diet", this.get_diet());
		j.put("state", this.get_state());
		return j;
	}

	public abstract void update(double dt);

	protected void avanzar(double dt, double d_energy, double d_desire, double d_speed) {

		move(d_speed * _speed * dt * Math.exp((_energy - 100.0) * 0.007));
		_age += dt;
		_energy -= d_energy * dt;
		if (_energy < 0.0)
			_energy = 0.0;
		_desire += d_desire * dt; // entre 0.0 y 100.0 siempre
		if (_desire > 100.0)
			_desire = 100.0;
	}

	@Override
	public State get_state() {
		return this._state;
	}

	@Override
	public Vector2D get_position() {
		return this._pos;
	}

	@Override
	public String get_genetic_code() {
		return this._genetic_code;
	}

	@Override
	public Diet get_diet() {
		return this._diet;
	}

	@Override
	public double get_speed() {
		return this._speed;
	}

	@Override
	public double get_sight_range() {
		return this._sight_range;
	}

	@Override
	public double get_energy() {
		return this._energy;
	}

	@Override
	public double get_age() {
		return this._age;
	}

	@Override
	public Vector2D get_destination() {
		return this._dest;
	}

	@Override
	public boolean is_pregnant() {
		if (_baby == null) {
			return false;
		} else
			return true;
	}

	
	protected void moverNormal() {
		if (_pos.distanceTo(_dest) < 8.0) {
			double x = Utils._rand.nextDouble(_region_mngr.get_width());
			double y = Utils._rand.nextDouble(_region_mngr.get_height());
			_dest = new Vector2D(x, y);
		}
	}
	
	protected void ajustarYComer(double dt, double age) {
		if (!estaDentro(_pos)) {
			ajustarPos(_pos);
			_state = State.NORMAL;
		}
		if (_energy == 0 || _age > age) {
			_state = State.DEAD;
		}
		if(_state != State.DEAD) {
			_energy += _region_mngr.get_food(this, dt);
			if (_energy > 100.0)
				_energy = 100.0;
		}
	}

}
