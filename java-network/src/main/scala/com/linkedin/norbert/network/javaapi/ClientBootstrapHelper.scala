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
package com.linkedin.norbert.network.javaapi

import com.linkedin.norbert.network.NetworkClientFactoryComponent
import com.google.protobuf.Message
import com.linkedin.norbert.cluster.javaapi.{Cluster, JavaClusterHelper, JavaRouterHelper}
import java.util.concurrent.TimeUnit
import com.linkedin.norbert.cluster.{Node, ClusterComponent}

trait ClientBootstrapHelper {
  protected val componentRegistry: NetworkClientFactoryComponent with ClusterComponent with JavaRouterHelper

  private val cluster = new JavaClusterHelper {
    lazy val componentRegistry = ClientBootstrapHelper.this.componentRegistry
  }

  import componentRegistry.networkClientFactory

  def getNetworkClient: NetworkClient = new NetworkClient {
    private val client = networkClientFactory.newNetworkClient

    def isConnected = client.isConnected

    def sendMessage(ids: Array[Int], message: Message): ResponseIterator = new ResponseIteratorWrapper(client.sendMessage(ids, message))

    def sendMessage[A](ids: Array[Int], message: Message, scatterGather: ScatterGatherHandler[A]) = {
      client.sendMessage(ids, message, (m, n, r) => scatterGather.customizeMessage(m, n, r.toArray),
        (m, ri) => scatterGather.gatherResponses(m, new ResponseIteratorWrapper(ri)))
    }

    def sendMessageToNode(node: Node, message: Message): ResponseIterator = new ResponseIteratorWrapper(client.sendMessageToNode(node, message))

    def close = client.close

    private class ResponseIteratorWrapper(it: com.linkedin.norbert.network.ResponseIterator) extends ResponseIterator {
      def next(timeout: Long, unit: TimeUnit) = next(it.next(timeout, unit))

      def next = next(it.next)

      def hasNext = it.hasNext

      private def next(block: => Option[Either[Throwable, Message]]): Response = block match {
        case Some(either) => new MessageResponse(either)
        case None => null
      }

      private class MessageResponse(either: Either[Throwable, Message]) extends Response {
        def getMessage = either.right.getOrElse(null)

        def isSuccess = either.isRight

        def getCause = either.left.getOrElse(null)
      }
    }
  }

  def getCluster: Cluster = cluster

  def shutdown: Unit = networkClientFactory.shutdown
}
