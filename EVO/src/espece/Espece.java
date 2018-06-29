package espece;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.*;
import map.Map;

import core.CONSTANTS;
import espece.capteur.Capteur;
import espece.network.NeuralNetwork;
import map.MapPanel;
import simulation.Simulation;
public class Espece {
	
	//Width doit etre plus grand que height
	public static final int ESPECES_WIDTH = 74, ESPECES_HEIGHT = 50;
	
	private double x, y;
	private int w, h;
	
	private boolean alive;
	
	private double orientationRad;
	private double vitesse;
	private double acceleration;
	
	private double fitness;
	
	private ArrayList<Capteur> capteurs = new ArrayList<Capteur>();

	//MAX 180
	private int NB_CAPTEUR = 6;
	
	public NeuralNetwork neuralNetwork;
	
	private Point oldPos;
	
	public static int conter;

	public boolean selected;
	public double totalSpeed;
	public double maxDistanceFromStart;

	private Map map;

	/**
	 * Cr�er une esp�ce avec la position et l'orientation sp�cifi�e
	 * 
	 * @param positionDeDepart Position de d�part
	 * @param orientationDeDepart Orientation de d�part en degr�s
	 */
	public Espece(Point positionDeDepart, double orientationDeDepart, Map map) {
		this();

		this.map = map;
		this.x = positionDeDepart.x;
		this.y = positionDeDepart.y;
		this.orientationRad = Math.toRadians(orientationDeDepart);

        //Load la meilleure espece

        int ran = utils.Random.getRandomIntegerValue(1000);

        if(CONSTANTS.USE_BEST && ran < 800) {
            try {
                FileInputStream fis = new FileInputStream("best_nn.dat");
                ObjectInputStream ois = new ObjectInputStream(fis);

                neuralNetwork = (NeuralNetwork) ois.readObject();
                neuralNetwork.minimalModificationWeight();

                NB_CAPTEUR = neuralNetwork.getLayer(0).getSize();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        else{
            neuralNetwork = new NeuralNetwork(NB_CAPTEUR, 2);
        }

        //Ajoute comme input la position actuel et la destination
		//neuralNetwork.getLayer(0).addNeuron();
		//neuralNetwork.getLayer(0).addNeuron();

    }
	
	public Espece(Point positionDeDepart, double orientationDeDepart, Espece e, Map map) {
		this();
		this.map = map;
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

		if(map != null) {
			double distanceFromStart = this.distanceFrom(map.depart);

			if (distanceFromStart > maxDistanceFromStart) {
				maxDistanceFromStart = distanceFromStart;
			}
		}
		
		//Update le r�saux de neuronnes avec la valeur des capteurs
		double[] capteurArray = capteursToArray();
		/*double[] inputs = new double[capteurs.size() + 2];

		for(int i = 0 ; i < capteurArray.length ; i++){
			inputs[i] = capteurArray[i];
		}

		if(map != null && map.depart != null && map.destination != null){

			Point depart = this.map.depart;
			Point destination = this.map.destination;
			int width = this.map.w;
			int height = this.map.h;


			//Distance de la fin comme input
			inputs[(inputs.length - 1) - 1] = (double)(destination.x - x) / (double)width;
			inputs[(inputs.length - 1)] = (double)(destination.y - y) / (double)height;
		}*/



		neuralNetwork.update(capteurArray);

		double[] values = neuralNetwork.getOutputValues();
		D = values[0];
		G = values[1];
		
		orientationRad += Math.toRadians(D*dt - G*dt)*CONSTANTS.TURNRATE;
		acceleration = D*dt*CONSTANTS.VITESSE_VOITURE/100 + G*dt*CONSTANTS.VITESSE_VOITURE/100;
		vitesse += acceleration - vitesse;
		
		if(vitesse < 0) {
			vitesse = 0;
		}

		//Pour avoir la vitesse moyenne
		totalSpeed += vitesse;
		
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
		this.neuralNetwork.setFitness(fitness);
    }
}
