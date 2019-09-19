package io.github.y0ngb1n.samples.multidatasource;

import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author yangbin
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class MultiDataSourceApplicationTests {

  @Autowired
  private DataSource fooDataSource;
  @Autowired
  private DataSource barDataSource;

  @Test
  public void testFooDataSource() throws SQLException {
    log.info("foo datasource runtime: {}", fooDataSource.getConnection());
    assert null != fooDataSource.getConnection();
    assert fooDataSource.getConnection().toString().contains("jdbc:h2:mem:foo");
  }

  @Test
  public void testBarDataSource() throws SQLException {
    log.info("bar datasource runtime: {}", barDataSource.getConnection());
    assert null != barDataSource.getConnection();
    assert barDataSource.getConnection().toString().contains("jdbc:h2:mem:bar");
  }
}
