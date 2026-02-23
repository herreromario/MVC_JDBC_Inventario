package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import excepciones.BusinessException;
import jdbc.ConexionJdbc;
import pojos.Departamento;

public class DaoDepartamento extends DaoGenerico<Departamento, Integer> {
	
	public Departamento buscarPorNombre(String nombreDep) throws BusinessException {
		
		Departamento dep = null;
		
		Connection con = ConexionJdbc.getConnection();
		PreparedStatement pstm = null;
		ResultSet rs = null;
		
		try {
			
			String sql = "select * from departamento where nombre=?";
			pstm = con.prepareStatement(sql);
			pstm.setString(1, nombreDep);
			
			rs = pstm.executeQuery();
			
			if (rs.first()) {
				
				dep = new Departamento();
				
				dep.setIddepartamento(rs.getInt("iddepartamento"));
				dep.setNombre(rs.getString("nombre"));
			}	
			
		} catch (SQLException e) {
			throw new BusinessException("Error al consultar");
			
		} finally {
			ConexionJdbc.cerrar(pstm);
		}
		
		return dep;
	}

}
