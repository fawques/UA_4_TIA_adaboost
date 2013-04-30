package tia;

public class clasificadorDebil {
	Linea linea;
	private double epsilon;
	private double alfa;
	
	public clasificadorDebil(Linea _linea){
		linea = _linea;
		setEpsilon(-1);
		setAlfa(0);
	}

	public double getAlfa() {
		return alfa;
	}

	public void setAlfa(double alfa) {
		this.alfa = alfa;
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

}
