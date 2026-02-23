package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import pojos.Articulo;
import excepciones.BusinessException;
import jdbc.ConexionJdbc;

public class DaoArticulo extends DaoGenerico<Articulo, Integer> {

	public void grabar(Articulo a) throws BusinessException {
		Connection con = ConexionJdbc.getConnection();
		PreparedStatement pstm = null;
		try {
			// Preparar para la inserción
			String sql = "INSERT INTO articulo " + "(idarticulo, numserie, estado, fechaalta, usuarioalta, "
					+ "modelo, departamento, espacio) " + "VALUES (?,?,?,?,?,?,?,?)";
			pstm = con.prepareStatement(sql);
			pstm.setInt(1, a.getIdArticulo());
			pstm.setString(2, a.getNumserie());
			pstm.setString(3, "operativo");
			pstm.setDate(4, new java.sql.Date(a.getFechaalta().getTime()));

			// Opción 2 insertar fecha pstm.setObject(4,a.getFechaalta());

			// Los Integer introducirlos como null utilizar setObject

			pstm.setObject(5, a.getUsuarioalta());
			pstm.setObject(6, a.getModelo());
			pstm.setObject(7, a.getDepartamento());
			pstm.setObject(8, a.getEspacio());

			// Insertar
			pstm.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new BusinessException("Error al insertar");
		} finally {
			ConexionJdbc.cerrar(pstm);
		}
	}

	// Ejercicio B1
	public void grabarCustom(Articulo articulo) throws BusinessException {

		Connection con = ConexionJdbc.getConnection();
		PreparedStatement pstm = null;

		try {

			if (articulo.getNumserie() == null) {
				throw new BusinessException("Número de serie obligatorio");
			}

			if (articulo.getFechaalta() == null) {
				throw new BusinessException("Fecha de alta obligatoria");
			}

			if (articulo.getUsuarioalta() == null) {
				throw new BusinessException("Usuario de alta obligatorio");
			}

			if (articulo.getModelo() == null) {
				throw new BusinessException("Modelo obligatorio");
			}

			if (articulo.getDepartamento() == null) {
				throw new BusinessException("Departamento obligatorio");
			}

			if (articulo.getEspacio() == null) {
				throw new BusinessException("Espacio obligatorio");
			}

			String sql = "INSERT INTO articulo " + "(idarticulo, numserie, estado, fechaalta, usuarioalta, "
					+ "modelo, departamento, espacio) " + "VALUES (?,?,?,?,?,?,?,?)";

			pstm = con.prepareStatement(sql);

			pstm.setInt(1, generarNuevoId());
			pstm.setString(2, articulo.getNumserie());
			pstm.setString(3, "operativo");
			pstm.setDate(4, new java.sql.Date(articulo.getFechaalta().getTime()));
			pstm.setInt(5, articulo.getUsuarioalta());
			pstm.setInt(6, articulo.getModelo());
			pstm.setInt(7, articulo.getDepartamento());
			pstm.setInt(8, articulo.getEspacio());

			pstm.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new BusinessException("Error al insertar");
		} finally {
			ConexionJdbc.cerrar(pstm);
		}
	}

	// Ejercicio B2
	public void actualizarCustom(Articulo articulo) throws BusinessException {
		Connection con = ConexionJdbc.getConnection();
		PreparedStatement pstm = null;

		try {

			if (articulo.getEstado().equals("retirado")) {
				throw new BusinessException("Articulo retirado, imposible la actualización.");
			}

			String sql = "UPDATE articulo SET estado = ? where idarticulo = ?";

			pstm = con.prepareStatement(sql);

			if (articulo.getEstado().equals("mantenimiento")) {
				pstm.setString(1, "operativo");

			} else if (articulo.getEstado().equals("operativo")) {
				pstm.setString(1, "mantenimiento");
			}

			pstm.setInt(2, articulo.getIdArticulo());

			pstm.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new BusinessException("Error al actualizar");
		} finally {
			ConexionJdbc.cerrar(pstm);
		}
	}

	// Ejercicio B3
	public void darBajaCustom(Articulo articulo) throws BusinessException {

		Connection con = ConexionJdbc.getConnection();
		PreparedStatement pstm = null;

		try {

			if (articulo == null)
				throw new BusinessException("Artículo no encontrado");

			if (articulo.getEstado().equals("retirado")) {
				throw new BusinessException("El articulo ya está dado de baja.");
			}

			if (!articulo.getEstado().equals("mantenimiento") && !articulo.getEstado().equals("operativo")) {
				throw new BusinessException("El estado del artículo es erroneo, revisalo antes de darlo de baja.");
			}

			if (articulo.getUsuariobaja() == null) {
				throw new BusinessException("El usuario de baja es obligatorio");
			}

			if (articulo.getFechabaja() == null) {
				throw new BusinessException("La fecha de baja es obligatoria");
			}

			String sql = "UPDATE articulo SET estado = ?, fechabaja = ?, usuariobaja = ? where idarticulo = ?";

			pstm = con.prepareStatement(sql);

			pstm.setString(1, "retirado");
			pstm.setDate(2, new java.sql.Date(articulo.getFechabaja().getTime()));
			pstm.setInt(3, articulo.getUsuariobaja());
			pstm.setInt(4, articulo.getIdArticulo());

			pstm.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new BusinessException("Error al dar de baja.");

		} finally {
			ConexionJdbc.cerrar(pstm);
		}
	}

	public void bajaMasivaCustom() throws BusinessException {

	    Connection con = ConexionJdbc.getConnection();
	    PreparedStatement pstm = null;

	    try {

	        String sql = "UPDATE articulo SET estado = ? WHERE fechabaja IS NOT NULL";
	        pstm = con.prepareStatement(sql);

	        pstm.setString(1, "retirado");

	        pstm.executeUpdate();

	    } catch (SQLException e) {
	        throw new BusinessException("Error en la actualización masiva");
	    } finally {
	        ConexionJdbc.cerrar(pstm);
	    }
	}
	
