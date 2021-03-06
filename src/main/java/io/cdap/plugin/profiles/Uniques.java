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
import com.clearspring.analytics.stream.cardinality.HyperLogLog;

import java.util.Arrays;
import java.util.List;

/**
 * This class <code>Uniques</code> profiler profiles String types using Hyper Log Log to determine
 * the cardinality.
 */
public final class Uniques extends Profile {
  private HyperLogLog hpp;

  public Uniques() {
    super("uniques");
  }

  @Override
  public List<Schema.Type> types() {
    return Arrays.asList(
      Schema.Type.STRING
    );
  }

  @Override
  public List<Schema.Field> fields() {
    return Arrays.asList(
      Schema.Field.of("value", Schema.of(Schema.Type.LONG))
    );
  }

  @Override
  public void reset() {
    hpp = new HyperLogLog(0.1f);
  }

  @Override
  public void update(Object value) {
    if (value instanceof String) {
      hpp.offer(value);
    }
  }

  @Override
  public void results(StructuredRecord.Builder builder) {
    builder.set("value", hpp.cardinality());
  }
}
