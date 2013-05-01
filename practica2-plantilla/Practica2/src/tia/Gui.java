package tia;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Point;
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
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Gui extends JFrame {

	private static final int MAXITERACIONES = 1;
	private static final int MAXLINEAS = 30;
	private final static int ANCHO = 400;
	private final static int ALTO = 400;

	private final static int RADIO_PUNTO = 2;

	private static final long serialVersionUID = 737365829727001543L;

	private List<Punto> listaPuntos;
	public List<Linea> listaLineas;
	public List<Linea> listaClasif;
	private Canvas areaPuntos;
	private Adaboost adaboost;

	private Gui interfaz;

	public Gui() {
		super("AdaBoost");
		interfaz = this;
		listaPuntos = new ArrayList<Punto>();
		listaLineas = new ArrayList<Linea>();
		listaClasif = new ArrayList<Linea>();
		adaboost = null;

		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
		Random rand = new Random();
		Date now = new Date();
		rand.setSeed(359534);

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
					listaLineas.clear();
					listaClasif.clear();
					adaboost = new Adaboost(interfaz, listaPuntos,MAXITERACIONES, MAXLINEAS, ANCHO, ALTO);
					adaboost.aplicarAdaboost(); // TODO: hacer algo aquí.
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
				listaClasif.clear();
				areaPuntos.repaint();
			}
		});
		areaBotones.add(botonLimpiar);

		this.setMinimumSize(new Dimension(ANCHO, ALTO));
	}

	public void refrescarCanvas() {
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

			g.setColor(Color.GREEN);
			for (Linea linea : listaLineas) {
				g.drawLine((int) linea.getOrigen().getX(), (int) linea
						.getOrigen().getY(), (int) linea.getDestino().getX(),
						(int) linea.getDestino().getY());
			}
			
			g.setColor(Color.BLACK);
			for (Linea linea : listaClasif) {
				g.drawLine((int) linea.getOrigen().getX(), (int) linea
						.getOrigen().getY(), (int) linea.getDestino().getX(),
						(int) linea.getDestino().getY());
			}

		}

	}
}
