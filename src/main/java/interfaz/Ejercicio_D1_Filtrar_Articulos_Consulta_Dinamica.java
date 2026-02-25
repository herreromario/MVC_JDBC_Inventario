package interfaz;

import java.util.List;

import dao.DaoArticulo;
import excepciones.BusinessException;
import jdbc.ConexionJdbc;
import pojos.Articulo;
import pojos.Departamento;
import pojos.Usuario;

/* 
 *	Obtener el id, numero de serie y estado filtrando los artículos 
 *	tanto por departamento como por usuario de alta, o por ninguno,
 *	en ese caso listaremos todos los artículos.
 */

public class Ejercicio_D1_Filtrar_Articulos_Consulta_Dinamica {

	public static void main(String[] args) {

		ConexionJdbc conJdbc = null;

		try {

			conJdbc = new ConexionJdbc("Configuracion/propiedadesInventario.txt");
			conJdbc.conectar();

			Departamento departamento = new Departamento();
			Usuario usuario = new Usuario();

			DaoArticulo daoArticulo = new DaoArticulo();
		
			List<Articulo> listaArticulos = daoArticulo.filtrarPorDepartamentoUsuario(departamento, usuario);
			
			for (Articulo articulo : listaArticulos) {
				System.out.println("\nID: " + articulo.getIdArticulo());
				System.out.println("Número de serie: " + articulo.getNumserie());
				System.out.println("Estado: " + articulo.getEstado() + "\n");
			}
			
		} catch (BusinessException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());

		} finally {
			conJdbc.desconectar();
		}

	}

}
