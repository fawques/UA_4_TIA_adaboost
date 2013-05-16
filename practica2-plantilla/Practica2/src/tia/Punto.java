package tia;
import static java.lang.Math.signum;

/**
 * punto para adaboost. Guarda una posición (X,Y) y un tipo de punto
 */
public class Punto {

	private double x;
	private double y;
	private double tipo;
	
	public Punto(double x, double y, double tipo) {
		super();
		this.x = x;
		this.y = y;
		this.setTipo(tipo);
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getTipo() {
		return tipo;
	}

	public void setTipo(double tipo) {
		this.tipo = signum(tipo);
	}

}
