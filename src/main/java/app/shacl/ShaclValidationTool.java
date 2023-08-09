package app.shacl;

import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.*;
import org.apache.jena.shacl.ShaclValidator;
import org.apache.jena.shacl.Shapes;
import org.apache.jena.shacl.ValidationReport;
import org.apache.jena.shacl.lib.ShLib;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * Utility class for working with RDF graphs and performing SHACL validation
 * using the Apache Jena framework.
 */

public class ShaclValidationTool {
	private static final Logger logger = LoggerFactory.getLogger(ShaclValidationTool.class);
	private static final Marker WTF_MARKER = MarkerFactory.getMarker("WTF");
	// Replace forward slashes with backslashes for Windows
//    private static final String DATA_DIRECTORY = "src/main/data/";
	private static final String DATA_DIRECTORY = "src" + File.separator + "main" + File.separator + "data";
	private static final String SHAPE_DIRECTORY = "src" + File.separator + "main" + File.separator + "shape";
	private static final String REPORTS_DIRECTORY = "src" + File.separator + "main" + File.separator + "reports";

	/**
	 * Loads an RDF model from a file.
	 *
	 * @param modelName The name of the file containing the RDF model.
	 * @return The loaded RDF model.
	 */
	public Model loadModel(String modelName) {
		try {
			String dataFilePath = getAbsolutePath(DATA_DIRECTORY) + File.separator + modelName;
			Model model = ModelFactory.createDefaultModel();
			model.read(dataFilePath);
			logger.info("Model loaded successfully: {}", dataFilePath);
			return model;
		} catch (RiotNotFoundException e) {
			logger.error("File not found, please provide a correct file path or file name to your model: {}", modelName,
					e);
		} catch (RiotException e) {
			logger.error(" ❌ [Bad Syntax] Error while parsing RDF model and serializing RDF -: {} of file {}",e.getMessage(), modelName);
		} catch (Exception e) {
			logger.error("Unexpected error occurred while parsing RDF: {}", modelName, e);
		}
		return null;
	}

	public Graph loadGraph(String graphName) {
		try {
			String dataFilePath = getAbsolutePath(DATA_DIRECTORY) + File.separator + graphName;
			Graph graph = RDFDataMgr.loadGraph(dataFilePath, Lang.TURTLE);
			logger.info("Graph loaded successfully : {}", graphName);
			return graph;
		} catch (RiotNotFoundException e) {
			logger.error("File not found, please provide a correct file path or file name to your graph: {}", graphName,
					e);
		} catch (RiotException e) {
			logger.error("❌ [Bad Syntax] Error while parsing RDF Graph and serializing RDF : {} of file {}", e.getMessage(), graphName);
		} catch (Exception e) {
			logger.error("Unexpected error occurred while parsing RDF: {}", graphName, e);
		}
		return null;
	}

	/**
	 * Validates an RDF model against a SHACL shape and optionally prints the
	 * validation report.
	 *
	 * @param dataModelName        The name of the file containing the RDF model.
	 * @param shapeName            The name of the file containing the SHACL shape.
	 * @param printViolationReport Flag indicating whether to print the violation
	 *                             report.
	 */

	public void validate(String dataModelName, String shapeName, boolean printViolationReport) {
		try {
			Graph dataGraph = loadGraphFromDifferentDirectory(DATA_DIRECTORY, dataModelName);
			Graph shapeGraph = loadGraphFromDifferentDirectory(SHAPE_DIRECTORY, shapeName);
			Shapes shapes = loadShapes(shapeGraph);
			ValidationReport report = ShaclValidator.get().validate(shapes, dataGraph);
			boolean conforms = report.conforms();
			logger.trace("Conforms = {}", conforms);

			if (conforms) {
				String dataFileNameWithoutExtension = removeFileExtension(dataModelName);
				String reportFilePath = getAbsolutePath(REPORTS_DIRECTORY) + File.separator
						+ dataFileNameWithoutExtension + "_conform_report.jsonld";
				logger.info("Data has been conformed to file: {} ...", reportFilePath);
				writeGraphToFile(dataGraph, reportFilePath, RDFLanguages.JSONLD);
			} else {
				String dataFileNameWithoutExtension = removeFileExtension(dataModelName);
				String reportFilePath = getAbsolutePath(REPORTS_DIRECTORY) + File.separator
						+ dataFileNameWithoutExtension + "_violation_report.ttl";
				logger.info("Data has not been conformed! see violation report in file: {}", reportFilePath);
				createReportFile(reportFilePath, report.getModel());

				if (printViolationReport) {
					if (printViolationReport) {
						logger.info("\n-------------- Violation Report ----------:");
						logger.info("Report of {}", reportFilePath);
						logger.info("<< Violation report contents >>");
						printValidationReport(report);
						logger.info("------------------------------ end of report###### {} \n", dataModelName);
					}
				}
			}
		} catch (Throwable t) {
			logger.error(WTF_MARKER, t.getMessage(), t);
		}
	}

