package tia;

import java.util.ArrayList;
import java.util.List;

public class clasificadorFuerte {
	List<clasificadorDebil> clasificadores;
	private double epsilon;
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
		for (int i = 0; i < _clas.clasificadores.size(); i++) {
			clasificadores.add(_clas.clasificadores.get(i));
		}
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
	 * @param punto
	 * @return
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
