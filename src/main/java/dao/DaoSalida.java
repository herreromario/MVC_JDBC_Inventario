package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pojos.Articulo;
import pojos.Rol;
import pojos.Salida;
import excepciones.BusinessException;
import jdbc.ConexionJdbc;

public class DaoSalida extends DaoGenerico<Salida, Integer> {

	@Override
	public void grabar(Salida s) throws BusinessException {
		Connection con = ConexionJdbc.getConnection();
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {

			// Preparar para la inserción
			String sql = "INSERT INTO salida " + "(usuario,articulo,fechasalida) " + "VALUES (?,?,?)";

			pstm = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
			pstm.setInt(1, s.getUsuario());
			pstm.setInt(2, s.getArticulo());
			pstm.setTimestamp(3, java.sql.Timestamp.valueOf(s.getFechaSalida()));

			// La fecha de devolución se actualiza al devolver el artículo
			// pstm.setTimestamp(4,java.sql.Timestamp.valueOf(o.getFechaDevolucion()));

			// Insertar
			pstm.executeUpdate();

			// Obtener clave generada
			rs = pstm.getGeneratedKeys();
			if (rs.first()) {
				Integer id = rs.getInt(1);
				s.setIdSalida(id);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw new BusinessException("Error al insertar");
		} finally {
			ConexionJdbc.cerrar(pstm);
		}
	}

	@Override
	public void actualizar(Salida o) throws BusinessException {
		Connection con = ConexionJdbc.getConnection();
		PreparedStatement pstm = null;
		try {
			// Preparar la actualización.
			String sql = "UPDATE salida" + " SET  usuario = ?, articulo = ? , fechasalida = ? , fechadevolucion = ? "
					+ " WHERE idsalida = ?";

			pstm = con.prepareStatement(sql);
			pstm.setInt(1, o.getUsuario());
			pstm.setInt(2, o.getArticulo());
			pstm.setTimestamp(3, java.sql.Timestamp.valueOf(o.getFechaSalida()));
			pstm.setTimestamp(4, java.sql.Timestamp.valueOf(o.getFechaDevolucion()));
			pstm.setInt(5, o.getIdSalida());

			// Ejecutar la actualización
			int actualizados = pstm.executeUpdate();
			if (actualizados == 0)
				throw new BusinessException("La salida a modificar no existe");

		} catch (SQLException e) {
			throw new BusinessException("Error al actualizar");
		} finally {
			ConexionJdbc.cerrar(pstm);
		}
	}

	@Override
	public List<Salida> buscarTodos() throws BusinessException {
		List<Salida> result = new ArrayList<Salida>();
		Connection con = ConexionJdbc.getConnection();
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT * FROM salida";
			pstm = con.prepareStatement(sql);
			rs = pstm.executeQuery();

			while (rs.next()) {
				Salida s = new Salida();
				s.setIdSalida(rs.getInt(1));
				s.setUsuario(rs.getInt(2));
				LocalDateTime fechaSal = rs.getTimestamp("fechaSalida").toLocalDateTime();
				s.setFechaSalida(fechaSal);

				result.add(s);
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
