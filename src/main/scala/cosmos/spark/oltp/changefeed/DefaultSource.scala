/*
  The MIT License (MIT)
  Copyright (c) 2016 Microsoft Corporation
  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  <p>The above copyright notice and this permission notice shall be included in all
  copies or substantial portions of the Software.

  <p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  SOFTWARE.
 */

package com.microsoft.azure.cosmos.spark.oltp.changefeed

import java.util

import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.connector.catalog.{SupportsRead, Table, TableCapability, TableProvider}
import org.apache.spark.sql.connector.expressions.Transform
import org.apache.spark.sql.connector.read.{Batch, InputPartition, PartitionReader, PartitionReaderFactory, Scan, ScanBuilder}
import org.apache.spark.sql.sources.DataSourceRegister
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.sql.util.CaseInsensitiveStringMap

import scala.collection.JavaConverters._

final class DefaultSource extends TableProvider with DataSourceRegister {
  def inferSchema(options: CaseInsensitiveStringMap): StructType = {
    CosmosItemToRowConverter.cosmosDbStreamSchema
  }

  override def getTable(options: CaseInsensitiveStringMap): Table = {
    this.getTable(options, this.inferSchema(options))
  }

  override def getTable(options: CaseInsensitiveStringMap, schema: StructType): Table = {
    new ChangefeedTable(schema)
  }

  override def shortName : String = "cosmos.oltp.changefeed"

  class ChangefeedTable(schema: StructType) extends Table with SupportsRead {
    override def name(): String = this.getClass.toString

    override def schema(): StructType = this.schema

    override def capabilities(): util.Set[TableCapability] = {
      Set(TableCapability.BATCH_READ).asJava
    }

    override def newScanBuilder(caseInsensitiveStringMap: CaseInsensitiveStringMap): ScanBuilder = {
      new ChangefeedScanBuilder()
    }
  }

  class ChangefeedScanBuilder extends ScanBuilder {
    override def build(): Scan = {
      new ChangefeedScan()
    }
  }

  class ChangefeedScan extends Scan with Batch {
    override def readSchema(): StructType = {
      CosmosItemToRowConverter.cosmosDbStreamSchema
    }

    override def toBatch: Batch = {
      this
    }

    override def planInputPartitions(): Array[InputPartition] = {
      Array(new ChangefeedPartition())
    }

    override def createReaderFactory(): PartitionReaderFactory = {
      new ChangefeedPartitionReaderFactory()
    }
  }

  class ChangefeedPartition extends InputPartition

  class ChangefeedPartitionReaderFactory extends PartitionReaderFactory {
    override def createReader(partition: InputPartition): PartitionReader[InternalRow] = {
      new ChangefeedPartitionReader()
    }
  }

  class ChangefeedPartitionReader extends PartitionReader[InternalRow] {
    override def next(): Boolean = ???

    override def get(): InternalRow = ???

    override def close(): Unit = ???
  }

  class CosmosItemToRowConverter

  object CosmosItemToRowConverter {
    val cosmosDbStreamSchema: StructType = {
      StructType(
        Seq(
          StructField("body", StringType),
          StructField("id", StringType),
          StructField("_rid", StringType),
          StructField("_self", StringType),
          StructField("_etag", StringType),
          StructField("_attachments", StringType),
          StructField("_ts", StringType)
        ))
    }
  }
}
