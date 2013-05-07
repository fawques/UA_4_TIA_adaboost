package tia;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import sun.awt.SunToolkit.InfiniteLoop;

/**
 * @author Víctor Ejecuta el algoritmo adaboost dada una serie de puntos, para
 *         clasificarlos en dos grupos.
 */
public class Adaboost {
	private List<Punto> puntos;
	private List<Double> pesos;
	//private List<Linea> clasificadoresDebiles;
	private clasificadorFuerte clasFinal;
	private clasificadorFuerte clasIter;

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

		//clasificadoresDebiles = new ArrayList<Linea>();
		clasFinal = new clasificadorFuerte(); //TODO: crear cuando
		// esté seguro de que es así

		anchoCanvas = _ancho;
		altoCanvas = _alto;
		interfaz = _interfaz;
		Date now = new Date();
		rand = new Random(now.getTime());
		clasIter = new clasificadorFuerte();
		clasIter.setEpsilon(99999);
	}

	public void aplicarAdaboost() {
		System.out.println();
		System.out.println("=======================================");
		System.out.println("Empezando el algoritmo AdaBoost");
		System.out.println();
		// empieza el bucle principal -- t = 1,...,T
		int i;
		for (i = 0; i < maxIteraciones; i++) {
			System.out.println("== Iteración " + i + " ==");
			clasificadorDebil clasificador = obtenerClasificador();
			
			if(((Double)clasificador.getAlpha()).isInfinite())
			{
				System.out.println("Hemos llegado a un clasificador débil con alpha infinito, desechamos los demás y nos quedamos con este");
				clasFinal.clear();
			}
			
			clasFinal.add(clasificador);
			clasFinal.clasificar(puntos, pesos);
			
			System.out.println("El factor de confianza es alpha = " + clasificador.getAlpha());
			System.out.println("Y su epsilon = " + clasificador.getEpsilon());
			
			if(clasIter.getEpsilon() > clasFinal.getEpsilon()){
				clasIter = new clasificadorFuerte(clasFinal);
			}
			
			if(clasFinal.getEpsilon() == 0.0){
				System.out.println("Encontrado el clasificador final perfecto");
				break;
			}
			
			
			
			System.out.println("El epsilon del clasificador fuerte acumulado es " + clasFinal.getEpsilon());
			
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
		}
		interfaz.clasificadorFinal = clasIter;
		if (i == maxIteraciones){
			System.out.println("No se ha encontrado un clasificador perfecto, el mejor encontrado tiene epsilon= " + clasIter.getEpsilon());
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
			//interfaz.repintarCanvas();

			clasificador.clasificar(puntos, pesos);
			
			if(mejorClasificador == null || clasificador.getEpsilon() < mejorClasificador.getEpsilon())
				mejorClasificador = clasificador;
		}
		double alpha = 0.5 * Math.log((1-mejorClasificador.getEpsilon()) /mejorClasificador.getEpsilon());
		
		mejorClasificador.setAlpha(alpha);
		interfaz.listaDebiles.add(mejorClasificador.getLinea());
		//interfaz.repintarCanvas();
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
