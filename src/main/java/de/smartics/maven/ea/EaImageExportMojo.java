/*
 * Copyright 2013-2014 smartics, Kronseder & Reiner GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.smartics.maven.ea;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.StringUtils;
import org.sparx.Project;
import org.sparx.Repository;

/**
 * Exports the model defined with Enterprise Architect as images.
 *
 * @since 1.0
 * @description Exports the model defined with Enterprise Architect as images.
 */
@Mojo(name = "export", threadSafe = true, requiresProject = true,
    defaultPhase = LifecyclePhase.PRE_SITE)
public class EaImageExportMojo extends AbstractMojo
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  /**
   * The Enterprise Architect model file.
   *
   * @since 1.0
   */
  @Parameter(defaultValue = "${project.build.directory}/model/model.eap")
  private File eaProjectRepositoryFile;

  /**
   * The target folder for the model report. Only used for HTML site generation.
   *
   * @since 1.0
   */
  @Parameter(defaultValue = "${project.reporting.outputDirectory}/model")
  private File reportTargetFolder;

  /**
   * The target folder for the model images.
   *
   * @since 1.0
   */
  @Parameter(defaultValue = "${project.reporting.outputDirectory}/xmi/Images")
  private File imagesTargetFolder;

  /**
   * The GUID of the package within the project to export. Only used for HTML
   * site generation.
   *
   * @since 1.0
   */
  @Parameter
  private String eaPackageGuid;

  /**
   * Export image format for HTML export. Only used for HTML site generation.
   *
   * @since 1.0
   */
  @Parameter(defaultValue = "PNG")
  private String htmlExportImageFormat;

  /**
   * Export style applied to the export. Only used for HTML site generation.
   *
   * @since 1.0
   */
  @Parameter(defaultValue = "<default>")
  private String htmlExportStyle;

  /**
   * File name extension for HTML pages. Only used for HTML site generation.
   *
   * @since 1.0
   */
  @Parameter(defaultValue = ".html")
  private String htmlExportFileNameExtension;

  /**
   * Set to <code>true</code> to generate the HTML report site with the
   * Enterprise Architect. If set on the command line use
   * <code>-Dsmartics-ea.generate-html-site</code>.
   *
   * @since 1.0
   */
  @Parameter(property = "smartics-ea.generate-html-site",
      defaultValue = "false")
  private boolean generateHtmlSite;

  /**
   * Set to <code>true</code> to generate the images for the Enterprise
   * Architect diagrams. If set on the command line use
   * <code>-Dsmartics-ea.generate-images</code>.
   *
   * @since 1.0
   */
  @Parameter(property = "smartics-ea.generate-images", defaultValue = "true")
  private boolean generateImages;

  /**
   * A simple flag to skip the execution of this MOJO. If set on the command
   * line use <code>-Dsmartics-ea.skip</code>.
   *
   * @since 1.0
   */
  @Parameter(property = "smartics-ea.skip", defaultValue = "false")
  private boolean skip;

  /**
   * The verbose level. If set on the command line use
   * <code>-Dsmartics-ea.verbose</code>.
   *
   * @since 1.0
   */
  @Parameter(property = "smartics-ea.verbose", defaultValue = "false")
  private boolean verbose;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  // --- business -------------------------------------------------------------

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException
  {
    if (skip)
    {
      final Log log = getLog();
      log.info("Skipping exporting the Enterprise Architect Model since skip='true'.");
      return;
    }

    runExport();
  }

  private void runExport() throws MojoExecutionException
  {
    final Repository repository = openRepository();
    try
    {
      final Project project = repository.GetProjectInterface();
      final Ea facade =
          new Ea(getLog(), project, imagesTargetFolder.getAbsolutePath());

      exportHtmlSite(project, facade);
      exportImages(facade);
    }
    finally
    {
      repository.CloseFile();
    }
  }

  private void exportHtmlSite(final Project project, final Ea facade)
    throws MojoExecutionException
  {
    final boolean hasEaPackageGuid = StringUtils.isNotBlank(eaPackageGuid);
    if (generateHtmlSite || hasEaPackageGuid)
    {
      MojoUtils.provideMojoDirectory(reportTargetFolder);
      final String exportFolder = reportTargetFolder.getAbsolutePath();

      if (hasEaPackageGuid)
      {
        project
            .RunHTMLReport(eaPackageGuid, exportFolder, htmlExportImageFormat,
                htmlExportStyle, htmlExportFileNameExtension);
      }
      else
      {
        for (final EaEntity eaProject : facade.readProjects())
        {
          final String eaPackageGuid = eaProject.getGuid();
          project.RunHTMLReport(eaPackageGuid, exportFolder,
              htmlExportImageFormat, htmlExportStyle,
              htmlExportFileNameExtension);
        }
      }
    }
  }

  private void exportImages(final Ea facade) throws MojoExecutionException
  {
    if (generateImages)
    {
      MojoUtils.provideMojoDirectory(imagesTargetFolder);
      facade.exportImages();
    }
  }

  private Repository openRepository() throws MojoExecutionException
  {
    final String eaProjectRepositoryPath =
        eaProjectRepositoryFile.getAbsolutePath();
    if (!eaProjectRepositoryFile.canRead())
    {
      throw new MojoExecutionException(String.format(
          "Cannot read EAP file '%s'.", eaProjectRepositoryPath));
    }

    final Repository repository = new Repository();
    final boolean success = repository.OpenFile(eaProjectRepositoryPath);

    if (!success)
    {
      throw new MojoExecutionException(String.format(
          "Cannot open EAP file '%s' to access model repository.",
          eaProjectRepositoryPath));
    }

    return repository;
  }

  // --- object basics --------------------------------------------------------

}
