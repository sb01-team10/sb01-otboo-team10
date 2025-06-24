package com.codeit.weatherwear.global.config;

import org.hibernate.boot.model.TypeContributions;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;
import org.hibernate.type.descriptor.jdbc.spi.JdbcTypeRegistry;

public class NamedEnumAwareH2Dialect extends H2Dialect {

  @Override
  protected void registerColumnTypes(TypeContributions typeContributions,
      ServiceRegistry serviceRegistry) {
    super.registerColumnTypes(typeContributions, serviceRegistry);
    JdbcTypeRegistry registry = typeContributions.getTypeConfiguration().getJdbcTypeRegistry();
    registry.addDescriptor(SqlTypes.NAMED_ENUM, VarcharJdbcType.INSTANCE); // NAMED_ENUM → VARCHAR 매핑
  }
}
