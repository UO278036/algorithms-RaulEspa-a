package algUO278036.p2 ;




public class Burbuja extends Vector
{
	public Burbuja(int nElementos) {
		super(nElementos);
	}

	/**
	 * Ordenaci�n por el m�todo de Burbuja
	 */
	@Override
	public void ordenar() {
		int n = elements.length;
		for (int i = 0; i <= n-2; i++) {
			for (int j = n -1; j>i;j--) {
				if(elements[j -1] > elements[j]) {
					intercambiar(j-1, j);
				}
			}
		}
	}  

	@Override
	public String getNombre() {
		return "Burbuja";
	}  
} 