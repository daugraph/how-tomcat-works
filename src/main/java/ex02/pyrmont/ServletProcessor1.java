package ex02.pyrmont;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class ServletProcessor1 {
	public void process(Request request, Response response) {
		String uri = request.getUri();
		String servletName = uri.substring(uri.lastIndexOf("/") + 1);
		URLClassLoader loader = null;

		try {
			URL[] urls = new URL[1];
			URLStreamHandler streamHandler = null;

			// get jar path
			final File classPath = new File(getClasspath());

			String repository = (new URL("file", null, classPath.getCanonicalPath() + File.separator)).toString();
			urls[0] = new URL(null, repository, streamHandler);
			System.out.println(urls[0]);
			loader = new URLClassLoader(urls);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Class<?> myClass = null;
		try {
			final String fullClassName = this.getClass().getPackage().getName() + "." + servletName;

			System.out.println(fullClassName);

			myClass = loader.loadClass(fullClassName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		Servlet servlet = null;
		try {
			servlet = (Servlet)myClass.newInstance();
			servlet.service((ServletRequest)request, (ServletResponse)response);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getClasspath() {
		return this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
	}
}
