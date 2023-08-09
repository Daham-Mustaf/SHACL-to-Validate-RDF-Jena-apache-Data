package jene.fuseki;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

public class SparqlQueryExecutor {
    public static ResultSet executeQuery(String sparqlQuery, Model model) {
        try (QueryExecution queryExecution = QueryExecutionFactory.create(sparqlQuery, model)) {
            return queryExecution.execSelect();
        }
    }

    public static void main(String[] args) {
        // Create a Model and load data into it
        Model model = ModelFactory.createDefaultModel();
        model.read("C:\\Users\\dmustafa\\Desktop\\SHACL_Jena_Appachi\\src\\main\\data\\providerLegalInformation.ttl");

        // Define the SPARQL query
        String sparqlQuery = "SELECT ?subject ?predicate ?object WHERE { ?subject ?predicate ?object }";

        // Execute the SPARQL query
        ResultSet resultSet = executeQuery(sparqlQuery, model);

        // Process the query results
        while (resultSet.hasNext()) {
            QuerySolution solution = resultSet.nextSolution();
            Resource subject = solution.getResource("subject");
            Resource predicate = solution.getResource("predicate");
            RDFNode object = solution.get("object");
            System.out.println("Subject: " + subject);
            System.out.println("Predicate: " + predicate);
            System.out.println("Object: " + object);
        }
    }
}
