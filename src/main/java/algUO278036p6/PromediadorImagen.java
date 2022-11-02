package algUO278036p6;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PromediadorImagen {
	
	private Imagen real_img, bad_img; // para almacenar las imagenes con patron bueno y malo (negativo del malo)
	private Imagen avg_img, half1_img, half2_img; // para almacenar los promedios del subconjunto 1 y 2
	private Imagen[] dataset; // almacena el conjunto de de imagenes generadas (buenas y malas)
	public int[] sol; // array que determina donde poner las im�genes 0->no asignada, 1->primer subconjunto, 2->segundo subconjunto
	public int[] bestSol; // mejor soluci�n
	private int width, height; // ancho y alto de las im�genes
	//backtracking variables
	private int counter; // contador de nodos en el arbol impl�cito
	private double max_zncc; // donde almacenar el ZNCC final
	private int counter0 = 0;
	private int counter1 = 0;
	private int counter2 = 0;
	/** Constructor
	* @real_path  ruta del modelo de imagen "buena" (patr�n a encontrar) en disco
	* @bad_path  ruta del modelo de imagen "mala"
	* @n_real  numero de imagenes buenas (>= 1)
	* @n_bad  numero de imagenes "malas" (tiene que ser menor que las buenas) 
	* @s_noise  standard deviation for noise 
	*/
	public PromediadorImagen(String real_path, String bad_path, int n_real, int n_bad, double s_noise) {
		
		assert (n_real >= 1) && (n_bad < n_real); 
		
		// Cargando los patrones de referencia (buena y mala)
		this.real_img = new Imagen(real_path);
		this.bad_img = new Imagen(bad_path);
		this.width = this.real_img.getWidth();
		this.height = this.real_img.getHeight();
		
		// Se crean con conjunto de imagenes con un array ordenado aleatoriamente para posicionar 
		// las imagenes buenas y malas aleatoriamente
		int total_imgs = n_real + n_bad; // numero total de im�genes
		this.dataset = new Imagen[total_imgs]; 
		this.sol = new int[total_imgs]; // d�nde se almacena la soluci�n actual (combinaci�n de asignaciones): 0->no asignada, 1->primer subconjunto, 2->segundo subconjunto 
		this.bestSol = new int[total_imgs]; // d�nde se almacena la mejor soluci�n
		int[] rand_index = this.randomIndexes(total_imgs); // array con las posiciones aleatorias
		Imagen hold_img; // imagen temporal
		int region = 0; // 0-arriba, 1-bajo, 2-izquierda, 3-derecha
		for (int i=0; i<n_real; i++) { // im�genes buenas
			hold_img = new Imagen(this.width, this.height); // creaci�n de la imagen
			hold_img.addSignal(this.real_img); // a�adir los valores de los p�xeles
			hold_img.suppressRegion(region); // suprimir una region
			hold_img.addNoise(s_noise); // a�adir ruido
			this.dataset[rand_index[i]] = hold_img; // incluir la imagne en una posci�n aleatorio de dataset
			if (region == 3) region = 0;
			else region++;
		}
		
		
		region = 0;
		for (int i=n_real; i<n_real+n_bad; i++) { // bucle para las im�genes malas
			hold_img = new Imagen(this.width, this.height); 
			hold_img.addSignal(this.bad_img); 
			hold_img.invertSignal(); 
			hold_img.suppressRegion(region); 
			hold_img.addNoise(s_noise);   
			this.dataset[rand_index[i]] = hold_img; 
			if (region == 3) region = 0;
			else region++;
		}
	}
	
	/**
	 * Gener una array con valores de posiciones aleatorios
	 * @param n longitud del array
	 * @return 
	 */
	public int[] randomIndexes(int n) {
		List<Integer> list = new ArrayList<>();
		for (int i = 0; i < n; i++)
			list.add(i);
		Collections.shuffle(list);
		int[] array = new int[n];
		for (int i = 0; i < n; i++)
			array[i] = list.get(i);
		return array;
	}
	
	/**
	 * Almacena las im�genes generadas seg�n la mejor soluci�n encontrada
	 * @out_dir directorio donde se almacenan las im�genes 
	 */
	public void saveResults(String out_dir) {
		this.avg_img.save(out_dir + "/img_avg.png");
		this.half1_img.save(out_dir + "/img_half1_avg.png");
		this.half2_img.save(out_dir + "/img_half2_avg.png");
		for(int i=0; i<this.dataset.length; i++) {
			this.dataset[i].save(out_dir + "/img_" + i + "_klass_" + this.bestSol[i] + ".png");
		}
	}
	
	/**
	 * @return devuelve el n�mero de pasos requeridos por el algoritmo 
	 */
	public int getCounter() {
		return counter;
	}
	
	/** Calcula el ZNCC entre los promedios de los dos subconjuntos de im�genes
	 * @return el valor de ZNCC
	 */
	public double zncc() {
		return this.half1_img.zncc(this.half2_img);
	}

	//	this.dataset[rand_index[i]] = hold_img; // incluir la imagne en una posci�n aleatorio de dataset

	
	/**
	 * Greedy algorithm: random instances for each half, the best one is the final solution    
	 * @n_tries numero de intentos aleatorios     
	 */

	public void splitSubsetsGreedy(int n_tries) {
	        counter = 0;
	        this.max_zncc = -1;
	        Random r = new Random();
	        int[] vector = new int[dataset.length];
	        Imagen grupo1P = new Imagen(width, height);
	        Imagen grupo2P = new Imagen(width, height);
	        for (int j = 0; j < n_tries; j++) {
	            grupo1P = new Imagen(width, height);
	            grupo2P = new Imagen(width, height);
	            counter++;
	            for (int i = 0; i < vector.length; i++) {
	                vector[i] = r.nextInt(3);
	                if (vector[i] == 1) {
	                    grupo1P.addSignal(this.dataset[i]);
	                }
	                if (vector[i] == 2) {
	                    grupo2P.addSignal(this.dataset[i]);
	                }
	            }
	            if (grupo2P.zncc(grupo1P) > max_zncc) {
	                half1_img = grupo1P.copy();
	                half2_img = grupo2P.copy();
	                max_zncc = grupo2P.zncc(grupo1P);
	                bestSol = vector.clone();
	            }
	        }
	        this.avg_img = new Imagen(width, height);
	        this.avg_img.addSignal(grupo2P);
	        this.avg_img.addSignal(grupo1P);
	  }
	
	public void backTracking(int nivel) 
	{

		
	}
	
    private void recursivoBactracking(int nivel) {
        if (nivel == dataset.length) {
            Imagen grupo1P = new Imagen(width, height);
            Imagen grupo2P = new Imagen(width, height);
            
            for (int i = 0; i < sol.length; i++) {
                if (sol[i] == 1) {
                    grupo1P.addSignal(this.dataset[i]);
                }
                if (sol[i] == 2) {
                    grupo2P.addSignal(this.dataset[i]);
                }
            }
            if (grupo2P.zncc(grupo1P) > max_zncc) {
                half1_img = grupo1P.copy();
                half2_img = grupo2P.copy();
                max_zncc = grupo2P.zncc(grupo1P);
                bestSol = sol.clone();
            }
            avg_img.addSignal(half1_img);
            avg_img.addSignal(half2_img);
        } else {
            sol[nivel] = 0;
            counter++;
            recursivoBactracking(nivel + 1);
            sol[nivel] = 1;
            counter++;
            recursivoBactracking(nivel + 1);
            sol[nivel] = 2;
            counter++;
            recursivoBactracking(nivel + 1);
            nivel = nivel + 1;
        }
    }
	
	/**
	 * Algoritmo backtracking con condici�n balanceo 
	 * @max_unbalancing: (condici�n de poda) determina la diferencia m�xima en el n�mero de im�genes
	 *                   entre los dos subconjuntos              
	 */
    private void recursivoBactrackingPoda(int nivel, int balanceo, int nrama1, int nrama2, int nrama3) {
        if (nivel == dataset.length) {
            Imagen grupo1P = new Imagen(width, height);
            Imagen grupo2P = new Imagen(width, height);
            
            for (int i = 0; i < sol.length; i++) {
                if (sol[i] == 1) {
                    grupo1P.addSignal(this.dataset[i]);
                }
                if (sol[i] == 2) {
                    grupo2P.addSignal(this.dataset[i]);
                }
            }
            if (grupo2P.zncc(grupo1P) > max_zncc) {
                half1_img = grupo1P.copy();
                half2_img = grupo2P.copy();
                max_zncc = grupo2P.zncc(grupo1P);
                bestSol = sol.clone();
            }
            avg_img.addSignal(half1_img);
            avg_img.addSignal(half2_img);
        } else {
            if (Math.abs(nrama1 - nrama2) < balanceo && Math.abs(nrama3 - nrama1) < balanceo) {
                sol[nivel] = 0;
                counter++;
                recursivoBactrackingPoda(nivel + 1, balanceo, nrama1 + 1, nrama2, nrama3);
            } if (Math.abs(nrama3 - nrama2) < balanceo) {
                sol[nivel] = 1;
                counter++;
                recursivoBactrackingPoda(nivel + 1, balanceo, nrama1, nrama2 + 1, nrama3);
            }
            sol[nivel] = 2;
            counter++;
            recursivoBactrackingPoda(nivel + 1, balanceo, nrama1, nrama2, nrama3 + 1);
        }
    }
    
    
    
    
    
    
    public void splitSubsetsBacktracking(int max_unbalancing) {
        counter = 1;
        max_zncc = 0;
        recursivoBactrackingPoda(0, max_unbalancing,0,0,0);
    }


	/**
	 * Algoritmo backtracking sin condici�n de balanceo.           
	 */
	public void splitSubsetsBacktracking() {
		this.counter = 1;
		this.max_zncc = 0;
		int nivel = 0;
		recursivoBactracking(nivel);	
	}

	public void branchAndBoundMethod() {
		// TODO Auto-generated method stub
		
	}



}
