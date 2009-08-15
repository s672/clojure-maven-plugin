package clojure;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import static org.fest.assertions.Assertions.assertThat;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.util.List;

@RunWith(Theories.class)
public class NamespaceDiscoveryTest {

    @Test
    public void testNamespaceDiscovery() throws MojoExecutionException {

        NamespaceDiscovery namespaceDiscovery = new NamespaceDiscovery(mock(Log.class), true);

        List<String> namespaces = namespaceDiscovery.discoverNamespacesIn(new File("src/test/resources"));

        assertThat(namespaces)
                .isNotNull()
                .isNotEmpty()
                .contains("test")
                .contains("com.test")
                .contains("test.test3");
    }

    public static class NamespaceData {
        public String[] namespaces;
        public File[] sourceDirectories;
        public boolean compileDeclaredNamespaceOnly;

        public int expectedSize;

        public NamespaceData(String[] namespaces, File[] sourceDirectories, boolean compileDeclaredNamespaceOnly, int expectedSize) {
            this.namespaces = namespaces;
            this.sourceDirectories = sourceDirectories;
            this.compileDeclaredNamespaceOnly = compileDeclaredNamespaceOnly;
            this.expectedSize = expectedSize;
        }
    }

    @DataPoint
    public static NamespaceData ns1 = new NamespaceData(new String[]{"test.*"}, new File[]{new File("src/test/resources")}, true, 2);

    @DataPoint
    public static NamespaceData ns2 = new NamespaceData(new String[]{"!com.*"}, new File[]{new File("src/test/resources")}, false, 2);

    @DataPoint
    public static NamespaceData ns3 = new NamespaceData(new String[]{"test"}, new File[]{new File("src/test/resources")}, true, 1);

    @DataPoint
    public static NamespaceData ns4 = new NamespaceData(new String[]{"com.*"}, new File[]{new File("src/test/resources")}, true, 1);

    @DataPoint
    public static NamespaceData ns5 = new NamespaceData(new String[]{"!com.*", "test.*"}, new File[]{new File("src/test/resources")}, true, 2);

    @Theory
    public void testNamespaceFiltering(NamespaceData ns) throws MojoExecutionException {

        NamespaceDiscovery namespaceDiscovery = new NamespaceDiscovery(mock(Log.class), ns.compileDeclaredNamespaceOnly);

        assertThat(namespaceDiscovery.discoverNamespacesIn(ns.namespaces, ns.sourceDirectories))
                .isNotNull()
                .isNotEmpty()
                .hasSize(ns.expectedSize);

    }

}