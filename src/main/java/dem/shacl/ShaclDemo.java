package dem.shacl;


import app.shacl.ShaclValidationTool;
import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.Model;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

class ShaclDemo {

	public static void main(String[] args) {

		ShaclValidationTool validator = new ShaclValidationTool();
//		validator.validate("providerLegalInformation.ttl","provider-legal-informationShape.ttl",true );
		validator.validate("person.ttl", "personShape.ttl", true );
		/*Task 3
		* reads both the instance and SHACL shape file.
		* */
		Model modelInstance = validator.loadModel("providerLegalInformation.ttl");
//		validator.printModel(modelInstance);
		//   load graph from file
//		Graph graphModel = validator.loadGraph("providerLegalInformation.ttl");
//		System.out.println(graphModel);
//
//		Model shapeModel = validator.loadModel("provider-legal-informationShape.ttl");
//		validator.printModel(modelInstance);

		/*Task 4 & 5
		 * 4 Extend your tool so that it validates your instance against the SHACL shape file
		 * and prints/displays the validation report.
		 * 5 Extend your tool so that it saves your instance in JSON-LD format, if the validation succeeds.
		 * */
//
//		validator.validate("providerLegalInformation.ttl", "provider-legal-informationShape.ttl", true);
//		validator.validate("person.ttl", "personShape.ttl", true);
//		validator.validate("asset_A_S.ttl", "aas_shacl.ttl", true);


		/*Task 4 & 5
		 * validate a list of constraints
		 * */
//        List<String> dataModelNames = Arrays.asList("person.ttl", "providerLegalInformation.ttl", "asset_A_S.ttl");
//        List<String> shapeNames = Arrays.asList("personShape.ttl", "provider-legal-informationShape.ttl", "aas_shacl.ttl");
//        validator.validateList(dataModelNames, shapeNames, true);
	}

	private String getAbsolutePath(String relativePath) {
		Path currentPath = Paths.get("").toAbsolutePath();
		return currentPath.resolve(relativePath).toString();
	}
}
