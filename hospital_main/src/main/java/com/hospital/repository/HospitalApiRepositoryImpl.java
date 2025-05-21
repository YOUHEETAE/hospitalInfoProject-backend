package com.hospital.repository;

import com.hospital.repository.HospitalApiRepository;
import com.hospital.entity.HospitalApiEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
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
		String sql = "CREATE TABLE IF NOT EXISTS hospitals (" + "id BIGINT AUTO_INCREMENT PRIMARY KEY," + // BIGINT는
																											// Long 타입에
																											// 적합
				"address VARCHAR(500)," + "clinic_code VARCHAR(10)," + "clinic_code_name VARCHAR(100),"
				+ "emdong_name VARCHAR(100)," + "establish_date VARCHAR(8)," + // YYYYMMDD 형식
				"hosp_name VARCHAR(255) NOT NULL," + // 병원명은 필수 값일 가능성이 높으므로 NOT NULL
				"hospital_url VARCHAR(500)," + "post_no VARCHAR(10)," + "sigungu_code VARCHAR(10),"
				+ "sigungu_code_name VARCHAR(100)," + "sido_code VARCHAR(10)," + "sido_code_name VARCHAR(100),"
				+ "tel_no VARCHAR(50)," + "x_pos DOUBLE," + // 경도
				"y_pos DOUBLE," + // 위도
				"yadm_name VARCHAR(255)," + "ykiho VARCHAR(255)," + "cmdc_gdr_count INT," + "cmdc_intn_count INT,"
				+ "cmdc_resdnt_count INT," + "cmdc_sdr_count INT," + "dety_gdr_count INT," + "dety_intn_count INT,"
				+ "dety_resdnt_count INT," + "dety_sdr_count INT," + "dr_tot_count INT," + "mdept_gdr_count INT,"
				+ "mdept_intn_count INT," + "mdept_resdnt_count INT," + "mdept_sdr_count INT," + "pnurs_count INT"
				+ ");";
		jdbcTemplate.execute(sql);
	}

	@Override
	public int[] insertHospitals(List<HospitalApiEntity> hospitals) {
		// SQL 쿼리를 INSERT ... ON DUPLICATE KEY UPDATE 문으로 변경
		String sql = "INSERT INTO hospitals (" + "address, clinic_code, clinic_code_name, emdong_name, establish_date, "
				+ "hosp_name, hospital_url, post_no, sigungu_code, sigungu_code_name, "
				+ "sido_code, sido_code_name, tel_no, x_pos, y_pos, yadm_name, ykiho, "
				+ "cmdc_gdr_count, cmdc_intn_count, cmdc_resdnt_count, cmdc_sdr_count, "
				+ "dety_gdr_count, dety_intn_count, dety_resdnt_count, dety_sdr_count, "
				+ "dr_tot_count, mdept_gdr_count, mdept_intn_count, mdept_resdnt_count, mdept_sdr_count, pnurs_count) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
				+ "ON DUPLICATE KEY UPDATE " + 
				"address = VALUES(address), " + "clinic_code = VALUES(clinic_code), "
				+ "clinic_code_name = VALUES(clinic_code_name), " + "emdong_name = VALUES(emdong_name), "
				+ "establish_date = VALUES(establish_date), " + "hosp_name = VALUES(hosp_name), "
				+ "hospital_url = VALUES(hospital_url), " + "post_no = VALUES(post_no), "
				+ "sigungu_code = VALUES(sigungu_code), " + "sigungu_code_name = VALUES(sigungu_code_name), "
				+ "sido_code = VALUES(sido_code), " + "sido_code_name = VALUES(sido_code_name), "
				+ "tel_no = VALUES(tel_no), " + "x_pos = VALUES(x_pos), " + "y_pos = VALUES(y_pos), "
				+ "yadm_name = VALUES(yadm_name), " + "cmdc_gdr_count = VALUES(cmdc_gdr_count), "
				+ "cmdc_intn_count = VALUES(cmdc_intn_count), " + "cmdc_resdnt_count = VALUES(cmdc_resdnt_count), "
				+ "cmdc_sdr_count = VALUES(cmdc_sdr_count), " + "dety_gdr_count = VALUES(dety_gdr_count), "
				+ "dety_intn_count = VALUES(dety_intn_count), " + "dety_resdnt_count = VALUES(dety_resdnt_count), "
				+ "dety_sdr_count = VALUES(dety_sdr_count), " + "dr_tot_count = VALUES(dr_tot_count), "
				+ "mdept_gdr_count = VALUES(mdept_gdr_count), " + "mdept_intn_count = VALUES(mdept_intn_count), "
				+ "mdept_resdnt_count = VALUES(mdept_resdnt_count), " + "mdept_sdr_count = VALUES(mdept_sdr_count), "
				+ "pnurs_count = VALUES(pnurs_count)";

		// JdbcTemplate의 batchUpdate 메서드를 사용하여 배치 삽입을 수행합니다.
		return jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				HospitalApiEntity hospital = hospitals.get(i);

				// 1. String 필드들
				ps.setString(1, hospital.getAddress());
				ps.setString(2, hospital.getClinicCode());
				ps.setString(3, hospital.getClinicCodeName());
				ps.setString(4, hospital.getEmdongName());
				ps.setString(5, hospital.getEstablishDate());
				ps.setString(6, hospital.getYadmName());
				ps.setString(7, hospital.getHospitalUrl());
				ps.setString(8, hospital.getPostNo());
				ps.setString(9, hospital.getSigunguCode());
				ps.setString(10, hospital.getSigunguCodeName());
				ps.setString(11, hospital.getSidoCode());
				ps.setString(12, hospital.getSidoCodeName());
				ps.setString(13, hospital.getTelNo());

				// 2. Double 필드들 (null 체크 후 setNull 사용)
				if (hospital.getXPos() != null) {
					ps.setDouble(14, hospital.getXPos());
				} else {
					ps.setNull(14, Types.DOUBLE);
				}
				if (hospital.getYPos() != null) {
					ps.setDouble(15, hospital.getYPos());
				} else {
					ps.setNull(15, Types.DOUBLE);
				}

				// 3. 나머지 String 필드들
				ps.setString(16, hospital.getYadmName());
				ps.setString(17, hospital.getYkiho());

				// 4. Integer 필드들 (null 체크 후 setNull 사용)
				if (hospital.getCmdcGdrCount() != null) {
					ps.setInt(18, hospital.getCmdcGdrCount());
				} else {
					ps.setNull(18, Types.INTEGER);
				}
				if (hospital.getCmdcIntnCount() != null) {
					ps.setInt(19, hospital.getCmdcIntnCount());
				} else {
					ps.setNull(19, Types.INTEGER);
				}
				if (hospital.getCmdcResdntCount() != null) {
					ps.setInt(20, hospital.getCmdcResdntCount());
				} else {
					ps.setNull(20, Types.INTEGER);
				}
				if (hospital.getCmdcSdrCount() != null) {
					ps.setInt(21, hospital.getCmdcSdrCount());
				} else {
					ps.setNull(21, Types.INTEGER);
				}
				if (hospital.getDetyGdrCount() != null) {
					ps.setInt(22, hospital.getDetyGdrCount());
				} else {
					ps.setNull(22, Types.INTEGER);
				}
				if (hospital.getDetyIntnCount() != null) {
					ps.setInt(23, hospital.getDetyIntnCount());
				} else {
					ps.setNull(23, Types.INTEGER);
				}
				if (hospital.getDetyResdntCount() != null) {
					ps.setInt(24, hospital.getDetyResdntCount());
				} else {
					ps.setNull(24, Types.INTEGER);
				}
				if (hospital.getDetySdrCount() != null) {
					ps.setInt(25, hospital.getDetySdrCount());
				} else {
					ps.setNull(25, Types.INTEGER);
				}
				if (hospital.getDrTotCount() != null) {
					ps.setInt(26, hospital.getDrTotCount());
				} else {
					ps.setNull(26, Types.INTEGER);
				}
				if (hospital.getMdeptGdrCount() != null) {
					ps.setInt(27, hospital.getMdeptGdrCount());
				} else {
					ps.setNull(27, Types.INTEGER);
				}
				if (hospital.getMdeptIntnCount() != null) {
					ps.setInt(28, hospital.getMdeptIntnCount());
				} else {
					ps.setNull(28, Types.INTEGER);
				}
				if (hospital.getMdeptResdntCount() != null) {
					ps.setInt(29, hospital.getMdeptResdntCount());
				} else {
					ps.setNull(29, Types.INTEGER);
				}
				if (hospital.getMdeptSdrCount() != null) {
					ps.setInt(30, hospital.getMdeptSdrCount());
				} else {
					ps.setNull(30, Types.INTEGER);
				}
				if (hospital.getPnursCount() != null) {
					ps.setInt(31, hospital.getPnursCount());
				} else {
					ps.setNull(31, Types.INTEGER);
				}
			}

			@Override
			public int getBatchSize() {
				return hospitals.size();
			}
		});
	}

	@Override
	public List<HospitalApiEntity> findAllHospitals() {
		String sql = "SELECT * FROM hospitals";
		return jdbcTemplate.query(sql, (rs, rowNum) -> {
			HospitalApiEntity hospital = new HospitalApiEntity();
			hospital.setId(rs.getLong("id"));
			hospital.setAddress(rs.getString("address")); // Entity의 setAddress() 사용
			hospital.setClinicCode(rs.getString("clinic_code")); // Entity의 setClinicCode() 사용
			hospital.setClinicCodeName(rs.getString("clinic_code_name")); // Entity의 setClinicCodeName() 사용
			hospital.setEmdongName(rs.getString("emdong_name")); // Entity의 setEmdongName() 사용
			hospital.setEstablishDate(rs.getString("establish_date")); // Entity의 setEstablishDate() 사용
			hospital.setHospName(rs.getString("hosp_name")); // Entity의 setHospName() 사용
			hospital.setHospitalUrl(rs.getString("hospital_url")); // Entity의 setHospitalUrl() 사용
			hospital.setPostNo(rs.getString("post_no")); // Entity의 setPostNo() 사용
			hospital.setSigunguCode(rs.getString("sigungu_code")); // Entity의 setSigunguCode() 사용
			hospital.setSigunguCodeName(rs.getString("sigungu_code_name")); // Entity의 setSigunguCodeName() 사용
			hospital.setSidoCode(rs.getString("sido_code")); // Entity의 setSidoCode() 사용
			hospital.setSidoCodeName(rs.getString("sido_code_name")); // Entity의 setSidoCodeName() 사용
			hospital.setTelNo(rs.getString("tel_no")); // Entity의 setTelNo() 사용

			// Double 타입 필드들
			hospital.setXPos(rs.getObject("x_pos", Double.class));
			hospital.setYPos(rs.getObject("y_pos", Double.class));

			hospital.setYadmName(rs.getString("yadm_name")); // Entity의 setYadmName() 사용
			hospital.setYkiho(rs.getString("ykiho")); // Entity의 setYkiho() 사용

			// Integer 타입의 숫자 필드들
			hospital.setCmdcGdrCount(rs.getObject("cmdc_gdr_count", Integer.class));
			hospital.setCmdcIntnCount(rs.getObject("cmdc_intn_count", Integer.class));
			hospital.setCmdcResdntCount(rs.getObject("cmdc_resdnt_count", Integer.class));
			hospital.setCmdcSdrCount(rs.getObject("cmdc_sdr_count", Integer.class));
			hospital.setDetyGdrCount(rs.getObject("dety_gdr_count", Integer.class));
			hospital.setDetyIntnCount(rs.getObject("dety_intn_count", Integer.class));
			hospital.setDetyResdntCount(rs.getObject("dety_resdnt_count", Integer.class));
			hospital.setDetySdrCount(rs.getObject("dety_sdr_count", Integer.class));
			hospital.setDrTotCount(rs.getObject("dr_tot_count", Integer.class));
			hospital.setMdeptGdrCount(rs.getObject("mdept_gdr_count", Integer.class));
			hospital.setMdeptIntnCount(rs.getObject("mdept_intn_count", Integer.class));
			hospital.setMdeptResdntCount(rs.getObject("mdept_resdnt_count", Integer.class));
			hospital.setMdeptSdrCount(rs.getObject("mdept_sdr_count", Integer.class));
			hospital.setPnursCount(rs.getObject("pnurs_count", Integer.class));
			return hospital;
		});
	}
}