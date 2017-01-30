package org.kduda.greedy.unit.algorithm.rm;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kduda.greedy.algorithm.DecisionTableFactory;
import org.kduda.greedy.algorithm.rm.HeuristicsRM;
import org.kduda.greedy.spark.reader.csv.SparkCsvReader;
import org.kduda.greedy.unit.SpringUnitTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import scala.Tuple2;
import scala.collection.immutable.List;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class HeuristicsRMTest extends SpringUnitTest {

	@Autowired private SparkCsvReader sparkCsvReader;

	private scala.collection.immutable.Map<String, Dataset<Row>> dtsMapped;

	@SuppressWarnings("Duplicates")
	@Before
	public void setUp() throws IOException {
		ClassPathResource resource = new ClassPathResource("/files/paper-sample.csv", getClass());
		File file = new File(resource.getURL().getPath());

		Map<String, String> options = new HashMap<>();
		options.put("header", "true");

		Dataset<Row> is = sparkCsvReader.read(file, options);
		Dataset<Row>[] dtsInconsistent = DecisionTableFactory.extractDecisionTables(is);
		Dataset<Row>[] consistent = DecisionTableFactory.removeInconsistenciesMCD(dtsInconsistent);
		dtsMapped = DecisionTableFactory.createMapOf(consistent);
	}

	@After
	public void tearDown() {
		dtsMapped = null;
	}

	@Test
	public void shouldReturnNonNullValue() {
		scala.collection.immutable.Map<String, List<List<Tuple2<String, String>>>> result = HeuristicsRM.calculateDecisionRules(dtsMapped);

		assertThat(result).isNotNull();
	}
}