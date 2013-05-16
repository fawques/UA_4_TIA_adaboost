package tia;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import sun.awt.SunToolkit.InfiniteLoop;

/**
 * Ejecuta el algoritmo adaboost dada una serie de puntos, para
 *         clasificarlos en dos grupos.
 */
public class Adaboost {
	/**
	 * Lista de puntos a clasificar
	 */
	private List<Punto> puntos;
	/**
	 * Lista de pesos de los puntos
	 */
	private List<Double> pesos;
	/**
	 * Lista de pesos antes de normalizar
	 */
	private List<Double> pesosSinNormalizar;
	/**
	 * Clasificador acumulado en cada iteración
	 */
	private clasificadorFuerte clasIteracion;
	/**
	 * Clasificador resultado, se va actualizando con cada iteración con éxito si mejora la anterior
	 */
	private clasificadorFuerte clasFinal;

	private int maxIteraciones;
	private int maxLineas;

	private int anchoCanvas;
	private int altoCanvas;
	/**
	 * referencia a la interfaz, para actualizar las líneas en pasos intermedios del algoritmo
	 */
	private Gui interfaz;
	
	private Random rand;

	/** Constructor. Inicializa los datos del algoritmo
	 * Inicializa los pesos de los puntos, inicializa el generador aleatorio.
	 * @param _interfaz referencia a la interfaz
	 * @param _puntos lista de puntos sobre la que calcular
	 * @param _maxIter número máximo de iteraciones a ejecutar
	 * @param _maxLineas número máximo de líneas a generar para cada clasificador débil
	 * @param _ancho ancho de la zona de dibujo
	 * @param _alto alto de la zona de dibujo
	 */
	public Adaboost(Gui _interfaz, List<Punto> _puntos, int _maxIter,
			int _maxLineas, int _ancho, int _alto) {
		;
		puntos = _puntos;
		inicializarPesos();
		maxIteraciones = _maxIter;
		maxLineas = _maxLineas;
		clasIteracion = new clasificadorFuerte();
		anchoCanvas = _ancho;
		altoCanvas = _alto;
		interfaz = _interfaz;
		Date now = new Date();
		rand = new Random(now.getTime());
		clasFinal = new clasificadorFuerte();
		clasFinal.setEpsilon(99999);
	}

	/**
	 * Aplica el algoritmo Adaboost sobre los parámetros definidos
	 */
	public void aplicarAdaboost() {
		System.out.println();
		System.out.println("=======================================");
		System.out.println("Empezando el algoritmo AdaBoost");
		System.out.println();
		// empieza el bucle principal -- t = 0,...,maxIteraciones-1
		int i;
		for (i = 0; i < maxIteraciones; i++) {
			System.out.println("== Iteración " + i + " ==");
			// obtenemos un nuevo clasificador débil
			clasificadorDebil clasificador = obtenerClasificador();
			
			if(((Double)clasificador.getAlpha()).isInfinite())
			{
				System.out.println("Hemos llegado a un clasificador débil con alpha infinito, desechamos los demás y nos quedamos con este");
				clasIteracion.clear();
			}
			
			clasIteracion.add(clasificador);
			clasIteracion.clasificar(puntos, pesos);
			
			System.out.println("El factor de confianza es alpha = " + clasificador.getAlpha());
			System.out.println("Y su epsilon = " + clasificador.getEpsilon());
			
			// actualizamos el mejor clasificador conseguido 
			if(clasFinal.getEpsilon() > clasIteracion.getEpsilon()){
				clasFinal = new clasificadorFuerte(clasIteracion);
			}
			
			if(clasIteracion.getEpsilon() == 0.0){
				System.out.println("Encontrado el clasificador final perfecto");
				break;
			}
			
			
			
			System.out.println("El epsilon del clasificador fuerte acumulado es " + clasIteracion.getEpsilon());
			
			actualizarPesos(clasificador);
		}
		// devolvemos el clasificador fuerte conseguido
		interfaz.clasificadorFinal = clasFinal;
		if (i == maxIteraciones){
			System.out.println("No se ha encontrado un clasificador perfecto, el mejor encontrado tiene epsilon= " + clasFinal.getEpsilon());
		}
		System.out.println("Número de clasificadores = " + clasFinal.getClasificadores().size());
		
	}

	/** Actualizar pesos de los puntos según el clasificador débil
	 * @param clasificador
	 */
	public void actualizarPesos(clasificadorDebil clasificador) {
		pesosSinNormalizar = new ArrayList<Double>();
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

	/** calcula un nuevo clasificador débil, generando líneas aleatorias y comprobando el nivel de error de cada una
	 * @return clasificador con menor error
	 */
	private clasificadorDebil obtenerClasificador() {
		clasificadorDebil mejorClasificador = null;
		List<clasificadorDebil> posiblesClasificadores = generarLineas();
		
		for (clasificadorDebil clasificador : posiblesClasificadores) {
			// actualizamos la lista de líneas intermedias de la interfaz
			interfaz.listaLineas.add(clasificador.getLinea());
			clasificador.clasificar(puntos, pesos);
			if(mejorClasificador == null || clasificador.getEpsilon() < mejorClasificador.getEpsilon())
				mejorClasificador = clasificador;
		}
		double alpha = 0.5 * Math.log((1-mejorClasificador.getEpsilon()) /mejorClasificador.getEpsilon());
		
		mejorClasificador.setAlpha(alpha);
		// añadimos el clasificador a la lista de clasificadores débiles
		interfaz.listaDebiles.add(mejorClasificador.getLinea());
		return mejorClasificador;
	}

	/** Generamos maxLineas líneas aleatorias
	 * @return lista con las líneas generadas
	 */
	public List<clasificadorDebil> generarLineas() {
		ArrayList<clasificadorDebil> lineas = new ArrayList<>();
		
		
		for (int i = 0; i < maxLineas; i++) {
			// el punto debe estar entre 0 y el tamaño del canvas
			Point punto = new Point(rand.nextInt(anchoCanvas),
					rand.nextInt(altoCanvas));
			// thetha debe estar entre 0 y 2*Pi
			double thetha = rand.nextDouble() * 2 * Math.PI;
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