	private Integer generarNuevoId() throws BusinessException {
		Connection con = ConexionJdbc.getConnection();
		PreparedStatement pstm = null;
		ResultSet rs = null;

		try {
			String sql = "SELECT MAX(idarticulo) FROM articulo";
			pstm = con.prepareStatement(sql);
			rs = pstm.executeQuery();

			if (rs.next()) {
				return rs.getInt(1) + 1;
			}

			return 1;

		} catch (SQLException e) {
			throw new BusinessException("Error generando id");
		} finally {
			ConexionJdbc.cerrar(rs);
			ConexionJdbc.cerrar(pstm);
		}
	}

	@Override
	public void actualizar(Articulo a) throws BusinessException {

		Connection con = ConexionJdbc.getConnection();
		PreparedStatement pstm = null;
		try {
			// Preparar la actualización.
			String sql = "UPDATE articulo " + " SET numserie= ?, estado = ?, fechaalta= ?, fechabaja= ?,"
					+ " usuarioalta = ?, usuariobaja = ?, modelo = ?, departamento = ?, espacio = ?,"
					+ "dentrode = ?, observaciones = ?" + " WHERE idarticulo = ?";

			pstm = con.prepareStatement(sql);
			pstm.setString(1, a.getNumserie());
			pstm.setString(2, a.getEstado());
			pstm.setDate(3, new java.sql.Date(a.getFechaalta().getTime()));
			pstm.setDate(4, new java.sql.Date(a.getFechabaja().getTime()));
			pstm.setInt(5, a.getUsuarioalta());
			pstm.setInt(6, a.getUsuariobaja());
			pstm.setInt(7, a.getModelo());
			pstm.setInt(8, a.getDepartamento());
			pstm.setInt(9, a.getEspacio());
			pstm.setInt(10, a.getDentrode());
			pstm.setString(11, a.getObservaciones());
			pstm.setInt(12, a.getIdArticulo());

			// Ejecutar la actualización
			pstm.executeUpdate();

		} catch (SQLException e) {
			throw new BusinessException("Error al actualizar");
		} finally {
			ConexionJdbc.cerrar(pstm);
		}
	}

	/*
	 * @Override public void grabarOActualizar(Articulo a) throws BusinessException
	 * { if(buscarPorId(a.getIdArticulo())!=null) grabar(a); else actualizar(a); }
	 */

	@Override
	public void borrar(Articulo a) throws BusinessException {
		borrar(a.getIdArticulo());
	}

	@Override
	public void borrar(Integer id) throws BusinessException {
		Connection con = ConexionJdbc.getConnection();
		PreparedStatement pstm = null;
		try {
			String sql = "DELETE FROM articulo WHERE idarticulo= ?";
			pstm = con.prepareStatement(sql);
			pstm.setInt(1, id);
			pstm.executeUpdate();
		} catch (SQLException e) {
			throw new BusinessException("Error al eliminar");
		} finally {
			ConexionJdbc.cerrar(pstm);
		}
	}

	@Override
	public Articulo buscarPorId(Integer id) throws BusinessException {
		Articulo a = null;
		Connection con = ConexionJdbc.getConnection();
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT * FROM articulo WHERE idarticulo=?";
			pstm = con.prepareStatement(sql);
			pstm.setInt(1, id);
			rs = pstm.executeQuery();
			if (rs.first()) {
				a = new Articulo();

				a.setIdArticulo(rs.getInt("idarticulo"));
				a.setNumserie(rs.getString("numserie"));
				a.setEstado(rs.getString("estado"));
				a.setFechaalta(rs.getDate("fechaalta"));
				a.setFechabaja(rs.getDate("fechabaja"));
				a.setUsuarioalta(rs.getInt("usuarioalta"));
				a.setUsuariobaja(rs.getInt("usuariobaja"));
				a.setModelo(rs.getInt("modelo"));
				a.setDepartamento(rs.getInt("departamento"));
				a.setEspacio(rs.getInt("espacio"));
				a.setDentrode(rs.getInt("dentrode"));
				a.setObservaciones(rs.getString("observaciones"));
			}
			return a;
		} catch (SQLException e) {
			throw new BusinessException("Error al consultar");
		} finally {
			ConexionJdbc.cerrar(pstm);
		}
	}

	@Override
	public List<Articulo> buscarTodos() throws BusinessException {
		List<Articulo> result = new ArrayList<Articulo>();
		Connection con = ConexionJdbc.getConnection();
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT * FROM articulo ORDER BY idarticulo";
			pstm = con.prepareStatement(sql);
			rs = pstm.executeQuery();
			while (rs.next()) {
				Articulo a = new Articulo();

				a.setIdArticulo(rs.getInt("idarticulo"));
				a.setNumserie(rs.getString("numserie"));
				a.setEstado(rs.getString("estado"));
				a.setFechaalta(rs.getDate("fechaalta"));
				a.setFechabaja(rs.getDate("fechabaja"));
				a.setUsuarioalta(rs.getInt("usuarioalta"));
				a.setUsuariobaja(rs.getInt("usuariobaja"));
				a.setModelo(rs.getInt("modelo"));
				a.setDepartamento(rs.getInt("departamento"));
				a.setEspacio(rs.getInt("espacio"));
				// a.setDentrode(rs.getInt("dentrode"));
				a.setObservaciones(rs.getString("observaciones"));

				result.add(a);
			}
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			throw new BusinessException("Error al consultar");
		} finally {
			ConexionJdbc.cerrar(pstm);
		}
	}

}