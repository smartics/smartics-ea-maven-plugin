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


/**
 * An Enterprise Architect entity.
 */
final class EaEntity
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  /**
   * The unique GUID of the entity.
   */
  private final String guid;

  /**
   * The name of the entity.
   */
  private final String name;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Default constructor.
   *
   * @param guid the unique GUID of the entity.
   * @param name the name of the entity.
   */
  EaEntity(final String guid, final String name)
  {
    this.guid = guid;
    this.name = name;
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  /**
   * Returns the unique GUID of the entity.
   *
   * @return the unique GUID of the entity.
   */
  String getGuid()
  {
    return guid;
  }

  /**
   * Returns the name of the entity.
   *
   * @return the name of the entity.
   */
  String getName()
  {
    return name;
  }

  // --- business -------------------------------------------------------------

  // --- object basics --------------------------------------------------------

  /**
   * Returns the hash code of the object.
   *
   * @return the hash code.
   */
  @Override
  public int hashCode()
  {
    int result = 17;
    result = 37 * result + guid.hashCode();
    result = 37 * result + name.hashCode();

    return result;
  }

  /**
   * Returns <code>true</code> if the given object is semantically equal to the
   * given object, <code>false</code> otherwise.
   *
   * @param object the instance to compare to.
   * @return <code>true</code> if the given object is semantically equal to the
   *         given object, <code>false</code> otherwise.
   */
  @Override
  public boolean equals(final Object object)
  {
    if (this == object)
    {
      return true;
    }
    else if (object == null || getClass() != object.getClass())
    {
      return false;
    }

    final EaEntity other = (EaEntity) object;

    return (guid.equals(other.guid) && name.equals(other.name));
  }

  @Override
  public String toString()
  {
    return guid + ": " + name;
  }
}
