package com.chenxian.language_platform.rowmapper;

import com.chenxian.language_platform.model.Service;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ServiceRowMapper implements RowMapper<Service> {
    @Override
    public Service mapRow(ResultSet rs, int rowNum) throws SQLException {
        Service service = new Service();
        service.setServiceId(rs.getInt("serviceId"));
        service.setServiceLocation(rs.getString("serviceLocation"));
        service.setServiceName(rs.getString("serviceName"));
        service.setServiceUrl(rs.getString("serviceUrl"));
        return service;
    }
}
