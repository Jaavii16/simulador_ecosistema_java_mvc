package simulator.model;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.function.Predicate;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.model.Animal;
import simulator.model.Region;

import java.util.LinkedList;

public class RegionManager implements AnimalMapView {

	private int _widthM;
	private int _heightM;
	private int _col;
	private int _row;
	private int _widthR;
	private int _heightR;
	private Region[][] _regions;
	private Map<Animal, Region> _animal_region;

	public RegionManager(int cols, int rows, int width, int height) {
		if(cols <= 0)throw new IllegalArgumentException("no puede haber 0 columnas");
		if(rows <= 0)throw new IllegalArgumentException("no puede haber 0 filas");
		if(width <= 0)throw new IllegalArgumentException("la anchura tiene que ser positiva");
		if(height <= 0)throw new IllegalArgumentException("la altura tiene que ser positiva");
		_widthM = width;
		_heightM = height;
		_col = cols;
		_row = rows;
		_widthR = _widthM / _col + (width % cols != 0 ? 1 :0 );
		_heightR = _heightM / _row + (height % rows != 0 ? 1 :0 );
		_regions = new Region[_row][_col];
		for (int i = 0; i < _row; i++) {
			for (int j = 0; j < _col; j++) {
				_regions[i][j] = new DefaultRegion();
			}
		}
		_animal_region = new HashMap<Animal, Region>();
	}

	@Override
	public int get_cols() {
		return _col;
	}

	@Override
	public int get_rows() {
		return _row;
	}

	@Override
	public int get_width() {
		return _widthM;
	}

	@Override
	public int get_height() {
		return _heightM;
	}

	@Override
	public int get_region_width() {
		return _widthR;
	}

	@Override
	public int get_region_height() {
		return _heightR;
	}

	@Override
	public double get_food(Animal a, double dt) {
		int col = (int) a.get_position().getX() / _widthR;
		int row = (int) a.get_position().getY() / _heightR;
		return _regions[row][col].get_food(a, dt);
	}

	@Override
	public List<Animal> get_animals_in_range(Animal e, Predicate<Animal> filter) {
		List<Animal> l = new LinkedList<>();

//		//numero de regiones que puede ver en su rango horizontal y vertical
		int x_range = (int) (e.get_sight_range()/_widthR);
		int y_range = (int) (e.get_sight_range()/_heightR);
		
		//col y row actuales del animal
		int col = (int)e.get_position().getX()/_widthR;
		int row = (int)e.get_position().getY()/_heightR;
		
		int minCol = col - x_range;
		int maxCol = col + x_range;
		int minRow = row - y_range;
		int maxRow = row + y_range;
		
		if(minCol<0) {
			minCol = 0;
		}
		if(maxCol>_col-1) {
			maxCol = _col;
		}
		if(minRow<0) {
			minRow = 0;
		}
		if(maxRow>_row-1) {
			maxRow = _row;
		}
		
		for(int i=minRow;i<maxRow;i++) {
			for(int j=minCol;j<maxCol;j++) {
				for(Animal k: _regions[i][j].getAnimals()) {
					if(k!= e) {
						if(k.get_position().distanceTo(e.get_position())<e.get_sight_range() && filter.test(k)) {
							l.add(k);
						}
					}
				}
			}
		}
		return l;
	}

	void set_region(int row, int col, Region r) {
		for (Animal a : _regions[row][col].getAnimals()) {
			r.add_animal(a);
			_animal_region.put(a, r);
		}
		_regions[row][col] = r;
	}

	public void register_animal(Animal a) {
		a.init(this);
		int col = (int) a.get_position().getX() / _widthR;
		int row = (int) a.get_position().getY() / _heightR;
		_regions[row][col].add_animal(a);
		_animal_region.put(a, _regions[row][col]);
	}

	void unregister_animal(Animal a) {
		int col = (int) a.get_position().getX() / _widthR;
		int row = (int) a.get_position().getY() / _heightR;
		_regions[row][col].remove_animal(a);
		_animal_region.remove(a, _regions[row][col]);
	}

	void update_animal_region(Animal a) {
		int col = (int) a.get_position().getX() / _widthR;
		int row = (int) a.get_position().getY() / _heightR;
		Region r = _animal_region.get(a);
		if (r != _regions[row][col]) {
			_regions[row][col].add_animal(a);
			r.remove_animal(a);
			_animal_region.put(a, _regions[row][col]);
		}
	}

	void update_all_regions(double dt) {
		for (int i = 0; i < _row; i++) {
			for (int j = 0; j < _col; j++) {
				_regions[i][j].update(dt);
			}
		}
	}

	@Override
	public JSONObject as_JSON() {
		JSONArray regs = new JSONArray();
		for (int j = 0; j < _row; j++) {
			for (int i = 0; i < _col; i++) {
				JSONObject o_i = new JSONObject();
				o_i.put("row", j);
				o_i.put("col", i);
				o_i.put("data", _regions[j][i].as_JSON());
				regs.put(o_i);
			}
		}
		JSONObject j = new JSONObject();
		j.put("regiones", regs);
		return j;
	}
	
	private Region get_region(Animal a) {
		int col = (int) a.get_position().getX() / _widthR;
		int row = (int) a.get_position().getY() / _heightR;
		return _regions[row][col];
	}

	public Iterator<RegionData> iterator() {
        return new RegionIterator();
    }
	
	private class RegionIterator implements Iterator<RegionData> {
        private int actRow;
        private int actCol;

        public RegionIterator() {
            actRow = 0;
            actCol = 0;
        }

        @Override
        public boolean hasNext() {
            return actRow < _regions.length && actCol < _regions[actRow].length;
        }

        @Override
        public RegionData next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            RegionData nextRegion = new RegionData(actRow, actCol, _regions[actRow][actCol]);

            // Avanza a la siguiente posición
            moveToNextRegion();

            return nextRegion;
        }

        private void moveToNextRegion() {
            actCol++;
            if (actCol >= _regions[actRow].length) {
                actRow++;
                actCol = 0;
            }
        }
    }
	
}
