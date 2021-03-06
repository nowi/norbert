/*
 * Copyright 2009-2010 LinkedIn, Inc
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
package com.linkedin.norbert.cluster

/**
 * A container for constants used in the <code>Cluster</code>.
 */
object ClusterDefaults {
  /**
   * The default ZooKeeper session timeout in milliseconds.
   */
  val ZOOKEEPER_SESSION_TIMEOUT = 30000

  /**
   * The default cluster disconnect timeout in milliseconds.
   */
  val CLUSTER_DISCONNECT_TIMEOUT = 30000
}
