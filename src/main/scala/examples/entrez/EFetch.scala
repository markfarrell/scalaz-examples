package entrez

import java.net.URLEncoder
import scala.xml._

import org.http4s._
import org.http4s.Http4s._
import org.http4s.client.blaze.defaultClient

import scalaz.\/-
import scalaz.concurrent.Task

import scalaz.stream._

object EFetch {

  // Base URL: send GET requests to retrieve article contents.
  val baseUrl = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi"

  // Search for articles on PubMed with the Entrez Utilities
  val db = "pubmed"

  // Return article contents as XML
  val retmode = "XML"

  // Local directory path to save articles in
  val articleDir = "articles"

  val client = defaultClient

  // Make a URL before fetching an article
  def url(id : String) : String = s"$baseUrl?db=$db&id=$id&retmode=$retmode"

  // Fetch an article as XML, given its UID
  def request(id : String) : Task[Elem] = {

    val task = Task.delay {
      val \/-(uri) = Uri.fromString(url(id))
      uri
    }

    task.flatMap(client.apply)
      .as[String]
      .map(XML.loadString)

  }

  // Fetch an article as XML, given its UID and save it to a file
  def requestAndSave(id : String) : Process[Task, Unit] = {

    val xml2String = process1.id[Elem].map(_.toString)
    val string2Utf8 = scalaz.stream.text.utf8Encode

    Process.eval(request(id))
      .pipe(xml2String)
      .pipe(string2Utf8)
      .to(io.fileChunkW(s"$articleDir/$id.xml"))

  }

  // Exercise : fetch an article and store it in a document database, such as MongoDB.
  def requestAndStore(id : String) : Process[Task, Unit] = ???

  // Takes a list of search terms in a file, fetches an upwards of 100000
  // search results for each term, and then saves all of the articles contents
  // in the search results to files.
  def saveAll(file : String) : Process[Task, Unit] = {

    io.linesR(file)
      .flatMap(term => ESearch.ids(term))
      .flatMap(requestAndSave)

  }

}
