package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
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

	public boolean comprobarPrestamo(Salida salida) throws BusinessException {

	    Connection con = null;
	    PreparedStatement pstm = null;
	    ResultSet rs = null;

	    try {

	        con = ConexionJdbc.getConnection();

	        // 1️ Comprobar estado del artículo
	        String sql = "SELECT estado FROM Articulo WHERE idarticulo=?";
	        pstm = con.prepareStatement(sql);
	        pstm.setInt(1, salida.getArticulo());
	        rs = pstm.executeQuery();

	        if (!rs.next()) {
	            throw new BusinessException("El artículo no existe");
	        }

	        String estado = rs.getString("estado");

	        if (!estado.equalsIgnoreCase("operativo")) {
	            throw new BusinessException("El artículo debe estar operativo");
	        }

	        ConexionJdbc.cerrar(rs);
	        ConexionJdbc.cerrar(pstm);

	        // 2️ Comprobar si el artículo ya está prestado
	        sql = "SELECT * FROM Salida WHERE articulo=? AND fechadevolucion IS NULL";
	        pstm = con.prepareStatement(sql);
	        pstm.setInt(1, salida.getArticulo());
	        rs = pstm.executeQuery();

	        if (rs.next()) {
	            throw new BusinessException("El artículo ya está prestado");
	        }

	        ConexionJdbc.cerrar(rs);
	        ConexionJdbc.cerrar(pstm);

	        // 3️ Comprobar si el usuario ya tiene préstamo activo
	        sql = "SELECT * FROM Salida WHERE usuario=? AND fechadevolucion IS NULL";
	        pstm = con.prepareStatement(sql);
	        pstm.setInt(1, salida.getUsuario());
	        rs = pstm.executeQuery();

	        if (rs.next()) {
	            throw new BusinessException("El usuario ya tiene un préstamo activo");
	        }

	        return true;

	    } catch (SQLException e) {
	        throw new BusinessException("Error al comprobar préstamo");
	    }
	}
	
	public void insertarPrestamo(Salida salida) throws BusinessException {

	    Connection con = null;
	    PreparedStatement pstm = null;

	    try {

	        con = ConexionJdbc.getConnection();

	        if (comprobarPrestamo(salida)) {

	            String sql = "INSERT INTO Salida (usuario, articulo, fechasalida) VALUES (?, ?, ?)";

	            pstm = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

	            pstm.setInt(1, salida.getUsuario());
	            pstm.setInt(2, salida.getArticulo());
	            pstm.setTimestamp(3, Timestamp.valueOf(salida.getFechaSalida()));

	            int filas = pstm.executeUpdate();

	            if (filas == 0) {
	                throw new BusinessException("No se pudo insertar el préstamo");
	            }

	            ResultSet rs = pstm.getGeneratedKeys();

	            if (rs.next()) {
	                salida.setIdSalida(rs.getInt(1));
	            }

	            System.out.println("Préstamo insertado correctamente");
	        }

	    } catch (SQLException e) {
	        throw new BusinessException("Error al insertar préstamo");
	    }
	}
	
	public void devolverArticulo(Salida salida, LocalDateTime fechaDev) throws BusinessException{
		
		Connection con = null;
	    PreparedStatement pstm = null;
	    ResultSet rs = null;
	    
	    try {
			
	    	con = ConexionJdbc.getConnection();
	    	
	    	// Comprobar fecha devolución
	    	String sql = "SELECT s.fechasalida FROM Salida s WHERE s.idsalida=?";
	    	pstm = con.prepareStatement(sql);
	    	pstm.setInt(1, salida.getIdSalida());
	    	rs = pstm.executeQuery();
	    	
	    	if (!rs.next()) {
	    	    throw new BusinessException("La salida no existe");
	    	}
	    	
	    	Timestamp fechaSalidaTS = rs.getTimestamp("fechasalida");
	    	LocalDateTime fechaSalidaBD = fechaSalidaTS.toLocalDateTime();
	    	
	    	if (fechaDev.isBefore(fechaSalidaBD)) {
	    		throw new BusinessException("La fecha de devolción no puede ser inferior a la fecha de salida.");	
	    	}
	    	
	    	if (fechaDev.isBefore(fechaSalidaBD.plusDays(30))) {
	    		throw new BusinessException("Deben haber pasado minimo 30 días para poder devolver el artículo.");
	    	}
	    	ConexionJdbc.cerrar(rs);
	        ConexionJdbc.cerrar(pstm);
	        
	        sql = "UPDATE Salida SET fechadevolucion = ? WHERE idsalida=?";
	        pstm = con.prepareStatement(sql);
	        pstm.setTimestamp(1, Timestamp.valueOf(fechaDev));
	        pstm.setInt(2, salida.getIdSalida());
	        
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
