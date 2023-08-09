package jene.fuseki;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.tdb2.TDB2Factory;
import org.apache.jena.util.FileManager;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FusekiConnectionExample {
    private static final String DATA_DIRECTORY = "src" + File.separator + "main" + File.separator + "data";

    private String getAbsolutePath(String fileName) {
        return "C:\\Users\\dmustafa\\Desktop\\SHACL_Jena_Appachi\\src\\main\\data\\" + fileName;
    }



    public static void main(String[] args) {

        FusekiConnectionExample example = new FusekiConnectionExample();
        String fileName = "providerLegalInformation.ttl";
        String absolutePath = example.getAbsolutePath(fileName);
        System.out.println(absolutePath);


        // Create a dataset and load the file
        Dataset dataset = DatasetFactory.create();
        dataset.begin(ReadWrite.READ);
        dataset.getDefaultModel().read(absolutePath);

        // SPARQL query
        String queryString = "SELECT ?subject ?predicate ?object WHERE { ?subject ?predicate ?object }";

        try (QueryExecution qexec = QueryExecutionFactory.create(queryString, dataset)) {
            // Execute the query
            ResultSet results = qexec.execSelect();

            // Process the query results
            while (results.hasNext()) {
                QuerySolution sol = results.nextSolution();
                RDFNode subject = sol.get("subject");
                RDFNode predicate = sol.get("predicate");
                RDFNode object = sol.get("object");

                System.out.println("Subject: " + subject);
                System.out.println("Predicate: " + predicate);
                System.out.println("Object: " + object);
                System.out.println("--------------------");
            }
        } finally {
            dataset.end();
        }
    }
}
