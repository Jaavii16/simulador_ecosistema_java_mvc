package simulator.view;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.Animal;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;


public class SpeciesTableModel extends AbstractTableModel implements EcoSysObserver{

	
	private static final long serialVersionUID = 8836347120492456974L;
	private final static String _species = "Species";
	private Controller _ctrl;
	String [] columnNames = new String[Animal.State.values().length + 1];	
    private Map<String, Map<Animal.State, Integer>> data;//<especie<estado,contador>>
	
	SpeciesTableModel(Controller ctrl) {
		_ctrl = ctrl;
		columnNames[0] = _species;
		data = new HashMap<>();
		int cont = 1;
		for(Animal.State s: Animal.State.values()) {
			columnNames[cont]=s.name();
			cont++;
		}
		_ctrl.addObserver(this);
	}
	

	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		List<String> speciesList = new ArrayList<>(data.keySet());
        String species = speciesList.get(rowIndex);
		if(columnIndex == 0) {
			return species;
		}
		else {
			Animal.State[] states = Animal.State.values();
            Animal.State state = states[columnIndex - 1]; // Restamos 1 porque la columna 0 es para especies
            Map<Animal.State, Integer> stateMap = data.get(species);
            if(stateMap != null) {
            	return stateMap.getOrDefault(state, 0);
            }
            else return 0;
		}
	}
	
	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		data = new HashMap<>();		
		fill_counters(animals);
		fireTableDataChanged();
	}



	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		data = new HashMap<>();
		fireTableDataChanged();
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		increase(a);
		fireTableDataChanged();
	}


	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
	}

	@Override
	public void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		fill_counters(animals);
		fireTableDataChanged();
	}
	

	private void increase(AnimalInfo a) {//incrementa en 1 en la fila y columna correspondiente
		Map<Animal.State, Integer> aux = data.get(a.get_genetic_code());
		if(aux != null) {
			if(aux.containsKey(a.get_state())) {
				int cont = aux.get(a.get_state());
				cont++;
				aux.put(a.get_state(), cont);
			}
			else {
				aux.put(a.get_state(), 1);
			}
			data.put(a.get_genetic_code(), aux);
		}
		else {
			Map<Animal.State, Integer> nuevo = new HashMap<>();
			nuevo.put(a.get_state(), 1);
			data.put(a.get_genetic_code(), nuevo);
		}
		
	}
	
	private void fill_counters(List<AnimalInfo> animals) {//rellena las columnas de los estados recorriendo la lista de animales
		data.clear();
		for(AnimalInfo a: animals) {
			increase(a);
		}
	}

}
