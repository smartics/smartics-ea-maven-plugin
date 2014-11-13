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

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * Utilities uses by the Mojos of this project.
 */
final class MojoUtils
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Utility class.
   */
  private MojoUtils()
  {
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  // --- business -------------------------------------------------------------

  /**
   * Ensures that the given directory is present and if not, creates it.
   *
   * @param directory the directory to be created.
   * @throws MojoExecutionException if the directory is not present and cannot
   *           be created.
   */
  static void provideMojoDirectory(final File directory)
    throws MojoExecutionException
  {
    if (!directory.exists())
    {
      final boolean created = directory.mkdirs();
      if (!created && !directory.exists())
      {
        throw new MojoExecutionException("Cannot create directory: "
                                         + directory.getAbsolutePath());
      }
    }
  }

  // --- object basics --------------------------------------------------------

}
