/*
 * Copyright 2014-2015 smartics, Kronseder & Reiner GmbH
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
  private File htmlSiteTargetFolder;

  /**
   * The target folder for the model images. Only used for XMI packages export.
   *
   * @since 1.0
   */
  @Parameter(defaultValue = "${project.reporting.outputDirectory}/xmi/images")
  private File xmiImagesTargetFolder;

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
   * Provides information to control the export of the HTML site.
   *
   * @since 1.0
   */
  @Parameter
  private HtmlExport htmlExportConfig;

  /**
   * Set to <code>true</code> to generate the images for the Enterprise
   * Architect diagrams (XMI export). If set on the command line use
   * <code>-Dsmartics-ea.generate-xmi</code>.
   *
   * @since 1.0
   */
  @Parameter(property = "smartics-ea.generate-xmi", defaultValue = "true")
  private boolean generateXmi;

  /**
   * Provides information to control the export of packages via XMI. Please
   * refer to <a href=
   * "http://www.sparxsystems.com/enterprise_architect_user_guide/9.3/automation/project_2.html"
   * >Project Interface</a> for details.
   *
   * @since 1.0
   */
  @Parameter
  private XmiPackageExport xmiPackageExportConfig;

  /**
   * The encoding provided by the EA interface for their XML files.
   *
   * @since 1.0
   */
  @Parameter(defaultValue = "UTF-8")
  private String eaXmlFileEncoding;

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

    provideDefaultConfig();

    runExport();
  }

  private void provideDefaultConfig()
  {
    if (htmlExportConfig == null)
    {
      htmlExportConfig = new HtmlExport();
    }

    if (xmiPackageExportConfig == null)
    {
      xmiPackageExportConfig = new XmiPackageExport();
    }

    xmiPackageExportConfig.init();

    final Log log = getLog();
    if (generateHtmlSite)
    {
      if (verbose) log.info("HTML config: " + htmlExportConfig);
    }
    if (generateXmi)
    {
      if (verbose) log.info("XMI config: " + xmiPackageExportConfig);
    }
  }

  private void runExport() throws MojoExecutionException
  {
    final Repository repository = openRepository();
    Project project = null;
    try
    {
      project = repository.GetProjectInterface();
      final Ea facade =
          new Ea(getLog(), eaProjectRepositoryFile.getAbsolutePath(), project,
              xmiImagesTargetFolder.getAbsolutePath(), eaXmlFileEncoding,
              xmiPackageExportConfig, verbose);

      exportHtmlSite(project, facade);
      exportImages(facade);
    }
    finally
    {
      if (project != null)
      {
        project.Exit();
      }
      repository.CloseFile();
    }
  }

  private void exportHtmlSite(final Project project, final Ea facade)
    throws MojoExecutionException
  {
    final boolean hasEaPackageGuid =
        htmlExportConfig != null && htmlExportConfig.hasEaPackageGuid();
    if (generateHtmlSite || hasEaPackageGuid)
    {
      MojoUtils.provideMojoDirectory(htmlSiteTargetFolder);

      if (hasEaPackageGuid)
      {
        final String exportFolder = htmlSiteTargetFolder.getAbsolutePath();
        project.RunHTMLReport(htmlExportConfig.getEaPackageGuid(),
            exportFolder, htmlExportConfig.getImageFormat(),
            htmlExportConfig.getStyle(),
            htmlExportConfig.getFileNameExtension());
      }
      else
      {
        for (final EaEntity eaProject : facade.readProjects())
        {
          final String projectGuid = eaProject.getGuid();
          final File projectExportFolder =
              new File(htmlSiteTargetFolder, projectGuid);
          MojoUtils.provideMojoDirectory(projectExportFolder);
          project.RunHTMLReport(projectGuid,
              projectExportFolder.getAbsolutePath(),
              htmlExportConfig.getImageFormat(), htmlExportConfig.getStyle(),
              htmlExportConfig.getFileNameExtension());
        }
      }
    }
  }

  private void exportImages(final Ea facade) throws MojoExecutionException
  {
    if (generateXmi)
    {
      MojoUtils.provideMojoDirectory(xmiImagesTargetFolder);
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
