package algUO278036.p2;

public class OrdenacionPruebas {

	/** Este programa sirve para probar todos los algoritmos de ordenacion
	 * */
	public static void main (String arg []){
		int n= Integer.parseInt(arg[0]);  //tama�o del problema
		
		
		// Prueba ordenaci�n: Insercion
		pruebaAlgoritmoOrdenacion(new Insercion(n));
		
		// Prueba ordenaci�n: Seleccion
		pruebaAlgoritmoOrdenacion(new Seleccion(n));
		
		// Prueba ordenaci�n: Burbuja
		pruebaAlgoritmoOrdenacion(new Burbuja(n));
		
		// Prueba ordenaci�n: RapidoFatal
		pruebaAlgoritmoOrdenacion(new RapidoFatal(n));
		
		// Prueba ordenaci�n: RapidoCentral
		pruebaAlgoritmoOrdenacion(new RapidoCentral(n));
		
		// Prueba ordenaci�n: RapidoMediana
		pruebaAlgoritmoOrdenacion(new RapidoMediana(n));
	}
	
	public static void pruebaAlgoritmoOrdenacion(Vector v) {
		System.out.println(" \n\nPrueba ordenaci�n: "+v.getNombre());
		System.out.println ("Ordenar Vector ya ordenado");
		v.ordenDirecto();
		System.out.println ("Vector a ordenar es:");
		v.escribe(System.out);	
		v.ordenar();
		System.out.println ("Vector despu�s de ordenar");
		v.escribe(System.out);

		System.out.println ("Ordenar Vector inverso");
		v.ordenInverso();
		System.out.println ("Vector a ordenar es:");
		v.escribe(System.out);	
		v.ordenar();
		System.out.println ("Vector despu�s de ordenar");
		v.escribe(System.out);

		System.out.println ("Ordenar Vector aleatorio");
		v.ordenAleatorio();
		System.out.println ("Vector a ordenar es:");
		v.escribe(System.out);	
		v.ordenar();
		System.out.println ("Vector despu�s de ordenar");
		v.escribe(System.out);
	}
	
}
