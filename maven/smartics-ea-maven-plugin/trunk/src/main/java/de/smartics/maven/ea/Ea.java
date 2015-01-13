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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.sparx.Project;

/**
 * Provides access to the EA entities.
 */
final class Ea
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  /**
   * The name of the XML element that contains the GUID of an EA entity.
   */
  private static final String XML_ELEMENT_NAME_GUID = "GUID";

  /**
   * The name of the XML element that contains the name of an EA entity.
   */
  private static final String XML_ELEMENT_NAME_NAME = "Name";

  // --- members --------------------------------------------------------------

  private final Log log;

  private final String eapFile;

  private final Project project;

  private final String imagesTargetPath;

  private final String xmlEncoding;

  private final XmiPackageExport xmiPackageExportConfig;

  private final boolean verbose;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Default constructor.
   */
  Ea(final Log log, final String eapFile, final Project project,
      final String imagesTargetPath, final String xmlEncoding,
      final XmiPackageExport xmiPackageExportConfig, final boolean verbose)
  {
    this.log = log;
    this.eapFile = eapFile;
    this.project = project;
    this.imagesTargetPath = imagesTargetPath;
    this.xmlEncoding = xmlEncoding;
    this.xmiPackageExportConfig = xmiPackageExportConfig;
    this.verbose = verbose;
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  // --- business -------------------------------------------------------------

  void exportImages() throws MojoExecutionException
  {
    final List<EaEntity> projects = readProjects();
    exportImages(projects);
  }

  void exportImages(final List<EaEntity> projects)
    throws MojoExecutionException
  {
    for (final EaEntity eaProject : projects)
    {
      if (verbose) log.info("Start export of project " + eaProject);
      final List<EaEntity> packages = readPackages(eaProject);
      for (final EaEntity eaPackage : packages)
      {
        exportImages(eaPackage);
      }
      if (verbose) log.info("End export of project " + eaProject);
    }
  }

  private void exportImages(final EaEntity eaPackage)
  {
    final String packageGuid = eaPackage.getGuid();

    if (verbose) log.info("  Start export of package " + eaPackage);

    project.ExportPackageXMIEx(packageGuid,
        xmiPackageExportConfig.getXmiType(),
        xmiPackageExportConfig.getDiagramXml(),
        xmiPackageExportConfig.getDiagramImage(),
        xmiPackageExportConfig.getFormatXml(),
        xmiPackageExportConfig.getUseDtd(), imagesTargetPath,
        xmiPackageExportConfig.getXmiFlags());

    if (verbose) log.info("  End export of package " + eaPackage);
  }

  List<EaEntity> readProjects() throws MojoExecutionException
  {
    final String projectXml = project.EnumProjects();
    try
    {
      final SAXBuilder builder = new SAXBuilder();
      final InputStream in =
          new ByteArrayInputStream(projectXml.getBytes(xmlEncoding));
      final Document document = builder.build(in);
      final Element root = document.getRootElement();

      final List<EaEntity> projects = new ArrayList<EaEntity>();
      for (final Element projectElement : root.getChildren("Project"))
      {
        final String guid = projectElement.getChildText(XML_ELEMENT_NAME_GUID);
        final String name = projectElement.getChildText(XML_ELEMENT_NAME_NAME);

        if (StringUtils.isBlank(guid))
        {
          log.warn("Project "
                   + (StringUtils.isNotBlank(name) ? "'" + name + "'" : "")
                   + "without a GUID. Skipping.");
          continue;
        }
        if (StringUtils.isBlank(name))
        {
          log.warn("Project "
                   + (StringUtils.isNotBlank(guid) ? "'" + guid + "'" : "")
                   + "without a name. Continue without name.");
        }

        final EaEntity project = new EaEntity(guid, name);
        projects.add(project);
      }

      if (projects.isEmpty())
      {
        log.warn("No projects found! Nothing to export from " + eapFile);
      }

      return projects;
    }
    catch (final Exception e)
    {
      throw new MojoExecutionException(
          "Cannot read project XML: " + projectXml, e);
    }
  }

  private List<EaEntity> readPackages(final EaEntity eaProject)
    throws MojoExecutionException
  {
    final List<EaEntity> packages = new ArrayList<EaEntity>();
    final String guid = eaProject.getGuid();
    try
    {
      addPackages(packages, guid);
      if (packages.isEmpty())
      {
        log.info("No packages found in project! Nothing to export: " + guid);
      }
    }
    catch (final Exception e)
    {
      log.error("Cannot read packages of project " + guid + ". Skipping.");
    }

    return packages;
  }

  private void addPackages(final List<EaEntity> packages, final String guid)
    throws JDOMException, IOException
  {
    final String packagesXml = this.project.EnumPackages(guid);
    if (StringUtils.isBlank(packagesXml))
    {
      return;
    }
    final SAXBuilder builder = new SAXBuilder();
    final InputStream in =
        new ByteArrayInputStream(packagesXml.getBytes(xmlEncoding));
    final Document document = builder.build(in);
    final Element root = document.getRootElement();

    for (final Element packageElement : root.getChildren("Package"))
    {
      final EaEntity packageEntity = createPackageEntity(packageElement);
      if (packageEntity == null)
      {
        continue;
      }

      packages.add(packageEntity);

      try
      {
        addPackages(packages, packageEntity.getGuid());
      }
      catch (final Exception e)
      {
        // continue
      }
    }
  }

  private EaEntity createPackageEntity(final Element packageElement)
  {
    final String guid = packageElement.getChildText(XML_ELEMENT_NAME_GUID);
    final String name = packageElement.getChildText(XML_ELEMENT_NAME_NAME);

    if (StringUtils.isBlank(guid))
    {
      log.warn("Package "
               + (StringUtils.isNotBlank(name) ? "'" + name + "'" : "")
               + "without a GUID. Skipping.");
      return null;
    }
    if (StringUtils.isBlank(name))
    {
      log.warn("Package "
               + (StringUtils.isNotBlank(guid) ? "'" + guid + "'" : "")
               + "without a name. Continue without name.");
    }

    final EaEntity packageEntity = new EaEntity(guid, name);
    return packageEntity;
  }

  // --- object basics --------------------------------------------------------

}
