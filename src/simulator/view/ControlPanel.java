package simulator.view;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import javax.swing.*;

import simulator.control.Controller;
import simulator.launcher.Main;
import simulator.model.Animal;
import simulator.model.AnimalInfo;

import org.json.JSONObject;
import org.json.JSONTokener;

public class ControlPanel extends JPanel {

	
	private static final long serialVersionUID = 2622912457958537541L;
	private Controller _ctrl;
	private ChangeRegionsDialog _changeRegionsDialog;
	private JToolBar _toolaBar;
	private JFileChooser _fc;
	private boolean _stopped = true; // utilizado en los botones de run/stop
	private JButton _quitButton;
	private JButton _folderButton;
	private JButton _viewButton;
	private JButton _regionsDialog;
	private JButton _runButton;
	private JTextField _dt_textField;
	private JSpinner _steps_spinner;
	private JButton _stopButton;
	private JButton _deadsButton;
	private JButton _deadsStateButton;
	private DeadAnimalsDialog _deadsDialog;
	private DeadsStateDialog _deadsStateDialog;
	
	private final static int _default_steps = 10000;
	private final static int _minimun_steps = 0;
	private final static int _maximum_steps = 1000000;
	private final static int _default_increment = 10;
	private final static double _default_dt = 0.03;
	private SpinnerModel spinnerModel = new SpinnerNumberModel(_default_steps, // valor inicial
            _minimun_steps,   // valor mínimo
            _maximum_steps, // valor máximo
            _default_increment);  // incremento
	
	

	
	static class Info{
		double time;
		long[] counts;
		Info(List<AnimalInfo> animals, double time){
			counts = new long[Animal.State.values().length];
			for(Animal.State s: Animal.State.values()) {
				counts[s.ordinal()] = animals.stream().filter((a)-> (a.get_state() == s)).count();
			}
		}
	}
	
	ControlPanel(Controller ctrl) {
		_ctrl = ctrl;
		_deadsDialog = new DeadAnimalsDialog(ctrl);
		_deadsStateDialog = new DeadsStateDialog(ctrl);
		initGUI();
	}
	
