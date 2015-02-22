package entrez

import java.net.URLEncoder
import scala.xml._

import org.http4s._
import org.http4s.Http4s._
import org.http4s.client.blaze.defaultClient

import scalaz.\/-
import scalaz.concurrent.Task

object ESearch {

  // Base URL: send GET requests to retrieve article UIDs
  val baseUrl = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi"

  // Search for articles on PubMed with the Entrez Utilities
  val db = "pubmed"

  // Search result offset
  val retstart = 0

  // XML
  val rettype= "ulist"

  // The maximum number of UIDs that we want returned for a request
  // Note: 100000 is the maximum number of UIDs that can be returned in a single request
  val retmax = 5000

  // Sort by publication date
  val sort = "pub+date"

  // Ask for range of publication dates
  val datetype = "pdat"

  // Start date
  val mindate = "1970/01/01"

  // End date
  val maxdate = "2015/02/20"

  val client = defaultClient

  def url(term : String) : String = {
    s"""$baseUrl?db=$db&term=${URLEncoder.encode(term, "UTF-8")}&retstart=$retstart""" +
    s"""&rettype=$rettype&retmax=$retmax&sort=$sort&datetype=$datetype&""" +
    s"""mindate=$mindate&maxdate=$maxdate"""
  }

  def request(term : String) : Task[Elem] = {

    val task = Task.delay {
      val \/-(uri) = Uri.fromString(url(term))
      uri
    }

    task.flatMap(client.apply)
      .as[String]
      .map(XML.loadString)

  }

  // Counts the number of search results for a single term.
  def count(term : String) : Task[Int] = {

    request(term).map(elem => (elem \ "Count").text.toInt)

  }

  // Takes a list of search terms in a file and counts
  // the total number of search results for all of the terms
  // on PubMed.
  def countAll(file : String) : Option[String] = {

    io.linesR(file)
      .map(term => Process.eval(count(term)))
      .scan(0)(_ + _)
      .runLast
      .run

  }

}
