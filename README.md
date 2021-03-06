# LAOP
## Recommandation pour l'importation
Le projet utilise maven et Java 10

## Ajouter un algorithme
Dans le package `org.lrima.network.algorithms`, ajouter un package du nom de votre algorithme
Dans le package, il faut créer deux nouvelles classes, une classe Model et une classe Network.
### La classe modèle
La classe modèle doit hériter de la classe générique `NeuralNetworkModel` et doit contenir une annotation de type `AlgorithmInformation`. Par exemple, pour l'algorithme fullyConnected, la déclaration doit être faite comme suit :  
```
@AlgorithmInformation(name="Fully Connected", description = "Fully connected network with one hidden layer containing 2 neurons. All neurons are connected to each neurons in the next layer.")
public class FullyConnectedNeuralModel extends NeuralNetworkModel<FullyConnectedNeuralNetwork> { ...
```
Notez ici que la classe générique `NeuralNetworkModel` demande comme paramètre générique votre classe de qui hérite de `NeuralNetork`. 

La classe `NeuralNetworkModel` demande de redéfinir deux méthodes :
- `getDefaultOptions()` modifie le LinkedHashMap `option` et assigne les valeurs utilisées par défaut par le système de neurone. Ces valeurs peuvent ensuite être modifiées dans l'application avant de partir l'animation.
- `getNeuralNetworkClass()` retourne l'objet classe de `NeuralNetwork` 
### La classe NeuralNetwork
La classe NeuralNetwork doit hériter de la classe `NeuralNetwork` et implémenter par l'interface serialisable, par exemple :  
```
public class FullyConnectedNeuralNetwork extends NeuralNetwork implements Serializable { 
```
Plusieurs fonctions ont besoin d'être redéfinies : 
- `feedForward()` : Méthode qui active les différentes couches du système de neurone. Pour accéder aux valeurs des capteurs et définir la valeur des roues, il faut faire comme suit : 
```
double[] entrees = this.transmitters.stream().mapToDouble(NeuralNetworkTransmitter::getNeuralNetworkInput).toArray();
...
receiver.setNeuralNetworkOutput(sorties);
```
Le tableau `entrees` contiendra toutes les valeurs des capteurs `[valeurCapteur 1, valeurCapteur2, ...]`. Et pour attribuer la valeur des roues, la fonction `receiver.setNeuralNetworkOutput([valeurRoue1, ValeurRoue2]);` est utilisée. 
- `init(ArrayList<NeuralNetworkTransmitter>, NeuralNetworkReceiver)` : initialise le neuralNetwork. Il faut appeler la fonction `super.init()` avec les bons paramètres pour être sûr que l'algorithme fonctionne bien 
- `crossOver(NeuralNetowk, NeuralNetwork)` : fonction qui permet au système de neurone d'apprendre. Elle retourne un nouveau système de neurone en fonction de deux autres en faisant un mélange des deux pour en obtenir un nouveau.
- `generationFinish()` : elle est appelée à la fin de chaque génération. Souvent utilisé pour muter ses connexions.
- `draw(Graphics2D)` : (optionnel) permet de visualiser graphiquement le système de neurone lorsqu'on clique sur une voiture pendant la simulation.
### Ajout de l'algorithme
Lorsque ces étapes sont faites, il faut ajouter le nouvel algorithme à la liste des algorithmes pris en charge. Ceci se fait en ajoutant la classe du model de notre algorithme dans la liste présente dans `AlgorithmManager` qui est dans `org.lrima.network.algorithms`
