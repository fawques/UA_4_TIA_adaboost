package tia;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Graphics;
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
	
	private final static int ANCHO = 400;
	private final static int ALTO = 400;
	
	private final static int RADIO_PUNTO = 2;
	
	private static final long serialVersionUID = 737365829727001543L;

	private List<Punto> listaPuntos;
	private Canvas areaPuntos;
	
	public Gui() {
		super("AdaBoost");
		
		listaPuntos = new ArrayList<Punto>();		
		
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Area de dibujo
		areaPuntos = new Canvas();
		areaPuntos.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int tipo = e.getButton()==MouseEvent.BUTTON1?1:-1;
				listaPuntos.add(new Punto(e.getX(),e.getY(),tipo));
				areaPuntos.repaint();			
			}
			
			
		});
		this.add(areaPuntos, BorderLayout.CENTER);

		// Area de botones
		JPanel areaBotones = new JPanel();
		areaBotones.setLayout(new FlowLayout());
		this.add(areaBotones, BorderLayout.SOUTH);

		//guardamos this para usarlo en el FileChooser
		final JFrame framethis=this;
		
		// Boton para cargar un fichero con todos los puntos
		areaBotones.add(new JButton(new AbstractAction("Cargar") {

			@Override
            public void actionPerformed(ActionEvent e) {
                FileDialog fd = new FileDialog(framethis, "Load", FileDialog.LOAD);
                fd.setVisible(true);
                String filename=fd.getFile();
                listaPuntos.clear();
				
				try {
					BufferedReader br = new BufferedReader(new FileReader(filename));
					try {
					    StringBuilder sb = new StringBuilder();
					    String line = br.readLine();
					    
					    while (line != null) {

					        String[] flostr = line.split(" ");
					        
					    	//int tipo = e.getButton()==MouseEvent.BUTTON1?1:-1;
							listaPuntos.add(new Punto(Float.parseFloat(flostr[0]),Float.parseFloat(flostr[1]),Float.parseFloat(flostr[2])));
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
                FileDialog fd = new FileDialog(framethis, "Save", FileDialog.SAVE);
                fd.setVisible(true);
                String filename=fd.getFile();
				try {
					PrintWriter out = new PrintWriter(filename);
					String text;
					for (Punto p : listaPuntos) {
						text=p.getX()+" "+p.getY()+" "+p.getTipo();
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
				// TODO Ejecutar AdaBoost y mostrar resultados obtenidos
			}
		});
		areaBotones.add(botonComenzar);

		// Boton para borrar todos los puntos
		JButton botonLimpiar = new JButton("Limpiar");
		botonLimpiar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				listaPuntos.clear();
				areaPuntos.repaint();
			}
		});
		areaBotones.add(botonLimpiar);

		this.setMinimumSize(new Dimension(ANCHO,ALTO));
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
			for(Punto p: listaPuntos) {
				if(p.getTipo()>0) {
					g.setColor(Color.BLUE);
				} else {
					g.setColor(Color.RED);
				}				
				g.drawOval((int)p.getX()-RADIO_PUNTO, (int)p.getY()-RADIO_PUNTO, RADIO_PUNTO*2+1, RADIO_PUNTO*2+1);				
			}
		}
		
	}
}
