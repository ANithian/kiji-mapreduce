/**
 * (c) Copyright 2013 WibiData, Inc.
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

package org.kiji.mapreduce.platform;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.TaskAttemptID;
import org.apache.hadoop.mapreduce.TaskType;

import org.kiji.annotations.ApiAudience;

/**
 * Hadoop 1.x-backed implementation of the KijiMRPlatformBridge API.
 */
@ApiAudience.Private
public final class Hadoop1xKijiMRBridge extends KijiMRPlatformBridge {

  /** {@inheritDoc} */
  @Override
  public TaskAttemptContext newTaskAttemptContext(Configuration conf, TaskAttemptID id) {
    // Create an instance of this class directly.
    return new TaskAttemptContext(conf, id);
  }

  /** {@inheritDoc} */
  @Override
  public TaskAttemptID newTaskAttemptID(String jtIdentifier, int jobId, TaskType type,
      int taskId, int id) {
    // In Hadoop 1.0, TaskType isn't an arg to TaskAttemptID; instead, there's just a
    // boolean indicating whether it's a map task or not.
    boolean isMap = type == TaskType.MAP;
    return new TaskAttemptID(jtIdentifier, jobId, isMap, taskId, id);
  }
}
