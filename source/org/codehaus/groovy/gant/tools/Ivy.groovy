//  Gant -- A Groovy build tool based on scripting Ant tasks
//
//  Copyright (C) 2006 Russel Winder <russel@russel.org.uk>
//
//  This library is free software; you can redistribute it and/or modify it under the terms of
//  the GNU Lesser General Public License as published by the Free Software Foundation; either
//  version 2.1 of the License, or (at your option) any later version.
//
//  This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
//  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//  See the GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License along with this
//  library; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor,
//  Boston, MA 02110-1301 USA

package org.codehaus.groovy.gant.tools

/**
 *  A class to provide support for using Ivy.
 *
 *  @author Russel Winder
 *  @version $Revision$ $Date$
 */
final class Ivy {
  private final Map environment ;
  Ivy ( final Map environment ) { this.environment = environment ; }
  void task_cachepath ( theMap ) { environment.Ant.cachepath ( theMap ) }
  void task_configure ( theMap ) { environment.Ant.configure ( theMap ) }
  void task_publish ( theMap ) { environment.Ant.publish ( theMap ) }
  void task_report ( theMap ) { environment.Ant.report ( theMap ) }
  void task_resolve ( theMap ) { environment.Ant.resolve ( theMap ) }
  void task_retrieve ( theMap ) { environment.Ant.retrieve ( theMap ) }
}
