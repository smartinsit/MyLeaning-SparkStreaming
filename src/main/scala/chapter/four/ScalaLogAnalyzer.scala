package chapter.four


import java.util.regex.Pattern
import java.util.regex.Matcher

/**
 * Created by smartins on 10/28/15.
 */
class ScalaLogAnalyzer extends Serializable {
  /*
  Transform the apache log files and convert them
  into a map key/value pair
   */
  def transformLogData(logLine: String): Map[String, String] = {
    /*
    Pattern which will extract the relevant data from
    Apache Access Log files
     */
    val LOG_ENTRY_PATTERN = """^(\S+) (\S+) (\S+) \[([\w:/]+\s[+\-]\d{4})\] "(\S+) (\S+) (\S+)" (\d{3}) (\S+)"""
    val PATTERN = Pattern.compile(LOG_ENTRY_PATTERN)
    val matcher = PATTERN.matcher(logLine)

    // Matching the pattern for each line of the Apache access log file
    if (!matcher.find()) {
      System.out.println("Cannot parse logline " + logLine)
    }
    /*
    Finally create the key/value pair extracted from the
    data and return to the calling program
     */
    createDataMap(matcher)
  }

  /*
  Create a Map of the data which is extracted by applying
  regular expression
   */
  def createDataMap(m: Matcher): Map[String, String] = {
    return Map[String, String] (
      ("IP" -> m.group()),
      ("client" -> m.group(2)),
      ("user" -> m.group(3)),
      ("date" -> m.group(4)),
      ("method" -> m.group(5)),
      ("request" -> m.group(6)),
      ("protocol" -> m.group(7)),
      ("respcode" -> m.group(8)),
      ("size" -> m.group(9))
      )
  }
}

