package interfaz;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Scanner;

import dao.DaoArticulo;
import dao.DaoDepartamento;
import dao.DaoUsuario;
import excepciones.BusinessException;
import jdbc.ConexionJdbc;
import pojos.Articulo;
import pojos.Departamento;
import pojos.Usuario;

/*
 * 	Operaciones de mantenimiento sobre la tabla de artículos:
 * 	
 *	Los posibles estados de un artículo son: operativo, mantenimiento o retirado
 *
 *	Restricción 1: Insertar arículos: fecha de alta, usuario de alta y estado = operativo
 *											
 * 	Enunciado: Inserta un artículo nuevo. 
 * 
 * 	Estado: operativo.
 * 
 * 	Obligatorio: modelo, espacio, fecha de alta, id del usuario, número de serie y departamento
 * 
 * 	El usuario introducirá el nombre
 */

public class Ejercicio_B1_Insertar_Articulo {

	public static void main(String[] args) {

		ConexionJdbc conJdbc = null;

		try {

			conJdbc = new ConexionJdbc("Configuracion/propiedadesInventario.txt");
			conJdbc.conectar();

			Scanner sc = new Scanner(System.in);

			Articulo articulo = new Articulo();

			System.out.print("Introduce el nombre del departamento: ");
			String nombreDepartamento = sc.nextLine();

			articulo.setModelo(3255);
			articulo.setEspacio(1);
			articulo.setFechaalta(new Date());
			articulo.setUsuarioalta(958);
			articulo.setNumserie("ABC123");

			DaoDepartamento daoDepartamento = new DaoDepartamento();
			Departamento departamento = daoDepartamento.buscarPorNombre(nombreDepartamento);

			if (departamento == null) {
				throw new BusinessException("Departamento no existe");
			}

			articulo.setDepartamento(departamento.getIddepartamento());

			DaoArticulo daoArticulo = new DaoArticulo();
			daoArticulo.grabarCustom(articulo);

			System.out.println("Artículo insertado correctamente");

		} catch (BusinessException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		} finally {
			conJdbc.desconectar();
		}
	}
}
