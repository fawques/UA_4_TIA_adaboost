package tia;

import java.util.List;

public class clasificadorDebil {
	Linea linea;
	private double epsilon;
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
	
	public boolean clasificar(Punto p){
		double dist = p.getX() * Math.cos(linea.getThetha())
				+ p.getY() * Math.sin(linea.getThetha());
		return Math.signum(dist - linea.getRho()) == p.getTipo();
	}
	
	public void clasificar(List<Punto> listaPuntos, List<Double> listaPesos){
		double thetha = getLinea().getThetha();
		double rho = getLinea().getRho();

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
