package tia;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * @author Víctor Ejecuta el algoritmo adaboost dada una serie de puntos, para
 *         clasificarlos en dos grupos.
 */
public class Adaboost {
	private List<Punto> puntos;
	private List<Integer> pesos;
	private List<Linea> clasificadoresDebiles;
	private List<Linea> clasificadorFuerte;

	private int maxIteraciones;
	private int maxLineas;

	private int anchoCanvas;
	private int altoCanvas;
	private Gui interfaz;

	public Adaboost(Gui _interfaz, List<Punto> _puntos, int _maxIter,
			int _maxLineas, int _ancho, int _alto) {
		;
		puntos = _puntos;
		inicializarPesos();
		maxIteraciones = _maxIter;
		maxLineas = _maxLineas;

		clasificadoresDebiles = new ArrayList<Linea>();
		// clasificadorFuerte = new ArrayList<Linea>(); //TODO: crear cuando
		// esté seguro de que es así

		anchoCanvas = _ancho;
		altoCanvas = _alto;
		interfaz = _interfaz;
	}

	public void aplicarAdaboost() { // TODO: void por ahora
		// empieza el bucle principal -- t = 1,...,T
		for (int i = 0; i < maxIteraciones; i++) {
			clasificadorDebil mejorClasificador = null;
			List<clasificadorDebil> posiblesClasificadores = generarLineas();
			for (clasificadorDebil clasificador : posiblesClasificadores) {
				interfaz.listaLineas.add(clasificador.getLinea());
				interfaz.refrescarCanvas();

				double thetha = clasificador.getLinea().getThetha();
				double rho = clasificador.getLinea().getRho();

				double epsilon = 0;

				for (int j = 0; j < puntos.size(); j++) {
					Punto punto = puntos.get(j);
					double dist = punto.getX() * Math.cos(thetha)
							+ punto.getY() * Math.sin(thetha);
					if (Math.signum(dist - rho) != punto.getTipo()) {
						epsilon += pesos.get(j);
					}
				}
				clasificador.setEpsilon(epsilon);
				if(mejorClasificador == null || epsilon < mejorClasificador.getEpsilon())
					mejorClasificador = clasificador;
			}
			interfaz.listaClasif.add(mejorClasificador.getLinea());
			interfaz.refrescarCanvas();
			
		}
	}

	public List<clasificadorDebil> generarLineas() {
		ArrayList<clasificadorDebil> lineas = new ArrayList<>();
		Date now = new Date();
		Random rand = new Random(now.getTime());
		for (int i = 0; i < maxLineas; i++) {
			Point punto = new Point(rand.nextInt(anchoCanvas),
					rand.nextInt(altoCanvas));
			double thetha = rand.nextDouble() * 2 * Math.PI;
			// rho = x*cos(th) + y*sin(th)
			double rho = punto.getX() * Math.cos(thetha) + punto.getY()
					* Math.sin(thetha);
			lineas.add(new clasificadorDebil(new Linea(rho, thetha,
					anchoCanvas, altoCanvas)));
		}
		return lineas;
	}

	/**
	 * Inicializa los pesos de todos los puntos a 1/N
	 */
	private void inicializarPesos() {
		int N = puntos.size();
		pesos = new ArrayList<>(N);
		int pesoInicial = 1 / N;
		for (int i = 0; i < N; i++) {
			pesos.add(pesoInicial);
		}
	}

}
