package espece;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import core.CONSTANTS;
import espece.capteur.Capteur;
import espece.network.NeuralNetwork;
import map.MapPanel;
import simulation.Simulation;
public class Espece {
	
	//Width doit etre plus grand que height
	public static final int ESPECES_WIDTH = 150, ESPECES_HEIGHT = 101;
	
	private double x, y;
	private int w, h;
	
	private boolean alive;
	
	private double orientationRad;
	private double vitesse;
	private double acceleration;
	
	private double fitness;
	
	private ArrayList<Capteur> capteurs = new ArrayList<Capteur>();

	//MAX 180
	private final int NB_CAPTEUR = 10;
	
	public NeuralNetwork neuralNetwork;
	
	private Point oldPos;
	
	public static int conter;

	public boolean selected;
	
	/**
	 * Cr�er une esp�ce avec la position et l'orientation sp�cifi�e
	 * 
	 * @param positionDeDepart Position de d�part
	 * @param orientationDeDepart Orientation de d�part en degr�s
	 */
	public Espece(Point positionDeDepart, double orientationDeDepart) {
		this();
		
		this.x = positionDeDepart.x;
		this.y = positionDeDepart.y;
		this.orientationRad = Math.toRadians(orientationDeDepart);
		neuralNetwork = new NeuralNetwork(NB_CAPTEUR, 2);
	}
	
	public Espece(Point positionDeDepart, double orientationDeDepart, Espece e) {
		this();
		this.x = positionDeDepart.x;
		this.y = positionDeDepart.y;
		this.orientationRad = Math.toRadians(orientationDeDepart);
		this.neuralNetwork = new NeuralNetwork(e.getNeuralNetwork());
		
	}
	
	public Espece() {
		this.w = ESPECES_WIDTH;
		this.h = ESPECES_HEIGHT;

		//Setup les capteurs
        for(int i = 90 ; i > -90 ; i -= 180 / NB_CAPTEUR){
            if(i > 0 && i < 90){
                capteurs.add(new Capteur(this, i, w/2, -h/2));
            }
            else if(i == 0){
                capteurs.add(new Capteur(this, i, w/2, 0));
            }
            else{
                capteurs.add(new Capteur(this, i, w/2, h/2));
            }

        }

		alive = true;
	}

	public Espece(Espece e) {
		neuralNetwork = e.getNeuralNetwork();
	}
	
	public double[] capteursToArray() {
		double[] capteursValue = new double[this.capteurs.size()];
		for (int i = 0; i < capteursValue.length; i++) {
			capteursValue[i] = this.capteurs.get(i).getValue();
		}
		
		return capteursValue;
		
	}
	
	public void update(double dt, double D, double G) {
		if(!alive) {
			return;
		}
		
		//Update le r�saux de neuronnes avec la valeur des capteurs
		neuralNetwork.update(this.capteursToArray());
		double[] values = neuralNetwork.getOutputValues();
		D = values[0];
		G = values[1];
		
		orientationRad += Math.toRadians(D*dt - G*dt)*CONSTANTS.TURNRATE;
		acceleration = D*dt*CONSTANTS.VITESSE_VOITURE/100 + G*dt*CONSTANTS.VITESSE_VOITURE/100;
		vitesse += acceleration - vitesse;
		
		if(vitesse < 0) {
			vitesse = 0;
		}
		
		x += vitesse*Math.cos(orientationRad);
		y += vitesse*Math.sin(orientationRad);
		
	}
	
	public void draw(Graphics2D g) {
		AffineTransform oldForm = g.getTransform();
		
		g.rotate(orientationRad,x, y);
		g.setColor(new Color(90, 200, 255));
		
		g.drawImage(MapPanel.IMG_VOITURE, (int)x-w/2, (int)y-h/2, null);

		//Draw les capteurs
        if(selected) {
            for (Capteur c : capteurs) {
                c.draw(g);
            }
        }

		g.setTransform(oldForm);
	}

	public void kill() {
		alive = false;
	}
	
	public void tp(Point p) {
		this.x = p.x;
		this.y = p.y;
	}

	public void resetCapteur() {
		for(Capteur c : capteurs) {
			c.reset();
		}
	}
	
	public double getOrientation() {
		return orientationRad;
	}
	
	public void setOrientation(double oriRad) {
		orientationRad = oriRad;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public int getWidth() {
		return w;
	}

	public int getHeight() {
		return h;
	}

	public double getFitness() {
		// TODO Auto-generated method stub
		return this.fitness;
	}
	
	public NeuralNetwork getNeuralNetwork() {
		return this.neuralNetwork;
	}

	public ArrayList<Capteur> getCapteursList() {
		// TODO Auto-generated method stub
		return capteurs;
	}

	public int distanceFrom(Point point) {
		return (int) ((point.x - this.x)*(point.x - this.x) + (point.y - this.y)*(point.y - this.y));
	}

	/**
	 * Tp la voiure au point d�part et lui donne une orientation orientation.
	 * 
	 * De plus, l'acceleration et la vitesse sont mise a z�ro
	 * 
	 * @param depart
	 * @param orientation
	 */
	public void tpLikeNew(Point depart, int orientation) {
		this.acceleration = 0;
		this.vitesse = 0;
		this.x = depart.getX();
		this.y = depart.getY();
		this.orientationRad = Math.toRadians(orientation);
		this.alive = true;
		this.resetCapteur();
		
	}

	public boolean isDead() {
		// TODO Auto-generated method stub
		return !alive;
	}
	
	public void mutate() {
		this.neuralNetwork.mutate();
	}


    public void setFitness(double fitness) {
		this.fitness = fitness;
    }
}
