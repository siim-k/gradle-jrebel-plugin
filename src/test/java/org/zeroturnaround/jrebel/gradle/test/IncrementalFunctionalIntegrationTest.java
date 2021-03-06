package org.zeroturnaround.jrebel.gradle.test;

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.zeroturnaround.jrebel.gradle.test.util.RebelXMLHelper;

/**
 * Functional Test class using TestKit based on https://docs.gradle.org/current/userguide/test_kit.html
 */
@RunWith(Parameterized.class)
public class IncrementalFunctionalIntegrationTest extends BaseRebelPluginFunctionalIntegrationTest {


  public IncrementalFunctionalIntegrationTest(String version) {
    super(version);
  }

  @Parameterized.Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] {
        { "4.0" },
        { "4.1" },
        { "4.2" },
        { "4.2.1" },
        { "4.3" },
        { "4.4" },
        { "4.5" },
        { "4.6" },
        { "4.7" },
        { "4.8" },
        { "4.9" },
        { "4.10.3", },
        { "5.0", },
        { "5.1", },
        { "5.2.1", },
    });
  }

  @Test
  public void testSecondBuildIsSkippedWhenNoChange() throws IOException {
    buildFileBuilder
        .write(buildFile);

    assertEquals(SUCCESS, build().task(":build").getOutcome());
    assertDefaultRebelXMLContent();

    assertGenerateRebelIsUpToDateOnRebuild();
  }

  @Test
  public void testAlwaysGenerateChangeTriggersRebuild() throws IOException {
    buildFileBuilder
        .rebelBlock("alwaysGenerate = false")
        .write(buildFile);

    assertEquals(SUCCESS, build().task(":build").getOutcome());
    assertDefaultRebelXMLContent();

    assertGenerateRebelIsUpToDateOnRebuild();

    buildFileBuilder
        .rebelBlock("alwaysGenerate = true\n")
        .write(buildFile);

    assertEquals(SUCCESS, build().task(":generateRebel").getOutcome());
    assertDefaultRebelXMLContent();

    assertEquals(SUCCESS, build().task(":generateRebel").getOutcome());
  }

  @Test
  public void testAlwaysGenerateTriggersRebuild() throws IOException {
    buildFileBuilder
        .rebelBlock("alwaysGenerate = true")
        .write(buildFile);

    assertEquals(SUCCESS, build().task(":build").getOutcome());
    assertDefaultRebelXMLContent();

    assertEquals(SUCCESS, build().task(":generateRebel").getOutcome());
  }

  @Test
  public void testShowXmlChangeTriggersRebuild() throws IOException {
    buildFileBuilder
        .rebelBlock("showGenerated = false")
        .write(buildFile);

    assertEquals(SUCCESS, build().task(":build").getOutcome());
    assertDefaultRebelXMLContent();

    assertGenerateRebelIsUpToDateOnRebuild();

    buildFileBuilder
        .rebelBlock("showGenerated = true")
        .write(buildFile);

    assertEquals(SUCCESS, build().task(":generateRebel").getOutcome());
    assertDefaultRebelXMLContent();

    assertGenerateRebelIsUpToDateOnRebuild();
  }

  @Test
  public void testRebelXmlDirectoryTriggersRebuild() throws IOException {
    buildFileBuilder
        .rebelBlock("rebelXmlDirectory = 'build/classes'")
        .write(buildFile);

    assertEquals(SUCCESS, build().task(":build").getOutcome());
    assertDefaultRebelXMLContent();

    assertGenerateRebelIsUpToDateOnRebuild();

    buildFileBuilder
        .rebelBlock("rebelXmlDirectory = 'build/classes2'")
        .write(buildFile);

    assertEquals(SUCCESS, build().task(":generateRebel").getOutcome());
    assertDefaultRebelXMLContent();

    assertGenerateRebelIsUpToDateOnRebuild();
  }

  @Test
  public void testProjectRootFromCommandLineTriggersRebuild() throws IOException {
    buildFileBuilder
        .rebelBlock("rebelXmlDirectory = 'build/classes'")
        .write(buildFile);

    assertEquals(SUCCESS, build().task(":build").getOutcome());
    assertDefaultRebelXMLContent();

    assertGenerateRebelIsUpToDateOnRebuild();

    runner = runner
        .withArguments("build", "--info", "--stacktrace", "-Prebel.rootPath=/some/root");

    assertEquals(SUCCESS, build().task(":generateRebel").getOutcome());
    RebelXMLHelper rebelXML = new RebelXMLHelper(getRebelXML());
    assertEquals("/some/root/build/resources/main", rebelXML.getClasspathDir(1));
    assertEquals("/some/root/build/classes/java/main", rebelXML.getClasspathDir(2));
    assertEquals("/some/root/src/main/webapp", rebelXML.getWebDir(1));

    assertGenerateRebelIsUpToDateOnRebuild();
  }

  @Test
  public void testWebBlockChangeTriggersRebuild() throws IOException {
    buildFileBuilder
        .write(buildFile);

    assertEquals(SUCCESS, build().task(":build").getOutcome());
    assertDefaultRebelXMLContent();

    assertGenerateRebelIsUpToDateOnRebuild();

    buildFileBuilder
        .rebelBlock("" +
            "  web {\n" +
            "    omitDefault = true\n" +
            "    resource {\n" +
            "      directory = \"src/main/jsps\"\n" +
            "      target = \"/WEB-INF/jsps\"\n" +
            "    }\n" +
            "  }\n"
            )
        .write(buildFile);

    assertEquals(SUCCESS, build().task(":generateRebel").getOutcome());

    RebelXMLHelper rebelXML = new RebelXMLHelper(getRebelXML());
    assertEquals(absolutePath("build/resources/main"), rebelXML.getClasspathDir(1));
    assertEquals(absolutePath("build/classes/java/main"), rebelXML.getClasspathDir(2));
    assertEquals(absolutePath("src/main/jsps"), rebelXML.getWebDir(1));

    assertGenerateRebelIsUpToDateOnRebuild();
  }

  @Test
  public void testClasspathBlockChangeTriggersRebuild() throws IOException {
    buildFileBuilder
        .write(buildFile);

    assertEquals(SUCCESS, build().task(":build").getOutcome());
    assertDefaultRebelXMLContent();

    assertGenerateRebelIsUpToDateOnRebuild();

    buildFileBuilder
        .rebelBlock("" +
            "classpath {\n" +
            "    omitDefaultClassesDir = true\n" +
            "    omitDefaultResourcesDir = true\n" +
            "    resource {\n" +
            "      directory = \"build/main/other-classes-dir\"\n" +
            "      includes = [\"**/*\"]\n" +
            "      excludes = [\"*.java\", \"*.properties\"]\n" +
            "    }\n" +
            "  }"
        )
        .write(buildFile);

    assertEquals(SUCCESS, build().task(":generateRebel").getOutcome());

    RebelXMLHelper rebelXML = new RebelXMLHelper(getRebelXML());
    assertEquals(absolutePath("build/main/other-classes-dir"), rebelXML.getClasspathDir(1));
    assertEquals(absolutePath("src/main/webapp"), rebelXML.getWebDir(1));

    assertGenerateRebelIsUpToDateOnRebuild();
  }

  private void assertDefaultRebelXMLContent() throws IOException {
    RebelXMLHelper rebelXML = new RebelXMLHelper(getRebelXML());
    assertEquals(absolutePath("build/resources/main"), rebelXML.getClasspathDir(1));
    assertEquals(absolutePath("build/classes/java/main"), rebelXML.getClasspathDir(2));
    assertEquals(absolutePath("src/main/webapp"), rebelXML.getWebDir(1));
  }
}