	/**
	 * Validates a list of RDF models against a list of SHACL shapes in a batch
	 * process, generating separate validation reports for each instance.
	 *
	 * @param dataModelNames       The list of file names containing the RDF models.
	 * @param shapeNames           The list of file names containing the SHACL
	 *                             shapes.
	 * @param printViolationReport Flag indicating whether to print the violation
	 *                             reports.
	 */

	public void validateList(List<String> dataModelNames, List<String> shapeNames, boolean printViolationReport) {
		if (dataModelNames.size() != shapeNames.size()) {
			logger.error("Data model names list and shape names list must have the same size.");
			return;
		}

		try {
			for (int i = 0; i < dataModelNames.size(); i++) {
				String dataModelName = dataModelNames.get(i);
				String shapeName = shapeNames.get(i);

				Graph dataGraph = loadGraphFromDifferentDirectory(DATA_DIRECTORY, dataModelName);
				Graph shapeGraph = loadGraphFromDifferentDirectory(SHAPE_DIRECTORY, shapeName);
				Shapes shapes = Shapes.parse(shapeGraph);

				ValidationReport report = ShaclValidator.get().validate(shapes, dataGraph);
				boolean conforms = report.conforms();
				logger.trace("Conforms = {}", conforms);

				Model reportModel = ModelFactory.createDefaultModel();
				reportModel.add(report.getModel());

				if (conforms) {
					String dataFileNameWithoutExtension = removeFileExtension(dataModelName);
					String reportFilePath = getAbsolutePath(REPORTS_DIRECTORY) + File.separator
							+ dataFileNameWithoutExtension + "_conform_report.jsonld";
					logger.info("Data has been conformed to file: {}", reportFilePath);
					writeGraphToFile(dataGraph, reportFilePath, RDFLanguages.JSONLD);

				} else {
					String dataFileNameWithoutExtension = removeFileExtension(dataModelName);
					String reportFilePath = getAbsolutePath(REPORTS_DIRECTORY) + File.separator
							+ dataFileNameWithoutExtension + "_violation_report.ttl";
					createReportFile(reportFilePath, reportModel);
					logger.info("Data has not been conformed! see violation report in file: {}", reportFilePath);

					if (printViolationReport) {
						logger.info("\n-------------- Violation Report ----------:");
						logger.info("Report of {}", reportFilePath);
						logger.info("<< Violation report contents >>");
						printValidationReport(report);
						logger.info("------------------------------ end of report###### {} \n", dataModelName);
					}
				}
			}
		} catch (Throwable t) {
			logger.error(WTF_MARKER, t.getMessage(), t);
		}
	}

	/**
	 * Writes an RDF graph to a file in the specified format.
	 *
	 * @param graph    The RDF graph to write.
	 * @param filePath The path of the output file.
	 * @param format   The format of the output file.
	 */

	public void writeGraphToFile(Graph graph, String filePath, Lang format) throws IOException {
		// Delete the existing file if it exists
		deleteFileIfExists(filePath);

		// Create the new report model file
		File modelFile = new File(filePath);
		modelFile.createNewFile();
		try (OutputStream outputStream = new FileOutputStream(modelFile)) {
			RDFDataMgr.write(outputStream, graph, format);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("Failed to write graph to file: {}", modelFile, e);
		}
		logger.info("Validation ended #####********");
		logger.info("-----------------------------------------------------\n");
	}

