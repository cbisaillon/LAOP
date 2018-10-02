package org.lrima.network.interfaces;

import org.lrima.Interface.options.Option;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public abstract class NeuralNetwork {

    protected ArrayList<?extends NeuralNetworkTransmitter> transmitters;
    protected NeuralNetworkReceiver receiver;
    protected double fitness;
    protected LinkedHashMap<String, Option> options;

    public NeuralNetwork(LinkedHashMap<String, Option> options){
        this.options = options;
    }

    /**
     * Used when you create a NeatGenome without knowing in advance the transmitters
     * @param transmitters the transmitters this NeatGenome should use
     */
    public void setTransmitters(ArrayList<?extends NeuralNetworkTransmitter> transmitters){
        this.transmitters = transmitters;
    }

    /**
     * Used when you create a NeatGenome without knowing in advance the receiver
     * @param receiver the receiver this NeatGenome should use
     */
    public void setReceiver(NeuralNetworkReceiver receiver){
        this.receiver = receiver;
    }

    public abstract void init(ArrayList<? extends NeuralNetworkTransmitter> transmitters, NeuralNetworkReceiver receiver);
    public abstract void feedForward();
    //TODO : Change the parameter by something general (like ArrayList<NeuralNetworkReceiver>)
    //public abstract ArrayList<Espece> alterEspecesListAtGenerationFinish(ArrayList<Espece> currentBatch);
    public abstract void generationFinish();
    public abstract void draw(Graphics2D g, Dimension panelDimensions);
    public abstract NeuralNetwork crossOver(NeuralNetwork network1, NeuralNetwork network2);

    public void setFitness(double fitness){
        this.fitness = fitness;
    }



}
