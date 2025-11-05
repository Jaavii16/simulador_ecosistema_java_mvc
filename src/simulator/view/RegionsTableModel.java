package simulator.view;

import java.util.Iterator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.Animal;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.MapInfo.RegionData;
import simulator.model.RegionInfo;

public class RegionsTableModel extends AbstractTableModel implements EcoSysObserver{

	
	private static final long serialVersionUID = -8136302043297611786L;
	private Controller _ctrl;
	Object[][] data;
	String [] columnNames = new String[Animal.Diet.values().length + 3];
	
	
	RegionsTableModel(Controller ctrl) {
		_ctrl = ctrl;
		columnNames[0] = "Row";
		columnNames[1] = "Col";
		columnNames[2] = "Desc.";
		int cont = 3;
		for(Animal.Diet d: Animal.Diet.values()) {
			columnNames[cont]=d.name();
			cont++;
		}
		_ctrl.addObserver(this);
	}
	
	@Override
	public int getRowCount() {
		return data.length;
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return data[rowIndex][columnIndex];
	}
	

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		int numFilas = map.get_cols() * map.get_rows();
		data = new Object[numFilas][columnNames.length];
		RegionData r;
		Iterator<RegionData> it = map.iterator();
		int fila = 0;
		while(it.hasNext()) {
			r = it.next();
			data[fila][0] = r.row();
			data[fila][1] = r.col();
			data[fila][2] = r.r().toString();
			for(int k=3;k<columnNames.length;k++) {
				data[fila][k] = 0;
			}
			//buscamos la dieta de cada animal en las columnas para sumar 1 en el contador correspondiente
			for(AnimalInfo a: r.r().getAnimalsInfo()) {
				String diet = a.get_diet().name();
				int diet_ind = findDietInd(diet);
				data[fila][diet_ind]= (int) data[fila][diet_ind] + 1;
			}
			fila++;
		}
		fireTableDataChanged();
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		int numFilas = map.get_cols() * map.get_rows();
		data = new Object[numFilas][map.get_cols()];
		RegionData r;
		Iterator<RegionData> it = map.iterator();
		int fila = 0;
		while(it.hasNext()) {
			r = it.next();
			data[fila][0] = r.row();
			data[fila][1] = r.col();
			data[fila][2] = r.r().toString();
			for(int k=3;k<columnNames.length;k++) {
				data[fila][k] = 0;
			}
			fila++;
		}
		fireTableDataChanged();
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		increase(map, a);
		fireTableDataChanged();
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		int fila = (row-1)*map.get_cols() + col;
		data[fila][2] = r.toString();
		//vaciamos los contadores de animales para no contar animales 2 veces
		data[fila][3] = 0;
		data[fila][4] = 0;
		//buscamos la dieta de cada animal en las columnas para sumar 1 en el contador correspondiente
		for(AnimalInfo a: r.getAnimalsInfo()) {
			String diet = a.get_diet().name();
			int diet_ind = findDietInd(diet);
			data[fila][diet_ind]= (int) data[fila][diet_ind] + 1;
		}
		fireTableDataChanged();
	}

	private int findDietInd(String diet) {
		for(int i=3;i<columnNames.length;i++) {
			if(columnNames[i] == diet) {
				return i;
			}
		}
		return -1;//no deberia llegar aqui porque solo llegaría si la dieta del animal no existe
	}
	
	@Override
	public void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		//ponemos todos los contadores a 0 porque no sabemos cuales se han podido cambiar de fila o columna
		for(int i=0;i<map.get_rows();i++) {
			for(int j=0;j<map.get_cols();j++) {
				int fila = i*map.get_cols() + j;
				for(int k = 3;k<columnNames.length;k++) {
					data[fila][k] = 0;
				}
			}
		}
		
		for(AnimalInfo a: animals) {
			increase(map, a);
		}
		fireTableDataChanged();
	}

	private void increase(MapInfo map, AnimalInfo a) {
		String diet = a.get_diet().name();
		int diet_ind = findDietInd(diet);
		int col = (int) a.get_position().getX() / map.get_region_width();
		int row = (int) a.get_position().getY() / map.get_region_height();
		int fila = (row)*map.get_cols() + col;
		data[fila][diet_ind]= (int) data[fila][diet_ind] + 1;
	}

}
