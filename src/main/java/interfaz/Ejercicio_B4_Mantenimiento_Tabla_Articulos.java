package interfaz;

import dao.DaoArticulo;
import excepciones.BusinessException;
import jdbc.ConexionJdbc;

/* 
 * 	Haz un mantenimiento de tu tabla de artículos actualizando 
 * 	el estado de todos los artículos que tienen fecha de baja a retirados.
 */

public class Ejercicio_B4_Mantenimiento_Tabla_Articulos {

	public static void main(String[] args) {

		ConexionJdbc conJdbc = null;

		try {

			conJdbc = new ConexionJdbc("Configuracion/propiedadesInventario.txt");
			conJdbc.conectar();

			DaoArticulo daoArticulo = new DaoArticulo();

			daoArticulo.bajaMasivaCustom();
			
			System.out.println("Articulos con fecha de baja actualizados correctamente.");

		} catch (BusinessException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());

		} finally {
			conJdbc.desconectar();
		}
	}
}
