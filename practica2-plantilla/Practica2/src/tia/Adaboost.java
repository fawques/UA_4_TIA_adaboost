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
	private List<Double> pesos;
	private List<Linea> clasificadoresDebiles;
	private List<Linea> clasificadorFuerte;

	private int maxIteraciones;
	private int maxLineas;

	private int anchoCanvas;
	private int altoCanvas;
	private Gui interfaz;
	
	private Random rand;

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
		Date now = new Date();
		rand = new Random(now.getTime());
	}

	public void aplicarAdaboost() { // TODO: void por ahora
		// empieza el bucle principal -- t = 1,...,T
		for (int i = 0; i < maxIteraciones; i++) {
			clasificadorDebil clasificador = obtenerClasificador();
			
			
			
			ArrayList<Double> pesosSinNormalizar = new ArrayList<Double>();
			double normalizacion = 0;
			
			for (int j = 0; j < puntos.size(); j++) {
				Punto punto = puntos.get(j);
				double nuevoPeso;
				if(clasificador.clasificar(punto)){
					 nuevoPeso = pesos.get(j) * Math.pow(Math.E, -1 * clasificador.getAlpha());
				}else{
					nuevoPeso = pesos.get(j) * Math.pow(Math.E, clasificador.getAlpha());
				}
				pesosSinNormalizar.add(nuevoPeso);
				normalizacion += nuevoPeso;
			}
			
			for (int j = 0; j < pesos.size(); j++) {
				pesos.set(j, pesosSinNormalizar.get(j)/normalizacion);
			}
			
			System.out.println("He terminado una iteración");
			System.out.println("El factor de confianza es alpha = " + clasificador.getAlpha());
			System.out.println("Y su epsilon = " + clasificador.getEpsilon());
			
			
		}
	}

	/**
	 * @param mejorClasificador
	 * @return
	 */
	private clasificadorDebil obtenerClasificador() {
		clasificadorDebil mejorClasificador = null;
		List<clasificadorDebil> posiblesClasificadores = generarLineas();
		for (clasificadorDebil clasificador : posiblesClasificadores) {
			interfaz.listaLineas.add(clasificador.getLinea());
			interfaz.refrescarCanvas();

			clasificador.clasificar(puntos, pesos);
			
			if(mejorClasificador == null || clasificador.getEpsilon() < mejorClasificador.getEpsilon())
				mejorClasificador = clasificador;
		}
		double alpha = 0.5 * Math.log((1-mejorClasificador.getEpsilon()) /mejorClasificador.getEpsilon());
		if(alpha > 1)
			alpha = 1;
			
		mejorClasificador.setAlpha(alpha);
		interfaz.listaClasif.add(mejorClasificador.getLinea());
		interfaz.refrescarCanvas();
		return mejorClasificador;
	}

	public List<clasificadorDebil> generarLineas() {
		ArrayList<clasificadorDebil> lineas = new ArrayList<>();
		
		
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
		double pesoInicial = 1.0 / N;
		for (int i = 0; i < N; i++) {
			pesos.add(pesoInicial);
		}
	}

}
