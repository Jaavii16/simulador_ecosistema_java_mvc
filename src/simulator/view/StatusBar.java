package simulator.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JSeparator;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

public class StatusBar extends JPanel implements EcoSysObserver {


	private static final long serialVersionUID = 8500658839898278271L;
	private double _act_time;
	private int _num_animals;
	private int _width;
	private int _height;
	private int _cols;
	private int _rows;
	private Controller _ctrl;
	private JLabel tiempo;
	private JLabel animales_t;
	private JLabel dimension;
	
	StatusBar(Controller ctrl) {
		_ctrl = ctrl;
		initGUI();
		_ctrl.addObserver(this);
	}
	
	private void initGUI() {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		setBorder(BorderFactory.createBevelBorder(1));
		
		//TIEMPO
		tiempo = new JLabel();
		this.add(tiempo);
		JSeparator s = new JSeparator(JSeparator.VERTICAL);
		s.setPreferredSize(new Dimension(10, 20));
		this.add(s);
		//TOTAL DE ANIMALES
		animales_t = new JLabel();
		this.add(animales_t);
		JSeparator s2 = new JSeparator(JSeparator.VERTICAL);
		s2.setPreferredSize(new Dimension(10, 20));
		this.add(s2);
		//DIMENSION
		dimension = new JLabel();
		this.add(dimension);
	}
	
	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		setValues(time, map, animals);
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		setValues(time, map, animals);
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		_act_time = time;
		_num_animals++;
		actLabels();
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
	}

	@Override
	public void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		_act_time = time;
		_num_animals = animals.size();
		actLabels();
	}

	
	//actualizamos los JLabels cuando cambian de valor
	void actLabels() {
		tiempo.setText("Time: " + String.format("%.3f", _act_time));
		animales_t.setText("Total Animals: " + _num_animals);
		dimension.setText("Dimension: " + _width + "x" + _height + " " + _cols + "x" + _rows);
	}
	
	//cargamos los valores en las variables
	private void setValues(double time, MapInfo map, List<AnimalInfo> animals) {
		_act_time = time;
		_num_animals = animals.size();
		_cols = map.get_cols();
		_rows = map.get_rows();
		_height = map.get_height();
		_width = map.get_width();
		actLabels();
	}
}
