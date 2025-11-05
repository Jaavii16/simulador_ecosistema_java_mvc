package simulator.view;

import java.awt.Dimension;
import java.awt.Frame;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import simulator.control.Controller;
import simulator.model.Animal;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

public class DeadAnimalsDialog extends JDialog implements EcoSysObserver{

	private Controller _ctrl;
	private DefaultTableModel _dataTableModel;
	private String[] _headers = { "Step", "DEAD"};
	private int _dead_animals = 0;
	private double _time = 0.0;
	private JTable tabla;
	
	
	public DeadAnimalsDialog(Controller ctrl) {
		super((Frame)null, true);
		_ctrl = ctrl;
		initGUI();
		_ctrl.addObserver(this);
	}

	private void initGUI() {
		setTitle("Dead animals");
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		setContentPane(mainPanel);
		
		JPanel buttonPanel = new JPanel();
		
		//TABLA
		_dataTableModel = new DefaultTableModel();
		_dataTableModel.setColumnIdentifiers(_headers);
		tabla = new JTable(_dataTableModel);
		mainPanel.add(new JScrollPane(tabla));
		
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
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		_dataTableModel.setRowCount(0);
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
	}

	@Override
	public void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		_dead_animals = (int) animals.stream().filter((a)->a.get_state().equals(Animal.State.DEAD)).count();
		_dataTableModel.addRow(new String[]{String.format("%.3f", time), _dead_animals+""});
	}

}
