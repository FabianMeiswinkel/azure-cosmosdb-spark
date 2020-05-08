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

package com.microsoft.azure.cosmos.spark.oltp

import java.util
import java.util.{List, Optional}
import java.util.function.Supplier

import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.sources.DataSourceRegister
import org.apache.spark.sql.sources.v2.DataSourceOptions
import org.apache.spark.sql.sources.v2.ReadSupport
import org.apache.spark.sql.sources.v2.WriteSupport
import org.apache.spark.sql.sources.v2.reader.DataSourceReader
import org.apache.spark.sql.sources.v2.reader.InputPartition
import org.apache.spark.sql.sources.v2.writer.DataSourceWriter
import org.apache.spark.sql.types.StructType

final class ItemsDataSource extends DataSourceRegister with ReadSupport with WriteSupport {
  override def createReader(schema: StructType, options: DataSourceOptions): DataSourceReader = {
    throw new NotImplementedError()
  }

  override def createReader(options: DataSourceOptions): DataSourceReader = {
    throw new NotImplementedError()
  }

  override def shortName: String = "cosmos.oltp.items"

  override def createWriter(
                             @SuppressWarnings(Array("checkstyle:AbbreviationAsWordInName")) writeUUID: String,
                             schema: StructType,
                             mode: SaveMode,
                             options: DataSourceOptions): Optional[DataSourceWriter] = {
    throw new NotImplementedError()
  }

  private class Reader(val userDefinedSchema: Option[StructType]) extends DataSourceReader {
    private lazy val effectiveSchema : StructType = userDefinedSchema.getOrElse(this.inferSchema)

    override def readSchema: StructType = {
      effectiveSchema
    }

    override def planInputPartitions: List[InputPartition[InternalRow]] = {
      throw new NotImplementedError()
    }

    private def inferSchema: StructType = {
      throw new NotImplementedError()
    }
  }
}
