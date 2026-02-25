package interfaz;

import java.time.LocalDateTime;
import java.util.Scanner;

import dao.DaoSalida;
import excepciones.BusinessException;
import jdbc.ConexionJdbc;
import pojos.Salida;

/*
 *	Realizar un prestamo de un articulo (dada la entrada de idarticulo)
 *	a un usuario (dada la entrada de idusuario) con las siguientes restricciones 
 *	antes de grabar una salida (tabla salida):
 *
 *	El estado del artículo tiene que ser 'operativo'
 *	La fecha de alta es la fecha actual
 *	El articulo no puede estar ya prestado
 *	El usuario no puede tener un artículo prestado en ese momento
 */

public class Ejercicio_C1_Realizar_Prestamo {

	public static void main(String[] args) {

		ConexionJdbc conJdbc = null;

		try {

			conJdbc = new ConexionJdbc("Configuracion/propiedadesInventario.txt");
			conJdbc.conectar();

			Scanner sc = new Scanner(System.in);
			Salida salida = new Salida();

			System.out.print("Introduce el id del artículo: ");
			int idarticulo = sc.nextInt();

			System.out.print("Introduce el id del usuario: ");
			int idusuario = sc.nextInt();

			salida.setArticulo(idarticulo);
			salida.setUsuario(idusuario);
			salida.setFechaSalida(LocalDateTime.now());

			DaoSalida daoSalida = new DaoSalida();

			daoSalida.insertarPrestamo(salida);

			System.out.println("Préstamo realizado");

			sc.close();

		} catch (BusinessException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());

		} finally {
			conJdbc.desconectar();

		}
	}
}
