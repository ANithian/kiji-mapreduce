/**
 * (c) Copyright 2012 WibiData, Inc.
 *
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kiji.mapreduce.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import org.kiji.mapreduce.produce.KijiProducer;
import org.kiji.mapreduce.produce.KijiProducerOutputException;
import org.kiji.mapreduce.produce.ProducerContext;
import org.kiji.mapreduce.produce.impl.KijiProducers;
import org.kiji.schema.KijiClientTest;
import org.kiji.schema.KijiDataRequest;
import org.kiji.schema.KijiRowData;
import org.kiji.schema.layout.InvalidLayoutException;
import org.kiji.schema.layout.KijiTableLayout;
import org.kiji.schema.layout.KijiTableLayouts;

public class TestKijiProducers extends KijiClientTest {
  private KijiTableLayout mTableLayout;

  @Before
  public void setupLayout() throws Exception {
    mTableLayout = KijiTableLayouts.getTableLayout(KijiTableLayouts.SIMPLE);
    getKiji().createTable(mTableLayout.getName(), mTableLayout);
  }

  public static class MyProducer extends KijiProducer {
    @Override
    public KijiDataRequest getDataRequest() {
      return null;
    }

    @Override
    public String getOutputColumn() {
      return "family:column";
    }

    @Override
    public void produce(KijiRowData input, ProducerContext context) {
      // meh.
    }
  }

  @Test
  public void testValidateOutputColumn()
      throws InvalidLayoutException, KijiProducerOutputException {
    MyProducer producer = new MyProducer();
    KijiProducers.validateOutputColumn(producer, mTableLayout);
  }

  @Test
  public void testValidateOutputColumnNonExistentFamily()
      throws InvalidLayoutException, KijiProducerOutputException {
    MyProducer producer = new MyProducer() {
      @Override
      public String getOutputColumn() {
        return "doesnt_exist:column";
      }
    };

    try {
      KijiProducers.validateOutputColumn(producer, mTableLayout);
      fail("Should have gotten a KijiProducerOutputException.");
    } catch (KijiProducerOutputException ke) {
      assertEquals("Producer 'org.kiji.mapreduce.util.TestKijiProducers$1' specifies "
          + "unknown output column family 'doesnt_exist' in table 'table'.",
          ke.getMessage());
    }
  }

  @Test
  public void testValidateOutputColumnNonExistentColumn()
      throws InvalidLayoutException, KijiProducerOutputException {
    MyProducer producer = new MyProducer() {
      @Override
      public String getOutputColumn() {
        return "family:doesnt_exist";
      }
    };

    try {
      KijiProducers.validateOutputColumn(producer, mTableLayout);
      fail("Should have gotten a KijiProducerOutputException.");
    } catch (KijiProducerOutputException ke) {
      assertEquals("Producer 'org.kiji.mapreduce.util.TestKijiProducers$2' specifies "
          + "unknown column 'family:doesnt_exist' in table 'table'.",
          ke.getMessage());
    }
 }

  /**
   * Not specifying a qualifier even to a group type is allowed so that you can write to
   * multiple groups in a column.
   */
  @Test
  public void testValidateOutputColumnNoQualifierToGroupType()
      throws InvalidLayoutException, KijiProducerOutputException {
    MyProducer producer = new MyProducer() {
      @Override
      public String getOutputColumn() {
        return "family";
      }
    };
    // This should validate just fine.
    KijiProducers.validateOutputColumn(producer, mTableLayout);
  }
}
