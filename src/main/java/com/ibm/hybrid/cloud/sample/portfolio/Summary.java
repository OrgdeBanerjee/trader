package com.ibm.hybrid.cloud.sample.portfolio;

import javax.json.Json;
import javax.json.JsonArray;
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
import java.io.Writer;
import java.math.RoundingMode;
import java.text.NumberFormat;

/**
 * Servlet implementation class Summary
 */
@WebServlet(description = "Portfolio summary servlet", urlPatterns = {"/summary"})
public class Summary extends HttpServlet {
    private static final long serialVersionUID = 4815162342L;
    private static final String CREATE = "create";
    private static final String RETRIEVE = "retrieve";
    private static final String UPDATE = "update";
    private static final String DELETE = "delete";
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
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Writer writer = response.getWriter();
        writer.append(getTableRows(request).toString());
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String body = getBody(request);
        System.out.println("Got request body: " + body);
        JsonReader reader = Json.createReader(new StringReader(body));
        JsonObject json = reader.readObject();
        reader.close();


        String action = json.getString("action");
        String owner = json.getString("owner");

        if (action != null) {
            //In minikube and CFC, the port number is wrong for the https redirect.
            //This will fix that if needed - otherwise, it just returns an empty string
            //so that we can still use relative paths
            String prefix = PortfolioServices.getRedirectWorkaround(request);

            if (action.equals(CREATE)) {
                response.sendRedirect(prefix + "addPortfolio"); //send control to the AddPortfolio servlet
            } else if (action.equals(RETRIEVE)) {
                response.sendRedirect(prefix + "viewPortfolio?owner=" + owner); //send control to the ViewPortfolio servlet
            } else if (action.equals(UPDATE)) {
                response.sendRedirect(prefix + "addStock?owner=" + owner); //send control to the AddStock servlet
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

    private JsonArray getTableRows(HttpServletRequest request) {
        StringBuffer rows = new StringBuffer();

        JsonArray portfolios = PortfolioServices.getPortfolios();
        return portfolios;
    }

    public static String getBody(HttpServletRequest request) throws IOException {
        String body = null;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }

        body = stringBuilder.toString();
        return body;
    }
}
