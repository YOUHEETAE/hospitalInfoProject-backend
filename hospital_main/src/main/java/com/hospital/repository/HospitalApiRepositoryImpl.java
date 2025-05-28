package com.hospital.repository;

import com.hospital.entity.HospitalApiEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class HospitalApiRepositoryImpl implements HospitalApiRepository {

	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public HospitalApiRepositoryImpl(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public void createHospitalTable() {
		String sql = "CREATE TABLE IF NOT EXISTS hospital_main (" + "hospital_code VARCHAR(255) PRIMARY KEY,"
				+ "hospital_name VARCHAR(255)," + "province_name VARCHAR(100)," + "district_name VARCHAR(100),"
				+ "hospital_address VARCHAR(500)," + "hospital_tel VARCHAR(50)," + "hospital_homepage VARCHAR(255),"
				+ "doctor_num INT," + "coordinate_x DOUBLE," + "coordinate_y DOUBLE" + ")";
		jdbcTemplate.execute(sql);
	}

	@Override
	public int[] insertHospitals(List<HospitalApiEntity> hospitals) {
		String sql = "INSERT INTO hospital_main ("
				+ "hospital_code, hospital_name, province_name, district_name, hospital_address, "
				+ "hospital_tel, hospital_homepage, doctor_num, coordinate_x, coordinate_y) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " + "ON DUPLICATE KEY UPDATE "
				+ "hospital_name = VALUES(hospital_name), " + "province_name = VALUES(province_name), "
				+ "district_name = VALUES(district_name), " + "hospital_address = VALUES(hospital_address), "
				+ "hospital_tel = VALUES(hospital_tel), " + "hospital_homepage = VALUES(hospital_homepage), "
				+ "doctor_num = VALUES(doctor_num), " + "coordinate_x = VALUES(coordinate_x), "
				+ "coordinate_y = VALUES(coordinate_y)";
		return jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				HospitalApiEntity hospital = hospitals.get(i);
				ps.setString(1, hospital.getHospitalCode());
				ps.setString(2, hospital.getHospitalName());
				ps.setString(3, hospital.getProvinceName());
				ps.setString(4, hospital.getDistrictName());
				ps.setString(5, hospital.getHospitalAddress());
				ps.setString(6, hospital.getHospitalTel());
				ps.setString(7, hospital.getHospitalHomepage());
				ps.setInt(8, hospital.getDoctorNum());
				ps.setDouble(9, hospital.getCoordinateX());
				ps.setDouble(10, hospital.getCoordinateY());
			}

			@Override
			public int getBatchSize() {
				return hospitals.size();
			}
		});
	}

	@Override
	public List<HospitalApiEntity> findAllHospitals() {
		String sql = "SELECT * FROM hospital_main";
		return jdbcTemplate.query(sql, (rs, rowNum) -> {
			HospitalApiEntity hospital = new HospitalApiEntity();
			hospital.setHospitalCode(rs.getString("hospital_code"));
			hospital.setHospitalName(rs.getString("hospital_name"));
			hospital.setProvinceName(rs.getString("province_name"));
			hospital.setDistrictName(rs.getString("district_name"));
			hospital.setHospitalAddress(rs.getString("hospital_address"));
			hospital.setHospitalTel(rs.getString("hospital_tel"));
			hospital.setHospitalHomepage(rs.getString("hospital_homepage"));
			hospital.setDoctorNum(rs.getInt("doctor_num"));
			hospital.setCoordinateX(rs.getDouble("coordinate_x"));
			hospital.setCoordinateY(rs.getDouble("coordinate_y"));
			return hospital;
		});
	}
}
