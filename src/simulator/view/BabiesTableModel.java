package simulator.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

public class BabiesTableModel extends AbstractTableModel implements EcoSysObserver{

	private String[] headers = {"Time"};
	private Controller _ctrl;
	private Map<String, Map<String, Integer>> data;//<tiempo,<especie,num>>
	private List<String>species;
	
	
	BabiesTableModel(Controller ctrl) {
		_ctrl = ctrl;
		_ctrl.addObserver(this);
	}

	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public int getColumnCount() {
		return headers.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		List<String> times = new ArrayList<>(data.keySet());
        String time = times.get(rowIndex);
        
		if(columnIndex == 0) {
			return time;
		}
		else {
			String spec = species.get(columnIndex-1);//-1 porque la primera es el tiempo
			return data.get(time).get(spec);
		}
	}
	
	@Override
	public String getColumnName(int column) {
		return headers[column];
	}

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		data = new TreeMap<>();
		species = animals.stream().map(AnimalInfo::get_genetic_code).distinct().collect(Collectors.toList());
		headers = new String[1 + species.size()];
		headers[0] = "Time";
		int cont = 1;
		for(String s: species) {
			headers[cont] = s;
			cont++;
		}
		
		fireTableStructureChanged();
		fireTableDataChanged();
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		data = new TreeMap<>();
		headers = new String[1];
		headers[0] = "Time";
		species.clear();
		fireTableStructureChanged();
		fireTableDataChanged();
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		if(time > 0.0) {//los que vienen del JSON no se toman como nacidos
			String tiempo = String.format("%.3f", time);
			if(!species.contains(a.get_genetic_code())) {
				species.add(a.get_genetic_code());
			}
			//ACTUALIZAMOS LOS HEADERS POR SI FALTA O SOBRA ALGUNO
			headers = new String[1 + species.size()];
			headers[0] = "Time";
			int cont = 1;
			for(String s: species) {
				headers[cont] = s;
				cont++;
			}
			
			Map<String, Integer> aux = data.get(tiempo);
			if(aux != null) {
				int num = aux.getOrDefault(a.get_genetic_code(), 0)+ 1;
				aux.put(a.get_genetic_code(), num);
				data.put(tiempo, aux);
			}
			else {
				Map<String, Integer> nuevo = new HashMap<>();
				nuevo.put(a.get_genetic_code(), 1);
				data.put(tiempo, nuevo);
			}
		}
		fireTableStructureChanged();
		fireTableDataChanged();
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
	}

	@Override
	public void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
	}
	
}
