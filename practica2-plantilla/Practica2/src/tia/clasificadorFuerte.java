package tia;

import java.util.ArrayList;
import java.util.List;

/**
 * Clasificador fuerte. Compuesto de clasificadores débiles.
 */
public class clasificadorFuerte {
	/**
	 * Lista de clasificadores débiles
	 */
	List<clasificadorDebil> clasificadores;
	/**
	 * Cantidad de error del clasificador
	 */
	private double epsilon;
	
	/**
	 * Nivel de confianza en el clasificador
	 */
	private double alpha;

	public clasificadorFuerte(List<clasificadorDebil> _clas) {
		clasificadores = _clas;
		setEpsilon(0);
		setAlpha(0);
	}
	public clasificadorFuerte() {
		clasificadores = new ArrayList<clasificadorDebil>();
		setEpsilon(0);
		setAlpha(0);
	}
	
	public clasificadorFuerte(clasificadorFuerte _clas) {
		clasificadores = new ArrayList<clasificadorDebil>(_clas.clasificadores);
		setEpsilon(_clas.getEpsilon());
		setAlpha(_clas.getAlpha());
	}

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public double getEpsilon() {
		return epsilon;
	}

	public void setEpsilon(double epsilon) {
		this.epsilon = epsilon;
	}
	public List<clasificadorDebil> getClasificadores() {
		return clasificadores;
	}
	
	public boolean add(clasificadorDebil clas) {
		return clasificadores.add(clas);
	}
	
	public void clear(){
		clasificadores.clear();
	}
	

	/**
	 * Clasifica una lista de puntos. Cuando termina el epsilon del clasificador indica la cantidad de error 
	 * @param listaPuntos lista de puntos a clasificar con el clasificador fuerte
	 * @param listaPesos lista de pesos de los puntos de listaPuntos
	 */
	public void clasificar(List<Punto> listaPuntos, List<Double> listaPesos) {
		double epsilon = 0;
		for (int i = 0; i < listaPuntos.size(); i++) {
			Punto punto = listaPuntos.get(i);

			double valorFinal = clasificar(punto);
			if (valorFinal != punto.getTipo()) {
				epsilon += listaPesos.get(i);
			}
		}
		setEpsilon(epsilon);

	}

	/**
	 * Clasifica un punto determinado.
	 * @param punto punto a clasificar
	 * @return signo de clasificar un punto con el clasificador fuerte
	 */
	public double clasificar(Punto punto) {
		double valorFinal = 0;
		for (int i = 0; i < clasificadores.size(); i++) {
			clasificadorDebil clasificador = clasificadores.get(i);
			double thetha = clasificador.getLinea().getThetha();
			double rho = clasificador.getLinea().getRho();

			double dist = punto.getX() * Math.cos(thetha) + punto.getY()
					* Math.sin(thetha);
			valorFinal += clasificador.getAlpha() * Math.signum(dist - rho);
		}
		return Math.signum(valorFinal);
	}
}
