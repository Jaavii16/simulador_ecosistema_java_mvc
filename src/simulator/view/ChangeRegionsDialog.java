package simulator.view;


import java.awt.Dimension;
import java.awt.Frame;
import java.util.Iterator;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JComboBox;
import javax.swing.JButton;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.control.Controller;
import simulator.launcher.Main;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

public class ChangeRegionsDialog extends JDialog implements EcoSysObserver {

	
	private static final long serialVersionUID = -2109300465276784359L;
	private DefaultComboBoxModel<String> _regionsModel;
	private DefaultComboBoxModel<String> _fromRowModel;
	private DefaultComboBoxModel<String> _toRowModel;
	private DefaultComboBoxModel<String> _fromColModel;
	private DefaultComboBoxModel<String> _toColModel;
	private DefaultTableModel _dataTableModel;
	private Controller _ctrl;
	private List<JSONObject> _regionsInfo;
	private String[] _headers = { "Key", "Value", "Description" };
	private int _status;
	
	private static final String _texto = "Select a region type, the rows/cols interval, and provides values" 
	+ "for the parameters in the Value column (default values are used for parameters with no value).";
	
	ChangeRegionsDialog(Controller ctrl) {
		super((Frame)null, true);
		_ctrl = ctrl;
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
        //TEXTO DE AYUDA
        JTextArea help_text = new JTextArea(_texto);
		help_text.setBackground(getForeground());
		help_text.setLineWrap(true);
		help_text.setWrapStyleWord(true);
		help_text.setEditable(false);
        
        mainPanel.add(help_text);
		
		
		
		
		
		_regionsInfo = Main.region_factory.get_info();
		
		//TABLA
		_dataTableModel = new DefaultTableModel() {
		@Override
		public boolean isCellEditable(int row, int column) {
			return column == 1;
		}
		};
		_dataTableModel.setColumnIdentifiers(_headers);
		JTable tabla = new JTable(_dataTableModel);
		mainPanel.add(new JScrollPane(tabla));
        
		
        //COMBOBOXES
		_regionsModel = new DefaultComboBoxModel<>();
		for (JSONObject regInfo : _regionsInfo) {
			_regionsModel.addElement(regInfo.getString("type"));
		}
		
		_fromColModel = new DefaultComboBoxModel<>();
		_fromRowModel = new DefaultComboBoxModel<>();
		_toColModel = new DefaultComboBoxModel<>();
		_toRowModel = new DefaultComboBoxModel<>();
		
		JComboBox<String> regionsComboBox = new JComboBox<>(_regionsModel);
		JComboBox<String> rowFromComboBox = new JComboBox<>(_fromRowModel);
		JComboBox<String> rowToComboBox = new JComboBox<>(_toRowModel);
		JComboBox<String> colFromComboBox = new JComboBox<>(_fromColModel);
		JComboBox<String> colToComboBox = new JComboBox<>(_toColModel);
		
		regionsComboBox.addActionListener((e) -> {
			int i = regionsComboBox.getSelectedIndex();
			JSONObject info = _regionsInfo.get(i);
			JSONObject data = info.getJSONObject("data");
			_dataTableModel.setRowCount(0);//borra lo q habia anteriormente
			Iterator<String> it = data.keySet().iterator();
			while(it.hasNext()) {
				String k = it.next();//la key
				String desc = data.getString(k);//el valor del JSONObject q tiene esa key
				_dataTableModel.addRow(new String[] {k, "" , desc});
			}
		});
		
		comboBoxPanel.add(new JLabel("Region type: "));
		comboBoxPanel.add(regionsComboBox);
		comboBoxPanel.add(new JLabel("Row from/to: "));
		comboBoxPanel.add(rowFromComboBox);
		comboBoxPanel.add(rowToComboBox);
		comboBoxPanel.add(new JLabel("Column from/to: "));
		comboBoxPanel.add(colFromComboBox);
		comboBoxPanel.add(colToComboBox);
		mainPanel.add(comboBoxPanel);
		
		//BOTONES
		JButton cancelButton = new JButton("Cancel");
        JButton okButton = new JButton("OK");
        cancelButton.addActionListener((e)->{
        	_status = 0;
        	this.setVisible(false);
        });
        okButton.addActionListener((e)->{
        	JSONObject region_data = getJSON();
        	String region_type = (String) regionsComboBox.getSelectedItem();
        	int row_from = Integer.parseInt((String)rowFromComboBox.getSelectedItem()) ;
        	int row_to = Integer.parseInt((String) rowToComboBox.getSelectedItem());
        	int col_from = Integer.parseInt((String) colFromComboBox.getSelectedItem());
        	int col_to = Integer.parseInt((String) colToComboBox.getSelectedItem());
        	JSONObject jo = new JSONObject();
        	JSONArray regions = new JSONArray();
        	JSONObject region = new JSONObject();
        	JSONArray row = new JSONArray();
        	row.put(row_from);
        	row.put(row_to);
        	JSONArray col = new JSONArray();
        	col.put(col_from);
        	col.put(col_to);
        	region.put("row", row);
        	region.put("col", col);
        	JSONObject spec = new JSONObject();
        	spec.put("type", region_type);
        	spec.put("data", region_data);
        	region.put("spec",spec);
        	regions.put(region);
        	jo.put("regions", regions);
        	try {
        		_ctrl.set_regions(jo);
        		_status = 1;
        		setVisible(false);
        	}catch(Exception ex) {
        		ViewUtils.showErrorMsg("La función no se ha podido realizar con éxito");
        	}
        });
        buttonPanel.add(cancelButton);
        buttonPanel.add(okButton);
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
		setComboBoxes(map);
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		setComboBoxes(map);
	}
	
	//vacia los comboBoxes por si había algo y asigna los nuevos valores
	private void setComboBoxes(MapInfo map) {
		_fromRowModel.removeAllElements();
		_toRowModel.removeAllElements();
		_fromColModel.removeAllElements();
		_toColModel.removeAllElements();
		for(int i=1;i<=map.get_cols();i++) {
			_fromColModel.addElement(i+"");
			_toColModel.addElement(i+"");	
		}
		for(int j=1;j<=map.get_rows();j++) {
			_fromRowModel.addElement(j+"");
			_toRowModel.addElement(j+"");
		}
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
	}

	@Override
	public void onAvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
	}
	
	private JSONObject getJSON() {
		JSONObject jo= new JSONObject();
		for (int i = 0; i < _dataTableModel.getRowCount(); i++) {
			String k = _dataTableModel.getValueAt(i, 0).toString();
			String v = _dataTableModel.getValueAt(i, 1).toString();
			jo.put(k, v);
		}
		return jo;
	}

}
