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

package org.codehaus.groovy.gant.infrastructure

/**
 *  This class is for code sharing between classes doing include activity.
 *
 *  @author Russel Winder
 *  @version $Revision$ $Date$
 */
abstract class AbstractInclude {
  protected binding
  protected createInstance ( Class theClass ) {
    theClass.getConstructor ( Binding ).newInstance ( [ binding ] as Object[] )
  }  
  protected AbstractInclude ( binding ) { this.binding = binding }
}