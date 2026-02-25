package interfaz;

import java.util.Scanner;

import dao.DaoArticulo;
import excepciones.BusinessException;
import jdbc.ConexionJdbc;
import pojos.Articulo;

/*
 * Restricción 2: Actualizar artículos: El estado del artículo
 *
 *										PERMITIDO: operativo -> mantenimiento y viceversa
 *
 *										PROHIBIDO: mantenimiento/operativo -> retirado	y viceversa
 *
 * Actualiza el estado de un articulo a un nuevo estado (operativo o mantenimiento)
 * Restricción 2. Dado el id del articulo, haz las comprobaciones necesarias
 * para que la operación sea correcta.
 */


public class Ejercicio_B2_Actualizar_Articulo {

	public static void main(String[] args) {
		
		ConexionJdbc conJdbc = null;
		
		try {
			
			conJdbc = new ConexionJdbc("Configuracion/propiedadesInventario.txt");
			conJdbc.conectar();
			
			Scanner sc = new Scanner(System.in);
			
			Articulo articulo = new Articulo();
			
			System.out.print("Introduce el id del artículo que quieres actualizar: ");
			int id = sc.nextInt();
			
			DaoArticulo daoArticulo = new DaoArticulo();
			
			articulo = daoArticulo.buscarPorId(id);
			
			daoArticulo.actualizarCustom(articulo);
			
			System.out.println("Articulo actualizado correctamente.");
			
			sc.close();
			
		} catch (BusinessException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		} finally {
			conJdbc.desconectar();
		}

	}

}
