// @formatter:off
package org.jboss.resteasy.plugins.server.servlet;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.NewCookie;

import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.specimpl.ResteasyHttpHeaders;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyUriInfo;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings("ALL")
// CUSTOMIZATION to avoid the registration of this servlet
// @WebServlet(asyncSupported = true, value="")
@WebServlet(asyncSupported = true)
public class HttpServlet30Dispatcher extends HttpServletDispatcher
{
   ScheduledExecutorService asyncCancelScheduler = Executors.newScheduledThreadPool(0);  // this is to get around TCK tests that call setTimeout in a separate thread which is illegal.
   @Override
   protected HttpRequest createHttpRequest(String httpMethod, HttpServletRequest httpServletRequest, ResteasyHttpHeaders httpHeaders, ResteasyUriInfo uriInfo, HttpResponse httpResponse, HttpServletResponse httpServletResponse)
   {
      Servlet3AsyncHttpRequest request = new Servlet3AsyncHttpRequest(httpServletRequest, httpServletResponse, getServletContext(), httpResponse, httpHeaders, uriInfo, httpMethod.toUpperCase(), (SynchronousDispatcher) getDispatcher());
      request.asyncScheduler = asyncCancelScheduler;
      return request;
   }

   @Override
   protected HttpResponse createServletResponse(HttpServletResponse response)
   {
      return new HttpServletResponseWrapper(response, getDispatcher().getProviderFactory()) {
         @Override
         public void addNewCookie(NewCookie cookie)
         {
            outputHeaders.add(javax.ws.rs.core.HttpHeaders.SET_COOKIE, cookie);
         }
      };
   }
}
// @formatter:on