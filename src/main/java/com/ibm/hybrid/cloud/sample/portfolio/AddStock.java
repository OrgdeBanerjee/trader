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

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

/**
 * Servlet implementation class AddStock
 */
@WebServlet(description = "Add Stock servlet", urlPatterns = {"/addStock"})
public class AddStock extends HttpServlet {
    private static final long serialVersionUID = 4815162342L;

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JsonObject json = BodyToJson.convert(request);

        String owner = json.getString("owner");
        String symbol = json.getString("symbol");
        String shareString = json.getString("shares");
        JsonObject newPortfolio;
        if ((shareString != null) && !shareString.equals("")) {
            int shares = Integer.parseInt(shareString);
            newPortfolio = PortfolioServices.updatePortfolio(owner, symbol, shares);
        }

        //In minikube and CFC, the port number is wrong for the https redirect.
        //This will fix that if needed - otherwise, it just returns an empty string
        //so that we can still use relative paths
        String prefix = PortfolioServices.getRedirectWorkaround(request);

        response.sendRedirect(prefix + "summary");
    }
}
