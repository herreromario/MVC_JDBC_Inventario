package interfaz;

import java.time.LocalDateTime;
import java.util.List;

import dao.DaoArticulo;
import excepciones.BusinessException;
import jdbc.ConexionJdbc;
import pojos.Articulo;
import pojos.Departamento;

/*
 *	Obtener el id, número de serie, y la descripción del modelo, filtrando
 *	los artículos tanto por departamento y fecha de alta, o por uno de los dos valores,
 *	o por ninguno, en ese caso listaremos todos los artículos.
 */

public class Ejercicio_D2_Consulta_Dinamica_2 {

	public static void main(String[] args) {

		ConexionJdbc conJdbc = null;

		try {

			conJdbc = new ConexionJdbc("Configuracion/propiedadesInventario.txt");
			conJdbc.conectar();

			Departamento departamento = new Departamento();

			LocalDateTime fechaAlta = LocalDateTime.of(2026, 02, 02, 00, 00);

			DaoArticulo daoArticulo = new DaoArticulo();

			List<Object> listaArticulos = daoArticulo.filtarPorDepartamentoFechaAlta(departamento, fechaAlta);

			for (Object object : listaArticulos) {

			    if (object instanceof Articulo) {
			        Articulo a = (Articulo) object;
			        System.out.println("\nID: " + a.getIdArticulo());
			        System.out.println("NumSerie: " + a.getNumserie());
			    }

			    if (object instanceof String) {
			        String descripcion = (String) object;
			        System.out.println("Modelo: " + descripcion + "\n");
			    }
			}

		} catch (BusinessException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());

		} finally {
			conJdbc.desconectar();
		}
	}
}