	/**
	 * Loads SHACL shapes from a file.
	 *
	 * @param shapeGraph The file containing the SHACL shapes.
	 * @return The loaded SHACL shapes.
	 */
	private Shapes loadShapes(Graph shapeGraph) {
		try {
			Shapes shapes = Shapes.parse(shapeGraph);
			logger.info("Shapes parsed and loaded successfully");
			return shapes;
		} catch (RiotNotFoundException e) {
			logger.error("File not found, please provide a correct file path or name: {}", shapeGraph, e);
		} catch (RiotException e) {
			logger.error("❌ [Bad Syntax] Error while parsing shapes: {}", shapeGraph, e);
		} catch (Exception e) {
			logger.error("Unexpected error occurred while loading shapes: {}", shapeGraph, e);
		}
		return null;
	}

	public void writeModelToFile(String filePath, Model model) throws IOException {
		File reportFile = new File(filePath);
		reportFile.createNewFile();
		// Write model to file
		try (OutputStream reportOutputStream = new FileOutputStream(reportFile)) {
			RDFDataMgr.write(reportOutputStream, model, RDFFormat.JSONLD_PRETTY);
		}
	}


	/**
	 * Prints the content of an RDF model.
	 *
	 * @param model The RDF model to print.
	 */
	public void printModel(Model model) {

		// Check if the model is empty
		if (model.isEmpty()) {
			logger.info("Model is empty. No content to print.");
			return;
		}
		// Iterate over the subjects in the model
		ResIterator iterator = model.listSubjects();
		while (iterator.hasNext()) {
			Resource subject = iterator.next();
			Statement[] statements = subject.listProperties().toList().toArray(new Statement[0]);
			// Print the subject URI
			logger.info("Subject: {}", subject.getURI());

			for (Statement statement : statements) {
				logger.info("  Property: {}", statement.getPredicate().getURI());
				RDFNode object = statement.getObject();
				// Print the properties and objects
				if (object.isLiteral()) {
					logger.info("    Literal Object: {}", object.asLiteral().getString());
				} else if (object.isResource()) {
					logger.info("    Resource Object: {}", object.asResource().getURI());
				} else {
					logger.info("    Unknown Object Type: {}", object.toString());
				}
			}
			logger.info(" end of model #####********");
			logger.info("-----------------------------------------------------\n");
		}
	}


	/**
	 * Modal methods
	 */

	private void printValidationReport(ValidationReport report) {
//		Prints the validation report to the logger.
		ShLib.printReport(report);
		logger.info("");
	}

	private Graph loadGraphFromDifferentDirectory(String directory, String graphName) {
		try {
			String dataFilePath = getAbsolutePath(directory) + File.separator + graphName;
			Graph graph = RDFDataMgr.loadGraph(dataFilePath, Lang.TURTLE);
			logger.info("{} -- Graph loaded successfully ", graphName);
			return graph;
		} catch (RiotNotFoundException e) {
			logger.error("File not found, please provide a correct file path or file name to your graph: {}", graphName,
					e);
		} catch (RiotException e) {
			logger.error("❌ [Bad Syntax] Error while parsing RDF graph and serializing RDF: {}", graphName, e);
		} catch (Exception e) {
			logger.error("Unexpected error occurred while parsing RDF: {}", graphName, e);
		}
		return null;
	}

	public void showModelSize(Model m) {
		logger.info(String.format("The model contains %d triples", m.size()));
	}

	public void showGraphSize(Graph g) {
		logger.info(String.format("The graph contains %d triples", g.size()));
	}

	private String getAbsolutePath(String relativePath) {
		Path currentPath = Paths.get("").toAbsolutePath();
		return currentPath.resolve(relativePath).toString();
	}

	private void createReportFile(String filePath, Model model) throws IOException {

		// Delete the existing file if it exists
		deleteFileIfExists(filePath);

		// Create the new report file
		File reportFile = new File(filePath);
		reportFile.createNewFile();
		try (OutputStream reportOutputStream = new FileOutputStream(reportFile)) {
			RDFDataMgr.write(reportOutputStream, model, RDFLanguages.TURTLE);
		}
	}

	private String removeFileExtension(String fileName) {
		int extensionIndex = fileName.lastIndexOf(".");
		if (extensionIndex != -1) {
			return fileName.substring(0, extensionIndex);
		}
		return fileName;
	}

	private void deleteFileIfExists(String filePath) throws IOException {
		File file = new File(filePath);
		if (file.exists()) {
			boolean deleted = file.delete();
			if (deleted) {
				logger.info("Already existed file {} deleted  ...", file.getName());
			} else {
				logger.warn("Failed to delete the file: {}", file.getName());
				throw new IOException("Failed to delete the file: " + file.getName());
			}
		}
	}

}
