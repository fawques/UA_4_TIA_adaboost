package tia;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

	private static int MAXITERACIONES = 100;
	private static int MAXLINEAS = 30;
	private static int ANCHO = 400;
	private static int ALTO = 400;

	private final static int RADIO_PUNTO = 2;

	private static final long serialVersionUID = 737365829727001543L;

	private List<Punto> listaPuntos;
	public List<Linea> listaLineas;
	public List<Linea> listaDebiles;
	// public List<clasificadorDebil> listaFinal;
	public clasificadorFuerte clasificadorFinal;
	private Canvas areaPuntos;
	private Adaboost adaboost;

	private Checkbox cb_intermedias;
	private Checkbox cb_debiles;
	private Checkbox cb_fuerte;
	private Checkbox cb_fondo;
	private JButton bt_repintar;
	private TextField tx_lineas;
	private TextField tx_iter;

	private Gui interfaz;

	public Gui() {
		super("AdaBoost");
		interfaz = this;
		listaPuntos = new ArrayList<Punto>();
		listaLineas = new ArrayList<Linea>();
		listaDebiles = new ArrayList<Linea>();
		// listaFinal = new ArrayList<clasificadorDebil>();
		clasificadorFinal = new clasificadorFuerte();
		adaboost = null;

		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Area de inputs
		JPanel barraSuperior = new JPanel();
		barraSuperior.setLayout(new FlowLayout());
		tx_iter = new TextField("100",4);
		barraSuperior.add(new Label("Iteraciones"));
		barraSuperior.add(tx_iter);
		tx_lineas = new TextField("1000",4);
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
					try{
						MAXITERACIONES = Integer.parseInt(tx_iter.getText());
					}catch(NumberFormatException exc){
						MAXITERACIONES = 100;
					}
					try {
						MAXLINEAS = Integer.parseInt(tx_lineas.getText());
					} catch (NumberFormatException e1) {
						MAXLINEAS = 1000;
					}
					listaLineas.clear();
					listaDebiles.clear();
					clasificadorFinal.clear();
					adaboost = new Adaboost(interfaz, listaPuntos,
							MAXITERACIONES, MAXLINEAS, ANCHO, ALTO);
					adaboost.aplicarAdaboost(); // TODO: hacer algo aquí.
					repintarCanvas();
					adaboost = null;
				}
			}
		});
		areaBotones.add(botonComenzar);

		// Boton para borrar todos los puntos
		JButton botonLimpiar = new JButton("Limpiar");
		botonLimpiar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				listaPuntos.clear();
				listaLineas.clear();
				listaDebiles.clear();
				clasificadorFinal.clear();
				areaPuntos.repaint();
			}
		});
		areaBotones.add(botonLimpiar);
		this.setMinimumSize(new Dimension(400, 400));
		ANCHO = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getMaximumWindowBounds().width;
		ALTO = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getMaximumWindowBounds().height;
		this.setSize(ANCHO, ALTO);
		this.setExtendedState(MAXIMIZED_BOTH);
	}

	public void repintarCanvas() {
		areaPuntos.repaint();
	}

	public static void main(String[] args) {
		Gui gui = new Gui();
		gui.setVisible(true);
	}

	class Canvas extends JPanel {
		private static final long serialVersionUID = -4449288527123357984L;

		@Override
		public void paint(Graphics g) {
			super.paint(g);
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, this.getWidth(), this.getHeight());

			if (cb_fondo.getState()) {
				Color fondoRojo = new Color(255, 100, 100, 200);
				Color fondoAzul = new Color(100, 100, 255, 200);
				for (int i = 0; i < this.getWidth(); i++) {
					for (int j = 0; j < this.getHeight(); j++) {
						double clase = clasificadorFinal.clasificar(new Punto(
								i, j, 0));
						if (clase > 0) {
							g.setColor(fondoAzul);
						} else {

							g.setColor(fondoRojo);
						}

						g.drawLine(i, j, i, j);

					}
				}
			}

			if (cb_intermedias.getState()) {
				g.setColor(Color.GREEN);
				for (Linea linea : listaLineas) {
					g.drawLine((int) linea.getOrigen().getX(), (int) linea
							.getOrigen().getY(), (int) linea.getDestino()
							.getX(), (int) linea.getDestino().getY());
				}
			}
			// Dibuja los puntos
			for (Punto p : listaPuntos) {
				if (p.getTipo() > 0) {
					g.setColor(Color.BLUE);
				} else {
					g.setColor(Color.RED);
				}
				g.drawOval((int) p.getX() - RADIO_PUNTO, (int) p.getY()
						- RADIO_PUNTO, RADIO_PUNTO * 2 + 1, RADIO_PUNTO * 2 + 1);
			}

			if (cb_debiles.getState()) {
				for (Linea linea : listaDebiles) {
					Linea lineaAux = new Linea(linea.getRho() - 1,
							linea.getThetha(), ANCHO, ALTO);
					Linea lineaExtra = new Linea(linea.getRho() + 1,
							linea.getThetha(), ANCHO, ALTO);

					g.setColor(Color.RED);
					g.drawLine((int) lineaAux.getOrigen().getX(),
							(int) lineaAux.getOrigen().getY(), (int) lineaAux
									.getDestino().getX(), (int) lineaAux
									.getDestino().getY());
					g.setColor(Color.BLUE);
					g.drawLine((int) lineaExtra.getOrigen().getX(),
							(int) lineaExtra.getOrigen().getY(),
							(int) lineaExtra.getDestino().getX(),
							(int) lineaExtra.getDestino().getY());
				}
			}

			if (cb_fuerte.getState()) {
				for (clasificadorDebil clasDebil : clasificadorFinal
						.getClasificadores()) {
					Linea linea = clasDebil.getLinea();
					Linea lineaExtra = new Linea(linea.getRho() + 2,
							linea.getThetha(), ANCHO, ALTO);
					Linea lineaExtra2 = new Linea(linea.getRho() - 2,
							linea.getThetha(), ANCHO, ALTO);

					g.setColor(Color.BLACK);
					g.drawLine((int) linea.getOrigen().getX(), (int) linea
							.getOrigen().getY(), (int) linea.getDestino()
							.getX(), (int) linea.getDestino().getY());
					g.setColor(Color.RED);
					g.drawLine((int) lineaExtra2.getOrigen().getX(),
							(int) lineaExtra2.getOrigen().getY(),
							(int) lineaExtra2.getDestino().getX(),
							(int) lineaExtra2.getDestino().getY());
					g.setColor(Color.BLUE);
					g.drawLine((int) lineaExtra.getOrigen().getX(),
							(int) lineaExtra.getOrigen().getY(),
							(int) lineaExtra.getDestino().getX(),
							(int) lineaExtra.getDestino().getY());
				}
			}

		}

	}
}
