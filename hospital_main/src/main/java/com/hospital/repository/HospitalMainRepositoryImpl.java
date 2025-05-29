package com.hospital.repository;

import com.hospital.entity.Hospital; // HospitalApiEntity 대신 Hospital 엔티티 임포트
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper; // findAllHospitals를 위한 RowMapper 임포트
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional; // findById를 위한 Optional 임포트 (필요하다면)

// 인터페이스도 HospitalApiRepository가 아닌 HospitalRepository로 변경 가정
@Repository // @Repository 어노테이션 유지
public class HospitalMainRepositoryImpl implements HospitalMainRepository { // 인터페이스 이름 변경 필요

	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public HospitalMainRepositoryImpl(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

    // RowMapper는 HospitalRepository 클래스 내부에 필드로 두는 것이 재사용에 유리합니다.
    private RowMapper<Hospital> hospitalRowMapper = new RowMapper<Hospital>() {
        @Override
        public Hospital mapRow(ResultSet rs, int rowNum) throws SQLException {
            Hospital hospital = new Hospital();
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
        }
    };

	@Override
	public void createHospitalTable() {
		String sql = "CREATE TABLE IF NOT EXISTS hospital_main (" + "hospital_code VARCHAR(255) PRIMARY KEY,"
				+ "hospital_name VARCHAR(255)," + "province_name VARCHAR(100)," + "district_name VARCHAR(100),"
				+ "hospital_address VARCHAR(500)," + "hospital_tel VARCHAR(50)," + "hospital_homepage VARCHAR(255),"
				+ "doctor_num INT," + "coordinate_x DOUBLE," + "coordinate_y DOUBLE" + ")";
		jdbcTemplate.execute(sql);
	}

	@Override
	public int[] insertHospitals(List<Hospital> hospitals) { // HospitalApiEntity -> Hospital
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
				Hospital hospital = hospitals.get(i); // HospitalApiEntity -> Hospital
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
	public List<Hospital> findAllHospitals() { // HospitalApiEntity -> Hospital
		String sql = "SELECT hospital_code, hospital_name, province_name, district_name, hospital_address, " +
                     "hospital_tel, hospital_homepage, doctor_num, coordinate_x, coordinate_y FROM hospital_main";
		return jdbcTemplate.query(sql, hospitalRowMapper); // 미리 정의된 RowMapper 사용
	}

    // 필요하다면 findById 등 다른 CRUD 메서드도 추가할 수 있습니다.
    public Optional<Hospital> findById(String hospitalCode) {
        String sql = "SELECT hospital_code, hospital_name, province_name, district_name, hospital_address, " +
                     "hospital_tel, hospital_homepage, doctor_num, coordinate_x, coordinate_y " +
                     "FROM hospital_main WHERE hospital_code = ?";
        List<Hospital> hospitals = jdbcTemplate.query(sql, hospitalRowMapper, hospitalCode);
        return hospitals.stream().findFirst();
    }
}