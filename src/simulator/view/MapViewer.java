package simulator.view;

import simulator.model.Animal;
import simulator.model.AnimalInfo;
import simulator.model.MapInfo;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings("serial")
public class MapViewer extends AbstractMapViewer {

	// Anchura/altura/ de la simulación -- se supone que siempre van a ser iguales
	// al tamaño del componente
	private int _width;
	private int _height;

	// Número de filas/columnas de la simulación
	private int _rows;
	private int _cols;

	// Anchura/altura de una región
	int _rwidth;
	int _rheight;

	// Mostramos sólo animales con este estado. Los posibles valores de _currState
	// son null, y los valores de Animal.State.values(). Si es null mostramos todo.
	Animal.State _currState;

	// En estos atributos guardamos la lista de animales y el tiempo que hemos
	// recibido la última vez para dibujarlos.
	volatile private Collection<AnimalInfo> _objs;
	volatile private Double _time;

	// Una clase auxilar para almacenar información sobre una especie
	private static class SpeciesInfo {
		private Integer _count;
		private Color _color;

		SpeciesInfo(Color color) {
			_count = 0;
			_color = color;
		}
	}

	// Un mapa para la información sobre las especies
	Map<String, SpeciesInfo> _kindsInfo = new HashMap<>();

	// El font que usamos para dibujar texto
	private Font _font = new Font("Arial", Font.BOLD, 12);

	// Indica si mostramos el texto la ayuda o no
	private boolean _showHelp;

	public MapViewer() {
		initGUI();
	}

	private void initGUI() {

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyChar()) {
				case 'h':
					_showHelp = !_showHelp;
					repaint();
					break;
				case 's':
					changeState();
					repaint();
				default:
				}
			}

			private void changeState() {
				Animal.State[] states = Animal.State.values();
				//si el estado es nulo ponemos el primero y sino sumamos 1, pero si al sumar nos pasamos
				//del tamaño ponemos null
				int nextState;
				if(_currState == null)_currState = states[0];
				else {
					nextState = _currState.ordinal()+1;
					if(nextState >= states.length) _currState = null;
					else _currState = states[nextState];
				}
			}

		});

		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {
				requestFocus(); // Esto es necesario para capturar las teclas cuando el ratón está sobre este
								// componente.
			}
		});

		// Por defecto mostramos todos los animales
		_currState = null;

		// Por defecto mostramos el texto de ayuda
		_showHelp = true;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D gr = (Graphics2D) g;
		gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		gr.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		// Cambiar el font para dibujar texto
		g.setFont(_font);

		// Dibujar fondo blanco
		gr.setBackground(Color.WHITE);
		gr.clearRect(0, 0, _width, _height);

		// Dibujar los animales, el tiempo, etc.
		if (_objs != null)
			drawObjects(gr, _objs, _time);

		if(_showHelp) {
			String h = "h: toggle help";
			String s = "s: show animals of a specific state";
			gr.drawString(h, 10 , 40);
			gr.drawString(s, 10, 60);
		}
	}

	private boolean visible(AnimalInfo a) {
		if(_currState == null || a.get_state() == _currState)return true;
		else return false;
	}

	private void drawObjects(Graphics2D g, Collection<AnimalInfo> animals, Double time) {
		//lineas verticales
		int i=0;
		while(i<_width) {
			g.drawLine(i, 0, i, _height);
			i+=_rwidth;
		}
		//lineas horizontales
		int j=0;
		while(j<_height) {
			g.drawLine(0, j, _width, j);
			j+=_rheight;
		}
		
		// Dibujar los animales
		for (AnimalInfo a : animals) {

			// Si no es visible saltamos la iteración
			if (!visible(a))
				continue;

			// La información sobre la especie de 'a'
			SpeciesInfo esp_info = _kindsInfo.get(a.get_genetic_code());

			if(esp_info == null) {
				esp_info = new SpeciesInfo(ViewUtils.get_color(a.get_genetic_code()));
				_kindsInfo.put(a.get_genetic_code(), esp_info);
			}

			//Incrementa el contador de la especie (es decir el contador dentro de
			// tag_info)
			esp_info._count++;
			//Dibuja el animal con tamaño relativo a su edad
			int size = (int) (a.get_age()/2+2);
			int x = (int) a.get_position().getX();
			int y = (int) a.get_position().getY();
			g.setColor(esp_info._color);
			g.fillOval(x, y, size, size);
		}

		//Dibuja la etiqueta del estado visible cuando es solo 1
		if(_currState != null) {
			g.setColor(Color.CYAN);
			drawStringWithRect(g, 10, _height-10, _currState.name());
		}
		
		//Dibuja la etiqueta del tiempo
		g.setColor(Color.PINK);
		drawStringWithRect(g, 10, _height-30, "Time: " + String.format("%.3f", time));

		//Dibuja la información de todas la especies
		int pos = _height-50;
		for (Entry<String, SpeciesInfo> e : _kindsInfo.entrySet()) {
			g.setColor(e.getValue()._color);
			drawStringWithRect(g, 10, pos, e.getKey() + ": " + e.getValue()._count);
			pos -= 20;
			e.getValue()._count = 0;
		}
	}

	// Un método que dibujar un texto con un rectángulo
	void drawStringWithRect(Graphics2D g, int x, int y, String s) {
		Rectangle2D rect = g.getFontMetrics().getStringBounds(s, g);
		g.drawString(s, x, y);
		g.drawRect(x - 1, y - (int) rect.getHeight(), (int) rect.getWidth() + 1, (int) rect.getHeight() + 5);
	}

	@Override
	public void update(List<AnimalInfo> objs, Double time) {
		_objs = objs;
		_time = time;
		repaint();
	}

	@Override
	public void reset(double time, MapInfo map, List<AnimalInfo> animals) {
		_width = map.get_width();
		_height = map.get_height();
		_cols = map.get_cols();
		_rows = map.get_rows();
		_rheight = map.get_region_height();
		_rwidth = map.get_region_width();
		// Esto cambia el tamaño del componente, y así cambia el tamaño de la ventana
		// porque en MapWindow llamamos a pack() después de llamar a reset
		setPreferredSize(new Dimension(_width, _height));

		// Dibuja el estado
		update(animals, time);
	}

}
