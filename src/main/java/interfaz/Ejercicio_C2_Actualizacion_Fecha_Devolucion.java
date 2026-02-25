package interfaz;

import java.time.LocalDateTime;

import dao.DaoSalida;
import excepciones.BusinessException;
import jdbc.ConexionJdbc;
import pojos.Salida;

/*
 * 	Actualización de la 'fechadedevolucion', 
 * 
 *	No se puede actualizar la fecha de devolución con un valor
 *  que sea anterior a la “fechadesalida”, ni posterior a más de
 *  dias de la 'fechadesalida'
 */

public class Ejercicio_C2_Actualizacion_Fecha_Devolucion {

	public static void main(String[] args) {

		ConexionJdbc conJdbc = null;
		LocalDateTime loDevuelvo;
		
		try {
			
			conJdbc = new ConexionJdbc("Configuracion/propiedadesInventario.txt");
			conJdbc.conectar();
			
			Salida salida = new Salida();
			salida.setIdSalida(5273);
			loDevuelvo = LocalDateTime.now().plusDays(31);
			
			DaoSalida daoSalida = new DaoSalida();
			
			daoSalida.devolverArticulo(salida, loDevuelvo);
			
			System.out.println("Fecha de devolución asignada. Artículo devuelto correctamente.");
			
		} catch (BusinessException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());

		} finally {
			conJdbc.desconectar();
		}
	}

}
