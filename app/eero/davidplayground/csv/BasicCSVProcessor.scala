package eero.davidplayground.csv

import scala.collection.mutable.ArrayBuffer
import scala.io.Source

trait BasicCSVProcessor extends CSVRowProcessor {
  val dataFileName: String
  val workingDirectory: String =
    "/Users/cerdadav/eero/cloud_playground/modules/davidplayground/test/eero/davidplayground/CSV_Files/"

  val data: String = "data/"
  val result: String = "result/"

  private val dataFullPath = {
    val default = "/Users/cerdadav/eero/cloud_playground/modules/davidplayground/test/eero/davidplayground/CSV_Files/"
    val elements = if (workingDirectory.startsWith("/")) {
      Seq(data, workingDirectory, dataFileName)
    } else {
      Seq(default, data, workingDirectory, dataFileName)
    }
    PathHelper.standardizePath(elements: _*)
  }

  override private[csv] val (headers: Vector[String], rows: IndexedSeq[CSVRow]) = {
    val bufferedSource = Source.fromFile(dataFullPath)
    val csvLines = ArrayBuffer[CSVRow]()
    val lines = bufferedSource.getLines()
    val headers = lines.next().split(",").map(_.trim).toVector
    for (line <- lines) {
      val row = line.split(",").map(_.trim)
      csvLines += new CSVRow(headers, row.toVector)
    }
    bufferedSource.close()
    (headers, csvLines.toIndexedSeq)
  }

  val writeFileName: Option[String]
  private val optCsvWriter: Option[BasicWriter] =
    writeFileName.map(file => new BasicWriter(file, result + workingDirectory))

  def write(statement: String): Unit = {
    optCsvWriter.map(_.write(statement))
  }

  def writeRow(row: String): Unit = {
    optCsvWriter.map(_.writeRow(row))
  }

  def closeFile(): Unit = {
    optCsvWriter.map(_.closeFile())
  }
}