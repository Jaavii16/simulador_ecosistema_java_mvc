package simulator.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.Animal;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

public class StatsTableModel extends AbstractTableModel implements EcoSysObserver{

	private static final long serialVersionUID = 8582859891644856384L;

	private String[] headers = {"Time", "New babys"};
	private Controller _ctrl;
	private Map<String, Integer> data;
	
	
	
	StatsTableModel(Controller ctrl) {
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
			return data.get(time);
		}
	}
	
	@Override
	public String getColumnName(int column) {
		return headers[column];
	}

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		data = new TreeMap<>();
		fireTableDataChanged();
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		data = new TreeMap<>();
		fireTableDataChanged();
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		if(time > 0.0) {//los que vienen del JSON no se toman como nacidos
			String tiempo = String.format("%.3f", time);
			int num = data.getOrDefault(tiempo, 0)+ 1;
			data.put(tiempo, num);
		}
		fireTableDataChanged();
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
	}

	@Override
	public void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
	}

}
