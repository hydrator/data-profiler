/*
 * Copyright © 2018-2019 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.cdap.plugin.profiles;

import io.cdap.cdap.api.data.format.StructuredRecord;
import io.cdap.cdap.api.data.schema.Schema;
import io.cdap.plugin.Profile;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.Arrays;
import java.util.List;

/**
 * This class <code>Quantitative</code> profiler profiles numbers to determine
 * minimum, maximum, mean, total, standard devication, median, skewness, kurtosis,
 * population variance, percentiles (80,95,99), geometric mean and quadratic mean.
 */
public final class Quantitative extends Profile {
  private DescriptiveStatistics statistics;

  public Quantitative() {
    super("quantitative");
    statistics = new DescriptiveStatistics();
  }

  @Override
  public List<Schema.Type> types() {
    return Arrays.asList(
      Schema.Type.INT,
      Schema.Type.LONG,
      Schema.Type.DOUBLE,
      Schema.Type.FLOAT
    );
  }

  @Override
  public List<Schema.Field> fields() {
    return Arrays.asList(
      Schema.Field.of("minimum", Schema.of(Schema.Type.DOUBLE)),
      Schema.Field.of("maximum", Schema.of(Schema.Type.DOUBLE)),
      Schema.Field.of("mean", Schema.of(Schema.Type.DOUBLE)),
      Schema.Field.of("total", Schema.of(Schema.Type.DOUBLE)),
      Schema.Field.of("stdev", Schema.of(Schema.Type.DOUBLE)),
      Schema.Field.of("median", Schema.of(Schema.Type.DOUBLE)),
      Schema.Field.of("skewness", Schema.of(Schema.Type.DOUBLE)),
      Schema.Field.of("kurtosis", Schema.of(Schema.Type.DOUBLE)),
      Schema.Field.of("population_variance", Schema.of(Schema.Type.DOUBLE)),
      Schema.Field.of("percentile_80", Schema.of(Schema.Type.DOUBLE)),
      Schema.Field.of("percentile_95", Schema.of(Schema.Type.DOUBLE)),
      Schema.Field.of("percentile_99", Schema.of(Schema.Type.DOUBLE)),
      Schema.Field.of("geometric_mean", Schema.of(Schema.Type.DOUBLE)),
      Schema.Field.of("quadratic_mean", Schema.of(Schema.Type.DOUBLE))
    );
  }

  @Override
  public void reset() {
    statistics.clear();
  }

  @Override
  public void update(Object value) {
    if (value != null) {
      double val = 0;
      if (value instanceof Integer) {
        val = Double.valueOf((Integer) value).doubleValue();
      } else if (value instanceof Long) {
        val = Double.valueOf((Long) value).doubleValue();
      } else if (value instanceof Float) {
        val = Double.valueOf((Float) value).doubleValue();
      } else if (value instanceof Double) {
        val = (Double) value;
      }
      statistics.addValue(val);
    }
  }

  @Override
  public void results(StructuredRecord.Builder builder) {
    builder.set("maximum", V(statistics.getMax()));
    builder.set("minimum", V(statistics.getMin()));
    builder.set("mean", V(statistics.getMean()));
    builder.set("stdev", V(statistics.getStandardDeviation()));
    builder.set("median", V(statistics.getPercentile(50)));
    builder.set("percentile_80", V(statistics.getPercentile(80)));
    builder.set("percentile_95", V(statistics.getPercentile(95)));
    builder.set("percentile_99", V(statistics.getPercentile(99)));
    builder.set("geometric_mean", V(statistics.getGeometricMean()));
    builder.set("skewness", V(statistics.getSkewness()));
    builder.set("kurtosis", V(statistics.getKurtosis()));
    builder.set("population_variance", V(statistics.getPopulationVariance()));
    builder.set("quadratic_mean", V(statistics.getQuadraticMean()));
    builder.set("total", V(statistics.getSum()));
  }
}