	private void initGUI() {
		setLayout(new BorderLayout());
		_toolaBar = new JToolBar();
		add(_toolaBar, BorderLayout.PAGE_START);
		
		//FILE CHOOSER
		_fc = new JFileChooser();
		_fc.setCurrentDirectory(new File(System.getProperty("user.dir") + "/resources/examples"));
		_folderButton = new JButton();
		_folderButton.setToolTipText("Open file");
		_folderButton.setIcon(new ImageIcon("resources/icons/open.png"));
		_folderButton.addActionListener((e) -> {
			_fc.showOpenDialog(ViewUtils.getWindow(this));
			if(_fc.getSelectedFile() != null) {
				File file = _fc.getSelectedFile();
				InputStream is;
				try {
					is = new FileInputStream(file);
					JSONObject jo = load_JSON_file(is);
					int _cols, _rows, _width, _height;
					_cols = jo.getInt("cols");
					_rows = jo.getInt("rows");
					_width = jo.getInt("width");
					_height = jo.getInt("height");
					
					_ctrl.reset(_cols, _rows, _width, _height);
					_ctrl.load_data(jo);
				} catch (FileNotFoundException e1) {
					ViewUtils.showErrorMsg("The file is not found");
				}
			}
		});
				
		_toolaBar.add(_folderButton);
		
		_toolaBar.addSeparator();
		
		//VISOR
		_viewButton = new JButton();
		_viewButton.setToolTipText("Open de viewer");
		_viewButton.setIcon(new ImageIcon("resources/icons/viewer.png"));
		_viewButton.addActionListener((e) -> {
			new MapWindow(ViewUtils.getWindow(this), _ctrl);
		});
		_toolaBar.add(_viewButton);
		
		//DIALOGO DE CAMBIO DE REGIONES
		_regionsDialog = new JButton();
		_regionsDialog.setToolTipText("Opens the regions dialog");
		_regionsDialog.setIcon(new ImageIcon("resources/icons/regions.png"));
		_regionsDialog.addActionListener((e)->_changeRegionsDialog.open((ViewUtils.getWindow(this))));
		_toolaBar.add(_regionsDialog);
		
		_toolaBar.addSeparator();
		
		//DEADS DIALOG BUTTON
		_deadsButton = new JButton("DEADS");
		_deadsButton.setToolTipText("Open de deads dialog");
		_deadsButton.addActionListener((e)-> _deadsDialog.open((ViewUtils.getWindow(this))));
		_toolaBar.add(_deadsButton);
		
		_toolaBar.addSeparator();
		
		//DEADS BY STATE DIALOG BUTTON
		_deadsStateButton = new JButton("DEADS_BY_STATE");
		_deadsStateButton.setToolTipText("Open de deads by state dialog");
		_deadsStateButton.addActionListener((e)-> _deadsStateDialog.open((ViewUtils.getWindow(this))));
		_toolaBar.add(_deadsStateButton);
		
		_toolaBar.addSeparator();
		
		//RUN
		_runButton = new JButton();
		_runButton.setToolTipText("Runs simulation");
		_runButton.setIcon(new ImageIcon("resources/icons/run.png"));
		_runButton.addActionListener((e)-> {
			_folderButton.setEnabled(false);
			_quitButton.setEnabled(false);
			_regionsDialog.setEnabled(false);
			_viewButton.setEnabled(false);
			_deadsButton.setEnabled(false);
			this.setEnabled(false);
			_stopped = false;
			String dt_text = _dt_textField.getText();
			try {
				double dt = Double.parseDouble(dt_text);
				int steps = (int) _steps_spinner.getValue();
				run_sim(steps, dt);
			}catch(NumberFormatException ex) {
				ViewUtils.showErrorMsg("El texto ingresado en el text field no es un numero valido");
			}
			
		});
		_toolaBar.add(_runButton);
		
		//STOP BUTTON
		_stopButton = new JButton();
		_stopButton.setToolTipText("Stops the simulation");
		_stopButton.setIcon(new ImageIcon("resources/icons/stop.png"));
		_stopButton.addActionListener((e)->_stopped=true);
		_toolaBar.add(_stopButton);
		

		_toolaBar.addSeparator();
		
		
		//SPINNER STEPS
		_toolaBar.add(new JLabel("Steps:"));
		_steps_spinner = new JSpinner(spinnerModel);
		_steps_spinner.setToolTipText("Introduce the actual value of steps");
		_toolaBar.add(_steps_spinner);
		
		//TEXT FIELD DT
		_toolaBar.add(new JLabel("Delta-Time:"));
		_dt_textField = new JTextField(""+_default_dt);
		_dt_textField.setToolTipText("Introduce the desired dt");
		_toolaBar.add(_dt_textField);
		
		_toolaBar.addSeparator();
		
		//QUIT BUTTON
		_toolaBar.add(Box.createGlue()); // this aligns the button to the right
		_toolaBar.addSeparator();
		_quitButton = new JButton();
		_quitButton.setToolTipText("Quit");
		_quitButton.setIcon(new ImageIcon("resources/icons/exit.png"));
		_quitButton.addActionListener((e) -> ViewUtils.quit(this));
		_toolaBar.add(_quitButton);
		
		
		
		_changeRegionsDialog = new ChangeRegionsDialog(_ctrl);
	}
	
	
	private void run_sim(int n, double dt) {
		if (n > 0 && !_stopped) {
		try {
			long startTime = System.currentTimeMillis(); 
			_ctrl.advance(dt); 
			long stepTimeMs = System.currentTimeMillis() - startTime; 
			long delay = (long) (dt * 1000 - stepTimeMs); Thread.sleep(delay > 0 ? delay : 0);
			SwingUtilities.invokeLater(() -> run_sim(n - 1, dt));
		} catch (Exception e) {
			e.printStackTrace();
			ViewUtils.showErrorMsg(e.getMessage());
			activateButtons();
		}
		} 
		else {
			activateButtons();
		}
	}
	
	private static JSONObject load_JSON_file(InputStream in) {
		return new JSONObject(new JSONTokener(in));
	}
	
	private void activateButtons() { //activa todos los botones
		_folderButton.setEnabled(true);
		_quitButton.setEnabled(true);
		_viewButton.setEnabled(true);
		_runButton.setEnabled(true);
		_stopButton.setEnabled(true);
		_regionsDialog.setEnabled(true);
		_deadsButton.setEnabled(true);
		_stopped = true;
	}
}
