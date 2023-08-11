package app.shacl;

import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShaclValidationToolTest {
    private ShaclValidationTool shaclValidationTool;

    @BeforeEach
    public void setUp() {
        shaclValidationTool = new ShaclValidationTool();
    }
    @Test
    public void testLoadModel() {
        Model model = shaclValidationTool.loadModel("person.ttl");
        assertNotNull(model);
        assertTrue(model.size() > 0);
    }

    @Test
    public void testLoadGraph() {
        Graph graph = shaclValidationTool.loadGraph("person.ttl");
        assertNotNull(graph);
        assertTrue(graph.size() > 0);
    }

    @Test
    public void testValidateConform() {
        String dataModelName = "data.ttl";
        String shapeName = "shape.ttl";
        shaclValidationTool.validate(dataModelName, shapeName, false);
    }

    @Test
    public void testValidateViolation() {
        String dataModelName = "person.ttl";
        String shapeName = "personShape.ttl";
        shaclValidationTool.validate(dataModelName, shapeName, false);
    }

}