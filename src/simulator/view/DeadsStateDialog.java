package simulator.view;

import java.awt.Dimension;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import simulator.control.Controller;
import simulator.model.Animal;
import simulator.model.Animal.State;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

public class DeadsStateDialog extends JDialog implements EcoSysObserver{

	
	private Controller _ctrl;
	private DefaultTableModel _dataTableModel;
	private String[] _headers = { "Step", "NUMBER OF ANIMALS"};
	private JTable tabla;
	private Vector<DefaultTableModel> _states_models;
	private Vector<DefaultTableModel>_code_models;
	//private HashSet<String> gen_codes;
	private List<String> gen_codes_list;
	private DefaultComboBoxModel<Animal.State> _statesModel;
	private DefaultComboBoxModel<String>_codesModel;
	
	
	public DeadsStateDialog(Controller ctrl) {
		super((Frame)null, true);
		_ctrl = ctrl;
		_states_models = new Vector<>();
		_code_models = new Vector<>();
		//gen_codes = new HashSet<>();
		gen_codes_list = new ArrayList<>();
		_codesModel = new DefaultComboBoxModel<>();
		initGUI();
		_ctrl.addObserver(this);
	}
	
	private void initGUI() {
		setTitle("Dead animals");
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		setContentPane(mainPanel);
		
		JPanel buttonPanel = new JPanel();
		JPanel comboBoxPanel = new JPanel();
		
		
		//INICIALIZO LOS MODELOS DE ESTADOS
		for(Animal.State s: Animal.State.values()) {
			DefaultTableModel _model = new DefaultTableModel();
			_model.setColumnIdentifiers(_headers);
			_states_models.add(_model);
		}
		
		//COMBOBOX
		_statesModel = new DefaultComboBoxModel<>();
		for(Animal.State s: Animal.State.values()) {
			_statesModel.addElement(s);
		}
		
		//TABLA
//		_dataTableModel = new DefaultTableModel();
//		_dataTableModel.setColumnIdentifiers(_headers);
		tabla = new JTable(_states_models.get(0));
		mainPanel.add(new JScrollPane(tabla));
		
		JComboBox<Animal.State> boxStates = new JComboBox<Animal.State>();
		boxStates.setModel(_statesModel);
		boxStates.addActionListener((e)->{
			tabla.setModel(_states_models.get(boxStates.getSelectedIndex()));
		});
		comboBoxPanel.add(boxStates);
		
		JComboBox<String> boxCodes = new JComboBox<String>();
		boxCodes.setModel(_codesModel);
		boxCodes.addActionListener(e->{
			if(!_code_models.isEmpty() && _codesModel.getSize() != 0) {
				tabla.setModel(_code_models.get(boxCodes.getSelectedIndex()));
			}
		});
		comboBoxPanel.add(boxCodes);
		
		
		//BOTONES
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener((e)->{
        	this.setVisible(false);
        });
		buttonPanel.add(cancelButton);
		mainPanel.add(comboBoxPanel);
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
		//INICIALIZO LOS MODELOS DE CODIGOS GENETICOS Y EL COMBOBOX
		this.gen_codes_list = animals.stream().map(AnimalInfo::get_genetic_code).distinct().collect(Collectors.toList());
		
//		for(AnimalInfo a: animals) {
//			gen_codes.add(a.get_genetic_code());
//		}
		
		for(String s:gen_codes_list) {
			DefaultTableModel _model = new DefaultTableModel();
			_model.setColumnIdentifiers(_headers);
			_code_models.add(_model);
			_codesModel.addElement(s);
		}
		
		actualizar(animals, time);
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		for(DefaultTableModel m: _states_models) {
			m.setRowCount(0);
		}
		for(DefaultTableModel mo: _code_models) {
			mo.setRowCount(0);
		}
		//this.gen_codes.clear();
		this.gen_codes_list.clear();
		this._codesModel.removeAllElements();
		this._code_models.removeAllElements();
		//this._statesModel.removeAllElements();
		tabla.setModel(this._states_models.get(0));
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		if(!gen_codes_list.contains(a.get_genetic_code())) {
			gen_codes_list.add(a.get_genetic_code());
		}
		//gen_codes.add(a.get_genetic_code());
		if(_codesModel.getSize() != gen_codes_list.size()) {
			//añado el nuevo codigo al comboBox
			_codesModel.addElement(a.get_genetic_code());
			//añado un nuevo modelo para el nuevo codigo
			DefaultTableModel _model = new DefaultTableModel();
			_model.setColumnIdentifiers(_headers);
			_code_models.add(_model);
		}
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
	}

	@Override
	public void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		actualizar(animals, time);
	}

	private void actualizar(List<AnimalInfo> animals, double time) {
		int state = 0;
		int count;
		for(DefaultTableModel m: _states_models) {
			final int sstate = state;
			count = (int) animals.stream().filter((a)->a.get_state().equals(Animal.State.values()[sstate])).count();
			m.addRow(new String[]{String.format("%.3f", time), count+""});
			state++;
		}
		int code = 0;
		int num;
		//gen_codes_list = new ArrayList<>(gen_codes);
		for(DefaultTableModel mo: _code_models) {
			final int ccode = code;
			num = (int) animals.stream().filter(a->a.get_genetic_code() == gen_codes_list.get(ccode)).count();
			code++;
			mo.addRow(new String[]{String.format("%.3f", time), num+""});
		}
	}
	
}
