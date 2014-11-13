/*
 * Copyright 2014 smartics, Kronseder & Reiner GmbH
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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.sparx.EnumXMIType;
import org.sparx.Project;

/**
 * Provides access to the EA entities.
 */
final class Ea
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  private final Log log;

  private final Project project;

  private final String imagesTargetPath;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Default constructor.
   */
  Ea(final Log log, final Project project, final String imagesTargetPath)
  {
    this.log = log;
    this.project = project;
    this.imagesTargetPath = imagesTargetPath;
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  // --- business -------------------------------------------------------------

  void exportImages() throws MojoExecutionException
  {
    for (final EaEntity eaProject : readProjects())
    {
      for (final EaEntity eaPackage : readPackages(eaProject))
      {
        exportImages(eaPackage);
      }
    }
  }

  private void exportImages(final EaEntity eaPackage)
  {
    final String packageGuid = eaPackage.getGuid();
    log.info("  Start export of package GUID=" + packageGuid);
    project.ExportPackageXMI(packageGuid, EnumXMIType.xmiEADefault, 2, 3, 0, 0,
        imagesTargetPath);
    log.info("  End of package export   GUID=" + packageGuid);
  }

  List<EaEntity> readProjects() throws MojoExecutionException
  {
    final String projectXml = project.EnumProjects();
    try
    {
      final SAXBuilder builder = new SAXBuilder();
      final InputStream in =
          new ByteArrayInputStream(projectXml.getBytes("UTF-8"));
      final Document document = builder.build(in);
      final Element root = document.getRootElement();

      final List<EaEntity> projects = new ArrayList<EaEntity>();
      for (final Element projectElement : root.getChildren("Project"))
      {
        final String guid = projectElement.getChildText("GUID");
        final String name = projectElement.getChildText("Name");
        final EaEntity project = new EaEntity(guid, name);
        projects.add(project);
      }

      return projects;
    }
    catch (final Exception e)
    {
      throw new MojoExecutionException(
          "Cannot read project XML: " + projectXml, e);
    }
  }

  private List<EaEntity> readPackages(final EaEntity project)
    throws MojoExecutionException
  {
    final String packagesXml = this.project.EnumPackages(project.getGuid());
    try
    {
      final SAXBuilder builder = new SAXBuilder();
      final InputStream in =
          new ByteArrayInputStream(packagesXml.getBytes("UTF-8"));
      final Document document = builder.build(in);
      final Element root = document.getRootElement();

      final List<EaEntity> packages = new ArrayList<EaEntity>();
      for (final Element packageElement : root.getChildren("Package"))
      {
        final String guid = packageElement.getChildText("GUID");
        final String name = packageElement.getChildText("Name");
        final EaEntity packageEntity = new EaEntity(guid, name);
        packages.add(packageEntity);
      }

      return packages;
    }
    catch (final Exception e)
    {
      throw new MojoExecutionException("Cannot read packages XML: "
                                       + packagesXml, e);
    }
  }

  // --- object basics --------------------------------------------------------

}
