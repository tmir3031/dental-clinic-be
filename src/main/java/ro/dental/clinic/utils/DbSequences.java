package ro.dental.clinic.utils;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.SQLException;

@RequiredArgsConstructor
@Slf4j
public class DbSequences {

  public static final String SPECIALIZATION_ID_SQ = "SPECIALIZATION_ID_SQ";
  public static final String TREATMENT_ID_SQ = "TREATMENT_ID_SQ";
  public static final String APPOINTMENT_ID_SQ = "APPOINTMENT_ID_SQ";

  private final DataSource dataSource;

  @SneakyThrows
  public void setSequenceNextVal(String sequenceName, Integer nextVal) {
    try (var statement = dataSource.getConnection().createStatement()) {
      try {
        statement.execute("drop sequence " + sequenceName);
      } catch (SQLException e) {
        log.debug("Could not drop sequence");
      }
      statement.execute("create sequence " + sequenceName + " start with " + nextVal);
    }
  }
}
