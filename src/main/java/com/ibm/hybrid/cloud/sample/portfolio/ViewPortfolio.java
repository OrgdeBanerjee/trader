/*
       Copyright 2017 IBM Corp All Rights Reserved

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.ibm.hybrid.cloud.sample.portfolio;

import javax.json.JsonObject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Iterator;

/**
 * Servlet implementation class ViewPortfolio
 */
@WebServlet(description = "View Portfolio servlet", urlPatterns = {"/viewPortfolio"})
public class ViewPortfolio extends HttpServlet {
    private static final long serialVersionUID = 4815162342L;
    private static final double ERROR = -1;
    private static final String ERROR_STRING = "Error";
    private NumberFormat currency = null;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ViewPortfolio() {
        super();

        currency = NumberFormat.getNumberInstance();
        currency.setMinimumFractionDigits(2);
        currency.setMaximumFractionDigits(2);
        currency.setRoundingMode(RoundingMode.HALF_UP);
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String owner = request.getParameter("owner");
        System.out.println("Owner is: " + owner);
        JsonObject portfolio = PortfolioServices.getPortfolio(owner);
        System.out.println("Portfolio is: " + portfolio.toString());
        double overallTotal = portfolio.getJsonNumber("total").doubleValue();
        String loyaltyLevel = portfolio.getString("loyalty");
        JsonObject stocks = portfolio.getJsonObject("stocks");
        Writer writer = response.getWriter();

        writer.append(stocks.toString());
    }


    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //In minikube and CFC, the port number is wrong for the https redirect.
        //This will fix that if needed - otherwise, it just returns an empty string
        //so that we can still use relative paths
        String prefix = PortfolioServices.getRedirectWorkaround(request);

        response.sendRedirect(prefix + "summary"); //send control to the Summary servlet
    }

    private String getTableRows(JsonObject stocks) {
        StringBuffer rows = new StringBuffer();

        Iterator<String> keys = stocks.keySet().iterator();

        while (keys.hasNext()) {
            String key = keys.next();
            JsonObject stock = stocks.getJsonObject(key);

            String symbol = stock.getString("symbol");
            int shares = stock.getInt("shares");
            double price = stock.getJsonNumber("price").doubleValue();
            String date = stock.getString("date");
            double total = stock.getJsonNumber("total").doubleValue();

            String formattedPrice = "$" + currency.format(price);
            String formattedTotal = "$" + currency.format(total);

            if (price == ERROR) {
                formattedPrice = ERROR_STRING;
                formattedTotal = ERROR_STRING;
            }

            rows.append("        <tr>");
            rows.append("          <td>" + symbol + "</td>");
            rows.append("          <td>" + shares + "</td>");
            rows.append("          <td>" + formattedPrice + "</td>");
            rows.append("          <td>" + date + "</td>");
            rows.append("          <td>" + formattedTotal + "</td>");
            rows.append("        </tr>");
        }

        return rows.toString();
    }
}
