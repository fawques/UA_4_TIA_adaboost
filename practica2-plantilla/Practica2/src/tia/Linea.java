/**
 * 
 */
package tia;

import java.awt.Point;

/**
 * Línea para adaboost. Se define con un rho y un thetha, según la fórmula
 * rho = x * cos(thetha) + y * sen(thetha)
 */
public class Linea {
	/**
	 * distancia al origen
	 */
	private double rho;
	/**
	 *  angulo de la recta
	 */
	private double thetha;
	
	/**
	 * punto que corta con uno de los límites del canvas
	 */
	private Point origen;
	/**
	 * punto que corta con otro límite del canvas
	 */
	private Point destino;
	
	/** Constructor de línea. Calcula los puntos de corte con los límites del canvas
	 * @param _rho rho de la línea
	 * @param _thetha thetha de la línea
	 * @param ancho ancho del canvas
	 * @param alto alto del canvas
	 */
	public Linea(double _rho, double _thetha, int ancho, int alto){
		rho = _rho;
		thetha = _thetha;
		// Calculamos los puntos extremo de la recta, de forma que vaya de lado a lado del canvas
		int x = 0;
		int y = (int) Math.round((rho/Math.sin(thetha)));
		if(y < 0 || y >= alto){
			y = 0;
			x = (int) Math.round((rho/Math.cos(thetha)));
			if(x < 0 || x >= ancho){
				y = alto -1;
				x = (int) Math.round(((rho - y * Math.sin(thetha))/Math.cos(thetha)));
			}
		}
		origen = new Point(x, y);
		
		x = ancho-1;
		y = (int) Math.round((rho - x * Math.cos(thetha))/Math.sin(thetha));
		if(y < 0 || y >= alto){
			y = alto-1;
			x = (int) Math.round(((rho - y * Math.sin(thetha))/Math.cos(thetha)));
			if(x < 0 || x >= ancho){
				y = 0;
				x = (int) Math.round(((rho - y * Math.sin(thetha))/Math.cos(thetha)));
			}
		}
		destino = new Point(x, y);
		
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
	public Point getOrigen(){
		return origen;
	}
	public Point getDestino(){
		return destino;
	}
}
