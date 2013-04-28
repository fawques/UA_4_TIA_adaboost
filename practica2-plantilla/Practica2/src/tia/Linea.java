/**
 * 
 */
package tia;

/**
 * @author Víctor
 *
 */
public class Linea {
	//distancia al origen
	private double rho;
	// angulo de la recta
	private double thetha;
	
	public Linea(double _rho, double _thetha){
		if(_rho > 0)
			rho = _rho;
		else
			rho = 0;
		
		thetha = _thetha;
	}
	
	public double getRho() {
		return rho;
	}
	public void setRho(double rho) {
		this.rho = rho;
	}
	public double getThetha() {
		return thetha;
	}
	public void setThetha(double thetha) {
		this.thetha = thetha;
	}
	
	
}
