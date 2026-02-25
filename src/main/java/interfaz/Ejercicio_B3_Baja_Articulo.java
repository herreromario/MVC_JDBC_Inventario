package interfaz;

import java.util.Date;
import java.util.Scanner;

import dao.DaoArticulo;
import excepciones.BusinessException;
import jdbc.ConexionJdbc;
import pojos.Articulo;

/*
 * 	Reestricción 3: Baja de artículos: el estado pasará a retirado
 * 
 * 	Haz una operación de baja, es decir, retirar un artículo,
 * 	los artículos que estan en estado operativo o de mantenimiento
 * 	pueden pasar a estado retirado. En ese caso, se tiene que actualizar
 * 	el usuario de baja y la fecha de baja.
 * 
 */

public class Ejercicio_B3_Baja_Articulo {

	public static void main(String[] args) {
		
		ConexionJdbc conJdbc = null;
		
		try {
			
			conJdbc = new ConexionJdbc("Configuracion/propiedadesInventario.txt");
			conJdbc.conectar();
			
			Scanner sc = new Scanner(System.in);
			
			Articulo articulo = new Articulo();
			
			System.out.print("Introduce el id del artículo que quieres dar de baja: ");
			int id = sc.nextInt();
			
			DaoArticulo daoArticulo = new DaoArticulo();
			
			articulo = daoArticulo.buscarPorId(id);
			
			articulo.setFechabaja(new Date());
			articulo.setUsuariobaja(958);
			
			daoArticulo.darBajaCustom(articulo);
			
			System.out.println("Articulo dado de baja correctamente.");
			
			sc.close();
			
		} catch (BusinessException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			
		} finally {
			conJdbc.desconectar();
		}

	}

}
