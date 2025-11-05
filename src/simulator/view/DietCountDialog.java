package simulator.view;

import java.awt.Dimension;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.control.Controller;
import simulator.launcher.Main;
import simulator.model.Animal;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

public class DietCountDialog extends JDialog implements EcoSysObserver{

	private DefaultTableModel _dataTableModel;
	private DefaultComboBoxModel<Animal.Diet> _diet_model;
	private Controller _ctrl;
	private String[] _headers = {"Time", "Count"};
	
	static class Info{
		private double time;
		private Integer[] cuentas;//lista con el num d animales d cada dieta
		public Info() {
			time = 0.0;
			cuentas = new Integer[Animal.Diet.values().length];
		}
		
	}
	
	private List<Info> data;
	private int index;
	
	
	DietCountDialog(Controller ctrl) {
		super((Frame)null, true);
		_ctrl = ctrl;
		data = new ArrayList<>();
		index = 0;
		initGUI();
		_ctrl.addObserver(this);
	}
	
	private void initGUI() {
		setTitle("Change Regions");
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		setContentPane(mainPanel);
		//creamos los paneles
        JPanel comboBoxPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        
		
		
		//TABLA
		_dataTableModel = new DefaultTableModel() {

			@Override
			public int getRowCount() {
				return data.size();
			}

			@Override
			public int getColumnCount() {
				return _headers.length;
			}

			@Override
			public String getColumnName(int column) {
				return _headers[column];
			}

			@Override
			public Object getValueAt(int row, int column) {
				Info i = data.get(row);
				if(column == 0) {
					return i.time;
				}
				else {
					return i.cuentas[index];
				}
			}
			
		};
		_dataTableModel.setColumnIdentifiers(_headers);
		JTable tabla = new JTable(_dataTableModel);
		mainPanel.add(new JScrollPane(tabla));
        
		
        //COMBOBOX
		_diet_model = new DefaultComboBoxModel<>();
		for(Animal.Diet d: Animal.Diet.values()) {
			_diet_model.addElement(d);
		}
		
		
		JComboBox<Animal.Diet> dietComboBox = new JComboBox<>(_diet_model);
		
		
		
		comboBoxPanel.add(dietComboBox);
		mainPanel.add(comboBoxPanel);
		
		//BOTONES
		JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener((e)->{
        	this.setVisible(false);
        });
        
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel);
        
		setPreferredSize(new Dimension(700, 400));
		pack();
		setResizable(false);
		setVisible(false);
	}
	
	public void open(Frame parent) {
		setLocation(//
		parent.getLocation().x + parent.getWidth() / 2 - getWidth() / 2, //
		parent.getLocation().y + parent.getHeight() / 2 - getHeight() / 2);
		pack();
		setVisible(true);
	}
	
	
	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		Info i = new Info();
		i.time = time;
		Integer[] list = i.cuentas;
		for(Animal.Diet d: Animal.Diet.values()) {
			i.cuentas[d.ordinal()] = (int) animals.stream().filter(a->a.get_diet().equals(d)).count();
		}
		data.add(i);
		_dataTableModel.addRow(new String[] {String.format("%.3f", time)});
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		data = new ArrayList<>();
		index = 0;
		_dataTableModel.setRowCount(0);
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		if(this.data.size() == 0 || time > this.data.get(data.size()-1).time) {
			Info i = new Info();
			i.time = time;
			i.cuentas[a.get_diet().ordinal()] = 1;
		}
		else {
			data.get(data.size()-1).cuentas[a.get_state().ordinal()]++;
		}
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
	}

	@Override
	public void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		
	}

}
