package tia;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Gui extends JFrame {

	/**
	 * Número máximo de iteraciones para el algoritmo
	 */
	private static int MAXITERACIONES = 100;
	/**
	 * Número máximo de líneas a generar
	 */
	private static int MAXLINEAS = 30;
	/**
	 * ancho del área de dibujo. Después se modificará al máximo de la pantalla del usuario
	 */
	private static int ANCHO = 400;
	/**
	 * alto del área de dibujo. Después se modificará al máximo de la pantalla del usuario
	 */
	private static int ALTO = 400;

	/**
	 * radio del punto a dibujar
	 */
	private final static int RADIO_PUNTO = 2;

	private static final long serialVersionUID = 737365829727001543L;

	/**
	 * lista de puntos sobre la que aplicar el algoritmo
	 */
	private List<Punto> listaPuntos;
	/**
	 * lista de líneas intermedias (líneas generadas para cada clasificador débil)
	 */
	public List<Linea> listaLineas;
	/**
	 * lista de líneas correspondientes a los clasificadores débiles encontrados
	 */
	public List<Linea> listaDebiles;
	/**
	 * clasificador fuerte encontrado
	 */
	public clasificadorFuerte clasificadorFinal;
	/**
	 * zona de pintado
	 */
	private Canvas areaPuntos;
	/**
	 * 
	 */
	private Adaboost adaboost;

	/**
	 * Elementos de interfaz 
	 */
	private Checkbox cb_intermedias;
	private Checkbox cb_debiles;
	private Checkbox cb_fuerte;
	private Checkbox cb_fondo;
	private JButton bt_repintar;
	private TextField tx_lineas;
	private TextField tx_iter;
	private Dialog d_mensaje;

	
	/**
	 * Imagen que almacena el último fondo pintado, para no tener que repintar
	 */
	private BufferedImage fondoCanvas;

	/**
	 * Auxiliar. Almacenará el this al crear la interfaz
	 */
	private Gui interfaz;

	/**
	 * Constructor de Gui. Inicializa los valores y asigna el tamaño
	 */
	public Gui() {
		super("AdaBoost");
		interfaz = this;
		listaPuntos = new ArrayList<Punto>();
		listaLineas = new ArrayList<Linea>();
		listaDebiles = new ArrayList<Linea>();
		clasificadorFinal = new clasificadorFuerte();
		adaboost = null;

		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Barra superior
		JPanel barraSuperior = new JPanel();
		barraSuperior.setLayout(new FlowLayout());
		tx_iter = new TextField("100", 4);
		barraSuperior.add(new Label("Iteraciones"));
		barraSuperior.add(tx_iter);
		tx_lineas = new TextField("1000", 4);
		barraSuperior.add(new Label("Líneas"));
		barraSuperior.add(tx_lineas);
		cb_intermedias = new Checkbox("Líneas intermedias");
		barraSuperior.add(cb_intermedias);
		cb_debiles = new Checkbox("Mostrar clasificadores débiles");
		barraSuperior.add(cb_debiles);
		cb_fuerte = new Checkbox("Mostrar clasificador fuerte");
		cb_fuerte.setState(true);
		barraSuperior.add(cb_fuerte);
		cb_fondo = new Checkbox("Pintar fondo");
		barraSuperior.add(cb_fondo);
		bt_repintar = new JButton(new AbstractAction("Repintar") {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				repintarCanvas();

			}
		});
		barraSuperior.add(bt_repintar);
		this.add(barraSuperior, BorderLayout.NORTH);
		
		// Area de dibujo
		areaPuntos = new Canvas();
		areaPuntos.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int tipo = e.getButton() == MouseEvent.BUTTON1 ? 1 : -1;
				listaPuntos.add(new Punto(e.getX(), e.getY(), tipo));
				areaPuntos.repaint();
			}

		});
		this.add(areaPuntos, BorderLayout.CENTER);
		fondoCanvas = null;

		// Area de botones
		JPanel areaBotones = new JPanel();
		areaBotones.setLayout(new FlowLayout());
		this.add(areaBotones, BorderLayout.SOUTH);

		// guardamos this para usarlo en el FileChooser
		final JFrame framethis = this;

		// Boton para cargar un fichero con todos los puntos
		areaBotones.add(new JButton(new AbstractAction("Cargar") {

			@Override
			public void actionPerformed(ActionEvent e) {
				FileDialog fd = new FileDialog(framethis, "Load",
						FileDialog.LOAD);
				fd.setVisible(true);
				String filename = fd.getDirectory() + fd.getFile();
				listaPuntos.clear();
				listaLineas.clear();
				listaDebiles.clear();
				clasificadorFinal.clear();

				try {
					BufferedReader br = new BufferedReader(new FileReader(
							filename));
					try {
						StringBuilder sb = new StringBuilder();
						String line = br.readLine();

						while (line != null) {

							String[] flostr = line.split(" ");

							// int tipo =
							// e.getButton()==MouseEvent.BUTTON1?1:-1;
							listaPuntos.add(new Punto(Float
									.parseFloat(flostr[0]), Float
									.parseFloat(flostr[1]), Float
									.parseFloat(flostr[2])));
							line = br.readLine();
						}

					} finally {
						br.close();
					}
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (Exception e1) {
					// TODO: handle exception
					e1.printStackTrace();
				}

				areaPuntos.repaint();
			}
		}));

		// Boton para guardar un fichero con todos los puntos
		areaBotones.add(new JButton(new AbstractAction("Guardar") {

			@Override
			public void actionPerformed(ActionEvent e) {
				FileDialog fd = new FileDialog(framethis, "Save",
						FileDialog.SAVE);
				fd.setVisible(true);
				String filename = fd.getFile();
				try {
					PrintWriter out = new PrintWriter(filename);
					String text;
					for (Punto p : listaPuntos) {
						text = p.getX() + " " + p.getY() + " " + p.getTipo();
						out.println(text);
					}
					out.close();

				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				areaPuntos.repaint();
			}
		}));

		// Boton para ejecutar el algoritmo
		JButton botonComenzar = new JButton("Comenzar");
		botonComenzar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (listaPuntos.size() > 0 && adaboost == null) {
					// comprobamos las entradas
					try {
						MAXITERACIONES = Integer.parseInt(tx_iter.getText());
					} catch (NumberFormatException exc) {
						MAXITERACIONES = 100;
						tx_iter.setText(""+MAXITERACIONES);
					}
					try {
						MAXLINEAS = Integer.parseInt(tx_lineas.getText());
					} catch (NumberFormatException e1) {
						MAXLINEAS = 1000;
						tx_lineas.setText(""+MAXLINEAS);
					}
					// limpiamos la ejecución anterior
					listaLineas.clear();
					listaDebiles.clear();
					clasificadorFinal.clear();
					
					adaboost = new Adaboost(interfaz, listaPuntos,
							MAXITERACIONES, MAXLINEAS, ANCHO, ALTO);
					adaboost.aplicarAdaboost();
					fondoCanvas = null;
					repintarCanvas();
					adaboost = null;

				}
			}
		});
		areaBotones.add(botonComenzar);

		// Boton para borrar todos los puntos y la ejecución anterior
		JButton botonLimpiar = new JButton("Limpiar");
		botonLimpiar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cb_fondo.setState(false);
				listaPuntos.clear();
				listaLineas.clear();
				listaDebiles.clear();
				clasificadorFinal.clear();
				fondoCanvas = null;
				areaPuntos.repaint();
			}
		});
		areaBotones.add(botonLimpiar);
		
		// Inicializamos el tamaño al máximo visible en la pantalla
		this.setMinimumSize(new Dimension(400, 400));
		ANCHO = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getMaximumWindowBounds().width;
		ALTO = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getMaximumWindowBounds().height;
		this.setSize(ANCHO, ALTO);
		this.setExtendedState(MAXIMIZED_BOTH);
		
		// creamos el mensaje de dibujando el fondo
		d_mensaje = new Dialog(this);
		d_mensaje.setLayout(new BorderLayout());
		d_mensaje.setTitle("Dibujando");
		d_mensaje.add(new Label("Dibujando el fondo, un momento",Label.CENTER));
		d_mensaje.getComponent(0).setBounds(d_mensaje.getWidth()/3, d_mensaje.getHeight()/3, d_mensaje.getWidth()/3, d_mensaje.getHeight()/3);
		d_mensaje.setResizable(false);
		d_mensaje.setSize(ANCHO/3, ALTO/3);
		d_mensaje.setLocation(ANCHO/3, ALTO/3);
		
	}

	/**
	 * refrescar el canvas
	 */
	public void repintarCanvas() {
		areaPuntos.repaint();
	}

	/**Main
	 * @param args
	 */
	public static void main(String[] args) {
		Gui gui = new Gui();
		gui.setVisible(true);
	}

	/**
	 * Zona de dibujo
	 */
	class Canvas extends JPanel {
		private static final long serialVersionUID = -4449288527123357984L;

		@Override
		public void paint(Graphics g) {
			super.paint(g);
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, this.getWidth(), this.getHeight());

			// pintar fondo seleccionado
			if (cb_fondo.getState()) {

				// si ha habido cambios en la clasificación
				if (fondoCanvas == null) {
					d_mensaje.setVisible(true);
					// pintamos sobre la imagen de buffer
					fondoCanvas = new BufferedImage(this.getWidth(),
							this.getHeight(), BufferedImage.TYPE_INT_ARGB);
					Graphics2D g2 = fondoCanvas.createGraphics();
					Color fondoRojo = new Color(255, 100, 100, 200);
					Color fondoAzul = new Color(100, 100, 255, 200);
					for (int i = 0; i < this.getWidth(); i++) {
						for (int j = 0; j < this.getHeight(); j++) {
							double clase = clasificadorFinal
									.clasificar(new Punto(i, j, 0));
							if (clase > 0) {
								g2.setColor(fondoAzul);
							} else {

								g2.setColor(fondoRojo);
							}
							// esto es muy sucio >_<
							g2.drawLine(i, j, i, j);
						}
					}
					// volcamos la imagen en el canvas
					g.drawImage(fondoCanvas, 0, 0, this.getWidth(),
							this.getHeight(), null);
				} else {
					// volcamos la imagen de la última ejecución en el canvas
					g.drawImage(fondoCanvas, 0, 0, this.getWidth(),
							this.getHeight(), null);
				}
				d_mensaje.setVisible(false);
			}

			// mostrar líneas intermedias
			if (cb_intermedias.getState()) {
				g.setColor(Color.GREEN);
				for (Linea linea : listaLineas) {
					g.drawLine((int) linea.getOrigen().getX(), (int) linea
							.getOrigen().getY(), (int) linea.getDestino()
							.getX(), (int) linea.getDestino().getY());
				}
			}
			
			// Dibujamos los puntos
			for (Punto p : listaPuntos) {
				if (p.getTipo() > 0) {
					g.setColor(Color.BLUE);
				} else {
					g.setColor(Color.RED);
				}
				g.drawOval((int) p.getX() - RADIO_PUNTO, (int) p.getY()
						- RADIO_PUNTO, RADIO_PUNTO * 2 + 1, RADIO_PUNTO * 2 + 1);
			}

			// mostrar clasificadores débiles
			if (cb_debiles.getState()) {
				for (Linea linea : listaDebiles) {
					Linea lineaRoja = new Linea(linea.getRho() - 1,
							linea.getThetha(), ANCHO, ALTO);
					Linea lineaAzul = new Linea(linea.getRho() + 1,
							linea.getThetha(), ANCHO, ALTO);

					g.setColor(Color.RED);
					g.drawLine((int) lineaRoja.getOrigen().getX(),
							(int) lineaRoja.getOrigen().getY(), (int) lineaRoja
									.getDestino().getX(), (int) lineaRoja
									.getDestino().getY());
					g.setColor(Color.BLUE);
					g.drawLine((int) lineaAzul.getOrigen().getX(),
							(int) lineaAzul.getOrigen().getY(), (int) lineaAzul
									.getDestino().getX(), (int) lineaAzul
									.getDestino().getY());
				}
			}

			// mostrar clasificador fuerte
			if (cb_fuerte.getState()) {
				for (clasificadorDebil clasDebil : clasificadorFinal
						.getClasificadores()) {
					Linea linea = clasDebil.getLinea();
					Linea lineaAzul = new Linea(linea.getRho() + 2,
							linea.getThetha(), ANCHO, ALTO);
					Linea lineaRoja = new Linea(linea.getRho() - 2,
							linea.getThetha(), ANCHO, ALTO);

					// línea central
					g.setColor(Color.BLACK);
					g.drawLine((int) linea.getOrigen().getX(), (int) linea
							.getOrigen().getY(), (int) linea.getDestino()
							.getX(), (int) linea.getDestino().getY());
					// línea del lado rojo
					g.setColor(Color.RED);
					g.drawLine((int) lineaRoja.getOrigen().getX(),
							(int) lineaRoja.getOrigen().getY(), (int) lineaRoja
									.getDestino().getX(), (int) lineaRoja
									.getDestino().getY());
					// línea del lado azul
					g.setColor(Color.BLUE);
					g.drawLine((int) lineaAzul.getOrigen().getX(),
							(int) lineaAzul.getOrigen().getY(), (int) lineaAzul
									.getDestino().getX(), (int) lineaAzul
									.getDestino().getY());
				}
			}

		}

	}
}
