package com.ibm.hybrid.cloud.sample.portfolio;

import java.io.IOException;
import java.io.Writer;
import java.math.RoundingMode;
import java.text.NumberFormat;

import javax.json.JsonArray;
import javax.json.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Summary
 */
@WebServlet(description = "Portfolio summary servlet", urlPatterns = { "/summary" })
public class Summary extends HttpServlet {
	private static final long serialVersionUID = 4815162342L;
	private static final String CREATE   = "create";
	private static final String RETRIEVE = "retrieve";
	private static final String UPDATE   = "update";
	private static final String DELETE   = "delete";
	private NumberFormat currency = null;

    /**
     * @see HttpServlet#HttpServlet()
     */
	public Summary() {
		super();

		System.out.println("Workaround version of Summary servlet");
		currency = NumberFormat.getNumberInstance();
		currency.setMinimumFractionDigits(2);
		currency.setMaximumFractionDigits(2);
		currency.setRoundingMode(RoundingMode.HALF_UP);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		String owner = request.getParameter("owner");

		if (action != null) {
			//In minikube and CFC, the port number is wrong for the https redirect.
			//This will fix that if needed - otherwise, it just returns an empty string
			//so that we can still use relative paths
			String prefix = PortfolioServices.getRedirectWorkaround(request);

			if (action.equals(CREATE)) {
				response.sendRedirect(prefix+"addPortfolio"); //send control to the AddPortfolio servlet
			} else if (action.equals(RETRIEVE)) {
				response.sendRedirect(prefix+"viewPortfolio?owner="+owner); //send control to the ViewPortfolio servlet
			} else if (action.equals(UPDATE)) {
				response.sendRedirect(prefix+"addStock?owner="+owner); //send control to the AddStock servlet
			} else if (action.equals(DELETE)) {
				PortfolioServices.deletePortfolio(owner);
				doGet(request, response); //refresh the Summary servlet
			} else {
				doGet(request, response); //something went wrong - just refresh the Summary servlet
			}
		} else {
			doGet(request, response); //something went wrong - just refresh the Summary servlet
		}
	}

	private String getTableRows(HttpServletRequest request) {
		StringBuffer rows = new StringBuffer();

		JsonArray portfolios = PortfolioServices.getPortfolios();

		for (int index=0; index<portfolios.size(); index++) {
			JsonObject portfolio = (JsonObject) portfolios.get(index);

			String owner = portfolio.getString("owner");
			double total = portfolio.getJsonNumber("total").doubleValue();
			String loyaltyLevel = portfolio.getString("loyalty");

			rows.append("        <tr>");
			rows.append("          <td><input type=\"radio\" name=\"owner\" value=\""+owner+"\"");
			if (index == 0) {
				rows.append(" checked");
			}
			rows.append("></td>");

			rows.append("          <td>"+owner+"</td>");
			rows.append("          <td>$"+currency.format(total)+"</td>");
			rows.append("          <td>"+loyaltyLevel+"</td>");
			rows.append("        </tr>");
		}

		return rows.toString();
	}
}
