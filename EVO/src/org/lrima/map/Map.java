package org.lrima.map;

import java.awt.Point;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.lrima.core.UserPrefs;
import org.lrima.espece.Espece;
import org.lrima.map.Studio.Drawables.Line;
import org.lrima.map.Studio.Drawables.Obstacle;
import org.lrima.simulation.Simulation;

public class Map {
	
	public ArrayList<Obstacle> obstacles;
	public Point destination = null;
	public Point depart = null;
	public Simulation simulation;
	
	public int w, h;
	public int orientation;


    /**
     * Initialize a map with a simulation
     * @param sim the simulation that runs the map
     */
	public Map(Simulation sim) {
		
		this.simulation = sim;
        orientation = 0;
        reloadMap();

		this.orientation = 0;
	}

    /**
     * Reload la org.lrima.map, apres l'avoir generer
     */
    private void reloadMap(){
        this.obstacles = new ArrayList<Obstacle>();

        try {
            FileInputStream fis = new FileInputStream("test.map");
            ObjectInputStream ois = new ObjectInputStream(fis);
            this.obstacles = (ArrayList<Obstacle>) ois.readObject();
        }catch (Exception e){
            e.printStackTrace();
        }

        //depart = new Point(500, 500);

        this.w = 10000;
        this.h = 10000;

        //Trouve le depart
        Iterator<Obstacle> iterator = obstacles.iterator();
        while (iterator.hasNext()){
            Obstacle obstacle = iterator.next();
            if(obstacle.type.equals(Obstacle.TYPE_START)){
                this.depart = obstacle.getPosition();
            }
        }

        /*for(int i = 0 ; i < mapping.length ; i++){
            for(int j = 0 ; j < mapping[0].length ; j++){
                switch(mapping[i][j]){
                    case 1:
                        this.obstacles.add(new ObstcaleRect(j * blocSize, i * blocSize, blocSize, blocSize));
                        break;
                    case 2:
                        this.obstacles.add(new ObstcaleRect(j * blocSize, (i * blocSize) + blocSize / 2, blocSize, blocSize / 2));
                        break;
                    case 3:
                        this.obstacles.add(new ObstcaleRect(j * blocSize, ((i + 1) * blocSize) - (int)(blocSize / 1.5), blocSize, (int)(blocSize / 1.5)));
                        break;
                    case 4:
                        this.obstacles.add(new ObstcaleRect(j * blocSize, ((i + 1) * blocSize) - (int)(blocSize / 1.1), blocSize, (int)(blocSize / 1.1)));
                        break;
                    case 9:
                        this.depart = new Point(j * blocSize,i * blocSize);
                        break;
                    case -2:
                        this.obstacles.add(new ObstacleArrivee(j * blocSize, i * blocSize, blocSize, blocSize));
                        break;
                }
            }
        }

        this.obstacles.add(new ObstacleContour(0, 0, w, h));*/
    }

	public void setFitnessToEspece(Espece e) {

		double scale = 0.001;

        double averageSpeed = e.getTotalSpeed() / simulation.getCurrentTime();
        int timeLimit = UserPrefs.TIME_LIMIT;

        double fitness = (e.getMaxDistanceFromStart() + e.getTotalDistanceTraveled()) ;

        //Quand il se rend a destination
        if(fitness == Double.POSITIVE_INFINITY){
            fitness = 9999999;
		}
		if(fitness == Double.NEGATIVE_INFINITY){
            fitness = 0.0;
        }

		if(e.getMaxDistanceFromStart() < 800){
		    fitness = 0.0;
        }

		e.setFitness(fitness);
	}

    public Point getDepart() {
        return depart;
    }

    public Point getDestination() {
        return destination;
    }

    public int getOrientation() {
        return orientation;
    }
}
