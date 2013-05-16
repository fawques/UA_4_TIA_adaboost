package tia;

import java.util.List;

/**
 * Clasificador débil. Compuesto por líneas.
 */
public class clasificadorDebil {
	/**
	 * Línea usada para clasificar
	 */
	Linea linea;
	/**
	 * Cantidad de error del clasificador
	 */
	private double epsilon;
	/**
	 * Nivel de confianza en el clasificador
	 */
	private double alpha;
	
	public clasificadorDebil(Linea _linea){
		linea = _linea;
		setEpsilon(0);
		setAlpha(0);
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
	public Linea getLinea() {
		return linea;
	}
	
	/** Clasifica el punto determinado según el clasificador débil actual
	 * @param p punto
	 * @return Indica si la clasificación es correcta o no.
	 */
	public boolean clasificar(Punto p){
		double dist = p.getX() * Math.cos(linea.getThetha())
				+ p.getY() * Math.sin(linea.getThetha());
		return Math.signum(dist - linea.getRho()) == p.getTipo();
	}
	
	/** Clasifica una lista de puntos según el clasificador débil actual
	 * @param listaPuntos lista de puntos a clasificar
	 * @param listaPesos lista de pesos de los puntos de listaPuntos
	 */
	public void clasificar(List<Punto> listaPuntos, List<Double> listaPesos){
		double epsilon = 0;
		for (int j = 0; j < listaPuntos.size(); j++) {
			Punto punto = listaPuntos.get(j);
			
			if (!clasificar(punto)) {
				epsilon += listaPesos.get(j);
			}
		}
		setEpsilon(epsilon);
		
	}

}
