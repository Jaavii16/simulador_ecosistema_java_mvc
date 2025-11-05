package simulator.view;

import javax.swing.JFrame;

import simulator.control.Controller;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.*;

public class MainWindow extends JFrame {
	
	
	private static final long serialVersionUID = 8720525028556625831L;
	private Controller _ctrl;
	
	public MainWindow(Controller ctrl) {
		super("[ECOSYSTEM SIMULATOR]");
		_ctrl = ctrl;
		initGUI();
	}
	
	private void initGUI() {
		JPanel mainPanel = new JPanel(new BorderLayout());
		setContentPane(mainPanel);
		//crea ControlPanel y lo añade en PAGE_START de mainPanel
		JPanel controlPanel = new ControlPanel(_ctrl);
		mainPanel.add(controlPanel, BorderLayout.PAGE_START);
		//crea StatusBar y lo añade en PAGE_END de mainPanel
		JPanel statusBar = new StatusBar(_ctrl);
		mainPanel.add(statusBar, BorderLayout.PAGE_END);
		// Definición del panel de tablas (usa un BoxLayout vertical)
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		mainPanel.add(contentPanel, BorderLayout.CENTER);
		
		//crea la tabla de especies y la añade a contentPanel.
		JPanel especies = new InfoTable("Species", new SpeciesTableModel(_ctrl));
		especies.setPreferredSize(new Dimension(500, 250));
		contentPanel.add(especies);
		
		// crea la tabla de regiones.
		JPanel regiones = new InfoTable("Regions", new RegionsTableModel(_ctrl));
		regiones.setPreferredSize(new Dimension(500, 250));
		contentPanel.add(regiones);
		
		//crea la tabla de bebes
		JPanel bebes = new InfoTable("Bebés", new BabiesTableModel(_ctrl));
		bebes.setPreferredSize(new Dimension(500, 250));
		contentPanel.add(bebes);
		
		addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
			}

			@Override
			public void windowClosing(WindowEvent e) {
				ViewUtils.quit(MainWindow.this);
			}

			@Override
			public void windowClosed(WindowEvent e) {
			}

			@Override
			public void windowIconified(WindowEvent e) {
				
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
			}

			@Override
			public void windowActivated(WindowEvent e) {
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
			}
			
		});
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		//para que salga al centro de la pantalla
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

}
