/*
 * Created by IntelliJ IDEA.
 * User: graham
 * Date: 18/02/2011
 * Time: 12:12
 */
package net.tackley.sg.snippet

import net.liftweb.util.Helpers._
import net.liftweb.http.S


class Facebook {
  def show = "* *" #> ("signed_request = " + S.param("signed_request"))
}